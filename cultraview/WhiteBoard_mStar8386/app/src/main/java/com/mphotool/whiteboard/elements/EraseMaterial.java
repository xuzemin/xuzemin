package com.mphotool.whiteboard.elements;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;

import com.mphotool.whiteboard.entity.SerializablePointF;
import com.mphotool.whiteboard.utils.Constants;
import com.mphotool.whiteboard.utils.PanelUtils;
import com.mphotool.whiteboard.utils.PathHelper;
import com.mphotool.whiteboard.view.PanelManager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class EraseMaterial extends Material {
    private static DashPathEffect mDashPathEffect = null; //实现虚线效果
    private static transient Paint mPaint = new Paint();
    private static final long serialVersionUID = Constants.serialVersionUID;
    private transient PanelManager mPanelManager;
    private transient Path mPath = new Path();
    private PathHelper mPathHelper = new PathHelper(Constants.BORDER_WIDTH_MEDIUM);
    private transient Path mSelectedPath = new Path();
    private ArrayList<SerializablePointF> points = new ArrayList();

    static {
        mDashPathEffect = null;
        mPaint.setColor(Color.rgb(64, 196, 255));
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Cap.ROUND);
        mPaint.setSubpixelText(true);
        mPaint.setStrokeWidth(Constants.BORDER_WIDTH_MEDIUM);
        mPaint.setStyle(Style.STROKE);
        float margin = mPaint.getStrokeWidth();
        mDashPathEffect = new DashPathEffect(new float[]{margin, margin * Constants.BORDER_WIDTH_THICK, margin, margin * Constants.BORDER_WIDTH_THICK}, 1.0f);
    }

    public EraseMaterial(PanelManager pm) {
        mPanelManager = pm;
        mManager = pm;
    }

    private void writeObject(ObjectOutputStream out) throws IOException
    {
        out.writeObject(points);
    }

    public static void setSelectorLineColor(int color) {
        mPaint.setColor(color);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        points = (ArrayList) in.readObject();
        rebuildPath();
    }

    public void rebuildPath() {
        mPath = new Path();
        int size = points.size();
        int last = size - 1;
        for (int i = 0; i < size; i++) {
            int action;
            PointF p = (PointF) points.get(i);
            if (i == 0) {
                action = 0;
            } else if (i == last) {
                action = 1;
            } else {
                action = 2;
            }
            addPoint(p.x, p.y, action);
        }
    }

    public void addPoint(float x, float y, int action) {
        points.add(new SerializablePointF(x, y));
        if (action == 0) {
            mPathHelper = new PathHelper(Constants.BORDER_WIDTH_MEDIUM);
        }
        mPathHelper.onTouch(x, y, action);
        mPath = mPathHelper.getPath();
        switch (action) {
            case 0:
                mSelectedPath.moveTo(x, y);
                return;
            case 1:
                mSelectedPath.lineTo(x, y);
                return;
            case 2:
                mSelectedPath.lineTo(x, y);
                return;
            default:
                return;
        }
    }

    public void drawPoint(PanelManager manager, float x, float y) {
        if (points.size() >= 1) {
            Rect rect = new Rect();
            if (points.size() == 1) {
                manager.getCacheCanvas().drawPoint(x, y, mPaint);
                int width = (int) mPaint.getStrokeWidth();
                rect.set(((int) x) - width, ((int) y) - width, ((int) x) + width, ((int) y) + width);
                manager.drawScreen(rect);
                return;
            }
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
        if (points.size() > 1) {
            Paint p = new Paint(mPaint);
            p.setPathEffect(mDashPathEffect);
            canvas.drawPath(mPath, p);
        }
    }

    public int eraseMaterials(PanelManager manager) {
        int count = 0;
        RectF rectF = new RectF();
        if (points.size() < 3) {
            return 0;
        }
        Region region = new Region();
        Path path = new Path(mSelectedPath);
        path.close();
        path.computeBounds(rectF, true);
        Rect bound = new Rect();
        PanelUtils.rectFtoRect(rectF, bound, 0);
        region.setPath(path, new Region(bound));
        List<Material> materials = manager.getMaterials();
        for (int mindex = materials.size() - 1; mindex >= 0; mindex--) {
            Material m = (Material) materials.get(mindex);
            m.setSelected(false);
            if (m.isInRegion(mPanelManager, region)) {
                manager.removeMaterial(m);
                count++;
            }
        }
        return count;
    }

    public Rect rect() {
        return new Rect(0, 0, 0, 0);
    }


    @Override public void continueTransform(Matrix matrix)
    {

    }

    @Override
    public void endTransform(Matrix matrix)
    {

    }

    @Override
    public void transform(Matrix matrix)
    {

    }

}
