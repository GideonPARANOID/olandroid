/**
 * @created 2015-02-04
 * @author gideon mw jones
 */

package uk.ac.aber.gij2.mmp;

import android.graphics.Color;

import uk.ac.aber.gij2.mmp.visualisation.Scene;


public class MMPApplication extends android.app.Application {

   private FlightManager flightManager;
   private ManoeuvreCatalogue manoeuvreCatalogue;
   private float animationProgress;


   @Override
   public void onCreate() {
      flightManager = new FlightManager(this);

      manoeuvreCatalogue = new ManoeuvreCatalogue(this);
      flightManager.setManoeuvreCatalogue(manoeuvreCatalogue);

      animationProgress = 1;
   }


   /**
    * @param olan - string olan description of a flight
    * @return - whether the flight was successfully built from the olan
    */
   public boolean buildFlight(String olan) {
      boolean result = false;

      if (flightManager.validOLAN(olan)) {
         flightManager.setCurrentFlight(flightManager.buildFlight(olan));

         result = true;
      }

      return result;
   }


   public Scene getScene() {
      return flightManager.getScene();
   }


   public ManoeuvreCatalogue getManoeuvreCatalogue() {
      return manoeuvreCatalogue;
   }


   /**
    * @param id - resource id of a colour to use
    * @return - array of floats depicting a colour
    */
   public float[] buildColourArray(int id) {
      int colour = getResources().getColor(id);
      return new float[]{
         (float) Color.red(colour) / 256f,
         (float) Color.green(colour) / 256f,
         (float) Color.blue(colour) / 256f,
         (float) Color.alpha(colour) / 256f
      };
   }


   public float getAnimationProgress() {
      return animationProgress;
   }


   public void setAnimationProgress(float animationProgress) {
      this.animationProgress = animationProgress;

      flightManager.getCurrentFlight().animate(animationProgress);
   }
}
