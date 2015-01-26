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
   private ShortBuffer drawListBuffer;
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
      ByteBuffer bb = ByteBuffer.allocateDirect(vertexCoords.length * 4);

      bb.order(ByteOrder.nativeOrder());
      vertexBuffer = bb.asFloatBuffer();
      vertexBuffer.put(vertexCoords);
      vertexBuffer.position(0);

      // initialize byte buffer for the draw list, 2 bytes per short
      ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);

      dlb.order(ByteOrder.nativeOrder());
      drawListBuffer = dlb.asShortBuffer();
      drawListBuffer.put(drawOrder);
      drawListBuffer.position(0);

      // getting references to the shader program
      mColorHandle = GLES20.glGetUniformLocation(program, "vColor");
      mPositionHandle = GLES20.glGetAttribLocation(program, "vPosition");
      mMVPMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
      Renderer.checkGlError("glGetUniformLocation");
   }


   public void draw(float[] mvpMatrix) {
      // Add program to OpenGL environment
      GLES20.glUseProgram(program);

      // get handle to vertex shader's vPosition member


      // enabling a handle to the triangle vertices
      GLES20.glEnableVertexAttribArray(mPositionHandle);

      // preparing the triangle coordinate data
      GLES20.glVertexAttribPointer(
         mPositionHandle, 3,
         GLES20.GL_FLOAT, false,
         12, vertexBuffer); // 3 coordinates per vertex, 4 bytes per int


      // set color for drawing the triangle
      GLES20.glUniform4fv(mColorHandle, 1, color, 0);


      // apply the projection and view transformation
      GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
      Renderer.checkGlError("glUniformMatrix4fv");

      // eventually drawing
         GLES20.glDrawElements(
         GLES20.GL_TRIANGLES, drawOrder.length,
         GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

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