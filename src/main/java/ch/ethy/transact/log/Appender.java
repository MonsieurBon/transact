package ch.ethy.transact.log;

public interface Appender {
  void append(String message);

  void appendStackTrace(Throwable throwable);
}
