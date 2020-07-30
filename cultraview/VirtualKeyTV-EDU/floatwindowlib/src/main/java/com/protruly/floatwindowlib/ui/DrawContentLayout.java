package com.protruly.floatwindowlib.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.apkfuns.logutils.LogUtils;
import com.protruly.floatwindowlib.callback.HttpCallBack;
import com.protruly.floatwindowlib.constant.CommConsts;
import com.protruly.floatwindowlib.control.FloatWindowManager;
import com.protruly.floatwindowlib.helper.SaveFileHelper;
import com.protruly.floatwindowlib.helper.ShareFileHelper;
import com.protruly.floatwindowlib.utils.MyUtils;
import com.protruly.floatwindowlib.utils.ScreenShot;
import com.yinghe.whiteboardlib.R;
import com.yinghe.whiteboardlib.bean.FileInfo;
import com.yinghe.whiteboardlib.bean.RespFileUpdate;
import com.yinghe.whiteboardlib.bean.SketchData;
import com.yinghe.whiteboardlib.ui.NumPopupView;
import com.yinghe.whiteboardlib.ui.SketchView;
import com.yinghe.whiteboardlib.ui.SlideButton;
import com.yinghe.whiteboardlib.utils.ACache;
import com.yinghe.whiteboardlib.utils.AppUtils;
import com.yinghe.whiteboardlib.utils.CommConst;
import com.yinghe.whiteboardlib.utils.DrawConsts;
import com.yinghe.whiteboardlib.utils.EncodingHandler;
import com.yinghe.whiteboardlib.utils.NetUtil;
import com.yinghe.whiteboardlib.utils.ScreenUtils;
import com.yinghe.whiteboardlib.utils.TimeUtils;
import com.yinghe.whiteboardlib.utils.ViewUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.apkfuns.logutils.LogUtils.d;
import static com.yinghe.whiteboardlib.utils.DrawConsts.COLOR_BLACK;
import static com.yinghe.whiteboardlib.utils.DrawConsts.COLOR_BLUE;
import static com.yinghe.whiteboardlib.utils.DrawConsts.COLOR_GREEN;
import static com.yinghe.whiteboardlib.utils.DrawConsts.COLOR_ORANGE;
import static com.yinghe.whiteboardlib.utils.DrawConsts.COLOR_RED;
import static com.yinghe.whiteboardlib.utils.DrawConsts.STROKE_TYPE_CIRCLE;
import static com.yinghe.whiteboardlib.utils.DrawConsts.STROKE_TYPE_DRAW;
import static com.yinghe.whiteboardlib.utils.DrawConsts.STROKE_TYPE_ERASER;
import static com.yinghe.whiteboardlib.utils.DrawConsts.STROKE_TYPE_LINE;
import static com.yinghe.whiteboardlib.utils.DrawConsts.STROKE_TYPE_RECTANGLE;
import static com.yinghe.whiteboardlib.utils.DrawConsts.eraserPupWindowsDPHeight;
import static com.yinghe.whiteboardlib.utils.DrawConsts.menuPupWindowsDPHeight;
import static com.yinghe.whiteboardlib.utils.DrawConsts.pupWindowsDPWidth;
import static com.yinghe.whiteboardlib.utils.DrawConsts.strokePupWindowsDPHeight;

/**
 * 绘制内容的界面
 *
 * @author wang
 * @time on 2017/3/17.
 */
public class DrawContentLayout extends FrameLayout {
    public static final String TAG = DrawContentLayout.class.getSimpleName();

    private Context mContext;
    private LayoutInflater mInflater;

    private Handler mHandler;
    private Bitmap screenshotBitmap;// 保存截图

    SketchView mSketchView;// 绘制画板
    RelativeLayout mSketchFl;
    public View mBtnStrokeFl;
    private View mTipShowWhiteboard;// 批注模式开启

    int strokeType = STROKE_TYPE_DRAW;//模式

    //画笔、橡皮擦参数设置弹窗实例
    PopupWindow strokePopupWindow, eraserPopupWindow,
            scanPopupWindow;
    //画笔、橡皮擦弹窗布局
    private View popupStrokeLayout, popupEraserLayout,
            scanPopupLayout;

    private NumPopupView mNumPopupView;// 数字键弹框

    private View menuShowSwitchRL;// 显示右边切换按钮的布局
    private SlideButton mSlideButton;// 滑动按钮
    private TextView encryptTip;// 加密提示
    private View showEncryptRL;// 显示密码布局
    private TextView mEncryptText;// 显示密码文字
    private ImageView mEncryptEdit;// 编辑密码按钮

    private String encrypt;// 加密的密码

    private View menuShowEditEncryptRL;// 编辑密码的布局
    private TextView editTextEncrypt;// 编辑密码
    private String encryptTextStr;
    private ImageView mScanImage;// 扫码的二维码
    private TextView mShowScanTv;// 扫码二维码提示文字

    private Bitmap qrcodeBitmap;// 生成的二维码图片

    private ProgressBar mProgressBar;// 保存或者加载时的进度条
    private ProgressBar mScanCreateProgressBar;// 二维码生成过程中的进度条

    private static String savePathName;// 保存文件的路径

    private SeekBar strokeSeekBar, strokeAlphaSeekBar, eraserSeekBar;
    private ImageView strokeImageView, strokeAlphaImage, eraserImageView;//画笔宽度，画笔不透明度，橡皮擦宽度IV
    private EditText strokeET;//绘制文字的内容
    private int size;
    private List<SketchData> sketchDataList = new ArrayList<>();

    RadioGroup strokeTypeRG, strokeColorRG;

    // 宽和高
    public static int viewWidth;
    public static int viewHeight;

    int keyboardHeight;
    int textOffX;
    int textOffY;

    public static int sketchViewHeight;
    public static int sketchViewWidth;
    public static int sketchViewRight;
    public static int sketchViewBottom;
    public static int decorHeight;
    public static int decorWidth;

