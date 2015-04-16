/**
 * @created 2015-04-13
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.olandroid.view;

import android.opengl.Matrix;


public class Aircraft extends Shape implements Drawable {


   public Aircraft(int textureId) {

      float s = 2f;

      // temporarily a cube - we'll see to that soon enough

      super.buildVerticesBuffer(new float[] {
         -s, -s,  s,     s, -s,  s,     s,  s,  s,    -s,  s,  s,
         -s, -s, -s,    -s,  s, -s,     s,  s, -s,     s, -s, -s,
         -s,  s, -s,    -s,  s,  s,     s,  s,  s,     s,  s, -s,
         -s, -s, -s,     s, -s, -s,     s, -s,  s,    -s, -s,  s,
         s, -s, -s,     s,  s, -s,     s,  s,  s,     s, -s,  s,
         -s, -s, -s,    -s, -s,  s,    -s,  s,  s,    -s,  s, -s
      });

      super.buildDrawOrderBuffer(new short[] {
         0,  1,  2,  0,  2,  3,
         4,  5,  6,  4,  6,  7,
         8,  9, 10,  8, 10, 11,
         12, 13, 14, 12, 14, 15,
         16, 17, 18, 16, 18, 19,
         20, 21, 22, 20, 22, 23
      });

      super.setTextureId(textureId);

      super.buildTextureCoordsBuffer(new float[] {
         0,  0,         1,  0,         1,  1,         0,  1,
         1,  0,         1,  1,         0,  1,         0,  0,
         0,  1,         0,  0,         1,  0,         1,  1,
         1,  1,         0,  1,         0,  0,         1,  0,
         1,  0,         1,  1,         0,  1,         0,  0,
         0,  0,         1,  0,         1,  1,         0,  1
      });
   }


   @Override
   public float[] getCompleteMatrix() {
      float[] matrix = new float[16];
      Matrix.setIdentityM(matrix, 0);
      return matrix;
   }


   @Override
   public void animate(float progressStart, float progressEnd, AnimationStyle style) {}


   @Override
   public float getLength() {
      return 0f;
   }
}
