/**
 * @created 2015-01-31
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.mmp.visualisation;


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
    * @param progress - how far the drawing has got
    */
   public void animate(float progress);


   /**
    * @return - length of the shapes
    */
   public float getLength();
}
