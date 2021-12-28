package com.moko.lw007.activity;


import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moko.ble.lib.MokoConstants;
import com.moko.ble.lib.event.ConnectStatusEvent;
import com.moko.ble.lib.event.OrderTaskResponseEvent;
import com.moko.ble.lib.task.OrderTask;
import com.moko.ble.lib.task.OrderTaskResponse;
import com.moko.ble.lib.utils.MokoUtils;
import com.moko.lw007.AppConstants;
import com.moko.lw007.R;
import com.moko.lw007.R2;
import com.moko.lw007.dialog.AlertMessageDialog;
import com.moko.lw007.dialog.BottomDialog;
import com.moko.lw007.dialog.LoadingMessageDialog;
import com.moko.lw007.utils.ToastUtils;
import com.moko.support.lw007.LoRaLW007MokoSupport;
import com.moko.support.lw007.OrderTaskAssembler;
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

public class PowerIndicatorColorActivity extends BaseActivity {


    @BindView(R2.id.tv_power_indicator_color_type)
    TextView tvPowerIndicatorColorType;
    @BindView(R2.id.et_blue)
    EditText etBlue;
    @BindView(R2.id.et_green)
    EditText etGreen;
    @BindView(R2.id.et_yellow)
    EditText etYellow;
    @BindView(R2.id.et_orange)
    EditText etOrange;
    @BindView(R2.id.et_red)
    EditText etRed;
    @BindView(R2.id.et_purple)
    EditText etPurple;
    @BindView(R2.id.ll_color_settings)
    LinearLayout llColorSettings;
    private boolean mReceiverTag = false;
    private boolean savedParamsError;
    private int deviceSpecification;
    private ArrayList<String> mValues;
    private int mSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lw007_activity_power_indicator_color_settings);
        ButterKnife.bind(this);
        deviceSpecification = getIntent().getIntExtra(AppConstants.EXTRA_KEY_DEVICE_SPECIFICATION, 0);
        mValues = new ArrayList<>();
        mValues.add("Active power indicator with color direct transition");
        mValues.add("Active power indicator with color smooth transition");
        mValues.add("White");
        mValues.add("Red");
        mValues.add("Green");
        mValues.add("Blue");
        mValues.add("Orange");
        mValues.add("Cyan");
        mValues.add("Purple");
        EventBus.getDefault().register(this);
        // 注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        mReceiverTag = true;
        showSyncingProgressDialog();
        List<OrderTask> orderTasks = new ArrayList<>();
        orderTasks.add(OrderTaskAssembler.getPowerIndicatorColor());
        LoRaLW007MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
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
                            if (configKeyEnum == null) {
                                return;
                            }
                            int length = value[3] & 0xFF;
                            if (flag == 0x01) {
                                // write
                                int result = value[4] & 0xFF;
                                switch (configKeyEnum) {
                                    case KEY_POWER_INDICATOR_COLOR:
                                        if (result != 1) {
                                            savedParamsError = true;
                                        }
                                        if (savedParamsError) {
                                            ToastUtils.showToast(PowerIndicatorColorActivity.this, "Opps！Save failed. Please check the input characters and try again.");
                                        } else {
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
                                    case KEY_POWER_INDICATOR_COLOR:
                                        if (length > 0) {
                                            mSelected = value[4] & 0xFF;
                                            tvPowerIndicatorColorType.setText(mValues.get(mSelected));
                                            if (mSelected > 1) {
                                                llColorSettings.setVisibility(View.GONE);
                                            } else {
                                                llColorSettings.setVisibility(View.VISIBLE);
                                            }
                                            byte[] blueBytes = Arrays.copyOfRange(value, 5, 7);
                                            int blue = MokoUtils.toInt(blueBytes);
                                            etBlue.setText(String.valueOf(blue));
                                            byte[] greenBytes = Arrays.copyOfRange(value, 7, 9);
                                            int green = MokoUtils.toInt(greenBytes);
                                            etGreen.setText(String.valueOf(green));
                                            byte[] yellowBytes = Arrays.copyOfRange(value, 9, 11);
                                            int yellow = MokoUtils.toInt(yellowBytes);
                                            etYellow.setText(String.valueOf(yellow));
                                            byte[] orangeBytes = Arrays.copyOfRange(value, 11, 13);
                                            int orange = MokoUtils.toInt(orangeBytes);
                                            etOrange.setText(String.valueOf(orange));
                                            byte[] redBytes = Arrays.copyOfRange(value, 13, 15);
                                            int red = MokoUtils.toInt(redBytes);
                                            etRed.setText(String.valueOf(red));
                                            byte[] purpleBytes = Arrays.copyOfRange(value, 15, 17);
                                            int purple = MokoUtils.toInt(purpleBytes);
                                            etPurple.setText(String.valueOf(purple));
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


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent != null) {
                String action = intent.getAction();
                if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blueState) {
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            dismissSyncProgressDialog();
                            finish();
                            break;
                    }
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiverTag) {
            mReceiverTag = false;
            // 注销广播
            unregisterReceiver(mReceiver);
        }
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
        setResult(RESULT_OK);
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
        final String blue = etBlue.getText().toString();
        final String green = etGreen.getText().toString();
        final String yellow = etYellow.getText().toString();
        final String orange = etOrange.getText().toString();
        final String red = etRed.getText().toString();
        final String purple = etPurple.getText().toString();
        if (TextUtils.isEmpty(blue) || TextUtils.isEmpty(green) || TextUtils.isEmpty(yellow)
                || TextUtils.isEmpty(orange) || TextUtils.isEmpty(red) || TextUtils.isEmpty(purple)) {
            return false;
        }
        int max = 4416;
        if (deviceSpecification == 1) {
            max = 2160;
        } else if (deviceSpecification == 2) {
            max = 3588;
        }
        final int blueValue = Integer.parseInt(blue);
        if (blueValue < 2 || blueValue >= (max - 5)) {
            return false;
        }

        final int greenValue = Integer.parseInt(green);
        if (greenValue <= blueValue || greenValue > (max - 4)) {
            return false;
        }

        final int yellowValue = Integer.parseInt(yellow);
        if (yellowValue <= greenValue || yellowValue > (max - 3)) {
            return false;
        }

        final int orangeValue = Integer.parseInt(orange);
        if (orangeValue <= yellowValue || orangeValue > (max - 2)) {
            return false;
        }

        final int redValue = Integer.parseInt(red);
        if (redValue <= orangeValue || redValue > (max - 1)) {
            return false;
        }

        final int purpleValue = Integer.parseInt(purple);
        if (purpleValue <= redValue || purpleValue > max) {
            return false;
        }
        return true;

    }

    private void saveParams() {

        final String blue = etBlue.getText().toString();
        final String green = etGreen.getText().toString();
        final String yellow = etYellow.getText().toString();
        final String orange = etOrange.getText().toString();
        final String red = etRed.getText().toString();
        final String purple = etPurple.getText().toString();
        final int blueValue = Integer.parseInt(blue);
        final int greenValue = Integer.parseInt(green);
        final int yellowValue = Integer.parseInt(yellow);
        final int orangeValue = Integer.parseInt(orange);
        final int redValue = Integer.parseInt(red);
        final int purpleValue = Integer.parseInt(purple);
        savedParamsError = false;
        List<OrderTask> orderTasks = new ArrayList<>();
        orderTasks.add(OrderTaskAssembler.setPowerIndicatorColor(mSelected,
                blueValue, greenValue, yellowValue,
                orangeValue, redValue, purpleValue));
        LoRaLW007MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }

    public void selectPowerIndicatorColorType(View view) {
        if (isWindowLocked())
            return;
        BottomDialog dialog = new BottomDialog();
        dialog.setDatas(mValues, mSelected);
        dialog.setListener(value -> {
            mSelected = value;
            tvPowerIndicatorColorType.setText(mValues.get(value));
            if (value > 1) {
                llColorSettings.setVisibility(View.GONE);
            } else {
                llColorSettings.setVisibility(View.VISIBLE);
            }
        });
        dialog.show(getSupportFragmentManager());
    }
}
