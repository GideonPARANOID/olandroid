package uk.ac.aber.gij2.mmp.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import uk.ac.aber.gij2.mmp.R;

public class MainActivity extends ActionBarActivity {

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
   }


   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.menu_main, menu);
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
      Intent intent = new Intent(this, OpenGLActivity.class);
      startActivity(intent);
   }
}
