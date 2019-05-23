package com.wilb0t.flamegraph;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JfrToFolded {

  public static void main(String[] args) {
    try {
      var processedArgs = processArgs(args);
      if (!processedArgs.keySet().containsAll(List.of("-o", "-i"))) {
        throw new IllegalArgumentException("Must pass -i <input file> -o <output file>");
      }

      var jfr = new File(processedArgs.get("-i"));
      var folded = new File(processedArgs.get("-o"));

      jfrToFolded(jfr, folded);
    } catch (IOException e) {
      System.err.println(e.getMessage());
      throw new RuntimeException(e);
    }
  }

  private static void jfrToFolded(File jfr, File folded) throws IOException {
    var reader = new JfrStackTraceReader();
    var writer = new JfrStackTraceWriter();

    var traceCounts = reader.getStackTraces(jfr.toPath());
    writer.write(folded, traceCounts);
  }

  static Map<String, String> processArgs(String[] args) {
    var processed = new HashMap<String, String>();
    for (int i = 0; i <= args.length - 2; i += 2) {
      processed.put(args[i], args[i+1]);
    }
    return processed;
  }
}
