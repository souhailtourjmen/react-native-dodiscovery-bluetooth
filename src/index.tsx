import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-dodiscovery-bluetooth' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const DodiscoveryBluetooth = NativeModules.DodiscoveryBluetooth
  ? NativeModules.DodiscoveryBluetooth
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

// Enable Bluetooth
export const enableBluetooth = async () => {
  try {
    const response = await DodiscoveryBluetooth.enableBluetooth();
    console.log('Bluetooth enabled:', response);
    return response; // Return the response if needed
  } catch (error) {
    console.error('Error enabling Bluetooth:', error);
    throw error; // Throw the error to handle it outside if necessary
  }
};
export const scanBluetoothDevices = async () => {
  try {
    const devices = await DodiscoveryBluetooth.scanBluetoothDevices();
    return devices;
  } catch (error) {
    console.error(error);
    throw error;
  }
};

export const connectToDevice = async (macAddress: string) => {
  try {
    const response = await DodiscoveryBluetooth.connectToDevice(macAddress);
    return response;
  } catch (error) {
    console.error(error);
    throw error;
  }
};
// Start Bluetooth Discovery
export const startDiscovery = async () => {
  try {
    const devices = await DodiscoveryBluetooth.doDiscovery();
    return devices; // Return the devices list
  } catch (error) {
    console.error('Error discovering devices:', error);
    throw error;
  }
};

// Get paired devices
export const getPairedDevices = async () => {
  try {
    const devices = await DodiscoveryBluetooth.getPairedDevices();
    console.log('Paired devices:', devices);
    return devices; // Return the paired devices list
  } catch (error) {
    console.error('Error getting paired devices:', error);
    throw error;
  }
};

// Cancel Discovery
export const cancelDiscovery = async () => {
  try {
    const message = await DodiscoveryBluetooth.cancelDiscovery();
    console.log('Discovery canceled:', message);
    return message; // Return the cancellation message
  } catch (error) {
    console.error('Error canceling discovery:', error);
    throw error;
  }
};
