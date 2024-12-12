package com.samun.gofortrip.fragments;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.samun.gofortrip.R;
import com.samun.gofortrip.adapters.ImageListRecAdapter;
import com.samun.gofortrip.databinding.FragmentPkgDetailsBinding;
import com.samun.gofortrip.helpers.OverlayProgBar;
import com.samun.gofortrip.helpers.PrefManager;
import com.samun.gofortrip.helpers.RatingPieChart;
import com.samun.gofortrip.helpers.UserVerification;
import com.samun.gofortrip.helpers.Utils;
import com.samun.gofortrip.models.Booking;
import com.samun.gofortrip.models.Package;
import com.samun.gofortrip.models.Review;
import com.samun.gofortrip.models.UserInfo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.CLIPBOARD_SERVICE;
import static com.samun.gofortrip.activities.SplashActivity.bookInfoDB;
import static com.samun.gofortrip.activities.SplashActivity.phone;
import static com.samun.gofortrip.activities.SplashActivity.pkgRevDB;

public class PkgDetailsFragment extends Fragment {
    Context context;
    FragmentPkgDetailsBinding b;
    Package pkg;
    ImageListRecAdapter adapter;
    RecyclerView recyclerView;
    boolean descExpanded;
    Dialog bookDiag , payDiag , sendPayDiag;

    Button diagNext, diagBack;
    EditText diagPass;
    TextView diagTotalPriceTV;
    int diagTotalPrice, diagTotalPass;

    LinearLayout rocketPayBtn;
    ShapeableImageView bKashPayBtn;

    TextView diagHeaderTV , diagMerchantNum;
    EditText diagTransET;
    Button diagBookBtn;
    String diagTransID;


    Booking booking;
    UserInfo userInfo;
    DatabaseReference databaseReference, reviewDB;
    String id;

    List<PieEntry> pieEntries;
    List<Integer> colors;
    int star1, star2, star3, star4, star5;

    PieChart pC;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        b = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_pkg_details, container, false);

        init(getContext());
        setData();
        handleBtns();

