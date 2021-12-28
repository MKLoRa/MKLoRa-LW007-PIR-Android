package com.moko.support.lw007.task;

import com.moko.ble.lib.task.OrderTask;
import com.moko.ble.lib.utils.MokoUtils;
import com.moko.support.lw007.entity.OrderCHAR;
import com.moko.support.lw007.entity.ParamsKeyEnum;

import androidx.annotation.IntRange;

public class ParamsWriteTask extends OrderTask {
    public byte[] data;

    public ParamsWriteTask() {
        super(OrderCHAR.CHAR_PARAMS, OrderTask.RESPONSE_TYPE_WRITE_NO_RESPONSE);
    }

    @Override
    public byte[] assemble() {
        return data;
    }

    public void setLoraRegion(@IntRange(from = 0, to = 9) int region) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_LORA_REGION.getParamsKey(),
                (byte) 0x01,
                (byte) region
        };
    }

    public void setLoraUploadMode(@IntRange(from = 1, to = 2) int mode) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_LORA_MODE.getParamsKey(),
                (byte) 0x01,
                (byte) mode
        };
    }

    public void setLoraClass(@IntRange(from = 0, to = 2) int loraClass) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_LORA_CLASS.getParamsKey(),
                (byte) 0x01,
                (byte) loraClass
        };
    }

    public void setLoraDevEUI(String devEui) {
        byte[] rawDataBytes = MokoUtils.hex2bytes(devEui);
        int length = rawDataBytes.length;
        data = new byte[4 + length];
        data[0] = (byte) 0xED;
        data[1] = (byte) 0x01;
        data[2] = (byte) ParamsKeyEnum.KEY_LORA_DEV_EUI.getParamsKey();
        data[3] = (byte) length;
        for (int i = 0; i < length; i++) {
            data[i + 4] = rawDataBytes[i];
        }
    }

    public void setLoraAppEUI(String appEui) {
        byte[] rawDataBytes = MokoUtils.hex2bytes(appEui);
        int length = rawDataBytes.length;
        data = new byte[4 + length];
        data[0] = (byte) 0xED;
        data[1] = (byte) 0x01;
        data[2] = (byte) ParamsKeyEnum.KEY_LORA_APP_EUI.getParamsKey();
        data[3] = (byte) length;
        for (int i = 0; i < length; i++) {
            data[i + 4] = rawDataBytes[i];
        }
    }

    public void setLoraAppKey(String appKey) {
        byte[] rawDataBytes = MokoUtils.hex2bytes(appKey);
        int length = rawDataBytes.length;
        data = new byte[4 + length];
        data[0] = (byte) 0xED;
        data[1] = (byte) 0x01;
        data[2] = (byte) ParamsKeyEnum.KEY_LORA_APP_KEY.getParamsKey();
        data[3] = (byte) length;
        for (int i = 0; i < length; i++) {
            data[i + 4] = rawDataBytes[i];
        }
    }

    public void setLoraDevAddr(String devAddr) {
        byte[] rawDataBytes = MokoUtils.hex2bytes(devAddr);
        int length = rawDataBytes.length;
        data = new byte[4 + length];
        data[0] = (byte) 0xED;
        data[1] = (byte) 0x01;
        data[2] = (byte) ParamsKeyEnum.KEY_LORA_DEV_ADDR.getParamsKey();
        data[3] = (byte) length;
        for (int i = 0; i < length; i++) {
            data[i + 4] = rawDataBytes[i];
        }
    }

    public void setLoraAppSKey(String appSkey) {
        byte[] rawDataBytes = MokoUtils.hex2bytes(appSkey);
        int length = rawDataBytes.length;
        data = new byte[4 + length];
        data[0] = (byte) 0xED;
        data[1] = (byte) 0x01;
        data[2] = (byte) ParamsKeyEnum.KEY_LORA_APP_SKEY.getParamsKey();
        data[3] = (byte) length;
        for (int i = 0; i < length; i++) {
            data[i + 4] = rawDataBytes[i];
        }
    }

    public void setLoraNwkSKey(String nwkSkey) {
        byte[] rawDataBytes = MokoUtils.hex2bytes(nwkSkey);
        int length = rawDataBytes.length;
        data = new byte[4 + length];
        data[0] = (byte) 0xED;
        data[1] = (byte) 0x01;
        data[2] = (byte) ParamsKeyEnum.KEY_LORA_NWK_SKEY.getParamsKey();
        data[3] = (byte) length;
        for (int i = 0; i < length; i++) {
            data[i + 4] = rawDataBytes[i];
        }
    }

    public void setLoraMessageType(@IntRange(from = 0, to = 1) int type) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_LORA_MESSAGE_TYPE.getParamsKey(),
                (byte) 0x01,
                (byte) type
        };
    }

    public void setMaxRetransmissionTimes(@IntRange(from = 1, to = 8) int times) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_LORA_MAX_RETRANSMISSION_TIMES.getParamsKey(),
                (byte) 0x01,
                (byte) times
        };
    }

    public void setLoraCH(int ch1, int ch2) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_LORA_CH.getParamsKey(),
                (byte) 0x02,
                (byte) ch1,
                (byte) ch2
        };
    }

    public void setLoraDR(int dr1) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_LORA_DR.getParamsKey(),
                (byte) 0x01,
                (byte) dr1
        };
    }

    public void setLoraUplinkStrategy(int adr, int dr1, int dr2) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_LORA_UPLINK_STRATEGY.getParamsKey(),
                (byte) 0x04,
                (byte) adr,
                (byte) 0x01,
                (byte) dr1,
                (byte) dr2
        };
    }


    public void setLoraDutyCycleEnable(@IntRange(from = 0, to = 1) int enable) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_LORA_DUTYCYCLE.getParamsKey(),
                (byte) 0x01,
                (byte) enable
        };
    }

    public void setLoraTimeSyncInterval(@IntRange(from = 0, to = 255) int interval) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_LORA_TIME_SYNC_INTERVAL.getParamsKey(),
                (byte) 0x01,
                (byte) interval
        };
    }

    public void setLoraNetworkInterval(@IntRange(from = 0, to = 255) int interval) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_LORA_NETWORK_CHECK_INTERVAL.getParamsKey(),
                (byte) 0x01,
                (byte) interval
        };
    }

    public void setPowerOnDefaultMode(@IntRange(from = 0, to = 2) int mode) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_POWER_ON_DEFAULT_MODE.getParamsKey(),
                (byte) 0x01,
                (byte) mode
        };
    }

    public void setSwitchPayloadsReportInterval(@IntRange(from = 10, to = 600) int interval) {
        byte[] rawDataBytes = MokoUtils.toByteArray(interval, 2);
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_SWITCH_PAYLOADS_REPORT_INTERVAL.getParamsKey(),
                (byte) 0x02,
                rawDataBytes[0],
                rawDataBytes[1]
        };
    }

    public void setElectricityPayloadsReportInterval(@IntRange(from = 5, to = 600) int interval) {
        byte[] rawDataBytes = MokoUtils.toByteArray(interval, 2);
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_ELECTRICITY_PAYLOADS_REPORT_INTERVAL.getParamsKey(),
                (byte) 0x02,
                rawDataBytes[0],
                rawDataBytes[1]
        };
    }

    public void setEnergyConfigInterval(@IntRange(from = 1, to = 60) int reportInterval, @IntRange(from = 1, to = 60) int saveInterval) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_ENERGY_CONFIG_INTERVAL.getParamsKey(),
                (byte) 0x02,
                (byte) saveInterval,
                (byte) reportInterval
        };
    }

    public void setPowerChangeValue(@IntRange(from = 1, to = 100) int value) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_POWER_CHANGE_VALUE.getParamsKey(),
                (byte) 0x01,
                (byte) value
        };
    }

    public void setOverVoltageProtection(int onOff, int value, int time) {
        byte[] rawDataBytes = MokoUtils.toByteArray(value, 2);
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_OVER_VOLTAGE_PROTECTION.getParamsKey(),
                (byte) 0x04,
                (byte) onOff,
                rawDataBytes[0],
                rawDataBytes[1],
                (byte) time
        };
    }

    public void setSagVoltageProtection(int onOff, int value, int time) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_SAG_VOLTAGE_PROTECTION.getParamsKey(),
                (byte) 0x03,
                (byte) onOff,
                (byte) value,
                (byte) time
        };
    }

    public void setOverCurrentProtection(int onOff, int value, int time) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_OVER_CURRENT_PROTECTION.getParamsKey(),
                (byte) 0x03,
                (byte) onOff,
                (byte) value,
                (byte) time
        };
    }

    public void setOverLoadProtection(int onOff, int value, int time) {
        byte[] rawDataBytes = MokoUtils.toByteArray(value, 2);
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_OVER_LOAD_PROTECTION.getParamsKey(),
                (byte) 0x04,
                (byte) onOff,
                rawDataBytes[0],
                rawDataBytes[1],
                (byte) time
        };
    }

    public void setLoadNotification(int start, int stop) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_LOAD_NOTIFICATION.getParamsKey(),
                (byte) 0x02,
                (byte) start,
                (byte) stop
        };
    }

    public void setLoadStatusThreshold(@IntRange(from = 1, to = 10) int threshold) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_LOAD_STATUS_THRESHOLD.getParamsKey(),
                (byte) 0x01,
                (byte) threshold
        };
    }

    public void setCountdownPayloadsReportInterval(@IntRange(from = 10, to = 60) int interval) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_COUNTDOWN_PAYLOADS_REPORT_INTERVAL.getParamsKey(),
                (byte) 0x01,
                (byte) interval
        };
    }

    public void setLEDIndicatorStatus(int networkIndicator, int powerIndicator) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_LED_INDICATOR_STATUS.getParamsKey(),
                (byte) 0x02,
                (byte) powerIndicator,
                (byte) networkIndicator
        };
    }

    public void setPowerIndicatorColor(int option, int blue, int green, int yellow, int orange, int red, int purple) {
        byte[] blueBytes = MokoUtils.toByteArray(blue, 2);
        byte[] greenBytes = MokoUtils.toByteArray(green, 2);
        byte[] yellowBytes = MokoUtils.toByteArray(yellow, 2);
        byte[] orangeBytes = MokoUtils.toByteArray(orange, 2);
        byte[] redBytes = MokoUtils.toByteArray(red, 2);
        byte[] purpleBytes = MokoUtils.toByteArray(purple, 2);
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_POWER_INDICATOR_COLOR.getParamsKey(),
                (byte) 0x0D,
                (byte) option,
                blueBytes[0],
                blueBytes[1],
                greenBytes[0],
                greenBytes[1],
                yellowBytes[0],
                yellowBytes[1],
                orangeBytes[0],
                orangeBytes[1],
                redBytes[0],
                redBytes[1],
                purpleBytes[0],
                purpleBytes[1]
        };
    }

    public void setBleAdvName(String advName) {
        byte[] advNameBytes = advName.getBytes();
        int length = advNameBytes.length;
        data = new byte[length + 4];
        data[0] = (byte) 0xED;
        data[1] = (byte) 0x01;
        data[2] = (byte) ParamsKeyEnum.KEY_BLE_ADV_NAME.getParamsKey();
        data[3] = (byte) length;
        for (int i = 0; i < advNameBytes.length; i++) {
            data[i + 4] = advNameBytes[i];
        }
    }

    public void setBleAdvInterval(@IntRange(from = 1, to = 100) int advInterval) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_BLE_ADV_INTERVAL.getParamsKey(),
                (byte) 0x01,
                (byte) advInterval
        };
    }

    public void setBleTxPower(int txPower) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_BLE_TX_POWER.getParamsKey(),
                (byte) 0x01,
                (byte) txPower
        };
    }

    public void setBleConnectable(int connectable) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_BLE_CONNECTABLE.getParamsKey(),
                (byte) 0x01,
                (byte) connectable
        };
    }

    public void setBleLoginMode(int loginMode) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_BLE_LOGIN_MODE.getParamsKey(),
                (byte) 0x01,
                (byte) loginMode
        };
    }

    public void setNewPassword(String password) {
        byte[] passwordBytes = password.getBytes();
        int length = passwordBytes.length;
        data = new byte[length + 4];
        data[0] = (byte) 0xED;
        data[1] = (byte) 0x01;
        data[2] = (byte) ParamsKeyEnum.KEY_BLE_CHANGE_PASSWORD.getParamsKey();
        data[3] = (byte) length;
        for (int i = 0; i < passwordBytes.length; i++) {
            data[i + 4] = passwordBytes[i];
        }
    }

    public void setTimezone(@IntRange(from = -24, to = 28) int timezone) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_TIME_ZONE.getParamsKey(),
                (byte) 0x01,
                (byte) timezone
        };
    }
}
