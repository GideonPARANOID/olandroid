/**
 * @created 2015-01-28
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.olandroid.controller;


import uk.ac.aber.gij2.olandroid.view.Drawable;
import uk.ac.aber.gij2.olandroid.model.Flight;

public class Scene {

   private Drawable ground;
   private Flight flight;


   /**
    * draws the current scene
    * @param initialMatrix - the initial matrix to start drawing from
    */
   public void draw(float[] initialMatrix) {
      ground.draw(initialMatrix);

      if (flight != null) {
         flight.draw(initialMatrix);
      }
   }


   /**
    * @return - the flight
    */
   public Flight getFlight() {
      return flight;
   }


   /**
    * @param flight - the flight
    */
   public void setFlight(Flight flight) {
      this.flight = flight;
   }


   /**
    * @return - the ground
    */
   public Drawable getGround() {
      return ground;
   }


   /**
    * @param ground - a drawable ground object
    */
   public void setGround(Drawable ground) {
      this.ground = ground;
   }
}

