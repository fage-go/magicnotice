package com.zhangshuo.magicnotice.ui.dialog;

import android.text.TextUtils;
import android.view.View;

import com.zhangshuo.common.dialog.BaseBottomDialog;
import com.zhangshuo.common.toast.ToastManager;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.databinding.DialogRandomGroupTxtBinding;
import com.zhangshuo.magicnotice.db.model.TxtClassifyData;
import com.zhangshuo.magicnotice.db.service.TxtDataService;
import com.zhangshuo.magicnotice.ui.model.TxtClassifyInfo;

public class RandomGroupTxtDialog extends BaseBottomDialog<DialogRandomGroupTxtBinding> {
    private TxtClassifyInfo txtClassifyInfo;
    private OnCallback onCallback;

    public RandomGroupTxtDialog setTxtClassifyInfo(TxtClassifyInfo txtClassifyInfo) {
        this.txtClassifyInfo = txtClassifyInfo;
        return this;
    }

    public RandomGroupTxtDialog setOnCallback(OnCallback onCallback) {
        this.onCallback = onCallback;
        return this;
    }

    @Override
    public void initView() {
        binding.tvTitleTip.getPaint().setFakeBoldText(true);
        if (txtClassifyInfo != null) {
            binding.etTitle.setText(txtClassifyInfo.getTitle());
            binding.tvDelete.setVisibility(View.VISIBLE);
            binding.mbNext.setText(R.string.random_group_txt_save);
        } else {
            binding.tvDelete.setVisibility(View.GONE);
            binding.mbNext.setText(R.string.random_group_txt_new);
        }
        binding.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtClassifyInfo != null) {
                    TxtDataService.deleteClassify(txtClassifyInfo.getId(), new AsyncCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean result) {
                            if (onCallback != null) {
                                onCallback.onHaveDelete();
                            }
                            close();
                        }
                    });
                }
            }
        });
        binding.mbNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });
    }

    private void saveData() {
        String title = binding.etTitle.getText().toString();
        if (TextUtils.isEmpty(title)) {
            ToastManager.get().showShortToast(R.string.random_group_txt_no_name);
            return;
        }

        TxtClassifyData txtClassifyData = new TxtClassifyData();
        txtClassifyData.setTitle(title);
        if (txtClassifyInfo != null) {
            txtClassifyData.setId(txtClassifyInfo.getId());
        }
        TxtDataService.insertClassify(txtClassifyData, new AsyncCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (onCallback != null) {
                    onCallback.onHaveAdd();
                }
                close();
            }
        });
    }

    public interface OnCallback {
        void onHaveAdd();

        void onHaveDelete();
    }
}
