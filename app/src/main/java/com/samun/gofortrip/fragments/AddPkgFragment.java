package com.samun.gofortrip.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.samun.gofortrip.R;
import com.samun.gofortrip.adapters.ImageListRecAdapter;
import com.samun.gofortrip.databinding.FragmentAddPkgBinding;
import com.samun.gofortrip.helpers.OverlayProgBar;
import com.samun.gofortrip.models.Package;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static android.app.Activity.RESULT_OK;
import static com.samun.gofortrip.activities.SignUpActivity.IMAGE_REQUEST;
import static com.samun.gofortrip.activities.SplashActivity.pkgImageStore;
import static com.samun.gofortrip.activities.SplashActivity.pkgInfoDB;

public class AddPkgFragment extends Fragment {
    Context context;
    FragmentAddPkgBinding b;
    ImageListRecAdapter adapter;
    List<String> imageUriStr, imageUrl;
    List<Uri> imageUri;
    RecyclerView recyclerView;
    boolean forIcon;
    String calDate, calTime;
    Uri iconUri;
    String id, iconUrl, name, address, time, location, desc;
    int price, seat;
    DatabaseReference databaseReference;
    StorageReference storageReference, packageRef;
    Package pkg;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        b = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_add_pkg, container, false);

        init(getContext());
        checkRecVis();
        handleBtns();

        return b.getRoot();
    }

    private void init(Context context) {
        this.context = context;

        imageUriStr = new ArrayList<>();
        imageUri = new ArrayList<>();
        imageUrl = new ArrayList<>();

        adapter = new ImageListRecAdapter(context, imageUriStr);
        recyclerView = b.pkgImageSelectRec;

        databaseReference = FirebaseDatabase.getInstance().getReference(pkgInfoDB());
        id = databaseReference.push().getKey();

        storageReference = FirebaseStorage.getInstance().getReference(pkgImageStore());
        packageRef = storageReference.child(id);
    }

    private void handleBtns() {
        b.pkgIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    /** ICON SELECT */
                openFileChooser();
                forIcon = true;
            }
        });

        b.selectImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    /** IMAGE SELECT */
                openFileChooser();
            }
        });

        b.pkgSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    /** SUBMIT PACKAGE */
                if (isAllInputValid()) {
                    OverlayProgBar.show(context);
                    uploadIcon();
                }
            }
        });

        b.pkgSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    /** TIME AND DATE */
                showCalender();
            }
        });

    }

    private boolean isAllInputValid() {
        if (iconUri == null) {
            Toast.makeText(context, "Please Select A Icon", Toast.LENGTH_SHORT).show();
            return false;
        }

        name = b.pkgName.getText().toString().trim();
        if (name.isEmpty()) {
            b.pkgName.setError("Please insert a name.");
            b.pkgName.requestFocus();
            return false;
        }

        address = b.pkgAddr.getText().toString().trim();
        if (address.isEmpty()) {
            b.pkgAddr.setError("Please insert a address.");
            b.pkgAddr.requestFocus();
            return false;
        }

        if (time == null) {
            Toast.makeText(context, "Please Select Departure Schedule", Toast.LENGTH_SHORT).show();
            return false;
        }

        location = b.pkgDepLoc.getText().toString().trim();
        if (location.isEmpty()) {
            b.pkgDepLoc.setError("Please insert a location.");
            b.pkgDepLoc.requestFocus();
            return false;
        }

        String price = b.pkgPrice.getText().toString().trim();
        if (price.isEmpty()) {
            b.pkgPrice.setError("Please insert a price.");
            b.pkgPrice.requestFocus();
            return false;
        } else {
            this.price = Integer.parseInt(price);
        }

        String seat = b.pkgTotalSeat.getText().toString().trim();
        if (seat.isEmpty()) {
            b.pkgTotalSeat.setError("Please insert total seat amount.");
            b.pkgTotalSeat.requestFocus();
            return false;
        } else {
            this.seat = Integer.parseInt(seat);
        }

        if (imageUriStr.size() == 0) {
            Toast.makeText(context, "Please Select at least one image.", Toast.LENGTH_SHORT).show();
            return false;
        }

        desc = b.pkgDesc.getText().toString().trim();
        if (desc.isEmpty()) {
            b.pkgDesc.setError("Please write some description.");
            b.pkgDesc.requestFocus();
            return false;
        }

        return true;
    }

    private void uploadIcon() {
        StorageReference reference = packageRef.child(name + "_Icon_" + System.currentTimeMillis() + "." + getFileExtension(iconUri));

        reference.putFile(iconUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;
                Uri iconUri = uriTask.getResult();

                assert iconUri != null;
                iconUrl = iconUri.toString();

                uploadImages();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                OverlayProgBar.cancel();
            }
        });

    }

    private void uploadImages() {
        for (String str : imageUriStr) {
            imageUri.add(Uri.parse(str));
        }

        if (imageUri.size() == imageUriStr.size()) {

            for (Uri uri : imageUri) {

                StorageReference reference = packageRef.child(name + "_ImageAsset_" + System.currentTimeMillis() + "." + getFileExtension(iconUri));

                reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        Uri imageDownloadUri = uriTask.getResult();

                        assert imageDownloadUri != null;
                        imageUrl.add(imageDownloadUri.toString());

                        if (imageUrl.size() == imageUri.size()) {
                            savePkg();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        OverlayProgBar.cancel();
                    }
                });

            }

        }


    }

    private void savePkg() {
        pkg = new Package(id, iconUrl, name, address, time, location, price, seat, imageUrl, desc);

        databaseReference.child(id).setValue(pkg)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        OverlayProgBar.cancel();
                        Toast.makeText(context, "Package Added Successfully", Toast.LENGTH_SHORT).show();
                        getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, new AllPkgFragment()).commit();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                OverlayProgBar.cancel();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private void checkRecVis() {
        if (imageUriStr.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.GONE);
        }
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            if (forIcon) {
                iconUri = data.getData();
                Picasso.get()
                        .load(iconUri)
                        .placeholder(R.drawable.ic_baseline_account_box)
                        .fit()
                        .into(b.pkgIcon);
                forIcon = false;
            } else {
                imageUriStr.add(data.getData().toString());
                int pos = 0;
                for (String ignored : imageUriStr) {
                    pos++;
                }
                checkRecVis();
                adapter.notifyItemInserted(pos);
            }
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver resolver = context.getContentResolver();
        MimeTypeMap map = MimeTypeMap.getSingleton();
        return map.getExtensionFromMimeType(resolver.getType(uri));
    }

    private void showCalender() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                calendar.set(Calendar.YEAR, y);
                calendar.set(Calendar.MONTH, m);
                calendar.set(Calendar.DAY_OF_MONTH, d);

                calDate = DateFormat.getDateInstance().format(calendar.getTime());

                TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int h, int m) {
                        calendar.set(Calendar.HOUR_OF_DAY, h);
                        calendar.set(Calendar.MINUTE, m);
                        calendar.setTimeZone(TimeZone.getDefault());

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
                        calTime = simpleDateFormat.format(calendar.getTime());

                        time = calDate + "  " + calTime;

                        b.pkgSchedule.setTextColor(Color.parseColor("#000000"));
                        b.pkgSchedule.setText(time);

                    }
                }, hour, minute, false);
                timePickerDialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnim;
                timePickerDialog.show();


            }
        }, year, month, day);
        datePickerDialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnim;
        datePickerDialog.show();
    }
}