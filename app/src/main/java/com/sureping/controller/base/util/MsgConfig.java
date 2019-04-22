package com.sureping.controller.base.util;

import android.bluetooth.BluetoothDevice;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sureping
 * @create 19-4-21.
 */
public class MsgConfig {
/*    private Map<String,String> map;
    private static final String KEY_SELECT = "DEVICE_NAME";
    private static final String KEY_ADDRESS = "DEVICE_ADDRESS";*/
    private BluetoothDevice selectDevice;
    public void setSelectDevice(BluetoothDevice device){
        selectDevice = device;
    }
    public BluetoothDevice getSelectDevice(){
        return selectDevice;
    }
}
