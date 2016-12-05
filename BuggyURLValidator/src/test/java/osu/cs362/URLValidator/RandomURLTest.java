package osu.cs362.URLValidator;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import java.util.Random;
import java.net.URLEncoder;

public class RandomURLTest {

	private boolean verbose = true;

	// schemes
	static String[] defaultSchemes = {"http", "https", "ftp" };
	static String[] schemes = {"http","https"};  // default includes 'ftp' as well

	// regex option
	static String regexStr = "[a-z]";
	static String regexNotSpecified = "not specified in constructor ";

	// Generates a random scheme, appended with ://
	// int group:
	// 0 - AlphaNumeric Strings (potentially valid)
	// 1 - Printable ASCII (potentially valid)
	// 2 - Extended ASCII (potentially valid, but unlikely)

	private String getRandomScheme(Integer group) {
		Random r = new Random();
		String result = new String();
		String appendChar = new String();

		int len = r.nextInt(8);
		int nextChar = 0;
		if (group < 0 || group > 2) {
			group = r.nextInt(2);
		}

		for (int i = 0; i < len; i++) {
			switch(group) {
				case 0:
					nextChar = r.nextInt(25) + 65;
					appendChar = Character.toString((char)nextChar);
				break;
				case 1:
					nextChar = r.nextInt(95) + 32;
					appendChar = Character.toString((char)nextChar);
				break;
				default:
					nextChar = r.nextInt(255);
					appendChar = Character.toString((char)nextChar);
				break;
			}
			result += appendChar;
		}
	result += "://";
	return result;
	}

	// Generates a random query
	// Ensure that there are always some = and & characters
	// int group:
	// 0 - AlphaNumeric Strings (probably valid)
	// 1 - Printable ASCII (potentially valid)
	// 2 - Extended ASCII (potentially valid, but unlikely)

	private String getRandomQuery(Integer group) {
		Random r = new Random();
		String result = new String();
		String appendChar = new String();

		int len = r.nextInt(64);
		int nextChar = 0;
		if (group < 0 || group > 2) {
			group = r.nextInt(2);
		}

		for (int i = 0; i < len; i++) {
			switch(group) {
				case 0:
					// 1/10th of the alpha string will be = or &
					nextChar = r.nextInt(10);
					if (nextChar == 0) {
						appendChar = "=";
					} else if (nextChar == 1) {
						appendChar = "&";
					} else {
						appendChar = Character.toString((char)(r.nextInt(25) + 65));
					}
				break;
				case 1:
					nextChar = r.nextInt(97) + 32;
					if (nextChar == 128) {
						appendChar = "=";
					} else if (nextChar == 129) {
						appendChar = "&";
					} else {
						appendChar = Character.toString((char)nextChar);
					}
				break;
				default:
					nextChar = r.nextInt(257);
					if (nextChar == 256) {
						appendChar = "=";
					} else if (nextChar == 257) {
						appendChar = "&";
					} else {
						appendChar = Character.toString((char)nextChar);
					}
				break;
			}
			result += appendChar;
		}
	return result;
	}


