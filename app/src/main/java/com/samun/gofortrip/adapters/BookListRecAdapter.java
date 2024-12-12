package com.samun.gofortrip.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.samun.gofortrip.R;
import com.samun.gofortrip.databinding.BookListModelBinding;
import com.samun.gofortrip.models.Booking;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BookListRecAdapter extends RecyclerView.Adapter<BookListRecAdapter.ViewHolder> {
    Context context;
    List<Booking> bookingList;

    private OnItemClickListener listener;

    public BookListRecAdapter(Context context, List<Booking> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.book_list_model, parent, false), listener);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder v, int position) {
        Booking currentItem = bookingList.get(position);

        if (currentItem.isConfirmed()) {
            v.b.bkListPending.setVisibility(View.INVISIBLE);
            v.b.bkListConfirmed.setVisibility(View.VISIBLE);
        } else {
            v.b.bkListPending.setVisibility(View.VISIBLE);
            v.b.bkListConfirmed.setVisibility(View.INVISIBLE);
        }

        Picasso.get()
                .load(currentItem.getUserImageUrl())
                .placeholder(R.drawable.ic_baseline_account_box)
                .centerCrop()
                .fit()
                .into(v.b.bkListUserIcon);

        v.b.bkListUserName.setText(currentItem.getUserName());
        v.b.bkListUserEmail.setText(currentItem.getUserEmail());
        v.b.bkListUserEmail.setSelected(true);
        v.b.bkListUserPhone.setText(currentItem.getUserPhone());

        Picasso.get()
                .load(currentItem.getPlaceIconUrl())
                .placeholder(R.drawable.ic_baseline_account_box)
                .centerCrop()
                .fit()
                .into(v.b.bookListPkgIcon);

        v.b.bkListPkgName.setText(currentItem.getPlaceName());
        v.b.bkListPkgName.setSelected(true);

        v.b.bkListTotalPass.setText("Passengers : " + currentItem.getTotalPass());
        v.b.bkListTotalPass.setSelected(true);

        v.b.bkListTotalPrice.setText("Price : " + currentItem.getTotalPrice() + " tk");
        v.b.bkListTotalPrice.setSelected(true);

        v.b.bkListTransId.setText("Trans ID : " + currentItem.getTransID());
        v.b.bkListTransId.setSelected(true);
    }


    @Override
    public int getItemCount() {
        return bookingList.size();
    }


    public interface OnItemClickListener {
        void onPkgInfoClick(int position, View view);

        void onConfirm(int position, View view);

        void onCancel(int position, View view);

        void onCall(int position, View view);

        void onEmail(int position, View view);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        BookListModelBinding b;

        public ViewHolder(BookListModelBinding layoutBinding, OnItemClickListener listener) {
            super(layoutBinding.getRoot());
            b = layoutBinding;

            b.bkListPkgInfo.setOnClickListener(new View.OnClickListener() {
                /** PKG INFO */
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onPkgInfoClick(position, view);
                    }
                }
            });

            b.bkListConfirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {    /** CONFIRM */
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onConfirm(position, view);
                    }
                }
            });

            b.bkListCancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {    /** CANCEL */
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onCancel(position, view);
                    }
                }
            });

            b.bkListEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {    /** EMAIL */
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onEmail(position, view);
                    }
                }
            });

            b.bkListPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {    /** CALL */
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onCall(position, view);
                    }
                }
            });
        }
    }

}
