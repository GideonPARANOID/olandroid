/**
 * @created 2015-02-01
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.olandroid;

import android.content.Context;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import uk.ac.aber.gij2.olandroid.visualisation.Component;
import uk.ac.aber.gij2.olandroid.visualisation.Manoeuvre;


public class ManoeuvreCatalogue {

   // we want a predictable iteration order for list populating
   private LinkedHashMap<String, Manoeuvre> catalogue;
   private String[] categories;
   private Context context;

   private Manoeuvre correction;

   /**
    * @param context - the context relevant for getting the xml
    */
   public ManoeuvreCatalogue(Context context) {

      this.context = context;
      catalogue = new LinkedHashMap<>();

      try {
         parseCategories();

      } catch (XmlPullParserException | IOException exception) {
         Log.d(this.getClass().getName(), exception.getMessage());
      }
   }


   /**
    * parses the xml manoeuvre catalogue's categories into the catalogue
    * @throws XmlPullParserException
    * @throws IOException
    */
   protected void parseCategories() throws XmlPullParserException, IOException {

      List<String> categoriesTemp = new ArrayList<>();

      XmlPullParser parser = context.getResources().getXml(R.xml.manoeurvre_catalogue);

      // skipping over the first two element - xml declarations & top level container.
      parser.next();
      parser.next();

      parser.require(XmlPullParser.START_TAG, null, "catalogue");

      while (parser.next() != XmlPullParser.END_TAG) {
         if (parser.getName().equals("category")) {

            String category = parser.getAttributeValue(null, "name");

            boolean inList = false;
            for (int i = 0; i < categoriesTemp.size() && !inList; i++) {
               if (categoriesTemp.get(i).equals(category)) {
                  inList = true;
               }
            }

            if (!inList) {
               categoriesTemp.add(category);
            }

            parseManoeuvres(parser, category);
         }
      }

      categories = categoriesTemp.toArray(new String[categoriesTemp.size()]);
   }


   /**
    * parses the xml manoeuvre catalogue category's manoeuvres into the catalogue
    * @throws XmlPullParserException
    * @throws IOException
    */
   protected void parseManoeuvres(XmlPullParser parser, String category) throws
      XmlPullParserException, IOException {

      parser.require(XmlPullParser.START_TAG, null, "category");

      while (parser.next() != XmlPullParser.END_TAG) {
         if (parser.getName().equals("manoeuvre")) {

            parseManoeuvreVariants(parser, parser.getAttributeValue(null, "olan"), category);
         }
      }
   }


   /**
    * @param parser - the parsing application for the xml
    * @param olan - the olan name of the top manoeuvre
    * @throws XmlPullParserException
    * @throws IOException
    */
   protected void parseManoeuvreVariants(XmlPullParser parser, String olan, String category) throws
      XmlPullParserException, IOException {

      parser.require(XmlPullParser.START_TAG, null, "manoeuvre");

      // looping through variants
      while (parser.next() != XmlPullParser.END_TAG) {
         if (parser.getName().equals("variant")) {

            // variables for the manoeuvre
            String fullOLAN = parser.getAttributeValue(null, "olanPrefix") + olan,
               aresti = parser.getAttributeValue(null, "aresti"),
               name = parser.getAttributeValue(null, "name"),
               parsedCorrection = parser.getAttributeValue(null, "correction");

            List<Component> components = new ArrayList<>();
            List<Integer> groupIndicesPre = new ArrayList<>(), groupIndicesPost = new ArrayList<>();

            parser.require(XmlPullParser.START_TAG, null, "variant");

            // looping through components
            for (int i = 0; parser.next() != XmlPullParser.END_TAG; i++) {
               if (parser.getName().equals("component")) {

                  components.add(new Component(
                     Component.Bound.parse(parser.getAttributeValue(null, "pitch")),
                     Component.Bound.parse(parser.getAttributeValue(null, "yaw")),
                     Component.Bound.parse(parser.getAttributeValue(null, "roll")),
                     Float.parseFloat(parser.getAttributeValue(null, "length")),
                     ((OLANdroid) context.getApplicationContext()).getColourTheme(
                        R.array.colour_theme_front),
                     ((OLANdroid) context.getApplicationContext()).getColourTheme(
                        R.array.colour_theme_back)));

                  // building the variable groups
                  switch (Manoeuvre.Group.parse(parser.getAttributeValue(null, "group"))) {
                     case PRE:
                        groupIndicesPre.add(i);
                        break;
                     case POST:
                        groupIndicesPost.add(i);
                        break;
                  }

                  // skipping content
                  skip(parser);
               }
            }

            // assembling the manoeuvre
            Manoeuvre manoeuvre = new Manoeuvre(
               components.toArray(new Component[components.size()]), fullOLAN, aresti, name,
               category, integerListToPrimitive(groupIndicesPre),
               integerListToPrimitive(groupIndicesPost));

            catalogue.put(fullOLAN, manoeuvre);

            // if the manoeuvre can be used as a correction component
            if (parsedCorrection != null) {
               correction = manoeuvre;
            }
         }
      }
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
    * small helper function
    * @param list - a list of lists (of type integer) to convert
    * @return - the list as an array of ints
    */
   private int[] integerListToPrimitive(List<Integer> list) {

      int[] primitive = new int[list.size()];

      for (int i = 0; i < primitive.length; i++) {
         primitive[i] = list.get(i);
      }
      return primitive;
   }


   /**
    * querying the manoeuvre catalogue with a key
    * @param key - the olan key to look for
    * @return - a manoeuvre with olan matching the input
    * @throws NullPointerException - might not be able to find the manoeuvre specified
    */
   public Manoeuvre get(String key) throws NullPointerException {
      return catalogue.get(key);
   }


   /**
    * querying the manoeuvre catalogue with a category
    * @param category - the category to look for
    * @return - an array of manoeuvres in that category
    * @throws NullPointerException - if the category isn't valid
    */
   public Manoeuvre[] getManoeuvres(String category) throws NullPointerException {
      List<String> ids = new ArrayList<>(catalogue.keySet());
      List<Manoeuvre> inCategory = new ArrayList<>();

     for (int i = 0; i < ids.size(); i++) {
         if (catalogue.get(ids.get(i)).getCategory().equals(category)) {
            inCategory.add(catalogue.get(ids.get(i)));
         }
      }

      return inCategory.toArray(new Manoeuvre[inCategory.size()]);
   }


   /**
    * @return - an array of categories of manoeuvres found in the catalogue
    */
   public String[] getCategories() {
      return categories;
   }


   /**
    * @return - a component which can be used for vertical correction
    */
   public Manoeuvre getCorrection() {
      return correction;
   }
}
