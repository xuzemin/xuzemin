package com.android.jdrd.headcontrol.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.jdrd.headcontrol.util.Constant;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/2/27 0027.
 */

public class HeadControlDao {

    private DBHelper myDBHelper;

    public HeadControlDao(Context context){
        //创建一个帮助类对象
        myDBHelper = new DBHelper(context);
    }

    public boolean add(HeadControlBean bean){

        //执行sql语句需要sqliteDatabase对象
        //调用getReadableDatabase方法,来初始化数据库的创建
        SQLiteDatabase 	db = myDBHelper.getReadableDatabase();


        ContentValues values = new ContentValues();//是用map封装的对象，用来存放值
//        values.put("name", bean.name);
//        values.put("phone", bean.phone);

        values.put("state_time01", bean.getTime01());
        values.put("end_time02", bean.getTime02());
        values.put("kai_guan", bean.getKaiguan());
        //table: 表名 , nullColumnHack：可以为空，标示添加一个空行, values:数据一行的值 , 返回值：代表添加这个新行的Id ，-1代表添加失败
        long result = db.insert("table1", null, values);//底层是在拼装sql语句

        //关闭数据库对象
        db.close();

        if(result != -1){//-1代表添加失败
            return true;
        }else{
            return false;
        }
    }

    public int del(HeadControlBean bean){

        //执行sql语句需要sqliteDatabase对象
        //调用getReadableDatabase方法,来初始化数据库的创建
        SQLiteDatabase db = myDBHelper.getReadableDatabase();

        //table ：表名, whereClause: 删除条件, whereArgs：条件的占位符的参数 ; 返回值：成功删除多少行
        int result = db.delete("table1", "_id = ?", new String[]{ bean.getId()+""});
        //关闭数据库对象
        db.close();

        return result;

    }

    public int update(HeadControlBean bean){

        //执行sql语句需要sqliteDatabase对象
        //调用getReadableDatabase方法,来初始化数据库的创建
        SQLiteDatabase db = myDBHelper.getReadableDatabase();
        ContentValues values = new ContentValues();//是用map封装的对象，用来存放值
        values.put("state_time01", bean.getTime01());
        values.put("end_time02", bean.getTime02());
        values.put("kai_guan", bean.getKaiguan());
        Constant.debugLog("-------Update time01------"+bean.getTime01());
        Constant.debugLog("-------Update time02------"+bean.getTime01());
        Constant.debugLog("-------Update 开为1关为0------"+bean.getKaiguan());
        Constant.debugLog("-----db.update--bean.getId-----"+bean.getId());
        //table:表名, values：更新的值, whereClause:更新的条件, whereArgs：更新条件的占位符的值,返回值：成功修改多少行
        int result = db.update("table1", values, "_id = ? ", new String[]{bean.getId()+""});

        //关闭数据库对象
        db.close();
        return result;

    }

    public HeadControlBean query(String id1) {

        //执行sql语句需要sqliteDatabase对象
        //调用getReadableDatabase方法,来初始化数据库的创建
        SQLiteDatabase db = myDBHelper.getReadableDatabase();

        //table:表名, columns：查询的列名,如果null代表查询所有列； selection:查询条件, selectionArgs：条件占位符的参数值,
        //groupBy:按什么字段分组, having:分组的条件, orderBy:按什么字段排序
        Cursor cursor = db.query("info", new String[]{"_id", "name", "phone"}, "id = ?", new String[]{id1}, null, null, "_id desc");
        //解析Cursor中的数据
        HeadControlBean bean=null;
        if (cursor != null && cursor.getCount() > 0) {//判断cursor中是否存在数据

            //循环遍历结果集，获取每一行的内容
            while(cursor.moveToNext()){//条件，游标能否定位到下一行
                //获取数据
                int id = cursor.getInt(0);
                String name_str = cursor.getString(1);
                String phone = cursor.getString(2);
                Constant.debugLog("_id:" + id + ";name:" + name_str + ";phone:" + phone);
                bean=new HeadControlBean();
            }
            cursor.close();//关闭结果集

        }
        //关闭数据库对象
        db.close();
        return  bean;
    }

    public ArrayList<HeadControlBean> allquery() {
        //执行sql语句需要sqliteDatabase对象
        //调用getReadableDatabase方法,来初始化数据库的创建
        SQLiteDatabase db = myDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from table1", null);
        Constant.debugLog("----------是否查询到数据-----------"+cursor);
        //解析Cursor中的数据
        ArrayList<HeadControlBean> arrayList = new ArrayList();
        if (cursor != null && cursor.getCount() > 0) {//判断cursor中是否存在数据

            //循环遍历结果集，获取每一行的内容
            while (cursor.moveToNext()) {//条件，游标能否定位到下一行
                //获取数据
                HeadControlBean bean = new HeadControlBean();
//                int id = cursor.getInt(0);
//                String name_str = cursor.getString(1);
//                String phone = cursor.getString(2);
//                Constant.debugLog("_id:" + id + ";name:" + name_str + ";phone:" + phone);

                bean.setId(cursor.getInt(0));
                bean.setTime01(cursor.getString(1));
                bean.setTime02(cursor.getString(2));
                bean.setKaiguan(cursor.getString(3));
                arrayList.add(bean);
                Constant.debugLog("---------下标1的数据-开始时间----------"+cursor.getString(1));
                Constant.debugLog("---------下标2的数据-结束时间----------"+cursor.getString(2));
                Constant.debugLog("---------下标3的数据--开为1关为0---------"+cursor.getString(3));

            }
            cursor.close();//关闭结果集

        }
        //关闭数据库对象
        db.close();
        return arrayList;
    }
}
