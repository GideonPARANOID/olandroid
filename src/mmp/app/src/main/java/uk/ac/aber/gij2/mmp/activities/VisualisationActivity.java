/**
 * @created 2015-01-26
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.mmp.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import uk.ac.aber.gij2.mmp.R;


public class VisualisationActivity extends ActionBarActivity {


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_visualisation);
   }


   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.menu_open_gl, menu);
      return true;
   }


   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      // which option menu item got selected
      switch (item.getItemId()) {
         case R.id.action_settings:
            return true;

         default:
            return super.onOptionsItemSelected(item);
      }
   }
}
