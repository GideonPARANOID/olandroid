/**
 * @created 2015-02-04
 * @author gideon mw jones
 */

package uk.ac.aber.gij2.mmp;

import java.util.ArrayList;

import uk.ac.aber.gij2.mmp.visualisation.Manoeuvre;


public class Flight {
   private ArrayList<Manoeuvre> manoeuvres;


   public Flight(ArrayList<Manoeuvre> manoeuvres) {
      this.manoeuvres = manoeuvres;
   }


   public ArrayList<Manoeuvre> getManoeuvres() {
      return manoeuvres;
   }
}
