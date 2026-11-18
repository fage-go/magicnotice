package com.zhangshuo.magicnotice.ui.h5;

import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zhangshuo.common.base.BaseActivity;
import com.zhangshuo.common.toast.ToastManager;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.config.RoutePaths;
import com.zhangshuo.magicnotice.databinding.ActivityH5Binding;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Route(path = RoutePaths.APP_ACTIVITY_H5)
public class H5Activity extends BaseActivity<ActivityH5Binding> {

    @Autowired
    String url = "";
    @Autowired
    String title = "";
    @Autowired
    String postData = "";

    @Override
    protected void initView() {
        if (!TextUtils.isEmpty(title)) {
            setTitleBar(title);
        }
        binding.webview.bindProgress(binding.progressBar);
        if (TextUtils.isEmpty(url)) {
            ToastManager.get().showShortToast(getString(R.string.h5_url_error));
            return;
        }
    }

    @Override
    protected void initData() {
        url = displayUrl(url);
        if (TextUtils.isEmpty(postData)) {
            binding.webview.loadUrl(url);
        } else {
            binding.webview.postUrl(url, postData.getBytes(StandardCharsets.UTF_8));
        }
    }


    @Override
    protected boolean interceptBackEvent() {
        boolean isHome = false;
        try {
            URL url = new URL(binding.webview.getUrl());
            String path = url.getPath();
            if (TextUtils.isEmpty(path)) {
                isHome = true;
            } else {
                isHome = path.startsWith("/embed/phone/401275/profile")
                        || "/embed/phone/401275".equals(path);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (binding.webview.canGoBack() && !isHome) {
            binding.webview.goBack();
            return true;
        }
        return false;
    }

    private String displayUrl(String url) {
        return url;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        binding.webview.onActivityResult(requestCode, resultCode, data);
    }
}
