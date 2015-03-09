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
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import uk.ac.aber.gij2.mmp.InvalidFlightException;
import uk.ac.aber.gij2.mmp.MMPApplication;
import uk.ac.aber.gij2.mmp.R;


public class FlightTitleDialogFragment extends DialogFragment {

   @Override
   public Dialog onCreateDialog(Bundle savedInstanceState) {

      final AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(getActivity().getLayoutInflater()
            .inflate(R.layout.dialog_flight_title, null))
         .setTitle(R.string.va_save)
         .setMessage(R.string.a_new_flight_title)
         .setPositiveButton(R.string.a_accept, null)
         .setNegativeButton(R.string.a_cancel, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {}

         }).create();


      // on a new dialog box being made, setup listening on the positive button, does it this way
      // to make sure we can validate input before its dismissed
      dialog.setOnShowListener(new DialogInterface.OnShowListener() {

         @Override
         public void onShow(DialogInterface unused) {

            final MMPApplication app = (MMPApplication) getActivity().getApplication();

            // sets the default text if modifying a saved flight
            if (app.getScene().getFlight().getName() != null) {
               ((EditText) getDialog().findViewById(R.id.d_text_flight_title)).setText(
                  app.getScene().getFlight().getName());
            }

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new
               View.OnClickListener() {

               @Override
               public void onClick(View view) {

                  try {
                     app.getFlightManager()
                        .saveCurrentFlight(((EditText) getDialog().findViewById(
                           R.id.d_text_flight_title)).getText().toString());

                     dialog.dismiss();

                      Toast.makeText(app,R.string.a_new_flight_title_valid,
                         Toast.LENGTH_SHORT).show();

                  } catch (InvalidFlightException exception) {

                     Toast.makeText(getActivity().getApplication(),
                        getText(R.string.a_new_flight_title_invalid) + " " + exception.getMessage(),
                        Toast.LENGTH_SHORT).show();
                  }
               }
            });
         }
      });

      return dialog;
   }
}
