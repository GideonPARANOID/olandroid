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
//      sceneGraph.add(new Cube(program, 1f));
      sceneGraph.add(new Component(program, Component.MAX, Component.ZERO, Component.ZERO));
      sceneGraph.add(new Grid(program, 1, 10));
   }


   public void draw(float[] matrix) {

      for (Shape shape : sceneGraph) {
         shape.draw(matrix);
      }

/*
      sceneGraph.get(0).draw(matrix);

      float[] newMatrix = new float[16];

      Matrix.translateM(newMatrix, 0, matrix, 0, .5f, 0f, .5f);


      sceneGraph.get(1).draw(newMatrix);*/
   }
}
