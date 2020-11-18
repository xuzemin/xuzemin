
package com.ctv.settings.timeanddate.bean;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.util.Log;

public class TimezoneInfo {
    private final String TAG = "TimezoneInfo";

    private final static String IP_URL = "http://whatismyipaddress.com";

    private String IPAddress = null;

    private String Country = null;

    private String Region = null;

    private String City = null;

    private Context context;

    private String latlngString = null;

    private String latlngJson = null;

    private String timezoneJson = null;

    private String timezoneId = null;

    public TimezoneInfo(Context context) {
        super();
        // TODO Auto-generated constructor stub
        this.context = context;
    }

    /**
     * 根据网站"http://www.webyield.net/ip/"直接获取当前timezoneid
     * 
     * @param null
     * @return
     * @throws IOException
     */
    public String getTimezoneFromWebyield() throws IOException {
        String url = "http://www.webyield.net/ip/";
        String webbyyieldTimezoneString = null;
        Connection c = Jsoup.connect(url);

        Document doc = c.data("query", "Java")
                .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; en; rv:1.9.2.15)")
                .cookie("auth", "token").timeout(5000).post();
        Log.d(TAG, "getTimezoneFromWebyield-------------------->>>>");

        // <tr>
        // <td align="right"><strong>Hostname</strong>:</td>
        // <td align="left">183.14.110.211</td>
        // </tr>

        // <tr>
        // <td align="right"><strong>City</strong>:</td>
        // <td align="left">Guangzhou</td>
        // </tr>

