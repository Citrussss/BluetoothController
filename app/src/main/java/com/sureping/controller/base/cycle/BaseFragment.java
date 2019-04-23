package com.sureping.controller.base.cycle;

import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.sureping.controller.BR;
import com.sureping.controller.R;
import com.sureping.controller.base.util.ChangeBgUtil;
import com.sureping.controller.base.util.FileViewManager;
import com.sureping.controller.ui.ControllerApplication;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;

import java.util.Objects;

import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.ListCompositeDisposable;

/**
 * @author sureping
 * @create 19-4-21.
 */
public abstract class BaseFragment<DataBinding extends ViewDataBinding> extends Fragment implements PopupMenu.OnMenuItemClickListener {
    protected abstract int getViewLayout();

    protected DataBinding dataBinding;
    protected RxPermissions rxPermissions;
    private final ListCompositeDisposable disposables = new ListCompositeDisposable();
    public ObservableField<String> bgSrc = new ObservableField<>("");

    @CallSuper
    protected void init() {
        rxPermissions = new RxPermissions(getActivity());
        SharedPreferences userSettings = getActivity().getSharedPreferences("setting", 0);
        String url = userSettings.getString(this.getClass().getName() + "bgSrc", "");
        if (!TextUtils.isEmpty(url)) {
            bgSrc.set(url);
        }
    }

    protected void saveBgSrc(String src) {
        SharedPreferences userSettings = Objects.requireNonNull(getActivity()).getSharedPreferences("setting", 0);
        SharedPreferences.Editor editor = userSettings.edit();
        editor.putString(this.getClass().getName() + "bgSrc", src);
        editor.apply();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dataBinding = DataBindingUtil.inflate(LayoutInflater.from(this.getActivity()), getViewLayout(), container, false);
        dataBinding.setVariable(BR.vm, this);
        EventBus.getDefault().register(this);
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

    public void toast(Throwable throwable) {
        toast(throwable.getMessage());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposables.dispose();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    protected void addDispose(Disposable disposable) {
        disposables.add(disposable);
    }

    public void onSelectFileClick(View view) {
        ChangeBgUtil.selectFile(view.getContext(), view, this);
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        String type = "";
        switch (item.getItemId()) {
            case R.id.take:
                type = FileViewManager.takePhoto;
                break;
            case R.id.photo:
                type = FileViewManager.image;
                break;
            default:
                break;
        }
        FileViewManager.selectFile(this.getContext(),file -> {
            saveBgSrc(file.getAbsolutePath());
            bgSrc.set(file.getAbsolutePath());
        },type);
        return false;
    }
}
