package com.zhangshuo.common.dialog;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.TransformationMethod;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.zhangshuo.common.R;

public class CommonInputDialog extends BaseDialog {

    // 控件
    private ViewGroup titleContainer;//标题布局
    private TextView tvTitle;//弹窗标题
    private ImageView ivClose;//关闭icon
    private EditText etInput;//输入内容
    private TextView tvTextNum;//剩余字数
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

    private String content;//输入内容
    private int contentSize;//内容大小
    private int contentColor = -1;//内容颜色
    private String hint;//提示语内容
    private int hintColor = -1;//提示语颜色
    private int maxNum = 100;//内容总字数(默认100)

    private String leftAction;//左边按钮内容
    private int leftActionSize;//左边按钮大小
    private int leftActionColor = -1;//左边按钮颜色
    private boolean leftActionBold;//左边按钮是否加粗

    private String commonAction;//通用按钮内容
    private int commonActionSize;//通用按钮大小
    private int commonActionColor = -1;//通用按钮颜色
    private boolean commonActionBold;//通用按钮是否加粗

    private boolean canDismissOutSide = false;//点击外部是否关闭(默认false)
    private boolean canCancel = true;//点击返回是否关闭(默认true)
    private boolean autoClose = true;//是否自动关闭(默认true)

    private ActionListener leftActionListener;//左边按钮listener
    private ActionListener commonActionListener;//通用按钮listener
    private InputListener inputListener;//输入监听listener
    private boolean showCount;// 是否显示计数view
    private int editHeight = -2;// 编辑框高度
    private int maxLine = Integer.MAX_VALUE;//最大行数
    private TransformationMethod transformationMethod;
    private Handler mainHandler;

    public static CommonInputDialog newInstance() {
        return new CommonInputDialog();
    }

