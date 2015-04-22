/**
 * @created 2015-01-28
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.olandroid.controller;


import uk.ac.aber.gij2.olandroid.view.Drawable;
import uk.ac.aber.gij2.olandroid.model.Flight;

public class Scene {

   private Drawable plane;
   private Flight flight;


   /**
    * draws the current scene
    * @param initialMatrix - the initial matrix to start drawing from
    */
   public void draw(float[] initialMatrix) {
      plane.draw(initialMatrix);

      if (flight != null) {
         flight.draw(initialMatrix);
      }

//      new Aircraft(0).draw(initialMatrix);
   }

   public Flight getFlight() {
      return flight;
   }

   public void setFlight(Flight flight) {
      this.flight = flight;
   }

   public Drawable getPlane() {
      return plane;
   }

   public void setPlane(Drawable plane) {
      this.plane = plane;
   }
}

