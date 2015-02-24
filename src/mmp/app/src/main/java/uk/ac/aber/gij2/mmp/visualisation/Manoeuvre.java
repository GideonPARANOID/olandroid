/**
 * @created 2015-01-31
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.mmp.visualisation;

import android.opengl.Matrix;


public class Manoeuvre extends Shape implements Drawable {
   private Component[] components;
   private float[][] matrices;
   private float[] componentsCumulativeLength;
   private String olan, name;
   private boolean matricesCalculated;


   public Manoeuvre(Component[] components, String olan, String name) {
      super();

      this.components = components;
      this.olan = olan;
      this.name = name;

      buildComponentsCumulativeLength();

      matricesCalculated = false;
   }


   /**
    * copy constructor
    * @param manoeuvre - instance of manoeuvre to copy
    */
   public Manoeuvre(Manoeuvre manoeuvre) {
      super();

      // need to copy components, to prevent duplicate manoeuvres from sharing animations
      Component[] oldComponents = manoeuvre.components;
      components = new Component[oldComponents.length];

      for (int i = 0; i < components.length; i++) {
         components[i] = new Component(oldComponents[i]);
      }

      this.olan = manoeuvre.olan;
      this.name = manoeuvre.name;

      buildComponentsCumulativeLength();

      matricesCalculated = false;
   }


   public void draw(float[] initialMatrix) {

      // setting up relies on an initial draw matrix, so have to do it in the draw loop
      if (!matricesCalculated) {
         calculateMatrices(initialMatrix);
      }

      for (int i = 0; i < components.length; i++) {
         components[i].draw(matrices[i]);
      }
   }


   /**
    * builds an array of matrices corresponding to the components, each relative the last component
    * as each component is a line, its matrix defines the transform from the beginning of that
    * line to the end, components need to be drawn starting from the end of the last line
    *
    * @param initialMatrix - the starting matrix
    */
   private void calculateMatrices(float[] initialMatrix) {

      matrices = new float[components.length + 1][16];

      matrices[0] = initialMatrix;

      // each matrix is a multiplication of the last constructed one & the last drawn one
      for (int i = 1; i < components.length + 1; i++) {
         Matrix.multiplyMM(matrices[i], 0, matrices[i - 1], 0,
            components[i - 1].getCompleteMatrix(), 0);
      }
   }


   /**
    * @return - the full matrix operation from the  beginning of the manoeuvre to the end
    */
   public float[] getCompleteMatrix() {

      // starting from scratch
      float[] blank = new float[16];
      Matrix.setIdentityM(blank, 0);
      calculateMatrices(blank);

      return matrices[matrices.length - 1];
   }


   /**
    * modifies a manoeuvre's drawing, from none to partial to full, starting at the beginning,
    *    taking into account the length of components
    * @param progress - level of progress, between 0 & 1
    */
   public void animate(float progress) {

      // if either fully drawn or fully not drawn
      if (progress == 0f || progress == 1f) {
         for (int i = 0; i < components.length; i++) {
            components[i].animate(progress);
         }

      } else {
         // getting progress to be in the context of the flight length in components
         progress *= getLength();

         int current = 0;

         while (current < components.length && componentsCumulativeLength[current] < progress) {
            components[current++].animate(1f);
         }

         // scaling across the cumulative middle
         components[current].animate(1 -
            ((componentsCumulativeLength[current] - progress) / components[current].getLength()));

         current++;

         while (current < components.length) {
            components[current++].animate(0f);
         }
      }
   }


   /**
    * builds the list of cumulative lengths of components
    */
   public void buildComponentsCumulativeLength() {

      componentsCumulativeLength = new float[components.length];
      componentsCumulativeLength[0] = components[0].getLength();

      for (int i = 1; i < components.length; i++) {
         componentsCumulativeLength[i] = componentsCumulativeLength[i - 1] +
            components[i].getLength();
      }
   }


   public float getLength() {
      return componentsCumulativeLength[componentsCumulativeLength.length - 1];
   }


   public String getOLAN() {
      return olan;
   }

   public String getName() {
      return name;
   }
}
