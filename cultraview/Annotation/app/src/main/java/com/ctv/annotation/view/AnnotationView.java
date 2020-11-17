package com.ctv.annotation.view;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.InetAddresses;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import android.widget.RelativeLayout;
import android.widget.Toast;


import com.ctv.annotation.BuildConfig;
import com.ctv.annotation.R;
import com.ctv.annotation.WhiteBoardApplication;
import com.ctv.annotation.AnnotationService;
import com.ctv.annotation.utils.BaseUtils;
import com.ctv.annotation.utils.BitmapUtils;
import com.ctv.annotation.utils.Constants;
import com.ctv.annotation.view.dialog.ColorItem;
import com.ctv.annotation.view.dialog.ColorPickerAdapter;
import com.ctv.annotation.view.dialog.ExitrlDialog;

import com.ctv.annotation.view.dialog.QrDialog;
import com.ctv.utils.SDKinfor;
import com.ctv.utils.SensitiveConstants;
import com.tencent.cos.xml.CosXmlServiceConfig;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.tag.RestoreConfigure;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import static com.ctv.annotation.utils.BaseUtils.createQRCode;


public class AnnotationView extends RelativeLayout implements View.OnClickListener {
    public static final  int CLOSE_USBTOUCH_FLOAT= 0x900;
    public static final  int  OPEN_USBTOUCH_FLOAT = 0X800;
    public static final  int MSG_VIEW_GONE = 0x000;
    public static final int MSG_RETURN_PEN = 0x200;
    public static final  int  EXIT = 0x700;
    public static final int MSG_VIEW_delay=0x123;
    public static final int MSG_UPDATE_IMAGE_FAILED = 0x600;
    public static final int MSG_VIEW_VISIBILITY=0x111;
    public static final int MSG_SHAR_FAILED=0x81100;
    public static final int  INIT_DRAWD = 0X111111;
    private static volatile boolean isCloseUSBTouch = false; // 是否改变USBTouch

