package com.zhangshuo.magicnotice.ui.view.configrule.behavior;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;

import com.zhangshuo.common.base.BaseFragment;
import com.zhangshuo.common.util.UIUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.databinding.FragmentCbBroadcastBinding;
import com.zhangshuo.magicnotice.tts.TTSHelper;
import com.zhangshuo.magicnotice.tts.TTSListener;
import com.zhangshuo.magicnotice.ui.dialog.SelectStepPopup;
import com.zhangshuo.magicnotice.ui.model.behavior.BroadcastBehaviorModel;
import com.zhangshuo.magicnotice.ui.view.configrule.IConfigRule;

import java.util.Arrays;

import razerdp.basepopup.BasePopupWindow;

public class BroadcastCBFragment extends BaseFragment<FragmentCbBroadcastBinding>
        implements IConfigRule {
    private BroadcastBehaviorModel broadcastBehaviorModel;
    private boolean isWaitBroad = false;

    @Override
    protected void initView() {
        TTSHelper.getInstance().initTextToSpeech(new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                binding.tvNoticeContent.setText(status == 0 ? getString(R.string.broadcast_cb_notice_tip)
                        : getString(R.string.broadcast_cb_notice_tip_no));
            }
        });
        binding.cbPlayNotice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                broadcastBehaviorModel.setNotice(isChecked);
            }
        });
        binding.cbPlayAuto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                broadcastBehaviorModel.setAutoPlay(isChecked);
            }
        });
        binding.llPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TTSHelper.getInstance().canTextToSpeech()) {
                    startSpeech();
                }
            }
        });
        //引擎设置
        binding.tvEngineValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent("com.android.settings.TTS_SETTINGS"));
            }
        });
        //应用
        binding.cbContentApp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int contentType = broadcastBehaviorModel.getContentType();
                contentType &= ~BroadcastBehaviorModel.CONTENT_TYPE_APP;
                if (isChecked) {
                    contentType = contentType | BroadcastBehaviorModel.CONTENT_TYPE_APP;
                }
                broadcastBehaviorModel.setContentType(contentType);
            }
        });
        binding.cbContentTitle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int contentType = broadcastBehaviorModel.getContentType();
                contentType &= ~BroadcastBehaviorModel.CONTENT_TYPE_TITLE;
                if (isChecked) {
                    contentType = contentType | BroadcastBehaviorModel.CONTENT_TYPE_TITLE;
                }
                broadcastBehaviorModel.setContentType(contentType);
            }
        });
        binding.cbContentDetail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int contentType = broadcastBehaviorModel.getContentType();
                contentType &= ~BroadcastBehaviorModel.CONTENT_TYPE_TXT;
                if (isChecked) {
                    contentType = contentType | BroadcastBehaviorModel.CONTENT_TYPE_TXT;
                }
                broadcastBehaviorModel.setContentType(contentType);
            }
        });
        //音量设置
        binding.crVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.crVolume.setSelect(true);
                new SelectStepPopup(getFragment(),
                        Arrays.asList(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), broadcastBehaviorModel.getVolume())
                        .setOnCallback(new SelectStepPopup.OnSelectCallback() {
                            @Override
                            public String onSelect(int value) {
                                broadcastBehaviorModel.setVolume(value);
                                showTipInfo();
                                return null;
                            }
                        }, new BasePopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                binding.crVolume.setSelect(false);
                            }
                        })
                        .setPopupFadeEnable(true)
                        .setOffsetX(UIUtil.dp2Px(10))
                        .setPopupGravityMode(BasePopupWindow.GravityMode.ALIGN_TO_ANCHOR_SIDE, BasePopupWindow.GravityMode.RELATIVE_TO_ANCHOR)
                        .setPopupGravity(Gravity.TOP | Gravity.LEFT)
                        .showPopupWindow(v);
            }
        });
        //音量设置
        binding.crSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.crSpeed.setSelect(true);
                new SelectStepPopup(getFragment(),
                        Arrays.asList(0, 1, 2, 3, 4, 5, 6), (int) ((broadcastBehaviorModel.getSpeed() - 0.5) * 2))
                        .setOnCallback(new SelectStepPopup.OnSelectCallback() {
                            @Override
                            public String onSelect(int value) {
                                broadcastBehaviorModel.setSpeed(0.5f + value / 2f);
                                showTipInfo();
                                return null;
                            }
                        }, new BasePopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                binding.crSpeed.setSelect(false);
                            }
                        })
                        .setPopupFadeEnable(true)
                        .setOffsetY(UIUtil.dp2Px(10))
                        .setPopupGravity(Gravity.TOP | Gravity.CENTER)
                        .showPopupWindow(v);

            }
        });
        //音调设置
        binding.crPitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.crPitch.setSelect(true);
                new SelectStepPopup(getFragment(),
                        Arrays.asList(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), broadcastBehaviorModel.getVoice())
                        .setOnCallback(new SelectStepPopup.OnSelectCallback() {
                            @Override
                            public String onSelect(int value) {
                                broadcastBehaviorModel.setVoice(value);
                                showTipInfo();
                                return null;
                            }
                        }, new BasePopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                binding.crPitch.setSelect(false);
                            }
                        })
                        .setPopupFadeEnable(true)
                        .setOffsetX(UIUtil.dp2Px(10))
                        .setPopupGravityMode(BasePopupWindow.GravityMode.ALIGN_TO_ANCHOR_SIDE, BasePopupWindow.GravityMode.RELATIVE_TO_ANCHOR)
                        .setPopupGravity(Gravity.TOP | Gravity.RIGHT)
                        .showPopupWindow(v);
            }
        });
    }

    @Override
    protected void initData() {
        binding.cbPlayAuto.setChecked(broadcastBehaviorModel.isNotice());
        showTipInfo();
    }

    @Override
    public void onResume() {
        super.onResume();
        showTipInfo();
    }

    private void showTipInfo() {
        String defaultEngine = TTSHelper.getInstance().getDefaultEngine();
        if (TextUtils.isEmpty(defaultEngine)) {
            //暂不支持引擎
            binding.tvEngineValue.setText(R.string.broadcast_cb_no_support);
        } else {
            binding.tvEngineValue.setText(defaultEngine);
        }
        if (broadcastBehaviorModel != null) {
            binding.cbPlayNotice.setChecked(broadcastBehaviorModel.isNotice());
            binding.cbPlayAuto.setChecked(broadcastBehaviorModel.isAutoPlay());
            binding.crVolume.setValue(broadcastBehaviorModel.getVolume() + "");
            binding.crSpeed.setValue(broadcastBehaviorModel.getSpeed() + "");
            binding.crPitch.setValue(broadcastBehaviorModel.getVoice() + "");
            binding.cbContentApp.setChecked(broadcastBehaviorModel.hasContentType(BroadcastBehaviorModel.CONTENT_TYPE_APP));
            binding.cbContentTitle.setChecked(broadcastBehaviorModel.hasContentType(BroadcastBehaviorModel.CONTENT_TYPE_TITLE));
            binding.cbContentDetail.setChecked(broadcastBehaviorModel.hasContentType(BroadcastBehaviorModel.CONTENT_TYPE_TXT));
            binding.etTxtPrefix.setText(broadcastBehaviorModel.getPrefix());
            binding.etTxtSuffix.setText(broadcastBehaviorModel.getSuffix());
        }
    }

    private void startSpeech() {
        if (isWaitBroad) return;
        isWaitBroad = true;
        binding.tvStatus.setText(R.string.broadcast_cb_sound_try);
        TTSHelper.getInstance().stopSpeech();

        TTSHelper.getInstance().startSpeech(getSpeechTxt(), broadcastBehaviorModel, new TTSListener() {
            @Override
            public void onDone(String id) {
                isWaitBroad = false;
                binding.tvStatus.setText(R.string.broadcast_cb_sound_end);
            }
        });
    }

    @Override
    public BaseFragment getFragment() {
        return this;
    }

    @Override
    public void onInitData(Object object) {
        if (object instanceof BroadcastBehaviorModel) {
            broadcastBehaviorModel = (BroadcastBehaviorModel) object;
        }
        if (broadcastBehaviorModel == null) {
            broadcastBehaviorModel = BroadcastBehaviorModel.getDefault();
        }
    }

    @Override
    public Object onResultData() {
        broadcastBehaviorModel.setLanguageName(binding.tvEngineValue.getText().toString());
        broadcastBehaviorModel.setPrefix(binding.etTxtPrefix.getText().toString());
        broadcastBehaviorModel.setSuffix(binding.etTxtSuffix.getText().toString());
        return broadcastBehaviorModel;
    }

    private String getSpeechTxt() {
        int contentType = broadcastBehaviorModel.getContentType();
        boolean hasApp = (contentType & BroadcastBehaviorModel.CONTENT_TYPE_APP) == BroadcastBehaviorModel.CONTENT_TYPE_APP;
        boolean hasTitle = (contentType & BroadcastBehaviorModel.CONTENT_TYPE_TITLE) == BroadcastBehaviorModel.CONTENT_TYPE_TITLE;
        boolean hasTxt = (contentType & BroadcastBehaviorModel.CONTENT_TYPE_TXT) == BroadcastBehaviorModel.CONTENT_TYPE_TXT;

        StringBuilder speechSB = new StringBuilder();

        if (!TextUtils.isEmpty(binding.etTxtPrefix.getText())) {
            speechSB.append(binding.etTxtPrefix.getText());
        }

        if (hasApp) {
            speechSB.append(getString(R.string.app_name) + ",");
        }
        if (hasTitle) {
            speechSB.append(binding.tvNoticeTitle.getText() + ",");
        }
        if (hasTxt) {
            speechSB.append(binding.tvNoticeContent.getText());
        }
        if (!TextUtils.isEmpty(binding.etTxtSuffix.getText())) {
            speechSB.append(binding.etTxtSuffix.getText());
        }
        return speechSB.toString();
    }
}