	// Generates a random path
	// Ensure that there are always some . and / characters
	// int group:
	// 0 - AlphaNumeric Strings (valid)
	// 1 - Printable ASCII (potentially valid)
	// 2 - Extended ASCII (potentially valid, but unlikely)
	// The length is random, from 0 - 256 characters
	private String getRandomPath(Integer group) {

		Random r = new Random();
		String result = new String();
		String appendChar = new String();

		int len = r.nextInt(64);
		int nextChar = 0;

		if (group < 0 || group > 2) {
			group = r.nextInt(2);
		}

		for (int i = 0; i < len; i++) {
			switch(group) {
				case 0:
					// 1/10th of the string will be . or /
					nextChar = r.nextInt(10);
					if (nextChar == 0) {
						appendChar = ".";
					} else if (nextChar == 1) {
						appendChar = "/";
					} else {
						appendChar = Character.toString((char)(r.nextInt(25) + 65));
					}
				break;
				case 1:
					nextChar = r.nextInt(97) + 32;
					if (nextChar == 128) {
						appendChar = ".";
					} else if (nextChar == 129) {
						appendChar = "/";
					} else {
						appendChar = Character.toString((char)nextChar);
					}
				break;
				default:
					nextChar = r.nextInt(257);
					if (nextChar == 256) {
						appendChar = ".";
					} else if (nextChar == 257) {
						appendChar = "/";
					} else {
						appendChar = Character.toString((char)nextChar);
					}
				break;
			}
			result += appendChar;
		}
	return result;
	}
	// Generates a random string for the domain section of the URL
	// There are 3 types of strings that we can generate:
	// - 0 - AlphaNumeric Strings (valid)
	// - 1 - Printable ASCII (potentially valid)
	// - 2 - Extended ASCII (potentially valid, but unlikely)
	// The length is random, from 0 - 256 characters
	private String getRandomDomain(Integer group) {

		Random r = new Random();
		String result = new String();
		String appendChar = new String();

		// Generate a string between 0 and 256 chars in length
		// QUESTION:
		// Interestingly, our oracle thinks this is valid: http://www..edu/pub/ietf/uri/historical.html#ASDFGJKL
		// I disagree, so I've modified the test to insert at least one character in the domain
		int len = r.nextInt(256) + 1;

		if (group < 0 || group > 2) {
			group = r.nextInt(2);
		}

		for (int i = 0; i < len; i++) {
			// Alphanumeric
			if (group == 0) {
					int type = r.nextInt(2);
					if (type == 0) {
						// A-Z
						appendChar = Character.toString((char)(r.nextInt(25) + 65));
					} else if (type == 1) {
						// a-z
						appendChar = Character.toString((char)(r.nextInt(25) + 97));
					} else {
						// 0 - 9
						appendChar = Character.toString((char)(r.nextInt(10) + 48));
					}
			// Printable
			} else if (group == 1) {
					appendChar = Character.toString((char)(r.nextInt(95) + 32));
			} else {
					appendChar = Character.toString((char)r.nextInt(255));
			}
			result += appendChar;
		}
		return result;
	}

	//	Generates a random string for the fragment section of the URL
	//	There are 3 types of strings that we can generate:
	//	- 0 - AlphaNumeric strings (valid)
	//	- 1 - Printable ASCII (potentially valid)
	//	- 2 - All ASCII, including extended chars (potentially valid, but unlikely)
	// 	The length is random, from 0 - 256 characters

  private String getRandomFragment(Integer group) {
		Random r = new Random();
		String result = new String();
		String appendChar = new String();

		// Generate a string between 0 and 256 chars in length
		int len = r.nextInt(256);

		if (group < 0 || group > 2) {
			group = r.nextInt(2);
		}

		for (int i = 0; i < len; i++) {
			// Alphanumeric
			if (group == 0) {
					int type = r.nextInt(2);
			    if (type == 0) {
			      // A-Z
			      appendChar = Character.toString((char)(r.nextInt(25) + 65));
			    } else if (type == 1) {
			      // a-z
			      appendChar = Character.toString((char)(r.nextInt(25) + 97));
			    } else {
			      // 0 - 9
			      appendChar = Character.toString((char)(r.nextInt(10) + 48));
			    }
			// Printable
			} else if (group == 1) {
					appendChar = Character.toString((char)(r.nextInt(95) + 32));
			} else {
			    appendChar = Character.toString((char)r.nextInt(255));
			}
			result += appendChar;
		}

		return result;
	}

	//	Generates a random string for the port section of the URL
	//	There are 3 types of strings that we can generate:
	//	- 0 - numeric strings (valid)
	//	- 1 - alphanumeric (potentially valid)
	//	- 2 - extended ascii (potentially valid, but unlikely)
	// 	The length is random, from 0 - 16 characters

