/**
 * @created 2015-02-04
 * @author gideon mw jones
 */

package uk.ac.aber.gij2.olandroid;

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

   private final String FILENAME = "flights.txt", olanRegex =
      "([\\+,-]*)([`]*)(\\w*)([`]*)([\\+,-]*)";
   private final Pattern olanPattern;

   private ManoeuvreCatalogue manoeuvreCatalogue;
   private OLANdroid app;

   private ArrayList<Flight> flights;


   protected FlightManager(OLANdroid app, ManoeuvreCatalogue manoeuvreCatalogue) {
      this.app = app;
      this.manoeuvreCatalogue = manoeuvreCatalogue;

      olanPattern = Pattern.compile(olanRegex);

      // only ever initialise once, otherwise arrayadapters will lose reference
      flights = new ArrayList<>();

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
         if (matcher.matches() && manoeuvreCatalogue.get(matcher.group(3)) != null) {
            manoeuvres[i] = new Manoeuvre(manoeuvreCatalogue.get(matcher.group(3)));

            // TODO: add proper support for minus

            // sorting the variable group scaling
            float groupLengthPre = Util.findOccurrences("`", matcher.group(2)),
               groupLengthPost = Util.findOccurrences("`", matcher.group(4));

            if (groupLengthPre > 0f) {
               manoeuvres[i].scaleGroup(Manoeuvre.Group.PRE, 1 / (groupLengthPre + 1f));
            }

            if (groupLengthPost > 0f) {
               manoeuvres[i].scaleGroup(Manoeuvre.Group.POST, 1 / (groupLengthPost + 1f));
            }

            // counting the pluses
            manoeuvres[i].addLengthPre(Util.findOccurrences("+", matcher.group(1)));
            manoeuvres[i].addLengthPost(Util.findOccurrences("+", matcher.group(5)));

         } else {
            throw new InvalidFlightException("invalid olan");
         }
      }

      return new Flight(manoeuvres);
   }


   /**
    * loads the flights from the file, parsing them & adding them
    */
   public void loadFlights() {
      flights.clear();

      File file = new File(app.getFilesDir(), FILENAME);

      if (file.exists()) {
         try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                  new FileInputStream(file)));

            // file is formatted such that flight name & olan are on alternate lines
            String name = reader.readLine(), olan = reader.readLine();
            while (name != null && olan != null) {

               Flight flight;

               try {
                  flight = buildFlight(olan);
                  flight.setName(name);

                  addFlight(flight, false);
                  Log.d(this.getClass().getName(), "file r: " + name + " - " + olan);

               } catch (InvalidFlightException exception) {
                  Log.d(this.getClass().getName(), exception.getMessage());
               }

               name = reader.readLine();
               olan = reader.readLine();
            }

         } catch (IOException exception) {
            Log.e(this.getClass().getName(), exception.getMessage());
         }

      } else {
         Log.d(this.getClass().getName(), "no saved flights");
      }
   }


   /**
    * gives a new name to the current flight in the scene & saves it
    * @param newName - the name for the flight
    * @throws InvalidFlightException - if there's another flight with the name already taken
    */
   public void saveCurrentFlight(String newName) throws InvalidFlightException {
      String activeFlightName = ((Flight) app.getScene().getFlight()).getName();

      // if we're not modifying the current flight, check there's no name clash
      if (!(activeFlightName == null ? "" : activeFlightName).equals(newName)) {
         for (Flight current : flights) {
            if (current.getName().equals(newName)) {
               throw new InvalidFlightException("name clash");
            }
         }
      }

      Flight flight = (Flight) app.getScene().getFlight();
      flight.setName(newName);

      addFlight(flight, true);

      saveFlights();
      loadFlights();
   }


   /**
    * saves the flights to the file
    */
   public void saveFlights() {
      File file = new File(app.getFilesDir(), FILENAME);

      try {
         BufferedWriter bufferedWriter= new BufferedWriter(new OutputStreamWriter(
               new FileOutputStream(file)));

         // file format is two lines for each flight, first is the name, second the olan
         for (Flight current : flights) {
            bufferedWriter.write(current.getName() + "\n" + current.getOLAN() + "\n");
            Log.d(this.getClass().getName(), "file w: " + current.getName() + " - "
               + current.getOLAN());
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
               break;

            } else if (flight.getOLAN().equals(current.getOLAN())
               || flight.getName().equals(current.getName())) {

               result = false;
               break;
            }
         }

         if (result) {

            Log.d(this.getClass().getName(), "flight a:" + flight.getName() + " - "
               + flight.getOLAN());
            flights.add(flight);
         }
      }

      return result;
   }


   public ArrayList<Flight> getFlights() {
      return flights;
   }


   /**
    * @param flight - the flight to remove
    */
   public void deleteFlight(Flight flight) {
      flights.remove(flight);

      Log.d(this.getClass().getName(), "flight d: " + flight.getName() + " - "
         + flight.getOLAN());

      saveFlights();
   }
}
