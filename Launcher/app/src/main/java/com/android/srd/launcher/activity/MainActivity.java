package com.android.srd.launcher.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.srd.launcher.Object.GridItem;
import com.android.srd.launcher.Object.IntentUrl;
import com.android.srd.launcher.R;
import com.android.srd.launcher.View.MyDialog;
import com.android.srd.launcher.adapter.GridViewAdapter;
import com.android.srd.launcher.util.Constant;
import com.google.gson.Gson;

import java.util.ArrayList;


public class MainActivity extends Activity implements View.OnClickListener {
    private IntentUrl intentUrl;
    private Intent intent;
    private LinearLayout amazon;
    private LinearLayout bbc;
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

        bbc = findViewById(R.id.bbc);
        bbc.setOnClickListener(this);
        initView();

        gridView = findViewById(R.id.all_content);

        intdata();
    }
    private void initView(){
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
            case R.id.message_layout:
                myDialog=new MyDialog(MainActivity.this,R.style.SquareEntranceDialogStyle,Constant.MESSAGE);
                myDialog.show();
                break;
            case R.id.shipin:
                myDialog=new MyDialog(MainActivity.this,R.style.MyDialog,Constant.VIDEO);
                myDialog.show();
                break;
            case R.id.contact:
                myDialog=new MyDialog(MainActivity.this,R.style.MyDialog,Constant.CONSTACT);
                myDialog.show();
                break;
            case R.id.settings:
                break;
            case R.id.play:
                break;
            case R.id.news_layout:
                myDialog=new MyDialog(MainActivity.this,R.style.MyDialog,Constant.NEW);
                myDialog.show();
                break;

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

    public static Bitmap fastblur(Bitmap sentBitmap, int radius) {

        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int temp = 256 * divsum;
        int dv[] = new int[temp];
        for (i = 0; i < temp; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16)
                        | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return (bitmap);
    }
}
