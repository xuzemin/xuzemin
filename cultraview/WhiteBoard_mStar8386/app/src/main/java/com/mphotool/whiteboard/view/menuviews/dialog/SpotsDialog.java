package com.mphotool.whiteboard.view.menuviews.dialog;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mphotool.whiteboard.R;
import com.mphotool.whiteboard.utils.BaseUtils;

public class SpotsDialog extends AlertDialog {
    private static final int DELAY = 150;
    private static final int DURATION = 1500;
    private AnimatorPlayer animator;
    private CharSequence message;
    private int size = 6;
    private AnimatedView[] spots;

    public SpotsDialog(Context context) {
        this(context, R.style.XDialog);
    }

    public SpotsDialog(Context context, CharSequence message) {
        this(context);
        this.message = message;
    }

    public SpotsDialog(Context context, CharSequence message, int theme) {
        this(context, theme);
        this.message = message;
    }

    public SpotsDialog(Context context, int theme) {
        super(context, theme);
    }

    public SpotsDialog(Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dmax_spots_dialog);
        setCanceledOnTouchOutside(false);
        initMessage();
        initProgress();
    }

    @Override public void show()
    {
        super.show();
        BaseUtils.hideNavigationBar(this);
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

    public void setMessage(CharSequence message) {
        ((TextView) findViewById(R.id.dmax_spots_title)).setText(message);
    }

    private void initMessage() {
        if (this.message != null && this.message.length() > 0) {
            ((TextView) findViewById(R.id.dmax_spots_title)).setText(this.message);
        }
    }

    private void initProgress() {
        FrameLayout progress = (FrameLayout) findViewById(R.id.dmax_spots_progress);
        this.spots = new AnimatedView[6];
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
