/**
 * @created 2015-01-28
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.mmp.visualisation;


import java.util.LinkedList;

public class Scene {

   private SceneGraph sceneGraph;


   public Scene() {
      sceneGraph = new SceneGraph();
   }


   public void setup(int program) {

      // demo
//      sceneGraph.add(new Cube(program, 1f));
      sceneGraph.add(new Grid(program, 1, 10));

      Component component1 = new Component(program, Component.MAX, Component.ZERO, Component.ZERO),
         component2 = new Component(program, Component.MAX, Component.ZERO, Component.ZERO);


      LinkedList temp = new LinkedList();
      temp.add(component1);
      temp.add(component2);

      sceneGraph.add(new Manoeuvre(temp));

   }


   public void draw(float[] matrix) {

      for (Drawable temp : sceneGraph) {
         temp.draw(matrix);
      }
   }
}
