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
	static String regexStr = "^[\\w-\\.]*$";
	static String regexNotSpecified = "not specified in constructor ";

	@Test
	public void IsValidFileSchemeTests() {
		UrlValidator uv = new UrlValidator();
		// QUESTION: I'm pretty sure this is valid
		//assertTrue("file:///path/index.html", uv.isValid("file:///path/index.html"));
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
		UrlValidatorExtension uv = new UrlValidatorExtension();
		assertFalse("null input to isValidAuthority returns true",uv.isValidAuthority(null));
		assertFalse("authorityMatcher should not match but returns true",uv.isValidAuthority(""));
		assertTrue("www.cnn.com:123",uv.isValidAuthority("www.cnn.com:123"));
		assertTrue("http://www.cnn.com:123/foo/", uv.isValid("http://www.cnn.com:123/foo/"));

		// Hits the String extra = authorityMatcher.group(PARSE_AUTHORITY_EXTRA); block in URLValidator
		assertFalse("http://www.cnn.com:123:abcde/foo/", uv.isValid("http://www.cnn.com:123:abcde/foo/"));

		// BUG: Authentication information is part of the authority, but always fails
		// assertTrue("bob:foo@www.cnn.com:123",uv.isValidAuthority("bob:foo@www.cnn.com:123"));

		assertFalse("authority \"www\" is not valid",uv.isValid("http://www/Addressing/"));
		assertTrue("authority \"www.w3.org\" is valid",uv.isValid("http://www.w3.org/Addressing/"));
		RegexValidator authorityValidator = new RegexValidator("[a-z]");
		UrlValidatorExtension uvR = new UrlValidatorExtension(null, authorityValidator, UrlValidator.NO_FRAGMENTS);
		assertTrue("authority \"www.w3.org\" is valid",uvR.isValid("http://www.w3.org"));
		assertFalse("authority \"WWW.W3.ORG\" is not allowed for authority \"[a-z]\"",uvR.isValid("HTTP://WWW.W3.ORG"));

	}

	@Test
	public void IsValidPathTest() {
		UrlValidator uv = new UrlValidator();
		assertFalse("null input to isValidPath returns true",uv.isValidPath(null));
		//should blank return false???
		assertTrue("empty path \"\" returns false",uv.isValidPath(""));

		assertFalse("IsValidPath - Complex, Double and Single Dots, Double-Slash Disallowed", uv.isValidPath("/F/LUUGNNPWO/MUMSS/../DFYH./MARWDO/RHN//././JIBPWDJHFDOGW/G/QCJ.html"));
		assertTrue("IsValidPath - Complex, Double and Single Dots, No Double-Slash", uv.isValidPath("/F/LUUGNNPWO/MUMSS/../DFYH./MARWDO/RHN/./JIBPWDJHFDOGW/G/QCJ.html"));
		assertTrue("IsValidPath - Complex, Single Dots", uv.isValidPath("/F/PSEJK/LUUGNNPWO.MUMSS/DFYHMARWDO/RHN/./JIBPWDJHFDOGW/G/QCJ"));
	}

	@Test
	public void IsValidQueryTest() {
		UrlValidatorExtension uv = new UrlValidatorExtension();
		assertTrue("null input to isValidQuery should return true",uv.isValidQuery(null));
		assertTrue("query of blank space \"\" returns true",uv.isValidQuery(""));
		// What is a true query???
		assertTrue("valid query \"?([^#]*)\" returns false for \"?abc=1\"", uv.isValidQuery("abc=1") );
		assertTrue("valid query, mulitple params", uv.isValidQuery("abc=1&def=2"));

		// BUG (3): isValidQuery() doesn't really check to see if the punctuation between parameters makes sense
		// assertFalse("invalid query section, crazy punctuation", uv.isValidQuery("abc==123&&&=def=1234"));

		// BUG (9): isValidQuery doesn't flag queries with disallowed characters as invalid
		// assertFalse("invalid query - disallowed characters", uv.isValidQuery("?kp.ndvrlq1Q,M'+UZYK?`zqhzb%a>~A\""));

		// Does # represent a missing query?
		//assertFalse("query with \"#\" should return false",uv.isValidQuery("#"));
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
