package com.zhangshuo.magicnotice.barrage;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.zhangshuo.common.util.LogUtils;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.common.util.UIUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.db.model.NotificationData;
import com.zhangshuo.magicnotice.ui.model.behavior.BarrageBehaviorModel;
import com.zhangshuo.magicnotice.ui.view.AnimationListenerImpl;

import java.util.ArrayList;
import java.util.List;

import me.jessyan.autosize.utils.AutoSizeUtils;

public class BarrageView extends FrameLayout {
    private ConstraintLayout clRoot;
    private List<TxtAnimCell> txtAnimCellList = new ArrayList<>();
    private OnAnimCallback onAnimCallback;
    private ConstraintLayout clView;
    private ImageView ivLogoTag;
    private ImageView imageView;
    private ConstraintLayout clContent;
    private BarrageTextView tvContent;
    private TextView tvTime;
    private String viewTag = "barrage"; //标记
    private BarrageInfo barrageInfo;
    private BarrageBehaviorModel behaviorModel;


    public void setBarrageModel(BarrageBehaviorModel barrageBehaviorModel) {
        this.behaviorModel = barrageBehaviorModel;
        int contentType = barrageBehaviorModel.getContentType();
        boolean haveLogo = haveContentType(contentType, BarrageBehaviorModel.CONTENT_TYPE_LOGO);
        imageView.setVisibility(haveLogo ? VISIBLE : GONE);
        setBarrageTextSize(barrageBehaviorModel.getTxtSizeSP());
        setBarrageTextColor(barrageBehaviorModel.getTxtColorHex());
        setBarrageTextAlpha(barrageBehaviorModel.getFontAlpha());
        setBarrageBackground(barrageBehaviorModel.getBgColorHex(), barrageBehaviorModel.getBgRadius(), barrageBehaviorModel.getBgAlpha());
        //fixme 去除彩蛋
//        ivLogoTag.setVisibility(TimeUtil.isSuperCaiDan() ? VISIBLE : GONE);
    }

    public void setBarrageInfo(BarrageInfo barrageInfo) {
        this.barrageInfo = barrageInfo;
        //取消动画
        if (clRoot != null) {
            clRoot.clearAnimation();
        }
        if (tvContent != null) {
            tvContent.clearAnimation();
        }
        BarrageBehaviorModel behaviorModel = barrageInfo.getBehaviorModel();
        setBarrageModel(behaviorModel);
        NotificationData notificationData = barrageInfo.getNotificationData();
        if (notificationData.getId() == BehaviorHelper.TEST_NOTIFICATION_ID) {
            setBarrageLogo(R.mipmap.ic_launcher);
        } else {
            setBarrageLogo(notificationData.getIconPath());
        }

        StringBuilder contentBuilder = new StringBuilder();

        if (haveContentType(behaviorModel.getContentType(), BarrageBehaviorModel.CONTENT_TYPE_TITLE)) {
            String title = notificationData.getTitle();
            if (TextUtils.isEmpty(title)) {
                title = notificationData.getAppName();
            }
            contentBuilder.append("【").append(title).append("】");
        }
        if (haveContentType(behaviorModel.getContentType(), BarrageBehaviorModel.CONTENT_TYPE_TXT)) {
            String txt = notificationData.getContent();
            if (!TextUtils.isEmpty(txt)) {
                contentBuilder.append(txt);
            }
            if (!TextUtils.isEmpty(notificationData.getPicturePath())) {
                contentBuilder.append(StringUtil.getString(R.string.barrage_tip_picture));
            }
        }
        String content = contentBuilder.toString();
        if (TextUtils.isEmpty(content)) {
            content = String.format(StringUtil.getString(R.string.barrage_tip_notice), notificationData.getAppName());
        }
        setBarrageText(content);
    }

    public BarrageInfo getBarrageInfo() {
        return barrageInfo;
    }

    public void setMoveX(int X) {
        setTranslationX(X);
    }

