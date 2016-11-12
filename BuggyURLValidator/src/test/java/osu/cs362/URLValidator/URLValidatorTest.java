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
	@Test
	public void URLValidatorConstructorTest() {
		String[] schemes = {"http","https"};
		UrlValidator urlValidatorSchemes = new UrlValidator(schemes);
		assertEquals("UrlValidator constructed with schemes {\"http\", \"https\"}; valid URL returned invalid", true, urlValidatorSchemes.isValid("http://foo.bar.com/") );
		assertEquals("UrlValidator constructed with schemes {\"http\", \"https\"}; invalid URL returned valid", false, urlValidatorSchemes.isValid("ftp://foo.bar.com/") );
		
		UrlValidator urlValidatorNoParams = new UrlValidator();
		assertEquals("UrlValidator constructed with no input; valid URL returned invalid", true, urlValidatorNoParams.isValid("ftp://foo.bar.com/") );
		assertEquals("UrlValidator constructed with no input; invalid URL returned valid", false, urlValidatorNoParams.isValid("localhost://foo.bar.com/") );

		UrlValidator urlValidatorOptions = new UrlValidator(UrlValidator.ALLOW_2_SLASHES);
		assertEquals("UrlValidator constructed with \"ALLOW_2_SLASHES\" option; valid URL returned invalid", true, urlValidatorOptions.isValid("ftp://foo.bar.com/") );
		assertEquals("UrlValidator constructed with \"ALLOW_2_SLASHES\" option; invalid URL returned valid", false, urlValidatorOptions.isValid("ftp:///foo//foo.bar.com/") );
		
		UrlValidator urlValidatorSchemesOptions = new UrlValidator(schemes, UrlValidator.NO_FRAGMENTS);
		assertEquals("UrlValidator constructed with " + schemes + " \"NO_FRAGMENTS\" option; valid URL returned invalid", true, urlValidatorOptions.isValid("ftp://foo.bar.com/") );
		assertEquals("UrlValidator constructed with " + schemes + " \"NO_FRAGMENTS\" option; invalid URL returned valid", false, urlValidatorOptions.isValid("ftp:///foo//foo.bar") );
		
	}

}
