/**
 * @created 2015-02-04
 * @author gideon mw jones
 */

package uk.ac.aber.gij2.mmp;

import java.util.ArrayList;

import uk.ac.aber.gij2.mmp.visualisation.Drawable;
import uk.ac.aber.gij2.mmp.visualisation.Manoeuvre;


public class Flight implements Drawable {
   private ArrayList<Manoeuvre> manoeuvres;


   public Flight(ArrayList<Manoeuvre> manoeuvres) {
      this.manoeuvres = manoeuvres;
   }


   public ArrayList<Manoeuvre> getManoeuvres() {
      return manoeuvres;
   }


   /**
    * @return - olan description of flight
    */
   public String getOLANDescription() {

      String olanDescription = "";
      for (Manoeuvre manoeuvre : manoeuvres) {
         olanDescription += " " + manoeuvre.getOlan();
      }

      return olanDescription;
   }


   public void draw(float[] initialMatrix) {

      for (Manoeuvre manoeuvre : manoeuvres) {
         manoeuvre.draw(initialMatrix);
      }


   }
}
