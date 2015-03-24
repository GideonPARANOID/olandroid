/**
 * @created 2015-02-04
 * @author gideon mw jones
 */

package uk.ac.aber.gij2.olandroid.visualisation;

import android.opengl.Matrix;

import uk.ac.aber.gij2.olandroid.AnimationManager;


public class Flight implements Drawable {

   private Manoeuvre[] manoeuvres;
   private float[][] matrices;
   private float[] manoeuvresCumulativeLength;

   private String name;
   private int style;


   public Flight(Manoeuvre[] manoeuvres) {
      this.manoeuvres = manoeuvres;

      setStyle(AnimationManager.STYLE_TWO);


      buildManoeuvresCumulativeLength();
   }


   /**
    * @return - olan description of flight
    */
   public String getOLAN() {
      StringBuilder result = new StringBuilder();

      for (Manoeuvre manoeuvre : manoeuvres) {
         result.append(manoeuvre.getOLAN()).append(' ');
      }
      return result.toString().trim();
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


   public void animate(float progress) {
      int current = 0;

      switch (style) {
         case AnimationManager.STYLE_ONE:
            // if either fully drawn or fully not drawn
            if (progress == 0f || progress == 1f) {
               for (Manoeuvre manoeuvre : manoeuvres) {
                  manoeuvre.animate(progress);
               }

            } else {

               // getting progress to be in the context of the flight length in manoeuvres
               progress *= getLength();

               while (current < manoeuvres.length && manoeuvresCumulativeLength[current] < progress) {
                  manoeuvres[current++].animate(1f);
               }

               // scaling across the cumulative middle
               manoeuvres[current].animate(1 -
                  ((manoeuvresCumulativeLength[current] - progress) / manoeuvres[current].getLength()));

               current++;

               while (current < manoeuvres.length) {
                  manoeuvres[current++].animate(0f);
               }
            }
            break;

         case AnimationManager.STYLE_TWO:
            if (progress == 0f || progress == 1f) {
               for (Manoeuvre manoeuvre : manoeuvres) {
                  manoeuvre.animate(progress);
               }

            } else {

              // TODO: implement cross-manoeuvre wing


               // getting progress to be in the context of the flight length in manoeuvres
               progress *= getLength();

               while (current < manoeuvres.length && manoeuvresCumulativeLength[current] < progress) {
                  manoeuvres[current++].animate(1f);
               }

               // scaling across the cumulative middle
               manoeuvres[current].animate(1 -
                  ((manoeuvresCumulativeLength[current] - progress) / manoeuvres[current].getLength()));

               current++;

               while (current < manoeuvres.length) {
                  manoeuvres[current++].animate(0f);
               }
            }
            break;
      }
   }



   /**
    * builds the list of cumulative lengths of components
    */
   public void buildManoeuvresCumulativeLength() {

      manoeuvresCumulativeLength = new float[manoeuvres.length];
      manoeuvresCumulativeLength[0] = manoeuvres[0].getLength();

      for (int i = 1; i < manoeuvres.length; i++) {
         manoeuvresCumulativeLength[i] = manoeuvresCumulativeLength[i - 1] +
            manoeuvres[i].getLength();
      }
   }


   public float getLength() {
      return manoeuvresCumulativeLength[manoeuvresCumulativeLength.length - 1];
   }



   public void setColourBack(float[] colourBack) {
      for (Manoeuvre manoeuvre : manoeuvres) {
         manoeuvre.setColourBack(colourBack);
      }
   }


   public void setColourFront(float[] colourFront) {
      for (Manoeuvre manoeuvre : manoeuvres) {
         manoeuvre.setColourFront(colourFront);
      }
   }


   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setStyle(int style) {
      this.style = style;

      System.out.println(style);

      for (Manoeuvre manoeuvre : manoeuvres) {
         manoeuvre.setStyle(style);
      }
   }
}
