/**
 * @created 2015-02-12
 * @author gideon mw jones
 */

package uk.ac.aber.gij2.mmp.test;

import android.app.Activity;
import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.ViewAsserts;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;

import uk.ac.aber.gij2.mmp.R;
import uk.ac.aber.gij2.mmp.activities.BuildFlightActivity;
import uk.ac.aber.gij2.mmp.activities.VisualisationActivity;


public class BuildFlightActivityTest extends ActivityInstrumentationTestCase2<BuildFlightActivity> {

   private Activity buildFlightActvity;
   private Instrumentation.ActivityMonitor visualisationMonitor;

   private Button visualisationButton;
   private EditText olanString;


   private final String[] validOLANStrings = new String[] {
      "d",
      "d d",
      " d d",
      "d+",
      "d++",
      "+d",
      "+++++d",
      "+d+",
      "+++d++"
   }, invalidOLANStrings = new String[] {
      "         ",
      "dflefken",
      "dd",
      "ddddd",
      "p",
      "d+d",
      "+d+d"
   };


   public BuildFlightActivityTest() {
      super(BuildFlightActivity.class);
   }


   @Override
   protected void setUp() throws Exception {
      super.setUp();
      setActivityInitialTouchMode(false);
      buildFlightActvity = getActivity();

      // find a button & press it
      visualisationButton = (Button) buildFlightActvity.findViewById(R.id.bfa_button_vis);
      olanString = (EditText) buildFlightActvity.findViewById(R.id.bfa_edittext_olan);

      visualisationMonitor = getInstrumentation().addMonitor(
         VisualisationActivity.class.getName(), null, false);
   }


   /**
    * exercises the build flight function to test the validity of olan strings
    */
   public void testValidOLAN() {

      for (final String validOLAN :validOLANStrings) {

         // test we're in the right place first
         ViewAsserts.assertOnScreen(buildFlightActvity.getWindow().getDecorView(), visualisationButton);

         buildFlightActvity.runOnUiThread(new Runnable() {
            public void run() {
               olanString.setText(validOLAN);
            }
         });

         TouchUtils.clickView(this, visualisationButton);
         assertNotNull("Valid OLAN, should launch visualisation for olan " + validOLAN,
            visualisationMonitor.waitForActivityWithTimeout(1000));

         // go back to the build flight activity
         this.sendKeys(KeyEvent.KEYCODE_BACK);
      }
   }


   /**
    * exercises the build flight function to test the invalidity of olan strings
    */
   public void testInvalidOLAN() {

      ViewAsserts.assertOnScreen(buildFlightActvity.getWindow().getDecorView(), visualisationButton);
      TouchUtils.clickView(this, visualisationButton);

      assertNull("Invalid OLAN, should not launch visualisation",
         visualisationMonitor.waitForActivityWithTimeout(1000));

      for (final String invalidOLAN : invalidOLANStrings) {

         // test we're in the right place first
         ViewAsserts.assertOnScreen(buildFlightActvity.getWindow().getDecorView(), visualisationButton);

         buildFlightActvity.runOnUiThread(new Runnable() {
            public void run() {
               olanString.setText(invalidOLAN);
            }
         });

         assertNull("Invalid OLAN, should not launch visualisation for olan " + invalidOLAN,
            visualisationMonitor.waitForActivityWithTimeout(1000));
      }
   }
}
