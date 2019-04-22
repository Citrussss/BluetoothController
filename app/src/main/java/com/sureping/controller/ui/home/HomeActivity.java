package com.sureping.controller.ui.home;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.pgyersdk.crash.PgyCrashManager;
import com.sureping.controller.R;
import com.sureping.controller.base.config.Configuration;
import com.sureping.controller.base.cycle.BaseActivity;
import com.sureping.controller.base.msg.EventMsg;
import com.sureping.controller.databinding.ActivityHomeBinding;
import com.sureping.controller.ui.ControllerApplication;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.reactivex.disposables.Disposable;

import static com.sureping.controller.base.msg.EventMsg.KEY_OPEN_BLUETOOTH;
import static com.sureping.controller.base.msg.EventMsg.KEY_OPEN_BLUETOOTH_RESULT;

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
    private BluetoothAdapter bluetoothAdapter;
    private final int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Disposable disposable = rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(it -> {
                    if (it) {
                        PgyCrashManager.register();
                    }
                });
        bluetoothAdapter= Configuration.adapter = BluetoothAdapter.getDefaultAdapter();
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
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void openBlueTooth(EventMsg eventMsg){
        if (eventMsg.getCode() ==EventMsg.KEY_OPEN_BLUETOOTH){
            if (bluetoothAdapter == null) {
                return;
            }
            if (!bluetoothAdapter.isEnabled()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, REQUEST_ENABLE_BT);
            } else {
                EventBus.getDefault().post(new EventMsg(KEY_OPEN_BLUETOOTH_RESULT));
            }
        }
    }

    protected void onActivityResult(int var1, int var2, Intent var3) {
        if (var1 == this.REQUEST_ENABLE_BT) {
            if (var2 != -1) {
                this.toast("打开蓝牙失败");
                return;
            }
            EventBus.getDefault().post(new EventMsg(KEY_OPEN_BLUETOOTH_RESULT));
        }
    }
}

