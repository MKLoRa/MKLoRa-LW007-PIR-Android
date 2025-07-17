package com.moko.lw007.activity;


import android.os.Bundle;
import android.view.View;

import com.moko.ble.lib.MokoConstants;
import com.moko.ble.lib.event.ConnectStatusEvent;
import com.moko.ble.lib.event.OrderTaskResponseEvent;
import com.moko.ble.lib.task.OrderTask;
import com.moko.ble.lib.task.OrderTaskResponse;
import com.moko.ble.lib.utils.MokoUtils;
import com.moko.lib.loraui.dialog.AlertMessageDialog;
import com.moko.lw007.databinding.Lw007ActivityHallSettingsBinding;
import com.moko.lw007.utils.ToastUtils;
import com.moko.support.lw007.LoRaLW007MokoSupport;
import com.moko.support.lw007.OrderTaskAssembler;
import com.moko.support.lw007.entity.ControlKeyEnum;
import com.moko.support.lw007.entity.OrderCHAR;
import com.moko.support.lw007.entity.ParamsKeyEnum;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HallSettingsActivity extends BaseActivity {

    private Lw007ActivityHallSettingsBinding mBind;
    private boolean savedParamsError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBind = Lw007ActivityHallSettingsBinding.inflate(getLayoutInflater());
        setContentView(mBind.getRoot());
        EventBus.getDefault().register(this);
        LoRaLW007MokoSupport.getInstance().enableHallStatusNotify();
        showSyncingProgressDialog();
        mBind.tvHallStatus.postDelayed(() -> {
            List<OrderTask> orderTasks = new ArrayList<>();
            orderTasks.add(OrderTaskAssembler.getHallStatusEnable());
            orderTasks.add(OrderTaskAssembler.getHallStatusSum());
            LoRaLW007MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
        }, 500);
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 200)
    public void onConnectStatusEvent(ConnectStatusEvent event) {
        final String action = event.getAction();
        runOnUiThread(() -> {
            if (MokoConstants.ACTION_DISCONNECTED.equals(action)) {
                finish();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 200)
    public void onOrderTaskResponseEvent(OrderTaskResponseEvent event) {
        final String action = event.getAction();
        if (!MokoConstants.ACTION_CURRENT_DATA.equals(action))
            EventBus.getDefault().cancelEventDelivery(event);
        runOnUiThread(() -> {
            if (MokoConstants.ACTION_CURRENT_DATA.equals(action)) {
                OrderTaskResponse response = event.getResponse();
                OrderCHAR orderCHAR = (OrderCHAR) response.orderCHAR;
                int responseType = response.responseType;
                byte[] value = response.responseValue;
                switch (orderCHAR) {
                    case CHAR_HALL_STATUS:
                        final int length = value.length;
                        if (length != 7)
                            return;
                        int header = value[0] & 0xFF;
                        int flag = value[1] & 0xFF;
                        int cmd = value[2] & 0xFF;
                        int len = value[3] & 0xFF;
                        if (header == 0xED && flag == 0x02 && cmd == 0x01 && len == 0x03) {
                            int hallStatus = value[4] & 0xFF;
                            int triggerTimes = MokoUtils.toInt(Arrays.copyOfRange(value, 5, 7));
                            if (hallStatus == 0xFF) {
                                mBind.tvHallStatus.setVisibility(View.GONE);
                                mBind.tvTotalTriggerTimes.setVisibility(View.GONE);
                            } else {
                                mBind.tvHallStatus.setVisibility(View.VISIBLE);
                                mBind.tvTotalTriggerTimes.setVisibility(View.VISIBLE);
                                mBind.tvHallStatus.setText(hallStatus == 1 ? "Open" : "Closed");
                                mBind.tvTotalTriggerTimes.setText(String.valueOf(triggerTimes));
                            }
                        }
                        break;
                }
            }
            if (MokoConstants.ACTION_ORDER_TIMEOUT.equals(action)) {
            }
            if (MokoConstants.ACTION_ORDER_FINISH.equals(action)) {
                dismissSyncProgressDialog();
            }
            if (MokoConstants.ACTION_ORDER_RESULT.equals(action)) {
                OrderTaskResponse response = event.getResponse();
                OrderCHAR orderCHAR = (OrderCHAR) response.orderCHAR;
                int responseType = response.responseType;
                byte[] value = response.responseValue;
                switch (orderCHAR) {
                    case CHAR_CONTROL:
                        if (value.length >= 4) {
                            int header = value[0] & 0xFF;// 0xED
                            int flag = value[1] & 0xFF;// read or write
                            int cmd = value[2] & 0xFF;
                            if (header != 0xED)
                                return;
                            ControlKeyEnum controlKeyEnum = ControlKeyEnum.fromParamKey(cmd);
                            if (controlKeyEnum == null) {
                                return;
                            }
                            int length = value[3] & 0xFF;
                            if (flag == 0x00) {
                                // read
                                switch (controlKeyEnum) {
                                    case KEY_HALL_STATUS_SUM:
                                        if (length > 0) {
                                            int hallStatus = value[4] & 0xFF;
                                            int triggerTimes = MokoUtils.toInt(Arrays.copyOfRange(value, 5, 7));
                                            if (hallStatus == 0xFF) {
                                                mBind.tvHallStatus.setVisibility(View.GONE);
                                                mBind.tvTotalTriggerTimes.setVisibility(View.GONE);
                                            } else {
                                                mBind.tvHallStatus.setVisibility(View.VISIBLE);
                                                mBind.tvTotalTriggerTimes.setVisibility(View.VISIBLE);
                                                mBind.tvHallStatus.setText(hallStatus == 1 ? "Open" : "Closed");
                                                mBind.tvTotalTriggerTimes.setText(String.valueOf(triggerTimes));
                                            }
                                        }
                                        break;
                                }
                            }
                        }
                        break;
                    case CHAR_PARAMS:
                        if (value.length >= 4) {
                            int header = value[0] & 0xFF;// 0xED
                            int flag = value[1] & 0xFF;// read or write
                            int cmd = value[2] & 0xFF;
                            if (header != 0xED)
                                return;
                            ParamsKeyEnum configKeyEnum = ParamsKeyEnum.fromParamKey(cmd);
                            if (configKeyEnum == null) {
                                return;
                            }
                            int length = value[3] & 0xFF;
                            if (flag == 0x01) {
                                // write
                                int result = value[4] & 0xFF;
                                switch (configKeyEnum) {
                                    case KEY_HALL_STATUS_ENABLE:
                                        if (result != 1) {
                                            savedParamsError = true;
                                        }
                                        if (savedParamsError) {
                                            ToastUtils.showToast(HallSettingsActivity.this, "Opps！Save failed. Please check the input characters and try again.");
                                        } else {
                                            mBind.tvHallStatus.setVisibility(mBind.cbHallEnable.isChecked() ? View.VISIBLE : View.GONE);
                                            mBind.tvTotalTriggerTimes.setVisibility(mBind.cbHallEnable.isChecked() ? View.VISIBLE : View.GONE);
                                            AlertMessageDialog dialog = new AlertMessageDialog();
                                            dialog.setMessage("Saved Successfully！");
                                            dialog.setConfirm("OK");
                                            dialog.setCancelGone();
                                            dialog.show(getSupportFragmentManager());
                                        }
                                        break;
                                }
                            }
                            if (flag == 0x00) {
                                // read
                                switch (configKeyEnum) {
                                    case KEY_HALL_STATUS_ENABLE:
                                        if (length > 0) {
                                            int enable = value[4] & 0xFF;
                                            mBind.tvHallStatus.setVisibility(enable == 0 ? View.GONE : View.VISIBLE);
                                            mBind.tvTotalTriggerTimes.setVisibility(enable == 0 ? View.GONE : View.VISIBLE);
                                            mBind.cbHallEnable.setChecked(enable == 1);
                                        }
                                        break;

                                }
                            }
                        }
                        break;
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    public void onBack(View view) {
        backHome();
    }

    @Override
    public void onBackPressed() {
        backHome();
    }

    private void backHome() {
        LoRaLW007MokoSupport.getInstance().disableHallStatusNotify();
        finish();
    }

    public void onSave(View view) {
        if (isWindowLocked())
            return;
        showSyncingProgressDialog();
        saveParams();
    }

    private void saveParams() {
        savedParamsError = false;
        List<OrderTask> orderTasks = new ArrayList<>();
        orderTasks.add(OrderTaskAssembler.setHallStatusEnable(mBind.cbHallEnable.isChecked() ? 1 : 0));
        if (mBind.cbHallEnable.isChecked()) {
            orderTasks.add(OrderTaskAssembler.getHallStatusSum());
        }
        LoRaLW007MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }
}
