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
	public void IsValidComponentsTest() {
		UrlValidator uv = new UrlValidator();
		String testString = "http://www.ics.uci.edu/pub/ietf/uri/historical.html?abc=1";
		System.out.println("\nComponents test for \"" + testString +"\"");
		assertTrue("Url \"" + testString + "\" returns false", uv.isValid(testString));
		System.out.println("end components test\n");
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
		assertTrue("authority \"www.w3.org\" is valid",uv.isValid("http://www.w3.org/Addressing/"));
		assertFalse("authority \"www\" is not valid",uv.isValid("http://www/Addressing/"));
	}

	@Test
	public void IsValidPathTest() {
		UrlValidatorExtension uv = new UrlValidatorExtension();
		assertFalse("null input to isValidPath returns true",uv.isValidPath(null));
		//should blank return false???
		assertTrue("empty path \"\" returns false",uv.isValidPath(""));
	}
	
	@Test
	public void IsValidQueryTest() {
		UrlValidatorExtension uv = new UrlValidatorExtension();
		assertTrue("null input to isValidQuery should return true",uv.isValidQuery(null));
		// should blank return false?
		assertFalse("query of blank space \"\" returns true",uv.isValidQuery(""));
		assertFalse("query with \"#\" should return false",uv.isValidQuery("#"));
		// What is a true query???
		assertTrue("valid query \"?([^#]*)\" returns false for \"?abc=1\"", uv.isValidQuery("?abc=1") );
	}
	
	@Test
	public void IsValidFragmentTest() {
		UrlValidatorExtension uvNoFragments = new UrlValidatorExtension(UrlValidator.NO_FRAGMENTS);
		assertFalse("NO_FRAGMENTS is set", uvNoFragments.isValidFragment("WARNING"));
		assertTrue("null input to isValidFragment returns false",uvNoFragments.isValidFragment(null));
		UrlValidatorExtension uv = new UrlValidatorExtension();  // by default allow fragments
		assertTrue("NO_FRAGMENTS is not set",uv.isValidFragment("WARNING"));
	}
	
}
