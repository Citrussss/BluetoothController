package com.sureping.controller.ui.home;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;

import com.sureping.controller.R;
import com.sureping.controller.base.config.Configuration;
import com.sureping.controller.base.cycle.BaseFragment;
import com.sureping.controller.base.msg.EventMsg;
import com.sureping.controller.databinding.FragmentBlueConnectBinding;
import com.sureping.controller.base.recycle.BlueToothEntity;
import com.sureping.controller.base.recycle.RecycleAdapter;
import com.sureping.controller.ui.ControllerApplication;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sureping.controller.base.msg.EventMsg.KEY_OPEN_BLUETOOTH;


/**
 * @author sureping
 * @create 19-4-21.
 */
public class ConnectBlueToothFragment extends BaseFragment<FragmentBlueConnectBinding> {
    private BluetoothAdapter bluetoothAdapter;
    private Map<String, BluetoothDevice> devices;
    private ArrayAdapter<String> deviceListAdapter;
    private RecyclerView recyclerView;
    public ObservableBoolean connectOb = new ObservableBoolean(false);
    public ObservableField<String> connectText = new ObservableField<>("关闭");
    public ObservableField<String> listText = new ObservableField<>("您匹配过的设备");
    private RecycleAdapter recycleAdapter;


    protected void connect() {
        if (bluetoothAdapter == null) {
            return;
        }
        if (!bluetoothAdapter.isEnabled()) {
            EventBus.getDefault().post(new EventMsg(KEY_OPEN_BLUETOOTH));
        } else {
            deviceList();
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
//        connect();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void openResult(EventMsg eventMsg) {
        if (eventMsg.getCode() == EventMsg.KEY_OPEN_BLUETOOTH_RESULT) {
            if (bluetoothAdapter == null) {
                return;
            }
            if (bluetoothAdapter.isEnabled()) {
                deviceList();
                connectOb.set(true);
                connectText.set("打开");
            } else {
                connectOb.set(false);
                connectText.set("关闭");
            }
        }
    }

    protected void deviceList() {
        connectOb.set(true);
        List<BlueToothEntity> deviceName = new ArrayList<>();
        for (BluetoothDevice device : bluetoothAdapter.getBondedDevices()) {
            devices.put(device.getName(), device);
            deviceName.add(new BlueToothEntity(device));

        }
        refreshList(deviceName);
    }

    protected void refreshList(List<BlueToothEntity> list) {
        if (this.getContext() == null) throw new RuntimeException("not fount context!");
        if (recycleAdapter == null)
            recycleAdapter = new RecycleAdapter();


        recycleAdapter.setList(list);
        getDataBinding().recycleView.setAdapter(recycleAdapter);
    }

    protected void init() {
        super.init();
        bluetoothAdapter = Configuration.adapter;
        devices = new HashMap<>();
        recyclerView = getDataBinding().recycleView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
       /* rec.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (view instanceof TextView) {
                    ControllerApplication.getConfig().setSelectDevice(devices.get(((TextView) view).getText()));
                    Configuration.beConnDevice = ControllerApplication.getConfig().getSelectDevice();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/
    }

    @Override
    protected int getViewLayout() {
        return R.layout.fragment_blue_connect;
    }

    public void onOpenClick(View view) {
        if (connectOb.get()) {
            close();
        } else {
            connect();
        }
    }

    public void onControlClick(View view) {
        EventBus.getDefault().post(new EventMsg(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void itemSelectEvent(BlueToothEntity entity) {
        ControllerApplication.getConfig().setSelectDevice(entity.getDevice());
        Configuration.beConnDevice = entity.getDevice();
        EventBus.getDefault().post(new EventMsg(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE));
    }

    private void close() {
        this.bluetoothAdapter.disable();
        this.toast("蓝牙已被您关闭");
        connectOb.set(false);
    }

}
