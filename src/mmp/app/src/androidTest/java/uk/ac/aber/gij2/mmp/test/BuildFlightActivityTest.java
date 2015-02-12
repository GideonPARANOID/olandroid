/**
 * @created 2015-02-12
 * @author gideon mw jones
 */

package uk.ac.aber.gij2.mmp.test;

import android.app.Activity;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.Button;

import uk.ac.aber.gij2.mmp.R;
import uk.ac.aber.gij2.mmp.activities.BuildFlightActivity;


public class BuildFlightActivityTest extends ActivityUnitTestCase<BuildFlightActivity> {

   private Activity buildFlightActvity;
   private Intent intent;


   public BuildFlightActivityTest() {
      super(BuildFlightActivity.class);
   }


   @Override
   protected void setUp() throws Exception {
      super.setUp();

      intent = new Intent(getInstrumentation().getTargetContext(), BuildFlightActivity.class);
      startActivity(intent, null, null);
   }


   @MediumTest
   public void testValidOLAN() {

      final Button buttonVisualisation = (Button) getActivity().findViewById(R.id.bfa_button_vis);
      buttonVisualisation.performClick();

      assertNotNull("Intent was null", getStartedActivityIntent());


   }



}
