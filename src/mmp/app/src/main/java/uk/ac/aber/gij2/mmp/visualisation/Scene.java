/**
 * @created 2015-01-28
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.mmp.visualisation;


public class Scene {

   private SceneGraph sceneGraph;


   public Scene() {
      sceneGraph = new SceneGraph();
   }


   public void setup(int program) {

      // demo
      sceneGraph.add(new Grid(program, 1, 10));

      Component component1 = new Component(program, Component.MAX, Component.ZERO, Component.ZERO),
         component2 = new Component(program, Component.MIN, Component.ZERO, Component.ZERO),
         component3 = new Component(program, Component.MIN, Component.MAX, Component.ZERO);

      Component[] temp = new Component[]{
         component1, component2, component3, component1, component1
      };

      sceneGraph.add(new Manoeuvre(temp));

   }


   public void draw(float[] matrix) {

      for (Drawable temp : sceneGraph) {
         temp.draw(matrix);
      }
   }
}
