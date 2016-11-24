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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class URLValidatorTest {
	private List<String> listResults = new ArrayList<String>();
	private int testCaseIndex = 0;
	private boolean verbose = false;
	private List<String> testStrings = new ArrayList<String>();

	// list of test strings
	static String[] arrayUrls
	= {
			// examples from rfc2396
			"http://www.ietf.org/rfc/rfc2396.txt",
			"http://www.w3.org/Addressing/",
			"ftp://ds.iternic.net/rfc/",
			"http://www.ics.uci.edu/pub/ietf/uri/historical.html#WARNING",
			
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
	
	// list of the option states corresponding to index
	private String[] strOptions = {
		// represented in this order:
		// ALLOW_LOCAL_URLS + NO_FRAGMENTS + ALLOW_2_SLASHES + ALLOW_ALL_SCHEMES
		"OFF,OFF,OFF,OFF",
		"OFF,OFF,OFF,ON",
		"OFF,OFF,ON,OFF",
		"OFF,OFF,ON,ON",
		"OFF,ON,OFF,OFF",
		"ON,OFF,ON,OFF",
		"OFF,ON,ON,OFF",
		"OFF,ON,ON,ON",
		"ON,OFF,OFF,OFF",
		"ON,OFF,OFF,ON",
		"ON,OFF,ON,OFF",
		"ON,OFF,ON,ON",
		"ON,ON,OFF,ON",
		"ON,ON,OFF,ON",
		"ON,ON,ON,OFF",
		"ON,ON,ON,ON"					 
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
	public void IsValidRfc2396Test() {
		readStrings("TestData/rfc2396URI.txt");
	}
	
	
	// Print Titles and results from the UrlValidatorAllTest
	public void printArrayUrlsTitle() {
		try {
			PrintWriter urlWriter = new PrintWriter("./target/results.csv", "UTF-8");
			try {
				urlWriter.print("testcase,scheme,ALLOW_LOCAL_URLS,NO_FRAGMENTS,ALLOW_2_SLASHES,ALLOW_ALL_SCHEMES,regex");
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
	
	
	// read in a list of test strings
	//reference: www.mkyong.com/how-to-read-file-from-java-bufferedreader-example/
	public void readStrings(String filename) {
		BufferedReader br = null;
		testStrings.clear();
		try {
			String sCurrentLine;
			
			br = new BufferedReader(new FileReader(filename));
			if (verbose) {
				System.out.println("\nList of URLs to test from \"" + filename + "\"");
			}
			while ((sCurrentLine = br.readLine()) != null) {
				testStrings.add(sCurrentLine);
				if (verbose) {
					System.out.println(sCurrentLine);
				}
			}
		}
		catch (IOException e) {
			System.out.println("file read failed: Message: " + e.getMessage() + " Localized: " + e.getLocalizedMessage());
			System.out.println("");			
		}
		finally {
			try {
				if (br != null)br.close();
			}
			catch (IOException ee) {
				System.out.println("read file close failed: Message: " + ee.getMessage() + " Localized: " + ee.getLocalizedMessage());
				System.out.println("");			
			}
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
	
	// if options are in range, print correct combination, else print error message
	private String printOptions(long options) {
		if (((int) options >= 0) || ( (int) options < 16 ) )
			return(strOptions[(int) options]);
		System.out.println("Option value not in valid range: " + (int) options);
		return("ERR,ERR,ERR,ERR");
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
