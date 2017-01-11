package roommap.jdrd.android.com.myapplication.Activity;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import roommap.jdrd.android.com.myapplication.View.MyGLSurfaceView;


public class MainActivity extends Activity {
    private GLSurfaceView mGLView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGLView = new MyGLSurfaceView(this);
        setContentView(mGLView);
    }
}
