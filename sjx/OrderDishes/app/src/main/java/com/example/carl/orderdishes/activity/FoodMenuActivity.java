package com.example.carl.orderdishes.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.Toast;
import com.example.carl.orderdishes.R;
import com.example.carl.orderdishes.adapter.FoodAdapter;
import com.example.carl.orderdishes.entity.Food;
import com.example.carl.orderdishes.util.Content;
import com.example.carl.orderdishes.util.TwitterRestClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Carl on 2017/10/31.
 *
 */

public class FoodMenuActivity extends BaseActivity{
    private int user_id,sohp_id;
    private GridView food;
    private FoodAdapter foodAdapter;
    private List<Food> Foolist = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_footmenu);

        SharedPreferences sp = getSharedPreferences(Content.Model,0);
        user_id = sp.getInt("user_id", 0);
        sohp_id =  sp.getInt("sohp_id", 0);

//        LOG_e(user_id+""+sohp_id+"");
        if(user_id!=0&&sohp_id!=0) {
            refreashFood();
        }

        if(user_id == 0){
            Toast.makeText(getApplicationContext(),"信息获取有误，请重新登录",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(FoodMenuActivity.this,LoginActivity.class));
            finish();
        }

        food = findViewById(R.id.food);
        foodAdapter = new FoodAdapter(this,Foolist);
        food.setAdapter(foodAdapter);
    }

    private void refreashFood() {
        String url = "FoodInfoServlet";
        RequestParams requestParams = new RequestParams();
        requestParams.add("sohpid", String.valueOf(sohp_id));
        requestParams.add("userid", String.valueOf(user_id));

        TwitterRestClient.post(url, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, org.apache.http.Header[] headers, byte[] responseBody) {
                try {
                    JSONObject jsonObject = new JSONObject(new String(responseBody));
                    int Result = jsonObject.getInt("ResultCode");
                    if(Result == Content.OK){
                        String array = jsonObject.getString("foodinfo");
                        JSONArray jsonArray = new JSONArray(array);
                        if(jsonArray!=null && jsonArray.length() >0){
                            Foolist = new ArrayList<>();
                            for(int i = 0 ,size = jsonArray.length();i<size;i++){
                                Food food  = new Food();
                                JSONObject json = (JSONObject) jsonArray.get(i);
                                food.setFid(json.getInt("fid"));
                                food.setFtname(json.getString("ftname"));
                                food.setFprice(json.getInt("fprice"));
                                food.setFvipprice(json.getInt("fvipprice"));
                                food.setFname(json.getString("fname"));
                                food.setFimgurl(json.getString("fimgurl"));
                                Foolist.add(food);
                            }
                        }
                    }
                    LOG_e(Foolist.toString());
                    LOG_e(Foolist.size()+"");

                    if (Foolist != null && Foolist.size() > 0) {
                        foodAdapter.list = Foolist;
                        foodAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"数据解析有误，请重试",Toast.LENGTH_SHORT).show();
                }
                hideProgress();
            }



            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(),"连接失败，请检查网络",Toast.LENGTH_SHORT).show();
                hideProgress();
            }
        });
        showProgress();
    }
}
