/**
 * @created 2015-01-26
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.mmp.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import uk.ac.aber.gij2.mmp.R;
import uk.ac.aber.gij2.mmp.visualisation.SurfaceView;


public class OpenGLActivity extends ActionBarActivity {

   private SurfaceView surfaceView;


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      surfaceView = new SurfaceView(this);

//      ManoeuvreCatalogue manoeuvreCatalogue = new ManoeuvreCatalogue(this);

      setContentView(surfaceView);



//      ((Application) getApplicationContext()).sayHi();

   }


   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.menu_open_gl, menu);
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


   @Override
   protected void onResume() {
      super.onResume();
      surfaceView.onResume();
   }

   @Override
   protected void onPause() {
      super.onPause();
      surfaceView.onPause();
   }
}
