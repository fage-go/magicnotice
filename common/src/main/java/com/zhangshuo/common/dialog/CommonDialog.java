package com.zhangshuo.common.dialog;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ScreenUtils;
import com.zhangshuo.common.R;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.common.view.MaxHeightScrollView;

public class CommonDialog extends BaseDialog {

    // 控件
    private ViewGroup titleContainer;//标题布局
    private TextView tvTitle;//弹窗标题
    private ImageView ivClose;//关闭icon
    private TextView tvContent;//弹窗内容
    private CheckBox cbNoTips;//不再提示
    private ViewGroup actionContainer;//按钮布局
    private TextView tvActionLeft;//左边按钮
    private View viewActionLine;//按钮分割线
    private TextView tvActionCommon;//通用按钮

    // 属性
    private CharSequence title;//弹窗标题
    private int titleSize;//标题大小
    private int titleColor = -1;//标题颜色
    private boolean titleBold = true;//标题是否加粗(默认true)

    private boolean showClose;//是否显示关闭icon
    private int closeResId;//关闭icon资源id

    private MaxHeightScrollView contentContainer;//内容布局
    private CharSequence content;//弹窗内容
    private int contentSize;//内容大小
    private int contentColor = -1;//内容颜色
    private boolean contentBold;//内容是否加粗
    private int contentGravity = -99;//内容方向(默认居中)
    private boolean isContentString;//内容是否是纯String
    private boolean dialogCanShow;//弹窗是否可展示(内容不能为空)

    private boolean showNoTips;//是否显示不再提示
    private String noTipsText;//不再提示内容

    private String leftAction;//左边按钮内容
    private int leftActionSize;//左边按钮大小
    private int leftActionColor = -1;//左边按钮颜色
    private boolean leftActionBold;//左边按钮是否加粗

    private String commonAction;//通用按钮内容
    private int commonActionSize;//通用按钮大小
    private int commonActionColor = -1;//通用按钮颜色
    private boolean commonActionBold;//通用按钮是否加粗

    private boolean canDismissOutSide = true;//点击外部是否关闭(默认true)
    private boolean canCancel = true;//点击返回是否关闭(默认true)
    private boolean autoClose = true;//是否自动关闭(默认true)
    private boolean shieldingBack = false;// 是否屏蔽返回键

    private ActionListener leftActionListener;//左边按钮listener
    private ActionListener commonActionListener;//通用按钮listener
    private CompoundButton.OnCheckedChangeListener cbChangeListener;//不再提示listener

    public static CommonDialog newInstance() {
        return new CommonDialog();
    }

