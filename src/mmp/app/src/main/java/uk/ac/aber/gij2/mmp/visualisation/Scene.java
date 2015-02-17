/**
 * @created 2015-01-28
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.mmp.visualisation;

import android.content.Context;

import java.util.ArrayList;

import uk.ac.aber.gij2.mmp.Flight;
import uk.ac.aber.gij2.mmp.MMPApplication;
import uk.ac.aber.gij2.mmp.R;


public class Scene {

   private ArrayList<Drawable> sceneGraph;
   private Context context;


   public Scene(Context context) {
      sceneGraph = new ArrayList<>();
      this.context = context;
      sceneGraph.add(new Grid(5, 10,
         ((MMPApplication) context.getApplicationContext()).buildColourArray(R.color.vis_grid)));
   }


   /**
    * @param flight - the current flight
    */
   public void setFlight(Flight flight) {
      sceneGraph = new ArrayList<>();
      sceneGraph.add(new Grid(5, 10,
         ((MMPApplication) context.getApplicationContext()).buildColourArray(R.color.vis_grid)));
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
