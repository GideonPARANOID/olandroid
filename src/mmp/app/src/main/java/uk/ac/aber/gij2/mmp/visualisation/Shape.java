/**
 * @created 2015-01-26
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.mmp.visualisation;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public abstract class Shape implements Drawable {

   private FloatBuffer vertexBuffer;
   private ShortBuffer drawOrderBuffer;

   private int mPositionHandle;
   private int mColorHandle;
   private int mMVPMatrixHandle;

   private float[] vertices;
   private short[] drawOrder;

   private float color[];
   protected boolean setup;


   public Shape() {
      color = Renderer.COLOR_FRAME;
      setup = false;
   }


   /**
    * constructs buffers & makes references to shader program variables, needs vertex & draw orders set
    */
   protected void setupDrawing() {

      // assume the draw order if none is explicitly set
      if (drawOrder == null) {
         drawOrder = new short[vertices.length / 3];

         for (int i = 0; i < drawOrder.length; i++) {
            drawOrder[i] = (short) i;
         }
      }

      // initialize vertex byte buffer for shape coordinates, 4 bytes per float
      vertexBuffer = ByteBuffer.allocateDirect(vertices.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
      vertexBuffer.put(vertices).position(0);

      // initialize byte buffer for the draw list, 2 bytes per short
      drawOrderBuffer = ByteBuffer.allocateDirect(drawOrder.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
      drawOrderBuffer.put(drawOrder).position(0);

      // getting references to the shader program
      mColorHandle = GLES20.glGetUniformLocation(Renderer.program, "vColor");
      mPositionHandle = GLES20.glGetAttribLocation(Renderer.program, "vPosition");
      mMVPMatrixHandle = GLES20.glGetUniformLocation(Renderer.program, "uMVPMatrix");
      Renderer.checkGlError("glGetUniformLocation");

      setup = true;
   }


   /**
    * draws the shape, will automatically setupDrawing
    * @param mvpMatrix - model view projection matrix
    */
   public void draw(float[] mvpMatrix) {

      if (!setup) {
         setupDrawing();
      }

      GLES20.glUseProgram(Renderer.program);

      // enabling a handle to the triangle vertices
      GLES20.glEnableVertexAttribArray(mPositionHandle);

      // preparing the triangle coordinate data
      GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 12, vertexBuffer);

      // set color for drawing the shape
      GLES20.glUniform4fv(mColorHandle, 1, color, 0);

      // apply the projection and view transformation
      GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
      Renderer.checkGlError("glUniformMatrix4fv");

      // eventually drawing
      GLES20.glDrawElements(GLES20.GL_LINE_STRIP, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, drawOrderBuffer);

      // disabling vertex array
      GLES20.glDisableVertexAttribArray(mPositionHandle);
   }

   protected void setColor(float[] color) {
      this.color = color;
   }

   protected void setVertices(float[] vertices) {
      this.vertices = vertices;
   }

   protected void setDrawOrder(short[] drawOrder) {
      this.drawOrder = drawOrder;
   }
}