package com.mphotool.whiteboard.elements;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;

import com.mphotool.whiteboard.BuildConfig;
import com.mphotool.whiteboard.R;
import com.mphotool.whiteboard.utils.BaseUtils;
import com.mphotool.whiteboard.utils.BitmapUtils;
import com.mphotool.whiteboard.utils.Constants;
import com.mphotool.whiteboard.utils.PanelUtils;
import com.mphotool.whiteboard.utils.PathHelper;
import com.mphotool.whiteboard.utils.ToofifiLog;
import com.mphotool.whiteboard.view.PanelManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

public class Selector extends Material {
    public static final int EDGE_MARGIN = 13;
    public static final int EDGE_BOTTOM = 1024;
    public static final int EDGE_LEFT = 128;
    public static final int EDGE_RIGHT = 512;
    public static final int EDGE_TOP = 256;
    private static DashPathEffect mDashPathEffect;
    private static transient Bitmap mDeleteBitmap = null;
    private static transient Bitmap mPasteBitmap = null;
    private static transient Bitmap mCropBitmap = null;
    private static transient Paint mPaint = new Paint();
    private static transient Paint mRectPaint = new Paint(mPaint);
    private float mDensity = 1.0f;

    private transient Path mPath = new Path();
    private PathHelper mPathHelper = new PathHelper(1.0f);
    private transient List<Point> mPoints = new ArrayList();

    private Rect mPathRect = new Rect();
    private ArrayList<Material> selections = new ArrayList();
    private Rect mRect = new Rect();
    private boolean mShowRect = false;
    private boolean mShowCrop = false;
    private boolean mShowPaste = false;

    private static final float COLOR_B = (float) Color.blue(128);
    private static final float COLOR_G = (float) Color.green(196);
    private static final float COLOR_R = (float) Color.red(64);
    private FloatBuffer mSelectRectVertexBuffer;
    private boolean mIsOpenGLEnabled = BuildConfig.enable_opengl;

    static
    {
        mDashPathEffect = null;
        mPaint.setColor(Color.rgb(64, 196, 128));
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Cap.ROUND);
        mPaint.setSubpixelText(true);
        mPaint.setStrokeWidth(Constants.BORDER_WIDTH_MEDIUM);
        mPaint.setStyle(Style.STROKE);
        mRectPaint.setStyle(Style.STROKE);
        mRectPaint.setColor(Color.argb(112, 64, 196, 255));
        float margin = mPaint.getStrokeWidth();

