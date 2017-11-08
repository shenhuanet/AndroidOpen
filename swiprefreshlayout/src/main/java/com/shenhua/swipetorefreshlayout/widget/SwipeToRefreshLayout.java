package com.shenhua.swipetorefreshlayout.widget;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;

import com.shenhua.swipetorefreshlayout.base.BaseMaterialProgressDrawable;
import com.shenhua.swipetorefreshlayout.base.BaseRefreshLoadingView;

/**
 * 上拉、下拉刷新组件，只允许其子组件数量为 1.
 * Created by shenhua on 8/26/2016.
 */
public class SwipeToRefreshLayout extends ViewGroup {

    private static final int CIRCLE_BG_LIGHT = 0xFF7FD1C0;// 圆圈背景颜色
    private int mTouchSlop;// 滑动操作的最低距离
    private int mOriginalOffsetTop;// 原始的距离top距离
    private int mCurrentTargetOffsetTop;// 当前loadingView 距离top的距离
    private int mActivePointerId = -1;// 活动点的id
    private int mLoadingViewIndex = -1;// loadingView的位置
    private int mFrom;// 记录其实点Y
    private int mCircleLoadingSize;// loadView的大小，宽高相等
    private float mTotalDragDistance = -1;// loadingView 总共拖动的距离
    private float mInitialMotionY;// 初始滑动的Y
    private float mSpinnerFinalOffset;// 对最终的偏移量作微调
    private boolean mBothType;
    private boolean isReady;// 是否准备好刷新
    private boolean isRefreshing = false;// 是否正在刷新
    private boolean isCalculateOrigOffset = false;// 原始偏移量是否计算
    private boolean isBackToStart;// 是否回到起始偏移
    private boolean shouldNotify;// 是否通知刷新
    private View mTarget; // listView，recyclerView
    private SwipeToRefreshLayoutType mType;// 刷新方向
    private OnRefreshListener mOnRefreshListener;// 刷新监听
    private DecelerateInterpolator mDecelerateInterpolator;// 减速插值器
    private BaseRefreshLoadingView mLoadingView;
    private BaseMaterialProgressDrawable mProgress;
    private Animation mAlphaStartAnimation;
    private Animation mAlphaMaxAnimation;

    public SwipeToRefreshLayout(Context context) {
        this(context, null);
    }

