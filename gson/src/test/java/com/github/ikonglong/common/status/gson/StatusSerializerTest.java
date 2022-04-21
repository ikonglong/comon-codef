package com.github.ikonglong.common.status.gson;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.ikonglong.common.status.gson.StatusDeserializer;
import com.github.ikonglong.common.status.gson.StatusSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.github.ikonglong.common.status.BasicDigitCodedCaseFactory;
import com.github.ikonglong.common.status.ComplexSystemCaseFactory;
import com.github.ikonglong.common.status.Status;
import com.github.ikonglong.common.status.Status.Code;
import org.junit.jupiter.api.Test;

public class StatusSerializerTest {

  final Gson gson =
      new GsonBuilder()
          .registerTypeAdapter(Status.class, new StatusSerializer())
          .registerTypeAdapter(Status.class, new StatusDeserializer())
          .create();

  @Test
  public void givenStatusWithCase_WhenToAndFromJson() {
    ComplexSystemCaseFactory codeFactory = new ComplexSystemCaseFactory(2, 5);
    String message = "Param x is invalid";
    String json = gson.toJson(Status.fromCase(codeFactory.firstInvalidArgument(), message));
    System.out.println(json);
    assertThat(gson.fromJson(json, Status.class))
        .satisfies(
            s -> {
              assertThat(s.code().value()).isEqualTo(Code.INVALID_ARGUMENT.value());
              assertThat(s.message()).isEqualTo(message);
              assertThat(s.theCase().identifier()).isEqualTo("2_5_001");
              assertThat(s.theCase().statusCode()).isEqualTo(Code.INVALID_ARGUMENT);
            });
  }

  @Test
  public void givenStatusWithoutCase_WhenToAndFromJson() {
    String message = "Param x is invalid";
    String json = gson.toJson(Status.INVALID_ARGUMENT.withMessage(message));
    assertThat(gson.fromJson(json, Status.class))
        .satisfies(
            s -> {
              assertThat(s.code().value()).isEqualTo(Code.INVALID_ARGUMENT.value());
              assertThat(s.message()).isEqualTo(message);
              assertThat(s.theCase()).isNull();
            });
  }

  @Test
  public void givenJsonReturnedFromV1_x_WhenDeserialize() {
    String json =
        "{\"code\":3,\"message\":\"file size exceeded the limit 5m\","
            + "\"caseCode\":{\"appCode\":\"1\",\"moduleCode\":\"1\",\"conditionCode\":\"10\",\"stringForm\":\"1_1_010\"}}";
    Status s = gson.fromJson(json, Status.class);
    assertThat(s.code()).isEqualTo(Code.INVALID_ARGUMENT);
    assertThat(s.message()).isEqualTo("file size exceeded the limit 5m");
    assertThat(s.theCase().identifier()).isEqualTo("1_1_010");
    assertThat(s.code()).isEqualTo(Code.INVALID_ARGUMENT);
  }

  @Test
  public void givenStatusWithDetails_WhenSerialize() {
    BasicDigitCodedCaseFactory codeFactory =
        new BasicDigitCodedCaseFactory.FactoryForComplexSystem(2, 5);
    String message = "Param x is invalid";
    Status status =
        Status.fromCase(codeFactory.firstInvalidArgument(), message).addDetail("reason 1");
    String json = gson.toJson(status);
    Status status2 = gson.fromJson(json, Status.class);
    assertThat(status.toString()).isEqualTo(status2.toString());
  }
}
