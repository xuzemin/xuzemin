package com.example.carl.orderdishes.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carl.orderdishes.R;
import com.example.carl.orderdishes.adapter.FoodAllInfo;
import com.example.carl.orderdishes.adapter.RecyclerAdapter;
import com.example.carl.orderdishes.entity.Food;
import com.example.carl.orderdishes.entity.FoodType;
import com.example.carl.orderdishes.entity.Mode;
import com.example.carl.orderdishes.entity.SuspensionDecoration;
import com.example.carl.orderdishes.util.Content;
import com.example.carl.orderdishes.util.TwitterRestClient;
import com.example.carl.orderdishes.view.IndexView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Carl on 2017/10/31.
 *
 */

public class FoodMenuActivity extends BaseActivity {
    private int user_id, sohp_id;
    public static ImageView imageView;
    private ArrayList<FoodAllInfo> foodAllInfosList = new ArrayList<>();
    public static RelativeLayout relativeLayout;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mTvHint.setVisibility(View.GONE);
        }
    };


    private List<Mode> mList = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private RecyclerAdapter mAdapter;

    private SuspensionDecoration mSuspensionDecoration;
    /**
     * 提示
     */
    private TextView mTvHint;

    private Handler mHandler = new Handler();

    private int mCurIndex;

    private IndexView mIndexView;

    private String[] str = new String[]{"A", "B", "C", "D", "E", "F", "G",
            "H", "I", "J", "K", "L", "M", "N",
            "O", "P", "Q", "R", "S", "T", "U",
            "V", "W", "X", "Y", "Z", "#"};
    /**
     * 存索引
     */
    private Map<String, Integer> mMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_footmenu);


        setView();

        SharedPreferences sp = getSharedPreferences(Content.Model, 0);
        user_id = sp.getInt("user_id", 0);
        sohp_id = sp.getInt("sohp_id", 0);
        if (user_id != 0 && sohp_id != 0) {
        }

        if (user_id == 0) {
            Toast.makeText(getApplicationContext(), "信息获取有误，请重新登录", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(FoodMenuActivity.this, LoginActivity.class));
            finish();
        } else {
            setView();
        }
        if (savedInstanceState != null) {
        }

    }

    private void setView() {
        imageView = findViewById(R.id.shownumber);
        mTvHint = findViewById(R.id.tv_hint);
        mIndexView = findViewById(R.id.indexView);
        mRecyclerView = findViewById(R.id.recycleView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        relativeLayout = findViewById(R.id.relayout);

//        refreshData();


        mList = new ArrayList<Mode>();
        mMap = new HashMap<>();

        for (int i = 0; i < str.length; i++) {
            Mode mode = new Mode();
            mode.setName("title" + i);
            mode.setTitle("title" + i);
            mode.setTag(str[i]);
            mList.add(mode);

            mode = new Mode();
            mode.setName("title" + i + 1);
            mode.setTitle("title" + i);
            mode.setTag(str[i]);
            mList.add(mode);

            if (!mMap.containsKey(str[i])) {
                mMap.put(str[i], i * 2);
            }
        }

        mAdapter = new RecyclerAdapter(this, mList);
        mRecyclerView.setAdapter(mAdapter);
        //加标题
        mSuspensionDecoration = new SuspensionDecoration(this, mList);
        mSuspensionDecoration.setOrien(SuspensionDecoration.Orien.VER);
        mRecyclerView.addItemDecoration(mSuspensionDecoration);

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int pos = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                Log.i("aaa", pos + "  " + mList.get(pos).getTag());

                if (!mList.get(pos).getTag().equals(mList.get(mCurIndex).getTag())) {
                    mHandler.removeCallbacks(mRunnable);
                    mTvHint.setVisibility(View.VISIBLE);
                    mTvHint.setText(mList.get(pos).getTag());
                    mIndexView.setIndexStr(mList.get(pos).getTag());
                    mHandler.postDelayed(mRunnable, 1000);
                }
                mCurIndex = pos;
            }
        });

        mIndexView.setListener(new IndexView.ScrollListener() {
            @Override
            public void backDownString(String str, int pos) {
                pos = mMap.get(str);
                Log.i("aaa", pos + "  "  + str);
                //这个有bug，在屏幕内不会移动
                //mRecyclerView.smoothScrollToPosition(pos);
                LinearLayoutManager manager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                manager.scrollToPositionWithOffset(pos, 0);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreashFoodType();
    }


    private void refreashFoodType() {
        String url = "FoodTypeInfoServlet";
        RequestParams requestParams = new RequestParams();
        requestParams.add("sohpid", String.valueOf(sohp_id));
        requestParams.add("userid", String.valueOf(user_id));

        TwitterRestClient.post(url, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, org.apache.http.Header[] headers, byte[] responseBody) {
                try {
                    JSONObject jsonObject = new JSONObject(new String(responseBody));
                    int Result = jsonObject.getInt("ResultCode");
                    if (Result == Content.OK) {
                        String foodtypeinfo = jsonObject.getString("foodtypeinfo");
                        JSONArray jsonArray = new JSONArray(foodtypeinfo);
                        for (int i = 0, size = jsonArray.length(); i < size; i++) {
                            FoodAllInfo foodAllInfo = new FoodAllInfo();
                            JSONObject json = (JSONObject) jsonArray.get(i);
                            foodAllInfo.setFoodType(new FoodType(json.getInt("ftid"), json.getString("ftname")));

                            String arrayFoodList = json.getString("foodlist");
                            JSONArray jsonArrayFoodList = new JSONArray(arrayFoodList);

                            for (int j = 0, sizefood = jsonArrayFoodList.length(); j < sizefood; j++) {
                                Food food = new Food();
                                JSONObject jsonfood = (JSONObject) jsonArrayFoodList.get(j);
                                food.setFid(jsonfood.getInt("fid"));
                                food.setFtname(jsonfood.getString("ftname"));
                                food.setFprice(jsonfood.getInt("fprice"));
                                food.setFvipprice(jsonfood.getInt("fvipprice"));
                                food.setFname(jsonfood.getString("fname"));
                                food.setFimgurl(jsonfood.getString("fimgurl"));
                                foodAllInfo.getFoolist().add(food);
                            }
                            foodAllInfosList.add(foodAllInfo);
                        }
                    }
                    LOG_e(foodAllInfosList.toString());
                    LOG_e(foodAllInfosList.size() + "");
//                    refreshData();
//                    mAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "数据解析有误，请重试", Toast.LENGTH_SHORT).show();
                }
                hideProgress();
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(), "连接失败，请检查网络", Toast.LENGTH_SHORT).show();
                hideProgress();
            }
        });
        showProgress();
    }


}
