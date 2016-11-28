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
	
	static String[] regexList = {
		"^[a-z]{1,10}$",
		"I lost my \\w+",
		"^[a-z0-9_-]{3,15}$",
		"^[0-9]{1,10}$"
	};
	
	static String[] regexListNull = {
		"\n",
		"",
		"",
		""
	};
	
	static String[] regexNull = null;
	
	@Test
	public void testRegexConstructor() {
		boolean fail = false;
		try {
			RegexValidator reggiesListNull = new RegexValidator(regexListNull);
		}
		catch(IllegalArgumentException e) {
			fail = true;
		}
		assertEquals(true, fail);
		fail = false;
		try {
			RegexValidator reggiesNull = new RegexValidator(regexNull);
		}
		catch(IllegalArgumentException e) {
			fail = true;
		}
		assertEquals(true, fail);
	}
	
		
	@Test
	public void testIsValid() {
		
		RegexValidator reggie = new RegexValidator("I lost my \\w+");
		RegexValidator reggieCase = new RegexValidator("I lost my \\w+", false);
		RegexValidator reggies = new RegexValidator(regexList);
		RegexValidator reggiesCase = new RegexValidator(regexList, false);
		
		assertTrue(reggie.isValid("I lost my wallet"));
		assertTrue(reggies.isValid("I lost my wallet"));
		assertTrue(reggiesCase.isValid("I lost my WALLET"));
		assertFalse(reggies.isValid(null));
		assertFalse(reggie.isValid(null));
		assertFalse(reggies.isValid("?"));
	}
	
	@Test
	public void testMatch() {
		
	}
	
	@Test
	public void testValidate() {
		RegexValidator reggie = new RegexValidator("I lost my \\w+");
		RegexValidator reggieCase = new RegexValidator("I lost my \\w+", false);
		RegexValidator grouped = new RegexValidator("(I) (lost) ((my) (wallet))");
		RegexValidator group = new RegexValidator("(I lost my wallet)");
		RegexValidator reggies = new RegexValidator(regexList);
		RegexValidator reggiesCase = new RegexValidator(regexList, false);
		assertEquals("", reggie.validate("I lost my wallet"));
		assertEquals(null, reggie.validate(null));
		assertEquals("Ilostmy walletmywallet", grouped.validate("I lost my wallet"));
		assertEquals("I lost my wallet", group.validate("I lost my wallet"));
		assertEquals(null, group.validate("I lost my wall"));
	}
	
	@Test
	public void testToString() {
		RegexValidator reggie = new RegexValidator("I lost my \\w+");
		RegexValidator reggies = new RegexValidator(regexList);
		assertEquals(reggies.toString(), "RegexValidator{^[a-z]{1,10}$,I lost my \\w+,^[a-z0-9_-]{3,15}$,^[0-9]{1,10}$}");
		assertEquals(reggie.toString(), "RegexValidator{I lost my \\w+}");
	}
}
