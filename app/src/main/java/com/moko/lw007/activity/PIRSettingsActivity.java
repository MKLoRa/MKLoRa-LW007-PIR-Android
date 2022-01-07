package com.moko.lw007.activity;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.moko.ble.lib.MokoConstants;
import com.moko.ble.lib.event.ConnectStatusEvent;
import com.moko.ble.lib.event.OrderTaskResponseEvent;
import com.moko.ble.lib.task.OrderTask;
import com.moko.ble.lib.task.OrderTaskResponse;
import com.moko.lw007.R;
import com.moko.lw007.R2;
import com.moko.lw007.dialog.AlertMessageDialog;
import com.moko.lw007.dialog.LoadingMessageDialog;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.carbswang.android.numberpickerview.library.NumberPickerView;

public class PIRSettingsActivity extends BaseActivity {

    @BindView(R2.id.cb_pir_enable)
    CheckBox cbPirEnable;
    @BindView(R2.id.npv_pir_sensitivity)
    NumberPickerView npvPirSensitivity;
    @BindView(R2.id.npv_pir_delay)
    NumberPickerView npvPirDelay;
    @BindView(R2.id.tv_pir_status)
    TextView tvPirStatus;
    @BindView(R2.id.et_pir_report_interval)
    EditText etPirReportInterval;
    private boolean savedParamsError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lw007_activity_pir_settings);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        npvPirSensitivity.setMaxValue(2);
        npvPirSensitivity.setMinValue(0);
        npvPirDelay.setMaxValue(2);
        npvPirDelay.setMinValue(0);
        LoRaLW007MokoSupport.getInstance().enablePIRNotify();
        showSyncingProgressDialog();
        tvPirStatus.postDelayed(() -> {
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
                            tvPirStatus.setVisibility(pirStatus == 0xFF ? View.GONE : View.VISIBLE);
                            tvPirStatus.setText(pirStatus == 1 ? "Motion detected" : "Motion not detected");
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
                                            tvPirStatus.setVisibility(pirStatus == 0xFF ? View.GONE : View.VISIBLE);
                                            tvPirStatus.setText(pirStatus == 1 ? "Motion detected" : "Motion not detected");
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
                                            tvPirStatus.setVisibility(cbPirEnable.isChecked() ? View.VISIBLE : View.GONE);
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
                                            tvPirStatus.setVisibility(enable == 0 ? View.GONE : View.VISIBLE);
                                            cbPirEnable.setEnabled(enable == 1);
                                        }
                                        break;
                                    case KEY_PIR_REPORT_INTERVAL:
                                        if (length > 0) {
                                            int interval = value[4] & 0xFF;
                                            etPirReportInterval.setText(String.valueOf(interval));
                                        }
                                        break;
                                    case KEY_PIR_SENSITIVITY:
                                        if (length > 0) {
                                            int sensitivity = value[4] & 0xFF;
                                            npvPirSensitivity.setValue(sensitivity - 1);
                                        }
                                        break;
                                    case KEY_PIR_DELAY_TIME:
                                        if (length > 0) {
                                            int delay = value[4] & 0xFF;
                                            npvPirDelay.setValue(delay - 1);
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

    private LoadingMessageDialog mLoadingMessageDialog;

    public void showSyncingProgressDialog() {
        mLoadingMessageDialog = new LoadingMessageDialog();
        mLoadingMessageDialog.setMessage("Syncing..");
        mLoadingMessageDialog.show(getSupportFragmentManager());

    }

    public void dismissSyncProgressDialog() {
        if (mLoadingMessageDialog != null)
            mLoadingMessageDialog.dismissAllowingStateLoss();
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
        final String intervalStr = etPirReportInterval.getText().toString();
        if (TextUtils.isEmpty(intervalStr))
            return false;
        final int interval = Integer.parseInt(intervalStr);
        if (interval < 1 || interval > 60)
            return false;
        return true;

    }

    private void saveParams() {
        final String intervalStr = etPirReportInterval.getText().toString();
        final int interval = Integer.parseInt(intervalStr);
        final int sensitivity = npvPirSensitivity.getValue() + 1;
        final int delay = npvPirDelay.getValue() + 1;
        savedParamsError = false;
        List<OrderTask> orderTasks = new ArrayList<>();
        orderTasks.add(OrderTaskAssembler.setPIRReportInterval(interval));
        orderTasks.add(OrderTaskAssembler.setPIRSensitivity(sensitivity));
        orderTasks.add(OrderTaskAssembler.setPIRDelayTime(delay));
        orderTasks.add(OrderTaskAssembler.setPIREnable(cbPirEnable.isChecked() ? 1 : 0));
        LoRaLW007MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }
}
