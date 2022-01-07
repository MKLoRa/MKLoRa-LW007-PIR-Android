package com.moko.support.lw007;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;

import com.elvishew.xlog.XLog;
import com.moko.ble.lib.MokoBleLib;
import com.moko.ble.lib.MokoBleManager;
import com.moko.ble.lib.MokoConstants;
import com.moko.ble.lib.event.ConnectStatusEvent;
import com.moko.ble.lib.event.OrderTaskResponseEvent;
import com.moko.ble.lib.task.OrderTask;
import com.moko.ble.lib.task.OrderTaskResponse;
import com.moko.support.lw007.entity.OrderCHAR;
import com.moko.support.lw007.handler.MokoCharacteristicHandler;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.UUID;

public class LoRaLW007MokoSupport extends MokoBleLib {
    private HashMap<OrderCHAR, BluetoothGattCharacteristic> mCharacteristicMap;

    private static volatile LoRaLW007MokoSupport INSTANCE;

    private Context mContext;

    private MokoBleConfig mBleConfig;

    private LoRaLW007MokoSupport() {
        //no instance
    }

    public static LoRaLW007MokoSupport getInstance() {
        if (INSTANCE == null) {
            synchronized (LoRaLW007MokoSupport.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LoRaLW007MokoSupport();
                }
            }
        }
        return INSTANCE;
    }

    public void init(Context context) {
        mContext = context;
        super.init(context);
    }

    @Override
    public MokoBleManager getMokoBleManager() {
        mBleConfig = new MokoBleConfig(mContext, this);
        return mBleConfig;
    }

    ///////////////////////////////////////////////////////////////////////////
    // connect
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onDeviceConnected(BluetoothGatt gatt) {
        mCharacteristicMap = new MokoCharacteristicHandler().getCharacteristics(gatt);
        ConnectStatusEvent connectStatusEvent = new ConnectStatusEvent();
        connectStatusEvent.setAction(MokoConstants.ACTION_DISCOVER_SUCCESS);
        EventBus.getDefault().post(connectStatusEvent);
    }

    @Override
    public void onDeviceDisconnected(BluetoothDevice device) {
        ConnectStatusEvent connectStatusEvent = new ConnectStatusEvent();
        connectStatusEvent.setAction(MokoConstants.ACTION_DISCONNECTED);
        EventBus.getDefault().post(connectStatusEvent);
    }

    @Override
    public BluetoothGattCharacteristic getCharacteristic(Enum orderCHAR) {
        return mCharacteristicMap.get(orderCHAR);
    }

    ///////////////////////////////////////////////////////////////////////////
    // order
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public boolean isCHARNull() {
        if (mCharacteristicMap == null || mCharacteristicMap.isEmpty()) {
            disConnectBle();
            return true;
        }
        return false;
    }

    @Override
    public void orderFinish() {
        OrderTaskResponseEvent event = new OrderTaskResponseEvent();
        event.setAction(MokoConstants.ACTION_ORDER_FINISH);
        EventBus.getDefault().post(event);
    }

    @Override
    public void orderTimeout(OrderTaskResponse response) {
        OrderTaskResponseEvent event = new OrderTaskResponseEvent();
        event.setAction(MokoConstants.ACTION_ORDER_TIMEOUT);
        event.setResponse(response);
        EventBus.getDefault().post(event);
    }

    @Override
    public void orderResult(OrderTaskResponse response) {
        OrderTaskResponseEvent event = new OrderTaskResponseEvent();
        event.setAction(MokoConstants.ACTION_ORDER_RESULT);
        event.setResponse(response);
        EventBus.getDefault().post(event);
    }

    @Override
    public boolean orderResponseValid(BluetoothGattCharacteristic characteristic, OrderTask orderTask) {
        final UUID responseUUID = characteristic.getUuid();
        final OrderCHAR orderCHAR = (OrderCHAR) orderTask.orderCHAR;
        return responseUUID.equals(orderCHAR.getUuid());
    }

    @Override
    public boolean orderNotify(BluetoothGattCharacteristic characteristic, byte[] value) {
        final UUID responseUUID = characteristic.getUuid();
        OrderCHAR orderCHAR = null;
        if (responseUUID.equals(OrderCHAR.CHAR_PIR.getUuid())) {
            orderCHAR = OrderCHAR.CHAR_PIR;
        }
        if (responseUUID.equals(OrderCHAR.CHAR_HALL_STATUS.getUuid())) {
            orderCHAR = OrderCHAR.CHAR_HALL_STATUS;
        }
        if (responseUUID.equals(OrderCHAR.CHAR_LOG.getUuid())) {
            orderCHAR = OrderCHAR.CHAR_LOG;
        }
        if (responseUUID.equals(OrderCHAR.CHAR_TH.getUuid())) {
            orderCHAR = OrderCHAR.CHAR_TH;
        }
        if (orderCHAR == null)
            return false;
        XLog.i(orderCHAR.name());
        OrderTaskResponse response = new OrderTaskResponse();
        response.orderCHAR = orderCHAR;
        response.responseValue = value;
        OrderTaskResponseEvent event = new OrderTaskResponseEvent();
        event.setAction(MokoConstants.ACTION_CURRENT_DATA);
        event.setResponse(response);
        EventBus.getDefault().post(event);
        return true;
    }

    public void enablePIRNotify() {
        if (mBleConfig != null)
            mBleConfig.enablePIRNotify();
    }

    public void disablePIRNotify() {
        if (mBleConfig != null)
            mBleConfig.disablePIRNotify();
    }

    public void enableHallStatusNotify() {
        if (mBleConfig != null)
            mBleConfig.enableHallStatusNotify();
    }

    public void disableHallStatusNotify() {
        if (mBleConfig != null)
            mBleConfig.disableHallStatusNotify();
    }

    public void enableTHNotify() {
        if (mBleConfig != null)
            mBleConfig.enableTHNotify();
    }

    public void disableTHNotify() {
        if (mBleConfig != null)
            mBleConfig.disableTHNotify();
    }

    public void enableLogNotify() {
        if (mBleConfig != null)
            mBleConfig.enableLogNotify();
    }

    public void disableLogNotify() {
        if (mBleConfig != null)
            mBleConfig.disableLogNotify();
    }

}
