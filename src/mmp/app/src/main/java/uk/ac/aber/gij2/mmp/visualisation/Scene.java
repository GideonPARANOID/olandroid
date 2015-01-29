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
      sceneGraph.add(new Cube(program, 1f));
      sceneGraph.add(new Component(program, Component.MAX, Component.ZERO, Component.ZERO));
   }


   public void draw(float[] matrix) {

      for (Shape shape : sceneGraph) {
         shape.draw(matrix);
      }
   }
}
