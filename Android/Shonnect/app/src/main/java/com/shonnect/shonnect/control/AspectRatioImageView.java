package com.shonnect.shonnect.control;

import com.shonnect.shonnect.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Aspect ratio image view.
 * Default will based on width to calculate height.
 */
public class AspectRatioImageView extends ImageView {

    public static final float RATIO_UNDEFINED = 0;
    public static final float RATIO_WIDTH = -1;
    public static final float RATIO_HEIGHT = -2;

    private float aspectRatio = RATIO_UNDEFINED;

    public AspectRatioImageView(Context context) {
        super(context);
    }

    public AspectRatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initFromAttrs(context, attrs);
    }

    public AspectRatioImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initFromAttrs(context, attrs);
    }

    private void initFromAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AspectRatioImageView);
        aspectRatio = typedArray.getFloat(R.styleable.AspectRatioImageView_aspectRatio, RATIO_UNDEFINED);
        typedArray.recycle();
    }

    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (aspectRatio != RATIO_UNDEFINED) {
            if (aspectRatio > 0) {
                int width = MeasureSpec.getSize(widthMeasureSpec);
                if (width > 0) {
                    setMeasuredDimension(width, (int) (width / aspectRatio));
                    return;
                }
            } else if (getDrawable() != null) {
                int intrinsicHeight = getDrawable().getIntrinsicHeight();
                int intrinsicWidth = getDrawable().getIntrinsicWidth();
                if (aspectRatio == RATIO_WIDTH && intrinsicWidth > 0) {
                    int width = MeasureSpec.getSize(widthMeasureSpec);
                    int height = (int) (width * intrinsicHeight / (float) intrinsicWidth);
                    setMeasuredDimension(width, height);
                    return;
                } else if (aspectRatio == RATIO_HEIGHT && intrinsicHeight > 0) {
                    int height = MeasureSpec.getSize(heightMeasureSpec);
                    int width = (int) (height * intrinsicWidth / (float) intrinsicHeight);
                    setMeasuredDimension(width, height);
                    return;
                }
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
