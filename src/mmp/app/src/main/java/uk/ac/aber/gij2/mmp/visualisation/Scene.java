/**
 * @created 2015-01-28
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.mmp.visualisation;

import uk.ac.aber.gij2.mmp.Flight;


public class Scene {

   private SceneGraph sceneGraph;


   public Scene() {
      sceneGraph = new SceneGraph();
   }


   public void setup() {
      sceneGraph.add(new Grid(1, 10));
   }


   /**
    * @param flight - the current flight
    */
   public void setFlight(Flight flight) {

      for (Manoeuvre manoeuvre : flight.getManoeuvres()) {
         sceneGraph.add(manoeuvre);
      }
   }

   /**
    * draws the current scene
    * @param matrix - the initial matrix to start drawing from
    */
   public void draw(float[] matrix) {

      for (Drawable temp : sceneGraph) {
         temp.draw(matrix);
      }
   }
}
