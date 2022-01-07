package com.moko.support.lw007.task;

import com.moko.ble.lib.task.OrderTask;
import com.moko.support.lw007.entity.OrderCHAR;
import com.moko.support.lw007.entity.ParamsKeyEnum;

public class ParamsReadTask extends OrderTask {
    public byte[] data;

    public ParamsReadTask() {
        super(OrderCHAR.CHAR_PARAMS, OrderTask.RESPONSE_TYPE_WRITE_NO_RESPONSE);
    }

    @Override
    public byte[] assemble() {
        return data;
    }

    public void setData(ParamsKeyEnum key) {
        switch (key) {
            case KEY_LORA_REGION:
            case KEY_LORA_MODE:
            case KEY_LORA_DEV_EUI:
            case KEY_LORA_APP_EUI:
            case KEY_LORA_APP_KEY:
            case KEY_LORA_DEV_ADDR:
            case KEY_LORA_APP_SKEY:
            case KEY_LORA_NWK_SKEY:
            case KEY_LORA_MESSAGE_TYPE:
            case KEY_LORA_MAX_RETRANSMISSION_TIMES:
            case KEY_LORA_CH:
            case KEY_LORA_DR:
            case KEY_LORA_UPLINK_STRATEGY:
            case KEY_LORA_DUTYCYCLE:
            case KEY_LORA_TIME_SYNC_INTERVAL:
            case KEY_LORA_NETWORK_CHECK_INTERVAL:

            case KEY_BLE_ENABLE:
            case KEY_BLE_ADV_INTERVAL:
            case KEY_BLE_CONNECTABLE:
            case KEY_BLE_TIMEOUT_DURATION:
            case KEY_BLE_LOGIN_MODE:
            case KEY_BLE_TX_POWER:
            case KEY_BLE_ADV_NAME:

            case KEY_PIR_ENABLE:
            case KEY_PIR_REPORT_INTERVAL:
            case KEY_PIR_SENSITIVITY:
            case KEY_PIR_DELAY_TIME:

            case KEY_HALL_STATUS_ENABLE:

            case KEY_TH_ENABLE:
            case KEY_TH_SAMPLE_RATE:
            case KEY_TEMP_THRESHOLD_ALARM_ENABLE:
            case KEY_TEMP_THRESHOLD_ALARM:
            case KEY_TEMP_CHANGE_ALARM_ENABLE:
            case KEY_TEMP_CHANGE_ALARM_DURATION:
            case KEY_TEMP_CHANGE_ALARM_VALUE:

            case KEY_HUMIDITY_THRESHOLD_ALARM_ENABLE:
            case KEY_HUMIDITY_THRESHOLD_ALARM:
            case KEY_HUMIDITY_CHANGE_ALARM_ENABLE:
            case KEY_HUMIDITY_CHANGE_ALARM_DURATION:
            case KEY_HUMIDITY_CHANGE_ALARM_VALUE:

            case KEY_TIME_ZONE:
            case KEY_CHANGE_PASSWORD:
            case KEY_HEARTBEAT:
            case KEY_LOW_POWER_PROMPT:
            case KEY_LOW_POWER_PAYLOAD:
            case KEY_LED_INDICATOR_STATUS:
                createGetConfigData(key.getParamsKey());
                break;
        }
    }

    private void createGetConfigData(int configKey) {
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x00,
                (byte) configKey,
                (byte) 0x00
        };
    }
}
