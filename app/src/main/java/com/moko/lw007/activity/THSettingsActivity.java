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
import com.moko.ble.lib.utils.MokoUtils;
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
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class THSettingsActivity extends BaseActivity {


    @BindView(R2.id.cb_th_enable)
    CheckBox cbThEnable;
    @BindView(R2.id.et_sample_rate)
    EditText etSampleRate;
    @BindView(R2.id.tv_temp_value)
    TextView tvTempValue;
    @BindView(R2.id.tv_humidity_value)
    TextView tvHumidityValue;
    @BindView(R2.id.cb_temp_threshold_alarm)
    CheckBox cbTempThresholdAlarm;
    @BindView(R2.id.et_temp_threshold_alarm_max)
    EditText etTempThresholdAlarmMax;
    @BindView(R2.id.et_temp_threshold_alarm_min)
    EditText etTempThresholdAlarmMin;
    @BindView(R2.id.cb_temp_change_alarm)
    CheckBox cbTempChangeAlarm;
    @BindView(R2.id.et_temp_duration_condition)
    EditText etTempDurationCondition;
    @BindView(R2.id.et_temp_change_threshold)
    EditText etTempChangeThreshold;
    @BindView(R2.id.cb_humidity_threshold_alarm)
    CheckBox cbHumidityThresholdAlarm;
    @BindView(R2.id.et_humidity_threshold_alarm_max)
    EditText etHumidityThresholdAlarmMax;
    @BindView(R2.id.et_humidity_threshold_alarm_min)
    EditText etHumidityThresholdAlarmMin;
    @BindView(R2.id.cb_humidity_change_alarm)
    CheckBox cbHumidityChangeAlarm;
    @BindView(R2.id.et_humidity_duration_condition)
    EditText etHumidityDurationCondition;
    @BindView(R2.id.et_humidity_change_threshold)
    EditText etHumidityChangeThreshold;
    private boolean savedParamsError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lw007_activity_th_settings);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        LoRaLW007MokoSupport.getInstance().enableTHNotify();
        showSyncingProgressDialog();
        cbThEnable.postDelayed(() -> {
            List<OrderTask> orderTasks = new ArrayList<>();
            orderTasks.add(OrderTaskAssembler.getTHData());
            orderTasks.add(OrderTaskAssembler.getTHEnable());
            orderTasks.add(OrderTaskAssembler.getTHSampleRate());
            orderTasks.add(OrderTaskAssembler.getTempThresholdAlarmEnable());
            orderTasks.add(OrderTaskAssembler.getTempThresholdAlarm());
            orderTasks.add(OrderTaskAssembler.getTempChangeAlarmEnable());
            orderTasks.add(OrderTaskAssembler.getTempChangeAlarmDuration());
            orderTasks.add(OrderTaskAssembler.getTempChangeAlarmValue());
            orderTasks.add(OrderTaskAssembler.getHumidityThresholdAlarmEnable());
            orderTasks.add(OrderTaskAssembler.getHumidityThresholdAlarm());
            orderTasks.add(OrderTaskAssembler.getHumidityChangeAlarmEnable());
            orderTasks.add(OrderTaskAssembler.getHumidityChangeAlarmDuration());
            orderTasks.add(OrderTaskAssembler.getHumidityChangeAlarmValue());
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
                    case CHAR_TH:
                        final int length = value.length;
                        if (length != 8)
                            return;
                        int header = value[0] & 0xFF;
                        int flag = value[1] & 0xFF;
                        int cmd = value[2] & 0xFF;
                        int len = value[3] & 0xFF;
                        if (header == 0xED && flag == 0x02 && cmd == 0x01 && len == 0x04) {
                            int temp = MokoUtils.toInt(Arrays.copyOfRange(value, 4, 6));
                            int humidity = MokoUtils.toInt(Arrays.copyOfRange(value, 6, 8));
                            if (temp == 0xFF && humidity == 0xFF) {
                                tvTempValue.setVisibility(View.GONE);
                                tvHumidityValue.setVisibility(View.GONE);
                            } else {
                                tvTempValue.setVisibility(View.VISIBLE);
                                tvHumidityValue.setVisibility(View.VISIBLE);
                                tvTempValue.setText(String.format("%s ℃", MokoUtils.getDecimalFormat("0.0").format(temp * 0.1f - 30)));
                                tvHumidityValue.setText(String.format("%s%%RH", MokoUtils.getDecimalFormat("0.0").format(humidity * 0.1f)));
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
                                    case KEY_TH_DATA:
                                        if (length > 0) {
                                            int temp = MokoUtils.toInt(Arrays.copyOfRange(value, 4, 6));
                                            int humidity = MokoUtils.toInt(Arrays.copyOfRange(value, 6, 8));
                                            if (temp == 0xFF && humidity == 0xFF) {
                                                tvTempValue.setVisibility(View.GONE);
                                                tvHumidityValue.setVisibility(View.GONE);
                                            } else {
                                                tvTempValue.setVisibility(View.VISIBLE);
                                                tvHumidityValue.setVisibility(View.VISIBLE);
                                                tvTempValue.setText(String.format("%s ℃", MokoUtils.getDecimalFormat("0.0").format(temp * 0.1f - 30)));
                                                tvHumidityValue.setText(String.format("%s%%RH", MokoUtils.getDecimalFormat("0.0").format(humidity * 0.1f)));
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
                                    case KEY_TH_SAMPLE_RATE:
                                    case KEY_TEMP_THRESHOLD_ALARM_ENABLE:
                                    case KEY_TEMP_THRESHOLD_ALARM:
                                    case KEY_TEMP_CHANGE_ALARM_ENABLE:
                                    case KEY_TEMP_CHANGE_ALARM_DURATION:
                                    case KEY_TEMP_CHANGE_ALARM_VALUE:
                                    case KEY_HUMIDITY_THRESHOLD_ALARM_ENABLE:
                                    case KEY_HUMIDITY_THRESHOLD_ALARM:
                                    case KEY_HUMIDITY_CHANGE_ALARM_ENABLE:
                                    case KEY_HUMIDITY_CHANGE_ALARM_DURATION:
                                    case KEY_HUMIDITY_CHANGE_ALARM_VALUE:
                                        if (result != 1) {
                                            savedParamsError = true;
                                        }
                                        break;
                                    case KEY_TH_ENABLE:
                                        if (result != 1) {
                                            savedParamsError = true;
                                        }
                                        if (savedParamsError) {
                                            ToastUtils.showToast(THSettingsActivity.this, "Opps！Save failed. Please check the input characters and try again.");
                                        } else {
                                            tvTempValue.setVisibility(cbThEnable.isChecked() ? View.VISIBLE : View.GONE);
                                            tvHumidityValue.setVisibility(cbThEnable.isChecked() ? View.VISIBLE : View.GONE);
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
                                    case KEY_TH_ENABLE:
                                        if (length > 0) {
                                            int enable = value[4] & 0xFF;
                                            tvTempValue.setVisibility(enable == 0 ? View.GONE : View.VISIBLE);
                                            tvHumidityValue.setVisibility(enable == 0 ? View.GONE : View.VISIBLE);
                                            cbThEnable.setChecked(enable == 1);
                                        }
                                        break;
                                    case KEY_TH_SAMPLE_RATE:
                                        if (length > 0) {
                                            int interval = value[4] & 0xFF;
                                            etSampleRate.setText(String.valueOf(interval));
                                        }
                                        break;
                                    case KEY_TEMP_THRESHOLD_ALARM_ENABLE:
                                        if (length > 0) {
                                            int enable = value[4] & 0xFF;
                                            cbTempThresholdAlarm.setChecked(enable == 1);
                                        }
                                        break;
                                    case KEY_TEMP_THRESHOLD_ALARM:
                                        if (length > 0) {
                                            int min = value[4];
                                            int max = value[5];
                                            etTempThresholdAlarmMin.setText(String.valueOf(min));
                                            etTempThresholdAlarmMax.setText(String.valueOf(max));
                                        }
                                        break;
                                    case KEY_TEMP_CHANGE_ALARM_ENABLE:
                                        if (length > 0) {
                                            int enable = value[4] & 0xFF;
                                            cbTempChangeAlarm.setChecked(enable == 1);
                                        }
                                        break;
                                    case KEY_TEMP_CHANGE_ALARM_DURATION:
                                        if (length > 0) {
                                            int duration = value[4] & 0xFF;
                                            etTempDurationCondition.setText(String.valueOf(duration));
                                        }
                                        break;
                                    case KEY_TEMP_CHANGE_ALARM_VALUE:
                                        if (length > 0) {
                                            int threshold = value[4] & 0xFF;
                                            etTempChangeThreshold.setText(String.valueOf(threshold));
                                        }
                                        break;
                                    case KEY_HUMIDITY_THRESHOLD_ALARM_ENABLE:
                                        if (length > 0) {
                                            int enable = value[4] & 0xFF;
                                            cbHumidityThresholdAlarm.setChecked(enable == 1);
                                        }
                                        break;
                                    case KEY_HUMIDITY_THRESHOLD_ALARM:
                                        if (length > 0) {
                                            int min = value[4];
                                            int max = value[5];
                                            etHumidityThresholdAlarmMin.setText(String.valueOf(min));
                                            etHumidityThresholdAlarmMax.setText(String.valueOf(max));
                                        }
                                        break;
                                    case KEY_HUMIDITY_CHANGE_ALARM_ENABLE:
                                        if (length > 0) {
                                            int enable = value[4] & 0xFF;
                                            cbHumidityChangeAlarm.setChecked(enable == 1);
                                        }
                                        break;
                                    case KEY_HUMIDITY_CHANGE_ALARM_DURATION:
                                        if (length > 0) {
                                            int duration = value[4] & 0xFF;
                                            etHumidityDurationCondition.setText(String.valueOf(duration));
                                        }
                                        break;
                                    case KEY_HUMIDITY_CHANGE_ALARM_VALUE:
                                        if (length > 0) {
                                            int threshold = value[4] & 0xFF;
                                            etHumidityChangeThreshold.setText(String.valueOf(threshold));
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
        LoRaLW007MokoSupport.getInstance().disableTHNotify();
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
        final String rateStr = etSampleRate.getText().toString();
        if (TextUtils.isEmpty(rateStr))
            return false;
        final int rate = Integer.parseInt(rateStr);
        if (rate < 1 || rate > 60)
            return false;
        final String tempThresholdAlarmMinStr = etTempThresholdAlarmMin.getText().toString();
        if (TextUtils.isEmpty(tempThresholdAlarmMinStr))
            return false;
        final int tempThresholdAlarmMin = Integer.parseInt(tempThresholdAlarmMinStr);
        if (tempThresholdAlarmMin < -30 || tempThresholdAlarmMin > 60)
            return false;
        final String tempThresholdAlarmMaxStr = etTempThresholdAlarmMax.getText().toString();
        if (TextUtils.isEmpty(tempThresholdAlarmMaxStr))
            return false;
        final int tempThresholdAlarmMax = Integer.parseInt(tempThresholdAlarmMaxStr);
        if (tempThresholdAlarmMax < -30 || tempThresholdAlarmMax > 60)
            return false;
        if (tempThresholdAlarmMin >= tempThresholdAlarmMax)
            return false;
        final String tempDurationConditionStr = etTempDurationCondition.getText().toString();
        if (TextUtils.isEmpty(tempDurationConditionStr))
            return false;
        final int tempDurationCondition = Integer.parseInt(tempDurationConditionStr);
        if (tempDurationCondition < 1 || tempDurationCondition > 24)
            return false;
        final String tempChangeThresholdStr = etTempChangeThreshold.getText().toString();
        if (TextUtils.isEmpty(tempChangeThresholdStr))
            return false;
        final int tempChangeThreshold = Integer.parseInt(tempChangeThresholdStr);
        if (tempChangeThreshold < 1 || tempChangeThreshold > 20)
            return false;

        final String humidityThresholdAlarmMinStr = etHumidityThresholdAlarmMin.getText().toString();
        if (TextUtils.isEmpty(humidityThresholdAlarmMinStr))
            return false;
        final int humidityThresholdAlarmMin = Integer.parseInt(humidityThresholdAlarmMinStr);
        if (humidityThresholdAlarmMin > 100)
            return false;
        final String humidityThresholdAlarmMaxStr = etHumidityThresholdAlarmMax.getText().toString();
        if (TextUtils.isEmpty(humidityThresholdAlarmMaxStr))
            return false;
        final int humidityThresholdAlarmMax = Integer.parseInt(humidityThresholdAlarmMaxStr);
        if (humidityThresholdAlarmMax > 100)
            return false;
        if (humidityThresholdAlarmMin >= humidityThresholdAlarmMax)
            return false;
        final String humidityDurationConditionStr = etHumidityDurationCondition.getText().toString();
        if (TextUtils.isEmpty(humidityDurationConditionStr))
            return false;
        final int humidityDurationCondition = Integer.parseInt(humidityDurationConditionStr);
        if (humidityDurationCondition < 1 || humidityDurationCondition > 24)
            return false;
        final String humidityChangeThresholdStr = etHumidityChangeThreshold.getText().toString();
        if (TextUtils.isEmpty(humidityChangeThresholdStr))
            return false;
        final int humidityChangeThreshold = Integer.parseInt(humidityChangeThresholdStr);
        if (humidityChangeThreshold < 1 || humidityChangeThreshold > 100)
            return false;
        return true;

    }

    private void saveParams() {
        final String rateStr = etSampleRate.getText().toString();
        final int rate = Integer.parseInt(rateStr);
        final String tempThresholdAlarmMinStr = etTempThresholdAlarmMin.getText().toString();
        final int tempThresholdAlarmMin = Integer.parseInt(tempThresholdAlarmMinStr);
        final String tempThresholdAlarmMaxStr = etTempThresholdAlarmMax.getText().toString();
        final int tempThresholdAlarmMax = Integer.parseInt(tempThresholdAlarmMaxStr);
        final String tempDurationConditionStr = etTempDurationCondition.getText().toString();
        final int tempDurationCondition = Integer.parseInt(tempDurationConditionStr);
        final String tempChangeThresholdStr = etTempChangeThreshold.getText().toString();
        final int tempChangeThreshold = Integer.parseInt(tempChangeThresholdStr);
        final String humidityThresholdAlarmMinStr = etHumidityThresholdAlarmMin.getText().toString();
        final int humidityThresholdAlarmMin = Integer.parseInt(humidityThresholdAlarmMinStr);
        final String humidityThresholdAlarmMaxStr = etHumidityThresholdAlarmMax.getText().toString();
        final int humidityThresholdAlarmMax = Integer.parseInt(humidityThresholdAlarmMaxStr);
        final String humidityDurationConditionStr = etHumidityDurationCondition.getText().toString();
        final int humidityDurationCondition = Integer.parseInt(humidityDurationConditionStr);
        final String humidityChangeThresholdStr = etHumidityChangeThreshold.getText().toString();
        final int humidityChangeThreshold = Integer.parseInt(humidityChangeThresholdStr);

        savedParamsError = false;
        List<OrderTask> orderTasks = new ArrayList<>();
        orderTasks.add(OrderTaskAssembler.setTHSampleRate(rate));

        orderTasks.add(OrderTaskAssembler.setTempThresholdAlarmEnable(cbTempThresholdAlarm.isChecked() ? 1 : 0));
        orderTasks.add(OrderTaskAssembler.setTempThresholdAlarm(tempThresholdAlarmMin, tempThresholdAlarmMax));
        orderTasks.add(OrderTaskAssembler.setTempChangeAlarmEnable(cbTempChangeAlarm.isChecked() ? 1 : 0));
        orderTasks.add(OrderTaskAssembler.setTempChangeAlarmDuration(tempDurationCondition));
        orderTasks.add(OrderTaskAssembler.setTempChangeAlarmValue(tempChangeThreshold));

        orderTasks.add(OrderTaskAssembler.setHumidityThresholdAlarmEnable(cbHumidityThresholdAlarm.isChecked() ? 1 : 0));
        orderTasks.add(OrderTaskAssembler.setHumidityThresholdAlarm(humidityThresholdAlarmMin, humidityThresholdAlarmMax));
        orderTasks.add(OrderTaskAssembler.setHumidityChangeAlarmEnable(cbHumidityChangeAlarm.isChecked() ? 1 : 0));
        orderTasks.add(OrderTaskAssembler.setHumidityChangeAlarmDuration(humidityDurationCondition));
        orderTasks.add(OrderTaskAssembler.setHumidityChangeAlarmValue(humidityChangeThreshold));

        orderTasks.add(OrderTaskAssembler.setTHEnable(cbThEnable.isChecked() ? 1 : 0));

        if (cbThEnable.isChecked()) {
            orderTasks.add(OrderTaskAssembler.getTHData());
        }
        LoRaLW007MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }
}
