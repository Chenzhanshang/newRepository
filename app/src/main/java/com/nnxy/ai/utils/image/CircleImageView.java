package com.example.camera_system.utils.image;

import android.content.Context;
import android.util.AttributeSet;

import com.example.camera_system.R;


public class CircleImageView extends CircleImageViewBase {

    public CircleImageView(Context paramContext) {
        super(paramContext);
    }

    public CircleImageView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
    }

    public CircleImageView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
    }

    /**
     * 设置默认值
     */
    @Override
    protected void initConfig() {
        setUseDefaultStyle(false);
        setIsFill(true);
        setTextBackgroundColorRes(R.color.colorPrimaryDark);
    }
}