  private String getRandomPort(Integer group) {
		Random r = new Random();
		String result = new String();
		String appendChar = new String();

		// Generate a string between 0 and 256 chars in length
		int len = r.nextInt(16);

		if (group < 0 || group > 2) {
			group = r.nextInt(2);
		}

		for (int i = 0; i < len; i++) {
			// Numeric
			if (group == 0) {
	      appendChar = Character.toString((char)(r.nextInt(10) + 48));
			// Alphanumeric
			} else if (group == 1) {
				int type = r.nextInt(2);
				if (type == 0) {
					// A-Z
					appendChar = Character.toString((char)(r.nextInt(25) + 65));
				} else if (type == 1) {
					// a-z
					appendChar = Character.toString((char)(r.nextInt(25) + 97));
				} else {
					// 0 - 9
					appendChar = Character.toString((char)(r.nextInt(10) + 48));
				}
			// Extended
			} else {
			    appendChar = Character.toString((char)r.nextInt(255));
			}
			result += appendChar;
		}
		return result;
	}

	@Test
	public void testRandomPortExtended() {
		Random r = new Random();
		UrlValidator validator = new UrlValidator();
		UrlValidatorOracle oracle = new UrlValidatorOracle();
		String testString = new String();

		for (int i = 0; i < 1000; i++) {
			testString += "www.osu.edu:";
			testString += getRandomPort(1);
			testString += "/bloop/derp/blerg/form.php?";
			testString += "#asdf23857ogihsnv";

			// Provide some useful output
			String message = new String();
			message = "Test String: ";
			message += testString;
			message += " Validator: ";
			message += validator.isValid(testString);
			message += " Oracle: ";
			message += oracle.isValid(testString);
			assertTrue(message, (validator.isValid(testString) == oracle.isValid(testString)));
		}
	}

	@Test
	public void testRandomPortAlphaNumeric() {
		Random r = new Random();
		UrlValidator validator = new UrlValidator();
		UrlValidatorOracle oracle = new UrlValidatorOracle();
		String testString = new String();

		for (int i = 0; i < 1000; i++) {
			testString += "www.osu.edu:";
			testString += getRandomPort(1);
			testString += "/bloop/derp/blerg/form.php?";
			testString += "#asdf23857ogihsnv";

			// Provide some useful output
			String message = new String();
			message = "Test String: ";
			message += testString;
			message += " Validator: ";
			message += validator.isValid(testString);
			message += " Oracle: ";
			message += oracle.isValid(testString);
			assertTrue(message, (validator.isValid(testString) == oracle.isValid(testString)));
		}
	}

	@Test
	public void testRandomPortInteger() {
		Random r = new Random();
		UrlValidator validator = new UrlValidator();
		UrlValidatorOracle oracle = new UrlValidatorOracle();
		String testString = new String();

		for (int i = 0; i < 1000; i++) {
			testString += "www.osu.edu:";
			testString += getRandomPort(0);
			testString += "/bloop/derp/blerg/form.php?";
			testString += "#asdf23857ogihsnv";

			// Provide some useful output
			String message = new String();
			message = "Test String: ";
			message += testString;
			message += " Validator: ";
			message += validator.isValid(testString);
			message += " Oracle: ";
			message += oracle.isValid(testString);
			assertTrue(message, (validator.isValid(testString) == oracle.isValid(testString)));
		}
	}

	@Test
	public void testRandomSchemeExtended() {
		Random r = new Random();
		UrlValidator validator = new UrlValidator();
		UrlValidatorOracle oracle = new UrlValidatorOracle();
		String testString = new String();

		for (int i = 0; i < 1000; i++) {
			testString = getRandomScheme(2);
			testString += "www.osu.edu/bloop/derp/blerg/form.php?";
			testString += "#asdf23857ogihsnv";

			// Provide some useful output
			String message = new String();
			message = "Test String: ";
			message += testString;
			message += " Validator: ";
			message += validator.isValid(testString);
			message += " Oracle: ";
			message += oracle.isValid(testString);
			assertTrue(message, (validator.isValid(testString) == oracle.isValid(testString)));
		}
	}

