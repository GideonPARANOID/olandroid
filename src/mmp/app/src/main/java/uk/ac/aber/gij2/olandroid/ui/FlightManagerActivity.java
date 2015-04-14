/**
 * @created 2015-03-07
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.olandroid.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import uk.ac.aber.gij2.olandroid.OLANdroid;
import uk.ac.aber.gij2.olandroid.R;
import uk.ac.aber.gij2.olandroid.visualisation.Flight;


public class FlightManagerActivity extends ActionBarActivity implements
   AdapterView.OnItemClickListener, DialogInterface.OnDismissListener {

   private OLANdroid app;
   private ArrayAdapter adapter;


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_flight_manager);

      app = (OLANdroid) getApplication();

      // if this is the first launch, show the help
      if (app.getIsFirstLaunch()) {
         new AlertDialog.Builder(this).setView(getLayoutInflater().inflate(R.layout.dialog_help,
            null)).setTitle(R.string.a_first).setMessage(R.string.a_first_message).create().show();
      }

      // adapter for converting flights into a listview
      adapter = new ArrayAdapter<Flight>(app, R.layout.list_flights,
         app.getFlightManager().getFlights()) {

         @Override
         public View getView(int position, View convertView, ViewGroup parent) {

            View row = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
               .inflate(R.layout.list_flights, parent, false);

            ((TextView) row.findViewById(R.id.lf_text_name)).setText(getItem(position).getName());
            ((TextView) row.findViewById(R.id.lf_text_olan)).setText(getItem(position).getOLAN());

            return row;
         }
      };

      ListView listFlights = (ListView) findViewById(R.id.fma_list_flights);

      // setup the listeners
      listFlights.setAdapter(adapter);
      listFlights.setOnItemClickListener(this);
      registerForContextMenu(listFlights);
   }


   @Override
   protected void onStart() {
      super.onStart();

      // refreshing the list to reflect any changes
      adapter.notifyDataSetChanged();
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
            app.setFlight(null);
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

      app.setFlight(app.getFlightManager().getFlights().get(position));
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

      // setting the flight the context menu is for to be the current flight
      app.setFlight((Flight) adapter.getItem(
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
            app.getFlightManager().deleteFlight(app.getFlight());
            app.setFlight(null);
            break;
      }

      // updating the list
      adapter.notifyDataSetChanged();
      return super.onContextItemSelected(item);
   }


   @Override
   public void onDismiss(DialogInterface dialog) {
      // updating things once a dialog has been dismissed
      adapter.notifyDataSetChanged();
   }
}
