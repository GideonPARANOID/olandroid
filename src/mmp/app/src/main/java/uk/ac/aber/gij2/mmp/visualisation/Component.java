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

   /**
    *
    * @param program - opengl program reference
    * @param pitch - vertical direction
    * @param yaw - horizontal direction
    * @param roll - //TODO implement
    */
   public Component(int program, int pitch, int yaw, int roll) {
      super(program);

      double directionAngle = Math.atan((double) pitch / (double) yaw),
         x = Math.sin(angle) * Math.cos(directionAngle),
         y = Math.sin(angle) * Math.sin(directionAngle),
         z = pitch == ZERO && yaw == ZERO ? 1d : Math.cos(angle);


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
}
