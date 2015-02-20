/**
 * @created 2015-01-26
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.mmp.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;

import uk.ac.aber.gij2.mmp.MMPApplication;
import uk.ac.aber.gij2.mmp.R;


public class VisualisationActivity extends ActionBarActivity implements SeekBar.OnSeekBarChangeListener {

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_visualisation);

      SeekBar animationSeek = ((SeekBar) findViewById(R.id.va_seek));

      // resetting animation & seek
      ((MMPApplication) getApplication()).setAnimationProgress(1f);
      animationSeek.setProgress(100);

      animationSeek.setOnSeekBarChangeListener(this);
   }


   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
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


   public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
      ((MMPApplication) getApplication()).setAnimationProgress((float) progress / 100f);
   }


   public void onStopTrackingTouch(SeekBar seekBar) {
   }


   public void onStartTrackingTouch(SeekBar seekBar) {
   }
}
