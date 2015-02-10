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
import java.util.HashMap;
import java.util.Map;

import uk.ac.aber.gij2.mmp.visualisation.Component;
import uk.ac.aber.gij2.mmp.visualisation.Manoeuvre;


public class ManoeuvreCatalogue {

   private final String namespace = null;
   private HashMap<String, Manoeuvre> manoeuvres;


   /**
    * @param context - the context relevant for getting the xml
    */
   public ManoeuvreCatalogue(Context context) {

      manoeuvres = new HashMap<>();

      try {
         XmlPullParser parser = context.getResources().getXml(R.xml.manoeurvre_catalogue);

         // skipping over the first two element - xml declarations & top level container.
         parser.next();
         parser.next();

         parser.require(XmlPullParser.START_TAG, null, "manoeuvres");

         while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
               continue;
            }

            if (parser.getName().equals("manoeuvre")) {

               buildVariantList(parser, parser.getAttributeValue(namespace, "olan"));

            } else {
               skip(parser);
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
   private void buildVariantList(XmlPullParser parser, String olan) throws XmlPullParserException, IOException {

      parser.require(XmlPullParser.START_TAG, namespace, "manoeuvre");

      while (parser.next() != XmlPullParser.END_TAG) {
         if (parser.getEventType() != XmlPullParser.START_TAG) {
            continue;
         }

         if (parser.getName().equals("variant")) {
            String fullOLAN = parser.getAttributeValue(namespace, "type") + olan;

            ArrayList<Component> components = buildComponentList(parser);

            manoeuvres.put(fullOLAN, new Manoeuvre(
                  components.toArray(new Component[components.size()]), fullOLAN));

         } else {
            skip(parser);
         }
      }
   }




      /**
    * @param parser - the parsing application for the xml
    * @return - a list of components parsed from the xml
    * @throws XmlPullParserException
    * @throws IOException
    */
   private ArrayList<Component> buildComponentList(XmlPullParser parser) throws XmlPullParserException, IOException {

      ArrayList<Component> components = new ArrayList<>();

      parser.require(XmlPullParser.START_TAG, namespace, "variant");

      while (parser.next() != XmlPullParser.END_TAG) {
         if (parser.getEventType() != XmlPullParser.START_TAG) {
            continue;
         }

         if (parser.getName().equals("component")) {

            components.add(new Component(
               parseComponentStrength(parser.getAttributeValue(namespace, "pitch")),
               parseComponentStrength(parser.getAttributeValue(namespace, "yaw")),
               parseComponentStrength(parser.getAttributeValue(namespace, "roll")),
               Float.parseFloat(parser.getAttributeValue(namespace, "length"))
            ));

            // skipping content
            skip(parser);
         }
      }

      return components;
   }


   /**
    * @param strength - the word to parse
    * @return - Component.MAX, Component.ZERO, Component.MIN
    */
   private int parseComponentStrength(String strength) {

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
    * @param parser - - the parsing application for the xml
    * @throws XmlPullParserException
    * @throws IOException
    */
   private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {

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
    * quering the manoeuvre catalogue
    * @param key - the olan key to look for
    * @return - a manoeuvre
    */
   public Manoeuvre getManoeuvre(String key) {
      return manoeuvres.get(key);
   }



      public String[] buildManoeuvreList() {
      String[] list = new String[manoeuvres.size()];

      int i = 0;
      for (Map.Entry<String, Manoeuvre> entry : manoeuvres.entrySet()) {
         list[i++] = entry.getKey();
      }

      return list;
   }
}
