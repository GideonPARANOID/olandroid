/**
 * @created 2015-01-31
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.mmp.visualisation;

import java.util.LinkedList;


public class Manoeuvre implements Drawable {

   private LinkedList<Component> components;


   public Manoeuvre(LinkedList<Component> components) {
      this.components = components;
   }


   public void draw(float[] initialMatrix) {

      for (int i = 1; i < components.size(); i++) {
         components.get(i).draw(initialMatrix);
      }
   }

}
