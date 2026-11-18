package com.zhangshuo.magicnotice.ui.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.config.CacheMemory;
import com.zhangshuo.magicnotice.ui.adapter.TxtConditionsSelectAdapter;
import com.zhangshuo.magicnotice.ui.model.TxtConditionsSelectInfo;

import java.util.Map;

public class TxtConditionsSelectPopup extends PopupWindow {
    private RecyclerView rv1, rv2;
    private TxtConditionsSelectAdapter adapter1;
    private TxtConditionsSelectAdapter adapter2;
    private OnCallback onCallback;

    public TxtConditionsSelectPopup(Context context, OnCallback callback) {
        super(context);
        View contentView = LayoutInflater.from(context).inflate(R.layout.popup_txt_conditions_select, null);
        setContentView(contentView);
        setWidth(-2);
        setHeight(-2);
        setFocusable(true);
        setTouchable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.onCallback = callback;
        rv1 = contentView.findViewById(R.id.rv_options_1);
        rv2 = contentView.findViewById(R.id.rv_options_2);
        adapter1 = new TxtConditionsSelectAdapter(context);
        adapter2 = new TxtConditionsSelectAdapter(context);
        rv1.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        rv2.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        rv1.setAdapter(adapter1);
        rv2.setAdapter(adapter2);

        for (Map.Entry<String, String> entry : CacheMemory.getTxtJudgeMap().entrySet()) {
            if (entry.getKey().contains("*")) {
                adapter2.getDataList().add(new TxtConditionsSelectInfo(entry.getValue(), entry.getKey()));
            } else {
                adapter1.getDataList().add(new TxtConditionsSelectInfo(entry.getValue(), entry.getKey()));
            }
        }
        adapter1.notifyDataSetChanged();
        adapter2.notifyDataSetChanged();
        adapter1.setOnItemClickListener(new BaseRAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                for (int i = 0; i < adapter1.getItemCount(); i++) {
                    TxtConditionsSelectInfo item = adapter1.getItem(i);
                    if (i == position) {
                        item.setSelect(true);
                        adapter1.notifyItemChanged(i);
                    } else {
                        if (item.isSelect()) {
                            item.setSelect(false);
                            adapter1.notifyItemChanged(i);
                        }
                    }
                }
                afterSelect();
            }
        });
        adapter2.setOnItemClickListener(new BaseRAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                for (int i = 0; i < adapter2.getItemCount(); i++) {
                    TxtConditionsSelectInfo item = adapter2.getItem(i);
                    if (i == position) {
                        item.setSelect(true);
                        adapter2.notifyItemChanged(i);
                    } else {
                        if (item.isSelect()) {
                            item.setSelect(false);
                            adapter2.notifyItemChanged(i);
                        }
                    }
                }
                afterSelect();
            }
        });

        contentView.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                afterSelect();
                dismiss();
            }
        });
    }

    public void haveSelect(String code) {
        String key[] = code.split("-");
        for (int i = 0; i < adapter1.getItemCount(); i++) {
            TxtConditionsSelectInfo item = adapter1.getItem(i);
            item.setSelect(item.getValue().equals(key[0]));
        }

        for (int i = 0; i < adapter2.getItemCount(); i++) {
            TxtConditionsSelectInfo item = adapter2.getItem(i);
            item.setSelect(item.getValue().equals(key[1]));
        }
        adapter1.notifyDataSetChanged();
        adapter2.notifyDataSetChanged();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    private void afterSelect() {
        String name = "";
        String value = "";
        for (int i = 0; i < adapter1.getItemCount(); i++) {
            TxtConditionsSelectInfo item = adapter1.getItem(i);
            if (item.isSelect()) {
                name = item.getName();
                value = item.getValue();
                break;
            }
        }
        for (int i = 0; i < adapter2.getItemCount(); i++) {
            TxtConditionsSelectInfo item = adapter2.getItem(i);
            if (item.isSelect()) {
                name = name + "-" + item.getName();
                value = value + "-" + item.getValue();
                break;
            }
        }

        if (onCallback != null) {
            onCallback.onSelect(name, value);
        }
    }

    public interface OnCallback {
        void onSelect(String name, String value);
    }
}
