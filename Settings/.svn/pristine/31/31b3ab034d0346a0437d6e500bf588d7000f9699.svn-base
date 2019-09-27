package com.ctv.settings.device.viewHolder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

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
 * @author wanghang
 * @date 2019/09/17
 */
public class ThemeSytleViewHolder extends BaseViewHolder implements View.OnClickListener {


    private final static String TAG = ThemeSytleViewHolder.class.getCanonicalName();
    private ThemeStyleAdapter themeBgAdapter;
    private RecyclerView recyclerView;
    private List<ThemeStyleItem> list;

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
        recyclerView = activity.findViewById(R.id.rv_list);

    }

    /**
     * 初始化数据
     *
     * @param activity
     */
    @Override
    public void initData(Activity activity) {
        list = new ArrayList<ThemeStyleItem>();
        Log.d(TAG, "initData");
        String[] stringArray = mActivity.getResources().getStringArray(R.array.themeStyle);
        for (int i = 0; i < stringArray.length; i++) {
            list.add(new ThemeStyleItem(stringArray[i], R.mipmap.bg_theme_default,activity.getClass()));
        }
        themeBgAdapter = new ThemeStyleAdapter(R.layout.item_theme_style, list);
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

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildPosition(view) == 0)
                outRect.top = space;
        }
    }

    /**
     * 初始化监听
     */
    @Override
    public void initListener() {
        Log.d(TAG, "initListener");
        themeBgAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                // TODO: 2019-09-24
                Intent i = new Intent("com.android.device.THEME_SYTLE_ACTION");
                i.putExtra("data",position);
                mActivity.sendBroadcast(i);
                
                Intent intent = new Intent();
                String title = list.get(position).getTitle();
                Log.d(TAG, "onItemClick----->"+title);
                intent.putExtra(Constant.themeSytleData, title);
                mActivity.setResult(Constant.theme_style_request_code, intent);
                mActivity.finish();
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

    }


}
