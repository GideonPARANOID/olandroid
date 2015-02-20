/**
 * @created 2015-01-28
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.mmp.visualisation;

import android.opengl.Matrix;


public class Component extends Shape implements Drawable {

   // bounds for movement
   public static final int ZERO = 0, MIN = -1, MAX = 1;
   private static final float ANGLE = 1f / 24f, WIDTH = 0.5f;
   private final float[] matrix, vertices;
   private float length;


   /**
    * @param pitch - vertical direction
    * @param yaw - horizontal direction
    * @param roll - //TODO implement
    */
   public Component(int pitch, int yaw, int roll, float length, float[] colourFront,
      float[] colourBack) {

      super();

      super.setColourFront(colourFront);
      super.setColourBack(colourBack);

      this.length = length;

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

         // does not like dividing by minus one, mathematically works but not programmatically
         if (yaw == MIN) {
            directionAngle = Math.PI + directionAngle;
         }

         double radians = Math.PI * 2 * ANGLE;
         x = (float) (length * Math.sin(radians) * Math.cos(directionAngle));
         y = (float) (length * Math.sin(radians) * Math.sin(directionAngle));
         z = (float) (length * Math.cos(radians));
      }

      vertices = new float[] {
         WIDTH, 0f, 0f,
         -WIDTH, 0f, 0f,
         x - WIDTH, y, z,
         x + WIDTH, y, z,
         WIDTH, 0f, 0f
      };

      super.buildVertices(vertices);
      super.buildDrawOrder(new short[]{
         0, 1, 2,
         0, 2, 3
      });

      // building the matrix transform from the beginning of this component to the end
      matrix = new float[16];
      Matrix.setIdentityM(matrix, 0);

      // if all values are zero, we get a matrix of NaNs, which corrupts the matrix stack
      if (pitch != ZERO || yaw != ZERO || roll != ZERO) {
         Matrix.rotateM(matrix, 0, 360f * ANGLE, (float) -pitch, (float) yaw, (float) roll);
      }

      Matrix.translateM(matrix, 0, 0f, 0f, length);
   }


   public float[] getCompleteMatrix() {
      return matrix;
   }


   /**
    * modifies a component's drawing, from none to full, starting at the beginning
    * @param progress - level of progress, between 0 & 1
    */
   public void animate(float progress) {

      // TODO: refine
      super.buildVertices(progress == 0f ? null : vertices);
   }


   public float getLength() {
      return length;
   }
}
