package uk.ac.aber.gij2.olandroid.view;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

import uk.ac.aber.gij2.olandroid.R;

public class AboutActivity extends ActionBarActivity {

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_about);

      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setDisplayShowHomeEnabled(true);

      // activating links
      ((TextView) findViewById(R.id.aa_text_content)).setMovementMethod(
         LinkMovementMethod.getInstance());
   }


   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
         case android.R.id.home:

            // overrides parent (there is none) to send to previous activity
            finish();
            break;
      }

      return super.onOptionsItemSelected(item);
   }
}
