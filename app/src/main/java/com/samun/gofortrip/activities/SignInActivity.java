package com.samun.gofortrip.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.samun.gofortrip.R;
import com.samun.gofortrip.databinding.ActivitySignInBinding;
import com.samun.gofortrip.helpers.OverlayProgBar;
import com.samun.gofortrip.helpers.PrefManager;
import com.samun.gofortrip.helpers.UserVerification;
import com.samun.gofortrip.models.UserInfo;

import static com.samun.gofortrip.activities.SplashActivity.userInfoDB;

public class SignInActivity extends AppCompatActivity {
    ActivitySignInBinding b;
    String email, pass;
    FirebaseAuth firebaseAuth;
    Dialog progBar;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = DataBindingUtil.setContentView(this, R.layout.activity_sign_in);
        init();
        handleButtons();
    }

    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference(userInfoDB());
    }

    private void handleButtons() {
        b.gotoSignUpTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    /** SIGN UP BTN */
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            }
        });

        b.signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    /** SIGN IN BTN */
                trySignIn();
            }
        });
    }

    private void trySignIn() {
        if (isEmailValid() && isPassValid()) {
            OverlayProgBar.show(this);
            firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        fetchUserInfo();
                    } else {
                        OverlayProgBar.cancel();
//                        Toast.makeText(SignInActivity.this, task.getException().getMessage() , Toast.LENGTH_SHORT).show();
                        b.emailEt.setError("Incorrect Email or Password.");
                        b.passEt.setError("Incorrect Email or Password.");
                    }
                }
            });
        }
    }

    private void fetchUserInfo() {
        DatabaseReference emailRef = databaseReference.child(UserVerification.getFirebasePath(email));
        emailRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserInfo userInfo = snapshot.getValue(UserInfo.class);
                    PrefManager.saveUserInfo(SignInActivity.this, userInfo);
                    PrefManager.saveLog(SignInActivity.this, pass);
                    enterIntoApp();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                b.emailEt.setError("Incorrect Email or Password.");
                b.passEt.setError("Incorrect Email or Password.");
            }
        });
    }

    private void enterIntoApp() {
        OverlayProgBar.cancel();
        Intent intent = new Intent(SignInActivity.this, MainDrawerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        Toast.makeText(this, "Signed In Successfully", Toast.LENGTH_SHORT).show();
        finish();
    }


    private boolean isEmailValid() {
        email = b.emailEt.getText().toString().trim();

        if (email.isEmpty()) {
            b.emailEt.setError("Please enter your Email address.");
            b.emailEt.requestFocus();
            return false;
        }

        if (!(Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
            b.emailEt.setError("Please enter a valid Email address.");
            b.emailEt.requestFocus();
            return false;
        }

        return true;
    }

    private boolean isPassValid() {
        pass = b.passEt.getText().toString().trim();

        if (pass.isEmpty()) {
            b.passEt.setError("Please enter your Password.");
            b.passEt.requestFocus();
            return false;
        }
        return true;
    }

}