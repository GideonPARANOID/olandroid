/**
 * @created 2015-01-31
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.olandroid.visualisation;

import android.opengl.Matrix;
import android.util.Log;


public class Manoeuvre implements Drawable {

   private Component[] components;
   private float[][] matrices;
   private float[] componentsCumulativeLength;
   private String olan, name, category;

   private int[][] groupIndices;
   private float[] groupScales;
   private float defaultEntryLength, defaultExitLength;

   /**
    *
    * @param components - list of components which constitute the movement of the manoeuvre
    * @param olan - olan figure for the manoeuvre
    * @param name - name of the manoeuvre
    * @param category - name of the category this manoeuvre falls into
    * @param groupIndices - groups of indices of components which are scaleable
    * @throws IndexOutOfBoundsException - thrown if there's no components
    */
   public Manoeuvre(Component[] components, String olan, String name, String category,
      int[][] groupIndices) throws IndexOutOfBoundsException {

      super();

      if (components.length == 0) {
         throw new IndexOutOfBoundsException("no components");
      }

      this.components = components;
      this.olan = olan;
      this.name = name;
      this.category = category;

      this.groupIndices = groupIndices;

      groupScales = new float[] {
         1f, 1f
      };

      defaultEntryLength = components[0].getLength();
      defaultExitLength = components[components.length - 1].getLength();

      buildComponentsCumulativeLength();
   }


   /**
    * deep copy constructor (copies components too)
    * @param manoeuvre - instance of manoeuvre to copy
    */
   public Manoeuvre(Manoeuvre manoeuvre) throws IndexOutOfBoundsException {
      super();

      if (manoeuvre.components.length == 0) {
         throw new IndexOutOfBoundsException("no components");
      }

      // need to copy components, to prevent duplicate manoeuvres from sharing animations
      this.components = new Component[manoeuvre.components.length];
      for (int i = 0; i < this.components.length; i++) {
         this.components[i] = new Component(manoeuvre.components[i]);
      }

      this.olan = manoeuvre.olan;
      this.name = manoeuvre.name;
      this.category = manoeuvre.category;

      // doesn't need to be copied - won't change
      this.groupIndices = manoeuvre.groupIndices;

      // needs to be copied
      this.groupScales = new float[] {
         manoeuvre.groupScales[0],
         manoeuvre.groupScales[1]
      };

      defaultEntryLength = components[0].getLength();
      defaultExitLength = components[components.length - 1].getLength();

      buildComponentsCumulativeLength();
   }


   public void draw(float[] initialMatrix) {
      calculateMatrices(initialMatrix);

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


   public void animate(float progress) {

      // if either fully drawn or fully not drawn
      if (progress == 0f || progress == 1f) {
         for (Component component : components) {
            component.animate(progress);
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
            ((componentsCumulativeLength[current] - progress) /
               Math.abs(components[current].getLength())));

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
      componentsCumulativeLength[0] = Math.abs(components[0].getLength());

      for (int i = 1; i < components.length; i++) {
         componentsCumulativeLength[i] = componentsCumulativeLength[i - 1] +
            Math.abs(components[i].getLength());
      }
   }


   /**
    * @param entryLength - length to add to the default entry component length
    */
   public void addEntryLength(int entryLength) {
      components[0].setLength(defaultEntryLength + (float) entryLength);

      buildComponentsCumulativeLength();
   }


   /**
    * @param exitLength - length to add to the default exit component length
    */
   public void addExitLength(int exitLength) {
      components[components.length - 1].setLength(defaultExitLength + (float) exitLength);

      buildComponentsCumulativeLength();
   }


   /**
    * @param groupIndex - index of the variable group
    * @param scale - value to scale the variable's components by
    */
   public void scaleGroup(int groupIndex, float scale) {
      try {
         for (int i = 0; i < groupIndices[groupIndex].length; i++) {

            // unscaling & rescaling with the new one
            components[groupIndices[groupIndex][i]].setLength((
               components[groupIndices[groupIndex][i]].getLength() /
                  groupScales[groupIndex]) * scale);

         }
      } catch (ArrayIndexOutOfBoundsException exception) {
         Log.d(this.getClass().getName(), "scale variable error - may not support variable");
         // manoeuvre may not support group variable
      }

      groupScales[groupIndex] = scale;
   }


   /**
    * @return - olan string including modifiers
    */
   public String getOLAN() {
      String entryLength = "", exitLength = "", variable0Length = "", variable1Length = "";

      for (int i = 0; i < components[0].getLength() - defaultEntryLength; i++) {
         entryLength += "+";
      }

      for (int i = 0; i < components[components.length - 1].getLength() - defaultExitLength; i++) {
         exitLength += "+";
      }

      // if only one group, it's the pre one
      if (groupIndices.length > 0) {
         for (int i = 1; i < (1 / groupScales[0]); i++) {
            variable0Length += "`";
         }

         if (groupIndices.length > 1) {
            for (int i = 1; i < (1 / groupScales[1]); i++) {
               variable1Length += "`";
            }
         }
      }

      return entryLength + variable0Length + olan + variable1Length + exitLength;
   }


   public float getLength() {
      return componentsCumulativeLength[componentsCumulativeLength.length - 1];
   }

   public String getName() {
      return name;
   }

   public String getCategory() {
      return category;
   }

   public void setColourBack(float[] colourBack) {
      for (Component component : components) {
         component.setColourBack(colourBack);
      }
   }


   public void setColourFront(float[] colourFront) {
      for (Component component : components) {
         component.setColourFront(colourFront);
      }
   }
}
