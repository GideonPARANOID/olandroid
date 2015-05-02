/**
 * @created 2015-02-12
 * @author gideon mw jones
 */

package uk.ac.aber.gij2.olandroid.test;

import android.app.Activity;
import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.ViewAsserts;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import uk.ac.aber.gij2.olandroid.R;
import uk.ac.aber.gij2.olandroid.view.BuildFlightActivity;
import uk.ac.aber.gij2.olandroid.view.VisualisationActivity;


public class BuildFlightActivityTest extends ActivityInstrumentationTestCase2<BuildFlightActivity> {

   private final String[] validOLAN = new String[]{
      "d",
      "d d",
      " d d",
      "d+",
      "d++",
      "+d",
      "+++++d",
      "+d+",
      "+++d++",
      "%2 d",
      "%1 d",
      "`d",
      "d`",
      "d````",
      "``d",
      "```d``",
      "+++``d``+"
   }, invalidOLAN = new String[]{
      "",
      "         ",
      "dflefken",
      "dd",
      "ddddd",
      "f",
      "d+d",
      "+d+d",
      "`d`d"
   };
   private Activity bfa;
   private Instrumentation.ActivityMonitor vaMonitor;
   private Button visualisationButton;
   private EditText olanEntry;


   public BuildFlightActivityTest() {
      super(BuildFlightActivity.class);
   }


   @Override
   protected void setUp() throws Exception {
      super.setUp();
      setActivityInitialTouchMode(false);
      bfa = getActivity();

      // find a button & press it
      visualisationButton = (Button) bfa.findViewById(R.id.bfa_button_vis);
      olanEntry = (EditText) bfa.findViewById(R.id.bfa_edittext_olan);

      vaMonitor = getInstrumentation().addMonitor(
         VisualisationActivity.class.getName(), null, false);
   }


   /**
    * testing everything is there in the ui
    */
   public void testUI() {

      View view = bfa.getWindow().getDecorView();

      ViewAsserts.assertOnScreen(view, bfa.findViewById(R.id.bfa_edittext_olan));
      ViewAsserts.assertOnScreen(view, bfa.findViewById(R.id.bfa_button_vis));
      ViewAsserts.assertOnScreen(view, bfa.findViewById(R.id.bfa_spinner_category));
      ViewAsserts.assertOnScreen(view, bfa.findViewById(R.id.bfa_list_manoeuvres));

      // actionbar
      ViewAsserts.assertOnScreen(view, bfa.findViewById(R.id.menu_a_help));
      ViewAsserts.assertOnScreen(view, bfa.findViewById(R.id.menu_a_settings));
   }



   /**
    * tests that the plus button appends a plus
    */
   public void testPlusButton() {

      bfa.runOnUiThread(new Runnable() {
         public void run() {
            olanEntry.setText("");
         }
      });

      TouchUtils.clickView(this, bfa.findViewById(R.id.bfa_button_plus));

      assertTrue(olanEntry.getText().toString().contains("+"));
   }


   /**
    * tests that the backtick button appends a backtick
    */
   public void testBacktickButton() {

      bfa.runOnUiThread(new Runnable() {
         public void run() {
            olanEntry.setText("");
         }
      });

      TouchUtils.clickView(this, bfa.findViewById(R.id.bfa_button_backtick));

      assertTrue(olanEntry.getText().toString().contains("`"));
   }


   /**
    * exercises the build flight function to test the validity of olan strings
    */
   public void testValidOLAN() {

      for (final String olan : validOLAN) {

         // test we're in the right place first
         ViewAsserts.assertOnScreen(bfa.getWindow().getDecorView(), visualisationButton);

         bfa.runOnUiThread(new Runnable() {
            public void run() {
               olanEntry.setText(olan);
            }
         });

         TouchUtils.clickView(this, visualisationButton);
         assertNotNull(vaMonitor.waitForActivityWithTimeout(1000));

         // go back to the build flight activity
         sendKeys(KeyEvent.KEYCODE_BACK);
      }
   }


   /**
    * exercises the build flight function to test the invalidity of olan strings
    */
   public void testInvalidOLAN() {

      for (final String olan : invalidOLAN) {

         // test we're in the right place first
         ViewAsserts.assertOnScreen(bfa.getWindow().getDecorView(), visualisationButton);

         bfa.runOnUiThread(new Runnable() {
            public void run() {
               olanEntry.setText(olan);
            }
         });

         assertNull(vaMonitor.waitForActivityWithTimeout(1000));
      }
   }
}
