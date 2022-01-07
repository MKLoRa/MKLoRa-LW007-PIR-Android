package com.moko.lw007.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.moko.lw007.R;
import com.moko.lw007.entity.ExportData;

public class ExportDataListAdapter extends BaseQuickAdapter<ExportData, BaseViewHolder> {

    public ExportDataListAdapter() {
        super(R.layout.lw007_item_export_data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ExportData item) {
        helper.setText(R.id.tv_time, item.name);
        helper.setImageResource(R.id.iv_checked, item.isSelected ? R.drawable.lw007_ic_selected : R.drawable.lw007_ic_unselected);
    }
}
