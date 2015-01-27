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

public abstract class Shape {

   private FloatBuffer vertexBuffer;
   private ShortBuffer drawOrderBuffer;

   private int program;
   private int mPositionHandle;
   private int mColorHandle;
   private int mMVPMatrixHandle;

   private float[] vertexCoords;
   private short[] drawOrder;

   private float color[];


   public Shape(int program) {
      this.program = program;
   }


   /**
    * constructs buffers & makes references to shader program variables
    */
   public void setup() {

      // initialize vertex byte buffer for shape coordinates, 4 bytes per float
      vertexBuffer = ByteBuffer.allocateDirect(vertexCoords.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
      vertexBuffer.put(vertexCoords).position(0);

      // initialize byte buffer for the draw list, 2 bytes per short
      drawOrderBuffer = ByteBuffer.allocateDirect(drawOrder.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
      drawOrderBuffer.put(drawOrder).position(0);

      // getting references to the shader program
      mColorHandle = GLES20.glGetUniformLocation(program, "vColor");
      mPositionHandle = GLES20.glGetAttribLocation(program, "vPosition");
      mMVPMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
      Renderer.checkGlError("glGetUniformLocation");
   }


   public void draw(float[] mvpMatrix) {
      // Add program to OpenGL environment
      GLES20.glUseProgram(program);

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
      GLES20.glDrawElements(GLES20.GL_LINE_STRIP, drawOrder.length,GLES20.GL_UNSIGNED_SHORT, drawOrderBuffer);

      // disabling vertex array
      GLES20.glDisableVertexAttribArray(mPositionHandle);
   }



   public void setColor(float[] color) {
      this.color = color;
   }

   public void setVertexCoords(float[] vertexCoords) {
      this.vertexCoords = vertexCoords;
   }

   public void setDrawOrder(short[] drawOrder) {
      this.drawOrder = drawOrder;
   }
}