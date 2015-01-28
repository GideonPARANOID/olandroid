/**
 * @created 2015-01-26
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.mmp.visualisation;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;


public class SurfaceView extends GLSurfaceView {

   private uk.ac.aber.gij2.mmp.visualisation.Renderer renderer;

   private float previousX, previousY;

   public SurfaceView(Context context) {
      super(context);

      renderer = new uk.ac.aber.gij2.mmp.visualisation.Renderer(context);

      setEGLContextClientVersion(2);
      setRenderer(renderer);
   }


   @Override
   public boolean onTouchEvent(MotionEvent event) {

      float currentX = event.getX(), currentY = event.getY();

      switch (event.getAction()) {
         case MotionEvent.ACTION_MOVE:

            float deltaX = currentX - previousX, deltaY = currentY - previousY;

            //System.out.println("x: " + currentX + "|" + previousX + "  y: " + currentY + "|" + previousY + " delta:" + deltaX + "|" + deltaY );

            // inverting the movement on crossing the centre lines
            if (currentY > getHeight() / 2) {
               deltaX *= -1;
            }

            if (currentX > getWidth() / 2) {
               deltaY *= -1 ;
            }

            renderer.setViewX(renderer.getViewX() + deltaX);
            renderer.setViewY(renderer.getViewY() + deltaY);
      }

      previousX = currentX;
      previousY = currentY;

      return true;
   }
}
