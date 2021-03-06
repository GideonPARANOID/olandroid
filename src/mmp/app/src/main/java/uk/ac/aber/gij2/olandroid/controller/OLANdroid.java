/**
 * @created 2015-02-04
 * @author gideon mw jones
 */

package uk.ac.aber.gij2.olandroid.controller;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;

import uk.ac.aber.gij2.olandroid.R;
import uk.ac.aber.gij2.olandroid.view.AnimationStyle;
import uk.ac.aber.gij2.olandroid.model.Flight;
import uk.ac.aber.gij2.olandroid.view.Grass;
import uk.ac.aber.gij2.olandroid.view.Grid;
import uk.ac.aber.gij2.olandroid.view.Renderer;


public class OLANdroid extends Application implements
   SharedPreferences.OnSharedPreferenceChangeListener {

   private SharedPreferences preferences;

   private Scene scene;
   private AnimationManager animationManager;


   @Override
   public void onCreate() {

      preferences = PreferenceManager.getDefaultSharedPreferences(this);
      preferences.registerOnSharedPreferenceChangeListener(this);

      scene = new Scene();

      scene.setGround(Integer.parseInt(preferences.getString("p_ground_style", "0")) == 0 ?
         new Grid(5f, getColourTheme(R.array.colour_theme_grid)) :
         new Grass(0));

      // singleton initialisation
      ManoeuvreCatalogue.getInstance().initialise(this, R.xml.manoeurvre_catalogue);
      FlightManager.getInstance().initialise(this);
      Renderer.getInstance().initialise(this, new int[] {
         R.drawable.grass
      });

      animationManager = AnimationManager.getInstance();
      animationManager.initialise(scene,
         Float.parseFloat(preferences.getString("p_animation_speed", "1")),
         Integer.parseInt(preferences.getString("p_animation_style", "0")) == 0 ?
            AnimationStyle.PREVIOUS_TRAIL : AnimationStyle.FLYING_WING);
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

      if (scene.getGround() instanceof Grid) {
         Grid grid = (Grid) scene.getGround();
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

         case "p_ground_style":
            scene.setGround(Integer.parseInt(preferences.getString("p_ground_style", "0")) == 0 ?
               new Grid(5f, getColourTheme(R.array.colour_theme_grid)) :
               new Grass(0));
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
                  AnimationStyle.PREVIOUS_TRAIL : AnimationStyle.FLYING_WING);

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


   /**
    * @return - reference number for control schemes
    */
   public int getControlScheme() {
      return Integer.parseInt(preferences.getString("p_control_scheme", "0"));
   }


   /**
    * @param flight - the flight to set on the scene to be the current flight
    */
   public void setFlight(Flight flight) {

      // copying the name
      if (getFlight() != null && flight != null) {
         flight.setName(getFlight().getName());
      }

      scene.setFlight(flight);
      updateColourTheme();
   }


   /**
    * @return - the current flight in the scene
    */
   public Flight getFlight() {
      return scene.getFlight();
   }


   /**
    * @return - the scene
    */
   public Scene getScene() {
      return scene;
   }
}
