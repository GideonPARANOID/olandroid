/**
 * @created 2015-01-29
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.mmp.test;

import android.app.Activity;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.Button;

import uk.ac.aber.gij2.mmp.R;
import uk.ac.aber.gij2.mmp.activities.MainActivity;


public class MainActivityTest extends ActivityUnitTestCase<MainActivity> {

   private Activity mainActvity;
   private Intent intent;


   public MainActivityTest() {
      super(MainActivity.class);
   }


   @Override
   protected void setUp() throws Exception {
      super.setUp();

      intent = new Intent(getInstrumentation().getTargetContext(), MainActivity.class);
      startActivity(intent, null, null);

   }


   @MediumTest
   public void testBuildActivityLaunched() {

      final Button buttonBuildFlight = (Button) getActivity().findViewById(R.id.ma_button_build);
      buttonBuildFlight.performClick();

      assertNotNull("Intent was null", getStartedActivityIntent());
   }
}