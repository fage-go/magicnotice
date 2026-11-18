package com.zhangshuo.magicnotice.ui.dialog;

import android.content.DialogInterface;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.view.View;

import com.zhangshuo.common.dialog.BaseTipsDialog;
import com.zhangshuo.common.toast.ToastManager;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.databinding.DialogFingerPrintBinding;

import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class FingerPrintDialog extends BaseTipsDialog<DialogFingerPrintBinding> {
    private static final String DEFAULT_KEY_NAME = "default_key";

    private FingerprintManager fingerprintManager;
    private CancellationSignal mCancellationSignal;
    private OnFingerAuthCallback onFingerAuthCallback;
    private KeyStore keyStore;

    private boolean isAuthSuccess = false;

    public void setInfo(OnFingerAuthCallback onFingerAuthCallback) {
        this.onFingerAuthCallback = onFingerAuthCallback;
    }

    @Override
    public void initView() {
        fingerprintManager = getContext().getSystemService(FingerprintManager.class);
        binding.tvFigureCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
        init();
        setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (onFingerAuthCallback != null) {
                    if (isAuthSuccess) {
                        onFingerAuthCallback.onSuccess();
                    } else {
                        onFingerAuthCallback.onFailed();
                    }
                }
            }
        });
    }

    @Override
    public void close() {
        stopListening();
        super.close();
    }

    @Override
    protected boolean backgroundDimEnabled() {
        return false;
    }


    @Override
    protected boolean canCancel() {
        return false;
    }

    @Override
    protected boolean canDismissOutSide() {
        return false;
    }

    @Override
    protected int getDialogWidth() {
        return -1;
    }

    @Override
    protected int getDialogHeight() {
        return -1;
    }

    @Override
    protected int getDialogAnimation() {
        return R.style.FingerPrint_DialogStyle;
    }

    public void init() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(DEFAULT_KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);
            keyGenerator.init(builder.build());
            keyGenerator.generateKey();
            SecretKey key = (SecretKey) keyStore.getKey(DEFAULT_KEY_NAME, null);
            Cipher cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            startListening(cipher);
        } catch (Exception e) {
            //指纹获取异常
            close();
        }
    }

    private void startListening(Cipher cipher) {
        mCancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(new FingerprintManager.CryptoObject(cipher), mCancellationSignal, 0, new FingerprintManager.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                isAuthSuccess = false;
                binding.tvFigureError.setText(errString);
                if (errorCode == FingerprintManager.FINGERPRINT_ERROR_LOCKOUT) {
                    ToastManager.get().showShortToast(errString.toString());
                    dismiss();
                }
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                binding.tvFigureError.setText(helpString);
            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                ToastManager.get().showShortToast(R.string.finger_print_suc);
                isAuthSuccess = true;
                close();
            }

            @Override
            public void onAuthenticationFailed() {
                binding.tvFigureError.setText(R.string.finger_print_fail);
                isAuthSuccess = false;
            }
        }, null);
    }

    private void stopListening() {
        if (mCancellationSignal != null) {
            mCancellationSignal.cancel();
            mCancellationSignal = null;
        }
    }

    public interface OnFingerAuthCallback {
        void onSuccess();

        void onFailed();
    }
}
