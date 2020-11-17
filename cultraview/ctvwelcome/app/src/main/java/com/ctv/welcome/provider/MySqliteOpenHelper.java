
package com.ctv.welcome.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import com.ctv.welcome.util.LogUtils;

public class MySqliteOpenHelper extends SQLiteOpenHelper {
    public static final String COLUME_STORE_TYPE = "store_type";

    public static final String TABLE_PIC_STORE = "tbl_pic_store";

    public MySqliteOpenHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createPicStoreTable(sqLiteDatabase);
    }

    private void createPicStoreTable(SQLiteDatabase db) {
        String sql = "create table tbl_pic_store(_id integer primary key autoincrement, store_type text)";
        String insert = "insert into tbl_pic_store(store_type) values('inner')";
        LogUtils.i("create table:" + sql);
        LogUtils.i("insert table:" + insert);
        db.execSQL(sql);
        db.execSQL(insert);
    }

    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}
