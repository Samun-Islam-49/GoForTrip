package com.samun.gofortrip.helpers;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.samun.gofortrip.R;

import static com.samun.gofortrip.activities.SplashActivity.email;
import static com.samun.gofortrip.activities.SplashActivity.image;
import static com.samun.gofortrip.activities.SplashActivity.name;
import static com.samun.gofortrip.activities.SplashActivity.phone;
import static com.samun.gofortrip.activities.SplashActivity.phone2;
import static com.samun.gofortrip.fragments.BookListFragment.REQUEST_CALL;

public class ContactDiag {
     Dialog dialog;
     Context context;

     ImageView icon , emailImg , phoneImg , phoneImg2;
     TextView nameTV , emailTV , phoneTV , phoneTV2;

    public ContactDiag(Context context) {
        this.context = context;
    }

    public void show() {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.contact_diag);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.prob_bar_bg);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnim;
        dialog.setCancelable(true);
        dialog.show();

        initViews();
        setData();
        handleBtns();
    }

    private void initViews() {
        icon = dialog.findViewById(R.id.contact_icon);
        emailImg = dialog.findViewById(R.id.contact_email_img);
        phoneImg = dialog.findViewById(R.id.contact_phone_img);
        phoneImg2 = dialog.findViewById(R.id.contact_phone_img2);

        nameTV = dialog.findViewById(R.id.contact_name);
        emailTV = dialog.findViewById(R.id.contact_email);
        phoneTV = dialog.findViewById(R.id.contact_phone);
        phoneTV2 = dialog.findViewById(R.id.contact_phone2);
    }

    private void setData() {
        byte[] decode = Base64.decode(image(), 0);
        icon.setImageBitmap(BitmapFactory.decodeByteArray(decode, 0, decode.length));

        emailTV.setText(email());
        nameTV.setText(name());
        phoneTV.setText(phone());
        phoneTV2.setText(phone2());
    }

    private void handleBtns() {
        emailImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail();
            }
        });

        phoneImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call(phone());
            }
        });

        phoneImg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call(phone2());
            }
        });
    }

    private void call(String phone) {
        if (hasCallPermission()) {
            String dial = "tel:" + phone;
            context.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }
    }

    private void sendEmail() {
        String recipient = email();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, recipient);

        intent.setType("message/rfc822");
        context.startActivity(Intent.createChooser(intent, "Choose an email client"));
    }

    private boolean hasCallPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            return false;
        }
        return true;
    }
}
