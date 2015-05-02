/**
 * @created 2015-02-04
 * @author gideon mw jones
 */

package uk.ac.aber.gij2.olandroid.controller;

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

import uk.ac.aber.gij2.olandroid.Util;
import uk.ac.aber.gij2.olandroid.model.Flight;
import uk.ac.aber.gij2.olandroid.model.Manoeuvre;


public class FlightManager {

   private static FlightManager instance;

   private final String FILENAME = "flights.txt", figureRegex =
      "([\\+,-]*)([`]*)(\\w*)([`]*)([\\+,-]*)", scaleRegex = "(\\d)%";

   private ManoeuvreCatalogue manoeuvreCatalogue;
   private OLANdroid app;

   private File file;

   private ArrayList<Flight> flights;


   /**
    * @return - instance of this class, singleton access
    */
   public static FlightManager getInstance() {
      if (instance == null) {
         instance = new FlightManager();
      }

      return instance;
   }


   private FlightManager() {}


   public void initialise(OLANdroid app) {
      file = new File(app.getFilesDir(), FILENAME);

      this.app = app;
      this.manoeuvreCatalogue = ManoeuvreCatalogue.getInstance();

      // only ever initialise once, otherwise arrayadapters will lose reference
      flights = new ArrayList<>();

      loadFlights();
   }


   /**
    * @param olan - a description of a flight
    * @param correct - whether or not to correct the height of the flight
    * @return - a flight defined by the olan string
    */
   public Flight buildFlight(String olan, boolean correct) {
      Flight flight = null;

      Pattern figurePattern = Pattern.compile(figureRegex),
         scalePattern = Pattern.compile(scaleRegex);


      if (olan != null) {

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

                flight = correct ?
                  correctLowestPoint(new Flight(manoeuvres.toArray(new Manoeuvre[manoeuvres.size()]))) :
                  new Flight(manoeuvres.toArray(new Manoeuvre[manoeuvres.size()]));

            }
         }
      }

      return flight;
   }


   /**
    * @param flight - flight to correct
    * @return - a flight corrected to avoid sinking below the ground
    */
   public Flight correctLowestPoint(Flight flight) {
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

      if (file.exists()) {
         try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                  new FileInputStream(file)));

            // file is formatted such that flight name & olan are on alternate lines
            String name = reader.readLine(), olan = reader.readLine();
            while (name != null && olan != null) {

               Flight flight = buildFlight(olan, false);

               if (flight != null) {
                  flight.setName(name);

                  addFlight(flight, false);
                  Log.d(this.getClass().getName(), "file r: " + name + " - " + olan);

               } else {
                  Log.d(this.getClass().getName(), "invalid flight");
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
    */
   public boolean saveCurrentFlight(String newName) {
      String activeFlightName = app.getFlight().getName();

      // if we're not modifying the current flight, check there's no name clash
      if (!(activeFlightName == null ? "" : activeFlightName).equals(newName)) {
         for (Flight current : flights) {
            if (current.getName().equals(newName)) {
               return false;
            }
         }
      }

      Flight flight = app.getFlight();
      flight.setName(newName);

      addFlight(flight, true);

      saveFlights();
      loadFlights();

      return true;
   }


   /**
    * saves the flights to the file
    */
   public void saveFlights() {

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
   public  boolean addFlight(Flight flight, boolean overwrite) {
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


   /**
    * @return - a list of flights
    */
   public ArrayList<Flight> getFlights() {
      // had to return an arraylist because it needs to keep the same memory address for listeners
      return flights;
   }


   /**
    * @param flight - the flight to remove
    */
   public void deleteFlight(Flight flight) {

      flights.remove(flight);

      // in case the first one isn't valid
      for (Flight current : flights) {
         if (current.getName().equals(flight.getName())) {
            flights.remove(current);
         }
      }

      Log.d(this.getClass().getName(), "flight d: " + flight.getName() + " - "
         + flight.getOLAN());

      saveFlights();
   }


   /**
    * empties the flights
    */
   public void clearFlights() {
      flights.clear();
   }
}
