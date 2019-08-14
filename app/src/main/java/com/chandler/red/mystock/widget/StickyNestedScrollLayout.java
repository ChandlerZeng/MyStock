package com.chandler.red.mystock.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

/**
 * create by lym on 2018/7/20.
 */
public class StickyNestedScrollLayout extends LinearLayout implements NestedScrollingParent {

    private static final long DEFAULT_DURATION = 250L;
    private View mHeaderView;
    private View mBodyView;
    private int mMaxScrollHeight;
    private ValueAnimator mScrollAnimator;

    public void setHeaderRetainHeight(int headerRetainHeight) {
        mHeaderRetainHeight = headerRetainHeight;
    }

    private int mHeaderRetainHeight;

    public StickyNestedScrollLayout(Context context) {
        this(context, null);
    }

    public StickyNestedScrollLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickyNestedScrollLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mHeaderView = getChildAt(0);
        mBodyView = getChildAt(1);
        mHeaderView.setFocusable(true);
        mHeaderView.setClickable(true);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (getChildCount() > 1) {
            throw new IllegalStateException("StickyNestedScrollLayout can host only two direct child");
        }
        super.addView(child, index, params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHeaderView.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        mMaxScrollHeight = mHeaderView.getMeasuredHeight() - mHeaderRetainHeight;
        //设置主体的高度：代码中设置match_parent
        if (mBodyView.getLayoutParams().height < getMeasuredHeight() - mHeaderRetainHeight) {
            mBodyView.getLayoutParams().height = getMeasuredHeight() - mHeaderRetainHeight;
        }
        setMeasuredDimension(getMeasuredWidth(), mBodyView.getLayoutParams().height + mHeaderView.getMeasuredHeight());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    private boolean canScroll(View target, int dy) {
        boolean hiddenTop = dy > 0 && getScrollY() < mMaxScrollHeight;
        boolean showTop = dy < 0 && getScrollY() > 0 && !ViewCompat.canScrollVertically(target, -1);
        return hiddenTop || showTop;
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        if (canScroll(target, dy)) {
            scrollBy(0, dy);
            consumed[1] = dy;
        }
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        int headerViewScrollDis = mMaxScrollHeight - getScrollY();
        //velocityY > 0  上滑
        if (velocityY > 0 && headerViewScrollDis > 0) {
            startFling(velocityY);
            return true;
        }
        //velocityY < 0 下滑
        if (velocityY < 0 && !ViewCompat.canScrollVertically(target, -1) && getScrollY() > 0) {
            startFling(velocityY);
            return true;
        }
        return false;
    }

    private void startFling(float velocityY) {
        float velY = normalize(velocityY);
        if (Math.abs(velY) < 1f) return;
        final int fromY = getScrollY();
        final int toY = (int) (fromY + velY * DEFAULT_DURATION);
        if (mScrollAnimator != null && mScrollAnimator.isStarted()) {
            mScrollAnimator.cancel();
        }
        post(new Runnable() {
            @Override
            public void run() {
                mScrollAnimator = ValueAnimator.ofFloat(1f);
                mScrollAnimator.setInterpolator(new DecelerateInterpolator(2f));
                mScrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float percent = animation.getAnimatedFraction();
                        float curY = ((toY - fromY) * percent + fromY);
                        scrollTo(0, (int) curY);
                    }
                });
                mScrollAnimator.setDuration(DEFAULT_DURATION);
                mScrollAnimator.start();
            }
        });
    }

    private float normalize(float velocityY) {
        return velocityY / 1000f;
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return super.onNestedFling(target, velocityX, velocityY, consumed);

    }

    @Override
    public void onStopNestedScroll(View child) {
        super.onStopNestedScroll(child);
    }


    @Override
    public void scrollTo(int x, int y) {
        if (y < 0){
            y = 0;
        }else if (y > mMaxScrollHeight){
            y = mMaxScrollHeight;
        }
        super.scrollTo(x, y);
    }

}
