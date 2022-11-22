package com.moko.lw007.adapter;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.moko.ble.lib.utils.MokoUtils;
import com.moko.lw007.R;
import com.moko.lw007.entity.AdvInfo;

public class DeviceListAdapter extends BaseQuickAdapter<AdvInfo, BaseViewHolder> {
    public DeviceListAdapter() {
        super(R.layout.lw007_list_item_device);
    }

    @Override
    protected void convert(BaseViewHolder helper, AdvInfo item) {
        final String rssi = String.format("%ddBm", item.rssi);
        helper.setText(R.id.tv_rssi, rssi);
        final String name = TextUtils.isEmpty(item.name) ? "N/A" : item.name;
        helper.setText(R.id.tv_name, name);
        helper.setText(R.id.tv_mac, String.format("MAC:%s", item.mac));

        final String intervalTime = item.intervalTime == 0 ? "<->N/A" : String.format("<->%dms", item.intervalTime);
        helper.setText(R.id.tv_track_interval, intervalTime);
        helper.setText(R.id.tv_tx_power, String.format("Tx Power:%ddBm", item.txPower));
//        helper.setText(R.id.tv_voltage, String.format("%s V", MokoUtils.getDecimalFormat("0.0").format(item.voltage)));
        helper.setVisible(R.id.tv_temp, item.isShowTH);
        helper.setVisible(R.id.tv_humidity, item.isShowTH);
        helper.setText(R.id.tv_temp, String.format("%s ℃", MokoUtils.getDecimalFormat("0.0").format(item.temp)));
        helper.setText(R.id.tv_humidity, String.format("%s%%RH", MokoUtils.getDecimalFormat("0.0").format(item.humidity)));
        helper.setText(R.id.tv_battery, item.battery == 0 ? "Normal" : "Low");
        helper.setVisible(R.id.tv_connect, item.connectable);
        helper.addOnClickListener(R.id.tv_connect);
    }
}
