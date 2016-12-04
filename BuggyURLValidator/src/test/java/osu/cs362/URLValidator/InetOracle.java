package osu.cs362.URLValidator;

/*
 * Credit:
 * http://www.mkyong.com/regular-expressions/how-to-validate-ip-address-with-regular-expression/
 */

import java.util.regex.*;

public class InetOracle {
	
	private static final Pattern PATTERN = Pattern.compile(
	        "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

	public static boolean validate(final String ip) {
	    return PATTERN.matcher(ip).matches();
	}
}
