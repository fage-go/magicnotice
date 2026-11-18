package com.zhangshuo.magicnotice.ui.dialog;

import android.content.ActivityNotFoundException;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;

import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.zhangshuo.common.dialog.BaseBottomDialog;
import com.zhangshuo.common.listener.OnClickCallback;
import com.zhangshuo.common.toast.ToastManager;
import com.zhangshuo.common.util.AsyncCallUtils;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.databinding.DialogShowWxBinding;

import java.io.File;
import java.util.concurrent.Callable;

public class ShowWXDialog extends BaseBottomDialog<DialogShowWxBinding> {
    private String extTip;

    public ShowWXDialog setExtTip(String extTip) {
        this.extTip = extTip;
        return this;
    }

    @Override
    public void initView() {
        binding.ivSaveImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveImage();
            }
        });
        binding.tvSaveImg.setOnClickListener(new OnClickCallback() {
            @Override
            public void click(View v) {
                onSaveImage();
            }
        });
        binding.mbOpen.setOnClickListener(new OnClickCallback() {
            @Override
            public void click(View v) {
                copyAndOpen();
            }
        });
        if (TextUtils.isEmpty(extTip)) {
            binding.tvTip.setVisibility(View.GONE);
        } else {
            binding.tvTip.setVisibility(View.VISIBLE);
            binding.tvTip.setText(extTip);
        }
    }

    private void onSaveImage() {
        showLoading();
        AsyncCallUtils.asyncCall(new Callable<String>() {
            @Override
            public String call() throws Exception {
                Bitmap bitmap = ImageUtils.drawable2Bitmap(ResourceUtils.getDrawable(R.mipmap.ic_wx_follow_img));
                File saveFile = ImageUtils.save2Album(bitmap, StringUtil.getString(R.string.app_name), Bitmap.CompressFormat.PNG);
                if (saveFile != null) {
                    return saveFile.getAbsolutePath();
                }
                return "";
            }
        }, new AsyncCallback<String>() {
            @Override
            public void onSuccess(String result) {
                hideLoading();
                if (TextUtils.isEmpty(result)) {
                    ToastManager.get().showShortToast(StringUtil.getString(R.string.save_picture_tip_fail));
                } else {
                    ToastManager.get().showShortToast(StringUtil.getString(R.string.save_picture_tip_suc));
                }
            }
        });
    }

    private void copyAndOpen() {
        ClipboardManager tvCopy = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        tvCopy.setText(StringUtil.getString(R.string.app_name));
        ToastManager.get().showShortToast(R.string.show_wx_copyed_name);
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cmp);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // TODO: handle exception
            ToastManager.get().showShortToast(R.string.show_wx_no_app);
        }
    }
}
