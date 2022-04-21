package com.github.ikonglong.common.status.gson;

import static com.github.ikonglong.common.status.json.StatusProperties.PROP_CASE;
import static com.github.ikonglong.common.status.json.StatusProperties.PROP_CODE;
import static com.github.ikonglong.common.status.json.StatusProperties.PROP_DETAILS;
import static com.github.ikonglong.common.status.json.StatusProperties.PROP_MESSAGE;
import static com.github.ikonglong.common.status.json.StatusProperties.PROP_STATUS;
import static java.util.Objects.isNull;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.github.ikonglong.common.status.Status;
import java.lang.reflect.Type;

public class StatusSerializer implements JsonSerializer<Status> {
  @Override
  public JsonElement serialize(Status status, Type type, JsonSerializationContext context) {
    JsonObject jsonObj = new JsonObject();
    jsonObj.addProperty(PROP_CODE, status.code().value());
    jsonObj.addProperty(PROP_STATUS, status.code().name());
    jsonObj.addProperty(PROP_MESSAGE, status.message());
    if (isNull(status.details())) {
      jsonObj.add(PROP_DETAILS, JsonNull.INSTANCE);
    } else {
      jsonObj.add(PROP_DETAILS, context.serialize(status.details()));
    }
    if (isNull(status.theCase())) {
      jsonObj.add(PROP_CASE, JsonNull.INSTANCE);
    } else {
      jsonObj.addProperty(PROP_CASE, status.theCase().identifier());
    }
    return jsonObj;
  }
}
