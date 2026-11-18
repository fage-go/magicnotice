package com.zhangshuo.magicnotice.util;

import android.util.Base64;

import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.BuildConfig;
import com.zhangshuo.magicnotice.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class DESUtil {

    private static final String KEY = decodeBuildSetting(BuildConfig.LEGACY_EXPORT_KEY_B64);
    private static final String IV = decodeBuildSetting(BuildConfig.LEGACY_EXPORT_IV_B64);

    private static String decodeBuildSetting(String value) {
        return new String(Base64.decode(value, Base64.DEFAULT), StandardCharsets.UTF_8);
    }

    public static boolean isLegacyExportConfigured() {
        return KEY.getBytes(StandardCharsets.UTF_8).length >= 8
                && IV.getBytes(StandardCharsets.UTF_8).length == 8;
    }

    public static String requireLegacyKey() {
        if (!isLegacyExportConfigured()) {
            throw new IllegalStateException(StringUtil.getString(R.string.legacy_rule_exchange_unavailable));
        }
        return KEY;
    }

    /**
     * 偏移变量，固定占8位字节
     */
    private String IV_PARAMETER = IV;
    /**
     * 密钥算法
     */
    private String ALGORITHM = "DES";
    /**
     * 加密/解密算法-工作模式-填充模式
     */
    private String CIPHER_ALGORITHM = "DES/CBC/PKCS5Padding";

    public void setIV_PARAMETER(String IV_PARAMETER) {
        this.IV_PARAMETER = IV_PARAMETER;
    }

    /**
     * 生成key
     *
     * @param password
     * @return
     * @throws Exception
     */
    private Key generateKey(String password) throws Exception {
        DESKeySpec dks = new DESKeySpec(password.getBytes(StandardCharsets.UTF_8));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        return keyFactory.generateSecret(dks);
    }


    /**
     * DES加密字符串
     *
     * @param password 加密密码，长度不能够小于8位
     * @param data     待加密字符串
     * @return 加密后内容
     */
    public String encrypt(String password, String data) {
        if (password == null || password.length() < 8) {
            throw new RuntimeException(StringUtil.getString(R.string.des_failed));
        }
        if (data == null)
            return null;
        try {
            Key secretKey = generateKey(password);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            IvParameterSpec iv = new IvParameterSpec(IV_PARAMETER.getBytes(StandardCharsets.UTF_8));
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            byte[] bytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return new String(Base64.encode(bytes, Base64.DEFAULT));
        } catch (Exception e) {
            e.printStackTrace();
            return data;
        }
    }

    /**
     * DES解密字符串
     *
     * @param password 解密密码，长度不能够小于8位
     * @param data     待解密字符串
     * @return 解密后内容
     */
    public String decrypt(String password, String data) {
        if (password == null || password.length() < 8) {
            throw new RuntimeException(StringUtil.getString(R.string.des_failed));
        }
        if (data == null)
            return null;
        try {
            Key secretKey = generateKey(password);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            IvParameterSpec iv = new IvParameterSpec(IV_PARAMETER.getBytes(StandardCharsets.UTF_8));
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

            return new String(cipher.doFinal(Base64.decode(data.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return data;
        }
    }

    /**
     * DES加密文件
     *
     * @param srcFile  待加密的文件
     * @param destFile 加密后存放的文件路径
     * @return 加密后的文件路径
     */
    public String encryptFile(String password, String srcFile, String destFile) {

        if (password == null || password.length() < 8) {
            throw new RuntimeException(StringUtil.getString(R.string.des_failed));
        }
        try {
            IvParameterSpec iv = new IvParameterSpec(IV_PARAMETER.getBytes(StandardCharsets.UTF_8));
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, generateKey(password), iv);
            InputStream is = new FileInputStream(srcFile);
            OutputStream out = new FileOutputStream(destFile);
            CipherInputStream cis = new CipherInputStream(is, cipher);
            byte[] buffer = new byte[1024];
            int r;
            while ((r = cis.read(buffer)) > 0) {
                out.write(buffer, 0, r);
            }
            cis.close();
            is.close();
            out.close();
            return destFile;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * DES解密文件
     *
     * @param srcFile  已加密的文件
     * @param destFile 解密后存放的文件路径
     * @return 解密后的文件路径
     */
    public String decryptFile(String password, String srcFile, String destFile) {
        if (password == null || password.length() < 8) {
            throw new RuntimeException(StringUtil.getString(R.string.des_failed));
        }
        try {
            File file = new File(destFile);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            IvParameterSpec iv = new IvParameterSpec(IV_PARAMETER.getBytes(StandardCharsets.UTF_8));
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, generateKey(password), iv);
            InputStream is = new FileInputStream(srcFile);
            OutputStream out = new FileOutputStream(destFile);
            CipherOutputStream cos = new CipherOutputStream(out, cipher);
            byte[] buffer = new byte[1024];
            int r;
            while ((r = is.read(buffer)) >= 0) {
                cos.write(buffer, 0, r);
            }
            cos.close();
            is.close();
            out.close();
            return destFile;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}