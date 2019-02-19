package com.example.viewtest.widget;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.ListViewCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

public class RefreshLayout extends FrameLayout {

    // 每次触摸事件中第一次接触屏幕的Y坐标
    private float downY;
    // 手指在Y轴的滑动距离
    private float dY;
    // 在刷新布局中的子View
    private View mTarget;
    // 最大Y轴滑动距离
    private float maxDY = 300;
    // 头部View
    private View headerView;
    // 下拉开始时显示的文字
    private String readyText = "下拉开始刷新";
    // 下拉到触发刷新的下拉距离之后的提示文字
    private String refreshOkText = "松开开始刷新";
    // 正在刷新时候提醒的文字
    private String refreshingText = "正在刷新";
    // 刷新成功的提示文字
    private String refreshSuc = "刷新成功！";
    // 刷新失败提醒的文字
    private String refreshFail = "刷新失败！";
    // 触发刷新的距离
    private float refreshDist = maxDY / 2;
    // 滑动多少距离才算是滑动，否则有时候是点击也会误触发滑动
    private int minDist;
    // 是否触发了刷新
    private boolean canRefresh = false;
    // 正在刷新？
    private boolean isRefreshing = false;
    // 控件状态监听
    private RefreshStateListender listener;
    // 状态表示代码
    private final int READY_REFRESH = 0; // 刚开始下拉时候的状态
    private final int CAN_REFRESH = 1; // 已经可以触发刷新的状态
    private final int ON_REFRESH = 2; // 正在刷新的状态
    private final int ON_FINISH = 3; // 刷新完成的状态

    public RefreshLayout(@NonNull Context context) {
        super(context);
        init(context);
    }

    public RefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setBackgroundColor(Color.parseColor("#cfcfcf"));
        minDist = ViewConfiguration.get(context).getScaledTouchSlop();
        if (headerView == null) {
            headerView = new TextView(context);
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_HORIZONTAL;
            headerView.setLayoutParams(params);
        }
        addView(headerView, 0);
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    private void ensureTarget() {
        if (this.mTarget == null) {
            final int count = this.getChildCount();
            for (int i = 0; i < count; i++) {
                View childView = getChildAt(i);
                if (!headerView.equals(childView)) {
                    this.mTarget = childView;
                    if (mTarget.getBackground() == null) {
                        mTarget.setBackgroundColor(Color.WHITE);
                    }
                }
            }
        }

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            downY = ev.getY();
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            final float dyTemp = ev.getY() - downY;
            return dyTemp >= minDist && !canChildScrollUp();
        } else if (ev.getAction() == MotionEvent.ACTION_UP) {
            return dY >= minDist && !canChildScrollUp();
        }
        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.ensureTarget();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.ensureTarget();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_MOVE) {
            dY = event.getY() - downY;
            if ((dY >= minDist && dY <= maxDY) && !isRefreshing) { // 如果没有正在刷新并且是下拉状态，并且没有超过最大下拉距离
                mTarget.setTranslationY(dY);
                if (dY > headerView.getMeasuredHeight()) { // 下拉距离超过headerView的高度，headerView在Y轴就要开始移动
                    headerView.setTranslationY((dY - headerView.getMeasuredHeight()) / 2);
                }
                if (dY > refreshDist) { // 已经到了可以触发刷新的下拉距离
                    configHeaderView(refreshOkText, CAN_REFRESH);
                    canRefresh = true;
                } else { // 已经下拉但是还没到可以触发刷新的距离
                    configHeaderView(readyText, READY_REFRESH);
                    canRefresh = false;
                }
            }
        } else if (action == MotionEvent.ACTION_UP) { // 松手触发刷新
            if (dY > maxDY) {
                dY = maxDY;
            }
            if (dY > minDist) {
                if (!canRefresh && !isRefreshing) { // 如果还不能触发刷新并且没有正在刷新，松手的话就回弹回去
                    ObjectAnimator.ofFloat(mTarget, "translationY", dY, 0).setDuration(500).start();
                    ObjectAnimator.ofFloat(headerView, "translationY",
                            (dY - headerView.getMeasuredHeight()) / 2, 0)
                            .setDuration(500).start();
                } else if (!isRefreshing){ // 如果已经能触发刷新并且没有正在刷新，松手的话就回弹到最大距离的一半并且提示正在刷新
                    ObjectAnimator.ofFloat(mTarget, "translationY", dY, refreshDist).setDuration(500).start();
                    ObjectAnimator animator = ObjectAnimator.ofFloat(headerView, "translationY",
                            (dY - headerView.getMeasuredHeight()) / 2, (refreshDist - headerView.getMeasuredHeight()) / 2)
                            .setDuration(500);
                    animator.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    configHeaderView(refreshingText, ON_REFRESH);
                                    isRefreshing = true;
                                    canRefresh = false;
                                }
                            });
                    animator.start();
                }
            }
            dY = 0;
        }
        return true;
    }

    private boolean canChildScrollUp() {
        return this.mTarget instanceof ListView ? ListViewCompat.canScrollList((ListView)this.mTarget, -1)
                : this.mTarget.canScrollVertically(-1);
    }

    private void configHeaderView(String text, int state, boolean isSuc) {
        if (this.headerView instanceof TextView) {
            ((TextView)headerView).setText(text);
        }
        if (listener != null) {
            switch (state) {
                case READY_REFRESH:
                    listener.onReadyRefresh(headerView);
                    break;
                case CAN_REFRESH:
                    listener.onCanRefresh(headerView);
                    break;
                case ON_REFRESH:
                    listener.onRefresh(headerView);
                    break;
                case ON_FINISH:
                    listener.onFinish(headerView, isSuc);
            }
        }
    }

    private void configHeaderView(String text, int state) {
        configHeaderView(text, state, false);
    }

    public void setRefreshStateListener(RefreshStateListender listener) {
        this.listener = listener;
    }

    // 对外提供获取Y轴滑动距离接口
    public float getDistY() {
        return this.dY;
    }

    public void refreshFinish(boolean suc) {
        if (suc) {
            configHeaderView(refreshSuc, ON_FINISH, suc);
        } else {
            configHeaderView(refreshFail, ON_FINISH, suc);
        }
        // 刷新完成，提示刷新结果后缩到顶部
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(mTarget, "translationY", refreshDist, 0);
        animator1.setDuration(200).setStartDelay(500);
        animator1.start();
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(headerView, "translationY",
                (refreshDist - headerView.getMeasuredHeight()) / 2, 0);
        animator2.setDuration(200).setStartDelay(500);
        animator2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isRefreshing = false;
                canRefresh = false;
            }
        });
        animator2.start();
    }

    public interface RefreshStateListender {
        void onReadyRefresh(View headerView);
        void onCanRefresh(View headerView);
        void onRefresh(View headerView);
        void onFinish(View headerView, boolean isSuccess);
    }
}