        /**指定虚线样式*/
        mDashPathEffect = new DashPathEffect(new float[]{margin, margin * 1.5f, margin, margin * 1.5f}, 1.0f);
    }

    public static void setSelectorLineColor(int color)
    {
        mPaint.setColor(color);
    }

    public static void setSelectorRectColor(int color)
    {
        mRectPaint.setColor(color);
    }

    public boolean canDrawForScreenShot()
    {
        return false;
    }

    @Override public void continueTransform(Matrix matrix)
    {
    }

    @Override public void endTransform(Matrix matrix)
    {

    }

    public void showRect(boolean s)
    {
        mShowRect = s;
    }

    public boolean selected()
    {
        return mShowRect;
    }

    public void setPanelManager(PanelManager panelManager)
    {
        mManager = panelManager;
        Resources res = mManager.getContext().getResources();
        if (mPasteBitmap == null)
        {
            int iconWidth = res.getDimensionPixelSize(R.dimen.select_icon_width);
            Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.selected_delete);
            mDeleteBitmap = BitmapUtils.scaleWithWidth(bitmap, iconWidth);
            if (!(mDeleteBitmap.getWidth() == bitmap.getWidth() && mDeleteBitmap.getHeight() == bitmap.getHeight()))
            {
                bitmap.recycle();
            }
            bitmap = BitmapFactory.decodeResource(res, R.drawable.paste);
            mPasteBitmap = BitmapUtils.scaleWithWidth(bitmap, iconWidth);
            if (!(mPasteBitmap.getWidth() == bitmap.getWidth() && mPasteBitmap.getHeight() == bitmap.getHeight()))
            {
                bitmap.recycle();
            }
            bitmap = BitmapFactory.decodeResource(res, R.drawable.image_crop);
            mCropBitmap = BitmapUtils.scaleWithWidth(bitmap, iconWidth);
            if (!(mCropBitmap.getWidth() == bitmap.getWidth() && mCropBitmap.getHeight() == bitmap.getHeight()))
            {
                bitmap.recycle();
            }
            mDensity = mManager.getContext().getResources().getDisplayMetrics().density;
        }
    }

    public Selector(PanelManager manager)
    {
        Resources res = manager.getContext().getResources();
        mManager = manager;
        if (mPasteBitmap == null)
        {
            int iconWidth = res.getDimensionPixelSize(R.dimen.select_icon_width);
            Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.selected_delete);
            mDeleteBitmap = BitmapUtils.scaleWithWidth(bitmap, iconWidth);
            bitmap.recycle();
            bitmap = BitmapFactory.decodeResource(res, R.drawable.paste);
            mPasteBitmap = BitmapUtils.scaleWithWidth(bitmap, iconWidth);
            if (!(mPasteBitmap.getWidth() == bitmap.getWidth() && mPasteBitmap.getHeight() == bitmap.getHeight()))
            {
                bitmap.recycle();
            }
            bitmap = BitmapFactory.decodeResource(res, R.drawable.image_crop);
            mCropBitmap = BitmapUtils.scaleWithWidth(bitmap, iconWidth);
            if (!(mCropBitmap.getWidth() == bitmap.getWidth() && mCropBitmap.getHeight() == bitmap.getHeight()))
            {
                bitmap.recycle();
            }
            mDensity = mManager.getContext().getResources().getDisplayMetrics().density;
        }

        initGL();
    }

    public void reset()
    {
        mPoints.clear();
        mShowRect = false;
        mRect.setEmpty();
        mPath.rewind();
        selections.clear();
    }

    public List<Material> getSelections()
    {
        return selections;
    }

    public void clearSelections()
    {
        for (Material temp : selections)
        {
            temp.setSelected(false);
        }
        selections.clear();
    }

    public void addSelections(List<Material> ms)
    {
        for (Material temp : ms)
        {
            temp.setSelected(true);
        }
        if (ms != null && ms.size() == 1 && ms.get(0) instanceof Image)
        {
            setShowCrop(true);
        }
        else
        {
            setShowCrop(false);
        }
        if (ms.size() <= 10)
        {
            setShowPaste(true);
        }
        else
        {
            setShowPaste(false);
        }

        selections.addAll(ms);
    }

    public void drawPoint(PanelManager manager, float x, float y)
    {
        if (mPoints.size() >= 1)
        {
            Rect rect = new Rect();
            if (mPoints.size() == 1)
            {
                manager.getCacheCanvas().drawPoint(x, y, mPaint);
                int width = (int) mPaint.getStrokeWidth();
                rect.set(((int) x) - width, ((int) y) - width, ((int) x) + width, ((int) y) + width);
                manager.drawScreen(rect);
                return;
            }

            /**绘制虚线*/
            Paint p = new Paint(mPaint);
            p.setPathEffect(mDashPathEffect);
            Path path = new Path();
            mPathHelper.addToPath(path);
            manager.getCacheCanvas().drawPath(path, p);
            mPathHelper.pointsToRect(rect, 3);
            manager.drawScreen(rect);
        }
    }

    @Override public void draw(Canvas canvas)
    {
        Rect rect;
//        BaseUtils.dbg("Selector", "----- mShowRect = " + mShowRect + " mPoints.size()=" + mPoints.size());
        if (mShowRect)
        {
            rect = new Rect(mRect.left, mRect.top, mRect.right, mRect.bottom);
            mRectPaint.setPathEffect(mDashPathEffect);

            // 绘制方框
            canvas.drawRect((float) (rect.left - 1), (float) (rect.top - 1), (float) (rect.right + 1), (float) (rect.bottom + 1), mRectPaint);
            Paint paint = new Paint();
            paint.setAntiAlias(true);

            if (mDeleteBitmap != null)
            {
                canvas.drawBitmap(mDeleteBitmap, (float) rect.left, (float) (rect.bottom + 10), mPaint);
            }
            if (showPaste())
            {
                if (mPasteBitmap != null)
                {
                    canvas.drawBitmap(mPasteBitmap, (float) ((rect.left + mPasteBitmap.getWidth()) + 10), (float) (rect.bottom + 10), mPaint);
                }
            }
            if (showCrop())
            {
                if (mCropBitmap != null)
                {
                    canvas.drawBitmap(mCropBitmap, (float) (rect.left + mPasteBitmap.getWidth() * 2 + 20), (float) (rect.bottom + 10), mPaint);
                }
            }
        }
        else if (mPoints.size() >= 1)
        {
            rect = new Rect();
            Paint p = new Paint(mPaint);
            p.setPathEffect(mDashPathEffect);
            canvas.drawPath(mPath, p);
        }
    }

    public void initGL()
    {
        this.mSelectRectVertexBuffer = ByteBuffer.allocateDirect(40).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.mSelectRectVertexBuffer.position(0);
    }

    public void drawGL(GL10 gl)
    {
        if (this.mIsOpenGLEnabled)
        {
//            BaseUtils.dbg("Selector", "drawGL");
            gl.glEnableClientState(32884);
            gl.glLineWidth(2.0f);
            gl.glColor4f(COLOR_R, COLOR_G, COLOR_B, 0.8f);
            this.mSelectRectVertexBuffer.clear();
            this.mSelectRectVertexBuffer.position(0);
            this.mSelectRectVertexBuffer.put(this.mRect.left);
            this.mSelectRectVertexBuffer.put(this.mRect.top);
            this.mSelectRectVertexBuffer.put(this.mRect.right);
            this.mSelectRectVertexBuffer.put(this.mRect.top);
            this.mSelectRectVertexBuffer.put(this.mRect.right);
            this.mSelectRectVertexBuffer.put(this.mRect.bottom);
            this.mSelectRectVertexBuffer.put(this.mRect.left);
            this.mSelectRectVertexBuffer.put(this.mRect.bottom);
            this.mSelectRectVertexBuffer.put(this.mRect.left);
            this.mSelectRectVertexBuffer.put(this.mRect.top);
            this.mSelectRectVertexBuffer.position(0);
            gl.glVertexPointer(2, 5126, 0, this.mSelectRectVertexBuffer);
            gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, 5);
            gl.glDisableClientState(32884);
        }
    }

    public int clickButton(float fx, float fy)
    {
        int x = (int) fx;
        int y = (int) fy;
        Rect r = new Rect();
        int offsetX = mRect.left;
        int offsetY = mRect.bottom + 10;
        int iconWidth = (int) (((float) mPasteBitmap.getWidth()));
        int iconHeight = (int) (((float) mPasteBitmap.getHeight()));

        /**判断touch点是否在图标范围内*/
        r.set(offsetX, offsetY, offsetX + iconWidth, offsetY + iconHeight);
        if (r.contains(x, y))
        {
            return 1;
        }
        offsetX += iconWidth + 10;
        r.set(offsetX, offsetY, offsetX + iconWidth, offsetY + iconHeight);
        if (showPaste() && r.contains(x, y))
        {
            return 2;
        }
        offsetX += iconWidth + 10;
        r.set(offsetX, offsetY, offsetX + iconWidth, offsetY + iconHeight);
        if (showCrop() && r.contains(x, y))
        {
            return 3;
        }

        int edge = 0;
        if (Math.abs(x - mRect.left) < EDGE_MARGIN)
        {
            edge = 0 | EDGE_LEFT;
        }
        if (Math.abs(x - mRect.right) < EDGE_MARGIN)
        {
            edge |= EDGE_RIGHT;
        }

        if (Math.abs(y - mRect.top) < EDGE_MARGIN)
        {
            edge |= EDGE_TOP;
        }
        if (Math.abs(y - mRect.bottom) < EDGE_MARGIN)
        {
            edge |= EDGE_BOTTOM;
        }
        if (edge == 0)
        {
            return -1;
        }
        return edge;
    }

    public Rect rect()
    {
        int iconHeight = (int) (((float) mPasteBitmap.getHeight()));
        int buttonWidth = (((int) (((float) mPasteBitmap.getWidth()))) + 10) * 3;
        Rect rect = new Rect(selectedRect());
        rect.bottom += (iconHeight + 10) + 10;
        if (rect.width() < buttonWidth)
        {
            rect.right = rect.left + buttonWidth;
        }
        PanelUtils.rectAddWidth(rect, 5);
        return rect;
    }

    public Rect getPathRect()
    {
        return mPathRect;
    }

    @Override
    public void transform(Matrix matrix)
    {

    }

    public void addPoint(float x, float y, MotionEvent event)
    {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN)
        {
            mPoints.clear();
            mPath.rewind();
            mPathRect.setEmpty();
            mPathRect.set((int) x, (int) y, (int) x + 1, (int) y - 1);
        }
        mPoints.add(new Point((int) x, (int) y));
        if (mPoints.isEmpty())
        {
            mPath.rewind();
        }
        mPathHelper.onTouch(x, y, action);

        mPath = mPathHelper.getPath();
        mPathRect.union((int) x, (int) y);
    }

    public Rect selectedRect()
    {
        return new Rect(mRect.left, mRect.top, mRect.right, mRect.bottom);
    }

    public void setSelectRect(int left, int top, int right, int bottom)
    {
        mRect.set(left - 2, top - 2, right + 2, bottom + 2);
    }

    public void setSelectRect(Rect r)
    {
        mRect.set(r.left - 2, r.top - 2, r.right + 2, r.bottom + 2);
    }

    public void addRect(Rect r)
    {
        mRect.union(r.left - 2, r.top - 2, r.right + 2, r.bottom + 2);
    }
    
    public boolean readObject(InputStream in) throws IOException, ClassNotFoundException
    {
        byte[] buf = new byte[1];
        if (!super.readObject(in))
        {
            return false;
        }
        if (in.read(buf) != 1)
        {
            ToofifiLog.e(Constants.TAG, "Selector: read boolean failed");
            return false;
        }
        mShowRect = buf[0] == (byte) 1;
        buf = new byte[4];
        int ret = in.read(buf);
        mRect.left = BaseUtils.byteArrayToInt(buf);
        ret = in.read(buf);
        mRect.top = BaseUtils.byteArrayToInt(buf);
        ret = in.read(buf);
        mRect.right = BaseUtils.byteArrayToInt(buf);
        if (in.read(buf) != 4)
        {
            ToofifiLog.e(Constants.TAG, "Selector: read rect failed");
            return false;
        }
        mRect.bottom = BaseUtils.byteArrayToInt(buf);
        return true;
    }

    public boolean writeObject(OutputStream out) throws IOException
    {
        int i = 0;
        if (!super.writeObject(out))
        {
            return false;
        }
        if (mShowRect)
        {
            i = 1;
        }
        out.write(i);
        out.write(BaseUtils.intToByteArray(mRect.left));
        out.write(BaseUtils.intToByteArray(mRect.top));
        out.write(BaseUtils.intToByteArray(mRect.right));
        out.write(BaseUtils.intToByteArray(mRect.bottom));
        return true;
    }

    public void calculateSelectRect()
    {
        Rect r = new Rect();
        for (Material m : getSelections())
        {
            if (m.isValid())
            {
                r.union(m.rect());
            }
        }
        setSelectRect(r);
    }

    public boolean showCrop()
    {
        return mShowCrop;
    }

    public void setShowCrop(boolean mShowCrop)
    {
        this.mShowCrop = mShowCrop;
    }

    public boolean showPaste()
    {
        return mShowPaste;
    }

    public void setShowPaste(boolean mShowPaste)
    {
        this.mShowPaste = mShowPaste;
    }
}
