package com.mphotool.whiteboard.view.menuviews.dialog;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mphotool.whiteboard.R;
import com.mphotool.whiteboard.utils.BaseUtils;

import static com.mphotool.whiteboard.R.id.qr;

public class QrDialog extends AlertDialog {
    private static final int DELAY = 150;
    private static final int DURATION = 1500;
    private AnimatorPlayer animator;
    private CharSequence message;
    private int size = 8;
    private AnimatedView[] spots;

    private RelativeLayout mQrLayout, mWaitingLayout;
    private ImageView mQr;
    private TextView mTvPageInfo;

    public QrDialog(Context context) {
        this(context, R.style.XDialog);
    }

    public QrDialog(Context context, CharSequence message) {
        this(context);
        this.message = message;
    }

    public QrDialog(Context context, CharSequence message, int theme) {
        this(context, theme);
        this.message = message;
    }

    public QrDialog(Context context, int theme) {
        super(context, theme);
    }

    public QrDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_layout);
        setCanceledOnTouchOutside(true);

        mQrLayout = (RelativeLayout) findViewById(R.id.qr_view);
        mWaitingLayout = (RelativeLayout) findViewById(R.id.waiting_view);
        mTvPageInfo = (TextView) findViewById(R.id.page_info);

        initProgress();
    }

    @Override public void show()
    {
        super.show();
        BaseUtils.hideNavigationBar(this);
    }

    public void refreshPageInfo(int index, int total){
        mTvPageInfo.setText(index + " / " + total);
    }

    public void showQrView(Bitmap bitmap){
        mQrLayout.setVisibility(View.VISIBLE);
        animator.stop();
        mWaitingLayout.setVisibility(View.GONE);
        mQr = (ImageView) findViewById(qr);
        mQr.setImageBitmap(bitmap);

    }

    protected void onStart() {
        super.onStart();
        this.animator = new AnimatorPlayer(createAnimations());
        this.animator.play();
    }

    protected void onStop() {
        super.onStop();
        this.animator.stop();
    }

    private void initProgress() {
        FrameLayout progress = (FrameLayout) findViewById(R.id.dmax_spots_progress);
        this.spots = new AnimatedView[8];
        int size = getContext().getResources().getDimensionPixelSize(R.dimen.spot_size);
        int progressWidth = getContext().getResources().getDimensionPixelSize(R.dimen.progress_width);
        for (int i = 0; i < this.spots.length; i++) {
            AnimatedView v = new AnimatedView(getContext());
            v.setBackgroundResource(R.drawable.bg_save_progress);
            v.setTarget(progressWidth);
            v.setXFactor(-1.0f);
            progress.addView((View) v, size, size);
            this.spots[i] = v;
        }
    }

    private Animator[] createAnimations() {
        Animator[] animators = new Animator[this.size];
        for (int i = 0; i < this.spots.length; i++) {
            Animator move = ObjectAnimator.ofFloat(this.spots[i], "xFactor", new float[]{0.0f, 1.0f});
            move.setDuration(1500);
            move.setInterpolator(new HesitateInterpolator());
            move.setStartDelay((long) (i * 150));
            animators[i] = move;
        }
        return animators;
    }
}
