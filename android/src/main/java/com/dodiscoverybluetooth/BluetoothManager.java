package com.dodiscoverybluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.os.Bundle;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BluetoothManager {

    public static final String TAG = "BluetoothManager";
    public static final boolean D = true;

    private BluetoothAdapter mBtAdapter;
    private BluetoothSocket mmSocket;
    private InputStream mmInStream;
    private OutputStream mmOutStream;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private Context context;
    private String strAddressList = "";
    private Thread thread;

    public BluetoothManager(Context context) {
        this.context = context;
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    // Start Bluetooth device discovery
    public void doDiscovery(final BluetoothDiscoveryListener listener) {
        if (D)
            Log.d(TAG, "doDiscovery()");

        // Start scanning for new devices
        new Thread(new Runnable() {
            @Override
            public void run() {
                int intStartCount = 0;
                while (!mBtAdapter.startDiscovery() && intStartCount < 5) {
                    Log.e("BlueTooth", "Scan attempt failed");
                    intStartCount++;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        // e.printStackTrace();
                    }
                }
            }
        }).start();

        // Register receiver for device discovery
        IntentFilter intent = new IntentFilter();
        intent.addAction(BluetoothDevice.ACTION_FOUND);
        intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                BluetoothDevice device = null;

                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device != null && device.getBondState() == BluetoothDevice.BOND_NONE) {
                        if (device.getBluetoothClass().getMajorDeviceClass() == 1536) {
                            if (!strAddressList.contains(device.getAddress())) {
                                strAddressList += device.getAddress() + ",";
                                // Calculate distance based on RSSI (optional)
                                Bundle b = intent.getExtras();
                                String rssiValue = String.valueOf(b.get("android.bluetooth.device.extra.RSSI"));
                                int value = Integer.parseInt(rssiValue);
                                float power = (float) ((Math.abs(value) - 59) / (10 * 2.0));
                                float distance = (float) Math.pow(10.0, power);
                                listener.onDeviceFound(device.getName(), device.getAddress(), distance);
                            }
                        }
                    }
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    listener.onDiscoveryFinished();
                }
            }
        }, intent);
    }

    // Connect to the Bluetooth device by its address
    public void connectToDevice(final String deviceAddress, final BluetoothConnectionListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    listener.onConnected(deviceAddress);

                } catch (Exception e) {
                    e.printStackTrace();
                    listener.onConnectionFailed(deviceAddress);
                }
            }
        }).start();
    }

    // Get paired devices list
    public List<String> getPairedDevices() {
        List<String> data = new ArrayList<>();
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                data.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            data.add("No paired devices found.");
        }
        return data;
    }

    // Stop device discovery and cleanup resources
    public void stopDiscovery() {
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }

    public interface BluetoothDiscoveryListener {
        void onDeviceFound(String deviceName, String deviceAddress, float distance);

        void onDiscoveryFinished();
    }

    public interface BluetoothConnectionListener {
        void onConnected(String deviceAddress);

        void onConnectionFailed(String deviceAddress);
    }
}
