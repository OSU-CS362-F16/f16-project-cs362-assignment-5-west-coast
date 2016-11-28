package osu.cs362.URLValidator;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

public class TestRegexValidator  {

  @Test
  // Invoking the getInstance() without a parameter should treat local URLs as invalid
  public void testRegexValidatorCaseSensitiveSingleString() {

    RegexValidator rv = new RegexValidator("a");
    assertTrue(rv.isValid("a"));
    assertFalse(rv.isValid("A"));
    assertFalse(rv.isValid("b"));

    RegexValidator rv2= new RegexValidator("^\\d+(\\.\\d+)?");
    assertTrue(rv2.isValid("5"));
    assertTrue(rv2.isValid("15"));
    assertTrue(rv2.isValid("1.42"));
    assertTrue(rv2.isValid("123.421"));
    assertFalse(rv2.isValid("bloop"));

  }
}
