package uk.ac.aber.gij2.mmp.test;

import android.app.Instrumentation.ActivityMonitor;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.ViewAsserts;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;

import uk.ac.aber.gij2.mmp.R;
import uk.ac.aber.gij2.mmp.activities.BuildFlightActivity;
import uk.ac.aber.gij2.mmp.activities.MainActivity;


public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

   private MainActivity mainActivity;


   public MainActivityTest() {
      super(MainActivity.class);
   }


   @Override
   protected void setUp() throws Exception {
      super.setUp();

      setActivityInitialTouchMode(false);
      mainActivity = getActivity();
   }

   public void testStartBuildFlightActivity() throws Exception {

      ActivityMonitor monitor = getInstrumentation().addMonitor(
         BuildFlightActivity.class.getName(), null, false);

      // find a button & press it
      Button buildFlightButton = (Button) mainActivity.findViewById(R.id.ma_button_build);

      ViewAsserts.assertOnScreen(mainActivity.getWindow().getDecorView(), buildFlightButton);

      TouchUtils.clickView(this, buildFlightButton);

      // waiting for the new activity to show up
      BuildFlightActivity buildFlightActivity = (BuildFlightActivity)
         monitor.waitForActivityWithTimeout(1000);
      assertNotNull(buildFlightActivity);

      // checking we've really loaded properly - expected elements are there
      EditText olanInput = (EditText) buildFlightActivity.findViewById(R.id.bfa_edittext_olan);

      ViewAsserts.assertOnScreen(buildFlightActivity.getWindow().getDecorView(), olanInput);

      assertEquals("text incorrect", "", olanInput.getText().toString());

      // go back to the mainactivity
      this.sendKeys(KeyEvent.KEYCODE_BACK);

       ViewAsserts.assertOnScreen(mainActivity.getWindow().getDecorView(), buildFlightButton);
   }
}