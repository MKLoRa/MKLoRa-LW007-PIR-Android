package com.moko.lw007.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.moko.ble.lib.task.OrderTask;
import com.moko.lw007.R;
import com.moko.lw007.R2;
import com.moko.lw007.activity.DeviceInfoActivity;
import com.moko.lw007.entity.TxPowerEnum;
import com.moko.support.lw007.LoRaLW007MokoSupport;
import com.moko.support.lw007.OrderTaskAssembler;

import java.util.ArrayList;
import java.util.List;

import androidx.constraintlayout.widget.ConstraintLayout;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BleFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {
    private static final String TAG = BleFragment.class.getSimpleName();
    private final String FILTER_ASCII = "[ -~]*";

    @BindView(R2.id.et_adv_name)
    EditText etAdvName;
    @BindView(R2.id.et_adv_interval)
    EditText etAdvInterval;
    @BindView(R2.id.iv_connectable)
    ImageView ivConnectable;
    @BindView(R2.id.iv_login_mode)
    ImageView ivLoginMode;
    @BindView(R2.id.tv_change_password)
    TextView tvChangePassword;
    @BindView(R2.id.sb_tx_power)
    SeekBar sbTxPower;
    @BindView(R2.id.tv_tx_power_value)
    TextView tvTxPowerValue;
    @BindView(R2.id.cl_beacon_mode_open)
    ConstraintLayout clBeaconModeOpen;
    @BindView(R2.id.et_adv_timeout)
    EditText etAdvTimeout;
    @BindView(R2.id.cl_beacon_mode_close)
    ConstraintLayout clBeaconModeClose;
    @BindView(R2.id.cb_beacon_mode)
    CheckBox cbBeaconMode;

    //    private boolean mOfflineFixEnable;
    private DeviceInfoActivity activity;

    public BleFragment() {
    }


    public static BleFragment newInstance() {
        BleFragment fragment = new BleFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.lw007_fragment_ble, container, false);
        ButterKnife.bind(this, view);
        activity = (DeviceInfoActivity) getActivity();
        sbTxPower.setOnSeekBarChangeListener(this);
        InputFilter inputFilter = (source, start, end, dest, dstart, dend) -> {
            if (!(source + "").matches(FILTER_ASCII)) {
                return "";
            }

            return null;
        };
        etAdvName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16), inputFilter});
        cbBeaconMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                clBeaconModeOpen.setVisibility(View.VISIBLE);
                clBeaconModeClose.setVisibility(View.GONE);
            } else {
                clBeaconModeOpen.setVisibility(View.GONE);
                clBeaconModeClose.setVisibility(View.VISIBLE);
            }
        });
        return view;
    }


    public void setBleEnable(int enable) {
        cbBeaconMode.setChecked(enable == 1);
        if (enable == 1) {
            clBeaconModeOpen.setVisibility(View.VISIBLE);
            clBeaconModeClose.setVisibility(View.GONE);
        } else {
            clBeaconModeOpen.setVisibility(View.GONE);
            clBeaconModeClose.setVisibility(View.VISIBLE);
        }
    }

    public void setBleAdvName(String advName) {
        etAdvName.setText(advName);
    }

    public void setBleAdvInterval(int advInterval) {
        etAdvInterval.setText(String.valueOf(advInterval));
    }

    public void setBleTimeoutDuration(int timeout) {
        etAdvTimeout.setText(String.valueOf(timeout));
    }

    private boolean isConnectable;

    public void setBleConnectable(int connectable) {
        isConnectable = connectable == 1;
        ivConnectable.setImageResource(isConnectable ? R.drawable.lw007_ic_checked : R.drawable.lw007_ic_unchecked);
    }

    private boolean isLoginMode;

    public void setBleLoginMode(int loginMode) {
        isLoginMode = loginMode == 1;
        ivLoginMode.setImageResource(isLoginMode ? R.drawable.lw007_ic_checked : R.drawable.lw007_ic_unchecked);
        tvChangePassword.setVisibility(isLoginMode ? View.VISIBLE : View.GONE);
    }

    public void setBleTxPower(int txPower) {
        TxPowerEnum txPowerEnum = TxPowerEnum.fromTxPower(txPower);
        int progress = txPowerEnum.ordinal();
        sbTxPower.setProgress(progress);
    }


    public void changeConnectable() {
        isConnectable = !isConnectable;
        ivConnectable.setImageResource(isConnectable ? R.drawable.lw007_ic_checked : R.drawable.lw007_ic_unchecked);
    }

    public void changeLoginMode() {
        isLoginMode = !isLoginMode;
        ivLoginMode.setImageResource(isLoginMode ? R.drawable.lw007_ic_checked : R.drawable.lw007_ic_unchecked);
        tvChangePassword.setVisibility(isLoginMode ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        TxPowerEnum txPowerEnum = TxPowerEnum.fromOrdinal(progress);
        if (txPowerEnum == null)
            return;
        int txPower = txPowerEnum.getTxPower();
        tvTxPowerValue.setText(String.format("%ddBm", txPower));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public boolean isValid() {
        final String advIntervalStr = etAdvInterval.getText().toString();
        if (!cbBeaconMode.isChecked()) {
            final String advTimeoutStr = etAdvTimeout.getText().toString();
            if (TextUtils.isEmpty(advTimeoutStr))
                return false;
            final int timeout = Integer.parseInt(advTimeoutStr);
            if (timeout < 1 || timeout > 60) {
                return false;
            }
        }
        if (TextUtils.isEmpty(advIntervalStr))
            return false;
        final int interval = Integer.parseInt(advIntervalStr);
        if (interval < 1 || interval > 100) {
            return false;
        }
        return true;
    }

    public void saveParams() {
        final String advNameStr = etAdvName.getText().toString();
        final String advIntervalStr = etAdvInterval.getText().toString();
        final int interval = Integer.parseInt(advIntervalStr);
        List<OrderTask> orderTasks = new ArrayList<>();
        orderTasks.add(OrderTaskAssembler.setBleEnable(cbBeaconMode.isChecked() ? 1 : 0));
        if (cbBeaconMode.isChecked()) {
            orderTasks.add(OrderTaskAssembler.setBleConnectable(isConnectable ? 1 : 0));
        } else {
            final String advTimeoutStr = etAdvTimeout.getText().toString();
            final int timeout = Integer.parseInt(advTimeoutStr);
            orderTasks.add(OrderTaskAssembler.setBleTimeoutDuration(timeout));
        }
        orderTasks.add(OrderTaskAssembler.setBleAdvName(advNameStr));
        orderTasks.add(OrderTaskAssembler.setBleAdvInterval(interval));
        int progress = sbTxPower.getProgress();
        TxPowerEnum txPowerEnum = TxPowerEnum.fromOrdinal(progress);
        orderTasks.add(OrderTaskAssembler.setBleTxPower(txPowerEnum.getTxPower()));
        orderTasks.add(OrderTaskAssembler.setBleLoginMode(isLoginMode ? 1 : 0));
        LoRaLW007MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }
}