    private View mPenSelector;
    private View mPenSelectorLayout;
    private  RelativeLayout mRoot;
    public View inflate;
    private Context context;
    private boolean isQrCancel;
    private ColorPickerAdapter adapter_left;
    private ColorPickerAdapter adapter_right;
    private static final String APP_DIR = TextUtils.isEmpty(BuildConfig.floder_name) ? "annotation" : BuildConfig.floder_name + "/annotation";
    private static final String SD_DIR = BaseUtils.getSDPATH();
    public static final String ROOT_DIR = (SD_DIR + "/" + APP_DIR);
    public static final String RECORD_TMP_FILE = (ROOT_DIR + "/temp/");
    private int mlrposition;
    private BroadcastReceiver mBroadcastReceiver;
    private String mpath;
    private int logal;
    private String toSavePath;
    private Bitmap save_bitmap;
    private ConnectivityManager mConnectivityManager;
//    private ColorPickerView colorPickerView_left;
//    private ColorPickerView colorPickerView;
    private String filename_date;
    private Handler mhandler = new Handler(){

        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case CLOSE_USBTOUCH_FLOAT:

                    BaseUtils.setEasyTouchEnable(false,context);
                    try {
                        closeUSBTouch(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case OPEN_USBTOUCH_FLOAT:

                    BaseUtils.setEasyTouchEnable(true,context);
                    break;
                case  MSG_UPDATE_IMAGE_FAILED:

                    Toast.makeText(context,R.string.upload_image_failed,Toast.LENGTH_LONG).show();
                    break;
                case INIT_DRAWD:
                    initdrawd();

                    break;
                case MSG_VIEW_VISIBILITY:
                    findViewById(R.id.tv_main).setVisibility(View.VISIBLE);
                    findViewById(R.id.bottom_buttons_left).setVisibility(View.VISIBLE);
                    findViewById(R.id.bottom_buttons).setVisibility(View.VISIBLE);
                    break;
                case MSG_SHAR_FAILED:
                    Toast.makeText(context, R.string.qr_upfale, Toast.LENGTH_SHORT).show();
                    break;
                case EXIT:
                    open_eduvir(context);
                    int easyTouchOpen = Settings.System.getInt(context.getContentResolver(),
                            "EASY_TOUCH_OPEN", 1);
                    try {
                        closeUSBTouch(false);
                        BaseUtils.dbg("hong","closeUSBTouch  false");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (easyTouchOpen == 0){
                        BaseUtils.setEasyTouchEnable(false,context);
                    }else {
                        BaseUtils.setEasyTouchEnable(true,context);
                    }

                    getContext().sendBroadcast(new Intent("com.ctv.annotate.FINISH"));
                    destory();
                    stopservice();
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(0);
                    break;
                case MSG_VIEW_GONE:
                    findViewById(R.id.tv_main).setVisibility(View.GONE);
                    savelayout_main.setVisibility(View.GONE);
                    savelayout_main_left.setVisibility(View.GONE);
                    findViewById(R.id.bottom_buttons_left).setVisibility(View.GONE);
                    findViewById(R.id.bottom_buttons).setVisibility(View.GONE);
                    break;
                case MSG_VIEW_delay:
                   showQr();
                    break;
                case MSG_RETURN_PEN:
                    if (!WhiteBoardApplication.isTurnBackStatus)
                    {
                        return;
                    }
                    /**被选中状态下不切回画笔状态*/
                    if (mPanelManager != null && !mPanelManager.hasSelected())
                    {
                        setPanelMode(0);
                    }

                    mPenSelectorLayout_left.setVisibility(View.GONE);
                    mPenSelectorLayout.setVisibility(View.GONE);
                    break;


                default:
                    break;

            }

        }
    };
    private View saveview;
    private View savelayout_main;
    private DrawBoard mDrawBoard;
 //   private PenWidth mPenWidthPreview;
    private View mBtnClean;
    private PanelManager mPanelManager;
    private View back_select;
    private View back;
    private View mPenSelector_left;
    private View mPenSelectorLayout_left;
    private View saveview_left;
    private View mBtnClean_left;
    private View savelayout_main_left;
    private View viewPenColor;
    private View dashLine2;
    private RecyclerView mColorPicker;
    private View ViewPenColor_left;
    private View line2_left;
    private RecyclerView mColorPicker_left;
    private ExitrlDialog exitrlDialog;
    public static final String action = "android.intent.action.close.annotation";



    public AnnotationView(@NonNull Context context) {
        super(context);
        this.context = context;

        BaseUtils.dbg("hong","1111");
       inflate = LayoutInflater.from(WhiteBoardApplication.context).inflate(R.layout.activity_main, this);
        mRoot = inflate.findViewById(R.id.activity_main);
        initview();
        mhandler.sendEmptyMessageDelayed(INIT_DRAWD,300);
        mBroadcastReceiver = new TestBroadcastReceiver();
        IntentFilter filter = new IntentFilter(action);// 过滤
        context.registerReceiver(mBroadcastReceiver, filter);

        close_eduvir(context);

        mhandler.sendEmptyMessageDelayed(CLOSE_USBTOUCH_FLOAT,800);

    }



    // 1 批注显示
    // 0 批注关闭
    private void close_eduvir(Context context){
         Settings.System.putInt(context.getContentResolver(), "annotate.start", 1);
    }
    private void open_eduvir(Context context){
        Settings.System.putInt(context.getContentResolver(), "annotate.start", 0);

    }

    //keww add 退出广播
    private class TestBroadcastReceiver extends BroadcastReceiver {
        // 接收广播
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("keww", "批注关闭....." + action);
            showExitrldialog();
            //mAnnotationview.handler.sendEmptyMessageDelayed(0x11111,1000);//不要弹框，直接退出
        }
    }
    public AnnotationView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        BaseUtils.dbg("hong","2222");
    }

    public AnnotationView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        BaseUtils.dbg("hong","3333");
    }

    private void initview(){
        filename_date="annotation_"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        //右侧返回键
         back = findViewById(R.id.back_select);
        //  back.setOnClickListener(this);

        //右侧画笔键
        mPenSelector = findViewById(R.id.pen_selector);
        mPenSelectorLayout = findViewById(R.id.pen_selector_layout);
        //右侧保存键
        saveview = findViewById(R.id.save_select);
        savelayout_main = findViewById(R.id.save_layout_main);
        //右侧橡皮擦
        mBtnClean = findViewById(R.id.clean_board);

   //     mPenWidthPreview = (PenWidth) findViewById(R.id.pen_width_preview);
        //左侧键
        savelayout_main_left = findViewById(R.id.save_layout_main_left);

        mPenSelector_left = findViewById(R.id.pen_selector_left);
        mPenSelectorLayout_left = findViewById(R.id.pen_selector_layout_left);

        //左侧返回键
        back_select = inflate.findViewById(R.id.back_select_left);

        //左侧保存键
        saveview_left = inflate.findViewById(R.id.save_select_left);

        //左侧橡皮擦
        mBtnClean_left = inflate.findViewById(R.id.clean_board_left);

        //画布

        dashLine2 = inflate.findViewById(R.id.pen_dash_line2);
        line2_left = inflate.findViewById(R.id.pen_dash_line2_left);


        BaseUtils.dbg("hong", "initview: ");
    }
    private void initdrawd(){

      mDrawBoard = (DrawBoard) inflate.findViewById(R.id.drawboard);
      mDrawBoard.setVisibility(VISIBLE);
//        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        mDrawBoard = new DrawBoard(getContext());
//        addView(mDrawBoard,params );
        mPanelManager = mDrawBoard.mPanelView.mPanelManager;
        setPanelMode(0);
        inintclik();

        initPenColorPicker();
        initPenColorPicker_left();

        initPenWidthOptions();
        initPenWidthOptions_left();
    }
    private void initPenWidthOptions_left()
    {
        View penLittle_left = inflate.findViewById(R.id.pen_little_left);
        final View penLittleActive_left =inflate.findViewById(R.id.pen_little_ative_left);
        View penMiddle_left = inflate.findViewById(R.id.pen_middle_left);
        final View penMiddleActive_left = inflate.findViewById(R.id.pen_middle_active_left);
        View penBig_left = inflate.findViewById(R.id.pen_big_left);
        final View penBigActive_left = inflate.findViewById(R.id.pen_big_active_left);
        penLittleActive_left.setVisibility(View.GONE);
        penMiddleActive_left.setVisibility(View.VISIBLE);
        penBigActive_left.setVisibility(View.GONE);
        mPanelManager.setPenWidth(Constants.PEN_WIDTH_MIDDLE);
        penLittle_left.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                if (mPanelManager != null && mPanelManager.isLocked)
                {
                    return;
                }
                clearAndResetTimer();
                penLittleActive_left.setVisibility(View.VISIBLE);
                penMiddleActive_left.setVisibility(View.GONE);
                penBigActive_left.setVisibility(View.GONE);
                mPanelManager.setPenWidth(Constants.PEN_WIDTH_LITTLE);
                mPenSelector.setBackgroundResource(R.drawable.pen_little_selector);
//                if (BuildConfig.FLAVOR.equals("CTV")){
//                    // viewPenColor.getLayoutParams().height = 10;
//                }
                mPenSelector.setActivated(true);
            }
        });
        penMiddle_left.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                if (mPanelManager != null && mPanelManager.isLocked)
                {
                    return;
                }
                clearAndResetTimer();
                penLittleActive_left.setVisibility(View.GONE);
                penMiddleActive_left.setVisibility(View.VISIBLE);
                penBigActive_left.setVisibility(View.GONE);
                mPanelManager.setPenWidth(Constants.PEN_WIDTH_MIDDLE);
                mPenSelector.setBackgroundResource(R.drawable.pen_middle_selector);
