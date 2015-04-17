/**
 * @created 2015-01-28
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.olandroid.view;

import android.opengl.Matrix;


public class Grid extends Shape implements Drawable {

   private final float unitSize;


   /**
    * @param unitSize - size of a grid unit, ideally a factor of the renderer's draw bounds
    * @param colour - the colour, as an array representation
    */
   public Grid(float unitSize, float[] colour) {
      super(Style.LINES);

      this.unitSize = unitSize;

      super.setColourFront(colour);
      super.setColourBack(colour);

      super.buildVerticesBuffer(new float[]{
         0, 0, 0,
         0, 0, unitSize,
         unitSize, 0, unitSize,
         unitSize, 0, 0
      });

      super.buildDrawOrderBuffer(new short[]{
         0, 1, 2, 3, 0
      });
   }


   @Override
   public void draw(float[] initialMatrix) {
      if (!super.drawingSetup) {
         super.setupDrawing();
      }

      // using the original matrix, translates it around & drawing the grid unit
      for (float limit = Renderer.DRAW_BOUNDS / 2f, i = -limit; i < limit; i += unitSize) {
         for (float j = -limit; j < limit; j += unitSize) {

            float[] newMatrix = new float[16];
            Matrix.translateM(newMatrix, 0, initialMatrix, 0, i, 0f, j);

            super.draw(newMatrix);
         }
      }
   }
}
