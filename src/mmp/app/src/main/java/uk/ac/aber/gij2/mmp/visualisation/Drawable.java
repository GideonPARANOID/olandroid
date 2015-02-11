/**
 * @created 2015-01-31
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.mmp.visualisation;


public interface Drawable {
   public void draw(float[] matrix);

   /**
    * @return - a matrix describing the transform from the beginning of the shape to the end
    */
   public float[] getCompleteMatrix();
}
