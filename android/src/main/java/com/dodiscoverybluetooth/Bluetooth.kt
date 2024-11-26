package com.dodiscoverybluetooth
import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class Bluetooth private constructor(private val context: Context) {

    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mTodata: ToData? = null

    companion object {
        private var bluetooth: Bluetooth? = null
        private const val REQUEST_BLUETOOTH_PERMISSIONS = 1001

        fun getBluetooth(context: Context): Bluetooth {
            // Initialize Bluetooth instance if null
            if (bluetooth == null) {
                bluetooth = Bluetooth(context)
            }
            return bluetooth!!
        }
    }

    private fun registerBroadcast() {
        val intentFilter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        }
        context.registerReceiver(mReceiver, intentFilter)
    }

    fun doDiscovery() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBluetoothAdapter == null) {
          
            return
        } else if (!mBluetoothAdapter!!.isEnabled) {
            mBluetoothAdapter!!.enable()
        }
        registerBroadcast()

        // Vérifier et demander les permissions Bluetooth
        if (hasBluetoothPermissions()) {
            startDiscovery()
        } else {
            requestBluetoothPermissions()
        }
    }

    private fun hasBluetoothPermissions(): Boolean {
        val bluetoothAdminPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH_ADMIN
        ) == PackageManager.PERMISSION_GRANTED

        val bluetoothPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH
        ) == PackageManager.PERMISSION_GRANTED

        val locationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return bluetoothAdminPermission && bluetoothPermission && locationPermission
    }

    private fun requestBluetoothPermissions() {
        if (context is AppCompatActivity) {
            ActivityCompat.requestPermissions(
                context,
                arrayOf(
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                REQUEST_BLUETOOTH_PERMISSIONS
            )
        }
    }

    private fun startDiscovery() {
        if (!mBluetoothAdapter!!.isEnabled) {
            mBluetoothAdapter!!.enable()
        }
        if (mBluetoothAdapter!!.isDiscovering) {
            mBluetoothAdapter!!.cancelDiscovery()
        }
        mBluetoothAdapter!!.startDiscovery()
    }

    fun getData(toData: ToData) {
        mTodata = toData
    }

    interface ToData {
        fun succeed(BTname: String, BTmac: String)
    }

    fun disReceiver() {
        mReceiver?.let {
            context.unregisterReceiver(it)
        }
        if (mBluetoothAdapter?.isDiscovering == true) {
            mBluetoothAdapter?.cancelDiscovery()
        }
    }

    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            val device: BluetoothDevice? = intent?.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
            when (action) {
                BluetoothDevice.ACTION_FOUND -> {
                    if (device?.bluetoothClass?.majorDeviceClass == 1536) {
                        mTodata?.succeed(
                            if (TextUtils.isEmpty(device.name)) "UnKnown" else device.name!!,
                            device.address
                        )
                    }
                }
                BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                    when (device?.bondState) {
                        BluetoothDevice.BOND_BONDING -> Log.d("Print", "正在配对......")
                        BluetoothDevice.BOND_BONDED -> Log.d("Print", "完成配对")
                        BluetoothDevice.BOND_NONE -> Log.d("Print", "取消配对")
                        else -> {}
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    Log.d("Print", "搜索完成")
                }
            }
        }
    }
}
