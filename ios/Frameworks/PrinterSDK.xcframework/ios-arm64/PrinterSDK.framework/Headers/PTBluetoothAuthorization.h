//
//  PTBluetoothAuthorization.h
//  PrinterSDK
//
//  Created by ios on 2022/5/23.
//  Copyright © 2022 Mellow. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

/**
 *  \~chinese
 *
 *  蓝牙状态
 *
 *  \~english
 *
 *  Bluetooth status
 *
 */
typedef NS_ENUM(NSInteger, PLBluetoothState) {
    /// unauthorized
    PLBluetoothStateUnauthorized  = 0,
    /// Unsupported
    PLBluetoothStateUnsupported  = 1,
    /// Off
    PLBluetoothStatePoweredOff  = 2,
    /// On
    PLBluetoothStatePoweredOn  = 3,
};

@interface PTBluetoothAuthorization : NSObject

+ (PTBluetoothAuthorization *)shared;

/**
 *  \~chinese
 *
 *  获取蓝牙状态
 *
 *  \~english
 *
 *  get Bluetooth State
 *
 */
- (void)getBluetoothState:(void(^)(PLBluetoothState state))stateBlock;

@end

NS_ASSUME_NONNULL_END
