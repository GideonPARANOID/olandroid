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
import java.io.InputStreamReader;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import uk.ac.aber.gij2.mmp.MMPApplication;
import uk.ac.aber.gij2.mmp.R;


public class Renderer implements GLSurfaceView.Renderer {

   public static int program;

   // view & scene building tools
   private final float[] mMVPMatrix, mProjectionMatrix, mViewMatrix;

   // for back referencing to the application
   private Context context;

   // shifting around the view
   private float viewX, viewY, viewZoom;
   private float[] viewMatrix = new float[16];

   // content
   private Scene scene;


   public Renderer(Context context) {
      this.context = context;

      mMVPMatrix = new float[16];
      mProjectionMatrix = new float[16];
      mViewMatrix = new float[16];

      viewX = 45f;
      viewY = 45f;
      viewZoom = 1f;

      buildViewMatrix();
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

      float[] colour  = ((MMPApplication) context.getApplicationContext()).getCurrentColourTheme(
         R.array.ct_background);

      GLES20.glClearColor(colour[0], colour[1], colour[2], colour[3]);

      GLES20.glEnable(GLES20.GL_CULL_FACE);
      GLES20.glEnable(GLES20.GL_DEPTH_TEST);

      Matrix.setIdentityM(mViewMatrix, 0);

      scene = ((MMPApplication) context.getApplicationContext()).getScene();

      buildViewMatrix();
   }


   public void onSurfaceChanged(GL10 gl, int width, int height) {
      GLES20.glViewport(0, 0, width, height);

      float ratio = (float) width / height;

      // projection matrix is applied to object coordinates in the draw method
      Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 1f, 1000f);

      // refreshig colours
      float[] colour  = ((MMPApplication) context.getApplicationContext()).getCurrentColourTheme(
         R.array.ct_background);

      GLES20.glClearColor(colour[0], colour[1], colour[2], colour[3]);

      buildViewMatrix();
   }


   public void onDrawFrame(GL10 unused) {
      GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

      // calculate the projection and view transformation
      Matrix.multiplyMM(mMVPMatrix, 0, viewMatrix, 0, mViewMatrix, 0);

      scene.draw(mMVPMatrix);
   }


   /**
    * refreshes the view matrix, showing where the camera is
    */
   public void buildViewMatrix() {
      float[] temp = new float[16];

      Matrix.translateM(viewMatrix, 0, mProjectionMatrix, 0, 0f, 0f, -20f * viewZoom);
      Matrix.rotateM(temp, 0, viewMatrix, 0, viewY, 1f, 0f, 0f);
      Matrix.rotateM(viewMatrix, 0, temp, 0, viewX, 0f, 1f, 0f);
   }

   public float getViewZoom() {
      return viewZoom;
   }

   public void setViewZoom(float viewZoom) {
      this.viewZoom = viewZoom;
      buildViewMatrix();
   }

   public float getViewX() {
      return viewX;
   }

   public void setViewX(float viewX) {
      this.viewX = viewX;
      buildViewMatrix();
   }

   public float getViewY() {
      return viewY;
   }

   public void setViewY(float viewY) {
      this.viewY = viewY;
      buildViewMatrix();
   }


   /**
    * utility method for debugging opengl calls, provide the name of the call just after making it
    *    if the operation is not successful, the check throws an error
    * @param operation - name of the opengl call to check.
    */
   public static void checkGlError(String operation) {
      int error;
      while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
         throw new RuntimeException(operation + ": gl error " + error);
      }
   }


   /**
    * @param type - GLES20.GL_VERTEX_SHADER or GLES20.GL_FRAGMENT_SHADER
    * @param shaderCode - string of glsl
    * @return - reference to the shader program
    */
   public int loadShader(int type, String shaderCode) {
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
         BufferedReader reader = new BufferedReader(new InputStreamReader(
               context.getResources().openRawResource(resource)));

         String read = reader.readLine();
         while (read != null) {
            shaderCode += read + "\n";
            read = reader.readLine();
         }

      } catch (Exception exception) {
         Log.e(this.getClass().getName(), "could not read shader: " + exception.getMessage());
      }

      return shaderCode;
   }
}
