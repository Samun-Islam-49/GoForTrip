package com.samun.gofortrip.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.samun.gofortrip.R;
import com.samun.gofortrip.adapters.BookListRecAdapter;
import com.samun.gofortrip.databinding.FragmentBookListBinding;
import com.samun.gofortrip.helpers.OverlayProgBar;
import com.samun.gofortrip.helpers.PrefManager;
import com.samun.gofortrip.helpers.UserVerification;
import com.samun.gofortrip.models.Booking;
import com.samun.gofortrip.models.Package;
import com.samun.gofortrip.models.UserInfo;

import java.util.ArrayList;
import java.util.List;

import static com.samun.gofortrip.activities.SplashActivity.bookInfoDB;
import static com.samun.gofortrip.activities.SplashActivity.pkgInfoDB;

public class BookListFragment extends Fragment {
    FragmentBookListBinding b;
    Context context;
    List<Booking> bookingListPending, bookingListConfirmed;
    UserInfo userInfo;
    RecyclerView recyclerView;
    BookListRecAdapter adapter;
    TabLayout tabLayout;

    DatabaseReference bookListRef, pkgListRef;

    public static final int REQUEST_CALL = 88 , REQUEST_SMS = 99;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        b = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_book_list, container, false);

        init(getContext());
        getPendingBookList();
        handelTabClick();

        return b.getRoot();
    }

    private void init(Context context) {
        this.context = context;
        bookingListPending = new ArrayList<>();
        bookingListConfirmed = new ArrayList<>();
        userInfo = PrefManager.getUserInfo(context);
        bookListRef = FirebaseDatabase.getInstance().getReference(bookInfoDB());
        pkgListRef = FirebaseDatabase.getInstance().getReference(pkgInfoDB());
        recyclerView = b.bkListRec;
        tabLayout = b.bkListTabLayout;
    }

    private void handelTabClick() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:    /** PENDING */
                        getPendingBookList();
                        break;

                    case 1:   /** CONFIRMED */
                        getConfirmedBookList();
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    private void getPendingBookList() {
        OverlayProgBar.show(context);
        bookListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookingListPending.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    Booking booking = data.getValue(Booking.class);

                    assert booking != null;
                    if (!booking.isConfirmed()) {
                        bookingListPending.add(booking);
                    }
                }

                setRecView(bookingListPending);
                OverlayProgBar.cancel();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                OverlayProgBar.cancel();
            }
        });
    }

    private void getConfirmedBookList() {
        OverlayProgBar.show(context);

        bookListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookingListConfirmed.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    Booking booking = data.getValue(Booking.class);

                    assert booking != null;
                    if (booking.isConfirmed()) {
                        bookingListConfirmed.add(booking);
                    }
                }

                setRecView(bookingListConfirmed);
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
        adapter = new BookListRecAdapter(context, bookingList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);

        handleBtns(bookingList);
    }

    private void handleBtns(List<Booking> bookingList) {
        adapter.setOnItemClickListener(new BookListRecAdapter.OnItemClickListener() {
            @Override
            public void onPkgInfoClick(int position, View view) {
                goToPkgDet(bookingList ,  position);
                OverlayProgBar.show(context);
            }

            @Override
            public void onConfirm(int position, View view) {
                if(hasSMSPermission()) {
                    tryConfirm(bookingList, position);
                }
            }

            @Override
            public void onCancel(int position, View view) {
                tryDelete(bookingList , position);
            }

            @Override
            public void onCall(int position, View view) {
                tryCall(bookingList , position);
            }

            @Override
            public void onEmail(int position, View view) {
                trySendEmail(bookingList , position);
            }
        });
    }

    private void tryConfirm(List<Booking> bookingList, int position) {
        OverlayProgBar.show(context);
        Booking aBooking = bookingList.get(position);
        aBooking.setConfirmed(true);

        String path = UserVerification.getFirebasePath(aBooking.getPlaceName()) + "_" + UserVerification.getFirebasePath(aBooking.getUserEmail()) + "_" + aBooking.getId();
        bookListRef.child(path).setValue(aBooking).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                bookSeats(aBooking , path);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                OverlayProgBar.cancel();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bookSeats(Booking aBooking, String path) {
        final boolean[] run = {true};
        pkgListRef.child(aBooking.getPlaceID())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (run[0]) {
                            Package pkg = snapshot.getValue(Package.class);

                            int pass = aBooking.getTotalPass();
                            int bookedSeats = pkg.getBookedSeats();

                            pkg.setBookedSeats(bookedSeats + pass);

                            pkgListRef.child(aBooking.getPlaceID()).setValue(pkg).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    FinalizeBook(aBooking, pkg, bookedSeats, path);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();

                                    aBooking.setConfirmed(false);
                                    bookListRef.child(path).setValue(aBooking).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            OverlayProgBar.cancel();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            e.printStackTrace();
                                            OverlayProgBar.cancel();
                                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                            run[0] = false;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();

                        aBooking.setConfirmed(false);
                        bookListRef.child(path).setValue(aBooking).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                OverlayProgBar.cancel();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                                OverlayProgBar.cancel();
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
    }

    private void FinalizeBook(Booking aBooking, Package pkg, int bookedSeats, String path) {

        if (ConfirmSmsSent(aBooking)) {
            OverlayProgBar.cancel();
            Toast.makeText(context, "Confirmed", Toast.LENGTH_SHORT).show();
            getPendingBookList();
        } else {
            pkg.setBookedSeats(bookedSeats);
            pkgListRef.child(aBooking.getPlaceID()).setValue(pkg).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    aBooking.setConfirmed(false);
                    bookListRef.child(path).setValue(aBooking).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            OverlayProgBar.cancel();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                            OverlayProgBar.cancel();
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                    OverlayProgBar.cancel();
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    private boolean ConfirmSmsSent(Booking aBooking) {
        String phoneNum = aBooking.getUserPhone();
        String sms = "Dear " + aBooking.getUserName() + " ,\n" +
                     "Your Request to book seat on GoTrip has been confirmed.\n\n" +
                     "Name : " + aBooking.getUserName() + "\n" +
                     "Total Passenger : " + aBooking.getTotalPass() + "\n" +
                     "Total Amount Paid : " + aBooking.getTotalPrice() + " tk\n" +
                     "Transaction ID : " + aBooking.getTransID() ;

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNum , null , sms , null , null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, e.getMessage() , Toast.LENGTH_SHORT).show();
            return false;
        }
    }


    private void tryDelete(List<Booking> bookingList, int position) {
        OverlayProgBar.show(context);
        Booking aBooking = bookingList.get(position);

        String path = UserVerification.getFirebasePath(aBooking.getPlaceName()) + "_" + UserVerification.getFirebasePath(aBooking.getUserEmail()) + "_" + aBooking.getId();

        bookListRef.child(path).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                OverlayProgBar.cancel();
                Toast.makeText(context, "Canceled", Toast.LENGTH_SHORT).show();
                getPendingBookList();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                OverlayProgBar.cancel();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void tryCall(List<Booking> bookingList, int position) {
        if(hasCallPermission()) {
            String dial = "tel:" + bookingList.get(position).getUserPhone();
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }
    }

    private void trySendEmail(List<Booking> bookingList, int position) {
        String recipient = bookingList.get(position).getUserEmail();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, recipient);

        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent, "Choose an email client"));
    }

    private boolean hasCallPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            return false;
        }
        return true;
    }

    private boolean hasSMSPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.SEND_SMS}, REQUEST_SMS);
            return false;
        }
        return true;
    }

    private void goToPkgDet(List<Booking> bookingList, int position) {
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