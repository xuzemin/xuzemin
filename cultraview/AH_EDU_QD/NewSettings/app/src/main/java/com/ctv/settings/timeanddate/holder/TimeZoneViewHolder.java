package com.ctv.settings.timeanddate.holder;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ctv.settings.R;
import com.ctv.settings.base.BaseViewHolder;
import com.ctv.settings.timeanddate.adapter.TimeZoneAdapter;
import com.ctv.settings.utils.CommonConsts;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class TimeZoneViewHolder extends BaseViewHolder {
    private final static String TAG = TimeZoneViewHolder.class.getCanonicalName();
    private ListView mLvTimeZone;
    private int mDefault = 0;
    private View btnBack;
    private TextView mTvBackTitle;

    public TimeZoneViewHolder(Activity Activity){
        super(Activity);
    }


    /**
     * @param activity 初始化UI
     */
    @Override
    public void initUI(Activity activity) {
        mLvTimeZone = (ListView)mActivity.findViewById(R.id.lv_timezone);
        mTvBackTitle = (TextView) mActivity.findViewById(R.id.back_title);
        btnBack = mActivity.findViewById(R.id.back_btn);
    }

    /**
     * @param activity 初始化数据
     */
    @Override
    public void initData(Activity activity) {
        mTvBackTitle.setText(mActivity.getResources().getString(R.string.title_timezone));
        List<HashMap<String, String>> timezoneList = getZones();
        TimeZoneAdapter mTimeZoneAdapter = new TimeZoneAdapter(activity, timezoneList,
                mDefault);
        mLvTimeZone.setAdapter(mTimeZoneAdapter);
        mLvTimeZone.setSelectionFromTop(mDefault, 250);

    }

    @Override
    public void refreshUI(View view) {


    }

    /**
     * 设置监听
     */
    @Override
    public void initListener() {
        mLvTimeZone.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<?, ?> map = (HashMap<?, ?>) mLvTimeZone.getItemAtPosition(position);
                AlarmManager alarm = (AlarmManager) mActivity
                        .getSystemService(Context.ALARM_SERVICE);
                alarm.setTimeZone((String) map.get(CommonConsts.KEY_ID));
                mActivity.setResult(CommonConsts.REFRESH_TIMEZONE);
                mActivity.finish();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.finish();
            }
        });
    }

    /**
     * @return 解析xml获取时区列表
     */
    private List<HashMap<String, String>> getZones() {
        List<HashMap<String, String>> myData = new ArrayList<HashMap<String, String>>();
        long date = Calendar.getInstance().getTimeInMillis();
        XmlResourceParser xrp = null;
        try {
            xrp = mActivity.getResources().getXml(R.xml.timezones);
            while (xrp.next() != XmlResourceParser.START_TAG)
                continue;
            xrp.next();
            while (xrp.getEventType() != XmlResourceParser.END_TAG) {
                while (xrp.getEventType() != XmlResourceParser.START_TAG) {
                    if (xrp.getEventType() == XmlResourceParser.END_DOCUMENT) {
                        return myData;
                    }
                    xrp.next();
                }
                if (xrp.getName().equals(CommonConsts.XMLTAG_TIMEZONE)) {
                    String id = xrp.getAttributeValue(0);
                    String displayName = xrp.nextText();
                    addItem(myData, id, displayName, date);
                }
                while (xrp.getEventType() != XmlResourceParser.END_TAG) {
                    xrp.next();
                }
                xrp.next();
            }
            xrp.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Unable to read timezones.xml file");
        } finally {
            if (xrp != null) {
                xrp.close();
            }
        }
        return myData;
    }
    protected void addItem(List<HashMap<String, String>> myData, String id, String displayName,
                           long date) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(CommonConsts.KEY_ID, id);
        map.put(CommonConsts.KEY_DISPLAYNAME, displayName);
        TimeZone tz = TimeZone.getTimeZone(id);
        int offset = tz.getOffset(date);
        int p = Math.abs(offset);
        StringBuilder name = new StringBuilder();
        name.append("GMT");
        if (offset < 0) {
            name.append('-');
        } else {
            name.append('+');
        }
        name.append(p / (CommonConsts.HOURS_1));
        name.append(':');
        int min = p / 60000;
        min %= 60;
        if (min < 10) {
            name.append('0');
        }
        name.append(min);
        map.put(CommonConsts.KEY_GMT, name.toString());
        map.put(CommonConsts.KEY_OFFSET, String.valueOf(offset));
        if (id.equals(TimeZone.getDefault().getID())) {
            mDefault = myData.size();
        }
        myData.add(map);
    }
}
