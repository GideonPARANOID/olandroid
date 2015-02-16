/**
 * @created 2015-02-04
 * @author gideon mw jones
 */

package uk.ac.aber.gij2.mmp;

import uk.ac.aber.gij2.mmp.visualisation.Manoeuvre;
import uk.ac.aber.gij2.mmp.visualisation.Scene;


public class FlightManager {

   private ManoeuvreCatalogue manoeuvreCatalogue;
   private Scene scene;
   private Flight currentFlight;


   public FlightManager() {
      scene = new Scene();
   }


   /**
    * @param olan - a description of a flight
    * @return - a flight defined by the olan string
    */
   public Flight buildFlight(String olan) {
      String[] figures = olan.toLowerCase().split(" ");
      Manoeuvre[] manoeuvres = new Manoeuvre[figures.length];

      for (int i = 0; i < figures.length; i++) {
         if (!figures[i].equals("")) {
            manoeuvres[i] = manoeuvreCatalogue.get(figures[i]);
         }
      }

      return new Flight(manoeuvres);
   }


   /**
    * @param olan - a description of a flight
    * @return - whether the olan is a valid description of a flight
    */
   public boolean validOLAN(String olan) {
      boolean result = true;

      String[] figures = olan.toLowerCase().split(" ");

      for (int i = 0; i < figures.length && result; i++) {
         if (!figures[i].equals("")) {
            if (manoeuvreCatalogue.get(figures[i]) == null) {
               result = false;
            }
         }
      }

      return result;
   }


   public void setCurrentFlight(Flight currentFlight) {
      this.currentFlight = currentFlight;
      scene.setFlight(currentFlight);
   }

   public void setManoeuvreCatalogue(ManoeuvreCatalogue manoeuvreCatalogue) {
      this.manoeuvreCatalogue = manoeuvreCatalogue;
   }

   public Scene getScene() {
      return scene;
   }

   public Flight getCurrentFlight() {
      return currentFlight;
   }

}
