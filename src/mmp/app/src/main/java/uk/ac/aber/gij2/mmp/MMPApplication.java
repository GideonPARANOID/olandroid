/**
 * @created 2015-02-04
 * @author gideon mw jones
 */

package uk.ac.aber.gij2.mmp;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;

import uk.ac.aber.gij2.mmp.visualisation.FlightAnimator;
import uk.ac.aber.gij2.mmp.visualisation.Scene;


public class MMPApplication extends android.app.Application {

   private SharedPreferences preferences;

   private FlightManager flightManager;
   private ManoeuvreCatalogue manoeuvreCatalogue;

   // TODO: relative to flight length
   private float animationProgress, animationStep;
   private boolean animationPlaying;
   private Thread animationThread;


   public MMPApplication() {
      // need to use real constructor to use final variables
      animationProgress = 1;
      animationPlaying = false;
      animationStep = 1f / 100f;
   }


   @Override
   public void onCreate() {
      preferences = PreferenceManager.getDefaultSharedPreferences(this);

      flightManager = new FlightManager(this);
      manoeuvreCatalogue = new ManoeuvreCatalogue(this);
      flightManager.setManoeuvreCatalogue(manoeuvreCatalogue);
   }


   /**
    * @param olan - string olan description of a flight
    * @throws InvalidOLANException - whether the flight was successfully built from the olan
    */
   public void buildFlight(String olan) throws InvalidOLANException {
      flightManager.setCurrentFlight(flightManager.buildFlight(olan));
   }


   public Scene getScene() {
      return flightManager.getScene();
   }


   public ManoeuvreCatalogue getManoeuvreCatalogue() {
      return manoeuvreCatalogue;
   }


   public float getAnimationProgress() {
      return animationProgress;
   }


   /**
    * sets off or kills the animation thread
    * @param play - start or stop the current animation
    */
   public void animationPlayToggle(boolean play) {

      if (play && !animationPlaying) {
         animationProgress = 0;
         animationThread = new Thread(new FlightAnimator(this, animationStep));
         animationThread.start();

      } else if (!play && !animationPlaying) {
         animationThread.interrupt();
         animationPlaying = false;
      }
   }


   public void setAnimationProgress(float animationProgress) {
      this.animationProgress = animationProgress;

      // TODO: update seekbar somehow
      flightManager.getCurrentFlight().animate(animationProgress);
   }


   /**
    * @param id - resource id of a colour to use
    * @return - array of floats depicting a colour
    */
   public float[] buildColourArray(int id) {
      int colour = getResources().getColor(id);
      return new float[] {
         (float) Color.red(colour) / 256f,
         (float) Color.green(colour) / 256f,
         (float) Color.blue(colour) / 256f,
         (float) Color.alpha(colour) / 256f
      };
   }


   /**
    * @param index - index of resource list of a colour to use
    * @param listId - id of list of colours to look in
    * @return - array of floats depicting a colour
    */
   public float[] buildColourArray(int index, int listId) {
      int colour = getResources().obtainTypedArray(listId).getColor(index, 0);
      return new float[] {
         (float) Color.red(colour) / 256f,
         (float) Color.green(colour) / 256f,
         (float) Color.blue(colour) / 256f,
         (float) Color.alpha(colour) / 256f
      };
   }


   /**
    * updates the colour theme for the current flight
    */
   public void updateColourTheme() {
      flightManager.getCurrentFlight().setColourFront(getCurrentColourTheme(
         R.array.p_colour_theme_front));
      flightManager.getCurrentFlight().setColourBack(getCurrentColourTheme(
         R.array.p_colour_theme_back));
   }


   /**
    * @param listId - id of list of colours to look in
    * @return - array of floats representing a colour
    */
   public float[] getCurrentColourTheme(int listId) {
      return buildColourArray(Integer.parseInt(preferences.getString("p_colour_theme", "0")), listId);
   }
}
