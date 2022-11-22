package com.moko.support.lw007.entity;


import java.io.Serializable;

public enum ParamsKeyEnum implements Serializable {


    // lora
    KEY_LORA_REGION(0x01),
    KEY_LORA_MODE(0x02),
    KEY_LORA_DEV_EUI(0x03),
    KEY_LORA_APP_EUI(0x04),
    KEY_LORA_APP_KEY(0x05),
    KEY_LORA_DEV_ADDR(0x06),
    KEY_LORA_APP_SKEY(0x07),
    KEY_LORA_NWK_SKEY(0x08),
    KEY_LORA_MESSAGE_TYPE(0x09),
    KEY_LORA_MAX_RETRANSMISSION_TIMES(0x0A),
    KEY_LORA_CH(0x0B),
    KEY_LORA_DR(0x0C),
    KEY_LORA_UPLINK_STRATEGY(0x0D),
    KEY_LORA_DUTYCYCLE(0x0E),
    KEY_LORA_TIME_SYNC_INTERVAL(0x0F),
    KEY_LORA_NETWORK_CHECK_INTERVAL(0x10),

    // ble
    KEY_BLE_ENABLE(0x20),
    KEY_BLE_ADV_INTERVAL(0x21),
    KEY_BLE_CONNECTABLE(0x22),
    KEY_BLE_TIMEOUT_DURATION(0x23),
    KEY_BLE_LOGIN_MODE(0x24),
    KEY_BLE_TX_POWER(0x25),
    KEY_BLE_ADV_NAME(0x26),
    // general pir
    KEY_PIR_ENABLE(0x30),
    KEY_PIR_REPORT_INTERVAL(0x31),
    KEY_PIR_SENSITIVITY(0x32),
    KEY_PIR_DELAY_TIME(0x33),
    // general hall status
    KEY_HALL_STATUS_ENABLE(0x34),
    // general T&H
    KEY_TH_ENABLE(0x35),
    KEY_TH_SAMPLE_RATE(0x36),

    KEY_TEMP_THRESHOLD_ALARM_ENABLE(0x37),
    KEY_TEMP_THRESHOLD_ALARM(0x38),
    KEY_TEMP_CHANGE_ALARM_DURATION(0x3B),
    KEY_TEMP_CHANGE_ALARM_ENABLE(0x3A),
    KEY_TEMP_CHANGE_ALARM_VALUE(0x3C),

    KEY_HUMIDITY_THRESHOLD_ALARM_ENABLE(0x3D),
    KEY_HUMIDITY_THRESHOLD_ALARM(0x3E),
    KEY_HUMIDITY_CHANGE_ALARM_ENABLE(0x40),
    KEY_HUMIDITY_CHANGE_ALARM_DURATION(0x41),
    KEY_HUMIDITY_CHANGE_ALARM_VALUE(0x42),
    // device
    KEY_TIME_ZONE(0x43),
    KEY_CHANGE_PASSWORD(0x44),
    KEY_HEARTBEAT(0x46),
//    KEY_LOW_POWER_PROMPT(0x47),
    KEY_LOW_POWER_PAYLOAD(0x48),
    KEY_LED_INDICATOR_STATUS(0x49),
    ;

    private int paramsKey;

    ParamsKeyEnum(int paramsKey) {
        this.paramsKey = paramsKey;
    }


    public int getParamsKey() {
        return paramsKey;
    }

    public static ParamsKeyEnum fromParamKey(int paramsKey) {
        for (ParamsKeyEnum paramsKeyEnum : ParamsKeyEnum.values()) {
            if (paramsKeyEnum.getParamsKey() == paramsKey) {
                return paramsKeyEnum;
            }
        }
        return null;
    }
}
