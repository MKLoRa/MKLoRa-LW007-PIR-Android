package com.moko.support.lw007.service;

import com.moko.support.lw007.entity.DeviceInfo;

public interface DeviceInfoParseable<T> {
    T parseDeviceInfo(DeviceInfo deviceInfo);
}
