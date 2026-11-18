package com.zhangshuo.magicnotice.config.helper;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;

import com.zhangshuo.common.util.LogUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HeadsetHelper extends StatusHelper<String> {
    private BroadcastReceiver headsetPlugReceiver;

    public static void getBluetoothList(OnCallback onCallback) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            onCallback.onResult(new ArrayList<>());
            return;
        }
        onCallback.onResult(new ArrayList<>(bluetoothAdapter.getBondedDevices()));
    }

    @Override
    public void register(Context context) {
        headsetPlugReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
                    LogUtils.i("headsetPlugReceiver >> ACTION_CONNECTION_STATE_CHANGED ");
                    onRefresh(context);
                } else if ("android.intent.action.HEADSET_PLUG".equals(action)) {
                    LogUtils.i("headsetPlugReceiver >> android.intent.action.HEADSET_PLUG ");
                    onRefresh(context);
                }
            }
        };
        try {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
            context.registerReceiver(headsetPlugReceiver, intentFilter);
            IntentFilter bluetoothFilter = new IntentFilter(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
            context.registerReceiver(headsetPlugReceiver, bluetoothFilter);
        } catch (Exception e) {

        }
    }

    @Override
    public void unRegister(Context context) {
        if (headsetPlugReceiver != null) {
            try {
                context.unregisterReceiver(headsetPlugReceiver);
            } catch (Exception e) {

            }
        }
    }

    //其他为没有连接，1为有线连接，2为蓝牙连接
    @Override
    public String refreshStatus(Context context) {
        String headsetStatus = "";
        try {
            headsetStatus = getHeadsetStatus(context);
        } catch (Exception e) {

        }
        return headsetStatus;
    }

    public String getHeadsetStatus(Context context) {
        AudioManager audoManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE); //获取声音管理器
        if (audoManager.isWiredHeadsetOn()) {  //有限耳机是否连接
            return "WiredHeadSet";
        }
        BluetoothAdapter ba = null;
        try {
            ba = BluetoothAdapter.getDefaultAdapter();  //蓝牙耳机
        } catch (Exception e) {

        }
        if (ba == null) { //若蓝牙耳机无连接
            return "";
        } else if (ba.isEnabled()) {
            String bluetoothName = "";
            int a2dp = ba.getProfileConnectionState(BluetoothProfile.A2DP);              //可操控蓝牙设备，如带播放暂停功能的蓝牙耳机
            int headset = ba.getProfileConnectionState(BluetoothProfile.HEADSET);        //蓝牙头戴式耳机，支持语音输入输出
            int health = ba.getProfileConnectionState(BluetoothProfile.HEALTH);          //蓝牙穿戴式设备
            //查看是否蓝牙是否连接到三种设备的一种，以此来判断是否处于连接状态还是打开并没有连接的状态
            int flag = -1;
            if (a2dp == BluetoothProfile.STATE_CONNECTED) {
                flag = a2dp;
            } else if (headset == BluetoothProfile.STATE_CONNECTED) {
                flag = headset;
            } else if (health == BluetoothProfile.STATE_CONNECTED) {
                flag = health;
            }
            //说明连接上了三种设备的一种
            if (flag != -1) {
                Set<BluetoothDevice> bondedDevices = ba.getBondedDevices();
                for (BluetoothDevice device : bondedDevices) {
                    try {
                        Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
                        isConnectedMethod.setAccessible(true);
                        boolean isConnected = (boolean) isConnectedMethod.invoke(device, (Object[]) null);
                        if (isConnected) {
                            bluetoothName = device.getName();
                            break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return bluetoothName;
            }
        }
        return "";
    }

    public interface OnCallback {
        void onResult(List<BluetoothDevice> deviceList);
    }
}
