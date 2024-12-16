package com.moko.lw007.activity;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.moko.ble.lib.MokoConstants;
import com.moko.ble.lib.event.ConnectStatusEvent;
import com.moko.ble.lib.event.OrderTaskResponseEvent;
import com.moko.ble.lib.task.OrderTask;
import com.moko.ble.lib.task.OrderTaskResponse;
import com.moko.ble.lib.utils.MokoUtils;
import com.moko.lw007.databinding.Lw007ActivityThSettingsBinding;
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

public class THSettingsActivity extends BaseActivity {


    private Lw007ActivityThSettingsBinding mBind;
    private boolean savedParamsError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBind = Lw007ActivityThSettingsBinding.inflate(getLayoutInflater());
        setContentView(mBind.getRoot());
        EventBus.getDefault().register(this);

        LoRaLW007MokoSupport.getInstance().enableTHNotify();
        showSyncingProgressDialog();
        mBind.cbThEnable.postDelayed(() -> {
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
                                mBind.tvTempValue.setVisibility(View.GONE);
                                mBind.tvHumidityValue.setVisibility(View.GONE);
                            } else {
                                mBind.tvTempValue.setVisibility(View.VISIBLE);
                                mBind.tvHumidityValue.setVisibility(View.VISIBLE);
                                mBind.tvTempValue.setText(String.format("%s ℃", MokoUtils.getDecimalFormat("0.0").format(temp * 0.1f - 30)));
                                mBind.tvHumidityValue.setText(String.format("%s%%RH", MokoUtils.getDecimalFormat("0.0").format(humidity * 0.1f)));
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
                                                mBind.tvTempValue.setVisibility(View.GONE);
                                                mBind.tvHumidityValue.setVisibility(View.GONE);
                                            } else {
                                                mBind.tvTempValue.setVisibility(View.VISIBLE);
                                                mBind.tvHumidityValue.setVisibility(View.VISIBLE);
                                                mBind.tvTempValue.setText(String.format("%s ℃", MokoUtils.getDecimalFormat("0.0").format(temp * 0.1f - 30)));
                                                mBind.tvHumidityValue.setText(String.format("%s%%RH", MokoUtils.getDecimalFormat("0.0").format(humidity * 0.1f)));
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
                                            mBind.tvTempValue.setVisibility(mBind.cbThEnable.isChecked() ? View.VISIBLE : View.GONE);
                                            mBind.tvHumidityValue.setVisibility(mBind.cbThEnable.isChecked() ? View.VISIBLE : View.GONE);
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
                                            mBind.tvTempValue.setVisibility(enable == 0 ? View.GONE : View.VISIBLE);
                                            mBind.tvHumidityValue.setVisibility(enable == 0 ? View.GONE : View.VISIBLE);
                                            mBind.cbThEnable.setChecked(enable == 1);
                                        }
                                        break;
                                    case KEY_TH_SAMPLE_RATE:
                                        if (length > 0) {
                                            int interval = value[4] & 0xFF;
                                            mBind.etSampleRate.setText(String.valueOf(interval));
                                        }
                                        break;
                                    case KEY_TEMP_THRESHOLD_ALARM_ENABLE:
                                        if (length > 0) {
                                            int enable = value[4] & 0xFF;
                                            mBind.cbTempThresholdAlarm.setChecked(enable == 1);
                                        }
                                        break;
                                    case KEY_TEMP_THRESHOLD_ALARM:
                                        if (length > 0) {
                                            int min = value[4];
                                            int max = value[5];
                                            mBind.etTempThresholdAlarmMin.setText(String.valueOf(min));
                                            mBind.etTempThresholdAlarmMax.setText(String.valueOf(max));
                                        }
                                        break;
                                    case KEY_TEMP_CHANGE_ALARM_ENABLE:
                                        if (length > 0) {
                                            int enable = value[4] & 0xFF;
                                            mBind.cbTempChangeAlarm.setChecked(enable == 1);
                                        }
                                        break;
                                    case KEY_TEMP_CHANGE_ALARM_DURATION:
                                        if (length > 0) {
                                            int duration = value[4] & 0xFF;
                                            mBind.etTempDurationCondition.setText(String.valueOf(duration));
                                        }
                                        break;
                                    case KEY_TEMP_CHANGE_ALARM_VALUE:
                                        if (length > 0) {
                                            int threshold = value[4] & 0xFF;
                                            mBind.etTempChangeThreshold.setText(String.valueOf(threshold));
                                        }
                                        break;
                                    case KEY_HUMIDITY_THRESHOLD_ALARM_ENABLE:
                                        if (length > 0) {
                                            int enable = value[4] & 0xFF;
                                            mBind.cbHumidityThresholdAlarm.setChecked(enable == 1);
                                        }
                                        break;
                                    case KEY_HUMIDITY_THRESHOLD_ALARM:
                                        if (length > 0) {
                                            int min = value[4];
                                            int max = value[5];
                                            mBind.etHumidityThresholdAlarmMin.setText(String.valueOf(min));
                                            mBind.etHumidityThresholdAlarmMax.setText(String.valueOf(max));
                                        }
                                        break;
                                    case KEY_HUMIDITY_CHANGE_ALARM_ENABLE:
                                        if (length > 0) {
                                            int enable = value[4] & 0xFF;
                                            mBind.cbHumidityChangeAlarm.setChecked(enable == 1);
                                        }
                                        break;
                                    case KEY_HUMIDITY_CHANGE_ALARM_DURATION:
                                        if (length > 0) {
                                            int duration = value[4] & 0xFF;
                                            mBind.etHumidityDurationCondition.setText(String.valueOf(duration));
                                        }
                                        break;
                                    case KEY_HUMIDITY_CHANGE_ALARM_VALUE:
                                        if (length > 0) {
                                            int threshold = value[4] & 0xFF;
                                            mBind.etHumidityChangeThreshold.setText(String.valueOf(threshold));
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
        final String rateStr = mBind.etSampleRate.getText().toString();
        if (TextUtils.isEmpty(rateStr))
            return false;
        final int rate = Integer.parseInt(rateStr);
        if (rate < 1 || rate > 60)
            return false;
        final String tempThresholdAlarmMinStr = mBind.etTempThresholdAlarmMin.getText().toString();
        if (TextUtils.isEmpty(tempThresholdAlarmMinStr))
            return false;
        final int tempThresholdAlarmMin = Integer.parseInt(tempThresholdAlarmMinStr);
        if (tempThresholdAlarmMin < -30 || tempThresholdAlarmMin > 60)
            return false;
        final String tempThresholdAlarmMaxStr = mBind.etTempThresholdAlarmMax.getText().toString();
        if (TextUtils.isEmpty(tempThresholdAlarmMaxStr))
            return false;
        final int tempThresholdAlarmMax = Integer.parseInt(tempThresholdAlarmMaxStr);
        if (tempThresholdAlarmMax < -30 || tempThresholdAlarmMax > 60)
            return false;
        if (tempThresholdAlarmMin >= tempThresholdAlarmMax)
            return false;
        final String tempDurationConditionStr = mBind.etTempDurationCondition.getText().toString();
        if (TextUtils.isEmpty(tempDurationConditionStr))
            return false;
        final int tempDurationCondition = Integer.parseInt(tempDurationConditionStr);
        if (tempDurationCondition < 1 || tempDurationCondition > 24)
            return false;
        final String tempChangeThresholdStr = mBind.etTempChangeThreshold.getText().toString();
        if (TextUtils.isEmpty(tempChangeThresholdStr))
            return false;
        final int tempChangeThreshold = Integer.parseInt(tempChangeThresholdStr);
        if (tempChangeThreshold < 1 || tempChangeThreshold > 20)
            return false;

        final String humidityThresholdAlarmMinStr = mBind.etHumidityThresholdAlarmMin.getText().toString();
        if (TextUtils.isEmpty(humidityThresholdAlarmMinStr))
            return false;
        final int humidityThresholdAlarmMin = Integer.parseInt(humidityThresholdAlarmMinStr);
        if (humidityThresholdAlarmMin > 100)
            return false;
        final String humidityThresholdAlarmMaxStr = mBind.etHumidityThresholdAlarmMax.getText().toString();
        if (TextUtils.isEmpty(humidityThresholdAlarmMaxStr))
            return false;
        final int humidityThresholdAlarmMax = Integer.parseInt(humidityThresholdAlarmMaxStr);
        if (humidityThresholdAlarmMax > 100)
            return false;
        if (humidityThresholdAlarmMin >= humidityThresholdAlarmMax)
            return false;
        final String humidityDurationConditionStr = mBind.etHumidityDurationCondition.getText().toString();
        if (TextUtils.isEmpty(humidityDurationConditionStr))
            return false;
        final int humidityDurationCondition = Integer.parseInt(humidityDurationConditionStr);
        if (humidityDurationCondition < 1 || humidityDurationCondition > 24)
            return false;
        final String humidityChangeThresholdStr = mBind.etHumidityChangeThreshold.getText().toString();
        if (TextUtils.isEmpty(humidityChangeThresholdStr))
            return false;
        final int humidityChangeThreshold = Integer.parseInt(humidityChangeThresholdStr);
        if (humidityChangeThreshold < 1 || humidityChangeThreshold > 100)
            return false;
        return true;

    }

    private void saveParams() {
        final String rateStr = mBind.etSampleRate.getText().toString();
        final int rate = Integer.parseInt(rateStr);
        final String tempThresholdAlarmMinStr = mBind.etTempThresholdAlarmMin.getText().toString();
        final int tempThresholdAlarmMin = Integer.parseInt(tempThresholdAlarmMinStr);
        final String tempThresholdAlarmMaxStr = mBind.etTempThresholdAlarmMax.getText().toString();
        final int tempThresholdAlarmMax = Integer.parseInt(tempThresholdAlarmMaxStr);
        final String tempDurationConditionStr = mBind.etTempDurationCondition.getText().toString();
        final int tempDurationCondition = Integer.parseInt(tempDurationConditionStr);
        final String tempChangeThresholdStr = mBind.etTempChangeThreshold.getText().toString();
        final int tempChangeThreshold = Integer.parseInt(tempChangeThresholdStr);
        final String humidityThresholdAlarmMinStr = mBind.etHumidityThresholdAlarmMin.getText().toString();
        final int humidityThresholdAlarmMin = Integer.parseInt(humidityThresholdAlarmMinStr);
        final String humidityThresholdAlarmMaxStr = mBind.etHumidityThresholdAlarmMax.getText().toString();
        final int humidityThresholdAlarmMax = Integer.parseInt(humidityThresholdAlarmMaxStr);
        final String humidityDurationConditionStr = mBind.etHumidityDurationCondition.getText().toString();
        final int humidityDurationCondition = Integer.parseInt(humidityDurationConditionStr);
        final String humidityChangeThresholdStr = mBind.etHumidityChangeThreshold.getText().toString();
        final int humidityChangeThreshold = Integer.parseInt(humidityChangeThresholdStr);

        savedParamsError = false;
        List<OrderTask> orderTasks = new ArrayList<>();
        orderTasks.add(OrderTaskAssembler.setTHSampleRate(rate));

        orderTasks.add(OrderTaskAssembler.setTempThresholdAlarmEnable(mBind.cbTempThresholdAlarm.isChecked() ? 1 : 0));
        orderTasks.add(OrderTaskAssembler.setTempThresholdAlarm(tempThresholdAlarmMin, tempThresholdAlarmMax));
        orderTasks.add(OrderTaskAssembler.setTempChangeAlarmEnable(mBind.cbTempChangeAlarm.isChecked() ? 1 : 0));
        orderTasks.add(OrderTaskAssembler.setTempChangeAlarmDuration(tempDurationCondition));
        orderTasks.add(OrderTaskAssembler.setTempChangeAlarmValue(tempChangeThreshold));

        orderTasks.add(OrderTaskAssembler.setHumidityThresholdAlarmEnable(mBind.cbHumidityThresholdAlarm.isChecked() ? 1 : 0));
        orderTasks.add(OrderTaskAssembler.setHumidityThresholdAlarm(humidityThresholdAlarmMin, humidityThresholdAlarmMax));
        orderTasks.add(OrderTaskAssembler.setHumidityChangeAlarmEnable(mBind.cbHumidityChangeAlarm.isChecked() ? 1 : 0));
        orderTasks.add(OrderTaskAssembler.setHumidityChangeAlarmDuration(humidityDurationCondition));
        orderTasks.add(OrderTaskAssembler.setHumidityChangeAlarmValue(humidityChangeThreshold));

        orderTasks.add(OrderTaskAssembler.setTHEnable(mBind.cbThEnable.isChecked() ? 1 : 0));

        if (mBind.cbThEnable.isChecked()) {
            orderTasks.add(OrderTaskAssembler.getTHData());
        }
        LoRaLW007MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }
}
