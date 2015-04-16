/**
 * @created 2015-01-31
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.olandroid.model;


import uk.ac.aber.gij2.olandroid.AnimationStyle;

public interface Drawable {

   /**
    * @param matrix - a matrix to start drawing from
    */
   public void draw(float[] matrix);


   /**
    * @return - a matrix describing the transform from the beginning of the shape to the end
    */
   public float[] getCompleteMatrix();


   /**
    * modifies a the resources used to draw the object
    * @param progress - how far the drawing has got, between 0 & 1
    * @param style - the style of the animation
    */
   public void animate(float progress, AnimationStyle style);


   /**
    * @return - length of the shapes
    */
   public float getLength();
}
