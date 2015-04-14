/**
 * @created 2015-02-04
 * @author gideon mw jones
 */

package uk.ac.aber.gij2.olandroid;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;

import uk.ac.aber.gij2.olandroid.visualisation.AnimationStyle;
import uk.ac.aber.gij2.olandroid.visualisation.Flight;
import uk.ac.aber.gij2.olandroid.visualisation.Grid;
import uk.ac.aber.gij2.olandroid.visualisation.Ground;
import uk.ac.aber.gij2.olandroid.visualisation.Scene;


public class OLANdroid extends Application implements
   SharedPreferences.OnSharedPreferenceChangeListener {

   private SharedPreferences preferences;

   private Scene scene;
   private FlightManager flightManager;
   private AnimationManager animationManager;


   @Override
   public void onCreate() {
      preferences = PreferenceManager.getDefaultSharedPreferences(this);
      preferences.registerOnSharedPreferenceChangeListener(this);

      scene = new Scene();

      scene.setPlane(Integer.parseInt(preferences.getString("p_plane_style", "0")) == 0 ?
         new Grid(5f, getColourTheme(R.array.colour_theme_grid)) :
         new Ground(0));

      ManoeuvreCatalogue manoeuvreCatalogue = ManoeuvreCatalogue.getInstance();
      manoeuvreCatalogue.initialise(this, R.xml.manoeurvre_catalogue);

      flightManager = FlightManager.getInstance();
      flightManager.initialise(this, manoeuvreCatalogue);

      animationManager = AnimationManager.getInstance();
      animationManager.initialise(scene,
         Float.parseFloat(preferences.getString("p_animation_speed", "1")),
         Integer.parseInt(preferences.getString("p_animation_style", "0")) == 0 ?
            AnimationStyle.ONE : AnimationStyle.TWO);

      // setting up the renderer early, with the texture catalogue
      new uk.ac.aber.gij2.olandroid.visualisation.Renderer(this, new int[] {
         R.drawable.grass
      });
   }


   /**
    * @param olan - string olan description of a flight
    * @throws InvalidFlightException - whether the flight was successfully built from the olan
    */
   public void buildAndSetFlight(String olan) throws InvalidFlightException {

      String oldName = getScene().getFlight() != null ? scene.getFlight().getName() : null;

      Flight flight = flightManager.buildFlight(olan, getAutocorrect());

      flight.setName(oldName);
      scene.setFlight(flight);
      updateColourTheme();
   }


   /**
    * updates the colour theme for the current flight
    */
   public void updateColourTheme() {

      Flight flight = scene.getFlight();
      if (flight != null) {

         flight.setColourFront(getColourTheme(R.array.colour_theme_front));
         flight.setColourBack(getColourTheme(R.array.colour_theme_back));
      }

      if (scene.getPlane() instanceof Grid) {
         Grid grid = (Grid) scene.getPlane();
         grid.setColourFront(getColourTheme(R.array.colour_theme_grid));
         grid.setColourBack(getColourTheme(R.array.colour_theme_grid));
      }
   }


   /**
    * @param listId - id of list of colours to look in
    * @return - array of floats representing a colour, rgba designed for use with opengl
    */
   public float[] getColourTheme(int listId) {
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
         case "p_autocorrect":
           break;

         case "p_plane_style":
            scene.setPlane(Integer.parseInt(preferences.getString("p_plane_style", "0")) == 0 ?
               new Grid(5f, getColourTheme(R.array.colour_theme_grid)) :
               new Ground(0));
            break;

         case "p_colour_theme":
            updateColourTheme();
            break;

         case "p_animation_speed":
            animationManager.setSpeed(Float.parseFloat(
               preferences.getString("p_animation_speed", "1")));
            break;

         case "p_animation_style":
            animationManager.setStyle(Integer.parseInt(
                  preferences.getString("p_animation_style", "0")) == 0 ?
                  AnimationStyle.ONE : AnimationStyle.TWO);

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
      preferences.edit().putBoolean("p_first_launch", result ? false : false).apply();
      return result;
   }


   public boolean getAutocorrect() {
      return preferences.getBoolean("p_autocorrect", true);
   }


   public int getControlScheme() {
      return Integer.parseInt(preferences.getString("p_control_scheme", "0"));
   }


   public void setFlight(Flight flight) {
      scene.setFlight(flight);
      updateColourTheme();
   }

   public Flight getFlight() {
      return scene.getFlight();
   }


   public Scene getScene() {
      return scene;
   }


   public FlightManager getFlightManager() {
      return flightManager;
   }
}
