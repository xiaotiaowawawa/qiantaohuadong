package com.gift.qiantaotest;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.OverScroller;


import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.io.File;
import java.lang.reflect.Field;

import static androidx.core.view.ViewCompat.TYPE_NON_TOUCH;
import static androidx.core.view.ViewCompat.TYPE_TOUCH;

public class MusicContentBehavior extends CoordinatorLayout.Behavior {
    private float maxRange;
    private float minRange;
    private int initTrans;
    private Toolbar topBar;
    private FrameLayout flTop;
    private int susHideDistance;
    private int susTopHideRange;
    private int topHeight;
    private View child;
    private boolean isStopFiling;
    private View nestContent;
    private boolean isUpFling;
    //默认回弹100像素
    private final float DEFAULT_BONCE = 100;

    public MusicContentBehavior() {

    }

    private ValueAnimator bonceAnimator;

    public MusicContentBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        bonceAnimator = new ValueAnimator();
        bonceAnimator.addUpdateListener(animation -> {
            final Object value = animation.getAnimatedValue();
            if (value instanceof Float) {
                final Float f_value = (Float) value;
                if (child != null) {
                    Log.i("msg1", "f_Value" + f_value);
                    child.setTranslationY(f_value);
                    susTopView(child, flTop);
                }
            }
        });

    }

    //被依赖的控件位置发生改变或者对于嵌套滑动接口执行，依赖的控件drawRect 发生改变 也会回调这个接口
    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
        initView(parent, child);
        if (dependency.getId() == R.id.nest_top) {
            return true;
        }
        return super.layoutDependsOn(parent, child, dependency);
    }

    boolean isInit;

    private void initView(View parent, View child) {
        if (isInit) {
            return;
        }
        Context context = parent.getContext();
        this.child = child;
        isInit = true;
        initTrans = (int) context.getResources().getDimension(R.dimen.init_trans);
        //下移200像素达到最大
        susHideDistance = (int) context.getResources().getDimension(R.dimen.sus_hide_distance);
        susTopHideRange = initTrans - susHideDistance;
        maxRange = initTrans + (int) context.getResources().getDimension(R.dimen.init_trans_max_range);
        topBar = parent.findViewById(R.id.bar);
        minRange = (int) context.getResources().getDimension(R.dimen.bar_height);
        flTop = parent.findViewById(R.id.nest_top);
        nestContent = parent.findViewById(R.id.ll_container);
        flTop.post(() -> {
            topHeight = flTop.getHeight();
            flTop.setTranslationY(initTrans - topHeight);
        });
        nestContent.setTranslationY(initTrans);
    }

    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
        Log.i("msg2", "onDependentViewChanged");
        return super.onDependentViewChanged(parent, child, dependency);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        if (target instanceof RecyclerView && target.canScrollVertically(axes)) {
            if (bonceAnimator.isStarted()){
                bonceAnimator.cancel();
            }
            return true;
        }
        return super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type);
    }

    @Override
    public void onNestedScrollAccepted(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        super.onNestedScrollAccepted(coordinatorLayout, child, directTargetChild, target, axes, type);
    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
        if (type == TYPE_TOUCH) {
            //触摸事件状态初始化
            touchEventRest();
        }
        final float trans = nestContent.getTranslationY();
        Log.i("msg1", "onNestedPreScroll type" + type + " " + dy);
        if (dy < 0) {
            if (target.canScrollVertically(dy)) {
                Log.i("msg1", "return");
                return;
            }
            if (type == TYPE_NON_TOUCH) {
                boolean isIntercept = false;
                //大于初始值要提供一个回弹效果
                if (trans > initTrans) {
                    Log.i("msg1", "stop");
                    stopAndConsume(target, consumed, dy);
                    isIntercept = true;
                } else {
                    isUpFling = true;
                }
                if (isUpFling) {
                    final float range = initTrans - trans;
                    //允许下滑 NO_TOUCH的时候，在initTrans 的 0-30像素值之间停止
                    if (range >= 0 && range <= 30) {
                        stopAndConsume(target, consumed, dy);
                        isIntercept = true;
                    }
                }
                if (isIntercept)
                    return;
            }
            if (nestContent.getTranslationY() == maxRange) {
                if (type == TYPE_TOUCH) {
                    consumed[1] = dy;
                }
                return;
            }
            //手势向下
            downTrans(dy, nestContent, consumed, maxRange);
        } else {
            //手势向上
            if (checkOutTopRange(nestContent.getTranslationY(), target)) {
                return;
            }
            upTrans(dy, nestContent, consumed, minRange);
        }
        susTopView(nestContent, flTop);
    }

    private void stopAndConsume(View target, int[] consumed, float dy) {
        stopFling(target);
        consumed[1] = (int) dy;
    }

    private void consumeAndTrans(final float trans, final float dy, View child, final int[] consume) {
        child.setTranslationY(trans);
        Log.i("test111", child.getY()+"");
        consume[1] = (int) dy;
    }

    private void stopFling(View target) {
        Log.i("msg1", "stop fiing");
        final long time = System.currentTimeMillis();
        if (target instanceof NestedScrollView) {
            NestedScrollView nestedScrollView = (NestedScrollView) target;
            try {
                Class<? extends NestedScrollView> clazz = nestedScrollView.getClass();
                Field field = clazz.getDeclaredField("mScroller");
                field.setAccessible(true);
                final OverScroller overScroller = (OverScroller) field.get(nestedScrollView);
                if (overScroller != null)
                    overScroller.abortAnimation();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (target instanceof RecyclerView) {
            RecyclerView rv = (RecyclerView) target;
            rv.stopScroll();
        } else if (target instanceof ViewPager2) {
            try {
                ViewPager2 vp = (ViewPager2) target;
                Field rvField = vp.getClass().getDeclaredField("mRecyclerView");
                rvField.setAccessible(true);
                RecyclerView rv = (RecyclerView) rvField.get(vp);
                rv.stopScroll();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void upTrans(final int dy, final View child, final int[] consume, final float topRange) {
        final float childTrans = child.getTranslationY();
        final float expireTrans = childTrans - dy;
        if (expireTrans >= topRange) {
            Log.i("msg1", "移动view up up up");
            consumeAndTrans(expireTrans, dy, child, consume);
        } else {
            if (childTrans >= topRange) {
                final float cons = childTrans - minRange;
                Log.i("msg1", "移动view up up up");
                consumeAndTrans(minRange, cons, child, consume);
            }
        }

    }

    private void downTrans(final int dy, final View child, final int[] consume, final float downRange) {
        final float childTrans = child.getTranslationY();
        final float expireTrans = childTrans - dy;
        if (expireTrans <= downRange) {
            Log.i("msg1", "移动view down down down");
            consumeAndTrans(expireTrans, dy, child, consume);
        } else {
            if (childTrans <= downRange) {
                final float cons = childTrans - downRange;
                Log.i("msg1", "移动view down down down");
                consumeAndTrans(downRange, cons, child, consume);
            }
        }
    }

    private boolean checkOutTopRange(final float trans, View target) {
        if (trans <= minRange) {
            return true;
        }
        return false;
    }


    //上方view跟随滑动
    private void susTopView(View child, View dependency) {
        if (child == null || dependency == null) return;
        final float childTrans = child.getTranslationY();
        if (childTrans <= susTopHideRange) {
            if (dependency.getVisibility() == View.VISIBLE) {
                dependency.setVisibility(View.GONE);
            }
        } else {
            if (dependency.getVisibility() == View.GONE) {
                dependency.setVisibility(View.VISIBLE);
            }
            final float depenceTrans = childTrans - topHeight;
            dependency.setTranslationY(depenceTrans);
            if (childTrans <= initTrans) {
                changeAlpha(childTrans - susTopHideRange, susHideDistance, dependency);
            }
        }
    }

    private void changeAlpha(final float distance, int allDistance, View child) {
        if (distance <= allDistance) {
            final float precent = distance / allDistance;
            child.setAlpha(precent);
        }
    }

    @Override
    public void onStopNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int type) {
        super.onStopNestedScroll(coordinatorLayout, child, target, type);
        final float childTrans = nestContent.getTranslationY();
        //isStopFling防止fling之前完成了动画，然后再stop 又会进行动画，也就是防止执行两次动画
        if ((childTrans > initTrans) && !(isStopFiling && type == TYPE_NON_TOUCH)) {
            isStopFiling = true;
            Log.i("msg1", "onStopNestedScroll" + type);
            if (bonceAnimator.isStarted()) {
                Log.i("msg1", "动画取消");
                bonceAnimator.cancel();
            }
            startDownBonceAnimator(childTrans);
        } else {
            Log.i("msg1", "" + childTrans);
        }
    }

    private void touchEventRest() {
        isStopFiling = false;
        isUpFling = false;
    }

    private void startDownBonceAnimator(float childTrans) {
        startBonceAnimator(childTrans, initTrans, maxRange, DEFAULT_BONCE, 500);
    }

    private void startUpToDownBonceAnimator(float childTrans) {
        startBonceAnimator(childTrans, initTrans, minRange, DEFAULT_BONCE, 1000);
    }

    private void startBonceAnimator(float childTrans, float initTrans, float rangeTrans, final float defaultTrans, final int animatorTime) {
        float expireTrans = childTrans;
        if (rangeTrans < childTrans) {
            expireTrans -= defaultTrans;
            expireTrans = Math.max(expireTrans, rangeTrans);
        } else {
            expireTrans += defaultTrans;
            expireTrans = Math.min(expireTrans, rangeTrans);
        }

        final long duration = (long) ((Math.abs(rangeTrans - childTrans) * 1.0 / Math.abs(rangeTrans - initTrans)) * animatorTime);
        Log.i("msg1","动画事件"+duration);
        if (duration > 0) {
            bonceAnimator.setDuration(duration);
            if (rangeTrans == initTrans) {
                Log.i("msg1", "设置动画" + childTrans + "  " + initTrans);
                bonceAnimator.setFloatValues(childTrans, initTrans);
            } else {
                Log.i("msg1", "设置动画" + childTrans + "  " + expireTrans + " " + initTrans);
                bonceAnimator.setFloatValues(childTrans, expireTrans, initTrans);
            }
        } else {
            if (rangeTrans == initTrans) {
                return;
            }
            bonceAnimator.setDuration(100);
            Log.i("msg1", "设置动画" + rangeTrans + "  " + initTrans);
            bonceAnimator.setFloatValues(expireTrans, initTrans);
        }
        bonceAnimator.start();
    }

    private boolean isStartFilingOnIntercept(View target, int dy, int[] consume) {
        if (dy < 0) {
            final float targetTrans = child.getTranslationY();
            if (!(targetTrans == minRange && target.canScrollVertically(((int) dy))) && (targetTrans < initTrans)) {
                stopFling(target);
                startUpToDownBonceAnimator(targetTrans);
                consume[1] = dy;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onNestedPreFling(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, float velocityX, float velocityY) {
        final float targetTrans = nestContent.getTranslationY();
        //从顶部到初始位置，我们选择拦截，用动画滑动
        if (velocityY < 0 && targetTrans > 0 && !target.canScrollVertically(velocityY > 0 ? 1 : -1)) {
            Log.i("msg1", "速度拦截");
            startUpToDownBonceAnimator(targetTrans);
            return true;
        }
        return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY);
    }
}
