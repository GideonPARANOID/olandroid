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

import uk.ac.aber.gij2.mmp.MMPApplication;
import uk.ac.aber.gij2.mmp.R;


public class Renderer implements GLSurfaceView.Renderer {

   public static int program;

   // view & scene building tools
   private final float[] mMVPMatrix = new float[16],
      mProjectionMatrix = new float[16],
      mViewMatrix = new float[16];

   // for back referencing to the application
   private Context context;
   private float viewX, viewY, viewZoom = 1f;

   // content
   private Scene scene;


   public Renderer(Context context) {
      this.context = context;
   }


   public void onSurfaceCreated(GL10 gl, EGLConfig config) {

      // shader loading & compilation
      int vertexShader = loadShader(
            GLES20.GL_VERTEX_SHADER,
            loadShaderCode(R.raw.vertex)),
         fragmentShader = loadShader(
            GLES20.GL_FRAGMENT_SHADER,
            loadShaderCode(R.raw.fragment));

      program = GLES20.glCreateProgram();
      GLES20.glAttachShader(program, vertexShader);
      GLES20.glAttachShader(program, fragmentShader);
      GLES20.glLinkProgram(program);

      float[] colour  = ((MMPApplication) context.getApplicationContext()).buildColourArray(
         R.color.vis_background);

      GLES20.glClearColor(colour[0], colour[1], colour[2], colour[3]);

      GLES20.glEnable(GLES20.GL_CULL_FACE);
      GLES20.glEnable(GLES20.GL_DEPTH_TEST);

      scene = ((MMPApplication) context.getApplicationContext()).getScene();
   }


   public void onSurfaceChanged(GL10 gl, int width, int height) {
      GLES20.glViewport(0, 0, width, height);

      float ratio = (float) width / height;

      // projection matrix is applied to object coordinates in the draw method
      Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 1f, 1000f);
   }


   public void onDrawFrame(GL10 unused) {

      // background
      GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

      // set the camera position
      Matrix.setLookAtM(mViewMatrix, 0,
         0, 0, 0,       // eye point
         0f, 0f, 0f,    // centre of view
         0f, 1f, 0f);   // up

      Matrix.setIdentityM(mViewMatrix, 0);

      // shifting the perspective
      float[] rotationY = new float[16], rotationX = new float[16], translation = new float[16];

      Matrix.translateM(translation, 0, mProjectionMatrix, 0, 0f, 0f, -20f * viewZoom);
      Matrix.rotateM(rotationY, 0, translation, 0, viewY, 1f, 0f, 0f);
      Matrix.rotateM(rotationX, 0, rotationY, 0, viewX, 0f, 1f, 0f);

      // calculate the projection and view transformation
      Matrix.multiplyMM(mMVPMatrix, 0, rotationX, 0, mViewMatrix, 0);

      scene.draw(mMVPMatrix);
   }


   /**
    * scales the distance from the centre of the scene, cuts off at the bottom .2f
    * @param factor - factor by which to modify the current scale
    */
   public void scaleViewZoom(float factor) {
      viewZoom = viewZoom / factor < 0.2f ? 0.2f : viewZoom / factor;
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


   /**
    * utility method for debugging opengl calls, provide the name of the call just after making it
    *    if the operation is not successful, the check throws an error
    * @param glOperation - name of the opengl call to check.
    */
   public static void checkGlError(String glOperation) {
      int error;
      while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
         Log.e("renderer", glOperation + ": glError " + error);
         throw new RuntimeException(glOperation + ": glError " + error);
      }
   }


   /**
    * @param type - GLES20.GL_VERTEX_SHADER or GLES20.GL_FRAGMENT_SHADER
    * @param shaderCode - string of glsl
    * @return - reference to the shader program
    */
   public int loadShader(int type, String shaderCode){

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
   public String loadShaderCode(int resource) {
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
         Log.d("renderer", "could not read shader: " + e.getLocalizedMessage());
      }

      return shaderCode;
   }
}
