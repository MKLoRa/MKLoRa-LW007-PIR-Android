package com.moko.support.lw007;

import com.moko.ble.lib.task.OrderTask;
import com.moko.support.lw007.entity.ControlKeyEnum;
import com.moko.support.lw007.entity.ParamsKeyEnum;
import com.moko.support.lw007.task.ControlReadTask;
import com.moko.support.lw007.task.ControlWriteTask;
import com.moko.support.lw007.task.GetFirmwareRevisionTask;
import com.moko.support.lw007.task.GetHardwareRevisionTask;
import com.moko.support.lw007.task.GetManufacturerNameTask;
import com.moko.support.lw007.task.GetModelNumberTask;
import com.moko.support.lw007.task.GetSoftwareRevisionTask;
import com.moko.support.lw007.task.ParamsReadTask;
import com.moko.support.lw007.task.ParamsWriteTask;
import com.moko.support.lw007.task.SetPasswordTask;

import androidx.annotation.IntRange;

public class OrderTaskAssembler {
    ///////////////////////////////////////////////////////////////////////////
    // READ
    ///////////////////////////////////////////////////////////////////////////

    public static OrderTask getManufacturer() {
        GetManufacturerNameTask getManufacturerTask = new GetManufacturerNameTask();
        return getManufacturerTask;
    }

    public static OrderTask getDeviceModel() {
        GetModelNumberTask getDeviceModelTask = new GetModelNumberTask();
        return getDeviceModelTask;
    }

    public static OrderTask getHardwareVersion() {
        GetHardwareRevisionTask getHardwareVersionTask = new GetHardwareRevisionTask();
        return getHardwareVersionTask;
    }

    public static OrderTask getFirmwareVersion() {
        GetFirmwareRevisionTask getFirmwareVersionTask = new GetFirmwareRevisionTask();
        return getFirmwareVersionTask;
    }

    public static OrderTask getSoftwareVersion() {
        GetSoftwareRevisionTask getSoftwareVersionTask = new GetSoftwareRevisionTask();
        return getSoftwareVersionTask;
    }

