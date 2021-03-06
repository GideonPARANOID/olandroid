/**
 * @created 2015-01-26
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.olandroid.view;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;


public abstract class Shape implements Drawable {

   // drawing options, whether things are setup & whether to fill draw or just draw lines
   protected enum Style {
      FILL, LINES
   }

   private FloatBuffer vertexBuffer, textureCoordsBuffer;
   private ShortBuffer drawOrderBuffer;
   private float[] colourFront, colourBack;

   // handles, including texture resource handle
   private int points, texture, textureId, mPositionHandle, mColourHandle, mMVPMatrixHandle,
      mUseTextureHandle, mTextureCoordsHandle, mTextureHandle;

   private boolean useTexture;

   private final Style drawMode;

   // whether to draw or not, a way of skipping emptying buffers
   private boolean draw;
   protected boolean drawingSetup;


   /**
    * @param drawMode - what draw mode to use - filled or wireframe
    */
   protected Shape(Style drawMode) {
      this.drawMode = drawMode;
      draw = true;
      drawingSetup = false;

      useTexture = false;
   }


   /**
    * constructs buffers & makes references to shader program variables, needs vertex & draw orders
    *    already initialised
    */
   protected final void setupDrawing() {

      if (drawOrderBuffer == null || vertexBuffer == null) {
         throw new NullPointerException("buffers uninitialised");
      }

      // gets the necessary references to variables in the shader
      mColourHandle = GLES20.glGetUniformLocation(Renderer.program, "vColour");
      mPositionHandle = GLES20.glGetAttribLocation(Renderer.program, "vPosition");
      mMVPMatrixHandle = GLES20.glGetUniformLocation(Renderer.program, "uMVPMatrix");
      mUseTextureHandle = GLES20.glGetUniformLocation(Renderer.program, "uUseTexture");
      mTextureHandle = GLES20.glGetUniformLocation(Renderer.program, "uTexture");
      mTextureCoordsHandle = GLES20.glGetAttribLocation(Renderer.program, "aTextureCoords");

      if (useTexture) {
         texture = Renderer.textures[textureId];
      }

      Renderer.checkGlError("glGetUniformLocation");

      drawingSetup = true;
   }


   /**
    * draws the shape, will automatically setupDrawing
    * @param mvpMatrix - model view projection matrix
    */
   public void draw(float[] mvpMatrix) {

      if (!drawingSetup) {
         setupDrawing();
      }

      if (draw) {
         GLES20.glUseProgram(Renderer.program);

         // enabling a handle to the triangle vertices
         GLES20.glEnableVertexAttribArray(mPositionHandle);

         // preparing the triangle coordinate data
         GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 12, vertexBuffer);

         // apply the projection and view transformation
         GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
         Renderer.checkGlError("glUniformMatrix4fv");

         switch (drawMode) {
            case FILL:
               if (useTexture) {
                  GLES20.glUniform1i(mUseTextureHandle, 1);

                  GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                  GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);

                  // giving the sampler the right texture id
                  GLES20.glUniform1i(mTextureHandle, 0);

                  GLES20.glVertexAttribPointer(mTextureCoordsHandle, 2, GLES20.GL_FLOAT, false, 0,
                     textureCoordsBuffer);

                  GLES20.glEnableVertexAttribArray(mTextureCoordsHandle);

                  GLES20.glDrawElements(GLES20.GL_TRIANGLES, points, GLES20.GL_UNSIGNED_SHORT,
                     drawOrderBuffer);

               } else {
                  GLES20.glUniform1i(mUseTextureHandle, 0);

                  // painting the front (bottom)
                  GLES20.glCullFace(GLES20.GL_FRONT);
                  GLES20.glUniform4fv(mColourHandle, 1, colourFront, 0);
                  GLES20.glDrawElements(GLES20.GL_TRIANGLES, points, GLES20.GL_UNSIGNED_SHORT,
                     drawOrderBuffer);

                  // painting the back (top)
                  GLES20.glCullFace(GLES20.GL_BACK);
                  GLES20.glUniform4fv(mColourHandle, 1, colourBack, 0);
                  GLES20.glDrawElements(GLES20.GL_TRIANGLES, points, GLES20.GL_UNSIGNED_SHORT,
                     drawOrderBuffer);
               }

               break;

            case LINES:
               GLES20.glUniform4fv(mColourHandle, 1, colourFront, 0);
               GLES20.glDrawElements(GLES20.GL_LINE_STRIP, points, GLES20.GL_UNSIGNED_SHORT,
                  drawOrderBuffer);
               break;
         }

         // disabling vertex array
         GLES20.glDisableVertexAttribArray(mPositionHandle);
      }
   }


   /**
    * @param vertices - vertices of the shape
    */
   protected final void buildVerticesBuffer(float[] vertices) {
      if (vertices == null) {
         draw = false;

      } else {
         draw = true;

         // initialise vertex byte buffer for shape coordinates, 4 bytes per float
         vertexBuffer = ByteBuffer.allocateDirect(vertices.length * 4).order(
            ByteOrder.nativeOrder()).asFloatBuffer();
         vertexBuffer.put(vertices).position(0);
      }
   }


   /**
    * @param drawOrder - order of drawing of the shape
    */
   protected final void buildDrawOrderBuffer(short[] drawOrder) {
      points = drawOrder.length;

      // initialise byte buffer for the draw list, 2 bytes per short
      drawOrderBuffer = ByteBuffer.allocateDirect(drawOrder.length * 2).order(
         ByteOrder.nativeOrder()).asShortBuffer();
      drawOrderBuffer.put(drawOrder).position(0);
   }

   /**
    * @param textureCoords - texture coords
    */
   protected final void buildTextureCoordsBuffer(float[] textureCoords) {

      // initialise texture coordinates byte buffer for shape coordinates, 4 bytes per float
      textureCoordsBuffer = ByteBuffer.allocateDirect(textureCoords.length * 4).order(
         ByteOrder.nativeOrder()).asFloatBuffer();
      textureCoordsBuffer.put(textureCoords).position(0);
   }


   /**
    * @param colourFront - a colour array for the front of the visualisation
    */
   public final void setColourFront(float[] colourFront) {
      this.colourFront = colourFront;
   }

   /**
    *
    * @param colourBack - a colour array for the back of the visualisation
    */
   public final void setColourBack(float[] colourBack) {
      this.colourBack = colourBack;
   }


   /**
    * @param textureId - a loaded texture resource id (from the renderer)
    */
   public final void setTextureId(int textureId) {
      this.textureId = textureId;
      useTexture = true;
   }
}