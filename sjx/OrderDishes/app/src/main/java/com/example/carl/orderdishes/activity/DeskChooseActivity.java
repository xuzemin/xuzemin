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
import com.example.carl.orderdishes.adapter.DeskChooseAdapter;
import com.example.carl.orderdishes.entity.Desk;
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
 * Created by Carl on 2017/10/30.
 *
 */

public class DeskChooseActivity extends BaseActivity {
    private GridView deskView;
    private DeskChooseAdapter desk_adapter;
    private int user_id,sohp_id;
    private static List<Desk> Desklist = new ArrayList<>();
    // 桌面数据列
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_deskchoose);

        deskView = this.findViewById(R.id.deskchoose);
        //获取数据
        desk_adapter = new DeskChooseAdapter(this, Desklist);
        // 初始化桌面列表
        deskView.setAdapter(desk_adapter);

        SharedPreferences sp = getSharedPreferences(Content.Model,0);
        user_id = sp.getInt("user_id", 0);
        sohp_id =  sp.getInt("sohp_id", 0);

        LOG_e(user_id+""+sohp_id+"");
        if(user_id!=0&&sohp_id!=0) {
            refreshDeskInfo();
        }

        if(user_id == 0){
            Toast.makeText(getApplicationContext(),"信息获取有误，请重新登录",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(DeskChooseActivity.this,LoginActivity.class));
            finish();
        }
    }



    private void refreshDeskInfo(){
        String url = "DeskInfoServlet";
        RequestParams requestParams = new RequestParams();
        requestParams.add("sohpid", String.valueOf(sohp_id));
        requestParams.add("userid", String.valueOf(user_id));

        TwitterRestClient.get(url, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, org.apache.http.Header[] headers, byte[] responseBody) {
                try {
                    JSONObject jsonObject = new JSONObject(new String(responseBody));
                    int Result = jsonObject.getInt("ResultCode");
                    if(Result == Content.OK){
                        String array = jsonObject.getString("deskinfo");
                        JSONArray jsonArray = new JSONArray(array);
                        if(jsonArray!=null && jsonArray.length() >0){
                            Desklist = new ArrayList<>();
                            for(int i = 0 ,size = jsonArray.length();i<size;i++){
                                Desk desk  = new Desk();
                                JSONObject json = (JSONObject) jsonArray.get(i);
                                desk.setDid(json.getInt("did"));
                                desk.setDname(json.getString("dname"));
                                desk.setDstatus(json.getString("dstatus"));
                                desk.setDdate(json.getString("ddate"));
                                Desklist.add(desk);
                            }
                        }
                    }
                    LOG_e(Desklist.toString());
                    LOG_e(Desklist.size()+"");
                    if (Desklist != null && Desklist.size() > 0) {
                        desk_adapter.list = Desklist;
                        desk_adapter.notifyDataSetChanged();
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
