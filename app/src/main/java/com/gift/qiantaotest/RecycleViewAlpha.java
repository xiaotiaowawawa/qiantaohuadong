package com.gift.qiantaotest;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

public class RecycleViewAlpha extends RecyclerView {
    public RecycleViewAlpha(@NonNull Context context) {
        super(context);
    }

    public RecycleViewAlpha(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RecycleViewAlpha(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    float downDy;
    float downDx;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {

        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        
        //使用以下方式可能会产生一个问题，就是当滑动的时候，恰好x轴大于y轴，然后y轴又大于x轴，如此循环的情况下 就会产生直观的卡顿现象，
        //对于嵌套滑动来说，应该不使用这种方式，这种方式 应该交给上方进行拦截，下方是嵌套滑动的child，应该全部交由嵌套滑动逻辑处理
        /*switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downDy = e.getY();
                downDx = e.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(e.getX() - downDx) > Math.abs(e.getY() - downDy)) {
                    Log.i("msg5","返回掉了"+Math.abs(e.getX() - downDx) +" "+ Math.abs(e.getY() - downDy));
                    return false;
                }
                break;
        }*/
        return super.onTouchEvent(e);
    }

}
