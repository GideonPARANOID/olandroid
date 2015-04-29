/**
 * @created 2015-01-28
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.olandroid.model;

import android.opengl.Matrix;

import uk.ac.aber.gij2.olandroid.view.AnimationStyle;
import uk.ac.aber.gij2.olandroid.view.Drawable;


public class Component implements Drawable.FlightPiece {

   private final float ANGLE = 1f / 24f;
   private Bound pitch, yaw, roll;
   private float length;
   private float[] matrix, vertices, cached;

   /**
    * @param pitch  - vertical amount
    * @param yaw    - horizontal amount
    * @param roll   - roll amount
    * @param length - length of the component
    */
   public Component(Bound pitch, Bound yaw, Bound roll, float length) {
      this.pitch = pitch;
      this.yaw = yaw;
      this.roll = roll;
      this.length = length;

      buildVertices();
      buildMatrix();
   }


   /**
    * copy constructor
    * @param component - instance of component to copy
    */
   public Component(Component component) {
      this(component.pitch, component.yaw, component.roll, component.length);
   }

   /**
    * builds the vertices array for a buffer
    */
   protected void buildVertices() {
      float x = 0f, y = 0f, z = length, xOffset = WIDTH, yOffset = 0, zOffset = 0;

      if (pitch != Bound.ZERO || yaw != Bound.ZERO) {
         // first & second triangles of euler
         double theta = ANGLE * Math.PI * 2, phi =
            Math.atan((double) pitch.getValue() / (double) yaw.getValue());

         // does not like dividing by minus one, mathematically works but not programmatically
         if (yaw == Bound.MIN) {
            phi = Math.PI + phi;
         }

         x = (float) (length * Math.sin(theta) * Math.cos(phi));
         y = (float) (length * Math.sin(theta) * Math.sin(phi));
         z = (float) (length * Math.cos(theta));
      }

      // building the difference for a roll
      if (roll != Bound.ZERO) {
         xOffset = (float) (WIDTH * Math.cos(roll.getValue() * ANGLE * Math.PI * 2f));
         yOffset = (float) (WIDTH * Math.sin(roll.getValue() * ANGLE * Math.PI * 2f));
      }

      if (yaw != Bound.ZERO) {
         zOffset = (float) -(WIDTH * Math.sin(yaw.getValue() * ANGLE * Math.PI * 2f));
      }

      cached = new float[]{
         WIDTH, 0f, 0f,
         -WIDTH, 0f, 0f,
         x - xOffset, y - yOffset, z - zOffset,
         x + xOffset, y + yOffset, z + zOffset
      };

      vertices = cached;
   }

   /**
    * builds the matrix which describes the transform from the beginning of the component to its end
    */
   protected void buildMatrix() {
      float[] mPitch = new float[16], mYaw = new float[16], mRoll = new float[16],
         mTemp = new float[16];

      // matrix library uses degrees instead of radians, heaven knows why
      float factor = 360f * ANGLE;

      matrix = new float[16];
      Matrix.setIdentityM(matrix, 0);
      Matrix.setIdentityM(mPitch, 0);
      Matrix.setIdentityM(mYaw, 0);
      Matrix.setIdentityM(mRoll, 0);

      if (pitch != Bound.ZERO) {
         Matrix.setRotateM(mPitch, 0, factor, (float) -pitch.getValue(), 0f, 0f);
      }

      if (yaw != Bound.ZERO) {
         Matrix.setRotateM(mYaw, 0, factor, 0f, (float) yaw.getValue(), 0f);
      }

      if (roll != Bound.ZERO) {
         Matrix.setRotateM(mRoll, 0, factor, 0f, 0f, (float) roll.getValue());
      }

      // combining the euler
      Matrix.multiplyMM(mTemp, 0, mPitch, 0, mYaw, 0);
      Matrix.multiplyMM(matrix, 0, mTemp, 0, mRoll, 0);
      Matrix.translateM(matrix, 0, 0f, 0f, length);
   }

   public float[] getCompleteMatrix() {
      return matrix;
   }

   public void animate(float progressPre, float progressPost, AnimationStyle style) {

      if (progressPre == 0f && progressPost == 0f) {
         vertices = new float[12];

      } else if (progressPre == 0f && progressPost == 1f) {
         vertices = cached;

      } else {
         vertices = cached;

         // correcting
         if (length < 0) {
            progressPost += 2;
         }

         // extending the y & z distance (not x, as that's width)
         vertices = new float[]{
            WIDTH, vertices[7] * progressPre, vertices[8] * progressPre,
            -WIDTH, vertices[10] * progressPre, vertices[11] * progressPre,
            vertices[6], vertices[7] * progressPost, vertices[8] * progressPost,
            vertices[9], vertices[10] * progressPost, vertices[11] * progressPost
         };
      }
   }

   public void draw(float[] initialMatrix) {
   }

   public float getLength() {
      return length;
   }

   public void setLength(float length) {
      this.length = length;
      buildVertices();
      buildMatrix();
   }

   public float[] getVertices() {
      return vertices;
   }


   // bounds for movement
   public enum Bound {
      MIN(-1), ZERO(0), MAX(1);

      private final int value;

      Bound(int value) {
         this.value = value;
      }

      public static Bound parse(String bound) {
         switch (bound) {
            case "MAX":
               return MAX;
            case "ZERO":
               return ZERO;
            case "MIN":
               return MIN;
            default:
               return ZERO;
         }
      }

      public int getValue() {
         return value;
      }
   }
}
