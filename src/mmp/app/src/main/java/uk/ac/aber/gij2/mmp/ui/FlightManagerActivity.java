/**
 * @created 2015-03-07
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.mmp.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
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
   AdapterView.OnItemClickListener {

   private ListView listFlights;


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_flight_manager);

      listFlights = (ListView) findViewById(R.id.fma_flight_list);

      refreshFlightsList();

      listFlights.setOnItemClickListener(this);
      registerForContextMenu(listFlights);
   }


   @Override
   protected void onStart() {
      super.onStart();

      // refreshing the list to reflect any changes
      ((ArrayAdapter) listFlights.getAdapter()).notifyDataSetChanged();
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
            ((MMPApplication) getApplication()).getScene().setFlight(null);
            startActivity(new Intent(this, BuildFlightActivity.class));
            break;

         case R.id.menu_a_help:
            new AlertDialog.Builder(this).setView(getLayoutInflater().inflate(R.layout.dialog_help,
                  null)).setTitle(R.string.a_help).setMessage(R.string.fma_help).create().show();
            break;

         case R.id.menu_a_settings:
            startActivity(new Intent(this, SettingsActivity.class));
            break;
      }

      return super.onOptionsItemSelected(item);
   }


   @Override
   public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {

      MMPApplication app = (MMPApplication) getApplication();
      app.getScene().setFlight(app.getFlightManager().getFlights()[position]);

      startActivity(new Intent(this, VisualisationActivity.class));
   }


   @Override
   public void onCreateContextMenu(ContextMenu menu, View view,
      ContextMenu.ContextMenuInfo menuInfo) {

      super.onCreateContextMenu(menu, view, menuInfo);
      getMenuInflater().inflate(R.menu.menu_flight_manager_context, menu);
   }


   @Override
   public boolean onContextItemSelected(MenuItem item) {

      MMPApplication app = (MMPApplication) getApplication();

      // setting the flight the context menu is for to be the current flight
      app.getScene().setFlight((Flight) listFlights.getAdapter().getItem(
            ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position));

      switch (item.getItemId()){
         case R.id.menu_fma_c_load:
            startActivity(new Intent(this, VisualisationActivity.class));
            break;

         case R.id.menu_fma_c_edit:
            startActivity(new Intent(this, BuildFlightActivity.class));
            break;

         case R.id.menu_fma_c_rename:
            new FlightTitleDialogFragment().show(getFragmentManager(), "flight_title");
            break;

         case R.id.menu_fma_c_delete:
            app.getFlightManager().deleteFlight(app.getScene().getFlight());
            app.getScene().setFlight(null);
            refreshFlightsList();
            break;
      }

      // updating the list
      listFlights.invalidateViews();
      ((ArrayAdapter) listFlights.getAdapter()).notifyDataSetChanged();
      return super.onContextItemSelected(item);
   }



   // TODO: fix notifyingdatasetchanged so this method isn't needed
   public void refreshFlightsList() {
            // setup the listview for the loaded flights
      listFlights.setAdapter(new ArrayAdapter<Flight>(getApplication(),
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
   }
}
