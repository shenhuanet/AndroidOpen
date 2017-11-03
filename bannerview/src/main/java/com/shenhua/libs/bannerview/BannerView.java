package com.shenhua.libs.bannerview;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.IntDef;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * Banner Basic
 * Created by shenhua on 8/29/2016.
 */
public class BannerView extends FrameLayout implements ViewPager.OnPageChangeListener {

    private int bannerStyle = BannerViewConfig.NOT_INDICATOR;
    private int delayTime = 2000;
    private int mIndicatorSelectedResId = R.drawable.indicator_gray_circle;
    private int mIndicatorUnselectedResId = R.drawable.indicator_white_circle;
    private int titleHeight;
    private int titleBackground;
    private int titleTextColor;
    private int titleTextSize;
    private int defaultImage = -1;
    private int count = 0;
    private int currentItem;
    private int gravity = -1;
    private int lastPosition = 1;
    private boolean isAutoPlay = true;
    private List<ImageView> imageViews;
    private List<ImageView> indicatorImages;
    private Context context;
    private ViewPager viewPager;
    private LinearLayout indicator, indicatorInside, titleView;
    private Handler handler = new Handler();
    private OnBannerItemClickListener listener;
    private String[] titles;
    private TextView bannerTitle, numIndicatorInside, numIndicator;
    private BannerPagerAdapter adapter;
    private ViewPager.OnPageChangeListener mOnPageChangeListener;

    public BannerView(Context context) {
        this(context, null);
    }

