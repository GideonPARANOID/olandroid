package uk.ac.aber.gij2.mmp.test;

import android.app.Instrumentation.ActivityMonitor;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.ViewAsserts;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.ListView;

import uk.ac.aber.gij2.mmp.R;

import uk.ac.aber.gij2.mmp.ui.FlightManagerActivity;
import uk.ac.aber.gij2.mmp.ui.MainActivity;


public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
   private MainActivity ma;


   public MainActivityTest() {
      super(MainActivity.class);
   }


   @Override
   protected void setUp() throws Exception {
      super.setUp();

      setActivityInitialTouchMode(false);
      ma = getActivity();
   }

   public void testStartBuildFlightActivity() throws Exception {

      ActivityMonitor monitor = getInstrumentation().addMonitor(
         FlightManagerActivity.class.getName(), null, false);

      // find a button & press it
      Button FlightManagerButton = (Button) ma.findViewById(R.id.ma_button_flight_manager);

      ViewAsserts.assertOnScreen(ma.getWindow().getDecorView(), FlightManagerButton);

      TouchUtils.clickView(this, FlightManagerButton);

      // waiting for the new activity to show up
      FlightManagerActivity fma = (FlightManagerActivity) monitor.waitForActivityWithTimeout(1000);
      assertNotNull(fma);

      // checking we've really loaded properly - expected elements are there
      ListView flightList = (ListView) fma.findViewById(R.id.fma_list_flights);

      ViewAsserts.assertOnScreen(fma.getWindow().getDecorView(), flightList);

      // go back to the mainactivity
      this.sendKeys(KeyEvent.KEYCODE_BACK);

      ViewAsserts.assertOnScreen(ma.getWindow().getDecorView(), FlightManagerButton);
   }
}