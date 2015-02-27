/**
 * @created 2015-02-04
 * @author gideon mw jones
 */

package uk.ac.aber.gij2.mmp;

import android.content.Context;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.ac.aber.gij2.mmp.visualisation.Manoeuvre;
import uk.ac.aber.gij2.mmp.visualisation.Scene;


public class FlightManager {

   private ManoeuvreCatalogue manoeuvreCatalogue;
   private Scene scene;
   private Flight currentFlight;
   private final String olanRegex = "([\\+,-]*)(\\w*)([\\+,-]*)";
   private final Pattern olanPattern;


   public FlightManager(Context context) {
      scene = new Scene(context);
      olanPattern = Pattern.compile(olanRegex);
   }


   /**
    * @param olan - a description of a flight
    * @return - a flight defined by the olan string
    * @throws InvalidOLANException - occurs if the passed olan is invalid
    */
   public Flight buildFlight(String olan) throws InvalidOLANException {

      // leading spaces are a challenge, so get rid of them
      if (olan.length() >= 1 && olan.substring(0, 1).equals(" ")) {
         olan = olan.substring(1);
      }

      String[] figures = olan.trim().toLowerCase().split(" ");
      Manoeuvre[] manoeuvres = new Manoeuvre[figures.length];

      for (int i = 0; i < figures.length; i++) {

         Matcher matcher = olanPattern.matcher(figures[i]);

         // testing if the regex holds true & that the final figure is in the catalogue
         if (matcher.matches() && manoeuvreCatalogue.get(matcher.group(2)) != null) {
            manoeuvres[i] = new Manoeuvre(manoeuvreCatalogue.get(matcher.group(2)));

            // TODO: add proper support for minus, interpret for positive exclusively
            manoeuvres[i].setEntryExitLengths(matcher.group(1).length(), matcher.group(3).length());

         } else {
            throw new InvalidOLANException();
         }
      }

      return new Flight(manoeuvres);
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
