package com.mphotool.whiteboard.elements;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.mphotool.whiteboard.BuildConfig;
import com.mphotool.whiteboard.activity.WhiteBoardApplication;
import com.mphotool.whiteboard.entity.SerializableMatrix;
import com.mphotool.whiteboard.utils.BaseUtils;
import com.mphotool.whiteboard.utils.BitmapUtils;
import com.mphotool.whiteboard.utils.Constants;
import com.mphotool.whiteboard.utils.PanelUtils;
import com.mphotool.whiteboard.utils.ToofifiLog;
import com.mphotool.whiteboard.view.PanelManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.UUID;

import javax.microedition.khronos.opengles.GL10;

public class Image extends Material {

    public static final int PICTURE_SELECTED_COLOR = -16776961;
    private static final float COLOR_B = ((float) Color.blue(PICTURE_SELECTED_COLOR));
    private static final float COLOR_G = ((float) Color.green(PICTURE_SELECTED_COLOR));
    private static final float COLOR_R = ((float) Color.red(PICTURE_SELECTED_COLOR));

    String TAG = "Image";

    private static final long serialVersionUID = Constants.serialVersionUID;
    protected transient String _mId = UUID.randomUUID().toString();
    private static int mSelectedEdgeColor = -49920;
    private Bitmap mBitmap = null;
    private String mFilePath;
    private transient Paint mPaint;
    private Rect mRect = new Rect();

    protected SerializableMatrix mSelfMatrix = new SerializableMatrix();


    private FloatBuffer mSelectRectVertexBuffer;
    private final Object BITMAP_LOCK = new Object();
    private static final Object VERTEX_LOCK = new Object();
    private int mImageTextureId = -1;
    public boolean mNeedRebindTexture = false;
    private boolean mIsOpenGLEnabled = BuildConfig.enable_opengl;
    private FloatBuffer mSquareTextureCoordsBuffer;
    private FloatBuffer mVerticesBuffer;
    private Rect mWindowRect = new Rect(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);

    public static class Datas implements Serializable {
        public byte[] bitmap = null;
        public int height;
        public String path = null;
        public int width;
    }

    public static void setSelectedEdgeColor(int color)
    {
        mSelectedEdgeColor = color;
    }

    public static int getSelectedEdgeColor()
    {
        return mSelectedEdgeColor;
    }

    public boolean writeObject(OutputStream out) throws IOException
    {
        super.writeObject(out);
        Datas datas = new Datas();
        datas.path = this.mFilePath;
        if (this.mBitmap != null)
        {
            datas.width = this.mBitmap.getWidth();
            datas.height = this.mBitmap.getHeight();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            this.mBitmap.compress(CompressFormat.WEBP, 100, baos);
            byte[] bytes = baos.toByteArray();
            out.write(BaseUtils.intToByteArray(bytes.length));
            out.write(bytes);
        }
        out.write(BaseUtils.intToByteArray(datas.width));
        out.write(BaseUtils.intToByteArray(datas.height));
        mSelfMatrix.writeObject(out);
        return true;
    }

