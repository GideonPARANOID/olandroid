/**
 * @created 2015-02-04
 * @author gideon mw jones
 */

package uk.ac.aber.gij2.mmp;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.ac.aber.gij2.mmp.visualisation.Flight;
import uk.ac.aber.gij2.mmp.visualisation.Manoeuvre;


public class FlightManager {

   private ManoeuvreCatalogue manoeuvreCatalogue;
   private Context context;

   private ArrayList<Flight> flights;



   private final String FILENAME = "flights.txt", olanRegex = "([\\+,-]*)(\\w*)([\\+,-]*)";
   private final Pattern olanPattern;



   public FlightManager(Context context, ManoeuvreCatalogue manoeuvreCatalogue) {
      this.context = context;
      this.manoeuvreCatalogue = manoeuvreCatalogue;


      olanPattern = Pattern.compile(olanRegex);

      loadFlights();
   }


   /**
    * @param olan - a description of a flight
    * @return - a flight defined by the olan string
    * @throws InvalidOLANException - occurs if the passed olan is invalid
    */
   public Flight buildFlight(String olan) throws InvalidOLANException {

      if (olan == null) {
         throw new InvalidOLANException();
      }

      // leading spaces are a challenge, so get rid of them
      if (olan.length() >= 1 && olan.substring(0, 1).equals(" ")) {
         olan = olan.substring(1);
      }

      String[] figures = olan.trim().toLowerCase().split(" ");
      Manoeuvre[] manoeuvres = new Manoeuvre[figures.length];

      // going through the figures
      for (int i = 0; i < figures.length; i++) {
         Matcher matcher = olanPattern.matcher(figures[i]);

         // testing if the regex holds true & that the final figure is in the catalogue
         if (matcher.matches() && manoeuvreCatalogue.get(matcher.group(2)) != null) {
            manoeuvres[i] = new Manoeuvre(manoeuvreCatalogue.get(matcher.group(2)));

            // TODO: add proper support for minus

            // counting the pluses
            manoeuvres[i].addEntryLength(findOccurrences("+", matcher.group(1)));
            manoeuvres[i].addExitLength(findOccurrences("+", matcher.group(3)));

         } else {
            throw new InvalidOLANException();
         }
      }

      return new Flight(manoeuvres);
   }


   /**
    * utility function for counting occurrences of a string in a string
    * @param search - string to look for
    * @param text - string to look in
    * @return - number of occurrences
    */
   private int findOccurrences(String search, String text) {
      return text.length() - text.replace(search, "").length();
   }


   public void loadFlights() {
      flights = new ArrayList<>();

      File file = new File(context.getFilesDir(), FILENAME);

      Log.d(this.getClass().getName(), "file: " + context.getFilesDir().getAbsolutePath() + "/" + FILENAME);

      if (file.exists()) {

         try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                  new FileInputStream(file)));

            String line = reader.readLine(), lineAlt = reader.readLine();
            while (line != null && lineAlt != null) {


               addFlight(parseFlight(line, lineAlt));

               line = reader.readLine();
               lineAlt = reader.readLine();
            }

         } catch (IOException exception) {
            Log.e(this.getClass().getName(), exception.getMessage());
         }

      } else {
         Log.d(this.getClass().getName(), "no saved flights");
      }

   }


   /**
    * saves the current flight in the scene
    * @param name - the name for the flight
    */
   public void saveCurrentFlight(String name) {
      Flight flight = ((MMPApplication) context).getScene().getFlight();
      flight.setName(name);

      addFlight(flight);

      File file = new File(context.getFilesDir(), FILENAME);

      try {
         BufferedWriter bufferedWriter= new BufferedWriter(new OutputStreamWriter(
               new FileOutputStream(file)));

         // file format is two lines for each flight, first is the name, second the olan
         for (Flight current : flights) {
            bufferedWriter.write(current.getName() + "\n" + current.getOLAN() + "\n");
         }

         bufferedWriter.close();

      } catch (IOException exception) {
         exception.printStackTrace();
      }

      // keeping things synchronised
      loadFlights();
   }


   /**
    * adds a new flight to the store, but only if unique
    * @param flight - new flight to add
    */
   private void addFlight(Flight flight) {

      if (flight != null) {
         boolean add = true;

         for (Flight current : flights) {
            if (flight.getOLAN().equals(current.getOLAN())
               || flight.getName().equals(current.getName())) {

               add = false;
               break;
            }
         }

         if (add) {
            Log.d(this.getClass().getName(), "added flight " + flight.getName());
            flights.add(flight);
         }
      }
   }


   /**
    *
    * @param title - title for a flight
    * @param olan - olan for a flight
    * @return - a new flight
    */
   private Flight parseFlight(String title, String olan) {
      try {
         Flight flight = buildFlight(olan);
         flight.setName(title);
         return flight;

      } catch (InvalidOLANException exception) {
         Log.d(this.getClass().getName(), "invalid flight");
         return null;
      }
   }


   public Flight[] getFlights() {
      return flights.toArray(new Flight[flights.size()]);
   }
}
