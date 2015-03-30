/**
 * @created 2015-03-30
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.olandroid.visualisation;

import android.opengl.Matrix;


public class Ground extends Shape implements Drawable {

   private final float dimensions;


   /**
    * @param dimensions - dimensions of the whole plane
    * @param textureId - textureId resource
    */
   public Ground(int dimensions, int textureId) {
      super();

      this.dimensions = dimensions;

      super.setTextureId(textureId);

      super.buildVerticesBuffer(new float[] {
         0, 0, 0,
         0, 0, dimensions,
         dimensions, 0, dimensions,
         dimensions, 0, 0
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
      Matrix.translateM(newMatrix, 0, initialMatrix, 0, -dimensions / 2f, 0, -dimensions / 2f);
      super.draw(newMatrix);

   }


   public float[] getCompleteMatrix() {
      float[] matrix = new float[16];
      Matrix.setIdentityM(matrix, 0);
      return matrix;
   }


   public void animate(float progress, AnimationStyle style) {}

   public float getLength() {
      return 0f;
   }
}