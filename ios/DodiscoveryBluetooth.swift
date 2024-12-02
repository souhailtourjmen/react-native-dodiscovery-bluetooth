import Foundation
import PrinterSDK

@objc(DodiscoveryBluetooth)
class DodiscoveryBluetooth: NSObject {
    private var dataSources = [PTPrinter]()
    
    @objc(scanBluetoothDevices:withRejecter:)
    func scanBluetoothDevices(_ resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) {
        PTDispatcher.share()?.stopScanBluetooth()
        dataSources.removeAll()
        
        PTDispatcher.share()?.scanBluetooth()
        PTDispatcher.share()?.whenFindAllBluetooth { [weak self] devices in
            guard let self = self else { return }
            guard let printers = devices as? [PTPrinter] else {
                reject("NO_DEVICES", "No devices found", nil)
                return
            }
            self.dataSources = printers.sorted(by: { $0.distance.floatValue < $1.distance.floatValue })
            let devicesList = self.dataSources.map { printer in
                return [
                    "name": printer.name ?? "Unknown",
                    "mac": printer.mac ?? "Unknown"
                ]
            }
            resolve(devicesList)
        }
    }
    
    @objc(connectToDevice:withResolver:withRejecter:)
    func connectToDevice(_ macAddress: String, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) {
        guard let device = dataSources.first(where: { $0.mac == macAddress }) else {
            reject("DEVICE_NOT_FOUND", "No device with given MAC address", nil)
            return
        }
        
        PTDispatcher.share()?.connect(device)
        
        PTDispatcher.share()?.whenConnectSuccess {
            resolve("Connected successfully")
        }
        
        PTDispatcher.share()?.whenConnectFailureWithErrorBlock { error in
            let errorMessage = self.getErrorMessage(from: error)
            reject("CONNECTION_FAILED", errorMessage, nil)
        }
    }
    
    private func getErrorMessage(from error: PTConnectError?) -> String {
        guard let error = error else { return "Unknown error" }
            return "Connection timeout"
        // switch error {
        // case .bleTimeout:
        // case .bleValidateTimeout:
        //     return "Verification timeout"
        // case .bleUnknownDevice:
        //     return "Unknown device"
        // case .bleSystem:
        //     return "System error"
        // case .bleValidateFail:
        //     return "Verification failed"
        // @unknown default:
        //     return "An unknown error occurred"
        // }
    }
}
