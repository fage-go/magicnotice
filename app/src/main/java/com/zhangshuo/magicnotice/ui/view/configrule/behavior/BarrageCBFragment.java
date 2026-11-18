package com.zhangshuo.magicnotice.ui.view.configrule.behavior;

import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;

import com.zhangshuo.common.base.BaseFragment;
import com.zhangshuo.common.floatwindow.permission.FloatPermissionManager;
import com.zhangshuo.common.listener.OnClickCallback;
import com.zhangshuo.common.model.EventMessage;
import com.zhangshuo.common.router.RouterUtil;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.common.util.GsonUtils;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.common.util.UIUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.barrage.BarrageInfo;
import com.zhangshuo.magicnotice.barrage.BehaviorHelper;
import com.zhangshuo.magicnotice.config.EventCodes;
import com.zhangshuo.magicnotice.config.GlobalConfig;
import com.zhangshuo.magicnotice.config.RoutePaths;
import com.zhangshuo.magicnotice.databinding.FragmentCbBarrageBinding;
import com.zhangshuo.magicnotice.db.model.GlobalConfigData;
import com.zhangshuo.magicnotice.db.service.GlobalConfigDataService;
import com.zhangshuo.magicnotice.ui.dialog.SelectOptionsPopup;
import com.zhangshuo.magicnotice.ui.dialog.SelectColorPopup;
import com.zhangshuo.magicnotice.ui.dialog.SelectStepPopup;
import com.zhangshuo.magicnotice.ui.model.BarrageGlobalConfig;
import com.zhangshuo.magicnotice.ui.model.OptionModel;
import com.zhangshuo.magicnotice.ui.model.behavior.BarrageBehaviorModel;
import com.zhangshuo.magicnotice.ui.view.configrule.IConfigRule;
import com.zhangshuo.magicnotice.util.ActivityFreeformUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import razerdp.basepopup.BasePopupWindow;

