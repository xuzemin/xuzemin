package com.android.jdrd.headcontrol.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.jdrd.headcontrol.util.Constant;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/2/27 0027.
 */

public class HeadControlDao {

    // TODO: 2017/3/29 0029 数据库中有很多NULL数据，没有处理

    private DBHelper myDBHelper;

    public HeadControlDao(Context context){
        myDBHelper = new DBHelper(context);
    }

    public boolean add(HeadControlBean bean){
        //执行sql语句需要sqliteDatabase对象
        //调用getReadableDatabase方法,来初始化数据库的创建
        SQLiteDatabase 	db = myDBHelper.getReadableDatabase();
        ContentValues values = new ContentValues();//是用map封装的对象，用来存放值
        values.put("function", bean.getFunction());
        values.put("dataInt", bean.getDataInt());
        values.put("dataString", bean.getDataString());
        //table: 表名 , nullColumnHack：可以为空，标示添加一个空行, values:数据一行的值 , 返回值：代表添加这个新行的Id ，-1代表添加失败
        long result = db.insert("headControl", null, values);//底层是在拼装sql语句
        //关闭数据库对象
        db.close();

        if(result != -1){//-1代表添加失败
            return true;
        }else{
            return false;
        }
    }

    public int del(String function){
        //执行sql语句需要sqliteDatabase对象
        //调用getReadableDatabase方法,来初始化数据库的创建
        SQLiteDatabase db = myDBHelper.getReadableDatabase();
        //table ：表名, whereClause: 删除条件, whereArgs：条件的占位符的参数 ; 返回值：成功删除多少行
        int result = db.delete("headControl", "function=?", new String[]{function});
        //关闭数据库对象
        db.close();
        return result;
    }

    public int update(HeadControlBean bean){
        //执行sql语句需要sqliteDatabase对象
        //调用getReadableDatabase方法,来初始化数据库的创建
        SQLiteDatabase db = myDBHelper.getReadableDatabase();
        ContentValues values = new ContentValues();//是用map封装的对象，用来存放值
        values.put("dataInt", bean.getDataInt());
        values.put("dataString", bean.getDataString());
        //table:表名, values：更新的值, whereClause:更新的条件, whereArgs：更新条件的占位符的值,返回值：成功修改多少行
        int result = db.update("headControl", values, "function=? ", new String[]{bean.getFunction()});
        //关闭数据库对象
        db.close();
        return result;
    }

    /**
     * 数据库查询
     * @param function:要查询的功能字段，例如“power”
     * @return
     */
    public HeadControlBean query(String function) {
        //执行sql语句需要sqliteDatabase对象
        //调用getReadableDatabase方法,来初始化数据库的创建
        SQLiteDatabase db = myDBHelper.getWritableDatabase();
        //table:表名, columns：查询的列名,如果null代表查询所有列； selection:查询条件, selectionArgs：条件占位符的参数值,
        //groupBy:按什么字段分组, having:分组的条件, orderBy:按什么字段排序
        Cursor cursor = db.query("headControl", null, "function=?", new String[]{function}, null, null, "_id desc");
//        Cursor cursor = db.rawQuery("select * from headControl where function=?", new String[]{function});
        //解析Cursor中的数据
        HeadControlBean bean= null;
        if (cursor != null && cursor.getCount() > 0) {//判断cursor中是否存在数据
            //循环遍历结果集，获取每一行的内容
            while(cursor.moveToNext()){//条件，游标能否定位到下一行
                //获取数据
                int id = cursor.getInt(0);
                String function2 = cursor.getString(1);
                int dataInt = cursor.getInt(2);
                String dataString = cursor.getString(3);
                Constant.debugLog("function: " + " ;power:" + dataInt);
                bean=new HeadControlBean();
                bean.setId(id);
                bean.setFunction(function2);
                bean.setDataInt(dataInt);
                bean.setDataString(dataString);
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
        Cursor cursor = db.rawQuery("select * from headControl", null);
        //解析Cursor中的数据
        ArrayList<HeadControlBean> arrayList = new ArrayList();
        HeadControlBean bean = null;
        if (cursor != null && cursor.getCount() > 0) {//判断cursor中是否存在数据
            //循环遍历结果集，获取每一行的内容
            while (cursor.moveToNext()) {//条件，游标能否定位到下一行
                //获取数据
                int id = cursor.getInt(0);
                String function2 = cursor.getString(1);
                int dataInt = cursor.getInt(2);
                String dataString = cursor.getString(3);

                bean=new HeadControlBean();
                bean.setId(id);
                bean.setFunction(function2);
                bean.setDataInt(dataInt);
                bean.setDataString(dataString);
                arrayList.add(bean);
                Constant.debugLog("---------下标1的数据-开始时间----------"+cursor.getString(1));
                Constant.debugLog("---------下标2的数据-结束时间----------"+cursor.getString(2));
                Constant.debugLog("---------下标3的数据--开为1关为0---------"+cursor.getString(3));
//                Constant.debugLog("---------下标4的数据--电量信息---------"+ bean.power);

            }
            cursor.close();//关闭结果集

        }
        //关闭数据库对象
        db.close();
        return arrayList;
    }
}
