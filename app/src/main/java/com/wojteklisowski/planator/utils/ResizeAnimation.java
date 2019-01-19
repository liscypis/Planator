package com.wojteklisowski.planator.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ResizeAnimation extends Animation {
    private int mStartHeight;
    private int mFinalHeight;
    private View view;

    public ResizeAnimation(int mStartHeight, int mFinalHeight, View view) {
        this.mStartHeight = mStartHeight;
        this.mFinalHeight = mFinalHeight;
        this.view = view;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation transformation) {
        int newHeight;
        if (view.getHeight() != mFinalHeight) {
            newHeight = (int) (mStartHeight + ((mFinalHeight - mStartHeight) * interpolatedTime));
            view.getLayoutParams().height = newHeight;
            view.requestLayout();
        }
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}
