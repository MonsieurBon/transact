package ch.ethy.transact.log;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static ch.ethy.transact.log.Severity.DEBUG;
import static ch.ethy.transact.log.Severity.ERROR;
import static ch.ethy.transact.log.Severity.FATAL;
import static ch.ethy.transact.log.Severity.INFO;
import static ch.ethy.transact.log.Severity.WARN;

public class Logger {
  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss,SSSXXX");
  private static final List<Appender> APPENDERS = new ArrayList<>();

  private static Severity severity = INFO;
  private static String format = "%timestamp% - %severity% - %class%: %message%";
  private static Supplier<ZonedDateTime> dateProvider = () -> ZonedDateTime.now(ZoneId.systemDefault());

  private final Class<?> clazz;

  public Logger(Class<?> clazz) {
    this.clazz = clazz;
  }

  public static Logger getLogger(Class<?> clazz) {
    return new Logger(clazz);
  }

  public static void addAppender(Appender appender) {
    Logger.APPENDERS.add(appender);
  }

  public static void setSeverity(Severity level) {
    Logger.severity = level;
  }

  public static void setFormat(String format) {
    Logger.format = format;
  }

  public static void setDateProvider(Supplier<ZonedDateTime> dateProvider) {
    Logger.dateProvider = dateProvider;
  }

  public void debug(String message, Throwable ...throwables) {
    if (DEBUG.compareTo(severity) < 0) {
      return;
    }
    log(DEBUG, message);

    logThrowables(throwables);
  }

  public void info(String message, Throwable ...throwables) {
    if (INFO.compareTo(severity) < 0) {
      return;
    }

    log(INFO, message);
    logThrowables(throwables);
  }

  public void warn(String message, Throwable ...throwables) {
    if (WARN.compareTo(severity) < 0) {
      return;
    }

    log(WARN, message);
    logThrowables(throwables);
  }

  public void error(String message, Throwable ...throwables) {
    if (ERROR.compareTo(severity) < 0) {
      return;
    }

    log(ERROR, message);
    logThrowables(throwables);
  }

  public void fatal(String message, Throwable ...throwables) {
    log(FATAL, message);
    logThrowables(throwables);
  }

  private void log(Severity severity, String message) {
    String formattedMessage = format(severity, message);
    APPENDERS.forEach(a -> a.append(formattedMessage));
  }

  private void logThrowables(Throwable[] throwables) {
    for (Throwable throwable : throwables) {
      APPENDERS.forEach(a -> a.appendStackTrace(throwable));
    }
  }

  private String format(Severity severity, String message) {
    return format.replace("%timestamp%", dateProvider.get().format(DATE_TIME_FORMATTER))
        .replace("%severity%", severity.toString())
        .replace("%class%", this.clazz.getCanonicalName())
        .replace("%message%", message);
  }
}
