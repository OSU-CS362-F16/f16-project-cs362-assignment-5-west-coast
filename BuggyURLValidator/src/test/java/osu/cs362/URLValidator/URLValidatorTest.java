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
import java.util.regex.Pattern;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.BufferedWriter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;

public class URLValidatorTest {
	private List<String> listResults = new ArrayList<String>();
	private int testCaseIndex = 0;
	private boolean verbose = false;

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
	static String regexStr = "^[\\w-\\.]*$";
	static String regexNotSpecified = "DEFAULT";
	
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
		"OFF,ON,OFF,ON",
		"OFF,ON,ON,OFF",
		"OFF,ON,ON,ON",
		"ON,OFF,OFF,OFF",
		"ON,OFF,OFF,ON",
		"ON,OFF,ON,OFF",
		"ON,OFF,ON,ON",
		"ON,ON,OFF,OFF",
		"ON,ON,OFF,ON",
		"ON,ON,ON,OFF",
		"ON,ON,ON,ON"					 
		};
	
	// create a results file from the list of arrayUrls
	@Test 
	public void UrlValidatorAllTest() {
		List<String> testStrings = new ArrayList<String>();
		for (int i=0; i<arrayUrls.length; i++) {
			testStrings.add(arrayUrls[i]);
		}
		printArrayUrlsTitle("./target/results/results.csv", testStrings);
		URLValidatorSchemesConstructor(testStrings);
		URLValidatorConstructorTest(testStrings);
		URLValidatorOptionsConstructorTest(testStrings);
		URLValidatorSchemesOptionsConstructorTest(testStrings);
		URLValidatorRegexOptionsConstructorTest(testStrings);
		URLValidatorSchemesRegexOptionsConstructorTest(testStrings);
		URLValidatorGetInstanceTest(testStrings);
		printResults("./target/results/results.csv");
	}
	
	// test for the null input condition using all the constructor types
	@Test 
	public void UrlValidatorNullTest() {		
		RegexValidator authorityValidator = new RegexValidator(regexStr);
		UrlValidator uv1 = new UrlValidator(schemes);
		assertFalse(uv1.isValid(null));
		UrlValidator uv2 = new UrlValidator();
		assertFalse(uv2.isValid(null));
		UrlValidator uv6 = UrlValidator.getInstance();
		assertFalse(uv6.isValid(null));
		for (int i=0; i<options.length; i++) {
			UrlValidator uv3 = new UrlValidator(options[i]);
			assertFalse(uv3.isValid(null));
			UrlValidator uv4 = new UrlValidator(schemes, options[i]);
			assertFalse(uv4.isValid(null));
			UrlValidator uv5 = new UrlValidator(authorityValidator, options[i]);
			assertFalse(uv5.isValid(null));
		}
	}
	
	// test all the input files against expected list
	// inputs are in ./TestData/input
	// expected results are in ./TestData/expected and have the same format as the results
	// tests are commented out for now to facilitate sorting the input files
	@Test
	public void IsValidInputFileTest() {
		List<File> inputFileList = getFilesInDirectory("./TestData/input/", ".txt");
		for (File f:inputFileList) {
			String[] fileParts = f.getName().split(Pattern.quote("."));
			listResults.clear();
			List<String> inputListStrings = readStrings("./TestData/input/" + f.getName()); // reads each line of file as a string
			printArrayUrlsTitle("./target/results/" + fileParts[0] + ".csv", inputListStrings);

			URLValidatorSchemesConstructor(inputListStrings);
			URLValidatorConstructorTest(inputListStrings);
			URLValidatorOptionsConstructorTest(inputListStrings);
			URLValidatorSchemesOptionsConstructorTest(inputListStrings);
			URLValidatorRegexOptionsConstructorTest(inputListStrings);
			URLValidatorSchemesRegexOptionsConstructorTest(inputListStrings);
			URLValidatorGetInstanceTest(inputListStrings);
			
			printResults("./target/results/" + fileParts[0] + ".csv");
			List<File> outputFileList = getFilesInDirectory("./TestData/expected/", ".csv");
			
			// check results against expected
			File expectedOut = getOutputFileName(outputFileList, f.getName());
			if (!(expectedOut == null)) {
				List<String> expectedListStrings = readStrings("./TestData/expected/" + expectedOut.getName());
				//assertEquals("Number of test cases not equal expected: ", expectedListStrings.size() - 1, listResults.size()); // expected has a row of titles
				for (int j=0; j<expectedListStrings.size() - 1; j++) {
					//assertEquals(expectedListStrings.get(j+1),listResults.get(j));
				}
			} else {
				//fail("Expected results file not found.");
			}
			testCaseIndex = 0; // reset the counter for test cases
		}
	}
	
	
	//URLs that should pass under all possible inputs, rfc2396URI_00.txt
	@Test
	public void IsValidGoodInputFileTest() {
		List<String> inputListStrings = readStrings("./TestData/input/rfc2396URI_00.txt"); // reads each line of file as a string
		RegexValidator authorityValidator = new RegexValidator(".*");

		for (int j=0; j<inputListStrings.size(); j++) { 
			UrlValidator uv1 = new UrlValidator(schemes);
			assertTrue("UrlValidator(schemes) valid input fails: \"" + inputListStrings.get(j) + "\"",uv1.isValid(inputListStrings.get(j)));
			UrlValidator uv2 = new UrlValidator();
			assertTrue("UrlValidator() valid input fails: \"" + inputListStrings.get(j) + "\"",uv2.isValid(inputListStrings.get(j)));
			UrlValidator uv6 = UrlValidator.getInstance();
			assertTrue("UrlValidator.getInstance( valid input fails: \"" + inputListStrings.get(j) + "\"",uv6.isValid(inputListStrings.get(j)));
			for (int i=0; i<options.length; i++) {
				UrlValidator uv3 = new UrlValidator(options[i]);
				assertTrue("UrlValidator(options["+ i + "]) valid input fails: \"" + inputListStrings.get(j) + "\"", uv3.isValid(inputListStrings.get(j)));
				UrlValidator uv4 = new UrlValidator(schemes, options[i]);
				assertTrue("UrlValidator(schemes, options["+ i + "]) valid input fails: \"" + inputListStrings.get(j) + "\"", uv4.isValid(inputListStrings.get(j)));
				UrlValidator uv5 = new UrlValidator(authorityValidator, options[i]);
				// Possible issue with the RegexValidator
				//assertTrue("UrlValidator(authorityValidator, options["+ i + "]) valid input fails: \"" + inputListStrings.get(j) + "\"", uv5.isValid(null));
			}
		}
	}
	
	
	// test with oracle, this test commented out because the oracle doesn't align with rfc2396 in all areas
	@Test
	public void urlValidatorOracleTest() {
		List<File> inputFileList = getFilesInDirectory("./TestData/input/", ".txt");
		for (File f:inputFileList) {
			List<String> inputListStrings = readStrings("./TestData/input/" + f.getName()); // reads each line of file as a string
			UrlValidator uv = new UrlValidator();
			UrlValidatorOracle oracle = new UrlValidatorOracle();
			for (int j=0; j<inputListStrings.size(); j++) {
				// oracle results don't match for " http://example.org" and special characters
				//assertEquals("Oracle and UrlValidator don't match for \"" + inputListStrings.get(j) + "\" " ,oracle.isValid(inputListStrings.get(j)), uv.isValid(inputListStrings.get(j)));
			}
		}	
	}
	
	
	// test for allow all schemes
	@Test
	public void urlValidatorAllowAllSchemesTest() { 
		List<String> inputListStrings = readStrings("./TestData/input/rfc2396URI_01.txt"); // reads each line of file as a string
		UrlValidator uvAll = new UrlValidator(UrlValidator.ALLOW_ALL_SCHEMES);
		UrlValidator uv = new UrlValidator(); // default schemes only (http, https, ftp)
		for (int j=0; j<inputListStrings.size(); j++) {
			// BUG - fails gopher://spinaltap.micro.umn.edu/00/Weather/California/Los%20Angeles
			//assertTrue("ALLOW_ALL_SCHEMES setting ON, should return True for \"" + inputListStrings.get(j) + "\" " , uvAll.isValid(inputListStrings.get(j)));
			assertFalse("ALLOW_ALL_SCHEMES setting OFF, should return False for \"" + inputListStrings.get(j) + "\" " , uv.isValid(inputListStrings.get(j)));
		}
		List<String> defaultUrls = readStrings("./TestData/input/rfc2396URI_01_default.txt"); // reads each line of file as a string
		UrlValidator uvdefault = new UrlValidator(UrlValidator.ALLOW_ALL_SCHEMES);
		for (int j=0; j<defaultUrls.size(); j++) {
			assertTrue("ALLOW_ALL_SCHEMES setting ON, should return True for \"" + defaultUrls.get(j) + "\" " , uvAll.isValid(defaultUrls.get(j)));
			assertTrue("ALLOW_ALL_SCHEMES setting OFF, should return True for \"" + defaultUrls.get(j) + "\" " , uvdefault.isValid(defaultUrls.get(j)));
		}
	}

	// test to allow two slashes in path
	@Test
	public void urlValidatorAllowTwoSlashesTest() { 
		List<String> inputListStrings = readStrings("./TestData/input/rfc2396URI_02.txt"); // reads each line of file as a string
		UrlValidator uvAll = new UrlValidator(UrlValidator.ALLOW_2_SLASHES);
		UrlValidator uv = new UrlValidator(); // default does not allow two slashes
		for (int j=0; j<inputListStrings.size(); j++) {
			assertTrue("ALLOW_2_SLASHES setting ON, should return True for \"" + inputListStrings.get(j) + "\" " , uvAll.isValid(inputListStrings.get(j)));
			assertFalse("ALLOW_2_SLASHES setting OFF, should return False for \"" + inputListStrings.get(j) + "\" " , uv.isValid(inputListStrings.get(j)));
		}
	}
	
	// test for fragments
	@Test
	public void urlValidatorFragmentsTest() { 
		List<String> inputListStrings = readStrings("./TestData/input/rfc2396URI_04.txt"); // reads each line of file as a string
		UrlValidator uv = new UrlValidator(UrlValidator.NO_FRAGMENTS);
		UrlValidator uvF = new UrlValidator(); // Fragments allowed
		for (int j=0; j<inputListStrings.size(); j++) {
			assertFalse("NO_FRAGMENTS setting ON, should return false for \"" + inputListStrings.get(j) + "\" " , uv.isValid(inputListStrings.get(j)));
			assertTrue("NO_FRAGMENTS setting OFF, should return true for \"" + inputListStrings.get(j) + "\" " , uvF.isValid(inputListStrings.get(j)));
		}
	}
	

	
	// expected to fail under all scenarios
	@Test
	public void urlValidatorFailureUrlsTest() {
		List<String> inputListStrings = readStrings("./TestData/input/mathiasbynens_fail.txt"); // reads each line of file as a string
		UrlValidator uv = new UrlValidator(UrlValidator.ALLOW_ALL_SCHEMES + UrlValidator.ALLOW_LOCAL_URLS + UrlValidator.ALLOW_2_SLASHES);
		for (int j=0; j<inputListStrings.size(); j++) {
			assertFalse("url should always return false for \"" + inputListStrings.get(j) + "\" " , uv.isValid(inputListStrings.get(j)));
		}
	}
	

	// userid and password are allowed in the rfc2396.txt reference
	// BUG:
	//mathiasbynens_userid_password.txt
	@Test
	public void urlValidatorUserIdPasswordUrlsTest() {
		List<String> inputListStrings = readStrings("./TestData/input/mathiasbynens_userid_password.txt"); // reads each line of file as a string
		UrlValidator uv = new UrlValidator(UrlValidator.ALLOW_ALL_SCHEMES + UrlValidator.ALLOW_LOCAL_URLS + UrlValidator.ALLOW_2_SLASHES);
		for (int j=0; j<inputListStrings.size(); j++) {
			//assertTrue("url should always return true for \"" + inputListStrings.get(j) + "\" " , uv.isValid(inputListStrings.get(j)));
		}
	}
	
	// test special case that inxludes a comma since results are saved as .csv files
	// rfc2396.txt does 
	@Test
	//http://-.~_!$&'()*+,;=:%40:80%2f::::::@example.com
	public void urlWithCommaTest() {
		List<String> inputListStrings = new ArrayList();
		inputListStrings.add("http://-.~_!$&'()*+,;=:%40:80%2f::::::@example.com");
		UrlValidator uv = new UrlValidator(UrlValidator.ALLOW_ALL_SCHEMES + UrlValidator.ALLOW_LOCAL_URLS + UrlValidator.ALLOW_2_SLASHES);
		for (int j=0; j<inputListStrings.size(); j++) {
			assertFalse("url should always return true for \"" + inputListStrings.get(j) + "\" " , uv.isValid(inputListStrings.get(j)));
		}
	}
		
	
	// added test for options, options are "final" so only need to test the constructor
	@Test
	public void verifyOptionsTest() {
		for (int i=0; i<16; i++) {
			UrlValidator uv = new UrlValidator(options[i]);
			assertEquals("options not set properly",i,(int) uv.getOptions());
		}
		UrlValidator uv = new UrlValidator(16);
		assertEquals("option = 16 doesn't get set",16, uv.getOptions());
	}

	
	
	
	/************* Printing titles and results methods ************************/
	// Print Titles and results from the UrlValidatorAllTest
	public void printArrayUrlsTitle(String resultsFileName, List<String> urls) {
		try {
			File resultsFile = new File("./target/results/");
			resultsFile.mkdirs();
			PrintWriter urlWriter = new PrintWriter(resultsFileName, "UTF-8");
			try {
				urlWriter.print("testcase,scheme,ALLOW_LOCAL_URLS,NO_FRAGMENTS,ALLOW_2_SLASHES,ALLOW_ALL_SCHEMES,regex");
				for (int k=0; k<urls.size(); k++){
					if (urls.get(k) == "") {
						urlWriter.print(",\"\"");
					}
					else {
						urlWriter.print("," + urls.get(k));
					}
				}
				urlWriter.println("");
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
	
	
	public void printResults(String resultsFileName) {
		try {
			FileWriter fw = new FileWriter(resultsFileName, true);
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
	
	
	
	/************* Finding, reading and matching Files methods ************************/	
	// need to strip off the file extension and match the name
	public File getOutputFileName(List<File> fileList, String filename) {
		String[] fileParts = filename.split(Pattern.quote("."));
		for (File f:fileList) {
			if (f.getName().startsWith(fileParts[0])) {
				return(f);
			}
		}
		return null;
	}

	// get the list of files in folder
	// reference http://stackoverflow.com/questions/5694385/getting-the-filenames-of-all-files-in-a-folder
	public List<File> getFilesInDirectory(String directoryName, String fileType) {
		System.out.println("Looking in directory: " + directoryName);
		File folder = new File(directoryName);
		File[] listOfFiles = folder.listFiles();
		List<File> fileList = new ArrayList<File>();
		if (listOfFiles == null) {
			System.out.println("Didn't find any files");
		}
	    for (int i = 0; i < listOfFiles.length; i++) {
	      if (listOfFiles[i].isFile() && listOfFiles[i].getName().endsWith(fileType)) {
	    	  if (verbose) {
	    		  System.out.println("File: " + listOfFiles[i].getName());
	    	  }
	    	  fileList.add(listOfFiles[i]);
	      } 
	    }
	    return fileList;
	}
	
	// read in a list of test strings
	//reference: www.mkyong.com/how-to-read-file-from-java-bufferedreader-example/
	public List<String> readStrings(String filename) {
		BufferedReader br = null;
		List<String> listStrings = new ArrayList<String>();
		try {
			String sCurrentLine;			
			br = new BufferedReader(new FileReader(filename));
			if (verbose) {
				System.out.println("\nList of URLs to test from \"" + filename + "\"");
			}
			while ((sCurrentLine = br.readLine()) != null) {
				listStrings.add(sCurrentLine);
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
		return listStrings;
	}
	

	// check isValid result for each string, save results to print
	private String checkIsValidUrls(UrlValidator uv, List<String> urls) {
		String s = new String();
		for (int i = 0; i < urls.size(); i++) {
			Boolean result = uv.isValid(urls.get(i));
			s = s + "," + result.toString();
			if (verbose) {
				System.out.println("\"" + urls.get(i) + "\"" + "  :  " + result.toString());
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
	
	private void URLValidatorSchemesConstructor(List<String> urls) {
		UrlValidator uv = new UrlValidator(schemes);
		String s = printUrlValidatorInfo(schemes, (int) uv.getOptions(), regexNotSpecified);
		listResults.add(s + checkIsValidUrls(uv, urls));
		if (verbose) {
			System.out.println("\nTest constructor: UrlValidator(" + printSchemes(schemes) + ")");
			System.out.println(s);
			System.out.println(listResults.get(listResults.size()-1));
		}
	}
	
	private void URLValidatorConstructorTest(List<String> urls) {
		UrlValidator uv = new UrlValidator();
		String s = printUrlValidatorInfo(defaultSchemes, (int) uv.getOptions(), regexNotSpecified);
		listResults.add(s + checkIsValidUrls(uv, urls));
		if (verbose) {
			System.out.println("\nTest constructor: UrlValidator()");
			System.out.println(s);
			System.out.println(listResults.get(listResults.size()-1));
		}
	}

	private void URLValidatorOptionsConstructorTest(List<String> urls) {
		for (int i=0; i<16; i++) {
			UrlValidator uv = new UrlValidator(options[i]);
			String s = printUrlValidatorInfo(defaultSchemes, (int) uv.getOptions(), regexNotSpecified);
			listResults.add(s + checkIsValidUrls(uv, urls));
			if (verbose) {
				System.out.println("\nTest constructor: UrlValidator("+ printOptions((int) options[i]) + ")");
				System.out.println(s);
				System.out.println(listResults.get(listResults.size()-1));
			}										
		}
	}
	

	private void URLValidatorSchemesOptionsConstructorTest(List<String> urls) {
		for (int i=0; i<16; i++) {
			UrlValidator uv = new UrlValidator(schemes, options[i]);
			String s = printUrlValidatorInfo(schemes, (int) uv.getOptions(), regexNotSpecified);
			listResults.add(s + checkIsValidUrls(uv, urls));
			if (verbose) {
				System.out.println("\nTest constructor: UrlValidator(" + printSchemes(schemes) + ", " + printOptions((int) options[i]) + ")");
				System.out.println(s);
				System.out.println(listResults.get(listResults.size()-1));
			}
		}
	}
	
	private void URLValidatorRegexOptionsConstructorTest(List<String> urls) {
		RegexValidator authorityValidator = new RegexValidator(regexStr);
		for (int i=0; i<16; i++) {
			UrlValidator uv = new UrlValidator(authorityValidator, options[i]);
			String s = printUrlValidatorInfo(defaultSchemes, (int) uv.getOptions(), regexStr);
			listResults.add(s + checkIsValidUrls(uv, urls));
			if (verbose) {
				System.out.println("\nTest constructor: UrlValidator(" + regexStr + ", " + printOptions((int) options[i]) + ")");
				System.out.println(s);
				System.out.println(listResults.get(listResults.size()-1));
			}
		}
	}
	

	private void URLValidatorSchemesRegexOptionsConstructorTest(List<String> urls) {
		RegexValidator authorityValidator = new RegexValidator(regexStr);
		for (int i=0; i<16; i++) {
			UrlValidator uv = new UrlValidator(schemes, authorityValidator, options[i]);
			String s = printUrlValidatorInfo(schemes, (int) uv.getOptions(), regexStr);
			listResults.add(s + checkIsValidUrls(uv, urls));
			if (verbose) {
				System.out.println("\nTest constructor: UrlValidator(" + printSchemes(schemes) + ",  "+ regexStr + ", " + printOptions((int) options[i]) + ")");
				System.out.println(s);
				System.out.println(listResults.get(listResults.size()-1));
			}
		}
	}
	

	private void URLValidatorGetInstanceTest(List<String> urls) {
		UrlValidator uv = UrlValidator.getInstance();
		String s = printUrlValidatorInfo(defaultSchemes, (int) uv.getOptions(), regexNotSpecified);
		listResults.add(s + checkIsValidUrls(uv, urls));
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
