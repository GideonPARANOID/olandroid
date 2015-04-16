/**
 * @created 2015-03-06
 * @author gideon mw jones
 */

package uk.ac.aber.gij2.olandroid.controller;

import android.util.Log;

import java.util.Observable;

import uk.ac.aber.gij2.olandroid.view.AnimationStyle;


public class AnimationManager extends Observable {

   private static AnimationManager instance;

   public static final float WING_LENGTH = 2f;

   private float progress, step, speed;
   private Thread animationThread;
   private AnimationRunner animator;
   private Scene scene;
   private AnimationStyle style;


   /**
    * @return - instance of this class, singleton access
    */
   public static AnimationManager getInstance() {
      if (instance == null) {
         instance = new AnimationManager();
      }

      return instance;
   }

   private AnimationManager() {}


   /**
    * @param scene - scene to find things to draw in
    * @param speed - a factor by which to animate
    */
   public void initialise(Scene scene, float speed, AnimationStyle style) {
      this.speed = speed;
      this.scene = scene;
      this.style = style;

      // need to use real constructor to use final variables
      progress = 1f;
      step = 1f / 100f;
   }


   /**
    * sets off or kills the animation thread
    * @param play - start or stop the current animation
    */
   public void animationPlayToggle(boolean play) {
      if (play && progress == 1f) {
         progress = 0f;

         animator = new AnimationRunner(step, scene.getFlight().getLength(), speed);
         animationThread = new Thread(animator);
         animationThread.start();

      } else if (play && progress >= 0f && progress < 1f) {
         animator = new AnimationRunner(step, scene.getFlight().getLength(), speed);
         animationThread = new Thread(animator);
         animationThread.start();

      } else if (!play) {
         try {
            animator.terminate();
            animationThread.join();

         } catch (InterruptedException | NullPointerException exception) {
            Log.e(this.getClass().getName(), "trouble with ending the animation thread");
         }

         animationThread = null;
      }
   }


   /**
    * setting the degree to which the animation has progressed
    * @param progress - number for the animation progress, bounded between 0 & 1
    * @throws NullPointerException - if there's no flight
    */
   public void setProgress(float progress) throws NullPointerException {
      if (progress > 1f) {
         this.progress = 1f;

      } else if (progress < 0f) {
         this.progress = 0f;

      } else {
         this.progress = progress;
      }

      setChanged();
      notifyObservers();

      scene.getFlight().animate(0f, this.progress, style);
   }


   public float getProgress() {
      return progress;
   }

   public void setSpeed(float speed) {
      this.speed = speed;
   }

   public AnimationStyle getStyle() {
      return style;
   }

   public void setStyle(AnimationStyle style) {
      this.style = style;
   }


   /**
    * loops through the animation
    */
   private class AnimationRunner implements Runnable {

      private boolean running;
      private float step;
      private long wait;


      /**
       * all three parameters play a role in how the speed at which the flight is animated
       * @param step - how much to iterate by, ties to the length of the animation too
       * @param length - how long the flight is
       * @param factor - scaling factor for the animation speed
       */
      public AnimationRunner(float step, float length, float factor) {
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
            for (float i = getProgress() / step, limit = (1f / step) + 1; i <= limit && running;
               i++) {

               setProgress(step * i);
               Thread.sleep(wait);
            }

         } catch (InterruptedException | NullPointerException exception) {
            // occurs most often when a flight is nullified
            running = false;
            Log.d(this.getClass().getName(), "thread dead");
         }
      }

      public void terminate() {
         running = false;
      }
   }
}