    View rLBtnStroke;//画笔
    ImageView btnStroke;//画笔
    View rLBtnEraser;//橡皮擦
    ImageView btnEraser;//橡皮擦
    View rLBtnUp;//向上翻页
    View rLBtnDown;//向下翻页
    View rLBtnShare;//分享
    View rLBtnSave;//保存
    View rLBtnEmpty;//清空
    View rLBtnShutDownSketch;//关闭画板

    // 对话框UI
    Dialog dialog;
    TextView mTitle;
    TextView mMessage;
    EditText mEditText;
    Button mConfirm; //确定按钮
    Button mCancel; //取消按钮

    private boolean isUploadToServer = false;// 是否上传到服务器

    private ACache aCache;
    private ShareFileHelper mShareFileHelper;
    private SaveFileHelper mSaveFileHelper;
    private ScreenShot mScreenShot;

    private boolean isRight = true;// 是由右边菜单点击的画板

    public DrawContentLayout(Context context) {
        super(context);
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);

        final View rootView = LayoutInflater.from(context).inflate(R.layout.draw_content_layout, this);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            //下面的代码主要是为了解决软键盘弹出后遮挡住文字录入PopWindow的问题
            Rect r = new Rect();
            rootView.getWindowVisibleDisplayFrame(r);//获取rootView的可视区域
            int screenHeight = rootView.getHeight();//获取rootView的高度
            keyboardHeight = screenHeight - (r.bottom - r.top);//用rootView的高度减去rootView的可视区域高度得到软键盘高度
            if (textOffY > (sketchViewHeight - keyboardHeight)) {//如果输入焦点出现在软键盘显示的范围内则进行布局上移操作
                rootView.setTop(-keyboardHeight);//rootView整体上移软键盘高度
                //更新PopupWindow的位置
                int x = textOffX;
                int y = textOffY - mSketchView.getHeight();
//                textPopupWindow.update(mSketchView, x, y,
//                        WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            }
        });

        mSketchFl = (RelativeLayout) findViewById(R.id.sketch_fl);
        mBtnStrokeFl = findViewById(R.id.btn_stroke_fl);
        mTipShowWhiteboard = findViewById(R.id.tip_show_whiteboard);
        viewWidth = mSketchFl.getLayoutParams().width;
        viewHeight = mSketchFl.getLayoutParams().height;

        mHandler = new Handler();

        initView();//载入所有的按钮实例
        initPopLayout();// 初始化弹框布局
        initDrawParams();// 默认绘制参数
        initDialogView();// 初始化对话框UI
        initPopupWindows();// 初始化弹框
        initData();// 初始化画板数据
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 初始化界面
     *
     */
    private void initView(){
        //画板整体布局
        mSketchView = (SketchView) findViewById(R.id.sketch_view);

        // 进度条
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);

        //设置点击监听
