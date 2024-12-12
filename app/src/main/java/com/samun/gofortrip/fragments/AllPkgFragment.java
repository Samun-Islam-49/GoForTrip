package com.samun.gofortrip.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.samun.gofortrip.R;
import com.samun.gofortrip.adapters.PkgListRecAdapter;
import com.samun.gofortrip.databinding.FragmentAllPkgBinding;
import com.samun.gofortrip.activities.SplashActivity;
import com.samun.gofortrip.helpers.OverlayProgBar;
import com.samun.gofortrip.helpers.PrefManager;
import com.samun.gofortrip.helpers.ShortDiag;
import com.samun.gofortrip.helpers.UserVerification;
import com.samun.gofortrip.models.Booking;
import com.samun.gofortrip.models.Package;

import java.util.ArrayList;
import java.util.List;

public class AllPkgFragment extends Fragment implements View.OnClickListener , PkgListRecAdapter.OnItemClickListener, SearchView.OnCloseListener , SearchView.OnQueryTextListener{
    FragmentAllPkgBinding b;
    Context context;
    DatabaseReference databaseReference, bookListRef , revDB;
    StorageReference storageReference;
    List<Package> packageList;
    PkgListRecAdapter adapter;
    RecyclerView recyclerView;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        b = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_all_pkg, container, false);

        init(getContext());
        setPkgData();

        return b.getRoot();
    }

    private void init(Context context) {
        this.context = context;
        packageList = new ArrayList<>();
        recyclerView = b.recView;
        b.pkgSearchBtn.setOnClickListener(this);
        b.pkgSearch.setOnCloseListener(this);
        b.pkgSearch.setOnQueryTextListener(this);

        databaseReference = FirebaseDatabase.getInstance().getReference(SplashActivity.pkgInfoDB());
        bookListRef = FirebaseDatabase.getInstance().getReference(SplashActivity.bookInfoDB());
        revDB = FirebaseDatabase.getInstance().getReference(SplashActivity.pkgRevDB());
        storageReference = FirebaseStorage.getInstance().getReference(SplashActivity.pkgImageStore());
    }

    private void setPkgData() {
        OverlayProgBar.show(context);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                packageList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    packageList.add(data.getValue(Package.class));
                }

                setAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                OverlayProgBar.cancel();
            }
        });
    }

    private void setAdapter() {
        adapter = new PkgListRecAdapter(context, packageList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);

        OverlayProgBar.cancel();
    }

    private void tryDeleteIcon(int position) {
        OverlayProgBar.show(context);
        Package currentPkg = packageList.get(position);

        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(currentPkg.getIconUrl());

        reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                tryDeleteImages(currentPkg);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                OverlayProgBar.cancel();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void tryDeleteImages(Package currentPkg) {
        List<String> imageUrls = currentPkg.getImageUrls();
        List<String> deletedUrls = new ArrayList<>();

        for (String url : imageUrls) {
            StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(url);

            reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    deletedUrls.add(url);

                    if (deletedUrls.size() == imageUrls.size()) {
                        tryDeleteAllBooks(currentPkg);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    OverlayProgBar.cancel();
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void tryDeleteAllBooks(Package currentPkg) {
        String matchId = currentPkg.getId();
        List<String> delPathList = new ArrayList<>();
        List<String> deletedPathList = new ArrayList<>();

        bookListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot data : snapshot.getChildren()) {
                    Booking aBooking = data.getValue(Booking.class);

                    assert aBooking != null;
                    if (aBooking.getPlaceID().equals(matchId)) {
                        String path = UserVerification.getFirebasePath(aBooking.getPlaceName()) + "_" + UserVerification.getFirebasePath(aBooking.getUserEmail()) + "_" + aBooking.getId();
                        delPathList.add(path);
                    }
                }

                for (String path : delPathList) {
                    bookListRef.child(path).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            deletedPathList.add(path);

                            if (deletedPathList.size() == delPathList.size()) {
                                tryDeleteReviews(currentPkg);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            OverlayProgBar.cancel();
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                OverlayProgBar.cancel();
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void tryDeleteReviews(Package currentPkg) {

        String revChild = UserVerification.getFirebasePath(currentPkg.getName() + "_" + currentPkg.getId());
        revDB.child(revChild).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                tryDeleteDB(currentPkg);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                OverlayProgBar.cancel();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void tryDeleteDB(Package currentPkg) {
        databaseReference.child(currentPkg.getId()).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        OverlayProgBar.cancel();
                        Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                        setPkgData();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                OverlayProgBar.cancel();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showModifySeatsDiag(int position) {
        ShortDiag dialog = new ShortDiag(context);
        dialog.show("Set Total Seat Amount", "Set");

        dialog.setOnMainBtnClick(new ShortDiag.OnMainBtnClick() {
            @Override
            public void onClick(int amount) {
                tryModifySeats(position, amount, dialog);
            }
        });
    }

    private void tryModifySeats(int position, int amount, ShortDiag dialog) {
        Package currentPkg = packageList.get(position);
        currentPkg.setSeat(amount);

        saveModifiedData(currentPkg, dialog);
    }

    private void showBookSeatsDiag(int position) {
        ShortDiag dialog = new ShortDiag(context);
        dialog.show("Enter Amount Of Seats", "Book");

        dialog.setOnMainBtnClick(new ShortDiag.OnMainBtnClick() {
            @Override
            public void onClick(int amount) {
                tryBookSeats(position, amount, dialog);
            }
        });
    }

    private void tryBookSeats(int position, int amount, ShortDiag dialog) {
        Package currentPkg = packageList.get(position);

        int bookedSeats = currentPkg.getBookedSeats();
        bookedSeats += amount;

        currentPkg.setBookedSeats(bookedSeats);

        saveModifiedData(currentPkg, dialog);
    }

    private void saveModifiedData(Package currentPkg, ShortDiag dialog) {
        OverlayProgBar.show(context);

        String path = currentPkg.getId();
        databaseReference.child(path).setValue(currentPkg).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                OverlayProgBar.cancel();
                dialog.cancel();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                OverlayProgBar.cancel();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    /** ALL BUTTONS */

    @Override
    public void onClick(View view) {
        b.pkgSearch.setVisibility(View.VISIBLE);
        b.pkgSearch.setIconified(false);
        b.pkgSearchBtn.setVisibility(View.GONE);
    }

    /** SEARCH */

    @Override
    public boolean onClose() {
        b.pkgSearch.setVisibility(View.GONE);
        b.pkgSearchBtn.setVisibility(View.VISIBLE);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        adapter.getFilter().filter(s);
        return false;
    }

    /** ADAPTER INTERFACES */

    @Override
    public void onItemClick(int position, View view) {
        PrefManager.savePkgInfo(context, packageList.get(position));
        getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, new PkgDetailsFragment()).addToBackStack(null).commit();
    }

    @Override
    public void onItemDelete(int position) {
        tryDeleteIcon(position);
    }

    @Override
    public void setTotalSeat(int position) {
        showModifySeatsDiag(position);
    }

    @Override
    public void bookSeat(int position) {
        showBookSeatsDiag(position);
    }
}