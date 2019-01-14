package com.android.srd.launcher.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.VideoView;

import com.android.srd.launcher.Object.IntentUrl;
import com.android.srd.launcher.R;
import com.android.srd.launcher.View.ProgressView;
import com.google.gson.Gson;

import static com.android.srd.launcher.util.Constant.TAG;

public class WebViewActivty extends Activity {
    private WebView webView;
    private ProgressView progressBar;
    private VideoView videoView;
    private LoadNewUrlListener loadNewUrlListener;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        setContentView(R.layout.activity_webview);
        videoView = findViewById(R.id.videoView);
        String stringJson=getIntent().getStringExtra("intentUrl");
        if(stringJson!=null) {
            IntentUrl intentUrl = new Gson().fromJson(stringJson, IntentUrl.class);
            if(intentUrl!=null && intentUrl.getUrl()!=null) {
                progressBar = findViewById(R.id.progressbar);//进度条
                webView = findViewById(R.id.webview);
                webView.loadUrl(intentUrl.getUrl());
                WebSettings webSettings = webView.getSettings();
                String ua = webSettings.getUserAgentString();
                webSettings.setUserAgentString(ua+";my mark");
                webSettings.setSupportMultipleWindows(false);
                webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
                webSettings.setJavaScriptEnabled(true);
                webSettings.setAllowContentAccess(true);
                webSettings.setAppCacheEnabled(false);
                webSettings.setBuiltInZoomControls(false);
                webSettings.setDisplayZoomControls(false);
                webSettings.setUseWideViewPort(true);
                webSettings.setLoadWithOverviewMode(true);
                webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
                webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
                webSettings.setPluginState(WebSettings.PluginState.ON);
//                DisplayMetrics metrics = new DisplayMetrics();
//                getWindowManager().getDefaultDisplay().getMetrics(metrics);
//                int mDensity = metrics.densityDpi;
//                if (mDensity == 120) {
//                    webSettings.setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
//                } else if (mDensity == 160) {
//                    webSettings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
//                } else if (mDensity == 240) {
//                    webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
//                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                }
                webSettings.setDomStorageEnabled(true);//这句话必须保留。。否则无法播放优酷视频网页。。其他的可以
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        //页面加载完成后加载下面的javascript，修改页面中所有用target="_blank"标记的url（在url前加标记为“newtab”）
                        //这里要注意一下那个js的注入方法，不要在最后面放那个替换的方法，不然会出错
                        view.loadUrl("javascript: var allLinks = document.getElementsByTagName('a'); if (allLinks) {var i;for (i=0; i<allLinks.length; i++) {var link = allLinks[i];var target = link.getAttribute('target'); if (target && target == '_blank') {link.href = 'newtab:'+link.href;link.setAttribute('target','_self');}}}");
                    }

                    //这个方法是在你点击webView中的链接时调用，你可以根据点击的内容来进行判断是否跳转该链接
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String urls) {
                        if (urls.startsWith("newtab:")) {
                            //在这里拦截加了newtab:前缀的URL，来进行你要做的操作
                            urls = urls.replace("newtab:", "");
                            view.loadUrl(urls);
                        } else if(urls.startsWith("http")){
                            if (loadNewUrlListener != null) {
                                if (!loadNewUrlListener.loadNewUrl(view, urls)) {
                                    view.loadUrl(urls);
                                }
                            } else {
                                view.loadUrl(urls);
                            }
                        }else {
                            try {
                                Intent intent = new Intent(Intent.ACTION_VIEW,
                                        Uri.parse(urls));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                view.getContext().startActivity(intent);
                            } catch (Exception e) {
//                没有安装对应的应用会抛异常
                                e.printStackTrace();
                            }
//                            view.loadUrl(urls); //如果是没有加那个前缀的就正常
                        }
                        progressBar.setVisibility(View.VISIBLE);
                        view.setWebChromeClient(new WebChromeClientProgress());
                        return true;
                    }
                });
            }
        }
    }

    private class WebChromeClientProgress extends WebChromeClient{

        @Override
        public void onProgressChanged(WebView view, int progress) {
            if (progressBar != null) {
                progressBar.setProgress(progress);
                if (progress == 100) progressBar.setVisibility(View.GONE);
            }
            super.onProgressChanged(view, progress);
        }

        @Override
        @SuppressWarnings("deprecation")
        public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) // Available in API level 14+, deprecated in API level 18+
        {
            onShowCustomView(view, callback);
        }


//        @Override
//        public void onShowCustomView(View view, CustomViewCallback callback) {
//            if (videoView == null) {
//                super.onShowCustomView(view, callback);
//            } else {
//                if (view instanceof FrameLayout) {
//                    FrameLayout frameLayout = (FrameLayout) view;
//                    View focusedChild = frameLayout.getFocusedChild();
//
////                更改状态
//                    this.isVideoFullscreen = true;
//                    this.videoViewContainer = frameLayout;
//                    this.videoViewCallback = callback;
//
////                隐藏布局
//                    noVideoView.setVisibility(View.INVISIBLE);
////                将网页视频的View ，加入显示的地方
//                    videoView.addView(videoViewContainer, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//                    videoView.setVisibility(View.VISIBLE);
//
////                添加视频播放事件
//                    if (focusedChild instanceof android.widget.VideoView) {
//                        // android.widget.VideoView (typically API level <11)
//                        android.widget.VideoView videoView = (android.widget.VideoView) focusedChild;
//
//                        // Handle all the required events
//                        videoView.setOnPreparedListener(this);
//                        videoView.setOnCompletionListener(this);
//                        videoView.setOnErrorListener(this);
//                    }
//
//                    // Notify full-screen change
//                    if (toggledFullscreenCallback != null) {
//                        toggledFullscreenCallback.toggledFullscreen(true);
//                    }
//                }else {
//                    super.onShowCustomView(view, callback);
//                }
//            }
//        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(TAG,"是否有上一个页面:"+webView.canGoBack());
        if (webView.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK){//点击返回按钮的时候判断有没有上一页
            webView.goBack(); // goBack()表示返回webView的上一页面
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.down_in, R.anim.down_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放资源
        webView.destroy();
        webView=null;
    }

    public LoadNewUrlListener getLoadNewUrlListener() {
        return loadNewUrlListener;
    }

    public void setLoadNewUrlListener(LoadNewUrlListener loadNewUrlListener) {
        this.loadNewUrlListener = loadNewUrlListener;
    }

    public interface LoadNewUrlListener {
        boolean loadNewUrl(WebView view, String url);
    }

}

