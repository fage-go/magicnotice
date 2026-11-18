package com.zhangshuo.magicnotice.ui.dialog;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.zhangshuo.common.dialog.BaseBottomDialog;
import com.zhangshuo.common.listener.OnClickCallback;
import com.zhangshuo.common.toast.ToastManager;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.databinding.DialogShowEmailBinding;

public class ShowEmailDialog extends BaseBottomDialog<DialogShowEmailBinding> {
    private String extTip;

    public ShowEmailDialog setExtTip(String extTip) {
        this.extTip = extTip;
        return this;
    }

    @Override
    public void initView() {
        binding.etTitle.setText(extTip);
        binding.tvCopyEmail.setOnClickListener(new OnClickCallback() {
            @Override
            public void click(View v) {
                ClipboardManager tvCopy = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                tvCopy.setText(StringUtil.getString(R.string.app_email_address));
                ToastManager.get().showShortToast(R.string.show_email_copy_tip);
            }
        });
        binding.mbOpen.setOnClickListener(new OnClickCallback() {
            @Override
            public void click(View v) {
                //打开邮箱
                openEmail();
            }
        });
    }

    private void openEmail() {
        String[] emailAddresses = {getString(R.string.app_email_address)};
        String subject = binding.etTitle.getText().toString();
        String message = binding.etContent.getText().toString();
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, emailAddresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            ToastManager.get().showShortToast(getString(R.string.show_email_send_notip));
        }
    }

}
