package com.gift.qiantaotest;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

public class BarBehavior extends CoordinatorLayout.Behavior {
    private Toolbar toolbar;
    private View tvTitle;
    private View tvCollect;
    private int allDistance;

    public BarBehavior() {
    }

    private int maxRange;
    private int minRange;

    public BarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        maxRange = (int) context.getResources().getDimension(R.dimen.init_trans) - (int) context.getResources().getDimension(R.dimen.bar_visiable);
        minRange = (int) context.getResources().getDimension(R.dimen.bar_height);
        allDistance = maxRange - minRange;
    }

    //依赖于nest_top
    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
        if (dependency.getId() == R.id.ll_container) {
            return true;
        }
        return super.layoutDependsOn(parent, child, dependency);
    }

    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
        initBar(tvTitle, dependency);
        initBar(tvCollect, dependency);

        return super.onDependentViewChanged(parent, child, dependency);
    }

    @Override
    public boolean onMeasureChild(@NonNull CoordinatorLayout parent, @NonNull View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        if (toolbar == null) {
            toolbar = parent.findViewById(R.id.bar);
            if (toolbar != null) {
                tvTitle = toolbar.findViewById(R.id.bar_title);
                tvCollect = toolbar.findViewById(R.id.bar_collection);
                toolbar.post(() -> {
                    minRange = toolbar.getHeight();
                });
            }
        }
        return super.onMeasureChild(parent, child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
    }

    private void initBar(View child, View dependency) {
        final float trans = dependency.getTranslationY();
        if (trans < maxRange) {
            if (child.getVisibility() == View.INVISIBLE) {
                child.setVisibility(View.VISIBLE);
            }
            changeViewAlpha(trans - minRange, allDistance, child);
        } else {
            if (child.getVisibility() == View.VISIBLE) {
                child.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void changeViewAlpha(float distance, float allDistance, View child) {
        if (distance <= allDistance) {
            float alpha = Math.max((1 - (distance / allDistance)), 0);
            child.setAlpha(alpha);
        }
    }
}
