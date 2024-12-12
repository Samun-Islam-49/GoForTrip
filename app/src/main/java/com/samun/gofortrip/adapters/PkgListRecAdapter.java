package com.samun.gofortrip.adapters;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.samun.gofortrip.R;
import com.samun.gofortrip.databinding.PkgListModelBinding;
import com.samun.gofortrip.helpers.UserVerification;
import com.samun.gofortrip.helpers.PrefManager;
import com.samun.gofortrip.models.Package;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PkgListRecAdapter extends RecyclerView.Adapter<PkgListRecAdapter.ViewHolder> implements Filterable {
    Context context;
    List<Package> packageList, packageListFull;

    private OnItemClickListener listener;

    public PkgListRecAdapter(Context context, List<Package> packageList) {
        this.context = context;
        this.packageList = packageList;
        this.packageListFull = new ArrayList<>(packageList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.pkg_list_model, parent, false), listener, context);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder v, int position) {
        v.b.pkgListName.setText(packageList.get(position).getName());
        Picasso.get()
                .load(packageList.get(position).getIconUrl())
                .placeholder(R.drawable.ic_baseline_account_box)
                .centerCrop()
                .fit()
                .into(v.b.pkgListIcon);
    }


    @Override
    public int getItemCount() {
        return packageList.size();
    }


    public interface OnItemClickListener {
        void onItemClick(int position, View view);
        void onItemDelete(int position);
        void setTotalSeat(int position);
        void bookSeat(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private final Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Package> filteredList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(packageListFull);
            } else {
                for (Package info : packageListFull) {
                    if (info.getName().toLowerCase().contains(charSequence.toString().toLowerCase().trim())) {
                        filteredList.add(info);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            packageList.clear();
            packageList.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener , MenuItem.OnMenuItemClickListener {

        Context context;
        PkgListModelBinding b;
        OnItemClickListener listener;

        public ViewHolder(PkgListModelBinding layoutBinding, OnItemClickListener listener, Context context) {
            super(layoutBinding.getRoot());
            b = layoutBinding;
            this.context = context;
            this.listener = listener;

            b.getRoot().setOnCreateContextMenuListener(this);
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

        @Override
        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
            if(!UserVerification.isUser(PrefManager.getUserInfo(context).getEmail())) {
                menu.setHeaderTitle("Choose an Option");
                MenuItem book = menu.add(Menu.NONE , 1 , 1 , "Book Seats");
                MenuItem totalSeat = menu.add(Menu.NONE , 2 , 2 , "Set Total Seat");
                MenuItem delete = menu.add(Menu.NONE , 3 , 3 , "Delete Package");

                book.setOnMenuItemClickListener(this);
                totalSeat.setOnMenuItemClickListener(this);
                delete.setOnMenuItemClickListener(this);
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {

                switch (menuItem.getItemId()) {
                    case 1 :
                        listener.bookSeat(position);
                        return true;

                    case 2 :
                        listener.setTotalSeat(position);
                        return true;

                    case 3 :
                        listener.onItemDelete(position);
                        return true;
                }

            }
            return false;
        }
    }

//    private static void goToDetailPage(Context context, Details details) {
//        Intent intent = new Intent(context, DetailsActivity.class);
//        intent.putExtra(INFO , GsonHelper.getStringOf(details));
//        context.startActivity(intent);
//}
}
