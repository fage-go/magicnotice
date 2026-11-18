package com.zhangshuo.magicnotice.ui.dialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;

import com.zhangshuo.common.dialog.BaseBottomDialog;
import com.zhangshuo.common.dialog.CommonDialog;
import com.zhangshuo.common.model.EventMessage;
import com.zhangshuo.common.router.RouterUtil;
import com.zhangshuo.common.toast.ToastManager;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.common.util.EventBusUtil;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.config.EventCodes;
import com.zhangshuo.magicnotice.config.RoutePaths;
import com.zhangshuo.magicnotice.databinding.DialogRandomTxtBinding;
import com.zhangshuo.magicnotice.db.model.TxtData;
import com.zhangshuo.magicnotice.db.service.TxtDataService;

public class RandomTxtDialog extends BaseBottomDialog<DialogRandomTxtBinding> {
    private int classifyId;

    public RandomTxtDialog setClassifyId(int classifyId) {
        this.classifyId = classifyId;
        return this;
    }

    @Override
    public void initView() {
        binding.tvTitleTip.getPaint().setFakeBoldText(true);
        binding.tvImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //导入
                openImportDialog();
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
        String content = binding.etContent.getText().toString();
        if (TextUtils.isEmpty(content)) {
            ToastManager.get().showShortToast(R.string.random_txt_no_content);
            return;
        }
        if (TextUtils.isEmpty(title)) {
            title = StringUtil.getString(R.string.app_name);
        }
        TxtData txtData = new TxtData();
        txtData.setClassifyId(classifyId);
        txtData.setTitle(title);
        txtData.setContent(content);
        TxtDataService.insert(txtData, new AsyncCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                EventBusUtil.sendEvent(new EventMessage<>(EventCodes.IMPORT_RANDOM_TXT));
                close();
            }
        });
    }

    private void openImportDialog() {
        new CommonDialog().setTitle(StringUtil.getString(R.string.random_txt_import_title))
                .setContentGravity(Gravity.LEFT)
                .setContent(StringUtil.getString(R.string.random_txt_import_content))
                .setCommonAction(StringUtil.getString(R.string.random_txt_import_action))
                .setCommonActionListener(new CommonDialog.ActionListener() {
                    @Override
                    public void onClick(CommonDialog dialog) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("classifyId", classifyId);
                        RouterUtil.get().navigation(RoutePaths.APP_ACTIVITY_IMPORT_TXT, bundle);
                        close();
                    }
                })
                .show(getContext());
    }
}