//                if (BuildConfig.FLAVOR.equals("CTV")){
//                    //viewPenColor.getLayoutParams().height = 15;
//                }
                mPenSelector.setActivated(true);
            }
        });
        penBig_left.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                if (mPanelManager != null && mPanelManager.isLocked)
                {
                    return;
                }
                clearAndResetTimer();
                penLittleActive_left.setVisibility(View.GONE);
                penMiddleActive_left.setVisibility(View.GONE);
                penBigActive_left.setVisibility(View.VISIBLE);
                mPanelManager.setPenWidth(Constants.PEN_WIDTH_LARGE);
                mPenSelector.setBackgroundResource(R.drawable.pen_large_selector);
//                if (BuildConfig.FLAVOR.equals("CTV")){
//                    //  viewPenColor.getLayoutParams().height = 20;
//                }
                mPenSelector.setActivated(true);
            }
        });
    }
    private void initPenWidthOptions()
    {
        View penLittle = findViewById(R.id.pen_little);
        final View penLittleActive = findViewById(R.id.pen_little_ative);
        View penMiddle = findViewById(R.id.pen_middle);
        final View penMiddleActive = findViewById(R.id.pen_middle_active);
        View penBig = findViewById(R.id.pen_big);
        final View penBigActive = findViewById(R.id.pen_big_active);
        penLittleActive.setVisibility(View.GONE);
        penMiddleActive.setVisibility(View.VISIBLE);
        penBigActive.setVisibility(View.GONE);
        mPanelManager.setPenWidth(Constants.PEN_WIDTH_MIDDLE);
        penLittle.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                BaseUtils.dbg("hhh","onClick: little");

                if (mPanelManager != null && mPanelManager.isLocked)
                {
                    return;
                }
                clearAndResetTimer();
                penLittleActive.setVisibility(View.VISIBLE);
                penMiddleActive.setVisibility(View.GONE);
                penBigActive.setVisibility(View.GONE);
                mPanelManager.setPenWidth(Constants.PEN_WIDTH_LITTLE);
                mPenSelector.setBackgroundResource(R.drawable.pen_little_selector);
//                if (BuildConfig.FLAVOR.equals("CTV")){
//                    viewPenColor.getLayoutParams().height = 10;
//                }
                mPenSelector.setActivated(true);
            }
        });
        penMiddle.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                BaseUtils.dbg("hhh","onClick: middle");

                if (mPanelManager != null && mPanelManager.isLocked)
                {
                    return;
                }
                clearAndResetTimer();
                penLittleActive.setVisibility(View.GONE);
                penMiddleActive.setVisibility(View.VISIBLE);
                penBigActive.setVisibility(View.GONE);
                mPanelManager.setPenWidth(Constants.PEN_WIDTH_MIDDLE);
                mPenSelector.setBackgroundResource(R.drawable.pen_middle_selector);
//                if (BuildConfig.FLAVOR.equals("CTV")){
//                    viewPenColor.getLayoutParams().height = 15;
//                }
                mPenSelector.setActivated(true);
            }
        });
        penBig.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                BaseUtils.dbg("hhh","onClick: pig");

                if (mPanelManager != null && mPanelManager.isLocked)
                {
                    return;
                }
                clearAndResetTimer();
                penLittleActive.setVisibility(View.GONE);
                penMiddleActive.setVisibility(View.GONE);
                penBigActive.setVisibility(View.VISIBLE);
                mPanelManager.setPenWidth(Constants.PEN_WIDTH_LARGE);
                mPenSelector.setBackgroundResource(R.drawable.pen_large_selector);
