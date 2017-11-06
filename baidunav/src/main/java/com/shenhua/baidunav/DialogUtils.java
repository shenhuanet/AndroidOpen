package com.shenhua.baidunav;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AlertDialog;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * loading dialog
 * Created by shenhua on 2016/6/1.
 */
public class DialogUtils {

    private static AlertDialog alertDialog;

    public static void showLoadDialog(Context context, String text) {
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.dialog_loading);
        ImageView iv = (ImageView) window.findViewById(R.id.dialog_iv);
        TextView tv = (TextView) window.findViewById(R.id.dialog_tv);
        tv.setText(text);
        AnimationDrawable anim = (AnimationDrawable) iv.getBackground();
        anim.start();
    }

    public static void dissmissLoadDialog() {
        alertDialog.dismiss();
    }

}
