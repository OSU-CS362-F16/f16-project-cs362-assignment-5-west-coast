package osu.cs362.URLValidator;

import org.jaxen.function.StringFunction;

//References used in development of this Test code
//CS362 class notes and lecture
//Java Language Specification Java SE 8 Edition (dated 2015-02-13)
//http://docs.oracle.com/javase/7/docs/api/java/util/Random.html
//reference: http://stackoverflow.com/questions/31423643/try-catch-in-a-junit-test
// org.apache.commons.validator
// stackoverflow.com/questions


import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

public class URLValidatorUnitTest {
	private boolean verbose = true;

	// schemes
	static String[] defaultSchemes = {"http", "https", "ftp" };
	static String[] schemes = {"http","https"};  // default includes 'ftp' as well

	// regex option
	static String regexStr = "[a-z]";
	static String regexNotSpecified = "not specified in constructor ";

	@Test
	public void IsValidFileSchemeTests() {
		String[] schemes = {"file"};
		UrlValidator uv = new UrlValidator(schemes);

		assertTrue("file:///path/index.html", uv.isValid("file:///path/index.html"));

		// BUG 11: File schemes don't seem to validate correctly
		// PER: RFC 2396

		/*
		 The path may consist of a sequence of path segments separated by a
	   single slash "/" character.  Within a path segment, the characters
	   "/", ";", "=", and "?" are reserved.  Each path segment may include a
	   sequence of parameters, indicated by the semicolon ";" character.
	   The parameters are not significant to the parsing of relative
	   references.
		 */

		//assertTrue("file:index.html", uv.isValid("file:index.html"));
		//assertTrue("file:/path/index.html", uv.isValid("file:/path/index.html"));
		//assertTrue("file://path/index.html", uv.isValid("file://path/index.html"));
	}

	@Test
	public void IsValidComponentsTest() {
		UrlValidator uv = new UrlValidator();
		String testString = "http://www.ics.uci.edu/pub/ietf/uri/historical.html?abc=1";
		//System.out.println("\nComponents test for \"" + testString +"\"");
		assertTrue("Url \"" + testString + "\" returns false", uv.isValid(testString));
		//System.out.println("end components test\n");
	}

	@Test
	public void IsValidUrlFragmentTest() {
		UrlValidator uv = new UrlValidator();
		// fragments are allowed
		assertTrue("Fragments allowed: \"" + "http://www.ics.uci.edu/pub/ietf/uri/historical.html#WARNING" + "\"", uv.isValid("http://www.ics.uci.edu/pub/ietf/uri/historical.html#WARNING"));
		UrlValidator uvNoFragment = new UrlValidator(UrlValidator.NO_FRAGMENTS);
		assertFalse("Fragments not allowed: \"" + "http://www.ics.uci.edu/pub/ietf/uri/historical.html#WARNING" + "\"", uvNoFragment.isValid("http://www.ics.uci.edu/pub/ietf/uri/historical.html#WARNING"));
	}


	@Test
	public void IsValidAuthorityTest() {
		UrlValidator uv = new UrlValidator();
		assertFalse("null input to isValidAuthority returns true",uv.isValidAuthority(null));
		assertFalse("authorityMatcher should not match but returns true",uv.isValidAuthority(""));
		assertTrue("www.cnn.com:123",uv.isValidAuthority("www.cnn.com:123"));
		assertTrue("http://www.cnn.com:123/foo/", uv.isValid("http://www.cnn.com:123/foo/"));

		// Hits the String extra = authorityMatcher.group(PARSE_AUTHORITY_EXTRA); block in URLValidator
		assertFalse("http://www.cnn.com:123:abcde/foo/", uv.isValid("http://www.cnn.com:123:abcde/foo/"));

		// BUG 12: Authentication information is part of the authority, but always fails
		//assertTrue("bob:foo@www.cnn.com:123",uv.isValidAuthority("bob:foo@www.cnn.com:123"));
		//assertTrue("http://bob:foo@www.cnn.com",uv.isValid("http://bob:foo@www.cnn.com"));

		assertFalse("authority \"www\" is not valid",uv.isValid("http://www/Addressing/"));
	}

	// Checking conditional logic around custom authority validator
	@Test
	public void IsValidCustomAuthorityTest() {
		RegexValidator av = new RegexValidator("foo");
		UrlValidator uv = new UrlValidator(null, av, 0);
		assertTrue("custom validator expects foo", uv.isValidAuthority("foo"));
		assertFalse("custom validator - no match", uv.isValidAuthority("blerp"));
		assertFalse("custom validator - null", uv.isValidAuthority(null));

		// Hitting the check on extra matches...
		assertFalse("custom validator - no match", uv.isValidAuthority("foo:foo.blerp asdf"));
	}

