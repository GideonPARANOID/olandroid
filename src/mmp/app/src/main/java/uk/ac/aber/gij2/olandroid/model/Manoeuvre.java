/**
 * @created 2015-01-31
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.olandroid.model;

import android.opengl.Matrix;

import uk.ac.aber.gij2.olandroid.view.Drawable;
import uk.ac.aber.gij2.olandroid.controller.AnimationManager;
import uk.ac.aber.gij2.olandroid.view.AnimationStyle;
import uk.ac.aber.gij2.olandroid.Util;
import uk.ac.aber.gij2.olandroid.view.Shape;


public class Manoeuvre extends Shape implements Drawable.FlightPiece {

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
   private float[] vertices, componentsCumulativeLength;
   private short[] drawOrder;
   private String olan, aresti, name, category;
   private int[] groupIndicesPre, groupIndicesPost;
   private float groupScalePre, groupScalePost, groupScaleFull;
   private int lengthPre, lengthPost;
   private boolean cached;


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

      super(Style.FILL);

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

      vertices = new float[components.length * components[0].getVertices().length];
      drawOrder = new short[components.length * 6];

      cached = false;

      buildComponentsCumulativeLength();
   }


   /**
    * deep copy constructor (copies components too)
    * @param manoeuvre - instance of manoeuvre to copy
    */
   public Manoeuvre(Manoeuvre manoeuvre) {
      super(Style.FILL);

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


      this.vertices = manoeuvre.vertices;
      this.drawOrder = manoeuvre.drawOrder;

      this.cached = manoeuvre.cached;

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
      calculateMatrices(initialMatrix);

      // far right column of a 4x4 matrix is the translation operation
      float result = 0;
      for (float[] matrix : matrices) {
         if (matrix[13] < result) {
            result = matrix[13];
         }
      }

      return result;
   }


   /**
    * constructs the vertices from the components' vertices
    */
   public void buildVertices() {

      // TODO: improve, currently results in too many vertices, indices 0 & 1 are duplicates
      for (int i = 0, drawOrderFactor = 0, verticesFactor = 0; i < components.length;
          i++, drawOrderFactor += 6, verticesFactor += 12) {

         // assembling the vertices
         float[] verticesCurrent = components[i].getVertices();
         for (int j = 0; j < verticesCurrent.length; j+= 3) {

            float[] result = new float[4];

            // cannot dump the result directly in the vertex array as it needs length 4 (for w)
            Matrix.multiplyMV(result, 0, matrices[i], 0, new float[] {
                  verticesCurrent[j],
                  verticesCurrent[j + 1],
                  verticesCurrent[j + 2],
                  1f
               }, 0);

            vertices[verticesFactor + j] = result[0];
            vertices[verticesFactor + j + 1] = result[1];
            vertices[verticesFactor + j + 2] = result[2];

         }

         // assembling the two triangles
         short verticesCount = (short) (i * 4);
         drawOrder[drawOrderFactor] = verticesCount;
         drawOrder[drawOrderFactor + 1] = (short) (verticesCount + 1);
         drawOrder[drawOrderFactor + 2] = (short) (verticesCount + 2);
         drawOrder[drawOrderFactor + 3] = verticesCount;
         drawOrder[drawOrderFactor + 4] = (short) (verticesCount + 2);
         drawOrder[drawOrderFactor + 5] = (short) (verticesCount + 3);
      }

      buildVerticesBuffer(vertices);
      buildDrawOrderBuffer(drawOrder);

      cached = true;
   }


   public void draw(float[] initialMatrix) {
      if (!cached) {

         buildVertices();
         super.draw(initialMatrix);
         calculateMatrices(initialMatrix);

      } else {
         super.draw(initialMatrix);
      }
   }


   public void animate(float progressStart, float progressEnd, AnimationStyle style) {
      int i;

      switch (style) {
         case PREVIOUS_TRAIL:
            // if either fully drawn or fully not drawn
            if (progressEnd == 0f || progressEnd == 1f) {
               for (Component component : components) {
                  component.animate(0f, progressEnd, AnimationStyle.PREVIOUS_TRAIL);
               }

            } else {
               // getting progress to be in the context of the flight length in components
               progressEnd *= getLength();

               for (i = 0; i < components.length && componentsCumulativeLength[i] < progressEnd; i++) {
                  components[i].animate(0f, 1f, AnimationStyle.PREVIOUS_TRAIL);
               }

               // scaling across the cumulative middle
               components[i].animate(0f,
                  ((components[i].getLength() - (componentsCumulativeLength[i] - progressEnd)) /
                     Math.abs(components[i].getLength())), AnimationStyle.PREVIOUS_TRAIL);

               for (i++; i < components.length; i++) {
                  components[i].animate(0f, 0f, AnimationStyle.PREVIOUS_TRAIL);
               }
            }
            break;

         case FLYING_WING:
            // if either fully drawn or fully not drawn
            if (progressEnd == 0f || progressEnd == 1f) {
               for (Component component : components) {
                  component.animate(0, progressEnd, AnimationStyle.FLYING_WING);
               }

            } else {

               // TODO: fix all of the horrible bugs

               progressEnd *= getLength();

               for (i = 0; i < components.length
                  && componentsCumulativeLength[i] < progressEnd; i++) {
                  components[i].animate(0f, 0f, AnimationStyle.FLYING_WING);
               }

               float cLength = Math.abs(components[i].getLength()),
                  cProgress = (cLength - (componentsCumulativeLength[i] - progressEnd)) / cLength;

               // wing will fit in this component
               if (componentsCumulativeLength[i] - progressEnd > AnimationManager.WING_LENGTH) {

                  // start & end of wing
                  components[i].animate(cProgress,
                     cProgress + (AnimationManager.WING_LENGTH / cLength), AnimationStyle.FLYING_WING);
                  i++;

               } else {

                  // start of wing
                  components[i].animate(cProgress, 1f, AnimationStyle.FLYING_WING);

                  float wingLeft = AnimationManager.WING_LENGTH - (cProgress * cLength);

                  // scaling component across back
                  for (i++; i < components.length
                     && wingLeft - Math.abs(components[i].getLength()) > 0; i++) {

                     wingLeft -= Math.abs(components[i].getLength());
                     components[i].animate(0f, 1f, AnimationStyle.FLYING_WING);
                  }

                  if (i < components.length) {

                     // end of wing
                     float mid = 1f - ((componentsCumulativeLength[i]
                        - AnimationManager.WING_LENGTH - progressEnd)
                        / Math.abs(components[i].getLength()));


                     // TODO: fix this flippin' piece
                     // if we go off the end of the cumulative lengths, we get nasty negative scaling
                     if (mid >= 0 && mid <= 1) {

                        // scaling component across front
                        components[i].animate(0f, mid, AnimationStyle.FLYING_WING);
                        i++;
                     }
                  }

               }

               if (i < components.length) {
                  for (; i < components.length; i++) {
                     components[i].animate(0f, 0f, AnimationStyle.FLYING_WING);
                  }
               }
            }
            break;
      }

      // since the components have changed, the vertices need to be rebuilt
      cached = false;
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


   /**
    * @return - aresti catalogue reference
    */
   public String getAresti() {
      return aresti;
   }


   /**
    * @return - descriptive name of the manouevre
    */
   public String getName() {
      return name;
   }


   /**
    * @return - category to which the manouevre belongs
    */
   public String getCategory() {
      return category;
   }

   public float getLength() {
      return componentsCumulativeLength[componentsCumulativeLength.length - 1];
   }
}
