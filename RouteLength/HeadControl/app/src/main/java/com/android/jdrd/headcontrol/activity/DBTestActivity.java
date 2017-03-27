package com.android.jdrd.headcontrol.activity;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.jdrd.headcontrol.R;
import com.android.jdrd.headcontrol.database.DBHelper;
import com.android.jdrd.headcontrol.database.HeadControlBean;
import com.android.jdrd.headcontrol.database.HeadControlDao;
import com.android.jdrd.headcontrol.util.Constant;

public class DBTestActivity extends Activity implements View.OnClickListener {

    private Context mContext;
    private Button bt_add;
    private Button bt_del;
    private Button bt_update;
    private Button bt_query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbtest);
        mContext = this;
        initView();
        initDB();
    }

    private void initView() {
        bt_add = (Button) findViewById(R.id.add);
        bt_del = (Button) findViewById(R.id.del);
        bt_update = (Button) findViewById(R.id.update);
        bt_query = (Button) findViewById(R.id.query);
        bt_add.setOnClickListener(this);
        bt_del.setOnClickListener(this);
        bt_update.setOnClickListener(this);
        bt_query.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        HeadControlDao headControlDao = new HeadControlDao(mContext);
        HeadControlBean bean = new HeadControlBean();

        switch (view.getId()) {
            case R.id.add:

                bean.name = "张三";
                bean.phone = "111";
                boolean add = headControlDao.add(bean);
                if(add) {
                    Constant.debugLog("添加数据成功");
                }else {
                    Constant.debugLog("添加数据失败");
                }
                break;
            case R.id.del:
//                int del = headControlDao.del("张三");
//                Constant.debugLog("删除了" + del + "行");
                break;
            case R.id.update:
//                HeadControlBean bean2 = new HeadControlBean();
                bean.name = "张三";
                bean.phone = "222";
                int update = headControlDao.update(bean);
                Constant.debugLog("更新了" + update + "行");
                break;
            case R.id.query:
                headControlDao.query("张三");
                break;
            default:
                break;
        }

    }

    private void initDB() {
        DBHelper myDBHelper = new DBHelper(mContext);
        SQLiteDatabase db = myDBHelper.getWritableDatabase();
    }


}
