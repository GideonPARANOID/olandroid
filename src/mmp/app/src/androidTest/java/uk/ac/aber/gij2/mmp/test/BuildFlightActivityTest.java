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


   public void testValidOLAN() {

      ViewAsserts.assertOnScreen(buildFlightActvity.getWindow().getDecorView(), visualisationButton);
      TouchUtils.clickView(this, visualisationButton);

      // waiting for the new activity to show up
      assertNull("Invalid OLAN, shouldn't launch visualisation",
         visualisationMonitor.waitForActivityWithTimeout(1000));


      buildFlightActvity.runOnUiThread(new Runnable() {
         public void run() {
            olanString.setText("d");
         }
      });

      TouchUtils.clickView(this, visualisationButton);
      assertNotNull("Valid OLAN, should launch visualisation",
         visualisationMonitor.waitForActivityWithTimeout(1000));

      // go back to the build flight activity
      this.sendKeys(KeyEvent.KEYCODE_BACK);

      ViewAsserts.assertOnScreen(buildFlightActvity.getWindow().getDecorView(), visualisationButton);
   }
}
