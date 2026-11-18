package com.zhangshuo.magicnotice.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.ball.FloatBallView;
import com.zhangshuo.magicnotice.barrage.BarrageView;
import com.zhangshuo.magicnotice.led.LightView;
import com.zhangshuo.magicnotice.ui.model.BehaviorModel;
import com.zhangshuo.magicnotice.ui.model.behavior.BarrageBehaviorModel;
import com.zhangshuo.magicnotice.ui.model.behavior.BroadcastBehaviorModel;
import com.zhangshuo.magicnotice.ui.model.behavior.CopyBehaviorModel;
import com.zhangshuo.magicnotice.ui.model.behavior.FlashLightBehaviorModel;
import com.zhangshuo.magicnotice.ui.model.behavior.FloatBallModel;
import com.zhangshuo.magicnotice.ui.model.behavior.FullScreenBehaviorModel;
import com.zhangshuo.magicnotice.ui.model.behavior.LightModel;
import com.zhangshuo.magicnotice.ui.model.behavior.NotificationBehaviorModel;
import com.zhangshuo.magicnotice.ui.model.behavior.OpenBehaviorModel;
import com.zhangshuo.magicnotice.ui.model.behavior.ShockBehaviorModel;
import com.zhangshuo.magicnotice.ui.model.behavior.SoundBehaviorModel;
import com.zhangshuo.magicnotice.ui.model.behavior.WidgetBehaviorModel;

public class BehaviorAdapter extends BaseRAdapter<BehaviorModel> {
    private OnCallback onCallback;

    public BehaviorAdapter(Context context) {
        super(context);
    }

    public void setOnCallback(OnCallback onCallback) {
        this.onCallback = onCallback;
    }

