package com.mphotool.whiteboard.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.hardware.SensorManager;

public class PanelUtils {
    private static final boolean SHOW_COMSUME = false;
    private static final String TOUCH_CMD_ACTION = "touch.uart.cmd";
    public static long startTime = 0;

    private static boolean mMultiPen = false; // 支持多点触控书写
    private static boolean mMarker = false; // 支持批注
    private static boolean mWriteSwitch = false; // 书写是否切换

    private static boolean mRecordWriteMode = false; // 书写是否切换

    static class Vectorf3D {
        float x;
        float y;
        float z;

        Vectorf3D() {
        }
    }

    public static void exchangeRect(Rect r1, Rect r2) {
        Rect r = new Rect(r2);
        r2.set(r1);
        r1.set(r);
    }

    public static boolean isIn2Rect(int x, int y, Rect r1, Rect r2) {
        return isBetween2Rect(x, y, r1, r2);
    }

    public static boolean isRectIntersectRegion(Rect rect, Region region) {
        return region.contains(rect.left, rect.top) || region.contains(rect.right, rect.top) || region.contains(rect.right, rect.bottom) || region.contains(rect.left, rect.bottom);
    }

    public static boolean isIntersectBetween2Rect(Rect src, Rect r1, Rect r2) {
        if (isBetween2Rect(src.left, src.top, r1, r2) || isBetween2Rect(src.right, src.top, r1, r2) || isBetween2Rect(src.right, src.bottom, r1, r2) || isBetween2Rect(src.left, src.bottom, r1, r2)) {
            return true;
        }
        return false;
    }

    public static boolean isIntersectBetween2Rect(int x, int y, int radius, Rect r1, Rect r2) {
        if (isBetween2Rect(x - radius, y - radius, r1, r2) || isBetween2Rect(x + radius, y - radius, r1, r2) || isBetween2Rect(x + radius, y + radius, r1, r2) || isBetween2Rect(x - radius, y + radius, r1, r2)) {
            return true;
        }
        return false;
    }

    public static Region rectToRegion(Rect r1, Rect r2) {
        if (r1.left > r2.left) {
            exchangeRect(r1, r2);
        }
        Region region = new Region();
        Rect bound = new Rect();
        Path path = new Path();
        if (r1.left == r2.left) {
            if (r1.top > r2.top) {
                exchangeRect(r1, r2);
            }
            path.moveTo((float) r1.left, (float) r1.top);
            path.lineTo((float) r1.right, (float) r1.top);
            path.lineTo((float) r2.right, (float) r2.bottom);
            path.lineTo((float) r1.bottom, (float) r1.left);
            path.close();
        } else if (r1.top == r2.top) {
            path.moveTo((float) r1.left, (float) r1.top);
            path.lineTo((float) r2.right, (float) r2.top);
            path.lineTo((float) r2.right, (float) r2.bottom);
            path.lineTo((float) r1.bottom, (float) r1.left);
            path.close();
        } else if (r1.top > r2.top) {
            path.moveTo((float) r1.left, (float) r1.bottom);
            path.lineTo((float) r1.left, (float) r1.top);
            path.lineTo((float) r2.left, (float) r2.top);
            path.lineTo((float) r2.right, (float) r2.top);
            path.lineTo((float) r2.right, (float) r2.bottom);
            path.lineTo((float) r1.right, (float) r1.bottom);
            path.close();
        } else {
            path.moveTo((float) r1.left, (float) r1.bottom);
            path.lineTo((float) r1.left, (float) r1.top);
            path.lineTo((float) r1.right, (float) r1.top);
            path.lineTo((float) r2.right, (float) r2.top);
            path.lineTo((float) r2.right, (float) r2.bottom);
            path.lineTo((float) r2.left, (float) r2.bottom);
            path.close();
        }
        bound.union(r1);
        bound.union(r2);
        region.setPath(path, new Region(bound));
        return region;
    }

