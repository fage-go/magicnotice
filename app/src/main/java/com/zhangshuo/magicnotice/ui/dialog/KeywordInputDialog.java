package com.zhangshuo.magicnotice.ui.dialog;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;

import com.google.android.flexbox.FlexboxLayoutManager;
import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.common.dialog.BaseBottomDialog;
import com.zhangshuo.common.toast.ToastManager;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.databinding.DialogKeywordInputBinding;
import com.zhangshuo.magicnotice.ui.adapter.KeyWordInfoAdapter;
import com.zhangshuo.magicnotice.ui.model.KeywordInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import razerdp.basepopup.BasePopupWindow;

public class KeywordInputDialog extends BaseBottomDialog<DialogKeywordInputBinding> {
    private OnCallback onCallback;
    private List<KeywordInfo> haveInputList = new ArrayList<>();
    private KeyWordInfoAdapter keyWordInfoAdapter;

    public KeywordInputDialog() {

    }

    public void setHaveInputList(List<KeywordInfo> haveInputList) {
        this.haveInputList = haveInputList;
    }

    public void setOnCallback(OnCallback onCallback) {
        this.onCallback = onCallback;
    }

    @Override
    public void initView() {
        binding.rvTag.setLayoutManager(new FlexboxLayoutManager(getContext()));
        keyWordInfoAdapter = new KeyWordInfoAdapter(getContext());
        binding.rvTag.setAdapter(keyWordInfoAdapter);
        keyWordInfoAdapter.setOnItemClickListener(new BaseRAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                keyWordInfoAdapter.removeItem(position);
            }
        });
        binding.circleColorView.setColor("#0081FF");
        binding.circleColorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SelectColorPopup(KeywordInputDialog.this,
                        binding.circleColorView.getColorHex())
                        .setOnCallback(new SelectColorPopup.OnSelectCallback() {
                            @Override
                            public void onSelect(String color) {
                                binding.circleColorView.setColor(color);
                            }
                        }, new BasePopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {

                            }
                        })
                        .setPopupFadeEnable(true)
                        .setPopupGravity(Gravity.TOP | Gravity.CENTER)
                        .showPopupWindow(v);
            }
        });
        binding.tvTagAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = binding.etTag.getText().toString();
                if (text.contains("、")) {
                    ToastManager.get().noticeShortMessage(R.string.dialog_keyword_input_reject);
                    return;
                }
                if (!TextUtils.isEmpty(text)) {
                    keyWordInfoAdapter.addData(Arrays.asList(new KeywordInfo(text, binding.circleColorView.getColorHex())));
                } else {
                    ToastManager.get().noticeShortMessage(R.string.dialog_keyword_input_no);
                }
                binding.etTag.setText("");
            }
        });

        binding.tvAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
                if (onCallback != null) {
                    onCallback.onResult(new ArrayList<>());
                }
            }
        });
        binding.tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
                if (onCallback != null) {
                    onCallback.onResult(keyWordInfoAdapter.getDataList());
                }
            }
        });
        keyWordInfoAdapter.addData(haveInputList, true);
    }

    public interface OnCallback {
        void onResult(List<KeywordInfo> haveInputList);
    }
}
