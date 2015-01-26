/**
 * @created 2015-01-26
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.mmp.visualisation;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import uk.ac.aber.gij2.mmp.R;


public class Renderer implements GLSurfaceView.Renderer {

   private static Context context;

   private int program;

   private final float[] background = {0.0f, 0.0f, 0.0f, 1.0f};

   private SceneGraph sceneGraph;

   private final float[] mMVPMatrix = new float[16];
   private final float[] mProjectionMatrix = new float[16];
   private final float[] mViewMatrix = new float[16];
   private final float[] mRotationMatrix = new float[16];

   public Renderer(Context context) {
      this.context = context;
   }


   public void onSurfaceCreated(GL10 unused, EGLConfig config) {

      // shader loading & compilation
      int vertexShader = Renderer.loadShader(
         GLES20.GL_VERTEX_SHADER,
         Renderer.loadShaderCode(R.raw.vertex));

      int fragmentShader = Renderer.loadShader(
         GLES20.GL_FRAGMENT_SHADER,
         Renderer.loadShaderCode(R.raw.fragment));

      program = GLES20.glCreateProgram();
      GLES20.glAttachShader(program, vertexShader);
      GLES20.glAttachShader(program, fragmentShader);
      GLES20.glLinkProgram(program);

      GLES20.glClearColor(background[0], background[1], background[2], background[3]);

      sceneGraph = new SceneGraph();

      // demo
      sceneGraph.add(new Triangle(program, 0.5f));
      sceneGraph.add(new Triangle(program, 1f));
   }


   public void onSurfaceChanged(GL10 unused, int width, int height) {
      GLES20.glViewport(0, 0, width, height);

      float ratio = (float) width / height;

      // projection matrix is applied to object coordinates in the onDrawFrame() method
      Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
   }


   public void onDrawFrame(GL10 unused) {

      // background
      GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

      // set the camera position
      Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

      // calculate the projection and view transformation
      Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

      // draw shapes
      for (Shape shape : sceneGraph) {
         shape.draw( mMVPMatrix);
      }
   }


   /**
    *
    * @param type - GLES20.GL_VERTEX_SHADER or GLES20.GL_FRAGMENT_SHADER
    * @param shaderCode - string of glsl
    * @return - reference to the shader program
    */
   public static int loadShader(int type, String shaderCode){

      int shader = GLES20.glCreateShader(type);

      // add the source code to the shader and compile it
      GLES20.glShaderSource(shader, shaderCode);
      GLES20.glCompileShader(shader);

      return shader;
   }


   /**
    * @param resource - id for finding a resource file
    * @return - shader code string
    */
   public static String loadShaderCode(int resource) {
      String shaderCode = "";

      try {
         InputStream inputStream = context.getResources().openRawResource(resource);
         BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

         String read = reader.readLine();
         while (read != null) {
            shaderCode += read + "\n";
            read = reader.readLine();
         }

      } catch (Exception e) {
         Log.d("error: reading shader", "could not read shader: " + e.getLocalizedMessage());
      }

      return shaderCode;
   }


   /**
    * utility method for debugging opengl calls, provide the name of the call just after making it
    *    if the operation is not successful, the check throws an error
    * @param glOperation - name of the opengl call to check.
    */
   public static void checkGlError(String glOperation) {
      int error;
      while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
         Log.e("Renderer", glOperation + ": glError " + error);
         throw new RuntimeException(glOperation + ": glError " + error);
      }
   }
}