    public static boolean isBetween2Rect(int x, int y, Rect r1, Rect r2) {
        if (r1.left > r2.left) {
            exchangeRect(r1, r2);
        }
        if (r1.left == r2.left) {
            if (r1.top > r2.top) {
                exchangeRect(r1, r2);
            }
            if (r1.left <= x && x <= r1.right && r1.top <= y && y <= r2.bottom) {
                return true;
            }
        } else if (r1.top == r2.top) {
            if (r1.left <= x && x <= r2.right && r1.top <= y && y <= r2.bottom) {
                return true;
            }
        } else if (r1.top > r2.top) {
            if (r1.contains(x, y) || r2.contains(x, y)) {
                return true;
            }
            Region region = new Region();
            Rect bound = new Rect();
            Path path = new Path();
            path.moveTo((float) r1.left, (float) r1.bottom);
            path.lineTo((float) r1.left, (float) r1.top);
            path.lineTo((float) r2.left, (float) r2.top);
            path.lineTo((float) r2.right, (float) r2.top);
            path.lineTo((float) r2.right, (float) r2.bottom);
            path.lineTo((float) r1.right, (float) r1.bottom);
            path.close();
            bound.union(r1);
            bound.union(r2);
            region.setPath(path, new Region(bound));
            return region.contains(x, y);
        } else if (r1.contains(x, y) || r2.contains(x, y)) {
            return true;
        } else {
            Region region = new Region();
            Rect bound = new Rect();
            Path path = new Path();
            path.moveTo((float) r1.left, (float) r1.bottom);
            path.lineTo((float) r1.left, (float) r1.top);
            path.lineTo((float) r1.right, (float) r1.top);
            path.lineTo((float) r2.right, (float) r2.top);
            path.lineTo((float) r2.right, (float) r2.bottom);
            path.lineTo((float) r2.left, (float) r2.bottom);
            path.close();
            bound.union(r1);
            bound.union(r2);
            region.setPath(path, new Region(bound));
            return region.contains(x, y);
        }
        return false;
    }

    public static boolean isInTriangle(float px, float py, float x1, float y1, float x2, float y2, float x3, float y3) {
        return isInTriangle((int) px, (int) py, (int) x1, (int) y1, (int) x2, (int) y2, (int) x3, (int) y3);
    }

    public static boolean isInTriangle(int px, int py, int x1, int y1, int x2, int y2, int x3, int y3) {
        int max = px - x1;
        int may = py - y1;
        int mbx = px - x2;
        int mby = py - y2;
        int mcy = py - y3;
        int mcx = px - x3;
        int a = (max * mby) - (may * mbx);
        int b = (mbx * mcy) - (mby * mcx);
        int c = (mcx * may) - (mcy * max);
        if ((a > 0 || b > 0 || c > 0) && (a <= 0 || b <= 0 || c <= 0)) {
            return false;
        }
        return true;
    }

    public static boolean isInTriangle(Point A, Point B, Point C, Point P) {
        Point MA = new Point(P.x - A.x, P.y - A.y);
        Point MB = new Point(P.x - B.x, P.y - B.y);
        Point MC = new Point(P.x - C.x, P.y - C.y);
        int a = (MA.x * MB.y) - (MA.y * MB.x);
        int b = (MB.x * MC.y) - (MB.y * MC.x);
        int c = (MC.x * MA.y) - (MC.y * MA.x);
        if ((a > 0 || b > 0 || c > 0) && (a <= 0 || b <= 0 || c <= 0)) {
            return false;
        }
        return true;
    }

    public static Rect rectFtoRect(RectF rf, Rect r, int padding) {
        r.set(((int) rf.left) - padding, ((int) rf.top) - padding, ((int) rf.right) + padding, ((int) rf.bottom) + padding);
        return r;
    }

    public static RectF rectToRectF(Rect r, RectF rf, float padding) {
        rf.set(((float) r.left) - padding, ((float) r.top) - padding, ((float) r.right) + padding, ((float) r.bottom) + padding);
        return rf;
    }

    public static boolean isRectImpact(Rect r1, Rect r2) {
        return Rect.intersects(r1, r2);
    }

