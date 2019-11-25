
package com.ctv.welcome.task;

import android.os.AsyncTask;
import android.util.Log;
import com.ctv.welcome.activity.SelectFileActivity;
import com.ctv.welcome.constant.Constants;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class FindImageTask extends AsyncTask<String, Void, List<String>> {
    private static final String[] STORE_IMAGES = new String[] {
            "_display_name", "latitude", "longitude", "_id"
    };

    SelectFileActivity mSelectFileActivity;

    public FindImageTask(SelectFileActivity selectFileActivity) {
        this.mSelectFileActivity = selectFileActivity;
    }

    private static boolean isImage(File file) {
        String fileName = file.getName();
        for (String suffix : Constants.IMAGE_FILTER) {
            if (fileName.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected java.util.List<java.lang.String> doInBackground(java.lang.String... r11) {
        /*
         * r10 = this; r3 = 0; r1 = r10.mSelectFileActivity; r0 =
         * r1.getContentResolver(); r1 =
         * android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI; r2 =
         * STORE_IMAGES; r4 = r3; r5 = r3; r6 = r0.query(r1, r2, r3, r4, r5); r1
         * = "test"; r2 = new java.lang.StringBuilder; r2.<init>(); r4 = "cu=";
         * r2 = r2.append(r4); r2 = r2.append(r6); r2 = r2.toString();
         * android.util.Log.d(r1, r2); if (r6 == 0) goto L_0x0064; L_0x002b: r1
         * = r6.getCount(); if (r1 <= 0) goto L_0x0064; L_0x0031: r1 =
         * r6.moveToNext(); if (r1 == 0) goto L_0x0064; L_0x0037: r8 =
         * r6.getColumnNames(); r2 = r8.length; r1 = 0; L_0x003d: if (r1 >= r2)
         * goto L_0x0031; L_0x003f: r7 = r8[r1]; r4 = "test"; r5 = new
         * java.lang.StringBuilder; r5.<init>(); r5 = r5.append(r7); r9 = "=";
         * r5 = r5.append(r9); r9 = r6.getColumnIndex(r7); r5 = r5.append(r9);
         * r5 = r5.toString(); android.util.Log.d(r4, r5); r1 = r1 + 1; goto
         * L_0x003d; L_0x0064: return r3;
         */
        throw new UnsupportedOperationException(
                "Method not decompiled: com.ctv.welcome.task.FindImageTask.doInBackground(java.lang.String[]):java.util.List<java.lang.String>");
    }

    private List<String> findImageFromDirectory(File directory) {
        List<String> result = new ArrayList();
        if (directory.isFile()) {
            result.add(directory.getAbsolutePath());
            return result;
        }
        File[] fs = directory.listFiles();
        if (fs == null || fs.length == 0) {
            return null;
        }
        for (File file : fs) {
            Log.d("test", "file=" + file.getName());
            if (file.isDirectory()) {
                List<String> temp = findImageFromDirectory(file);
                if (temp != null && temp.size() > 0) {
                    result.addAll(temp);
                }
            } else if (isImage(file)) {
                result.add(file.getAbsolutePath());
            }
        }
        return result;
    }

    protected void onPostExecute(List<String> strings) {
        super.onPostExecute(strings);
        Log.d("test", "result=" + strings);
    }
}
