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

   private int mPositionHandle, mColourHandle, mMVPMatrixHandle;

   private float[] vertices, colourFront, colourBack;
   private short[] drawOrder;

   // drawing options, whether things are setup & whether to fill draw or just draw lines
   protected boolean drawingSetup;
   protected static int FILL = 0, LINES = 1;
   private int drawMode;


   public Shape() {
      drawingSetup = false;
      drawMode = FILL;
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

      buildBuffers();
      getReferences();

      drawingSetup = true;
   }


   /**
    * constructs the buffers for drawing
    */
   protected void buildBuffers() {
      // initialise vertex byte buffer for shape coordinates, 4 bytes per float
      vertexBuffer = ByteBuffer.allocateDirect(vertices.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
      vertexBuffer.put(vertices).position(0);

      // initialise byte buffer for the draw list, 2 bytes per short
      drawOrderBuffer = ByteBuffer.allocateDirect(drawOrder.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
      drawOrderBuffer.put(drawOrder).position(0);
   }


   /**
    * gets the necessary references to variables in the shader
    */
   protected void getReferences() {
      mColourHandle = GLES20.glGetUniformLocation(Renderer.program, "vColour");
      mPositionHandle = GLES20.glGetAttribLocation(Renderer.program, "vPosition");
      mMVPMatrixHandle = GLES20.glGetUniformLocation(Renderer.program, "uMVPMatrix");

      Renderer.checkGlError("glGetUniformLocation");
   }

   /**
    * draws the shape, will automatically setupDrawing
    * @param mvpMatrix - model view projection matrix
    */
   public void draw(float[] mvpMatrix) {

      if (!drawingSetup) {
         setupDrawing();
      }

      GLES20.glUseProgram(Renderer.program);

      // enabling a handle to the triangle vertices
      GLES20.glEnableVertexAttribArray(mPositionHandle);

      // preparing the triangle coordinate data
      GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 12, vertexBuffer);

      // apply the projection and view transformation
      GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
      Renderer.checkGlError("glUniformMatrix4fv");

      if (drawMode == FILL) {
         // painting the front (bottom)
         GLES20.glCullFace(GLES20.GL_FRONT);
         GLES20.glUniform4fv(mColourHandle, 1, colourFront, 0);
         GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, drawOrderBuffer);

         // painting the back (top)
         GLES20.glCullFace(GLES20.GL_BACK);
         GLES20.glUniform4fv(mColourHandle, 1, colourBack, 0);
         GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, drawOrderBuffer);

      } else {
         GLES20.glUniform4fv(mColourHandle, 1, colourFront, 0);
         GLES20.glDrawElements(GLES20.GL_LINE_STRIP, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, drawOrderBuffer);
      }


      // disabling vertex array
      GLES20.glDisableVertexAttribArray(mPositionHandle);
   }


   protected void setVertices(float[] vertices) {
      this.vertices = vertices;
   }

   protected void setDrawOrder(short[] drawOrder) {
      this.drawOrder = drawOrder;
   }

   public void setColourFront(float[] colourFront) {
      this.colourFront = colourFront;
   }

   public void setColourBack(float[] colourBack) {
      this.colourBack = colourBack;
   }

   public void setDrawMode(int drawMode) {
      this.drawMode = drawMode;
   }
}