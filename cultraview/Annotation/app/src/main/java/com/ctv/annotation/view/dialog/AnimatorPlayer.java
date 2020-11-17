package com.ctv.annotation.view.dialog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;

public class AnimatorPlayer extends AnimatorListenerAdapter {
    private Animator[] animators;
    private boolean interrupted = false;

    public AnimatorPlayer(Animator[] animators) {
        this.animators = animators;
    }

    public void onAnimationEnd(Animator animation) {
        if (!this.interrupted) {
            animate();
        }
    }

    public void play() {
        animate();
    }

    public void stop() {
        this.interrupted = true;
    }

    private void animate() {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(this.animators);
        set.addListener(this);
        set.start();
    }
}
