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
   private static final String TAG = "Renderer";

   private static Context context;
   private final float[] background = {1f, 1f, 1f, 1f};
   private final float[] mMVPMatrix = new float[16];
   private final float[] mProjectionMatrix = new float[16];
   private final float[] mViewMatrix = new float[16];
   private int program;
   private Scene scene;
   private float viewX, viewY;


   public Renderer(Context context) {
      this.context = context;
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
         Log.d(TAG, "could not read shader: " + e.getLocalizedMessage());
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
         Log.e(TAG, glOperation + ": glError " + error);
         throw new RuntimeException(glOperation + ": glError " + error);
      }
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

      scene = new Scene();
      scene.setup(program);
   }

   public void onSurfaceChanged(GL10 unused, int width, int height) {
      GLES20.glViewport(0, 0, width, height);

      float ratio = (float) width / height;

      // projection matrix is applied to object coordinates in the onDrawFrame() method
      Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 1, 100);
   }

   public void onDrawFrame(GL10 unused) {

      // background
      GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

      // set the camera position
      Matrix.setLookAtM(mViewMatrix, 0,
         0, 0, 0,      // eye point
         0f, 0f, 0f,    // centre of view
         0f, 1f, 0f);   // up

      Matrix.setIdentityM(mViewMatrix, 0);

      // shifting the perspective
      float[] rotationY = new float[16], rotationX = new float[16], translation = new float[16];

      Matrix.translateM(translation, 0, mProjectionMatrix, 0, 0f, 0f, -10f);
      Matrix.rotateM(rotationY, 0, translation, 0, viewY, 1f, 0f, 0f);
      Matrix.rotateM(rotationX, 0, rotationY, 0, viewX, 0f, 1f, 0f);

      // calculate the projection and view transformation
      Matrix.multiplyMM(mMVPMatrix, 0, rotationX, 0, mViewMatrix, 0);

      scene.draw(mMVPMatrix);
   }

   public float getViewX() {
      return viewX;
   }

   public void setViewX(float viewX) {
      this.viewX = viewX;
   }

   public float getViewY() {
      return viewY;
   }

   public void setViewY(float viewY) {
      this.viewY = viewY;
   }
}
