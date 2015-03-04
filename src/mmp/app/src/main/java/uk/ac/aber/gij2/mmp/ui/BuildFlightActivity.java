/**
 * @created 2015-02-05
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.mmp.ui;

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

import uk.ac.aber.gij2.mmp.MMPApplication;
import uk.ac.aber.gij2.mmp.ManoeuvreCatalogue;
import uk.ac.aber.gij2.mmp.R;


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
            ((MMPApplication) getApplication()).getManoeuvreCatalogue().getCategories()));

      // on changing the spinner
      spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItem,
               int position, long id) {

               final String category = ((MMPApplication)getApplication()).getManoeuvreCatalogue()
                  .getCategories()[position];

               final ListView listView = (ListView) findViewById(R.id.bfa_manoeuvre_list);


               // setup the listview on changing the category
               listView.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                     R.layout.list_olan, ((MMPApplication) getApplication()).getManoeuvreCatalogue()
                     .getOLANs(category)) {

                     @Override
                     public View getView(int position, View convertView, ViewGroup parent) {

                        MMPApplication context = ((MMPApplication) getApplication());

                        View row = ((LayoutInflater) context.getSystemService(
                           Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_olan, parent,
                           false);

                        String[] olans = context.getManoeuvreCatalogue().getOLANs(category);

                        ((TextView) row.findViewById(R.id.ol_text_olan)).setText(olans[position]);
                        ((TextView) row.findViewById(R.id.ol_text_name)).setText(
                           context.getManoeuvreCatalogue().get(olans[position]).getName());

                        return row;
                     }
               });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
      });

      ((ListView) findViewById(R.id.bfa_manoeuvre_list)).setOnItemClickListener(this);
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
         case R.id.menu_a_help:
            new AlertDialog.Builder(this).setTitle(R.string.app_help).setMessage(
               R.string.bfa_help_message).create().show();
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
      ManoeuvreCatalogue catalogue = ((MMPApplication) getApplication()).getManoeuvreCatalogue();

      // finding the category, then finding the manoeuvre in that category, then the olan
      olanEntry.append(catalogue.get(catalogue.getOLANs((String) ((Spinner) findViewById(
         R.id.bfa_spinner_category)).getSelectedItem())[position]).getOLAN() + " ");
   }


   /**
    * listener on the build flight button
    * @param view - view element source
    */
   public void button_vis(View view) {

      try {
         ((MMPApplication) getApplication()).buildAndSetFlight(olanEntry.getText().toString());
         startActivity(new Intent(this, VisualisationActivity.class));

      } catch (Exception exception) {
         Toast.makeText(getApplication(), R.string.bfa_toast_invalid, Toast.LENGTH_SHORT).show();
      }
   }
}
