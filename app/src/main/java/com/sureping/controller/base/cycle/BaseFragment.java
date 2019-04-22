package com.sureping.controller.base.cycle;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sureping.controller.BR;
import com.sureping.controller.ui.ControllerApplication;

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

    public void Logi(String var1) {
        Log.i("kevin", var1);
    }

    public void toast(String var1) {
        Toast.makeText(ControllerApplication.getInstance(), var1, Toast.LENGTH_SHORT).show();
    }
}
