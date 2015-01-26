package uk.ac.aber.gij2.mmp.visualisation;

import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * Created by paranoia on 26/01/15.
 */
public class SurfaceView extends GLSurfaceView {

   public SurfaceView(Context context) {
      super(context);

      setRenderer(new uk.ac.aber.gij2.mmp.visualisation.Renderer(context));
      setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
   }
}
