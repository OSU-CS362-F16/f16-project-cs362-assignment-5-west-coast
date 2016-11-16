package osu.cs362.URLValidator;



import org.jaxen.function.StringFunction;

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
	static Map<String, String> mapUrls = new HashMap<String, String>();
	//static Map<String, long> optionValues = new HashMap<String, long>();
	
	@Before
	public void runBefore() {
		mapUrls.put("Valid_http", "http://foo.bar.com/");
		mapUrls.put("TwoSlashes_Valid_http", "http://foo//foo.bar.com//");
		mapUrls.put("Invalid_http", "http/foo.bar.com/");  // invalid
		mapUrls.put("Fragment_http", "http://foo");   // NO_FRAGMENTS
	
		mapUrls.put("Valid_https", "https://foo.bar.com/");
		mapUrls.put("TwoSlashes_Valid_https","https://foo//foo.bar.com//");
		mapUrls.put("Invalid_https", "https//foo..bar.com/");
		mapUrls.put("Fragment_https", "https://foo");
		
		mapUrls.put("Valid_ftp", "ftp://foo.bar.com/");
		mapUrls.put("TwoSlashes_Valid_ftp", "ftp://foo//foo.bar.com//");
		mapUrls.put("Invalid_ftp","ftp:///foo.bar.com/");
		mapUrls.put("Fragment_ftp","ftp://foo");
		
		mapUrls.put("Valid_file", "file://C:/foo.bar.html/");  // ALLOW_LOCAL_URLS
		mapUrls.put("TwoSlashes_valid_file", "file://C://foo//foo.bar.html//"); // ALLOW_LOCAL_URLS &&  ALLOW_2_SLASHES
		mapUrls.put("Invalid_file","file///C:/foo.bar.html/"); // invalid
		mapUrls.put("Fragment_file", "file://c:/foo");
		
		mapUrls.put("Domain_abc","abc://foo.bar.com/"); // regexStr valid
		mapUrls.put("Domain_123","123://foo.bar.com/"); // regexStr invalid - numbers
		mapUrls.put("Valid_HTTP","HTTP://foo.bar.com/" );// regexStr invalid - uppercase
		mapUrls.put("Valid_http_FOO.bar.com","http://FOO.bar.com/" );
		mapUrls.put("Valid_http_foo.BAR.com","http://foo.BAR.com/" );
		mapUrls.put("Valid_http","http://foo.bar.COM/" );
	}
	
	
	static String[] arrayUrls
	= {
	"http:/foo.bar.com/",  // valid http URL
	"http://foo//foo.bar.com//",  // ALLOW_2_SLASHES
	"http//foo.bar.com/",  // invalid
	"http://foo",   // NO_FRAGMENTS
	
	"https:/foo.bar.com/",
	"https://foo//foo.bar.com//",
	"https//foo..bar.com/",
	"https://foo",
	
	"ftp:/foo.bar.com/",
	"ftp://foo//foo.bar.com//",
	"ftp:///foo.bar.com/",
	"ftp://foo",
	
	"file:/C:/foo.bar.html/",  // ALLOW_LOCAL_URLS
	"file://C://foo//foo.bar.html//", // ALLOW_LOCAL_URLS &&  ALLOW_2_SLASHES
	"file///C:/foo.bar.html/", // invalid
	"file://c:/foo", 
	
	"abc://foo.bar.com/", // regexStr valid
	"123://foo.bar.com/", // regexStr invalid - numbers
	"HTTP://foo.bar.com/", // regexStr invalid - uppercase
	"http://FOO.bar.com/",
	"http://foo.BAR.com/",
	"http://foo.bar.COM/"
	};


	static String[] defaultSchemes = {"defaults {http, https , ftp}" }; 
	static String[] schemes = {"http","https"};  // default includes 'ftp' as well
	static String regexStr = "[a-z]";
	static String regexNotSpecified = "not specified in constructor ";
	
	
	private void checkUrls(boolean[] chkStates, String assertStr, UrlValidator uv) {
		int idx = 0;
		for(String s:arrayUrls) {
			assertEquals(assertStr+ "; \"" + s + "\"", chkStates[idx], uv.isValid(s));
			idx++;
		}
	}
	
	private Map<String, Boolean> checkmapUrls(UrlValidator uv) {
		Map<String, Boolean> resultsUrls = new HashMap<String, Boolean>();
		for  (String s:arrayUrls) {
			resultsUrls.put(s, uv.isValid(s));
		}
		return resultsUrls;
	}
		
	private String printOptions(Integer val){
		switch (val) {
		case 0:
			return new String("NO options set");
		case 1:
			return new String("ALLOW_ALL_SCHEMES");
		case 2:
			return new String("ALLOW_2_SLASHES");
		case 3: 
			return new String("ALLOW_ALL_SCHEMES + ALLOW_2_SLASHES");
		case 4:
			return new String("NO_FRAGMENTS");
		case 5:
			return new String("NO_FRAGMENTS + ALLOW_ALL_SCHEMES");
		case 6:
			return new String("NO_FRAGMENTS + ALLOW_2_SLASHES");
		case 7:
			return new String("NO_FRAGMENTS + ALLOW_2_SLASHES + ALLOW_ALL_SCHEMES");
		case 8:
			return new String("ALLOW_LOCAL_URLS");
		case 9:
			return new String("ALLOW_LOCAL_URLS + ALLOW_ALL_SCHEMES");
		case 10:
			return new String("ALLOW_LOCAL_URLS + ALLOW_2_SLASHES");
		case 11:
			return new String("ALLOW_LOCAL_URLS + ALLOW_2_SLASHES + ALLOW_ALL_SCHEMES");
		case 12:
			return new String("ALLOW_LOCAL_URLS + NO_FRAGMENTS");
		case 13:
			return new String("ALLOW_LOCAL_URLS + NO_FRAGMENTS + ALLOW_ALL_SCHEMES");
		case 14:
			return new String("ALLOW_LOCAL_URLS + NO_FRAGMENTS + ALLOW_2_SLASHES");
		case 15:
			return new String("ALLOW_LOCAL_URLS + NO_FRAGMENTS + ALLOW_2_SLASHES + ALLOW_ALL_SCHEMES");					 
		}
		return new String("Unexpected combination of options with value: " + val);
	}
	
	private String printUrlValidatorInfo(String[] schemes, Integer options, String regex) {
		return new String("UrlValidator construction: schemes:  " + printSchemes(schemes)
		 	+ ", options: " + printOptions(options) + ", regular expression: " + regex);
	}
	
	private void printMap(Map<String, Boolean> map ) {
		for(Map.Entry<String, Boolean> m:map.entrySet()) {
			System.out.println("\"" + m.getKey().toString() +"\"; " +m.getValue().toString());
		}
	}
	
	@Test
	public void URLValidatorSchemesConstructorTest() {
		UrlValidator uv = new UrlValidator(schemes);
		String s = printUrlValidatorInfo(schemes, (int) uv.getOptions(), regexNotSpecified);
		System.out.println("\nTest constructor: UrlValidator(" + printSchemes(schemes) + ")");
		System.out.println(s);
		Map<String, Boolean> checkMap = checkmapUrls(uv);
		printMap(checkMap);
//		checkUrls( new boolean[]{ true, false, false, false, 
//					true, false, false, false, 
//					false, false, false, false,
//					false, false, false,
//					false, false, false }
//				, s, uv);
	}
	
	@Test
	public void URLValidatorConstructorTest() {
		UrlValidator uv = new UrlValidator();
		String s = printUrlValidatorInfo(defaultSchemes, (int) uv.getOptions(), regexNotSpecified);
		System.out.println("\nTest constructor: UrlValidator()");
		System.out.println(s);
		Map<String, Boolean> checkMap = checkmapUrls(uv);
		printMap(checkMap);
//		checkUrls( new boolean[]{ true, false, false, false, 
//				true, false, false, false, 
//				true, false, false, false,
//				false, false, false,
//				false, false, false }
//			, s, uv);
	}
	
	
	@Test
	public void URLValidatorOptionsConstructorTest() {
		for (int i=0; i<16; i++) {
			UrlValidator uv = new UrlValidator((long) i);
			String s = printUrlValidatorInfo(defaultSchemes, (int) uv.getOptions(), regexNotSpecified);
			System.out.println("\nTest constructor: UrlValidator("+ printOptions(i) + ")");
			System.out.println(s);
			Map<String, Boolean> checkMap = checkmapUrls(uv);
			printMap(checkMap);
		}
//		checkUrls( new boolean[]{ true, true, false, false, 
//				true, true, false, false, 
//				true, true, false, false,
//				false, false, false,
//				false, false, false }
//			, "UrlValidator constructed with \"ALLOW_2_SLASHES\" ", uv);
	}
	
	@Test
	public void URLValidatorSchemesOptionsConstructorTest() {
		for (int i=0; i<16; i++) {
			UrlValidator uv = new UrlValidator(schemes, (long) i);
			String s = printUrlValidatorInfo(schemes, (int) uv.getOptions(), regexNotSpecified);
			System.out.println("\nTest constructor: UrlValidator(" + printSchemes(schemes) + ", " + printOptions(i) + ")");
			System.out.println(s);
			Map<String, Boolean> checkMap = checkmapUrls(uv);
			printMap(checkMap);
		}
//		checkUrls( new boolean[]{ true, false, false, false, 
//				true, false, false, false, 
//				false, false, false, false,
//				false, false, false,
//				false, false, false }
//			, s, uv);
	}
	
	@Test
	public void URLValidatorRegexOptionsConstructorTest() {
		RegexValidator authorityValidator = new RegexValidator(regexStr);
		for (int i=0; i<16; i++) {
			UrlValidator uv = new UrlValidator(authorityValidator, (long) i);
			String s = printUrlValidatorInfo(defaultSchemes, (int) uv.getOptions(), regexStr);
			System.out.println("\nTest constructor: UrlValidator(" + regexStr + ", " + printOptions(i) + ")");
			System.out.println(s);
			Map<String, Boolean> checkMap = checkmapUrls(uv);
			printMap(checkMap);
		}
//		checkUrls( new boolean[]{ true, false, false, false, 
//				true, false, false, false, 
//				true, false, false, false,
//				true, false, false,
//				true, false, false }
//			, "UrlValidator constructed with authority validator: " + regexStr + " and \"ALLOW_LOCAL_URLS\"", uv);
	}
	
	@Test
	public void URLValidatorSchemesRegexOptionsConstructorTest() {
		RegexValidator authorityValidator = new RegexValidator(regexStr);
		for (int i=0; i<16; i++) {
			UrlValidator uv = new UrlValidator(schemes, authorityValidator, (long) i);
			String s = printUrlValidatorInfo(schemes, (int) uv.getOptions(), regexStr);
			System.out.println("\nTest constructor: UrlValidator(" + printSchemes(schemes) + ",  "+ regexStr + ", " + printOptions(i) + ")");
			System.out.println(s);
			Map<String, Boolean> checkMap = checkmapUrls(uv);
			printMap(checkMap);
		}		
//		checkUrls( new boolean[]{ true, false, false, false, 
//				true, false, false, false, 
//				true, false, false, false,
//				true, false, false,
//				true, false, false }
//			, "UrlValidator constructed with authority validator: " + regexStr + " and \"ALLOW_LOCAL_URLS\"", uv);
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
