package com.moko.lw007.adapter;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.TextView;

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
        helper.setText(R.id.tv_voltage, String.format("%s V", MokoUtils.getDecimalFormat("0.0").format(item.voltage * 0.1)));
        helper.setText(R.id.tv_current, String.format("%s A", MokoUtils.getDecimalFormat("0.000").format(item.current * 0.001)));
        helper.setText(R.id.tv_power, String.format("%s W", MokoUtils.getDecimalFormat("0.0").format(item.power * 0.1)));
        helper.setText(R.id.tv_power_factor, String.format("%d %%", item.powerFactor));
        helper.setText(R.id.tv_current_rate, String.format("%s HZ", MokoUtils.getDecimalFormat("0.0").format(item.currentRate * 0.001)));
        TextView view = helper.getView(R.id.tv_load_status);
        Drawable left = mContext.getResources().getDrawable(R.drawable.lw007_ic_overload);
        view.setCompoundDrawablesWithIntrinsicBounds(left, null, null, null);
        if ((item.loadState & 0x04) == 0x04) {
            view.setText("OverLoad");
        } else if ((item.loadState & 0x08) == 0x08) {
            view.setText("OverCurrent");
        } else if ((item.loadState & 0x10) == 0x10) {
            view.setText("SagVoltage");
        } else if ((item.loadState & 0x20) == 0x20) {
            view.setText("OverVoltage");
        } else if ((item.loadState & 0x80) == 0x80) {
            view.setText("ON");
            Drawable on = mContext.getResources().getDrawable(R.drawable.lw007_ic_load_open);
            view.setCompoundDrawablesWithIntrinsicBounds(on, null, null, null);
        } else {
            view.setText("OFF");
            Drawable off = mContext.getResources().getDrawable(R.drawable.lw007_ic_load_close);
            view.setCompoundDrawablesWithIntrinsicBounds(off, null, null, null);
        }
        helper.setVisible(R.id.tv_connect, item.connectable);
        helper.addOnClickListener(R.id.tv_connect);
    }
}
