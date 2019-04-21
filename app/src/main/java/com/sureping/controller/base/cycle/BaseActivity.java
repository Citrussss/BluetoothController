package com.sureping.controller.base.cycle;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;

import com.sureping.controller.BR;

/**
 * @author sureping
 * @create 19-4-21.
 */
public abstract class BaseActivity<DataBinding extends ViewDataBinding> extends AppCompatActivity {
    protected abstract int getViewLayout();
    protected DataBinding dataBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = DataBindingUtil.setContentView(this,getViewLayout());
        dataBinding.setVariable(BR.vm,this);
    }

    public DataBinding getDataBinding() {
        return dataBinding;
    }
}
