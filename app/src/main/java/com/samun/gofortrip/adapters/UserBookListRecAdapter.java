package com.samun.gofortrip.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.samun.gofortrip.R;
import com.samun.gofortrip.databinding.PendingListModelBinding;
import com.samun.gofortrip.models.Booking;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserBookListRecAdapter extends RecyclerView.Adapter<UserBookListRecAdapter.ViewHolder> {
    Context context;
    List<Booking> bookingList;

    private OnItemClickListener listener;

    public UserBookListRecAdapter(Context context, List<Booking> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.pending_list_model, parent, false), listener);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder v, int position) {
        Booking currentItem = bookingList.get(position);

        Picasso.get()
                .load(currentItem.getPlaceIconUrl())
                .placeholder(R.drawable.ic_baseline_account_box)
                .centerCrop()
                .fit()
                .into(v.b.penIcon);

        v.b.penName.setText(currentItem.getPlaceName());
        v.b.penName.setSelected(true);

        v.b.penTotalPass.setText("Passengers : " + currentItem.getTotalPass());
        v.b.penTotalPass.setSelected(true);

        v.b.penTotalPrice.setText("Price : " + currentItem.getTotalPrice() + " tk");
        v.b.penTotalPrice.setSelected(true);

        v.b.penTransId.setText("Trans ID : " + currentItem.getTransID());
        v.b.penTransId.setSelected(true);

        if (currentItem.isConfirmed()) {
            v.b.penStatus.setTextColor(Color.parseColor("#669900"));
            v.b.penStatus.setText("Confirmed");
            v.b.penStatusIcon.setImageResource(R.drawable.ic_check_circle_outline);
        } else {
            v.b.penStatus.setTextColor(Color.parseColor("#CC0000"));
            v.b.penStatus.setText("Pending...");
            v.b.penStatusIcon.setImageResource(R.drawable.ic_access_time);
        }
    }


    @Override
    public int getItemCount() {
        return bookingList.size();
    }


    public interface OnItemClickListener {
        void onItemClick(int position, View view);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {

        PendingListModelBinding b;

        public ViewHolder(PendingListModelBinding layoutBinding, OnItemClickListener listener) {
            super(layoutBinding.getRoot());
            b = layoutBinding;

            b.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position , view);
                    }
                }
            });
        }
    }

//    private static void goToDetailPage(Context context, Details details) {
//        Intent intent = new Intent(context, DetailsActivity.class);
//        intent.putExtra(INFO , GsonHelper.getStringOf(details));
//        context.startActivity(intent);
//}
}