    public void setOnAnimCallback(OnAnimCallback onAnimCallback) {
        this.onAnimCallback = onAnimCallback;
    }


    public void startCustomAnimate() {
        checkInitAnim();
        startNextAnim();
    }

    private void checkInitAnim() {
        LogUtils.i("checkBarrageView >> checkInitAnim");
        txtAnimCellList.clear();
        tvContent.clearAnimation();
        clRoot.clearAnimation();
        tvContent.removeCallbacks(null);
        txtAnimCellList.add(new TxtAnimCell() {
            @Override
            public boolean needAnim() {
                int viewGroupWidth = clContent.getMeasuredWidth();
                int contentWidth = tvContent.getMeasuredWidth();
                return contentWidth > viewGroupWidth;
            }

            @Override
            public void onAnimStart() {
                int viewGroupWidth = clContent.getMeasuredWidth();
                int contentWidth = tvContent.getMeasuredWidth();
                int offset = (int) (contentWidth - viewGroupWidth) + AutoSizeUtils.dp2px(getContext(), imageView.getVisibility() == VISIBLE ? 30 : 10);
                int duration = (int) (offset * getSpeed());

                TranslateAnimation translateAnimation = new TranslateAnimation(0, -offset, 0, 0);
                translateAnimation.setStartOffset(800);
                translateAnimation.setDuration(duration);
                translateAnimation.setFillAfter(true);
                translateAnimation.setInterpolator(new LinearInterpolator());
                translateAnimation.setAnimationListener(new AnimationListenerImpl() {
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        super.onAnimationEnd(animation);
                        startNextAnim();
                    }
                });
                tvContent.startAnimation(translateAnimation);
            }
        });
        txtAnimCellList.add(new TxtAnimCell() {
            @Override
            public void onAnimStart() {
                //执行2秒隐藏动画
                int startOffset = barrageInfo.getBehaviorModel().getStayEffectTime();
                int animResId = R.anim.barrage_exit_alpha;
                switch (barrageInfo.getBehaviorModel().getExitEffectType()) {
                    case BarrageBehaviorModel.EXIT_EFFECT_TYPE_ALPHA:
                        animResId = R.anim.barrage_exit_alpha;
                        break;
                    case BarrageBehaviorModel.EXIT_EFFECT_TYPE_MOVE:
                        animResId = R.anim.barrage_exit_move;
                        break;
                    case BarrageBehaviorModel.EXIT_EFFECT_TYPE_SCALE:
                        animResId = R.anim.barrage_exit_scale;
                        break;
                    case BarrageBehaviorModel.EXIT_EFFECT_TYPE_SLIDE:
                        animResId = R.anim.barrage_exit_slide;
                        break;
                }
                Animation animation = AnimationUtils.loadAnimation(getContext(), animResId);
                animation.setStartOffset(startOffset);
                animation.setAnimationListener(new AnimationListenerImpl() {
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        super.onAnimationEnd(animation);
                        startNextAnim();
                    }
                });
                clRoot.startAnimation(animation);
            }
        });
    }

    private void startNextAnim() {
        if (txtAnimCellList.isEmpty()) {
            if (onAnimCallback != null) {
                onAnimCallback.onEnd(this);
            }
            return;
        }
        TxtAnimCell txtAnimCell = txtAnimCellList.remove(0);
        if (txtAnimCell.needAnim()) {
            txtAnimCell.onAnimStart();
        } else {
            startNextAnim();
        }
    }

    public void setContentTypeTip(int contentType) {
        boolean haveLogo = haveContentType(contentType, BarrageBehaviorModel.CONTENT_TYPE_LOGO);
        imageView.setVisibility(haveLogo ? VISIBLE : GONE);
        boolean haveTitle = haveContentType(contentType, BarrageBehaviorModel.CONTENT_TYPE_TITLE);
        boolean haveTxt = haveContentType(contentType, BarrageBehaviorModel.CONTENT_TYPE_TXT);
        StringBuilder stringBuilder = new StringBuilder();
        if (haveTitle) {
            stringBuilder.append("【" + StringUtil.getString(R.string.app_name) + "】");
        }
        if (haveTxt) {
            String textContent = StringUtil.getString(R.string.toast_barrage_effect_pre)
                    + BarrageBehaviorModel.getClickTip(behaviorModel.getClickType())
                    + StringUtil.getString(R.string.toast_barrage_double) + BarrageBehaviorModel.getClickTip(behaviorModel.getDoubleClickType());
            stringBuilder.append(textContent);
        } else {
            stringBuilder.append(StringUtil.getString(R.string.barrage_tip_content_preview_no));
        }
        tvContent.setText(stringBuilder);
    }

    public boolean haveContentType(int contentType, int type) {
        return (contentType & type) == type;
    }

    public void setBarrageLogo(String logoPath) {
        if (TextUtils.isEmpty(logoPath)) {
            imageView.setVisibility(GONE);
        } else if (imageView.getVisibility() == VISIBLE) {
            imageView.setBackgroundResource(0);
            Glide.with(imageView).load(logoPath).into(imageView);
        }
    }

    public void setBarrageLogo(int resId) {
        imageView.setBackgroundResource(resId);
    }

    public void setBarrageText(String text) {
        if (tvContent == null) return;
        tvContent.setText(text);
        tvContent.removeCallbacks(null);
        tvContent.post(new Runnable() {
            @Override
            public void run() {
                tvContent.setTranslationX(0);
            }
        });
    }

    public void setBarrageTextColor(String textColor) {
        if (tvContent == null) return;
        tvContent.setTextColor(Color.parseColor(textColor));
    }

    public void setBarrageTextSize(int textSize) {
        if (tvContent == null) return;
        tvContent.setTextSize(textSize);
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.width = (int) (UIUtil.dp2Px(textSize) * 1.2);
        layoutParams.height = layoutParams.width;
        imageView.setLayoutParams(layoutParams);
    }

    public void setBarrageTextAlpha(float alpha) {
        if (tvContent != null) {
            tvContent.setAlpha(alpha);
        }
        if (imageView != null) {
            imageView.setAlpha(alpha);
        }
    }

    public void setBarrageBackground(String bgColorHex, int bgRadius, float alpha) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.parseColor(bgColorHex));
        drawable.setCornerRadius(UIUtil.dp2Px(bgRadius));
        drawable.setAlpha((int) (alpha * 255));
        clView.setBackground(drawable);
    }

    public void setSpeed(int speed) {
        if (behaviorModel != null) {
//            behaviorModel.setSpeed(speed);
        }
    }

    public float getSpeed() {
//        if (behaviorModel != null) {
//            return behaviorModel.getSpeed();
//        }
//        if (barrageInfo != null && barrageInfo.getBehaviorModel() != null) {
//            return barrageInfo.getBehaviorModel().getSpeed();
//        }
        return 2.5f;
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_barrage, this);
        clRoot = findViewById(R.id.barrage_root);
        ivLogoTag = findViewById(R.id.barrage_iv_tag);
        clView = findViewById(R.id.barrage_cl_view);
        imageView = findViewById(R.id.barrage_iv_logo);
        tvContent = findViewById(R.id.barrage_tv_name);
        clContent = findViewById(R.id.barrage_cl_txt);
        tvContent.setMeasureType(1);
        tvTime = findViewById(R.id.barrage_tv_time);
    }

    public void setMaxWidth(int maxWidth) {
        clView.setMaxWidth(maxWidth);
    }

    public String getViewTag() {
        return viewTag;
    }

    public BarrageView(Context context, String viewTag) {
        super(context);
        this.viewTag = viewTag;
        init();
    }

    public BarrageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.viewTag = "";
        init();
    }

    public BarrageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.viewTag = "";
        init();
    }

    public BarrageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.viewTag = "";
        init();
    }

    public interface OnAnimCallback {
        void onStart(BarrageView view);

        void onEnd(BarrageView view);
    }

}
