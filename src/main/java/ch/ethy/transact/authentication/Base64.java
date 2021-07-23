package ch.ethy.transact.authentication;

import java.nio.charset.*;

public class Base64 {
  public static String encode(String source) {
    return encode(source.getBytes(StandardCharsets.UTF_8));
  }

  public static String encode(byte[] source) {
    return new String(java.util.Base64.getUrlEncoder().withoutPadding().encode(source), StandardCharsets.UTF_8);
  }

  public static byte[] decode(String base64) {
    return java.util.Base64.getUrlDecoder().decode(base64.getBytes(StandardCharsets.UTF_8));
  }

  public static String decodeToString(String base64) {
    byte[] decodedBytes = decode(base64);
    return new String(decodedBytes, StandardCharsets.UTF_8);
  }
}
