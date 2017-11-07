package com.example.carl.orderdishes.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.example.carl.orderdishes.R;
import com.example.carl.orderdishes.adapter.FoodAllInfo;
import com.example.carl.orderdishes.adapter.IndexAdapter;
import com.example.carl.orderdishes.adapter.RecyclerAdapter;
import com.example.carl.orderdishes.entity.Food;
import com.example.carl.orderdishes.entity.FoodType;
import com.example.carl.orderdishes.entity.SuspensionDecoration;
import com.example.carl.orderdishes.util.Content;
import com.example.carl.orderdishes.util.TwitterRestClient;
import com.example.carl.orderdishes.view.CustomPopWindow;
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

public class FoodMenuActivity extends BaseActivity implements View.OnClickListener, Animation.AnimationListener {
    private int user_id, sohp_id;
    public static ImageView imageView;
    private ArrayList<FoodAllInfo> foodAllInfosList = new ArrayList<>();
    public static RelativeLayout relativeLayout;
    private IndexAdapter mIndexAdapter;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mTvHint.setVisibility(View.GONE);
        }
    };
    private List<Food> mList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private RecyclerAdapter mAdapter;
    private SuspensionDecoration mSuspensionDecoration;
    /**
     * 提示
     */
    private TextView mTvHint;
    private Handler mHandler = new Handler();
    private int mCurIndex;
    private List<String> list = new ArrayList<>();
    private Button count;
    private ListView mIndexView;
    private RelativeLayout relayout;
    private LinearLayout popupwindow;
    /**
     * 存索引
     */
    private Map<String, Integer> mMap;
    private ImageView view;
    private static Map<String,Integer> chooseFoodlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_footmenu);
        SharedPreferences sp = getSharedPreferences(Content.Model, 0);
        user_id = sp.getInt("user_id", 0);
        sohp_id = sp.getInt("sohp_id", 0);
        if (user_id == 0) {
            Toast.makeText(getApplicationContext(), "信息获取有误，请重新登录", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(FoodMenuActivity.this, LoginActivity.class));
            finish();
        }
        if (user_id != 0 && sohp_id != 0) {
        }
        setView();
        if (savedInstanceState != null) {
        }

    }

    private void refreshData(){
        mList = new ArrayList<>();
        mMap = new HashMap<>();
        list = new ArrayList<>();
        int ALL_SIZE = foodAllInfosList.size();
        for (int i = 0; i < ALL_SIZE; i++) {
            LOG_e(foodAllInfosList.get(i).getFoodType().getFtname());
            list.add(foodAllInfosList.get(i).getFoodType().getFtname());
            LOG_e(list.get(i));
            int FOOD_SIZE = foodAllInfosList.get(i).getFoolist().size();
            for(int j = 0; j < FOOD_SIZE; j++){
                Food food = foodAllInfosList.get(i).getFoolist().get(j);
                mList.add(food);
                mList.add(food);
                mList.add(food);
                mList.add(food);
            }
            if (!mMap.containsKey(foodAllInfosList.get(i).getFoodType().getFtname())) {
                mMap.put(foodAllInfosList.get(i).getFoodType().getFtname(), i * 4);
            }
        }
        mIndexAdapter.list = list;
        mIndexAdapter.notifyDataSetChanged();
        mAdapter.mList = mList;
        mAdapter.notifyDataSetChanged();
        if(mList.size()>0) {
            mSuspensionDecoration = new SuspensionDecoration(this, mList);
            mSuspensionDecoration.setOrien(SuspensionDecoration.Orien.VER);
            mRecyclerView.addItemDecoration(mSuspensionDecoration);
        }
        mIndexView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int index = mMap.get(list.get(position));
                LinearLayoutManager manager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                manager.scrollToPositionWithOffset(index, 0);
            }
        });
    }

    private void setView() {
        view = findViewById(R.id.view);
        view.setOnClickListener(this);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showPopListView();
                return true;
            }
        });
        popupwindow = findViewById(R.id.popupwindow);
        relayout = findViewById(R.id.midi);
        relayout.setOnClickListener(this);
        imageView = findViewById(R.id.shownumber);
        count = findViewById(R.id.count);
        count.setOnClickListener(this);
        imageView.setOnClickListener(this);
        mTvHint = findViewById(R.id.tv_hint);
        mIndexView = findViewById(R.id.indexView);
        mRecyclerView = findViewById(R.id.recycleView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        relativeLayout = findViewById(R.id.relayout);
        mIndexAdapter = new IndexAdapter(this,list);
        mIndexView.setAdapter(mIndexAdapter);
        mAdapter = new RecyclerAdapter(this, mList);
        mRecyclerView.setAdapter(mAdapter);
        //加标题
        if(mList.size()>0) {
            mSuspensionDecoration = new SuspensionDecoration(this, mList);
            mSuspensionDecoration.setOrien(SuspensionDecoration.Orien.VER);
            mRecyclerView.addItemDecoration(mSuspensionDecoration);
        }
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int pos = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                if (!mList.get(pos).getFtname().equals(mList.get(mCurIndex).getFtname())) {
                    mHandler.removeCallbacks(mRunnable);
                    mTvHint.setVisibility(View.VISIBLE);
                    mTvHint.setText(mList.get(pos).getFtname());
//                    mIndexView.setIndexStr(mList.get(pos).getTag());
                    mHandler.postDelayed(mRunnable, 1000);
                }
                mCurIndex = pos;
            }
        });
        chooseFoodlist = new HashMap<>();
    }

    @Override
    protected void onStart() {
        super.onStart ();
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
                    refreshData();
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


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.shownumber:
                showPopListView();
                Toast.makeText(getApplicationContext(),"11",Toast.LENGTH_SHORT).show();
                break;
            case R.id.count:
                break;
            case R.id.relayout:
                if(flag) {
                    showPopListView();
                }
                break;
            case R.id.view:
                if(flag) {
                    showPopListView();
                }else{
                    view.setVisibility(View.GONE);
                }
                break;
            default:break;
        }
    }

    private String[] name = { "剑萧舞蝶", "张三", "hello", "诗情画意" };
    private String[] desc = { "魔域玩家", "百家执行", "高级的富一代", "妹子请过来..一个善于跑妹子的" };
    private TranslateAnimation translateAnimation;
    private static boolean flag = false,IsFinish = true;
    private void showPopListView(){
        handleListView();
        if(IsFinish) {
            IsFinish = false;
            if (flag) {
                translateAnimation = new TranslateAnimation(Animation.ABSOLUTE, 0,
                        Animation.ABSOLUTE, 0.0f,
                        Animation.ABSOLUTE, 0.0f,
                        Animation.ABSOLUTE, popupwindow.getHeight()
                );
                translateAnimation.setDuration(300);
                translateAnimation.setFillAfter(true);
                translateAnimation.setAnimationListener(FoodMenuActivity.this);
            } else {
//                WindowManager.LayoutParams lp = getWindow().getAttributes();
//                lp.alpha = 0.7f;
//                getWindow().setAttributes(lp);
                view.setAlpha(0.5f);
                view.setVisibility(View.VISIBLE);
                view.setClickable(false);
                popupwindow.setVisibility(View.VISIBLE);
                translateAnimation = new TranslateAnimation(Animation.ABSOLUTE, 0,
                        Animation.ABSOLUTE, 0.0f,
                        Animation.ABSOLUTE, popupwindow.getHeight(),
                        Animation.ABSOLUTE, 0.0f
                );
                translateAnimation.setDuration(300);
                translateAnimation.setFillAfter(true);
                translateAnimation.setAnimationListener(FoodMenuActivity.this);
            }
            popupwindow.startAnimation(translateAnimation);
        }
    }
    private void handleListView(){
        ListView listview = findViewById(R.id.shoplist);
        List<Map<String, Object>> listems = new ArrayList<>();
        for (int i = 0; i < name.length; i++) {
            Map<String, Object> listem = new HashMap<>();
            listem.put("name", name[i]);
            listem.put("desc", desc[i]);
            listems.add(listem);
            listems.add(listem);
        }
        SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(),listems,R.layout.item_layout, new String[] { "name", "desc"},
                new int[] {R.id.tv,R.id.tv1});
        listview.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onAnimationStart(Animation animation) {
//        if(flag){
//            popupwindow.setVisibility(View.VISIBLE);
//        }
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        popupwindow.clearAnimation();
        if(flag){
            flag = false;
            popupwindow.setVisibility(View.INVISIBLE);
            view.setVisibility(View.INVISIBLE);
        }else{
            flag = true;
        }
        IsFinish = true;

    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }
}
