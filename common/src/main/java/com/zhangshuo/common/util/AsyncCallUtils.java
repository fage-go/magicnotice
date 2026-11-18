package com.zhangshuo.common.util;

import java.util.concurrent.Callable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AsyncCallUtils {

    public static <T> void asyncSingleCall(Callable<T> callable, AsyncCallback<T> callback) {
        if (callable == null) return;
        Single.fromCallable((callable))
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<T>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(T data) {
                        if (callback != null) {
                            callback.onSuccess(data);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (callback != null) {
                            callback.onError(e);
                        }
                    }
                });
    }


    /**
     * 异步调用并回调结果到主线程
     *
     * @param callable 异步调用方法
     * @param callback 结果回调
     */
    public static <T> void asyncCall(Callable<T> callable, AsyncCallback<T> callback) {
        asyncCall(callable, callback, null);
    }

    /**
     * 异步调用并回调结果到主线程
     *
     * @param callable            异步调用方法
     * @param callback            结果回调
     * @param compositeDisposable 回调集合
     */
    public static <T> void asyncCall(Callable<T> callable, AsyncCallback<T> callback, CompositeDisposable compositeDisposable) {
        if (callable == null) return;
        Single.fromCallable((callable))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<T>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        if (compositeDisposable != null) {
                            compositeDisposable.add(d);
                        }
                    }

                    @Override
                    public void onSuccess(T data) {
                        if (callback != null) {
                            callback.onSuccess(data);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (callback != null) {
                            callback.onError(e);
                        }
                    }
                });
    }
}