    public static Rect rectAddWidth(Rect r1, int width) {
        r1.left -= width;
        r1.top -= width;
        r1.right += width;
        r1.bottom += width;
        return r1;
    }

    public static Rect rectdelWidth(Rect r1, int width) {
        r1.left += width;
        r1.top += width;
        r1.right -= width;
        r1.bottom -= width;
        return r1;
    }



    public static Rect buildRect(int x1, int y1, int x2, int y2) {
        Rect r = new Rect();
        if (x1 > x2) {
            int tmp = x2;
            x2 = x1;
            x1 = tmp;
        }
        if (y1 > y2) {
            int tmp = y2;
            y2 = y1;
            y1 = tmp;
        }
        r.set(x1, y1, x2, y2);
        return r;
    }

    public static boolean isIntersectLine(float p1x, float p1y, float p2x, float p2y, float q1x, float q1y, float q2x, float q2y) {
        Vectorf3D v1 = new Vectorf3D();
        Vectorf3D v2 = new Vectorf3D();
        Vectorf3D v3 = new Vectorf3D();
        Vectorf3D v4 = new Vectorf3D();
        v1.x = p1x - q1x;
        v1.y = p1y - q1y;
        v1.z = 0.0f;
        v2.x = q2x - q1x;
        v2.y = q2y - q1y;
        v2.z = 0.0f;
        v3.x = (v1.y * v2.z) - (v1.z * v2.y);
        v3.y = (v1.z * v2.x) - (v1.x * v2.z);
        v3.z = (v1.x * v2.y) - (v1.y * v2.x);
        v1.x = p2x - q1x;
        v1.y = p2y - q1y;
        v1.z = 0.0f;
        v4.x = (v2.y * v1.z) - (v2.z * v1.y);
        v4.y = (v2.z * v1.x) - (v2.x * v1.z);
        v4.z = (v2.x * v1.y) - (v2.y * v1.x);
        float fTemp1 = ((v3.x * v4.x) + (v3.y * v4.y)) + (v3.z * v4.z);
        v1.x = q1x - p1x;
        v1.y = q1y - p1y;
        v1.z = 0.0f;
        v2.x = p2x - p1x;
        v2.y = p2y - p1y;
        v2.z = 0.0f;
        v3.x = (v1.y * v2.z) - (v1.z * v2.y);
        v3.y = (v1.z * v2.x) - (v1.x * v2.z);
        v3.z = (v1.x * v2.y) - (v1.y * v2.x);
        v1.x = q2x - p1x;
        v1.y = q2y - p1y;
        v1.z = 0.0f;
        v4.x = (v2.y * v1.z) - (v2.z * v1.y);
        v4.y = (v2.z * v1.x) - (v2.x * v1.z);
        v4.z = (v2.x * v1.y) - (v2.y * v1.x);
        float fTemp2 = ((v3.x * v4.x) + (v3.y * v4.y)) + (v3.z * v4.z);
        if (fTemp1 < SensorManager.LIGHT_NO_MOON || fTemp2 < SensorManager.LIGHT_NO_MOON) {
            return false;
        }
        return true;
    }

