/**
 * @created 2015-02-05
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.mmp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import uk.ac.aber.gij2.mmp.MMPApplication;
import uk.ac.aber.gij2.mmp.R;


public class BuildFlightActivity extends ActionBarActivity implements
   AdapterView.OnItemClickListener {

   private EditText olanEntry;


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_build_flight);

      olanEntry = (EditText) findViewById(R.id.bfa_edittext_olan);

      final ListView listView = (ListView) findViewById(R.id.bfa_manoeuvre_list);

      listView.setAdapter(((MMPApplication) getApplication()).getManoeuvreCatalogue());
      listView.setOnItemClickListener(this);
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


   @Override
   public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {

      // appending olan id to the end of the olan entry string& moving the cursor to the end
      olanEntry.append(((MMPApplication) getApplication()).getManoeuvreCatalogue().get(
         position).getOLAN() + " ");
   }


   /**
    * listener on the build flight button
    * @param view - view element source
    */
   public void button_vis(View view) {

      try {
         ((MMPApplication) getApplication()).buildFlight(olanEntry.getText().toString());
         startActivity(new Intent(this, VisualisationActivity.class));

      } catch (Exception exception) {
         Toast.makeText(getApplication(), R.string.bfa_toast_invalid, Toast.LENGTH_SHORT).show();
      }
   }
}