//                if (BuildConfig.FLAVOR.equals("CTV")){
//                    viewPenColor.getLayoutParams().height = 20;
//                }
                mPenSelector.setActivated(true);
            }
        });

        //橡皮擦

        mBtnClean_left.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!WhiteBoardApplication.isWhiteBoardEnable)
                {
                    return;
                }
                if (mPanelManager != null && mPanelManager.isLocked)
                {
                    return;
                }
                clearTimer();
                mPenSelectorLayout.setVisibility(View.GONE);
                hideAllWindows(2);
                setPanelMode(1);
                setTimer();
            }
        });

        mBtnClean.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v)
            {
                if (!WhiteBoardApplication.isWhiteBoardEnable)
                {
                    return;
                }
                if (mPanelManager != null && mPanelManager.isLocked)
                {
                    return;
                }

                clearTimer();
                mPenSelectorLayout.setVisibility(View.GONE);
                hideAllWindows(2);
                setPanelMode(1);
                setTimer();
            }
        });

    }
    public void inintclik(){
        mPenSelector_left.setOnClickListener(this);
        mPenSelector.setOnClickListener(this);
//点击弹出保存layout
        saveview.setOnClickListener(this);
        saveview_left.setOnClickListener(this);
        savelayout_main_left.findViewById(R.id.iv_save_select_main_left).setOnClickListener(this);
        savelayout_main.findViewById(R.id.iv_save_select_main).setOnClickListener(this);
        savelayout_main_left.findViewById(R.id.share_erwei_main_left).setOnClickListener(this);
        savelayout_main.findViewById(R.id.share_erwei_main).setOnClickListener(this);
        back.setOnClickListener(this);
        back_select.setOnClickListener(this);

    }
    private void initPenColorPicker(){
        viewPenColor = findViewById(R.id.view_button_pen_color);
        if(Constants.IS_AH_EDU_QD) {
            viewPenColor.setBackgroundColor(Constants.PEN_COLOLR_AH_EDU_QD);
            mlrposition = 2;
        }else{
            viewPenColor.setBackgroundColor(Constants.PEN_COLOLR_DEFAULT);
            mlrposition = 0;
        }



        mColorPicker = (RecyclerView) findViewById(R.id.color_picker);
        mColorPicker.setLayoutManager(new GridLayoutManager(getContext(), 4));
        int[] colors = getResources().getIntArray(R.array.pen_colors);
        final List<ColorItem> colorItemList = new ArrayList();
        for (int color : colors)
        {
            ColorItem colorItem = new ColorItem();
            colorItem.setType(0);
            colorItem.setColor(color);
            colorItemList.add(colorItem);
        }
          adapter_right = new ColorPickerAdapter(getContext(), R.layout.pen_color_item, colorItemList);
        adapter_right.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                mlrposition = position;
                if (position < 0 && position >= colorItemList.size())
                {
                    return;
                }
                clearAndResetTimer();
                if (position != adapter_right.getActivePosition()) {
                    ColorItem colorItemClick = (ColorItem) colorItemList.get(position);
                    switch (colorItemClick.getType()) {
                        case 0:
                            dismissColorPickerView();
                            int color = colorItemClick.getColor();
                            adapter_right.setCurrentColor(color);
                            if (color == -1) {
                                ViewPenColor_left.setBackgroundColor(getResources().getColor(R.color.color1));
                                viewPenColor.setBackgroundColor(getResources().getColor(R.color.color1));
                            } else {
                                ViewPenColor_left.setBackgroundColor(color);
                                viewPenColor.setBackgroundColor(color);
                            }
                            mDrawBoard.setPenColor(color);

                            break;
                    }
                    adapter_right.setActivePosition(position);
                    adapter_right.notifyDataSetChanged();
                }
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        mColorPicker.setAdapter(adapter_right);
    }

    private void initPenColorPicker_left(){
        ViewPenColor_left = findViewById(R.id.view_button_pen_color_left);
        if(Constants.IS_AH_EDU_QD) {
            ViewPenColor_left.setBackgroundColor(Constants.PEN_COLOLR_AH_EDU_QD);
            mlrposition = 2;
        }else{
            ViewPenColor_left.setBackgroundColor(Constants.PEN_COLOLR_DEFAULT);
            mlrposition = 0;
        }

        mColorPicker_left = (RecyclerView) findViewById(R.id.color_picker_left);
        mColorPicker_left.setLayoutManager(new GridLayoutManager(getContext(),4));
        int[] colors = getResources().getIntArray(R.array.pen_colors);
        final List<ColorItem> colorItemList = new ArrayList();
        for (int color : colors)
        {
            ColorItem colorItem = new ColorItem();
            colorItem.setType(0);
            colorItem.setColor(color);
            colorItemList.add(colorItem);
        }
        adapter_left = new ColorPickerAdapter(getContext(), R.layout.pen_color_item, colorItemList);
        adapter_left.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position)
            {

                mlrposition = position;
                if (position < 0 && position >= colorItemList.size())
                {
                    return;
                }
                clearAndResetTimer();
                if (position != adapter_left.getActivePosition())
                {

                    ColorItem colorItemClick = (ColorItem) colorItemList.get(position);
                    switch (colorItemClick.getType())
                    {
                        case 0:
                            dismissColorPickerView();
                            int color = colorItemClick.getColor();
                            adapter_left.setCurrentColor(color);
                            if (color == -1)
                            {
                                ViewPenColor_left.setBackgroundColor(getResources().getColor(R.color.color1));
                                viewPenColor.setBackgroundColor(getResources().getColor(R.color.color1));
                            }
                            else
                            {
                                ViewPenColor_left.setBackgroundColor(color);
                                viewPenColor.setBackgroundColor(color);
                            }
                            mDrawBoard.setPenColor(color);

                            break;

                    }
                    adapter_left.setActivePosition(position);
                    adapter_left.notifyDataSetChanged();
                }
            }

            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position)
            {
                return false;
            }
        });
        mColorPicker_left.setAdapter(adapter_left);
    }

    private void dismissColorPickerView() {
        dashLine2.setVisibility(View.GONE);
    }
    private void  showQr()
    {
        isQrCancel = false;
        SDKinfor sdKinfor = new SDKinfor(context);

        final QrDialog qr_dialog = new QrDialog(getContext());
        qr_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override public void onDismiss(DialogInterface dialog)
            {
                BaseUtils.dbg("hong","onDismiss:qr_dialog onDismiss");
                isQrCancel = true;
                BaseUtils.deleteFloder(RECORD_TMP_FILE);

            }
        });
        qr_dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override public void onCancel(DialogInterface dialog)
            {
                // Log.d("hong", "onCancel:qr_dialog setOnCancelListener ");
                mhandler.sendEmptyMessage(MSG_VIEW_VISIBILITY);
                isQrCancel = true;
                BaseUtils.deleteFloder(RECORD_TMP_FILE);
                // BaseUtils.hideNavigationBar(MainActivity.this);
            }
        });
        qr_dialog.show();
        qr_dialog.getWindow().setLayout(getResources().getDimensionPixelOffset(R.dimen.qr_dialog_width), getResources().getDimensionPixelOffset(R.dimen.qr_dialog_width));

        //上传至腾讯云

        String region = SensitiveConstants.stringFromJNI(SensitiveConstants.COS_REGION);//"ap-guangzhou";

