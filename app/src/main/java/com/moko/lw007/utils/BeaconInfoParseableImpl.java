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
        if (manufacturerSpecificDataByte.length != 14)
            return null;
        int deviceType = -1;
        int battery = ((manufacturerSpecificDataByte[0] & 0x40) == 0x40) ? 1 : 0;
        int tempInt = MokoUtils.toInt(Arrays.copyOfRange(manufacturerSpecificDataByte, 1, 3));
        int humidityInt = MokoUtils.toInt(Arrays.copyOfRange(manufacturerSpecificDataByte, 3, 5));
        float temp = 0f;
        float humidity = 0f;
        boolean isShowTH = false;
        if (tempInt != 65535 && humidityInt != 65535) {
            isShowTH = true;
            temp = tempInt * 0.1f - 30;
            humidity = humidityInt * 0.1f;
        }
        float voltage = 2.2f + (manufacturerSpecificDataByte[5] & 0xFF) * 0.1f;
        int txPower = manufacturerSpecificDataByte[6];
        int needPassword = manufacturerSpecificDataByte[13];
        Iterator iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            ParcelUuid parcelUuid = (ParcelUuid) iterator.next();
            if (parcelUuid.toString().startsWith("0000aa05")) {
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
            advInfo.battery = battery;
            advInfo.txPower = txPower;
            advInfo.connectable = result.isConnectable();
            advInfo.temp = temp;
            advInfo.humidity = humidity;
            advInfo.voltage = voltage;
            advInfo.isShowTH = isShowTH;
            advInfo.needPassword = needPassword == 1;
        } else {
            advInfo = new AdvInfo();
            advInfo.name = deviceInfo.name;
            advInfo.mac = deviceInfo.mac;
            advInfo.rssi = deviceInfo.rssi;
            advInfo.deviceType = deviceType;
            advInfo.scanTime = SystemClock.elapsedRealtime();
            advInfo.battery = battery;
            advInfo.txPower = txPower;
            advInfo.connectable = result.isConnectable();
            advInfo.temp = temp;
            advInfo.humidity = humidity;
            advInfo.voltage = voltage;
            advInfo.isShowTH = isShowTH;
            advInfo.needPassword = needPassword == 1;
            advInfoHashMap.put(deviceInfo.mac, advInfo);
        }

        return advInfo;
    }
}
