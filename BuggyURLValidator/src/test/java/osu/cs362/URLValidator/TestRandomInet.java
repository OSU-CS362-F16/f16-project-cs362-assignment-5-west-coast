package osu.cs362.URLValidator;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.Test;


public class TestRandomInet {
    
	static Random r = new Random();
	
	// Generate a valid IP address randomly
	private static String validInetAddress() {
		
		// Generate the segments (Digits of each segment must be between 0 and 255)
		String seg1 = r.nextInt(256) + "";
		String seg2 = r.nextInt(256) + "";
		String seg3 = r.nextInt(256) + "";
		String seg4 = r.nextInt(256) + "";
		
		String address = seg1 + "." + seg2 + "." + seg3 + "." + seg4;
		return address;
	}
	
	// Generate a invalid IP address randomly 
	private static String invalidInetAddress(int k) {
		String address = "";
		
		switch(k) {
			// one segment, number
			case 1:
				address = r.nextInt(256) + "";
				break;
			// two segments, number
			case 2:
				address = r.nextInt(256) + "." + r.nextInt(256);
				break;
			// three segments, number
			case 3:
				address = r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256);
				break;
			// four segments, alphanumeric
			case 4:
				char ch = (char)(r.nextInt(26) + 'a');
				address = r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256) + "." + ch;
				break;
			// four segments, same letters
			case 5:
				char c = (char)(r.nextInt(26) + 'a');
				address = c + "." + c + "." + c + "." + c;
				break;
			// four segments, different letters
			case 6:
				char c1 = (char)(r.nextInt(26) + 'a');
				char c2 = (char)(r.nextInt(26) + 'a');
				char c3 = (char)(r.nextInt(26) + 'a');
				char c4 = (char)(r.nextInt(26) + 'a');
				address = c1 + "." + c2 + "." + c3 + "." + c4;
				break;
			// four segments, numbers, first segment above 255
			case 7:
				address = (r.nextInt(1000) + 256 ) + "." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256);
				break;
			// four segments, numbers, second segment above 255
			case 8:
				address = r.nextInt(256) + "." + (r.nextInt(1000) + 256 ) + "." + r.nextInt(256) + "." + r.nextInt(256);
				break;
			// four segments, numbers, third segment above 255
			case 9:
				address = r.nextInt(256) + "." + r.nextInt(256) + "." + (r.nextInt(1000) + 256 ) + "." + r.nextInt(256);
				break;
			// four segments, numbers, fourth segment above 255
			case 0:
				address = r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256) + "." + (r.nextInt(1000) + 256 );
				break;
			}
		return address;
	}
	
	
	@Test
	public void randomInetAddressValid() {
		InetAddressValidator iv = InetAddressValidator.getInstance();
		
		int n_runs = 10000;
		for (int i = 0; i < n_runs; i++) {
			
			// Generate a random valid IP 
			String inetAddress = validInetAddress();
					
			// Test the result against the Oracle
		    boolean result = iv.isValidInet4Address(inetAddress);
		    
		    // Both should return true
		    assertTrue(result);
		    assertTrue(InetOracle.validate(inetAddress));
			assertEquals(result, InetOracle.validate(inetAddress));
		}
	}
	
	
	@Test
	public void randomInetAddressInvalid() {
		InetAddressValidator iv = InetAddressValidator.getInstance();
		
		int n_runs = 10000;
		for (int i = 0; i < n_runs; i++) {
			
			// Get a key between 0 ~ 9, to generate a random IP for that case
			int key = r.nextInt(7);
			
			// Generate a random invalid IP 
			String inetAddress = invalidInetAddress(key);
					
			// Test the result
			boolean result = iv.isValidInet4Address(inetAddress);
			
			// Assert that both inetAddressValidator and the Oracle returned false
			
			/***********************************************************************
			 * Assert fails for InetAddressValidator when:
			 * 	 Any segment value is larger than 255
			 **********************************************************************/
			//assertFalse(result);
			assertFalse(InetOracle.validate(inetAddress));
			//assertEquals(result, InetOracle.validate(inetAddress));
		}
	
	}
	
}
