/**
 * @created 2015-01-26
 * @author gideon mw jones.
 */

package uk.ac.aber.gij2.mmp.visualisation;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;


public class SurfaceView extends GLSurfaceView {

   public SurfaceView(Context context) {
      super(context);

      setEGLContextClientVersion(2);
      setRenderer(new uk.ac.aber.gij2.mmp.visualisation.Renderer(context));
      setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
   }


   @Override
   public boolean onTouchEvent(MotionEvent event) {

      switch (event.getAction()) {

         default:
            requestRender();
            return true;
      }
   }
}
