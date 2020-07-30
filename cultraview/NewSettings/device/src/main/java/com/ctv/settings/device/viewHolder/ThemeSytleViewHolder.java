package com.ctv.settings.device.viewHolder;

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
import com.ctv.settings.device.adapter.ThemeStyleAdapter;
import com.ctv.settings.device.bean.ThemeBgItem;
import com.ctv.settings.device.bean.ThemeStyleItem;
import com.ctv.settings.device.data.Constant;
import com.ctv.settings.security.R;
import com.ctv.settings.utils.L;

import java.util.ArrayList;
import java.util.List;


/**
 * 主题ViewHolder
 *
 * @author minqingkang
 * @date 2019/09/17
 */
public class ThemeSytleViewHolder extends BaseViewHolder implements View.OnClickListener {


    private final static String TAG = ThemeSytleViewHolder.class.getCanonicalName();
    private ThemeStyleAdapter themeBgAdapter;
    private RecyclerView recyclerView;
    private List<ThemeBgItem> list;
    private Button btnSave, btnCancel;

    public ThemeSytleViewHolder(Activity activity) {
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
        ((TextView) activity.findViewById(R.id.back_title)).setText(mActivity.getString(R.string.item_theme_style));
        ((ImageView) activity.findViewById(R.id.back_btn)).setOnClickListener(this);
        btnSave = (Button) activity.findViewById(R.id.btn_save);
        btnCancel = (Button) activity.findViewById(R.id.btn_cancel);

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
        String[] stringArray = mActivity.getResources().getStringArray(R.array.themeStyle);
        int[] resImg = {R.mipmap.ic_theme_one_small, R.mipmap.ic_theme_two_small};
        for (int i = 0; i < stringArray.length; i++) {
            list.add(new ThemeBgItem(stringArray[i], resImg[i], activity.getClass()));
        }
        themeBgAdapter = new ThemeStyleAdapter(R.layout.item_theme_style, list);
        recyclerView.addItemDecoration(new SpacesItemDecoration(20));
        recyclerView.setAdapter(themeBgAdapter);
        SharedPreferences sharedPreferences = mActivity.getSharedPreferences("data", Context.MODE_PRIVATE);
        int styleIndex = sharedPreferences.getInt("styleIndex", 0);
        themeBgAdapter.setLastPosition(styleIndex);

    }

    /**
     * 间隙
     */
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
        if (id == R.id.back_btn || id == R.id.btn_cancel) {
            mActivity.finish();
        } else if (id == R.id.btn_save) {// TODO: 2019-09-24

            SharedPreferences sharedPreferences = mActivity.getSharedPreferences("data", Context.MODE_PRIVATE);
            //步骤2： 实例化SharedPreferences.Editor对象
            SharedPreferences.Editor editor = sharedPreferences.edit();
            //步骤3：将获取过来的值放入文件
            editor.putInt("styleIndex", themeBgAdapter.getLastPosition());
            //步骤4：提交
            editor.commit();
            Intent i = new Intent("com.android.device.THEME_SYTLE_ACTION");
            i.putExtra("data", themeBgAdapter.getLastPosition());
            mActivity.sendBroadcast(i);


            Intent intent = new Intent();
            String title = list.get(themeBgAdapter.getLastPosition()).getTitle();
            Log.d(TAG, "onItemClick----->" + title);
            intent.putExtra(Constant.themeBgData, title);
            mActivity.setResult(Constant.theme_style_request_code, intent);
            mActivity.finish();
        }
    }


}