    @Override
    protected void onConvertView(ViewHolder viewHolder, BehaviorModel behaviorModel, int position) {
        if (viewHolder.getItemViewType() == -1) {
            ((TextView) viewHolder.$(R.id.item_tv_add)).getPaint().setFakeBoldText(true);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onCallback != null) {
                        onCallback.onAdd();
                    }
                }
            });
        } else {
            TextView tvTitle = (TextView) viewHolder.$(R.id.item_tv_title);
            tvTitle.setText(BehaviorModel.getBehaviorName(behaviorModel.getType()));
            tvTitle.getPaint().setFakeBoldText(true);
            if (behaviorModel instanceof ShockBehaviorModel) {
                TextView tvContent = (TextView) viewHolder.$(R.id.item_tv_content);
                ShockBehaviorModel shockBehaviorModel = (ShockBehaviorModel) behaviorModel;
                tvContent.setText(shockBehaviorModel.getName());
                tvContent.setVisibility(View.VISIBLE);
            } else if (behaviorModel instanceof FlashLightBehaviorModel) {
                TextView tvContent = (TextView) viewHolder.$(R.id.item_tv_content);
                FlashLightBehaviorModel flashLightBehaviorModel = (FlashLightBehaviorModel) behaviorModel;
                tvContent.setText(flashLightBehaviorModel.getName());
                tvContent.setVisibility(View.VISIBLE);
            } else if (behaviorModel instanceof SoundBehaviorModel) {
                TextView tvContent = (TextView) viewHolder.$(R.id.item_tv_content);
                SoundBehaviorModel soundBehaviorModel = (SoundBehaviorModel) behaviorModel;
                tvContent.setText(soundBehaviorModel.getRingtoneTitle());
                tvContent.setVisibility(View.VISIBLE);
            } else if (behaviorModel instanceof BroadcastBehaviorModel) {
                TextView tvContent = (TextView) viewHolder.$(R.id.item_tv_content);
                BroadcastBehaviorModel broadcastBehaviorModel = (BroadcastBehaviorModel) behaviorModel;
                tvContent.setText(broadcastBehaviorModel.getLanguageName());
                tvContent.setVisibility(View.VISIBLE);
            } else if (behaviorModel instanceof OpenBehaviorModel) {
                TextView tvContent = (TextView) viewHolder.$(R.id.item_tv_content);
                tvContent.setVisibility(View.GONE);
            } else if (behaviorModel instanceof CopyBehaviorModel) {
                TextView tvContent = (TextView) viewHolder.$(R.id.item_tv_content);
                CopyBehaviorModel copyBehaviorModel = (CopyBehaviorModel) behaviorModel;
                tvContent.setText(copyBehaviorModel.getName());
                tvContent.setVisibility(View.VISIBLE);
            } else if (behaviorModel instanceof BarrageBehaviorModel) {
                BarrageView barrageView = (BarrageView) viewHolder.$(R.id.item_barrage_view);
                BarrageBehaviorModel barrageBehaviorModel = (BarrageBehaviorModel) behaviorModel;
                barrageView.setBarrageLogo(R.mipmap.ic_launcher);
                barrageView.setBarrageModel(barrageBehaviorModel);
                barrageView.setContentTypeTip(barrageBehaviorModel.getContentType());
            } else if (behaviorModel instanceof FloatBallModel) {
                FloatBallView floatBallView = (FloatBallView) viewHolder.$(R.id.float_ball_view);
                floatBallView.setClickable(false);
                FloatBallModel floatBallModel = (FloatBallModel) behaviorModel;
                floatBallView.setFloatBallModel(floatBallModel);
                floatBallView.setDataInfo(99, getContext().getPackageName(), null);
            } else if (behaviorModel instanceof FullScreenBehaviorModel) {
                TextView tvContent = (TextView) viewHolder.$(R.id.item_tv_content);
                tvContent.setVisibility(View.GONE);
            } else if (behaviorModel instanceof NotificationBehaviorModel) {
                TextView tvContent = (TextView) viewHolder.$(R.id.item_tv_content);
                tvContent.setVisibility(View.GONE);
            } else if (behaviorModel instanceof WidgetBehaviorModel) {
                TextView tvContent = (TextView) viewHolder.$(R.id.item_tv_content);
                tvContent.setVisibility(View.GONE);
            } else if (behaviorModel instanceof LightModel) {
                LightView lightView = (LightView) viewHolder.$(R.id.light_view);
                LightModel lightModel = (LightModel) behaviorModel;
                lightView.setMock(true);
                lightView.setProgress(1f);
                lightView.setAutoAnim(true);
                lightView.bindData(lightModel);
            }
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onCallback != null) {
                        onCallback.onItemClick(position);
                    }
                }
            });
        }
    }

    @Override
    protected int onSetItemViewType(BehaviorModel behaviorModel, int position) {
        return behaviorModel.getType();
    }

    @Override
    protected int onBindView(int viewType) {
        switch (viewType) {
            case BehaviorModel.BehaviorType.BEHAVIOR_SHOCK:
            case BehaviorModel.BehaviorType.BEHAVIOR_SOUND:
            case BehaviorModel.BehaviorType.BEHAVIOR_BROADCAST:
            case BehaviorModel.BehaviorType.BEHAVIOR_FLASH_LIGHT:
            case BehaviorModel.BehaviorType.BEHAVIOR_OPEN:
            case BehaviorModel.BehaviorType.BEHAVIOR_COPY:
            case BehaviorModel.BehaviorType.BEHAVIOR_FULL_SCREEN:
            case BehaviorModel.BehaviorType.BEHAVIOR_NOTIFICATION:
            case BehaviorModel.BehaviorType.BEHAVIOR_WIDGET:
                return R.layout.item_behavior_common;
            case BehaviorModel.BehaviorType.BEHAVIOR_BARRAGE:
                return R.layout.item_behavior_barrage;
            case BehaviorModel.BehaviorType.BEHAVIOR_FLOAT_BALL:
                return R.layout.item_behavior_float_ball;
            case BehaviorModel.BehaviorType.BEHAVIOR_LIGHT:
                return R.layout.item_behavior_light;
        }
        if (viewType == -1)
            return R.layout.item_behavior_add;
        else
            return R.layout.item_line_empty;
    }
}
