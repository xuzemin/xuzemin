
package com.ctv.welcome.util;

import android.text.TextUtils;
import android.util.Log;
import com.bumptech.glide.load.Key;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONObject;

public class UploadUtil {
    private static final String FAILURE = "上传失败";

    private static final String SUCCESS = "上传成功";

    public static Exception exception;

    private static String imageUrl;

    private static String jsonURL;

    public static String uploadFile(String actionUrl, File uploadFile) {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(actionUrl).openConnection();
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setChunkedStreamingMode(51200);
            con.setRequestMethod("POST");
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", Key.STRING_CHARSET_NAME);
            con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            con.setConnectTimeout(5000);
            DataOutputStream ds = new DataOutputStream(con.getOutputStream());
            ds.writeBytes(twoHyphens + boundary + end);
            ds.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\""
                    + uploadFile.getName() + "\"" + end);
            ds.writeBytes(end);
            FileInputStream fStream = new FileInputStream(uploadFile);
            byte[] buffer = new byte[1024];
            while (true) {
                int length = fStream.read(buffer);
                if (length == -1) {
                    break;
                }
                ds.write(buffer, 0, length);
            }
            ds.writeBytes(end);
            ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
            fStream.close();
            ds.flush();
            if (con.getResponseCode() == 200) {
                InputStream is = con.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buff = new byte[8192];
                while (true) {
                    int len = is.read(buff);
                    if (len == -1) {
                        break;
                    }
                    baos.write(buff, 0, len);
                }
                is.close();
                baos.close();
                String json = baos.toString().trim();
                if (!TextUtils.isEmpty(json)) {
                    JSONObject jSONObject = new JSONObject(json);
                    if (jSONObject.getInt("code") == 200) {
                        JSONArray data = jSONObject.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            imageUrl = data.getJSONObject(i).getString("url");
                        }
                        return imageUrl;
                    }
                }
            }
            ds.close();
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static String uploadFile(String[] picPaths, String requestURL) {
        Exception e;
        String boundary = UUID.randomUUID().toString();
        String prefix = "--";
        String end = "\r\n";
        String content_type = "multipart/form-data";
        String CHARSET = "utf-8";
        try {
            int i;
            int len;
            HttpURLConnection conn = (HttpURLConnection) new URL(requestURL).openConnection();
            conn.setReadTimeout(100000);
            conn.setConnectTimeout(100000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Charset", "utf-8");
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", content_type + ";boundary=" + boundary);
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(prefix);
            stringBuffer.append(boundary);
            stringBuffer.append(end);
            dos.write(stringBuffer.toString().getBytes());
            String name = "image";
            dos.writeBytes(end);
            for (i = 0; i < picPaths.length; i++) {
                File file = new File(picPaths[i]);
                StringBuffer sb = new StringBuffer();
                sb.append(prefix);
                sb.append(boundary);
                sb.append(end);
                sb.append("image" + i + "=");
                sb.append(end);
                dos.write(sb.toString().getBytes());
                InputStream fileInputStream = new FileInputStream(file);
                byte[] bytes = new byte[8192];
                while (true) {
                    len = fileInputStream.read(bytes);
                    if (len == -1) {
                        break;
                    }
                    dos.write(bytes, 0, len);
                }
                fileInputStream.close();
            }
            int res = conn.getResponseCode();
            Log.e("TAG", "response code:" + res);
            if (res == 200) {
                InputStream is = conn.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[8192];
                while (true) {
                    len = is.read(buffer);
                    if (len == -1) {
                        break;
                    }
                    baos.write(buffer, 0, len);
                }
                String str = new String(baos.toByteArray());
                System.out.println("jsonString" + str);
                baos.close();
                is.close();
                try{
                    JSONArray data = new JSONObject(str).getJSONArray("data");
                    if (data != null && data.length() > 0) {
                        for (i = 0; i < data.length(); i++) {
                            jsonURL = data.getJSONObject(i).getString("url");
                        }
                    }
                    return SUCCESS;
                }catch (Exception es){
                    es.printStackTrace();
                }

            }
        } catch (IOException e2) {
            e2.printStackTrace();
        }

        return FAILURE;
    }
}
