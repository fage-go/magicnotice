package com.zhangshuo.magicnotice.ui.view.configrule.behavior;

import android.view.Gravity;
import android.view.View;

import com.zhangshuo.common.base.BaseFragment;
import com.zhangshuo.common.floatwindow.permission.FloatPermissionManager;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.databinding.FragmentCbFloatBallBinding;
import com.zhangshuo.magicnotice.ui.dialog.SelectOptionsPopup;
import com.zhangshuo.magicnotice.ui.dialog.SelectColorPopup;
import com.zhangshuo.magicnotice.ui.dialog.SelectStepPopup;
import com.zhangshuo.magicnotice.ui.model.OptionModel;
import com.zhangshuo.magicnotice.ui.model.behavior.FloatBallModel;
import com.zhangshuo.magicnotice.ui.view.configrule.IConfigRule;
import com.zhangshuo.common.util.UIUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import razerdp.basepopup.BasePopupWindow;

public class FloatBallCBFragment extends BaseFragment<FragmentCbFloatBallBinding>
        implements IConfigRule {

    private FloatBallModel floatBallModel;

    @Override
    protected void initView() {
        binding.tvFont.getPaint().setFakeBoldText(true);
        binding.tvBg.getPaint().setFakeBoldText(true);
        binding.tvOpt.getPaint().setFakeBoldText(true);

        bindClickListener();
    }

    @Override
    protected void initData() {
        showTipInfo(floatBallModel);
    }

    @Override
    public BaseFragment getFragment() {
        return this;
    }

    @Override
    public void onInitData(Object object) {
        if (object instanceof FloatBallModel) {
            floatBallModel = (FloatBallModel) object;
        }
        if (floatBallModel == null) {
            floatBallModel = FloatBallModel.getDefault();
        }
    }

    @Override
    public Object onResultData() {
        if (!FloatPermissionManager.getInstance().checkPermission(getContext())) {
            //没有权限，则申请权限
            FloatPermissionManager.getInstance().applyFloatWindow(getContext(), true);
            return null;
        }
        return floatBallModel;
    }

    private void bindClickListener() {
        //文本设置
        //字体大小
        binding.crFontSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.crFontSize.setSelect(true);
                new SelectStepPopup(getFragment(),
                        Arrays.asList(10, 14, 16, 18, 22), floatBallModel.getTxtSizeSP())
                        .setOnCallback(new SelectStepPopup.OnSelectCallback() {
                            @Override
                            public String onSelect(int value) {
                                floatBallModel.setTxtSizeSP(value);
                                showTipInfo(floatBallModel);
                                return null;
                            }
                        }, new BasePopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                binding.crFontSize.setSelect(false);
                            }
                        })
                        .setPopupFadeEnable(true)
                        .setPopupGravity(Gravity.BOTTOM)
                        .showPopupWindow(v);
            }
        });
        //字体颜色
        binding.crFontColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.crFontColor.setSelect(true);
                new SelectColorPopup(getFragment(),
                        floatBallModel.getTxtColorHex())
                        .setOnCallback(new SelectColorPopup.OnSelectCallback() {
                            @Override
                            public void onSelect(String color) {
                                floatBallModel.setTxtColorHex(color);
                                showTipInfo(floatBallModel);
                            }
                        }, new BasePopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                binding.crFontColor.setSelect(false);
                            }
                        })
                        .setPopupFadeEnable(true)
                        .setPopupGravity(Gravity.BOTTOM | Gravity.CENTER)
                        .showPopupWindow(v);

            }
        });
        //字体透明度
        binding.crFontAlpha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.crFontAlpha.setSelect(true);
                new SelectStepPopup(getFragment(),
                        Arrays.asList(2, 3, 4, 5, 6, 7, 8, 9, 10), (int) (floatBallModel.getFontAlpha() * 10))
                        .setOnCallback(new SelectStepPopup.OnSelectCallback() {
                            @Override
                            public String onSelect(int value) {
                                floatBallModel.setFontAlpha(value / 10f);
                                showTipInfo(floatBallModel);
                                return null;
                            }
                        }, new BasePopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                binding.crFontAlpha.setSelect(false);
                            }
                        })
                        .setPopupFadeEnable(true)
                        .setPopupGravityMode(BasePopupWindow.GravityMode.ALIGN_TO_ANCHOR_SIDE, BasePopupWindow.GravityMode.RELATIVE_TO_ANCHOR)
                        .setPopupGravity(Gravity.BOTTOM | Gravity.RIGHT)
                        .showPopupWindow(v);
            }
        });
        //背景设置
        //展示形势
        binding.crBgType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.crBgType.setSelect(true);
                new SelectOptionsPopup(getFragment(),
                        Arrays.asList(
                                new OptionModel(getString(R.string.float_ball_cb_option_normal), String.valueOf(FloatBallModel.TYPE_BG_NORMAL)),
                                new OptionModel(getString(R.string.float_ball_cb_option_edge), String.valueOf(FloatBallModel.TYPE_BG_EDGE)),
                                new OptionModel(getString(R.string.float_ball_cb_option_logo), String.valueOf(FloatBallModel.TYPE_BG_LOGO))
                        ), String.valueOf(floatBallModel.getBgType()))
                        .setOnCallback(new SelectOptionsPopup.OnCallback() {
                            @Override
                            public void onSelect(OptionModel animOptionModel) {
                                floatBallModel.setBgType(StringUtil.stringToInt(animOptionModel.getValue()));
                                showTipInfo(floatBallModel);
                            }
                        }, new BasePopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                binding.crBgType.setSelect(false);
                            }
                        })
                        .setPopupFadeEnable(true)
                        .setOffsetX(-UIUtil.dp2Px(10))
                        .setPopupGravity(Gravity.TOP)
                        .showPopupWindow(v);
            }
        });
        //背景颜色
        binding.crBgColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.crBgColor.setSelect(true);
                new SelectColorPopup(getFragment(),
                        floatBallModel.getBgColorHex())
                        .setOnCallback(new SelectColorPopup.OnSelectCallback() {
                            @Override
                            public void onSelect(String color) {
                                floatBallModel.setBgColorHex(color);
                                showTipInfo(floatBallModel);
                            }
                        }, new BasePopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                binding.crBgColor.setSelect(false);
                            }
                        })
                        .setPopupFadeEnable(true)
                        .setPopupGravity(Gravity.BOTTOM | Gravity.CENTER)
                        .showPopupWindow(v);

            }
        });
        //背景透明度
        binding.crBgAlpha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.crBgAlpha.setSelect(true);
                new SelectStepPopup(getFragment(),
                        Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10), (int) (floatBallModel.getBgAlpha() * 10))
                        .setOnCallback(new SelectStepPopup.OnSelectCallback() {
                            @Override
                            public String onSelect(int value) {
                                floatBallModel.setBgAlpha(value / 10f);
                                showTipInfo(floatBallModel);
                                return null;
                            }
                        }, new BasePopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                binding.crBgAlpha.setSelect(false);
                            }
                        })
                        .setPopupFadeEnable(true)
                        .setPopupGravityMode(BasePopupWindow.GravityMode.ALIGN_TO_ANCHOR_SIDE, BasePopupWindow.GravityMode.RELATIVE_TO_ANCHOR)
                        .setPopupGravity(Gravity.BOTTOM | Gravity.RIGHT)
                        .showPopupWindow(v);
            }
        });
        //特殊设置
        binding.crOptSingle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.crOptSingle.setSelect(true);
                List<OptionModel> optionModelList = new ArrayList<>();
                optionModelList.add(new OptionModel(getString(R.string.float_ball_cb_click_none), String.valueOf(FloatBallModel.TYPE_CLICK_NONE)));
