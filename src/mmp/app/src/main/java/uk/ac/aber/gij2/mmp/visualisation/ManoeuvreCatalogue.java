/**
 * @created 2015-02-01
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.mmp.visualisation;


import android.content.Context;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import uk.ac.aber.gij2.mmp.R;


public class ManoeuvreCatalogue {


   private String namespace = null;

   private int program;

   private HashMap<String, Manoeuvre> manoeuvres;


   public ManoeuvreCatalogue(Context context) {

      manoeuvres = new HashMap<>();

      try {
         XmlPullParser parser = context.getResources().getXml(R.xml.manoeurvre_catalogue);

         parser.next();
         parser.next();

         parser.require(XmlPullParser.START_TAG, null, "manoeuvres");

         while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
               continue;
            }

            if (parser.getName().equals("manoeuvre")) {

               String olan = parser.getAttributeValue(namespace, "olan");

               manoeuvres.put(olan, new Manoeuvre(
                     (Component[]) buildComponentsList(parser).toArray(), olan));

            } else {
               skip(parser);
            }
         }
      } catch (XmlPullParserException exception) {
         System.err.println(exception.getMessage());
      } catch (IOException exception) {
         System.err.println(exception.getMessage());
      }
   }


   /**
    * @param parser - the parsing application for the xml
    * @return - a list of components parsed from the xml
    * @throws XmlPullParserException
    * @throws IOException
    */
   private ArrayList<Component> buildComponentsList(XmlPullParser parser) throws XmlPullParserException, IOException {

      ArrayList<Component> components = new ArrayList<>();

      parser.require(XmlPullParser.START_TAG, namespace, "manoeuvre");

      while (parser.next() != XmlPullParser.END_TAG) {
         if (parser.getEventType() != XmlPullParser.START_TAG) {
            continue;
         }

         if (parser.getName().equals("component")) {

            components.add(new Component(program,
               parseComponentStrength(parser.getAttributeValue(namespace, "pitch")),
               parseComponentStrength(parser.getAttributeValue(namespace, "yaw")),
               parseComponentStrength(parser.getAttributeValue(namespace, "roll"))
            ));

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
}
