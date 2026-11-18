package com.zhangshuo.magicnotice.ui.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.blankj.utilcode.util.SpanUtils;
import com.blankj.utilcode.util.VibrateUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.common.util.UIUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.db.model.NotificationData;
import com.zhangshuo.magicnotice.notice.NotificationHelper;
import com.zhangshuo.magicnotice.notice.factory.ProcessFactory;
import com.zhangshuo.magicnotice.ui.dialog.DetailDialog;
import com.zhangshuo.magicnotice.ui.model.ConfigRuleModel;
import com.zhangshuo.magicnotice.ui.model.KeywordInfo;
import com.zhangshuo.magicnotice.ui.model.behavior.BarrageBehaviorModel;
import com.zhangshuo.magicnotice.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotificationAdapter extends BaseRAdapter<NotificationData> {
    private boolean showRuleInfo = true;//是否展示规则信息
    private boolean showSelectOption = false;//是否展示选择功能
    private final boolean showWeekday;
    private boolean showSmallMode = false;//是否显示小字体
    private List<KeywordInfo> highlightKeyWordList = new ArrayList<>();
    private DetailDialog detailDialog;
    private OnClickCallback onClickCallback;

    public NotificationAdapter(Context context) {
        this(context, false);
    }

    public NotificationAdapter(Context context, boolean showWeekday) {
        super(context);
        this.showWeekday = showWeekday;
        initDetailDialog();
    }

    public void setOnClickCallback(OnClickCallback onClickCallback) {
        this.onClickCallback = onClickCallback;
    }

    private void initDetailDialog() {
        detailDialog = new DetailDialog();
    }

    public void setShowRuleInfo(boolean showRuleInfo) {
        this.showRuleInfo = showRuleInfo;
    }

    public void setShowSelectOption(boolean showSelectOption) {
        this.showSelectOption = showSelectOption;
    }

    public void setHighlightKeyWordList(List<KeywordInfo> highlightKeyWordList) {
        this.highlightKeyWordList = highlightKeyWordList;
    }

    @Override
    protected void onConvertView(ViewHolder viewHolder, NotificationData notificationData, int position) {
        ImageView ivLogo = (ImageView) viewHolder.$(R.id.item_iv_app_logo);
        ImageView ivLock = (ImageView) viewHolder.$(R.id.item_iv_lock);
        TextView tvName = (TextView) viewHolder.$(R.id.item_tv_app_name);
        TextView tvTime = (TextView) viewHolder.$(R.id.item_tv_time);
        ImageView ivTitle = (ImageView) viewHolder.$(R.id.item_iv_title);
        TextView tvTitle = (TextView) viewHolder.$(R.id.item_tv_title);
        ImageView ivTag = (ImageView) viewHolder.$(R.id.item_iv_tag);
        ConstraintLayout clContent = (ConstraintLayout) viewHolder.$(R.id.item_cl_content);
        TextView tvContent = (TextView) viewHolder.$(R.id.item_tv_content);
        ImageView ivPicture = (ImageView) viewHolder.$(R.id.item_iv_picture);
        View vLine = (View) viewHolder.$(R.id.item_v_line);
        TextView tvRuleName = (TextView) viewHolder.$(R.id.item_tv_rule);
        ImageView ivCheck = (ImageView) viewHolder.$(R.id.item_iv_check);

        if (clContent != null) {
            int readType = notificationData.getReadType();
            clContent.setBackgroundResource(
                    readType == 2 ? R.drawable.bg_content_round_gray : R.drawable.bg_content_round
            );
        }

        if (ivLogo != null)
            Glide.with(getContext())
                    .load(notificationData.getLogoPath())
                    .into(ivLogo);

        if (ivTitle != null) {
            String titleIcon = notificationData.getIconPath();
            if (TextUtils.isEmpty(titleIcon)) {
                titleIcon = notificationData.getLogoPath();
            }
            Glide.with(getContext())
                    .load(titleIcon)
                    .transform(new RoundedCorners(UIUtil.dp2Px(4)))
                    .into(ivTitle);
        }

        if (ivLock != null)
            ivLock.setVisibility(notificationData.getLock() == 1 ? View.VISIBLE : View.GONE);

        if (tvName != null)
            tvName.setText(notificationData.getAppName());
        if (tvTime != null) {
            long postTime = notificationData.getPostTime();
            tvTime.setText(showWeekday
                    ? TimeUtil.formatHomeNotificationTime(postTime) : TimeUtil.dateFormatTime(postTime));
        }

        if (tvTitle != null)
            tvTitle.setText(handleHighlight(notificationData.getTitle()));
        if (tvContent != null)
            tvContent.setText(handleHighlight(notificationData.getContent()));

        if (ivPicture != null) {
            if (!TextUtils.isEmpty(notificationData.getPicturePath())) {
                ivPicture.setVisibility(View.VISIBLE);
                Glide.with(getContext())
                        .load(notificationData.getPicturePath())
                        .fitCenter()
                        .into(ivPicture);
            } else {
                ivPicture.setVisibility(View.GONE);
            }
        }
        if (tvRuleName != null) {
            if (TextUtils.isEmpty(notificationData.getRuleName())) {
                tvRuleName.setText(R.string.notification_rule_none);
            } else {
                SpanUtils.with(tvRuleName)
                        .append(StringUtil.getString(R.string.test_config_result_hit))
                        .append(notificationData.getRuleName())
                        .setBold()
                        .append(StringUtil.getString(R.string.test_config_result_hit_rule))
                        .create();
            }
        }
        //所有都不显示，上述规则就无效了
        if (showRuleInfo) {
            if (vLine != null)
                vLine.setVisibility(View.VISIBLE);
            if (tvRuleName != null)
                tvRuleName.setVisibility(View.VISIBLE);
        } else {
            if (vLine != null)
                vLine.setVisibility(View.GONE);
            if (tvRuleName != null)
                tvRuleName.setVisibility(View.GONE);
        }

        int tagColor = 0;
        try {
            ConfigRuleModel configRuleModel = ProcessFactory.get().getMagicProcess().getConfigRuleModelById(notificationData.getRuleId());
            if (configRuleModel != null) {
                tagColor = Color.parseColor(configRuleModel.getTagColor());
            }
        } catch (Exception e) {

        }
        if (tagColor == 0) {
            if (ivTag != null)
                ivTag.setVisibility(View.GONE);
            if (tvTitle != null) {
                ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) tvTitle.getLayoutParams();
                lp.rightMargin = UIUtil.dp2Px(10);
                tvTitle.setLayoutParams(lp);
            }
        } else {
            if (ivTag != null) {
                ivTag.setVisibility(View.VISIBLE);
                ivTag.setBackgroundTintList(ColorStateList.valueOf(tagColor));
            }
            if (tvTitle != null) {
                ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) tvTitle.getLayoutParams();
                lp.rightMargin = 0;
                tvTitle.setLayoutParams(lp);
            }
        }
        if (showSelectOption) {
            //如果是可以被点击的
            if (ivCheck != null)
                ivCheck.setVisibility(View.VISIBLE);
            if (tvContent != null) {
                ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) tvContent.getLayoutParams();
                lp.rightMargin = UIUtil.dp2Px(10);
                tvContent.setLayoutParams(lp);
            }
        } else {
            //如果不是可被点击的
            if (ivCheck != null) {
                ivCheck.setVisibility(View.GONE);
            }
            if (tvContent != null) {
                ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) tvContent.getLayoutParams();
                lp.rightMargin = 0;
                tvContent.setLayoutParams(lp);
            }
            if (tvTitle != null) {
                tvTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDetailDialog(notificationData);
                    }
                });
            }
            if (tvContent != null) {
                tvContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDetailDialog(notificationData);
                    }
                });
            }
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDetailDialog(notificationData);
                }
            });
        }

    }

    public void setShowSmallMode(boolean smallMode) {
        this.showSmallMode = smallMode;
    }

    private void showDetailDialog(NotificationData notificationData) {
        boolean needIntercept = false;
        if (onClickCallback != null) {
            needIntercept = onClickCallback.onIntercept(notificationData);
            onClickCallback.onClick(notificationData);
        }
        if (needIntercept) {
            return;
        }
        if (showSmallMode) {
            NotificationHelper.get().openApp(notificationData, BarrageBehaviorModel.TYPE_CLICK_MSG);
        } else {
            VibrateUtils.vibrate(30L);
            detailDialog.setNotification(notificationData);
            detailDialog.show(getContext());
        }
    }

    public int getPositionById(int id) {
        for (int i = 0; i < getDataList().size(); i++) {
            NotificationData notificationData = getDataList().get(i);
            if (notificationData != null && notificationData.getId() == id) {
                return i;
            }
        }
        return -1;
    }

    @Override
    protected int onSetItemViewType(NotificationData notificationData, int position) {
        return 0;
    }

    @Override
    protected int onBindView(int viewType) {
        return showSmallMode ? R.layout.item_notification_tiny : R.layout.item_notification;
    }

    public CharSequence handleHighlight(String content) {
        if (TextUtils.isEmpty(content)) return "";
        if (highlightKeyWordList == null || highlightKeyWordList.isEmpty()) return content;
        SpannableStringBuilder builder = new SpannableStringBuilder(content);
        for (KeywordInfo keywordInfo : highlightKeyWordList) {
            Pattern p = Pattern.compile(keywordInfo.getWord());
            Matcher m = p.matcher(content);
            while (m.find()) {
                int startIndex = m.start();
                int endIndex = m.end();
                if (startIndex == endIndex) continue;
                builder.setSpan(new ForegroundColorSpan(Color.parseColor(keywordInfo.getColor()))
                        , startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new StyleSpan(Typeface.BOLD), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return builder;
    }


    public void hideDetailDialog() {
        if (detailDialog != null) {
            if (detailDialog.isShowing()) {
                detailDialog.close();
            }
        }
    }

    public interface OnClickCallback {
        boolean onIntercept(NotificationData notificationData);

        void onClick(NotificationData notificationData);
    }
}
