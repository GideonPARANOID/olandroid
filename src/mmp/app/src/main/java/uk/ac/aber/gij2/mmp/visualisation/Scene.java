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

      Component[] temp = new Component[] {

         new Component(program, -1, -1, 0),
         new Component(program, 1,0, 0),
         new Component((program), 0, 0, 0),
         new Component((program), 0, 0, 0),
         new Component(program, Component.MIN, Component.MIN, Component.ZERO),
         new Component(program, Component.MIN, Component.ZERO, Component.ZERO),
         new Component(program, Component.MAX, Component.ZERO, Component.ZERO),
         new Component(program, Component.MAX, Component.ZERO, Component.ZERO),
         new Component(program, Component.MAX, Component.ZERO, Component.ZERO),
         new Component(program, Component.MAX, Component.ZERO, Component.ZERO),
         new Component((program), 0, 0, 0),
         new Component((program), 0, 0, 0),
         new Component((program), 0, 0, 0),
         new Component((program), 0, 0, 0),
      };

      sceneGraph.add(new Manoeuvre(temp));

   }


   public void draw(float[] matrix) {

      for (Drawable temp : sceneGraph) {
         temp.draw(matrix);
      }
   }
}
