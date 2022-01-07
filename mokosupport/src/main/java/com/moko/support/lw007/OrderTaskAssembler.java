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

    public static OrderTask getBleEnable() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_BLE_ENABLE);
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

    public static OrderTask getBleTimeoutDuration() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_BLE_TIMEOUT_DURATION);
        return task;
    }

    public static OrderTask getBleLoginMode() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_BLE_LOGIN_MODE);
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

    public static OrderTask getPIREnable() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_PIR_ENABLE);
        return task;
    }

    public static OrderTask getPIRReportInterval() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_PIR_REPORT_INTERVAL);
        return task;
    }

    public static OrderTask getPIRSensitivity() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_PIR_SENSITIVITY);
        return task;
    }

    public static OrderTask getPIRDelayTime() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_PIR_DELAY_TIME);
        return task;
    }

    public static OrderTask getHallStatusEnable() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_HALL_STATUS_ENABLE);
        return task;
    }

    public static OrderTask getTHEnable() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_TH_ENABLE);
        return task;
    }

    public static OrderTask getTHSampleRate() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_TH_SAMPLE_RATE);
        return task;
    }

    public static OrderTask getTempThresholdAlarmEnable() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_TEMP_THRESHOLD_ALARM_ENABLE);
        return task;
    }

    public static OrderTask getTempThresholdAlarm() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_TEMP_THRESHOLD_ALARM);
        return task;
    }

    public static OrderTask getTempChangeAlarmEnable() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_TEMP_CHANGE_ALARM_ENABLE);
        return task;
    }

    public static OrderTask getTempChangeAlarmDuration() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_TEMP_CHANGE_ALARM_DURATION);
        return task;
    }

    public static OrderTask getTempChangeAlarmValue() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_TEMP_CHANGE_ALARM_VALUE);
        return task;
    }

    public static OrderTask getHumidityThresholdAlarmEnable() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_HUMIDITY_THRESHOLD_ALARM_ENABLE);
        return task;
    }

    public static OrderTask getHumidityThresholdAlarm() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_HUMIDITY_THRESHOLD_ALARM);
        return task;
    }

    public static OrderTask getHumidityChangeAlarmEnable() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_HUMIDITY_CHANGE_ALARM_ENABLE);
        return task;
    }

    public static OrderTask getHumidityChangeAlarmDuration() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_HUMIDITY_CHANGE_ALARM_DURATION);
        return task;
    }

    public static OrderTask getHumidityChangeAlarmValue() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_HUMIDITY_CHANGE_ALARM_VALUE);
        return task;
    }

    public static OrderTask getLEDIndicatorStatus() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_LED_INDICATOR_STATUS);
        return task;
    }

    public static OrderTask getHeartbeat() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_HEARTBEAT);
        return task;
    }

    public static OrderTask getLowPowerPrompt() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_LOW_POWER_PROMPT);
        return task;
    }

    public static OrderTask getLowPowerPayload() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_LOW_POWER_PAYLOAD);
        return task;
    }

    public static OrderTask getTimeZone() {
        ParamsReadTask task = new ParamsReadTask();
        task.setData(ParamsKeyEnum.KEY_TIME_ZONE);
        return task;
    }

    public static OrderTask getNetworkStatus() {
        ControlReadTask task = new ControlReadTask();
        task.setData(ControlKeyEnum.KEY_NETWORK_STATUS);
        return task;
    }

    public static OrderTask getBattery() {
        ControlReadTask task = new ControlReadTask();
        task.setData(ControlKeyEnum.KEY_BATTERY);
        return task;
    }

    public static OrderTask getMacAddress() {
        ControlReadTask task = new ControlReadTask();
        task.setData(ControlKeyEnum.KEY_MAC);
        return task;
    }

    public static OrderTask getPIR() {
        ControlReadTask task = new ControlReadTask();
        task.setData(ControlKeyEnum.KEY_PIR);
        return task;
    }

    public static OrderTask getHallStatusSum() {
        ControlReadTask task = new ControlReadTask();
        task.setData(ControlKeyEnum.KEY_HALL_STATUS_SUM);
        return task;
    }

    public static OrderTask getTHData() {
        ControlReadTask task = new ControlReadTask();
        task.setData(ControlKeyEnum.KEY_TH_DATA);
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

    public static OrderTask setBleLoginMode(int loginMode) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setBleLoginMode(loginMode);
        return task;
    }

    public static OrderTask setBleEnable(int enable) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setBleEnable(enable);
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

    public static OrderTask setBleTimeoutDuration(int duration) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setBleTimeoutDuration(duration);
        return task;
    }

    public static OrderTask setPIREnable(int enable) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setPIREnable(enable);
        return task;
    }

    public static OrderTask setPIRReportInterval(int interval) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setPIRReportInterval(interval);
        return task;
    }

    public static OrderTask setPIRSensitivity(int sensitivity) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setPIRSensitivity(sensitivity);
        return task;
    }

    public static OrderTask setPIRDelayTime(int delayTime) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setPIRDelayTime(delayTime);
        return task;
    }

    public static OrderTask setHallStatusEnable(int enable) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setHallStatusEnable(enable);
        return task;
    }

    public static OrderTask setTHEnable(int enable) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setTHEnable(enable);
        return task;
    }

    public static OrderTask setTHSampleRate(int rate) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setTHSampleRate(rate);
        return task;
    }

    public static OrderTask setTempThresholdAlarmEnable(int enable) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setTempThresholdAlarmEnable(enable);
        return task;
    }

    public static OrderTask setTempThresholdAlarm(int min, int max) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setTempThresholdAlarm(min, max);
        return task;
    }

    public static OrderTask setTempChangeAlarmEnable(int enable) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setTempChangeAlarmEnable(enable);
        return task;
    }

    public static OrderTask setTempChangeAlarmDuration(int duration) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setTempChangeAlarmDuration(duration);
        return task;
    }

    public static OrderTask setTempChangeAlarmValue(int value) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setTempChangeAlarmValue(value);
        return task;
    }

    public static OrderTask setHumidityThresholdAlarmEnable(int enable) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setHumidityThresholdAlarmEnable(enable);
        return task;
    }

    public static OrderTask setHumidityThresholdAlarm(int min, int max) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setHumidityThresholdAlarm(min, max);
        return task;
    }

    public static OrderTask setHumidityChangeAlarmEnable(int enable) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setHumidityChangeAlarmEnable(enable);
        return task;
    }

    public static OrderTask setHumidityChangeAlarmDuration(int duration) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setHumidityChangeAlarmDuration(duration);
        return task;
    }

    public static OrderTask setHumidityChangeAlarmValue(int value) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setHumidityChangeAlarmValue(value);
        return task;
    }

    public static OrderTask setHeartbeat(int heartbeat) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setHeartbeat(heartbeat);
        return task;
    }

    public static OrderTask setLowPowerPrompt(int lowPowerPrompt) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setLowPowerPrompt(lowPowerPrompt);
        return task;
    }

    public static OrderTask setLowPowerPayload(int payload) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setLowPowerPayload(payload);
        return task;
    }

    public static OrderTask setLEDIndicatorStatus(int networkIndicator, int powerIndicator) {
        ParamsWriteTask task = new ParamsWriteTask();
        task.setLEDIndicatorStatus(networkIndicator, powerIndicator);
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
