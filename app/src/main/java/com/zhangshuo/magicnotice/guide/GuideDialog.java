package com.zhangshuo.magicnotice.guide;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.zhangshuo.common.dialog.BaseTipsDialog;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.databinding.FragmentGuideBinding;

import java.util.ArrayList;
import java.util.List;

public class GuideDialog extends BaseTipsDialog<FragmentGuideBinding> {
    private List<GuideInfo> guideInfoList = new ArrayList<>();
    private OnCallback onCallback;
    private View.OnClickListener onClickListener;
    private int currentIndex = 0;

    public void setGuideInfoList(List<GuideInfo> guideInfoList) {
        this.guideInfoList = guideInfoList;
        this.currentIndex = 0;
    }

    public void setOnCallback(OnCallback onCallback) {
        this.onCallback = onCallback;
    }

    @Override
    public void initView() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNext();
            }
        };
        showNext();
    }

    private void showNext() {
        if (guideInfoList.size() <= currentIndex) {
            close();
            onCallback = null;
        } else {
            binding.flGuide.removeAllViews();
            GuideInfo guideInfo = guideInfoList.get(currentIndex);
            int layoutId = guideInfo.getLayoutId();
            View view = LayoutInflater.from(getContext()).inflate(layoutId, null);
            TextView textView = view.findViewWithTag("tv_info");
            if ((currentIndex + 1) == guideInfoList.size()) {
                textView.setText(StringUtil.getString(R.string.guide_dialog_done));
            } else {
                textView.setText(StringUtil.getString(R.string.guide_dialog_next)
                        + " (" + (currentIndex + 1) + "/" + guideInfoList.size() + ")");
            }
            textView.setOnClickListener(onClickListener);
            binding.flGuide.addView(view, new FrameLayout.LayoutParams(-1, -1));
            if (onCallback != null) {
                onCallback.onNext(currentIndex);
            }
            currentIndex++;
        }
    }

    @Override
    protected int getDialogWidth() {
        return -1;
    }

    @Override
    protected int getDialogHeight() {
        return -1;
    }

    public interface OnCallback {
        void onNext(int position);
    }


}
