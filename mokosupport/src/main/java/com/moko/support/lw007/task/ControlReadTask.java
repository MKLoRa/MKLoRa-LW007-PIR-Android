package com.moko.support.lw007.task;

import com.moko.ble.lib.task.OrderTask;
import com.moko.support.lw007.entity.ControlKeyEnum;
import com.moko.support.lw007.entity.OrderCHAR;

public class ControlReadTask extends OrderTask {
    public byte[] data;

    public ControlReadTask() {
        super(OrderCHAR.CHAR_CONTROL, OrderTask.RESPONSE_TYPE_WRITE_NO_RESPONSE);
    }

    @Override
    public byte[] assemble() {
        return data;
    }

    public void setData(ControlKeyEnum key) {
        switch (key) {
            case KEY_NETWORK_STATUS:
//            case KEY_BATTERY:
            case KEY_MAC:
            case KEY_PIR:
            case KEY_HALL_STATUS_SUM:
            case KEY_TH_DATA:
            case KEY_PCBA_STATUS:
            case KEY_SELFTEST_STATUS:
            case KEY_BATTERY_INFO:
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
