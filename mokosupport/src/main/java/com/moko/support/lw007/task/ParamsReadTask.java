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
            case KEY_LORA_CLASS:
            case KEY_LORA_DEV_EUI:
            case KEY_LORA_APP_EUI:
            case KEY_LORA_APP_KEY:
            case KEY_LORA_DEV_ADDR:
            case KEY_LORA_APP_SKEY:
            case KEY_LORA_NWK_SKEY:
            case KEY_LORA_MESSAGE_TYPE:
            case KEY_LORA_CH:
            case KEY_LORA_DR:
            case KEY_LORA_UPLINK_STRATEGY:
            case KEY_LORA_DUTYCYCLE:
            case KEY_LORA_MAX_RETRANSMISSION_TIMES:
            case KEY_LORA_TIME_SYNC_INTERVAL:
            case KEY_LORA_NETWORK_CHECK_INTERVAL:

            case KEY_POWER_ON_DEFAULT_MODE:
            case KEY_SWITCH_PAYLOADS_REPORT_INTERVAL:
            case KEY_ELECTRICITY_PAYLOADS_REPORT_INTERVAL:
            case KEY_ENERGY_CONFIG_INTERVAL:
            case KEY_POWER_CHANGE_VALUE:

            case KEY_DEVICE_SPECIFICATION:
            case KEY_OVER_LOAD_PROTECTION:
            case KEY_OVER_VOLTAGE_PROTECTION:
            case KEY_SAG_VOLTAGE_PROTECTION:
            case KEY_OVER_CURRENT_PROTECTION:

            case KEY_LOAD_NOTIFICATION:
            case KEY_LOAD_STATUS_THRESHOLD:
            case KEY_TIME_ZONE:
            case KEY_COUNTDOWN_PAYLOADS_REPORT_INTERVAL:
            case KEY_LED_INDICATOR_STATUS:
            case KEY_POWER_INDICATOR_COLOR:

            case KEY_BLE_ADV_NAME:
            case KEY_BLE_ADV_INTERVAL:
            case KEY_BLE_TX_POWER:
            case KEY_BLE_CONNECTABLE:
            case KEY_BLE_LOGIN_MODE:
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
