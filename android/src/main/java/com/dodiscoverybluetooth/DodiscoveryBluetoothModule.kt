package com.dodiscoverybluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.facebook.react.bridge.*
import java.util.ArrayList

class DodiscoveryBluetoothModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val bluetoothManager: BluetoothManager = BluetoothManager(reactContext)

    companion object {
        const val NAME = "DodiscoveryBluetooth"
        private const val REQUEST_ENABLE_BT = 1 // Code to request Bluetooth activation
    }

    override fun getName(): String {
        return NAME
    }

    @ReactMethod
    fun enableBluetooth(promise: Promise) {
        if (bluetoothAdapter == null) {
            promise.reject("Bluetooth not supported", "This device does not support Bluetooth")
            return
        }

        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            currentActivity?.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        } else {
            promise.resolve(true)
        }
    }

    // Convert a list of BluetoothDevice objects to a WritableArray
    private fun convertArrayListToWritableArray(devices: ArrayList<BluetoothDevice>): WritableArray {
        val writableArray = Arguments.createArray()

        for (device in devices) {
            val deviceMap = Arguments.createMap()
            deviceMap.putString("name", device.name)
            deviceMap.putString("address", device.address)

            writableArray.pushMap(deviceMap)
        }

        return writableArray
    }

    @ReactMethod
    fun doDiscovery(promise: Promise) {
        bluetoothManager.doDiscovery(object : BluetoothManager.BluetoothDiscoveryListener {
            override fun onDeviceFound(deviceName: String, deviceAddress: String, distance: Float) {
                // Envoie les informations du périphérique à React Native
                val deviceMap = Arguments.createMap()
                deviceMap.putString("name", deviceName)
                deviceMap.putString("address", deviceAddress)
                deviceMap.putDouble("distance", distance.toDouble())
                
                // Utilisez promise.resolve pour envoyer les données
                promise.resolve(deviceMap)
            }

            override fun onDiscoveryFinished() {
                // La découverte est terminée
                promise.resolve("Discovery finished")
            }
        })
    }

    @ReactMethod
    fun getPairedDevices(promise: Promise) {
        try {
            val pairedDevices = bluetoothAdapter?.bondedDevices ?: emptySet()
            val devicesList = ArrayList(pairedDevices)

            // Convert the list to WritableArray for React Native
            val devicesArray = convertArrayListToWritableArray(devicesList)

            // Send the list to the JavaScript side
            promise.resolve(devicesArray)
        } catch (e: Exception) {
            e.printStackTrace()
            promise.reject("ERROR", "Error getting paired devices: ${e.message}")
        }
    }

    @ReactMethod
    fun cancelDiscovery(promise: Promise) {
        if (bluetoothAdapter == null) {
            promise.reject("Bluetooth not supported", "This device does not support Bluetooth")
            return
        }

        bluetoothAdapter.cancelDiscovery()
        promise.resolve("Discovery canceled")
    }

    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            reactApplicationContext,
            Manifest.permission.BLUETOOTH
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            reactApplicationContext,
            Manifest.permission.BLUETOOTH_ADMIN
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            currentActivity!!,
            arrayOf(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN),
            REQUEST_ENABLE_BT
        )
    }
}
