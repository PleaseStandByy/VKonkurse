package com.example.pleasestop.vkonkurse.Adapters;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.databinding.library.baseAdapters.BR;

import java.util.ArrayList;
import java.util.List;


public class RecyclerUniversalAdapter<T, P> extends RecyclerView.Adapter<RecyclerUniversalAdapter.ViewHolder> {
    private List<T> items = new ArrayList<>();
    private P presenter;
    private int layout;

    public RecyclerUniversalAdapter(int layout, P presenter) {
        this.layout = layout;
        this.presenter = presenter;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerUniversalAdapter.ViewHolder holder,
                                 int position) {
        final T item = items.get(position);
        holder.getBinding().setVariable(BR.item, item);
        holder.getBinding().setVariable(BR.presenter, presenter);
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addAll(List<T> items) {
        addAll(items, false);
    }

    public void addAll(List<T> items,
                       boolean sortDistricts) {
        this.items.addAll(items);

        notifyDataSetChanged();
    }

    public void clear() {
        items.clear();
    }

    public void add(T item) {
        items.add(item);
        notifyItemInserted(items.size() - 1);
        notifyItemRangeChanged(items.size() - 1, items.size());
    }

    public T getItem(int i) {
        return items.get(i);
    }

    public List<T> getItems() {
        return items;
    }

    public void setList(List<T> list) {
        this.items = list;
    }

    public void remove(T item) {
        int i = items.indexOf(item);
        items.remove(item);
        notifyItemRemoved(i);
        notifyItemRangeChanged(i, items.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ViewDataBinding binding;

        public ViewHolder(View rowView) {
            super(rowView);
            binding = DataBindingUtil.bind(rowView);
        }

        public ViewDataBinding getBinding() {
            return binding;
        }

    }
}



