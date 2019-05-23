package com.wilb0t.flamegraph;

import static java.util.stream.Collectors.summingLong;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordedFrame;
import jdk.jfr.consumer.RecordedMethod;
import jdk.jfr.consumer.RecordedStackTrace;
import jdk.jfr.consumer.RecordingFile;

public class JfrStackTraceReader {

  private static final String EVENT_TYPE = "jdk.ExecutionSample";

  public Map<String, Long> getStackTraces(Path input) throws IOException {
    try (var recording = new RecordingFile(input)) {
      return asStream(recording)
          .filter(event -> event.getEventType().getName().equalsIgnoreCase(EVENT_TYPE))
          .map(JfrStackTraceReader::getTrace)
          .collect(Collectors.groupingBy(Function.identity(), summingLong(s -> 1)));
    }
  }

  private static Stream<RecordedEvent> asStream(RecordingFile recording) throws IOException {
    if (recording.hasMoreEvents()) {
      RecordedEvent event = recording.readEvent();
      return Stream.iterate(
          event,
          e -> recording.hasMoreEvents(),
          e -> {
            try {
              return recording.readEvent();
            } catch (IOException ex) {
              throw new RuntimeException(ex);
            }
          });
    }
    return Stream.empty();
  }

  private static String getTrace(RecordedEvent event) {
    RecordedStackTrace trace = event.getStackTrace();
    if (trace == null) {
      return "";
    }
    var frames = trace.getFrames().stream()
        .filter(RecordedFrame::isJavaFrame)
        .flatMap(JfrStackTraceReader::getFrameName)
        .collect(Collectors.toList());
    Collections.reverse(frames);

    return String.join(";", frames);
  }

  private static Stream<String> getFrameName(RecordedFrame frame) {
    StringBuilder methodBuilder = new StringBuilder();
    RecordedMethod method = frame.getMethod();
    if (method == null) {
      return Stream.empty();
    }

    methodBuilder
        .append(method.getType().getName())
        .append("::")
        .append(method.getName());

    return Stream.of(methodBuilder.toString());
  }

}
