
package com.zhangshuo.magicnotice.refresh;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.scwang.smart.drawable.ProgressDrawable;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshKernel;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;
import com.zhangshuo.magicnotice.R;

public class MFooterView extends FrameLayout implements RefreshFooter {

    private final View view;
    private final TextView tvTip;
    private final ImageView ivProgress;
    private final ProgressDrawable mProgressDrawable;

    public MFooterView(Context context) {
        super(context, null, 0);
        view = LayoutInflater.from(getContext()).inflate(R.layout.view_refresh_footer, this);
        tvTip = view.findViewById(R.id.footer_tv_tip);
        ivProgress = view.findViewById(R.id.footer_iv_progress);
        mProgressDrawable = new ProgressDrawable();
        mProgressDrawable.setColor(0xff666666);
        ivProgress.setImageDrawable(mProgressDrawable);
    }

    @Override
    public boolean setNoMoreData(boolean noMoreData) {
        if (noMoreData) {
            ivProgress.setVisibility(GONE);
            tvTip.setText(R.string.refresh_load_no_more);
        } else {
            ivProgress.setVisibility(VISIBLE);
            tvTip.setText(R.string.refresh_load_more);
        }
        return true;
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @NonNull
    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;
    }

    @Override
    public void setPrimaryColors(int... colors) {

    }

    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {

    }

    @Override
    public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
        onStartAnimator(refreshLayout, height, maxDragHeight);
    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
        if (ivProgress.getVisibility() == VISIBLE) {
            Drawable drawable = ivProgress.getDrawable();
            if (drawable instanceof Animatable) {
                ((Animatable) drawable).start();
            } else {
                ivProgress.animate().rotation(36000).setDuration(100000);
            }
        }
    }

    @Override
    public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
        if (ivProgress.getVisibility() == VISIBLE) {
            Drawable drawable = ivProgress.getDrawable();
            if (drawable instanceof Animatable) {
                if (((Animatable) drawable).isRunning()) {
                    ((Animatable) drawable).stop();
                }
            } else {
                ivProgress.animate().rotation(0).setDuration(0);
            }
        }
        return 0;
    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }


    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {

    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {

    }
}
