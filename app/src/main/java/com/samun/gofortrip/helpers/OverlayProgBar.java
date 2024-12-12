package com.samun.gofortrip.helpers;

import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup;

import com.samun.gofortrip.R;

public class OverlayProgBar {
    static Dialog dialog;

    public static void show(Context context) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.overlay_prog_bar);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.prob_bar_bg);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnim;
        dialog.setCancelable(false);
        dialog.show();
    }

    public static void cancel() {
        if (dialog != null) {
            dialog.cancel();
        }
    }

}
