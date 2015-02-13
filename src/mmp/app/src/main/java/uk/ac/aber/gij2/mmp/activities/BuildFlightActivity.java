/**
 * @created 2015-02-05
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.mmp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import uk.ac.aber.gij2.mmp.Application;
import uk.ac.aber.gij2.mmp.R;


public class BuildFlightActivity extends Activity {

   private EditText olanEntry;


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_build_flight);

      olanEntry = (EditText) findViewById(R.id.bfa_olan_string);

      final ListView listView = (ListView) findViewById(R.id.bfa_manoeuvre_list);

      listView.setAdapter(((Application) getApplication()).getManoeuvreCatalogue());
      listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

         @Override
         public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {

            // appending olan id to the end of the olan entry string& moving the cursor to the end
            olanEntry.append(((Application) getApplication()).getManoeuvreCatalogue().get(
                  position).getOLAN() + " ");
         }
      });
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

      if (((Application) getApplication()).buildFlight(olanEntry.getText().toString())) {
         startActivity(intent);

      } else {
         Toast.makeText(getApplication(), R.string.bfa_toast_invalid, Toast.LENGTH_SHORT).show();
      }
   }
}