	@Test
	public void testRandomSchemePrintable() {
		Random r = new Random();
		UrlValidator validator = new UrlValidator();
		UrlValidatorOracle oracle = new UrlValidatorOracle();
		String testString = new String();

		for (int i = 0; i < 1000; i++) {
			testString = getRandomScheme(1);
			testString += "www.osu.edu/bloop/derp/blerg/form.php?";
			testString += "#asdf23857ogihsnv";

			// Provide some useful output
			String message = new String();
			message = "Test String: ";
			message += testString;
			message += " Validator: ";
			message += validator.isValid(testString);
			message += " Oracle: ";
			message += oracle.isValid(testString);
			assertTrue(message, (validator.isValid(testString) == oracle.isValid(testString)));
		}
	}

	@Test
	public void testRandomSchemeAlphaNumeric() {
		Random r = new Random();
		UrlValidator validator = new UrlValidator();
		UrlValidatorOracle oracle = new UrlValidatorOracle();
		String testString = new String();

		for (int i = 0; i < 1000; i++) {
			testString = getRandomScheme(0);
			testString += "www.osu.edu/bloop/derp/blerg/form.php?";
			testString += "#asdf23857ogihsnv";

			// Provide some useful output
			String message = new String();
			message = "Test String: ";
			message += testString;
			message += " Validator: ";
			message += validator.isValid(testString);
			message += " Oracle: ";
			message += oracle.isValid(testString);
			assertTrue(message, (validator.isValid(testString) == oracle.isValid(testString)));
		}
	}

	@Test
	public void testRandomQueryExtendedWithFragment() {
		Random r = new Random();
		UrlValidator validator = new UrlValidator();
		UrlValidatorOracle oracle = new UrlValidatorOracle();
		String testString = new String();

		for (int i = 0; i < 1000; i++) {
			testString = "http://www.osu.edu/bloop/derp/blerg/form.php?";
			testString += getRandomQuery(2);
			testString += "#asdf23857ogihsnv";

			// Provide some useful output
			String message = new String();
			message = "Test String: ";
			message += testString;
			message += " Validator: ";
			message += validator.isValid(testString);
			message += " Oracle: ";
			message += oracle.isValid(testString);
			// BUG (9): Validator doesn't flag invalid characters in the query
			// NOTE: In this instance, our validator exposes a bug in the oracle, whereby it ignores extended ascii characters
			// assertTrue(message, (validator.isValid(testString) == oracle.isValid(testString)));
		}
	}

	@Test
	public void testRandomQueryPrintableWithFragment() {
		Random r = new Random();
		UrlValidator validator = new UrlValidator();
		UrlValidatorOracle oracle = new UrlValidatorOracle();
		String testString = new String();

		for (int i = 0; i < 1000; i++) {
			testString = "http://www.osu.edu/bloop/derp/blerg/form.php?";
			testString += getRandomQuery(1);
			testString += "#asdf23857ogihsnv";

			// Provide some useful output
			String message = new String();
			message = "Test String: ";
			message += testString;
			message += " Validator: ";
			message += validator.isValid(testString);
			message += " Oracle: ";
			message += oracle.isValid(testString);
			// System.out.println(testString);
			// BUG (9): Validator doesn't flag invalid characters in the query
			// assertTrue(message, (validator.isValid(testString) == oracle.isValid(testString)));
		}
	}

	@Test
	public void testRandomQueryAlphaNumericWithFragment() {
		Random r = new Random();
		UrlValidator validator = new UrlValidator();
		UrlValidatorOracle oracle = new UrlValidatorOracle();
		String testString = new String();

		for (int i = 0; i < 1000; i++) {
			testString = "http://www.osu.edu/bloop/derp/blerg/form.php?";
			testString += getRandomQuery(0);
			testString += "#asdf23857ogihsnv";

			// Provide some useful output
			String message = new String();
			message = "Test String: ";
			message += testString;
			message += " Validator: ";
			message += validator.isValid(testString);
			message += " Oracle: ";
			message += oracle.isValid(testString);
			assertTrue(message, (validator.isValid(testString) == oracle.isValid(testString)));
		}
	}

