package com.samun.gofortrip.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.samun.gofortrip.R;
import com.samun.gofortrip.databinding.ImageModelBinding;
import com.samun.gofortrip.databinding.ReviewListModelBinding;
import com.samun.gofortrip.models.Review;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ReviewListRecAdapter extends RecyclerView.Adapter<ReviewListRecAdapter.ViewHolder> {

    Context context;
    List<Review> reviewList;

    public ReviewListRecAdapter(Context context, List<Review> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.review_list_model, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder v, int position) {

            Review aReview = reviewList.get(position);

            Picasso.get()
                    .load(aReview.getUserIconUrl())
                    .placeholder(R.drawable.ic_baseline_account_box)
                    .fit()
                    .centerCrop()
                    .into(v.b.revListUserProfilePic);

            v.b.revListUserName.setText(aReview.getUserName());
            v.b.revListRatBar.setRating(aReview.getRating());
            v.b.revListReview.setText(aReview.getReview());

    }


    @Override
    public int getItemCount() {
        return reviewList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        ReviewListModelBinding b;
        public LinearLayout.LayoutParams params;

        public ViewHolder(ReviewListModelBinding b) {
            super(b.getRoot());
            this.b = b;

            params = new LinearLayout.LayoutParams(0, 0);

        }
    }
}
