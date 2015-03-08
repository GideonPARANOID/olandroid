/**
 * @created 2015-03-02
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.mmp.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import uk.ac.aber.gij2.mmp.R;


public class SettingsActivity extends ActionBarActivity {

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_settings);

      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setDisplayShowHomeEnabled(true);
   }


   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
         case android.R.id.home:

            // overrides parent (there is none) to send to previous activity
            finish();
            break;
      }

      return super.onOptionsItemSelected(item);
   }
}
