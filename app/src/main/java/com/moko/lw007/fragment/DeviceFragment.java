package com.moko.lw007.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moko.lw007.R;
import com.moko.lw007.R2;
import com.moko.lw007.activity.DeviceInfoActivity;
import com.moko.lw007.dialog.BottomDialog;
import com.moko.support.lw007.LoRaLW007MokoSupport;
import com.moko.support.lw007.OrderTaskAssembler;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceFragment extends Fragment {
    private static final String TAG = DeviceFragment.class.getSimpleName();
    @BindView(R2.id.tv_time_zone)
    TextView tvTimeZone;
    private ArrayList<String> mTimeZones;
    private int mSelectedTimeZone;
//    private boolean mShutdownPayloadEnable;
//    private boolean mLowPowerPayloadEnable;


    private DeviceInfoActivity activity;

    public DeviceFragment() {
    }


    public static DeviceFragment newInstance() {
        DeviceFragment fragment = new DeviceFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.lw007_fragment_device, container, false);
        ButterKnife.bind(this, view);
        activity = (DeviceInfoActivity) getActivity();
        mTimeZones = new ArrayList<>();
        for (int i = -24; i <= 28; i++) {
            if (i < 0) {
                if (i % 2 == 0) {
                    mTimeZones.add(String.format("UTC%d", i / 2));
                } else {
                    mTimeZones.add(i < -1 ? String.format("UTC%d:30", (i + 1) / 2) : "UTC-0:30");
                }
            } else if (i == 0) {
                mTimeZones.add("UTC");
            } else {
                if (i % 2 == 0) {
                    mTimeZones.add(String.format("UTC+%d", i / 2));
                } else {
                    mTimeZones.add(String.format("UTC+%d:30", (i - 1) / 2));
                }
            }
        }
        return view;
    }

    public void setTimeZone(int timeZone) {
        mSelectedTimeZone = timeZone + 24;
        tvTimeZone.setText(mTimeZones.get(mSelectedTimeZone));
    }

    public void showTimeZoneDialog() {
        BottomDialog dialog = new BottomDialog();
        dialog.setDatas(mTimeZones, mSelectedTimeZone);
        dialog.setListener(value -> {
            mSelectedTimeZone = value;
            tvTimeZone.setText(mTimeZones.get(value));
            activity.showSyncingProgressDialog();
            LoRaLW007MokoSupport.getInstance().sendOrder(OrderTaskAssembler.setTimezone(value - 24));
        });
        dialog.show(activity.getSupportFragmentManager());
    }


//    public void setShutdownPayload(int enable) {
//        mShutdownPayloadEnable = enable == 1;
//        ivShutdownPayload.setImageResource(mShutdownPayloadEnable ? R.drawable.lw007_ic_checked : R.drawable.lw007_ic_unchecked);
//    }
//
//    public void setLowPower(int lowPower) {
//        if ((lowPower & 1) == 1) {
//            mSelectedLowPowerPrompt = 1;
//        } else {
//            mSelectedLowPowerPrompt = 0;
//        }
//        tvLowPowerPrompt.setText(mLowPowerPrompts.get(mSelectedLowPowerPrompt));
//        tvLowPowerPromptTips.setText(getString(R.string.low_power_prompt_tips, mLowPowerPrompts.get(mSelectedLowPowerPrompt)));
//        if ((lowPower & 2) == 2) {
//            mLowPowerPayloadEnable = true;
//            ivLowPowerPayload.setImageResource(R.drawable.lw007_ic_checked);
//        } else {
//            mLowPowerPayloadEnable = false;
//            ivLowPowerPayload.setImageResource(R.drawable.lw007_ic_unchecked);
//        }
//    }
//
//    public void showLowPowerDialog() {
//        BottomDialog dialog = new BottomDialog();
//        dialog.setDatas(mLowPowerPrompts, mSelectedLowPowerPrompt);
//        dialog.setListener(value -> {
//            mSelectedLowPowerPrompt = value;
//            tvLowPowerPrompt.setText(mLowPowerPrompts.get(value));
//            tvLowPowerPromptTips.setText(getString(R.string.low_power_prompt_tips, mLowPowerPrompts.get(value)));
//            int lowPower = mSelectedLowPowerPrompt | (mLowPowerPayloadEnable ? 2 : 0);
//            activity.showSyncingProgressDialog();
//            LoRaLW007MokoSupport.getInstance().sendOrder(OrderTaskAssembler.setLowPower(lowPower));
//        });
//        dialog.show(activity.getSupportFragmentManager());
//
//    }
//
//    public void changeShutdownPayload() {
//        mShutdownPayloadEnable = !mShutdownPayloadEnable;
//        activity.showSyncingProgressDialog();
//        ArrayList<OrderTask> orderTasks = new ArrayList<>();
//        orderTasks.add(OrderTaskAssembler.setShutdownInfoReport(mShutdownPayloadEnable ? 1 : 0));
//        orderTasks.add(OrderTaskAssembler.getShutdownInfoReport());
//        LoRaLW007MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
//    }
//
//    public void changeLowPowerPayload() {
//        mLowPowerPayloadEnable = !mLowPowerPayloadEnable;
//        activity.showSyncingProgressDialog();
//        int lowPower = mSelectedLowPowerPrompt | (mLowPowerPayloadEnable ? 2 : 0);
//        ArrayList<OrderTask> orderTasks = new ArrayList<>();
//        orderTasks.add(OrderTaskAssembler.setLowPower(lowPower));
//        orderTasks.add(OrderTaskAssembler.getLowPower());
//        LoRaLW007MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
//    }
}
