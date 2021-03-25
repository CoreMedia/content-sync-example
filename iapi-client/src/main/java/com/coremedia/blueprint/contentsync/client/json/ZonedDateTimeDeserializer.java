package com.coremedia.blueprint.contentsync.client.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ZonedDateTimeDeserializer extends JsonDeserializer<ZonedDateTime> {

  @Override
  public ZonedDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
    return ZonedDateTime.parse(parser.getText(), DateTimeFormatter.ISO_ZONED_DATE_TIME);
  }
}
