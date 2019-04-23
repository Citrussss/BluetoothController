package com.sureping.controller.base.recycle;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sureping.controller.BR;
import com.sureping.controller.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sureping
 * @create 19-4-23.
 */
public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.RecycleHolder> {
    private List<BlueToothEntity> list =new ArrayList<>();
    private int layoutId = R.layout.holder_bluetooth_device_mac;

    @NonNull
    @Override
    public RecycleHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ViewDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), layoutId, viewGroup, false);
        RecycleHolder recycleHolder = new RecycleHolder(dataBinding);
        recycleHolder.setDataBinding(list.get(i));
        return recycleHolder;
    }

    public void setList(List<BlueToothEntity> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleHolder viewHolder, int i) {
        viewHolder.setDataBinding(list.get(i));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class RecycleHolder extends RecyclerView.ViewHolder {
        private ViewDataBinding dataBinding;

        private RecycleHolder(@NonNull View itemView) {
            super(itemView);
        }

        public RecycleHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.dataBinding= binding;
        }

        public void setDataBinding(BlueToothEntity t) {
            dataBinding.setVariable(BR.vm, t);
        }
    }
}
