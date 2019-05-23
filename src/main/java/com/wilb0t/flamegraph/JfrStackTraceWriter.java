package com.wilb0t.flamegraph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class JfrStackTraceWriter {

  public void write(File outputFile, Map<String, Long> traceCounts) throws IOException {
    try (Writer writer = new FileWriter(outputFile, StandardCharsets.UTF_8);
         BufferedWriter bufferedWriter = new BufferedWriter(writer)
    ) {
      traceCounts.forEach(
          (trace, count) ->
          {
            try {
              bufferedWriter.write(String.format("%s %d%n", trace, count));
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          }
      );
    }
  }
}
