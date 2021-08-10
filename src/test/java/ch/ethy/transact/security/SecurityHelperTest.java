package ch.ethy.transact.security;

import org.junit.jupiter.api.*;
import org.opentest4j.*;

import java.util.*;

import static ch.ethy.transact.authentication.Base64.*;
import static ch.ethy.transact.security.SecurityHelper.*;
import static org.junit.jupiter.api.Assertions.*;

class SecurityHelperTest {
  @Test
  public void can_generate_random_bytes() {
    byte[] firstBytes = randomBytes(32).get();

    assertEquals(32, firstBytes.length);
  }

  @Test
  public void random_bytes_are_not_equal() {
    byte[] firstBytes = randomBytes(32).get();
    byte[] secondBytes = randomBytes(32).get();

    assertArrayNotEquals(firstBytes, secondBytes);
  }

  @Test
  public void can_hash_and_verify_password() {
    String hash = passwordHasher(10, 128, 8).apply("password");

    assertTrue(passwordVerifier().test(hash, "password"));
  }

  @Test
  public void adds_correct_algorithm_specs() {
    String hash = passwordHasher(37, 64, 8).apply("password");

    String[] hashParts = hash.split("\\.");
    String specs = hashParts[0];

    assertEquals("{\"algorithm\":\"PBKDF2WithHmacSHA512\",\"iterations\":37,\"keyLength\":64}", decodeToString(specs));
  }

  @Test
  public void generates_correct_salt() {
    int saltLength = new Random().nextInt(1000);
    String hash = passwordHasher(10, 128, saltLength).apply("password");

    String[] hashParts = hash.split("\\.");
    String salt = hashParts[1];

    assertEquals(saltLength, decode(salt).length);
  }

  private static void assertArrayNotEquals(byte[] expected, byte[] actual) {
    if (expected == null && actual == null) {
      throw new AssertionFailedError();
    }

    if (expected == null || actual == null) {
      return;
    }

    if (expected.length != actual.length) {
      return;
    }

    for (int i = 0; i < expected.length; i++) {
      if (expected[i] != actual[i]) {
        return;
      }
    }

    String message = "expected: not equal but was: <" + actual + ">";
    throw new AssertionFailedError(message);
  }
}