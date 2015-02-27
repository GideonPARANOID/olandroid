/**
 * @created 2015-02-27
 * @author gideon mw jones
 */

package uk.ac.aber.gij2.mmp.visualisation;

import uk.ac.aber.gij2.mmp.MMPApplication;


public class FlightAnimator implements Runnable {

   private float step;
   private long wait;

   private MMPApplication application;

   /**
    *
    * @param application - context to look for the animate function
    * @param step - how much to iterate by, ties to the length of the animation too
    */
   public FlightAnimator(MMPApplication application, float step) {
      super();

      this.application = application;
      this.step = step;
      wait = (long) (1000 * step);
   }

   public void run() {

      try {
         for (float i = 0; i < 1; i += step) {
            application.setAnimationProgress(i);
            Thread.sleep(wait);
         }

      } catch (InterruptedException exception) {
         exception.printStackTrace();
      }
   }
}