    // ---------------------------- 私有方法 ----------------------------

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.base_dialog_common_input, container, false);
        titleContainer = view.findViewById(R.id.title_container);
        tvTitle = view.findViewById(R.id.tv_title);
        ivClose = view.findViewById(R.id.iv_close);
        etInput = view.findViewById(R.id.et_input);
        tvTextNum = view.findViewById(R.id.tv_text_num);
        actionContainer = view.findViewById(R.id.action_container);
        tvActionLeft = view.findViewById(R.id.tv_action_left);
        viewActionLine = view.findViewById(R.id.view_action_line);
        tvActionCommon = view.findViewById(R.id.tv_action_common);
        mainHandler = new Handler(Looper.getMainLooper());
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
                params.width = (int) (ScreenUtils.getScreenWidth() * 0.75);
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

        // 输入内容
        if (!TextUtils.isEmpty(content)) {
            etInput.setText(content);
            etInput.setSelection(content.length());
        }
        if (contentSize > 0) {
            etInput.setText(contentSize);
        }
        if (contentColor != -1) {
            etInput.setTextColor(contentColor);
        }
        if (!TextUtils.isEmpty(hint)) {
            etInput.setHint(hint);
        }
        etInput.setMaxLines(maxLine);
        if (transformationMethod != null) {
            etInput.setTransformationMethod(transformationMethod);
        }
        if (hintColor != -1) {
            etInput.setHintTextColor(hintColor);
        }
        if (maxNum > 0) {
            etInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxNum) {
            }});
        }
        tvTextNum.setVisibility(showCount ? View.VISIBLE : View.GONE);
        if (inputListener != null) {
            inputListener.onEditing(textWatcher, etInput, etInput.getText().toString(), maxNum);
        } else {
            tvTextNum.setText(etInput.getText().toString().length() + "/" + maxNum);
        }
        if (editHeight > 0) {
            ViewGroup.LayoutParams edit = etInput.getLayoutParams();
            edit.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            edit.height = SizeUtils.dp2px(editHeight);
            etInput.setLayoutParams(edit);
        }
        etInput.addTextChangedListener(textWatcher);

        // 自动获取焦点
        try {
            mainHandler.postDelayed(() -> {
                if (isFragmentEnable() && etInput != null) {
                    KeyboardUtils.showSoftInput(etInput);
                }
            }, 100);
        } catch (Exception ignored) {
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
                        leftActionListener.onClick(CommonInputDialog.this, etInput.getText().toString());
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
                        commonActionListener.onClick(CommonInputDialog.this, etInput.getText().toString());
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
        }
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            // 输入监听
            if (inputListener != null) {
                inputListener.onEditing(textWatcher, etInput, etInput.getText().toString(), maxNum);
            } else {
                tvTextNum.setText(etInput.getText().toString().length() + "/" + maxNum);
            }
        }
    };

    // ------------------------- 对外提供API -------------------------

    /**
     * 弹窗标题
     */
    public CommonInputDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * 标题大小
     */
    public CommonInputDialog setTitleSize(int titleSize) {
        this.titleSize = titleSize;
        return this;
    }

    /**
     * 标题颜色
     */
    public CommonInputDialog setTitleColor(int titleColor) {
        this.titleColor = titleColor;
        return this;
    }

    /**
     * 标题是否加粗
     */
    public CommonInputDialog setTitleBold(boolean titleBold) {
        this.titleBold = titleBold;
        return this;
    }

    /**
     * 是否显示关闭icon
     */
    public CommonInputDialog setShowClose(boolean showClose) {
        this.showClose = showClose;
        return this;
    }

    /**
     * 关闭icon资源id
     */
    public CommonInputDialog setCloseResId(int closeResId) {
        this.closeResId = closeResId;
        return this;
    }

    /**
     * 输入内容
     */
    public CommonInputDialog setContent(String content) {
        this.content = content;
        return this;
    }

    /**
     * 内容大小
     */
    public CommonInputDialog setContentSize(int contentSize) {
        this.contentSize = contentSize;
        return this;
    }

    /**
     * 内容颜色
     */
    public CommonInputDialog setContentColor(int contentColor) {
        this.contentColor = contentColor;
        return this;
    }

    /**
     * 提示语内容
     */
    public CommonInputDialog setHint(String hint) {
        this.hint = hint;
        return this;
    }

    /**
     * 提示语颜色
     */
    public CommonInputDialog setHintColor(int hintColor) {
        this.hintColor = hintColor;
        return this;
    }

    /**
     * 内容总字数
     */
    public CommonInputDialog setMaxNum(int maxNum) {
        this.maxNum = maxNum;
        return this;
    }

    /**
     * 左边按钮内容
     */
    public CommonInputDialog setLeftAction(String leftAction) {
        this.leftAction = leftAction;
        return this;
    }

    /**
     * 左边按钮大小
     */
    public CommonInputDialog setLeftActionSize(int leftActionSize) {
        this.leftActionSize = leftActionSize;
        return this;
    }

    /**
     * 左边按钮颜色
     */
    public CommonInputDialog setLeftActionColor(int leftActionColor) {
        this.leftActionColor = leftActionColor;
        return this;
    }

    /**
     * 左边按钮是否加粗
     */
    public CommonInputDialog setLeftActionBold(boolean leftActionBold) {
        this.leftActionBold = leftActionBold;
        return this;
    }

    /**
     * 通用按钮内容
     */
    public CommonInputDialog setCommonAction(String commonAction) {
        this.commonAction = commonAction;
        return this;
    }

    /**
     * 通用按钮大小
     */
    public CommonInputDialog setCommonActionSize(int commonActionSize) {
        this.commonActionSize = commonActionSize;
        return this;
    }

    /**
     * 通用按钮颜色
     */
    public CommonInputDialog setCommonActionColor(int commonActionColor) {
        this.commonActionColor = commonActionColor;
        return this;
    }

    /**
     * 通用按钮是否加粗
     */
    public CommonInputDialog setCommonActionBold(boolean commonActionBold) {
        this.commonActionBold = commonActionBold;
        return this;
    }

    /**
     * 点击外部是否关闭
     */
    public CommonInputDialog setCanDismissOutSide(boolean canDismissOutSide) {
        this.canDismissOutSide = canDismissOutSide;
        return this;
    }

    /**
     * 点击返回是否关闭
     */
    public CommonInputDialog setCanCancel(boolean canCancel) {
        this.canCancel = canCancel;
        return this;
    }

    /**
     * 点击操作按钮是否自动关闭
     */
    public CommonInputDialog setAutoClose(boolean autoClose) {
        this.autoClose = autoClose;
        return this;
    }

    /**
     * 左边按钮listener
     */
    public CommonInputDialog setLeftActionListener(ActionListener leftActionListener) {
        this.leftActionListener = leftActionListener;
        return this;
    }

    /**
     * 通用按钮listener
     */
    public CommonInputDialog setCommonActionListener(ActionListener commonActionListener) {
        this.commonActionListener = commonActionListener;
        return this;
    }

    /**
     * 输入监听listener
     */
    public CommonInputDialog setInputListener(InputListener inputListener) {
        this.inputListener = inputListener;
        return this;
    }


    /**
     * 设置是否显示计数view
     */
    public CommonInputDialog setShowCount(boolean showCount) {
        this.showCount = showCount;
        return this;
    }

    /**
     * 设置编辑框高度
     */
    public CommonInputDialog setEditHeight(int editHeight) {
        this.editHeight = editHeight;
        return this;
    }

    /**
     * 设置最大行数
     */
    public CommonInputDialog setMaxLine(int maxLine) {
        this.maxLine = maxLine;
        return this;
    }

    /**
     * 设置转化方法（可以设置禁止换行）
     */
    public CommonInputDialog setTransformationMethod(TransformationMethod method) {
        this.transformationMethod = method;
        return this;
    }


    @Override
    public void show(Context context) {
        if (filter()) return;
        super.show(context);
    }

    @Override
    public void close() {
        try {
            // 收起键盘
            mainHandler = null;
            if (etInput != null) {
                KeyboardUtils.hideSoftInput(etInput);
            }
        } catch (Exception ignored) {
        }
        super.close();
    }

    // ------------------------- 接口定义 -------------------------

    /**
     * 输入内容listener
     */
    public interface InputListener {

        // 输入监听
        void onEditing(TextWatcher textWatcher, EditText editText, String curContent, int maxNum);
    }

    /**
     * 按钮点击listener
     */
    public interface ActionListener {

        // 按钮点击
        void onClick(CommonInputDialog dialog, String content);
    }
}
