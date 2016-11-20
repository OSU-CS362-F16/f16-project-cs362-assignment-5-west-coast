package osu.cs362.URLValidator;

import org.jaxen.function.StringFunction;

//References used in development of this Test code
//CS362 class notes and lecture
//Java Language Specification Java SE 8 Edition (dated 2015-02-13)
//http://docs.oracle.com/javase/7/docs/api/java/util/Random.html
//reference: http://stackoverflow.com/questions/31423643/try-catch-in-a-junit-test
// org.apache.commons.validator
// stackoverflow.com/questions


import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.BufferedWriter;

public class URLValidatorTest {
	private List<String> listResults = new ArrayList<String>();
	private int testCaseIndex = 0;
	private boolean verbose = false;

	// list of test strings
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
	"http://foo.bar.COM/",
	
	"\\",
	"\\\\",	
	"http:\\foo.bar.com\\",  // valid http URL
	"http:\\\\foo\\\\foo.bar.com\\",  // ALLOW_2_SLASHES
	"http\\\\foo.bar.com\\",  // invalid
	"http:\\\\foo",   // NO_FRAGMENTS

	"10.112.104.5://foo.bar.com/",
	"10.112.104.5:3000",
	"",
	":",
	"//",
	"//../////",
	"Réal.com"
	};


	// schemes
	static String[] defaultSchemes = {"http", "https", "ftp" }; 
	static String[] schemes = {"http","https"};  // default includes 'ftp' as well
	
	// regex option
	static String regexStr = "[a-z]";
	static String regexNotSpecified = "not specified in constructor ";
	
	// options
	private long[] options = {
			0,
			UrlValidator.ALLOW_ALL_SCHEMES,
			UrlValidator.ALLOW_2_SLASHES,
			UrlValidator.ALLOW_ALL_SCHEMES + UrlValidator.ALLOW_2_SLASHES,			
			UrlValidator.NO_FRAGMENTS,
			UrlValidator.NO_FRAGMENTS + UrlValidator.ALLOW_ALL_SCHEMES,
			UrlValidator.NO_FRAGMENTS + UrlValidator.ALLOW_2_SLASHES,
			UrlValidator.NO_FRAGMENTS + UrlValidator.ALLOW_ALL_SCHEMES + UrlValidator.ALLOW_2_SLASHES,
			UrlValidator.ALLOW_LOCAL_URLS,
			UrlValidator.ALLOW_LOCAL_URLS + UrlValidator.ALLOW_ALL_SCHEMES,
			UrlValidator.ALLOW_LOCAL_URLS + UrlValidator.ALLOW_2_SLASHES,
			UrlValidator.ALLOW_LOCAL_URLS + UrlValidator.ALLOW_ALL_SCHEMES + UrlValidator.ALLOW_2_SLASHES,			
			UrlValidator.ALLOW_LOCAL_URLS + UrlValidator.NO_FRAGMENTS,
			UrlValidator.ALLOW_LOCAL_URLS + UrlValidator.NO_FRAGMENTS + UrlValidator.ALLOW_ALL_SCHEMES,
			UrlValidator.ALLOW_LOCAL_URLS + UrlValidator.NO_FRAGMENTS + UrlValidator.ALLOW_2_SLASHES,
			UrlValidator.ALLOW_LOCAL_URLS + UrlValidator.NO_FRAGMENTS + UrlValidator.ALLOW_ALL_SCHEMES + UrlValidator.ALLOW_2_SLASHES,
	};
	
	@Test 
	public void UrlValidatorAllTest() {
		printArrayUrlsTitle();
		URLValidatorSchemesConstructor();
		URLValidatorConstructorTest();
		URLValidatorOptionsConstructorTest();
		URLValidatorSchemesOptionsConstructorTest();
		URLValidatorRegexOptionsConstructorTest();
		URLValidatorSchemesRegexOptionsConstructorTest();
		URLValidatorGetInstanceTest();
		printResults();
	}
	
	@Test
	public void IsValidAuthorityTest() {
		UrlValidatorExtension uv = new UrlValidatorExtension();
		assertEquals("null input to isValidAuthority returns true",false,uv.isValidAuthority(null));
		assertEquals("authorityMatcher should not match but returns true",false,uv.isValidAuthority(""));
		
	}

	@Test
	public void IsValidPathTest() {
		UrlValidatorExtension uv = new UrlValidatorExtension();
		assertEquals("null input to isValidPath returns true",false,uv.isValidPath(null));
		assertEquals("path should not match but returns true",false,uv.isValidPath(""));
	}
	
	@Test
	public void IsValidQuery() {
		UrlValidatorExtension uv = new UrlValidatorExtension();
		assertEquals("null input to isValidQuery returns false",true,uv.isValidQuery(null));
		assertEquals("query should not match but returns true",false,uv.isValidQuery(""));
	}
	
	@Test
	public void IsValidFragment() {
		UrlValidatorExtension uv = new UrlValidatorExtension(UrlValidator.NO_FRAGMENTS);
		assertEquals("NO_FRAGMENTS is set", false, uv.isValidFragment("foo.com"));
		assertEquals("null input to isValidFragment returns false",true,uv.isValidFragment(null));
		assertEquals("fragment should not match but returns true",false,uv.isValidFragment(""));
	}
	
	
	// Print Titles and results from the UrlValidatorAllTest
	public void printArrayUrlsTitle() {
		try {
			PrintWriter urlWriter = new PrintWriter("./target/results.csv", "UTF-8");
			try {
				urlWriter.print("testcase,scheme,option,regex");
				for (int k=0; k<arrayUrls.length; k++){
					if (arrayUrls[k] == "") {
						urlWriter.print(",\"\"");
					}
					else {
						urlWriter.print("," + arrayUrls[k] );
					}
				}
				urlWriter.println(",null,");
			} 
			catch (Exception e) {
				System.out.println("couldn't write results file titles: Message: " + e.getMessage() + " Localized: " + e.getLocalizedMessage());
				System.out.println("");
			}
			urlWriter.close();
		} catch (Exception ee) {
			System.out.println("file creation message: Message: " + ee.getMessage() + " Localized: " + ee.getLocalizedMessage());
			System.out.println("");			
		}		
	}
	
	
	public void printResults() {
		try {
			FileWriter fw = new FileWriter("./target/results.csv", true);
			PrintWriter writer = new PrintWriter(new BufferedWriter(fw));
			try {
				for(int i=0; i<listResults.size(); i++) {
					writer.println(listResults.get(i));
				}			
			} catch (Exception e) {
				System.out.println("couldn't write results file: Message: " + e.getMessage() + " Localized: " + e.getLocalizedMessage());
				System.out.println("");
			}
			writer.close();
		} catch (Exception ee) {
			System.out.println("file creation message: Message: " + ee.getMessage() + " Localized: " + ee.getLocalizedMessage());
			System.out.println("");			
		}
	}
	
	
