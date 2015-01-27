package uk.ac.aber.gij2.mmp.visualisation;

/**
 * Created by paranoia on 27/01/15.
 */
public class Tile extends Shape {


      public Tile(int program) {
      super(program);

      float size = .5f;

      setVertexCoords(new float[]{
         -size, 0,-size,
         -size, 0, size,
         size, 0, size,
         size, 0, -size
      });

      setDrawOrder(new short[]{
         0, 1, 2, 3, 0
      });

      setColor(new float[]{
         .5f, .5f, .5f, 0f
      });

      setup();
   }
}
