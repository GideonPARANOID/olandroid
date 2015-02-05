 /**
 * @created 2015-02-05
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.mmp.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import uk.ac.aber.gij2.mmp.R;


public class BuildFlightActivity extends ActionBarActivity {
   public final static String OLAN_MESSAGE = "uk.ac.aber.gij2.mmp.activities.OLAN_MESSAGE";


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_build_flight);
   }


   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.menu_build_flight, menu);
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


   public void button_vis(View view) {
      Intent intent = new Intent(this, VisualisationActivity.class);

      EditText editText = (EditText) findViewById(R.id.bfa_olan_string);
      String message = editText.getText().toString();
      intent.putExtra(OLAN_MESSAGE, message);

      startActivity(intent);
   }
}
