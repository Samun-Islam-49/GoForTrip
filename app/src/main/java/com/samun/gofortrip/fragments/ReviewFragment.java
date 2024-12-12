package com.samun.gofortrip.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.samun.gofortrip.R;
import com.samun.gofortrip.adapters.ReviewListRecAdapter;
import com.samun.gofortrip.databinding.FragmentReviewBinding;
import com.samun.gofortrip.helpers.Diag;
import com.samun.gofortrip.helpers.OverlayProgBar;
import com.samun.gofortrip.helpers.PrefManager;
import com.samun.gofortrip.helpers.RatingPieChart;
import com.samun.gofortrip.helpers.UserVerification;
import com.samun.gofortrip.models.Package;
import com.samun.gofortrip.models.Review;
import com.samun.gofortrip.models.UserInfo;

import java.util.ArrayList;
import java.util.List;

import static com.samun.gofortrip.activities.SplashActivity.pkgRevDB;


public class ReviewFragment extends Fragment implements View.OnClickListener {
    Context context;
    FragmentReviewBinding b;

    Button diagCancel, diagSubmit;
    EditText diagEt;
    RatingBar diagRatBar;
    String review;
    int rating;

    DatabaseReference reviewDB;
    Review aReview;
    Package pkg;
    UserInfo user;
    String id;

    List<PieEntry> pieEntries;
    List<Integer> colors;
    int star1, star2, star3, star4, star5;


    ReviewListRecAdapter adapter;
    List<Review> reviewList;
    RecyclerView recyclerView;

    PieChart pC;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        b = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_review, container, false);

        init(getContext());

        return b.getRoot();
    }

    private void init(Context context) {
        this.context = context;
        b.pkgRevAddRevBtn.setOnClickListener(this);

        reviewList = new ArrayList<>();
        recyclerView = b.pkgRevRecView;

        pkg = PrefManager.getPkgInfo(context);

        reviewDB = FirebaseDatabase.getInstance().getReference(pkgRevDB());
        id = reviewDB.push().getKey();

        pieEntries = new ArrayList<>();
        colors = new ArrayList<>();
        star1 = 0;
        star2 = 0;
        star3 = 0;
        star4 = 0;
        star5 = 0;

        pC = new PieChart(context);
        pC.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        b.pkgRevPieChartLiner.addView(pC);


        fetchAllPkgRating();
    }

    private void fetchAllPkgRating() {

        OverlayProgBar.show(context);

        String pkgChild = UserVerification.getFirebasePath(pkg.getName() + "_" + pkg.getId());

        reviewDB.child(pkgChild).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                reviewList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    Review aReview = data.getValue(Review.class);

                    reviewList.add(aReview);

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
                setRecAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                OverlayProgBar.cancel();
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setRecAdapter() {
        adapter = new ReviewListRecAdapter(context, reviewList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
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
        PrefManager.savePieEntryList(context, pieEntries);
        PrefManager.savePieColorList(context, colors);
    }

    private void showAddReviewDiag() {
        Diag.show(context, R.layout.review);

        initDiagViews();
    }

    private void initDiagViews() {
        diagCancel = Diag.dialog.findViewById(R.id.rev_back_btn);
        diagSubmit = Diag.dialog.findViewById(R.id.rev_submit_btn);
        diagEt = Diag.dialog.findViewById(R.id.rev_review);
        diagRatBar = Diag.dialog.findViewById(R.id.rev_rating_bar);


        diagCancel.setOnClickListener(this);
        diagSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == b.pkgRevAddRevBtn.getId()) {    /** ADD REV BTN */
            showAddReviewDiag();

        } else if (view.getId() == diagCancel.getId()) {   /** DIAG CANCEL BTN */
            Diag.cancel();

        } else if (view.getId() == diagSubmit.getId()) {    /** DIAG SUBMIT BTN */

            if (allDiagInputValid()) {
                trySubmitReview();
            }

        }
    }

    private void trySubmitReview() {
        OverlayProgBar.show(context);

        String pkgChild = UserVerification.getFirebasePath(pkg.getName() + "_" + pkg.getId());
        String revChild = UserVerification.getFirebasePath(user.getEmail() + "_" + id);

        reviewDB.child(pkgChild).child(revChild).setValue(aReview).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Review Successfully Submitted", Toast.LENGTH_SHORT).show();
                Diag.cancel();
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, new ReviewFragment()).commit();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                OverlayProgBar.cancel();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean allDiagInputValid() {
        review = diagEt.getText().toString().trim();
        rating = (int) diagRatBar.getRating();

        if (rating < 1) {
            Toast.makeText(context, "Please Select Star Rating", Toast.LENGTH_SHORT).show();
            return false;
        }

        pkg = PrefManager.getPkgInfo(context);
        user = PrefManager.getUserInfo(context);

        aReview = new Review(id, pkg.getName(), pkg.getId(), user.getName(), user.getEmail(), user.getImgUrl(), review, rating);

        return true;
    }

}