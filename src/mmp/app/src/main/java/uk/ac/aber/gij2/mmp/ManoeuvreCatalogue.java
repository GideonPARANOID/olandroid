/**
 * @created 2015-02-01
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.mmp;

import android.content.Context;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import uk.ac.aber.gij2.mmp.visualisation.Component;
import uk.ac.aber.gij2.mmp.visualisation.Manoeuvre;


public class ManoeuvreCatalogue {

   // we want a predictable iteration order
   private LinkedHashMap<String, Manoeuvre> manoeuvres;


   /**
    * @param context - the context relevant for getting the xml
    */
   public ManoeuvreCatalogue(Context context) {
      manoeuvres = new LinkedHashMap<>();

      try {
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
      } catch (XmlPullParserException | IOException exception) {
         System.err.println(exception.getMessage());
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

            manoeuvres.put(fullOLAN, new Manoeuvre(parseManoeuvreComponents(parser),
                  fullOLAN, name));
         }
      }
   }


   /**
    * @param parser - the parsing application for the xml
    * @return - a list of components parsed from the xml
    * @throws XmlPullParserException
    * @throws IOException
    */
   protected Component[] parseManoeuvreComponents(XmlPullParser parser) throws XmlPullParserException,
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

      if (parser.getEventType() != XmlPullParser.START_TAG) {
         throw new IllegalStateException();
      }

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
    * querying the manoeuvre catalogue
    * @param key - the olan key to look for
    * @return - a manoeuvre with olan matchin the input
    * @throws NullPointerException - might not be able to find the manoeuvre specified
    */
   public Manoeuvre get(String key) throws NullPointerException {
      return manoeuvres.get(key);
   }


   /**
    * @return - an array of olan figures available in this catalogue
    */
   public String[] getOLANs() {
      ArrayList<String> ids = new ArrayList<>(manoeuvres.keySet());
      return ids.toArray(new String[ids.size()]);
   }
}
