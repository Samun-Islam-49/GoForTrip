package com.samun.gofortrip.helpers;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.samun.gofortrip.R;

public class ShortDiag implements View.OnClickListener {
    Context context;
    Dialog dialog;

    EditText editText;
    Button mainBtn , backBtn;

    private OnMainBtnClick listener;

    public ShortDiag(Context context) {
        this.context = context;
    }

    public void show(String hintText , String mainBtnText) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.short_diag);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.prob_bar_bg);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnim;
        dialog.setCancelable(false);
        dialog.show();

        initViews();
        setData(hintText , mainBtnText);
    }

    private void initViews() {
        editText = dialog.findViewById(R.id.s_diag_et);
        mainBtn = dialog.findViewById(R.id.s_diag_main_btn);
        backBtn = dialog.findViewById(R.id.s_diag_back_btn);

        mainBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
    }

    private void setData(String hintText, String mainBtnText) {
        editText.setHint(hintText);
        mainBtn.setText(mainBtnText);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.s_diag_main_btn :

                String txt = editText.getText().toString().trim();

                if(!txt.isEmpty()) {
                    listener.onClick(Integer.parseInt(txt));
                } else {
                    editText.setError("Please enter an amount");
                }

                break;

            case R.id.s_diag_back_btn :
                cancel();
                break;
        }
    }

    public void cancel() {
        if (dialog != null) {
            dialog.cancel();
        }
    }

    public interface OnMainBtnClick {
        void onClick(int amount);
    }

    public void setOnMainBtnClick (OnMainBtnClick listener) {
        this.listener = listener;
    }
}
