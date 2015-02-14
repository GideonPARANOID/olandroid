/**
 * @created 2015-02-04
 * @author gideon mw jones
 */

package uk.ac.aber.gij2.mmp;

import uk.ac.aber.gij2.mmp.visualisation.Scene;


public class MMPApplication extends android.app.Application {

   private FlightManager flightManager;
   private ManoeuvreCatalogue manoeuvreCatalogue;

   @Override
   public void onCreate() {
      flightManager = new FlightManager();

      manoeuvreCatalogue = new ManoeuvreCatalogue(this);
      flightManager.setManoeuvreCatalogue(manoeuvreCatalogue);
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
}
