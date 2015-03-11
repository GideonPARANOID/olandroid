/**
 * @created 2015-02-05
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.olandroid.ui;

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

import uk.ac.aber.gij2.olandroid.InvalidFlightException;
import uk.ac.aber.gij2.olandroid.OLANdroid;
import uk.ac.aber.gij2.olandroid.ManoeuvreCatalogue;
import uk.ac.aber.gij2.olandroid.R;
import uk.ac.aber.gij2.olandroid.visualisation.Manoeuvre;


public class BuildFlightActivity extends ActionBarActivity implements
   AdapterView.OnItemClickListener {

   private EditText olanEntry;


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_build_flight);

      olanEntry = (EditText) findViewById(R.id.bfa_edittext_olan);

      final Spinner spinner = (Spinner) findViewById(R.id.bfa_spinner_category);

      spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
            ((OLANdroid) getApplication()).getManoeuvreCatalogue().getCategories()));

      // on changing the spinner
      spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItem,
               int position, long id) {

               final ListView listManoeuvres = (ListView) findViewById(R.id.bfa_list_manoeuvres);
               final ManoeuvreCatalogue manoeuvreCatalogue = ((OLANdroid) getApplication())
                  .getManoeuvreCatalogue();


               // setup the listview on changing the category
               listManoeuvres.setAdapter(new ArrayAdapter<Manoeuvre>(getApplicationContext(),
                  R.layout.list_manoeuvres, manoeuvreCatalogue.getManoeuvres(
                  manoeuvreCatalogue.getCategories()[position])) {

                  @Override
                  public View getView(int position, View convertView, ViewGroup parent) {

                     View row = ((LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_manoeuvres,
                        parent, false);

                     ((TextView) row.findViewById(R.id.lm_text_olan)).setText(
                        getItem(position).getOLAN());
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
      if (app.getScene().getFlight() != null && app.getScene().getFlight().getOLAN() != null) {
         olanEntry.setText(app.getScene().getFlight().getOLAN() + " ");
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
      olanEntry.append(((OLANdroid) getApplication()).getManoeuvreCatalogue().getManoeuvres(
         (String) ((Spinner) findViewById(R.id.bfa_spinner_category)).getSelectedItem())[position]
         .getOLAN() + " ");
   }


   /**
    * listener on the build flight button
    * @param view - view element source
    */
   public void button_vis(View view) {

      try {
         OLANdroid app = (OLANdroid) getApplication();

         app.buildAndSetFlight(olanEntry.getText().toString());
         startActivity(new Intent(this, VisualisationActivity.class));

      } catch (InvalidFlightException exception) {
         Toast.makeText(getApplication(), R.string.bfa_toast_invalid, Toast.LENGTH_SHORT).show();
      }
   }
}
