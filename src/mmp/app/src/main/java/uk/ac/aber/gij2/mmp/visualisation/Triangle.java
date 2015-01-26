/**
 * @created 2015-01-26
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.mmp.visualisation;


public class Triangle extends Shape {

   private final short drawOrder[] = {0, 1, 2};

   /**
    * @param program - reference to an opengl program to use to draw the shape
    * @param size - determines the size of the resultant shape
    */
   public Triangle(int program, float size) {
      super(program);

      float half = size / 2f;

      float coords[] = {
         -half, -half, 0f,
         0f, half, 0f,
         half, -half, 0f
      };

      float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 0.0f };

      super.setVertexCoords(coords);
      super.setDrawOrder(drawOrder);
      super.setColor(color);
      super.setup();
   }
}