public class BarrageCBFragment extends BaseFragment<FragmentCbBarrageBinding>
        implements IConfigRule {

    /**
     * 内容设置->大小、颜色，透明度
     * 背景设置->圆角，颜色，透明度
     * 动画效果->进入效果(禁止、平移、闪现、跳动）停留时间，出去效果（
     * 全局设置->同时出现个数，竖屏位置，横屏位置
     */

    private BarrageBehaviorModel barrageBehaviorModel;
    private BarrageGlobalConfig barrageGlobalConfig;
    private BarrageInfo barrageInfo;

    @Override
    protected void initView() {
        binding.tvContent.getPaint().setFakeBoldText(true);
        binding.tvFont.getPaint().setFakeBoldText(true);
        binding.tvBg.getPaint().setFakeBoldText(true);
        binding.tvAnim.getPaint().setFakeBoldText(true);
        binding.tvGlobal.getPaint().setFakeBoldText(true);
        bindClickListener();
        getGlobalConfig();
    }

    @Override
    protected void initData() {
        if (barrageBehaviorModel == null) {
            barrageBehaviorModel = BarrageBehaviorModel.getDefault();
        }
        barrageInfo = new BarrageInfo(BehaviorHelper.getTestNotificationData(), barrageBehaviorModel);

        binding.cbContentLogo.setChecked((barrageBehaviorModel.getContentType() & BarrageBehaviorModel.CONTENT_TYPE_LOGO) == BarrageBehaviorModel.CONTENT_TYPE_LOGO);
        binding.cbContentTitle.setChecked((barrageBehaviorModel.getContentType() & BarrageBehaviorModel.CONTENT_TYPE_TITLE) == BarrageBehaviorModel.CONTENT_TYPE_TITLE);
        binding.cbContentDetail.setChecked((barrageBehaviorModel.getContentType() & BarrageBehaviorModel.CONTENT_TYPE_TXT) == BarrageBehaviorModel.CONTENT_TYPE_TXT);

        showTipInfo(barrageBehaviorModel);
    }

    @Override
    public BaseFragment getFragment() {
        return this;
    }

    @Override
    public void onInitData(Object object) {
        if (object instanceof BarrageBehaviorModel) {
            barrageBehaviorModel = (BarrageBehaviorModel) object;
        }
        if (barrageBehaviorModel == null) {
            barrageBehaviorModel = BarrageBehaviorModel.getDefault();
        }
    }

    @Override
    public Object onResultData() {
        if (!FloatPermissionManager.getInstance().checkPermission(getContext())) {
            //没有权限，则申请权限
            FloatPermissionManager.getInstance().applyFloatWindow(getContext(), true);
            return null;
        }
        return barrageBehaviorModel;
    }

    private void getGlobalConfig() {
        GlobalConfigDataService.getGlobalConfig(GlobalConfig.KEY_BARRAGE, new AsyncCallback<GlobalConfigData>() {
            @Override
            public void onSuccess(GlobalConfigData result) {
                if (result != null) {
                    String value = result.getValue();
                    barrageGlobalConfig = GsonUtils.toBean(value, BarrageGlobalConfig.class);
                }
                if (barrageGlobalConfig == null) {
                    barrageGlobalConfig = BarrageGlobalConfig.getDefault();
                }
                showGlobalTipInfo(barrageGlobalConfig);
            }

            @Override
            public void onError(Throwable t) {
                barrageGlobalConfig = BarrageGlobalConfig.getDefault();
                showGlobalTipInfo(barrageGlobalConfig);
            }
        });
    }

    private void bindClickListener() {
        //展示内容
        //logo
        binding.cbContentLogo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int contentType = barrageBehaviorModel.getContentType();
                contentType &= ~BarrageBehaviorModel.CONTENT_TYPE_LOGO;
                if (isChecked) {
                    contentType = contentType | BarrageBehaviorModel.CONTENT_TYPE_LOGO;
                }
                barrageBehaviorModel.setContentType(contentType);
                showTipInfo(barrageBehaviorModel);
            }
        });
        //标题
        binding.cbContentTitle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int contentType = barrageBehaviorModel.getContentType();
                contentType &= ~BarrageBehaviorModel.CONTENT_TYPE_TITLE;
                if (isChecked) {
                    contentType = contentType | BarrageBehaviorModel.CONTENT_TYPE_TITLE;
                }
                barrageBehaviorModel.setContentType(contentType);
                showTipInfo(barrageBehaviorModel);
            }
        });
        //详情
        binding.cbContentDetail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int contentType = barrageBehaviorModel.getContentType();
                contentType &= ~BarrageBehaviorModel.CONTENT_TYPE_TXT;
                if (isChecked) {
                    contentType = contentType | BarrageBehaviorModel.CONTENT_TYPE_TXT;
                }
                barrageBehaviorModel.setContentType(contentType);
                showTipInfo(barrageBehaviorModel);
            }
        });
        //文本设置
        //字体大小
        binding.crFontSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.crFontSize.setSelect(true);
                new SelectStepPopup(getFragment(),
                        Arrays.asList(10, 14, 16, 18, 22), barrageBehaviorModel.getTxtSizeSP())
                        .setOnCallback(new SelectStepPopup.OnSelectCallback() {
                            @Override
                            public String onSelect(int value) {
                                barrageBehaviorModel.setTxtSizeSP(value);
                                showTipInfo(barrageBehaviorModel);
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
                        barrageBehaviorModel.getTxtColorHex())
                        .setOnCallback(new SelectColorPopup.OnSelectCallback() {
                            @Override
                            public void onSelect(String color) {
                                barrageBehaviorModel.setTxtColorHex(color);
                                showTipInfo(barrageBehaviorModel);
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
                        Arrays.asList(2, 3, 4, 5, 6, 7, 8, 9, 10), (int) (barrageBehaviorModel.getFontAlpha() * 10))
                        .setOnCallback(new SelectStepPopup.OnSelectCallback() {
                            @Override
                            public String onSelect(int value) {
                                barrageBehaviorModel.setFontAlpha(value / 10f);
                                showTipInfo(barrageBehaviorModel);
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
        //圆角尺寸
        binding.crBgSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.crBgSize.setSelect(true);
                new SelectStepPopup(getFragment(),
                        Arrays.asList(0, 4, 8, 12, 16, 20), barrageBehaviorModel.getBgRadius())
                        .setOnCallback(new SelectStepPopup.OnSelectCallback() {
                            @Override
                            public String onSelect(int value) {
                                barrageBehaviorModel.setBgRadius(value);
                                showTipInfo(barrageBehaviorModel);
                                return null;
                            }
                        }, new BasePopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                binding.crBgSize.setSelect(false);
                            }
                        })
                        .setPopupFadeEnable(true)
                        .setPopupGravity(Gravity.BOTTOM)
                        .showPopupWindow(v);
            }
        });
        //背景颜色
        binding.crBgColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.crBgColor.setSelect(true);
                new SelectColorPopup(getFragment(),
                        barrageBehaviorModel.getBgColorHex())
                        .setOnCallback(new SelectColorPopup.OnSelectCallback() {
                            @Override
                            public void onSelect(String color) {
                                barrageBehaviorModel.setBgColorHex(color);
                                showTipInfo(barrageBehaviorModel);
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
                        Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10), (int) (barrageBehaviorModel.getBgAlpha() * 10))
                        .setOnCallback(new SelectStepPopup.OnSelectCallback() {
                            @Override
                            public String onSelect(int value) {
                                barrageBehaviorModel.setBgAlpha(value / 10f);
                                showTipInfo(barrageBehaviorModel);
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

        //动画效果
        //进入动画
        binding.crAnimEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.crAnimEnter.setSelect(true);
                new SelectOptionsPopup(getFragment(),
                        Arrays.asList(
                                new OptionModel(getString(R.string.barrage_cb_option_enter_move), String.valueOf(BarrageBehaviorModel.ENTER_EFFECT_TYPE_MOVE)),
                                new OptionModel(getString(R.string.barrage_cb_option_enter_expand), String.valueOf(BarrageBehaviorModel.ENTER_EFFECT_TYPE_SLIDE)),
                                new OptionModel(getString(R.string.barrage_cb_options_enter_shake), String.valueOf(BarrageBehaviorModel.ENTER_EFFECT_TYPE_ALPHA)),
                                new OptionModel(getString(R.string.barrage_cb_options_enter_jump), String.valueOf(BarrageBehaviorModel.ENTER_EFFECT_TYPE_JUMP))
                        ), String.valueOf(barrageBehaviorModel.getEnterEffectType()))
                        .setOnCallback(new SelectOptionsPopup.OnCallback() {
                            @Override
                            public void onSelect(OptionModel animOptionModel) {
                                barrageBehaviorModel.setEnterEffectType(StringUtil.stringToInt(animOptionModel.getValue()));
                                showTipInfo(barrageBehaviorModel);
                                startEnterAnim();
                            }
                        }, new BasePopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                binding.crAnimEnter.setSelect(false);
                            }
                        })
                        .setPopupFadeEnable(true)
                        .setOffsetX(-UIUtil.dp2Px(10))
                        .setPopupGravity(Gravity.TOP)
                        .showPopupWindow(v);
            }
        });
        //停留时长
        binding.crAnimStay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.crAnimStay.setSelect(true);
                new SelectStepPopup(getFragment(),
                        Arrays.asList(500, 1000, 2000, 3000, 4000, 5000), barrageBehaviorModel.getStayEffectTime())
                        .setOnCallback(new SelectStepPopup.OnSelectCallback() {
                            @Override
                            public String onSelect(int value) {
                                barrageBehaviorModel.setStayEffectTime(value);
                                showTipInfo(barrageBehaviorModel);
                                return null;
                            }
                        }, new BasePopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                binding.crAnimStay.setSelect(false);
                            }
                        })
                        .setPopupFadeEnable(true)
                        .setOffsetY(UIUtil.dp2Px(10))
                        .setPopupGravity(Gravity.TOP | Gravity.CENTER)
                        .showPopupWindow(v);
            }
        });
        //退出动画
        binding.crAnimExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.crAnimExit.setSelect(true);
                new SelectOptionsPopup(getFragment(),
                        Arrays.asList(
                                new OptionModel(getString(R.string.barrage_cb_option_exit_move), String.valueOf(BarrageBehaviorModel.EXIT_EFFECT_TYPE_MOVE)),
                                new OptionModel(getString(R.string.barrage_cb_option_exit_slide), String.valueOf(BarrageBehaviorModel.EXIT_EFFECT_TYPE_SLIDE)),
                                new OptionModel(getString(R.string.barrage_cb_option_enter_alpha), String.valueOf(BarrageBehaviorModel.EXIT_EFFECT_TYPE_ALPHA)),
                                new OptionModel(getString(R.string.barrage_cb_option_enter_scale), String.valueOf(BarrageBehaviorModel.EXIT_EFFECT_TYPE_SCALE))
                        ), String.valueOf(barrageBehaviorModel.getExitEffectType()))
                        .setOnCallback(new SelectOptionsPopup.OnCallback() {
                            @Override
                            public void onSelect(OptionModel animOptionModel) {
                                barrageBehaviorModel.setExitEffectType(StringUtil.stringToInt(animOptionModel.getValue()));
                                showTipInfo(barrageBehaviorModel);
                                startExitAnim();
                            }
                        }, new BasePopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                binding.crAnimExit.setSelect(false);
                            }
                        })
                        .setPopupFadeEnable(true)
                        .setOffsetX(UIUtil.dp2Px(10))
                        .setPopupGravityMode(BasePopupWindow.GravityMode.ALIGN_TO_ANCHOR_SIDE, BasePopupWindow.GravityMode.RELATIVE_TO_ANCHOR)
                        .setPopupGravity(Gravity.TOP | Gravity.RIGHT)
                        .showPopupWindow(v);
            }
        });

        //全局配置
        binding.crGlobalNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RouterUtil.get().navigation(RoutePaths.APP_ACTIVITY_CONFIG_BARRAGE);
            }
        });
        //单击行为
        binding.crGlobalClick.setOnClickListener(new OnClickCallback() {
            @Override
            public void click(View v) {
                binding.crGlobalClick.setSelect(true);
                List<OptionModel> optionModelList = new ArrayList<>();
                optionModelList.add(new OptionModel(getString(R.string.barrage_cb_option_click_hide), String.valueOf(BarrageBehaviorModel.TYPE_CLICK_CANCEL)));
                optionModelList.add(new OptionModel(getString(R.string.barrage_cb_option_click_open), String.valueOf(BarrageBehaviorModel.TYPE_CLICK_MSG)));
                if (ActivityFreeformUtil.isFreeformSupport(getContext()).isSupport()) {
                    optionModelList.add(new OptionModel(getString(R.string.barrage_cb_option_click_float), String.valueOf(BarrageBehaviorModel.TYPE_CLICK_REPLAY_FREE)));
                }
                optionModelList.add(new OptionModel(getString(R.string.barrage_cb_option_click_collect), String.valueOf(BarrageBehaviorModel.TYPE_CLICK_COLLECT)));
                optionModelList.add(new OptionModel(getString(R.string.barrage_cb_option_click_msg_ball), String.valueOf(BarrageBehaviorModel.TYPE_CLICK_MSG_BALL)));
                new SelectOptionsPopup(getFragment(),
                        optionModelList, String.valueOf(barrageBehaviorModel.getClickType()))
                        .setOnCallback(new SelectOptionsPopup.OnCallback() {
                            @Override
                            public void onSelect(OptionModel animOptionModel) {
                                barrageBehaviorModel.setClickType(StringUtil.stringToInt(animOptionModel.getValue()));
                                showTipInfo(barrageBehaviorModel);
                            }
                        }, new BasePopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                binding.crGlobalClick.setSelect(false);
                            }
                        })
                        .setPopupFadeEnable(true)
                        .setOffsetX(-UIUtil.dp2Px(10))
                        .setPopupGravity(Gravity.TOP | Gravity.CENTER)
                        .showPopupWindow(v);
            }
        });
        //双击行为
        binding.crGlobalClickDouble.setOnClickListener(new OnClickCallback() {
            @Override
            public void click(View v) {
                binding.crGlobalClick.setSelect(true);
                List<OptionModel> optionModelList = new ArrayList<>();
                optionModelList.add(new OptionModel(getString(R.string.barrage_cb_option_clickd_cancel), String.valueOf(BarrageBehaviorModel.TYPE_CLICK_CANCEL)));
                optionModelList.add(new OptionModel(getString(R.string.barrage_cb_option_clickd_open), String.valueOf(BarrageBehaviorModel.TYPE_CLICK_MSG)));
                if (ActivityFreeformUtil.isFreeformSupport(getContext()).isSupport()) {
                    optionModelList.add(new OptionModel(getString(R.string.barrage_cb_option_clickd_float), String.valueOf(BarrageBehaviorModel.TYPE_CLICK_REPLAY_FREE)));
                }
                optionModelList.add(new OptionModel(getString(R.string.barrage_cb_option_clickd_collect), String.valueOf(BarrageBehaviorModel.TYPE_CLICK_COLLECT)));
                optionModelList.add(new OptionModel(getString(R.string.barrage_cb_option_clickd_msg), String.valueOf(BarrageBehaviorModel.TYPE_CLICK_MSG_BALL)));
                new SelectOptionsPopup(getFragment(),
                        optionModelList, String.valueOf(barrageBehaviorModel.getDoubleClickType()))
                        .setOnCallback(new SelectOptionsPopup.OnCallback() {
                            @Override
                            public void onSelect(OptionModel animOptionModel) {
                                barrageBehaviorModel.setDoubleClickType(StringUtil.stringToInt(animOptionModel.getValue()));
                                showTipInfo(barrageBehaviorModel);
                            }
                        }, new BasePopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                binding.crGlobalClick.setSelect(false);
                            }
                        })
                        .setPopupFadeEnable(true)
                        .setOffsetX(-UIUtil.dp2Px(10))
                        .setPopupGravity(Gravity.TOP | Gravity.CENTER)
                        .showPopupWindow(v);
            }
        });
    }

    @Override
    public void onReceiveEvent(EventMessage eventMessage) {
        super.onReceiveEvent(eventMessage);
        if (eventMessage.getCode() == EventCodes.REFRESH_BARRAGE_GLOBAL) {
            getGlobalConfig();
        }
    }


    private void showTipInfo(BarrageBehaviorModel model) {

        binding.crFontSize.setValue(getFontSizeTip(model.getTxtSizeSP()));
        binding.crFontColor.setValue(model.getTxtColorHex());
        binding.crFontAlpha.setValue(getAlphaTip(model.getFontAlpha()));

        binding.crBgSize.setValue(getRadiusSizeTip(model.getBgRadius()));
        binding.crBgColor.setValue(model.getBgColorHex());
        binding.crBgAlpha.setValue(getAlphaTip(model.getBgAlpha()));

        binding.crAnimEnter.setValue(getEnterAnimTip(model.getEnterEffectType()));
        binding.crAnimStay.setValue(getStayAnimTip(model.getStayEffectTime()));
        binding.crAnimExit.setValue(getExitAnimTip(model.getExitEffectType()));

        binding.crGlobalClick.setValue(getClickType(model.getClickType()));
        binding.crGlobalClickDouble.setValue(getClickType(model.getDoubleClickType()));
        String textContent = String.format(
                getString(R.string.barrage_cb_effect_tip),
                BarrageBehaviorModel.getClickTip(model.getClickType()),
                BarrageBehaviorModel.getClickTip(model.getDoubleClickType())
        );
        barrageInfo.getNotificationData().setContent(textContent);
        barrageInfo.setBehaviorModel(model);
        binding.barrageView.setBarrageInfo(barrageInfo);
    }

    private void showGlobalTipInfo(BarrageGlobalConfig model) {
        if (model == null) {
            binding.tvGlobal.setVisibility(View.GONE);
            binding.llGlobal.setVisibility(View.GONE);
        } else {
            binding.tvGlobal.setVisibility(View.VISIBLE);
            binding.llGlobal.setVisibility(View.VISIBLE);
            binding.crGlobalNum.setValue(getGlobalNums(model.getMaxLines()));
        }
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

    private String getRadiusSizeTip(int radius) {
        // 0, 4, 8, 12, 16, 20
        // 改为 百分比这种 ，统一乘5
        return String.valueOf(radius * 5) + " %";

    }

    private String getAlphaTip(float alpha) {
        int alphaValue = (int) (alpha * 100);
        return String.valueOf(alphaValue) + " %";
    }

    private String getEnterAnimTip(int animStyle) {
        StringBuilder stringBuilder = new StringBuilder();
        switch (animStyle) {
            case BarrageBehaviorModel.ENTER_EFFECT_TYPE_MOVE:
                stringBuilder.append(getString(R.string.barrage_cb_option_enter_move));
                break;
            case BarrageBehaviorModel.ENTER_EFFECT_TYPE_SLIDE:
                stringBuilder.append(getString(R.string.barrage_cb_option_enter_expand));
                break;
            case BarrageBehaviorModel.ENTER_EFFECT_TYPE_ALPHA:
                stringBuilder.append(getString(R.string.barrage_cb_options_enter_shake));
                break;
            case BarrageBehaviorModel.ENTER_EFFECT_TYPE_JUMP:
                stringBuilder.append(getString(R.string.barrage_cb_options_enter_jump));
                break;
        }
        return stringBuilder.toString();
    }


    private String getStayAnimTip(int stayTime) {
        String format = String.format(getString(R.string.barrage_cb_stayanim_tip), (stayTime / 1000f));
        return format.replace(".0", "");
    }

    private String getExitAnimTip(int animStyle) {
        StringBuilder stringBuilder = new StringBuilder();
        switch (animStyle) {
            case BarrageBehaviorModel.EXIT_EFFECT_TYPE_MOVE:
                stringBuilder.append(getString(R.string.barrage_cb_option_exit_move));
                break;
            case BarrageBehaviorModel.EXIT_EFFECT_TYPE_SLIDE:
                stringBuilder.append(getString(R.string.barrage_cb_option_exit_slide));
                break;
            case BarrageBehaviorModel.EXIT_EFFECT_TYPE_ALPHA:
                stringBuilder.append(getString(R.string.barrage_cb_option_enter_alpha));
                break;
            case BarrageBehaviorModel.EXIT_EFFECT_TYPE_SCALE:
                stringBuilder.append(getString(R.string.barrage_cb_option_enter_scale));
                break;
        }
        return stringBuilder.toString();
    }

    private void startEnterAnim() {
        binding.barrageView.clearAnimation();
        int animResId = R.anim.barrage_window_enter_move;
        switch (barrageInfo.getBehaviorModel().getEnterEffectType()) {
            case BarrageBehaviorModel.ENTER_EFFECT_TYPE_ALPHA:
                animResId = R.anim.barrage_window_enter_alpha;
                break;
            case BarrageBehaviorModel.ENTER_EFFECT_TYPE_MOVE:
                animResId = R.anim.barrage_window_enter_move;
                break;
            case BarrageBehaviorModel.ENTER_EFFECT_TYPE_SLIDE:
                animResId = R.anim.barrage_window_enter_slide;
                break;
            case BarrageBehaviorModel.ENTER_EFFECT_TYPE_JUMP:
                animResId = R.anim.barrage_window_enter_jump;
                break;
        }
        Animation animation = AnimationUtils.loadAnimation(getContext(), animResId);
        binding.barrageView.startAnimation(animation);
    }

    private void startExitAnim() {
        binding.barrageView.clearAnimation();
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
        binding.barrageView.startAnimation(animation);
    }

    private String getGlobalNums(int num) {
        return String.format(StringUtil.getString(R.string.barrage_cb_global_num), num);
    }

    private String getGlobalPoint(int top, int maxHeight) {
        int offset = (int) (top * 100f / maxHeight);
        return getString(R.string.barrage_cb_global_point) + offset + "%";
    }

    private String getClickType(int clickType) {
        StringBuilder stringBuilder = new StringBuilder();
        switch (clickType) {
            case BarrageBehaviorModel.TYPE_CLICK_CANCEL:
                stringBuilder.append(StringUtil.getString(R.string.barrage_cb_option_click_hide));
                break;
            case BarrageBehaviorModel.TYPE_CLICK_MSG:
                stringBuilder.append(StringUtil.getString(R.string.barrage_cb_option_click_open));
                break;
            case BarrageBehaviorModel.TYPE_CLICK_REPLAY_FREE:
                stringBuilder.append(StringUtil.getString(R.string.barrage_cb_option_click_float));
                break;
            case BarrageBehaviorModel.TYPE_CLICK_COLLECT:
                stringBuilder.append(StringUtil.getString(R.string.barrage_cb_option_click_collect));
                break;
            case BarrageBehaviorModel.TYPE_CLICK_MSG_BALL:
                stringBuilder.append(StringUtil.getString(R.string.barrage_cb_option_click_msg_ball));
                break;
        }
        return stringBuilder.toString();
    }

}
