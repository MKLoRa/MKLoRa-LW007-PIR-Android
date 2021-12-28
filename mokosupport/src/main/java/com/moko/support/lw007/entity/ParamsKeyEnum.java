package com.moko.support.lw007.entity;


import java.io.Serializable;

public enum ParamsKeyEnum implements Serializable {


    // lora
    KEY_LORA_MODE(0x01),
    KEY_LORA_DEV_EUI(0x02),
    KEY_LORA_APP_EUI(0x03),
    KEY_LORA_APP_KEY(0x04),
    KEY_LORA_DEV_ADDR(0x05),
    KEY_LORA_APP_SKEY(0x06),
    KEY_LORA_NWK_SKEY(0x07),
    KEY_LORA_REGION(0x08),
    KEY_LORA_CLASS(0x09),
    KEY_LORA_MESSAGE_TYPE(0x0A),
    KEY_LORA_CH(0x0B),
    KEY_LORA_DUTYCYCLE(0x0C),
    KEY_LORA_DR(0x0D),
    KEY_LORA_UPLINK_STRATEGY(0x0E),
    KEY_LORA_MAX_RETRANSMISSION_TIMES(0x0F),
    KEY_LORA_TIME_SYNC_INTERVAL(0x10),
    KEY_LORA_NETWORK_CHECK_INTERVAL(0x11),

    // ble
    KEY_BLE_ADV_NAME(0x21),
    KEY_BLE_ADV_INTERVAL(0x22),
    KEY_BLE_TX_POWER(0x23),
    KEY_BLE_CONNECTABLE(0x24),
    KEY_BLE_LOGIN_MODE(0x25),
    KEY_BLE_CHANGE_PASSWORD(0x26),
    // general switch control
    KEY_POWER_ON_DEFAULT_MODE(0x41),
    KEY_SWITCH_PAYLOADS_REPORT_INTERVAL(0x42),
    // general electricity
    KEY_ELECTRICITY_PAYLOADS_REPORT_INTERVAL(0x43),
    // general energy
    KEY_ENERGY_CONFIG_INTERVAL(0x44),
    KEY_POWER_CHANGE_VALUE(0x45),
    // general protection settings
    KEY_DEVICE_SPECIFICATION(0x46),
    KEY_OVER_VOLTAGE_PROTECTION(0x47),
    KEY_SAG_VOLTAGE_PROTECTION(0x48),
    KEY_OVER_CURRENT_PROTECTION(0x49),
    KEY_OVER_LOAD_PROTECTION(0x4A),
    // general load notification
    KEY_LOAD_NOTIFICATION(0x4B),
    KEY_LOAD_STATUS_THRESHOLD(0x4C),
    // general power indicator color
    KEY_POWER_INDICATOR_COLOR(0x4D),
    // timeZone
    KEY_TIME_ZONE(0x4E),
    // general countdown
    KEY_COUNTDOWN_PAYLOADS_REPORT_INTERVAL(0x4F),
    // general LED
    KEY_LED_INDICATOR_STATUS(0x50),
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
