package com.zhangshuo.magicnotice.ui.dialog;

import android.view.View;

import com.zhangshuo.common.dialog.BaseTipsDialog;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.config.RoutePaths;
import com.zhangshuo.magicnotice.databinding.DialogPrivacyBinding;
import com.zhangshuo.magicnotice.ui.view.AgreementView;
import com.zhangshuo.magicnotice.util.ConfigInit;

import java.util.ArrayList;
import java.util.List;

public class PrivacyDialog extends BaseTipsDialog<DialogPrivacyBinding> {
    private OnPrivacyCallback onPrivacyCallback;

    public void setOnPrivacyCallback(OnPrivacyCallback onPrivacyCallback) {
        this.onPrivacyCallback = onPrivacyCallback;
    }

    @Override
    public void initView() {
        List<AgreementView.AgreementInfoModel> agreementInfoModelList = new ArrayList<>();

        if (ConfigInit.isBuildTypeZH()) {
            AgreementView.AgreementInfoModel agreement1 = new AgreementView.AgreementInfoModel();
            agreement1.setTitle(getString(R.string.agreement_title_user_privacy));
            agreement1.setContentUrl(RoutePaths.APP_USER_PRIVACY_PATH);
            agreementInfoModelList.add(agreement1);
        }

        AgreementView.AgreementInfoModel agreement2 = new AgreementView.AgreementInfoModel();
        agreement2.setTitle(getString(R.string.agreement_title_privacy));
        if (ConfigInit.isBuildTypeZH()) {
            agreement2.setContentUrl(RoutePaths.APP_PRIVACY_PATH);
        } else {
            agreement2.setContentUrl(RoutePaths.APP_PRIVACY_EN_PATH);
        }
        agreementInfoModelList.add(agreement2);


        binding.agreementView.showCheckBox(false);
        binding.agreementView.setAgreement(true, getString(R.string.agreement_privacy_agree), agreementInfoModelList);

        binding.tvExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
                if (onPrivacyCallback != null) {
                    onPrivacyCallback.onOpt(false);
                }
            }
        });
        binding.tvPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
                if (onPrivacyCallback != null) {
                    onPrivacyCallback.onOpt(true);
                }
            }
        });
    }

    @Override
    protected boolean canCancel() {
        return false;
    }

    public interface OnPrivacyCallback {
        void onOpt(boolean agree);
    }
}