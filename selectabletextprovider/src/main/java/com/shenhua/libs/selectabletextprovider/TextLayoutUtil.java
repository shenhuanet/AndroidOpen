package com.shenhua.libs.selectabletextprovider;

import android.content.Context;
import android.text.Layout;
import android.widget.TextView;

/**
 * Created by shenhua on 3/15/2017.
 * Email shenhuanet@126.com
 */
public class TextLayoutUtil {

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getPreciseOffset(TextView textView, int x, int y) {
        Layout layout = textView.getLayout();
        if (layout != null) {
            int topVisibleLine = layout.getLineForVertical(y);
            int offset = layout.getOffsetForHorizontal(topVisibleLine, x);

            int offsetX = (int) layout.getPrimaryHorizontal(offset);

            if (offsetX > x) {
                return layout.getOffsetToLeftOf(offset);
            } else {
                return offset;
            }
        } else {
            return -1;
        }
    }

    public static int getHysteresisOffset(TextView textView, int x, int y, int previousOffset) {
        final Layout layout = textView.getLayout();
        if (layout == null) return -1;

        int line = layout.getLineForVertical(y);

        if (isEndOfLineOffset(layout, previousOffset)) {
            int left = (int) layout.getPrimaryHorizontal(previousOffset - 1);
            int right = (int) layout.getLineRight(line);
            int threshold = (right - left) / 2;
            if (x > right - threshold) {
                previousOffset -= 1;
            }
        }
        final int previousLine = layout.getLineForOffset(previousOffset);
        final int previousLineTop = layout.getLineTop(previousLine);
        final int previousLineBottom = layout.getLineBottom(previousLine);
        final int hysteresisThreshold = (previousLineBottom - previousLineTop) / 2;

        if (((line == previousLine + 1) && ((y - previousLineBottom) < hysteresisThreshold)) ||
                ((line == previousLine - 1) && ((previousLineTop - y) < hysteresisThreshold))) {
            line = previousLine;
        }

        int offset = layout.getOffsetForHorizontal(line, x);

        if (offset < textView.getText().length() - 1) {
            if (isEndOfLineOffset(layout, offset + 1)) {
                int left = (int) layout.getPrimaryHorizontal(offset);
                int right = (int) layout.getLineRight(line);
                int threshold = (right - left) / 2;
                if (x > right - threshold) {
                    offset += 1;
                }
            }
        }
        return offset;
    }

    private static boolean isEndOfLineOffset(Layout layout, int offset) {
        return offset > 0 && layout.getLineForOffset(offset) == layout.getLineForOffset(offset - 1) + 1;
    }

    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
