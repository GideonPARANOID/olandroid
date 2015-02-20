/**
 * @created 2015-02-04
 * @author gideon mw jones
 */

package uk.ac.aber.gij2.mmp;

import android.opengl.Matrix;

import uk.ac.aber.gij2.mmp.visualisation.Drawable;
import uk.ac.aber.gij2.mmp.visualisation.Manoeuvre;


public class Flight implements Drawable {
   private Manoeuvre[] manoeuvres;
   private float[][] matrices;


   public Flight(Manoeuvre[] manoeuvres) {
      this.manoeuvres = manoeuvres;
   }


   /**
    * @return - olan description of flight
    */
   public String getOLAN() {

      String olan = "";
      for (Manoeuvre manoeuvre : manoeuvres) {
         olan += " " + manoeuvre.getOLAN();
      }

      return olan;
   }


   public void draw(float[] initialMatrix) {

      calculateMatrices(initialMatrix);

       for (int i = 0; i < manoeuvres.length; i++) {
         manoeuvres[i].draw(matrices[i]);
      }

   }


   /**
    * builds an array of matrices corresponding to the components, each relative the last component
    *    as each component is a line, its matrix defines the transform from the beginning of that
    *    line to the end, components need to be drawn starting from the end of the last line
    * @param initialMatrix - the starting matrix
    */
   private void calculateMatrices(float[] initialMatrix) {

      matrices = new float[manoeuvres.length + 1][16];
      matrices[0] = initialMatrix;

      // each matrix is a multiplication of the last constructed one & the last drawn one
      for (int i = 1; i < manoeuvres.length + 1; i++) {
         Matrix.multiplyMM(matrices[i], 0, matrices[i - 1], 0,
            manoeuvres[i - 1].getCompleteMatrix(), 0);
      }
   }


   public float[] getCompleteMatrix() {

      // starting from scratch
      float[] blank = new float[16];
      Matrix.setIdentityM(blank, 0);
      calculateMatrices(blank);

      return new float[16];
   }


   /**
    * modifies a flight's drawing, from none to partial to full, starting at the beginning
    * @param progress - level of progress, between 0 & 1
    */
   public void animate(float progress) {

      // if either fully drawn or fully not drawn
      if (progress == 0f || progress == 1f) {
         for (int i = 0; i < manoeuvres.length; i++) {
            manoeuvres[i].animate(progress);
         }

      } else {
         float midManoeuvre = manoeuvres.length * progress;
         int midManoeuvreMin = ((int) Math.floor(midManoeuvre));

         // full
         for (int i = 0; i <= midManoeuvreMin; i++) {
            manoeuvres[i].animate(1f);
            System.out.println(i + " full");
         }

         // mid
         manoeuvres[midManoeuvreMin].animate(midManoeuvre - (float) midManoeuvreMin);

         // none
         for (int i = midManoeuvreMin + 1; i < manoeuvres.length; i++) {
            manoeuvres[i].animate(0f);
         }
      }
   }


   public float getLength() {
      float total = 0;

      for (int i = 0; i < manoeuvres.length; i++) {
         total += manoeuvres[i].getLength();
      }

      return total;
   }
}
