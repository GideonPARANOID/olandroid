/**
 * @created 2015-01-29
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.mmp.test;

import android.app.Activity;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.MediumTest;

import uk.ac.aber.gij2.mmp.activities.MainActivity;


public class ActivityTest extends ActivityUnitTestCase<MainActivity> {

   private Activity mainActvity;

   public ActivityTest() {
      super(MainActivity.class);
   }



   @Override
   protected void setUp() throws Exception {
      super.setUp();


      Intent intent = new Intent(getInstrumentation().getTargetContext(), MainActivity.class);
      startActivity(intent, null, null);
   }


   @MediumTest
   public void testMainActivityLaunched() {

   }
}