    public SwipeToRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        setWillNotDraw(false);
        mDecelerateInterpolator = new DecelerateInterpolator(2f);
        // 默认下拉刷新
        mType = SwipeToRefreshLayoutType.TOP;
        mBothType = false;
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        mCircleLoadingSize = (int) (40 * metrics.density);
        initLoadingView();
        ViewCompat.setChildrenDrawingOrderEnabled(this, true);
        mSpinnerFinalOffset = 64 * metrics.density;
        mTotalDragDistance = mSpinnerFinalOffset;
    }

    private void initLoadingView() {
        mLoadingView = new BaseRefreshLoadingView(getContext(), CIRCLE_BG_LIGHT, 40 / 2);
        mProgress = new BaseMaterialProgressDrawable(getContext(), this);
        mProgress.setBackgroundColor(CIRCLE_BG_LIGHT);
        mLoadingView.setImageDrawable(mProgress);
        mLoadingView.setVisibility(View.GONE);
        addView(mLoadingView);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        if (getChildCount() == 0) return;
        if (mTarget == null) findTarget();
        if (mTarget == null) return;
        final View child = mTarget;
        final int childLeft = getPaddingLeft();
        final int childTop = getPaddingTop();
        final int childWidth = width - getPaddingLeft() - getPaddingRight();
        final int childHeight = height - getPaddingTop() - getPaddingBottom();
        child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
        int loadWidth = mLoadingView.getMeasuredWidth();
        int loadHeight = mLoadingView.getMeasuredHeight();
        mLoadingView.layout((width / 2 - loadWidth / 2), mCurrentTargetOffsetTop, (width / 2 + loadWidth / 2), mCurrentTargetOffsetTop + loadHeight);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mTarget == null) findTarget();
        if (mTarget == null) return;
        mTarget.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY));
        mLoadingView.measure(MeasureSpec.makeMeasureSpec(mCircleLoadingSize, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(mCircleLoadingSize, MeasureSpec.EXACTLY));
        if (!isCalculateOrigOffset) {
            isCalculateOrigOffset = true;
            switch (mType) {
                case BOTTOM:
                    mCurrentTargetOffsetTop = mOriginalOffsetTop = getMeasuredHeight() - mLoadingView.getMeasuredHeight();
                    break;
                case TOP:
                default:
                    mCurrentTargetOffsetTop = mOriginalOffsetTop = -mLoadingView.getMeasuredHeight();
                    break;
            }
        }
        mLoadingViewIndex = -1;
        for (int index = 0; index < getChildCount(); index++) {
            if (getChildAt(index) == mLoadingView) {
                mLoadingViewIndex = index;
                break;
            }
        }
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean b) {
    }

    /**
     * 在OnTouchEvent事件之前调用
     *
     * @param ev touch事件
     * @return false不拦截，反之拦截
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        findTarget();
        final int action = MotionEventCompat.getActionMasked(ev);
        if (isBackToStart && action == MotionEvent.ACTION_DOWN)
            isBackToStart = false;
        switch (mType) {
            case BOTTOM:
                if (!isEnabled() || isBackToStart || (!mBothType && canChildScrollDown()) || isRefreshing)
                    return false;
                break;
            case TOP:
            default:
                if (!isEnabled() || isBackToStart || (!mBothType && canChildScrollUp()) || isRefreshing)
                    return false;
                break;
        }
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (InterceptTouchActionDown(ev)) return false;
            case MotionEvent.ACTION_MOVE:
                if (InterceptTouchActionMove(ev)) return false;
                break;
            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isReady = false;
                mActivePointerId = -1;
                break;
        }
        return isReady;
    }

    private boolean InterceptTouchActionMove(MotionEvent ev) {
        if (mActivePointerId == -1) return true;
        final float y = getMotionEventY(ev, mActivePointerId);
        if (y == -1) return true;
        if (mBothType) {
            if (y > mInitialMotionY) {
                setRawType(SwipeToRefreshLayoutType.TOP);
            } else if (y < mInitialMotionY) {
                setRawType(SwipeToRefreshLayoutType.BOTTOM);
            }
            if ((mType == SwipeToRefreshLayoutType.BOTTOM && canChildScrollDown())
                    || (mType == SwipeToRefreshLayoutType.TOP && canChildScrollUp())) {
                return true;
            }
        }
        float yDiff;
        switch (mType) {
            case BOTTOM:
                yDiff = mInitialMotionY - y;
                break;
            case TOP:
            default:
                yDiff = y - mInitialMotionY;
                break;
        }
        if (yDiff > mTouchSlop && !isReady) {
            isReady = true;
            mProgress.setAlpha((int) (0.3f * 255));
        }
        return false;
    }

    private boolean InterceptTouchActionDown(MotionEvent ev) {
        setTargetOffsetTopAndBottom(mOriginalOffsetTop - mLoadingView.getTop(), true);
        mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
        isReady = false;
        final float initialMotionY = getMotionEventY(ev, mActivePointerId);
        if (initialMotionY == -1) return true;
        mInitialMotionY = initialMotionY;
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        if (isBackToStart && action == MotionEvent.ACTION_DOWN) isBackToStart = false;
        switch (mType) {
            case BOTTOM:
                if (!isEnabled() || isBackToStart || canChildScrollDown() || isRefreshing) {
                    return false;
                }
                break;
            case TOP:
            default:
                if (!isEnabled() || isBackToStart || canChildScrollUp() || isRefreshing) {
                    return false;
                }
                break;
        }
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                isReady = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (TouchActionMove(ev)) return false;
                break;
            case MotionEventCompat.ACTION_POINTER_DOWN:
                final int index = MotionEventCompat.getActionIndex(ev);
                mActivePointerId = MotionEventCompat.getPointerId(ev, index);
                break;
            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                return TouchActionCancel(ev);
            }
        }
        return true;
    }

    private boolean TouchActionCancel(MotionEvent ev) {
        if (mActivePointerId == -1) return false;
        final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
        final float y = MotionEventCompat.getY(ev, pointerIndex);
        float overScrollTop;
        switch (mType) {
            case BOTTOM:
                overScrollTop = (mInitialMotionY - y) * 0.5f;
                break;
            case TOP:
            default:
                overScrollTop = (y - mInitialMotionY) * 0.5f;
                break;
        }
        isReady = false;
        if (overScrollTop > mTotalDragDistance) {
            setRefreshing(true, true);
        } else {
            isRefreshing = false;
            mProgress.setStartEndTrim(0f, 0f);
            Animation.AnimationListener listener = new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    startScaleDownAnimation(null);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

            };
            animateOffsetToStartPosition(mCurrentTargetOffsetTop, listener);
            mProgress.showArrow(false);
        }
        mActivePointerId = -1;
        return false;
    }

    private boolean TouchActionMove(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
        if (pointerIndex < 0) return true;
        final float y = MotionEventCompat.getY(ev, pointerIndex);
        float overScrollTop;
        switch (mType) {
            case BOTTOM:
                overScrollTop = (mInitialMotionY - y) * 0.5f;
                break;
            case TOP:
            default:
                overScrollTop = (y - mInitialMotionY) * 0.5f;
                break;
        }
        if (isReady) {
            mProgress.showArrow(true);
            float originalDragPercent = overScrollTop / mTotalDragDistance;
            if (originalDragPercent < 0) return true;
            float dragPercent = Math.min(1f, Math.abs(originalDragPercent));
            float adjustedPercent = (float) Math.max(dragPercent - 0.4, 0) * 5 / 3;
            float extraOS = Math.abs(overScrollTop) - mTotalDragDistance;
            float slingshotDist = mSpinnerFinalOffset;
            float tensionSlingshotPercent = Math.max(0, Math.min(extraOS, slingshotDist * 2) / slingshotDist);
            float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow((tensionSlingshotPercent / 4), 2)) * 2f;
            float extraMove = (slingshotDist) * tensionPercent * 2;
            int targetY;
            if (mType == SwipeToRefreshLayoutType.TOP) {
                targetY = mOriginalOffsetTop + (int) ((slingshotDist * dragPercent) + extraMove);
            } else {
                targetY = mOriginalOffsetTop - (int) ((slingshotDist * dragPercent) + extraMove);
            }
            if (mLoadingView.getVisibility() != View.VISIBLE) {
                mLoadingView.setVisibility(View.VISIBLE);
            }
            ViewCompat.setScaleX(mLoadingView, 1f);
            ViewCompat.setScaleY(mLoadingView, 1f);
            if (overScrollTop < mTotalDragDistance) {
                if (mProgress.getAlpha() > (int) (0.3f * 255) && !isAnimationRunning(mAlphaStartAnimation)) {
                    startProgressAlphaStartAnimation();
                }
                float strokeStart = adjustedPercent * 0.8f;
                mProgress.setStartEndTrim(0f, Math.min(0.8f, strokeStart));
                mProgress.setArrowScale(Math.min(1f, adjustedPercent));
            } else {
                if (mProgress.getAlpha() < 255 && !isAnimationRunning(mAlphaMaxAnimation)) {
                    startProgressAlphaMaxAnimation();
                }
            }
            float rotation = (-0.25f + 0.4f * adjustedPercent + tensionPercent * 2) * 0.5f;
            mProgress.setProgressRotation(rotation);
            setTargetOffsetTopAndBottom(targetY - mCurrentTargetOffsetTop, true);
        }
        return false;
    }

    private Animation.AnimationListener mRefreshListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (isRefreshing) {
                mProgress.setAlpha(255);
                mProgress.start();
                if (shouldNotify) {
                    if (mOnRefreshListener != null) {
                        mOnRefreshListener.onRefresh(mType);
                    }
                }
            } else {
                mProgress.stop();
                mLoadingView.setVisibility(View.GONE);
                setColorViewAlpha(255);
                setTargetOffsetTopAndBottom(mOriginalOffsetTop - mCurrentTargetOffsetTop, true);
            }
            mCurrentTargetOffsetTop = mLoadingView.getTop();
        }
    };

    private void startScaleUpAnimation(Animation.AnimationListener listener) {
        mLoadingView.setVisibility(View.VISIBLE);
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            mProgress.setAlpha(255);
        }
        Animation mScaleAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                setAnimationProgress(interpolatedTime);
            }
        };
        mScaleAnimation.setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
        if (listener != null) {
            mLoadingView.setAnimationListener(listener);
        }
        mLoadingView.clearAnimation();
        mLoadingView.startAnimation(mScaleAnimation);
    }

    private void startScaleDownAnimation(Animation.AnimationListener listener) {
        Animation mScaleDownAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                setAnimationProgress(1 - interpolatedTime);
            }
        };
        mScaleDownAnimation.setDuration(150);
        mLoadingView.setAnimationListener(listener);
        mLoadingView.clearAnimation();
        mLoadingView.startAnimation(mScaleDownAnimation);
    }

    private Animation startAlphaAnimation(final int startingAlpha, final int endingAlpha) {
        if (Build.VERSION.SDK_INT < 11) {
            return null;
        }
        Animation alpha = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                mProgress.setAlpha((int) (startingAlpha + ((endingAlpha - startingAlpha) * interpolatedTime)));
            }
        };
        alpha.setDuration(300);
        mLoadingView.setAnimationListener(null);
        mLoadingView.clearAnimation();
        mLoadingView.startAnimation(alpha);
        return alpha;
    }

    private final class AnimateToRightPos extends Animation {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            int targetTop;
            int endTarget;
            switch (mType) {
                case BOTTOM:
                    endTarget = getMeasuredHeight() - (int) (mSpinnerFinalOffset);
                    break;
                case TOP:
                default:
                    endTarget = (int) (mSpinnerFinalOffset - Math.abs(mOriginalOffsetTop));
                    break;
            }
            targetTop = (mFrom + (int) ((endTarget - mFrom) * interpolatedTime));
            int offset = targetTop - mLoadingView.getTop();
            setTargetOffsetTopAndBottom(offset, false);
        }
    }

    private final class AnimateToStartPos extends Animation {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            moveToStart(interpolatedTime);
        }
    }

    private void animateOffsetToRightPosition(int from, Animation.AnimationListener listener) {
        mFrom = from;
        AnimateToRightPos animateToRightPos = new AnimateToRightPos();
        animateToRightPos.reset();
        animateToRightPos.setDuration(200);
        animateToRightPos.setInterpolator(mDecelerateInterpolator);
        if (listener != null) {
            mLoadingView.setAnimationListener(listener);
        }
        mLoadingView.clearAnimation();
        mLoadingView.startAnimation(animateToRightPos);
    }

    private void animateOffsetToStartPosition(int from, Animation.AnimationListener listener) {
        mFrom = from;
        AnimateToStartPos animateToStartPos = new AnimateToStartPos();
        animateToStartPos.reset();
        animateToStartPos.setDuration(200);
        animateToStartPos.setInterpolator(mDecelerateInterpolator);
        if (listener != null) mLoadingView.setAnimationListener(listener);
        mLoadingView.clearAnimation();
        mLoadingView.startAnimation(animateToStartPos);
    }

    private void startProgressAlphaStartAnimation() {
        mAlphaStartAnimation = startAlphaAnimation(mProgress.getAlpha(), (int) (0.3f * 255));
    }

    private void startProgressAlphaMaxAnimation() {
        mAlphaMaxAnimation = startAlphaAnimation(mProgress.getAlpha(), 255);
    }

    private void findTarget() {
        if (mTarget == null) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (!child.equals(mLoadingView)) {
                    mTarget = child;
                    break;
                }
            }
        }
    }

    private void moveToStart(float interpolatedTime) {
        int targetTop;
        targetTop = (mFrom + (int) ((mOriginalOffsetTop - mFrom) * interpolatedTime));
        int offset = targetTop - mLoadingView.getTop();
        setTargetOffsetTopAndBottom(offset, false);
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
        if (pointerId == mActivePointerId) {
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
        }
    }

    public boolean canChildScrollUp() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mTarget instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mTarget;
                return absListView.getChildCount() > 0 && (absListView.getFirstVisiblePosition() > 0
                        || absListView.getChildAt(0).getTop() < absListView.getPaddingTop());
            } else {
                return mTarget.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mTarget, -1);
        }
    }

    public boolean canChildScrollDown() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mTarget instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mTarget;
                try {
                    if (absListView.getCount() > 0) {
                        if (absListView.getLastVisiblePosition() + 1 == absListView.getCount()) {
                            int lastIndex = absListView.getLastVisiblePosition() - absListView.getFirstVisiblePosition();
                            return absListView.getChildAt(lastIndex).getBottom() == absListView.getPaddingBottom();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            } else {
                return true;
            }
        } else {
            return ViewCompat.canScrollVertically(mTarget, 1);
        }
    }

    private boolean isAnimationRunning(Animation animation) {
        return animation != null && animation.hasStarted() && !animation.hasEnded();
    }

    public boolean isRefreshing() {
        return isRefreshing;
    }

    public SwipeToRefreshLayoutType getType() {
        return mBothType ? SwipeToRefreshLayoutType.TOP_AND_BOTTOM : mType;
    }

    private float getMotionEventY(MotionEvent ev, int activePointerId) {
        final int index = MotionEventCompat.findPointerIndex(ev, activePointerId);
        if (index < 0) return -1;
        return MotionEventCompat.getY(ev, index);
    }

    protected int getChildDrawingOrder(int childCount, int i) {
        if (mLoadingViewIndex < 0) {
            return i;
        } else if (i == childCount - 1) {
            return mLoadingViewIndex;
        } else if (i >= mLoadingViewIndex) {
            return i + 1;
        } else {
            return i;
        }
    }

    private void setColorViewAlpha(int targetAlpha) {
        mLoadingView.getBackground().setAlpha(targetAlpha);
        mProgress.setAlpha(targetAlpha);
    }

    private void setAnimationProgress(float progress) {
        if (Build.VERSION.SDK_INT < 11) {
            setColorViewAlpha((int) (progress * 255));
        } else {
            ViewCompat.setScaleX(mLoadingView, progress);
            ViewCompat.setScaleY(mLoadingView, progress);
        }
    }

    private void setRefreshing(boolean refreshing, final boolean notify) {
        if (isRefreshing != refreshing) {
            shouldNotify = notify;
            findTarget();
            isRefreshing = refreshing;
            if (isRefreshing) {
                animateOffsetToRightPosition(mCurrentTargetOffsetTop, mRefreshListener);
            } else {
                startScaleDownAnimation(mRefreshListener);
            }
        }
    }

    private void setRawType(SwipeToRefreshLayoutType direction) {
        if (mType == direction) return;
        mType = direction;
        switch (mType) {
            case BOTTOM:
                mCurrentTargetOffsetTop = mOriginalOffsetTop = getMeasuredHeight();
                break;
            case TOP:
                mCurrentTargetOffsetTop = mOriginalOffsetTop = -mLoadingView.getMeasuredHeight();
                break;
            default:
                mCurrentTargetOffsetTop = mOriginalOffsetTop = -mLoadingView.getMeasuredHeight();
                break;
        }
    }

    private void setTargetOffsetTopAndBottom(int offset, boolean requiresUpdate) {
        mLoadingView.bringToFront();
        mLoadingView.offsetTopAndBottom(offset);
        mCurrentTargetOffsetTop = mLoadingView.getTop();
        if (requiresUpdate && android.os.Build.VERSION.SDK_INT < 11) {
            invalidate();
        }
    }

    public void setRefreshing(boolean refreshing) {
        if (refreshing && isRefreshing != refreshing) {
            isRefreshing = refreshing;
            int endTarget;
            endTarget = (int) (mSpinnerFinalOffset + mOriginalOffsetTop);
            setTargetOffsetTopAndBottom(endTarget - mCurrentTargetOffsetTop, true);
            shouldNotify = false;
            startScaleUpAnimation(mRefreshListener);
        } else {
            setRefreshing(refreshing, false);
        }
    }

    public void setProgressBackgroundColor(int colorRes) {
        mLoadingView.setBackgroundColor(colorRes);
        mProgress.setBackgroundColor(getResources().getColor(colorRes));
    }

    @Deprecated
    public void setColorScheme(int... colors) {
        setColorSchemeResources(colors);
    }

    public void setColorSchemeResources(int... colorResIds) {
        final Resources res = getResources();
        int[] colorRes = new int[colorResIds.length];
        for (int i = 0; i < colorResIds.length; i++) {
            colorRes[i] = res.getColor(colorResIds[i]);
        }
        setColorSchemeColors(colorRes);
    }

    public void setColorSchemeColors(int... colors) {
        findTarget();
        mProgress.setColorSchemeColors(colors);
    }

    public void setDistanceToTriggerSync(int distance) {
        mTotalDragDistance = distance;
    }

    public void setType(SwipeToRefreshLayoutType direction) {
        if (direction == SwipeToRefreshLayoutType.TOP_AND_BOTTOM) {
            mBothType = true;
        } else {
            mBothType = false;
            mType = direction;
        }
        switch (mType) {
            case BOTTOM:
                mCurrentTargetOffsetTop = mOriginalOffsetTop = getMeasuredHeight();
                break;
            case TOP:
                mCurrentTargetOffsetTop = mOriginalOffsetTop = -mLoadingView.getMeasuredHeight();
                break;
            default:
                mCurrentTargetOffsetTop = mOriginalOffsetTop = -mLoadingView.getMeasuredHeight();
                break;
        }
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        mOnRefreshListener = listener;
    }

    public interface OnRefreshListener {
        void onRefresh(SwipeToRefreshLayoutType direction);
    }

    public enum SwipeToRefreshLayoutType {
        TOP, BOTTOM, TOP_AND_BOTTOM;
    }
}
