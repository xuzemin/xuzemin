package com.ctv.ctvlauncher.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.ctv.ctvlauncher.R;

/**
 * 控件的点击动画
 */
public class AnimClickUtil {
    //动画执行速度
    public  final int ANIM_SPEED = 400;

    //旋转角度
    private  final float POTATION_VALUE = 7f;

    //变速器
    public OvershootInterpolator interpolator = new OvershootInterpolator(3f);

    //缩放比例
    private  final float SCALE_END = 0.8f;

    //阴影最小值
    private  final float SHADOW_END = 0;

//    private boolean isClick;    //标识点击是否有效，有效执行回掉，否则只执行动画

    /**
     * 启动按压动画
     *
     * @param view   执行动画的View
     * @param superb 效果类型【true：华丽效果】【false：缩放效果】
     * @param x      触点X坐标
     * @param y      触点Y坐标
     * @return 动画执行顶点
     */

    public int startAnimDown(View view, boolean superb, float x, float y) {
        if (!view.isClickable()) {
            return 1;
        }
        int pivot;
        // 缩放效果
        if (!superb) {
            pivot = 0;
            // 执行缩小动画【缩放效果】
            froBig_ToSmall(view);
            return pivot;
        }
        // 华丽效果
        int w = view.getWidth();
        int h = view.getHeight();
        if ((w / 5 * 2) < x && x < (w / 5 * 3) && (h / 5 * 2) < y && y < (h / 5 * 3)) {
            pivot = 0;
        } else if (x < w / 2 && y < h / 2) { // 第一象限
            if (x / (w / 2) > y / (h / 2)) {
                pivot = 1;
            } else {
                pivot = 4;
            }
        } else if (x < w / 2 && y >= h / 2) { // 第四象限
            if ((w - x) / (w / 2) > y / (h / 2)) {
                pivot = 4;
            } else {
                pivot = 3;
            }
        } else if (x >= w / 2 && y >= h / 2) { // 第三象限
            if ((w - x) / (w / 2) > (h - y) / (h / 2)) {
                pivot = 3;
            } else {
                pivot = 2;
            }
        } else { // 第二象限
            if (x / (w / 2) > (h - y) / (h / 2)) {
                pivot = 2;
            } else {
                pivot = 1;
            }
        }
        String anim = "";
        switch (pivot) {
            case 0:
                view.setPivotX(w / 2);
                view.setPivotY(h / 2);
                // 执行缩小动画【缩放效果】
                froBig_ToSmall(view);
                return pivot;
            case 1:
            case 3:
                anim = "rotationX";
                break;
            case 2:
            case 4:
                anim = "rotationY";
                break;
            default:
                break;
        }
        view.setPivotX(w / 2);
        view.setPivotY(h / 2);
        // 执行缩小动画【华丽效果】
        froBig_ToSmall(view, pivot, anim);
        return pivot;
    }

    /**
     * 启动抬起动画
     *
     * @param view  执行动画的View
     * @param pivot 动画执行顶点
     */
    public void startAnimUp(View view, int pivot) {
        if (!view.isClickable()) {
            return;
        }
        if (pivot == 0) {
            // 执行放大动画【缩放效果】
            froSmall_ToBig(view);
        } else {
            String anim = "";
            switch (pivot) {
                case 1:
                case 3:
                    anim = "rotationX";
                    break;
                case 2:
                case 4:
                    anim = "rotationY";
                    break;
            }
            // 执行放大动画【华丽效果】
            froSmall_ToBig(view, pivot, anim);
        }
    }

    /**
     * 【华丽效果】从大过渡到小
     */
    private void froBig_ToSmall(View view, int pivot, String anim) {
        float potationEnd;
        if (pivot == 3 || pivot == 4) {
            potationEnd = 0 - POTATION_VALUE;
        } else {
            potationEnd = POTATION_VALUE;
        }
        int potationStart = 0;
        if (pivot == 2 || pivot == 4) {
            potationStart = (int) view.getRotationY();
        } else {
            potationStart = (int) view.getRotationX();
        }
        ObjectAnimator animObject = ObjectAnimator.ofFloat(view, anim, potationStart, potationEnd)
                .setDuration(ANIM_SPEED);
        animObject.setInterpolator(interpolator);
        animObject.start();
    }

    /**
     * 【华丽效果】从小过渡到大
     */
    private void froSmall_ToBig(View view, int pivot, String anim) {
        int potation;
        if (pivot == 2 || pivot == 4) {
            potation = (int) view.getRotationY();
        } else {
            potation = (int) view.getRotationX();
        }
        ObjectAnimator animObject = ObjectAnimator.ofFloat(view, anim, potation, 0).setDuration(ANIM_SPEED);
        animObject.setInterpolator(interpolator);
        animObject.start();
    }

    /**
     * 【缩放效果】从大过渡到小
     */
    public void froBig_ToSmall(View view) {
        try {
            float tzStart = 0;
            Object viewTag = view.getTag(R.string.app_name);
            if (android.os.Build.VERSION.SDK_INT >= 21) {
                tzStart = view.getTranslationZ();
                if (viewTag == null || false == viewTag instanceof Float) {
                    view.setTag(R.string.app_name, tzStart);
                }
            }

            //控件的长宽高执行缩小动画
            PropertyValuesHolder tz = PropertyValuesHolder.ofFloat("translationZ", tzStart, SHADOW_END);
            PropertyValuesHolder sx = PropertyValuesHolder.ofFloat("scaleX", view.getScaleX(), SCALE_END);
            PropertyValuesHolder sy = PropertyValuesHolder.ofFloat("scaleY", view.getScaleY(), SCALE_END);

            ObjectAnimator animatorD = ObjectAnimator.ofPropertyValuesHolder(view, tz, sx, sy).setDuration(ANIM_SPEED);
            animatorD.setInterpolator(interpolator);
            animatorD.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    //动画结束时的回掉
                    if (listener1 != null ) {
                        listener1.onDownEnd();
                    }
                }
            });
            animatorD.start();
        } catch (Exception e) {

        }
    }

    /**
     * 【缩放效果】从小过渡到大
     */
    public void froSmall_ToBig(View view) {
        try {
            float tzStart = 0, tzEnd = 0;
            Object viewTag = view.getTag(R.string.app_name);
            if (android.os.Build.VERSION.SDK_INT >= 21) {
                tzStart = view.getTranslationZ();
                if (viewTag != null && viewTag instanceof Float) {
                    tzEnd = (Float) viewTag;
                }
            }

            //控件的长宽高执行缩小后的恢复动画
            PropertyValuesHolder tz = PropertyValuesHolder.ofFloat("translationZ", tzStart, tzEnd);
            PropertyValuesHolder sx = PropertyValuesHolder.ofFloat("scaleX", view.getScaleX(), 1);
            PropertyValuesHolder sy = PropertyValuesHolder.ofFloat("scaleY", view.getScaleY(), 1);

            ObjectAnimator animatorD = ObjectAnimator.ofPropertyValuesHolder(view, tz, sx, sy).setDuration(ANIM_SPEED);
            animatorD.setInterpolator(interpolator);
            animatorD.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    //动画结束时的回掉
                    if (listener1 != null ) {
                        listener1.onUpEnd();
                    }
                }
            });
            animatorD.start();
        } catch (Exception ignored) {

        }
    }

    public interface OnAnimEndListener {
        void onDownEnd();
        void onUpEnd();
    }

    private OnAnimEndListener listener1;
    public void setAnimEndListener(OnAnimEndListener listener) {
        listener1 = listener;
    }
}