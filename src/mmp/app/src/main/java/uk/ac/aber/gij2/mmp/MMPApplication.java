/**
 * @created 2015-02-04
 * @author gideon mw jones
 */

package uk.ac.aber.gij2.mmp;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;

import uk.ac.aber.gij2.mmp.visualisation.Scene;


public class MMPApplication extends Application implements
   SharedPreferences.OnSharedPreferenceChangeListener{

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
    * @throws InvalidOLANException - whether the flight was successfully built from the olan
    */
   public void buildAndSetFlight(String olan) throws InvalidOLANException {
      scene.setFlight(flightManager.buildFlight(olan));
      updateColourTheme();
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
