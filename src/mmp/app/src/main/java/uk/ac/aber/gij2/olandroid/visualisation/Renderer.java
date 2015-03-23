/**
 * @created 2015-01-26
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.olandroid.visualisation;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import uk.ac.aber.gij2.olandroid.OLANdroid;
import uk.ac.aber.gij2.olandroid.R;
import uk.ac.aber.gij2.olandroid.ui.Util;


public class Renderer implements GLSurfaceView.Renderer {

   public static int program;

   // view & scene building tools
   private final float[] mMVPMatrix, mProjectionMatrix, mViewMatrix;

   // for back referencing to the application
   private Context context;

   // shifting around the view
   private float viewRotationY, viewRotationX, viewTranslationZ, viewTranslationX, viewZoom;
   private float[] viewMatrix;

   // content
   private Scene scene;


   public Renderer(Context context) {
      this.context = context;

      mMVPMatrix = new float[16];
      mProjectionMatrix = new float[16];
      mViewMatrix = new float[16];

      viewRotationY = 45f;
      viewRotationX = 45f;
      viewTranslationZ = 0f;
      viewTranslationX = 0f;
      viewZoom = 1f;

      viewMatrix = new float[16];

      buildViewMatrix();
   }


   public void onSurfaceCreated(GL10 gl, EGLConfig config) {

      // shader loading & compilation
      int vertexShader = 0, fragmentShader = 0;

      try {
         vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,
            Util.inputStreamToString(context.getResources().openRawResource(R.raw.vertex)));
         fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,
            Util.inputStreamToString(context.getResources().openRawResource(R.raw.fragment)));

      } catch (IOException exception) {
         Log.e(this.getClass().getName(), "error loading shader code");
      }

      program = GLES20.glCreateProgram();
      GLES20.glAttachShader(program, vertexShader);
      GLES20.glAttachShader(program, fragmentShader);
      GLES20.glLinkProgram(program);

      float[] colour  = ((OLANdroid) context.getApplicationContext()).getCurrentColourTheme(
         R.array.colour_theme_background);

      GLES20.glClearColor(colour[0], colour[1], colour[2], colour[3]);

      GLES20.glEnable(GLES20.GL_CULL_FACE);
      GLES20.glEnable(GLES20.GL_DEPTH_TEST);

      Matrix.setIdentityM(mViewMatrix, 0);

      scene = ((OLANdroid) context.getApplicationContext()).getScene();

      buildViewMatrix();
   }


   public void onSurfaceChanged(GL10 gl, int width, int height) {
      GLES20.glViewport(0, 0, width, height);

      float ratio = (float) width / height;

      // projection matrix is applied to object coordinates in the draw method
      Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 1f, 1000f);

      // refreshing colours
      float[] colour  = ((OLANdroid) context.getApplicationContext()).getCurrentColourTheme(
         R.array.colour_theme_background);

      GLES20.glClearColor(colour[0], colour[1], colour[2], colour[3]);

      buildViewMatrix();
   }


   public void onDrawFrame(GL10 unused) {
      GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
      scene.draw(mMVPMatrix);
   }


   /**
    * refreshes the view matrix, showing where the camera is
    */
   public void buildViewMatrix() {
      float[] temp = new float[16], temp2 = new float[16];

      Matrix.translateM(viewMatrix, 0, mProjectionMatrix, 0, 0f, 0f, -20f * viewZoom);
      Matrix.rotateM(temp, 0, viewMatrix, 0, viewRotationX, 1f, 0f, 0f);
      Matrix.rotateM(temp2, 0, temp, 0, viewRotationY, 0f, 1f, 0f);
      Matrix.translateM(viewMatrix, 0, temp2, 0, viewTranslationX, 0f, viewTranslationZ);

      Matrix.multiplyMM(mMVPMatrix, 0, viewMatrix, 0, mViewMatrix, 0);
   }

   public float getViewZoom() {
      return viewZoom;
   }

   public void setViewZoom(float viewZoom) {
      this.viewZoom = viewZoom;
      buildViewMatrix();
   }


   public void viewRotationYDelta(float delta) {
      viewRotationY += delta;

      if (viewRotationY > 360f) {
         viewRotationY -= 360f;
      }

      buildViewMatrix();
   }


   public float getViewRotationY() {
      return viewRotationY;
   }

   public void setViewRotationY(float viewRotationY) {
      this.viewRotationY = viewRotationY;
      buildViewMatrix();
   }

   public void viewRotationXDelta(float delta) {
      float test = viewRotationX + delta;

      // view bounds - the floor
      if (test >= 0 && test <= 90) {
         viewRotationX += delta;
         buildViewMatrix();
      }
   }


   public void viewParallelTranslationDelta(float delta) {
      viewTranslationZ -= Math.cos((viewRotationY / 360f) * Math.PI * 2) * delta;
      viewTranslationX += Math.sin((viewRotationY / 360f) * Math.PI * 2) * delta;
      buildViewMatrix();
   }


   public void viewHorizontalTranslationDelta(float delta) {
      // TODO: implement
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
}
