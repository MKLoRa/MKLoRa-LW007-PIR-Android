package com.moko.lw007.utils;

import android.os.ParcelUuid;
import android.os.SystemClock;
import android.util.SparseArray;

import com.moko.ble.lib.utils.MokoUtils;
import com.moko.lw007.entity.AdvInfo;
import com.moko.support.lw007.entity.DeviceInfo;
import com.moko.support.lw007.service.DeviceInfoParseable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import no.nordicsemi.android.support.v18.scanner.ScanRecord;
import no.nordicsemi.android.support.v18.scanner.ScanResult;

public class BeaconInfoParseableImpl implements DeviceInfoParseable<AdvInfo> {
    private HashMap<String, AdvInfo> advInfoHashMap;

    public BeaconInfoParseableImpl() {
        this.advInfoHashMap = new HashMap<>();
    }

    @Override
    public AdvInfo parseDeviceInfo(DeviceInfo deviceInfo) {
        ScanResult result = deviceInfo.scanResult;
        ScanRecord record = result.getScanRecord();
        Map<ParcelUuid, byte[]> map = record.getServiceData();
        if (map == null || map.isEmpty())
            return null;
        SparseArray<byte[]> manufacturer = result.getScanRecord().getManufacturerSpecificData();
        if (manufacturer == null || manufacturer.size() == 0)
            return null;
        byte[] manufacturerSpecificDataByte = record.getManufacturerSpecificData(manufacturer.keyAt(0));
        if (manufacturerSpecificDataByte.length != 23)
            return null;
        int deviceType = -1;
        int voltage = MokoUtils.toInt(Arrays.copyOfRange(manufacturerSpecificDataByte, 6, 8));
        int current = MokoUtils.toIntSigned(Arrays.copyOfRange(manufacturerSpecificDataByte, 8, 10));
        int power = MokoUtils.toIntSigned(Arrays.copyOfRange(manufacturerSpecificDataByte, 10, 14));
        int powerFactor = manufacturerSpecificDataByte[14] & 0xFF;
        int currentRate = MokoUtils.toInt(Arrays.copyOfRange(manufacturerSpecificDataByte, 15, 17));
        int loadState = manufacturerSpecificDataByte[21] & 0xFF;
        int txPower = manufacturerSpecificDataByte[22];
        Iterator iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            ParcelUuid parcelUuid = (ParcelUuid) iterator.next();
            if (parcelUuid.toString().startsWith("0000aa04")) {
                byte[] bytes = map.get(parcelUuid);
                if (bytes != null) {
                    deviceType = bytes[0] & 0xFF;
                }
            }
        }
        if (deviceType == -1)
            return null;
        AdvInfo advInfo;
        if (advInfoHashMap.containsKey(deviceInfo.mac)) {
            advInfo = advInfoHashMap.get(deviceInfo.mac);
            advInfo.name = deviceInfo.name;
            advInfo.rssi = deviceInfo.rssi;
            advInfo.deviceType = deviceType;
            long currentTime = SystemClock.elapsedRealtime();
            long intervalTime = currentTime - advInfo.scanTime;
            advInfo.intervalTime = intervalTime;
            advInfo.scanTime = currentTime;
            advInfo.txPower = txPower;
            advInfo.connectable = result.isConnectable();
            advInfo.voltage = voltage;
            advInfo.current = current;
            advInfo.power = power;
            advInfo.powerFactor = powerFactor;
            advInfo.currentRate = currentRate;
            advInfo.needPassword = (loadState & 0x02) == 2;
            advInfo.loadState = loadState;
        } else {
            advInfo = new AdvInfo();
            advInfo.name = deviceInfo.name;
            advInfo.mac = deviceInfo.mac;
            advInfo.rssi = deviceInfo.rssi;
            advInfo.deviceType = deviceType;
            advInfo.scanTime = SystemClock.elapsedRealtime();
            advInfo.txPower = txPower;
            advInfo.connectable = result.isConnectable();
            advInfo.voltage = voltage;
            advInfo.current = current;
            advInfo.power = power;
            advInfo.powerFactor = powerFactor;
            advInfo.currentRate = currentRate;
            advInfo.needPassword = (loadState & 0x02) == 2;
            advInfo.loadState = loadState;
            advInfoHashMap.put(deviceInfo.mac, advInfo);
        }

        return advInfo;
    }
}
