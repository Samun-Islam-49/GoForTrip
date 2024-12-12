package com.samun.gofortrip.helpers;

import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;

import com.samun.gofortrip.R;

public class Diag {
    public static Dialog dialog;

    public static void show(Context context , @LayoutRes int layout) {
        dialog = new Dialog(context);
        dialog.setContentView(layout);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.prob_bar_bg);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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
