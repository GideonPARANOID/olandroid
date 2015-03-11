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
//      scaleGestureDetector.onTouchEvent(event);

      // TODO: think about scaling in correspondence to the view (screen) size
      final float ROTATION_SCALE = 0.25f, TRANSLATION_SCALE = 0.125f;

      if (event.getAction() == MotionEvent.ACTION_DOWN) {

         // if starting a new swipe, reset the start to prevent skipping
         previousX = event.getX();
         previousY = event.getY();

      } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
         switch (event.getPointerCount()) {
            case 1:
               renderer.viewRotationYDelta((event.getX() - previousX) * ROTATION_SCALE);
               renderer.viewRotationXDelta((event.getY() - previousY) * ROTATION_SCALE);
               break;

            case 2:
               renderer.viewTranslationZDelta((event.getX() - previousX) * TRANSLATION_SCALE);
               renderer.viewTranslationXDelta((event.getY() - previousY) * TRANSLATION_SCALE);
               break;
         }

         previousX = event.getX();
         previousY = event.getY();
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
