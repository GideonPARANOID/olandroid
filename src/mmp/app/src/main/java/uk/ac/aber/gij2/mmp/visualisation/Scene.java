/**
 * @created 2015-01-28
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.mmp.visualisation;

import android.content.Context;

import uk.ac.aber.gij2.mmp.MMPApplication;
import uk.ac.aber.gij2.mmp.R;


public class Scene {

   private Grid grid;
   private Flight flight;

   public Scene(Context context) {
      grid = new Grid(5, 10,
         ((MMPApplication) context.getApplicationContext()).getCurrentColourTheme(R.array.ct_grid));
   }


   /**
    * draws the current scene
    * @param initialMatrix - the initial matrix to start drawing from
    */
   public void draw(float[] initialMatrix) {
      grid.draw(initialMatrix);

      if (flight != null) {
         flight.draw(initialMatrix);
      }
   }


   public void animate(float completion) {
      flight.animate(completion);
   }


   public Flight getFlight() {
      return flight;
   }

   public void setFlight(Flight flight) {
      this.flight = flight;
   }

   public Grid getGrid() {
      return grid;
   }

   public void setGrid(Grid grid) {
      this.grid = grid;
   }
}

