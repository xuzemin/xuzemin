package com.android.jdrd.random.random;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Administrator on 2016/12/9.
 */

public class DBHelper2 extends SQLiteOpenHelper {
    private String TAG = "Random";
    private final static String DB_NAME ="basicB.db";//数据库名
    private final static int VERSION = 1;//版本号

    //自带的构造方法
    public DBHelper2(Context context, String name, SQLiteDatabase.CursorFactory factory,
                    int version) {
        super(context, name, factory, version);
    }

    //为了每次构造时不用传入dbName和版本号，自己得新定义一个构造方法
    public DBHelper2(Context cxt){
        this(cxt, DB_NAME, null, VERSION);//调用上面的构造方法
    }

    //版本变更时
    public DBHelper2(Context cxt, int version) {
        this(cxt,DB_NAME,null,version);
    }

    //当数据库创建的时候调用
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table person(" +
                "id integer primary key autoincrement," +
                "name varchar(20)," +
                "age int)";

        db.execSQL(sql);
        Log.e(TAG,"数据库创建");
        insertData(new Person("朱凯",1111),db);
        insertData(new Person("张成礼",1111),db);
        insertData(new Person("黄总",1111),db);
        insertData(new Person("蓝赛英",1111),db);
        insertData(new Person("张敏",1111),db);
    }

    //版本更新时调用
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql  = "update student ....";//自己的Update操作
        db.execSQL(sql);
    }
    public void insertData(Person stu,SQLiteDatabase db) {
        Log.e(TAG,"插入");
        String sql = "insert into person (name,age)values(?,?)";
        db.execSQL(sql, new Object[] { stu.name, stu.age });
    }
}
