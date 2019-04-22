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
import com.sureping.controller.base.cycle.BaseFragment;
import com.sureping.controller.base.msg.EventMsg;
import com.sureping.controller.databinding.FragmentBlueConnectBinding;
import com.sureping.controller.ui.ControllerApplication;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            return;
        }
        int REQUEST_ENABLE_BT = 1;
        if (!bluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_ENABLE_BT);
        } else {
            deviceList();
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
//        connect();
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
        spinner = getDataBinding().spinner;
        devices = new HashMap<>();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (view instanceof TextView) {
                    ControllerApplication.getConfig().setSelectDevice(devices.get(((TextView) view).getText()));
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