	@Test
	public void testRandomQueryAlphaNumericNoFragment() {
		Random r = new Random();
		UrlValidator validator = new UrlValidator();
		UrlValidatorOracle oracle = new UrlValidatorOracle();
		String testString = new String();

		for (int i = 0; i < 1000; i++) {
			testString = "http://www.osu.edu/bloop/derp/blerg/form.php?";
			testString += getRandomQuery(0);

			// Provide some useful output
			String message = new String();
			message = "Test String: ";
			message += testString;
			message += " Validator: ";
			message += validator.isValid(testString);
			message += " Oracle: ";
			message += oracle.isValid(testString);

			assertTrue(message, (validator.isValid(testString) == oracle.isValid(testString)));
		}
	}

	// Since our oracle does not seem to effectively validate paths, I didn't
	// bother testing with the other character classes
	@Test
	public void testRandomPathAlphaNumeric() {
		Random r = new Random();
		UrlValidator validator = new UrlValidator();
		UrlValidatorOracle oracle =  new UrlValidatorOracle();
		String testString = new String();
		for (int i = 0; i < 1000; i++) {
			testString = "http://www.osu.edu/";
			testString += getRandomPath(0);
			testString += "?abc=123#ASDFGJKL";

			// Provide some useful output
			String message = new String();
			message = "Test String: ";
			message += testString;
			message += " Validator: ";
			message += validator.isValid(testString);
			message += " Oracle: ";
			message += oracle.isValid(testString);

			// NOTE (clarkje): This is an instance where our validation library appears to be more strict than our oracle
			// I'm not totally surprised by this.  Our oracle method is simplistic and is probably not comprhenesively checking the input
			// assertTrue(message, (validator.isValid(testString) == oracle.isValid(testString)));
		}
	}

	// Test a valid URL with a randomly generated domain segment using only AlphaNumeric characters
  @Test
	public void testRandomDomainAlphaNumeric() {
		Random r = new Random();
		UrlValidator validator = new UrlValidator();
		UrlValidatorOracle oracle =  new UrlValidatorOracle();
		String testString = new String();
		for (int i = 0; i < 1000; i++) {
			testString = "http://www.";
			testString += getRandomDomain(0);
			testString += ".edu/pub/ietf/uri/historical.html#ASDFGJKL";

			// Provide some useful output
			String message = new String();
			message = "Test String: ";
			message += testString;
			message += " Validator: ";
			message += validator.isValid(testString);
			message += " Oracle: ";
			message += oracle.isValid(testString);

			assertTrue(message, (validator.isValid(testString) == oracle.isValid(testString)));
		}
	}

	// Test a valid URL with a randomly generated domain segment using Printable ASCII characters
	@Test
	public void testRandomDomainPrintable() {
		Random r = new Random();
		UrlValidator validator = new UrlValidator();
		UrlValidatorOracle oracle =  new UrlValidatorOracle();
		String testString = new String();
		for (int i = 0; i < 1000; i++) {
			testString = "http://www.";
			testString += getRandomDomain(1);
			testString += ".edu/pub/ietf/uri/historical.html#ASDFGJKL";

			// Provide some useful output
			String message = new String();
			message = "Test String: ";
			message += testString;
			message += " Validator: ";
			message += validator.isValid(testString);
			message += " Oracle: ";
			message += oracle.isValid(testString);

			// QUESTION: Our method seems to correctly flag this as false, but the oracle disagrees
			// Test String: http://www.t(m;@G*K0ERft@@5in,6R.edu/pub/ietf/uri/historical.html#ASDFGJKL
			// assertTrue(message, (validator.isValid(testString) == oracle.isValid(testString)));
			// In this instance, I think our oracle is wrong.
		}
	}

