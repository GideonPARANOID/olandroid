/**
 * @created 2015-03-07
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.mmp.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;

import uk.ac.aber.gij2.mmp.MMPApplication;
import uk.ac.aber.gij2.mmp.R;


public class FlightTitleDialogFragment extends DialogFragment {

   @Override
   public Dialog onCreateDialog(Bundle savedInstanceState) {

      return new AlertDialog.Builder(getActivity()).setView(getActivity().getLayoutInflater()
            .inflate(R.layout.dialog_flight_title, null))
         .setTitle(R.string.va_save)
         .setMessage(R.string.va_new_flight_title)
         .setPositiveButton(R.string.a_dialog_accept, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {

               ((MMPApplication) getActivity().getApplication()).getFlightManager()
                  .saveCurrentFlight(((EditText) getDialog().findViewById(R.id.d_text_flight_title))
                     .getText().toString());
            }

         }).setNegativeButton(R.string.a_dialog_cancel, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {}

      }).create();
   }
}
