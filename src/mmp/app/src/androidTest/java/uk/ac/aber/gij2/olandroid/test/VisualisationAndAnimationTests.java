/**
 * @created 2015-05-02
 * @author gideon mw jones
 */

package uk.ac.aber.gij2.olandroid.test;

import android.app.Activity;
import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.ViewAsserts;
import android.view.View;
import android.widget.SeekBar;

import uk.ac.aber.gij2.olandroid.R;
import uk.ac.aber.gij2.olandroid.controller.AnimationManager;
import uk.ac.aber.gij2.olandroid.controller.FlightManager;
import uk.ac.aber.gij2.olandroid.controller.OLANdroid;
import uk.ac.aber.gij2.olandroid.view.VisualisationActivity;


public class VisualisationAndAnimationTests extends ActivityInstrumentationTestCase2<VisualisationActivity> {

   private Activity va;
   private Instrumentation.ActivityMonitor vaMonitor;
   private AnimationManager am;

   public VisualisationAndAnimationTests() {
      super(VisualisationActivity.class);
   }


   @Override
   protected void setUp() throws Exception {
      super.setUp();
      setActivityInitialTouchMode(false);
      va = getActivity();

      vaMonitor = getInstrumentation().addMonitor(
         VisualisationActivity.class.getName(), null, false);

      am = AnimationManager.getInstance();

      // setup a demo flight to work with
      ((OLANdroid) va.getApplication()).setFlight(
         FlightManager.getInstance().buildFlight("d", false));
   }


   /**
    * tests the ui is setup properly
    */
   public void testBuildUI() {
      View view = va.getWindow().getDecorView();

      ViewAsserts.assertOnScreen(view, va.findViewById(R.id.va_seek));

      ViewAsserts.assertOnScreen(view, va.findViewById(R.id.menu_va_play));
      ViewAsserts.assertOnScreen(view, va.findViewById(R.id.menu_va_save));
      ViewAsserts.assertOnScreen(view, va.findViewById(R.id.menu_va_edit));
   }


   /**
    * tests moving the seek bar sets the animation progress
    */
   public void testAnimationSeekBar() {

      // defaults to 1f
      assertEquals(1f, am.getProgress());

      // triggers a click right in the centre of the view, can use it to set progress to 0.5f
      TouchUtils.clickView(this, va.findViewById(R.id.va_seek));

      assertEquals(0.5f, am.getProgress());
   }


   /**
    * tests if the seekbar reflects animation progression
    */
   public void testSetAnimation() {

      am.setProgress(1f);
      assertEquals(100, ((SeekBar) va.findViewById(R.id.va_seek)).getProgress());

      am.setProgress(0f);
      assertEquals(0, ((SeekBar) va.findViewById(R.id.va_seek)).getProgress());
   }


   /**
    * tests the playing of the animation progression via the button
    */
   public void testPlayAnimation() {
      am.setProgress(1f);

      assertEquals(1f, am.getProgress());
      TouchUtils.clickView(this, va.findViewById(R.id.menu_va_play));

      // cannot test a specific value, so just check that the animation progression is between 0 & 1
      assertTrue(am.getProgress() > 0f);
      assertTrue(am.getProgress() < 1f);

      vaMonitor.waitForActivityWithTimeout(5000);

      // should be done by now
      assertEquals(1f, am.getProgress());
   }


   /**
    * tests the pausing of the animation progression via the button
    */
   public void testPauseAnimation() {
      am.setProgress(1f);

      assertEquals(1f, am.getProgress());
      TouchUtils.clickView(this, va.findViewById(R.id.menu_va_play));
      TouchUtils.clickView(this, va.findViewById(R.id.menu_va_play));

      // cannot test a specific value, so just check that the animation progression is between 0 & 1
      assertTrue(am.getProgress() > 0f);
      assertTrue(am.getProgress() < 1f);

      float progress = am.getProgress();

      vaMonitor.waitForActivityWithTimeout(5000);

      // animation should not have moved
      assertEquals(progress, am.getProgress());
   }
}
