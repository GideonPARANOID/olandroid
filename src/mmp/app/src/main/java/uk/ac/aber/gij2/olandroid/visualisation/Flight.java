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


   public Flight(Manoeuvre[] manoeuvres) {
      this.manoeuvres = manoeuvres;

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
    * looks through the manoeuvres & finds the lowest one in the flight, from the passed matrix
    * @param initialMatrix - matrix to build the matrix stack for manoeuvres from
    * @return - the x translation of the lowest point in the flight
    */
   public float getLowestPoint(float[] initialMatrix) {
      calculateMatrices(initialMatrix);

      float result = 0;
      for (int i = 0; i < manoeuvres.length; i++) {
         float lowestPoint = manoeuvres[i].getLowestPoint(matrices[i]);

         if (lowestPoint < result) {
            result = lowestPoint;
         }
      }

      return result;
   }



   public void draw(float[] initialMatrix) {

      calculateMatrices(initialMatrix);

      for (int i = 0; i < manoeuvres.length; i++) {
         manoeuvres[i].draw(matrices[i]);
      }
   }


   public void animate(float progress, AnimationStyle style) {
      int i;

      switch (style) {
         case ONE:
            // if either fully drawn or fully not drawn
            if (progress == 0f || progress == 1f) {
               for (Manoeuvre manoeuvre : manoeuvres) {
                  manoeuvre.animate(progress, AnimationStyle.ONE);
               }

            } else {

               // getting progress to be in the context of the flight length in manoeuvres
               progress *= getLength();

               for (i = 0; i < manoeuvres.length && manoeuvresCumulativeLength[i] < progress; i++) {
                  manoeuvres[i].animate(1f, AnimationStyle.ONE);
               }

               // scaling across the cumulative middle
               manoeuvres[i].animate(1 -
                  ((manoeuvresCumulativeLength[i] - progress) / manoeuvres[i].getLength()), AnimationStyle.ONE);

               for (i++; i < manoeuvres.length; i++) {
                  manoeuvres[i].animate(0f, AnimationStyle.ONE);
               }
            }
            break;

         case TWO:
            if (progress == 0f || progress == 1f) {
               for (Manoeuvre manoeuvre : manoeuvres) {
                  manoeuvre.animate(progress, AnimationStyle.TWO);
               }

            } else {

               // getting progress to be in the context of the flight length in manoeuvres
               progress *= getLength();

               for (i = 0; i < manoeuvres.length && manoeuvresCumulativeLength[i] < progress; i++) {
                  manoeuvres[i].animate(0f, AnimationStyle.TWO);
               }

               float mLength = manoeuvres[i].getLength(),
                  mProgress = ((mLength - (manoeuvresCumulativeLength[i] - progress))
                     / mLength),
                  mAnimation = (mProgress * mLength) + AnimationManager.WING_LENGTH;


               // scaling across the cumulative middle
               manoeuvres[i].animate(mProgress, AnimationStyle.TWO);

               i++;

               // if a manoeuvre's animation spills over into the next one
               if (mAnimation > mLength && i < manoeuvres.length) {
                  manoeuvres[i].animate(
                     (mAnimation - mLength - AnimationManager.WING_LENGTH)
                        / manoeuvres[i].getLength(), AnimationStyle.TWO);
                  i++;
               }

               for (; i < manoeuvres.length; i++) {
                  manoeuvres[i].animate(0f, AnimationStyle.TWO);
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
}
