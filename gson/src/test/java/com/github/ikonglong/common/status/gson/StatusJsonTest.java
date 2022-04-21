package com.github.ikonglong.common.status.gson;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.ikonglong.common.status.Case.Default;
import com.github.ikonglong.common.status.Status;
import com.github.ikonglong.common.status.Status.Code;
import com.github.ikonglong.common.status.gson.StatusJson;
import org.junit.jupiter.api.Test;

public class StatusJsonTest {

  StatusJson sj = StatusJson.DEFAULT_INSTANCE;

  @Test
  public void toJson() {
    String json =
        sj.toJson(
            Status.INVALID_ARGUMENT.withCase(
                new Default("file_size_limit_exceeded", Code.INVALID_ARGUMENT), "Bad request"));
    String expected =
        "{\"code\":3,\"status\":\"INVALID_ARGUMENT\",\"message\":\"Bad request\",\"theCase\":\"file_size_limit_exceeded\"}";
    assertThat(json).isEqualTo(expected);
  }

  @Test
  public void fromJson() {
    String json =
        "{\"code\":3,\"status\":\"INVALID_ARGUMENT\",\"message\":\"Bad request\",\"theCase\":\"file_size_limit_exceeded\"}";
    Status s = sj.fromJson(json);
    assertThat(s.code()).isEqualTo(Code.INVALID_ARGUMENT);
    assertThat(s.message()).isEqualTo("Bad request");
    assertThat(s.theCase().identifier()).isEqualTo("file_size_limit_exceeded");
    assertThat(s.theCase().statusCode()).isEqualTo(Code.INVALID_ARGUMENT);
  }
}
