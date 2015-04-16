/**
 * @created 2015-02-05
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.olandroid.view;

import android.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import uk.ac.aber.gij2.olandroid.controller.FlightManager;
import uk.ac.aber.gij2.olandroid.InvalidFlightException;
import uk.ac.aber.gij2.olandroid.controller.OLANdroid;
import uk.ac.aber.gij2.olandroid.controller.ManoeuvreCatalogue;
import uk.ac.aber.gij2.olandroid.R;
import uk.ac.aber.gij2.olandroid.model.Flight;
import uk.ac.aber.gij2.olandroid.model.Manoeuvre;


public class BuildFlightActivity extends ActionBarActivity implements
   AdapterView.OnItemClickListener {

   private EditText olanEntry;


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_build_flight);

      olanEntry = (EditText) findViewById(R.id.bfa_edittext_olan);

      final Spinner spinner = (Spinner) findViewById(R.id.bfa_spinner_category);

      spinner.setAdapter(new ArrayAdapter<>(this, R.layout.list_category,
            ManoeuvreCatalogue.getInstance().getCategories()));

      // on changing the spinner
      spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItem,
               int position, long id) {

               final ListView listManoeuvres = (ListView) findViewById(R.id.bfa_list_manoeuvres);
               final ManoeuvreCatalogue manoeuvreCatalogue = ManoeuvreCatalogue.getInstance();

               // setup the listview on changing the category
               listManoeuvres.setAdapter(new ArrayAdapter<Manoeuvre>(getApplicationContext(),
                  R.layout.list_manoeuvre, manoeuvreCatalogue.getManoeuvres(
                  manoeuvreCatalogue.getCategories()[position])) {

                  @Override
                  public View getView(int position, View convertView, ViewGroup parent) {

                     View row = ((LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_manoeuvre,
                        parent, false);

                     ((TextView) row.findViewById(R.id.lm_text_olan)).setText(
                        getItem(position).getOLAN());
                      ((TextView) row.findViewById(R.id.lm_text_aresti)).setText(
                        getItem(position).getAresti());
                     ((TextView) row.findViewById(R.id.lm_text_name)).setText(
                        getItem(position).getName());

                     return row;
                  }
               });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
      });

      ((ListView) findViewById(R.id.bfa_list_manoeuvres)).setOnItemClickListener(this);
   }


   @Override
   protected void onStart() {
      super.onStart();

      OLANdroid app = (OLANdroid) getApplication();

      // set the default text for editing flights
      Flight flight = app.getFlight();

      if (flight != null && flight.getOLAN() != null) {
         olanEntry.setText(flight.getOLAN() + " ");
         olanEntry.setSelection(olanEntry.getText().length());
      }
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.menu_build_flight, menu);
      return super.onCreateOptionsMenu(menu);
   }


   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
         case R.id.menu_a_help:
            new AlertDialog.Builder(this).setView(getLayoutInflater().inflate(R.layout.dialog_help,
                  null)).setTitle(R.string.a_help).setMessage(R.string.bfa_help).create().show();
            break;

         case R.id.menu_a_settings:
            startActivity(new Intent(this, SettingsActivity.class));
            break;
      }

      return super.onOptionsItemSelected(item);
   }


   @Override
   public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {

      // finding the manoeuvre in the catalogue, getting its olan & adding to the current string
      String olan = ManoeuvreCatalogue.getInstance().getManoeuvres(
         (String) ((Spinner) findViewById(R.id.bfa_spinner_category)).getSelectedItem())[position]
            .getOLAN() + " ";

      int olanPosition = olanEntry.getSelectionStart();
      olanEntry.setText(olanEntry.getText().insert(olanPosition, olan));
      olanEntry.setSelection(olanPosition + olan.length());
   }


   /**
    * listener on the build flight button
    * @param view - view element source
    */
   public void button_vis(View view) {
      OLANdroid app = (OLANdroid) getApplication();

      try {
         app.setFlight(FlightManager.getInstance().buildFlight(olanEntry.getText().toString(),
               app.getAutocorrect()));

         // if the olan has changed in the building process, consider it corrected
         if (!app.getFlight().getOLAN().equals(olanEntry.getText().toString().trim())) {
            Toast.makeText(app, R.string.bfa_toast_corrected, Toast.LENGTH_SHORT).show();
         }

         startActivity(new Intent(this, VisualisationActivity.class));

      } catch (InvalidFlightException exception) {
         Toast.makeText(app, R.string.bfa_toast_invalid, Toast.LENGTH_SHORT).show();
      }
   }


   public void button_plus(View view) {
      int position = olanEntry.getSelectionStart();
      olanEntry.setText(olanEntry.getText().insert(position,
            getText(R.string.bfa_button_plus)));
      olanEntry.setSelection(position + 1);
   }

   public void button_backtick(View view) {
      int position = olanEntry.getSelectionStart();
      olanEntry.setText(olanEntry.getText().insert(position,
            getText(R.string.bfa_button_backtick)));
      olanEntry.setSelection(position + 1);
   }
}
