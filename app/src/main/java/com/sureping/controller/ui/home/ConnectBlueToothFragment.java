package com.sureping.controller.ui.home;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.sureping.controller.R;
import com.sureping.controller.base.config.Configuration;
import com.sureping.controller.base.cycle.BaseFragment;
import com.sureping.controller.base.msg.EventMsg;
import com.sureping.controller.databinding.FragmentBlueConnectBinding;
import com.sureping.controller.ui.ControllerApplication;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sureping.controller.base.msg.EventMsg.KEY_OPEN_BLUETOOTH;
import static com.sureping.controller.base.msg.EventMsg.KEY_OPEN_BLUETOOTH_RESULT;


/**
 * @author sureping
 * @create 19-4-21.
 */
public class ConnectBlueToothFragment extends BaseFragment<FragmentBlueConnectBinding> {
    private BluetoothAdapter bluetoothAdapter;
    private Map<String, BluetoothDevice> devices;
    private ArrayAdapter<String> deviceListAdapter;
    private Spinner spinner;

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
    public void openResult(EventMsg eventMsg){
        if (eventMsg.getCode() ==EventMsg.KEY_OPEN_BLUETOOTH_RESULT){
            if (bluetoothAdapter == null) {
                return;
            }
            if (bluetoothAdapter.isEnabled()) {
                deviceList();
            }
        }
    }
    protected void deviceList() {
        List<String> deviceName = new ArrayList<>();
        for (BluetoothDevice device : bluetoothAdapter.getBondedDevices()) {
            devices.put(device.getName(), device);
            deviceName.add(device.getName());
        }
        refreshList(deviceName);
    }

    protected void refreshList(List<String> list) {
        if (this.getContext() == null) throw new RuntimeException("not fount context!");
        if (deviceListAdapter == null)
            deviceListAdapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_item);
        deviceListAdapter.clear();
        deviceListAdapter.addAll(list);
        spinner.setAdapter(deviceListAdapter);
    }

    protected void init() {
        super.init();
        spinner = getDataBinding().spinner;
        devices = new HashMap<>();
        bluetoothAdapter = Configuration.adapter;
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        });
    }

    @Override
    protected int getViewLayout() {
        return R.layout.fragment_blue_connect;
    }

    public void onConnectClick(View view) {
        connect();
    }

    public void onControlClick(View view) {
        EventBus.getDefault().post(new EventMsg(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE));
    }

    @Override
    public void onDestroy() {
        spinner.setOnItemSelectedListener(null);
        super.onDestroy();
    }
}
