
package com.ctv.welcome.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.Nullable;

public class MyContentProvider extends ContentProvider {
    public static final String AUTHORITY = "com.ctv.welcome.provider";

    public static final Uri URI = Uri.parse("content://com.ctv.welcome.provider");

    private static final int version = 1;

    private SQLiteDatabase db;

    private MySqliteOpenHelper helper;

    public boolean onCreate() {
        this.helper = new MySqliteOpenHelper(getContext(), "store-db", null, 1);
        this.db = this.helper.getReadableDatabase();
        return true;
    }

    @Nullable
    public Cursor query(Uri uri, String[] colums, String where, String[] whereArgs, String sortOrder) {
        return this.db.query(MySqliteOpenHelper.TABLE_PIC_STORE, colums, where, whereArgs, null,
                null, sortOrder);
    }

    @Nullable
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    public Uri insert(Uri uri, ContentValues contentValues) {
        long rawId = this.db.insert(MySqliteOpenHelper.TABLE_PIC_STORE, null, contentValues);
        if (rawId > 0) {
            return ContentUris.withAppendedId(uri, rawId);
        }
        return null;
    }

    public int delete(Uri uri, String s, String[] strings) {
        return this.db.delete(MySqliteOpenHelper.TABLE_PIC_STORE, s, strings);
    }

    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return this.db.update(MySqliteOpenHelper.TABLE_PIC_STORE, contentValues, s, strings);
    }
}
