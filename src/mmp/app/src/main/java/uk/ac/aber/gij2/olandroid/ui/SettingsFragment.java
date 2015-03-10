/**
 * @created 2015-03-02
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.olandroid.ui;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import uk.ac.aber.gij2.olandroid.R;


public class SettingsFragment extends PreferenceFragment {

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      addPreferencesFromResource(R.xml.preferences);
   }
}

