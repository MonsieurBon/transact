package ch.ethy.transact.log;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static ch.ethy.transact.log.Severity.DEBUG;
import static ch.ethy.transact.log.Severity.ERROR;
import static ch.ethy.transact.log.Severity.FATAL;
import static ch.ethy.transact.log.Severity.INFO;
import static ch.ethy.transact.log.Severity.WARN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoggerTest {
  private Logger logger;
  private MockAppender appender = new MockAppender();

  @BeforeEach
  public void setup() {
    Logger.setSeverity(INFO);
    Logger.addAppender(appender);
    Logger.setDateProvider(() -> ZonedDateTime.of(2020, 1, 26, 2, 27, 0, 0, ZoneId.of("Europe/Zurich")));
    Logger.setFormat("%message%");
    logger = Logger.getLogger(LoggerTest.class);
  }

  @Test
  public void getLogger_returnsLogger() {
    assertNotNull(logger);
  }

  @Test
  public void appendsMessageToAppender() {
    logger.info("foo");
    logger.info("bar");

    assertEquals(List.of("foo", "bar"), appender.messages);
  }

  @Test
  public void appendsMessageToAllAppenders() {
    MockAppender secondAppender = new MockAppender();
    Logger.addAppender(secondAppender);

    logger.info("foo");

    assertEquals(List.of("foo"), appender.messages);
    assertEquals(List.of("foo"), secondAppender.messages);
  }

  @Test
  public void onlyLogsIfLevelIsEqualOrLower() {
    Logger.setSeverity(DEBUG);

    logger.debug("debug");
    logger.info("info");
    logger.warn("warn");
    logger.error("error");
    logger.fatal("fatal");

    assertEquals(List.of("debug", "info", "warn", "error", "fatal"), appender.messages);

    Logger.setSeverity(INFO);
    MockAppender appender = new MockAppender();
    Logger.addAppender(appender);

    logger.debug("debug");
    logger.info("info");
    logger.warn("warn");
    logger.error("error");
    logger.fatal("fatal");

    assertEquals(List.of("info", "warn", "error", "fatal"), appender.messages);

    Logger.setSeverity(WARN);
    appender = new MockAppender();
    Logger.addAppender(appender);

    logger.debug("debug");
    logger.info("info");
    logger.warn("warn");
    logger.error("error");
    logger.fatal("fatal");

    assertEquals(List.of("warn", "error", "fatal"), appender.messages);

    Logger.setSeverity(ERROR);
    appender = new MockAppender();
    Logger.addAppender(appender);

    logger.debug("debug");
    logger.info("info");
    logger.warn("warn");
    logger.error("error");
    logger.fatal("fatal");

    assertEquals(List.of("error", "fatal"), appender.messages);

    Logger.setSeverity(FATAL);
    appender = new MockAppender();
    Logger.addAppender(appender);

    logger.debug("debug");
    logger.info("info");
    logger.warn("warn");
    logger.error("error");
    logger.fatal("fatal");

    assertEquals(List.of("fatal"), appender.messages);
  }

  @Test
  public void messageIsFormattedAccordingToFormat() {
    Logger.setFormat("%timestamp% - %severity% - %class%: %message%");
    logger.info("my message");

    assertEquals(appender.messages.get(0), "2020-01-26T02:27:00,000+01:00 - INFO - ch.ethy.transact.log.LoggerTest: my message");
  }

  @Test
  public void logsExceptionStacktraceForDebug() {
    Logger.setSeverity(DEBUG);

    try {
      throw new IllegalStateException("even more messages", new RuntimeException("Another message", new Exception("Some message")));
    } catch (Exception e) {
      logger.debug("Error!", e);
      assertEquals("Error!", appender.messages.get(0));
      assertEquals("java.lang.IllegalStateException: even more messages", appender.messages.get(1));
      assertTrue(appender.messages.get(2).startsWith("\tat ch.ethy.transact.log.LoggerTest.logsExceptionStacktraceForDebug(LoggerTest.java:"));
      assertTrue(appender.messages.contains("Caused by: java.lang.RuntimeException: Another message"));
      assertTrue(appender.messages.contains("Caused by: java.lang.Exception: Some message"));
    }
  }

  @Test
  public void logsExceptionStacktraceForInfo() {
    try {
      throw new IllegalStateException("even more messages", new RuntimeException("Another message", new Exception("Some message")));
    } catch (Exception e) {
      logger.info("Error!", e);
      assertEquals("Error!", appender.messages.get(0));
      assertEquals("java.lang.IllegalStateException: even more messages", appender.messages.get(1));
      assertTrue(appender.messages.get(2).startsWith("\tat ch.ethy.transact.log.LoggerTest.logsExceptionStacktraceForInfo(LoggerTest.java:"));
      assertTrue(appender.messages.contains("Caused by: java.lang.RuntimeException: Another message"));
      assertTrue(appender.messages.contains("Caused by: java.lang.Exception: Some message"));
    }
  }

  @Test
  public void logsExceptionStacktraceForWarn() {
    try {
      throw new IllegalStateException("even more messages", new RuntimeException("Another message", new Exception("Some message")));
    } catch (Exception e) {
      logger.warn("Error!", e);
      assertEquals("Error!", appender.messages.get(0));
      assertEquals("java.lang.IllegalStateException: even more messages", appender.messages.get(1));
      assertTrue(appender.messages.get(2).startsWith("\tat ch.ethy.transact.log.LoggerTest.logsExceptionStacktraceForWarn(LoggerTest.java:"));
      assertTrue(appender.messages.contains("Caused by: java.lang.RuntimeException: Another message"));
      assertTrue(appender.messages.contains("Caused by: java.lang.Exception: Some message"));
    }
  }

  @Test
  public void logsExceptionStacktraceForError() {
    try {
      throw new IllegalStateException("even more messages", new RuntimeException("Another message", new Exception("Some message")));
    } catch (Exception e) {
      logger.error("Error!", e);
      assertEquals("Error!", appender.messages.get(0));
      assertEquals("java.lang.IllegalStateException: even more messages", appender.messages.get(1));
      assertTrue(appender.messages.get(2).startsWith("\tat ch.ethy.transact.log.LoggerTest.logsExceptionStacktraceForError(LoggerTest.java:"));
      assertTrue(appender.messages.contains("Caused by: java.lang.RuntimeException: Another message"));
      assertTrue(appender.messages.contains("Caused by: java.lang.Exception: Some message"));
    }
  }

  @Test
  public void logsExceptionStacktraceForFatal() {
    try {
      throw new IllegalStateException("even more messages", new RuntimeException("Another message", new Exception("Some message")));
    } catch (Exception e) {
      logger.fatal("Error!", e);
      assertEquals("Error!", appender.messages.get(0));
      assertEquals("java.lang.IllegalStateException: even more messages", appender.messages.get(1));
      assertTrue(appender.messages.get(2).startsWith("\tat ch.ethy.transact.log.LoggerTest.logsExceptionStacktraceForFatal(LoggerTest.java:"));
      assertTrue(appender.messages.contains("Caused by: java.lang.RuntimeException: Another message"));
      assertTrue(appender.messages.contains("Caused by: java.lang.Exception: Some message"));
    }
  }

  private static class MockAppender implements Appender {
    private List<String> messages = new ArrayList<>();

    @Override
    public void append(String message) {
      messages.add(message);
    }

    @Override
    public void appendStackTrace(Throwable throwable) {
      MockPrintWriter mockPrintWriter = new MockPrintWriter();
      throwable.printStackTrace(mockPrintWriter);
      messages.addAll(mockPrintWriter.lines);
    }
  }

  private static class MockPrintWriter extends PrintWriter {
    private final List<String> lines = new ArrayList<>();

    public MockPrintWriter() {
      super(OutputStream.nullOutputStream());
    }

    @Override
    public void println(String line) {
      this.lines.add(line);
    }

    @Override
    public void println(Object x) {
      println(x.toString());
    }

    @Override
    public void println() {
      this.lines.add("");
    }
  }
}
