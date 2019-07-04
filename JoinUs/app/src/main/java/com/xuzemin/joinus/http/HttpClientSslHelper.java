package com.xuzemin.joinus.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.*;
import java.security.*;

public class HttpClientSslHelper {
    private static final String KEY_STORE_TYPE_BKS = "bks";
    private static final String KEY_STORE_TYPE_P12 = "PKCS12";
    private static final String SCHEME_HTTPS = "https";
    private static final int HTTPS_PORT = 8444;

    private static final String KEY_STORE_CLIENT_PATH = "client.p12";
    private static final String KEY_STORE_TRUST_PATH = "client.truststore";
    private static final String KEY_STORE_PASSWORD = "123456";
    private static final String KEY_STORE_TRUST_PASSWORD = "123456";
    private static KeyStore keyStore;
    private static KeyStore trustStore;
    public static HttpClient getSslHttpClient(Context pContext) {
        HttpClient httpsClient = new DefaultHttpClient();
        try {
            // 服务器端需要验证的客户端证书
            keyStore = KeyStore.getInstance(KEY_STORE_TYPE_P12);

            // 客户端信任的服务器端证书
            trustStore = KeyStore.getInstance(KEY_STORE_TYPE_BKS);

            InputStream ksIn = pContext.getResources().getAssets().open(KEY_STORE_CLIENT_PATH);
            InputStream tsIn = pContext.getResources().getAssets().open(KEY_STORE_TRUST_PATH);
            try {
                keyStore.load(ksIn, KEY_STORE_PASSWORD.toCharArray());
                trustStore.load(tsIn, KEY_STORE_TRUST_PASSWORD.toCharArray());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    ksIn.close();
                } catch (Exception ignore) {
                }
                try {
                    tsIn.close();
                } catch (Exception ignore) {
                }
            }
            SSLSocketFactory socketFactory = new SSLSocketFactory(keyStore, KEY_STORE_PASSWORD, trustStore);
            Scheme sch = new Scheme(SCHEME_HTTPS, socketFactory, HTTPS_PORT);
            httpsClient.getConnectionManager().getSchemeRegistry().register(sch);
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return httpsClient;
    }
}
