package osu.cs362.URLValidator;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import java.util.Random;

public class RandomURLTest {


	private boolean verbose = true;

	// schemes
	static String[] defaultSchemes = {"http", "https", "ftp" };
	static String[] schemes = {"http","https"};  // default includes 'ftp' as well

	// regex option
	static String regexStr = "[a-z]";
	static String regexNotSpecified = "not specified in constructor ";

	//	Generates a random string for the fragment section of the URL
	//	There are 3 types of strings that we'll generate in equal proportion:
	//	- AlphaNumeric strings (valid)
	//	- Printable ASCII (potentially valid)
	//	- All ASCII, including extended chars (potentially valid, but unlikely)
	// 	The length is random, from 0 - 256 characters

  private String getRandomFragment() {
		Random r = new Random();
		String result = new String();
		String appendChar = new String();

		// Generate a string between 0 and 256 chars in length
		int len = r.nextInt(256);
		int group = r.nextInt(2);

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

	@Test
	public void testRandomFragments() {
		Random r = new Random();
		UrlValidatorExtension validator = new UrlValidatorExtension();
		UrlValidatorOracle oracle =  new UrlValidatorOracle();
		String testString = new String();
		for (int i = 0; i < 1000; i++) {
			testString = "http://www.ics.uci.edu/pub/ietf/uri/historical.html#";
			testString += getRandomFragment();

			// Provide some useful output
			String message = new String();
			message += "Test String: ";
			message += testString;
			message += " Validator: ";
			message += validator.isValid(testString);
			message += " Oracle: ";
			message += oracle.isValid(testString);

			// Do the commparison between our validator and the oracle implementation
			assertTrue(message, (validator.isValid(testString) == oracle.isValid(testString)));
		}
	}
}
