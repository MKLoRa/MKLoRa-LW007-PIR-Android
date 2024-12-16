package com.moko.lw007.activity;


import android.app.AlertDialog;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import com.moko.ble.lib.MokoConstants;
import com.moko.ble.lib.event.ConnectStatusEvent;
import com.moko.ble.lib.event.OrderTaskResponseEvent;
import com.moko.ble.lib.task.OrderTask;
import com.moko.ble.lib.task.OrderTaskResponse;
import com.moko.ble.lib.utils.MokoUtils;
import com.moko.lw007.AppConstants;
import com.moko.lw007.R;
import com.moko.lw007.databinding.Lw007ActivityDeviceInfoBinding;
import com.moko.lw007.dialog.AlertMessageDialog;
import com.moko.lw007.dialog.ChangePasswordDialog;
import com.moko.lw007.dialog.LoadingMessageDialog;
import com.moko.lw007.fragment.BleFragment;
import com.moko.lw007.fragment.DeviceFragment;
import com.moko.lw007.fragment.GeneralFragment;
import com.moko.lw007.fragment.LoRaFragment;
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
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.IdRes;

public class DeviceInfoActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {

    private Lw007ActivityDeviceInfoBinding mBind;
    private FragmentManager fragmentManager;
    private LoRaFragment loraFragment;
    private BleFragment bleFragment;
    private GeneralFragment generalFragment;
    private DeviceFragment deviceFragment;
    private ArrayList<String> mUploadMode;
    private ArrayList<String> mRegions;
    private int mSelectedRegion;
    private int mSelectUploadMode;
    private boolean mReceiverTag = false;
    private int disConnectType;
    private boolean noPassword;