        // <tr>
        // <td align="right"><strong>Time Zone</strong>:</td>
        // <td align="left">Asia/Chongqing</td>
        // </tr>
        Elements tables = doc.select("table");
        for (Element table : tables) {
            Elements trs = table.select("tr");
            for (Element tr : trs) {
                if (tr.text().contains("Time Zone")) {
                    webbyyieldTimezoneString = tr.text();
                    break;
                }
            }
            if (webbyyieldTimezoneString != null) {
                break;
            }
        }
        if (webbyyieldTimezoneString != null) {
            String aa[] = webbyyieldTimezoneString.split(":");
            timezoneId = aa[1].replaceAll(" ", "");
        }
        Log.d(TAG, "getTimezoneFromWebyield-------------------->>>>timezoneId:" + timezoneId);
        return timezoneId;
    }

    /**
     * 根据web service地址和参数获取当前外网IP信息,当前地址信息
     * 
     * @param null
     * @return
     */
    public void getIPAddressInfo() throws Exception {

        Connection c = Jsoup.connect(IP_URL);

        Document doc = c.data("query", "Java")
                .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; en; rv:1.9.2.15)")
                .cookie("auth", "token").timeout(5000).post();

        // Document doc = c.timeout(5000).post();
        Log.d(TAG, "getIPAddressInfo--------------------》");
        // get IPaddress
        // <a href="//whatismyipaddress.com/ip/121.15.143.210"
        // style="font-weight:bold;color:#007cc3;font-size:26px;text-decoration:none;">
        // 121.15.143.210</a>
        Elements as = doc.select("a");
        for (Element a : as) {
            if (a.attr("href").contains("/ip/")) {
                String str_temp = a.attr("href");
                String aa[] = str_temp.split("/");
                IPAddress = aa[4];
                break;
            }
        }
        Log.d(TAG, "IPAddress:" + IPAddress);
        // get Country
        // <tr><th style="font-weight:bold;color:#676769;">City:</th><td
        // style="font-size:14px;">Orange</td></tr>
        // <tr><th style="font-weight:bold;color:#676769;">Region:</th><td
        // style="font-size:14px;">California</td></tr>
        // <tr><th style="font-weight:bold;color:#676769;">Country:</th><td
        // style="font-size:14px;">United States</td></tr>
        Elements tables = doc.select("table");
        for (Element table : tables) {
            Elements trs = table.select("tr");
            for (Element tr : trs) {
                if (tr.text().contains("City")) {
                    City = tr.text();
                    // City = tr.select("td").text();
                } else if (tr.text().contains("Region")) {
                    Region = tr.text();
                    // Region = tr.select("td").text();
                } else if (tr.text().contains("Country")) {
                    Country = tr.text();
                    // Country = tr.select("td").text();
                }
            }
        }
        Log.d(TAG, "City:" + City);
    }

    /**
     * 根据API地址和参数获取响应对象HttpResponse
     * 
     * @param params
     * @param url
     * @return
     */
    private HttpResponse post(Map<String, Object> params, String url) {

        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("charset", HTTP.UTF_8);
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        HttpResponse response = null;
        if (params != null && params.size() > 0) {
            List<NameValuePair> nameValuepairs = new ArrayList<NameValuePair>();
            for (String key : params.keySet()) {
                nameValuepairs.add(new BasicNameValuePair(key, (String) params.get(key)));
            }
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuepairs, HTTP.UTF_8));
                response = client.execute(httpPost);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        } else {
            try {
                response = client.execute(httpPost);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                Log.d(TAG, "ClientProtocolException:" + e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "IOException:" + e.getMessage());
            }
        }
        return response;
    }

    /**
     * 得到JSON值
     * 
     * @param params
     * @param url
     * @return
     */
    private Object getResponseValues(Map<String, Object> params, String url) {
        String token = "";
        HttpResponse response = post(params, url);
        if (response != null) {
            try {
                token = EntityUtils.toString(response.getEntity());
                response.removeHeaders("operator");
            } catch (ParseException e) {
                token = null;
                e.printStackTrace();
                Log.d(TAG, "ParseException:" + e.getMessage());
            } catch (IOException e) {
                token = null;
                e.printStackTrace();
                Log.d(TAG, "IOException:" + e.getMessage());
            }
        }
        return token;
    }

    /**
     * 根据城市地址获取经纬度
     * 
     * @param String city
     * @return
     */
    public String getLatlngString(String city) {
        String validCity = city.replaceAll(" ", "%20");
        String url = "http://maps.google.com/maps/api/geocode/json?address=" + validCity
                + "&language=en&sensor=false";
        // return parseLatlngJson((String)getValues(null, url));
        latlngJson = (String) getResponseValues(null, url);
        latlngString = parseLatlngJson(latlngJson);
        return latlngString;
    }

    /**
     * 获取时区ID
     * 
     * @param 经纬度 latlng
     * @return
     */
    public String getTimezoneId(String latlng) {
        // https://maps.googleapis.com/maps/api/timezone/json?location=39.6034810,-119.6822510&timestamp=1331161200
        String url = "https://maps.googleapis.com/maps/api/timezone/json?location=" + latlng
                + "&timestamp=1331161200";
        // {
        // "dstOffset" : 0,
        // "rawOffset" : -28800,
        // "status" : "OK",
        // "timeZoneId" : "America/Los_Angeles",
        // "timeZoneName" : "Pacific Standard Time"
        // }
        timezoneJson = (String) getResponseValues(null, url);
        try {
            JSONObject json = new JSONObject(timezoneJson);
            timezoneId = json.getString("timeZoneId");
            Log.d(TAG, "getTimezoneId:" + timezoneId);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.d(TAG, "JSONException:" + e.getMessage());
            return null;
        }

        return timezoneId;
    }

    /**
     * 解析出相应的经纬度
     * 
     * @param String jsonString
     * @return
     */
    public String parseLatlngJson(String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);
            JSONArray jsonResults = json.getJSONArray("results");
            JSONObject jsonGeometry = jsonResults.getJSONObject(0).getJSONObject("geometry");
            JSONObject jsonLocation = jsonGeometry.getJSONObject("location");
            Log.d(TAG, "latitude--longitude:" + jsonLocation.toString());

            String latitude = jsonLocation.getString("lat");
            String longitude = jsonLocation.getString("lng");

            latlngString = latitude + "," + longitude;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.d(TAG, "JSONException:" + e.getMessage());
        }
        return latlngString;
    }

    public String getIPAddress() {
        try {
            return IPAddress;
        } catch (Exception e) {
            // TODO: handle exception
            Log.d(TAG, "getIPAddress,Exception");
            e.printStackTrace();
            return null;
        }
    }

    public String getCountry() {
        try {
            String Country_temp[] = Country.split(":");

            return Country_temp[1].trim();
        } catch (Exception e) {
            // TODO: handle exception
            Log.d(TAG, "getCountry,Exception");
            e.printStackTrace();
            return null;
        }
    }

    public String getRegion() {
        try {
            String Region_temp[] = Region.split(":");
            return Region_temp[1].trim();
        } catch (Exception e) {
            // TODO: handle exception
            Log.d(TAG, "getRegion,Exception");
            e.printStackTrace();
            return null;
        }
    }

    public String getCity() {
        try {
            String City_temp[] = City.split(":");
            return City_temp[1].trim();
        } catch (Exception e) {
            // TODO: handle exception
            Log.d(TAG, "getCity,Exception");
            e.printStackTrace();
            return null;
        }
    }
}
