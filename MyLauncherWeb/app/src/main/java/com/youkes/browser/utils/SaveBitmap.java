package com.youkes.browser.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.UUID;

public class SaveBitmap {
    private static ArrayList<String> ImageNameList;
    private static ArrayList<Bitmap> ImageBitmapList;
    private static SaveBitmap instance;
    public static final String SD_PATH = "/sdcard/Background/";
//    private static final String IN_PATH = "/dskqxt/pic/";

    public static SaveBitmap getInstance(){
        if(instance == null){
            instance = new SaveBitmap();
        }
        return instance;
    }

    public ArrayList<String> getImageNameList() {
        return ImageNameList;
    }

    public void setImageNameList(ArrayList<String> imageNameList) {
        ImageNameList = imageNameList;
    }

    public ArrayList<Bitmap> getImageBitmapList() {
        return ImageBitmapList;
    }

    public void setImageBitmapList(ArrayList<Bitmap> imageBitmapList) {
        ImageBitmapList = imageBitmapList;
    }

    public static String getSdPath() {
        return SD_PATH;
    }

    /**
     * 随机生产文件名
     *
     * @return
     */
    private String generateFileName() {
        return UUID.randomUUID().toString();
    }
    /**
     * 保存bitmap到本地
     *
     * @param context
     * @param mBitmap
     * @return
     */
    public Boolean saveBitmap(Context context, Bitmap mBitmap,String fileName) {
        if(ImageNameList != null && ImageNameList.size()>0) {
            for (String str : ImageNameList) {
                if (fileName.equals(str)) {
                    fileName = fileName + "";
                }
            }
            File filePic;
            try {
                filePic = new File(SD_PATH + fileName + ".jpg");
                if (!filePic.exists()) {
                    filePic.getParentFile().mkdirs();
                    filePic.createNewFile();
                }
                FileOutputStream fos = new FileOutputStream(filePic);
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            }
        }
        return true;//filePic.getAbsolutePath();
    }

    public Bitmap getBitmapFileName(String fileName){
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(SD_PATH + fileName + ".jpg");
            Bitmap bitmap  = BitmapFactory.decodeStream(fis);
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT; // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }// DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            } else if ("content".equalsIgnoreCase(uri.getScheme())) { // Return the remote address i
                if (isGooglePhotosUri(uri)) return uri.getLastPathSegment();
                return getDataColumn(context, uri, null, null);
            } // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        }
        return null;
    }//Android 4.4以下版本自动使用该方法


    /**
     * @param uri The Uri to check. * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check. * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check. * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check. * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    /**
     *  复制单个文件
     * @param oldPath 原文件路径 如：c:/fqf.txt String
     * @param newPath 复制后路径 如：f:/fqf.txt
     */
    public void copyFile(String oldPath, String newPath, Handler handler) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) {//文件存在时
                Constant.debugLog("wenjiancunzai");
                InputStream inStream = new FileInputStream(oldPath);//读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                int value = 0 ;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread;//字节数 文件大小
                    value ++ ;  //计数
                    System.out.println("完成"+bytesum+"  总大小");
                    fs.write(buffer, 0, byteread);
                    Message msg  = new Message(); //创建一个msg对象
                    msg.what =110 ;
                    msg.arg1 = value ; //当前的value
                    handler.sendMessage(msg) ;
                    Thread.sleep(10);//每隔10ms发送一消息，也就是说每隔10ms value就自增一次，将这个value发送给主线程处理
                }
                inStream.close();
            }else{
                Constant.debugLog("wenjian  no   cunzai");
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        }
    }

    public void getPhotoVideoPath(){
        File path = new File(SD_PATH);
        if (Environment.getExternalStorageState().
                equals(Environment.MEDIA_MOUNTED)) {
            File[] files = path.listFiles();// 读取文件夹下文件
            getPhotoName(files);
        }
    }

    protected void getPhotoName(File[] files) {
        if(files != null){
            ImageNameList = new ArrayList<>();
            ImageBitmapList = new ArrayList<>();
            for (File file : files) {
                if (!file.isDirectory()) {
                    if (file.getName().endsWith(".jpg")){
                        String filename = file.getName().substring(0,file.getName().length()-4);
                        ImageNameList.add(filename);
                        ImageBitmapList.add(getBitmapFileName(filename));
                    }
                }
            }
        }
    }

    public Bitmap getBitMBitmap(String urlpath) {
        Bitmap map = null;
        try {
            URL url = new URL(urlpath);
            URLConnection conn = url.openConnection();
            conn.connect();
            InputStream in;
            in = conn.getInputStream();
            map = BitmapFactory.decodeStream(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    public File getFileFromMediaUri(Context ac, Uri uri) {
        if(uri.getScheme().toString().compareTo("content") == 0){
            ContentResolver cr = ac.getContentResolver();
            Cursor cursor = cr.query(uri, null, null, null, null);// 根据Uri从数据库中找
            if (cursor != null) {
                cursor.moveToFirst();
                String filePath = cursor.getString(cursor.getColumnIndex("_data"));// 获取图片路径
                cursor.close();
                if (filePath != null) {
                    return new File(filePath);
                }
            }
        }else if(uri.getScheme().toString().compareTo("file") == 0){
            return new File(uri.toString().replace("file://",""));
        }
        return null;
    }


    public int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;

        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }

    public Bitmap getBitmapFormUri(Activity ac, Uri uri) throws FileNotFoundException, IOException {
        InputStream input = ac.getContentResolver().openInputStream(uri);
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        int originalWidth = onlyBoundsOptions.outWidth;
        int originalHeight = onlyBoundsOptions.outHeight;
        if ((originalWidth == -1) || (originalHeight == -1))
            return null;
        //图片分辨率以480x800为标准
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (originalWidth > originalHeight && originalWidth > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (originalWidth / ww);
        } else if (originalWidth < originalHeight && originalHeight > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (originalHeight / hh);
        }
        if (be <= 0)
            be = 1;
        //比例压缩
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = be;//设置缩放比例
        bitmapOptions.inDither = true;//optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        input = ac.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();

        return compressImage(bitmap);//再进行质量压缩
    }

    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    public Bitmap resizeBitmap(Bitmap bitmap,int w,int h)
    {
        if(bitmap!=null)
        {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int newWidth = w;
            int newHeight = h;
            float scaleWight = ((float)newWidth)/width;
            float scaleHeight = ((float)newHeight)/height;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWight, scaleHeight);
            Bitmap res = Bitmap.createBitmap(bitmap, 0,0,width, height, matrix, true);
            return res;

        }
        else{
            return null;
        }
    }
}
