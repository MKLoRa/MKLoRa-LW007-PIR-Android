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
        createGetConfigData(key.getParamsKey());
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
