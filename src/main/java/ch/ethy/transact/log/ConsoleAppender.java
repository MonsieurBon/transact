package ch.ethy.transact.log;

public class ConsoleAppender extends PrintStreamAppender {
  public ConsoleAppender() {
    super(System.out);
  }
}
