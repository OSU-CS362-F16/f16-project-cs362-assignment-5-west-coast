package osu.cs362.URLValidator;

//References used in development of this Test code
// CS362 class notes and lecture
//Java Language Specification Java SE 8 Edition (dated 2015-02-13)
//http://docs.oracle.com/javase/7/docs/api/java/util/Random.html
//reference: http://stackoverflow.com/questions/31423643/try-catch-in-a-junit-test

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

public class ResultPairTest {
	static String emptyStr = "";
	static String charStr = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	static String specialCharStr = "~!@#$%%^&*()`;\'<>,.?/{}[]\\|+=-_";
	static String numStr = "012345678910.0e-10";
	
	
	@Test
	public void ResultPairConstructorTest() {
		String[] arrStrings = new String[]{charStr, specialCharStr, numStr, emptyStr};
		for (String str: arrStrings) {
			ResultPair rpT = new ResultPair(str, true);
			ResultPair rpF = new ResultPair(str, false);
			assertEquals("Failed to construct ResultPair with correct str=\"" + str + "\" and valid=true" , str, rpT.item);
			assertEquals("Failed to construct ResultPair with valid=true  and str=\"" + str + "\"", true, rpT.valid);
			assertEquals("Failed to construct ResultPair with correct str=\"" + str + "\" and valid=false", str, rpF.item);
			assertEquals("Failed to construct ResultPair with valid=false and str=\"" + str + "\"", false, rpF.valid);
		}
		
	}

}
