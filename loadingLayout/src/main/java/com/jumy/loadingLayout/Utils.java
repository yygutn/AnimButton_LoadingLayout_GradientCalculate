package com.jumy.loadingLayout;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.view.View;

/**
 * create by Jumy
 * on date 17/1/9 11:33.
 */

public class Utils {


    public static Drawable getDrawable(Context context, @DrawableRes int id) {
        return ContextCompat.getDrawable(context, id);
    }

    public static int getColor(Context conetxt, @ColorRes int id) {
        return ContextCompat.getColor(conetxt, id);
    }

    public static String getString(Context context, @StringRes int id) {
        return context.getResources().getString(id);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics()
                .scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int dp2px(Context context, int dip) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

    public static <T extends View> T findViewById(View v, int id) {
        return (T) v.findViewById(id);
    }

}