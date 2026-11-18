package com.zhangshuo.magicnotice.ui.view.configrule.behavior;

import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;

import com.zhangshuo.common.base.BaseFragment;
import com.zhangshuo.common.listener.OnClickCallback;
import com.zhangshuo.common.router.RouterUtil;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.common.util.UIUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.config.RoutePaths;
import com.zhangshuo.magicnotice.databinding.FragmentCbLightBinding;
import com.zhangshuo.magicnotice.ui.dialog.SelectColorPopup;
import com.zhangshuo.magicnotice.ui.dialog.SelectGradualColorDialog;
import com.zhangshuo.magicnotice.ui.dialog.SelectOptionsPopup;
import com.zhangshuo.magicnotice.ui.dialog.SelectStepPopup;
import com.zhangshuo.magicnotice.ui.model.OptionModel;
import com.zhangshuo.magicnotice.ui.model.behavior.LightModel;
import com.zhangshuo.magicnotice.ui.view.StepSeekBar;
import com.zhangshuo.magicnotice.ui.view.configrule.IConfigRule;
import com.zhangshuo.magicnotice.util.ColorUtil;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import razerdp.basepopup.BasePopupWindow;

public class LightCBFragment extends BaseFragment<FragmentCbLightBinding>
        implements IConfigRule {

    private LightModel lightModel;


    @Override
    protected void initView() {
        binding.tvConfigSet.setOnClickListener(new OnClickCallback() {
            @Override
            public void click(View v) {
                RouterUtil.get().navigation(RoutePaths.APP_ACTIVITY_CUTOUT_LIGHT);
            }
        });
        binding.tvStyle.getPaint().setFakeBoldText(true);


        binding.crStyleType.setValue(lightModel.getViewTypeName());
        binding.crStyleType.setOnClickListener(new OnClickCallback() {
            @Override
            public void click(View v) {
                binding.crStyleType.setSelect(true);
                List<OptionModel> optionModelList = new LinkedList<>();
                optionModelList.add(new OptionModel(getString(R.string.light_behavior_view_border), String.valueOf(LightModel.TYPE_BORDER)));
                optionModelList.add(new OptionModel(getString(R.string.light_behavior_view_cutout), String.valueOf(LightModel.TYPE_CUTOUT)));
                optionModelList.add(new OptionModel(getString(R.string.light_behavior_view_slide), String.valueOf(LightModel.TYPE_SLIDE)));
                new SelectOptionsPopup(getFragment(),
                        optionModelList, String.valueOf(lightModel.getViewType()))
                        .setOnCallback(new SelectOptionsPopup.OnCallback() {
                            @Override
                            public void onSelect(OptionModel animOptionModel) {
                                lightModel.setViewType(StringUtil.stringToInt(animOptionModel.getValue()));
                                binding.crStyleType.setValue(lightModel.getViewTypeName());
                                showLight();
                            }
                        }, new BasePopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                binding.crStyleType.setSelect(false);
                            }
                        })
                        .setPopupFadeEnable(true)
                        .setOffsetX(-UIUtil.dp2Px(10))
                        .setPopupGravity(Gravity.BOTTOM | Gravity.CENTER)
                        .showPopupWindow(v);
            }
        });

        binding.crStyleAnim.setValue(lightModel.getAnimTypeName());
        binding.crStyleAnim.setOnClickListener(new OnClickCallback() {
            @Override
            public void click(View v) {
                binding.crStyleAnim.setSelect(true);
                List<OptionModel> optionModelList = new LinkedList<>();
                optionModelList.add(new OptionModel(getString(R.string.light_behavior_anim_fill), String.valueOf(LightModel.ANIM_FILL)));
                optionModelList.add(new OptionModel(getString(R.string.light_behavior_anim_fade), String.valueOf(LightModel.ANIM_FADE)));
                optionModelList.add(new OptionModel(getString(R.string.light_behavior_anim_move), String.valueOf(LightModel.ANIM_MOVE)));
                optionModelList.add(new OptionModel(getString(R.string.light_behavior_anim_breath), String.valueOf(LightModel.ANIM_BREATH)));
                new SelectOptionsPopup(getFragment(),
                        optionModelList, String.valueOf(lightModel.getViewType()))
                        .setOnCallback(new SelectOptionsPopup.OnCallback() {
                            @Override
                            public void onSelect(OptionModel animOptionModel) {
                                lightModel.setAnimType(StringUtil.stringToInt(animOptionModel.getValue()));
                                binding.crStyleAnim.setValue(lightModel.getAnimTypeName());
                                showLight();
                            }
                        }, new BasePopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                binding.crStyleAnim.setSelect(false);
                            }
                        })
                        .setPopupFadeEnable(true)
                        .setOffsetX(-UIUtil.dp2Px(10))
                        .setPopupGravity(Gravity.BOTTOM | Gravity.CENTER)
                        .showPopupWindow(v);
            }
        });

        binding.crStyleColor.setValue(lightModel.getColorArray());
        binding.crStyleColor.setOnClickListener(new OnClickCallback() {
            @Override
            public void click(View v) {
                binding.crStyleColor.setSelect(true);
                List<String> colorList = ColorUtil.getGradualColorList();
                colorList.add("");//选择项
                SelectGradualColorDialog selectGradualColorDialog = SelectGradualColorDialog.create()
                        .setColorList(colorList)
                        .setSelectColor(lightModel.getColorArray())
                        .setOnSelectCallback(new SelectGradualColorDialog.OnSelectCallback() {
                            @Override
                            public void onSelect(String color) {
                                lightModel.setColorArray(color);
                                binding.crStyleColor.setValue(color);
                                showLight();
                            }
                        });
                selectGradualColorDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        binding.crStyleColor.setSelect(false);
                    }
                });
                selectGradualColorDialog.show(getCtx());
            }
        });

        binding.crEffectSpeed.setValue(getSpeedTip(lightModel.getSpeed()));
        binding.crEffectSpeed.setOnClickListener(new OnClickCallback() {
            @Override
            public void click(View v) {
                binding.crEffectSpeed.setSelect(false);
                new SelectStepPopup(getFragment(),
                        Arrays.asList(0, 1, 2), lightModel.getSpeed())
                        .setOnCallback(new SelectStepPopup.OnSelectCallback() {
                            @Override
                            public String onSelect(int value) {
                                lightModel.setSpeed(value);
                                binding.crEffectSpeed.setValue(getSpeedTip(lightModel.getSpeed()));
                                showLight();
                                return null;
                            }
                        }, new BasePopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                binding.crEffectSpeed.setSelect(false);
                            }
                        })
                        .setPopupFadeEnable(true)
                        .setOffsetY(UIUtil.dp2Px(10))
                        .setPopupGravity(Gravity.TOP | Gravity.CENTER)
                        .showPopupWindow(v);
            }
        });
        binding.crEffectStroke.setValue(getStrokeWidthTip(lightModel.getStrokeWidth()));
        binding.crEffectStroke.setOnClickListener(new OnClickCallback() {
            @Override
            public void click(View v) {
                binding.crEffectStroke.setSelect(true);
                int dp1 = UIUtil.dp2Px(1);
                List<Integer> fontSizArray = Arrays.asList(dp1, dp1 * 2, dp1 * 3, dp1 * 4, dp1 * 8);
                new SelectStepPopup(getFragment(), fontSizArray, lightModel.getStrokeWidth())
                        .setOnCallback(new SelectStepPopup.OnSelectCallback() {
                            @Override
                            public String onSelect(int value) {
                                lightModel.setStrokeWidth(value);
                                binding.crEffectStroke.setValue(getStrokeWidthTip(lightModel.getStrokeWidth()));
                                showLight();
                                return null;
                            }
                        }, new BasePopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                binding.crEffectStroke.setSelect(false);
                            }
                        })
                        .setPopupFadeEnable(true)
                        .setPopupGravityMode(BasePopupWindow.GravityMode.ALIGN_TO_ANCHOR_SIDE, BasePopupWindow.GravityMode.RELATIVE_TO_ANCHOR)
                        .setPopupGravity(Gravity.TOP | Gravity.RIGHT)
                        .showPopupWindow(v);
            }
        });
        binding.crEffectColor.setValue(lightModel.getColorShadow());
        binding.crEffectColor.setOnClickListener(new OnClickCallback() {
            @Override
            public void click(View v) {
                binding.crEffectColor.setSelect(true);
                new SelectColorPopup(getFragment(),
                        lightModel.getColorShadow())
                        .canNone(true)
                        .setOnCallback(new SelectColorPopup.OnSelectCallback() {
                            @Override
                            public void onSelect(String color) {
                                lightModel.setColorShadow(color);
                                binding.crEffectColor.setValue(lightModel.getColorShadow());
                                showLight();
                            }
                        }, new BasePopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                binding.crEffectColor.setSelect(false);
                            }
                        })
                        .setPopupFadeEnable(true)
                        .setPopupGravity(Gravity.BOTTOM | Gravity.CENTER)
                        .showPopupWindow(v);
            }
        });
        binding.tvLightCount.getPaint().setFakeBoldText(true);
        binding.tvLightCount.setText(String.format(StringUtil.getString(R.string.cb_count_tip), lightModel.getRepeatCount()));
        binding.stepSeekCount.setProgress(lightModel.getRepeatCount() - 1);
        binding.stepSeekCount.setMaxProgress(5);
        binding.stepSeekCount.invalidate();
        binding.stepSeekCount.setOnProgressCallback(new StepSeekBar.OnProgressCallback() {
            @Override
            public void onProgress(int maxProgress, int progress) {
                lightModel.setRepeatCount(progress + 1);
                binding.tvLightCount.setText(String.format(StringUtil.getString(R.string.cb_count_tip), lightModel.getRepeatCount()));
            }
        });


    }


    private String getStrokeWidthTip(int strokeWidth) {
        //原始为2,4,6,8,10
        //改为 极小10，小14，标准16，大18，特大22
        int dp1 = UIUtil.dp2Px(1);
        List<Integer> fontSizArray = Arrays.asList(dp1, dp1 * 2, dp1 * 3, dp1 * 4, dp1 * 8);
        List<String> fontTipArray = Arrays.asList(getString(R.string.cb_font_size_10), getString(R.string.cb_font_size_14),
                getString(R.string.cb_font_size_16), getString(R.string.cb_font_size_18), getString(R.string.cb_font_size_22));
        int i = fontSizArray.indexOf(strokeWidth);
        if (i != -1) {
            return fontTipArray.get(i);
        } else {
            return getString(R.string.cb_font_size_16);
        }
    }

    private String getSpeedTip(int type) {
        List<String> fontTipArray = Arrays.asList(getString(R.string.light_behavior_speed_type_slow), getString(R.string.light_behavior_speed_type_normal),
                getString(R.string.light_behavior_speed_type_fast));
        return fontTipArray.get(type);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onResume() {
        super.onResume();
        showLight();
    }

    private void showLight() {
        binding.lightView.setMock(true);
        binding.lightView.setAutoAnim(true);
        binding.lightView.bindData(lightModel);
    }

    @Override
    public BaseFragment getFragment() {
        return this;
    }

    @Override
    public void onInitData(Object object) {
        if (object instanceof LightModel) {
            lightModel = (LightModel) object;
        }
        if (lightModel == null) {
            lightModel = LightModel.getDefault();
        }
    }

    @Override
    public Object onResultData() {
        return lightModel;
    }
}
