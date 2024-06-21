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

    public void setLoraRegion(@IntRange(from = 0, to = 13) int region) {
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
                (byte) 0x03,
                (byte) adr,
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


    public void setBleEnable(@IntRange(from = 0, to = 1) int enable) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_BLE_ENABLE.getParamsKey(),
                (byte) 0x01,
                (byte) enable
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

    public void setBleTimeoutDuration(@IntRange(from = 1, to = 60) int timeout) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_BLE_TIMEOUT_DURATION.getParamsKey(),
                (byte) 0x01,
                (byte) timeout
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

    public void setBleConnectable(@IntRange(from = 0, to = 1) int connectable) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_BLE_CONNECTABLE.getParamsKey(),
                (byte) 0x01,
                (byte) connectable
        };
    }

    public void setBleLoginMode(@IntRange(from = 0, to = 1) int loginMode) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_BLE_LOGIN_MODE.getParamsKey(),
                (byte) 0x01,
                (byte) loginMode
        };
    }

    public void setPIREnable(@IntRange(from = 0, to = 1) int enable) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_PIR_ENABLE.getParamsKey(),
                (byte) 0x01,
                (byte) enable
        };
    }

    public void setPIRReportInterval(@IntRange(from = 1, to = 60) int interval) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_PIR_REPORT_INTERVAL.getParamsKey(),
                (byte) 0x01,
                (byte) interval
        };
    }

    public void setPIRSensitivity(@IntRange(from = 1, to = 3) int sensitivity) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_PIR_SENSITIVITY.getParamsKey(),
                (byte) 0x01,
                (byte) sensitivity
        };
    }

    public void setPIRDelayTime(@IntRange(from = 1, to = 3) int delayTime) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_PIR_DELAY_TIME.getParamsKey(),
                (byte) 0x01,
                (byte) delayTime
        };
    }

    public void setHallStatusEnable(@IntRange(from = 0, to = 1) int enable) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_HALL_STATUS_ENABLE.getParamsKey(),
                (byte) 0x01,
                (byte) enable
        };
    }

    public void setTHEnable(@IntRange(from = 0, to = 1) int enable) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_TH_ENABLE.getParamsKey(),
                (byte) 0x01,
                (byte) enable
        };
    }

    public void setTHSampleRate(@IntRange(from = 1, to = 60) int rate) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_TH_SAMPLE_RATE.getParamsKey(),
                (byte) 0x01,
                (byte) rate
        };
    }

    public void setTempThresholdAlarmEnable(@IntRange(from = 0, to = 1) int enable) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_TEMP_THRESHOLD_ALARM_ENABLE.getParamsKey(),
                (byte) 0x01,
                (byte) enable
        };
    }

    public void setTempThresholdAlarm(@IntRange(from = -30, to = 60) int min,
                                      @IntRange(from = -30, to = 60) int max) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_TEMP_THRESHOLD_ALARM.getParamsKey(),
                (byte) 0x02,
                (byte) min,
                (byte) max
        };
    }

    public void setTempChangeAlarmEnable(@IntRange(from = 0, to = 1) int enable) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_TEMP_CHANGE_ALARM_ENABLE.getParamsKey(),
                (byte) 0x01,
                (byte) enable
        };
    }

    public void setTempChangeAlarmDuration(@IntRange(from = 1, to = 24) int duration) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_TEMP_CHANGE_ALARM_DURATION.getParamsKey(),
                (byte) 0x01,
                (byte) duration
        };
    }

    public void setTempChangeAlarmValue(@IntRange(from = 1, to = 20) int value) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_TEMP_CHANGE_ALARM_VALUE.getParamsKey(),
                (byte) 0x01,
                (byte) value
        };
    }

    public void setHumidityThresholdAlarmEnable(@IntRange(from = 0, to = 1) int enable) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_HUMIDITY_THRESHOLD_ALARM_ENABLE.getParamsKey(),
                (byte) 0x01,
                (byte) enable
        };
    }

    public void setHumidityThresholdAlarm(@IntRange(from = 0, to = 100) int min,
                                          @IntRange(from = 0, to = 100) int max) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_HUMIDITY_THRESHOLD_ALARM.getParamsKey(),
                (byte) 0x02,
                (byte) min,
                (byte) max
        };
    }

    public void setHumidityChangeAlarmEnable(@IntRange(from = 0, to = 1) int enable) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_HUMIDITY_CHANGE_ALARM_ENABLE.getParamsKey(),
                (byte) 0x01,
                (byte) enable
        };
    }

    public void setHumidityChangeAlarmDuration(@IntRange(from = 1, to = 24) int duration) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_HUMIDITY_CHANGE_ALARM_DURATION.getParamsKey(),
                (byte) 0x01,
                (byte) duration
        };
    }

    public void setHumidityChangeAlarmValue(@IntRange(from = 1, to = 100) int value) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_HUMIDITY_CHANGE_ALARM_VALUE.getParamsKey(),
                (byte) 0x01,
                (byte) value
        };
    }

    public void setNewPassword(String password) {
        byte[] passwordBytes = password.getBytes();
        int length = passwordBytes.length;
        data = new byte[length + 4];
        data[0] = (byte) 0xED;
        data[1] = (byte) 0x01;
        data[2] = (byte) ParamsKeyEnum.KEY_CHANGE_PASSWORD.getParamsKey();
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


    public void setHeartbeat(@IntRange(from = 1, to = 14400) int heartbeat) {
        byte[] heartbeatBytes = MokoUtils.toByteArray(heartbeat, 2);
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_HEARTBEAT.getParamsKey(),
                (byte) 0x02,
                heartbeatBytes[0],
                heartbeatBytes[1],
        };
    }

//    public void setLowPowerPrompt(@IntRange(from = 0, to = 1) int lowPowerPrompt) {
//        data = new byte[]{
//                (byte) 0xED,
//                (byte) 0x01,
//                (byte) ParamsKeyEnum.KEY_LOW_POWER_PROMPT.getParamsKey(),
//                (byte) 0x01,
//                (byte) lowPowerPrompt
//        };
//    }

    public void setLowPowerPayload(@IntRange(from = 0, to = 1) int lowPowerPayload) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) ParamsKeyEnum.KEY_LOW_POWER_PAYLOAD.getParamsKey(),
                (byte) 0x01,
                (byte) lowPowerPayload
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
}
