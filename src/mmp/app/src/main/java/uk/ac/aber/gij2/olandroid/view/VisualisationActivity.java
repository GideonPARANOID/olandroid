/**
 * @created 2015-01-26
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.olandroid.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;

import java.util.Observable;
import java.util.Observer;

import uk.ac.aber.gij2.olandroid.controller.AnimationManager;
import uk.ac.aber.gij2.olandroid.R;


public class VisualisationActivity extends ActionBarActivity implements Observer,
   SeekBar.OnSeekBarChangeListener {

   private AnimationManager animationManager;
   private MenuItem menuItemPlay;
   private SeekBar animationSeek;


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_visualisation);

      // animation listening
      animationManager = AnimationManager.getInstance();
      animationManager.addObserver(this);

      animationSeek = ((SeekBar) findViewById(R.id.va_seek));

      // resetting animation & seek
      animationManager.setProgress(1f);
      animationSeek.setProgress(100);

      animationSeek.setOnSeekBarChangeListener(this);
   }


   @Override
   protected void onStart() {
      super.onStart();
      ((SurfaceView) findViewById(R.id.va_visualisation)).onStart();
   }


   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.menu_visualisation, menu);

      menuItemPlay = menu.findItem(R.id.menu_va_play);

      if (menuItemPlay != null) {
         menuItemPlay.setIcon(R.drawable.ic_action_play).setTitle(R.string.va_play);
      }

      return super.onCreateOptionsMenu(menu);
   }


   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
         case R.id.menu_va_play:
            boolean playing = item.getTitle().equals(getString(R.string.va_play));

            animationManager.animationPlayToggle(playing);
            item.setTitle(playing ? R.string.va_stop : R.string.va_play).setIcon(
               playing ? R.drawable.ic_action_stop : R.drawable.ic_action_play);
            break;

         case R.id.menu_va_edit:
            startActivity(new Intent(this, BuildFlightActivity.class));
            break;

         case R.id.menu_va_save:
            new FlightTitleDialogFragment().show(getFragmentManager(), "flight_title");
            break;

         case R.id.menu_a_help:
            new AlertDialog.Builder(this).setView(getLayoutInflater().inflate(R.layout.dialog_help,
                  null)).setTitle(R.string.a_help).setMessage(R.string.va_help).create().show();
            break;

         case R.id.menu_a_settings:
            startActivity(new Intent(this, SettingsActivity.class));
            break;
      }

      return super.onOptionsItemSelected(item);
   }


   // seekbar
   @Override
   public void onProgressChanged(SeekBar seekBar, int progress, boolean human) {
      if (human) {
         animationManager.setProgress((float) progress / 100f);
      }
   }


   // seekbar
   @Override
   public void onStopTrackingTouch(SeekBar seekBar) {}


   // seekbar
   @Override
   public void onStartTrackingTouch(SeekBar seekBar) {
      animationManager.animationPlayToggle(false);

      if (menuItemPlay != null) {
         menuItemPlay.setIcon(R.drawable.ic_action_play).setTitle(R.string.va_play);
      }
   }


   // animation
   @Override
   public void update(Observable observable, Object data) {
      animationSeek.setProgress((int) (100f * animationManager.getProgress()));

      if (animationManager.getProgress() == 1f) {
         invalidateOptionsMenu();
      }
   }
}
