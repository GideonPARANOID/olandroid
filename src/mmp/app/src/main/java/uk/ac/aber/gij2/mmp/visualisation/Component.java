/**
 * @created 2015-01-28
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.mmp.visualisation;


public class Component extends Shape {

   public static final int COMPONENT_STRAIGHT = 0,
      COMPONENT_PITCH_UP = 1,
      COMPONENT_PITCH_DOWN = 2,
      COMPONENT_YAW_LEFT = 3,
      COMPONENT_YAW_RIGHT = 4,
      COMPONENT_ROLL_LEFT = 5,
      COMPONENT_ROLL_RIGHT = 6;

   private static int angle = 45;

   public Component(int program, int componentType) {
      super(program);

      float length = 1,
         adjacent = length * ((float) Math.cos(angle)),
         opposite = length * ((float) Math.sin(angle));

      switch(componentType) {

         case COMPONENT_STRAIGHT:
            setVertexCoords(new float[]{
               0f, 0f, 0f,
               length, length, length

            });

         case COMPONENT_PITCH_UP:
            setVertexCoords(new float[]{
               0f, 0f, 0f,
               adjacent, opposite, 0f
            });
      }

      setVertexCoords(new float[]{

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
