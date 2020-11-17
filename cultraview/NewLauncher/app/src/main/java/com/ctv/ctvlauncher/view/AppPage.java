package com.ctv.ctvlauncher.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import com.ctv.ctvlauncher.R;
import com.ctv.ctvlauncher.adapter.AppPageAdapter;
import com.ctv.ctvlauncher.bean.AppPageBean;
import com.ctv.ctvlauncher.compat.LauncherActivityInfoCompat;
import com.ctv.ctvlauncher.fragment.FragmentAppGride;
import com.ctv.ctvlauncher.utils.AnimClickUtil;

public class AppPage extends GridLayout implements View.OnClickListener {
    private static AppPageBean appBeanList;
    private static OnPageItemClick onPageItemClick;
    private static OnLongClickListener onLongClickListener;
    private OnFocusChangeListener onFocusChangeListener;
    private Context context;
    private boolean isDown = false;
    private final static int EVENT_DOWN = 0;
    private final static int EVENT_UP = 1;
    private final static int EVENT_DOWN_LONG = 2;
    private View view;
    private long time_donw = 0;
    private final Handler handler = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case EVENT_DOWN:
                    time_donw = System.currentTimeMillis();
                    handler.sendEmptyMessageDelayed(EVENT_DOWN_LONG,300);
                    break;
                case EVENT_UP:
                    long CurrentTime = System.currentTimeMillis();
                    if(CurrentTime - time_donw < 300){
                        handler.removeMessages(EVENT_DOWN_LONG);
                        LauncherActivityInfoCompat launcherActivityInfoCompat = appBeanList.get((Integer) view.getTag());
                        if (onPageItemClick != null) {
                            onPageItemClick.onPageItemClicked(launcherActivityInfoCompat);
                        }
                    }
                    break;
                case EVENT_DOWN_LONG:
                    time_donw = System.currentTimeMillis();
                    onLongClickListener.onLongClick(view);
                    break;
                default:
                    break;
            }
        }
    };
    public interface OnPageItemClick {
        void onPageItemClicked(LauncherActivityInfoCompat launcherActivityInfoCompat);
    }
    public AppPage(Context context, OnFocusChangeListener onFocusChangeListener, OnLongClickListener onLongClickListener) {
        super(context);
        this.context = context;
        this.onFocusChangeListener = onFocusChangeListener;
        AppPage.onLongClickListener = onLongClickListener;
    }

    public AppPage(Context context, AttributeSet attrs,OnFocusChangeListener onFocusChangeListener) {
        super(context, attrs);
        this.context = context;
        this.onFocusChangeListener = onFocusChangeListener;
    }

    public AppPage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public AppPage(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
    }
    @SuppressLint("WrongConstant")
    public void setAppBeanList(AppPageBean appPageBean) {
        appBeanList = appPageBean;
        updateView();
    }

    public void updateView(){
        int i = 0;
        int childCount;
        if (appBeanList == null) {
            childCount = getChildCount();
            while (i < childCount) {
                getChildAt(i).setVisibility(INVISIBLE);
                i++;
            }
            return;
        }
        childCount = getRowCount();
        int columnCount = getColumnCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            for (int i3 = 0; i3 < columnCount; i3++) {
                int i4 = (i2 * columnCount) + i3;
                if (appBeanList.getCount() <= i4) {
                    if (i4 < getChildCount()) {
                        getChildAt(i4).setVisibility(INVISIBLE);
                    }
                    return;
                }
                View childAt;
                LauncherActivityInfoCompat launcherActivityInfoCompat = appBeanList.get(i4);
                String packageName = launcherActivityInfoCompat.getComponentName().getPackageName();
                Log.d("APPpage", "setAppBeanList: packageName ="+packageName);
                if (i4 < getChildCount()) {
                    childAt = getChildAt(i4);
                } else {
                    childAt = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.item_app, null);
                    LayoutParams layoutParams = new LayoutParams(GridLayout.spec(i2), GridLayout.spec(i3));
                    layoutParams.width = FragmentAppGride.GRID_ITEM_WIDTH;
                    layoutParams.height = FragmentAppGride.GRID_ITEM_HEIGHT;
                    addView(childAt, layoutParams);
                }
                childAt.setVisibility(VISIBLE);
                final View findViewById = childAt.findViewById(R.id.button_item);
                findViewById.setTag(i4);
                findViewById.setOnClickListener(this);
                findViewById.setOnFocusChangeListener(onFocusChangeListener);
                findViewById.setOnLongClickListener(onLongClickListener);
                final AnimRelativeLayout anim = childAt.findViewById(R.id.anim);
                anim.setDownEndListener(new AnimClickUtil.OnAnimEndListener() {
                    @Override
                    public void onDownEnd() {
                        view = findViewById;
                        handler.sendEmptyMessage(EVENT_DOWN);
                    }

                    @Override
                    public void onUpEnd() {
                        view = findViewById;
                        handler.sendEmptyMessage(EVENT_UP);
                    }
                });

                TextView textView = childAt.findViewById(R.id.textview_appname);
                View imageview_appicon_delete = childAt.findViewById(R.id.imageview_appicon_delete);
                if(AppPageAdapter.isDelete){
                    imageview_appicon_delete.setVisibility(VISIBLE);
                }else{
                    imageview_appicon_delete.setVisibility(GONE);
                }
                childAt = childAt.findViewById(R.id.imageview_appicon);
                if (packageName.equals("com.mysher.mtalk")){
                    childAt.setBackgroundResource(R.mipmap.bt_video_normal);
                    textView.setText(R.string.video_phone);
                } else if (packageName.equals("com.jxw.launcher")){
                    childAt.setBackgroundResource(R.drawable.wiedu);
                    textView.setText(R.string.wiedu);
                }else if (packageName.equals("com.example.newmagnifier")){
                    childAt.setBackgroundResource(R.drawable.fangda);
                    textView.setText(launcherActivityInfoCompat.getLabel());
                } else if (packageName.equals("com.android.calculator2")){
                    childAt.setBackgroundResource(R.drawable.bt_calculater_normal);
                    textView.setText(launcherActivityInfoCompat.getLabel());
                }else if (packageName.equals("com.xingen.camera")){
                    childAt.setBackgroundResource(R.drawable.takephoto);
                    textView.setText(R.string.camera);
                }else if (packageName.equals("cn.wps.moffice_eng")){
                    childAt.setBackgroundResource(R.drawable.bt_wps_normal);
                    textView.setText(launcherActivityInfoCompat.getLabel());
                }else if (packageName.equals("com.dazzle.timer")){
                    childAt.setBackgroundResource(R.drawable.tmier);
                    textView.setText(launcherActivityInfoCompat.getLabel());
                }else if (packageName.equals("com.dazzlewisdom.screenrec")){
                    childAt.setBackgroundResource(R.drawable.screenboard);
                    textView.setText(launcherActivityInfoCompat.getLabel());
                }else if(packageName.equals("com.android.toofifi")){
                    childAt.setBackgroundResource(R.drawable.feitu_dispaly);
                    textView.setText(launcherActivityInfoCompat.getLabel());
                }else if (packageName.equals("com.tencent.androidqqmail")){
                    childAt.setBackgroundResource(R.mipmap.qqyouxiang);
                    textView.setText(R.string.qqemail);
                }
                else {
                    childAt.setBackground(launcherActivityInfoCompat.getIcon(240));
                    textView.setText(launcherActivityInfoCompat.getLabel());
                }

            }
        }
    }

    @Override
    public void onClick(View v) {
        LauncherActivityInfoCompat launcherActivityInfoCompat = appBeanList.get((Integer) v.getTag());
        if (onPageItemClick != null) {
            onPageItemClick.onPageItemClicked(launcherActivityInfoCompat);
        }
    }
    public void setOnPageItemClick(OnPageItemClick onPageItemClick) {
        AppPage.onPageItemClick = onPageItemClick;
    }

}
