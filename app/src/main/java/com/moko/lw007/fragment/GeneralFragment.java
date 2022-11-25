package com.moko.lw007.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moko.ble.lib.task.OrderTask;
import com.moko.lw007.activity.DeviceInfoActivity;
import com.moko.lw007.databinding.Lw007FragmentGeneralBinding;
import com.moko.support.lw007.LoRaLW007MokoSupport;
import com.moko.support.lw007.OrderTaskAssembler;

import java.util.ArrayList;
import java.util.List;

public class GeneralFragment extends Fragment {
    private static final String TAG = GeneralFragment.class.getSimpleName();

    private Lw007FragmentGeneralBinding mBind;
    private DeviceInfoActivity activity;

    public GeneralFragment() {
    }


    public static GeneralFragment newInstance() {
        GeneralFragment fragment = new GeneralFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");
        mBind = Lw007FragmentGeneralBinding.inflate(inflater, container, false);
        activity = (DeviceInfoActivity) getActivity();
        return mBind.getRoot();
    }

    public void setHeartbeat(int heartbeat) {
        mBind.etHeartbeat.setText(String.valueOf(heartbeat));
    }

    public boolean isValid() {
        final String heartbeatStr = mBind.etHeartbeat.getText().toString();
        if (TextUtils.isEmpty(heartbeatStr))
            return false;
        final int heartbeat = Integer.parseInt(heartbeatStr);
        if (heartbeat < 1 || heartbeat > 14400) {
            return false;
        }
        return true;
    }

    public void saveParams() {
        final String heartbeatStr = mBind.etHeartbeat.getText().toString();
        final int heartbeat = Integer.parseInt(heartbeatStr);
        List<OrderTask> orderTasks = new ArrayList<>();
        orderTasks.add(OrderTaskAssembler.setHeartbeat(heartbeat));
        LoRaLW007MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }
}
