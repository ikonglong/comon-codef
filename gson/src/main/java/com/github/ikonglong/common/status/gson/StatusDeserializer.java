package com.github.ikonglong.common.status.gson;

import static com.github.ikonglong.common.status.json.StatusProperties.PROP_CASE;
import static com.github.ikonglong.common.status.json.StatusProperties.PROP_CASE_CODE;
import static com.github.ikonglong.common.status.json.StatusProperties.PROP_CODE;
import static com.github.ikonglong.common.status.json.StatusProperties.PROP_DETAILS;
import static com.github.ikonglong.common.status.json.StatusProperties.PROP_MESSAGE;
import static java.util.Objects.isNull;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.github.ikonglong.common.status.Case.Default;
import com.github.ikonglong.common.status.Status;
import java.lang.reflect.Type;
import java.util.List;

public class StatusDeserializer implements JsonDeserializer<Status> {
  @Override
  public Status deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
      throws JsonParseException {
    if (!jsonElement.isJsonObject()) {
      throw new JsonParseException(
          "Expected a json object, actual is '" + jsonElement + "' while deserializing status");
    }
    JsonObject jsonObj = (JsonObject) jsonElement;
    int statusCode = jsonObj.getAsJsonPrimitive(PROP_CODE).getAsInt();
    String message = jsonObj.get(PROP_MESSAGE).getAsString();

    // For backward compatibility
    Status status;
    JsonElement caseCodeJson = jsonObj.get(PROP_CASE_CODE);
    // `isNull` means that the property is absent.
    // `jsJsonNull` means that the value of the property is `null`.
    if (!isNull(caseCodeJson) && !caseCodeJson.isJsonNull()) {
      CaseCode caseCode = context.deserialize(jsonObj.get(PROP_CASE_CODE), CaseCode.class);
      status =
          Status.fromCodeValue(statusCode)
              .withCase(new Default(caseCode.stringForm, statusCode), message);
    }

    // For the latest abstraction
    JsonElement caseJson = jsonObj.get(PROP_CASE);
    if (isNull(caseJson) || caseJson.isJsonNull()) {
      status = Status.fromCodeValue(statusCode).withMessage(message);
    } else {
      status =
          Status.fromCodeValue(statusCode)
              .withCase(new Default(caseJson.getAsString(), statusCode), message);
    }

    JsonElement detailsJson = jsonObj.get(PROP_DETAILS);
    if (!isNull(detailsJson) && !detailsJson.isJsonNull()) {
      status.addDetails(context.deserialize(detailsJson, List.class));
    }

    return status;
  }

  static class CaseCode {
    String appCode;
    String moduleCode;
    String conditionCode;
    String stringForm;
  }
}
