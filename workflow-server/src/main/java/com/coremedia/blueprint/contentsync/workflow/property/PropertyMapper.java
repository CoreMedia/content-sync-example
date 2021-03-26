package com.coremedia.blueprint.contentsync.workflow.property;

import com.coremedia.blueprint.contentsync.client.model.content.ContentDataModel;
import com.coremedia.blueprint.contentsync.client.model.content.ContentRefDataModel;
import com.coremedia.blueprint.contentsync.client.model.property.*;
import com.coremedia.blueprint.contentsync.client.services.IAPIRepository;
import com.coremedia.cap.common.BlobService;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.struct.Struct;
import com.coremedia.xml.Markup;
import com.coremedia.xml.MarkupFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimeTypeParseException;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Converter to map properties in remote contents to CoreMedia properties. Outgoing links are resolved to local
 * contents, if possible, and removed otherwise. Blobs are pulled from server.
 */
public class PropertyMapper {

  private static final Logger LOG = LoggerFactory.getLogger(PropertyMapper.class);

  private final ContentRepository repository;
  private final IAPIRepository iapiRepository;

  public PropertyMapper(ContentRepository repository, IAPIRepository iapiRepository) {
    this.repository = repository;
    this.iapiRepository = iapiRepository;
  }

  /**
   * Returns the property values of the given ingest content as CoreMedia properties. Markup and struct links are rewritten
   * according to given id map. Link lists are linked to local contents according to the given local content map.
   * Server blobs will be read from remote CMS repository.
   *
   * @param repository content repository.
   * @param idMap      map from remote numeric content ids to local numeric content ids.
   */
  public Map<String, Object> getCoreMediaProperties(ContentDataModel ingestContent,
                                                    ContentRepository repository,
                                                    Map<String, String> idMap) {
    Map<String, PropertyModel> properties = ingestContent.getProperties();
    Map<String, Object> convertedProperties = new HashMap<>();
    properties.forEach((name, value) -> {
      try {
        Object convertedValue = getConverted(value, repository, idMap);
        if (convertedValue != null) {
          convertedProperties.put(name, convertedValue);
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });
    return convertedProperties;
  }

  private Object getConverted(PropertyModel property, ContentRepository repository, Map<String, String> idMap)
          throws Exception {
    // TODO take care of special property "masterVersion" (e.g., always point to latest version of referenced master)
    if (property instanceof StringPropertyModel) {
      return ((StringPropertyModel) property).getValue();
    } else if (property instanceof MarkupPropertyModel) {
      return getConverted((MarkupPropertyModel) property, idMap);
    } else if (property instanceof LinklistPropertyModel) {
      return getConverted((LinklistPropertyModel) property, repository, idMap);
    } else if (property instanceof StructPropertyModel) {
      return getConverted((StructPropertyModel) property, idMap);
    } else if (property instanceof IntegerPropertyModel) {
      return ((IntegerPropertyModel) property).getValue();
    } else if (property instanceof DatePropertyModel) {
      return getConverted((DatePropertyModel) property);
    } else if (property instanceof ServerBlobPropertyModel) {
      return getConverted((ServerBlobPropertyModel) property);
    } else if (property instanceof UrlBlobPropertyModel) {
      throw new RuntimeException("cannot process url blob references");
    } else if (property instanceof TempBlobPropertyModel) {
      throw new RuntimeException("cannot process temp blob references");
    }
    throw new IllegalArgumentException("unknown property model " + property.getClass().getName());
  }

  private List<Content> getConverted(LinklistPropertyModel property, ContentRepository repository, Map<String, String> idMap) {
    List<ContentRefDataModel> references = property.getReferences();
    if (references != null) {
      return references.stream()
              .filter(reference -> idMap.containsKey(reference.getNumericId()))
              .map(reference -> idMap.get(reference.getNumericId()))
              .map(localReference -> repository.getContent(IdHelper.formatContentId(localReference)))
              .collect(Collectors.toList());
    }
    return null;
  }

  private Struct getConverted(StructPropertyModel property, Map<String, String> idMap) {
    if (property.getValue() != null) {
      ReplacementXMLFilter handler = new ReplacementXMLFilter(idMap);
      Markup markup = MarkupFactory.fromString(property.getValue()).transform(handler);
      return repository.getConnection().getStructService().fromMarkup(markup);
    } else {
      return null;
    }
  }

  private Markup getConverted(MarkupPropertyModel property, Map<String, String> idMap) {
    if (property.getValue() != null) {
      ReplacementXMLFilter handler = new ReplacementXMLFilter(idMap);
      return MarkupFactory.fromString(property.getValue()).transform(handler);
    } else {
      return null;
    }
  }

  private GregorianCalendar getConverted(DatePropertyModel property) {
    ZonedDateTime dateTime = property.getValue();
    if (dateTime != null) {
      return GregorianCalendar.from(dateTime);
    }
    return null;
  }

  private Object getConverted(ServerBlobPropertyModel property) throws MimeTypeParseException {
    String blobUrl = property.getUrl();
    if (blobUrl != null) {
      String[] pathExploded = blobUrl.split("/");
      if (pathExploded.length > 4) {
        String propertyName = pathExploded[pathExploded.length - 2];
        String contentId = pathExploded[pathExploded.length - 4];
        byte[] resolvedBlob = iapiRepository.getBlob(propertyName, contentId);
        BlobService blobService = repository.getConnection().getBlobService();
        String mimeType = property.getMimeType();
        if (mimeType != null) {
          return blobService.fromBytes(resolvedBlob, mimeType);
        }
      } else {
        LOG.error("invalid blob url {} (too few path segments)", blobUrl);
      }
    }
    return null;
  }
}
