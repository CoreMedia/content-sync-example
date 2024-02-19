package com.coremedia.blueprint.contentsync.client.model.content;

import com.coremedia.blueprint.contentsync.client.json.ZonedDateTimeDeserializer;
import com.coremedia.blueprint.contentsync.client.model.property.LinklistPropertyModel;
import com.coremedia.blueprint.contentsync.client.model.property.PropertyModel;
import com.coremedia.blueprint.contentsync.client.model.property.ReferenceProperty;
import com.coremedia.blueprint.contentsync.client.predicates.ContentPropertyNamePredicate;
import com.coremedia.blueprint.contentsync.client.predicates.ContentTypePredicate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ContentDataModel extends ContentRefDataModel {

  private boolean isPublished;
  private boolean isInProduction;
  private LifeCycleStatus lifeCycleStatus;
  private ZonedDateTime creationDate;
  private ZonedDateTime modificationDate;
  private ZonedDateTime editionDate;
  private ZonedDateTime publicationDate;

  private List<ContentRefDataModel> children;
  private Map<String, PropertyModel> properties;

  private Map<String, Object> links;


  public boolean isPublished() {
    return isPublished;
  }

  public void setPublished(boolean published) {
    isPublished = published;
  }

  public boolean isInProduction() {
    return isInProduction;
  }

  public void setInProduction(boolean inProduction) {
    isInProduction = inProduction;
  }

  public LifeCycleStatus getLifeCycleStatus() {
    return lifeCycleStatus;
  }

  public void setLifeCycleStatus(LifeCycleStatus lifeCycleStatus) {
    this.lifeCycleStatus = lifeCycleStatus;
  }

  public ZonedDateTime getCreationDate() {
    return creationDate;
  }

  @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
  public void setCreationDate(ZonedDateTime creationDate) {
    this.creationDate = creationDate;
  }

  public ZonedDateTime getModificationDate() {
    return modificationDate;
  }

  @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
  public void setModificationDate(ZonedDateTime modificationDate) {
    this.modificationDate = modificationDate;
  }

  public ZonedDateTime getEditionDate() {
    return editionDate;
  }

  @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
  public void setEditionDate(ZonedDateTime editionDate) {
    this.editionDate = editionDate;
  }

  public ZonedDateTime getPublicationDate() {
    return publicationDate;
  }

  @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
  public void setPublicationDate(ZonedDateTime publicationDate) {
    this.publicationDate = publicationDate;
  }

  public List<ContentRefDataModel> getChildren() {
    return children;
  }

  public List<ContentRefDataModel> getChildren(ContentTypePredicate predicate) {
    return getChildren().stream().filter(cr -> predicate.test(cr.getType())).collect(Collectors.toList());
  }

  public Map<String, Object> getLinks() {
    return links;
  }

  public void setLinks(Map<String, Object> links) {
    this.links = links;
  }

  public void setChildren(List<ContentRefDataModel> children) {
    this.children = children;
  }

  public Map<String, PropertyModel> getProperties() {
    return properties;
  }

  public void setProperties(Map<String, PropertyModel> properties) {
    this.properties = properties;
  }

  /**
   * Returns the CoreMedia content IDs
   * ({@link com.coremedia.blueprint.contentsync.client.IAPIConstants#ID_PREFIX coremedia:///cap/content/...})
   * referenced by the {@link ReferenceProperty referencing properties} of this content.
   */
  @JsonIgnore
  public List<String> getReferences() {
    List<String> references = new ArrayList<>();
    getProperties()
            .values()
            .stream()
            .filter(property -> property instanceof ReferenceProperty)
            .map(property -> ((ReferenceProperty) property).getReferenceIDs())
            .forEach(references::addAll);
    return references;
  }

  @JsonIgnore
  public List<String> getReferences(@NonNull ContentPropertyNamePredicate propertyPredicate, @NonNull ContentTypePredicate contentPredicate) {
    List<String> references = new ArrayList<>();
    getProperties()
            .entrySet()
            .stream()
            .filter(es -> propertyPredicate.test(es.getKey()))
            .map(Map.Entry::getValue)
            .filter(entry -> entry instanceof ReferenceProperty)
            .map(entry -> ((ReferenceProperty) entry).getFilteredReferences(contentPredicate))
            .forEach(references::addAll);
    return references;
  }

  /**
   * Returns {@link ContentRefDataModel complete references} for {@link LinklistPropertyModel link list properties}
   * of this content.
   */
  @JsonIgnore
  public List<ContentRefDataModel> getReferenceModels() {
    List<ContentRefDataModel> references = new ArrayList<>();
    getProperties()
            .values()
            .stream()
            .filter(property -> property instanceof LinklistPropertyModel)
            .map(property -> ((LinklistPropertyModel) property).getReferences())
            .forEach(references::addAll);
    return references;
  }
}
