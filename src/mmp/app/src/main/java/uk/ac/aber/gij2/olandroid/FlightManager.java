/**
 * @created 2015-02-04
 * @author gideon mw jones
 */

package uk.ac.aber.gij2.olandroid;

import android.opengl.Matrix;
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

   private final String FILENAME = "flights.txt", figureRegex =
      "([\\+,-]*)([`]*)(\\w*)([`]*)([\\+,-]*)", scaleRegex = "(\\d)%";
   private final Pattern figurePattern, scalePattern;

   private ManoeuvreCatalogue manoeuvreCatalogue;
   private OLANdroid app;

   private ArrayList<Flight> flights;


   protected FlightManager(OLANdroid app, ManoeuvreCatalogue manoeuvreCatalogue) {
      this.app = app;
      this.manoeuvreCatalogue = manoeuvreCatalogue;

      figurePattern = Pattern.compile(figureRegex);
      scalePattern = Pattern.compile(scaleRegex);

      // only ever initialise once, otherwise arrayadapters will lose reference
      flights = new ArrayList<>();

      loadFlights();
   }


   /**
    * @param olan - a description of a flight
    * @param correct - whether or not to correct the height of the flight
    * @return - a flight defined by the olan string
    * @throws InvalidFlightException - occurs if the passed olan is invalid
    */
   public Flight buildFlight(String olan, boolean correct) throws InvalidFlightException {

      if (olan == null) {
         throw new InvalidFlightException("invalid olan");
      }

      // getting rid of multiple spaces, so we don't break the split
      olan = olan.replaceAll(" +", " ");

      // leading spaces are a challenge, so get rid of them
      if (olan.length() >= 1 && olan.substring(0, 1).equals(" ")) {
         olan = olan.substring(1);
      }

      String[] figures = olan.trim().toLowerCase().split(" ");

      ArrayList<Manoeuvre> manoeuvres = new ArrayList<>();

      // going through the figures
      for (int i = 0; i < figures.length; i++) {
         int fullScale = 1;

         Matcher scaleMatcher = scalePattern.matcher(figures[i]);

         // if we're dealing with a scale figure, grab the scale & move onto the next figure
         if (scaleMatcher.matches()) {
            fullScale = Integer.parseInt(scaleMatcher.group(1));
            i++;
         }

         Matcher figureMatcher = figurePattern.matcher(figures[i]);

         // testing if the regex holds true & that the final figure is in the catalogue
         if (figureMatcher.matches() && manoeuvreCatalogue.get(figureMatcher.group(3)) != null) {
            Manoeuvre manoeuvre = new Manoeuvre(manoeuvreCatalogue.get(figureMatcher.group(3)));

            // TODO: add proper support for full range of modifiers - minus & tilde

            if (fullScale > 1) {
               manoeuvre.scaleGroup(Manoeuvre.Group.FULL, (float) fullScale);
            }

            // sorting the variable group scaling
            float groupLengthPre = Util.findOccurrences("`", figureMatcher.group(2)),
               groupLengthPost = Util.findOccurrences("`", figureMatcher.group(4));

            // scaling is an expensive operation, so avoid it if possible
            if (groupLengthPre > 0f) {
               manoeuvre.scaleGroup(Manoeuvre.Group.PRE, 1f / (groupLengthPre + 1f));
            }

            if (groupLengthPost > 0f) {
               manoeuvre.scaleGroup(Manoeuvre.Group.POST, 1f / (groupLengthPost + 1f));
            }

            // counting the pluses
            manoeuvre.addLength(Manoeuvre.Group.PRE, Util.findOccurrences("+", figureMatcher.group(1)));
            manoeuvre.addLength(Manoeuvre.Group.POST, Util.findOccurrences("+", figureMatcher.group(5)));

            manoeuvres.add(manoeuvre);

         } else {
            throw new InvalidFlightException("invalid olan");
         }
      }

      return correct ?
         correctLowestPoint(new Flight(manoeuvres.toArray(new Manoeuvre[manoeuvres.size()]))) :
         new Flight(manoeuvres.toArray(new Manoeuvre[manoeuvres.size()]));
   }


   /**
    * @param flight - flight to correct
    * @return - a flight corrected to avoid sinking below the ground
    * @throws InvalidFlightException - any olan problems encountered with extending the flight
    */
   public Flight correctLowestPoint(Flight flight) throws InvalidFlightException {
      Flight result = flight;

      float[] initialMatrix = new float[16];
      Matrix.setIdentityM(initialMatrix, 0);

      if (flight.getLowestPoint(initialMatrix) < 0) {
         Log.d(this.getClass().getName(), "added height correction");
         result = buildFlight(manoeuvreCatalogue.getCorrection().getOLAN()
               + " " + flight.getOLAN(), true);
      }

      return result;
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
                  flight = buildFlight(olan, false);
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
      String activeFlightName = app.getScene().getFlight().getName();

      // if we're not modifying the current flight, check there's no name clash
      if (!(activeFlightName == null ? "" : activeFlightName).equals(newName)) {
         for (Flight current : flights) {
            if (current.getName().equals(newName)) {
               throw new InvalidFlightException("name clash");
            }
         }
      }

      Flight flight = app.getScene().getFlight();
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
