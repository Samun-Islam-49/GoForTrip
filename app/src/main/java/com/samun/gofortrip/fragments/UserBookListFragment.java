package com.samun.gofortrip.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.samun.gofortrip.R;
import com.samun.gofortrip.adapters.UserBookListRecAdapter;
import com.samun.gofortrip.databinding.FragmentUserBookListBinding;
import com.samun.gofortrip.activities.SplashActivity;
import com.samun.gofortrip.helpers.OverlayProgBar;
import com.samun.gofortrip.helpers.PrefManager;
import com.samun.gofortrip.models.Booking;
import com.samun.gofortrip.models.Package;
import com.samun.gofortrip.models.UserInfo;

import java.util.ArrayList;
import java.util.List;


public class UserBookListFragment extends Fragment {
    FragmentUserBookListBinding b;
    Context context;
    List<Booking> bookingList;
    UserInfo userInfo;
    RecyclerView recyclerView;
    UserBookListRecAdapter adapter;

    DatabaseReference bookListRef, pkgListRef;

    public static boolean isApproved;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        b = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_user_book_list, container, false);

        init(getContext());
        getAllData();

        return b.getRoot();
    }

    private void init(Context context) {
        this.context = context;
        bookingList = new ArrayList<>();
        userInfo = PrefManager.getUserInfo(context);
        bookListRef = FirebaseDatabase.getInstance().getReference(SplashActivity.bookInfoDB());
        pkgListRef = FirebaseDatabase.getInstance().getReference(SplashActivity.pkgInfoDB());
        recyclerView = b.pendingListRec;
    }

    private void getAllData() {
        OverlayProgBar.show(context);
        bookListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookingList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    Booking booking = data.getValue(Booking.class);

                    assert booking != null;
                    if (booking.getUserEmail().equals(userInfo.getEmail())) {

                        if (isApproved) {

                            if (booking.isConfirmed()) {
                                bookingList.add(booking);
                            }

                        } else {

                            if (!booking.isConfirmed()) {
                                bookingList.add(booking);
                            }
                        }

                    }
                }

                setRecView(bookingList);
                OverlayProgBar.cancel();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                OverlayProgBar.cancel();
            }
        });
    }

    private void setRecView(List<Booking> bookingList) {
        adapter = new UserBookListRecAdapter(context, bookingList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new UserBookListRecAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                OverlayProgBar.show(context);
                getPkgData(bookingList, position);
            }
        });
    }

    private void getPkgData(List<Booking> bookingList, int position) {
        pkgListRef.child(bookingList.get(position).getPlaceID())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Package pkg = snapshot.getValue(Package.class);
                        PrefManager.savePkgInfo(context, pkg);
                        getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, new PkgDetailsFragment()).addToBackStack(null).commit();
                        OverlayProgBar.cancel();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                        OverlayProgBar.cancel();
                    }
                });
    }


}