// add checks later
//	private void checkUrls(boolean[] chkStates, String assertStr, UrlValidator uv) {
//		for (int i = 0; i < arrayUrls.length; i++) {
//			assertEquals(assertStr+ "; \"" + arrayUrls[i] + "\"", chkStates[i], uv.isValid(arrayUrls[i]));
//		}
//	}
	
	
	// check isValid result for each string, save results to print
	private String checkIsValidUrls(UrlValidator uv) {
		String s = new String();
		for (int i = 0; i < arrayUrls.length; i++) {
			Boolean result = uv.isValid(arrayUrls[i]);
			s = s + "," + result.toString();
			if (verbose) {
				System.out.println("\"" + arrayUrls[i] + "\"" + "  :  " + result.toString());
			}
		}
		try {
			Boolean result = uv.isValid(null);
			s = s + "," + result.toString();
			if (verbose) {
				System.out.println("\"null\"" + "  :  " +  result.toString());
			}
		}
		catch (NullPointerException e) {
			s = s + ",exception";
			if (verbose) {
				System.out.println("\"null\"" + "  :  exception");
			}
		}
		if (verbose) {
			System.out.println(s);
		}
		return s;
	}
		

	// print the options
	private String printOptions(Integer val){
		switch (val) {
		case 0:
			return new String("NONE");
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
		return new String(testCaseIndex++ + "," + printSchemes(schemes)
		 	+ "," + printOptions(options) + "," + regex);
	}
	
	private void URLValidatorSchemesConstructor() {
		UrlValidator uv = new UrlValidator(schemes);
		String s = printUrlValidatorInfo(schemes, (int) uv.getOptions(), regexNotSpecified);
		listResults.add(s + checkIsValidUrls(uv));
		if (verbose) {
			System.out.println("\nTest constructor: UrlValidator(" + printSchemes(schemes) + ")");
			System.out.println(s);
			System.out.println(listResults.get(listResults.size()-1));
		}
	}
	
	private void URLValidatorConstructorTest() {
		UrlValidator uv = new UrlValidator();
		String s = printUrlValidatorInfo(defaultSchemes, (int) uv.getOptions(), regexNotSpecified);
		listResults.add(s + checkIsValidUrls(uv));
		if (verbose) {
			System.out.println("\nTest constructor: UrlValidator()");
			System.out.println(s);
			System.out.println(listResults.get(listResults.size()-1));
		}
	}
	

	private void URLValidatorOptionsConstructorTest() {
		for (int i=0; i<16; i++) {
			UrlValidator uv = new UrlValidator(options[i]);
			String s = printUrlValidatorInfo(defaultSchemes, (int) uv.getOptions(), regexNotSpecified);
			listResults.add(s + checkIsValidUrls(uv));
			if (verbose) {
				System.out.println("\nTest constructor: UrlValidator("+ printOptions((int) options[i]) + ")");
				System.out.println(s);
				System.out.println(listResults.get(listResults.size()-1));
			}
		}
	}
	

	private void URLValidatorSchemesOptionsConstructorTest() {
		for (int i=0; i<16; i++) {
			UrlValidator uv = new UrlValidator(schemes, options[i]);
			String s = printUrlValidatorInfo(schemes, (int) uv.getOptions(), regexNotSpecified);
			listResults.add(s + checkIsValidUrls(uv));
			if (verbose) {
				System.out.println("\nTest constructor: UrlValidator(" + printSchemes(schemes) + ", " + printOptions((int) options[i]) + ")");
				System.out.println(s);
				System.out.println(listResults.get(listResults.size()-1));
			}
		}
	}
	

	private void URLValidatorRegexOptionsConstructorTest() {
		RegexValidator authorityValidator = new RegexValidator(regexStr);
		for (int i=0; i<16; i++) {
			UrlValidator uv = new UrlValidator(authorityValidator, options[i]);
			String s = printUrlValidatorInfo(defaultSchemes, (int) uv.getOptions(), regexStr);
			listResults.add(s + checkIsValidUrls(uv));
			if (verbose) {
				System.out.println("\nTest constructor: UrlValidator(" + regexStr + ", " + printOptions((int) options[i]) + ")");
				System.out.println(s);
				System.out.println(listResults.get(listResults.size()-1));
			}
		}
	}
	

	private void URLValidatorSchemesRegexOptionsConstructorTest() {
		RegexValidator authorityValidator = new RegexValidator(regexStr);
		for (int i=0; i<16; i++) {
			UrlValidator uv = new UrlValidator(schemes, authorityValidator, options[i]);
			String s = printUrlValidatorInfo(schemes, (int) uv.getOptions(), regexStr);
			listResults.add(s + checkIsValidUrls(uv));
			if (verbose) {
				System.out.println("\nTest constructor: UrlValidator(" + printSchemes(schemes) + ",  "+ regexStr + ", " + printOptions((int) options[i]) + ")");
				System.out.println(s);
				System.out.println(listResults.get(listResults.size()-1));
			}
		}
	}
	

	private void URLValidatorGetInstanceTest() {
		UrlValidator uv = UrlValidator.getInstance();
		String s = printUrlValidatorInfo(defaultSchemes, (int) uv.getOptions(), regexNotSpecified);
		listResults.add(s + checkIsValidUrls(uv));
		if (verbose) {
			System.out.println("\nTest constructor: UrlValidator.getInstance()");
			System.out.println(s);
			System.out.println(listResults.get(listResults.size()-1));
		}
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
