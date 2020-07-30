package com.protruly.floatwindowlib.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;

/**亮度数据库
 * Created by wy on 2017/6/27.
 */

public class LightDB {
    Context mcontext;
    public LightDB(Context context){
        this.mcontext=context;
    }


    public int queryCurInputSrc() {
        int value = 0;
        Cursor cursor = mcontext.getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/systemsetting"), null, null, null, null);
        if (cursor.moveToFirst()) {
            value = cursor.getInt(cursor.getColumnIndex("enInputSourceType"));
        }
        cursor.close();
        return value;
    }

    public int queryePicMode(int inputSrcType) {
        Cursor cursorVideo = mcontext.getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/videosetting/inputsrc/" + inputSrcType),
                null, null, null, null);
        int value = -1;
        if (cursorVideo.moveToNext()) {
            value = cursorVideo.getInt(cursorVideo.getColumnIndex("ePicture"));
        }
        cursorVideo.close();
        return value;
    }
    public void updatePicModeSetting(int value) {
        int inputSrcType=queryCurInputSrc();
        int pictureModeType=queryePicMode(inputSrcType);
        long ret = -1;
        ContentValues vals = new ContentValues();
        vals.put("u8Backlight", value);
        try {
            ret = mcontext.getContentResolver().update(
                    Uri.parse("content://mstar.tv.usersetting/picmode_setting/inputsrc/"
                            + inputSrcType + "/picmode/" + pictureModeType), vals, null, null);
        } catch (SQLException e) {
        }
        if (ret == -1) {
            System.out.println("update tbl_PicMode_Setting ignored");
        }
    }


}
