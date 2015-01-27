/**
 * @created 2015-01-26
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.mmp.visualisation;


public class Cube extends Shape {

   public Cube(int program, float size) {
      super(program);

      size /= 2;

      setVertexCoords(new float[]{
         -size, -size, size,
         size, -size, size,
         size, size, size,
         -size, size, size,
         -size, -size, -size,
         -size, size, -size,
         size, size, -size,
         size, -size, -size,
         -size, size, -size,
         -size, size, size,
         size, size, size,
         size, size, -size,
         -size, -size, -size,
         size, -size, -size,
         size, -size, size,
         -size, -size, size,
         size, -size, -size,
         size, size, -size,
         size, size, size,
         size, -size, size,
         -size, -size, -size,
         -size, -size, size,
         -size, size, size,
         -size, size, -size
      });

      setDrawOrder(new short[]{
         0, 1, 2, 0, 2, 3, 4, 5, 6, 4, 6, 7,
         8, 9, 10,  8, 10, 11, 12, 13, 14, 12, 14, 15,
         16, 17, 18, 16, 18, 19, 20, 21, 22, 20, 22, 23
      });

      setColor(new float[]{
        0f, 0f, 0f, 1f
      });

      setup();
   }

}