    public boolean readObject(InputStream in) throws IOException, ClassNotFoundException
    {
        super.readObject(in);
        byte[] buf = new byte[4];
        Datas datas = new Datas();
        datas.bitmap = new byte[BaseUtils.readInputStreamInt(in, buf)];
        in.read(datas.bitmap);
        datas.width = BaseUtils.readInputStreamInt(in, buf);
        datas.height = BaseUtils.readInputStreamInt(in, buf);
        if (datas.bitmap != null)
        {
            mBitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(datas.bitmap));
        }
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        if (this.mBitmap != null)
        {
            this.mRect.set(0, 0, this.mBitmap.getWidth(), this.mBitmap.getHeight());
        }
        mSelfMatrix.readObject(in);
        return true;
    }

    public Image(PanelManager manager, String filePath, float x, float y, float width, float height)
    {
        mManager = manager;
        this.mFilePath = filePath;
        try
        {
            this.mBitmap = BitmapUtils.getBitmapFromFile(filePath, (int) width, (int) height);
        }
        catch (OutOfMemoryError e)
        {
            this.mBitmap = null;
        }
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        if (this.mBitmap != null)
        {
            int dwidth = this.mBitmap.getWidth();
            int dheight = this.mBitmap.getHeight();
            if (width != 0.0f && width < ((float) dwidth))
            {
                dwidth = (int) width;
            }
            if (height != 0.0f && height < ((float) dheight))
            {
                dheight = (int) height;
            }
            this.mRect.set(0, 0, dwidth, dheight);
        }
        mSelfMatrix.setTranslate(x, y);
        initGL();
        updateImageVertex(getActualRectF());
    }

    public Image(PanelManager manager, Bitmap bitmap, float x, float y)
    {
        mManager = manager;
        this.mBitmap = bitmap;
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        if (this.mBitmap != null)
        {
            this.mRect.set(0, 0, this.mBitmap.getWidth(), this.mBitmap.getHeight());
            mSelfMatrix.setTranslate(x, y);
        }
        initGL();
        updateImageVertex(getActualRectF());
    }

    public Image()
    {
        initGL();
    }

    private void initGL()
    {
        mIsOpenGLEnabled = BuildConfig.enable_opengl;
        if (this.mIsOpenGLEnabled)
        {
            this.mSquareTextureCoordsBuffer = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder()).asFloatBuffer();
            this.mSquareTextureCoordsBuffer.position(0);
            this.mSquareTextureCoordsBuffer.put(new float[]{0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f});
            this.mSquareTextureCoordsBuffer.position(0);
            this.mVerticesBuffer = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder()).asFloatBuffer();
            this.mVerticesBuffer.position(0);
            this.mSelectRectVertexBuffer = ByteBuffer.allocateDirect(40).order(ByteOrder.nativeOrder()).asFloatBuffer();
            this.mSelectRectVertexBuffer.position(0);
        }
    }

    public Image cropImage(RectF preRectF, RectF rectf)
    {
        float spanX = rectf.left - preRectF.left;
        float spanY = rectf.top - preRectF.top;
        int x = (int) (spanX);
        int y = (int) (spanY);
        int width = (int) (rectf.width());
        int height = (int) (rectf.height());

//        BaseUtils.dbg("ImageEditView", "cropImage: x=" + x + ", y=" + y + ", width=" + width + ", height=" + height);
//
//        BaseUtils.dbg("ImageEditView", "cropImage--- bimap info : width=" + mBitmap.getWidth() + ", height=" + mBitmap.getHeight());
        int bitmapWidth = mBitmap.getWidth();
        if (x + width >= bitmapWidth)
        {
            width = bitmapWidth - x;
        }
        if (width % 2 != 0)
        {
            width--;
        }
        int bitmapHeight = this.mBitmap.getHeight();
        if (y + height >= bitmapHeight)
        {
            height = bitmapHeight - y;
        }
        if (x < 0)
        {
            x = 0;
        }
        if (y < 0)
        {
            y = 0;
        }
        if (width <= 0 || height <= 0)
        {
            return null;
        }
        Bitmap newBitmap = null;
        if (!(x == 0 && y == 0 && width == this.mBitmap.getWidth() && height == this.mBitmap.getHeight()))
        {
//            BaseUtils.dbg("ImageEditView", "cropImage -- createBitmap");
            newBitmap = Bitmap.createBitmap(this.mBitmap, x, y, width, height);
        }
        if (newBitmap != null)
        {
//            this.mRect.set(0, 0, newBitmap.getWidth(), newBitmap.getHeight());
            Image newImage = new Image(mManager, newBitmap, rectf.left, rectf.top);
            newImage.mNeedRebindTexture = true;
            return newImage;
        }
        else
        {
            return null;
        }


    }

    public int getWidth()
    {
        if (this.mBitmap == null || this.mBitmap.isRecycled())
        {
            return 0;
        }
        return this.mBitmap.getWidth();
    }

    public int getHeight()
    {
        if (this.mBitmap == null || this.mBitmap.isRecycled())
        {
            return 0;
        }
        return this.mBitmap.getHeight();
    }

    public boolean isBitmapReady()
    {
        return this.mBitmap != null;
    }

    public boolean reloadBitmap()
    {
        if (this.mBitmap != null)
        {
            this.mBitmap.recycle();
        }
        this.mBitmap = BitmapUtils.getBitmapFromFile(this.mFilePath);
        return this.mBitmap != null;
    }

    @Override public void draw(Canvas canvas)
    {
        if (this.mBitmap == null)
        {
            ToofifiLog.e(Constants.TAG, "no bitmap for : " + this.mFilePath);
            return;
        }
        Matrix m2 = new Matrix();
        Matrix matrix = new Matrix();
        m2.reset();
        matrix.postConcat(mSelfMatrix);

        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, 3));
        canvas.drawBitmap(this.mBitmap, matrix, this.mPaint);

        if (isSelected())
        {
            DashPathEffect effect = new DashPathEffect(new float[]{4.0f, 4.0f, 4.0f, 4.0f}, 1.0f);
            Paint pe = new Paint(this.mPaint);
            pe.setStrokeWidth(Constants.BORDER_WIDTH_MEDIUM);
            pe.setStyle(Style.STROKE);
            pe.setStrokeCap(Cap.ROUND);
            pe.setColor(getSelectedEdgeColor());
            pe.setPathEffect(effect);
            Rect rect = new Rect();
            RectF r1 = new RectF(0.0f, 0.0f, (float) this.mBitmap.getWidth(), (float) this.mBitmap.getHeight());
            RectF r2 = new RectF();
            matrix.mapRect(r2, r1);
            PanelUtils.rectFtoRect(r2, rect, 0);
            effect = new DashPathEffect(new float[]{4.0f, 4.0f, 4.0f, 4.0f}, 1.0f);
            pe.setColor(getSelectedEdgeColor());
            pe.setPathEffect(effect);
            canvas.drawRect(rect, pe);
        }
    }

    public void drawGLShape(GL10 gl)
    {
        boolean onlyRebind = false;
        Rect bounds = getActualRect();
        if (this.mWindowRect.contains(bounds) || bounds.intersect(this.mWindowRect))
        {
            synchronized (VERTEX_LOCK)
            {
                if (this.mVerticesBuffer != null)
                {
                    if (this.mImageTextureId <= 0 && !GLES20.glIsTexture(this.mImageTextureId))
                    {
                        generateTextureId(gl, false);
                    }
                    else if (this.mNeedRebindTexture)
                    {
                        HashMap textureReferenceMap = PanelManager.getTextureReferenceMap();
                        textureReferenceMap.remove(this._mId);
                        if (!textureReferenceMap.containsValue(Integer.valueOf(this.mImageTextureId)))
                        {
                            onlyRebind = true;
                        }
                        generateTextureId(gl, onlyRebind);
                        this.mNeedRebindTexture = false;
                    }
                    if (this.mImageTextureId > 0)
                    {
                        gl.glEnable(3553);
                        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                        gl.glEnableClientState(32884);
                        this.mVerticesBuffer.position(0);
                        gl.glVertexPointer(2, 5126, 0, this.mVerticesBuffer);
                        gl.glEnableClientState(32888);
                        gl.glTexCoordPointer(2, 5126, 0, this.mSquareTextureCoordsBuffer);
                        gl.glBindTexture(3553, this.mImageTextureId);
                        gl.glDrawArrays(6, 0, 4);
                        gl.glDisableClientState(32884);
                        gl.glDisableClientState(32888);
                        gl.glBindTexture(3553, 0);
                        if (isSelected())
                        {
                            Rect selectedRect = getActualRect();
                            gl.glEnableClientState(32884);
                            gl.glLineWidth(0.8f);
                            gl.glColor4f(COLOR_R, COLOR_G, COLOR_B, 1.0f);
                            this.mSelectRectVertexBuffer.clear();
                            this.mSelectRectVertexBuffer.position(0);
                            this.mSelectRectVertexBuffer.put((float) selectedRect.left);
                            this.mSelectRectVertexBuffer.put((float) selectedRect.top);
                            this.mSelectRectVertexBuffer.put((float) selectedRect.right);
                            this.mSelectRectVertexBuffer.put((float) selectedRect.top);
                            this.mSelectRectVertexBuffer.put((float) selectedRect.right);
                            this.mSelectRectVertexBuffer.put((float) selectedRect.bottom);
                            this.mSelectRectVertexBuffer.put((float) selectedRect.left);
                            this.mSelectRectVertexBuffer.put((float) selectedRect.bottom);
                            this.mSelectRectVertexBuffer.put((float) selectedRect.left);
                            this.mSelectRectVertexBuffer.put((float) selectedRect.top);
                            this.mSelectRectVertexBuffer.position(0);
                            gl.glVertexPointer(2, 5126, 0, this.mSelectRectVertexBuffer);
                            gl.glDrawArrays(3, 0, 5);
                            gl.glDisableClientState(32884);
                        }
                    }
                }
            }
        }
    }

    private void generateTextureId(GL10 gl, boolean isRebind)
    {
        if (gl != null)
        {
            if (!isRebind)
            {
                int[] textures = new int[1];
                /**产生纹理名称*/
                gl.glGenTextures(1, textures, 0);
                this.mImageTextureId = textures[0];
                HashMap textureReferenceMap = PanelManager.getTextureReferenceMap();
                textureReferenceMap.remove(this._mId);
                textureReferenceMap.put(this._mId, Integer.valueOf(this.mImageTextureId));
            }
            gl.glBindTexture(GL10.GL_TEXTURE_2D, this.mImageTextureId);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, 10241, 9729.0f);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, 10240, 9729.0f);
            synchronized (this.BITMAP_LOCK)
            {
                if (!(this.mBitmap == null || this.mBitmap.isRecycled()))
                {
                    /**此处绘制bitmap*/
                    GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, this.mBitmap, 0);
                }
            }
        }
    }

    public void generateTextureIdIfNecessary(GL10 gl)
    {
        boolean onlyRebind = false;
        synchronized (VERTEX_LOCK)
        {
            if (this.mVerticesBuffer != null)
            {
                if (this.mImageTextureId <= 0 && !GLES20.glIsTexture(this.mImageTextureId))
                {
                    generateTextureId(gl, false);
                }
                else if (this.mNeedRebindTexture)
                {
                    HashMap textureReferenceMap = PanelManager.getTextureReferenceMap();
                    textureReferenceMap.remove(this._mId);
                    if (!textureReferenceMap.containsValue(Integer.valueOf(this.mImageTextureId)))
                    {
                        onlyRebind = true;
                    }
                    generateTextureId(gl, onlyRebind);
                    this.mNeedRebindTexture = false;
                }
            }
        }
    }

    public void updateImageVertex(RectF pRect)
    {
        synchronized (VERTEX_LOCK)
        {
            if (this.mVerticesBuffer != null)
            {
                this.mVerticesBuffer.clear();
                this.mVerticesBuffer.put(pRect.left);
                this.mVerticesBuffer.put(pRect.top);
                this.mVerticesBuffer.put(pRect.left);
                this.mVerticesBuffer.put(pRect.bottom);
                this.mVerticesBuffer.put(pRect.right);
                this.mVerticesBuffer.put(pRect.bottom);
                this.mVerticesBuffer.put(pRect.right);
                this.mVerticesBuffer.put(pRect.top);
                this.mVerticesBuffer.position(0);
            }
        }
    }

    public boolean intersect(Rect dst)
    {
        boolean isIntersect = PanelUtils.isRectImpact(dst, getActualRect());
        return isIntersect;
    }

    public boolean isInRegion(PanelManager manager, Region region)
    {
        Rect r = getActualRect();
        if (region.contains(r.left, r.top))
        {
            return true;
        }
        if (region.contains(r.right, r.top))
        {
            return true;
        }
        if (region.contains(r.right, r.bottom))
        {
            return true;
        }
        if (region.contains(r.left, r.bottom))
        {
            return true;
        }
        return false;
    }

    @Override public void continueTransform(Matrix matrix)
    {
        mSelfMatrix.postConcat(matrix);
        updateImageVertex(getActualRectF());
    }

    @Override public void endTransform(Matrix matrix)
    {

    }

    public Rect getActualRect()
    {
        return PanelUtils.rectFtoRect(getActualRectF(), new Rect(), 2);
    }

    public RectF getActualRectF()
    {
        RectF rf = new RectF();
        Matrix matrix = new Matrix();
        Matrix m2 = new Matrix();
        m2.reset();
        matrix.postConcat(mSelfMatrix);
        PanelUtils.rectToRectF(new Rect(mRect), rf, 0.0f);
        matrix.mapRect(rf);
        return rf;
    }

    public boolean isCross(float x1, float y1, float x2, float y2)
    {
        RectF rf = getActualRectF();
        boolean cross = PanelUtils.isIntersectLine(x1, y1, x2, y2, rf.left, rf.top, rf.left, rf.bottom);
        if (cross)
        {
            return cross;
        }
        cross = PanelUtils.isIntersectLine(x1, y1, x2, y2, rf.left, rf.top, rf.right, rf.top);
        if (cross)
        {
            return cross;
        }
        cross = PanelUtils.isIntersectLine(x1, y1, x2, y2, rf.right, rf.top, rf.right, rf.bottom);
        if (cross)
        {
            return cross;
        }
        return PanelUtils.isIntersectLine(x1, y1, x2, y2, rf.left, rf.bottom, rf.right, rf.bottom);
    }

    public Rect rect()
    {
        return getActualRect();
    }

    @Override
    public void transform(Matrix matrix)
    {
        mSelfMatrix.postConcat(matrix);
//        getActualRect();
        updateImageVertex(getActualRectF());
    }

    public void release()
    {
        if (mBitmap != null && !mBitmap.isRecycled())
        {
            mBitmap.recycle();
            mBitmap = null;
        }
    }

    protected void finalize() throws Throwable
    {
        super.finalize();
        PanelManager.getBitmapReferenceMap().remove(this._mId);
    }

    public String getMId()
    {
        return this._mId;
    }

    public int getGLTextureId()
    {
        return this.mImageTextureId;
    }

    public void setBitmap(Bitmap pBitmap)
    {
        this.mBitmap = pBitmap;
    }

    public void setGLTextureId(int pTextureId)
    {
        this.mImageTextureId = pTextureId;
    }
}
