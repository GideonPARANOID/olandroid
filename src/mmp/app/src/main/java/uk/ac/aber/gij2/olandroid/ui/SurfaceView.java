/**
 * @created 2015-01-26
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.olandroid.ui;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;


public class SurfaceView extends GLSurfaceView implements
   ScaleGestureDetector.OnScaleGestureListener {

   private uk.ac.aber.gij2.olandroid.visualisation.Renderer renderer;

   private float previousX, previousY;
   private ScaleGestureDetector scaleGestureDetector;


   public SurfaceView(Context context, AttributeSet attributes) {
      super(context);
      super.setEGLConfigChooser(8 , 8, 8, 8, 16, 0);

      renderer = new uk.ac.aber.gij2.olandroid.visualisation.Renderer(context);

      setEGLContextClientVersion(2);
      setRenderer(renderer);

      // setting up pinch listening
      scaleGestureDetector = new ScaleGestureDetector(context, this);
   }


   @Override
   public boolean onTouchEvent(@NonNull MotionEvent event) {
      scaleGestureDetector.onTouchEvent(event);


      if (event.getAction() == MotionEvent.ACTION_MOVE) {
         switch (event.getPointerCount()) {

            case 1:
               float currentX = event.getX(), currentY = event.getY(),
                  deltaX = currentX - previousX, deltaY = currentY - previousY;

               // inverting the movement on crossing the centre lines
               if (currentY > getHeight() / 2) {
                  deltaX = -deltaX;
               }

               if (currentX > getWidth() / 2) {
                  deltaY = -deltaY;
               }

               renderer.setViewX(renderer.getViewX() + (deltaX * 0.5f));

               float resultY = renderer.getViewY() + (deltaY * 0.5f);
               if (resultY >= 0 && resultY <= 90) {
                  renderer.setViewY(resultY);
               }

               previousX = currentX;
               previousY = currentY;
               break;

            case 2:


               System.out.println(event.getX() + " " + event.getY());
               break;
         }
      }

      return true;
   }


   @Override
   public void onScaleEnd(ScaleGestureDetector detector) {}


   @Override
   public boolean onScaleBegin(ScaleGestureDetector detector) {
      return true;
   }


   @Override
   public boolean onScale(ScaleGestureDetector detector) {

      float factor = detector.getScaleFactor(), previousViewZoom = renderer.getViewZoom();

      if (previousViewZoom / factor >= 0.2f) {
         renderer.setViewZoom(previousViewZoom / ((factor * 0.1f) + 0.9f));
      }

      return false;
   }
}