package ch.ethy.transact.authentication;

import java.time.*;

public interface DateTimeProvider {
  ZonedDateTime now();
}