//                optionModelList.add(new OptionModel(getString(R.string.float_ball_cb_click_expand), String.valueOf(FloatBallModel.TYPE_CLICK_MSG)));
                optionModelList.add(new OptionModel(getString(R.string.float_ball_cb_click_small), String.valueOf(FloatBallModel.TYPE_CLICK_DETAIL_SMALL)));
                optionModelList.add(new OptionModel(getString(R.string.float_ball_cb_click_detail), String.valueOf(FloatBallModel.TYPE_CLICK_DETAIL)));
                new SelectOptionsPopup(getFragment(),
                        optionModelList, String.valueOf(floatBallModel.getClickType()))
                        .setOnCallback(new SelectOptionsPopup.OnCallback() {
                            @Override
                            public void onSelect(OptionModel animOptionModel) {
                                floatBallModel.setClickType(StringUtil.stringToInt(animOptionModel.getValue()));
                                showTipInfo(floatBallModel);
                            }
                        }, new BasePopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                binding.crOptSingle.setSelect(false);
                            }
                        })
                        .setPopupFadeEnable(true)
                        .setOffsetX(-UIUtil.dp2Px(10))
                        .setPopupGravity(Gravity.TOP | Gravity.CENTER)
                        .showPopupWindow(v);
            }
        });
    }

    private void showTipInfo(FloatBallModel model) {
        binding.crFontSize.setValue(getFontSizeTip(model.getTxtSizeSP()));
        binding.crFontColor.setValue(model.getTxtColorHex());
        binding.crFontAlpha.setValue(getAlphaTip(model.getFontAlpha()));

        binding.crBgType.setValue(getBgType(model.getBgType()));
        binding.crBgColor.setValue(model.getBgColorHex());
        binding.crBgAlpha.setValue(getAlphaTip(model.getBgAlpha()));

        binding.crOptSingle.setValue(getClickType(model.getClickType()));

        binding.floatBallView.setFloatBallModel(model);
        binding.floatBallView.setDataInfo(99, getCtx().getPackageName(), null);
    }

    private String getFontSizeTip(int fontSize) {
        //原始为10，12，14，16，18，20，22
        //改为 极小10，小14，标准16，大18，特大22
        List<Integer> fontSizArray = Arrays.asList(10, 14, 16, 18, 22);
        List<String> fontTipArray = Arrays.asList(getString(R.string.cb_font_size_10), getString(R.string.cb_font_size_14),
                getString(R.string.cb_font_size_16), getString(R.string.cb_font_size_18), getString(R.string.cb_font_size_22));
        int i = fontSizArray.indexOf(fontSize);
        if (i != -1) {
            return fontTipArray.get(i);
        } else {
            return getString(R.string.cb_font_size_16);
        }
    }

    private String getAlphaTip(float alpha) {
        int alphaValue = (int) (alpha * 100);
        return String.valueOf(alphaValue) + " %";
    }

    private String getBgType(int bgType) {
        StringBuilder stringBuilder = new StringBuilder();
        switch (bgType) {
            case FloatBallModel.TYPE_BG_NORMAL:
                stringBuilder.append(getString(R.string.float_ball_cb_option_normal));
                break;
            case FloatBallModel.TYPE_BG_EDGE:
                stringBuilder.append(getString(R.string.float_ball_cb_option_edge));
                break;
            case FloatBallModel.TYPE_BG_LOGO:
                stringBuilder.append(getString(R.string.float_ball_cb_option_logo));
                break;
        }
        return stringBuilder.toString();
    }

    private String getClickType(int clickType) {
        StringBuilder stringBuilder = new StringBuilder();
        switch (clickType) {
            case FloatBallModel.TYPE_CLICK_NONE:
                stringBuilder.append(getString(R.string.float_ball_cb_click_none));
                break;
            case FloatBallModel.TYPE_CLICK_MSG:
                stringBuilder.append(getString(R.string.float_ball_cb_click_expand));
                break;
            case FloatBallModel.TYPE_CLICK_DETAIL_SMALL:
                stringBuilder.append(getString(R.string.float_ball_cb_click_small));
                break;
            case FloatBallModel.TYPE_CLICK_DETAIL:
                stringBuilder.append(getString(R.string.float_ball_cb_click_detail));
                break;
        }
        return stringBuilder.toString();
    }

}
