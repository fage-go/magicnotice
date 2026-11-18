package com.zhangshuo.magicnotice.ui.view.web;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.core.content.FileProvider;

import com.zhangshuo.common.dialog.BottomOptionDialog;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;


public class MWebVIew extends WebView {
    private SmoothProgressBar smoothProgressBar;
    private PowerCallback powerCallback;

    public static final int INPUT_FILE_REQUEST_CODE = 1;
    private static final int REQUEST_CODE_ALBUM = 3;
    private ValueCallback<Uri[]> mFilePathCallback;
    private ValueCallback<Uri> nFilePathCallback;
    private String mCameraPhotoPath;
    private Uri photoURI;


    public void bindProgress(SmoothProgressBar progressBar) {
        this.smoothProgressBar = progressBar;
    }

    public void setPowerCallback(PowerCallback powerCallback) {
        this.powerCallback = powerCallback;
    }

    private void initWebViewConfig() {
        WebSettings webSetting = this.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(false);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSetting.setCacheMode(android.webkit.WebSettings.LOAD_DEFAULT);
        String ua = webSetting.getUserAgentString();
//        //开启Application Caches功能
        webSetting.setAppCacheEnabled(true);
    }


    private void init() {
        setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (smoothProgressBar != null) {
                    smoothProgressBar.smoothScrollTo(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (powerCallback != null) {
                    powerCallback.onReceivedTitle(title);
                }
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = filePathCallback;
                openSelectImageFile(fileChooserParams);
                return true;
            }
        });
        setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                super.shouldOverrideUrlLoading(view, url);
                if (url == null) return false;
                try {
                    if (url.startsWith("weixin://")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        view.getContext().startActivity(intent);
                        return true;
                    }
                } catch (Exception e) {
                    return false;
                }
                view.loadUrl(url);
                return true;
            }
        });
        initWebViewConfig();
    }


    private void openSelectImageFile(WebChromeClient.FileChooserParams fileChooserParams) {
        BottomOptionDialog.create(getContext(), Arrays.asList(
                        StringUtil.getString(R.string.webview_action_take),
                        StringUtil.getString(R.string.webview_action_photo)),
                new BottomOptionDialog.OnOptionCallback() {

                    @Override
                    public void onClick(int position) {
                        if (position == 0) {
                            takeCamera(fileChooserParams);
                        } else if (position == 1) {
                            selectAlbum();
                        }
                    }

                    @Override
                    public void onCancel() {
                        if (mFilePathCallback != null) {
                            mFilePathCallback.onReceiveValue(null);
                        }
                        mFilePathCallback = null;
                    }
                });
    }

    private void takeCamera(WebChromeClient.FileChooserParams fileChooserParams) {
        String[] acceptTypes = fileChooserParams.getAcceptTypes();
        if (acceptTypes[0].equals("image/*")) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", 0); // 调用后置摄像头
            if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                    takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                } catch (IOException ex) {
                    Log.e("TAG", "Unable to create Image File", ex);
                }
                //适配7.0
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    if (photoFile != null) {
                        photoURI = FileProvider.getUriForFile(getContext(),
                                "com.zhangshuo.common.fileprovider", photoFile);
                        takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    }
                } else {
                    if (photoFile != null) {
                        mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                        Log.d("nimei", "mCameraPhotoPath：" + mCameraPhotoPath);
                    } else {
                        takePictureIntent = null;
                    }
                }
            }
            ((Activity) getContext()).startActivityForResult(takePictureIntent, INPUT_FILE_REQUEST_CODE);
        }
    }

    private void selectAlbum() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        ((Activity) getContext()).startActivityForResult(Intent.createChooser(i, "Image Chooser"), REQUEST_CODE_ALBUM);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* 前缀 */
                ".jpg",         /* 后缀 */
                storageDir      /* 文件夹 */
        );
        mCameraPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri[] results = null;
        Uri mUri = null;
        if (resultCode == Activity.RESULT_OK && requestCode == INPUT_FILE_REQUEST_CODE) {
            if (data == null) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    mUri = photoURI;
                    results = new Uri[]{mUri};
                } else {
                    if (mCameraPhotoPath != null) {
                        mUri = Uri.parse(mCameraPhotoPath);
                        results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                    }
                }
            } else {
                Uri nUri = data.getData();
                if (nUri != null) {
                    mUri = nUri;
                    results = new Uri[]{nUri};
                }
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_ALBUM) {
            //选择相册
            if (data != null) {
                mUri = data.getData();
            }
            results = new Uri[]{mUri};
        }
        mFilePathCallback.onReceiveValue(results);
        mFilePathCallback = null;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }


    public MWebVIew(Context context) {
        super(context);
    }

    public MWebVIew(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MWebVIew(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
