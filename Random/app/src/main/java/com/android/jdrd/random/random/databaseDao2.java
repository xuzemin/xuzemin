package com.android.jdrd.random.random;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Administrator on 2016/12/9.
 */

public class databaseDao2 {
    private String TAG = "Random";
    DBHelper2 helperB = null;

    public databaseDao2(Context cxt) {
        helperB = new DBHelper2(cxt);
    }

    /**
     * 当Activity中调用此构造方法，传入一个版本号时，系统会在下一次调用数据库时调用Helper中的onUpgrade()方法进行更新
     * @param cxt
     * @param version
     */
    public databaseDao2(Context cxt, int version) {
        helperB = new DBHelper2(cxt, version);
    }

    // 插入操作
    public void insertData(Person stu) {
        Log.e(TAG,"插入");
        String sql = "insert into person (name,age)values(?,?)";
        SQLiteDatabase db = helperB.getWritableDatabase();
        db.execSQL(sql, new Object[] { stu.name, stu.age });
    }

    public String selectData(int number) {
        Log.e(TAG,"查询"+number);
        SQLiteDatabase db = helperB.getWritableDatabase();
        String name = null;
        Cursor cursor = db.rawQuery("select * from person where id = "+number, null);
        while (cursor.moveToNext()) {
            //int personid = cursor.getInt(0); //获取第一列的值,第一列的索引从0开始
            name = cursor.getString(1);//获取第二列的值
            //int age = cursor.getInt(2);//获取第三列的值
        }
        return name;
    }
    // 其它操作
    public void selectData() {
        Log.e(TAG,"查询所有");
        SQLiteDatabase db = helperB.getWritableDatabase();
        String name = null;
        Cursor cursor = db.rawQuery("select * from person ", null);
        while (cursor.moveToNext()) {
            name = cursor.getString(1);//获取第二列的值
            Log.e(TAG,"name"+name);
        }
    }
}
