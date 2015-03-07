/**
 * @created 2015-03-07
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.mmp.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import uk.ac.aber.gij2.mmp.R;


public class HelpDialogFragment extends DialogFragment {

   private int contentId;


   public HelpDialogFragment(int contentId) {
      super();

      this.contentId = contentId;
   }

   @Override
   public Dialog onCreateDialog(Bundle savedInstanceState) {

      return new AlertDialog.Builder(getActivity()).setView(getActivity().getLayoutInflater()
            .inflate(R.layout.dialog_help, null))
         .setTitle(R.string.a_help)
         .setMessage(contentId).create();
   }
}