//        RatingPieChart.create(PrefManager.getPieEntryList(getContext()), PrefManager.getPieColorList(getContext()) , b.pkgDetPieChart , "Ratings");
        return b.getRoot();
    }

    private void init(Context context) {
        this.context = context;
        pkg = PrefManager.getPkgInfo(context);

        recyclerView = b.recyclerView;
        adapter = new ImageListRecAdapter(context, pkg.getImageUrls());

        descExpanded = false;

        databaseReference = FirebaseDatabase.getInstance().getReference(bookInfoDB());

        reviewDB = FirebaseDatabase.getInstance().getReference(pkgRevDB());

        pieEntries = new ArrayList<>();
        colors = new ArrayList<>();
        star1 = 0;
        star2 = 0;
        star3 = 0;
        star4 = 0;
        star5 = 0;

        pC = new PieChart(context);
        pC.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        b.pkgDetPieChartLiner.addView(pC);

        fetchAllPkgRating();
    }


    private void setData() {
        Picasso.get()
                .load(pkg.getIconUrl())
                .placeholder(R.drawable.ic_baseline_account_box)
                .centerCrop()
                .fit()
                .into(b.pkgDetIcon);

        b.pkgDetName.setText(pkg.getName());
        b.pkgDetAddr.setText(pkg.getAddress());
        b.pkgDetPrice.setText(pkg.getPrice() + " tk");

        b.pkgDetBooking.setText(String.valueOf(pkg.getBookedSeats()));
        int seatLeft = pkg.getSeat() - pkg.getBookedSeats();
        b.pkgDetSeatLeft.setText(String.valueOf(seatLeft));

        setRecView();

        b.pkgDetDesc.setText(pkg.getDesc());
        b.pkgDetDepLocation.setText(pkg.getLocation());
        b.pkgDetDest.setText(pkg.getName());
        b.pkgDetDepTime.setText(pkg.getTime());
        b.pkgDetSeat.setText(String.valueOf(pkg.getSeat()));

    }


    private void setRecView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
    }

    private void handleBtns() {
        b.pkgDetDescExpandBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    /** EXPAND DESC */
                ViewGroup.LayoutParams params = b.pkgDetDesc.getLayoutParams();
                if (descExpanded) {
                    params.height = (int) Utils.dpToPx(context, 150);
                    b.pkgDetDesc.setLayoutParams(params);
                    b.pkgDetDesc2.setVisibility(View.GONE);
                    b.pkgDetDescExpandBtn.setText("Click here to expand");
                    descExpanded = false;
                } else {
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    b.pkgDetDesc.setLayoutParams(params);
                    b.pkgDetDesc2.setVisibility(View.VISIBLE);
                    b.pkgDetDescExpandBtn.setText("Click here to collapse");
                    descExpanded = true;
                }
            }
        });

        b.pkgDetRevExpandBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    /** EXPAND REVIEW */

                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, new ReviewFragment()).addToBackStack(null).commit();

            }
        });

        b.pkgDetBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    /** BOOK */
                if (isSeatAvailable()){
                    showBookDiag();
                }
            }
        });
    }

    private boolean isSeatAvailable() {
        int totalSeat = pkg.getSeat();
        int bookedSeat = pkg.getBookedSeats();

        if ((totalSeat - bookedSeat) > 0) {
            return true;
        } else {
            Toast.makeText(context, "No Seat left", Toast.LENGTH_SHORT).show();
            return false;
        }
    }


    private boolean isSeatAvailableForAll() {
        int totalSeat = pkg.getSeat();
        int bookedSeat = pkg.getBookedSeats();
        int seatLeft = totalSeat - bookedSeat;

        if (diagTotalPass <= seatLeft) {
            return true;
        } else {
            Toast.makeText(context, "Not Enough Seats left", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void fetchAllPkgRating() {

        OverlayProgBar.show(context);

        String pkgChild = UserVerification.getFirebasePath(pkg.getName() + "_" + pkg.getId());

        reviewDB.child(pkgChild).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot data : snapshot.getChildren()) {
                    Review aReview = data.getValue(Review.class);

                    int star = aReview.getRating();


                    switch (star) {
                        case 1:
                            star1++;
                            break;

                        case 2:
                            star2++;
                            break;

                        case 3:
                            star3++;
                            break;

                        case 4:
                            star4++;
                            break;

                        case 5:
                            star5++;
                            break;
                    }
                }


                OverlayProgBar.cancel();
                setPieData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                OverlayProgBar.cancel();
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setPieData() {
        pieEntries.clear();

        if (star5 > 0) {
            pieEntries.add(new PieEntry(star5, "5⭐️"));
            colors.add(Color.parseColor("#00cc00"));
        }

        if (star4 > 0) {
            pieEntries.add(new PieEntry(star4, "4⭐️"));
            colors.add(Color.parseColor("#80ff00"));
        }

        if (star3 > 0) {
            pieEntries.add(new PieEntry(star3, "3⭐️"));
            colors.add(Color.parseColor("#e6e600"));
        }

        if (star2 > 0) {
            pieEntries.add(new PieEntry(star2, "2⭐️"));
            colors.add(Color.parseColor("#ff9900"));
        }

        if (star1 > 0) {
            pieEntries.add(new PieEntry(star1, "1⭐️"));
            colors.add(Color.parseColor("#ff0000"));
        }


        String str = "";

        if (pieEntries.size() > 0) {
            double i = (5 * star5 + 4 * star4 + 3 * star3 + 2 * star2 + star1);
            double i2 = (star1 + star2 + star3 + star4 + star5);
            double average = i / i2;

            str = Math.floor(average * 100) / 100 + "⭐️";
        }


        RatingPieChart.create(pieEntries, colors, pC, str);
        b.pkgDetRating.setText(str);

        if (b.pkgDetRating.getText().toString().isEmpty()) {
            b.pkgDetRating.setText("0.0⭐️");
        }


        PrefManager.savePieEntryList(context, pieEntries);
        PrefManager.savePieColorList(context, colors);
    }

    private void showPaymentDiag() {
        payDiag = new Dialog(context);
        payDiag.setContentView(R.layout.payment_opt_diag);
        payDiag.getWindow().setBackgroundDrawableResource(R.drawable.prob_bar_bg);
        payDiag.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        payDiag.getWindow().getAttributes().windowAnimations = R.style.dialogAnim;
        payDiag.setCancelable(false);
        payDiag.show();

        initPayDiagView();
    }

    private void initPayDiagView() {
        rocketPayBtn = payDiag.findViewById(R.id.rocket_pay);
        bKashPayBtn = payDiag.findViewById(R.id.bkash_pay);

        handlePayDiagBtns();
    }

    private void handlePayDiagBtns() {
        rocketPayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    /** ROCKET PAY */
                showSendPaymentDiag();
                payDiag.cancel();
            }
        });

        bKashPayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    /** BKASH PAY */
                showSendPaymentDiag();
                payDiag.cancel();
            }
        });
    }

    private void showSendPaymentDiag() {
        sendPayDiag = new Dialog(context);
        sendPayDiag.setContentView(R.layout.send_pay_diag);
        sendPayDiag.getWindow().setBackgroundDrawableResource(R.drawable.prob_bar_bg);
        sendPayDiag.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        sendPayDiag.getWindow().getAttributes().windowAnimations = R.style.dialogAnim;
        sendPayDiag.setCancelable(false);
        sendPayDiag.show();

        initSendPayDiagViews();
    }

    private void initSendPayDiagViews() {
        diagHeaderTV = sendPayDiag.findViewById(R.id.send_pay_diag_header);
        diagMerchantNum = sendPayDiag.findViewById(R.id.send_pay_diag_number);
        diagTransET = sendPayDiag.findViewById(R.id.send_pay_diag_trans_id_et);
        diagBookBtn = sendPayDiag.findViewById(R.id.send_pay_diag_book_btn);

        String s = "Send " + diagTotalPrice +" tk to this number.";
        diagHeaderTV.setText(s);

        diagMerchantNum.setText(phone());

        handleSendPayDiagBtns();

    }

    private void handleSendPayDiagBtns() {
        diagMerchantNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    /** COPY MERCHANT NUMBER */
            
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Payment Number", phone());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "Number Copied To Clipboard", Toast.LENGTH_SHORT).show();

            }
        });

        diagBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    /** BOOK BTN */
                if(isTransIDValid()) {
                    tryBook();
                }
            }
        });
    }

    private void showBookDiag() {
        bookDiag = new Dialog(context);
        bookDiag.setContentView(R.layout.book_dialog);
        bookDiag.getWindow().setBackgroundDrawableResource(R.drawable.prob_bar_bg);
        bookDiag.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bookDiag.getWindow().getAttributes().windowAnimations = R.style.dialogAnim;
        bookDiag.setCancelable(false);
        bookDiag.show();

        initBookDiagView();
    }

    private void initBookDiagView() {
        diagNext = bookDiag.findViewById(R.id.s_diag_main_btn);
        diagBack = bookDiag.findViewById(R.id.s_diag_back_btn);
        diagTotalPriceTV = bookDiag.findViewById(R.id.diag_total_price_tv);
        diagPass = bookDiag.findViewById(R.id.s_diag_et);

        annotateDiagTV();

        handleBookDiagBtns();
    }

    private void annotateDiagTV() {
        diagPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s = editable.toString();
                if (!(s.isEmpty())) {
                    diagTotalPass = Integer.parseInt(s);
                    diagTotalPrice = diagTotalPass * pkg.getPrice();
                    diagTotalPriceTV.setText("Total Price : " + diagTotalPrice + " tk");
                } else {
                    diagTotalPriceTV.setText(R.string.diag_total_price_tv_default);
                }
            }
        });
    }

    private void handleBookDiagBtns() {
        diagBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    /** DIAG BACK */
                bookDiag.cancel();
            }
        });

        diagNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    /** DIAG NEXT */
                if (isPassNumValid() && isSeatAvailableForAll()) {
                    showPaymentDiag();
                    bookDiag.cancel();
                }
            }
        });
    }

    private void tryBook() {
        OverlayProgBar.show(context);

        userInfo = PrefManager.getUserInfo(context);
        id = databaseReference.push().getKey();

        booking = new Booking(id, userInfo.getEmail(), userInfo.getName(), userInfo.getPhone(), userInfo.getImgUrl(), diagTransID, pkg.getName(), pkg.getId(), pkg.getIconUrl(), diagTotalPass, diagTotalPrice, false);

        String pathName = UserVerification.getFirebasePath(pkg.getName()) + "_" + UserVerification.getFirebasePath(userInfo.getEmail()) + "_" + id;

        databaseReference.child(pathName).setValue(booking)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Booking Success. Awaiting for approval.", Toast.LENGTH_SHORT).show();
                        sendPayDiag.cancel();
                        OverlayProgBar.cancel();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                OverlayProgBar.cancel();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private boolean isPassNumValid() {
        String diagPass = this.diagPass.getText().toString().trim();
        if (diagPass.isEmpty()) {
            this.diagPass.setError("Please specify amount of passenger");
            return false;
        }
        return true;
    }

    private boolean isTransIDValid() {
        String transID = diagTransET.getText().toString().trim();
        if (transID.isEmpty()) {
            diagTransET.setError("Please enter your Transaction ID");
            return false;
        } else {
            diagTransID = transID;
            return true;
        }
    }

}