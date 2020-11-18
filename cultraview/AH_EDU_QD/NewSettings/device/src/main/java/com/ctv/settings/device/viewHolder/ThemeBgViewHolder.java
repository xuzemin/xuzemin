package com.ctv.settings.device.viewHolder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ctv.settings.base.BaseViewHolder;
import com.ctv.settings.device.adapter.ThemeBgAdapter;
import com.ctv.settings.device.bean.ThemeBgItem;
import com.ctv.settings.device.bean.ThemeStyleItem;
import com.ctv.settings.device.data.Constant;
import com.ctv.settings.security.R;
import com.ctv.settings.utils.L;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * 主题ViewHolder
 *
 * @author minqingkang
 * @date 2019/09/17
 */
public class ThemeBgViewHolder extends BaseViewHolder implements View.OnClickListener {


    private final static String TAG = ThemeBgViewHolder.class.getCanonicalName();
    private ThemeBgAdapter themeBgAdapter;
    private RecyclerView recyclerView;
    private List<ThemeBgItem> list;
    private Button btnSave, btnCancel;

    public ThemeBgViewHolder(Activity activity) {
        super(activity);
    }

    /**
     * 初始化UI
     *
     * @param activity
     */
    @Override
    public void initUI(Activity activity) {
        recyclerView = (RecyclerView) activity.findViewById(R.id.rv_list);
        btnSave = (Button) activity.findViewById(R.id.btn_save);
        btnCancel = (Button) activity.findViewById(R.id.btn_cancel);
        ((TextView) activity.findViewById(R.id.back_title)).setText(mActivity.getString(R.string.item_theme_background));
        ((ImageView) activity.findViewById(R.id.back_btn)).setOnClickListener(this);
    }

    /**
     * 初始化数据
     *
     * @param activity
     */
    @Override
    public void initData(Activity activity) {
        list = new ArrayList<ThemeBgItem>();
        Log.d(TAG, "initData");

        String[] stringArray = mActivity.getResources().getStringArray(R.array.themeBg);
        int[] resImg = {R.mipmap.bg_theme_default_small, R.mipmap.bg_theme_qipao_small, R.mipmap.bg_theme_jingshu_small,
                R.mipmap.bg_theme_yemo_small, R.mipmap.bg_theme_jianjie_small, R.mipmap.bg_theme_xingkong_small};
        for (int i = 0; i < stringArray.length; i++) {
            list.add(new ThemeBgItem(stringArray[i], resImg[i], activity.getClass()));

        }
        themeBgAdapter = new ThemeBgAdapter(R.layout.item_theme_style, list);
        SharedPreferences sharedPreferences = mActivity.getSharedPreferences("data", Context.MODE_PRIVATE);
        themeBgAdapter.setLastPosition(sharedPreferences.getInt("bgIndex", 0));
        recyclerView.addItemDecoration(new SpacesItemDecoration(20));
        recyclerView.setAdapter(themeBgAdapter);
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;
            outRect.top = space;
//            // Add top margin only for the first item to avoid double space between items
//            if (parent.getChildPosition(view) == 0)
//                outRect.top = space;
        }
    }

    /**
     * 初始化监听
     */
    @Override
    public void initListener() {
        Log.d(TAG, "initListener");


        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);


        themeBgAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {


                themeBgAdapter.setLastPosition(position);
                themeBgAdapter.notifyDataSetChanged();


            }
        });
    }

    /**
     * 刷新指定view
     *
     * @param view
     */
    @Override
    public void refreshUI(View view) {
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        L.d(TAG, "mOnClickListener view.id->%s", id);
        if (id == R.id.btn_cancel) {
            mActivity.finish();
        } else if (id == R.id.btn_save) {// TODO: 2019-09-24

            try {
                WallpaperManager wallpaperManager = (WallpaperManager) mActivity.getSystemService(
                        Context.WALLPAPER_SERVICE);

                setWallResource(wallpaperManager, themeBgAdapter.getLastPosition());

                SharedPreferences sharedPreferences = mActivity.getSharedPreferences("data", Context.MODE_PRIVATE);
                //步骤2： 实例化SharedPreferences.Editor对象
                SharedPreferences.Editor editor = sharedPreferences.edit();
                //步骤3：将获取过来的值放入文件
                editor.putInt("bgIndex", themeBgAdapter.getLastPosition());
                //步骤4：提交
                editor.commit();


                Intent i = new Intent("com.android.device.THEME_BG_ACTION");
                i.putExtra("data", themeBgAdapter.getLastPosition());
                mActivity.sendBroadcast(i);


                Intent intent = new Intent();
                String title = list.get(themeBgAdapter.getLastPosition()).getTitle();
                Log.d(TAG, "onItemClick----->" + title);
                intent.putExtra(Constant.themeBgData, title);
                mActivity.setResult(Constant.theme_bg_request_code, intent);
                mActivity.finish();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.back_btn) {
            mActivity.finish();
        }

    }

    /**
     * 设置壁纸背景
     *
     * @param wallpaperManager
     * @param lastPosition
     */
    @SuppressLint("ResourceType")
    private void setWallResource(WallpaperManager wallpaperManager, int lastPosition) throws IOException {
        L.d("qkmin---->setWallResource" + lastPosition);

        switch (lastPosition) {
            case 2:
                wallpaperManager.setResource(R.mipmap.bg_theme_jingshu);
                break;
            case 0:
                wallpaperManager.setResource(R.mipmap.bg_theme_default);
                break;
            case 1:
                wallpaperManager.setResource(R.mipmap.bg_theme_qipao);
                break;
            case 3:
                wallpaperManager.setResource(R.mipmap.bg_theme_yemo);
                break;
            case 4:
                wallpaperManager.setResource(R.mipmap.bg_theme_jianjie);
                break;
            case 5:
                wallpaperManager.setResource(R.mipmap.bg_theme_xingkong);
                break;
        }
    }
}
