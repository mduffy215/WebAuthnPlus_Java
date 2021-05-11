package io.trustnexus.util;

public class GeneralTest {

  public static void main(String[] args) {
    
    String testString = "abc\n123";
    
    int index = testString.indexOf("\n");
    int index2 = testString.indexOf("1");
    
    System.out.println(testString.length() + "  " + index + "  " + index2);

  }

}







