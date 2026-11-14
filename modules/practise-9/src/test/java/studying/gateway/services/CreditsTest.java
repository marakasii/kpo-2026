package studying.gateway.services;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import studying.gateway.model.*;

class CreditsTest {
  @Test
  void roundsEachPartUp() {
    assertEquals(2, Credits.cost(1, 1, 100, 200));
    assertEquals(11, Credits.cost(18, 42, 100, 200));
    assertEquals(0, Credits.cost(0, 0, 100, 200));
    assertThrows(IllegalArgumentException.class, () -> Credits.cost(-1, 0, 1, 1));
  }

  @Test
  void multiplicationDoesNotOverflowBeforeDivision() {
    assertEquals(Long.MAX_VALUE, Credits.cost(1000, 0, Long.MAX_VALUE, 0));
    assertThrows(ArithmeticException.class, () -> Credits.cost(2000, 0, Long.MAX_VALUE, 0));
  }

  @Test
  void underestimatedReservationCannotConsumeOtherReserves() {
    Wallet wallet = new Wallet(new User("test@example.com"));
    wallet.topUp(100);
    wallet.reserve(80);
    wallet.reserve(10);
    assertThrows(IllegalArgumentException.class, () -> wallet.charge(91, 80));
    assertEquals(100, wallet.getBalance());
    assertEquals(90, wallet.getReservedBalance());
    wallet.charge(85, 80);
    assertEquals(15, wallet.getBalance());
    assertEquals(10, wallet.getReservedBalance());
  }
}
