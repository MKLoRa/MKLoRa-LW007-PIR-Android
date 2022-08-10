package com.moko.support.lw007.entity;


import java.io.Serializable;

public enum ControlKeyEnum implements Serializable {


    KEY_RESTART(0x50),
    KEY_RESTORE(0x51),
    KEY_POWER_OFF(0x52),
    KEY_TIME(0x53),
    KEY_NETWORK_STATUS(0x54),
    KEY_BATTERY(0x56),
    KEY_MAC(0x57),
    KEY_PIR(0x58),
    KEY_HALL_STATUS_SUM(0x59),
    KEY_TH_DATA(0x5A),
    KEY_PCBA_STATUS(0x5C),
    KEY_SELFTEST_STATUS(0x5D),
    ;

    private int paramsKey;

    ControlKeyEnum(int paramsKey) {
        this.paramsKey = paramsKey;
    }


    public int getParamsKey() {
        return paramsKey;
    }

    public static ControlKeyEnum fromParamKey(int paramsKey) {
        for (ControlKeyEnum paramsKeyEnum : ControlKeyEnum.values()) {
            if (paramsKeyEnum.getParamsKey() == paramsKey) {
                return paramsKeyEnum;
            }
        }
        return null;
    }
}
