package com.sureping.controller.ui.home;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;

import com.pgyersdk.crash.PgyCrashManager;
import com.sureping.controller.R;
import com.sureping.controller.base.config.Configuration;
import com.sureping.controller.base.cycle.BaseFragment;
import com.sureping.controller.base.msg.EventMsg;
import com.sureping.controller.databinding.FragmentBlueConnectBinding;
import com.sureping.controller.databinding.FragmentBlueControllerBinding;
import com.sureping.controller.ui.ControllerApplication;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.reactivex.disposables.Disposable;

import static com.sureping.controller.base.config.Configuration.bt_bg;
import static com.sureping.controller.base.msg.EventMsg.KEY_OPEN_BLUETOOTH;

/**
 * @author sureping
 * @create 19-4-21.
 */
public class ControllerFragment extends BaseFragment<FragmentBlueControllerBinding> {
    private BluetoothDevice device;
    private BluetoothAdapter adapter;
    public ObservableField<String> controlStatusText = new ObservableField<>("未连接");
    public ObservableBoolean connectOb = new ObservableBoolean(false);
    private ConnectedThread connectedThread = null;
    private ConnectThread connectThread = null;
    private int REQUEST_ENABLE_BT = 1;
    public ObservableField<String> debugValue =new ObservableField<>("");
    @Override
    protected int getViewLayout() {
        return R.layout.fragment_blue_controller;
    }

    public void onBackClick(View view) {
        EventBus.getDefault().post(new EventMsg(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT));
    }

    @Override
    protected void init() {
        super.init();
        adapter = Configuration.adapter;
        device = ControllerApplication.getConfig().getSelectDevice();
    }

