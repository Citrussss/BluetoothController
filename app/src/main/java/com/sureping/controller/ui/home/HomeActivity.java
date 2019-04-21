package com.sureping.controller.ui.home;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sureping.controller.R;
import com.sureping.controller.base.cycle.BaseActivity;
import com.sureping.controller.databinding.ActivityHomeBinding;

/**
 * @author sureping
 * @create 19-4-21.
 */
public class HomeActivity extends BaseActivity<ActivityHomeBinding> {
    @Override
    protected int getViewLayout() {
        return R.layout.activity_home;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content,new ConnectBlueToothFragment())
                .commitAllowingStateLoss();
    }
}
