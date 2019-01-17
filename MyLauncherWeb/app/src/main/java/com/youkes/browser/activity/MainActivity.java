package com.youkes.browser.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.youkes.browser.R;
import com.youkes.browser.adapter.GridViewAdapter;
import com.youkes.browser.object.GridItem;
import com.youkes.browser.object.IntentUrl;
import com.youkes.browser.utils.Constant;
import com.youkes.browser.utils.SaveBitmap;
import com.youkes.browser.utils.ScreenUtil;
import com.youkes.browser.utils.SharedPreferencesHelper;
import com.youkes.browser.view.BlurBuilder;
import com.youkes.browser.view.MyDialog;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends Activity implements View.OnClickListener {
    public static IntentUrl intentUrl;
    private Intent intent;
    private LinearLayout layout_cn,layout_content,layout_video,layout_new,layout_message,layout_contact,background,layout_background;
    private boolean isMain = true;
    private int currentIndex = 0;
    private static Bitmap backgroundback = null;
    private GridView gridView,video_girdview,news_girdview,message_girdview,contact_girdview,gridview_background;
    private MyDialog myDialog;
    private static int REQUEST_EXTERNAL_STRONGE = 1;
    private RelativeLayout shipin,news_layout,message_layout,contact,settings,play,content;
    private GridViewAdapter gridViewAdapter,gridViewAdapter_video,gridViewAdapter_news,gridViewAdapter_message,gridViewAdapter_contact,gridViewAdapter_background;
    private ArrayList<GridItem> mGridData;
    private static ArrayList<String> urllist,video_urllist,news_urllist,message_urllist,contact_urllist;
    private PackageManager packageManager;
    private ProgressDialog progressDialog;
    private static ArrayList<String> ImageNameList;
    private static ArrayList<Bitmap> ImageBitmapList;
    private static String FileName = null;
    private SharedPreferencesHelper sharedPreferencesHelper;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 110:
                    Constant.debugLog(""+msg.arg1);
                    //拷贝过程中更新进度条
                    updateProgressDialog(msg.arg1);
                    //msg.arg1其实就是copyFile发过来的value值
                    if(msg.arg1==100) {
                        //拷贝完了就隐藏进度条
                        hideProgressDialog();
                        RefreshArray();
                    }
                    break;
            }
        }
    } ;

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
        setContentView(R.layout.activity_first);
        layout_cn = findViewById(R.id.layout_cn);
        gridView = findViewById(R.id.all_content);
        video_girdview = findViewById(R.id.video_girdview);
        layout_content = findViewById(R.id.layout_content);
        layout_video = findViewById(R.id.layout_video);
        news_girdview = findViewById(R.id.new_girdview);
        layout_new = findViewById(R.id.layout_new);
        message_girdview = findViewById(R.id.message_girdview);
        layout_message = findViewById(R.id.layout_message);
        contact_girdview = findViewById(R.id.contact_girdview);
        layout_contact = findViewById(R.id.layout_contact);
        background = findViewById(R.id.background);
        layout_background =findViewById(R.id.layout_background);
        gridview_background = findViewById(R.id.gridview_background);
        initView();
    }
    private void initView(){
//        layout_cn.setVisibility(View.VISIBLE);
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
        content = findViewById(R.id.content);
        content.setOnClickListener(this);
        background.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                currentIndex = Constant.BACKGROUND;
                isMain = false;
                setAnimation(Constant.BACKGROUND,layout_cn,layout_background);
                return false;
            }
        });
        intdata();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedPreferencesHelper = new SharedPreferencesHelper(
                MainActivity.this, Constant.TAG);
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            backgroundback = ((BitmapDrawable)getResources().getDrawable(R.mipmap.beijing4)).getBitmap();
            background.setBackgroundDrawable(new BitmapDrawable(SaveBitmap.getInstance().resizeBitmap(
                    backgroundback,ScreenUtil.getScreenWidth(getApplicationContext()),ScreenUtil.getScreenHeight(getApplicationContext()))));
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STRONGE);
        }else {
            if(sharedPreferencesHelper.contain(Constant.fileName)){
//            SaveBitmap.saveBitmap(getApplicationContext(), backgroundback, Constant.backround);
                RefreshArray();
                if(ImageNameList !=null && ImageNameList.size() >= 4){
                    Constant.debugLog(ImageNameList.get(0));
                    Constant.debugLog(""+ImageBitmapList.get(0).getHeight());
                    reBackground();
                }else{
                    if(backgroundback !=null) {
                        background.setBackgroundDrawable(new BitmapDrawable(SaveBitmap.getInstance().resizeBitmap(
                                backgroundback,ScreenUtil.getScreenWidth(getApplicationContext()),ScreenUtil.getScreenHeight(getApplicationContext()))));
                    }else{
                        ImageInit();
                    }
                }
                reBackground();
            }else{
                ImageInit();
            }

        }
    }
    private void reBackground(){
        if(!sharedPreferencesHelper.contain(Constant.fileName)){
            ImageInit();
        }else {
            FileName = sharedPreferencesHelper.getSharedPreference(Constant.fileName, "").toString().trim();
            Constant.debugLog(FileName);
            if (!FileName.equals("")) {
                backgroundback = SaveBitmap.getInstance().getBitmapFileName(FileName);
                if(backgroundback == null){
                    ImageInit();
                }else{
                    background.setBackgroundDrawable(new BitmapDrawable(SaveBitmap.getInstance().resizeBitmap(
                            backgroundback,ScreenUtil.getScreenWidth(getApplicationContext()),ScreenUtil.getScreenHeight(getApplicationContext()))));
                }
            }
        }
    }

    public void RefreshArray(){
        SaveBitmap.getInstance().getPhotoVideoPath();
        ImageNameList = SaveBitmap.getInstance().getImageNameList();
        ImageBitmapList = SaveBitmap.getInstance().getImageBitmapList();

        mGridData = new ArrayList<>();
        if(ImageNameList !=null && ImageNameList.size()>0 &&
                ImageBitmapList !=null && ImageBitmapList.size()>0
                && ImageNameList.size() == ImageBitmapList.size()) {
            GridItem item = new GridItem();
            item.setTitle(getString(R.string.add));
            item.setImage(ContextCompat.getDrawable(getApplicationContext(),R.mipmap.add_1));
            mGridData.add(item);
            for (int i = 0; i < ImageNameList.size(); i++) {
                item = new GridItem();
                item.setTitle(ImageNameList.get(i));
                item.setImage(new BitmapDrawable(ImageBitmapList.get(i)));
                mGridData.add(item);
            }

            gridViewAdapter_background = new GridViewAdapter(this, R.layout.gridview_item, mGridData);
            gridview_background.setAdapter(gridViewAdapter_background);
            gridview_background.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    /**获得Intent*/
                    if(position == 0){
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");//无类型限制
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        startActivityForResult(intent, 1);
                    }else{
                        Constant.debugLog(ImageNameList.get(position - 1));
                        sharedPreferencesHelper.put(Constant.fileName,ImageNameList.get(position - 1));
                        reBackground();
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            String finalName = null;
            Bitmap photoBmp = null;
            Uri mImageCaptureUri = data.getData();
            Constant.debugLog("mImageCaptureUri:"+mImageCaptureUri);
            String fileName = SaveBitmap.getInstance().getPath(this,mImageCaptureUri);
            if(fileName != null) {
                String[] array = fileName.split("/");
                Constant.debugLog("fileName:" + fileName);
                if (array.length > 0) {
                    fileName = array[array.length - 1];
                    Constant.debugLog("path:" + fileName);
                    array = fileName.split("\\.");
                    if (array.length > 0) {
                        Constant.debugLog("path:" + array[array.length - 1] + " " + array[array.length - 1].length());
                        finalName = fileName.substring(0, fileName.length() - (array[array.length - 1].length() + 1));
                    }
                }
            }else{
                String[] array = mImageCaptureUri.toString().split("/");
                Constant.debugLog("fileName:" + fileName);
                if (array.length > 0) {
                    fileName = array[array.length - 1];
                    Constant.debugLog("path:" + fileName);
                    array = fileName.split("\\.");
                    if (array.length > 0) {
                        Constant.debugLog("path:" + array[array.length - 1] + " " + array[array.length - 1].length());
                        finalName = fileName.substring(0, fileName.length() - (array[array.length - 1].length() + 1));
                    }
                }
            }
            Constant.debugLog("path:"+finalName);
            if (mImageCaptureUri != null) {
                try {
                    photoBmp = SaveBitmap.getInstance().getBitmapFormUri(this, mImageCaptureUri);
//                    photoBmp = SaveBitmap.getInstance().getBitmapFormUri(this, Uri.fromFile(file));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
//            int degree = SaveBitmap.getInstance().getBitmapDegree(file.getAbsolutePath());
            /**
             * 把图片旋转为正的方向
             */
//            Bitmap bitmap = SaveBitmap.getInstance().rotateBitmapByDegree(photoBmp, degree);
//            Uri uri = data.getData();
//            Constant.debugLog("uri"+uri);
//            String  oldpath = SaveBitmap.getInstance().getPath(this, uri);
//            Constant.debugLog("oldpath"+oldpath);
//            Bitmap bitmap = SaveBitmap.getInstance().getBitMBitmap(oldpath);

            if(photoBmp !=null) {
                if(finalName== null) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");// HH:mm:ss
                    Date date = new Date(System.currentTimeMillis());
                    SaveBitmap.getInstance().saveBitmap(getApplicationContext(), photoBmp, simpleDateFormat.format(date));
                }else{
                    SaveBitmap.getInstance().saveBitmap(getApplicationContext(), photoBmp, finalName);
                }
                RefreshArray();
            }
        }
    }

    private void ImageInit(){
        backgroundback = ((BitmapDrawable)getResources().getDrawable(R.mipmap.beijing4)).getBitmap();
        SaveBitmap.getInstance().saveBitmap(getApplicationContext(), backgroundback, Constant.Default);
        Bitmap cache = ((BitmapDrawable)getResources().getDrawable(R.mipmap.beijing1)).getBitmap();
        SaveBitmap.getInstance().saveBitmap(getApplicationContext(), cache, "backround");
        cache = ((BitmapDrawable)getResources().getDrawable(R.mipmap.beijing2)).getBitmap();
        SaveBitmap.getInstance().saveBitmap(getApplicationContext(), cache, "backround1");
        cache = ((BitmapDrawable)getResources().getDrawable(R.mipmap.beijing)).getBitmap();
        SaveBitmap.getInstance().saveBitmap(getApplicationContext(), cache, "backround2");
        sharedPreferencesHelper.put(Constant.fileName,Constant.Default);
        FileName = "default";
        RefreshArray();
        reBackground();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//根据请求是否通过的返回码进行判断，然后进一步运行程序
        if (grantResults.length > 0 && requestCode == REQUEST_EXTERNAL_STRONGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            ImageInit();
            RefreshArray();
        }
//        else{
////            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STRONGE);
//        }

    }


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
        GridItem items = new GridItem();
        items.setTitle("Settings");
        items.setImage(getResources().getDrawable(R.mipmap.shezhi));
        mGridData.add(items);
        items = new GridItem();
        items.setTitle("Wallpaper");
        items.setImage(getResources().getDrawable(R.mipmap.bak));
        mGridData.add(items);
        items = new GridItem();
        items.setTitle("Google Play");
        items.setImage(getResources().getDrawable(R.mipmap.googleplay));
        mGridData.add(items);
        for (int i=0; i<icno.length; i++) {
            items = new GridItem();
            items.setTitle(name[i]);
            items.setImage(getResources().getDrawable(icno[i]));
            mGridData.add(items);
        }
        gridViewAdapter = new GridViewAdapter(this, R.layout.gridview_item_main, mGridData);
        gridView.setAdapter(gridViewAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /**获得Intent*/
                if(position <= 2){
                    switch (position){
                        case 0:
                            packageManager = getPackageManager();
                            Intent intentSetting = packageManager.getLaunchIntentForPackage("com.android.settings"); //com.xx.xx是我们获取到的包名 
                            if(intentSetting!=null){
                                startActivity(intentSetting);
                                overridePendingTransition(R.anim.dialog_enter_anim, R.anim.dialog_exit_anim);
                            }else{
                                Toast.makeText(getApplicationContext(),R.string.settings_not_exit,Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case 1:
                            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                backgroundback = ((BitmapDrawable)getResources().getDrawable(R.mipmap.beijing4)).getBitmap();
                                background.setBackgroundDrawable(new BitmapDrawable(SaveBitmap.getInstance().resizeBitmap(
                                        backgroundback,ScreenUtil.getScreenWidth(getApplicationContext()),ScreenUtil.getScreenHeight(getApplicationContext()))));
                                Toast.makeText(getApplicationContext(),R.string.get_file_no_permission,Toast.LENGTH_SHORT).show();
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STRONGE);
                            }else {
                                currentIndex = Constant.BACKGROUND;
                                isMain = false;
                                setAnimation(Constant.BACKGROUND, layout_content, layout_background);
                            }
                            break;
                        case 2:
                            packageManager = getPackageManager();
                            Intent intent = packageManager.getLaunchIntentForPackage("com.android.vending"); //com.xx.xx是我们获取到的包名 
                            if(intent!=null){
                                startActivity(intent);
                                overridePendingTransition(R.anim.dialog_enter_anim, R.anim.dialog_exit_anim);
                            }else{
                                Toast.makeText(getApplicationContext(),R.string.google_play_not_exit,Toast.LENGTH_SHORT).show();
                            }
                            break;
                    }
                }else {
                    starIntent(urllist.get(position - 3));
                }
            }
        });

        video_urllist = new ArrayList<>();
        video_urllist.add("https://www.iqiyi.com/");
        video_urllist.add("https://www.spotify.com/");
        video_urllist.add("https://www.youku.com/");
        video_urllist.add("https://www.youtube.com/");
        int icno_video[] = { R.mipmap.logo_qiy,
                R.mipmap.logo_spotify,R.mipmap.logo_youku, R.mipmap.logo_youtube
        };
        String name_video[]={ getString(R.string.logo_qiy),
                getString(R.string.logo_spotify),getString(R.string.logo_youku)
                ,getString(R.string.logo_youtube)
        };
        mGridData = new ArrayList<>();
        for (int i=0; i<icno_video.length; i++) {
            GridItem item = new GridItem();
            item.setTitle(name_video[i]);
            item.setImage(getResources().getDrawable(icno_video[i]));
            mGridData.add(item);
        }
        gridViewAdapter_video = new GridViewAdapter(this, R.layout.gridview_item, mGridData);
        video_girdview.setAdapter(gridViewAdapter_video);
        video_girdview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /**获得Intent*/
                starIntent(video_urllist.get(position));
            }
        });


        news_urllist = new ArrayList<>();
        news_urllist.add("https://www.bbc.co.uk/news");
        news_urllist.add("https://www.cnn.com/");
        news_urllist.add("https://news.google.com/");
        news_urllist.add("https://www.economist.com/");
        int news_icno[] = { R.mipmap.logo_bbc,
                R.mipmap.logo_cnn,R.mipmap.logo_googlenews, R.mipmap.logo_economist
        };
        String news_name[]={ getString(R.string.bbc),
                getString(R.string.ccn),getString(R.string.google_news)
                ,getString(R.string.economist)
        };
        mGridData = new ArrayList<>();
        for (int i=0; i<news_icno.length; i++) {
            GridItem item = new GridItem();
            item.setTitle(news_name[i]);
            item.setImage(getResources().getDrawable(news_icno[i]));
            mGridData.add(item);
        }
        gridViewAdapter_news = new GridViewAdapter(this, R.layout.gridview_item, mGridData);
        news_girdview.setAdapter(gridViewAdapter_news);
        news_girdview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /**获得Intent*/
                starIntent(news_urllist.get(position));
            }
        });

        contact_urllist = new ArrayList<>();
        contact_urllist.add("https://www.weibo.com/");
        contact_urllist.add("https://www.facebook.com/");
        contact_urllist.add("https://www.instagram.com/");
        contact_urllist.add("https://twitter.com/");
        int constact_icno[] = { R.mipmap.logo_weibo,
                R.mipmap.logo_fb,R.mipmap.logo_ig, R.mipmap.logo_twitter
        };
        String constact_name[]={ getString(R.string.logo_weibo),
                getString(R.string.fb),getString(R.string.logo_ig)
                ,getString(R.string.logo_twitter)
        };
        mGridData = new ArrayList<>();
        for (int i=0; i<constact_icno.length; i++) {
            GridItem item = new GridItem();
            item.setTitle(constact_name[i]);
            item.setImage(getResources().getDrawable(constact_icno[i]));
            mGridData.add(item);
        }
        gridViewAdapter_contact = new GridViewAdapter(this, R.layout.gridview_item, mGridData);
        contact_girdview.setAdapter(gridViewAdapter_contact);
        contact_girdview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /**获得Intent*/
                starIntent(contact_urllist.get(position));
            }
        });

        message_urllist = new ArrayList<>();
        message_urllist.add("https://www.amazon.com/");
        message_urllist.add("https://www.mlb.com/");
        message_urllist.add("https://www.discovery.com/");
        message_urllist.add("https://www.nasa.gov/");
        message_urllist.add("https://www.nba.com/");
        message_urllist.add("https://www.ted.com/");
        int message[] = { R.mipmap.logo_amazon,
                R.mipmap.logo_mlb,R.mipmap.logo_discovery, R.mipmap.logo_nasa,
                R.mipmap.logo_nba,R.mipmap.logo_ted
        };
        String messagename[]={ getString(R.string.amazon),
                getString(R.string.logo_mlb),getString(R.string.discovery)
                ,getString(R.string.lnasa),getString(R.string.logo_nba)
                ,getString(R.string.logo_ted)
        };
        mGridData = new ArrayList<>();
        for (int i=0; i<message.length; i++) {
            GridItem item = new GridItem();
            item.setTitle(messagename[i]);
            item.setImage(getResources().getDrawable(message[i]));
            mGridData.add(item);
        }
        gridViewAdapter_message = new GridViewAdapter(this, R.layout.gridview_item, mGridData);
        message_girdview.setAdapter(gridViewAdapter_message);
        message_girdview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /**获得Intent*/
                starIntent(message_urllist.get(position));
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
//        BlurBuilder.snapShotWithoutStatusBar(this);
        switch (v.getId()){
            case R.id.message_layout:
//                myDialog=new MyDialog(MainActivity.this,R.style.MyDialog,Constant.MESSAGE,backgroundback,onClickListener);
//                myDialog.show();
                currentIndex = Constant.MESSAGE;
                isMain = false;
                setAnimation(Constant.MESSAGE,layout_cn,layout_message);
                break;
            case R.id.shipin:
                currentIndex = Constant.VIDEO;
                isMain = false;
                setAnimation(Constant.VIDEO,layout_cn,layout_video);
//                myDialog=new MyDialog(MainActivity.this,R.style.MyDialog,Constant.VIDEO, backgroundback,onClickListener);
//                myDialog.show();
                break;
            case R.id.contact:
                currentIndex = Constant.CONSTACT;
                isMain = false;
                setAnimation(Constant.CONSTACT,layout_cn,layout_contact);
//                myDialog=new MyDialog(MainActivity.this,R.style.MyDialog,Constant.CONSTACT,backgroundback,onClickListener);
//                myDialog.show();
                break;
            case R.id.settings:
                packageManager = getPackageManager();
                Intent intentSetting = packageManager.getLaunchIntentForPackage("com.android.settings"); //com.xx.xx是我们获取到的包名 
                if(intentSetting!=null){
                    startActivity(intentSetting);
                    overridePendingTransition(R.anim.dialog_enter_anim, R.anim.dialog_exit_anim);
                }else{
                    Toast.makeText(getApplicationContext(),R.string.settings_not_exit,Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.play:
                packageManager = getPackageManager();
                Intent intent = packageManager.getLaunchIntentForPackage("com.android.vending"); //com.xx.xx是我们获取到的包名 
                if(intent!=null){
                    startActivity(intent);
                    overridePendingTransition(R.anim.dialog_enter_anim, R.anim.dialog_exit_anim);
                }else{
                    Toast.makeText(getApplicationContext(),R.string.google_play_not_exit,Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.news_layout:
                currentIndex = Constant.NEW;
                isMain = false;
                setAnimation(Constant.NEW,layout_cn,layout_new);
//                myDialog=new MyDialog(MainActivity.this,R.style.MyDialog,Constant.NEW,backgroundback,onClickListener);
//                myDialog.show();
                break;
            case R.id.content:
                currentIndex = Constant.CONTENT;
                isMain = false;
                setAnimation(Constant.CONTENT,layout_cn,layout_content);
                break;
        }
    }

    public void starIntent(String url){
        intentUrl=new IntentUrl();
        intentUrl.setUrl(url);
        intent=new Intent(this,BrowserMainActivity.class);
        intent.putExtra("intentUrl",new Gson().toJson(intentUrl));
        startActivity(intent);
//        Uri uri = Uri.parse(url);
//        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//        startActivity(intent);
        overridePendingTransition(R.anim.dialog_enter_anim, R.anim.dialog_exit_anim);
    }

    @Override
    public void finish() {
        BlurBuilder.recycle();
        overridePendingTransition(R.anim.dialog_enter_anim, R.anim.dialog_exit_anim);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if(!isMain) {
                    isMain = true;
                    switch (currentIndex) {
                        case Constant.BACKGROUND:
                            setAnimation(Constant.VIDEO,layout_background, layout_content);
                            break;
                        case Constant.VIDEO:
                            setAnimation(Constant.VIDEO,layout_video, layout_cn);
                            break;
                        case Constant.CONTENT:
                            setAnimation(Constant.CONTENT,layout_content, layout_cn);
                            break;
                        case Constant.NEW:
                            setAnimation(Constant.NEW,layout_new, layout_cn);
                            break;
                        case Constant.MESSAGE:
                            setAnimation(Constant.MESSAGE,layout_message, layout_cn);
                            break;
                        case Constant.CONSTACT:
                            setAnimation(Constant.CONSTACT,layout_contact, layout_cn);
                            break;
                    }
                    currentIndex = -1;
                }
                return false;//拦截事件
            case KeyEvent.KEYCODE_MENU:
                break;
            case KeyEvent.KEYCODE_HOME:
                // 收不到
                break;
            case KeyEvent.KEYCODE_APP_SWITCH:
                // 收不到
                break;
            default:
                break;
        }

        return super.onKeyDown(keyCode, event);
    }

    private TranslateAnimation old_TA ;
    private TranslateAnimation new_TA;
    private AnimationSet old_AS;
    private AnimationSet new_AS;
    private AlphaAnimation old_AA;
    private AlphaAnimation new_AA;
    private ScaleAnimation old_SA;
    private ScaleAnimation new_SA;
    public void setAnimation(int Number,final LinearLayout layout_old,final LinearLayout layout_new){
        if(isMain){
            background.setLongClickable(true);
        }else{
            background.setLongClickable(false);
        }
        old_AS = new AnimationSet(true);
        new_AS = new AnimationSet(true);
        old_AS.setDuration(Constant.ANIMATIONTIME);
        new_AS.setDuration(Constant.ANIMATIONTIME);
        old_AA = new AlphaAnimation(1,0);
        old_AA.setDuration(Constant.ANIMATIONTIME);
        old_AS.addAnimation(old_AA);
        new_AA = new AlphaAnimation(0,1);
        new_AA.setDuration(Constant.ANIMATIONTIME);
        new_AS.addAnimation(new_AA);

//        old_SA = new ScaleAnimation(1, 0.1f, 1, 0.1f,Animation.ABSOLUTE,1f,Animation.ABSOLUTE,1f);
//        old_SA.setDuration(Constant.ANIMATIONTIME);
//        old_AS.addAnimation(old_SA);
//
//        new_SA = new ScaleAnimation(0.1f, 1, 0.1f, 1,Animation.ABSOLUTE,1f,Animation.ABSOLUTE,1f);
//        new_SA.setDuration(Constant.ANIMATIONTIME);
//        new_AS.addAnimation(new_SA);

        if(isMain) {
            switch (Number){
                case Constant.BACKGROUND:
                case Constant.VIDEO:
                    new_TA = new TranslateAnimation(ScreenUtil.getScreenWidth(this), 0, 0, 0);
                    old_TA = new TranslateAnimation(0, ScreenUtil.getScreenWidth(this) * -1, 0, 0);
                    break;
                case Constant.NEW:
                    new_TA = new TranslateAnimation(0, 0, ScreenUtil.getScreenHeight(this) , 0);
                    old_TA = new TranslateAnimation(0, 0, 0, ScreenUtil.getScreenHeight(this)* -1);
                    break;
                case Constant.MESSAGE:
                case Constant.CONSTACT:
                    new_TA = new TranslateAnimation(0, 0, ScreenUtil.getScreenHeight(this) * -1, 0);
                    old_TA = new TranslateAnimation(0, 0, 0, ScreenUtil.getScreenHeight(this) );
                    break;
                case Constant.CONTENT:
                    new_TA = new TranslateAnimation(ScreenUtil.getScreenWidth(this) * -1, 0, 0, 0);
                    old_TA = new TranslateAnimation(0, ScreenUtil.getScreenWidth(this), 0, 0);
                    break;
            }
        }else{
            switch (Number){
                case Constant.BACKGROUND:
                case Constant.VIDEO:
                    new_TA = new TranslateAnimation(ScreenUtil.getScreenWidth(this) * -1, 0, 0, 0);
                    old_TA = new TranslateAnimation(0, ScreenUtil.getScreenWidth(this), 0, 0);
                    break;
                case Constant.NEW:
                    new_TA = new TranslateAnimation(0, 0, ScreenUtil.getScreenHeight(this) * -1, 0);
                    old_TA = new TranslateAnimation(0, 0, 0, ScreenUtil.getScreenHeight(this));
                    break;
                case Constant.MESSAGE:
                case Constant.CONSTACT:
                    new_TA = new TranslateAnimation(0, 0, ScreenUtil.getScreenHeight(this), 0);
                    old_TA = new TranslateAnimation(0, 0, 0, ScreenUtil.getScreenHeight(this) * -1);
                    break;
                case Constant.CONTENT:
                    new_TA = new TranslateAnimation(ScreenUtil.getScreenWidth(this), 0, 0, 0);
                    old_TA = new TranslateAnimation(0, ScreenUtil.getScreenWidth(this) * -1, 0, 0);
                    break;
            }
        }
        old_TA.setDuration(Constant.ANIMATIONTIME);
        old_AS.addAnimation(old_TA);
        new_TA.setDuration(Constant.ANIMATIONTIME);
        new_AS.addAnimation(new_TA);

        layout_old.startAnimation(old_AS);

        old_AS.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                layout_new.setVisibility(View.VISIBLE);
                layout_new.startAnimation(new_AS);
            }
            @SuppressLint("NewApi")
            @Override
            public void onAnimationEnd(Animation animation) {
                layout_new.setVisibility(View.VISIBLE);
                layout_old.setVisibility(View.GONE);
//                if(!isMain){
//                    if(backgroundback != null) {
//                        background.setBackgroundDrawable(new BitmapDrawable(backgroundback));
//                    }else if(SaveBitmap.getBitmapFileName("backgroundbak")!=null){
//                        background.setBackgroundDrawable(new BitmapDrawable(SaveBitmap.getBitmapFileName("backgroundbak")));
//                    }else{
//                        background.setBackgroundResource(R.mipmap.beijing4);
//                    }
//                }else{
//                    background.setBackgroundResource(R.mipmap.beijing4);
//                }
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void showProgressDialog(String titile) {
        progressDialog = new ProgressDialog(MainActivity.this) ;
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL) ; //设置为水平进度条，当然你也可以设置为圆形
        progressDialog.setCancelable(false); //通过点击back是否可以退出dialog
        progressDialog.setCanceledOnTouchOutside(false); //通过点击外边是否可以diss
        progressDialog.setIcon(R.drawable.ic_launcher_background);
        progressDialog.setTitle(titile);//设置标题
        progressDialog.setMax(100);//设置到头的数值，这里就用100结束了
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() { //你懂的的点击方法
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        progressDialog.show();  //不能忘的最后一句show ，没有他上边再怎么写也是显示不出来滴！
    }

    public void updateProgressDialog(int value)
    {
        if(progressDialog!=null) {
            progressDialog.setProgress(value);
        }
    }

    public void hideProgressDialog() {
        if(progressDialog!=null)
            progressDialog.dismiss();
    }
}
