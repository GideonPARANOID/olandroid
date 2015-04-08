/**
 * @created 2015-01-31
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.olandroid.visualisation;

import android.opengl.Matrix;

import uk.ac.aber.gij2.olandroid.AnimationManager;
import uk.ac.aber.gij2.olandroid.Util;


public class Manoeuvre implements Drawable {

   /**
    * the group for the olan modifier, before or after
    * also used for variable group scaling
    */
   public enum Group {
      PRE, POST, FULL;

      public static Group parse(String group) {
         switch (group) {
            case "PRE":
               return PRE;
            case "POST":
               return POST;
            case "FULL":
               return FULL;
            default:
               return FULL;
         }
      }
   }

   private Component[] components;
   private float[][] matrices;
   private float[] componentsCumulativeLength;
   private String olan, aresti, name, category;
   private int[] groupIndicesPre, groupIndicesPost;
   private float groupScalePre, groupScalePost, groupScaleFull;
   private int lengthPre, lengthPost;


   /**
    * @param components - list of components which constitute the movement of the manoeuvre
    * @param olan - olan figure for the manoeuvre
    * @param aresti - the aresti catalogue reference for the manoeuvre
    * @param name - name of the manoeuvre
    * @param category - name of the category this manoeuvre falls into
    * @param groupIndicesPre - first group of indices of components which are scalable
    * @param groupIndicesPost - second group of indices of components which are scalable
    * @throws IndexOutOfBoundsException - thrown if there's no components
    */
   public Manoeuvre(Component[] components, String olan, String aresti, String name,
      String category, int[] groupIndicesPre, int[] groupIndicesPost) throws
         IndexOutOfBoundsException {

      super();

      if (components.length == 0) {
         throw new IndexOutOfBoundsException("no components");
      }

      this.components = components;
      this.olan = olan;
      this.name = name;
      this.aresti = aresti;
      this.category = category;

      this.groupIndicesPre = groupIndicesPre;
      this.groupIndicesPost = groupIndicesPost;

      groupScalePre = 1f;
      groupScalePost = 1f;
      groupScaleFull = 1f;

      lengthPre = 0;
      lengthPost = 0;

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
      this.aresti = manoeuvre.aresti;
      this.name = manoeuvre.name;
      this.category = manoeuvre.category;

      // doesn't need to be copied - won't change
      this.groupIndicesPre = manoeuvre.groupIndicesPre;
      this.groupIndicesPost = manoeuvre.groupIndicesPost;

      // needs to be copied
      this.groupScalePre = manoeuvre.groupScalePre;
      this.groupScalePost = manoeuvre.groupScalePost;
      this.groupScaleFull = manoeuvre.groupScaleFull;

      this.lengthPre = manoeuvre.lengthPre;
      this.lengthPost = manoeuvre.lengthPost;

      buildComponentsCumulativeLength();
   }


   /**
    * builds an array of matrices corresponding to the components, each relative the last component
    *    as each component is a line, its matrix defines the transform from the beginning of that
    *    line to the end, components need to be drawn starting from the end of the last line
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
    * looks through the components & finds the lowest one in the manoeuvre, from the passed matrix
    * @param initialMatrix - matrix to build the matrix stack for components from
    * @return - the x translation of the lowest point in the manoeuvre
    */
   public float getLowestPoint(float[] initialMatrix) {
      float result = 0;

      calculateMatrices(initialMatrix);

      // far right column of a 4x4 matrix is the translation operation
      for (float[] matrix : matrices) {
         if (matrix[13] < result) {
            result = matrix[13];
         }
      }

      return result;
   }


   public void draw(float[] initialMatrix) {
      calculateMatrices(initialMatrix);

      for (int i = 0; i < components.length; i++) {
         components[i].draw(matrices[i]);
      }
   }


   public void animate(float progress, AnimationStyle style) {
      int i;

      switch (style) {
         case ONE:
            // if either fully drawn or fully not drawn
            if (progress == 0f || progress == 1f) {
               for (Component component : components) {
                  component.animate(0f, progress, AnimationStyle.ONE);
               }

            } else {
               // getting progress to be in the context of the flight length in components
               progress *= getLength();

               for (i = 0; i < components.length && componentsCumulativeLength[i] < progress; i++) {
                  components[i].animate(0f, 1f, AnimationStyle.ONE);
               }

               // scaling across the cumulative middle
               components[i].animate(
                  ((components[i].getLength() - (componentsCumulativeLength[i] - progress)) /
                     Math.abs(components[i].getLength())), AnimationStyle.ONE);

               for (i++; i < components.length; i++) {
                  components[i].animate(0f, 0f, AnimationStyle.ONE);
               }
            }
            break;

         case TWO:
            // if either fully drawn or fully not drawn
            if (progress == 0f || progress == 1f) {
               for (Component component : components) {
                  component.animate(0, progress, AnimationStyle.TWO);
               }

            } else {

               // TODO: fix all of the horrible bugs

               progress *= getLength();

               for (i = 0; i < components.length
                  && componentsCumulativeLength[i] < progress; i++) {
                  components[i].animate(0f, AnimationStyle.TWO);
               }

               float cLength = Math.abs(components[i].getLength()),
                  cProgress = (cLength - (componentsCumulativeLength[i] - progress)) / cLength;

               // wing will fit in this component
               if (componentsCumulativeLength[i] - progress > AnimationManager.WING_LENGTH) {

                  // start & end of wing
                  components[i].animate(cProgress,
                     cProgress + (AnimationManager.WING_LENGTH / cLength), AnimationStyle.TWO);
                  i++;

               } else {

                  // start of wing
                  components[i].animate(cProgress, 1f, AnimationStyle.TWO);

                  float wingLeft = AnimationManager.WING_LENGTH - (cProgress * cLength);

                  // scaling component across back
                  for (i++; i < components.length
                     && wingLeft - Math.abs(components[i].getLength()) > 0; i++) {

                     wingLeft -= Math.abs(components[i].getLength());
                     components[i].animate(0f, 1f, AnimationStyle.TWO);
                  }

                  if (i < components.length) {

                     // end of wing
                     float mid = 1f - ((componentsCumulativeLength[i]
                        - AnimationManager.WING_LENGTH - progress)
                        / Math.abs(components[i].getLength()));


                     // TODO: fix this flippin' piece
                     // if we go off the end of the cumulative lengths, we get nasty negative scaling
                     if (mid >= 0 && mid <= 1) {

                        // scaling component across front
                        components[i].animate(0f, mid, AnimationStyle.TWO);
                        i++;
                     }
                  }

               }

               if (i < components.length) {
                  for (; i < components.length; i++) {
                     components[i].animate(0f, 0f, AnimationStyle.TWO);
                  }
               }
            }
            break;
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
    * @param group - the group to add length to (before or after olan)
    * @param extra - length to add to the default entry component length
    */
   public void addLength(Group group, int extra) {
      switch (group) {
         case PRE:
            components[0].setLength(components[0].getLength() - this.lengthPre + extra);
            this.lengthPre = extra;
            break;

         case POST:
            components[components.length - 1].setLength(
               components[components.length - 1].getLength() - this.lengthPost + extra);
            this.lengthPost = extra;
            break;
      }

      buildComponentsCumulativeLength();
   }


   /**
    * @param group - variable group
    * @param scale - value to scale the variable's components by
    */
   public void scaleGroup(Group group, float scale) {

      // unscaling & rescaling with the new one
      switch (group) {
         case PRE:
            for (int index : groupIndicesPre) {
               components[index].setLength(
                  (components[index].getLength() / groupScalePre) * scale);
            }

            groupScalePre = scale;
            break;

         case POST:
            for (int index : groupIndicesPost) {
               components[index].setLength(
                  (components[index].getLength() / groupScalePost) * scale);
            }

            groupScalePost = scale;
            break;
         case FULL:
            for (Component component : components) {
               component.setLength((component.getLength() / groupScaleFull) * scale);
            }

            groupScaleFull = scale;
            break;
      }
   }


   /**
    * @return - olan string including modifiers
    */
   public String getOLAN() {
      return Util.multiplyString(lengthPre, "+")
         + Util.multiplyString((int) ((1 / groupScalePre) - 1), "`") + olan
         + Util.multiplyString((int) ((1 / groupScalePost) - 1), "`")
         + Util.multiplyString(lengthPost, "+");
   }

   public String getAresti() {
      return aresti;
   }

   public String getName() {
      return name;
   }

   public String getCategory() {
      return category;
   }

   public float getLength() {
      return componentsCumulativeLength[componentsCumulativeLength.length - 1];
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
