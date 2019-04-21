package com.sureping.controller.base.cycle;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sureping.controller.BR;

/**
 * @author sureping
 * @create 19-4-21.
 */
public abstract class BaseFragment<DataBinding extends ViewDataBinding> extends Fragment {
    protected abstract int getViewLayout();
    protected DataBinding dataBinding;
    protected void init() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dataBinding = DataBindingUtil.inflate(LayoutInflater.from(this.getActivity()), getViewLayout(), container, false);
        dataBinding.setVariable(BR.vm,this);
        init();
        return dataBinding.getRoot();
    }

    public DataBinding getDataBinding() {
        return dataBinding;
    }
}
