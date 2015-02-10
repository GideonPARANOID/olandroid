/**
 * @created 2015-02-04
 * @author gideon mw jones
 */

package uk.ac.aber.gij2.mmp;

import java.util.ArrayList;

import uk.ac.aber.gij2.mmp.visualisation.Manoeuvre;
import uk.ac.aber.gij2.mmp.visualisation.Scene;


public class FlightManager {

   private ManoeuvreCatalogue manoeuvreCatalogue;
   private Scene scene;
   private ArrayList<Flight> flights;


   public FlightManager() {
      scene = new Scene();
      flights = new ArrayList<>();
   }


   /**
    * sets up the flight manager
    * @param manoeuvreCatalogue - the catalogue of available manoeuvres
    */
   public void setup(ManoeuvreCatalogue manoeuvreCatalogue) {
      this.manoeuvreCatalogue = manoeuvreCatalogue;

      scene.setup();

      // demo
      ArrayList<Manoeuvre> manoeuvres = new ArrayList<>();
      manoeuvres.add(manoeuvreCatalogue.getManoeuvre("v"));
      manoeuvres.add(manoeuvreCatalogue.getManoeuvre("d"));
//      manoeuvres.add(manoeuvreCatalogue.getManoeuvre("z"));
      Flight demo = new Flight(manoeuvres);

      flights.add(demo);

      scene.setFlight(flights.get(0));
   }


   public Flight buildFlight(String olan) {
      ArrayList<Manoeuvre> manoeuvres = new ArrayList<>();

      //TODO: implement

      return new Flight(manoeuvres);
   }


   public boolean validOLAN(String olan) {
      return true;
   }

   public Scene getScene() {
      return scene;
   }
}
