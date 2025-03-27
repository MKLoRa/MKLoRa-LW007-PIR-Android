package com.moko.lw007.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.moko.ble.lib.task.OrderTask;
import com.moko.lw007.R;
import com.moko.lw007.activity.DeviceInfoActivity;
import com.moko.lw007.databinding.Lw007FragmentBleBinding;
import com.moko.lw007.entity.TxPowerEnum;
import com.moko.support.lw007.LoRaLW007MokoSupport;
import com.moko.support.lw007.OrderTaskAssembler;

import java.util.ArrayList;
import java.util.List;

public class BleFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {
    private static final String TAG = BleFragment.class.getSimpleName();
    private final String FILTER_ASCII = "[ -~]*";
    private Lw007FragmentBleBinding mBind;

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
        mBind = Lw007FragmentBleBinding.inflate(inflater, container, false);
        activity = (DeviceInfoActivity) getActivity();
        mBind.sbTxPower.setOnSeekBarChangeListener(this);
        InputFilter inputFilter = (source, start, end, dest, dstart, dend) -> {
            if (!(source + "").matches(FILTER_ASCII)) {
                return "";
            }

            return null;
        };
        mBind.etAdvName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16), inputFilter});
        mBind.cbBeaconMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mBind.clBeaconModeOpen.setVisibility(View.VISIBLE);
                mBind.clBeaconModeClose.setVisibility(View.GONE);
            } else {
                mBind.clBeaconModeOpen.setVisibility(View.GONE);
                mBind.clBeaconModeClose.setVisibility(View.VISIBLE);
            }
        });
        return mBind.getRoot();
    }


    public void setBleEnable(int enable) {
        mBind.cbBeaconMode.setChecked(enable == 1);
        if (enable == 1) {
            mBind.clBeaconModeOpen.setVisibility(View.VISIBLE);
            mBind.clBeaconModeClose.setVisibility(View.GONE);
        } else {
            mBind.clBeaconModeOpen.setVisibility(View.GONE);
            mBind.clBeaconModeClose.setVisibility(View.VISIBLE);
        }
    }

    public void setBleAdvName(String advName) {
        mBind.etAdvName.setText(advName);
    }

    public void setBleAdvInterval(int advInterval) {
        mBind.etAdvInterval.setText(String.valueOf(advInterval));
    }

    public void setBleTimeoutDuration(int timeout) {
        mBind.etAdvTimeout.setText(String.valueOf(timeout));
    }

    private boolean isConnectable;

    public void setBleConnectable(int connectable) {
        isConnectable = connectable == 1;
        mBind.ivConnectable.setImageResource(isConnectable ? R.drawable.ic_checked : R.drawable.ic_unchecked);
    }

    private boolean isLoginMode;

    public void setBleLoginMode(int loginMode) {
        isLoginMode = loginMode == 1;
        mBind.ivLoginMode.setImageResource(isLoginMode ? R.drawable.ic_checked : R.drawable.ic_unchecked);
        mBind.tvChangePassword.setVisibility(isLoginMode ? View.VISIBLE : View.GONE);
    }

    public void setBleTxPower(int txPower) {
        TxPowerEnum txPowerEnum = TxPowerEnum.fromTxPower(txPower);
        int progress = txPowerEnum.ordinal();
        mBind.sbTxPower.setProgress(progress);
    }


    public void changeConnectable() {
        isConnectable = !isConnectable;
        mBind.ivConnectable.setImageResource(isConnectable ? R.drawable.ic_checked : R.drawable.ic_unchecked);
    }

    public void changeLoginMode() {
        isLoginMode = !isLoginMode;
        mBind.ivLoginMode.setImageResource(isLoginMode ? R.drawable.ic_checked : R.drawable.ic_unchecked);
        mBind.tvChangePassword.setVisibility(isLoginMode ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        TxPowerEnum txPowerEnum = TxPowerEnum.fromOrdinal(progress);
        if (txPowerEnum == null)
            return;
        int txPower = txPowerEnum.getTxPower();
        mBind.tvTxPowerValue.setText(String.format("%ddBm", txPower));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public boolean isValid() {
        final String advIntervalStr = mBind.etAdvInterval.getText().toString();
        if (!mBind.cbBeaconMode.isChecked()) {
            final String advTimeoutStr = mBind.etAdvTimeout.getText().toString();
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
        final String advNameStr = mBind.etAdvName.getText().toString();
        final String advIntervalStr = mBind.etAdvInterval.getText().toString();
        final int interval = Integer.parseInt(advIntervalStr);
        List<OrderTask> orderTasks = new ArrayList<>();
        orderTasks.add(OrderTaskAssembler.setBleEnable(mBind.cbBeaconMode.isChecked() ? 1 : 0));
        if (mBind.cbBeaconMode.isChecked()) {
            orderTasks.add(OrderTaskAssembler.setBleConnectable(isConnectable ? 1 : 0));
        } else {
            final String advTimeoutStr = mBind.etAdvTimeout.getText().toString();
            final int timeout = Integer.parseInt(advTimeoutStr);
            orderTasks.add(OrderTaskAssembler.setBleTimeoutDuration(timeout));
        }
        orderTasks.add(OrderTaskAssembler.setBleAdvName(advNameStr));
        orderTasks.add(OrderTaskAssembler.setBleAdvInterval(interval));
        int progress = mBind.sbTxPower.getProgress();
        TxPowerEnum txPowerEnum = TxPowerEnum.fromOrdinal(progress);
        orderTasks.add(OrderTaskAssembler.setBleTxPower(txPowerEnum.getTxPower()));
        orderTasks.add(OrderTaskAssembler.setBleLoginMode(isLoginMode ? 1 : 0));
        LoRaLW007MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }
}
