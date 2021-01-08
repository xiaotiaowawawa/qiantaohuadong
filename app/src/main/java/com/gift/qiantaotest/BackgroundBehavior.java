package com.gift.qiantaotest;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.palette.graphics.Palette;

public class BackgroundBehavior extends CoordinatorLayout.Behavior {
    private int maxRange;
    private int minRange;
    private int allDistance;
    private View imageView;
    private View maskView;
    private int backgroundMaxTrans;
    private int initTrans;

    public BackgroundBehavior() {
    }

    private Drawable drawable;

    public BackgroundBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        maxRange = (int) context.getResources().getDimension(R.dimen.background_max_range);
        minRange = (int) context.getResources().getDimension(R.dimen.bar_height);
        backgroundMaxTrans = (int) context.getResources().getDimension(R.dimen.background_max_trans);
        initTrans = (int) context.getResources().getDimension(R.dimen.background_init_trans);
        allDistance = maxRange - minRange;

        //抽取图片资源的亮色或者暗色作为蒙层的背景渐变色
        Palette palette = Palette.from(BitmapFactory.decodeResource(context.getResources(),R.drawable.jj))
                .generate();
        Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
        Palette.Swatch mutedSwatch = palette.getMutedSwatch();
        int[] colors = new int[2];
        if (mutedSwatch != null) {
            colors[0] = mutedSwatch.getRgb();
            colors[1] = getTranslucentColor(0.6f, mutedSwatch.getRgb());
        } else if (vibrantSwatch != null) {
            colors[0] = vibrantSwatch.getRgb();
            colors[1] = getTranslucentColor(0.6f, vibrantSwatch.getRgb());
        } else {
            colors[0] = Color.parseColor("#4D000000");
            colors[1] = getTranslucentColor(0.6f, Color.parseColor("#4D000000"));
        }
        drawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);
    }

    @Override
    public boolean onLayoutChild(@NonNull CoordinatorLayout parent, @NonNull View child, int layoutDirection) {
        ViewCompat.setBackground(maskView,drawable);
        return super.onLayoutChild(parent, child, layoutDirection);
    }

    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
        initImageView(imageView, maskView, dependency);
        return super.onDependentViewChanged(parent, child, dependency);
    }

    @Override
    public boolean onMeasureChild(@NonNull CoordinatorLayout parent, @NonNull View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        imageView = parent.findViewById(R.id.iv_background);
        maskView = parent.findViewById(R.id.mask_view);
        return super.onMeasureChild(parent, child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
    }

    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
        if (dependency.getId() == R.id.ll_container) {
            return true;
        }
        return super.layoutDependsOn(parent, child, dependency);
    }

    private void initImageView(View background, View mask, View dependency) {
        final float trans = dependency.getTranslationY();
        if (trans <= maxRange && trans >= minRange) {
            final float percent = (trans - minRange) / allDistance;
            final float backGroundTrans = backgroundMaxTrans * percent;
            mask.setAlpha(Math.max(1 - percent, 0));
            background.setTranslationY(initTrans + backGroundTrans);
            Log.i("test1", dependency.getId()+"trans"+trans);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private int getTranslucentColor(float percent, int rgb) {
        int blue = Color.blue(rgb);
        int green = Color.green(rgb);
        int red = Color.red(rgb);
        int alpha = Color.alpha(rgb);
        alpha = Math.round(alpha * percent);
        return Color.argb(alpha, red, green, blue);
    }
}
