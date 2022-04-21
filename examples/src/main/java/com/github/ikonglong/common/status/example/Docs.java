package com.github.ikonglong.common.status.example;

import static java.util.Objects.requireNonNull;

import com.github.ikonglong.common.status.BasicDigitCodedCaseFactory;
import com.github.ikonglong.common.status.DigitCodedCase.NumRange;
import com.github.ikonglong.common.status.Status;
import com.github.ikonglong.common.status.StatusCodeMapper;
import java.util.Map.Entry;

public final class Docs {

  private Docs() {}

  public static void main(String[] args) {
    System.out.println(new BasicDigitCodedCaseFactory.FactoryForMultiModuleApp(8));
    // System.out.println(Docs.docForCodeMapper(new StatusCodeToNumRangeMapper(),
    // DocFormat.SIMPLE));
  }

  public static String docForCodeMapper(StatusCodeMapper codeMapper, DocFormat docFormat) {
    requireNonNull(codeMapper, "codeMapper");
    requireNonNull(docFormat, "docFormat");
    if (docFormat == DocFormat.MARKDOWN) {
      throw new UnsupportedOperationException("Unsupported to generate markdown doc");
    }
    StringBuilder doc = new StringBuilder(codeMapper.mappings().size() * 60);
    doc.append("| Condition Code Segment | Status | HTTP Status |").append("\n");
    doc.append("| ------ | ------ | ------ |").append("\n");
    for (Entry<Status.Code, NumRange> entry : codeMapper.mappings().entrySet()) {
      Status.Code statusCode = entry.getKey();
      doc.append("| ")
          .append(entry.getValue())
          .append(" | ")
          .append(statusCode.name())
          .append(":")
          .append(statusCode.value())
          .append(" | ")
          .append(statusCode.toHttpStatus())
          .append(" |")
          .append("\n");
    }
    return doc.toString();
  }

  public enum DocFormat {
    SIMPLE,
    MARKDOWN
  }
}