    // ---------------------------- 私有方法 ----------------------------

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.base_dialog_common, container, false);
        titleContainer = view.findViewById(R.id.title_container);
        tvTitle = view.findViewById(R.id.tv_title);
        ivClose = view.findViewById(R.id.iv_close);
        contentContainer = view.findViewById(R.id.content_container);
        tvContent = view.findViewById(R.id.tv_content);
        cbNoTips = view.findViewById(R.id.cb_no_tips);
        actionContainer = view.findViewById(R.id.action_container);
        tvActionLeft = view.findViewById(R.id.tv_action_left);
        viewActionLine = view.findViewById(R.id.view_action_line);
        tvActionCommon = view.findViewById(R.id.tv_action_common);
        initDialogData();//弹窗数据
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            // 防止非UI线程设置layout导致崩溃
            if (getDialog() != null && getDialog().getWindow() != null) {
                // 设置window宽度固定为屏宽的0.75
                Window window = getDialog().getWindow();
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = (int) (ScreenUtils.getScreenWidth() * 0.8);
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//                if (contains && !autoClose) {
//                    // 当前窗口已存在dialog且dialog可重叠，去掉动画(避免窗口闪烁)
//                    window.setWindowAnimations(R.style.DialogNoAnim);
//                }
                window.setAttributes(params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化弹窗数据
     */
    private void initDialogData() {
        // 弹窗标题
        if (TextUtils.isEmpty(title)) {
            titleContainer.setVisibility(View.GONE);
        } else {
            titleContainer.setVisibility(View.VISIBLE);
            tvTitle.setText(title);
            if (titleSize > 0) {
                tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, titleSize);
            }
            if (titleColor != -1) {
                tvTitle.setTextColor(titleColor);
            }
            if (!titleBold) {
                tvTitle.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            }
            // 关闭icon
            if (!showClose) {
                ivClose.setVisibility(View.GONE);
            } else {
                ivClose.setVisibility(View.VISIBLE);
                if (closeResId > 0) {
                    ivClose.setImageResource(closeResId);
                }
                ivClose.setOnClickListener(v -> close());
            }
        }

        // 弹窗内容
        if (TextUtils.isEmpty(content)) {
            contentContainer.setVisibility(View.GONE);
        } else {
            contentContainer.setVisibility(View.VISIBLE);
            // 设置内容scrollView最大高度为屏高的0.6
            contentContainer.setMaxHeight((int) (ScreenUtils.getScreenHeight() * 0.6));
            if (isContentString) {
                // String
                tvContent.setText(content);
                if (contentSize > 0) {
                    tvContent.setTextSize(TypedValue.COMPLEX_UNIT_DIP, contentSize);
                }
                if (contentColor != -1) {
                    tvContent.setTextColor(contentColor);
                }
                if (contentBold) {
                    tvContent.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                }
            } else {
                // Html.fromHtml() or SpannableString
                tvContent.setText(content);
            }
            if (contentGravity != -99) {
                tvContent.setGravity(contentGravity);
            }
        }

        // 不再提示
        if (!showNoTips) {
            cbNoTips.setVisibility(View.GONE);
        } else {
            cbNoTips.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(noTipsText)) {
                cbNoTips.setText(noTipsText);
            }
            cbNoTips.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (cbChangeListener != null) {
                        cbChangeListener.onCheckedChanged(buttonView, isChecked);
                    }
                }
            });
        }

        // 左边按钮
        if (TextUtils.isEmpty(leftAction)) {
            tvActionLeft.setVisibility(View.GONE);
            viewActionLine.setVisibility(View.GONE);
        } else {
            tvActionLeft.setVisibility(View.VISIBLE);
            viewActionLine.setVisibility(View.VISIBLE);
            tvActionLeft.setText(leftAction);
            if (leftActionSize > 0) {
                tvActionLeft.setTextSize(TypedValue.COMPLEX_UNIT_DIP, leftActionSize);
            }
            if (leftActionColor != -1) {
                tvActionLeft.setTextColor(leftActionColor);
            }
            if (leftActionBold) {
                tvActionLeft.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            }
            tvActionLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (leftActionListener != null) {
                        leftActionListener.onClick(CommonDialog.this);
                    }
                    if (autoClose) close();
                }
            });
        }

        // 右边按钮
        if (TextUtils.isEmpty(commonAction)) {
            tvActionCommon.setVisibility(View.GONE);
        } else {
            tvActionCommon.setVisibility(View.VISIBLE);
            tvActionCommon.setText(commonAction);
            if (commonActionSize > 0) {
                tvActionCommon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, commonActionSize);
            }
            if (commonActionColor != -1) {
                tvActionCommon.setTextColor(commonActionColor);
            }
            if (commonActionBold) {
                tvActionCommon.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            }
            tvActionCommon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (commonActionListener != null) {
                        commonActionListener.onClick(CommonDialog.this);
                    }
                    if (autoClose) close();
                }
            });
        }

        // 按钮布局是否显示以commonAction为主
        if (TextUtils.isEmpty(commonAction)) {
            actionContainer.setVisibility(View.GONE);
        } else {
            actionContainer.setVisibility(View.VISIBLE);
        }

        if (getDialog() != null) {
            getDialog().setCanceledOnTouchOutside(canDismissOutSide);
            setCancelable(canCancel);
            // 返回健监听
            getDialog().setOnKeyListener((dialogInterface, i, keyEvent) -> {
                if (i == KeyEvent.KEYCODE_BACK) {
                    return shieldingBack;
                } else {
                    return false;
                }
            });
        }
    }

    // ------------------------- 对外提供API -------------------------

    /**
     * 弹窗标题
     */
    public CommonDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public CommonDialog setDefaultTitle() {
        this.title = StringUtil.getString(R.string.dialog_title);
        return this;
    }

    /**
     * 标题大小
     */
    public CommonDialog setTitleSize(int titleSize) {
        this.titleSize = titleSize;
        return this;
    }

    /**
     * 标题颜色
     */
    public CommonDialog setTitleColor(int titleColor) {
        this.titleColor = titleColor;
        return this;
    }

    /**
     * 标题是否加粗
     */
    public CommonDialog setTitleBold(boolean titleBold) {
        this.titleBold = titleBold;
        return this;
    }

    /**
     * 是否显示关闭icon
     */
    public CommonDialog setShowClose(boolean showClose) {
        this.showClose = showClose;
        return this;
    }

    /**
     * 关闭icon资源id
     */
    public CommonDialog setCloseResId(int closeResId) {
        this.closeResId = closeResId;
        return this;
    }

    /**
     * 弹窗内容(支持String/Html.fromHtml()/SpannableString)
     */
    public CommonDialog setContent(CharSequence content) {
        // String
        this.content = content;
        this.isContentString = true;
        this.dialogCanShow = !TextUtils.isEmpty(content);
        return this;
    }

    public CommonDialog setContent(Spanned content) {
        // Html.fromHtml()
        this.content = content;
        this.isContentString = false;
        this.dialogCanShow = !TextUtils.isEmpty(content);
        return this;
    }

    public CommonDialog setContent(SpannableString content) {
        // SpannableString
        this.content = content;
        this.isContentString = false;
        this.dialogCanShow = !TextUtils.isEmpty(content);
        return this;
    }

    /**
     * 内容大小
     */
    public CommonDialog setContentSize(int contentSize) {
        this.contentSize = contentSize;
        return this;
    }

    /**
     * 内容颜色
     */
    public CommonDialog setContentColor(int contentColor) {
        this.contentColor = contentColor;
        return this;
    }

    /**
     * 内容是否加粗
     */
    public CommonDialog setContentBold(boolean contentBold) {
        this.contentBold = contentBold;
        return this;
    }

    /**
     * 内容是否加粗
     */
    public CommonDialog setContentGravity(int contentGravity) {
        this.contentGravity = contentGravity;
        return this;
    }

    /**
     * 是否显示不再提示
     */
    public CommonDialog setShowNoTips(boolean showNoTips) {
        this.showNoTips = showNoTips;
        return this;
    }

    /**
     * 不再提示内容
     */
    public CommonDialog setNoTipsText(String noTipsText) {
        this.noTipsText = noTipsText;
        return this;
    }

    /**
     * 左边按钮内容
     */
    public CommonDialog setLeftAction(String leftAction) {
        this.leftAction = leftAction;
        return this;
    }

    /**
     * 左边按钮大小
     */
    public CommonDialog setLeftActionSize(int leftActionSize) {
        this.leftActionSize = leftActionSize;
        return this;
    }

    /**
     * 左边按钮颜色
     */
    public CommonDialog setLeftActionColor(int leftActionColor) {
        this.leftActionColor = leftActionColor;
        return this;
    }

    /**
     * 左边按钮是否加粗
     */
    public CommonDialog setLeftActionBold(boolean leftActionBold) {
        this.leftActionBold = leftActionBold;
        return this;
    }

    /**
     * 通用按钮内容
     */
    public CommonDialog setCommonAction(String commonAction) {
        this.commonAction = commonAction;
        return this;
    }

    /**
     * 通用按钮大小
     */
    public CommonDialog setCommonActionSize(int commonActionSize) {
        this.commonActionSize = commonActionSize;
        return this;
    }

    /**
     * 通用按钮颜色
     */
    public CommonDialog setCommonActionColor(int commonActionColor) {
        this.commonActionColor = commonActionColor;
        return this;
    }

    /**
     * 通用按钮是否加粗
     */
    public CommonDialog setCommonActionBold(boolean commonActionBold) {
        this.commonActionBold = commonActionBold;
        return this;
    }

    /**
     * 点击外部是否关闭
     */
    public CommonDialog setCanDismissOutSide(boolean canDismissOutSide) {
        this.canDismissOutSide = canDismissOutSide;
        return this;
    }

    /**
     * 是否屏蔽返回键
     */
    public CommonDialog setShieldingBack(boolean shieldingBack) {
        this.shieldingBack = shieldingBack;
        return this;
    }

    /**
     * 点击返回是否关闭
     */
    public CommonDialog setCanCancel(boolean canCancel) {
        this.canCancel = canCancel;
        return this;
    }

    /**
     * 点击操作按钮是否自动关闭
     */
    public CommonDialog setAutoClose(boolean autoClose) {
        this.autoClose = autoClose;
        return this;
    }

    /**
     * 左边按钮listener
     */
    public CommonDialog setLeftActionListener(ActionListener leftActionListener) {
        this.leftActionListener = leftActionListener;
        return this;
    }

    /**
     * 通用按钮listener
     */
    public CommonDialog setCommonActionListener(ActionListener commonActionListener) {
        this.commonActionListener = commonActionListener;
        return this;
    }

    /**
     * 不再提示listener
     */
    public CommonDialog setCbChangeListener(CompoundButton.OnCheckedChangeListener cbChangeListener) {
        this.cbChangeListener = cbChangeListener;
        return this;
    }

    @Override
    public void show(Context context) {
        if (filter()) return;
        if (!dialogCanShow) return;
        super.show(context);
    }

    // ------------------------- 接口定义 -------------------------

    /**
     * 按钮点击listener
     */
    public interface ActionListener {

        // 按钮点击
        void onClick(CommonDialog dialog);
    }
}
