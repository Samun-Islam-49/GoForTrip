package com.samun.gofortrip.activities;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.samun.gofortrip.R;
import com.samun.gofortrip.databinding.ActivitySplashBinding;
import com.samun.gofortrip.helpers.OverlayProgBar;
import com.samun.gofortrip.helpers.PrefManager;
import com.samun.gofortrip.models.UserInfo;

public class SplashActivity extends AppCompatActivity {

    private static final long MILLIS = 700, TRANS = -150, TRANS2 = -300;

    static {
        System.loadLibrary("auth");
    }

    ActivitySplashBinding b;
    Animation slideDown, slideRight;
    CountDownTimer waitingTimer;
    AlertDialog noInternet;
    FirebaseAuth firebaseAuth;

    public static native String userImgStore();

    public static native String userInfoDB();

    public static native String isNotUser();

    public static native String pkgImageStore();

    public static native String pkgInfoDB();

    public static native String bookInfoDB();

    public static native String menBkList();

    public static native String menAdPkg();

    public static native String pkgRevDB();

    public static native String image();

    public static native String name();

    public static native String email();

    public static native String phone();

    public static native String phone2();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = DataBindingUtil.setContentView(this, R.layout.activity_splash);

        init();
        prepAnim();
        animate();

    }

    private void init() {
        slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);
        slideRight = AnimationUtils.loadAnimation(this, R.anim.slide_right);
        firebaseAuth = FirebaseAuth.getInstance();
    }


    private void tryAutoSign() {
        UserInfo userInfo = PrefManager.getUserInfo(this);
        String pass = PrefManager.getLog(this);
        if (!pass.isEmpty() && userInfo != null) {

            firebaseAuth.signInWithEmailAndPassword(userInfo.getEmail(), pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(SplashActivity.this, MainDrawerActivity.class));
                        finish();
                        OverlayProgBar.cancel();
                    } else {
                        animateReg();
                        handleButtons();
                    }
                }
            });


        } else {
            animateReg();
            handleButtons();
        }
    }


    private void handleButtons() {
        b.gotoSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    /** SIGN UP BTN */
                startActivity(new Intent(SplashActivity.this, SignUpActivity.class));
            }
        });

        b.gotoSignInTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    /** SIGN IN BTN */
                startActivity(new Intent(SplashActivity.this, SignInActivity.class));
            }
        });
    }


    private void prepAnim() {
        b.busImage.setTranslationX(TRANS2);

        b.gotoSignInTv.setTranslationY(-TRANS2);
        b.gotoSignInTv.setVisibility(View.INVISIBLE);

        b.gotoSignUpBtn.setTranslationY(-TRANS2);
        b.gotoSignUpBtn.setVisibility(View.INVISIBLE);
    }

    private void animate() {

        b.busImage.animate().translationX(0f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(700);
        new CountDownTimer(MILLIS, MILLIS) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                b.goTxt.setVisibility(View.VISIBLE);
                b.goTxt.startAnimation(slideRight);
                b.tripTxt.setVisibility(View.VISIBLE);
                b.tripTxt.startAnimation(slideRight);
                new CountDownTimer(MILLIS, MILLIS) {
                    @Override
                    public void onTick(long l) {
                    }

                    @Override
                    public void onFinish() {
                        b.bsTxt.setVisibility(View.VISIBLE);
                        b.bsTxt.startAnimation(slideDown);
                        b.lpTxt.setVisibility(View.VISIBLE);
                        b.lpTxt.startAnimation(slideDown);
                        delayedAnimation();
                    }
                }.start();
            }
        }.start();
    }

    private void delayedAnimation() {
        new CountDownTimer(MILLIS, MILLIS) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                b.busImage.animate().translationY(TRANS).setInterpolator(new DecelerateInterpolator()).setDuration(MILLIS);
                b.goTxt.animate().translationY(TRANS).setInterpolator(new DecelerateInterpolator()).setDuration(MILLIS);
                b.tripTxt.animate().translationY(TRANS).setInterpolator(new DecelerateInterpolator()).setDuration(MILLIS);
                b.bsTxt.animate().translationY(TRANS).setInterpolator(new DecelerateInterpolator()).setDuration(MILLIS);
                b.lpTxt.animate().translationY(TRANS).setInterpolator(new DecelerateInterpolator()).setDuration(MILLIS);
                new CountDownTimer(MILLIS, MILLIS) {
                    @Override
                    public void onTick(long l) {

                    }

                    @Override
                    public void onFinish() {
                        ensureConnection();
                    }
                }.start();
            }
        }.start();

    }

    private void ensureConnection() {
        OverlayProgBar.show(this);
        if (checkConnection()) {
            if (waitingTimer != null) {
                waitingTimer.cancel();
            }
            if (noInternet != null) {
                noInternet.cancel();
            }
            tryAutoSign();
        } else {
            if (waitingTimer != null) {
                waitingTimer.cancel();
            } else {
                noConnectionDialog();
            }
            startWaiting();
        }
    }

    private void animateReg() {
        b.gotoSignUpBtn.setVisibility(View.VISIBLE);
        b.gotoSignInTv.setVisibility(View.VISIBLE);
        b.gotoSignUpBtn.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).setDuration(MILLIS);
        b.gotoSignInTv.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).setDuration(MILLIS);
        OverlayProgBar.cancel();
    }

    private boolean checkConnection() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mob = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return (wifi != null && wifi.isConnected()) || (mob != null && mob.isConnected());
    }

    private void noConnectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Check Your Internet Connection")
                .setCancelable(false)
                .create();

        noInternet = builder.show();
    }

    private void startWaiting() {
        waitingTimer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long l) {
                ensureConnection();
            }

            @Override
            public void onFinish() {
                waitingTimer.cancel();
            }
        }.start();
    }

}