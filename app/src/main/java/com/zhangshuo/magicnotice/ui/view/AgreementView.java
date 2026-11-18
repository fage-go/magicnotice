package com.zhangshuo.magicnotice.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zhangshuo.common.router.RouterUtil;
import com.zhangshuo.common.toast.ToastManager;
import com.zhangshuo.common.view.CheckView;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.config.RoutePaths;

import java.io.Serializable;
import java.util.List;

public class AgreementView extends FrameLayout {
    private CheckView checkView;
    private TextView textView;

    /**
     * @param defaultChecked         是否默认勾选
     * @param protocol               协议的文案 包含协议名称 如 已阅读并同意《玩物社区公约》
     * @param agreementInfoModelList
     */
    public void setAgreement(boolean defaultChecked, String protocol, List<AgreementInfoModel> agreementInfoModelList) {
        checkView.setChecked(defaultChecked);
        SpannableString protocolString = new SpannableString(protocol);
        for (int i = 0; i < agreementInfoModelList.size(); i++) {
            AgreementInfoModel agreementInfoModel = agreementInfoModelList.get(i);
            String name = agreementInfoModel.getTitle();
            if (TextUtils.isEmpty(name)) continue;
            int startIndex = protocol.indexOf(name);
            int endIndex = startIndex + name.length();
            protocolString.setSpan(new ClickableSpanBlue() {
                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setUnderlineText(false);
                    ds.setColor(agreementInfoModel.getTxtColor());
                }

                @Override
                public void onClick(@NonNull View widget) {
                    Bundle bundle = new Bundle();
                    bundle.putString("url", agreementInfoModel.getContentUrl());
                    bundle.putString("title", agreementInfoModel.getTitle());
                    RouterUtil.get().navigation(RoutePaths.APP_ACTIVITY_H5, bundle);
                }
            }, startIndex, endIndex, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(protocolString);
    }

    public void showCheckBox(boolean show) {
        checkView.setVisibility(show ? VISIBLE : GONE);
    }

    public boolean isAgree() {
        return getVisibility() != VISIBLE || checkView.isChecked();
    }

    public void showAgreeTip() {
        ToastManager.get().showShortToast(textView.getText().toString());
        startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.protocol_shake_anim));
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_agreenment, this);
        checkView = findViewById(R.id.cb_protocol_agree);
        textView = findViewById(R.id.tv_protocol_text);
    }

    public AgreementView(@NonNull Context context) {
        super(context);
        init();
    }

    public AgreementView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AgreementView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public AgreementView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Keep
    public static class AgreementInfoModel implements Serializable {
        private String title;
        private String contentUrl;
        private int txtColor = Color.parseColor("#2792C3");


        public int getTxtColor() {
            return txtColor == 0 ? Color.parseColor("#2792C3") : txtColor;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContentUrl() {
            return contentUrl;
        }

        public void setContentUrl(String contentUrl) {
            this.contentUrl = contentUrl;
        }

        public void setTxtColor(int txtColor) {
            this.txtColor = txtColor;
        }
    }
}