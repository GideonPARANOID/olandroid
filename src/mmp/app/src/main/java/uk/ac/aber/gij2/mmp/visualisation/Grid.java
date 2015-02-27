/**
 * @created 2015-01-28
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.mmp.visualisation;

import android.opengl.Matrix;


public class Grid extends Shape implements Drawable {

   private final float unit, dimensions;


   /**
    * @param unit - size of a unit of the grid
    * @param dimensions - dimensions of the whole grid in terms of grid units
    */
   public Grid(float unit, float dimensions, float[] colour) {
      super();

      super.setColourFront(colour);

      this.unit = unit;
      this.dimensions = dimensions * unit;

      super.buildVerticesBuffer(new float[]{
         0, 0, 0,
         0, 0, unit,
         unit, 0, unit,
         unit, 0, 0
      });

      super.buildDrawOrderBuffer(new short[]{
         0, 1, 2, 3, 0
      });

      super.setDrawMode(Shape.LINES);
   }


   @Override
   public void draw(float[] initialMatrix) {
      if (!super.drawingSetup) {
         setupDrawing();
      }

      // using the original matrix, translates it around & drawing the grid unit
      for (float i = -dimensions; i < dimensions; i += unit) {
         for (float j = -dimensions; j < dimensions; j += unit) {

            float[] newMatrix = new float[16];
            Matrix.translateM(newMatrix, 0, initialMatrix, 0, i, 0f, j);

            super.draw(newMatrix);
         }
      }
   }


   public float[] getCompleteMatrix() {
      float[] matrix = new float[16];
      Matrix.setIdentityM(matrix, 0);
      return matrix;
   }


   public void animate(float progress) {
   }

   public float getLength() {
      return 0f;
   }
}
