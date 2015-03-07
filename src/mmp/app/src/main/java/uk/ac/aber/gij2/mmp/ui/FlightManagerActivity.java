/**
 * @created 2015-03-07
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.mmp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import uk.ac.aber.gij2.mmp.MMPApplication;
import uk.ac.aber.gij2.mmp.R;
import uk.ac.aber.gij2.mmp.visualisation.Flight;


public class FlightManagerActivity extends ActionBarActivity implements
   AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_flight_manager);

      final ListView listView = (ListView) findViewById(R.id.fma_flight_list);

      // setup the listview for the loaded flights
      listView.setAdapter(new ArrayAdapter<Flight>(getApplicationContext(),
            R.layout.list_flights, ((MMPApplication) getApplication()).getFlightManager()
            .getFlights()) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

               View row = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                  .inflate(R.layout.list_flights, parent, false);

               ((TextView) row.findViewById(R.id.lf_text_name)).setText(getItem(position)
                  .getName());
               ((TextView) row.findViewById(R.id.lf_text_olan)).setText(getItem(position)
                  .getOLAN());

               return row;
            }
      });

      listView.setOnItemClickListener(this);
      listView.setOnItemLongClickListener(this);
   }


   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.menu_flight_manager, menu);
      return super.onCreateOptionsMenu(menu);
   }


   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
         case R.id.menu_fma_new:
            startActivity(new Intent(this, BuildFlightActivity.class));
            return true;

         case R.id.menu_a_help:
            new HelpDialogFragment(R.string.fma_help).show(getFragmentManager(), "help");
            return true;

         case R.id.menu_a_settings:
            startActivity(new Intent(this, SettingsActivity.class));
            return true;

         default:
            return super.onOptionsItemSelected(item);
      }
   }


   @Override
   public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
      ((MMPApplication) getApplication()).getScene().setFlight(((MMPApplication) getApplication())
         .getFlightManager().getFlights()[position]);

      startActivity(new Intent(this, VisualisationActivity.class));
   }


   @Override
   public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

      // TODO: implement delete
      return false;
   }
}
