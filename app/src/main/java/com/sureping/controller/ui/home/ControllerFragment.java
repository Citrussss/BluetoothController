package com.sureping.controller.ui.home;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.ActivityInfo;
import android.databinding.ObservableField;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.sureping.controller.R;
import com.sureping.controller.base.config.Configuration;
import com.sureping.controller.base.cycle.BaseFragment;
import com.sureping.controller.base.msg.EventMsg;
import com.sureping.controller.ui.ControllerApplication;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author sureping
 * @create 19-4-21.
 */
public class ControllerFragment extends BaseFragment {
    private BluetoothDevice device;
    private BluetoothAdapter adapter;
    public ObservableField<String> controlStatusText = new ObservableField<>("未连接");
    private ConnectedThread connectedThread = null;
    private ConnectThread connectThread = null;

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
}
