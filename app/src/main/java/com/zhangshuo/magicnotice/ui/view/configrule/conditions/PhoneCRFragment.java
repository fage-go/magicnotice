package com.zhangshuo.magicnotice.ui.view.configrule.conditions;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.PermissionUtils;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.zhangshuo.common.base.BaseFragment;
import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.common.model.EventMessage;
import com.zhangshuo.common.toast.ToastManager;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.config.EventCodes;
import com.zhangshuo.magicnotice.databinding.FragmentCrPhoneBinding;
import com.zhangshuo.magicnotice.ui.adapter.PhoneSelectAdapter;
import com.zhangshuo.magicnotice.ui.model.PhoneInfo;
import com.zhangshuo.magicnotice.ui.model.conditions.PhoneConditionsModel;
import com.zhangshuo.magicnotice.ui.view.configrule.IConfigRule;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class PhoneCRFragment extends BaseFragment<FragmentCrPhoneBinding>
        implements IConfigRule {

    private PhoneConditionsModel phoneConditionsModel;
    private PhoneSelectAdapter adapter;


    @Override
    protected void initView() {
        binding.tvAddNew.getPaint().setFakeBoldText(true);
        binding.rvPhone.setLayoutManager(new FlexboxLayoutManager(getContext(), FlexDirection.ROW, FlexWrap.WRAP));
        adapter = new PhoneSelectAdapter(getContext());
        binding.rvPhone.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseRAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                adapter.removeItem(position);
            }
        });
        binding.tvAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean granted = PermissionUtils.isGranted(Manifest.permission.READ_CONTACTS,
                        Manifest.permission.READ_CALL_LOG,
                        Manifest.permission.READ_PHONE_STATE);
                if (granted) {
                    gotoSelectContact();
                } else {
                    PermissionUtils.permission(Manifest.permission.READ_CONTACTS,
                            Manifest.permission.READ_CALL_LOG,
                            Manifest.permission.READ_PHONE_STATE)
                            .callback(new PermissionUtils.SingleCallback() {
                                @Override
                                public void callback(boolean isAllGranted, @NonNull List<String> granted, @NonNull List<String> deniedForever, @NonNull List<String> denied) {
                                    if (isAllGranted) {
                                        gotoSelectContact();
                                    } else {
                                        ToastManager.get().noticeShortMessage(getString(R.string.phone_cr_permission_granted));
                                    }
                                }
                            }).request();
                }
            }
        });
    }

    @Override
    protected void initData() {
        binding.filterConfig.setConfig(phoneConditionsModel.isFilterType(),
                R.string.c_filter_tip_phone_y, R.string.c_filter_tip_phone_n);
        adapter.addData(phoneConditionsModel.getDataList(), true);
    }

    @Override
    public BaseFragment getFragment() {
        return this;
    }

    @Override
    public void onInitData(Object object) {
        if (object instanceof PhoneConditionsModel) {
            phoneConditionsModel = (PhoneConditionsModel) object;
        }
        if (phoneConditionsModel == null) {
            phoneConditionsModel = new PhoneConditionsModel();
        }
    }

    @Override
    public Object onResultData() {
        phoneConditionsModel.setFilterType(binding.filterConfig.isChecked());
        List<PhoneInfo> dataList = adapter.getDataList();
        phoneConditionsModel.setDataList(dataList);
        return phoneConditionsModel;
    }

    private void readContact(Uri contactUri) {
        try {
            Cursor cursor = getContext().getContentResolver().query(contactUri, null,
                    null, null, null);
            //Key联系人姓名,Value联系人手机号
            List<PhoneInfo> phoneInfoList = getContactPhone(cursor);
            adapter.addData(phoneInfoList);
        } catch (Exception e) {

        }
    }

    /**
     * EventBus事件接收
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(EventMessage eventMessage) {
        if (eventMessage.getCode() == EventCodes.PHONE_SELECTED_RESULT) {
            Uri contactUri = (Uri) eventMessage.getData();
            readContact(contactUri);
        }
    }

    private void gotoSelectContact() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        ((Activity) getContext()).startActivityForResult(intent, 6678);
    }


    /**
     * 获取联系人姓名及手机号
     *
     * @param cursor
     * @return Key为联系人姓名, Value为联系人手机号
     */
    private List<PhoneInfo> getContactPhone(Cursor cursor) {
        List<PhoneInfo> phoneInfoList = new ArrayList<>();
        String phoneName = "";// 姓名
        String mobilePhoneNo = "";// 手机号
        if (null != cursor) {
            cursor.moveToFirst();
            // 获得联系人的ID号
            int idFieldIndex = cursor
                    .getColumnIndex(ContactsContract.Contacts._ID);
            String contactId = cursor.getString(idFieldIndex);
            // 联系人姓名
            int idPhoneNameIndex = cursor
                    .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            phoneName = cursor.getString(idPhoneNameIndex);
            // 获得联系人的电话号码的cursor;
            Cursor allPhones = getContext().getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                    new String[]{contactId}, null);
            // 所以联系电话（包话电话和手机号）
            if (allPhones.moveToFirst()) {
                // 遍历所有的电话号码
                for (; !allPhones.isAfterLast(); allPhones.moveToNext()) {
                    int telNoTypeIndex = allPhones
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
                    int telNoType = allPhones.getInt(telNoTypeIndex);
                    int telNoIndex = allPhones
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    String telNo = allPhones.getString(telNoIndex);
                    if (!TextUtils.isEmpty(telNo))
                        mobilePhoneNo = mobilePhoneNo + telNo + ";";
                }
                if (!allPhones.isClosed()) {
                    allPhones.close();
                }
            }
            if (!cursor.isClosed()) {
                cursor.close();
            }
            if (!TextUtils.isEmpty(mobilePhoneNo)) {
                mobilePhoneNo = mobilePhoneNo.substring(0, mobilePhoneNo.length() - 1);
            }
            phoneInfoList.add(new PhoneInfo(phoneName, mobilePhoneNo));
        }
        return phoneInfoList;
    }
}
