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
	// List of simple regexs
	static String[] regexList = {
		"^[a-z]{1,10}$",		// character string from 1 to 10 chars long
		"I lost my \\w+",		// string "I lost my " and then any fourth word
		"^[a-z0-9_-]{3,15}$",	// alpha-numeric username regex
		"^[0-9]{1,10}$"			// number string from 1 to 10 numbers long
	};
	
	// Regex list with multiple elements that are empty
	static String[] regexListNull = {
		"\n",
		"",
		"",
		""
	};
	
	// Null regex list
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
		RegexValidator reggie = new RegexValidator("I lost my \\w+");
		RegexValidator reggieCase = new RegexValidator("I lost my \\w+", false);
		RegexValidator reggies = new RegexValidator(regexList);
		RegexValidator reggiesCase = new RegexValidator(regexList, false);
		RegexValidator grouped = new RegexValidator("(I) (lost) ((my) (wallet))");
		
		String[] groups = grouped.match("I lost my wallet");
		//System.out.println(Arrays.toString(groups));
		String[] group2 = grouped.match("lost my wallet");
		String[] group3 = grouped.match(null);
		assertEquals(Arrays.toString(groups), "[I, lost, my wallet, my, wallet]");
		assertEquals(group2, null);
		assertEquals(group3, null);
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
