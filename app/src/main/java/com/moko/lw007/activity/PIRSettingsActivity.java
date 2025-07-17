package com.moko.lw007.activity;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.moko.ble.lib.MokoConstants;
import com.moko.ble.lib.event.ConnectStatusEvent;
import com.moko.ble.lib.event.OrderTaskResponseEvent;
import com.moko.ble.lib.task.OrderTask;
import com.moko.ble.lib.task.OrderTaskResponse;
import com.moko.lib.loraui.dialog.AlertMessageDialog;
import com.moko.lw007.databinding.Lw007ActivityPirSettingsBinding;
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
import java.util.List;

public class PIRSettingsActivity extends BaseActivity {

    private Lw007ActivityPirSettingsBinding mBind;
    private boolean savedParamsError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBind = Lw007ActivityPirSettingsBinding.inflate(getLayoutInflater());
        setContentView(mBind.getRoot());
        EventBus.getDefault().register(this);
        mBind.npvPirSensitivity.setMaxValue(2);
        mBind.npvPirSensitivity.setMinValue(0);
        mBind.npvPirDelay.setMaxValue(2);
        mBind.npvPirDelay.setMinValue(0);
        LoRaLW007MokoSupport.getInstance().enablePIRNotify();
        showSyncingProgressDialog();
        mBind.tvPirStatus.postDelayed(() -> {
            List<OrderTask> orderTasks = new ArrayList<>();
            orderTasks.add(OrderTaskAssembler.getPIR());
            orderTasks.add(OrderTaskAssembler.getPIREnable());
            orderTasks.add(OrderTaskAssembler.getPIRReportInterval());
            orderTasks.add(OrderTaskAssembler.getPIRSensitivity());
            orderTasks.add(OrderTaskAssembler.getPIRDelayTime());
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
                    case CHAR_PIR:
                        final int length = value.length;
                        if (length != 5)
                            return;
                        int header = value[0] & 0xFF;
                        int flag = value[1] & 0xFF;
                        int cmd = value[2] & 0xFF;
                        int len = value[3] & 0xFF;
                        if (header == 0xED && flag == 0x02 && cmd == 0x01 && len == 0x01) {
                            int pirStatus = value[4] & 0xFF;
                            mBind.tvPirStatus.setVisibility(pirStatus == 0xFF ? View.GONE : View.VISIBLE);
                            mBind.tvPirStatus.setText(pirStatus == 1 ? "Motion detected" : "Motion not detected");
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
                                    case KEY_PIR:
                                        if (length > 0) {
                                            int pirStatus = value[4] & 0xFF;
                                            mBind.tvPirStatus.setVisibility(pirStatus == 0xFF ? View.GONE : View.VISIBLE);
                                            mBind.tvPirStatus.setText(pirStatus == 1 ? "Motion detected" : "Motion not detected");
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
                                    case KEY_PIR_REPORT_INTERVAL:
                                    case KEY_PIR_SENSITIVITY:
                                    case KEY_PIR_DELAY_TIME:
                                        if (result != 1) {
                                            savedParamsError = true;
                                        }
                                        break;
                                    case KEY_PIR_ENABLE:
                                        if (result != 1) {
                                            savedParamsError = true;
                                        }
                                        if (savedParamsError) {
                                            ToastUtils.showToast(PIRSettingsActivity.this, "Opps！Save failed. Please check the input characters and try again.");
                                        } else {
                                            mBind.tvPirStatus.setVisibility(mBind.cbPirEnable.isChecked() ? View.VISIBLE : View.GONE);
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
                                    case KEY_PIR_ENABLE:
                                        if (length > 0) {
                                            int enable = value[4] & 0xFF;
                                            mBind.tvPirStatus.setVisibility(enable == 0 ? View.GONE : View.VISIBLE);
                                            mBind.cbPirEnable.setChecked(enable == 1);
                                        }
                                        break;
                                    case KEY_PIR_REPORT_INTERVAL:
                                        if (length > 0) {
                                            int interval = value[4] & 0xFF;
                                            mBind.etPirReportInterval.setText(String.valueOf(interval));
                                        }
                                        break;
                                    case KEY_PIR_SENSITIVITY:
                                        if (length > 0) {
                                            int sensitivity = value[4] & 0xFF;
                                            mBind.npvPirSensitivity.setValue(sensitivity - 1);
                                        }
                                        break;
                                    case KEY_PIR_DELAY_TIME:
                                        if (length > 0) {
                                            int delay = value[4] & 0xFF;
                                            mBind.npvPirDelay.setValue(delay - 1);
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
        LoRaLW007MokoSupport.getInstance().disablePIRNotify();
        finish();
    }

    public void onSave(View view) {
        if (isWindowLocked())
            return;
        if (isValid()) {
            showSyncingProgressDialog();
            saveParams();
        } else {
            ToastUtils.showToast(this, "Opps！Save failed. Please check the input characters and try again.");
        }
    }

    private boolean isValid() {
        final String intervalStr = mBind.etPirReportInterval.getText().toString();
        if (TextUtils.isEmpty(intervalStr))
            return false;
        final int interval = Integer.parseInt(intervalStr);
        if (interval < 1 || interval > 60)
            return false;
        return true;

    }

    private void saveParams() {
        final String intervalStr = mBind.etPirReportInterval.getText().toString();
        final int interval = Integer.parseInt(intervalStr);
        final int sensitivity = mBind.npvPirSensitivity.getValue() + 1;
        final int delay = mBind.npvPirDelay.getValue() + 1;
        savedParamsError = false;
        List<OrderTask> orderTasks = new ArrayList<>();
        orderTasks.add(OrderTaskAssembler.setPIRReportInterval(interval));
        orderTasks.add(OrderTaskAssembler.setPIRSensitivity(sensitivity));
        orderTasks.add(OrderTaskAssembler.setPIRDelayTime(delay));
        orderTasks.add(OrderTaskAssembler.setPIREnable(mBind.cbPirEnable.isChecked() ? 1 : 0));
        if (mBind.cbPirEnable.isChecked()) {
            orderTasks.add(OrderTaskAssembler.getPIR());
        }
        LoRaLW007MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }
}
