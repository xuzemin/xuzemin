package com.android.srd.launcher.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.srd.launcher.Object.GridItem;
import com.android.srd.launcher.Object.IntentUrl;
import com.android.srd.launcher.R;
import com.android.srd.launcher.View.BlurBuilder;
import com.android.srd.launcher.View.FastBlur;
import com.android.srd.launcher.View.MyDialog;
import com.android.srd.launcher.adapter.GridViewAdapter;
import com.android.srd.launcher.util.Constant;
import com.google.gson.Gson;

import java.util.ArrayList;


public class MainActivity extends Activity implements View.OnClickListener {
    private IntentUrl intentUrl;
    private Intent intent;
    private LinearLayout amazon,layout_cn,girdview_layout;
    private LinearLayout bbc;
    private static Bitmap backgroundback = null;
    private GridView gridView;
    private MyDialog myDialog;
    private RelativeLayout shipin,news_layout,message_layout,contact,settings,play;
    private GridViewAdapter gridViewAdapter;
    private ArrayList<GridItem> mGridData;
    private static ArrayList<String> urllist;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);
        layout_cn = findViewById(R.id.layout_cn);
        gridView = findViewById(R.id.all_content);
        girdview_layout = findViewById(R.id.girdview_layout);
        initView();
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initView(){
        if(!Constant.isCN) {
            girdview_layout.setVisibility(View.GONE);
            layout_cn.setVisibility(View.VISIBLE);
            shipin = findViewById(R.id.shipin);
            shipin.setOnClickListener(this);
            news_layout = findViewById(R.id.news_layout);
            news_layout.setOnClickListener(this);
            message_layout = findViewById(R.id.message_layout);
            message_layout.setOnClickListener(this);
            contact = findViewById(R.id.contact);
            contact.setOnClickListener(this);
            play = findViewById(R.id.play);
            play.setOnClickListener(this);
            settings = findViewById(R.id.settings);
            settings.setOnClickListener(this);
        }else{
            girdview_layout.setVisibility(View.VISIBLE);
            layout_cn.setVisibility(View.GONE);
            intdata();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                backgroundback = FastBlur.doBlur(BlurBuilder.getTab_bg(MainActivity.this),80,false);

            }
        }).start();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void intdata(){
        urllist = new ArrayList<>();
        urllist.add("https://www.amazon.com/");
        urllist.add("https://www.bbc.co.uk/news");
        urllist.add("https://www.cnn.com/");
        urllist.add("https://www.discovery.com/");
        urllist.add("https://www.economist.com/");
        urllist.add("https://www.facebook.com/");

        urllist.add("https://news.google.com/");
        urllist.add("https://www.instagram.com/");
        urllist.add("https://www.mlb.com/");
        urllist.add("https://www.nasa.gov/");
        urllist.add("https://www.nba.com/");
        urllist.add("https://www.iqiyi.com/");

        urllist.add("https://www.spotify.com/");
        urllist.add("https://www.ted.com/");
        urllist.add("https://twitter.com/");
        urllist.add("https://www.weibo.com/");
        urllist.add("https://www.youku.com/");
        urllist.add("https://www.youtube.com/");
        int icno[] = { R.mipmap.logo_amazon, R.mipmap.logo_bbc, R.mipmap.logo_cnn,
                R.mipmap.logo_discovery, R.mipmap.logo_economist, R.mipmap.logo_fb,
                R.mipmap.logo_googlenews, R.mipmap.logo_ig, R.mipmap.logo_mlb,
                R.mipmap.logo_nasa, R.mipmap.logo_nba, R.mipmap.logo_qiy,
                R.mipmap.logo_spotify, R.mipmap.logo_ted, R.mipmap.logo_twitter,
                R.mipmap.logo_weibo, R.mipmap.logo_youku, R.mipmap.logo_youtube
        };
        String name[]={ getString(R.string.amazon),getString(R.string.bbc),getString(R.string.ccn),
                getString(R.string.discovery),getString(R.string.economist),getString(R.string.fb),
                getString(R.string.google_news),getString(R.string.logo_ig),getString(R.string.logo_mlb),
                getString(R.string.lnasa),getString(R.string.logo_nba),getString(R.string.logo_qiy),
                getString(R.string.logo_spotify),getString(R.string.logo_ted),getString(R.string.logo_twitter),
                getString(R.string.logo_weibo),getString(R.string.logo_youku),getString(R.string.logo_youtube)
        };
        mGridData = new ArrayList<>();
        for (int i=0; i<icno.length; i++) {
            GridItem item = new GridItem();
            item.setTitle(name[i]);
            item.setImage(getDrawable(icno[i]));
            mGridData.add(item);
        }
        gridViewAdapter = new GridViewAdapter(this, R.layout.gridview_item_main, mGridData);
        gridView.setAdapter(gridViewAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /**获得Intent*/
                intentUrl=new IntentUrl();
                intentUrl.setUrl(urllist.get(position));
                intent=new Intent(MainActivity.this,WebViewActivty.class);
                intent.putExtra("intentUrl",new Gson().toJson(intentUrl));
                startActivity(intent);
            }
        });
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(myDialog != null && myDialog.isShowing()){
                myDialog.dismiss();
            }
        }
    };

    @Override
    public void onClick(View v) {
        BlurBuilder.snapShotWithoutStatusBar(this);

        switch (v.getId()){
            case R.id.message_layout:
                myDialog=new MyDialog(MainActivity.this,R.style.MyDialog,Constant.MESSAGE,backgroundback,onClickListener);
                myDialog.show();
                break;
            case R.id.shipin:
                myDialog=new MyDialog(MainActivity.this,R.style.MyDialog,Constant.VIDEO, backgroundback,onClickListener);
                myDialog.show();
                break;
            case R.id.contact:
                myDialog=new MyDialog(MainActivity.this,R.style.MyDialog,Constant.CONSTACT,backgroundback,onClickListener);
                myDialog.show();
                break;
            case R.id.settings:
                break;
            case R.id.play:
                break;
            case R.id.news_layout:
                myDialog=new MyDialog(MainActivity.this,R.style.MyDialog,Constant.NEW,backgroundback,onClickListener);
                myDialog.show();
                break;
        }
    }

    public void starIntent(String url){
        intentUrl=new IntentUrl();
        intentUrl.setUrl(url);
        intent=new Intent(this,WebViewActivty.class);
        intent.putExtra("intentUrl",new Gson().toJson(intentUrl));
        startActivity(intent);
    }

    @Override
    public void finish() {
        BlurBuilder.recycle();
        overridePendingTransition(android.view.animation.Animation.INFINITE,
                android.view.animation.Animation.INFINITE);
    }

}
