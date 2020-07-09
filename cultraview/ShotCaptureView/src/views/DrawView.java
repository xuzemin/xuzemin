
package views;

import com.example.cutcapture.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class DrawView extends View {

    private Context mContext;

    private int width = 0;

    private int height = 0;

    private int y = 0;

    private int x = 0;

    private int mX = 0;

    private int mY = 0;

    private Paint unMarkPaint;

    public Paint circlePaint;

    private int displayWidth;

    private int displayHeight;

    public Paint mBitPaint;

    private Bitmap okBitmap;

    private int okWidth;

    private int okHeight;

    private Bitmap cancleBitmap;

    private int cancleWidth;

    private int cancleHeight;

    private Rect okRect;

    private Rect cancleRect;

    private Rect okDisRect;

    private Rect cancleDisRect;

    private int pointLeft;

    private int pointTop;

    private ClickListenrInterface mClickListenerInterfate;

    private int[] areas;

    private int[] points;

    private int pointRight;

    private int pointBottom;

    private Rect markRect;

    private int starX;

    private int starY;

    private int pointULX;

    private int pointULY;

    private int pointUMX;

    private int pointUMY;

    private int pointURX;

    private int pointURY;

    private int pointMLX;

    private int pointMLY;

    private int pointMRX;

    private int pointMRY;

    private int pointDLX;

    private int pointDLY;

    private int pointDMX;

    private int pointDMY;

    private int pointDRX;

    private int pointDRY;

    private Rect uLRect;

    private Rect uMRect;

    private Rect uRRect;

    private Rect mLRect;

    private Rect mRRect;

    private Rect dLRect;

    private Rect dMRect;

    private Rect dRRect;

    private int xx;

    private int yy;

    private Bitmap zoomBitmap;

    private int zoomWidth;

    private int zoomHeith;

    private Rect zoomRect;

    private Rect zoomDisRect;

    private int[] drawAreas;

    private int[] drawPoints;

    private int circle_radius = 15;

    private int button_top_distance = 20;

    private int button_bottom_distance = 90;

    private int ok_bt_right_distance = 70;

    private int cancle_bt_right_distance = 140;

    private int zoom_bt_right_distance = 210;

    public DrawView(Context context) {
        super(context, null);
        setWillNotDraw(false);
        this.mContext = context;
        init();
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        this.mContext = context;
        init();
    }

    private void init() {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        displayWidth = wm.getDefaultDisplay().getWidth();
        displayHeight = wm.getDefaultDisplay().getHeight();

        unMarkPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
        unMarkPaint.setColor(getResources().getColor(R.color.tran_black));

        circlePaint = new Paint(Paint.FILTER_BITMAP_FLAG);
        circlePaint.setColor(Color.WHITE);
        // circlePaint.setAlpha(90);
        circlePaint.setStrokeWidth(3);
        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Style.STROKE);

        mBitPaint = new Paint();
        mBitPaint.setFilterBitmap(true);
        mBitPaint.setDither(true);
        // mBitPaint.setAlpha(100);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        okBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ok);
        okWidth = okBitmap.getWidth();
        okHeight = okBitmap.getHeight();
        cancleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cancle);
        cancleWidth = cancleBitmap.getWidth();
        cancleHeight = cancleBitmap.getHeight();
        zoomBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.zoomout);
        zoomWidth = zoomBitmap.getWidth();
        zoomHeith = zoomBitmap.getHeight();

        okRect = new Rect(0, 0, okWidth, okHeight);
        cancleRect = new Rect(0, 0, cancleWidth, cancleHeight);
        zoomRect = new Rect(0, 0, zoomWidth, zoomHeith);

        areas = new int[2]; // 截图区域宽高
        points = new int[4]; // 触摸松手后截图区域左上角和右下角坐标
        drawAreas = new int[2]; // 重绘后截图区域的宽高
        drawPoints = new int[4];// 重绘后截图区域的点的坐标

        markRect = new Rect();
        uLRect = new Rect();
        uMRect = new Rect();
        uRRect = new Rect();
        mLRect = new Rect();
        mRRect = new Rect();
        dLRect = new Rect();
        dMRect = new Rect();
        dRRect = new Rect();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            xx = (int) event.getX();
            yy = (int) event.getY();
            // 必须先对点击事件做判断,否则会执行到move中
            if (okDisRect.contains(xx, yy)) {
                mClickListenerInterfate.doConfirm();
                return false;
            } else if (cancleDisRect.contains(xx, yy)) {
                mClickListenerInterfate.doCancel();
                return false;
            } else if (zoomDisRect.contains(xx, yy)) {
                mClickListenerInterfate.doZoomOut();
                return false;
            } else if (markRect.contains(xx, yy) || uLRect.contains(xx, yy)
                    || uMRect.contains(xx, yy) || uRRect.contains(xx, yy)
                    || mLRect.contains(xx, yy) || mRRect.contains(xx, yy)
                    || dLRect.contains(xx, yy) || dMRect.contains(xx, yy)
                    || dRRect.contains(xx, yy)) {
                starX = (int) event.getX();
                starY = (int) event.getY();
            } else {
                x = (int) event.getX();
                y = (int) event.getY();
            }
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (markRect.contains(xx, yy) || uLRect.contains(xx, yy) || uMRect.contains(xx, yy)
                    || uRRect.contains(xx, yy) || mLRect.contains(xx, yy)
                    || mRRect.contains(xx, yy) || dLRect.contains(xx, yy)
                    || dMRect.contains(xx, yy) || dRRect.contains(xx, yy)) {
                int endX = (int) event.getX();
                int endY = (int) event.getY();
                // 计算触摸点移动后的位置和移动距离
                if (uMRect.contains(starX, starY)) { // 上中
                    y = points[1] + endY - starY;
                } else if (mLRect.contains(starX, starY)) { // 中左
                    x = points[0] + endX - starX;
                } else if (mRRect.contains(starX, starY)) { // 中右
                    mX = points[2] + endX - starX;
                } else if (dMRect.contains(starX, starY)) { // 下中
                    mY = points[3] + endY - starY;
                } else if (dLRect.contains(starX, starY)) { // 下左
                    x = points[0] + endX - starX;
                    mY = points[3] + endY - starY;
                } else if (dRRect.contains(starX, starY)) { // 下右---
                    mX = points[2] + endX - starX;
                    mY = points[3] + endY - starY;
                } else if (uLRect.contains(starX, starY)) { // 上左
                    x = points[0] + endX - starX;
                    y = points[1] + endY - starY;
                } else if (uRRect.contains(starX, starY)) { // 上右---
                    y = points[1] + endY - starY;
                    mX = points[2] + endX - starX;
                } else if (markRect.contains(starX, starY)) { // 截图区
                    x = points[0] + endX - starX;
                    y = points[1] + endY - starY;
                    mX = points[2] + endX - starX;
                    mY = points[3] + endY - starY;
                }
                // 边界值
                if (mX > displayWidth) {
                    mX = displayWidth;
                }
                if (mX <= 0) {
                    mX = 0;
                }
                if (mY >= displayHeight) {
                    mY = displayHeight;
                }
                if (mY <= 0) {
                    mY = 0;
                }
                if (x >= displayWidth) {
                    x = displayWidth;
                }
                if (x <= 0) {
                    x = 0;
                }
                if (y >= displayHeight) {
                    y = displayHeight;
                }
                if (y <= 0) {
                    y = 0;
                }
            } else {
                mX = (int) event.getX();
                mY = (int) event.getY();
            }

            localOptions();
            postInvalidate();
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getX() > x) {
                width = (int) (event.getX() - x);
            } else {
                width = (int) (x - event.getX());
            }
            if (event.getY() > y) {
                height = (int) (event.getY() - y);
            } else {
                height = (int) (y - event.getY());
            }
            areas[0] = width;
            areas[1] = height;

            points[0] = pointLeft;
            points[1] = pointTop;
            points[2] = pointRight;
            points[3] = pointBottom;

            markRect = new Rect(pointLeft, pointTop, pointRight, pointBottom);
            uLRect = new Rect(pointULX - circle_radius, pointULY - circle_radius, pointULX
                    + circle_radius, pointULY + circle_radius);
            uMRect = new Rect(pointUMX - circle_radius, pointUMY - circle_radius, pointUMX
                    + circle_radius, pointUMY + circle_radius);
            uRRect = new Rect(pointURX - circle_radius, pointURY - circle_radius, pointURX
                    + circle_radius, pointURY + circle_radius);
            mLRect = new Rect(pointMLX - circle_radius, pointMLY - circle_radius, pointMLX
                    + circle_radius, pointMLY + circle_radius);
            mRRect = new Rect(pointMRX - circle_radius, pointMRY - circle_radius, pointMRX
                    + circle_radius, pointMRY + circle_radius);
            dLRect = new Rect(pointDLX - circle_radius, pointDLY - circle_radius, pointDLX
                    + circle_radius, pointDLY + circle_radius);
            dMRect = new Rect(pointDMX - circle_radius, pointDMY - circle_radius, pointDMX
                    + circle_radius, pointDMY + circle_radius);
            dRRect = new Rect(pointDRX - circle_radius, pointDRY - circle_radius, pointDRX
                    + circle_radius, pointDRY + circle_radius);
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (x < mX && y < mY) { // 左上-->右下
            canvas.drawRect(new Rect(0, y, x, mY), unMarkPaint); // 左
            canvas.drawRect(new Rect(0, 0, displayWidth, y), unMarkPaint); // 上
            canvas.drawRect(new Rect(mX, y, displayWidth, mY), unMarkPaint); // 右
            canvas.drawRect(new Rect(0, mY, displayWidth, displayHeight), unMarkPaint); // 下

            drawCircle(canvas, x, y, circlePaint); // 上左
            drawCircle(canvas, Math.abs(mX - x) / 2 + x, y, circlePaint); // 上中
            drawCircle(canvas, mX, y, circlePaint); // 上右
            drawCircle(canvas, x, Math.abs(mY - y) / 2 + y, circlePaint); // 中左
            drawCircle(canvas, mX, Math.abs(mY - y) / 2 + y, circlePaint); // 中右
            drawCircle(canvas, x, mY, circlePaint); // 下左
            drawCircle(canvas, Math.abs(mX - x) / 2 + x, mY, circlePaint); // 下中
            drawCircle(canvas, mX, mY, circlePaint); // 下右
        } else if (x < mX && y > mY) { // 左下-->右上
            canvas.drawRect(new Rect(0, mY, x, y), unMarkPaint); // 左
            canvas.drawRect(new Rect(0, 0, displayWidth, mY), unMarkPaint); // 上
            canvas.drawRect(new Rect(mX, mY, displayWidth, y), unMarkPaint); // 右
            canvas.drawRect(new Rect(0, y, displayWidth, displayHeight), unMarkPaint); // 下

            drawCircle(canvas, x, mY, circlePaint); // 上左
            drawCircle(canvas, Math.abs(mX - x) / 2 + x, mY, circlePaint); // 上中
            drawCircle(canvas, mX, mY, circlePaint); // 上右
            drawCircle(canvas, x, Math.abs(mY - y) / 2 + mY, circlePaint); // 中左
            drawCircle(canvas, mX, Math.abs(mY - y) / 2 + mY, circlePaint); // 中右
            drawCircle(canvas, x, y, circlePaint); // 下左
            drawCircle(canvas, Math.abs(mX - x) / 2 + x, y, circlePaint); // 下中
            drawCircle(canvas, mX, y, circlePaint); // 下右
        } else if (x > mX && y < mY) { // 右上-->左下
            canvas.drawRect(new Rect(0, y, mX, mY), unMarkPaint); // 左
            canvas.drawRect(new Rect(0, 0, displayWidth, y), unMarkPaint); // 上
            canvas.drawRect(new Rect(x, y, displayWidth, mY), unMarkPaint); // 右
            canvas.drawRect(new Rect(0, mY, displayWidth, displayHeight), unMarkPaint); // 下

            drawCircle(canvas, mX, y, circlePaint); // 上左
            drawCircle(canvas, Math.abs(mX - x) / 2 + mX, y, circlePaint); // 上中
            drawCircle(canvas, x, y, circlePaint); // 上右
            drawCircle(canvas, mX, Math.abs(mY - y) / 2 + y, circlePaint); // 中左
            drawCircle(canvas, x, Math.abs(mY - y) / 2 + y, circlePaint); // 中右
            drawCircle(canvas, mX, mY, circlePaint); // 下左
            drawCircle(canvas, Math.abs(mX - x) / 2 + mX, mY, circlePaint); // 下中
            drawCircle(canvas, x, mY, circlePaint); // 下右
        } else if (x > mX && y > mY) { // 右下-->左上
            canvas.drawRect(new Rect(0, mY, mX, y), unMarkPaint); // 左
            canvas.drawRect(new Rect(0, 0, displayWidth, mY), unMarkPaint); // 上
            canvas.drawRect(new Rect(x, mY, displayWidth, y), unMarkPaint); // 右
            canvas.drawRect(new Rect(0, y, displayWidth, displayHeight), unMarkPaint); // 下

            drawCircle(canvas, mX, mY, circlePaint); // 上左
            drawCircle(canvas, Math.abs(mX - x) / 2 + mX, mY, circlePaint); // 上中
            drawCircle(canvas, x, mY, circlePaint); // 上右
            drawCircle(canvas, mX, Math.abs(mY - y) / 2 + mY, circlePaint); // 中左
            drawCircle(canvas, x, Math.abs(mY - y) / 2 + mY, circlePaint); // 中右
            drawCircle(canvas, mX, y, circlePaint); // 下左
            drawCircle(canvas, Math.abs(mX - x) / 2 + mX, y, circlePaint); // 下中
            drawCircle(canvas, x, y, circlePaint); // 下右
        } else {
            canvas.drawRect(new Rect(0, 0, displayWidth, displayHeight), unMarkPaint);
            okDisRect = new Rect(0, 0, 0, 0);
            cancleDisRect = new Rect(0, 0, 0, 0);
            zoomDisRect = new Rect(0, 0, 0, 0);
        }

        circlePoint();
        localOptions();

        canvas.drawBitmap(okBitmap, okRect, okDisRect, mBitPaint);
        canvas.drawBitmap(cancleBitmap, cancleRect, cancleDisRect, mBitPaint);
        canvas.drawBitmap(zoomBitmap, zoomRect, zoomDisRect, mBitPaint);
    }

    private void drawCircle(Canvas canvas, int x, int y, Paint paint) {
        canvas.drawCircle(x, y, circle_radius, paint);
    }

    public void setBitmapPaint(int imgId) {
        zoomBitmap = BitmapFactory.decodeResource(getResources(), imgId);
        zoomWidth = zoomBitmap.getWidth();
        zoomHeith = zoomBitmap.getHeight();
    }

    public int[] getArea() {
        return areas;
    }

    // 重绘后
    public int[] getDrawArea() {
        return drawAreas;
    }

    public int[] getMarkPoint() {
        return points;
    }

    // 重绘后
    public int[] getDrawMarkPoint() {
        return drawPoints;
    }

    // 设置截图区域的坐标点
    public void setMarkPoint(int[] are) {
        x = are[0];
        y = are[1];
        mX = are[2];
        mY = are[3];
    }

    // 启动时,初始化截图区域
    public void initMarkArea(int[] are) {
        x = are[0];
        y = are[1];
        mX = are[2];
        mY = are[3];

        points[0] = x;
        points[1] = y;
        points[2] = mX;
        points[3] = mY;

        // 圆的坐标点
        pointULX = x;
        pointULY = y;

        pointUMX = Math.abs(mX - x) / 2 + x;
        pointUMY = y;

        pointURX = mX;
        pointURY = y;

        pointMLX = x;
        pointMLY = Math.abs(mY - y) / 2 + y;

        pointMRX = mX;
        pointMRY = Math.abs(mY - y) / 2 + y;

        pointDLX = x;
        pointDLY = mY;

        pointDMX = Math.abs(mX - x) / 2 + x;
        pointDMY = mY;

        pointDRX = mX;
        pointDRY = mY;

        markRect = new Rect(x, y, mX, mY);
        uLRect = new Rect(pointULX - circle_radius, pointULY - circle_radius, pointULX
                + circle_radius, pointULY + circle_radius);
        uMRect = new Rect(pointUMX - circle_radius, pointUMY - circle_radius, pointUMX
                + circle_radius, pointUMY + circle_radius);
        uRRect = new Rect(pointURX - circle_radius, pointURY - circle_radius, pointURX
                + circle_radius, pointURY + circle_radius);
        mLRect = new Rect(pointMLX - circle_radius, pointMLY - circle_radius, pointMLX
                + circle_radius, pointMLY + circle_radius);
        mRRect = new Rect(pointMRX - circle_radius, pointMRY - circle_radius, pointMRX
                + circle_radius, pointMRY + circle_radius);
        dLRect = new Rect(pointDLX - circle_radius, pointDLY - circle_radius, pointDLX
                + circle_radius, pointDLY + circle_radius);
        dMRect = new Rect(pointDMX - circle_radius, pointDMY - circle_radius, pointDMX
                + circle_radius, pointDMY + circle_radius);
        dRRect = new Rect(pointDRX - circle_radius, pointDRY - circle_radius, pointDRX
                + circle_radius, pointDRY + circle_radius);
    }

    private void localOptions() {
        // 按钮位置
        if (x < mX && y < mY) { // 左上-->右下
            if (displayHeight - mY >= 2 * okHeight) { // 底下
                okDisRect = new Rect(mX - ok_bt_right_distance, mY + button_top_distance, okWidth
                        + mX - ok_bt_right_distance, okHeight + mY + button_top_distance);
                cancleDisRect = new Rect(mX - cancle_bt_right_distance, mY + button_top_distance,
                        cancleWidth + mX - cancle_bt_right_distance, cancleHeight + mY
                                + button_top_distance);
                zoomDisRect = new Rect(mX - zoom_bt_right_distance, mY + button_top_distance,
                        zoomWidth + mX - zoom_bt_right_distance, zoomHeith + mY
                                + button_top_distance);
            } else if (y >= 3 * okHeight) { // 上面
                okDisRect = new Rect(mX - ok_bt_right_distance, y - button_bottom_distance, okWidth
                        + mX - ok_bt_right_distance, okHeight + y - button_bottom_distance);
                cancleDisRect = new Rect(mX - cancle_bt_right_distance, y - button_bottom_distance,
                        cancleWidth + mX - cancle_bt_right_distance, cancleHeight + y
                                - button_bottom_distance);
                zoomDisRect = new Rect(mX - zoom_bt_right_distance, y - button_bottom_distance,
                        zoomWidth + mX - zoom_bt_right_distance, zoomHeith + y
                                - button_bottom_distance);
            } else { // 截图区右下角
                okDisRect = new Rect(mX - ok_bt_right_distance, mY - button_bottom_distance,
                        okWidth + mX - ok_bt_right_distance, okHeight + mY - button_bottom_distance);
                cancleDisRect = new Rect(mX - cancle_bt_right_distance,
                        mY - button_bottom_distance, cancleWidth + mX - cancle_bt_right_distance,
                        cancleHeight + mY - button_bottom_distance);
                zoomDisRect = new Rect(mX - zoom_bt_right_distance, mY - button_bottom_distance,
                        zoomWidth + mX - zoom_bt_right_distance, zoomHeith + mY
                                - button_bottom_distance);
            }
        } else if (x < mX && y > mY) { // 左下-->右上
            if (displayHeight - y >= 2 * okHeight) {// 截图区右下
                okDisRect = new Rect(mX - ok_bt_right_distance, y + button_top_distance, okWidth
                        + mX - ok_bt_right_distance, okHeight + y + button_top_distance);
                cancleDisRect = new Rect(mX - cancle_bt_right_distance, y + button_top_distance,
                        cancleWidth + mX - cancle_bt_right_distance, cancleHeight + y
                                + button_top_distance);
                zoomDisRect = new Rect(mX - zoom_bt_right_distance, y + button_top_distance,
                        zoomWidth + mX - zoom_bt_right_distance, zoomHeith + y
                                + button_top_distance);
            } else if (mY > 3 * okHeight) {// 右上
                okDisRect = new Rect(mX - ok_bt_right_distance, mY - button_bottom_distance,
                        okWidth + mX - ok_bt_right_distance, okHeight + mY - button_bottom_distance);
                cancleDisRect = new Rect(mX - cancle_bt_right_distance,
                        mY - button_bottom_distance, cancleWidth + mX - cancle_bt_right_distance,
                        cancleHeight + mY - button_bottom_distance);
                zoomDisRect = new Rect(mX - zoom_bt_right_distance, mY - button_bottom_distance,
                        zoomWidth + mX - zoom_bt_right_distance, zoomHeith + mY
                                - button_bottom_distance);
            } else {// 截图区右下角
                okDisRect = new Rect(mX - ok_bt_right_distance, y - button_bottom_distance, okWidth
                        + mX - ok_bt_right_distance, okHeight + y - button_bottom_distance);
                cancleDisRect = new Rect(mX - cancle_bt_right_distance, y - button_bottom_distance,
                        cancleWidth + mX - cancle_bt_right_distance, cancleHeight + y
                                - button_bottom_distance);
                zoomDisRect = new Rect(mX - zoom_bt_right_distance, y - button_bottom_distance,
                        zoomWidth + mX - zoom_bt_right_distance, zoomHeith + y
                                - button_bottom_distance);
            }

        } else if (x > mX && y < mY) { // 右上-->左下
            if (displayHeight - mY >= 2 * okHeight) {
                okDisRect = new Rect(x - ok_bt_right_distance, mY + button_top_distance, okWidth
                        + x - ok_bt_right_distance, okHeight + mY + button_top_distance);
                cancleDisRect = new Rect(x - cancle_bt_right_distance, mY + button_top_distance,
                        cancleWidth + x - cancle_bt_right_distance, cancleHeight + mY
                                + button_top_distance);
                zoomDisRect = new Rect(x - zoom_bt_right_distance, mY + button_top_distance,
                        zoomWidth + x - zoom_bt_right_distance, zoomHeith + mY
                                + button_top_distance);
            } else if (y >= 3 * okHeight) {
                okDisRect = new Rect(x - ok_bt_right_distance, y - button_bottom_distance, okWidth
                        + x - ok_bt_right_distance, okHeight + y - button_bottom_distance);
                cancleDisRect = new Rect(x - cancle_bt_right_distance, y - button_bottom_distance,
                        cancleWidth + x - cancle_bt_right_distance, cancleHeight + y
                                - button_bottom_distance);
                zoomDisRect = new Rect(x - zoom_bt_right_distance, y - button_bottom_distance,
                        zoomWidth + x - zoom_bt_right_distance, zoomHeith + y
                                - button_bottom_distance);
            } else {
                okDisRect = new Rect(x - ok_bt_right_distance, mY - button_bottom_distance, okWidth
                        + x - ok_bt_right_distance, okHeight + mY - button_bottom_distance);
                cancleDisRect = new Rect(x - cancle_bt_right_distance, mY - button_bottom_distance,
                        cancleWidth + x - cancle_bt_right_distance, cancleHeight + mY
                                - button_bottom_distance);
                zoomDisRect = new Rect(x - zoom_bt_right_distance, mY - button_bottom_distance,
                        zoomWidth + x - zoom_bt_right_distance, zoomHeith + mY
                                - button_bottom_distance);
            }
        } else if (x > mX && y > mY) { // 右下-->左上
            if (displayHeight - y > 2 * okHeight) {
                okDisRect = new Rect(x - ok_bt_right_distance, y + button_top_distance, okWidth + x
                        - ok_bt_right_distance, okHeight + y + button_top_distance);
                cancleDisRect = new Rect(x - cancle_bt_right_distance, y + button_top_distance,
                        cancleWidth + x - cancle_bt_right_distance, cancleHeight + y
                                + button_top_distance);
                zoomDisRect = new Rect(x - zoom_bt_right_distance, y + button_top_distance,
                        zoomWidth + x - zoom_bt_right_distance, zoomHeith + y + button_top_distance);
            } else if (mY >= 3 * okHeight) {
                okDisRect = new Rect(x - ok_bt_right_distance, mY - button_bottom_distance, okWidth
                        + x - ok_bt_right_distance, okHeight + mY - button_bottom_distance);
                cancleDisRect = new Rect(x - cancle_bt_right_distance, mY - button_bottom_distance,
                        cancleWidth + x - cancle_bt_right_distance, cancleHeight + mY
                                - button_bottom_distance);
                zoomDisRect = new Rect(x - zoom_bt_right_distance, mY - button_bottom_distance,
                        zoomWidth + x - zoom_bt_right_distance, zoomHeith + mY
                                - button_bottom_distance);
            } else {
                okDisRect = new Rect(x - ok_bt_right_distance, y - button_bottom_distance, okWidth
                        + x - ok_bt_right_distance, okHeight + y - button_bottom_distance);
                cancleDisRect = new Rect(x - cancle_bt_right_distance, y - button_bottom_distance,
                        cancleWidth + x - cancle_bt_right_distance, cancleHeight + y
                                - button_bottom_distance);
                zoomDisRect = new Rect(x - zoom_bt_right_distance, y - button_bottom_distance,
                        zoomWidth + x - zoom_bt_right_distance, zoomHeith + y
                                - button_bottom_distance);
            }
        }
    }

    private void circlePoint() {
        if (x < mX && y < mY) { // 左上-->右下
            // 截图区域左上右下角
            pointLeft = x;
            pointTop = y;
            pointRight = mX;
            pointBottom = mY;

            // 圆的坐标点
            pointULX = x;
            pointULY = y;

            pointUMX = Math.abs(mX - x) / 2 + x;
            pointUMY = y;

            pointURX = mX;
            pointURY = y;

            pointMLX = x;
            pointMLY = Math.abs(mY - y) / 2 + y;

            pointMRX = mX;
            pointMRY = Math.abs(mY - y) / 2 + y;

            pointDLX = x;
            pointDLY = mY;

            pointDMX = Math.abs(mX - x) / 2 + x;
            pointDMY = mY;

            pointDRX = mX;
            pointDRY = mY;

            drawPoints[0] = pointLeft;
            drawPoints[1] = pointTop;
            drawPoints[2] = pointRight;
            drawPoints[3] = pointBottom;

            drawAreas[0] = mX - x;
            drawAreas[1] = mY - y;
        } else if (x > mX && y > mY) { // 右下-->左上
            pointLeft = mX;
            pointTop = mY;
            pointRight = x;
            pointBottom = y;

            // 圆的坐标点
            pointULX = mX;
            pointULY = mY;

            pointUMX = Math.abs(mX - x) / 2 + mX;
            pointUMY = mY;

            pointURX = x;
            pointURY = mY;

            pointMLX = x;
            pointMLY = Math.abs(mY - y) / 2 + mY;

            pointMRX = x;
            pointMRY = Math.abs(mY - y) / 2 + mY;

            pointDLX = mX;
            pointDLY = y;

            pointDMX = Math.abs(mX - x) / 2 + mX;
            pointDMY = y;

            pointDRX = x;
            pointDRY = y;

            drawPoints[0] = pointLeft;
            drawPoints[1] = pointTop;
            drawPoints[2] = pointRight;
            drawPoints[3] = pointBottom;

            drawAreas[0] = x - mX;
            drawAreas[1] = y - mY;
        } else if (x < mX && y > mY) { // 左下-->右上
            pointLeft = x;
            pointTop = mY;
            pointRight = mX;
            pointBottom = y;

            // 圆的坐标点
            pointULX = x;
            pointULY = mY;

            pointUMX = Math.abs(mX - x) / 2 + x;
            pointUMY = mY;

            pointURX = mX;
            pointURY = mY;

            pointMLX = x;
            pointMLY = Math.abs(mY - y) / 2 + mY;

            pointMRX = mX;
            pointMRY = Math.abs(mY - y) / 2 + mY;

            pointDLX = x;
            pointDLY = y;

            pointDMX = Math.abs(mX - x) / 2 + x;
            pointDMY = y;

            pointDRX = mX;
            pointDRY = y;

            drawPoints[0] = pointLeft;
            drawPoints[1] = pointTop;
            drawPoints[2] = pointRight;
            drawPoints[3] = pointBottom;

            drawAreas[0] = mX - x;
            drawAreas[1] = y - mY;
        } else if (x > mX && y < mY) { // 右上-->左下
            pointLeft = mX;
            pointTop = y;
            pointRight = x;
            pointBottom = mY;

            // 圆的坐标点
            pointULX = mX;
            pointULY = y;

            pointUMX = Math.abs(mX - x) / 2 + mX;
            pointUMY = y;

            pointURX = x;
            pointURY = y;

            pointMLX = mX;
            pointMLY = Math.abs(mY - y) / 2 + y;

            pointMRX = x;
            pointMRY = Math.abs(mY - y) / 2 + y;

            pointDLX = mX;
            pointDLY = mY;

            pointDMX = Math.abs(mX - x) / 2 + mX;
            pointDMY = mY;

            pointDRX = x;
            pointDRY = mY;

            drawPoints[0] = pointLeft;
            drawPoints[1] = pointTop;
            drawPoints[2] = pointRight;
            drawPoints[3] = pointBottom;

            drawAreas[0] = x - mX;
            drawAreas[1] = mY - y;
        }
    }

    public interface ClickListenrInterface {
        public void doConfirm();

        public void doZoomOut();

        public void doCancel();
    }

    public void setClickListener(ClickListenrInterface clickListener) {
        this.mClickListenerInterfate = clickListener;
    }
}
