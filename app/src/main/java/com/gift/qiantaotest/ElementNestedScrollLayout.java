package com.gift.qiantaotest;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.NestedScrollingChild2;
import androidx.core.view.NestedScrollingParent2;
import androidx.core.view.ViewCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

public class ElementNestedScrollLayout extends FrameLayout implements NestedScrollingParent2 {
    private FrameLayout headConentView;
    private FrameLayout headBottomView;
    private LinearLayout headContainer;
    private FrameLayout susTopView;
    private ImageView susTopUpView;
    private TabLayout susTbContentView;
    private NestedScrollingChild2 susViewPager;
    private ConstraintLayout susContainer;
    private Toolbar topBar;
    private ImageView topBgView;
    private FrameLayout bottomView;
    //bar的高度
    private int barHeight = 0;
    //head设置的初始位置
    private int headPositionY = 0;
    private int susPositionY = 0;
    private int bottomPosition = 0;
    private ScrollListener listener;
    private int maxChildRange = 300;
    private boolean isDown = false;
    private boolean isMaxTrans = false;

    public ElementNestedScrollLayout(@NonNull Context context) {
        super(context);
    }

    public ElementNestedScrollLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ElementNestedScrollLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    public void addScrollListener(ScrollListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        headConentView = findViewById(R.id.content_head);
        headBottomView = findViewById(R.id.content_bottom);
        susTopView = findViewById(R.id.top_view);
        susTopUpView = findViewById(R.id.top_up);
        susTbContentView = findViewById(R.id.tb_content);
        susViewPager = findViewById(R.id.vp_content);
        topBar = findViewById(R.id.top_bar);
        topBgView = findViewById(R.id.top_bg_view);
        bottomView = findViewById(R.id.bottom_view);
        headContainer = findViewById(R.id.content_coll);
        susContainer = findViewById(R.id.sus_container);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //设置头部位置和高度
        changeHeadView();
        final int topContentHeight = headConentView.getMeasuredHeight();
        if (topContentHeight != 0) {
            //悬浮内容位置
            susContainer.setY(topContentHeight);
            susPositionY = (topContentHeight + susTopView.getMeasuredHeight());
        }
        bottomPosition = (int) bottomView.getY();
    }

    private void changeHeadView() {
        final int topBarHeight = topBar.getMeasuredHeight();
        barHeight = topBarHeight;
        int toolbarBottom = topBar.getBottom();
        ViewGroup.LayoutParams layoutParams = headContainer.getLayoutParams();
        layoutParams.height = getMeasuredHeight() - topBarHeight;
        headPositionY = toolbarBottom;
        headConentView.setY(toolbarBottom);
    }

    //是否允许child接受分发
    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        if (target instanceof ViewPager2) {
            return true;
        }
        return false;
    }

    //接受分发之后回调
    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {
        Log.i("msg1", "target 接受分发了");
    }

    //child停止滑动
    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        //资源回收
    }

    //child消费之后，再次请求parent处理
    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {

    }

    //child 消费之前 请求parent处理
    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        //手势向上
        if (dy < 0) {
            //如果child没有到达顶部,消费，这里需要有一个折叠的距离监听
            final View child = susTbContentView;
            final float childY = child.getY();
            if (childY > barHeight) {
                final float distance = childY - barHeight;
                float consumedY = (distance + dy) > 0 ? dy : -distance;
                consumed[1] = (int) consumedY; /*((int) (dy-consumedY));*/
                final float transy = childY + consumedY;
                child.setTranslationY(transy);
                if (listener != null) {
                    listener.childTranslateY(consumedY);
                }
            }
            //如果到达顶部，不消费
        } else if (dx > 0) {
            //手势向下
            if (listener != null && listener.isEnable()) {
                isDown = true;
                //ACTION_MOVE
                //head和悬浮view是否同步滑动，head到达最大滑动位置就不滑动，悬浮view话哦的那个到某个位置就滑动底部消失
                final float curHeadPosition = headContainer.getY();
                final View child = susTbContentView;
                final float childTransY = child.getTranslationY();
                if (curHeadPosition > headPositionY) {
                    //head 滑动，不能超过最大滑动距离
                    float transHeadY = curHeadPosition + dy;
                    int consumedHeadY;
                    if (transHeadY > headPositionY) {
                        consumedHeadY = dy;
                    } else {
                        consumedHeadY = ((int) curHeadPosition - headPositionY);
                        transHeadY = headPositionY;
                    }
                    //child滑动，不能超过最大滑动距离
                    final float consumedChildY = childExpectTransY(dy, childTransY, child);
                    if (consumedHeadY != 0) {
                        headContainer.setTranslationY(consumedHeadY);
                    }
                    transSus(dy, consumedChildY, child);
                    consumed[1] = (int) Math.max(consumedChildY, consumedHeadY);
                } else if (childTransY > 0 && childTransY < maxChildRange) {
                    //child滑动
                    final float consumedChildY = (int) childExpectTransY(dy, childTransY, child);
                    transSus(dy, consumedChildY, child);
                    consumed[1] = (int) consumedChildY;
                }
            }
        }
    }

    private void transSus(int dy, float consumed, View child) {
        if (consumed != 0) {
            if (consumed != dy) {
                //消失
                isMaxTrans = true;
                child.setTranslationY(getMeasuredHeight());
                bottomView.setTranslationY(getMeasuredHeight());
            } else {
                final float transY = child.getTranslationY();
                child.setTranslationY(transY + consumed);
                bottomView.setTranslationY(transY + consumed);
            }
        }
        if (listener != null) {
            listener.childTranslateY(consumed);
        }
    }

    private final float childExpectTransY(int dy, float childTransY, View child) {
        final float transY = dy + childTransY;
        if (transY < maxChildRange) {
            return dy;
        } else if (transY > maxChildRange) {
            final float cons = maxChildRange - childTransY;
            return cons;
        }
        return 0;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_UP:
                //手势向下抬起,是否达到某个距离，没有就恢复，有就移动到底部
                if (isDown) {
                    isDown = false;
                    if (isMaxTrans) {
                        isMaxTrans = true;
                    } else {
                        headContainer.setTranslationY(headPositionY);
                        bottomView.setTranslationY(bottomPosition);
                        susTbContentView.setTranslationY(susPositionY);
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    //child再次请求
    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return super.onNestedFling(target, velocityX, velocityY, consumed);
    }

    //child 请求
    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return super.onNestedPreFling(target, velocityX, velocityY);
    }

    public interface ScrollListener {
        boolean isEnable();

        int childTranslateY(float dy);
    }
}
