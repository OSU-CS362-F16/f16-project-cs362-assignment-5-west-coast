package osu.cs362.URLValidator;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

public class TestDomainValidator  {

  @Test
  // Invoking the getInstance() without a parameter should treat local URLs as invalid
  public void testDefaultGetInstance() {
    DomainValidator dv = DomainValidator.getInstance();
    assertFalse(dv.isValid("localhost"));
    assertFalse(dv.isValid("localdomain"));
    assertTrue(dv.isValid("google.com"));
    assertTrue(dv.isValid("instagr.am"));
  }

  @Test
  // Confirm that it correctly validates a top-level DomainValidator

  // TOP_LABEL_REGEX
  // \p{Alpha}{2,}
  // DOMAIN_LABEL_REGEX
  // \p{Alnum}(?>[\p{Alnum}-]*\p{Alnum})*
  // ^(?:\p{Alnum}(?>[\p{Alnum}-]*\p{Alnum})*\\.)+(\p{Alpha}{2,})$

  public void testIsValid() {

    DomainValidator dv = DomainValidator.getInstance(true);

    // Trivial
    assertTrue("google.com", dv.isValid("google.com"));
    assertFalse("go_ogle.com", dv.isValid("go_ogle.com"));

    // Domains can start with numbers per RFC
    assertTrue("1google.com", dv.isValid("1google.com"));

    // Special Characters at the start of the URL
    assertFalse("$google.com", dv.isValid("$google.com"));
    assertTrue("co.br", dv.isValid("co.br"));

    // Per RFC 1123: A 255 character hostname should be valid
    assertTrue("255 Character Hostname", dv.isValid("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa.com"));

    // BUG (7): Per RFC 1123 says 255 character hostnames should be supported.
    // We validate hostnames that are longer, but this may not be compatible with all clients.
    // assertFalse("256 Character Hostname", dv.isValid("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa.com"));

    // It's only a TLD if it's at the end of the string
    assertTrue("host.org.com.gov.co.br", dv.isValid("host.org.com.gov.co.br"));
    // assertTrue("google.com.uk", dv.isValid("google.com.uk"));

    // Domains that start and end with numbers
    assertTrue("482ksafj239r8.edu",dv.isValid("482ksafj239r8.edu"));
    assertTrue("bloopsadfo32397ohwifljshld.com", dv.isValid("bloopsadfo32397ohwifljshld.com"));
    assertTrue("localhost.localdomain (with local allowed)", dv.isValid("localhost.localdomain"));

    // BUG (5): Unicode -- Only ASCII is allowed in URLs, and I don't think we're validating IRIs here
    // assertTrue("名がドメイン.com", dv.isValid("名がドメイン.com"));

    // Punycode
    assertTrue("xn--v8jxj3d1dzdz08w.com", dv.isValid("xn--v8jxj3d1dzdz08w.com"));

    assertFalse("Empty String", dv.isValid(""));

    // BUG (6): RFC 2181: The zero length full name is defined as representing the root of
    // the DNS tree, and is typically written and displayed as ".
    // assertTrue(".", dv.isValid("."));

    // Gibberish with Special Characters
    assertFalse("$#(*@73598237t468937133>.&#9857)", dv.isValid("$#(*@73598237t468937133>.&#9857)"));

    // Canonical hostname on localhost/localdomain
    assertTrue("foo.localhost", dv.isValid("foo.localhost"));
    assertTrue("foo.localdomain", dv.isValid("foo.localdomain"));
  }

  @Test
  public void testIsValidTld() {
    DomainValidator dv = DomainValidator.getInstance(true);

    // Generic with leading dot
    assertTrue(".com", dv.isValidTld(".com"));
    // Generic
    assertTrue("com", dv.isValidTld("com"));
    // Local
    assertTrue("localdomain", dv.isValidTld("localdomain"));
    assertTrue("localhost", dv.isValidTld("localhost"));
    // Country Code
    assertTrue("de", dv.isValidTld("de"));
    assertTrue(".de", dv.isValidTld(".de"));
    // Infrastructure
    assertTrue("arpa", dv.isValidTld("arpa"));
    // Invalid
    assertFalse("12345", dv.isValidTld("12345"));

    // Double Leading dot
    assertFalse("..com", dv.isValidTld("..com"));

    // Special Characters
    assertFalse("/d&4362!}{}';327!@#$%^&*()'", dv.isValidTld("/d&4362!}{}';327!@#$%^&*()'"));

    // null
    assertFalse("Empty String", dv.isValidTld(""));
  }

  @Test
  public void testIsValidTldAllowLocal() {

    DomainValidator dv = DomainValidator.getInstance();

    // Generic with leading dot
    assertTrue(".com", dv.isValidTld(".com"));

    // Generic
    assertTrue("com", dv.isValidTld("com"));

    // Local domains are not valid unless allowLocal is set
    assertFalse("localdomain", dv.isValidTld("localdomain"));
    assertFalse("localhost", dv.isValidTld("localhost"));

    // Country Code
    assertTrue("de", dv.isValidTld("de"));
    assertTrue(".de", dv.isValidTld(".de"));

    // Infrastructure
    assertTrue("arpa", dv.isValidTld("arpa"));

    // Invalid
    assertFalse("12345", dv.isValidTld("12345"));

    // Double Leading dot
    assertFalse("..com", dv.isValidTld("..com"));

    // Special Characters
    assertFalse("/d&4362!}{}';327!@#$%^&*()'", dv.isValidTld("/d&4362!}{}';327!@#$%^&*()'"));

    // null
    assertFalse("Empty String", dv.isValidTld(""));
  }

  @Test
  public void testIsValidLocalTld() {
    DomainValidator dv = DomainValidator.getInstance(true);
    assertTrue("localdomain", dv.isValidLocalTld("localdomain"));
    assertTrue("locahost", dv.isValidLocalTld("localhost"));
    assertTrue(".localhost", dv.isValidLocalTld(".localhost"));
    assertFalse(".com", dv.isValidLocalTld(".com"));
  }
}
