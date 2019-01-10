package com.android.srd.launcher.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.srd.launcher.Object.GridItem;
import com.android.srd.launcher.Object.IntentUrl;
import com.android.srd.launcher.R;
import com.android.srd.launcher.adapter.GridViewAdapter;
import com.google.gson.Gson;

import java.util.ArrayList;

public class MainActivity extends Activity implements View.OnClickListener {
    private IntentUrl intentUrl;
    private Intent intent;
    private LinearLayout amazon;
    private LinearLayout bbc;
    private GridView gridView;
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

//        amazon = findViewById(R.id.amazon);
        bbc = findViewById(R.id.bbc);
//        amazon.setOnClickListener(this);
        bbc.setOnClickListener(this);

        gridView = findViewById(R.id.all_content);

        intdata();
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
        gridViewAdapter = new GridViewAdapter(this, R.layout.gridview_item, mGridData);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
//            case R.id.amazon:
//                intentUrl=new IntentUrl();
//                intentUrl.setUrl("https://www.amazon.com/");
//                intent=new Intent(this,WebViewActivty.class);
//                intent.putExtra("intentUrl",new Gson().toJson(intentUrl));
//                startActivity(intent);
//                break;
//
            case R.id.bbc:
                starIntent("https://www.bbc.co.uk/news");
                break;
//
//            case R.id.btn_amazon:
//                intentUrl=new IntentUrl();
//                intentUrl.setUrl("https://www.amazon.com/");
//                intent=new Intent(this,WebViewActivty.class);
//                intent.putExtra("intentUrl",new Gson().toJson(intentUrl));
//                startActivity(intent);
//                break;
//
//            case R.id.btn_bbc:
//                intentUrl=new IntentUrl();
//                intentUrl.setUrl("https://www.bbc.co.uk/news");
//                intent=new Intent(this,WebViewActivty.class);
//                intent.putExtra("intentUrl",new Gson().toJson(intentUrl));
//                startActivity(intent);
//                break;
        }
    }

    public void starIntent(String url){
        intentUrl=new IntentUrl();
        intentUrl.setUrl(url);
        intent=new Intent(this,WebViewActivty.class);
        intent.putExtra("intentUrl",new Gson().toJson(intentUrl));
        startActivity(intent);
    }
}
