package com.zhangshuo.common.base;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import com.zhangshuo.common.R;
import com.zhangshuo.common.model.EventMessage;
import com.zhangshuo.common.mvp.BaseViewBinder;
import com.zhangshuo.common.mvp.IBaseView;
import com.zhangshuo.common.page.PageState;
import com.zhangshuo.common.page.PageView;
import com.zhangshuo.common.router.RouterUtil;
import com.zhangshuo.common.toast.ToastManager;
import com.zhangshuo.common.util.EventBusUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public abstract class BaseFragment<D extends ViewBinding> extends Fragment implements IBaseView {

    private static final String TAG = "PageName";//页面打印

    private View rootView;

    private PageView pageView;

    protected D binding;

    private final View.OnKeyListener keyBackListener = (view, code, keyEvent) -> {
        if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            //这边判断，如果是back的按键被点击了，就判断拦截
            if (code == KeyEvent.KEYCODE_BACK && interceptBackEvent()) {
                //拦截返回事件
                return true;
            }
        }
        return false;
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, this.toString());
        binding = (D) BaseViewBinder.createViewBinding(this, inflater, container);
        rootView = inflater.inflate(R.layout.base_page_view, container, false);
        pageView = rootView.findViewById(R.id.page_view);
        View root = binding.getRoot();
        pageView.setLayoutView(root);
        pageView.setOnKeyListener(keyBackListener);
        RouterUtil.get().inject(this);
        EventBusUtil.register(this);
        reSavedInstanceState(savedInstanceState);
        initView();
        initData();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public String getJumpUrl() {
        return "";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBusUtil.unregister(this);
    }

    protected void reSavedInstanceState(Bundle bundle){

    }

    /**
     * 设置初始化View
     */
    protected abstract void initView();

    /**
     * 设置初始化值
     */
    protected abstract void initData();


    @Override
    public Context getCtx() {
        return getContext();
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
        showLoading(true);
    }

    @Override
    public void showLoading(String loadText) {
        if (getCtx() == null) return;
        if (!isAdded()) return;
        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).showLoading(loadText);
        }
    }

    @Override
    public void showLoading(boolean cancelable) {
        if (getCtx() == null) return;
        if (!isAdded()) return;
        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).showLoading();
        }
    }

    @Override
    public void hideLoading() {
        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).hideLoading();
        }
    }

    @Override
    public void finishAct() {
        if (getActivity() != null)
            getActivity().finish();
    }

    /**
     * EventBus事件接收
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(EventMessage eventMessage) {
    }

    /**
     * 返回事件拦截
     */
    protected boolean interceptBackEvent() {
        return false;
    }

    /**
     * Fragment是否已被销毁
     */
    public boolean isFragmentEnable() {
        return getActivity() != null
                && !getActivity().isDestroyed() && !getActivity().isFinishing()
                && isAdded();
    }
}
