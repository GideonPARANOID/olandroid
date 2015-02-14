/**
 * @created 2015-02-12
 * @author gideon mw jones
 */

package uk.ac.aber.gij2.mmp.test;

import android.app.Activity;
import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;

import uk.ac.aber.gij2.mmp.R;
import uk.ac.aber.gij2.mmp.activities.BuildFlightActivity;
import uk.ac.aber.gij2.mmp.activities.VisualisationActivity;


public class BuildFlightActivityTest extends ActivityInstrumentationTestCase2<BuildFlightActivity> {

   private Activity buildFlightActvity;
   private Instrumentation.ActivityMonitor visualisationMonitor;

   public BuildFlightActivityTest() {
      super(BuildFlightActivity.class);
   }


   @Override
   protected void setUp() throws Exception {
      super.setUp();

      buildFlightActvity = getActivity();
   }


   public void testValidOLAN() {

   }
}
