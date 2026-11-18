package com.zhangshuo.common.base;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.viewbinding.ViewBinding;

import com.blankj.utilcode.util.SizeUtils;
import com.gyf.immersionbar.ImmersionBar;
import com.zhangshuo.common.R;
import com.zhangshuo.common.dialog.LoadingDialog;
import com.zhangshuo.common.model.EventMessage;
import com.zhangshuo.common.mvp.BaseViewBinder;
import com.zhangshuo.common.mvp.IBaseView;
import com.zhangshuo.common.page.PageState;
import com.zhangshuo.common.page.PageView;
import com.zhangshuo.common.router.RouterUtil;
import com.zhangshuo.common.toast.ToastManager;
import com.zhangshuo.common.util.EventBusUtil;
import com.zhangshuo.common.util.LogUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public abstract class BaseActivity<D extends ViewBinding> extends FragmentActivity implements IBaseView {

    private static final String TAG = "PageName";//页面打印

    protected View vBar;
    protected RelativeLayout rlTitle;
    protected TextView tvTitle;
    private ImageView ivClose;
    protected LinearLayout llClose;
    protected LinearLayout llOpt;
    protected ImageView ivOpt;
    protected LinearLayout llOptTxt;
    protected TextView tvOptTxt;

    private ImmersionBar immersionBar;
    private PageView pageView;

    //P层
    protected D binding;
    private boolean isCreated = false;

    //加载进度条
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, getClass().getSimpleName());
        setContentView(R.layout.base_activity);
        immersionBar = ImmersionBar.with(this);
        immersionBar.fitsSystemWindows(false)
                .transparentStatusBar()
                .statusBarDarkFont(true)
                .init();//默认全屏
        binding = (D) BaseViewBinder.createViewBinding(this, getLayoutInflater());
        if (binding == null) {
            ToastManager.get().noticeShortMessage(R.string.app_sys_error);
            return;
        }
        bindBaseView();
        View root = binding.getRoot();
        pageView.setLayoutView(root);
        RouterUtil.get().inject(this);
        EventBusUtil.register(this);
        initView();
    }

    private void bindBaseView() {
        vBar = findViewById(R.id.v_bar);
        rlTitle = findViewById(R.id.rl_title);
        tvTitle = findViewById(R.id.tv_title);
        ivClose = findViewById(R.id.iv_close);
        llClose = findViewById(R.id.ll_close);
        llOpt = findViewById(R.id.ll_opt);
        ivOpt = findViewById(R.id.iv_opt);
        llOptTxt = findViewById(R.id.ll_opt_txt);
        tvOptTxt = findViewById(R.id.tv_opt_txt);
        pageView = findViewById(R.id.base_page_view);
        llClose.setOnClickListener(v -> onCloseClick());
        tvTitle.setOnClickListener(v -> onCloseClick());
    }

    public void setTitleMaxWidth(int width) {
        if (tvTitle != null) {
            tvTitle.setMaxWidth(width);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isCreated) {
            isCreated = true;
            initData();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 放在onStop有时候不会走
        if (isFinishing()) {
            EventBusUtil.unregister(this);
        }

        LogUtils.d(
                TAG + getClass().getSimpleName() + ":onDestroy," + isFinishing()
        );
    }

    /**
     * 设置初始化View
     */
    protected abstract void initView();

    /**
     * 设置初始化值
     */
    protected abstract void initData();

    public void onCloseClick() {
        if (!interceptBackEvent()) {
            finish();
        }
    }


    /**
     * 标题栏控制
     */
    protected void showTitle(boolean flag) {
        showTitle(flag, flag);
    }

    protected void showTitle(boolean barFlag, boolean titleFlag) {
        if (barFlag) {
            vBar.setVisibility(View.VISIBLE);
            statusShow(true);//状态栏高度
        } else {
            vBar.setVisibility(View.GONE);
        }
        if (titleFlag) {
            rlTitle.setVisibility(View.VISIBLE);
        } else {
            rlTitle.setVisibility(View.GONE);
        }
    }

    /**
     * 状态栏控制
     */
    protected void statusShow(boolean flag) {
        View view = findViewById(R.id.v_bar);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
        if (flag) {
            //非沉浸式。计算状态栏高度
            layoutParams.height = ImmersionBar.getStatusBarHeight(this);
            statusDark(true);
        } else {
            //沉浸式
            layoutParams.height = 0;
            statusDark(false);
        }
        view.setLayoutParams(layoutParams);
    }

    protected void statusDark(boolean dark) {
        immersionBar.statusBarDarkFont(dark).init();
    }

    protected int getStatusHeight() {
        return ImmersionBar.getStatusBarHeight(this);
    }

    protected void setTitleBar(int strResId) {
        String title = getString(strResId);
        setTitleBar(title);
    }

    protected void setTitleBar(String title) {
        setTitleBar(title, true);
    }

    protected void setTitleBar(String title, boolean openClose) {
        setTitleBar(title, openClose, -1);
    }

    protected void setTitleBar(String title, boolean openClose, int closeResId) {
        if (rlTitle == null || tvTitle == null) return;
        tvTitle.setText(title);
        rlTitle.setVisibility(View.VISIBLE);
        if (closeResId != -1) {
            ivClose.setBackgroundResource(closeResId);
        } else {
            ivClose.setBackgroundResource(R.mipmap.base_back);
        }
        llClose.setVisibility(openClose ? View.VISIBLE : View.GONE);
        statusShow(true);//页面
    }

    protected void setTitleBarOpt(@DrawableRes int resId, View.OnClickListener onClickListener) {
        if (llOpt == null || ivOpt == null) return;
        llOpt.setVisibility(View.VISIBLE);
        ivOpt.setBackgroundResource(resId);
        llOpt.setPadding(
                0, 0, SizeUtils.dp2px(14), 0
        );
        llOpt.setOnClickListener(onClickListener);
    }

    protected void setRightTxt(String text, View.OnClickListener onClickListener) {
        if (llOptTxt == null || tvOptTxt == null) return;
        llOptTxt.setVisibility(View.VISIBLE);
        tvOptTxt.setText(text);
        llOptTxt.setOnClickListener(onClickListener);
    }

    /**
     * 设置右侧自定义布局
     */
    protected void setRightCusLayout(View view) {
        if (view != null) {
            llOpt.setVisibility(View.VISIBLE);
            llOpt.removeAllViews();
            llOpt.addView(view);
        }
    }

    protected void setTitleBarColor(@ColorInt int color) {
        vBar.setBackgroundColor(color);
        rlTitle.setBackgroundColor(color);
    }

    @Override
    public Context getCtx() {
        return this;
    }


    @Override
    public void showPageState(PageState pageState) {
        if (pageView != null) {
            pageView.changePageState(pageState);
        }
    }


    @Override
    public void showToast(String msg) {
        if (TextUtils.isEmpty(msg)) return;
        ToastManager.get().noticeShortMessage(msg);
    }

    @Override
    public void showLoading() {
        showLoading(false);
    }

    @Override
    public void showLoading(String loadText) {
        if (getCtx() == null) return;
        if (isFinishing()) return;
        try {
            if (loadingDialog == null) {
                loadingDialog = LoadingDialog.show(this, loadText, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showLoading(boolean cancelable) {
        if (getCtx() == null) return;
        if (isFinishing()) return;
        try {
            if (loadingDialog == null) {
                loadingDialog = LoadingDialog.show(this, cancelable);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void hideLoading() {
        if (loadingDialog != null) {
            try {
                loadingDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            loadingDialog = null;
        }
    }

    @Override
    public void finishAct() {
        finish();
    }

    /**
     * EventBus事件接收
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(EventMessage eventMessage) {
    }


    /**
     * EventBus粘性事件接收
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onReceiveStickyEvent(EventMessage eventMessage) {

    }

    public String getJumpUrl() {
        return "";
    }

    /**
     * 返回事件拦截
     */
    protected boolean interceptBackEvent() {
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && interceptBackEvent()) {
            //拦截返回事件
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Activity是否已被销毁
     */
    public boolean isActivityEnable() {
        return !isDestroyed() && !isFinishing();
    }

}
