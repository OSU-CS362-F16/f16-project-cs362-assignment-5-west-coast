package osu.cs362.URLValidator;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

/**
* Validates a URL against the Java URL object implementation
* Code borrowed from: http://obscuredclarity.blogspot.com/2011/10/validate-url-in-java.html
**/

public class UrlValidatorOracle {

    public boolean isValid(String pUrl) {

        URL u = null;
        try {
            u = new URL(pUrl);
        } catch (MalformedURLException e) {
            return false;
        }
        try {
            u.toURI();
        } catch (URISyntaxException e) {
            return false;
        }
        return true;
    }
}
