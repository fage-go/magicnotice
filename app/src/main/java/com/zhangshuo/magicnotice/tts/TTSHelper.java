package com.zhangshuo.magicnotice.tts;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import androidx.annotation.Nullable;

import com.zhangshuo.common.AppUtil;
import com.zhangshuo.common.util.LogUtils;
import com.zhangshuo.common.util.MainHandlerUtil;
import com.zhangshuo.magicnotice.db.model.NotificationData;
import com.zhangshuo.magicnotice.ui.model.behavior.BroadcastBehaviorModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class TTSHelper {
    private TTSService.TTSBinder ttsBinder;
    private volatile boolean canTextToSpeech = false;
    private boolean initializingTextToSpeech = false;
    private boolean bindingTTSService = false;
    private final List<TTSInfo> ttsInfoList = new ArrayList<>();
    private final List<TextToSpeech.OnInitListener> pendingInitListeners = new ArrayList<>();
    private final Map<String, TTSListener> ttsListeners = new HashMap<>();
    private TextToSpeech textToSpeech;

    public synchronized void addTTSInfo(NotificationData notificationData, BroadcastBehaviorModel model) {
        TTSInfo ttsInfo = new TTSInfo(notificationData, model);
        ttsInfoList.add(ttsInfo);
        LogUtils.i("TTS addTTS " + ttsInfoList.size());
        initTextToSpeech(null);
        checkTTSService();
    }

    private synchronized void checkTTSService() {
        if (ttsBinder == null) {
            if (bindingTTSService) return;
            bindingTTSService = true;
            Intent serviceIntent = new Intent(AppUtil.get().getApplication(), TTSService.class);
            try {
                AppUtil.get().getApplication().bindService(serviceIntent, new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName name, IBinder service) {
                        synchronized (TTSHelper.this) {
                            bindingTTSService = false;
                            ttsBinder = (TTSService.TTSBinder) service;
                        }
                        LogUtils.i("TTS onServiceConnected");
                        notifyTTSService();
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName name) {
                        synchronized (TTSHelper.this) {
                            bindingTTSService = false;
                            ttsBinder = null;
                        }
                        LogUtils.i("TTS onServiceDisconnected");
                    }
                }, Context.BIND_AUTO_CREATE);
            } catch (Exception e) {
                bindingTTSService = false;
                LogUtils.e("TTS bind service failed: " + e.getMessage());
            }
        } else {
            notifyTTSService();
        }
    }

    public void initTextToSpeech(@Nullable TextToSpeech.OnInitListener onInitListener) {
        synchronized (this) {
            if (canTextToSpeech()) {
                dispatchInitListener(onInitListener, TextToSpeech.SUCCESS);
                return;
            }
            if (onInitListener != null) {
                pendingInitListeners.add(onInitListener);
            }
            if (initializingTextToSpeech) return;

            initializingTextToSpeech = true;
            if (textToSpeech != null) {
                try {
                    textToSpeech.shutdown();
                } catch (Exception ignored) {
                }
                textToSpeech = null;
            }
            try {
                textToSpeech = new TextToSpeech(AppUtil.get().getApplication(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        handleTextToSpeechInit(status);
                    }
                });
                textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(final String id) {
                        LogUtils.i("onStart>>" + id);
                        final TTSListener listener = getSpeechListener(id);
                        MainHandlerUtil.get().post(new Runnable() {
                            @Override
                            public void run() {
                                if (listener != null) {
                                    listener.onStart(id);
                                }
                            }
                        });
                    }

                    @Override
                    public void onDone(final String id) {
                        LogUtils.i("onDone>>" + id);
                        final TTSListener listener = removeSpeechListener(id);
                        MainHandlerUtil.get().post(new Runnable() {
                            @Override
                            public void run() {
                                if (listener != null) {
                                    listener.onDone(id);
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(final String id) {
                        LogUtils.i("onError>>" + id);
                        final TTSListener listener = removeSpeechListener(id);
                        MainHandlerUtil.get().post(new Runnable() {
                            @Override
                            public void run() {
                                if (listener != null) {
                                    listener.onError(id);
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(final String id, int errorCode) {
                        LogUtils.i("onError>>" + id + ">>" + errorCode);
                        onError(id);
                    }
                });
            } catch (Exception e) {
                LogUtils.e("TTS init failed: " + e.getMessage());
                handleTextToSpeechInit(TextToSpeech.ERROR);
            }
        }
    }

    @Nullable
    private synchronized TTSListener getSpeechListener(String id) {
        return ttsListeners.get(id);
    }

    @Nullable
    private synchronized TTSListener removeSpeechListener(String id) {
        return ttsListeners.remove(id);
    }

    private synchronized void clearSpeechListeners() {
        ttsListeners.clear();
    }

    private void handleTextToSpeechInit(int status) {
        List<TextToSpeech.OnInitListener> initListeners;
        synchronized (this) {
            initializingTextToSpeech = false;
            canTextToSpeech = status == TextToSpeech.SUCCESS;
            initListeners = new ArrayList<>(pendingInitListeners);
            pendingInitListeners.clear();
        }
        LogUtils.i(">>>>onInit >> " + status);
        for (TextToSpeech.OnInitListener listener : initListeners) {
            dispatchInitListener(listener, status);
        }
        if (canTextToSpeech) {
            notifyTTSService();
        }
    }

    private void dispatchInitListener(@Nullable final TextToSpeech.OnInitListener listener, final int status) {
        if (listener == null) return;
        MainHandlerUtil.get().post(new Runnable() {
            @Override
            public void run() {
                listener.onInit(status);
            }
        });
    }

    private void notifyTTSService() {
        final TTSService.TTSBinder binder;
        synchronized (this) {
            binder = ttsBinder;
        }
        if (binder == null || !canTextToSpeech()) return;
        MainHandlerUtil.get().post(new Runnable() {
            @Override
            public void run() {
                TTSService service = binder.getService();
                if (service == null) return;
                if (service.isNotify()) {
                    service.updateNextTip();
                } else {
                    service.updateNotificationShow(TTSService.BROADCAST_KEY_START);
                }
            }
        });
    }

    public TTSService.TTSBinder getTTSBinder() {
        return ttsBinder;
    }

    @Nullable
    public synchronized TTSInfo getTopTTS() {
        if (ttsInfoList.size() == 0) return null;
        return ttsInfoList.get(0);
    }

    public synchronized void remoteTopTTS() {
        if (ttsInfoList.size() == 0) return;
        ttsInfoList.remove(0);
    }

    public synchronized int getTTSSize() {
        return ttsInfoList.size();
    }


    public boolean startSpeech(String text, BroadcastBehaviorModel model, TTSListener ttsListener) {
        if (!canTextToSpeech()) {
            initTextToSpeech(null);
            return false;
        }
        //构建
        setTextSpeechConfig(model);
        String utteranceId = String.valueOf(System.nanoTime());
        synchronized (this) {
            ttsListeners.put(utteranceId, ttsListener);
        }
        HashMap<String, String> ttsOptions = new HashMap<>();
        ttsOptions.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,
                utteranceId);
        ttsOptions.put(TextToSpeech.Engine.KEY_PARAM_VOLUME, String.valueOf(model.getVolume() / 100f));//音量
        ttsOptions.put(TextToSpeech.Engine.KEY_PARAM_STREAM,
                String.valueOf(AudioManager.STREAM_MUSIC));//播放类型
        try {
            int ret = textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, ttsOptions);
            LogUtils.i("tts>>>" + ret);
            if (ret != TextToSpeech.SUCCESS) {
                removeSpeechListener(utteranceId);
            }
            return ret == TextToSpeech.SUCCESS;
        } catch (Exception e) {
            removeSpeechListener(utteranceId);
            LogUtils.e("TTS speak failed: " + e.getMessage());
            return false;
        }
    }

    private void setTextSpeechConfig(BroadcastBehaviorModel model) {
        if (!canTextToSpeech) return;
        LogUtils.i("tts>>>" + model.getSpeed() + ">>" + model.getVoice());
        // Use the media speech route instead of the notification stream, which some devices mute via AppOps.
        textToSpeech.setAudioAttributes(new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build());
        textToSpeech.setLanguage(Locale.CHINESE);//设置语言
        textToSpeech.setSpeechRate(model.getSpeed());//设置语速
        textToSpeech.setPitch(model.getVoice() / 100f);//设置音调
    }

    public boolean canTextToSpeech() {
        return textToSpeech != null && canTextToSpeech;
    }

    public String getDefaultEngine() {
        if (canTextToSpeech()) {
            return textToSpeech.getDefaultEngine();
        } else {
            return "";
        }
    }

    public void stopSpeech() {
        if (!canTextToSpeech()) return;
        try {
            textToSpeech.stop();
        } catch (Exception e) {
            LogUtils.e("TTS stop failed: " + e.getMessage());
        } finally {
            clearSpeechListeners();
        }
    }


    public static TTSHelper getInstance() {
        return Holder.instance;
    }

    private TTSHelper() {
    }

    private static final class Holder {
        private static final TTSHelper instance = new TTSHelper();
    }
}
