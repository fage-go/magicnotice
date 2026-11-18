package com.zhangshuo.magicnotice.ball;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.AppUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.zhangshuo.common.util.LogUtils;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.ui.activity.NotReadListActivity;
import com.zhangshuo.magicnotice.ui.model.behavior.FloatBallModel;
import com.zhangshuo.common.util.UIUtil;

public class FloatBallView extends FrameLayout {
    private String ruleId;
    private int unReadNum = 0;
    private LinearLayout llBall;
    private ImageView ivContent;
    private TextView tvContent;
    private FloatBallModel floatBallModel;

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }


    public void setFloatBallModel(FloatBallModel floatBallModel) {
        this.floatBallModel = floatBallModel;
        buildBallView();
    }

    private void buildBallView() {
        llBall.removeAllViews();
        ViewGroup viewContentRoot;
        if (floatBallModel.getBgType() == FloatBallModel.TYPE_BG_LOGO) {
            //如果是logo
            viewContentRoot = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.view_float_ball_logo, null);
            ivContent = viewContentRoot.findViewById(R.id.iv_logo);
            tvContent = viewContentRoot.findViewById(R.id.tv_content);

            int contentViewSize = UIUtil.dp2Px(5 + floatBallModel.getTxtSizeSP() / 2F);
            ViewGroup.LayoutParams tvLP = tvContent.getLayoutParams();
            tvLP.width = contentViewSize;
            tvLP.height = contentViewSize;
            tvContent.setLayoutParams(tvLP);

            LayerDrawable countBackground = getCountBackground(false);
            tvContent.setTextColor(UIUtil.getAlphaColor(floatBallModel.getFontAlpha(), floatBallModel.getTxtColorHex()));
            tvContent.setTextSize(floatBallModel.getTxtSizeSP() / 2f);
            tvContent.setBackground(countBackground);


            ViewGroup.LayoutParams layoutParams = ivContent.getLayoutParams();
            layoutParams.width = UIUtil.dp2Px(floatBallModel.getTxtSizeSP() * 2);
            layoutParams.height = layoutParams.width;
            ivContent.setLayoutParams(layoutParams);

        } else {
            viewContentRoot = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.view_float_ball_normal, null);
            tvContent = viewContentRoot.findViewById(R.id.tv_content);

            int contentViewSize = UIUtil.dp2Px(24 + floatBallModel.getTxtSizeSP());
            ViewGroup.LayoutParams layoutParams = tvContent.getLayoutParams();
            layoutParams.width = contentViewSize;
            layoutParams.height = contentViewSize;
            tvContent.setLayoutParams(layoutParams);

            tvContent.setTextColor(UIUtil.getAlphaColor(floatBallModel.getFontAlpha(), floatBallModel.getTxtColorHex()));
            tvContent.setTextSize(floatBallModel.getTxtSizeSP());
            LayerDrawable countBackground = getCountBackground(floatBallModel.getBgType() == FloatBallModel.TYPE_BG_EDGE);
            tvContent.setBackground(countBackground);
        }
        llBall.addView(viewContentRoot, new LayoutParams(-2, -2));
    }

    public void setDataInfo(int num, String pkgName, String iconPath) {
        showReadNum(num);
        if (ivContent != null) {
            if (TextUtils.isEmpty(iconPath) && !TextUtils.isEmpty(pkgName)) {
                ivContent.setBackground(AppUtils.getAppIcon(pkgName));
            } else {
                Glide.with(getContext())
                        .load(iconPath)
                        .transform(new RoundedCorners(UIUtil.dp2Px(20)))
                        .into(ivContent);
            }
        }
    }


    public void showReadNum(int num) {
        unReadNum = num;
        if (tvContent != null) {
            String numInfo = "";
            if (num > 99) {
                numInfo = "..";
            } else {
                numInfo = String.valueOf(num);
            }
            tvContent.setText(numInfo);
        }
    }


    private LayerDrawable getCountBackground(boolean needEdge) {
        ShapeDrawable drawable02 = new ShapeDrawable();
        drawable02.getPaint().setColor(UIUtil.getAlphaColor(floatBallModel.getBgAlpha(), floatBallModel.getBgColorHex()));
        drawable02.setShape(new OvalShape());
        if (needEdge) {
            ShapeDrawable drawable01 = new ShapeDrawable();
            drawable01.getPaint().setColor(UIUtil.getAlphaColor(0.5f * floatBallModel.getBgAlpha(), floatBallModel.getBgColorHex()));
            drawable01.setShape(new OvalShape());
            int offset = UIUtil.dp2Px(floatBallModel.getTxtSizeSP() / 4f);
            drawable01.setPadding(new Rect(offset, offset, offset, offset));

            Drawable[] layers = new Drawable[2];
            layers[0] = drawable01;
            layers[1] = drawable02;
            return new LayerDrawable(layers);
        } else {
            Drawable[] layers = new Drawable[1];
            layers[0] = drawable02;
            return new LayerDrawable(layers);
        }
    }

    public void onClick() {
        LogUtils.i("onClick >> " + floatBallModel.getClickType());
        if (floatBallModel.getClickType() == FloatBallModel.TYPE_CLICK_DETAIL_SMALL) {
            gotoDetail(true);
        } else if (floatBallModel.getClickType() == FloatBallModel.TYPE_CLICK_DETAIL) {
            gotoDetail(false);
        } else if (floatBallModel.getClickType() == FloatBallModel.TYPE_CLICK_MSG) {
            gotoDetail(true);
        } else {
            FloatBallHelper.get().dismissFloatBall(ruleId);
        }
    }

    private void gotoDetail(boolean trySmall) {
        if (TextUtils.isEmpty(ruleId)) return;
        int ruleIdValue = Integer.parseInt(ruleId.replace("RuleView_", ""));
        NotReadListActivity.openNotReadWindow(getContext(), ruleIdValue, unReadNum, trySmall);
        if (trySmall) {
            FloatBallHelper.get().hideFloatBall(ruleId);
        }else {
            FloatBallHelper.get().dismissFloatBall(ruleId);
        }
    }


    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_float_ball_root, this);
        llBall = findViewById(R.id.fl_ball);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                FloatBallView.this.onClick();
            }
        });
    }


    public FloatBallView(@NonNull Context context) {
        super(context);
        init();
    }

    public FloatBallView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FloatBallView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public FloatBallView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }
}
