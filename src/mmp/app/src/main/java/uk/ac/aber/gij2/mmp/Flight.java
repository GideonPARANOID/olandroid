/**
 * @created 2015-02-04
 * @author gideon mw jones
 */

package uk.ac.aber.gij2.mmp;

import android.opengl.Matrix;

import java.util.ArrayList;

import uk.ac.aber.gij2.mmp.visualisation.Drawable;
import uk.ac.aber.gij2.mmp.visualisation.Manoeuvre;


public class Flight implements Drawable {
   private ArrayList<Manoeuvre> manoeuvres;
   private float[][] matrices;


   public Flight(ArrayList<Manoeuvre> manoeuvres) {
      this.manoeuvres = manoeuvres;
   }


   public ArrayList<Manoeuvre> getManoeuvres() {
      return manoeuvres;
   }


   /**
    * @return - olan description of flight
    */
   public String getOLANDescription() {

      String olanDescription = "";
      for (Manoeuvre manoeuvre : manoeuvres) {
         olanDescription += " " + manoeuvre.getOlan();
      }

      return olanDescription;
   }


   public void draw(float[] initialMatrix) {

      buildMatricesList(initialMatrix);

       for (int i = 0; i < manoeuvres.size(); i++) {
         manoeuvres.get(i).draw(matrices[i]);
      }

   }


   /**
    * builds an array of matrices corresponding to the components, each relative the last component
    *    as each component is a line, its matrix defines the transform from the beginning of that
    *    line to the end, components need to be drawn starting from the end of the last line
    * @param initialMatrix - the starting matrix
    */
   private void buildMatricesList(float[] initialMatrix) {

      matrices = new float[manoeuvres.size() + 1][16];

      matrices[0] = initialMatrix;

      // each matrix is a multiplication of the last constructed one & the last drawn one
      for (int i = 1; i < manoeuvres.size() + 1; i++) {
         Matrix.multiplyMM(matrices[i], 0, matrices[i - 1], 0, manoeuvres.get(i - 1).getMatrix(), 0);
      }
   }


   public float[] getMatrix() {
      return new float[16];
   }
}
