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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import uk.ac.aber.gij2.mmp.Application;
import uk.ac.aber.gij2.mmp.ManoeuvreCatalogue;
import uk.ac.aber.gij2.mmp.ManoeuvreCatalogueArrayAdapter;
import uk.ac.aber.gij2.mmp.R;


public class BuildFlightActivity extends ActionBarActivity {

   private EditText olanEntry;


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_build_flight);

      olanEntry = (EditText) findViewById(R.id.bfa_olan_string);

      ManoeuvreCatalogue manoeuvreCatalogue =
         ((Application) getApplication()).getManoeuvreCatalogue();

      final ManoeuvreCatalogueArrayAdapter adapter = new ManoeuvreCatalogueArrayAdapter(this,
         manoeuvreCatalogue);

      final ListView listView = (ListView) findViewById(R.id.bfa_manoeuvre_list);

      listView.setAdapter(adapter);
      listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

         @Override
         public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {


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

      String olan = olanEntry.getText().toString();

      if (((Application) getApplication()).buildFlightFromOLAN(olan)) {
         startActivity(intent);

      } else {
         Toast.makeText(getApplication(), "Invalid OLAN", Toast.LENGTH_SHORT).show();
      }
   }
}
