/**
 * @created 2015-03-30
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.olandroid.view;

import android.opengl.Matrix;


public class Ground extends Shape implements Drawable {

   /**
    * @param textureId - textureId resource
    */
   public Ground(int textureId) {
      super(Style.FILL);

      super.setTextureId(textureId);

      super.buildVerticesBuffer(new float[] {
         0, 0, 0,
         0, 0, Renderer.DRAW_BOUNDS,
         Renderer.DRAW_BOUNDS, 0, Renderer.DRAW_BOUNDS,
         Renderer.DRAW_BOUNDS, 0, 0
      });

      super.buildDrawOrderBuffer(new short[] {
         0, 1, 2,
         0, 2, 3
      });

      super.buildTextureCoordsBuffer(new float[] {
         0f, 1f,
         1f, 1f,
         1f, 0f,
         0f, 0f,
         0f, 1f,
         1f, 0f,
      });
   }


   @Override
   public void draw(float[] initialMatrix) {
      if (!super.drawingSetup) {
         super.setupDrawing();
      }

      float[] newMatrix = new float[16];
      Matrix.translateM(newMatrix, 0, initialMatrix, 0, -Renderer.DRAW_BOUNDS / 2f, 0, -Renderer.DRAW_BOUNDS / 2f);
      super.draw(newMatrix);
   }
}
