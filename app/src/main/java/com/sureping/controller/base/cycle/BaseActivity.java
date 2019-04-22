package com.sureping.controller.base.cycle;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.sureping.controller.BR;
import com.sureping.controller.ui.ControllerApplication;
import com.tbruyelle.rxpermissions2.RxPermissions;

/**
 * @author sureping
 * @create 19-4-21.
 */
public abstract class BaseActivity<DataBinding extends ViewDataBinding> extends AppCompatActivity {
    protected abstract int getViewLayout();

    protected DataBinding dataBinding;
    protected RxPermissions rxPermissions;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = DataBindingUtil.setContentView(this, getViewLayout());
        dataBinding.setVariable(BR.vm, this);
        rxPermissions = new RxPermissions(this);
    }

    public DataBinding getDataBinding() {
        return dataBinding;
    }

    public void toast(String var1) {
        Toast.makeText(ControllerApplication.getInstance(), var1, Toast.LENGTH_SHORT).show();
    }

    public void toast(Throwable throwable) {
        toast(throwable.getMessage());
    }
}
