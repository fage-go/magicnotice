package com.zhangshuo.common.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zhangshuo.common.R;
import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.common.databinding.DialogBottomOptionsBinding;

import java.util.List;

public class BottomOptionDialog extends BaseBottomSheetDialog<DialogBottomOptionsBinding> {

    public static void create(Context context, List<String> options, OnOptionCallback onOptionCallback) {
        BottomOptionDialog bottomOptionDialog = new BottomOptionDialog();
        bottomOptionDialog.options = options;
        bottomOptionDialog.onOptionCallback = onOptionCallback;
        bottomOptionDialog.show(context);
    }

    private List<String> options;
    private OnOptionCallback onOptionCallback;


    @Override
    public void initView() {
        binding.rvOptions.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        BaseRAdapter<String> adapter = new BaseRAdapter<String>(getContext()) {
            @Override
            protected void onConvertView(ViewHolder viewHolder, String s, int position) {
                TextView tvOptions = (TextView) viewHolder.$(R.id.tv_options);
                tvOptions.setText(options.get(position));
            }

            @Override
            protected int onSetItemViewType(String s, int position) {
                return 0;
            }

            @Override
            protected int onBindView(int viewType) {
                return R.layout.item_bottom_options;
            }
        };
        adapter.setOnItemClickListener(new BaseRAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                dismiss();
                if (onOptionCallback != null) {
                    onOptionCallback.onClick(position);
                }
            }
        });
        adapter.getDataList().addAll(options);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), RecyclerView.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.item_line_drawable));
        binding.rvOptions.addItemDecoration(dividerItemDecoration);
        binding.rvOptions.setAdapter(adapter);
        binding.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (onOptionCallback != null) {
                    onOptionCallback.onCancel();
                }
            }
        });
    }

    public interface OnOptionCallback {
        void onClick(int position);

        void onCancel();
    }
}
