/**
 * @created 2015-01-31
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.olandroid.view;


public interface Drawable {

   /**
    * @param matrix - a matrix to start drawing from
    */
   public void draw(float[] matrix);


   public interface FlightPiece extends Drawable {
      public final float WIDTH = 0.5f;

      /**
       * @return - length of the shapes
       */
      public float getLength();


      /**
       * @return - a matrix describing the transform from the beginning of the shape to the end
       */
      public float[] getCompleteMatrix();


      /**
       * modifies a the resources used to draw the object
       *
       * @param progressStart - how far the start of the drawing has got, between 0 & 1
       * @param progressEnd   - how far the end of the drawing has got, between 0 & 1
       * @param style         - the style of the animation
       */
      public void animate(float progressStart, float progressEnd, AnimationStyle style);
   }
}
