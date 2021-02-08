package ch.ethy.transact.log;

import java.io.PrintStream;

public class PrintStreamAppender implements Appender {
  private final PrintStream printStream;

  public PrintStreamAppender(PrintStream printStream) {
    this.printStream = printStream;
  }

  @Override
  public void append(String message) {
    printStream.println(message);
  }

  @Override
  public void appendStackTrace(Throwable throwable) {
    throwable.printStackTrace(printStream);
  }
}
