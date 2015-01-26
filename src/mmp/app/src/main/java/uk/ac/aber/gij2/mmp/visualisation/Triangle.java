/**
 * @created 2015-01-26
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.mmp.visualisation;


public class Triangle extends Shape {

   /**
    * @param program - reference to an opengl program to use to draw the shape
    * @param size - determines the size of the resultant shape
    */
   public Triangle(int program, float size) {
      super(program);

      size /= 2f;

      setVertexCoords(new float[]{
         -size, -size, 0f,
         0f, size, 0f,
         size, -size, 0f
      });

      setDrawOrder(new short[]{0, 1, 2});
      setColor(new float[]{0.63671875f, 0.76953125f, 0.22265625f, 0f});

      setup();
   }
}
