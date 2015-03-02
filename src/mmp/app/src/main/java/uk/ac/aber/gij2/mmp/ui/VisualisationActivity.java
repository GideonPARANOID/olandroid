/**
 * @created 2015-01-26
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.mmp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;

import uk.ac.aber.gij2.mmp.MMPApplication;
import uk.ac.aber.gij2.mmp.R;


public class VisualisationActivity extends ActionBarActivity implements
   SeekBar.OnSeekBarChangeListener {


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
      getMenuInflater().inflate(R.menu.menu_visualisation, menu);
      return true;
   }


   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
         case R.id.menu_a_help:
            return true;

         case R.id.menu_a_settings:
            startActivity(new Intent(this, SettingsActivity.class));
            return true;

         case R.id.menu_va_play:
            boolean playing = item.getTitle().equals(getString(R.string.va_play));

            ((MMPApplication) getApplication()).animationPlayToggle(playing);
            item.setTitle(playing ? R.string.va_stop : R.string.va_play);
            item.setIcon(playing ? R.drawable.ic_action_stop : R.drawable.ic_action_play);
            return true;

         default:
            return super.onOptionsItemSelected(item);
      }
   }


   // seekbar
   @Override
   public void onProgressChanged(SeekBar seekBar, int progress, boolean human) {
      if (human) {
         ((MMPApplication) getApplication()).setAnimationProgress((float) progress / 100f);
      }
   }


   // seekbar
   @Override
   public void onStopTrackingTouch(SeekBar seekBar) {}


   // seekbar
   @Override
   public void onStartTrackingTouch(SeekBar seekBar) {}
}