    private boolean savedParamsError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBind = Lw007ActivityDeviceInfoBinding.inflate(getLayoutInflater());
        setContentView(mBind.getRoot());
        fragmentManager = getFragmentManager();
        initFragment();
        mBind.radioBtnLora.setChecked(true);
        mBind.tvTitle.setText(R.string.title_lora);
        mBind.rgOptions.setOnCheckedChangeListener(this);
        EventBus.getDefault().register(this);
        mUploadMode = new ArrayList<>();
        mUploadMode.add("ABP");
        mUploadMode.add("OTAA");
        mRegions = new ArrayList<>();
        mRegions.add("AS923");
        mRegions.add("AU915");
        mRegions.add("CN470");
        mRegions.add("CN779");
        mRegions.add("EU433");
        mRegions.add("EU868");
        mRegions.add("KR920");
        mRegions.add("IN865");
        mRegions.add("US915");
        mRegions.add("RU864");
        mRegions.add("AS923-1");
        mRegions.add("AS923-2");
        mRegions.add("AS923-3");
        mRegions.add("AS923-4");
        // 注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        mReceiverTag = true;
        if (!LoRaLW007MokoSupport.getInstance().isBluetoothOpen()) {
            LoRaLW007MokoSupport.getInstance().enableBluetooth();
        } else {
            showSyncingProgressDialog();
            mBind.tvTitle.postDelayed(() -> {
                List<OrderTask> orderTasks = new ArrayList<>();
                // sync time after connect success;
                orderTasks.add(OrderTaskAssembler.setTime());
                // get lora params
                orderTasks.add(OrderTaskAssembler.getLoraRegion());
                orderTasks.add(OrderTaskAssembler.getLoraUploadMode());
                orderTasks.add(OrderTaskAssembler.getLoraNetworkStatus());
                LoRaLW007MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
            }, 500);
        }
    }

    private void initFragment() {
        loraFragment = LoRaFragment.newInstance();
        generalFragment = GeneralFragment.newInstance();
        bleFragment = BleFragment.newInstance();
        deviceFragment = DeviceFragment.newInstance();
        fragmentManager.beginTransaction()
                .add(R.id.frame_container, loraFragment)
                .add(R.id.frame_container, generalFragment)
                .add(R.id.frame_container, bleFragment)
                .add(R.id.frame_container, deviceFragment)
                .show(loraFragment)
                .hide(generalFragment)
                .hide(bleFragment)
                .hide(deviceFragment)
                .commit();
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 100)
    public void onConnectStatusEvent(ConnectStatusEvent event) {
        EventBus.getDefault().cancelEventDelivery(event);
        final String action = event.getAction();
        runOnUiThread(() -> {
            if (MokoConstants.ACTION_DISCONNECTED.equals(action)) {
                showDisconnectDialog();
            }
            if (MokoConstants.ACTION_DISCOVER_SUCCESS.equals(action)) {
            }
        });

    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 100)
    public void onOrderTaskResponseEvent(OrderTaskResponseEvent event) {
        EventBus.getDefault().cancelEventDelivery(event);
        final String action = event.getAction();
        runOnUiThread(() -> {
            if (MokoConstants.ACTION_CURRENT_DATA.equals(action)) {
                OrderTaskResponse response = event.getResponse();
                OrderCHAR orderCHAR = (OrderCHAR) response.orderCHAR;
                int responseType = response.responseType;
                byte[] value = response.responseValue;
                switch (orderCHAR) {
                    case CHAR_DISCONNECTED_NOTIFY:
                        final int length = value.length;
                        if (length != 5)
                            return;
                        int header = value[0] & 0xFF;
                        int flag = value[1] & 0xFF;
                        int cmd = value[2] & 0xFF;
                        int len = value[3] & 0xFF;
                        int type = value[4] & 0xFF;
                        if (header == 0xED && flag == 0x02 && cmd == 0x01 && len == 0x01) {
                            disConnectType = type;
                            if (type == 1) {
                                // valid password timeout
                            } else if (type == 2) {
                                // change password success
                            } else if (type == 3) {
                                // no data exchange timeout
                            } else if (type == 4) {
                                // reset success
                            } else if (type == 5) {
                                // change unconnectable
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
                            ControlKeyEnum configKeyEnum = ControlKeyEnum.fromParamKey(cmd);
                            if (configKeyEnum == null) {
                                return;
                            }
                            int length = value[3] & 0xFF;
                            if (flag == 0x01) {
                                // write
                                int result = value[4] & 0xFF;
                                switch (configKeyEnum) {
                                    case KEY_TIME:
                                        if (result == 1)
                                            ToastUtils.showToast(DeviceInfoActivity.this, "Time sync completed!");
                                        break;
                                    case KEY_BATTERY_RESET:
                                        if (result == 1) {
                                            AlertMessageDialog dialog = new AlertMessageDialog();
                                            dialog.setMessage("Reset Successfully！");
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
                                    case KEY_NETWORK_STATUS:
                                        if (length > 0) {
                                            int networkStatus = value[4] & 0xFF;
                                            loraFragment.setLoraStatus(networkStatus);
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
                                    case KEY_BLE_ENABLE:
                                    case KEY_BLE_TIMEOUT_DURATION:
                                    case KEY_BLE_ADV_NAME:
                                    case KEY_BLE_ADV_INTERVAL:
                                    case KEY_BLE_TX_POWER:
                                    case KEY_BLE_CONNECTABLE:
                                        if (result != 1) {
                                            savedParamsError = true;
                                        }
                                        break;
                                    case KEY_BLE_LOGIN_MODE:
                                    case KEY_HEARTBEAT:
                                    case KEY_TIME_ZONE:
//                                    case KEY_LOW_POWER_PROMPT:
                                    case KEY_LOW_POWER_PAYLOAD:
                                        if (result != 1) {
                                            savedParamsError = true;
                                        }
                                        if (savedParamsError) {
                                            ToastUtils.showToast(DeviceInfoActivity.this, "Opps！Save failed. Please check the input characters and try again.");
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
                                    case KEY_LORA_REGION:
                                        if (length > 0) {
                                            final int region = value[4] & 0xFF;
                                            mSelectedRegion = region;
                                        }
                                        break;
                                    case KEY_LORA_MODE:
                                        if (length > 0) {
                                            final int mode = value[4];
                                            mSelectUploadMode = mode;
                                            String loraInfo = String.format("%s/%s/ClassA",
                                                    mUploadMode.get(mSelectUploadMode - 1),
                                                    mRegions.get(mSelectedRegion));
                                            loraFragment.setLoRaInfo(loraInfo);
                                        }
                                        break;
                                    case KEY_HEARTBEAT:
                                        if (length > 0) {
                                            final int heartbeat = MokoUtils.toInt(Arrays.copyOfRange(value, 4, 4 + length));
                                            generalFragment.setHeartbeat(heartbeat);
                                        }
                                        break;
                                    case KEY_BLE_ENABLE:
                                        if (length > 0) {
                                            final int enable = value[4] & 0xFF;
                                            bleFragment.setBleEnable(enable);
                                        }
                                        break;
                                    case KEY_BLE_TIMEOUT_DURATION:
                                        if (length > 0) {
                                            int timeout = value[4] & 0xFF;
                                            bleFragment.setBleTimeoutDuration(timeout);
                                        }
                                        break;
                                    case KEY_BLE_ADV_NAME:
                                        if (length > 0) {
                                            String advName = new String(Arrays.copyOfRange(value, 4, 4 + length));
                                            bleFragment.setBleAdvName(advName);
                                        }
                                        break;
                                    case KEY_BLE_ADV_INTERVAL:
                                        if (length > 0) {
                                            int advInterval = value[4] & 0xFF;
                                            bleFragment.setBleAdvInterval(advInterval);
                                        }
                                        break;
                                    case KEY_BLE_CONNECTABLE:
                                        if (length > 0) {
                                            int connectable = value[4] & 0xFF;
                                            bleFragment.setBleConnectable(connectable);
                                        }
                                        break;
                                    case KEY_BLE_LOGIN_MODE:
                                        if (length > 0) {
                                            int loginMode = value[4] & 0xFF;
                                            noPassword = loginMode == 0;
                                            bleFragment.setBleLoginMode(loginMode);
                                        }
                                        break;
                                    case KEY_BLE_TX_POWER:
                                        if (length > 0) {
                                            int txPower = value[4];
                                            bleFragment.setBleTxPower(txPower);
                                        }
                                        break;
                                    case KEY_TIME_ZONE:
                                        if (length > 0) {
                                            int timeZone = value[4];
                                            deviceFragment.setTimeZone(timeZone);
                                        }
                                        break;
//                                    case KEY_LOW_POWER_PROMPT:
//                                        if (length > 0) {
//                                            int prompt = value[4] & 0xFF;
//                                            deviceFragment.setLowPowerPrompt(prompt);
//                                        }
//                                        break;
                                    case KEY_LOW_POWER_PAYLOAD:
                                        if (length > 0) {
                                            int payload = value[4] & 0xFF;
                                            deviceFragment.setLowPowerPayload(payload);
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

    private void showDisconnectDialog() {
        if (disConnectType == 2) {
            AlertMessageDialog dialog = new AlertMessageDialog();
            dialog.setTitle("Change Password");
            dialog.setMessage("Password changed successfully!Please reconnect the device.");
            dialog.setConfirm("OK");
            dialog.setCancelGone();
            dialog.setOnAlertConfirmListener(() -> {
                setResult(RESULT_OK);
                finish();
            });
            dialog.show(getSupportFragmentManager());
        } else if (disConnectType == 3) {
            AlertMessageDialog dialog = new AlertMessageDialog();
            dialog.setMessage("No data communication for 3 minutes, the device is disconnected.");
            dialog.setConfirm("OK");
            dialog.setCancelGone();
            dialog.setOnAlertConfirmListener(() -> {
                setResult(RESULT_OK);
                finish();
            });
            dialog.show(getSupportFragmentManager());
        } else if (disConnectType == 5) {
            AlertMessageDialog dialog = new AlertMessageDialog();
            dialog.setTitle("Factory Reset");
            dialog.setMessage("Factory reset successfully!\nPlease reconnect the device.");
            dialog.setConfirm("OK");
            dialog.setCancelGone();
            dialog.setOnAlertConfirmListener(() -> {
                setResult(RESULT_OK);
                finish();
            });
            dialog.show(getSupportFragmentManager());
        } else if (disConnectType == 4) {
            AlertMessageDialog dialog = new AlertMessageDialog();
            dialog.setTitle("Dismiss");
            dialog.setMessage("Reboot successfully!\nPlease reconnect the device");
            dialog.setConfirm("OK");
            dialog.setCancelGone();
            dialog.setOnAlertConfirmListener(() -> {
                setResult(RESULT_OK);
                finish();
            });
            dialog.show(getSupportFragmentManager());
        } else if (disConnectType == 1) {
            AlertMessageDialog dialog = new AlertMessageDialog();
            dialog.setMessage("The device is disconnected!");
            dialog.setConfirm("OK");
            dialog.setCancelGone();
            dialog.setOnAlertConfirmListener(() -> {
                setResult(RESULT_OK);
                finish();
            });
            dialog.show(getSupportFragmentManager());
        } else {
            if (LoRaLW007MokoSupport.getInstance().isBluetoothOpen()) {
                AlertMessageDialog dialog = new AlertMessageDialog();
                dialog.setTitle("Dismiss");
                dialog.setMessage("The device disconnected!");
                dialog.setConfirm("Exit");
                dialog.setCancelGone();
                dialog.setOnAlertConfirmListener(() -> {
                    setResult(RESULT_OK);
                    finish();
                });
                dialog.show(getSupportFragmentManager());
            }
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
                            dismissSyncProgressDialog();
                            AlertDialog.Builder builder = new AlertDialog.Builder(DeviceInfoActivity.this);
                            builder.setTitle("Dismiss");
                            builder.setCancelable(false);
                            builder.setMessage("The current system of bluetooth is not available!");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DeviceInfoActivity.this.setResult(RESULT_OK);
                                    finish();
                                }
                            });
                            builder.show();
                            break;

                    }
                }
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.REQUEST_CODE_LORA_CONN_SETTING) {
            if (resultCode == RESULT_OK) {
                mBind.tvTitle.postDelayed(() -> {
                    showSyncingProgressDialog();
                    List<OrderTask> orderTasks = new ArrayList<>();
                    // setting
                    orderTasks.add(OrderTaskAssembler.getLoraRegion());
                    orderTasks.add(OrderTaskAssembler.getLoraUploadMode());
                    orderTasks.add(OrderTaskAssembler.getLoraNetworkStatus());
                    LoRaLW007MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
                }, 500);
            }
        }
        if (requestCode == AppConstants.REQUEST_CODE_SYSTEM_INFO) {
            if (resultCode == RESULT_OK) {
                AlertMessageDialog dialog = new AlertMessageDialog();
                dialog.setTitle("Update Firmware");
                dialog.setMessage("Update firmware successfully!\nPlease reconnect the device.");
                dialog.setConfirm("OK");
                dialog.setCancelGone();
                dialog.setOnAlertConfirmListener(() -> {
                    setResult(RESULT_OK);
                    finish();
                });
                dialog.show(getSupportFragmentManager());
            }
            if (resultCode == RESULT_FIRST_USER) {
                String mac = data.getStringExtra(AppConstants.EXTRA_KEY_DEVICE_MAC);
                mBind.frameContainer.postDelayed(() -> {
                    if (LoRaLW007MokoSupport.getInstance().isConnDevice(mac)) {
                        LoRaLW007MokoSupport.getInstance().disConnectBle();
                        return;
                    }
                    showDisconnectDialog();
                }, 500);
            }
        }
    }

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

    public void onBack(View view) {
        if (isWindowLocked())
            return;
        back();
    }

    public void onSave(View view) {
        if (isWindowLocked())
            return;
        if (mBind.radioBtnGeneral.isChecked()) {
            if (generalFragment.isValid()) {
                showSyncingProgressDialog();
                generalFragment.saveParams();
            } else {
                ToastUtils.showToast(this, "Opps！Save failed. Please check the input characters and try again.");
            }
        }
        if (mBind.radioBtnBle.isChecked()) {
            if (bleFragment.isValid()) {
                showSyncingProgressDialog();
                bleFragment.saveParams();
            } else {
                ToastUtils.showToast(this, "Opps！Save failed. Please check the input characters and try again.");
            }
        }
    }

    private void back() {
        mBind.frameContainer.postDelayed(() -> {
            LoRaLW007MokoSupport.getInstance().disConnectBle();
        }, 500);
    }

    @Override
    public void onBackPressed() {
        if (isWindowLocked())
            return;
        back();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        if (checkedId == R.id.radioBtn_lora) {
            showLoRaAndGetData();
        } else if (checkedId == R.id.radioBtn_general) {
            showGeneralAndGetData();
        } else if (checkedId == R.id.radioBtn_ble) {
            showBleAndGetData();
        } else if (checkedId == R.id.radioBtn_device) {
            showDeviceAndGetData();
        }
    }

    private void showDeviceAndGetData() {
        mBind.tvTitle.setText("Device Settings");
        mBind.ivSave.setVisibility(View.GONE);
        fragmentManager.beginTransaction()
                .hide(loraFragment)
                .hide(generalFragment)
                .hide(bleFragment)
                .show(deviceFragment)
                .commit();
        showSyncingProgressDialog();
        List<OrderTask> orderTasks = new ArrayList<>();
        // device
        orderTasks.add(OrderTaskAssembler.getTimeZone());
//        orderTasks.add(OrderTaskAssembler.getLowPowerPrompt());
        orderTasks.add(OrderTaskAssembler.getLowPowerPayload());
        LoRaLW007MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }

    private void showGeneralAndGetData() {
        mBind.tvTitle.setText("General Settings");
        mBind.ivSave.setVisibility(View.VISIBLE);
        fragmentManager.beginTransaction()
                .hide(loraFragment)
                .show(generalFragment)
                .hide(bleFragment)
                .hide(deviceFragment)
                .commit();
        showSyncingProgressDialog();
        List<OrderTask> orderTasks = new ArrayList<>();
        // get general params
        orderTasks.add(OrderTaskAssembler.getHeartbeat());
        LoRaLW007MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }

    private void showBleAndGetData() {
        mBind.tvTitle.setText("Bluetooth Settings");
        mBind.ivSave.setVisibility(View.VISIBLE);
        fragmentManager.beginTransaction()
                .hide(loraFragment)
                .hide(generalFragment)
                .show(bleFragment)
                .hide(deviceFragment)
                .commit();
        showSyncingProgressDialog();
        List<OrderTask> orderTasks = new ArrayList<>();
        // get ble params
        orderTasks.add(OrderTaskAssembler.getBleAdvName());
        orderTasks.add(OrderTaskAssembler.getBleAdvInterval());
        orderTasks.add(OrderTaskAssembler.getBleEnable());
        orderTasks.add(OrderTaskAssembler.getBleConnectable());
        orderTasks.add(OrderTaskAssembler.getBleTimeoutDuration());
        orderTasks.add(OrderTaskAssembler.getBleLoginMode());
        orderTasks.add(OrderTaskAssembler.getBleTxPower());
        LoRaLW007MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }

    private void showLoRaAndGetData() {
        mBind.tvTitle.setText(R.string.title_lora);
        mBind.ivSave.setVisibility(View.GONE);
        fragmentManager.beginTransaction()
                .show(loraFragment)
                .hide(generalFragment)
                .hide(bleFragment)
                .hide(deviceFragment)
                .commit();
        showSyncingProgressDialog();
        List<OrderTask> orderTasks = new ArrayList<>();
        // get lora params
        orderTasks.add(OrderTaskAssembler.getLoraRegion());
        orderTasks.add(OrderTaskAssembler.getLoraUploadMode());
        orderTasks.add(OrderTaskAssembler.getLoraNetworkStatus());
        LoRaLW007MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }

    public void onChangePassword(View view) {
        if (isWindowLocked())
            return;
        if (noPassword)
            return;
        final ChangePasswordDialog dialog = new ChangePasswordDialog(this);
        dialog.setOnPasswordClicked(password -> {
            showSyncingProgressDialog();
            LoRaLW007MokoSupport.getInstance().sendOrder(OrderTaskAssembler.setNewPassword(password));
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

    // lora
    public void onLoRaConnSetting(View view) {
        if (isWindowLocked())
            return;
        Intent intent = new Intent(this, LoRaConnSettingActivity.class);
        startActivityForResult(intent, AppConstants.REQUEST_CODE_LORA_CONN_SETTING);
    }

    public void onLoRaAppSetting(View view) {
        if (isWindowLocked())
            return;
        Intent intent = new Intent(this, LoRaAppSettingActivity.class);
        startActivity(intent);
    }

    // general
    public void onPIRSettings(View view) {
        if (isWindowLocked())
            return;
        Intent intent = new Intent(this, PIRSettingsActivity.class);
        startActivity(intent);
    }

    public void onHallSettings(View view) {
        if (isWindowLocked())
            return;
        Intent intent = new Intent(this, HallSettingsActivity.class);
        startActivity(intent);
    }

    public void onTHSettings(View view) {
        if (isWindowLocked())
            return;
        Intent intent = new Intent(this, THSettingsActivity.class);
        startActivity(intent);
    }

    // ble
    public void onConnectable(View view) {
        if (isWindowLocked())
            return;
        bleFragment.changeConnectable();
    }

    public void onChangeLoginMode(View view) {
        if (isWindowLocked())
            return;
        bleFragment.changeLoginMode();
    }

    // device
//    public void selectLowPowerPrompt(View view) {
//        if (isWindowLocked())
//            return;
//        deviceFragment.showLowPowerDialog();
//    }

    public void onLowPowerPayload(View view) {
        if (isWindowLocked())
            return;
        deviceFragment.changeLowPowerPayload();
    }

    public void onPowerOff(View view) {
        if (isWindowLocked())
            return;
        AlertMessageDialog dialog = new AlertMessageDialog();
        dialog.setTitle("Warning!");
        dialog.setMessage("Are you sure to turn off the device? Please make sure the device has a button to turn on!");
        dialog.setConfirm("OK");
        dialog.setOnAlertConfirmListener(() -> {
            showSyncingProgressDialog();
            LoRaLW007MokoSupport.getInstance().sendOrder(OrderTaskAssembler.setPowerOff());
        });
        dialog.show(getSupportFragmentManager());
    }

    public void selectTimeZone(View view) {
        if (isWindowLocked())
            return;
        deviceFragment.showTimeZoneDialog();
    }

    // device
    public void onDeviceInfo(View view) {
        if (isWindowLocked())
            return;
        startActivityForResult(new Intent(this, SystemInfoActivity.class), AppConstants.REQUEST_CODE_SYSTEM_INFO);
    }

    public void onFactoryReset(View view) {
        if (isWindowLocked())
            return;
        AlertMessageDialog dialog = new AlertMessageDialog();
        dialog.setTitle("Factory Reset");
        dialog.setMessage("After factory reset,all the data will be reseted to the factory values.");
        dialog.setConfirm("OK");
        dialog.setOnAlertConfirmListener(() -> {
            showSyncingProgressDialog();
            LoRaLW007MokoSupport.getInstance().sendOrder(OrderTaskAssembler.restore());
        });
        dialog.show(getSupportFragmentManager());
    }


    public void onBatteryReset(View view) {
        if (isWindowLocked())
            return;
        AlertMessageDialog dialog = new AlertMessageDialog();
        dialog.setTitle("Warning！");
        dialog.setMessage("Are you sure to reset battery?");
        dialog.setConfirm("OK");
        dialog.setOnAlertConfirmListener(() -> {
            showSyncingProgressDialog();
            LoRaLW007MokoSupport.getInstance().sendOrder(OrderTaskAssembler.setBatteryReset());
        });
        dialog.show(getSupportFragmentManager());
    }
}
