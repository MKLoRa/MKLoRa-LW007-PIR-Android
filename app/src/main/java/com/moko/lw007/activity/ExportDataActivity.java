package com.moko.lw007.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.moko.ble.lib.MokoConstants;
import com.moko.ble.lib.event.ConnectStatusEvent;
import com.moko.ble.lib.event.OrderTaskResponseEvent;
import com.moko.ble.lib.task.OrderTaskResponse;
import com.moko.lw007.AppConstants;
import com.moko.lw007.R;
import com.moko.lw007.R2;
import com.moko.lw007.adapter.ExportDataListAdapter;
import com.moko.lw007.dialog.AlertMessageDialog;
import com.moko.lw007.entity.ExportData;
import com.moko.lw007.utils.Utils;
import com.moko.support.lw007.LoRaLW007MokoSupport;
import com.moko.support.lw007.entity.OrderCHAR;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ExportDataActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener {

    public static String TAG = ExportDataActivity.class.getSimpleName();
    @BindView(R2.id.tv_sync_switch)
    TextView tvSyncSwitch;
    @BindView(R2.id.iv_sync)
    ImageView ivSync;
    @BindView(R2.id.tv_export)
    TextView tvExport;
    @BindView(R2.id.tv_empty)
    TextView tvEmpty;
    @BindView(R2.id.rv_export_data)
    RecyclerView rvExportData;
    private StringBuilder storeString;
    private ArrayList<ExportData> exportDatas;
    private boolean isSync;
    private ExportDataListAdapter adapter;
    private String logDirPath;
    private String mDeviceMac;
    private int selectedCount;
    private String syncTime;
    private Animation animation = null;
    private boolean isDisconnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lw007_activity_export_data);
        ButterKnife.bind(this);
        mDeviceMac = getIntent().getStringExtra(AppConstants.EXTRA_KEY_DEVICE_MAC).replaceAll(":", "");
        logDirPath = LoRaLW007MainActivity.PATH_LOGCAT + File.separator + mDeviceMac;
        exportDatas = new ArrayList<>();
        adapter = new ExportDataListAdapter();
        adapter.openLoadAnimation();
        adapter.replaceData(exportDatas);
        adapter.setOnItemClickListener(this);
        rvExportData.setLayoutManager(new LinearLayoutManager(this));
        rvExportData.setAdapter(adapter);
        EventBus.getDefault().register(this);
        File file = new File(logDirPath);
        if (file.exists()) {
            File[] logFiles = file.listFiles();
            for (int i = 0, l = logFiles.length; i < l; i++) {
                File logFile = logFiles[i];
                ExportData data = new ExportData();
                data.filePath = logFile.getAbsolutePath();
                data.name = logFile.getName().replaceAll(".txt", "");
                exportDatas.add(data);
            }
            adapter.replaceData(exportDatas);
        }
        // 点击无效间隔改为1秒
        voidDuration = 1000;
        storeString = new StringBuilder();
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 300)
    public void onConnectStatusEvent(ConnectStatusEvent event) {
        final String action = event.getAction();
        EventBus.getDefault().cancelEventDelivery(event);
        runOnUiThread(() -> {
            if (MokoConstants.ACTION_DISCONNECTED.equals(action)) {
                isDisconnected = true;
                // 中途断开，要先保存数据
                tvSyncSwitch.setEnabled(false);
                if (isSync)
                    stopSync();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 300)
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
                    case CHAR_LOG:
                        final int length = value.length;
                        if (length > 0) {
                            String log = new String(value);
                            storeString.append(log);
                        }
                        break;
                }
            }
        });
    }


    public void onSyncSwitch(View view) {
        if (isWindowLocked())
            return;
        int size = exportDatas.size();
        if (size >= 10) {
            AlertMessageDialog dialog = new AlertMessageDialog();
            dialog.setTitle("Tips");
            dialog.setMessage("Up to 10 log files can be stored, please delete the useless logs first！");
            dialog.setConfirm("OK");
            dialog.setCancelGone();
            dialog.show(getSupportFragmentManager());
            return;
        }
        if (animation == null) {
            tvSyncSwitch.setText("Stop");
            isSync = true;
            animation = AnimationUtils.loadAnimation(this, R.anim.lw007_rotate_refresh);
            ivSync.startAnimation(animation);
            LoRaLW007MokoSupport.getInstance().enableLogNotify();
            Calendar calendar = Calendar.getInstance();
            syncTime = Utils.calendar2strDate(calendar, "yyyy-MM-dd HH-mm-ss");
        } else {
            LoRaLW007MokoSupport.getInstance().disableLogNotify();
            stopSync();
        }
    }

    public void writeLogFile2SDCard(String filePath) {
        String log = storeString.toString();
        File file = new File(filePath);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(log);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onEmpty(View view) {
        if (isWindowLocked())
            return;
        AlertMessageDialog dialog = new AlertMessageDialog();
        dialog.setTitle("Warning!");
        dialog.setMessage("Are you sure to empty the saved debugger log?");
        dialog.setOnAlertConfirmListener(() -> {
            Iterator<ExportData> iterator = exportDatas.iterator();
            while (iterator.hasNext()) {
                ExportData exportData = iterator.next();
                if (!exportData.isSelected)
                    continue;
                File file = new File(exportData.filePath);
                if (file.exists())
                    file.delete();
                iterator.remove();
                selectedCount--;
            }
            if (selectedCount > 0) {
                tvEmpty.setEnabled(true);
                tvExport.setEnabled(true);
            } else {
                tvEmpty.setEnabled(false);
                tvExport.setEnabled(false);
            }
            adapter.replaceData(exportDatas);
        });
        dialog.show(getSupportFragmentManager());
    }

    public void onExport(View view) {
        if (isWindowLocked())
            return;
        ArrayList<File> selectedFiles = new ArrayList<>();
        for (ExportData exportData : exportDatas) {
            if (exportData.isSelected) {
                selectedFiles.add(new File(exportData.filePath));
            }
        }
        if (!selectedFiles.isEmpty()) {
            File[] files = selectedFiles.toArray(new File[]{});
            // 发送邮件
            String address = "Development@mokotechnology.com";
            String title = "Debugger Log";
            String content = title;
            Utils.sendEmail(ExportDataActivity.this, address, content, title, "Choose Email Client", files);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void backHome() {
        if (isSync) {
            LoRaLW007MokoSupport.getInstance().disableLogNotify();
            stopSync();
        } else {
            if (isDisconnected) {
                Intent intent = new Intent(this, LoRaLW007MainActivity.class);
                intent.putExtra(AppConstants.EXTRA_KEY_FROM_ACTIVITY, TAG);
                startActivity(intent);
                return;
            }
            finish();
        }
    }

    private void stopSync() {
        tvSyncSwitch.setText("Start");
        isSync = false;
        // 关闭通知
        ivSync.clearAnimation();
        animation = null;
        if (storeString.length() == 0) {
            AlertMessageDialog dialog = new AlertMessageDialog();
            dialog.setTitle("Tips");
            dialog.setMessage("No debug logs are sent during this process！");
            dialog.setConfirm("OK");
            dialog.setCancelGone();
            dialog.show(getSupportFragmentManager());
            return;
        }
        File logDir = new File(logDirPath);
        if (!logDir.exists())
            logDir.mkdirs();
        String logFilePath = logDirPath + File.separator + String.format("%s.txt", syncTime);
        writeLogFile2SDCard(logFilePath);
        ExportData exportData = new ExportData();
        exportData.name = syncTime;
        exportData.filePath = logFilePath;
        exportDatas.add(exportData);
        adapter.replaceData(exportDatas);
    }

    @Override
    public void onBackPressed() {
        backHome();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        ExportData exportData = (ExportData) adapter.getItem(position);
        if (exportData != null) {
            exportData.isSelected = !exportData.isSelected;
            if (exportData.isSelected) {
                selectedCount++;
            } else {
                selectedCount--;
            }
            if (selectedCount > 0) {
                tvEmpty.setEnabled(true);
                tvExport.setEnabled(true);
            } else {
                tvEmpty.setEnabled(false);
                tvExport.setEnabled(false);
            }
            adapter.notifyItemChanged(position);
        }
    }

    public void onBack(View view) {
        if (isWindowLocked())
            return;
        backHome();
    }
}
