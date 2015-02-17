/**
 * @created 2015-01-26
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.mmp.visualisation;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;


public class SurfaceView extends GLSurfaceView {

   private uk.ac.aber.gij2.mmp.visualisation.Renderer renderer;

   private float previousX, previousY;
   private ScaleGestureDetector scaleGestureDetector;


   public SurfaceView(Context context) {
      super(context);

      renderer = new uk.ac.aber.gij2.mmp.visualisation.Renderer(context);

      setEGLContextClientVersion(2);
      setRenderer(renderer);

      // setting up pinch listening
      scaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.OnScaleGestureListener() {

         @Override
         public void onScaleEnd(ScaleGestureDetector detector) {
         }

         @Override
         public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
         }

         @Override
         public boolean onScale(ScaleGestureDetector detector) {

            // rough
            renderer.scaleViewZoom(detector.getScaleFactor());
            return false;
         }
      });
   }


   @Override
   public boolean onTouchEvent(@NonNull MotionEvent event) {
      scaleGestureDetector.onTouchEvent(event);

      if (event.getPointerCount() == 1) {

         float currentX = event.getX(), currentY = event.getY();

         if (event.getAction() == MotionEvent.ACTION_MOVE) {

            float deltaX = currentX - previousX, deltaY = currentY - previousY;

            // inverting the movement on crossing the centre lines
            if (currentY > getHeight() / 2) {
               deltaX *= -1;
            }

            if (currentX > getWidth() / 2) {
               deltaY *= -1;
            }

            renderer.setViewX(renderer.getViewX() + deltaX);
            renderer.setViewY(renderer.getViewY() + deltaY);
         }


         previousX = currentX;
         previousY = currentY;

      }

      return true;
   }
}
