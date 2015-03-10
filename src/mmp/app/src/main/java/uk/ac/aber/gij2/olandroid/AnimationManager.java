/**
 * @created 2015-03-06
 * @author gideon mw jones
 */

package uk.ac.aber.gij2.olandroid;

import java.util.Observable;

import uk.ac.aber.gij2.olandroid.visualisation.Scene;


public class AnimationManager extends Observable {

   // TODO: make relative to flight length
   private float animationProgress, animationStep;
   private Thread animationThread;
   private FlightAnimator animator;

   private Scene scene;


   public AnimationManager(Scene scene) {
      // need to use real constructor to use final variables
      animationProgress = 1f;
      animationStep = 1f / 100f;

      this.scene = scene;
   }


   /**
    * sets off or kills the animation thread
    * @param play - start or stop the current animation
    */
   public void animationPlayToggle(boolean play) {

      if (play && animationProgress == 1f) {
         animationProgress = 0f;

         animator = new FlightAnimator(animationStep);
         animationThread = new Thread(animator);
         animationThread.start();

      } else if (play && animationProgress >= 0f && animationProgress < 1f) {
         animator = new FlightAnimator(animationStep);
         animationThread = new Thread(animator);
         animationThread.start();


      } else if (!play) {
         animator.terminate();

         try {
            animationThread.join();
         } catch (InterruptedException exception) {
            System.err.println(exception.getMessage());
         }
      }
   }


   /**
    * setting the degree to which the animation has progressed
    * @param animationProgress - number for the animation progress, bounded between 0 & 1
    */
   public void setAnimationProgress(float animationProgress) {
      if (animationProgress > 1f) {
         this.animationProgress = 1f;

      } else if (animationProgress < 0f) {
         this.animationProgress = 0f;

      } else {
         this.animationProgress = animationProgress;
      }

      setChanged();
      notifyObservers();

      scene.animate(this.animationProgress);
   }


   public float getAnimationProgress() {
      return animationProgress;
   }


   /**
    * animation class
    */
   private class FlightAnimator implements Runnable {

      private boolean running;
      private float step;
      private long wait;


      /**
       * @param step - how much to iterate by, ties to the length of the animation too
       */
      public FlightAnimator(float step) {
         super();

         this.step = step;
         wait = (long) (1000 * step);
      }


      public void run() {
         running = true;

         try {
            for (float i = getAnimationProgress() / step, limit = 1f / step;
               i <= limit && running; i++) {

               setAnimationProgress(step * i);
               Thread.sleep(wait);
            }

         } catch (InterruptedException exception) {
            exception.printStackTrace();
         }
      }

      public void terminate() {
         running = false;
      }
   }
}