    private class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice var2) {
            try {
                this.mmSocket = var2.createRfcommSocketToServiceRecord(Configuration.COMUUID);
            } catch (Exception var3) {
                var3.printStackTrace();
            }
        }

        public void cancel() {
            try {
                if (this.mmSocket != null) {
                    this.mmSocket.close();
                    this.mmSocket = null;
                }

            } catch (IOException var2) {
                var2.printStackTrace();
            }
        }

        public void run() {
            adapter.cancelDiscovery();

            try {
                this.mmSocket.connect();
                Message var5 = viewHandler.obtainMessage();
                var5.obj = 1;
                viewHandler.sendMessage(var5);
                connectedThread = new ConnectedThread(this.mmSocket);
            } catch (Exception var4) {
                Exception var1 = var4;
                Message var2 = viewHandler.obtainMessage();
                var2.obj = -2;
                viewHandler.sendMessage(var2);
                try {
                    this.mmSocket.close();
                    var1.printStackTrace();
                    setNULL();
                } catch (Exception var3) {
                    var3.printStackTrace();
                }
            }
        }
    }

    private class ConnectedThread extends Thread {
        private InputStream mmInStream;
        private OutputStream mmOutStream;
        private BluetoothSocket mmSocket;

        public ConnectedThread(BluetoothSocket var2) {
            this.mmSocket = var2;
            InputStream var7 = null;
            Object var4 = null;

            OutputStream var9;
            label25:
            {
                InputStream var3;
                label24:
                {
                    IOException var10000;
                    label29:
                    {
                        boolean var10001;
                        try {
                            var3 = var2.getInputStream();
                        } catch (IOException var6) {
                            var10000 = var6;
                            var10001 = false;
                            break label29;
                        }

                        var7 = var3;

                        try {
                            var9 = var2.getOutputStream();
                            break label24;
                        } catch (IOException var5) {
                            var10000 = var5;
                            var10001 = false;
                        }
                    }

                    IOException var8 = var10000;
                    var8.printStackTrace();
                    var9 = (OutputStream) var4;
                    break label25;
                }

                var7 = var3;
            }

            this.mmInStream = var7;
            this.mmOutStream = var9;
        }

        public void cancel() {
            try {
                this.mmOutStream.close();
                this.mmInStream.close();
                this.mmSocket.close();
                this.mmSocket = null;
                this.mmOutStream = null;
                this.mmInStream = null;
            } catch (IOException var2) {
                var2.printStackTrace();
            }
        }

        public void run() {
            byte[] var1 = new byte[1024];

            try {
                Message var2 = handler.obtainMessage();
                var2.obj = this.mmInStream.read(var1);
                handler.sendMessage(var2);
            } catch (Exception var3) {
                var3.printStackTrace();
            }
        }

        public void write(byte[] var1) throws Exception {
            this.mmOutStream.write(var1);
        }
    }

    Handler viewHandler = new Handler() {
        public void handleMessage(Message var1) {
            super.handleMessage(var1);
            switch ((Integer) var1.obj) {
                case -2:
                    controlStatusText.set("连接失败");
                    return;
                case -1:
                    controlStatusText.set("已断开连接");
                    return;
                case 0:
                default:
                    return;
                case 1:
                    controlStatusText.set("已连接");
            }
        }
    };
    Handler handler = new Handler() {
        public void handleMessage(Message var1) {
            super.handleMessage(var1);
            toast("handler be run ");
            toast("   " + var1.obj.toString());
            handler.post(connectedThread);
        }
    };

    private void setNULL() {
        if (this.connectedThread != null) {
            this.connectedThread.cancel();
            this.connectedThread = null;
        }

        if (this.connectThread != null) {
            this.connectThread.cancel();
            this.connectThread = null;
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void openResult(EventMsg eventMsg) {
        if (eventMsg.getCode() == EventMsg.KEY_OPEN_BLUETOOTH_RESULT) {
            if (adapter == null) {
                return;
            }
            if (!this.adapter.isEnabled()) {
                this.toast("打开蓝牙失败");
                connectOb.set(false);
                return;
            }
            connectOb.set(true);
            toast("打开蓝牙成功");
            controlStatusText.set("正在连接设备中");
            if (this.connectThread == null) {
                this.connectThread = new ConnectThread(Configuration.beConnDevice);
            }
            this.connectThread.start();
        }
    }

    public void OpenBluetooth() {
        if (this.adapter != null) {
            if (!adapter.isEnabled()) {
                EventBus.getDefault().post(new EventMsg(KEY_OPEN_BLUETOOTH));
            }
        } else {
            connectOb.set(false);
            this.toast("您的手机不支持蓝牙");
        }
    }

    public void onOpenClick(View view) {
        if (!connectOb.get()) {
            connectOb.set(true);
            OpenBluetooth();
        } else {
            close();
        }
    }

    private void close() {
        this.adapter.disable();
        this.setNULL();
        this.toast("蓝牙已被您关闭");
        connectOb.set(false);
    }
    public boolean onTouch(View v, MotionEvent event){
        int action = event.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                onClick(v);return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
                try {
                    connectedThread.write(Configuration.bt_bg);
                } catch (Exception e) {
                    e.printStackTrace();
                }return true;
        }
        return false;
    }
    public boolean onFunctionKeyTouch(View v, MotionEvent event){
        int action = event.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                onClick(v);return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
                try {
                    switch (v.getId()){
                        case R.id.light:connectedThread.write(Configuration.close_light);break;
                        case R.id.sound:connectedThread.write(Configuration.close_bee);break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }return true;
        }
        return false;
    }
    public void onClick(View view){
        byte[] code = bt_bg;
        switch (view.getId()){
            case R.id.left_s: code = Configuration.left_s;break;
            case R.id.top_s: code=Configuration.top_s;break;
            case R.id.right_s: code = Configuration.right_s;break;
            case R.id.bottom_s: code =Configuration.bottom_s;break;
            case R.id.bt_bg: code = Configuration.bt_bg;break;
            case R.id.light:code = Configuration.open_light;break;
            case R.id.sound:code = Configuration.open_bee;break;
            default:
                if (!TextUtils.isEmpty(debugValue.get())){
                    try {
                        connectedThread.write(debugValue.get().getBytes());
                    } catch (Exception e) {
                        PgyCrashManager.reportCaughtException(e);
                    }
                }else {
                    toast("debug值不能为空");
                }
        }
        try {
            connectedThread.write(code);
        } catch (Exception e) {
            PgyCrashManager.reportCaughtException(e);
        }
    }

    public void onPause() {
        if (this.connectedThread != null) {
            this.connectThread.cancel();
            this.connectThread = null;
        }

        if (this.connectedThread != null) {
            try {
                this.connectedThread.write(Configuration.STOP.getBytes());
            } catch (Exception var2) {
                Message var1 = this.viewHandler.obtainMessage();
                var1.obj = -1;
                this.viewHandler.sendMessage(var1);
            }

            this.connectedThread.cancel();
            this.connectedThread = null;
        }

        super.onPause();
    }
    public void onResume() {
        super.onResume();
        this.connectThread = new ConnectThread(Configuration.beConnDevice);
        this.connectThread.start();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        close();
    }
}
