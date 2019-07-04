package com.xuzemin.joinus.utils;

import okhttp3.*;

import java.io.IOException;

public class HttpClientHelper {
    private static HttpClientHelper httpClient;
    public static HttpClientHelper getInstance(){
        if(httpClient == null){
            httpClient = new HttpClientHelper();
        }
        return httpClient;
    }

    public void geturl(String url){
        //1.创建OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        //2.创建Request对象，设置一个url地址（百度地址）,设置请求方式。
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        //3.创建一个call对象,参数就是Request请求对象
        Call call = okHttpClient.newCall(request);
        //4.同步调用会阻塞主线程,这边在子线程进行
        try {
            //同步调用,返回Response,会抛出IO异常
            Response response = call.execute();
            LogUtil.i("response"+response.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
