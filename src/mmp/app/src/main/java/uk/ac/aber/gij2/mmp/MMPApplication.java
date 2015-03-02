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


public class MMPApplication extends android.app.Application implements
   SharedPreferences.OnSharedPreferenceChangeListener{

   private SharedPreferences preferences;

   private Scene scene;
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
      preferences.registerOnSharedPreferenceChangeListener(this);

      scene = new Scene(this);
      manoeuvreCatalogue = new ManoeuvreCatalogue(this);
      flightManager = new FlightManager(manoeuvreCatalogue);
   }


   /**
    * @param olan - string olan description of a flight
    * @throws InvalidOLANException - whether the flight was successfully built from the olan
    */
   public void buildAndSetFlight(String olan) throws InvalidOLANException {
      scene.setFlight(flightManager.buildFlight(olan));
      updateColourTheme();
   }


   public Scene getScene() {
      return scene;
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
      scene.animate(animationProgress);
   }


   /**
    * updates the colour theme for the current flight
    */
   public void updateColourTheme() {
      if (scene.getFlight() != null) {
         scene.getFlight().setColourFront(getCurrentColourTheme(R.array.ct_front));
         scene.getFlight().setColourBack(getCurrentColourTheme(R.array.ct_back));
      }

      scene.getGrid().setColourFront(getCurrentColourTheme(R.array.ct_grid));
      scene.getGrid().setColourBack(getCurrentColourTheme(R.array.ct_grid));
   }


   /**
    * @param listId - id of list of colours to look in
    * @return - array of floats representing a colour, rgba designed for use with opengl
    */
   public float[] getCurrentColourTheme(int listId) {

      int colour = getResources().obtainTypedArray(listId).getColor(
         Integer.parseInt(preferences.getString("p_ct", "0")), 0);

      return new float[] {
         (float) Color.red(colour) / 256f,
         (float) Color.green(colour) / 256f,
         (float) Color.blue(colour) / 256f,
         (float) Color.alpha(colour) / 256f
      };
   }


   public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
      switch (key) {
         case "p_ct":
            updateColourTheme();
      }
   }
}
