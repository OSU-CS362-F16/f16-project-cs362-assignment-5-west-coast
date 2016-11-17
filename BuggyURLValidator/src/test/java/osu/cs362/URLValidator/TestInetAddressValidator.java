package osu.cs362.URLValidator;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

public class TestInetAddressValidator  {

  @Test
  // Invoking the getInstance() without a parameter should treat local URLs as invalid
  public void testIsValidInet4Address() {
    InetAddressValidator iv = InetAddressValidator.getInstance();

    assertTrue("192.168.1.100",iv.isValidInet4Address("192.168.1.100"));
    assertTrue("10.12.200.123",iv.isValidInet4Address("10.12.200.123"));

    // The last digit is reserved, but it's still a valid IP
    assertTrue("127.0.0.0", iv.isValidInet4Address("127.0.0.0"));

    // BUG: Segment values should be 1 - 255
    // assertFalse("192.168.999.256", iv.isValid("192.168.999.256"));

    // A 32-bit Decimal Representation is technically valid, but maybe not since
    // we're only validating string representations here...
    // assertTrue("3221226219", iv.isValid("3221226219"));

    // An Octal Representation is technically valid, but maybe not since
    // we're only validating string representations here...
    // assertTrue("0300.0000.0002.0353", iv.isValid("0300.0000.0002.0353"));

    // There should be four Segments
    assertFalse("10.12.100", iv.isValidInet4Address("10.12.100"));
    assertFalse("10.12.100.123.123", iv.isValidInet4Address("10.12.100.123.123"));

    // Each segment should be three digits
    assertFalse("190.123.127.1234", iv.isValidInet4Address("192.123.127.1234"));

    // Each segment should be 1-3 digits
    assertFalse("ABC.DEF.GHI.JKL", iv.isValidInet4Address("ABC.DEF.GHI.JKL"));

    // Trying to hit NumberFormatException with no luck...
    assertFalse("AB$.D?!.GH*.J(L", iv.isValidInet4Address("AB$.D?!.GH*.J(L"));
    assertFalse("Kona.Kona.Kona.Kona", iv.isValidInet4Address("Kona.Kona.Kona.Kona"));
    assertFalse("Kona", iv.isValidInet4Address("Kona"));
    assertFalse(" ", iv.isValidInet4Address(" "));
    assertFalse("null", iv.isValidInet4Address(null));

    // Not sure if all 0s is valid
    // assertFalse("000.000.000.000", iv.isValid("000.000.000.000"));
    assertFalse("10..123.120", iv.isValidInet4Address("10..123.120"));




  }
}
