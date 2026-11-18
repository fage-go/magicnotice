package com.zhangshuo.magicnotice.ui.dialog;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;

import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.common.dialog.BaseBottomSheetDialog;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.databinding.DialogSelectGradualColorBinding;
import com.zhangshuo.magicnotice.ui.view.GradualColorView;

import java.util.List;

import razerdp.basepopup.BasePopupWindow;

public class SelectGradualColorDialog extends BaseBottomSheetDialog<DialogSelectGradualColorBinding> {
    private String selectColor;
    private OnSelectCallback onSelectCallback;
    private boolean autoDismiss = true;
    private List<String> colorList;
    private BaseRAdapter dataAdapter;

    public static SelectGradualColorDialog create() {
        SelectGradualColorDialog selectColorDialog = new SelectGradualColorDialog();
        return selectColorDialog;
    }

    public SelectGradualColorDialog setSelectColor(String selectColor) {
        this.selectColor = selectColor;
        return this;
    }

    public SelectGradualColorDialog setColorList(List<String> colorList) {
        this.colorList = colorList;
        return this;
    }


    public void setAutoDismiss(boolean autoDismiss) {
        this.autoDismiss = autoDismiss;
    }


    public SelectGradualColorDialog setOnSelectCallback(OnSelectCallback onSelectCallback) {
        this.onSelectCallback = onSelectCallback;
        return this;
    }

    @Override
    public void initView() {
        dataAdapter = new BaseRAdapter<String>(getContext()) {
            @Override
            protected void onConvertView(ViewHolder viewHolder, String s, int position) {
                GradualColorView gradualColorView = (GradualColorView) viewHolder.$(R.id.gradual_color_view);
                TextView tvSelect = (TextView) viewHolder.$(R.id.tv_color_select);
                gradualColorView.setColor(s);
                if (TextUtils.isEmpty(s)) {
                    tvSelect.setVisibility(View.VISIBLE);
                } else {
                    tvSelect.setVisibility(View.GONE);
                }
            }

            @Override
            protected int onSetItemViewType(String s, int position) {
                return 0;
            }


            @Override
            protected int onBindView(int viewType) {
                return R.layout.item_select_gradual_color;
            }
        };
        binding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        binding.recyclerView.setAdapter(dataAdapter);
        dataAdapter.addData(colorList, true);
        dataAdapter.setOnItemClickListener(new BaseRAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                selectColor = colorList.get(position);
                if (TextUtils.isEmpty(selectColor)) {
                    selectSingleColor();
                } else {
                    if (onSelectCallback != null) {
                        onSelectCallback.onSelect(selectColor);
                    }
                    if (autoDismiss) {
                        dismiss();
                    }
                }
            }
        });

    }

    private void selectSingleColor() {
        new SelectColorPopup(getDialog(), "")
                .canNone(true)
                .setOnCallback(new SelectColorPopup.OnSelectCallback() {
                    @Override
                    public void onSelect(String color) {
                        selectColor = color;
                        if (onSelectCallback != null) {
                            onSelectCallback.onSelect(selectColor);
                        }
                        if (autoDismiss) {
                            dismiss();
                        }
                    }
                }, new BasePopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {

                    }
                })
                .setPopupFadeEnable(true)
                .setPopupGravity(Gravity.BOTTOM | Gravity.CENTER)
                .showPopupWindow(binding.recyclerView);
    }

    public interface OnSelectCallback {
        void onSelect(String color);
    }
}
