package com.fieldnation.userprofile.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fieldnation.userprofile.BR;
import com.fieldnation.userprofile.R;
import com.fieldnation.userprofile.databinding.ItemLayoutLoadingBinding;
import com.fieldnation.userprofile.databinding.ItemLayoutUserBinding;
import com.fieldnation.userprofile.model.User;
import com.fieldnation.userprofile.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    public UserListAdapter(List<User> listOfUsers, Context context) {
        this.userList = listOfUsers;
        this.filterableList.addAll(userList);
        this.context = context;
    }

    private List<User> userList;
    private final List<User> filterableList = new ArrayList<>();
    private Context context;
    private static final int VIEW_TYPE_LOADING = 110;
    private static final int VIEW_TYPE_NORMAL = 111;
    private boolean isLoaderVisible = false;
    private ValueFilter valueFilter;


    /**
     * Called when RecyclerView needs a new {@link UserDataViewHolder} of the given type to represent
     * an item.
     * <p>
     * This new UserDataViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_NORMAL) {
            ItemLayoutUserBinding binding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.item_layout_user, parent, false);
            return new UserDataViewHolder(binding);

        } else {
            ItemLayoutLoadingBinding binding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.item_layout_loading, parent, false);
            return new ProgressHolder(binding);
        }


    }


    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link UserDataViewHolder#itemView} to reflect the item at the given
     * position.
     * <p>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link UserDataViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p>
     * Override onBindViewHolder instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder   The UserDataViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        User user = userList.get(position);
        if (holder instanceof UserDataViewHolder && user != null) {
            ((UserDataViewHolder) holder).bind(user);
            Glide.with(((UserDataViewHolder) holder).itemLayoutUserBinding.userImage).load(user.getAvatar())
                    .placeholder(getPlaceHolder())
                    .into(((UserDataViewHolder) holder).itemLayoutUserBinding.userImage);

        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class UserDataViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ItemLayoutUserBinding itemLayoutUserBinding;
        public ItemLayoutLoadingBinding itemLoadingBinding;


        public UserDataViewHolder(@NonNull ItemLayoutUserBinding binding) {
            super(binding.getRoot());
            this.itemLayoutUserBinding = binding;
        }

        public void bind(Object obj) {

            if (this.itemLayoutUserBinding != null) {
                this.itemLayoutUserBinding.setVariable(BR.user, obj);
                this.itemLayoutUserBinding.executePendingBindings();
            }
        }

        @Override
        public void onClick(View view) {
        }
    }

    public static class ProgressHolder extends RecyclerView.ViewHolder {
        ItemLayoutLoadingBinding itemLoadingBinding;

        ProgressHolder(ItemLayoutLoadingBinding itemLoadingBinding) {
            super(itemLoadingBinding.getRoot());
            this.itemLoadingBinding = itemLoadingBinding;
        }
    }


    private Drawable getPlaceHolder() {
        return AppCompatResources.getDrawable(context.getApplicationContext().getApplicationContext(), R.drawable.placeholder);
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible) {
            return position == userList.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    public void addContents(List<User> fetchedData) {
        int oldPos = userList.size();
        for (User user : fetchedData) {
            if (!userList.contains(user)) {
                userList.add(user);
            }
        }
        this.filterableList.addAll(userList);
        notifyItemRangeInserted(oldPos, userList.size());
    }

    public void addLoading() {
        isLoaderVisible = true;
        userList.add(null);
        notifyItemInserted(userList.size() - 1);
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = userList.size() - 1;
        userList.remove(position);
        notifyItemRemoved(position);
    }

    public void clear() {
        userList.clear();
        notifyDataSetChanged();
    }

    User getItem(int position) {
        return userList.get(position);
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }


    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<User> filteredList = new ArrayList<>();
            if (constraint != null || constraint.length() > 0) {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (User user : filterableList) {
                    if(user.getFirst_name().toLowerCase().contains(filterPattern) || user.getLast_name().toLowerCase().contains(filterPattern))
                    filteredList.add(user);
                }
            } else {
                filteredList.addAll(filterableList);
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            userList.clear();
            userList.addAll((List) results.values);
            notifyDataSetChanged();
        }


    }

}
    
    
    
    
    
    
    
    

