/**
 * @created 2015-03-09
 * @author gideon mw jones
 */

package uk.ac.aber.gij2.olandroid.test;

import android.app.Activity;
import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.ViewAsserts;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import uk.ac.aber.gij2.olandroid.R;
import uk.ac.aber.gij2.olandroid.controller.FlightManager;
import uk.ac.aber.gij2.olandroid.controller.ManoeuvreCatalogue;
import uk.ac.aber.gij2.olandroid.model.Flight;
import uk.ac.aber.gij2.olandroid.view.BuildFlightActivity;
import uk.ac.aber.gij2.olandroid.view.FlightManagerActivity;


public class FlightManagerTests extends ActivityInstrumentationTestCase2<FlightManagerActivity> {

   private final String[] validOLAN = new String[] {
      "d",
      "d d",
      " d d",
      "d+",
      "d++",
      "+d",
      "+++++d",
      "+d+",
      "+++d++",
      "%2 d",
      "%1 d",
      "`d",
      "d`",
      "d````",
      "``d",
      "```d``",
      "+++``d``+"
   }, invalidOLAN = new String[] {
      "",
      "         ",
      "dflefken",
      "dd",
      "ddddd",
      "f",
      "d+d",
      "+d+d",
      "`d`d"
   };
   private Activity fma;
   private FlightManager fm;


   public FlightManagerTests() {
      super(FlightManagerActivity.class);
   }


   @Override
   protected void setUp() throws Exception {
      super.setUp();
      setActivityInitialTouchMode(false);
      fma = getActivity();
      fm = FlightManager.getInstance();
   }


   /**
    * testing that all the expected ui elements are there
    */
   public void testBuildUI() {

      View view = fma.getWindow().getDecorView();

      ViewAsserts.assertOnScreen(view, fma.findViewById(R.id.fma_list_flights));

      // action bar
      ViewAsserts.assertOnScreen(view, fma.findViewById(R.id.menu_fma_new));
      ViewAsserts.assertOnScreen(view, fma.findViewById(R.id.menu_a_help));
      ViewAsserts.assertOnScreen(view, fma.findViewById(R.id.menu_a_settings));
   }


   /**
    * tests the flight list is populated correctly
    */
   public void testFlightList() {
      String name = "" + System.currentTimeMillis(), olan = "d";

      // setup the list
      Flight f = fm.buildFlight(olan, false);
      f.setName(name);
      fm.addFlight(f, false);

      // refreshing the activity
      fma.finish();
      fma.startActivity(fma.getIntent());

      View item = ((ListView) fma.findViewById(R.id.fma_list_flights)).getAdapter()
         .getView(0, null, null);

      assertEquals(name, ((TextView) item.findViewById(R.id.lf_text_name)).getText().toString());
      assertEquals(olan, ((TextView) item.findViewById(R.id.lf_text_olan)).getText().toString());
   }


   /**
    * tests that the new flight button takes us to the
    */
   public void testNewFlightButton() {

      Instrumentation.ActivityMonitor bfaMonitor = getInstrumentation().addMonitor(
         BuildFlightActivity.class.getName(), null, false);

      TouchUtils.clickView(this, fma.findViewById(R.id.menu_fma_new));

      assertNotNull(bfaMonitor.waitForActivityWithTimeout(1000));

      sendKeys(KeyEvent.KEYCODE_BACK);
   }


   /**
    * tests the addition of flights
    */
   public void testAddFlight() {

      fm.clearFlights();
      fm.saveFlights();

      assertEquals(0, fm.getFlights().size());

      Flight f = fm.buildFlight("id", false);
      f.setName(" " + System.currentTimeMillis());

      fm.addFlight(f, true);

      assertEquals(1, fm.getFlights().size());
   }


   /**
    * tests the removal of flights
    */
   public void testDeleteFlight() {
      fm.clearFlights();
      fm.saveFlights();

      Flight f = fm.buildFlight("id", false);
      f.setName(" " + System.currentTimeMillis());

      fm.addFlight(f, true);

      assertEquals(1, fm.getFlights().size());

      fm.deleteFlight(f);
      assertEquals(0, fm.getFlights().size());
   }


   /**
    * tests valid olan
    */
   public void testValidOLAN() {
      for (final String olan : validOLAN) {
         assertNotNull(fm.buildFlight(olan, false));
      }
   }


   /**
    * tests invalid olan
    */
   public void testInvalidOLAN() {
      for (final String olan : invalidOLAN) {
         assertNull(fm.buildFlight(olan, false));
      }
   }


   /**
    * tests flights are saved
    */
   public void testSaveAndLoad() {

      fm.clearFlights();
      Flight f = fm.buildFlight("id", false);
      fm.addFlight(f, false);

      assertEquals(1, fm.getFlights().size());

      fm.saveFlights();
      fm.clearFlights();

      assertEquals(0, fm.getFlights().size());

      fm.loadFlights();

      assertEquals(1, fm.getFlights().size());
   }


   /**
    * data dependent
    * tests that flights are corrected properly
    */
   public void testNewFlightCorrection() {
      assertEquals("id", fm.buildFlight("id", false).getOLAN());
      assertEquals(ManoeuvreCatalogue.getInstance().getCorrection().getOLAN()
         + " id", fm.buildFlight("id", true).getOLAN());
   }
}
