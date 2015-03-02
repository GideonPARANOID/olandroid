/**
 * @created 2015-03-02
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.mmp.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import uk.ac.aber.gij2.mmp.MMPApplication;
import uk.ac.aber.gij2.mmp.R;


public class SettingsFragment extends PreferenceFragment implements
   SharedPreferences.OnSharedPreferenceChangeListener {

   private MMPApplication app;


   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      app = (MMPApplication) getActivity().getApplication();

      addPreferencesFromResource(R.xml.preferences);
      PreferenceManager.getDefaultSharedPreferences(app).registerOnSharedPreferenceChangeListener(
         this);
   }


   public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
      switch (key) {
         case "p_colour_theme":
            app.updateColourTheme();
      }
   }
}

