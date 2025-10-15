package com.moko.lw007.activity;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.moko.ble.lib.MokoConstants;
import com.moko.ble.lib.event.ConnectStatusEvent;
import com.moko.ble.lib.event.OrderTaskResponseEvent;
import com.moko.ble.lib.task.OrderTask;
import com.moko.ble.lib.task.OrderTaskResponse;
import com.moko.ble.lib.utils.MokoUtils;
import com.moko.lib.loraui.dialog.BottomDialog;
import com.moko.lib.loraui.utils.ToastUtils;
import com.moko.lw007.databinding.Lw007ActivitySelftestNewBinding;
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

public class SelfTestNewActivity extends BaseActivity {

    private Lw007ActivitySelftestNewBinding mBind;
    private ArrayList<String> mValues;
    private boolean savedParamsError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBind = Lw007ActivitySelftestNewBinding.inflate(getLayoutInflater());
        setContentView(mBind.getRoot());
        mValues = new ArrayList<>();
        for (int i = 44; i <= 64; i++) {
            mValues.add(MokoUtils.getDecimalFormat("0.##").format(i * 0.05f));
        }
        mBind.tvCondition1VoltageThreshold.setOnClickListener(v -> {
            showThresholdDialog(v);
        });
        EventBus.getDefault().register(this);
        showSyncingProgressDialog();
        mBind.tvBack.postDelayed(() -> {
            List<OrderTask> orderTasks = new ArrayList<>();
            orderTasks.add(OrderTaskAssembler.getSelfTestStatus());
            orderTasks.add(OrderTaskAssembler.getPCBAStatus());
            orderTasks.add(OrderTaskAssembler.getCondition1VoltageThreshold());
            orderTasks.add(OrderTaskAssembler.getCondition1MinSampleInterval());
            orderTasks.add(OrderTaskAssembler.getCondition1SampleTimes());
            LoRaLW007MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
        }, 500);
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 300)
    public void onConnectStatusEvent(ConnectStatusEvent event) {
        final String action = event.getAction();
        runOnUiThread(() -> {
            if (MokoConstants.ACTION_DISCONNECTED.equals(action)) {
                finish();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 300)
    public void onOrderTaskResponseEvent(OrderTaskResponseEvent event) {
        final String action = event.getAction();
        if (!MokoConstants.ACTION_CURRENT_DATA.equals(action))
            EventBus.getDefault().cancelEventDelivery(event);
        runOnUiThread(() -> {
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
                    case CHAR_PARAMS:
                        if (value.length >= 4) {
                            int header = value[0] & 0xFF;// 0xED
                            int flag = value[1] & 0xFF;// read or write
                            int cmd = value[2] & 0xFF;
                            if (header != 0xED)
                                return;
                            ParamsKeyEnum configKeyEnum = ParamsKeyEnum.fromParamKey(cmd);
                            if (configKeyEnum == null) return;
                            int length = value[3] & 0xFF;
                            if (flag == 0x01) {
                                // write
                                int result = value[4] & 0xFF;
                                switch (configKeyEnum) {
                                    case KEY_CONDITION_1_VOLTAGE_THRESHOLD:
                                    case KEY_CONDITION_1_MIN_SAMPLE_INTERVAL:
                                        if (result != 1)
                                            savedParamsError = true;
                                        break;
                                    case KEY_CONDITION_1_SAMPLE_TIMES:
                                        if (result != 1) {
                                            savedParamsError = true;
                                        }
                                        if (savedParamsError)
                                            ToastUtils.showToast(this, "Opps！Save failed. Please check the input characters and try again.");
                                        else
                                            ToastUtils.showToast(this, "Save Successfully！");
                                        break;
                                }
                            }
                            if (flag == 0x00) {
                                // read
                                switch (configKeyEnum) {
                                    case KEY_CONDITION_1_VOLTAGE_THRESHOLD:
                                        int thresholdNon = value[4] & 0xFF;
                                        int selectedNon = thresholdNon - 44;
                                        mBind.tvCondition1VoltageThreshold.setText(mValues.get(selectedNon));
                                        mBind.tvCondition1VoltageThreshold.setTag(selectedNon);
                                        break;
                                    case KEY_CONDITION_1_MIN_SAMPLE_INTERVAL:
                                        int intervalNon = MokoUtils.toInt(Arrays.copyOfRange(value, 4, 4 + length));
                                        mBind.etCondition1MinSampleInterval.setText(String.valueOf(intervalNon));
                                        break;
                                    case KEY_CONDITION_1_SAMPLE_TIMES:
                                        int timesNon = value[4] & 0xFF;
                                        mBind.etCondition1SampleTimes.setText(String.valueOf(timesNon));
                                        break;
                                }
                            }
                        }
                    case CHAR_CONTROL:
                        if (value.length >= 4) {
                            int header = value[0] & 0xFF;// 0xED
                            int flag = value[1] & 0xFF;// read or write
                            int cmd = value[2] & 0xFF;
                            if (header != 0xED)
                                return;
                            ControlKeyEnum configKeyEnum = ControlKeyEnum.fromParamKey(cmd);
                            if (configKeyEnum == null) {
                                return;
                            }
                            int length = value[3] & 0xFF;
                            if (flag == 0x00) {
                                // read
                                switch (configKeyEnum) {
                                    case KEY_PCBA_STATUS:
                                        if (length > 0) {
                                            mBind.tvPcbaStatus.setText(String.valueOf(value[4] & 0xFF));
                                        }
                                        break;
                                    case KEY_SELFTEST_STATUS:
                                        int status = value[5] & 0xFF;
                                        mBind.tvSelftestStatus.setVisibility(status == 0 ? View.VISIBLE : View.GONE);
                                        if ((status & 0x01) == 0x01)
                                            mBind.tvThStatus.setVisibility(View.VISIBLE);
                                        break;

                                }
                            }
                        }
                        break;
                }
            }
        });
    }

    private void showThresholdDialog(View v) {
        if (isWindowLocked()) return;
        int selected = (int) v.getTag();
        BottomDialog dialog = new BottomDialog();
        dialog.setDatas(mValues, selected);
        dialog.setListener(value -> {
            ((TextView) v).setText(mValues.get(value));
            v.setTag(value);
        });
        dialog.show(getSupportFragmentManager());
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
        finish();
    }

    public void onSave(View view) {
        if (isValid()) {
            showSyncingProgressDialog();
            saveParams();
        } else {
            ToastUtils.showToast(this, "Para error!");
        }
    }

    private boolean isValid() {
        if (TextUtils.isEmpty(mBind.etCondition1MinSampleInterval.getText())) return false;
        String intervalNonStr = mBind.etCondition1MinSampleInterval.getText().toString();
        int intervalNon = Integer.parseInt(intervalNonStr);
        if (intervalNon < 1 || intervalNon > 1440)
            return false;
        if (TextUtils.isEmpty(mBind.etCondition1SampleTimes.getText())) return false;
        String timesNonStr = mBind.etCondition1SampleTimes.getText().toString();
        int timesNon = Integer.parseInt(timesNonStr);
        if (timesNon < 1 || timesNon > 100)
            return false;
        return true;
    }

    private void saveParams() {
        savedParamsError = false;
        int thresholdNon = (int) mBind.tvCondition1VoltageThreshold.getTag() + 44;
        int intervalNon = Integer.parseInt(mBind.etCondition1MinSampleInterval.getText().toString());
        int timesNon = Integer.parseInt(mBind.etCondition1SampleTimes.getText().toString());

        List<OrderTask> orderTasks = new ArrayList<>();
        orderTasks.add(OrderTaskAssembler.setCondition1VoltageThreshold(thresholdNon));
        orderTasks.add(OrderTaskAssembler.setCondition1MinSampleInterval(intervalNon));
        orderTasks.add(OrderTaskAssembler.setCondition1SampleTimes(timesNon));
        LoRaLW007MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }
}
