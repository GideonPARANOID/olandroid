/**
 * @created 2015-01-31
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.mmp.visualisation;

import android.opengl.Matrix;


public class Manoeuvre implements Drawable {

   private Component[] components;
   private float[][] matrices;

   private String olan;

   public Manoeuvre(Component[] components, String olan) {
      this.components = components;
      this.olan = olan;
   }


   public void draw(float[] initialMatrix) {

      // TODO: find a way of caching this perhaps
      buildMatricesList(initialMatrix);

      for (int i = 0; i < components.length; i++) {
         components[i].draw(matrices[i]);
      }
   }


   /**
    * builds an array of matrices corresponding to the components, each relative the last component
    *    as each component is a line, its matrix defines the transform from the beginning of that
    *    line to the end, components need to be drawn starting from the end of the last line
    * @param initialMatrix - the starting matrix
    */
   private void buildMatricesList(float[] initialMatrix) {

      matrices = new float[components.length][16];

      matrices[0] = initialMatrix;

      // each matrix is a multiplication of the last constructed one & the last drawn one
      for (int i = 1; i < components.length; i++) {
         Matrix.multiplyMM(matrices[i], 0, matrices[i - 1], 0, components[i - 1].getMatrix(), 0);
      }
   }


   public String getOlan() {
      return olan;
   }
}

