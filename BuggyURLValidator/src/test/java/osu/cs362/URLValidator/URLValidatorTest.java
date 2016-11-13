package osu.cs362.URLValidator;



//References used in development of this Test code
//CS362 class notes and lecture
//Java Language Specification Java SE 8 Edition (dated 2015-02-13)
//http://docs.oracle.com/javase/7/docs/api/java/util/Random.html
//reference: http://stackoverflow.com/questions/31423643/try-catch-in-a-junit-test
// org.apache.commons.validator

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;



public class URLValidatorTest {
	static String[] arrayUrls = {
	"http://foo.bar.com/",  // valid http URL
	"http://foo//foo.bar.com//",  // ALLOW_2_SLASHES
	"http//foo.bar.com/",  // invalid
	"http://foo",   // NO_FRAGMENTS
	
	"https://foo.bar.com/",
	"https://foo//foo.bar.com//",
	"https//foo.bar.com/",
	"https://foo",
	
	"ftp://foo.bar.com/",
	"ftp://foo//foo.bar.com//",
	"ftp//foo.bar.com/",
	"ftp://foo",
	
	"file:///C:/foo.bar.html/",  // ALLOW_LOCAL_URLS
	"file:///C://foo//foo.bar.html//", // ALLOW_LOCAL_URLS &&  ALLOW_2_SLASHES
	"file///C:/foo.bar.html/", // invalid
	
	"abc://foo.bar.com/", // regexStr valid
	"123://foo.bar.com/", // regexStr invalid - numbers
	"HTTP://foo.bar.com/" // regexStr invalid - uppercase
	};
	
	static String[] schemes = {"http","https"};  // default includes 'ftp' as well
	static String regexStr = "[a-z]";
	
	private void checkUrls(boolean[] chkStates, String assertStr, UrlValidator uv) {
		int idx = 0;
		for(String s:arrayUrls) {
			assertEquals(assertStr+ "; \"" + s + "\"", chkStates[idx], uv.isValid(s));
			idx++;
		}
	}
	
	@Test
	public void URLValidatorSchemesConstructorTest() {
		UrlValidator uv = new UrlValidator(schemes);
		checkUrls( new boolean[]{ true, false, false, false, 
					true, false, false, false, 
					false, false, false, false,
					false, false, false,
					false, false, false }
				, "UrlValidator constructed with " + printSchemes(schemes), uv);
	}
	
	@Test
	public void URLValidatorConstructorTest() {
		UrlValidator uv = new UrlValidator();
		checkUrls( new boolean[]{ true, false, false, false, 
				true, false, false, false, 
				true, false, false, false,
				false, false, false,
				false, false, false }
			, "UrlValidator constructed with no input ", uv);
	}
	
	
	@Test
	public void URLValidatorOptionsConstructorTest() {
		UrlValidator uv = new UrlValidator(UrlValidator.ALLOW_2_SLASHES);
		checkUrls( new boolean[]{ true, true, false, false, 
				true, true, false, false, 
				true, true, false, false,
				false, false, false,
				false, false, false }
			, "UrlValidator constructed with \"ALLOW_2_SLASHES\" ", uv);
	}
	
	@Test
	public void URLValidatorSchemesOptionsConstructorTest() {
		UrlValidator uv = new UrlValidator(schemes, UrlValidator.NO_FRAGMENTS);
		checkUrls( new boolean[]{ true, false, false, false, 
				true, false, false, false, 
				false, false, false, false,
				false, false, false,
				false, false, false }
			, "UrlValidator constructed " + printSchemes(schemes) + " and \"NO_FRAGMENTS\" ", uv);
	}
	
	@Test
	public void URLValidatorRegexOptionsConstructorTest() {
		RegexValidator authorityValidator = new RegexValidator(regexStr);
		UrlValidator uv = new UrlValidator(authorityValidator, UrlValidator.ALLOW_LOCAL_URLS);
		checkUrls( new boolean[]{ true, false, false, false, 
				true, false, false, false, 
				true, false, false, false,
				true, false, false,
				true, false, false }
			, "UrlValidator constructed with authority validator: " + regexStr + " and \"ALLOW_LOCAL_URLS\"", uv);
	}
	
	@Test
	public void URLValidatorSchemesRegexOptionsConstructorTest() {
		RegexValidator authorityValidator = new RegexValidator(regexStr);
		UrlValidator uv = new UrlValidator(schemes, authorityValidator, UrlValidator.NO_FRAGMENTS);
		checkUrls( new boolean[]{ true, false, false, false, 
				true, false, false, false, 
				true, false, false, false,
				true, false, false,
				true, false, false }
			, "UrlValidator constructed with authority validator: " + regexStr + " and \"ALLOW_LOCAL_URLS\"", uv);
	}

	private String printSchemes(String[] arrSchemes) {
		String str;
		str = "{";
		for(String s:arrSchemes) {
			str = str + "[" + s + "]";
		}
		str = str + "}";
		return str;
	}
	
}
