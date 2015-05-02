/**
 * @created 2015-05-02
 * @author gideon mw jones
 */

package uk.ac.aber.gij2.olandroid.test;

import uk.ac.aber.gij2.olandroid.R;
import uk.ac.aber.gij2.olandroid.view.VisualisationActivity;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.app.Activity;
import android.view.View;


public class VisualisationActivityTest extends ActivityInstrumentationTestCase2<VisualisationActivity> {

   private Activity va;

   public VisualisationActivityTest() {
      super(VisualisationActivity.class);
   }


   @Override
   protected void setUp() throws Exception {
      super.setUp();
      setActivityInitialTouchMode(false);
      va = getActivity();
   }


   /**
    * tests the ui is setup properly
    */
   public void testUI() {
      View view = va.getWindow().getDecorView();

      ViewAsserts.assertOnScreen(view, va.findViewById(R.id.va_seek));

      ViewAsserts.assertOnScreen(view, va.findViewById(R.id.menu_va_play));
      ViewAsserts.assertOnScreen(view, va.findViewById(R.id.menu_va_save));
      ViewAsserts.assertOnScreen(view, va.findViewById(R.id.menu_va_edit));
   }
}
