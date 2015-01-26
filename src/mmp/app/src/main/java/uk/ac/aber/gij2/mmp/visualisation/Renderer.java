package uk.ac.aber.gij2.mmp.visualisation;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import uk.ac.aber.gij2.mmp.R;

/**
 * Created by paranoia on 26/01/15.
 */
public class Renderer implements GLSurfaceView.Renderer {

   private static Context context;

   private int program;

   private final float[] background = {0.0f, 0.0f, 1.0f, 1.0f};

   private SceneGraph sceneGraph;




   public Renderer(Context context) {
      this.context = context;

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

      sceneGraph = new SceneGraph();
   }






   public static int loadShader(int type, String shaderCode){

      // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
      // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
      int shader = GLES20.glCreateShader(type);

      // add the source code to the shader and compile it
      GLES20.glShaderSource(shader, shaderCode);
      GLES20.glCompileShader(shader);

      return shader;
   }

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


   public void onSurfaceCreated(GL10 unused, EGLConfig config) {

      GLES20.glClearColor(background[0], background[1], background[2], background[3]);
   }

   public void onDrawFrame(GL10 unused) {
      // Redraw background color
      GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);


   }

   public void onSurfaceChanged(GL10 unused, int width, int height) {
      GLES20.glViewport(0, 0, width, height);
   }
}
