package com.android.jdrd.robot.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.jdrd.robot.R;
import com.android.jdrd.robot.adapter.MyAdapter;
import com.android.jdrd.robot.helper.RobotDBHelper;
import com.android.jdrd.robot.util.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by jdrd on 2017/6/13.
 *
 */

public class DeskConfigPathAcitivty extends Activity implements View.OnClickListener {
    private RobotDBHelper robotDBHelper;
    private int deskid,areaid;
    private EditText name;
    private Map deskconfiglist;
    private ListView commandlistview;
    private List<Map> command_list = new ArrayList<>();
    private boolean IsADD = false;
    private MyAdapter myAdapter;


    private List<View> listViews;
    private ImageView cursorIv;
    private TextView tab01, tab02;
    private TextView[] titles;
    private ViewPager viewPager;

    private int offset = 0;

    /**
     * 下划线图片宽度
     */
    private int lineWidth;

    /**
     * 当前选项卡的位置
     */
    private int current_index = 0;

    /**
     * 选项卡总数
     */
    private static final int TAB_COUNT = 2;

    private static final int TAB_0 = 0;

    private static final int TAB_1 = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_config);
        robotDBHelper = RobotDBHelper.getInstance(getApplicationContext());

        Intent intent =getIntent();// 收取 email
        deskid = intent.getIntExtra("id",0);
        areaid = intent.getIntExtra("area",0);

        name = (EditText) findViewById(R.id.deskname);
        findViewById(R.id.change_name).setOnClickListener(this);
        findViewById(R.id.setting_back).setOnClickListener(this);
        findViewById(R.id.btn_delete).setOnClickListener(this);
        findViewById(R.id.card).setOnClickListener(this);

        if(deskid == 0){
            findViewById(R.id.btn_delete).setVisibility(View.GONE);
            ((TextView)findViewById(R.id.title)).setText(R.string.desk_add);
            IsADD = true;
        }else{
            ((TextView)findViewById(R.id.title)).setText(R.string.desk_settings);
            IsADD = false;
            List<Map> desklist = robotDBHelper.queryListMap("select * from desk where id = '"+ deskid +"'" ,null);
            deskconfiglist = desklist.get(0);
            name.setHint(deskconfiglist.get("name").toString());
            ((TextView)findViewById(R.id.title)).setText(R.string.desk_deract);
        }

        viewPager = (ViewPager) findViewById(R.id.vPager);
        cursorIv = (ImageView) findViewById(R.id.iv_tab_bottom_img);
        tab01 = (TextView) findViewById(R.id.tv01);
        tab02 = (TextView) findViewById(R.id.tv02);

        tab01.setOnClickListener(this);
        tab02.setOnClickListener(this);

        // 获取图片宽度
        lineWidth = BitmapFactory.decodeResource(getResources(), R.mipmap.huakuai01).getWidth();
        // Android提供的DisplayMetrics可以很方便的获取屏幕分辨率
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels; // 获取分辨率宽度
        offset = (screenW / TAB_COUNT - lineWidth) / 2;  // 计算偏移值
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        // 设置下划线初始位置
        cursorIv.setImageMatrix(matrix);

        listViews = new ArrayList<>();
        LayoutInflater mInflater = getLayoutInflater();
        listViews.add(mInflater.inflate(R.layout.tab_01, null));
        refreshCommand();
        Constant.debugLog("command_list1111"+command_list);
        View view = mInflater.inflate(R.layout.tab_02, null);
        commandlistview =(ListView)view.findViewById(R.id.added_command);
        List<String> data =  new ArrayList<>();
        data.add("aaaaa");
        data.add("sssss");
        ListAdapter listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data);
        commandlistview.setAdapter(listAdapter);
//        myAdapter = new MyAdapter(this,command_list);
//        commandlistview.setAdapter(myAdapter);
//        setListViewHeightBasedOnChildren(commandlistview);
        listViews.add(mInflater.inflate(R.layout.tab_02, null));


        viewPager.setAdapter(new MyPagerAdapter(listViews));
        viewPager.setCurrentItem(0);
        titles = new TextView[]{tab01, tab02};
        viewPager.setOffscreenPageLimit(titles.length);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int one = offset * 2 + lineWidth;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                // 下划线开始移动前的位置
                float fromX = one * current_index;
                // 下划线移动完毕后的位置
                float toX = one * position;
                Animation animation = new TranslateAnimation(fromX, toX, 0, 0);
                animation.setFillAfter(true);
                animation.setDuration(500);
                // 给图片添加动画
                cursorIv.startAnimation(animation);
                // 当前Tab的字体变成红色
                titles[position].setTextColor(Color.RED);
                titles[current_index].setTextColor(Color.BLACK);
                current_index = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });



