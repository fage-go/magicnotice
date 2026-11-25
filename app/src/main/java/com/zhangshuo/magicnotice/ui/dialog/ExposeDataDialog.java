package com.zhangshuo.magicnotice.ui.dialog;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.zhangshuo.common.dialog.BaseBottomDialog;
import com.zhangshuo.common.model.EventMessage;
import com.zhangshuo.common.util.AsyncCallUtils;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.common.util.EventBusUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.backup.RuleBackup;
import com.zhangshuo.magicnotice.config.EventCodes;
import com.zhangshuo.magicnotice.databinding.DialogExposeDataBinding;
import com.zhangshuo.magicnotice.db.service.ConfigRuleDataService;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExposeDataDialog extends BaseBottomDialog<DialogExposeDataBinding> {
    private boolean busy;
    private byte[] pendingExport;
    private AlertDialog prompt;

    private static final int SAVE_DOCUMENT = 1811;
    private static final int OPEN_DOCUMENT = 1812;

    @Override
    public void initView() {
        binding.mbExposeRule.setOnClickListener(v -> selectRules());
        binding.mbInputData.setOnClickListener(v -> openRuleFile());
    }

    private void selectRules() {
        if (busy) return;
        SelectRuleDialog selector = new SelectRuleDialog(false);
        selector.setBackupSelection();
        selector.setOnCallback((ids, names) -> {
            if (!isAdded() || getView() == null) return;
            if (ids.isEmpty()) {
                notifyUser(R.string.rule_backup_choose_rules);
                return;
            }
            List<Integer> snapshot = new ArrayList<>(ids);
            prompt = new AlertDialog.Builder(requireContext())
                    .setTitle(R.string.expose_data_rule)
                    .setMessage(R.string.rule_backup_plaintext)
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> prepareExport(snapshot))
                    .show();
        });
        selector.show(requireContext());
    }

    private void prepareExport(List<Integer> ids) {
        if (!isAdded() || getView() == null) return;
        busy = true;
        AsyncCallUtils.asyncCall(() -> {
            try {
                return Outcome.success(ConfigRuleDataService.exportRuleBackup(ids));
            } catch (RuleBackup.Invalid error) {
                return Outcome.<byte[]>failure(resource(error.reason));
            } catch (RuntimeException error) {
                return Outcome.<byte[]>failure(R.string.rule_backup_export_failed);
            }
        }, new AsyncCallback<Outcome<byte[]>>() {
            @Override public void onSuccess(Outcome<byte[]> result) {
                if (!isAdded() || getView() == null) return;
                if (result.error != 0) {
                    busy = false;
                    notifyUser(result.error);
                    return;
                }
                pendingExport = result.value;
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT)
                        .addCategory(Intent.CATEGORY_OPENABLE)
                        .setType("application/json")
                        .putExtra(Intent.EXTRA_TITLE, "MagicNotice-" + System.currentTimeMillis()
                                + RuleBackup.EXTENSION);
                try {
                    startActivityForResult(intent, SAVE_DOCUMENT);
                } catch (RuntimeException error) {
                    busy = false;
                    pendingExport = null;
                    notifyUser(R.string.rule_backup_picker_failed);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SAVE_DOCUMENT) saveResult(selectedUri(resultCode, data));
        if (requestCode == OPEN_DOCUMENT) openResult(selectedUri(resultCode, data));
    }

    private void saveResult(Uri uri) {
        busy = false;
        byte[] bytes = pendingExport;
        pendingExport = null;
        if (uri == null || !isAdded() || getView() == null) return;
        if (bytes == null) {
            notifyUser(R.string.rule_backup_export_failed);
            return;
        }
        ContentResolver resolver = requireContext().getContentResolver();
        busy = true;
        AsyncCallUtils.asyncCall(() -> {
            try (OutputStream out = resolver.openOutputStream(uri, "wt")) {
                if (out == null) return false;
                out.write(bytes);
                return true;
            } catch (Exception error) {
                return false; // Do not log provider/file content or metadata.
            }
        }, new AsyncCallback<Boolean>() {
            @Override public void onSuccess(Boolean saved) {
                busy = false;
                notifyUser(saved ? R.string.rule_backup_export_done : R.string.rule_backup_export_failed);
            }
        });
    }

    private void openRuleFile() {
        if (busy) return;
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .setType("*/*"); // Older .cicada files can be selected to show the explicit explanation.
        busy = true;
        try {
            startActivityForResult(intent, OPEN_DOCUMENT);
        } catch (RuntimeException error) {
            busy = false;
            notifyUser(R.string.rule_backup_picker_failed);
        }
    }

    private void openResult(Uri uri) {
        busy = false;
        if (uri == null || !isAdded() || getView() == null) return;
        ContentResolver resolver = requireContext().getContentResolver();
        busy = true;
        AsyncCallUtils.asyncCall(() -> {
            if (oldFilename(resolver, uri)) return Outcome.<List<RuleBackup.Rule>>failure(
                    R.string.rule_backup_old_file);
            try (InputStream in = resolver.openInputStream(uri)) {
                if (in == null) return Outcome.<List<RuleBackup.Rule>>failure(R.string.rule_backup_invalid);
                return Outcome.success(RuleBackup.read(in));
            } catch (RuleBackup.Invalid error) {
                return Outcome.<List<RuleBackup.Rule>>failure(resource(error.reason));
            } catch (Exception error) {
                return Outcome.<List<RuleBackup.Rule>>failure(R.string.rule_backup_invalid);
            }
        }, new AsyncCallback<Outcome<List<RuleBackup.Rule>>>() {
            @Override public void onSuccess(Outcome<List<RuleBackup.Rule>> parsed) {
                busy = false;
                if (!isAdded() || getView() == null) return;
                if (parsed.error == 0) confirmImport(parsed.value);
                else notifyUser(parsed.error);
            }
        });
    }

    private static boolean oldFilename(ContentResolver resolver, Uri uri) {
        try (Cursor cursor = resolver.query(uri, new String[]{OpenableColumns.DISPLAY_NAME},
                null, null, null)) {
            if (cursor == null || !cursor.moveToFirst()) return false;
            String name = cursor.getString(0);
            return name != null && name.toLowerCase(Locale.ROOT).endsWith(".cicada");
        } catch (RuntimeException error) {
            return false; // Some providers have no display name; the legacy file marker is checked too.
        }
    }

    private void confirmImport(List<RuleBackup.Rule> rules) {
        prompt = new AlertDialog.Builder(requireContext())
                .setTitle(R.string.import_rule_config)
                .setMessage(getString(R.string.rule_backup_confirm, rules.size()))
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> importRules(rules))
                .show();
    }

    private void importRules(List<RuleBackup.Rule> rules) {
        if (!isAdded() || busy) return;
        busy = true;
        AsyncCallUtils.asyncCall(() -> {
            try {
                ConfigRuleDataService.importRuleBackup(rules);
                return true;
            } catch (RuntimeException error) {
                return false;
            }
        }, new AsyncCallback<Boolean>() {
            @Override public void onSuccess(Boolean inserted) {
                busy = false;
                if (inserted) EventBusUtil.sendEvent(new EventMessage<>(EventCodes.UPDATE_RULE));
                notifyUser(inserted ? R.string.rule_backup_import_done : R.string.expose_data_import_failed);
            }
        });
    }

    private static Uri selectedUri(int resultCode, Intent intent) {
        if (resultCode != Activity.RESULT_OK || intent == null) return null;
        Uri uri = intent.getData();
        return uri != null && "content".equals(uri.getScheme()) ? uri : null;
    }

    private static int resource(RuleBackup.Failure reason) {
        switch (reason) {
            case LEGACY: return R.string.rule_backup_old_file;
            case UNSUPPORTED: return R.string.rule_backup_unsupported;
            case TOO_LARGE: return R.string.rule_backup_too_large;
            default: return R.string.rule_backup_invalid;
        }
    }

    private void notifyUser(int message) {
        Context context = getContext();
        if (context != null) Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    @Override public void onDestroyView() {
        if (prompt != null) {
            prompt.dismiss();
            prompt = null;
        }
        super.onDestroyView();
    }

    private static final class Outcome<T> {
        final T value;
        final int error;
        private Outcome(T value, int error) { this.value = value; this.error = error; }
        static <T> Outcome<T> success(T value) { return new Outcome<>(value, 0); }
        static <T> Outcome<T> failure(int error) { return new Outcome<>(null, error); }
    }
}
