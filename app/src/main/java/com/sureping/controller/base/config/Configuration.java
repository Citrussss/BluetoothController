//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sureping.controller.base.config;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.util.UUID;

public class Configuration {
    public static String A = "s";
    public static String A_NAME = "A";
    public static String Access_name = "00001101-0000-1000-8000-00805F9B34FB";
    public static String B = "x";
    public static String B_NAME = "B";
    public static String C = "y";
    public static UUID COMUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static String C_NAME = "C";
    public static String D = "z";
    public static String DOWN = "b";
    public static String DOWN_NAME = "DOWN";
    public static String D_NAME = "D";
    public static String LEFT = "c";
    public static String LEFTDOWN = "g";
    public static String LEFTDOWN_NAME = "LEFTDOWN";
    public static String LEFTUP = "e";
    public static String LEFTUP_NAME = "LEFTUP";
    public static String LEFT_NAME = "LEFT";
    public static String RIGHT = "d";
    public static String RIGHTDOWN = "h";
    public static String RIGHTDOWN_NAME = "RIGHTDOWN";
    public static String RIGHTUP = "f";
    public static String RIGHTUP_NAME = "RIGHTUP";
    public static String RIGHT_NAME = "RIGHT";
    public static String STOP = "s";
    public static String STOP_NAME = "STOP";
    public static String UP = "a";
    public static String UP_NAME = "UP";
    public static BluetoothAdapter adapter = null;
    public static BluetoothDevice beConnDevice = null;
    public static String isSaved = "isSaved";
    public static String preferencesName = "KevinSetup";
    public static byte[] bt_bg = new byte[]{(byte) 0xFF, 0x00, 0x01, 0x00, (byte) 0xFF};
    public static byte[] top_s = new byte[]{(byte) 0xFF, 0x00, 0x02, 0x00, (byte) 0xFF};
    public static byte[] bottom_s = new byte[]{(byte) 0xFF, 0x00, 0x03, 0x00, (byte) 0xFF};
    public static byte[] left_s = new byte[]{(byte) 0xFF, 0x00, 0x04, 0x00, (byte) 0xFF};
    public static byte[] right_s = new byte[]{(byte) 0xFF, 0x00, 0x05, 0x00, (byte) 0xFF};


    public Configuration() {
    }
}
