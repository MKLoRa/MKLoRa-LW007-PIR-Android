package com.moko.lw007.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moko.lw007.R;
import com.moko.lw007.R2;
import com.moko.lw007.activity.DeviceInfoActivity;

import butterknife.ButterKnife;

public class GeneralFragment extends Fragment {
    private static final String TAG = GeneralFragment.class.getSimpleName();



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
}