//        commandlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(DeskConfigPathAcitivty.this, CommandAcitivty.class);
//                intent.putExtra("command_id", command_list.get(position).get("id").toString());
//                startActivity(intent);
//            }
//        });
    }

    public void testClick(View v) {
        switch (v.getId()) {
            case R.id.straight:
                if (!IsADD) {
                    robotDBHelper.execSQL("insert into command (type,desk) values ('0','" + deskid + "')");
                    Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "请先输入名称添加", Toast.LENGTH_SHORT).show();
                }
                refreshCommand();
                break;
            case R.id.derail:
                if (!IsADD) {
                    robotDBHelper.execSQL("insert into command (type,desk) values ('1','" + deskid + "')");
                    Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "请先输入名称添加", Toast.LENGTH_SHORT).show();
                }
                refreshCommand();
                break;
            case R.id.rotato:
                if (!IsADD) {
                    robotDBHelper.execSQL("insert into command (type,desk) values ('2','" + deskid + "')");
                    Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "请先输入名称添加", Toast.LENGTH_SHORT).show();
                }
                refreshCommand();
                break;
            case R.id.wait:
                if (!IsADD) {
                    robotDBHelper.execSQL("insert into command (type,desk) values ('3','" + deskid + "')");
                    Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "请先输入名称添加", Toast.LENGTH_SHORT).show();
                }
                refreshCommand();
                break;
            case R.id.puthook:
                if (!IsADD) {
                    robotDBHelper.execSQL("insert into command (type,desk) values ('4','" + deskid + "')");
                    Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "请先输入名称添加", Toast.LENGTH_SHORT).show();
                }
                refreshCommand();
                break;
            case R.id.lockhook:
                if (!IsADD) {
                    robotDBHelper.execSQL("insert into command (type,desk) values ('5','" + deskid + "')");
                    Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "请先输入名称添加", Toast.LENGTH_SHORT).show();
                }
                refreshCommand();
                break;
        }
    };

    public void refreshCommand(){
        command_list.clear();
        List<Map> list = robotDBHelper.queryListMap("select * from command where desk = '"+ deskid +"'" ,null);
        command_list.addAll(list);
//        setListViewHeightBasedOnChildren(commandlistview);
        Constant.debugLog("command_list"+command_list.toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
//        refreshCommand();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_delete:
                robotDBHelper.execSQL("delete from desk where id= '"+ deskid +"'");
                finish();
                break;
            case R.id.card:
                startActivity(new Intent(DeskConfigPathAcitivty.this,CardConfig.class));
                break;
            case R.id.setting_back:
                finish();
                break;
            case R.id.change_name:
                if(name.getText().toString().trim().equals("")){
                    Toast.makeText(getApplicationContext(),"名称不能为空",Toast.LENGTH_SHORT).show();
                }else {
                    if(IsADD){
                        robotDBHelper.execSQL("insert into desk (name,area) values ('"+name.getText().toString()+"','"+areaid+"')");
                        List<Map> desklist = robotDBHelper.queryListMap("select * from desk where area = '"+areaid+"'" ,null);
                        deskid = (int) desklist.get(desklist.size()-1).get("id");
                        desklist = robotDBHelper.queryListMap("select * from desk where id = '"+ deskid +"'" ,null);
                        deskconfiglist = desklist.get(0);
                        ((TextView)findViewById(R.id.title)).setText(R.string.desk_deract);
                        IsADD = false;
                        Toast.makeText(getApplicationContext(),"添加成功",Toast.LENGTH_SHORT).show();
                    }else{
                        robotDBHelper.execSQL("update desk set name= '"+name.getText().toString().trim()+"' where id= '"+ deskid +"'");
                        Toast.makeText(getApplicationContext(),"更新成功",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.tv01:
                // 避免重复加载
                if (viewPager.getCurrentItem() != TAB_0) {
                    viewPager.setCurrentItem(TAB_0);
                }
                break;
            case R.id.tv02:
                if (viewPager.getCurrentItem() != TAB_1) {
                    viewPager.setCurrentItem(TAB_1);
                    refreshCommand();
                    View view = getLayoutInflater().inflate(R.layout.tab_02, null);
                    commandlistview =(ListView)view.findViewById(R.id.added_command);
                    List<String> data =  new ArrayList<>(); 
                    data.add("aaaaa");
                    data.add("sssss");
                    ListAdapter listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data);
                    commandlistview.setAdapter(listAdapter);
                }
                break;
        }
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        MyAdapter listAdapter = (MyAdapter) listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 5;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    /**
     * ViewPager适配器
     */
    public class MyPagerAdapter extends PagerAdapter {

        public List<View> mListViews;

        public MyPagerAdapter(List<View> mListViews) {
            this.mListViews = mListViews;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mListViews.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            container.addView(mListViews.get(position), 0);
            return mListViews.get(position);
        }

        @Override
        public int getCount() {
            return mListViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

}
