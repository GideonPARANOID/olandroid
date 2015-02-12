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
      sceneGraph = new ArrayList<>();
      sceneGraph.add(new Grid(1, 10));
   }


   /**
    * @param flight - the current flight
    */
   public void setFlight(Flight flight) {
      sceneGraph = new ArrayList<>();
      sceneGraph.add(new Grid(1, 10));
      sceneGraph.add(flight);
   }


   /**
    * draws the current scene
    * @param initialMatrix - the initial matrix to start drawing from
    */
   public void draw(float[] initialMatrix) {

      for (Drawable item : sceneGraph) {
         item.draw(initialMatrix);
      }
   }
}
