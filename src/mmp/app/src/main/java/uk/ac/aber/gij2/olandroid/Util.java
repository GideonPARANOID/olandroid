/**
 * @created 2015-03-23
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.olandroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

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





   public static int loadTexture(final Context context, final int resourceId) {
      final int[] textureHandle = new int[1];

      GLES20.glGenTextures(1, textureHandle, 0);

      if (textureHandle[0] != 0) {
         final BitmapFactory.Options options = new BitmapFactory.Options();
         options.inScaled = false;   // no pre-scaling

         // Read in the resource
         final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

         // bind to the texture in opengl
         GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

         // set filtering
         GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
         GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

         // load the bitmap into the bound texture
         GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

         // recycle the bitmap, since its data has been loaded into opengl
         bitmap.recycle();
      }

      if (textureHandle[0] == 0) {
         throw new RuntimeException("error loading texture");
      }

      return textureHandle[0];
   }
}
