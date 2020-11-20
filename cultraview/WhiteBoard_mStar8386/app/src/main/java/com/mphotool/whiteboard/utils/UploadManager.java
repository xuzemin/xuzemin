package com.mphotool.whiteboard.utils;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.mphotool.whiteboard.activity.MainActivity.RECORD_TMP_FILE;

/**
 * Created by Dong.Daoping on 2018/5/16 0016
 * 说明：图片上传
 */
public class UploadManager {
    private static final String TAG = "UploadManager";

    private static String uploadUrl = "http://www.mphotool.com/whiteboard/upview.php?mac=%s&workid=%s&total=%s&page=%s&product=0";

    private static String responseUrl = "http://www.mphotool.com/whiteboard/GenQrUrl.php?mac=%s&workid=%s&ask=%s&tdir=%s&product=0";

    private static String result = "";

    public static String getResponse(String deviceFlag, int workid, int askType, String serverDir) throws IOException
    {
        result = "";

        OkHttpClient client = new OkHttpClient();

        String realUrl = String.format(responseUrl, deviceFlag, workid, askType, serverDir);
        BaseUtils.dbg(TAG, "getResponse url =  " + realUrl);
//        BaseUtils.dbg(TAG, "stringFromJNI =  " + SensitiveConstants.stringFromJNI("www365-1300722642"));

        //构建请求
        final Request request = new Request.Builder()
                .url(realUrl)//地址
                .build();

        Call call = client.newCall(request);
        Response response = call.execute();
        result = response.body().string();
        return result;
    }


    public static String uoloadBitmap(String deviceFlag, int workid, int pageCount, int pageIndex, Bitmap bitmap) throws IOException
    {
        result = "";
        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        OkHttpClient client = new OkHttpClient();

        String path = compressImage(bitmap).getAbsolutePath();

        final String realUrl = String.format(uploadUrl, deviceFlag, workid, pageCount, pageIndex);
        BaseUtils.dbg(TAG, "upLoadBitmap  url =  " + realUrl);

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        File f = new File(path);
        BaseUtils.dbg(TAG, "upLoadBitmap  f =  " + f.toString());
        builder.addFormDataPart("upfile", f.getName(), RequestBody.create(MEDIA_TYPE_PNG, f));

        final MultipartBody requestBody = builder.build();
        //构建请求
        final Request request = new Request.Builder()
                .url(realUrl)//地址
                .post(requestBody)//添加请求体
                .build();

        Call call = client.newCall(request);
        Response response = call.execute();
        result = response.body().string();
        return result;
    }

    /**
     * 压缩图片（质量压缩）
     *
     * @param bitmap
     */
    public static File compressImage(Bitmap bitmap)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 500)
        {  //循环判断如果压缩后图片是否大于500kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            options -= 10;//每次都减少10
            bitmap.compress(Bitmap.CompressFormat.PNG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            long length = baos.toByteArray().length;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(System.currentTimeMillis());

        new File(RECORD_TMP_FILE).mkdirs();
        File file = new File(RECORD_TMP_FILE, format.format(date) + ".png");
        try
        {
            FileOutputStream fos = new FileOutputStream(file);
            try
            {
                fos.write(baos.toByteArray());
                fos.flush();
                fos.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        recycleBitmap(bitmap);
        return file;
    }

    public static void recycleBitmap(Bitmap... bitmaps)
    {
        if (bitmaps == null)
        {
            return;
        }
        for (Bitmap bm : bitmaps)
        {
            if (null != bm && !bm.isRecycled())
            {
                bm.recycle();
//                bm = null;
            }
        }
    }

}
