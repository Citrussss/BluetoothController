package com.sureping.controller.recycle;

import android.bluetooth.BluetoothDevice;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

/**
 * @author sureping
 * @create 19-4-23.
 */
public class BlueToothEntity {
    private BluetoothDevice device;

    public BlueToothEntity(BluetoothDevice device) {
        this.device = device;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public void onSelectClick(View view){
        EventBus.getDefault().post(this);
    }
}