//创建 CosXmlServiceConfig 对象，根据需要修改默认的配置参数
        final CosXmlServiceConfig serviceConfig = new CosXmlServiceConfig.Builder()
                .setRegion(region)
                .isHttps(true) // 使用 https 请求, 默认 http 请求
                .setDebuggable(true)
                .builder();

// String secretId = SensitiveConstants.stringFromJNI(SensitiveConstants.COS_SECRETID);//"AKIDzmJa4IA3ZzB8zkdekcEeplW1HH5W3nZG"; //永久密钥 secretId
// String secretKey =SensitiveConstants.stringFromJNI(SensitiveConstants.COS_SECRETKEY);//"g8Yt23tNL2ZqwmRqGdmgOs8MOBl9JL9h"; //永久密钥 secretKey
// String secretId="AKIDmJcgV6XTLsRlrA8DZiJekU0Hc2DAtITP";
// String secretKey="rS0KEsGPJxKVgbGH6h15uhFXYN7bqDDz";
/**
 * 初始化 {@link QCloudCredentialProvider} 对象，来给 SDK 提供临时密钥。
 * @parma secretId 永久密钥 secretId
 * @param secretKey 永久密钥 secretKey
 * @param keyDuration 密钥有效期,单位为秒
 */
//        QCloudCredentialProvider credentialProvider = new ShortTimeCredentialProvider(secretId,
//                secretKey, 300);
//
//        CosXmlService cosXmlService = new CosXmlService(context, serviceConfig, credentialProvider);
//        TransferConfig transferConfig = new TransferConfig.Builder().build();

//初始化
        Bitmap jiemap= BitmapUtils.srceenshot(context);
        String Path = compressImage(jiemap).getAbsolutePath();
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        logal = 0;
        logal++;
        df.format(new Date());

        if (!new File(Path).exists()){
            Log.d("hong", "File not exist!"+Path);
        }

        String cosPath ="annotation/"+"annotation_"+df.format(new Date()) +".png"; //即对象到 COS 上的绝对路径, 格式如 cosPath = "text.txt";
        Log.d("hong", "showQr: cosPath="+cosPath);
        String srcPath =Path; // 如 srcPath=Environment.getExternalStorageDirectory().getPath() + "/text.txt";
        final String uploadId = null; //若存在初始化分片上传的 UploadId，则赋值对应 uploadId 值用于续传，否则，赋值 null。
        // 上传对象
        //  COSXMLUploadTask cosxmlUploadTask = transferManager.upload(bucket, cosPath, srcPath, uploadId);



        //检测监听
        CosXmlResultListener cosXmlResultListener = new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest cosXmlRequest, CosXmlResult cosXmlResult) {

                final String url_cos=cosXmlResult.accessUrl;
                Log.d("hong", "onSuccess: ");
                //  COSXMLUploadTask.COSXMLUploadTaskResult cOSXMLUploadTaskResult = (COSXMLUploadTask.COSXMLUploadTaskResult)cosXmlResult;
                if (TextUtils.isEmpty(url_cos))
                {
                    Observable.just(Integer.valueOf(1)).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
                        public void call(Integer integer)
                        {

                            qr_dialog.dismiss();
                            mhandler.sendEmptyMessage(MSG_UPDATE_IMAGE_FAILED);
                            mhandler.sendEmptyMessage(MSG_VIEW_VISIBILITY);
                        }
                    });
                    return;
                }
                else
                {
                    Observable.just(Integer.valueOf(1)).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
                        public void call(Integer integer)
                        {

                            Log.d("hong", "call: show qrview");
                            if (qr_dialog.isShowing()){
                                qr_dialog.showQrView(createQRCode(url_cos, getResources().getDimensionPixelOffset(R.dimen.qr_width)));
                            }else {

                                mhandler.sendEmptyMessage(MSG_SHAR_FAILED);
                                // handler.sendEmptyMessage(EXIT);
                            }
                        }
                    });
                }
            }

            @Override
            public void onFail(CosXmlRequest cosXmlRequest, CosXmlClientException e, CosXmlServiceException e1) {
                Log.d("hong", "onFail client: " + e.toString());
                Observable.just(Integer.valueOf(1)).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
                    public void call(Integer integer)
                    {
                        Log.d("hong", "call: qr_dialog.dismiss");
                        qr_dialog.dismiss();
                        mhandler.sendEmptyMessage(MSG_VIEW_VISIBILITY);
                        mhandler.sendEmptyMessage(MSG_UPDATE_IMAGE_FAILED);
                    }
                });
                // Log.d("hong", "onFail Service: " + serviceException.toString());
            }
        };
        sdKinfor.upload(cosPath,srcPath,cosXmlResultListener);


        //监听
