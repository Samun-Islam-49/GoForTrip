package com.samun.gofortrip.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.samun.gofortrip.R;
import com.samun.gofortrip.databinding.ImageModelBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageListRecAdapter extends RecyclerView.Adapter<ImageListRecAdapter.ViewHolder> {

    Context context;
    List<String> imageUrls;

    private OnItemClickListener listener;

    public ImageListRecAdapter(Context context, List<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.image_model, parent, false), listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder v, int position) {
        Picasso.get()
                .load(imageUrls.get(position))
                .placeholder(R.drawable.ic_baseline_account_box)
                .fit()
                .into(v.b.imageModel);
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public interface OnItemClickListener {
        void onMenuClick(int position);

        void onItemClick(int position, View view);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageModelBinding b;

        public ViewHolder(ImageModelBinding b, OnItemClickListener listener) {
            super(b.getRoot());
            this.b = b;

//            b.getRoot().setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (listener != null) {
//                        int position = getAdapterPosition();
//                        if (position != RecyclerView.NO_POSITION) {
////                            listener.onItemClick(position , view);
//                        }
//                    }
//                }
//            });
        }
    }
}
