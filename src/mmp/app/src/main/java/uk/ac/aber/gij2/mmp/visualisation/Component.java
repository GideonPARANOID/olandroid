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


   private final double angleRadians = Math.PI / 4d, angleDegrees = 45;
   private double length = 1;
   private int pitch, yaw, roll;
   private float[] matrix;


   /**
    * @param program - opengl program reference
    * @param pitch - vertical direction
    * @param yaw - horizontal direction
    * @param roll - //TODO implement
    */
   public Component(int program, int pitch, int yaw, int roll) {
      super(program);

      this.pitch = pitch;
      this.yaw = yaw;
      this.roll = roll;

      double directionAngle = Math.atan((double) pitch / (double) yaw),
         x = length * Math.sin(angleRadians) * length * Math.cos(directionAngle),
         y = length * Math.sin(angleRadians) * length * Math.sin(directionAngle),
         z = pitch == ZERO && yaw == ZERO ? length : length * Math.cos(angleRadians);


      // building the matrix transform from the beginning of this component to the end
      matrix = new float[16];
      Matrix.setIdentityM(matrix, 0);
      Matrix.rotateM(matrix, 0, (float) angleDegrees, (float) -pitch, (float) yaw, (float) roll);
      Matrix.translateM(matrix, 0, 0f, 0f, (float) length);


      setVertexCoords(new float[]{
         0f, 0f, 0f,
         (float) x, (float) y, (float) z
      });

      setDrawOrder(new short[]{
         0, 1
      });

      setColor(new float[]{
         .5f, .5f, .5f, 0f
      });

      setup();

   }


   public int getPitch() {
      return pitch;
   }

   public int getRoll() {
      return roll;
   }

   public int getYaw() {
      return yaw;
   }

   public double getLength() {
      return length;
   }

   public float[] getMatrix() {
      return matrix;
   }
}
