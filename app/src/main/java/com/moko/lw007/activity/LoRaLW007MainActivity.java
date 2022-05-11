package com.moko.lw007.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.elvishew.xlog.XLog;
import com.moko.ble.lib.MokoConstants;
import com.moko.ble.lib.event.ConnectStatusEvent;
import com.moko.ble.lib.event.OrderTaskResponseEvent;
import com.moko.ble.lib.task.OrderTask;
import com.moko.ble.lib.task.OrderTaskResponse;
import com.moko.lw007.AppConstants;
import com.moko.lw007.BuildConfig;
import com.moko.lw007.R;
import com.moko.lw007.R2;
import com.moko.lw007.adapter.DeviceListAdapter;
import com.moko.lw007.dialog.AlertMessageDialog;
import com.moko.lw007.dialog.LoadingDialog;
import com.moko.lw007.dialog.LoadingMessageDialog;
import com.moko.lw007.dialog.PasswordDialog;
import com.moko.lw007.dialog.ScanFilterDialog;
import com.moko.lw007.entity.AdvInfo;
import com.moko.lw007.utils.BeaconInfoParseableImpl;
import com.moko.lw007.utils.SPUtiles;
import com.moko.lw007.utils.ToastUtils;
import com.moko.lw007.utils.Utils;
import com.moko.support.lw007.LoRaLW007MokoSupport;
import com.moko.support.lw007.MokoBleScanner;
import com.moko.support.lw007.OrderTaskAssembler;
import com.moko.support.lw007.callback.MokoScanDeviceCallback;
import com.moko.support.lw007.entity.DeviceInfo;
import com.moko.support.lw007.entity.OrderCHAR;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class LoRaLW007MainActivity extends BaseActivity implements MokoScanDeviceCallback, BaseQuickAdapter.OnItemChildClickListener {
    @BindView(R2.id.iv_refresh)
    ImageView ivRefresh;
    @BindView(R2.id.rv_devices)
    RecyclerView rvDevices;
    @BindView(R2.id.tv_device_num)
    TextView tvDeviceNum;
    @BindView(R2.id.rl_edit_filter)
    RelativeLayout rl_edit_filter;
    @BindView(R2.id.rl_filter)
    RelativeLayout rl_filter;
    @BindView(R2.id.tv_filter)
    TextView tv_filter;
    private boolean mReceiverTag = false;
    private ConcurrentHashMap<String, AdvInfo> beaconInfoHashMap;
    private ArrayList<AdvInfo> beaconInfos;
    private DeviceListAdapter adapter;
    private Animation animation = null;
    private MokoBleScanner mokoBleScanner;
    public Handler mHandler;
    private boolean isPasswordError;
    private boolean isNeedPassword;

    public static String PATH_LOGCAT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lw007_activity_main);
        ButterKnife.bind(this);
        // 初始化Xlog
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // 优先保存到SD卡中
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                PATH_LOGCAT = getExternalFilesDir(null).getAbsolutePath() + File.separator + (BuildConfig.IS_LIBRARY ? "MKLoRa" : "LW007");
            } else {
                PATH_LOGCAT = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + (BuildConfig.IS_LIBRARY ? "MKLoRa" : "LW007");
            }
        } else {
            // 如果SD卡不存在，就保存到本应用的目录下
            PATH_LOGCAT = getFilesDir().getAbsolutePath() + File.separator + (BuildConfig.IS_LIBRARY ? "MKLoRa" : "LW007");
        }
        LoRaLW007MokoSupport.getInstance().init(getApplicationContext());
        mSavedPassword = SPUtiles.getStringValue(this, AppConstants.SP_KEY_SAVED_PASSWORD_LW007, "");
        beaconInfoHashMap = new ConcurrentHashMap<>();
        beaconInfos = new ArrayList<>();
        adapter = new DeviceListAdapter();
        adapter.replaceData(beaconInfos);
        adapter.setOnItemChildClickListener(this);
        adapter.openLoadAnimation();
        rvDevices.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.lw007_shape_recycleview_divider));
        rvDevices.addItemDecoration(itemDecoration);
        rvDevices.setAdapter(adapter);
        mHandler = new Handler(Looper.getMainLooper());
        mokoBleScanner = new MokoBleScanner(this);
        EventBus.getDefault().register(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        mReceiverTag = true;
        if (!LoRaLW007MokoSupport.getInstance().isBluetoothOpen()) {
            LoRaLW007MokoSupport.getInstance().enableBluetooth();
        } else {
            if (animation == null) {
                startScan();
            }
        }
    }

    private void startScan() {
        if (!LoRaLW007MokoSupport.getInstance().isBluetoothOpen()) {
            LoRaLW007MokoSupport.getInstance().enableBluetooth();
            return;
        }
        animation = AnimationUtils.loadAnimation(this, R.anim.lw007_rotate_refresh);
        ivRefresh.startAnimation(animation);
        beaconInfoParseable = new BeaconInfoParseableImpl();
        mokoBleScanner.startScanDevice(this);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mokoBleScanner.stopScanDevice();
            }
        }, 1000 * 60);
    }


    private BeaconInfoParseableImpl beaconInfoParseable;
    public String filterName;
    public int filterRssi = -127;

    @Override
    public void onStartScan() {
        beaconInfoHashMap.clear();
        new Thread(() -> {
            while (animation != null) {
                runOnUiThread(() -> {
                    adapter.replaceData(beaconInfos);
                    tvDeviceNum.setText(String.format("DEVICE(%d)", beaconInfos.size()));
                });
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                updateDevices();
            }
        }).start();
    }

    @Override
    public void onScanDevice(DeviceInfo deviceInfo) {
        AdvInfo beaconInfo = beaconInfoParseable.parseDeviceInfo(deviceInfo);
        if (beaconInfo == null)
            return;
        beaconInfoHashMap.put(beaconInfo.mac, beaconInfo);
    }

    @Override
    public void onStopScan() {
        ivRefresh.clearAnimation();
        animation = null;
    }

    private void updateDevices() {
        beaconInfos.clear();
        if (!TextUtils.isEmpty(filterName) || filterRssi != -127) {
            ArrayList<AdvInfo> beaconInfosFilter = new ArrayList<>(beaconInfoHashMap.values());
            Iterator<AdvInfo> iterator = beaconInfosFilter.iterator();
            while (iterator.hasNext()) {
                AdvInfo beaconInfo = iterator.next();
                if (beaconInfo.rssi > filterRssi) {
                    if (TextUtils.isEmpty(filterName)) {
                        continue;
                    } else {
                        if (TextUtils.isEmpty(beaconInfo.name) && TextUtils.isEmpty(beaconInfo.mac)) {
                            iterator.remove();
                        } else if (TextUtils.isEmpty(beaconInfo.name) && beaconInfo.mac.toLowerCase().replaceAll(":", "").contains(filterName.toLowerCase())) {
                            continue;
                        } else if (TextUtils.isEmpty(beaconInfo.mac) && beaconInfo.name.toLowerCase().contains(filterName.toLowerCase())) {
                            continue;
                        } else if (!TextUtils.isEmpty(beaconInfo.name) && !TextUtils.isEmpty(beaconInfo.mac) && (beaconInfo.name.toLowerCase().contains(filterName.toLowerCase()) || beaconInfo.mac.toLowerCase().replaceAll(":", "").contains(filterName.toLowerCase()))) {
                            continue;
                        } else {
                            iterator.remove();
                        }
                    }
                } else {
                    iterator.remove();
                }
            }
            beaconInfos.addAll(beaconInfosFilter);
        } else {
            beaconInfos.addAll(beaconInfoHashMap.values());
        }
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        Collections.sort(beaconInfos, (lhs, rhs) -> {
            if (lhs.rssi > rhs.rssi) {
                return -1;
            } else if (lhs.rssi < rhs.rssi) {
                return 1;
            }
            return 0;
        });
    }

    public void onRefresh(View view) {
        if (isWindowLocked())
            return;
        if (!LoRaLW007MokoSupport.getInstance().isBluetoothOpen()) {
            LoRaLW007MokoSupport.getInstance().enableBluetooth();
            return;
        }
        if (animation == null) {
            startScan();
        } else {
            mHandler.removeMessages(0);
            mokoBleScanner.stopScanDevice();
        }
    }

    public void onBack(View view) {
        back();
    }

    private void back() {
        if (animation != null) {
            mHandler.removeMessages(0);
            mokoBleScanner.stopScanDevice();
        }
        if (BuildConfig.IS_LIBRARY) {
            finish();
        } else {
            AlertMessageDialog dialog = new AlertMessageDialog();
            dialog.setMessage(R.string.main_exit_tips);
            dialog.setOnAlertConfirmListener(() -> LoRaLW007MainActivity.this.finish());
            dialog.show(getSupportFragmentManager());
        }
    }

    public void onAbout(View view) {
        if (isWindowLocked())
            return;
        startActivity(new Intent(this, AboutActivity.class));
    }


    public void onFilter(View view) {
        if (isWindowLocked())
            return;
        if (animation != null) {
            mHandler.removeMessages(0);
            mokoBleScanner.stopScanDevice();
        }
        ScanFilterDialog scanFilterDialog = new ScanFilterDialog(this);
        scanFilterDialog.setFilterName(filterName);
        scanFilterDialog.setFilterRssi(filterRssi);
        scanFilterDialog.setOnScanFilterListener(new ScanFilterDialog.OnScanFilterListener() {
            @Override
            public void onDone(String filterName, int filterRssi) {
                LoRaLW007MainActivity.this.filterName = filterName;
                LoRaLW007MainActivity.this.filterRssi = filterRssi;
                if (!TextUtils.isEmpty(filterName) || filterRssi != -127) {
                    rl_filter.setVisibility(View.VISIBLE);
                    rl_edit_filter.setVisibility(View.GONE);
                    StringBuilder stringBuilder = new StringBuilder();
                    if (!TextUtils.isEmpty(filterName)) {
                        stringBuilder.append(filterName);
                        stringBuilder.append(";");
                    }
                    if (filterRssi != -127) {
                        stringBuilder.append(String.format("%sdBm", filterRssi + ""));
                        stringBuilder.append(";");
                    }
                    tv_filter.setText(stringBuilder.toString());
                } else {
                    rl_filter.setVisibility(View.GONE);
                    rl_edit_filter.setVisibility(View.VISIBLE);
                }
                if (isWindowLocked())
                    return;
                if (animation == null) {
                    startScan();
                }
            }
        });
        scanFilterDialog.setOnDismissListener(dialog -> {
            if (isWindowLocked())
                return;
            if (animation == null) {
                startScan();
            }
        });
        scanFilterDialog.show();
    }

    public void onFilterDelete(View view) {
        if (animation != null) {
            mHandler.removeMessages(0);
            mokoBleScanner.stopScanDevice();
        }
        rl_filter.setVisibility(View.GONE);
        rl_edit_filter.setVisibility(View.VISIBLE);
        filterName = "";
        filterRssi = -127;
        if (isWindowLocked())
            return;
        if (animation == null) {
            startScan();
        }
    }

    private String mPassword;
    private String mSavedPassword;

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if (!LoRaLW007MokoSupport.getInstance().isBluetoothOpen()) {
            LoRaLW007MokoSupport.getInstance().enableBluetooth();
            return;
        }
        AdvInfo advInfo = (AdvInfo) adapter.getItem(position);
        if (advInfo.connectable && advInfo != null && !isFinishing()) {
            if (animation != null) {
                mHandler.removeMessages(0);
                mokoBleScanner.stopScanDevice();
            }
            isNeedPassword = advInfo.needPassword;
            if (!isNeedPassword) {
                showLoadingProgressDialog();
                ivRefresh.postDelayed(() -> LoRaLW007MokoSupport.getInstance().connDevice(advInfo.mac), 500);
                return;
            }
            // show password
            final PasswordDialog dialog = new PasswordDialog(LoRaLW007MainActivity.this);
            dialog.setData(mSavedPassword);
            dialog.setOnPasswordClicked(new PasswordDialog.PasswordClickListener() {
                @Override
                public void onEnsureClicked(String password) {
                    if (!LoRaLW007MokoSupport.getInstance().isBluetoothOpen()) {
                        LoRaLW007MokoSupport.getInstance().enableBluetooth();
                        return;
                    }
                    XLog.i(password);
                    mPassword = password;
                    if (animation != null) {
                        mHandler.removeMessages(0);
                        mokoBleScanner.stopScanDevice();
                    }
                    showLoadingProgressDialog();
                    ivRefresh.postDelayed(() -> LoRaLW007MokoSupport.getInstance().connDevice(advInfo.mac), 500);
                }

                @Override
                public void onDismiss() {

                }
            });
            dialog.show();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    runOnUiThread(() -> dialog.showKeyboard());
                }
            }, 200);
        }
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
                            if (animation != null) {
                                mHandler.removeMessages(0);
                                mokoBleScanner.stopScanDevice();
                                onStopScan();
                            }
                            break;
                        case BluetoothAdapter.STATE_ON:
                            if (animation == null) {
                                startScan();
                            }
                            break;
                    }
                }
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectStatusEvent(ConnectStatusEvent event) {
        String action = event.getAction();
        if (MokoConstants.ACTION_DISCONNECTED.equals(action)) {
            mPassword = "";
            dismissLoadingProgressDialog();
            dismissLoadingMessageDialog();
            if (isPasswordError) {
                isPasswordError = false;
            } else {
                ToastUtils.showToast(LoRaLW007MainActivity.this, "Connection Failed");
            }
            if (animation == null) {
                startScan();
            }
        }
        if (MokoConstants.ACTION_DISCOVER_SUCCESS.equals(action)) {
            dismissLoadingProgressDialog();
            if (!isNeedPassword) {
                XLog.i("Success");
                Intent i = new Intent(LoRaLW007MainActivity.this, DeviceInfoActivity.class);
                startActivityForResult(i, AppConstants.REQUEST_CODE_DEVICE_INFO);
                return;
            }
            showLoadingMessageDialog();
            mHandler.postDelayed(() -> {
                // open password notify and set password
                List<OrderTask> orderTasks = new ArrayList<>();
                orderTasks.add(OrderTaskAssembler.setPassword(mPassword));
                LoRaLW007MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
            }, 500);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOrderTaskResponseEvent(OrderTaskResponseEvent event) {
        final String action = event.getAction();
        if (MokoConstants.ACTION_ORDER_TIMEOUT.equals(action)) {
        }
        if (MokoConstants.ACTION_ORDER_FINISH.equals(action)) {
        }
        if (MokoConstants.ACTION_ORDER_RESULT.equals(action)) {
            OrderTaskResponse response = event.getResponse();
            OrderCHAR orderCHAR = (OrderCHAR) response.orderCHAR;
            int responseType = response.responseType;
            byte[] value = response.responseValue;
            switch (orderCHAR) {
                case CHAR_PASSWORD:
                    dismissLoadingMessageDialog();
                    if (value.length == 5) {
                        int header = value[0] & 0xFF;// 0xED
                        int flag = value[1] & 0xFF;// read or write
                        int cmd = value[2] & 0xFF;
                        if (header != 0xED)
                            return;
                        int length = value[3] & 0xFF;
                        if (flag == 0x01 && cmd == 0x01 && length == 0x01) {
                            int result = value[4] & 0xFF;
                            if (1 == result) {
                                mSavedPassword = mPassword;
                                SPUtiles.setStringValue(LoRaLW007MainActivity.this, AppConstants.SP_KEY_SAVED_PASSWORD_LW007, mSavedPassword);
                                XLog.i("Success");
                                Intent i = new Intent(LoRaLW007MainActivity.this, DeviceInfoActivity.class);
                                startActivityForResult(i, AppConstants.REQUEST_CODE_DEVICE_INFO);
                            }
                            if (0 == result) {
                                isPasswordError = true;
                                ToastUtils.showToast(LoRaLW007MainActivity.this, "Password Error");
                                LoRaLW007MokoSupport.getInstance().disConnectBle();
                            }
                        }
                    }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.REQUEST_CODE_DEVICE_INFO) {
            if (resultCode == RESULT_OK) {
                if (animation == null) {
                    startScan();
                }
            }
        }
    }

    private LoadingDialog mLoadingDialog;

    private void showLoadingProgressDialog() {
        mLoadingDialog = new LoadingDialog();
        mLoadingDialog.show(getSupportFragmentManager());

    }

    private void dismissLoadingProgressDialog() {
        if (mLoadingDialog != null)
            mLoadingDialog.dismissAllowingStateLoss();
    }

    private LoadingMessageDialog mLoadingMessageDialog;

    private void showLoadingMessageDialog() {
        mLoadingMessageDialog = new LoadingMessageDialog();
        mLoadingMessageDialog.setMessage("Verifying..");
        mLoadingMessageDialog.show(getSupportFragmentManager());

    }

    private void dismissLoadingMessageDialog() {
        if (mLoadingMessageDialog != null)
            mLoadingMessageDialog.dismissAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        back();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiverTag) {
            mReceiverTag = false;
            unregisterReceiver(mReceiver);
        }
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        XLog.i("onNewIntent...");
        setIntent(intent);
        if (getIntent().getExtras() != null) {
            String from = getIntent().getStringExtra(AppConstants.EXTRA_KEY_FROM_ACTIVITY);
            if (ExportDataActivity.TAG.equals(from)) {
                if (animation == null) {
                    startScan();
                }
            }
        }
    }
}