	@Test
	public void IsValidPathTest() {
		UrlValidator uv = new UrlValidator();
		assertFalse("null input to isValidPath returns true",uv.isValidPath(null));
		//should blank return false???
		assertTrue("empty path \"\" returns false",uv.isValidPath(""));
		// Absolute Path
		assertTrue("absolute path returns true", uv.isValidPath("/img/img.jpg"));

		// Some weird edge cases for semicolons and path parameters
		// From http://doriantaylor.com/policy/http-url-path-parameter-syntax
		assertTrue("/path/name;param1;p2;p3 is valid", uv.isValidPath("/path/name;param1;p2;p3"));
		assertTrue("/path/param=value;p2 is valid", uv.isValidPath("/path/param=value;p2"));
		assertTrue(";param=val1,val2,val3 is valid", uv.isValidPath("/;param=val1,val2,val3"));

		assertFalse("IsValidPath - Complex, Double and Single Dots, Double-Slash Disallowed", uv.isValidPath("/F/LUUGNNPWO/MUMSS/../DFYH./MARWDO/RHN//././JIBPWDJHFDOGW/G/QCJ.html"));
		assertTrue("IsValidPath - Complex, Double and Single Dots, No Double-Slash", uv.isValidPath("/F/LUUGNNPWO/MUMSS/../DFYH./MARWDO/RHN/./JIBPWDJHFDOGW/G/QCJ.html"));
		assertTrue("IsValidPath - Complex, Single Dots", uv.isValidPath("/F/PSEJK/LUUGNNPWO.MUMSS/DFYHMARWDO/RHN/./JIBPWDJHFDOGW/G/QCJ"));

		// Some esoteric edge cases for non alpha-numeric chars in URLs
		// From http://doriantaylor.com/policy/other-non-alpha-numeric-characters-in-http-urls
		assertTrue("http://news.example.com/money/$5.2-billion-merger is valid", uv.isValid("http://news.example.com/money/$5.2-billion-merger"));
		assertTrue("http://blog.example.com/wutchoo-talkin'-bout-willis!? is valid", uv.isValid("http://blog.example.com/wutchoo-talkin'-bout-willis!?"));
		assertTrue("https://spam.example.com/viagra-only-$2-per-pill* is valid", uv.isValid("https://spam.example.com/viagra-only-$2-per-pill*"));
	}

	@Test
	public void IsValidQueryTest() {
		UrlValidator uv = new UrlValidator();
		assertTrue("null input to isValidQuery should return true",uv.isValidQuery(null));
		assertTrue("query of blank space \"\" returns true",uv.isValidQuery(""));
		// What is a true query???
		assertTrue("valid query \"?([^#]*)\" returns false for \"?abc=1\"", uv.isValidQuery("abc=1") );
		assertTrue("valid query, mulitple params", uv.isValidQuery("abc=1&def=2"));

		// BUG (3): isValidQuery() doesn't really check to see if the punctuation between parameters makes sense
		// assertFalse("invalid query section, crazy punctuation", uv.isValidQuery("abc==123&&&=def=1234"));

		// BUG (9): isValidQuery doesn't flag queries with disallowed characters as invalid
		// assertFalse("invalid query - disallowed characters", uv.isValidQuery("?kp.ndvrlq1Q,M'+UZYK?`zqhzb%a>~A\""));

		// QUESTION: Does # represent a missing query?
		// assertFalse("query with \"#\" should return false",uv.isValidQuery("#"));
	}

	@Test
	public void IsValidFragment_NoFragmentsTest() {
		UrlValidatorExtension uvNoFragments = new UrlValidatorExtension(UrlValidator.NO_FRAGMENTS);
		assertFalse("NO_FRAGMENTS - otherwise valid fragment", uvNoFragments.isValidFragment("WARNING"));
		assertFalse("NO_FRAGMENTS - random punctuation", uvNoFragments.isValidFragment("./?$23&%*(@#``!!($)@[];,,..//\\*&%^$%#@!*())"));
		assertTrue("NO_FRAGMENTS - isValidFragment is NULL",uvNoFragments.isValidFragment(null));
	}

	@Test
	public void IsValidFragmentTest() {
 		UrlValidatorExtension uv = new UrlValidatorExtension();  // by default allow fragments
 		assertTrue("NO_FRAGMENTS is not set",uv.isValidFragment("WARNING"));
 		assertTrue("Valid Fragment",uv.isValidFragment("WARNING"));

 		// BUG (4): isValidFragment() fails to detect improper usage of % (there's probably more bugs to mine in this vein...)
 		// assertFalse("FRAGMENTS - random punctuation", uv.isValidFragment("./?%$2%3&%*%(@#`%`!!(%$)@[];,,%..//\\*%&%^$%%#@!*%())%"));
 		assertTrue("FRAGMENTS - percent encoding", uv.isValidFragment("If%20you%20haven%27t%20got%20anything%20nice%20to%20say%20about%20anybody%2C%20come%20sit%20next%20to%20me.%20"));
 		assertTrue("FRAGMENTS - NULL Fragment is valid",uv.isValidFragment(null));
 		assertTrue("FRAGMENTS - Empty Fragment is valid",uv.isValidFragment("#"));

 		// BUG (8): isValidFragment() fails to detect special characters in the fragment
 		// assertFalse("FRAGMENTS - Special Characters", uv.isValidFragment("http://www.ics.uci.edu/pub/ietf/uri/historical.html#EnZ^b1XXL"));
 		assertTrue("FRAGMENTS - No Special Characters", uv.isValidFragment("http://www.ics.uci.edu/pub/ietf/uri/historical.html#EnZb1XXL"));

 		// QUESTION: Technically, anything between the fragment identifier and the end of the URL is valid, but a trailing unencoded space seems weird
 		assertTrue("FRAGMENTS - Empty Fragment followed by space",uv.isValidFragment("# "));
 	}


}
