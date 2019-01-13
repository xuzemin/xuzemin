package com.android.srd.launcher.View;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.srd.launcher.Object.GridItem;
import com.android.srd.launcher.Object.IntentUrl;
import com.android.srd.launcher.R;
import com.android.srd.launcher.activity.WebViewActivty;
import com.android.srd.launcher.adapter.GridViewAdapter;
import com.android.srd.launcher.util.Constant;
import com.android.srd.launcher.util.ScreenUtil;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * 创建自定义的Dialog，主要学习实现原理
 * Created by admin on 2017/8/30.
 */

public class MyDialog extends Dialog {
    //确定文本和取消文本的显示的内容
    private int xmlId;
    private IntentUrl intentUrl;
    private Intent intent;
    private TextView title;
    private Context context;
    private LinearLayout background;
    private ArrayList<GridItem> mGridData;
    private GridViewAdapter gridViewAdapter;
    private static ArrayList<String> urllist;
    private GridView girdView;
    private Bitmap bmp;
    private View.OnClickListener backgoundClick;
    public MyDialog(@NonNull Context context, @StyleRes int themeResId,int xmlId) {
        super(context, themeResId);
        this.xmlId = xmlId;
        this.context = context;
    }
    public MyDialog(@NonNull Context context, @StyleRes int themeResId, int xmlId, Bitmap bmp, View.OnClickListener backgoundClick) {
        super(context, themeResId);
        this.xmlId = xmlId;
        this.context = context;
        this.bmp = bmp;
        this.backgoundClick = backgoundClick;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化界面控件
        initView();

        //初始化界面数据
        initData();
        //初始化界面控件的事件
        initEvent();
    }

    /**
     * 初始化界面控件
     */
    @SuppressLint("NewApi")
    private void initView() {
        switch (xmlId){
            case Constant.MESSAGE:
                setContentView(R.layout.activity_dialog_message);
                //空白处不能取消动画
                break;
            default:
                setContentView(R.layout.activity_dialog);
                //空白处不能取消动画
                break;
        }
        setCancelable(true);
        setCanceledOnTouchOutside(true);
//        if(bmp !=null){
        background = findViewById(R.id.background);
        if(bmp !=null) {
            background.setBackgroundDrawable(new BitmapDrawable(bmp));
        }
        background.setOnClickListener(backgoundClick);
//        }
        title = findViewById(R.id.title);
        girdView = findViewById(R.id.news_girdview);
    }

    @SuppressLint("NewApi")
    public void setAdapter(int[] icno, String[] name){
        mGridData = new ArrayList<>();
        for (int i=0; i<icno.length; i++) {
            GridItem item = new GridItem();
            item.setTitle(name[i]);
            item.setImage(context.getDrawable(icno[i]));
            mGridData.add(item);
        }
        gridViewAdapter = new GridViewAdapter(context, R.layout.gridview_item, mGridData);
        girdView.setAdapter(gridViewAdapter);
        girdView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /**获得Intent*/
                intentUrl=new IntentUrl();
                intentUrl.setUrl(urllist.get(position));
                intent=new Intent(context,WebViewActivty.class);
                intent.putExtra("intentUrl",new Gson().toJson(intentUrl));
                context.startActivity(intent);
            }
        });
    }

    /**
     * 初始化界面控件的显示数据
     */
    private void initData() {
        switch (xmlId){
            case Constant.VIDEO:
                title.setText(R.string.video);
                urllist = new ArrayList<>();
                urllist.add("https://www.iqiyi.com/");
                urllist.add("https://www.spotify.com/");
                urllist.add("https://www.youku.com/");
                urllist.add("https://www.youtube.com/");
                int icno[] = { R.mipmap.logo_qiy,
                        R.mipmap.logo_spotify,R.mipmap.logo_youku, R.mipmap.logo_youtube
                };
                String name[]={ context.getString(R.string.logo_qiy),
                        context.getString(R.string.logo_spotify),context.getString(R.string.logo_youku)
                        ,context.getString(R.string.logo_youtube)
                };
                setAdapter(icno,name);
                break;
            case Constant.MESSAGE:
                title.setText(R.string.message);
                urllist = new ArrayList<>();
                urllist.add("https://www.amazon.com/");
                urllist.add("https://www.mlb.com/");
                urllist.add("https://www.discovery.com/");
                urllist.add("https://www.nasa.gov/");
                urllist.add("https://www.nba.com/");
                urllist.add("https://www.ted.com/");
                int message[] = { R.mipmap.logo_amazon,
                        R.mipmap.logo_mlb,R.mipmap.logo_discovery, R.mipmap.logo_nasa,
                        R.mipmap.logo_nba,R.mipmap.logo_ted
                };
                String messagename[]={ context.getString(R.string.amazon),
                        context.getString(R.string.logo_mlb),context.getString(R.string.discovery)
                        ,context.getString(R.string.lnasa),context.getString(R.string.logo_nba)
                        ,context.getString(R.string.logo_ted)
                };
                setAdapter(message,messagename);
                break;
            case Constant.CONSTACT:
                title.setText(R.string.social_contact);
                urllist = new ArrayList<>();

                urllist.add("https://www.weibo.com/");
                urllist.add("https://www.facebook.com/");
                urllist.add("https://www.instagram.com/");
                urllist.add("https://twitter.com/");
                int constact_icno[] = { R.mipmap.logo_weibo,
                        R.mipmap.logo_fb,R.mipmap.logo_ig, R.mipmap.logo_twitter
                };
                String constact_name[]={ context.getString(R.string.logo_weibo),
                        context.getString(R.string.fb),context.getString(R.string.logo_ig)
                        ,context.getString(R.string.logo_twitter)
                };
                setAdapter(constact_icno,constact_name);
                break;
            case Constant.NEW:
                title.setText(R.string.news);
                urllist = new ArrayList<>();
                urllist.add("https://www.bbc.co.uk/news");
                urllist.add("https://www.cnn.com/");
                urllist.add("https://news.google.com/");
                urllist.add("https://www.economist.com/");
                int news_icno[] = { R.mipmap.logo_bbc,
                        R.mipmap.logo_cnn,R.mipmap.logo_googlenews, R.mipmap.logo_economist
                };
                String news_name[]={ context.getString(R.string.bbc),
                        context.getString(R.string.ccn),context.getString(R.string.google_news)
                        ,context.getString(R.string.economist)
                };
                setAdapter(news_icno,news_name);
                break;
        }
    }

    /**
     * 初始化界面的确定和取消监听
     */
    private void initEvent() {
        switch (xmlId){

        }
    }

    @Override
    public void show() {
        super.show();
        /**
         * 设置宽度全屏，要设置在show的后面
         */

        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.width = ScreenUtil.getScreenWidth(context);
        layoutParams.height = ScreenUtil.getScreenHeight(context);
        getWindow().getDecorView().setPadding(0, 0, 0, 0);
        getWindow().setAttributes(layoutParams);
        getWindow().setWindowAnimations(R.style.dialogWindowAnim);
    }

}
