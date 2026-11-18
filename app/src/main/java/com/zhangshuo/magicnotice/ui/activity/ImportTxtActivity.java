package com.zhangshuo.magicnotice.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zhangshuo.common.base.BaseActivity;
import com.zhangshuo.common.model.EventMessage;
import com.zhangshuo.common.util.AsyncCallUtils;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.common.util.EventBusUtil;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.config.EventCodes;
import com.zhangshuo.magicnotice.config.RoutePaths;
import com.zhangshuo.magicnotice.databinding.ActivityImportTxtBinding;
import com.zhangshuo.magicnotice.db.service.TxtDataService;
import com.zhangshuo.magicnotice.ui.adapter.RandomTxtAdapter;
import com.zhangshuo.magicnotice.ui.model.RandomTxtInfo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@Route(path = RoutePaths.APP_ACTIVITY_IMPORT_TXT)
public class ImportTxtActivity extends BaseActivity<ActivityImportTxtBinding> {

    @Autowired
    int classifyId = 9;

    private RandomTxtAdapter adapter;

    @Override
    protected void initView() {
        setTitleBar(getString(R.string.title_import_txt));
        adapter = new RandomTxtAdapter(this);
        adapter.setOnCallback(new RandomTxtAdapter.OnCallback() {
            @Override
            public void onDelete(int hasCode) {
                for (int i = 0; i < adapter.getDataList().size(); i++) {
                    RandomTxtInfo txtInfo = adapter.getDataList().get(i);
                    if (txtInfo.hashCode() == hasCode) {
                        adapter.removeItem(i);
                        break;
                    }
                }
                binding.mbNext.setText(getString(R.string.random_txt_import) + "(" + adapter.getItemCount() + ")");
            }
        });
        binding.rvImport.setAdapter(adapter);
        binding.rvImport.setLayoutManager(new LinearLayoutManager(this));
        binding.mbNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<RandomTxtInfo> dataList = adapter.getDataList();
                if (dataList != null && dataList.size() > 0) {
                    for (int i = 0; i < dataList.size(); i++) {
                        dataList.get(i).setClassifyId(classifyId);
                    }
                    showLoading();
                    TxtDataService.insertAll(dataList, new AsyncCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean result) {
                            hideLoading();
                            EventBusUtil.sendEvent(new EventMessage<>(EventCodes.IMPORT_RANDOM_TXT));
                            finishAct();
                        }
                    });
                } else {
                    finishAct();
                }
            }
        });
    }

    @Override
    protected void initData() {
        goSelectTxt();
    }

    private void goSelectTxt() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/plain");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            readContentAsync(data.getData());
        } else {
            finishAct();
        }
    }

    private void readContentAsync(Uri inUri) {
        AsyncCallUtils.asyncCall(new Callable<List<RandomTxtInfo>>() {
            @Override
            public List<RandomTxtInfo> call() throws Exception {
                try {
                    List<RandomTxtInfo> dataList = new ArrayList<>();
                    InputStream inputStream = getContentResolver().openInputStream(inUri);
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String str = "";
                    while ((str = bufferedReader.readLine()) != null) {
                        if (!TextUtils.isEmpty(str)) {
                            String lineData = str.replace("\n", "");
                            String title = StringUtil.getString(R.string.app_name);
                            String content = lineData;
                            String split = "/";
                            if (lineData.contains(split)) {
                                title = lineData.substring(0, lineData.indexOf(split));
                                content = lineData.substring(lineData.indexOf(split) + split.length());
                            }
                            dataList.add(new RandomTxtInfo(title, content));
                        }
                    }
                    return dataList;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }, new AsyncCallback<List<RandomTxtInfo>>() {
            @Override
            public void onSuccess(List<RandomTxtInfo> resultList) {
                if (resultList != null) {
                    adapter.addData(resultList, true);
                    binding.mbNext.setText(getString(R.string.random_txt_import) + "(" + resultList.size() + ")");
                }
            }
        });
    }
}
