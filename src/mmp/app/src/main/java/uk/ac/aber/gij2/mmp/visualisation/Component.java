/**
 * @created 2015-01-28
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.mmp.visualisation;


public class Component extends Shape {

   // bounds for movement
   public static final int ZERO = 0,
      MIN = -1,
      MAX = 1;


   private final double angle = Math.PI / 4d;
   private double length = 1;
   private  int pitch, yaw, roll;

   /**
    *
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
         x = length * Math.sin(angle) * length * Math.cos(directionAngle),
         y = length * Math.sin(angle) * length * Math.sin(directionAngle),
         z = pitch == ZERO && yaw == ZERO ? length : length * Math.cos(angle);


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
}
