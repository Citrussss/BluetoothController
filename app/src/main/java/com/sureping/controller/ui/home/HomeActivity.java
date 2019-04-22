package com.sureping.controller.ui.home;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.sureping.controller.R;
import com.sureping.controller.base.cycle.BaseActivity;
import com.sureping.controller.base.msg.EventMsg;
import com.sureping.controller.databinding.ActivityHomeBinding;
import com.sureping.controller.ui.ControllerApplication;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @author sureping
 * @create 19-4-21.
 */
public class HomeActivity extends BaseActivity<ActivityHomeBinding> {
    @Override
    protected int getViewLayout() {
        return R.layout.activity_home;
    }

    private ConnectBlueToothFragment selectFragment = new ConnectBlueToothFragment();
    private ControllerFragment controllerFragment = new ControllerFragment();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        selectDevice();
    }

    public void selectDevice() {
        if (!selectFragment.isAdded()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            getSupportFragmentManager().beginTransaction()
                    .remove(controllerFragment)
                    .add(R.id.content, selectFragment)
                    .commitAllowingStateLoss();
        }
    }

    public void controlDevice() {
        if (ControllerApplication.getConfig().getSelectDevice() != null && !controllerFragment.isAdded()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            getSupportFragmentManager().beginTransaction()
                    .remove(selectFragment)
                    .add(R.id.content, controllerFragment)
                    .commitAllowingStateLoss();
        } else {
            selectDevice();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(EventMsg eventMsg) {
        if (eventMsg.getCode() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            selectDevice();
        } else if (eventMsg.getCode() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            controlDevice();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    public void onSelectClick(View view) {
        selectDevice();
    }

    public void onControlClick(View view) {
        controlDevice();
    }

}

