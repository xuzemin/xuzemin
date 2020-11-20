package com.mphotool.whiteboard.view;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.util.AttributeSet;

import com.mphotool.whiteboard.elements.Image;
import com.mphotool.whiteboard.elements.Material;
import com.mphotool.whiteboard.elements.PencilInk;
import com.mphotool.whiteboard.elements.Selector;
import com.mphotool.whiteboard.utils.BaseUtils;

import java.util.HashMap;
import java.util.Iterator;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class BaseGLSurfaceView extends GLSurfaceView implements Renderer {
    static String TAG = "BaseGLSurfaceView";
    protected PanelManager mManager;
    private boolean mDeleteTextures = false;
    protected boolean mDrawShape = false;

    public BaseGLSurfaceView(Context context)
    {
        super(context);
        init();
    }

    public BaseGLSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    private void init()
    {
//        BaseUtils.dbg(TAG, "init");
        setEGLContextClientVersion(1);
//        setEGLConfigChooser(new MyConfigChooser());
        setEGLConfigChooser(8,8,8,8,16,0);
        getHolder().setFormat(-3);
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public void setBoardContent(PanelManager manager)
    {
        this.mManager = manager;
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig pEGLConfig)
    {
//        gl.glEnable(32925);
//        gl.glEnable(2848);
//        gl.glHint(2848, 4354);
//        gl.glEnable(3042);
//        gl.glBlendFunc(770, 771);
//        gl.glDisable(2929);
        BaseUtils.dbg(TAG, "onSurfaceCreated");
        gl.glEnable(GL10.GL_MULTISAMPLE);
        gl.glEnable(GL10.GL_LINE_SMOOTH);
        gl.glHint(GL10.GL_LINE_SMOOTH, GL10.GL_NICEST);
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        gl.glDisable(GL10.GL_DEPTH_TEST);
    }

    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
//        BaseUtils.dbg(TAG, "onSurfaceChanged");
        gl.glViewport(0, 0, width, height);

        gl.glMatrixMode(GL10.GL_PROJECTION);

        gl.glLoadIdentity();

        GL10 gl10 = gl;
        gl10.glOrthof(0.0f, (float) width, (float) (-height), 0.0f, -1.0f, 1.0f);

        gl.glScalef(1.0f, -1.0f, 1.0f);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

//    Lock drawLock;

    @Override public void onDrawFrame(GL10 gl)
    {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        Iterator i$;
        if (this.mDeleteTextures)
        {
            HashMap<String, Integer> texturesMap = PanelManager.getTextureReferenceMap();
            int[] textures = new int[texturesMap.size()];
            int index = 0;
            for (Integer textureId : texturesMap.values())
            {
                int index2 = index + 1;
                textures[index] = textureId.intValue();
                index = index2;
            }
            gl.glDeleteTextures(textures.length, textures, 0);
            texturesMap.clear();
            this.mDeleteTextures = false;
        }
        else if (this.mDrawShape)
        {
            page = this.mManager.getCurrentPage();

            synchronized (page.SHAPE_LOCK)
            {
                i$ = page.materialList.iterator();
                while (i$.hasNext())
                {
                    Material m = (Material) i$.next();
                    if (m instanceof PencilInk)
                    {
                        ((PencilInk) m).drawGLShape(gl);
                    }
                    if (m instanceof Image)
                    {
                        ((Image) m).drawGLShape(gl);
                    }
                    if(m instanceof Selector){
                        ((Selector) m).drawGL(gl);
                    }
                }
            }
//            this.mManager.getSelector().drawGL(gl);
        }
        else
        {
            page = this.mManager.getCurrentPage();
            synchronized (page.SHAPE_LOCK)
            {
                i$ = page.materialList.iterator();

                while (i$.hasNext())
                {
                    Material shape = (Material) i$.next();

                    if (shape instanceof Image)
                    {
                        ((Image) shape).generateTextureIdIfNecessary(gl);
                    }
                }
            }
        }
    }

    Page page;


    public void setDrawShape(boolean isDrawShape)
    {
        this.mDrawShape = isDrawShape;
    }

    public void deleteTextures()
    {
        this.mDeleteTextures = true;
        requestRender();
    }
}
