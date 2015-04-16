/**
 * @created 2015-03-09
 * @author gideon mw jones
 */

package uk.ac.aber.gij2.olandroid.test;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;

import uk.ac.aber.gij2.olandroid.R;
import uk.ac.aber.gij2.olandroid.view.FlightManagerActivity;


public class FlightManagerActivityTest extends ActivityInstrumentationTestCase2<FlightManagerActivity> {

   private Activity fma;

   public FlightManagerActivityTest() {
      super(FlightManagerActivity.class);
   }


   @Override
   protected void setUp() throws Exception {
      super.setUp();
      setActivityInitialTouchMode(false);
      fma = getActivity();
   }


   /**
    * testing that all the expected ui elements are there
    */
   public void testUI() {

      ViewAsserts.assertOnScreen(fma.getWindow().getDecorView(), fma.findViewById(
         R.id.fma_list_flights));

      // action bar
      ViewAsserts.assertOnScreen(fma.getWindow().getDecorView(), fma.findViewById(
         R.id.menu_fma_new));
      ViewAsserts.assertOnScreen(fma.getWindow().getDecorView(), fma.findViewById(
         R.id.menu_a_help));
      ViewAsserts.assertOnScreen(fma.getWindow().getDecorView(), fma.findViewById(
         R.id.menu_a_settings));
   }
}