    public BannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        imageViews = new ArrayList<>();
        indicatorImages = new ArrayList<>();
        initView();
    }

    private void initView() {
        imageViews.clear();
        View view = LayoutInflater.from(context).inflate(R.layout.banner, this, true);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        titleView = (LinearLayout) view.findViewById(R.id.titleView);
        indicator = (LinearLayout) view.findViewById(R.id.indicator);
        indicatorInside = (LinearLayout) view.findViewById(R.id.indicatorInside);
        bannerTitle = (TextView) view.findViewById(R.id.bannerTitle);
        numIndicator = (TextView) view.findViewById(R.id.numIndicator);
        numIndicatorInside = (TextView) view.findViewById(R.id.numIndicatorInside);
        /**
         * You can set the default configuration here
         */
        mIndicatorSelectedResId = R.drawable.indicator_gray_circle;
        mIndicatorUnselectedResId = R.drawable.indicator_white_circle;
        titleBackground = BannerViewConfig.TITLE_BACKGROUND;
        titleHeight = BannerViewConfig.TITLE_HEIGHT;
        titleTextColor = BannerViewConfig.TITLE_TEXT_COLOR;
        titleTextSize = BannerViewConfig.TITLE_TEXT_SIZE;
    }

    private void initImages() {
        imageViews.clear();
        if (bannerStyle == BannerViewConfig.CIRCLE_INDICATOR ||
                bannerStyle == BannerViewConfig.CIRCLE_INDICATOR_TITLE_VERTICAL ||
                bannerStyle == BannerViewConfig.CIRCLE_INDICATOR_TITLE_HORIZONTAL) {
            createIndicator();
        } else if (bannerStyle == BannerViewConfig.NUM_INDICATOR_TITLE) {
            numIndicatorInside.setText("1/" + count);
        } else if (bannerStyle == BannerViewConfig.NUM_INDICATOR) {
            numIndicator.setText("1/" + count);
        }
    }

    // Creates an indicator
    private void createIndicator() {
        indicatorImages.clear();
        indicator.removeAllViews();
        indicatorInside.removeAllViews();
        for (int i = 0; i < count; i++) {
            ImageView imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            int mIndicatorWidth = BannerViewConfig.INDICATOR_SIZE;
            int mIndicatorHeight = BannerViewConfig.INDICATOR_SIZE;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mIndicatorWidth, mIndicatorHeight);
            int mIndicatorMargin = BannerViewConfig.PADDING_SIZE;
            params.leftMargin = mIndicatorMargin;
            params.rightMargin = mIndicatorMargin;
            if (i == 0) {
                imageView.setImageResource(mIndicatorSelectedResId);
            } else {
                imageView.setImageResource(mIndicatorUnselectedResId);
            }
            indicatorImages.add(imageView);
            if (bannerStyle == BannerViewConfig.CIRCLE_INDICATOR ||
                    bannerStyle == BannerViewConfig.CIRCLE_INDICATOR_TITLE_VERTICAL)
                indicator.addView(imageView, params);
            else if (bannerStyle == BannerViewConfig.CIRCLE_INDICATOR_TITLE_HORIZONTAL)
                indicatorInside.addView(imageView, params);
        }
    }

    // Starts automatic playback
    private void startAutoPlay() {
        if (isAutoPlay) {
            handler.removeCallbacks(task);
            handler.postDelayed(task, delayTime);
        }
    }

    private final Runnable task = new Runnable() {

        @Override
        public void run() {
            if (isAutoPlay) {
                if (count > 1) {
                    currentItem = currentItem % (count + 1) + 1;
                    if (currentItem == 1) {
                        viewPager.setCurrentItem(currentItem, false);
                    } else {
                        viewPager.setCurrentItem(currentItem);
                    }
                    handler.postDelayed(task, delayTime);
                }
            }
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (count > 1) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isAutoPlay(false);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    isAutoPlay(true);
                    break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private class BannerPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imageViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            container.addView(imageViews.get(position));
            final ImageView view = imageViews.get(position);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.OnBannerClick(v, position);
                    }
                }
            });
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(imageViews.get(position));
        }

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageSelected(position);
        }
        if (bannerStyle == BannerViewConfig.CIRCLE_INDICATOR ||
                bannerStyle == BannerViewConfig.CIRCLE_INDICATOR_TITLE_VERTICAL ||
                bannerStyle == BannerViewConfig.CIRCLE_INDICATOR_TITLE_HORIZONTAL) {
            indicatorImages.get((lastPosition - 1 + count) % count).setImageResource(mIndicatorUnselectedResId);
            indicatorImages.get((position - 1 + count) % count).setImageResource(mIndicatorSelectedResId);
            lastPosition = position;
        }
        if (position == 0) position = 1;
        switch (bannerStyle) {
            case BannerViewConfig.CIRCLE_INDICATOR:
                break;
            case BannerViewConfig.NUM_INDICATOR:
                if (position > count) position = count;
                numIndicator.setText(position + "/" + count);
                break;
            case BannerViewConfig.NUM_INDICATOR_TITLE:
                if (position > count) position = count;
                numIndicatorInside.setText(position + "/" + count);
                if (titles != null && titles.length > 0) {
                    if (position > titles.length) position = titles.length;
                    bannerTitle.setText(titles[position - 1]);
                }
                break;
            case BannerViewConfig.CIRCLE_INDICATOR_TITLE_VERTICAL:
                if (titles != null && titles.length > 0) {
                    if (position > titles.length) position = titles.length;
                    bannerTitle.setText(titles[position - 1]);
                }
                break;
            case BannerViewConfig.CIRCLE_INDICATOR_TITLE_HORIZONTAL:
                if (titles != null && titles.length > 0) {
                    if (position > titles.length) position = titles.length;
                    bannerTitle.setText(titles[position - 1]);
                }
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        switch (state) {
            case 1:
                isAutoPlay = false;
                break;
            case 2:
                isAutoPlay = true;
                break;
            case 0:
                if (viewPager.getCurrentItem() == 0) {
                    viewPager.setCurrentItem(count, false);
                } else if (viewPager.getCurrentItem() == count + 1) {
                    viewPager.setCurrentItem(1, false);
                }
                currentItem = viewPager.getCurrentItem();
                isAutoPlay = true;
                break;
        }
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrollStateChanged(state);
        }
    }

    private void setData() {
        currentItem = 1;
        if (adapter == null) {
            adapter = new BannerPagerAdapter();
            viewPager.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
        viewPager.setFocusable(true);
        viewPager.setCurrentItem(1);
        viewPager.addOnPageChangeListener(this);
        if (gravity != -1)
            indicator.setGravity(gravity);
        startAutoPlay();
    }

    // Whether to play automatically
    public void isAutoPlay(boolean isAutoPlay) {
        this.isAutoPlay = isAutoPlay;
        startAutoPlay();
    }

    /**
     * Set the banner style
     *
     * @param bannerStyle 0-5
     */
    public void setBannerStyle(@BannerViewConfig.BannerTypeChecker int bannerStyle) {
        this.bannerStyle = bannerStyle;
        switch (bannerStyle) {
            case BannerViewConfig.CIRCLE_INDICATOR:
                indicator.setVisibility(View.VISIBLE);
                break;
            case BannerViewConfig.NUM_INDICATOR:
                numIndicator.setVisibility(View.VISIBLE);
                break;
            case BannerViewConfig.NUM_INDICATOR_TITLE:
                numIndicatorInside.setVisibility(View.VISIBLE);
                break;
            case BannerViewConfig.CIRCLE_INDICATOR_TITLE_VERTICAL:
                indicator.setVisibility(View.VISIBLE);
                break;
            case BannerViewConfig.CIRCLE_INDICATOR_TITLE_HORIZONTAL:
                indicatorInside.setVisibility(VISIBLE);
                break;
            case BannerViewConfig.NOT_INDICATOR:
                break;
        }
    }

    /**
     * Sets the banner datas,use array
     *
     * @param datas BannerData
     */
    public void setBannerDataA(BannerData datas) {
        int size = datas.getaImage().length;
        for (int i = 0; i < size; i++) {
            setBannerImageArray(datas.getaImage());
            setBannerTitleArray(datas.getaTitle());
        }
    }

    /**
     * Sets the banner datas,use List
     *
     * @param datas BannerData
     */
    public void setBannerDataB(BannerData datas) {
        int size = datas.getbImage().size();
        for (int i = 0; i < size; i++) {
            setBannerImageList(datas.getbImage());
            setBannerTitleList(datas.getbTitle());
        }
    }

    /**
     * Sets the title array
     *
     * @param titles title array
     */
    public void setBannerTitleArray(String[] titles) {
        this.titles = titles;
        if (bannerStyle == BannerViewConfig.CIRCLE_INDICATOR_TITLE_VERTICAL ||
                bannerStyle == BannerViewConfig.NUM_INDICATOR_TITLE ||
                bannerStyle == BannerViewConfig.CIRCLE_INDICATOR_TITLE_HORIZONTAL) {
            if (titleBackground != -1) {
                titleView.setBackgroundColor(titleBackground);
            }
            if (titleHeight != -1) {
                titleView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, titleHeight));
            }
            if (titleTextColor != -1) {
                bannerTitle.setTextColor(titleTextColor);
            }
            if (titleTextSize != -1) {
                bannerTitle.setTextSize(titleTextSize);
            }
            if (titles != null && titles.length > 0) {
                bannerTitle.setText(titles[0]);
                bannerTitle.setVisibility(View.VISIBLE);
                titleView.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Sets the title list
     *
     * @param titles title list
     */
    public void setBannerTitleList(List<String> titles) {
        setBannerTitleArray(titles.toArray(new String[titles.size()]));
    }

    /**
     * Sets the image array
     *
     * @param imagesUrl image array
     */
    public void setBannerImageArray(Object[] imagesUrl) {
        if (imagesUrl == null || imagesUrl.length <= 0) {
            return;
        }
        count = imagesUrl.length;
        initImages();
        for (int i = 0; i <= count + 1; i++) {
            ImageView iv = new ImageView(context);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            Object url;
            if (i == 0) {
                url = imagesUrl[count - 1];
            } else if (i == count + 1) {
                url = imagesUrl[0];
            } else {
                url = imagesUrl[i - 1];
            }
            imageViews.add(iv);
            if (defaultImage != -1)
                Glide.with(context).load(url).centerCrop().crossFade().into(iv);
            else
                Glide.with(context).load(url).centerCrop().crossFade().placeholder(defaultImage).into(iv);

        }
        setData();
    }

    /**
     * Sets the picture list
     *
     * @param imagesUrl picture list
     */
    public void setBannerImageList(List<?> imagesUrl) {
        if (imagesUrl == null || imagesUrl.size() <= 0) {
            return;
        }
        count = imagesUrl.size();
        initImages();
        for (int i = 0; i <= count + 1; i++) {
            ImageView iv = new ImageView(context);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            Object url;
            if (i == 0) {
                url = imagesUrl.get(count - 1);
            } else if (i == count + 1) {
                url = imagesUrl.get(0);
            } else {
                url = imagesUrl.get(i - 1);
            }
            imageViews.add(iv);
            if (defaultImage != -1)
                Glide.with(context).load(url).centerCrop().crossFade().into(iv);
            else
                Glide.with(context).load(url).centerCrop().crossFade().placeholder(defaultImage).into(iv);

        }
        setData();
    }

    // Set the display time
    public void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }

    // To set the indicator alignment (left center-right)
    public void setIndicatorGravity(int type) {
        switch (type) {
            case BannerViewConfig.LEFT:
                this.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
                break;
            case BannerViewConfig.CENTER:
                this.gravity = Gravity.CENTER;
                break;
            case BannerViewConfig.RIGHT:
                this.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
                break;
        }
    }

    // banner Click to listen
    public void setOnBannerClickListener(OnBannerItemClickListener listener) {
        this.listener = listener;
    }

    // viewPager Toggle
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        mOnPageChangeListener = onPageChangeListener;
    }

    public interface OnBannerItemClickListener {
        void OnBannerClick(View view, int position);
    }

    // Related default configuration classes
    public static class BannerViewConfig {
        /**
         * Indicator Style
         */
        @BannerTypeChecker
        public static final int NOT_INDICATOR = 0;// No indicator (default)
        @BannerTypeChecker
        public static final int CIRCLE_INDICATOR = 1;// Circular indicator
        @BannerTypeChecker
        public static final int NUM_INDICATOR = 2;// Digital indicator
        @BannerTypeChecker
        public static final int NUM_INDICATOR_TITLE = 3;// Digital indicators and titles
        @BannerTypeChecker
        public static final int CIRCLE_INDICATOR_TITLE_VERTICAL = 4;// Round indicator and title (up and down)
        @BannerTypeChecker
        public static final int CIRCLE_INDICATOR_TITLE_HORIZONTAL = 5;// Round indicator and title (left and right)
        /**
         * Indicator Position Alignment
         */
        public static final int LEFT = 5;// Left justified
        public static final int CENTER = 6; // Center alignment
        public static final int RIGHT = 7;// Right justified
        /**
         * banner Related configurations
         */
        public static final int INDICATOR_SIZE = 8;// Indicator size
        public static final int PADDING_SIZE = 5;// Inner Margin
        /**
         * Title style
         */
        public static final int TITLE_BACKGROUND = -1;// background
        public static final int TITLE_HEIGHT = -1;// height
        public static final int TITLE_TEXT_COLOR = -1;// color
        public static final int TITLE_TEXT_SIZE = -1;// font size

        @IntDef({NOT_INDICATOR, CIRCLE_INDICATOR, NUM_INDICATOR, NUM_INDICATOR_TITLE, CIRCLE_INDICATOR_TITLE_VERTICAL, CIRCLE_INDICATOR_TITLE_HORIZONTAL})
        @Retention(RetentionPolicy.SOURCE)
        public @interface BannerTypeChecker {

        }
    }
}
