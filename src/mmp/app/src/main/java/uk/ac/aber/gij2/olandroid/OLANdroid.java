/**
 * @created 2015-02-04
 * @author gideon mw jones
 */

package uk.ac.aber.gij2.olandroid;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;

import uk.ac.aber.gij2.olandroid.visualisation.Scene;


public class OLANdroid extends Application implements SharedPreferences.OnSharedPreferenceChangeListener {

   private SharedPreferences preferences;

   private Scene scene;
   private ManoeuvreCatalogue manoeuvreCatalogue;
   private FlightManager flightManager;
   private AnimationManager animationManager;


   @Override
   public void onCreate() {
      preferences = PreferenceManager.getDefaultSharedPreferences(this);
      preferences.registerOnSharedPreferenceChangeListener(this);

      scene = new Scene(this);
      manoeuvreCatalogue = new ManoeuvreCatalogue(this);
      flightManager = new FlightManager(this, manoeuvreCatalogue);
      animationManager = new AnimationManager(scene);
   }


   /**
    * @param olan - string olan description of a flight
    * @throws InvalidFlightException - whether the flight was successfully built from the olan
    */
   public void buildAndSetFlight(String olan) throws InvalidFlightException {

      String oldName = getScene().getFlight() != null ? scene.getFlight().getName() : null;
      scene.setFlight(flightManager.buildFlight(olan));
      scene.getFlight().setName(oldName);
      updateColourTheme();
   }


   /**
    * updates the colour theme for the current flight
    */
   public void updateColourTheme() {
      if (scene.getFlight() != null) {
         scene.getFlight().setColourFront(getCurrentColourTheme(R.array.colour_theme_front));
         scene.getFlight().setColourBack(getCurrentColourTheme(R.array.colour_theme_back));
      }

      scene.getGrid().setColourFront(getCurrentColourTheme(R.array.colour_theme_grid));
      scene.getGrid().setColourBack(getCurrentColourTheme(R.array.colour_theme_grid));
   }


   /**
    * @param listId - id of list of colours to look in
    * @return - array of floats representing a colour, rgba designed for use with opengl
    */
   public float[] getCurrentColourTheme(int listId) {
      int colour = getResources().obtainTypedArray(listId).getColor(
         Integer.parseInt(preferences.getString("p_colour_theme", "0")), 0);

      return new float[] {
         (float) Color.red(colour) / 256f,
         (float) Color.green(colour) / 256f,
         (float) Color.blue(colour) / 256f,
         (float) Color.alpha(colour) / 256f
      };
   }


   @Override
   public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
      switch (key) {
         case "p_colour_theme":
            updateColourTheme();
            break;
         case "p_controls_scheme":

            break;
      }
   }


   /**
    * checks whether it was the first launch, & if so, flip the variable
    * @return whether it was the first launch of the application or not
    */
   public boolean getIsFirstLaunch() {
      boolean result = preferences.getBoolean("p_first_launch", true);
      preferences.edit().putBoolean("p_first_launch", result ? false : false).commit();
      return result;
   }


   public int getControlScheme() {
      return Integer.parseInt(preferences.getString("p_control_scheme", "0"));
   }


   public Scene getScene() {
      return scene;
   }


   public ManoeuvreCatalogue getManoeuvreCatalogue() {
      return manoeuvreCatalogue;
   }


   public FlightManager getFlightManager() {
      return flightManager;
   }


   public AnimationManager getAnimationManager() {
      return animationManager;
   }
}