//        cosxmlUploadTask.setCosXmlResultListener(new CosXmlResultListener() {
//            @Override
//            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
//
//                final String url_cos=result.accessUrl;
//                Log.d("hong", "onSuccess: ");
//                COSXMLUploadTask.COSXMLUploadTaskResult cOSXMLUploadTaskResult = (COSXMLUploadTask.COSXMLUploadTaskResult)result;
//                if (TextUtils.isEmpty(url_cos))
//                {
//                    Observable.just(Integer.valueOf(1)).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
//                        public void call(Integer integer)
//                        {
//                           qr_dialog.dismiss();
//                           handler.sendEmptyMessage(MSG_UPDATE_IMAGE_FAILED);
//                           handler.sendEmptyMessage(MSG_VIEW_VISIBILITY);
//                        }
//                    });
//                    return;
//                }
//                else
//                {
//                    Observable.just(Integer.valueOf(1)).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
//                        public void call(Integer integer)
//                        {
//
//                            Log.d("hong", "call: show qrview");
//                            if (qr_dialog.isShowing()){
//                                qr_dialog.showQrView(createQRCode(url_cos, getResources().getDimensionPixelOffset(R.dimen.qr_width)));
//                            }else {
//
//                              handler.sendEmptyMessage(MSG_SHAR_FAILED);
//                            }
//                        }
//                    });
//                }
//
//                // qr_dialog.showQrView(createQRCode(url, getResources().getDimensionPixelOffset(R.dimen.qr_width)));
//            }
//
//            @Override
//            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
//                Log.d("hong", "onFail client: " + exception.toString());
//                Observable.just(Integer.valueOf(1)).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
//                    public void call(Integer integer)
//                    {
//                        Log.d("hong", "call: qr_dialog.dismiss");
//                        qr_dialog.dismiss();
//                        handler.sendEmptyMessage(MSG_VIEW_VISIBILITY);
//                        handler.sendEmptyMessage(MSG_UPDATE_IMAGE_FAILED);
//                    }
//                });
//                // Log.d("hong", "onFail Service: " + serviceException.toString());
//            }
//        });
    }


    /**
     * 压缩图片（质量压缩）
     *
     * @param bitmap
     */
    public static File compressImage(Bitmap bitmap)
    {
        Log.d("shunxu", "compressImage: 333333333333");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 500)
        {  //循环判断如果压缩后图片是否大于500kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            options -= 10;//每次都减少10
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            long length = baos.toByteArray().length;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(System.currentTimeMillis());
        String filename = format.format(date);
        new File(RECORD_TMP_FILE).mkdirs();
        File file = new File(RECORD_TMP_FILE, filename + ".png");
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

    private void hidelayout(){

        mPenSelectorLayout.setVisibility(View.GONE);
        savelayout_main.setVisibility(View.GONE);
        savelayout_main_left.setVisibility(View.GONE);
        mPenSelectorLayout_left.setVisibility(View.GONE);

    }
    private void clearAndResetTimer()
    {
        clearTimer();
        setTimer();
    }
    private void setTimer()
    {
        mhandler.sendEmptyMessageDelayed(MSG_RETURN_PEN, 3500);
    }


    private void clearTimer()
    {
        mhandler.removeMessages(MSG_RETURN_PEN);
    }
    private void hideAllWindows(int index)
    {
        if (mPenSelectorLayout.isShown() && index != 4)
        {
            mPenSelectorLayout.setVisibility(View.GONE);
        }
        if (savelayout_main.isShown()&&index!=5){
            savelayout_main.setVisibility(View.GONE);
        }
        if (savelayout_main_left.isShown() && index != 6) {

            savelayout_main_left.setVisibility(View.GONE);
        }
        if (mPenSelectorLayout_left.isShown()&&index!=7){
            mPenSelectorLayout_left.setVisibility(View.GONE);
        }
    }
    private void show_left_penselector(){

        adapter_left.setActivePosition(mlrposition);
        adapter_left.notifyDataSetChanged();

        mPenSelectorLayout.setVisibility(GONE);
        mPenSelectorLayout_left.setVisibility(VISIBLE);

    }
    private void show_right_penselector(){

        adapter_right.setActivePosition(mlrposition);
        adapter_right.notifyDataSetChanged();
        BaseUtils.dbg("hhh","show_right_penselector:");

        mPenSelectorLayout.setVisibility(VISIBLE);
        mPenSelectorLayout_left.setVisibility(GONE);

    }
    public void showExitrldialog(){
        exitrlDialog = new ExitrlDialog(context);
        exitrlDialog.show();

        exitrlDialog.getWindow().setLayout(getResources().getDimensionPixelSize(R.dimen.exit_dialog_width), getResources().getDimensionPixelSize(R.dimen.exit_dialog_height));
        exitrlDialog.setSave(new ExitrlDialog.Save() {
            @Override
            public void save() {
                final String mpath=Constants.WHITE_BOARD_IMAGE_PATH;

                mhandler.sendEmptyMessage(MSG_VIEW_GONE);
                exitrlDialog.dismiss();
                Handler mhandler=new Handler();
                mhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("hong", "dialog---run: ");
                        savephoto(mpath,filename_date);
                     //   AnnotationManager.removeview();

                    }
                },200);
            }

            @Override
            public void exit() {
                mhandler.sendEmptyMessageDelayed(EXIT,10);

            }
        });
    }

    private void setPanelMode(int mode)
    {
        mDrawBoard.mPanelView.mPanelManager.setMode(mode);
        mPenSelector_left.setActivated(false);
        mPenSelector.setActivated(false);
        mBtnClean.setActivated(false);
        mBtnClean_left.setActivated(false);
        switch (mode)
        {
            case 0:
                mPenSelector.setActivated(true);
                mPenSelector_left.setActivated(true);
                return;
            case 1:
            case 2:
                mBtnClean.setActivated(true);
                mBtnClean_left.setActivated(true);
                return;
            case 3:
         //       mSelection.setActivated(true);
                return;
            default:
                return;
        }
    }
    @SuppressLint("WrongConstant")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pen_selector_left:
                {
                if (!WhiteBoardApplication.isWhiteBoardEnable)
                {
                    return;
                }
                hideAllWindows(7);
                if (mPanelManager != null && mPanelManager.isLocked)
                {
                    return;
                }
                if (mPenSelectorLayout.isShown())
                {
                    mPenSelectorLayout.setVisibility(View.GONE);

                }
                if (mPenSelectorLayout_left.isShown()){
                    mPenSelectorLayout_left.setVisibility(View.GONE);
                    clearTimer();
                    return;
                }
                if (mPenSelector_left.isActivated()){
                    //   mPenSelectorLayout_left.setVisibility(View.VISIBLE);
                    show_left_penselector();
                    clearAndResetTimer();
                }
                setPanelMode(0);
            }

                break;
            case R.id.pen_selector:
            {
                if (!WhiteBoardApplication.isWhiteBoardEnable)
                {
                    return;
                }

                hideAllWindows(4);
                if (mPanelManager != null && mPanelManager.isLocked)
                {
                    return;
                }
                if (mPenSelectorLayout_left.isShown()){
                    mPenSelectorLayout_left.setVisibility(View.GONE);
                }
                if (mPenSelectorLayout.isShown())
                {
                    BaseUtils.dbg("ggg","onClick: rightlayoutgone");

                    mPenSelectorLayout.setVisibility(View.GONE);
                    clearTimer();
                    return;
                }

                if (mPenSelector.isActivated())
                {
                    // mPenSelectorLayout.setVisibility(View.VISIBLE);
                    show_right_penselector();
                    clearAndResetTimer();
                }

                setPanelMode(0);
            }

                break;
            case R.id.save_select:
            {
                if (!WhiteBoardApplication.isWhiteBoardEnable)
                {
                    return;
                }
                if (mPanelManager != null && mPanelManager.isLocked)
                {
                    return;
                }

                if (savelayout_main.isShown())
                {
                    savelayout_main.setVisibility(View.GONE);
                    clearTimer();
                }
                else
                {
                    savelayout_main.setVisibility(View.VISIBLE);
                    hideAllWindows(5);
                    savelayout_main.measure(0, 0);
                    // BaseUtils.hideNavigationBar(MainActivity.this);
                //    setPanelMode(4);
                }

            }

                    break;
            case R.id.save_select_left:
            {
                if (!WhiteBoardApplication.isWhiteBoardEnable)
                {
                    return;
                }
                if (mPanelManager != null && mPanelManager.isLocked)
                {
                    return;
                }
                if (savelayout_main.isShown())
                {
                    savelayout_main.setVisibility(View.GONE);
                    clearTimer();
                }
                if (savelayout_main_left.isShown()){
                    savelayout_main_left.setVisibility(View.GONE);
                }
                else
                {
                    savelayout_main_left.setVisibility(View.VISIBLE);
                    hideAllWindows(6);
                    savelayout_main_left.measure(0, 0);

                //    setPanelMode(4);
                }
            }


                break;
            case R.id.iv_save_select_main_left:
            {
                if (!WhiteBoardApplication.isWhiteBoardEnable)
                {
                    return;
                }
                int fileCount_left = BaseUtils.getFileCount(new File(ROOT_DIR));
                if (fileCount_left >= Constants.MAX_FILE_COUNT)
                {
                   //   showFileCountDlg();
                    Toast.makeText(getContext(),getContext().getText(R.string.file_already_full),1).show();
                }
                else
                {
                    mpath = Constants.WHITE_BOARD_IMAGE_PATH;
                    mhandler.sendEmptyMessage(MSG_VIEW_GONE);
                    Handler mhandler=new Handler();
                    mhandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            savephoto(mpath, filename_date);


                        }
                    },500);


                }
            }

                break;
            case R.id.iv_save_select_main:
            {
                if (!WhiteBoardApplication.isWhiteBoardEnable)
                {
                    return;
                }

                int fileCount_right = BaseUtils.getFileCount(new File(ROOT_DIR));

                if (fileCount_right >= Constants.MAX_FILE_COUNT)
                {
                    //    showFileCountDlg();
                    Toast.makeText(getContext(),getContext().getText(R.string.file_already_full),1).show();

                }
                else
                {
                    final String mpath=Constants.WHITE_BOARD_IMAGE_PATH;

                    mhandler.sendEmptyMessage(MSG_VIEW_GONE);

                    Handler mhandler=new Handler();
                    mhandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            savephoto(mpath,filename_date);


                        }
                    },500);


                }

            }
            break;
            case R.id.share_erwei_main_left:
            case R.id.share_erwei_main:
                { if (!WhiteBoardApplication.isWhiteBoardEnable)
            {
                return;
            }
                if (mPanelManager != null && mPanelManager.isLocked)
                {
                    return;
                }
                hideAllWindows(0);
                mhandler.sendEmptyMessage(MSG_VIEW_GONE );
                if (mConnectivityManager == null)
                {
                    mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                }
                if (BaseUtils.isNetworkAvailed(mConnectivityManager))
                {

                    mhandler.sendEmptyMessageDelayed(MSG_VIEW_delay,400);
                    return;
                }
                BaseUtils.dbg("hhh","onClick:MSG_VIEW_VISIBILITY ");

                mhandler.sendEmptyMessage(MSG_VIEW_VISIBILITY);
                Toast.makeText(context, R.string.unaviled_network, Toast.LENGTH_SHORT).show();

            }
            break;
            case R.id.back_select:
            case  R.id.back_select_left: {
                if (!WhiteBoardApplication.isWhiteBoardEnable)
            {
                return;
            }
    //          mhandler.sendEmptyMessageDelayed(EXIT,300);
                hidelayout();
                showExitrldialog();

            }
            break;


        }

    }

    public void destory(){
        mDrawBoard.mPanelView.destory();
        try {
            closeUSBTouch(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mBroadcastReceiver != null) {
            context.unregisterReceiver(mBroadcastReceiver);
        }
    }
    @SuppressLint("WrongConstant")
    private void savephoto(String mpath, String filename){
        Log.d("hong", "----savephoto----");
        String str = "call: ";
        initInternalPath();
        PanelManager panelManager = this.mPanelManager;

        if (panelManager != null ){
            StringBuilder stringBuilder;
            String str2 = "hong";
            Log.d(str2, "call: mPanelManager");
            String path = "";

                //   Log.d(str2, "call: mPanelManager.getPageCount ="+mPanelManager.getPageCount());
                this.save_bitmap = BitmapUtils.srceenshot(getContext());
                Log.d("hong", "savephoto: photo.size ="+save_bitmap.getByteCount());
                StringBuilder stringBuilder2;
                try {
                    stringBuilder =new StringBuilder();
                    stringBuilder.append("call =");
                    stringBuilder.append(path);
                    Log.d(str2, stringBuilder.toString());
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(mpath);
                    stringBuilder.append(Constants.ROOT);
                    stringBuilder.append(filename);
                    stringBuilder.append(".png");
                    path = stringBuilder.toString();
                    File toFile = new File(path);
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(str);
                    stringBuilder2.append(path);
                    Log.d(str2, stringBuilder2.toString());
                    if (!toFile.exists()){
                        toFile.createNewFile();
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("call : tofile");
                        stringBuilder2.append(toFile);
                        Log.d(str2, stringBuilder2.toString());
                    }
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(str);
                    stringBuilder2.append(path);
                    Log.d(str2, stringBuilder2.toString());
                    final String tpath = path;
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            try {
                                Log.d("hong", "save ---run:00000");
                                FileOutputStream out = new FileOutputStream(tpath);
                                Log.d("hong", "save ---run:11111");
                                AnnotationView.this.save_bitmap.compress(Bitmap.CompressFormat.JPEG,90,out);
                                Log.d("hong", "save ---run:22222");
                                out.close();
                                Log.d("hong", "save ---run:33333");
                               mhandler.sendEmptyMessageDelayed(EXIT,1000);
                            } catch (Exception e) {

                                e.printStackTrace();

                            }
                        }
                    }.start();

                } catch (Exception e) {


                }


            this.toSavePath = path;
            updateMediaContents(mpath);

            String ss = getResources().getString(R.string.savephoto);
            Context context = this.context;
            stringBuilder = new StringBuilder();
            stringBuilder.append(filename);
            stringBuilder.append(ss+"Annotation/image");
            // stringBuilder.append("图片保存在" +"/Annotation/image");

            Toast.makeText(context, stringBuilder.toString(), 1).show();
            Log.d(str2, "call:save_map ");


        }

    }
    private void closeUSBTouch(boolean isCloseUSBTouch){
        if (isCloseUSBTouch){ // 关闭USB触控
            Log.d("hhh", "关闭USB触控");
            this.isCloseUSBTouch = true;
            BaseUtils.changeUSBTouch(getContext(),false);
        } else { // 恢复USB触控
            if(this.isCloseUSBTouch) {
                Log.d("hhh", "恢复USB触控");
                this.isCloseUSBTouch = false;
                BaseUtils.changeUSBTouch(getContext(),true);
            }
        }

    }

    private void updateMediaContents(String file)
    {
        // BaseUtils.dbg(TAG,"updateMediaContents:" + file);
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(new File(file)));
        getContext().sendBroadcast(intent);
        getContext().sendBroadcast(new Intent("action_media_scanner_start"));
    }
    private void initInternalPath() {
        File root;


        String path = Constants.WHITE_BOARD_IMAGE_PATH;
        root = new File(path);
        if (!root.exists()) {
            Log.d("hong", "make local dirs: ");
            root.mkdirs();
        }

    }
    private void stopservice(){
        Intent intent = new Intent(context, AnnotationService.class);
        context.stopService(intent);
    }
}
