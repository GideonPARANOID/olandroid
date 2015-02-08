/**
 * @created 2015-01-28
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.mmp.visualisation;

import java.util.ArrayList;

import uk.ac.aber.gij2.mmp.Flight;


public class Scene {

   private ArrayList<Drawable> sceneGraph;


   public Scene() {
      sceneGraph = new ArrayList();
   }


   public void setup() {
      sceneGraph.add(new Grid(1, 10));
   }


   /**
    * @param flight - the current flight
    */
   public void setFlight(Flight flight) {
      sceneGraph.add(flight);

   }

   /**
    * draws the current scene
    * @param matrix - the initial matrix to start drawing from
    */
   public void draw(float[] initialMatrix) {


      for (Drawable temp : sceneGraph) {
         temp.draw(initialMatrix);
      }
   }
}