    public static boolean isIntersectLine(PointF p1, PointF p2, PointF q1, PointF q2) {
        Vectorf3D v1 = new Vectorf3D();
        Vectorf3D v2 = new Vectorf3D();
        Vectorf3D v3 = new Vectorf3D();
        Vectorf3D v4 = new Vectorf3D();
        v1.x = p1.x - q1.x;
        v1.y = p1.y - q1.y;
        v1.z = 0.0f;
        v2.x = q2.x - q1.x;
        v2.y = q2.y - q1.y;
        v2.z = 0.0f;
        v3.x = (v1.y * v2.z) - (v1.z * v2.y);
        v3.y = (v1.z * v2.x) - (v1.x * v2.z);
        v3.z = (v1.x * v2.y) - (v1.y * v2.x);
        v1.x = p2.x - q1.x;
        v1.y = p2.y - q1.y;
        v1.z = 0.0f;
        v4.x = (v2.y * v1.z) - (v2.z * v1.y);
        v4.y = (v2.z * v1.x) - (v2.x * v1.z);
        v4.z = (v2.x * v1.y) - (v2.y * v1.x);
        float fTemp1 = ((v3.x * v4.x) + (v3.y * v4.y)) + (v3.z * v4.z);
        v1.x = q1.x - p1.x;
        v1.y = q1.y - p1.y;
        v1.z = 0.0f;
        v2.x = p2.x - p1.x;
        v2.y = p2.y - p1.y;
        v2.z = 0.0f;
        v3.x = (v1.y * v2.z) - (v1.z * v2.y);
        v3.y = (v1.z * v2.x) - (v1.x * v2.z);
        v3.z = (v1.x * v2.y) - (v1.y * v2.x);
        v1.x = q2.x - p1.x;
        v1.y = q2.y - p1.y;
        v1.z = 0.0f;
        v4.x = (v2.y * v1.z) - (v2.z * v1.y);
        v4.y = (v2.z * v1.x) - (v2.x * v1.z);
        v4.z = (v2.x * v1.y) - (v2.y * v1.x);
        float fTemp2 = ((v3.x * v4.x) + (v3.y * v4.y)) + (v3.z * v4.z);
        if (fTemp1 < SensorManager.LIGHT_NO_MOON || fTemp2 < SensorManager.LIGHT_NO_MOON) {
            return false;
        }
        return true;
    }

    public static void sendToUart(Context context, byte[] bytes) {
        Intent intent = new Intent(TOUCH_CMD_ACTION);
        intent.putExtra("bytes", bytes);
        intent.putExtra("type", 5);
        context.sendBroadcast(intent);
    }

    public static void sendInEraseMode(Context context, boolean en) {
        int i;
        byte[] datas = new byte[2];
        if (en) {
            i = 1;
        } else {
            i = 0;
        }
        datas[0] = (byte) i;
        datas[1] = (byte) 3;
        sendToUart(context, nsTouchGenerateCmdPackage(3, 11, datas, datas.length));
    }

    public static byte[] nsTouchGenerateCmdPackage(int mcmd, int scmd, byte[] data, int dataLength) {
        byte[] cmd = new byte[((dataLength + 9) + 6)];
        cmd[0] = (byte) -41;
        cmd[1] = (byte) -82;
        cmd[2] = (byte) 1;
        cmd[3] = (byte) 0;
        cmd[4] = (byte) (dataLength + 9);
        cmd[5] = (byte) -51;
        cmd[6] = (byte) 1;
        cmd[7] = (byte) 119;
        cmd[8] = (byte) 1;
        cmd[9] = (byte) -35;
        cmd[10] = (byte) mcmd;
        cmd[11] = (byte) scmd;
        cmd[12] = (byte) -52;
        cmd[13] = (byte) dataLength;
        for (int i = 0; i < dataLength; i++) {
            cmd[(i + 9) + 5] = data[i];
        }
        cmd[cmd.length - 1] = getChecksum(cmd);
        return cmd;
    }

    private static byte getChecksum(byte[] bytes) {
        byte cs = (byte) 0;
        for (int i = 0; i < bytes.length - 1; i++) {
            cs = (byte) (bytes[i] + cs);
        }
        return cs;
    }

	/**
	 * 设置多手指触控
	 * @param multiPen
	 */
	public static void setMultiPen(boolean multiPen) {
        mMultiPen = multiPen;
    }

    public static void setWriteSwitch(boolean mSwitch){
        mWriteSwitch = mSwitch;
    }

    public static boolean isWriteMode(){
	    return mWriteSwitch;
    }
	/**
	 * 获取多手指触控
	 */
    public static boolean isMultiPen() {
        return mMultiPen;
    }

	/**
	 * 设置批注模式
	 * @param marker
	 */
	public static void setMarker(boolean marker) {
		mMarker = marker;
	}

	/**
	 * 获得批注模式
	 * @return
	 */
	public static boolean isMarker() {
		return mMarker;
	}
}
