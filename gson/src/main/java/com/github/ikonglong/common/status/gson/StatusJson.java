package com.github.ikonglong.common.status.gson;

import static java.util.Objects.requireNonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonReader;
import com.github.ikonglong.common.status.Status;
import java.io.Reader;

public class StatusJson {

  public static final StatusJson DEFAULT_INSTANCE = new StatusJson();

  private final Gson gson;

  public StatusJson() {
    this(false);
  }

  public StatusJson(boolean printPrettyJson) {
    GsonBuilder b =
        new GsonBuilder()
            .registerTypeAdapter(Status.class, new StatusDeserializer())
            .registerTypeAdapter(Status.class, new StatusSerializer())
            .serializeNulls();
    if (printPrettyJson) {
      b.setPrettyPrinting();
    }
    this.gson = b.create();
  }

  public StatusJson(Gson gson) {
    this.gson = requireNonNull(gson, "gson");
  }

  public String toJson(Status status) {
    return gson.toJson(status);
  }

  public Status fromJson(String json) {
    return gson.fromJson(json, Status.class);
  }

  public Status fromJson(Reader json) {
    return gson.fromJson(json, Status.class);
  }

  public Status fromJson(JsonElement json) {
    return gson.fromJson(json, Status.class);
  }

  public Status fromJson(JsonReader json) {
    return gson.fromJson(json, Status.class);
  }
}
