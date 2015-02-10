/**
 * @created 2015-01-28
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.mmp.visualisation;


import android.opengl.Matrix;

public class Grid extends Shape implements Drawable {

   private float gridSize, gridDimensions;
   private float[] matrix;


   /**
    * @param gridSize - size of a unit of the grid
    * @param gridDimensions - dimensions of the whole grid in terms of grid units
    */
   public Grid(float gridSize, float gridDimensions) {
      super();

      this.gridSize = gridSize;
      this.gridDimensions = gridDimensions;

      setVertices(new float[]{
         0, 0, 0,
         0, 0, gridSize,
         gridSize, 0, gridSize,
         gridSize, 0, 0
      });

      setDrawOrder(new short[]{
         0, 1, 2, 3, 0
      });

      setColor(new float[]{
         .5f, .5f, .5f, 0f
      });

      setup();
   }

   @Override
   public void draw(float[] initialMatrix) {
      this.matrix = initialMatrix;

      // using the original matrix, translates it around & drawing the grid unit
      for (float i = -gridDimensions; i < gridDimensions; i += gridSize) {
         for (float j = -gridDimensions; j < gridDimensions; j += gridSize) {

            float[] newMatrix = new float[16];
            Matrix.translateM(newMatrix, 0, matrix, 0, i, 0f, j);

            super.draw(newMatrix);

         }
      }
   }

   public float[] getMatrix() {
      return matrix;
   }
}
