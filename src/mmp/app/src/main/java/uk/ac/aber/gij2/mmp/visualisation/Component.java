/**
 * @created 2015-01-28
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.mmp.visualisation;

import android.opengl.Matrix;


public class Component extends Shape implements Drawable {

   // bounds for movement
   public static final int ZERO = 0,
      MIN = -1,
      MAX = 1;

   // how many sections to build the line in
   private final int SECTIONS = 2;

   private final double angleRadians = Math.PI / 4d, angleDegrees = 45;
   private float[] matrix;

   /**
    * @param pitch - vertical direction
    * @param yaw - horizontal direction
    * @param roll - //TODO implement
    */
   public Component(int pitch, int yaw, int roll, float length) {
      super();

      float x, y, z;

      // continuing straight lines are a different case - much easier
      if (pitch == ZERO && yaw == ZERO) {
         x = 0f;
         y = 0f;
         z = length;

         // lines which shift direction
      } else {
         // calculating the angle at which the line shifts the direction from the pitch & yaw
         double directionAngle = Math.atan((double) pitch / (double) yaw);

         // the previous statement does not like dividing by minus one
         if (yaw == MIN) {
            directionAngle = Math.PI + directionAngle;
         }

         x = (float) (length * Math.sin(angleRadians) * Math.cos(directionAngle));
         y = (float) (length * Math.sin(angleRadians) * Math.sin(directionAngle));
         z = (float) (length * Math.cos(angleRadians));
      }


      // building sections
      float[] sections = new float[(SECTIONS + 1) * 3];
      short[] order = new short[SECTIONS + 1];

      float step = 1f / (float) SECTIONS;

      for (int i = 0; i <= SECTIONS ; i++) {
         sections[i * 3] = x * step * i;
         sections[(i * 3) + 1] = y * step * i;
         sections[(i * 3) + 2] = z * step * i;

         order[i] = (short) i;
      }

      setVertexCoords(sections);
      setDrawOrder(order);

      setColor(new float[]{
         .5f, .5f, .5f, 0f
      });


      // building the matrix transform from the beginning of this component to the end
      matrix = new float[16];
      Matrix.setIdentityM(matrix, 0);

      // if all values are zero, we get a matrix of NaNs, which corrupts the matrix stack
      if (pitch != ZERO || yaw != ZERO || roll != ZERO) {
         Matrix.rotateM(matrix, 0, (float) angleDegrees, (float) -pitch, (float) yaw, (float) roll);
      }

      Matrix.translateM(matrix, 0, 0f, 0f, length);

      setup();
   }

   public float[] getMatrix() {
      return matrix;
   }
}
