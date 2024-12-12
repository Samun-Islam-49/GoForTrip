package com.samun.gofortrip.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.samun.gofortrip.R;
import com.samun.gofortrip.databinding.ActivitySignUpBinding;
import com.samun.gofortrip.helpers.OverlayProgBar;
import com.samun.gofortrip.helpers.PrefManager;
import com.samun.gofortrip.helpers.UserVerification;
import com.samun.gofortrip.models.UserInfo;
import com.squareup.picasso.Picasso;

public class SignUpActivity extends AppCompatActivity {
    ActivitySignUpBinding b;
    Uri imageUri;
    String name, email, pass, conPass, phone;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    FirebaseAuth firebaseAuth;


    public static int IMAGE_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        init();
        handleButtons();

    }

    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference(SplashActivity.userInfoDB());
        storageReference = FirebaseStorage.getInstance().getReference(SplashActivity.userImgStore());
    }

    private void handleButtons() {
        b.profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    /** IMAGE SELECT */
                openFileChooser();
            }
        });

        b.gotoSignInTv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    /** SIGN IN */
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
            }
        });

        b.signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    /** SIGN UP */
                trySignUp();
            }
        });
    }

    private void trySignUp() {
        if (isAllInputValid()) {
            OverlayProgBar.show(this);
            signUp();
        }
    }

    private void signUp() {
        firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                uploadImg();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                OverlayProgBar.cancel();
            }
        });
    }

    private void uploadImg() {
        StorageReference reference = storageReference.child(email + "." + getFileExtension(imageUri));

        reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;
                Uri imageUri = uriTask.getResult();

                assert imageUri != null;
                UserInfo userInfo = new UserInfo(imageUri.toString(), name, email, phone);

                saveData(userInfo);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                OverlayProgBar.cancel();
            }
        });

    }

    private void saveData(UserInfo userInfo) {
        databaseReference.child(UserVerification.getFirebasePath(userInfo.getEmail())).setValue(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                PrefManager.saveUserInfo(SignUpActivity.this, userInfo);
                PrefManager.saveLog(SignUpActivity.this, pass);

                enterIntoApp();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                OverlayProgBar.cancel();
            }
        });
    }

    private void enterIntoApp() {
        Intent intent = new Intent(SignUpActivity.this, MainDrawerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        OverlayProgBar.cancel();
        Toast.makeText(this, "Signed Up Successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    private boolean isAllInputValid() {
        name = b.nameEt.getText().toString().trim();
        if (name.isEmpty()) {
            b.nameEt.setError("Enter Your Name.");
            b.nameEt.requestFocus();
            return false;
        }

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

        pass = b.passEt.getText().toString().trim();
        conPass = b.conPassEt.getText().toString().trim();
        if (pass.isEmpty()) {
            b.passEt.setError("Please enter a Password.");
            b.passEt.requestFocus();
            return false;
        }

        if (pass.length() < 6) {
            b.passEt.setError("Password must be 6 character long");
            b.passEt.requestFocus();
            return false;
        }

        if (!pass.equals(conPass)) {
            b.conPassEt.setError("Password doesn't match.");
            b.conPassEt.requestFocus();
            return false;
        }

        phone = b.phoneEt.getText().toString().trim();
        if ((!phone.startsWith("017") && !phone.startsWith("018") && !phone.startsWith("013") && !phone.startsWith("019") && !phone.startsWith("015")) || phone.length() != 11) {
            b.phoneEt.setError("Please enter a valid phone number.");
            b.phoneEt.requestFocus();
            return false;
        }

        if (imageUri == null) {
            Toast.makeText(this, "Please Select a Image", Toast.LENGTH_SHORT).show();
            openFileChooser();
            return false;
        }

        return true;
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver resolver = getContentResolver();
        MimeTypeMap map = MimeTypeMap.getSingleton();
        return map.getExtensionFromMimeType(resolver.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get()
                    .load(imageUri)
                    .into(b.profileImageView);
        }
    }
}