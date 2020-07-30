package com.protruly.floatwindowlib.callback;

/**
 * Desc:http回球结果的回调，用于在主线程中处理
 *
 * @author Administrator
 * @time 2017/6/20.
 */
public interface HttpCallBack<T> {
    void onError();
    void onResponse(T response);
}
