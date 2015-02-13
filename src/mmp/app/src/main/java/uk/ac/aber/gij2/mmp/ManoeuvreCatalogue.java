/**
 * @created 2015-02-01
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.mmp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import uk.ac.aber.gij2.mmp.visualisation.Component;
import uk.ac.aber.gij2.mmp.visualisation.Manoeuvre;


public class ManoeuvreCatalogue extends ArrayAdapter<String> {

   // we want a predictable iteration order
   private LinkedHashMap<String, Manoeuvre> manoeuvres;
   private Context context;


   /**
    * @param context - the context relevant for getting the xml
    */
   public ManoeuvreCatalogue(Context context) {
      super(context, R.layout.list_olan);

      this.context = context;
      manoeuvres = new LinkedHashMap<>();

      try {
         parseManoeuvres();

      } catch (XmlPullParserException | IOException exception) {
         System.err.println(exception.getMessage());
      }
   }

   @Override
   public View getView(int position, View convertView, ViewGroup parent) {

      View row = ((LayoutInflater) context.getSystemService(
         Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_olan, parent, false);

      String[] olans = getOLANs();

      ((TextView) row.findViewById(R.id.ol_text_olan)).setText(olans[position]);
      ((TextView) row.findViewById(R.id.ol_text_name)).setText(get(olans[position]).getName());

      return row;
   }


   /**
    * parses the xml manoeuvre catalogue into the linked hash map
    * @throws XmlPullParserException
    * @throws IOException
    */
   protected void parseManoeuvres() throws XmlPullParserException, IOException {
      XmlPullParser parser = context.getResources().getXml(R.xml.manoeurvre_catalogue);

      // skipping over the first two element - xml declarations & top level container.
      parser.next();
      parser.next();

      parser.require(XmlPullParser.START_TAG, null, "manoeuvres");

      while (parser.next() != XmlPullParser.END_TAG) {
         if (parser.getName().equals("manoeuvre")) {

            parseManoeuvreVariants(parser, parser.getAttributeValue(null, "olan"));
         }
      }
   }



   /**
    * @param parser - the parsing application for the xml
    * @param olan - the olan name of the top manoeuvre
    * @throws XmlPullParserException
    * @throws IOException
    */
   protected void parseManoeuvreVariants(XmlPullParser parser, String olan) throws
      XmlPullParserException, IOException {

      parser.require(XmlPullParser.START_TAG, null, "manoeuvre");

      while (parser.next() != XmlPullParser.END_TAG) {
         if (parser.getName().equals("variant")) {

            // have to get these now before the parser moves onto the components
            String fullOLAN = parser.getAttributeValue(null, "type") + olan,
               name = parser.getAttributeValue(null, "name");

            manoeuvres.put(fullOLAN, new Manoeuvre(parseVariantComponents(parser),
                  fullOLAN, name));

            super.add("");
         }
      }
   }


   /**
    * @param parser - the parsing application for the xml
    * @return - a list of components parsed from the xml
    * @throws XmlPullParserException
    * @throws IOException
    */
   protected Component[] parseVariantComponents(XmlPullParser parser) throws XmlPullParserException,
      IOException {

      ArrayList<Component> components = new ArrayList<>();

      parser.require(XmlPullParser.START_TAG, null, "variant");

      while (parser.next() != XmlPullParser.END_TAG) {
         if (parser.getName().equals("component")) {

            components.add(new Component(
               parseComponentStrength(parser.getAttributeValue(null, "pitch")),
               parseComponentStrength(parser.getAttributeValue(null, "yaw")),
               parseComponentStrength(parser.getAttributeValue(null, "roll")),
               Float.parseFloat(parser.getAttributeValue(null, "length"))
            ));

            // skipping content
            skip(parser);
         }
      }

      return components.toArray(new Component[components.size()]);
   }


   /**
    * @param strength - the word to parse
    * @return - Component.MAX, Component.ZERO, Component.MIN
    */
   protected int parseComponentStrength(String strength) {

      switch (strength) {
         case "MAX":
            return Component.MAX;
         case "ZERO":
            return Component.ZERO;
         case "MIN":
            return Component.MIN;
      }
      return Component.ZERO;
   }


   /**
    * @param parser - the parsing application for the xml
    * @throws XmlPullParserException
    * @throws IOException
    */
   protected void skip(XmlPullParser parser) throws XmlPullParserException, IOException {

      int depth = 1;
      while (depth != 0) {
         switch (parser.next()) {
            case XmlPullParser.END_TAG:
               depth--;
               break;
            case XmlPullParser.START_TAG:
               depth++;
               break;
         }
      }
   }


   /**
    * querying the manoeuvre catalogue with a key
    * @param key - the olan key to look for
    * @return - a manoeuvre with olan matchin the input
    * @throws NullPointerException - might not be able to find the manoeuvre specified
    */
   public Manoeuvre get(String key) throws NullPointerException {
      return manoeuvres.get(key);
   }


   /**
    * querying the manoeuvre catalogue with an index, matching the order of getOLANs
    * @param index - the olan index to retrieve
    * @return - a manoeuvre with olan matchin the input
    * @throws IndexOutOfBoundsException - might not be able to find the manoeuvre specified
    */
   public Manoeuvre get(int index) throws IndexOutOfBoundsException {
      return manoeuvres.get(getOLANs()[index]);
   }


   /**
    * @return - an array of olan figures available in this catalogue, always in the same order
    */
   public String[] getOLANs() {
      ArrayList<String> ids = new ArrayList<>(manoeuvres.keySet());
      return ids.toArray(new String[ids.size()]);
   }
}