    public static OrderTask getLoraRegion() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_LORA_REGION);
        return task;
    }

    public static OrderTask getLoraClass() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_LORA_CLASS);
        return task;
    }

    public static OrderTask getLoraUploadMode() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_LORA_MODE);
        return task;
    }

    public static OrderTask getLoraNetworkStatus() {
        ControlReadTask task = new ControlReadTask();
        task.setData(ControlKeyEnum.KEY_NETWORK_STATUS);
        return task;
    }

    public static OrderTask getLoraDevEUI() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_LORA_DEV_EUI);
        return task;
    }

    public static OrderTask getLoraAppEUI() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_LORA_APP_EUI);
        return task;
    }

    public static OrderTask getLoraAppKey() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_LORA_APP_KEY);
        return task;
    }

    public static OrderTask getLoraDevAddr() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_LORA_DEV_ADDR);
        return task;
    }

    public static OrderTask getLoraAppSKey() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_LORA_APP_SKEY);
        return task;
    }

    public static OrderTask getLoraNwkSKey() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_LORA_NWK_SKEY);
        return task;
    }

    public static OrderTask getLoraMessageType() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_LORA_MESSAGE_TYPE);
        return task;
    }

    public static OrderTask getLoraCH() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_LORA_CH);
        return task;
    }

    public static OrderTask getLoraDR() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_LORA_DR);
        return task;
    }

    public static OrderTask getLoraUplinkStrategy() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_LORA_UPLINK_STRATEGY);
        return task;
    }

    public static OrderTask getLoraMaxRetransmissionTimes() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_LORA_MAX_RETRANSMISSION_TIMES);
        return task;
    }


    public static OrderTask getLoraDutyCycleEnable() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_LORA_DUTYCYCLE);
        return task;
    }

    public static OrderTask getLoraTimeSyncInterval() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_LORA_TIME_SYNC_INTERVAL);
        return task;
    }

    public static OrderTask getLoraNetworkCheckInterval() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_LORA_NETWORK_CHECK_INTERVAL);
        return task;
    }

    public static OrderTask getSwitchStatus() {
        ControlReadTask task = new ControlReadTask();
        task.setData(ControlKeyEnum.KEY_SWITCH_STATUS);
        return task;
    }

    public static OrderTask getPowerOnDefaultMode() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_POWER_ON_DEFAULT_MODE);
        return task;
    }

    public static OrderTask getSwitchPayloadsReportInterval() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_SWITCH_PAYLOADS_REPORT_INTERVAL);
        return task;
    }

    public static OrderTask getElectricityPayloadsReportInterval() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_ELECTRICITY_PAYLOADS_REPORT_INTERVAL);
        return task;
    }

    public static OrderTask getEnergyConfigInterval() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_ENERGY_CONFIG_INTERVAL);
        return task;
    }

    public static OrderTask getPowerChangeValue() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_POWER_CHANGE_VALUE);
        return task;
    }

    public static OrderTask getTotalEnergy() {
        ControlReadTask task = new ControlReadTask();
        task.setData(ControlKeyEnum.KEY_TOTAL_ENERGY);
        return task;
    }

    public static OrderTask getDeviceSpecification() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_DEVICE_SPECIFICATION);
        return task;
    }

    public static OrderTask getOverVoltageProtection() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_OVER_VOLTAGE_PROTECTION);
        return task;
    }

    public static OrderTask getSagVoltageProtection() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_SAG_VOLTAGE_PROTECTION);
        return task;
    }

    public static OrderTask getOverCurrentProtection() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_OVER_CURRENT_PROTECTION);
        return task;
    }

    public static OrderTask getOverLoadProtection() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_OVER_LOAD_PROTECTION);
        return task;
    }

    public static OrderTask getLoadStatus() {
        ControlReadTask task = new ControlReadTask();
        task.setData(ControlKeyEnum.KEY_LOAD_STATUS);
        return task;
    }

    public static OrderTask getLoadNotification() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_LOAD_NOTIFICATION);
        return task;
    }

    public static OrderTask getLoadStatusThreshold() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_LOAD_STATUS_THRESHOLD);
        return task;
    }

    public static OrderTask getCountdownPayloadsReportInterval() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_COUNTDOWN_PAYLOADS_REPORT_INTERVAL);
        return task;
    }

    public static OrderTask getLEDIndicatorStatus() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_LED_INDICATOR_STATUS);
        return task;
    }

    public static OrderTask getPowerIndicatorColor() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_POWER_INDICATOR_COLOR);
        return task;
    }

    public static OrderTask getBleAdvName() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_BLE_ADV_NAME);
        return task;
    }

    public static OrderTask getBleAdvInterval() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_BLE_ADV_INTERVAL);
        return task;
    }

    public static OrderTask getBleTxPower() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_BLE_TX_POWER);
        return task;
    }

    public static OrderTask getBleConnectable() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_BLE_CONNECTABLE);
        return task;
    }

    public static OrderTask getBleLoginMode() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_BLE_LOGIN_MODE);
        return task;
    }


    public static OrderTask getTimeZone() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_TIME_ZONE);
        return task;
    }

    public static OrderTask getMacAddress() {
        ControlReadTask task = new ControlReadTask();
        task.setData(ControlKeyEnum.KEY_MAC);
        return task;
    }


    ///////////////////////////////////////////////////////////////////////////
    // WIRTE
    ///////////////////////////////////////////////////////////////////////////
    public static OrderTask setPassword(String password) {
        SetPasswordTask task = new SetPasswordTask();
        task.setData(password);
        return task;
    }

    public static OrderTask restart() {
        ControlWriteTask task = new ControlWriteTask();
        task.restart();
        return task;
    }

    public static OrderTask restore() {
        ControlWriteTask task = new ControlWriteTask();
        task.restore();
        return task;
    }

    public static OrderTask setTime() {
        ControlWriteTask task = new ControlWriteTask();
        task.setTime();
        return task;
    }

    public static OrderTask setLoraRegion(@IntRange(from = 0, to = 9) int region) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setLoraRegion(region);
        return task;
    }

    public static OrderTask setLoraUploadMode(@IntRange(from = 1, to = 2) int mode) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setLoraUploadMode(mode);
        return task;
    }

    public static OrderTask setLoraClass(@IntRange(from = 0, to = 2) int loraClass) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setLoraClass(loraClass);
        return task;
    }

    public static OrderTask setLoraDevEUI(String devEUI) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setLoraDevEUI(devEUI);
        return task;
    }

    public static OrderTask setLoraAppEUI(String appEUI) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setLoraAppEUI(appEUI);
        return task;
    }

    public static OrderTask setLoraAppKey(String appKey) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setLoraAppKey(appKey);
        return task;
    }

    public static OrderTask setLoraDevAddr(String devAddr) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setLoraDevAddr(devAddr);
        return task;
    }

    public static OrderTask setLoraAppSKey(String appSkey) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setLoraAppSKey(appSkey);
        return task;
    }

    public static OrderTask setLoraNwkSKey(String nwkSkey) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setLoraNwkSKey(nwkSkey);
        return task;
    }

    public static OrderTask setLoraMessageType(@IntRange(from = 0, to = 1) int type) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setLoraMessageType(type);
        return task;
    }

    public static OrderTask setMaxRetransmissionTimes(@IntRange(from = 1, to = 8) int times) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setMaxRetransmissionTimes(times);
        return task;
    }

    public static OrderTask setLoraCH(int ch1, int ch2) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setLoraCH(ch1, ch2);
        return task;
    }

    public static OrderTask setLoraDR(int dr1) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setLoraDR(dr1);
        return task;
    }

    public static OrderTask setLoraUplinkStrategy(int adr, int dr1, int dr2) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setLoraUplinkStrategy(adr, dr1, dr2);
        return task;
    }


    public static OrderTask setLoraDutyCycleEnable(@IntRange(from = 0, to = 1) int enable) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setLoraDutyCycleEnable(enable);
        return task;
    }

    public static OrderTask setLoraTimeSyncInterval(@IntRange(from = 0, to = 255) int interval) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setLoraTimeSyncInterval(interval);
        return task;
    }

    public static OrderTask setLoraNetworkInterval(@IntRange(from = 0, to = 255) int interval) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setLoraNetworkInterval(interval);
        return task;
    }

    public static OrderTask setSwitchStatus(int status) {
        ControlWriteTask task = new ControlWriteTask();
        task.setSwitchStatus(status);
        return task;
    }

    public static OrderTask setPowerOnDefaultMode(int mode) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setPowerOnDefaultMode(mode);
        return task;
    }

    public static OrderTask setSwitchPayloadsReportInterval(int interval) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setSwitchPayloadsReportInterval(interval);
        return task;
    }

    public static OrderTask setElectricityPayloadsReportInterval(int interval) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setElectricityPayloadsReportInterval(interval);
        return task;
    }


    public static OrderTask setEnergyConfigInterval(int reportInterval, int saveInterval) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setEnergyConfigInterval(reportInterval, saveInterval);
        return task;
    }

    public static OrderTask setPowerChangeValue(int value) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setPowerChangeValue(value);
        return task;
    }

    public static OrderTask setOverVoltageProtection(int onOff, int value, int time) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setOverVoltageProtection(onOff, value, time);
        return task;
    }

    public static OrderTask setSagVoltageProtection(int onOff, int value, int time) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setSagVoltageProtection(onOff, value, time);
        return task;
    }

    public static OrderTask setOverCurrentProtection(int onOff, int value, int time) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setOverCurrentProtection(onOff, value, time);
        return task;
    }

    public static OrderTask setOverLoadProtection(int onOff, int value, int time) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setOverLoadProtection(onOff, value, time);
        return task;
    }

    public static OrderTask setLoadNotification(int start, int stop) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setLoadNotification(start, stop);
        return task;
    }

    public static OrderTask setLoadStatusThreshold(int threshold) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setLoadStatusThreshold(threshold);
        return task;
    }

    public static OrderTask setCountdownPayloadsReportInterval(int interval) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setCountdownPayloadsReportInterval(interval);
        return task;
    }

    public static OrderTask setLEDIndicatorStatus(int networkIndicator, int powerIndicator) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setLEDIndicatorStatus(networkIndicator, powerIndicator);
        return task;
    }

    public static OrderTask setPowerIndicatorColor(int option, int blue, int green, int yellow, int orange, int red, int purple) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setPowerIndicatorColor(option, blue, green, yellow, orange, red, purple);
        return task;
    }

    public static OrderTask setBleAdvName(String advName) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setBleAdvName(advName);
        return task;
    }

    public static OrderTask setBleAdvInterval(int advInterval) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setBleAdvInterval(advInterval);
        return task;
    }

    public static OrderTask setBleTxPower(int txPower) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setBleTxPower(txPower);
        return task;
    }

    public static OrderTask setBleConnectable(int connectable) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setBleConnectable(connectable);
        return task;
    }

    public static OrderTask setBleLoginMode(int loginMode) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setBleLoginMode(loginMode);
        return task;
    }

    public static OrderTask setNewPassword(String password) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setNewPassword(password);
        return task;
    }

    public static OrderTask setTimezone(@IntRange(from = -24, to = 28) int timezone) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setTimezone(timezone);
        return task;
    }
}
