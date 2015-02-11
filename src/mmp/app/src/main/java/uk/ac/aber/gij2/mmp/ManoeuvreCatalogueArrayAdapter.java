/**
 * @created 2015-02-11
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.mmp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class ManoeuvreCatalogueArrayAdapter extends ArrayAdapter<String> {

   private final Context context;
   private final ManoeuvreCatalogue manoeuvreCatalogue;


   public ManoeuvreCatalogueArrayAdapter(Context context, ManoeuvreCatalogue manoeuvreCatalogue) {
      super(context, R.layout.list_olan, new String[manoeuvreCatalogue.getOLANs().length]);
      this.context = context;
      this.manoeuvreCatalogue = manoeuvreCatalogue;
   }


   @Override
   public View getView(int position, View convertView, ViewGroup parent) {

      View row = ((LayoutInflater) context.getSystemService(
         Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_olan, parent, false);

      String[] olans = manoeuvreCatalogue.getOLANs();

      ((TextView) row.findViewById(R.id.ol_text_id)).setText(olans[position]);
      ((TextView) row.findViewById(R.id.ol_text_name)).setText(manoeuvreCatalogue.get(
         olans[position]).getName());

      return row;
   }
}
