/**
 * @created 2015-02-04
 * @author gideon mw jones
 */

package uk.ac.aber.gij2.olandroid;

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

import uk.ac.aber.gij2.olandroid.visualisation.Flight;
import uk.ac.aber.gij2.olandroid.visualisation.Manoeuvre;


public class FlightManager {

   private final String FILENAME = "flights.txt", olanRegex = "([\\+,-]*)(\\w*)([\\+,-]*)";
   private final Pattern olanPattern;

   private ManoeuvreCatalogue manoeuvreCatalogue;
   private Context context;

   private ArrayList<Flight> flights;


   public FlightManager(Context context, ManoeuvreCatalogue manoeuvreCatalogue) {
      this.context = context;
      this.manoeuvreCatalogue = manoeuvreCatalogue;

      olanPattern = Pattern.compile(olanRegex);

      loadFlights();
   }


   /**
    * @param olan - a description of a flight
    * @return - a flight defined by the olan string
    * @throws InvalidFlightException - occurs if the passed olan is invalid
    */
   public Flight buildFlight(String olan) throws InvalidFlightException {

      if (olan == null) {
         throw new InvalidFlightException("invalid olan");
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
            throw new InvalidFlightException("invalid olan");
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

      if (file.exists()) {

         try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                  new FileInputStream(file)));

            String line = reader.readLine(), lineAlt = reader.readLine();
            while (line != null && lineAlt != null) {

               addFlight(parseFlight(line, lineAlt), false);

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
    * gives a newName to the current flight in the scene & saves it
    * @param newName - the name for the flight
    * @throws InvalidFlightException - if there's another flight with the name already taken
    */
   public void saveCurrentFlight(String newName) throws InvalidFlightException {

      String currentFlightName = ((OLANdroidApplication) context.getApplicationContext()).getScene()
         .getFlight().getName();

      // there might not be a name yet
      currentFlightName = currentFlightName == null ? "" : currentFlightName;

      // if we're not modifying the current flight, check there's no name clash
      if (!currentFlightName.equals(newName)) {
         for (Flight flight : flights) {
            if (flight.getName().equals(newName)) {
               throw new InvalidFlightException("name clash");
            }
         }
      }

      Flight flight = ((OLANdroidApplication) context).getScene().getFlight();
      flight.setName(newName);

      addFlight(flight, true);

      saveFlights();
      loadFlights();
   }


   /**
    * saves the flights to the file
    */
   public void saveFlights() {
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
   }


   /**
    * adds a new flight to the store, but only if unique
    * @param flight - new flight to add
    * @param overwrite - whether or not it's allowed to overwrite based on names
    * @return - whether the flight was added or not
    */
   private boolean addFlight(Flight flight, boolean overwrite) {
      boolean result = true;

      if (flight != null) {
         for (Flight current : flights) {

            if (overwrite && (flight.getName().equals(current.getName())
               && !flight.getOLAN().equals(current.getOLAN()))) {

               flights.remove(current);
               result = true;
               break;

            } else if (flight.getOLAN().equals(current.getOLAN())
               || flight.getName().equals(current.getName())) {

               result = false;
               break;
            }
         }

         if (result) {
            Log.d(this.getClass().getName(), "added flight " + flight.getName() + " - "
               + flight.getOLAN());
            flights.add(flight);
         }
      }

      return result;
   }


   /**
    * @param title - title for a flight
    * @param olan - olan for a flight
    * @return - a new flight
    */
   private Flight parseFlight(String title, String olan) {
      Flight flight = null;

      try {
         flight = buildFlight(olan);
         flight.setName(title);

      } catch (InvalidFlightException exception) {
         Log.d(this.getClass().getName(), exception.getMessage());
      }

      return flight;
   }


   public Flight[] getFlights() {
      return flights.toArray(new Flight[flights.size()]);
   }


   public void deleteFlight(Flight flight) {
      flights.remove(flight);

      Log.d(this.getClass().getName(), "removed flight " + flight.getName() + " "
         + flight.getOLAN());

      saveFlights();
   }
}
