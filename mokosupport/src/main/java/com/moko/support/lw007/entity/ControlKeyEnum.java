package com.moko.support.lw007.entity;


import java.io.Serializable;

public enum ControlKeyEnum implements Serializable {


    KEY_SWITCH_STATUS(0x61),
    KEY_NETWORK_STATUS(0x62),
    KEY_LOAD_STATUS(0x63),
    KEY_TOTAL_ENERGY(0x65),
    KEY_RESTART(0x66),
    KEY_MAC(0x68),
    KEY_TIME(0x69),
    KEY_RESTORE(0x6A),
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
