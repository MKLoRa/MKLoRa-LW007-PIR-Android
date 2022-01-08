package com.moko.lw007.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.moko.ble.lib.task.OrderTask;
import com.moko.lw007.R;
import com.moko.lw007.R2;
import com.moko.lw007.activity.DeviceInfoActivity;
import com.moko.support.lw007.LoRaLW007MokoSupport;
import com.moko.support.lw007.OrderTaskAssembler;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GeneralFragment extends Fragment {
    private static final String TAG = GeneralFragment.class.getSimpleName();
    @BindView(R2.id.et_heartbeat)
    EditText etHeartbeat;


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
        View view = inflater.inflate(R.layout.lw007_fragment_general, container, false);
        ButterKnife.bind(this, view);
        activity = (DeviceInfoActivity) getActivity();
        return view;
    }

    public void setHeartbeat(int heartbeat) {
        etHeartbeat.setText(String.valueOf(heartbeat));
    }

    public boolean isValid() {
        final String heartbeatStr = etHeartbeat.getText().toString();
        if (TextUtils.isEmpty(heartbeatStr))
            return false;
        final int heartbeat = Integer.parseInt(heartbeatStr);
        if (heartbeat < 1 || heartbeat > 14400) {
            return false;
        }
        return true;
    }

    public void saveParams() {
        final String heartbeatStr = etHeartbeat.getText().toString();
        final int heartbeat = Integer.parseInt(heartbeatStr);
        List<OrderTask> orderTasks = new ArrayList<>();
        orderTasks.add(OrderTaskAssembler.setHeartbeat(heartbeat));
        LoRaLW007MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }
}
