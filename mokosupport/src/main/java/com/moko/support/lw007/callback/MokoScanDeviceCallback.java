package com.moko.support.lw007.callback;

import com.moko.support.lw007.entity.DeviceInfo;

public interface MokoScanDeviceCallback {
    void onStartScan();

    void onScanDevice(DeviceInfo device);

    void onStopScan();
}
