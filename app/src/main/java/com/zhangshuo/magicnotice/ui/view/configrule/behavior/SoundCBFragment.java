package com.zhangshuo.magicnotice.ui.view.configrule.behavior;

import android.media.Ringtone;
import android.text.TextUtils;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;

import com.zhangshuo.common.base.BaseFragment;
import com.zhangshuo.common.toast.ToastManager;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.config.CacheMemory;
import com.zhangshuo.magicnotice.databinding.FragmentCbSoundBinding;
import com.zhangshuo.magicnotice.ui.adapter.TxtSelectBehaviorAdapter;
import com.zhangshuo.magicnotice.ui.model.TxtSelectedInfo;
import com.zhangshuo.magicnotice.ui.model.behavior.SoundBehaviorModel;
import com.zhangshuo.magicnotice.ui.view.configrule.IConfigRule;
import com.zhangshuo.magicnotice.util.RingtoneHelper;

import java.util.ArrayList;
import java.util.List;

public class SoundCBFragment extends BaseFragment<FragmentCbSoundBinding>
        implements IConfigRule {

    private SoundBehaviorModel soundBehaviorModel;
    private TxtSelectBehaviorAdapter adapter;
    private Ringtone lastRingtone;

    @Override
    protected void initView() {
        binding.rvSound.setLayoutManager(new GridLayoutManager(getContext(), 3));
        adapter = new TxtSelectBehaviorAdapter(getContext());
        binding.rvSound.setAdapter(adapter);
        adapter.setOnItemSelectCallback(new TxtSelectBehaviorAdapter.OnItemSelectCallback() {
            @Override
            public void onSelected(int position) {
                TxtSelectedInfo item = adapter.getItem(position);
                soundBehaviorModel.setRingtoneTitle(item.getName());
                soundBehaviorModel.setRingtoneUri(item.getValue());

                if (lastRingtone != null && lastRingtone.isPlaying()) {
                    lastRingtone.stop();
                }
                lastRingtone = RingtoneHelper.playRingtone(item.getValue());
            }
        });
    }

    @Override
    protected void initData() {
        getData();
    }

    @Override
    public BaseFragment getFragment() {
        return this;
    }

    @Override
    public void onInitData(Object object) {
        if (object instanceof SoundBehaviorModel) {
            soundBehaviorModel = (SoundBehaviorModel) object;
        }
        if (soundBehaviorModel == null) {
            soundBehaviorModel = new SoundBehaviorModel();
        }
    }

    @Override
    public Object onResultData() {
        return soundBehaviorModel;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (lastRingtone != null && lastRingtone.isPlaying()) {
            lastRingtone.stop();
        }
    }

    private void getData() {
        List<TxtSelectedInfo> soundCacheList = (List<TxtSelectedInfo>) CacheMemory.cacheData.get("Sound_Cache");
        if (soundCacheList == null || soundCacheList.size() == 0) {
            binding.pbLoading.setVisibility(View.VISIBLE);
            RingtoneHelper.getRingtoneInfoList(getContext(), new AsyncCallback<List<RingtoneHelper.RingtoneInfo>>() {
                @Override
                public void onSuccess(List<RingtoneHelper.RingtoneInfo> result) {
                    binding.pbLoading.setVisibility(View.GONE);
                    if (result == null) {
                        ToastManager.get().noticeShortMessage(getContext().getString(R.string.sound_behavior_error));
                        return;
                    }
                    List<TxtSelectedInfo> infoList = new ArrayList<>();
                    for (int i = 0; i < result.size(); i++) {
                        RingtoneHelper.RingtoneInfo ringtoneInfo = result.get(i);
                        String path = ringtoneInfo.getUri();
                        if (TextUtils.isEmpty(path)) continue;
                        boolean isSelected = path.equals(soundBehaviorModel.getRingtoneUri());
                        infoList.add(new TxtSelectedInfo(ringtoneInfo.getName(), path, isSelected));
                    }
                    CacheMemory.cacheData.put("Sound_Cache", infoList);
                    adapter.addData(infoList, true);
                }
            });
        } else {
            adapter.addData(soundCacheList, true);
        }
    }
}