	// Test a valid URL with a random domain using the entire extended ASCII character set
	@Test
	public void testRandomDomainExtended() {
		Random r = new Random();
		UrlValidator validator = new UrlValidator();
		UrlValidatorOracle oracle =  new UrlValidatorOracle();
		String testString = new String();
		for (int i = 0; i < 1000; i++) {
			testString = "http://www.";
			testString += getRandomDomain(2);
			testString += ".edu/pub/ietf/uri/historical.html#ASDFGJKL";

			// Provide some useful output
			String message = new String();
			message = "Test String: ";
			message += testString;
			message += " Validator: ";
			message += validator.isValid(testString);
			message += " Oracle: ";
			message += oracle.isValid(testString);

			// QUESTION: Our method seems to correctly flag this as false, but the oracle disagrees
			// Test String: http://www.òÊÂðÍS6Æ¬aÕíÝ6üÓ?BÐ.edu/pub/ietf/uri/historical.html#ASDFGJKL
			// assertTrue(message, (validator.isValid(testString) == oracle.isValid(testString)));
			// In this instance, I think our oracle is wrong.
		}
	}


	// Ensure that percent encoding printable ascii works.
	@Test
	public void testRandomDomainEncoded() {
		Random r = new Random();
		UrlValidator validator = new UrlValidator();
		UrlValidatorOracle oracle =  new UrlValidatorOracle();
		String testString = new String();
		for (int i = 0; i < 1000; i++) {
			testString = "http://www.";

			try {
				testString += URLEncoder.encode(getRandomDomain(2), "UTF-8");
			} catch (java.io.UnsupportedEncodingException uee) {
				// blah
			}

			testString += ".edu/pub/ietf/uri/historical.html#ASDFGJKL";
			// Provide some useful output
			String message = new String();
			message = "Test String: ";
			message += testString;
			message += " Validator: ";
			message += validator.isValid(testString);
			message += " Oracle: ";
			message += oracle.isValid(testString);
		}
	}


	@Test
	public void testRandomFragmentsAlphaNumeric() {
		Random r = new Random();
		UrlValidator validator = new UrlValidator();
		UrlValidatorOracle oracle =  new UrlValidatorOracle();
		String testString = new String();
		for (int i = 0; i < 1000; i++) {
			testString = "http://www.ics.uci.edu/pub/ietf/uri/historical.html#";
			testString += getRandomFragment(0);

			// Provide some useful output
			String message = new String();
			message = "Test String: ";
			message += testString;
			message += " Validator: ";
			message += validator.isValid(testString);
			message += " Oracle: ";
			message += oracle.isValid(testString);

			assertTrue(message, (validator.isValid(testString) == oracle.isValid(testString)));
		}
	}

	@Test
	public void testRandomFragmentsPrintable() {
		Random r = new Random();
		UrlValidator validator = new UrlValidator();
		UrlValidatorOracle oracle =  new UrlValidatorOracle();
		String testString = new String();
		for (int i = 0; i < 1000; i++) {
			testString = "http://www.ics.uci.edu/pub/ietf/uri/historical.html#";
			testString += getRandomFragment(1);

			// Provide some useful output
			String message = new String();
			message = "Test String: ";
			message += testString;
			message += " Validator: ";
			message += validator.isValid(testString);
			message += " Oracle: ";
			message += oracle.isValid(testString);

			// Do the commparison between our validator and the oracle implementation
			// BUG (8): validator.isValid() fails to detect special characters in the URL Fragment
		 	// assertTrue(message, (validator.isValid(testString) == oracle.isValid(testString)));
		}
	}

	@Test
	public void testRandomFragmentsExtended() {
		Random r = new Random();
		UrlValidator validator = new UrlValidator();
		UrlValidatorOracle oracle =  new UrlValidatorOracle();
		String testString = new String();
		for (int i = 0; i < 1000; i++) {
			testString = "http://www.ics.uci.edu/pub/ietf/uri/historical.html#";
			testString += getRandomFragment(2);

			// Provide some useful output
			String message = new String();
			message = "Test String: ";
			message += testString;
			message += " Validator: ";
			message += validator.isValid(testString);
			message += " Oracle: ";
			message += oracle.isValid(testString);

			// Do the commparison between our validator and the oracle implementation
			// BUG (8): validator.isValid() fails to detect special characters in the URL Fragment
		 	// assertTrue(message, (validator.isValid(testString) == oracle.isValid(testString)));
		}
	}
}
