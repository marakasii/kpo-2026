package studying.gateway.services;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.HexFormat;

public final class Credits {
  private Credits() {}

  public static long cost(int input, int output, long inputPrice, long outputPrice) {
    return Math.addExact(part(input, inputPrice), part(output, outputPrice));
  }

  private static long part(int tokens, long price) {
    if (tokens < 0 || price < 0) throw new IllegalArgumentException("Negative usage or price");
    return BigInteger.valueOf(tokens)
        .multiply(BigInteger.valueOf(price))
        .add(BigInteger.valueOf(999))
        .divide(BigInteger.valueOf(1000))
        .longValueExact();
  }

  public static String hash(String value) {
    try {
      return HexFormat.of()
          .formatHex(
              MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    }
  }
}
