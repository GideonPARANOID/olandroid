/**
 * @created 2015-03-23
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.olandroid.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class Util {


   /**
    * @param times - amount of times to multiply the string
    * @param text - the string in question
    * @return - a string multiplied by the amount of times specified
    */
   public static String multiplyString(int times, String text) {
      StringBuilder result = new StringBuilder();

      for (int i = 0; i < times; i++) {
         result.append(text);
      }
      return result.toString();
   }


   /**
    * @param search - string to look for
    * @param text - string to look in
    * @return - number of occurrences of the search in the text
    */
   public static int findOccurrences(String search, String text) {
      return text.length() - text.replace(search, "").length();
   }


   /**
    * @param input - an input stream to parse
    * @return - the input stream parsed as a string
    * @throws IOException - any errors encountered during the reading
    */
   public static String inputStreamToString(InputStream input) throws IOException {
      BufferedReader reader = new BufferedReader(new InputStreamReader(input));
      StringBuilder result = new StringBuilder();

      String line = reader.readLine();
      while (line != null) {
         result.append(line).append('\n');
         line = reader.readLine();
      }

      return result.toString();
   }
}
