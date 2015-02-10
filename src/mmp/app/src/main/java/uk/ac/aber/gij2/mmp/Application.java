/**
 * @created 2015-02-04
 * @author gideon mw jones
 */

package uk.ac.aber.gij2.mmp;

import uk.ac.aber.gij2.mmp.visualisation.Scene;


public class Application extends android.app.Application {


   private FlightManager flightManager;
   private ManoeuvreCatalogue manoeuvreCatalogue;

   @Override
   public void onCreate() {
      flightManager = new FlightManager();
   }


   public void setup() {
      manoeuvreCatalogue = new ManoeuvreCatalogue(this);
      flightManager.setup(manoeuvreCatalogue);
   }


   public Scene getScene() {
      return flightManager.getScene();
   }

   public boolean buildFlightFromOLAN(String olan) {
      boolean result = false;

      if (flightManager.validOLAN(olan)) {
         flightManager.buildFlight(olan);

         result = true;
      }

      return result;
   }


   public String[] getManoeuvres() {
      return manoeuvreCatalogue.buildManoeuvreList();
   }
}
