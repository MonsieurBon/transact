package ch.ethy.transact.log;

import org.junit.jupiter.api.Test;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PrintStreamAppenderTest {

  @Test
  public void test() {
    MockOutputStream os = new MockOutputStream();
    PrintStream ps = new MockPrintStream(os);
    PrintStreamAppender appender = new PrintStreamAppender(ps);

    String input = "foobar";

    appender.append(input);

    List<Integer> f = Stream.of('f', 'o', 'o', 'b', 'a', 'r', '\n')
        .map(c -> (int) c)
        .collect(toList());

    assertEquals(f, os.bytes);
  }

  private static class MockPrintStream extends PrintStream {
    public MockPrintStream(OutputStream out) {
      super(out);
    }
  }

  private static class MockOutputStream extends OutputStream {
    private final List<Integer> bytes = new ArrayList<>();

    @Override
    public void write(int b) {
      bytes.add(b);
    }
  }
}