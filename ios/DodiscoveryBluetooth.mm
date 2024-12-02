#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(DodiscoveryBluetooth, NSObject)



RCT_EXTERN_METHOD(scanBluetoothDevices:(RCTPromiseResolveBlock)resolve 
                  withRejecter:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(connectToDevice:(NSString *)macAddress 
                  withResolver:(RCTPromiseResolveBlock)resolve 
                  withRejecter:(RCTPromiseRejectBlock)reject)

+ (BOOL)requiresMainQueueSetup
{
  return NO;
}

@end
