/**
 * @created 2015-03-06
 * @author gideon mw jones
 */

package uk.ac.aber.gij2.olandroid;

import java.util.Observable;

import uk.ac.aber.gij2.olandroid.visualisation.Scene;


public class AnimationManager extends Observable {

   private float animationProgress, animationStep, animationSpeed;
   private Thread animationThread;
   private FlightAnimator animator;

   private Scene scene;


   /**
    * @param scene - scene to find things to draw in
    * @param animationSpeed - a factor by which to animate
    */
   public AnimationManager(Scene scene, float animationSpeed) {
      this.animationSpeed = animationSpeed;

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

         animator = new FlightAnimator(animationStep, scene.getFlight().getLength(),
            animationSpeed);
         animationThread = new Thread(animator);
         animationThread.start();

      } else if (play && animationProgress >= 0f && animationProgress < 1f) {
         animator = new FlightAnimator(animationStep, scene.getFlight().getLength(),
            animationSpeed);
         animationThread = new Thread(animator);
         animationThread.start();


      } else if (!play) {
         animator.terminate();

         try {
            animationThread.join();
            animationThread = null;
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



   public void setAnimationSpeed(float animationSpeed) {
      this.animationSpeed = animationSpeed;
   }

   /**
    * animation class
    */
   private class FlightAnimator implements Runnable {

      private boolean running;
      private float step;
      private long wait;


      /**
       * all three parameters play a role in how the speed at which the flight is animated
       * @param step - how much to iterate by, ties to the length of the animation too
       * @param length - how long the flight is
       * @param factor - scaling factor for the animation speed
       */
      public FlightAnimator(float step, float length, float factor) {
         super();

         this.step = (step / length) * factor * 10;
         wait = (long) (1000 * step);
      }


      /**
       * actual drawing loop
       */
      public void run() {
         running = true;

         try {
            for (float i = getAnimationProgress() / step, limit = (1f / step) + 1;
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