//        mSketchView.setOnDrawChangedListener(mOnDrawChangedListener);//设置撤销动作监听器
//        mSketchView.setTextWindowCallback(mTextWindowCallback);

        // 绘制工具
        rLBtnStroke = findViewById(R.id.rl_btn_stroke);
        btnStroke = (ImageView) findViewById(R.id.btn_stroke);

        rLBtnEraser = findViewById(R.id.rl_btn_eraser);
        btnEraser = (ImageView) findViewById(R.id.btn_eraser);

        rLBtnUp = findViewById(R.id.rl_btn_up);
        rLBtnDown = findViewById(R.id.rl_btn_down);

        rLBtnShare = findViewById(R.id.rl_btn_scan_share);
        rLBtnSave = findViewById(R.id.rl_btn_save);
        rLBtnEmpty = findViewById(R.id.rl_btn_empty);

        rLBtnShutDownSketch = findViewById(R.id.rl_btn_shutdown_sketch);

        rLBtnStroke.setOnClickListener(mOnClickListener);
        rLBtnEraser.setOnClickListener(mOnClickListener);
        rLBtnUp.setOnClickListener(mOnClickListener);
        rLBtnDown.setOnClickListener(mOnClickListener);
        rLBtnShare.setOnClickListener(mOnClickListener);
        rLBtnSave.setOnClickListener(mOnClickListener);
        rLBtnEmpty.setOnClickListener(mOnClickListener);
        rLBtnShutDownSketch.setOnClickListener(mOnClickListener);
    }

    /**
     * 初始化弹框布局
     */
    private void initPopLayout(){
        // 初始化画笔弹框布局
        initPopupStrokeLayout();

        // 初始化橡皮擦弹框布局
        initPopupEraserLayout();

        // 初始化保存中弹框布局
        initScanPopupLayout();

        // 数字键弹框布局
        initNumPopupLayout();

        //计算选择图片弹窗的高宽
        getSketchSize();
    }

    /**
     * 数字键弹框布局
     */
    private void initNumPopupLayout(){
        mNumPopupView = new NumPopupView(mContext, editTextEncrypt);

        // 设置数字键完成的监听
        mNumPopupView.setConfirmCallback(() -> {
            LogUtils.d("密码编辑完成");
            // 获得加密文字
            encrypt = editTextEncrypt.getText().toString().trim();

            // 加密的密码不能为空
            if (TextUtils.isEmpty(encrypt)){
                Toast.makeText(getContext(), "密码不能为空！", Toast.LENGTH_LONG).show();
                return;
            }

            // 显示密码UI
            menuShowSwitchRL.setVisibility(View.VISIBLE);
            encryptTip.setVisibility(View.INVISIBLE);
            showEncryptRL.setVisibility(View.VISIBLE);

            String text = String.format(encryptTextStr, encrypt);
            mEncryptText.setText(text);

            // 编辑密码UI隐藏
            menuShowEditEncryptRL.setVisibility(View.GONE);

            // 修改文件密码
            String md5filename = aCache.getAsString("md5filename");
            mShareFileHelper.changeFilePwd(md5filename, encrypt);
        });

        mNumPopupView.setCancelCallback(() -> {
            menuShowSwitchRL.setVisibility(View.VISIBLE);
            encryptTip.setVisibility(View.INVISIBLE);
            showEncryptRL.setVisibility(View.VISIBLE);

            menuShowEditEncryptRL.setVisibility(View.INVISIBLE);

            String text = "";
            encrypt = editTextEncrypt.getText().toString().trim();
            if (TextUtils.isEmpty(encrypt)){
                text = "请设置密码!";
            } else {
                text = String.format(encryptTextStr, encrypt);
            }

            mEncryptText.setText(text);
            encryptTip.setVisibility(View.GONE);
        });
    }

    /**
     * 初始化对话框UI
     */
    private void initDialogView(){
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.custom_dialog_layout, null);
        mTitle = (TextView) dialogView.findViewById(R.id.title);
        mTitle.setVisibility(View.GONE);
        mEditText = (EditText) dialogView.findViewById(R.id.message_edit);
        mMessage = (TextView) dialogView.findViewById(R.id.message);
        mConfirm = (Button) dialogView.findViewById(R.id.positiveButton);
        mCancel = (Button) dialogView.findViewById(R.id.negativeButton);

        dialog = new Dialog(getContext());
        dialog.setContentView(dialogView);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

        // 设置对话框的大小
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        lp.width = ScreenUtils.dip2px(getContext(), 400); // 宽度
        lp.height = ScreenUtils.dip2px(getContext(), 240); // 高度

        dialogWindow.setAttributes(lp);
    }

    /**
     * 初始化绘制数据
     */
    private void initDrawParams() {
        //画笔宽度缩放基准参数
        Drawable circleDrawable = getResources().getDrawable(R.drawable.circle);
        assert circleDrawable != null;
        size = circleDrawable.getIntrinsicWidth();
    }

    /**
     * 初始化弹框
     */
    private void initPopupWindows() {
        initStrokePop();
        initEraserPop();
        initScanPop();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        SketchData newSketchData = new SketchData();
        sketchDataList.add(newSketchData);
        mSketchView.setSketchData(newSketchData);

        aCache = ACache.get(getContext());
        mShareFileHelper = new ShareFileHelper();
        mSaveFileHelper = new SaveFileHelper();
        mScreenShot = new ScreenShot(getContext());
    }

    /**
     * 画笔擦弹框
     */
    private void initStrokePop() {
        //画笔弹窗
        strokePopupWindow = new PopupWindow(mContext);
        strokePopupWindow.setContentView(popupStrokeLayout);//设置主体布局
        strokePopupWindow.setWidth(ScreenUtils.dip2px(mContext, pupWindowsDPWidth));//宽度
//        strokePopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);//高度自适应
        strokePopupWindow.setHeight(ScreenUtils.dip2px(mContext, strokePupWindowsDPHeight));//高度
        strokePopupWindow.setFocusable(true);
        strokePopupWindow.setBackgroundDrawable(new BitmapDrawable());//设置空白背景
        strokePopupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);//动画
        strokeTypeRG.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.stroke_type_rbtn_draw) {
                strokeType = STROKE_TYPE_DRAW;
            } else if (checkedId == R.id.stroke_type_rbtn_line) {
                strokeType = STROKE_TYPE_LINE;
            } else if (checkedId == R.id.stroke_type_rbtn_circle) {
                strokeType = STROKE_TYPE_CIRCLE;
            } else if (checkedId == R.id.stroke_type_rbtn_rectangle) {
                strokeType = STROKE_TYPE_RECTANGLE;
            }

            mSketchView.setStrokeType(strokeType);
        });

        strokeColorRG.setOnCheckedChangeListener((group, checkedId) -> {
            int color = COLOR_BLACK;
            if (checkedId == R.id.stroke_color_black) {
                color = COLOR_BLACK;
            } else if (checkedId == R.id.stroke_color_red) {
                color = COLOR_RED;
            } else if (checkedId == R.id.stroke_color_green) {
                color = COLOR_GREEN;
            } else if (checkedId == R.id.stroke_color_orange) {
                color = COLOR_ORANGE;
            } else if (checkedId == R.id.stroke_color_blue) {
                color = COLOR_BLUE;
            }
            mSketchView.setStrokeColor(color);
        });

        //画笔宽度拖动条
        strokeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }


            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                setSeekBarProgress(progress, STROKE_TYPE_DRAW);
            }
        });
        strokeSeekBar.setProgress(SketchView.DEFAULT_STROKE_SIZE);
//        strokeColorRG.check(R.id.stroke_color_black);

        //画笔不透明度拖动条
        strokeAlphaSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }


            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                int alpha = (progress * 255) / 100;//百分比转换成256级透明度
                mSketchView.setStrokeAlpha(alpha);
                strokeAlphaImage.setAlpha(alpha);
            }
        });
        strokeAlphaSeekBar.setProgress(SketchView.DEFAULT_STROKE_ALPHA);
    }

    /**
     * 橡皮擦弹框
     */
    private void initEraserPop() {
        //橡皮擦弹窗
        eraserPopupWindow = new PopupWindow(mContext);
        eraserPopupWindow.setContentView(popupEraserLayout);//设置主体布局
        eraserPopupWindow.setWidth(ScreenUtils.dip2px(mContext, pupWindowsDPWidth));//宽度200dp
//        eraserPopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);//高度自适应
        eraserPopupWindow.setHeight(ScreenUtils.dip2px(mContext, eraserPupWindowsDPHeight));//高度自适应
        eraserPopupWindow.setFocusable(true);
        eraserPopupWindow.setBackgroundDrawable(new BitmapDrawable());//设置空白背景
        eraserPopupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);//动画
        //橡皮擦宽度拖动条
        eraserSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                setSeekBarProgress(progress, STROKE_TYPE_ERASER);
            }
        });
        eraserSeekBar.setProgress(SketchView.DEFAULT_ERASER_SIZE);
    }

    /**
     * 菜单弹框
     */
    private void initScanPop(){
        //菜单弹窗
        scanPopupWindow = new PopupWindow(mContext);
        scanPopupWindow.setContentView(scanPopupLayout);//设置主体布局

        // 设置弹窗大小
        scanPopupWindow.setWidth(ScreenUtils.dip2px(mContext, pupWindowsDPWidth));//宽度
        scanPopupWindow.setHeight(ScreenUtils.dip2px(mContext, menuPupWindowsDPHeight));//高度
        scanPopupWindow.setFocusable(true);
        scanPopupWindow.setBackgroundDrawable(new BitmapDrawable());//设置空白背景
        scanPopupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);//动画
    }

    /**
     * 设置橡皮擦进度条
     *
     * @param progress
     * @param drawMode
     */
    protected void setSeekBarProgress(int progress, int drawMode) {
        int calcProgress = progress > 1 ? progress : 1;
        int newSize = Math.round((size / 100f) * calcProgress);
        int offset = Math.round((size - newSize) / 2);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(newSize, newSize);
        lp.setMargins(offset, offset, offset, offset);
        if (drawMode == STROKE_TYPE_DRAW) {
            strokeImageView.setLayoutParams(lp);
        } else {
            eraserImageView.setLayoutParams(lp);
        }
        mSketchView.setSize(newSize, drawMode);
    }

    /**
     * 初始化画笔弹框布局
     */
    private void initPopupStrokeLayout() {
        //画笔弹窗布局
        popupStrokeLayout = mInflater.inflate(R.layout.popup_sketch_stroke, null);
        strokeImageView = (ImageView) popupStrokeLayout.findViewById(R.id.stroke_circle);
        strokeAlphaImage = (ImageView) popupStrokeLayout.findViewById(R.id.stroke_alpha_circle);
        strokeSeekBar = (SeekBar) (popupStrokeLayout.findViewById(R.id.stroke_seekbar));
        strokeAlphaSeekBar = (SeekBar) (popupStrokeLayout.findViewById(R.id.stroke_alpha_seekbar));
        //画笔颜色
        strokeTypeRG = (RadioGroup) popupStrokeLayout.findViewById(R.id.stroke_type_radio_group);
        strokeColorRG = (RadioGroup) popupStrokeLayout.findViewById(R.id.stroke_color_radio_group);
    }

    /**
     * 初始化橡皮擦弹框布局
     */
    private void initPopupEraserLayout(){
        //橡皮擦弹窗布局
        popupEraserLayout = mInflater.inflate(R.layout.popup_sketch_eraser, null);
        eraserImageView = (ImageView) popupEraserLayout.findViewById(R.id.stroke_circle);
        eraserSeekBar = (SeekBar) (popupEraserLayout.findViewById(R.id.stroke_seekbar));

        //文本录入弹窗布局
//        popupTextLayout = mInflater.inflate(R.layout.popup_sketch_text, null);
//        strokeET = (EditText) popupTextLayout.findViewById(R.id.text_pupwindow_et);
    }

    /**
     * 初始化扫码二维码弹框布局
     */
    private void initScanPopupLayout(){
        //扫码分享的界面布局
        scanPopupLayout = mInflater.inflate(R.layout.popup_sketch_menu_scan_share, null);

        // 扫码分享的界面
        menuShowSwitchRL = scanPopupLayout.findViewById(R.id.rl_show_switch_ui);
        mSlideButton = (SlideButton) scanPopupLayout.findViewById(R.id.switch_button);
        mSlideButton.setOnSlideButtonClickListener(onSlideButtonClickListener);
        encryptTip = (TextView) scanPopupLayout.findViewById(R.id.tv_encrypt_tip);

        // 右边UI:显示密码
        showEncryptRL = scanPopupLayout.findViewById(R.id.rl_show_encrypt);
        mEncryptText = (TextView) scanPopupLayout.findViewById(R.id.doc_encrypt_text);
        encryptTextStr = getContext().getResources().getString(R.string.doc_encrypt_text);
        mEncryptEdit = (ImageView) scanPopupLayout.findViewById(R.id.btn_edit_encrypt);
        mEncryptEdit.setOnClickListener(mOnClickListener);

        // 右边UI:显示密码编辑
        menuShowEditEncryptRL = scanPopupLayout.findViewById(R.id.rl_show_edit_encrypt_ui);
        editTextEncrypt = (TextView) scanPopupLayout.findViewById(R.id.edit_text_encrypt);

        // 底部UI:生成二维码过程中的加载进度条
        mScanCreateProgressBar = (ProgressBar) scanPopupLayout.findViewById(R.id.scan_create_progressbar);
        mScanImage = (ImageView) scanPopupLayout.findViewById(R.id.scan_image);
        mShowScanTv = (TextView) scanPopupLayout.findViewById(R.id.show_scan_image_tv);
    }

    /**
     * 滑块事件
     */
    private SlideButton.OnSlideButtonClickListener onSlideButtonClickListener = isChecked -> {
        if (isChecked){// 打开状态
            LogUtils.d("打开滑块");
            menuShowSwitchRL.setVisibility(View.GONE);
            menuShowEditEncryptRL.setVisibility(View.VISIBLE);

            mNumPopupView.showNumPopupWindow(rLBtnShare);
        } else {// 关闭状态
            menuShowSwitchRL.setVisibility(View.VISIBLE);
            encryptTip.setVisibility(View.VISIBLE);
            showEncryptRL.setVisibility(View.GONE);

            editTextEncrypt.setText("");
            menuShowEditEncryptRL.setVisibility(View.GONE);

            // 上传文件，生成二维码
            LogUtils.d("关闭滑块");
            encrypt = "";
            String md5filename = aCache.getAsString("md5filename");
            mShareFileHelper.changeFilePwd(md5filename, "");
        }
    };

    /**
     * 获得画板界面尺寸
     */
    private void getSketchSize() {
        ViewTreeObserver vto = mSketchView.getViewTreeObserver();
        vto.addOnPreDrawListener(() -> {
            if (sketchViewHeight == 0 && sketchViewWidth == 0) {
                int height = mSketchView.getMeasuredHeight();
                int width = mSketchView.getMeasuredWidth();
                sketchViewHeight = height;
                sketchViewWidth = width;
                sketchViewRight = mSketchView.getRight();
                sketchViewBottom = mSketchView.getBottom();
                d("onPreDraw", sketchViewHeight + "  " + sketchViewWidth);

                WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

                int screenWidth = windowManager.getDefaultDisplay().getWidth();
                int screenHeight = windowManager.getDefaultDisplay().getHeight();

                decorHeight = screenWidth;
                decorWidth = screenHeight;
                d("onPreDraw", "decor height:" + decorHeight + "   width:" + decorHeight);
            }
            return true;

        });
        LogUtils.d("getSketchSize", sketchViewHeight + "  " + sketchViewWidth);
    }

    /**
     * 在菜单栏中点击了画笔按钮
     */
    public void btnStroke(View v){
        if (mSketchView.getEditMode() == DrawConsts.EDIT_STROKE
                && mSketchView.getStrokeType() != STROKE_TYPE_ERASER
                && (mSketchView.getStrokeType() != DrawConsts.STROKE_TYPE_ERASER_CIRCLE)
                && (mSketchView.getStrokeType() != DrawConsts.STROKE_TYPE_ERASER_RECT)) {
            showParamsPopupWindow(v, DrawConsts.STROKE_TYPE_DRAW);
        } else {
            int checkedId = strokeTypeRG.getCheckedRadioButtonId();
            if (checkedId == R.id.stroke_type_rbtn_draw) {
                strokeType = DrawConsts.STROKE_TYPE_DRAW;
            } else if (checkedId == R.id.stroke_type_rbtn_line) {
                strokeType = DrawConsts.STROKE_TYPE_LINE;
            } else if (checkedId == R.id.stroke_type_rbtn_circle) {
                strokeType = DrawConsts.STROKE_TYPE_CIRCLE;
            } else if (checkedId == R.id.stroke_type_rbtn_rectangle) {
                strokeType = DrawConsts.STROKE_TYPE_RECTANGLE;
            }
            mSketchView.setStrokeType(strokeType);
        }
        mSketchView.setEditMode(DrawConsts.EDIT_STROKE);
    }

    /**
     * 在菜单栏中点击了橡皮擦按钮
     */
    public void btnEraser(View v){
//        if (mSketchView.getEditMode() == SketchView.EDIT_STROKE && mSketchView.getStrokeType() == STROKE_TYPE_ERASER) {
//            showParamsPopupWindow(v, STROKE_TYPE_ERASER);
//        } else {
//            mSketchView.setStrokeType(STROKE_TYPE_ERASER);
//        }

        mSketchView.setStrokeType(DrawConsts.STROKE_TYPE_ERASER_CIRCLE);
        mSketchView.setEditMode(DrawConsts.EDIT_STROKE);
    }

    /**
     * 设置按钮的状态
     *
     * @param iv
     */
    private void setStrokeBtn(View iv) {
        int id = iv.getId();
        if (id == R.id.rl_btn_stroke){// 画笔
            btnStroke.setImageResource(R.mipmap.tools_pen_focus);
            btnEraser.setImageResource(R.mipmap.tools_eraser_default);
        } else if (id == R.id.rl_btn_eraser){// 橡皮擦
            btnStroke.setImageResource(R.mipmap.tools_pen_default);
            btnEraser.setImageResource(R.mipmap.tools_eraser_focus);
        }
    }

    /**
     * 在菜单栏中点击了清空按钮
     */
    private void btnEmpty(){
        if (mSketchView.getRecordCount() == 0) {// 当前没有绘图，不必弹出删除对话框
            Toast.makeText(getContext(), getResources().getString(R.string.tip_is_empty_sketch), Toast.LENGTH_SHORT).show();
            return;
        }

        // 清除绘制记录
        mTitle.setVisibility(View.GONE);
        mEditText.setVisibility(View.GONE);
        mMessage.setText(R.string.dialog_ask_clear_all_title);
        mMessage.setVisibility(View.VISIBLE);

        LogUtils.d("清除内容");
        mConfirm.setOnClickListener(v -> {
            LogUtils.d("点击确认");

            // 清空画板中内容
            clearAll();
            dialog.dismiss();
        });

        mCancel.setOnClickListener(v -> {
            LogUtils.i("取消清空批注..");
            dialog.dismiss();
        });
        dialog.show();
    }

    /**
     * 在菜单栏中点击了分享按钮
     */
    private void btnShareFiles(){
        LogUtils.d("btnShareFiles");
        if (mSketchView.getRecordCount() == 0) {
            Toast.makeText(mContext, "您还没用添加批注内容", Toast.LENGTH_SHORT).show();
        } else {
            mTipShowWhiteboard.setVisibility(View.INVISIBLE);
            mBtnStrokeFl.setVisibility(View.INVISIBLE);

            // 在线程中获取截图
            new Thread(() -> {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 回收上一次的截图
                if (screenshotBitmap != null && !screenshotBitmap.isRecycled()){
                    screenshotBitmap.recycle();
                    screenshotBitmap = null;
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // 获得最新的截图
                final Bitmap bitmap = screenshot();
                if (bitmap == null){
                    LogUtils.tag(TAG).d("screenshot截图失败！");
                } else {
                    LogUtils.tag(TAG).d("screenshot截图成功！");
                }

                // 显示弹框
                screenshotBitmap = bitmap;
                mHandler.post(() -> {
                    if (screenshotBitmap == null){
                        Toast.makeText(mContext, "生成图片失败！", Toast.LENGTH_LONG).show();
                    } else {
                        // 上传到服务器
                        menuShowSwitchRL.setVisibility(View.GONE);
                        showEncryptRL.setVisibility(View.GONE);
                        menuShowEditEncryptRL.setVisibility(View.GONE);
                        LogUtils.d("显示二维码弹框");

                        // 显示二维码弹框
                        showScanPopupWindow(rLBtnShare);

                        // 获得文件名
                        savePathName = TimeUtils.getNowTimeString();
                        saveInUI(screenshotBitmap, savePathName);// 保存图片到文件中
                    }

                    // 显示画笔工具栏
                    mTipShowWhiteboard.setVisibility(VISIBLE);
                    mBtnStrokeFl.setVisibility(VISIBLE);
                });
            }).start();
        }
    }

    /**
     * 在菜单栏中点击了保存按钮
     */
    private void btnSave() {
        LogUtils.d("btnSave");
        if (mSketchView.getRecordCount() == 0) {
            Toast.makeText(mContext, "您还没用添加批注内容", Toast.LENGTH_SHORT).show();
        } else {
            mTipShowWhiteboard.setVisibility(View.INVISIBLE);
            mBtnStrokeFl.setVisibility(View.INVISIBLE);

            // 获得文件名
            String relativeDir = mSaveFileHelper.createMeetingDirName(mContext);
            mEditText.setText(relativeDir);
            mEditText.setSelection(relativeDir.length());//将光标移至文字末尾

            // 显示对话框
            mTitle.setVisibility(View.GONE);
            mMessage.setVisibility(View.GONE);
            mEditText.setVisibility(View.VISIBLE);

            mConfirm.setOnClickListener(v -> {
                LogUtils.d("btnSave 点击了保存");
                savePathName = mEditText.getText().toString();
                if (TextUtils.isEmpty(savePathName)){
                    Toast.makeText(mContext, getResources().getString(R.string.tip_save_file_not_null), Toast.LENGTH_SHORT).show();
                    return;
                }

                // 在线程中获取截图
                new Thread(() -> {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // 回收上一次的截图
                    if (screenshotBitmap != null && !screenshotBitmap.isRecycled()){
                        screenshotBitmap.recycle();
                        screenshotBitmap = null;
                    }

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // 获得最新的截图
                    final Bitmap bitmap = screenshot();
                    if (bitmap == null){
                        LogUtils.tag(TAG).d("screenshot截图失败！");
                    } else {
                        LogUtils.tag(TAG).d("screenshot截图成功！");
                    }

                    // 显示弹框
                    screenshotBitmap = bitmap;
                    mHandler.post(() -> {
                        if (screenshotBitmap == null){
                            Toast.makeText(mContext, "生成图片失败！", Toast.LENGTH_LONG).show();
                        } else {
                            saveInUI(screenshotBitmap, savePathName);
                        }

                        // 显示画笔工具栏
                        mTipShowWhiteboard.setVisibility(VISIBLE);
                        mBtnStrokeFl.setVisibility(VISIBLE);
                    });
                }).start();

                dialog.dismiss();
            });

            mCancel.setOnClickListener(v -> {
                LogUtils.i("btnSave 点击取消。");
                mTipShowWhiteboard.setVisibility(VISIBLE);
                mBtnStrokeFl.setVisibility(VISIBLE);

                dialog.dismiss();
            });

            dialog.show();
        }
    }

    /**
     * 在菜单栏中点击了关闭画板
     */
    private void btnShutDownSketch(){
//        // 判断是否在PC界面中
//        MyUtils.openAndClosePCTouch(true);

        // 关闭白板之后，清空绘制内容
        clearAll();

        // 隐藏画板
        DrawContentLayout drawContentLayout = FloatWindowManager.getContentWindow();
        if (drawContentLayout != null){
            drawContentLayout.setVisibility(View.GONE);
        }

        // 显示悬浮控制菜单
        ControlMenuLayout menuLeft = FloatWindowManager.getMenuWindowLeft();
        ControlMenuLayout menuRight = FloatWindowManager.getMenuWindow();
        if (menuRight != null && menuLeft != null){
            // 左边控制菜单
            menuLeft.setVisibility(View.VISIBLE);

            // 右边控制菜单
            menuRight.setVisibility(View.VISIBLE);

            // 收起控制菜单
            if (isRight){
                menuRight.shrinkMenu();
            } else {
                menuLeft.shrinkMenu();
            }
        }
    }

    /**
     * 清空白画板中内容
     */
    private void clearAll(){
        mSketchView.clearAll();

        // 设置为画笔模式
        setStrokeBtn(rLBtnStroke);
        mSketchView.setEditMode(DrawConsts.EDIT_STROKE);//切换笔画编辑模式
        mSketchView.setStrokeType(strokeType);
    }

    /**
     * 上下翻页的处理
     * @param isUp
     */
    private void btnUpAndDown(boolean isUp){
//        // 若是在PC界面，发送翻页广播
//        if (MyUtils.IsPc()) {
//            String action = CommConst.ACTION_PAGE_DOWN;
//            if (isUp){
//                action = CommConst.ACTION_PAGE_UP;
//            }
//
//            Intent intent = new Intent(action);
//            mContext.sendBroadcast(intent);
//
//            LogUtils.d("sendBroadcast,btnUpAndDown action->%s", action);
//        } else {
//            if (isUp){
//                AppUtils.keyEventBySystem(KeyEvent.KEYCODE_PAGE_UP);
//            } else {
//                AppUtils.keyEventBySystem(KeyEvent.KEYCODE_PAGE_DOWN);
//            }
//        }

        if (isUp){
            AppUtils.keyEventBySystem(KeyEvent.KEYCODE_PAGE_UP);
        } else {
            AppUtils.keyEventBySystem(KeyEvent.KEYCODE_PAGE_DOWN);
        }
    }

    /**
     * 保存图片
     *
     * @param imgName
     */
    private void saveInUI(final Bitmap bitmap, final String imgName) {
        new SaveToFileTask(bitmap).execute(imgName);
    }

    /**
     * 显示画笔参数弹框
     *
     * @param anchor
     * @param drawMode
     */
    private void showParamsPopupWindow(View anchor, int drawMode) {
        if (drawMode == STROKE_TYPE_DRAW) {
            strokePopupWindow.showAsDropDown(anchor, 0, ScreenUtils.dip2px(getContext(), DrawConsts.POPUP_WIN_YOFF));
        } else {
            eraserPopupWindow.showAsDropDown(anchor, 0, ScreenUtils.dip2px(getContext(), DrawConsts.POPUP_WIN_YOFF));
        }
    }

    /**
     * 获得截图
     */
    private Bitmap screenshot(){
        if (viewWidth <= 0 || viewHeight <= 0) {
            return null;
        }

        // 使用SurfaceControl截图
        LogUtils.tag(TAG).d("viewWidth * viewHeight -> %s * %s", viewWidth, viewHeight);
        Bitmap newBM = mScreenShot.screenshotToBitmap();//SurfaceControl.screenshot(viewWidth, viewHeight);
        if (newBM != null){
            LogUtils.tag(TAG).d("mScreenBitmap截图成功！");
        } else {
            LogUtils.tag(TAG).d("mScreenBitmap截图失败！");
            return null;
        }

        // 获得状态栏和底部虚拟按键的高度
        int statusBarHeight = ScreenUtils.getStatusBarHeight(getContext());// 状态栏
        int bottomStatusHeight = ScreenUtils.getBottomStatusHeight(getContext());// 底部虚拟按键

        // 左边菜单栏的宽度
        int leftMenuW = (ControlMenuLayout.viewWidth < 0) ? 0 : ControlMenuLayout.viewWidth;

        // 获得画板图像
        int right = viewWidth - leftMenuW;
        int bottom = viewHeight - statusBarHeight - bottomStatusHeight;
        Bitmap bitmap = Bitmap.createBitmap(newBM, 0, statusBarHeight, right, bottom);
        return bitmap;
    }

    /**
     * 显示扫码弹框
     *
     * @param anchor
     */
    private void showScanPopupWindow(View anchor) {
        scanPopupWindow.showAsDropDown(anchor, 0,
                ScreenUtils.dip2px(mContext, DrawConsts.POPUP_WIN_YOFF));
    }

    /**
     * 保存文件异步任务
     */
    class SaveToFileTask extends AsyncTask<String, Void, File> {
        private Bitmap mBitmap;// 截图
        private boolean isEncrypt;

        public SaveToFileTask(Bitmap bitmap) {
            mBitmap = bitmap;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (isUploadToServer){ // 上传到服务器
                isEncrypt = mSlideButton.isChecked();
                // 显示加载条
                showScanProgressBar();
            } else { // 保存本地
                mProgressBar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected File doInBackground(String... photoName) {
            LogUtils.d("保存图片到file中");
            // 保存图片到file中
            File file;

            if (isUploadToServer){ // 上传到服务器
                LogUtils.d("上传文件到服务器");
                file = saveInOI(DrawConsts.ANNOTATE_SHARE_FILES_DIR, mBitmap, photoName[0], 80);

                if (file != null){
                    mShareFileHelper.uploadFileToServer(file.getAbsolutePath(), encrypt, isEncrypt, uploadFileHttpCallBack);
                }
            } else { // 保存本地
                // 保存会议记录的名称信息
                file = saveInOI(DrawConsts.ANNOTATE_LOCAL_DIR, mBitmap, photoName[0], 80);

                if (file != null){
                    mSaveFileHelper.saveNextMeetingDirName(mContext, photoName[0]);
                }
            }

            return file;
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            if (!isUploadToServer){// 保存本地时，隐藏进度条
                mProgressBar.setVisibility(View.GONE);
            }

            String tips = "保存批注失败！";
            if (file != null && file.exists()) {
                String localPath = file.getAbsolutePath();
                if (!isUploadToServer){
                    String dirInfo = "sdcard/screenshot/" + file.getName();
                    tips = getResources().getString(R.string.tip_save_successfully, dirInfo);// file.getAbsolutePath()
                    Toast.makeText(getContext(), tips, Toast.LENGTH_SHORT).show();
                }

                // 通知图库
                AppUtils.noticeMediaScan(getContext(), localPath);
            } else {
                Toast.makeText(getContext(), tips, Toast.LENGTH_SHORT).show();
            }
        }

        /**
         * show 保存图片到本地文件，耗时操作
         * @param filePath 文件保存路径
         * @param imgName  文件名
         * @param compress 压缩百分比1-100
         * @return 返回保存的图片文件
         */
        public File saveInOI(String filePath, Bitmap bitmap, String imgName, int compress) {
            if (!imgName.toLowerCase().contains(DrawConsts.IMAGE_SAVE_SUFFIX)) {
                imgName += DrawConsts.IMAGE_SAVE_SUFFIX;
            }
            Log.e(TAG, "saveInOI: " + System.currentTimeMillis());

            if (bitmap == null ){
                LogUtils.tag(TAG).d("bitmap为null，请检查！");
                return null;
            }

            Log.e(TAG, "saveInOI: " + System.currentTimeMillis());
            FileOutputStream out = null;
            try {
                File dir = new File(filePath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File f = new File(filePath, imgName);
                if (!f.exists()) {
                    f.createNewFile();
                } else {
                    f.delete();
                }
                out = new FileOutputStream(f);
                Log.e(TAG, "saveInOI: " + System.currentTimeMillis());

                if (compress >= 1 && compress <= 100)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, compress, out);
                else {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
                }
                Log.e(TAG, "saveInOI: " + System.currentTimeMillis());
                return f;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                bitmap.recycle();
                bitmap = null;
                try {
                    out.close();
                } catch (Exception e1){
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * 显示身材二维码的进度条
     */
    private void showScanProgressBar(){
        // 显示加载条
        if (mScanCreateProgressBar.getVisibility() != View.VISIBLE){
            mScanCreateProgressBar.setVisibility(View.VISIBLE);
            mScanImage.setVisibility(View.GONE);
            mShowScanTv.setVisibility(View.GONE);
        }
    }

    /**
     * 显示二维码
     */
    private void showScanImage(){
        // 隐藏进度条
        mScanCreateProgressBar.setVisibility(View.GONE);

        // 显示二维码
        menuShowSwitchRL.setVisibility(View.VISIBLE);
        boolean isEncrypt = mSlideButton.isChecked();
        if (isEncrypt){
            showEncryptRL.setVisibility(View.VISIBLE);
            mEncryptText.setVisibility(View.VISIBLE);

            String text = "";
            if (TextUtils.isEmpty(encrypt)){
                text = "请设置密码!";
            } else {
                text = String.format(encryptTextStr, encrypt);
            }

            mEncryptText.setText(text);
            encryptTip.setVisibility(View.GONE);
        } else {
            showEncryptRL.setVisibility(View.GONE);
            encryptTip.setVisibility(View.VISIBLE);
        }

        mScanImage.setVisibility(View.VISIBLE);
        mScanImage.setImageBitmap(qrcodeBitmap);

        mShowScanTv.setVisibility(View.VISIBLE);
        mShowScanTv.setText(R.string.menu_scan_share_tip);
    }

    /**
     * 显示二维码
     */
    private void showScanImageError(int type){
        // 隐藏进度条
        mScanCreateProgressBar.setVisibility(View.GONE);

        // 显示二维码
        menuShowSwitchRL.setVisibility(View.VISIBLE);
        mScanImage.setVisibility(View.GONE);

        mShowScanTv.setVisibility(View.VISIBLE);

        int textID = R.string.menu_create_error_tip;
        if (type == 1){// 上传失败
            textID = R.string.menu_upload_error_tip;
        }
        mShowScanTv.setText(textID);
    }

    public void setRight(boolean right) {
        isRight = right;
    }

    /**
     * 点击事件监听
     */
    private OnClickListener mOnClickListener = v -> {
        if (ViewUtils.isFastDoubleClick()){
            // Toast.makeText(activity, getResources().getString(R.string.fast_double_click_tip), Toast.LENGTH_SHORT).show();
            return;
        }

        if (mSketchView.isTouch){// 绘制过程中，点击按钮无效
//            Toast.makeText(getContext(), "批注过程中，点击按钮无效！", Toast.LENGTH_SHORT).show();
            return;
        }

        int id = v.getId();
        switch (id){
            case R.id.rl_btn_stroke:
                setStrokeBtn(v);
                btnStroke(v);
                LogUtils.d("点击了画笔");
                break;
            case R.id.rl_btn_eraser:
                setStrokeBtn(v);
                btnEraser(v);
                LogUtils.d("点击了橡皮擦");
                break;
            case R.id.rl_btn_up:{// 向上翻页
                btnUpAndDown(true);
                break;
            }
            case R.id.rl_btn_down:{// 向下翻页
                btnUpAndDown(false);
                break;
            }
            case R.id.rl_btn_scan_share:{ // 保存数据
                if (NetUtil.NETWORK_NONE == NetUtil.getNetWorkState(getContext().getApplicationContext())){
                    Toast.makeText(getContext(),"网络连接异常，请检查网络。",Toast.LENGTH_LONG).show();
                    LogUtils.i("网络连接异常，请检查网络。");
                    break;
                }

                ScreenUtils.showInput(mSketchView);

                isUploadToServer = true;// 上传到服务器
                btnShareFiles();
                break;
            }
            case R.id.rl_btn_save:{ // 保存批注
                isUploadToServer = false;// 保存到本地
                btnSave();
                break;
            }
            case R.id.rl_btn_empty:{ // 清空数据
                btnEmpty();
                break;
            }
            case R.id.rl_btn_shutdown_sketch:{// 关闭白板
                setStrokeBtn(rLBtnStroke);
                btnShutDownSketch();
                break;
            }
            case R.id.btn_edit_encrypt:{// 点击编辑密码
                LogUtils.d("点击编辑密码");
                menuShowSwitchRL.setVisibility(View.GONE);
                menuShowEditEncryptRL.setVisibility(View.VISIBLE);

                mNumPopupView.showNumPopupWindow(rLBtnShare);
                break;
            }
            default:
                break;
        }
    };

    /**
     * 上传文件的回调
     */
    private HttpCallBack<RespFileUpdate> uploadFileHttpCallBack = new HttpCallBack<RespFileUpdate>() {
        @Override
        public void onError() {
            // 生成二维码失败
            mHandler.postDelayed(() -> {
                showScanImageError(1);
            },10);
        }

        @Override
        public void onResponse(RespFileUpdate response) {
            List<FileInfo> data = response.getData();
            if (data != null && !data.isEmpty()){
                String downUrl = "";
                String md5filename = data.get(0).getMd5filename();
                downUrl = CommConsts.SHARE_URL + "/" + md5filename;
                LogUtils.d("downUrl-->" + downUrl);
                aCache.put("md5filename", md5filename);

                // 生成二维码
                try{
                    int size = ScreenUtils.dip2px(getContext(), 100);
                    qrcodeBitmap = EncodingHandler.createQRCode(downUrl, size);
                } catch (Exception e){
                    e.printStackTrace();
                    LogUtils.e("生成二维码失败!" + e.getMessage());
                    // 提示生成二维码失败
                    mHandler.postDelayed(() -> {
                        showScanImageError(0);
                    }, 10);
                    return;
                }

                // 显示二维码
                mHandler.postDelayed(() -> {
                    showScanImage();
                },10);
            }
        }
    };
}
