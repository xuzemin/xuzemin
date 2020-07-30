package com.protruly.floatwindowlib.ui;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apkfuns.logutils.LogUtils;
import com.protruly.floatwindowlib.control.FloatWindowManager;
import com.protruly.floatwindowlib.entity.SignalInfo;
import com.protruly.floatwindowlib.service.FloatWindowService;
import com.protruly.floatwindowlib.utils.MyUtils;
import com.yinghe.whiteboardlib.R;
import com.yinghe.whiteboardlib.utils.AppUtils;
import com.yinghe.whiteboardlib.utils.CmdUtils;
import com.yinghe.whiteboardlib.utils.SPUtil;
import com.yinghe.whiteboardlib.utils.ScreenUtils;
import com.yinghe.whiteboardlib.utils.ViewUtils;

import java.lang.ref.WeakReference;


/**
 * 控制菜单布局
 *
 * @author wang
 * @time on 2017/3/17.
 */
public class ControlMenuLayout extends FrameLayout {
	private final static String TAG = ControlMenuLayout.class.getSimpleName();
	private Context mContext;

	// 宽和高
	public static int viewWidth;
	public static int viewHeight;

	/**
	 * 用于更新小悬浮窗的位置
	 */
	private WindowManager.LayoutParams menuParams;// 浮窗的参数

	private boolean isRight = false;// 控制菜单的位置:true为右边菜单，false为左边菜单
	private boolean isShrink = true;// 判断菜单是否收缩

	private View rootView;
	public LinearLayout menuLayout;

	View rlBtnArrow;// 箭头按钮的父布局
	ImageView btnArrow;// 箭头按钮:控制菜单显示和隐藏

	View btnBack;// 返回
	View btnHome;// 回到主页
	View btnAppSwitch;// 多功能切换键
	View btnShowSetting;// 显示设置
	View rlBtnWhiteBoard;//批注按钮的父布局

	View rlUserDefined; //用户自定义

	View rlBtnPC; // 快捷PC
	ImageView btnPC;// 快捷PC按钮

	Animation showAnim;
	Animation hideAnim;

	public static Handler mHandler;

	private float xInScreen;// 记录当前手指位置在屏幕上的横坐标值
	private float yInScreen;// 记录当前手指位置在屏幕上的纵坐标值
	private float xDownInScreen;// 记录手指按下时在屏幕上的横坐标的值
	private float yDownInScreen;// 记录手指按下时在屏幕上的纵坐标的值
	private float xInView;// 记录手指按下时在小悬浮窗的View上的横坐标的值
	private float yInView;// 记录手指按下时在小悬浮窗的View上的纵坐标的值

	private int actionType = 0; // 0:默认 1:左右上下侧滑显示 2:可移动 3:侧滑显示且可移动
	private static boolean isMenuMove = false;// 菜单可以移动的标识
	private final static float MIN_CLICK_SCALE = 0.05f;
	private final static float MAN_CLICK_SCALE = 0.99f;

	// 对话框UI
	Dialog dialog;
	TextView mTitle;
	TextView mMessage;
	EditText mEditText;
	Button mConfirm; //确定按钮
	Button mCancel; //取消按钮

	// 对话框UI
	Dialog signalDialog;
	private int shortSource = 25;
	public ControlMenuLayout(Context context, boolean isRight) {
		super(context);
		mContext = context;
		this.isRight = isRight;
		init(context);
	}

	/**
	 * 初始化
	 *
	 * @param context
	 */
	private void init(Context context) {
		LayoutInflater.from(context).inflate(R.layout.right_btn_menu, this);
		mHandler = new UIHandler(this);

		initView();
		initDialogView();
		initSignalDialog();
		setListener();

		if (isRight) { // 右边的控制菜单
			btnArrow.setImageResource(R.drawable.btn_arrow_left_normal);
		} else { // 左边的控制菜单
			btnArrow.setImageResource(R.drawable.btn_arrow_right_normal);
		}
	}

	/**
	 * 初始化对话框UI
	 */
	private void initDialogView() {
		View dialogView = LayoutInflater.from(this.getContext()).inflate(R.layout.update_dialog_layout, null);
		mTitle = (TextView) dialogView.findViewById(R.id.title);
		mEditText = (EditText) dialogView.findViewById(R.id.message_edit);
		mMessage = (TextView) dialogView.findViewById(R.id.message);
		mConfirm = (Button) dialogView.findViewById(R.id.positiveButton);
		mCancel = (Button) dialogView.findViewById(R.id.negativeButton);

		dialog = new Dialog(this.getContext(), R.style.DialogStyle);
		dialog.setContentView(dialogView);
		dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dialog.setCanceledOnTouchOutside(true);

		// 设置对话框的大小
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CENTER);
		lp.width = ScreenUtils.dip2px(this.getContext(), 400); // 宽度
		lp.height = ScreenUtils.dip2px(this.getContext(), 300); // 高度

		dialogWindow.setAttributes(lp);
	}

	/**
	 * 初始化对话框UI
	 */
	private void initSignalDialog() {
		SignalDialogLayout dialogView = new SignalDialogLayout(getContext());
		dialogView.setCallback((source)->{
			shortSource = source;
		});

		signalDialog = new Dialog(this.getContext(), R.style.DialogStyle);
		signalDialog.setContentView(dialogView);
		signalDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		signalDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		signalDialog.setCanceledOnTouchOutside(true);

		// 设置对话框的大小
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CENTER);
		lp.width = ScreenUtils.dip2px(this.getContext(), 400); // 宽度
		lp.height = ScreenUtils.dip2px(this.getContext(), 300); // 高度

		dialogWindow.setAttributes(lp);
	}

	private void changeSource(int source){
		if (source == -1 || source == 30){ // 跳转到主页
			AppUtils.keyEventBySystem(KeyEvent.KEYCODE_HOME);
		} else { // 其他信号切换
			// HDMI2 DP
			if (source == 24){
				int type =  0;// HDMI2
				CmdUtils.changeTIPort(getContext(), type); // 切换TI Port
			}

			if (source >= 0 && source != 34){ // 切换到其他信号源
				LogUtils.d("source != 34");
				AppUtils.changeSignal(mContext, source);

				getContext().sendBroadcast(new Intent("com.ctv.launcher.HIDE"));
			}
		}

		// 发送SOURCE广播
		AppUtils.noticeChangeSignal(getContext(), source);
		LogUtils.d("source->%s", source);
	}

	/**
	 * 显示用户自定义对话框
	 */
	private void showSignalDialog() {
		if (!signalDialog.isShowing()){
			signalDialog.show();
		}
	}

	/**
	 * 显示用户自定义对话框
	 */
	private void showUserDefinedDialog() {
		mEditText.setVisibility(View.VISIBLE);

		// 获得当前source
		int inputSource = AppUtils.getCurrentSource(getContext());

		Log.d(TAG, "显示用户自定义对话框, getCurrentSource->" + inputSource);

		SignalInfo signalInfo = querySourceName(inputSource);
		mEditText.setText(signalInfo.getName());
		mEditText.setSelection(signalInfo.getName().length());//将光标移至文字末尾
		mEditText.setFilters(new InputFilter[]{new MaxTextLengthFilter(11)});
		mMessage.setVisibility(View.GONE);
		mTitle.setVisibility(View.VISIBLE);
		mTitle.setText(R.string.title_tip_user_defined);

		mConfirm.setOnClickListener(v -> {
			String text = mEditText.getText().toString();
			if (!TextUtils.isEmpty(text)){
				updateSourceName(signalInfo.getSourceId(), text);
			}
//			Toast.makeText(getContext(), "自定义sourceName：" + text + " SourceId:" + signalInfo.getSourceId(), Toast.LENGTH_LONG).show();
			dialog.dismiss();
		});

		mCancel.setOnClickListener(v -> {
			Log.d(TAG, "取消修改..");
			dialog.dismiss();
		});
		dialog.show();
	}

	class MaxTextLengthFilter implements InputFilter {
		private int mMaxLength;
		private Toast toast;

		public MaxTextLengthFilter(int max){
			mMaxLength = max - 1;
			toast = Toast.makeText(getContext(),getContext().getResources().getString(R.string.tip_max_text, "" + mMaxLength),Toast.LENGTH_LONG);
			toast.setGravity(Gravity.TOP, 0, 300);
		}

		public CharSequence filter(CharSequence source, int start, int end,
		                           Spanned dest, int dstart , int dend){
			int keep = mMaxLength - (dest.length() - (dend - dstart));
			if(keep < (end - start)){
				toast.show();
			}
			if(keep <= 0){
				return "";
			}else if(keep >= end - start){
				return null;
			}else{
				return source.subSequence(start,start + keep);
			}
		}
	}

	/**
	 * 修改source名称
	 * @param inputSource
	 */
	public SignalInfo querySourceName(int inputSource) {
		// 获得信号源参数值

		int[] idList = {};
		String[] nameList = null;
		if("JPE".equals(AppUtils.client)){
			if("CV648H_L".equals(AppUtils.clientBoard)){
				nameList = getResources().getStringArray(com.protruly.floatwindowlib.R.array.input_source_name_list_jpe_l);
				idList = getResources().getIntArray(R.array.input_source_id_list_jpe_l);
			}else if(("CN648_K55".equals(AppUtils.clientBoard))){
				nameList = getResources().getStringArray(com.protruly.floatwindowlib.R.array.input_source_name_list_jpe_kn);
				idList = getResources().getIntArray(R.array.input_source_id_list_jpe_kn);
			}else{
				nameList = getResources().getStringArray(com.protruly.floatwindowlib.R.array.input_source_name_list_jpe);
				idList = getResources().getIntArray(R.array.input_source_id_list_jpe);
			}
		}else if("ZX".equals(AppUtils.client)){
			nameList = getResources().getStringArray(com.protruly.floatwindowlib.R.array.input_source_name_list_zx);
			idList = getResources().getIntArray(R.array.input_source_id_list_zx);
		}else if("CM".equals(AppUtils.client)){
			nameList = getResources().getStringArray(com.protruly.floatwindowlib.R.array.input_source_name_list_cm);
			idList = getResources().getIntArray(R.array.input_source_id_list_cm);
		}else if("HLC".equals(AppUtils.client)){
			nameList = getResources().getStringArray(com.protruly.floatwindowlib.R.array.input_source_name_list_hlc);
			idList = getResources().getIntArray(R.array.input_source_id_list_hlc);
		}else{
			if("CV648H_L".equals(AppUtils.clientBoard)){
				nameList = getResources().getStringArray(com.protruly.floatwindowlib.R.array.input_source_name_list_edu_l);
				idList = getResources().getIntArray(R.array.input_source_id_list_edu_l);
			}else{
				nameList = getResources().getStringArray(com.protruly.floatwindowlib.R.array.input_source_name_list);
				idList = getResources().getIntArray(R.array.input_source_id_list);
			}
		}

		ContentResolver contentResolver = getContext().getContentResolver();
		String uriString = "content://com.cultraview.ctvmenu/sourcename/query";
		Uri uri = Uri.parse(uriString);
		String where = "source_id=?";
		String[] where_args = {inputSource + ""};
		Cursor cursor = contentResolver.query(uri, null, where,
				where_args, null);
		String sourceName = "";
		int sourceId = -1;
		if (cursor != null && cursor.moveToFirst()){ // 选择自定义名称
			do {
				sourceName = cursor.getString(cursor.getColumnIndex("editName"));
				if (!TextUtils.isEmpty(sourceName)){ // 编辑名为空，则选择默认名称
					sourceId = inputSource;
					break;
				}
			} while (cursor.moveToNext());
		}

		if (TextUtils.isEmpty(sourceName)){ // 查询的结果为空,则从xml中获得
			int len = idList.length;

			for (int i =0; i < len; i++){
				sourceId = idList[i];
				if (sourceId == inputSource){ // 找到source
					sourceName = nameList[i];
					break;
				}
			}
		}

		if (TextUtils.isEmpty(sourceName)){
			sourceName = nameList[nameList.length - 1];
		}

		SignalInfo signalInfo = new SignalInfo(sourceId, sourceName);
		Log.d(TAG, "querySourceName inputSource->" + signalInfo.toString());

		return signalInfo;
	}

	/**
	 * 修改source名称
	 * @param inputSource
	 * @param sourceName
	 */
	public void updateSourceName(int inputSource, String sourceName) {
		Log.d(TAG, "inputSource->" + inputSource + " sourceName->" + sourceName);

		ContentResolver contentResolver = getContext().getContentResolver();
		String uriString = "content://com.cultraview.ctvmenu/sourcename/update";
		Uri uri = Uri
				.parse(uriString);
		ContentValues values = new ContentValues();
		values.put("editName", sourceName);
		String where = "source_id=?";
		String[] where_args = {inputSource + ""};
		contentResolver.update(uri, values, where, where_args);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		viewWidth = MeasureSpec.getSize(widthMeasureSpec);
		viewHeight = MeasureSpec.getSize(heightMeasureSpec);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	/**
	 * 初始化UI
	 */
	private void initView() {
		rootView = findViewById(R.id.root_right_menu);
		viewWidth = rootView.getLayoutParams().width;
		viewHeight = rootView.getLayoutParams().height;

		// 箭头按钮
		rlBtnArrow = findViewById(R.id.rl_btn_arrow);
		btnArrow = (ImageView) findViewById(R.id.btn_arrow);

		// 菜单按钮
		menuLayout = (LinearLayout) findViewById(R.id.menu_layout);

		btnBack = findViewById(R.id.rl_btn_back);
		btnHome = findViewById(R.id.rl_btn_home);
		btnAppSwitch = findViewById(R.id.rl_btn_app_switch);
		btnShowSetting = findViewById(R.id.rl_btn_show_setting);

		rlBtnWhiteBoard = findViewById(R.id.rl_btn_whiteboard);
		rlUserDefined = findViewById(R.id.rl_btn_user_defined);

		rlBtnPC = findViewById(R.id.rl_btn_user_pc);

		// 添加menu菜单隐藏和显示的动画
		int inAnimResId =  R.anim.left_in;
		int outAnimResId =  R.anim.left_out;
		if (isRight){ // 右边
			inAnimResId =  R.anim.right_in;
			outAnimResId =  R.anim.right_out;
		}
		showAnim = AnimationUtils.loadAnimation(getContext(), inAnimResId);
//		showAnim.setAnimationListener(showAnimationListener);
		hideAnim = AnimationUtils.loadAnimation(getContext(), outAnimResId);
		hideAnim.setAnimationListener(hideAnimationListener);
	}

    @Override
    public boolean onTouchEvent(MotionEvent event) {
		if (actionType <= 1){
			return super.onTouchEvent(event);
		}
	    mHandler.removeCallbacks(shrinkRunnable);

        // 收起来更新窗口
        if (FloatWindowManager.getDownloadWindow() != null){
            FloatWindowManager.getDownloadWindow().setVisibility(INVISIBLE);
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:{
                // 手指按下时记录必要数据,纵坐标的值都需要减去状态栏高度
                xInView = event.getX();
                yInView = event.getY();
                xDownInScreen = event.getRawX();
                yDownInScreen = event.getRawY() - ScreenUtils.getStatusBarHeight(getContext());
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - ScreenUtils.getStatusBarHeight(getContext());
                break;
            }
            case MotionEvent.ACTION_MOVE:{
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - ScreenUtils.getStatusBarHeight(getContext());

                // 手指移动的时候悬浮窗的位置
	            int dy = (int)Math.abs(yInScreen - yDownInScreen);
	            if (dy > ScreenUtils.dip2px(getContext(), 20)){
		            isMenuMove = true;
		            updateViewPosition();
		            return true;
	            }

                break;
            }
            case MotionEvent.ACTION_UP:{
                // 如果手指离开屏幕时，xDownInScreen和xInScreen相等，且yDownInScreen和yInScreen相等，则视为触发了单击事件。
                if (Math.abs(xDownInScreen - xInScreen) < 20 && Math.abs(yDownInScreen - yInScreen) < 20) {
                    int arrowW = rlBtnArrow.getWidth() + rootView.getPaddingLeft() + rootView.getPaddingRight();
                    int arrowH = rlBtnArrow.getHeight() + rootView.getPaddingBottom() + rootView.getPaddingTop();

                    // 判断是否点击了箭头按钮
                    boolean flagW = (int)(arrowW * MIN_CLICK_SCALE) < xInView && xInView < arrowW;
                    boolean flagH = (int)(arrowH * MIN_CLICK_SCALE) < yInView && yInView < arrowH;
                    if (flagW && flagH){
	                    mHandler.removeCallbacks(shrinkRunnable);
                        changeMenuView();
                    }
                }

	            mHandler.postDelayed(shrinkRunnable, 5000);
                break;
            }
            default:
                break;
        }

        return false;
    }

	/**
	 * 更新位置
	 */
	private void updateViewPosition() {
		int dx = (int) (xInScreen - xInView);
		int dy = (int) (yInScreen - yInView);

		if (dy > ScreenUtils.dip2px(getContext(), 10)){
			FloatWindowManager.updateMenuWindow(getContext(), dx, dy);
		}

		// 设置菜单栏位置的改变标识为true
//        FloatWindowManager.isMenuParamsChange = true;
	}

	/**
	 * 设置参数
	 *
	 * @param menuParams
	 */
	public void setMenuParams(WindowManager.LayoutParams menuParams) {
		this.menuParams = menuParams;
	}

	/**
	 * 设置监听
	 */
	private void setListener() {
		if (actionType <= 1){
			rlBtnArrow.setOnClickListener(mOnClickListener);
		}

		btnBack.setOnClickListener(mOnClickListener);
		btnHome.setOnClickListener(mOnClickListener);
		btnAppSwitch.setOnClickListener(mOnClickListener);
		btnShowSetting.setOnClickListener(mOnClickListener);
		rlBtnWhiteBoard.setOnClickListener(mOnClickListener);
		rlUserDefined.setOnClickListener(mOnClickListener);
		rlBtnPC.setOnClickListener(mOnClickListener);
		rlBtnPC.setOnLongClickListener(mOnLongClickListener);
	}

	/**
	 * 显示批注
	 */
	private void showAnnotate() {
		changeMenuView();

		// 发送广播，启动批注
		getContext().sendBroadcast(new Intent("com.cultraview.annotate.broadcast.OPEN"));

		int source = AppUtils.getCurrentSource();
		if (source == 25){ // 当前为内置电脑时,关闭USB触控
			new Thread(()->{
				SystemClock.sleep(50);
				CmdUtils.changeUSBTouch(getContext(), false);  // 关闭USB触控
				SystemClock.sleep(200);

//				SystemProperties.set("ctv.usbTouch","close_usb_touch");
				Settings.System.putInt(ControlMenuLayout.this.getContext().getContentResolver(), "annotate.start", 1);
				int isStart = Settings.System.getInt(ControlMenuLayout.this.getContext().getContentResolver(), "annotate.start", 0);

				Log.d(TAG, "showAnnotate annotate.start->" + isStart);
			}).start();
		}
	}

	/**
	 * 改变菜单栏
	 */
	private void changeMenuView() {
		if (!isShrink) { // 收起菜单
			shrinkMenu();
		} else {  // 展开菜单
			unfoldMenu();
		}
	}

	/**
	 * 收缩菜单
	 */
	public void onlyShrinkMenu() {
		if (isRight) {
			btnArrow.setImageResource(R.drawable.btn_arrow_left_normal);
		} else {
			btnArrow.setImageResource(R.drawable.btn_arrow_right_normal);
		}

		isShrink = true;
		menuLayout.setVisibility(View.GONE);
		menuLayout.clearAnimation();
	}

	/**
	 * 收缩菜单
	 */
	public void shrinkMenu() {
		if (isRight) {
			btnArrow.setImageResource(R.drawable.btn_arrow_left_normal);
		} else {
			btnArrow.setImageResource(R.drawable.btn_arrow_right_normal);
		}

		if (!isShrink){ // 若是展开时，则收缩
			isShrink = true;
			if (menuLayout.getVisibility() == View.VISIBLE){
				menuLayout.clearAnimation();
				menuLayout.startAnimation(hideAnim);
			}
		}
	}

	/**
	 * 展开菜单
	 */
	public void unfoldMenu() {
		SPUtil.saveData(getContext(), "lastUnfoldMenu", isRight);

		// 改变箭头按钮的状态
		if (isRight) {
			btnArrow.setImageResource(R.drawable.btn_arrow_right_normal);
		} else {
			btnArrow.setImageResource(R.drawable.btn_arrow_left_normal);
		}

		if (isShrink){ // 若是收缩时，则展开
			isShrink = false;
			if (menuLayout.getVisibility() != View.VISIBLE){
				menuLayout.setVisibility(View.VISIBLE);
				menuLayout.clearAnimation();
				menuLayout.startAnimation(showAnim);
			}
		}

		// 一方展开，另一方收起
		if (isRight) { // 当前为右边菜单，则左边菜单收起
			if (!FloatWindowManager.getMenuWindowLeft().isShrink()){
				FloatWindowManager.getMenuWindowLeft().shrinkMenu();
			}
		} else { // 当前为左边菜单，则右边菜单收起
			if (!FloatWindowManager.getMenuWindow().isShrink()){
				FloatWindowManager.getMenuWindow().shrinkMenu();
			}
		}
	}

	/**
	 * 显示设置界面
	 */
	private void showSetting() {
		// 打开设置界面
		MyUtils.openSettingActivity(getContext(), isRight);
	}

	/**
	 * 显示用户自定义信号源对话框
	 */
	private void showUserDefined() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		} else {
			showUserDefinedDialog();
		}
	}

	/**
	 * 关闭其他界面
	 */
	private void closeOtherUI() {
		// 收起来更新窗口
		if (FloatWindowManager.getDownloadWindow() != null) {
			FloatWindowManager.getDownloadWindow().setVisibility(View.GONE);
		}
	}

	/**
	 * 是否收缩菜单
	 *
	 * @return
	 */
	public boolean isShrink() {
		return isShrink;
	}

	/**
	 * 开始收缩动作
	 */
	public void shrinkAction(){
		mHandler.removeCallbacks(shrinkRunnable);
		mHandler.postDelayed(shrinkRunnable, 5000);
	}

	/**
	 * 收缩动作
	 */
	Runnable shrinkRunnable = ()-> {
		int source = AppUtils.getCurrentSource();
		if (source >= 0 && source != 34){
			Log.d(TAG, "shrinkRunnable 在非Android通道下, source->" + source);
			return;
		}

		// 在信号源UI时
		ControlMenuLayout left = FloatWindowManager.createMenuWindowLeft(getContext());
		ControlMenuLayout right = FloatWindowManager.createMenuWindow(getContext());
		int visible = View.VISIBLE;

		if (left.getVisibility() != visible){
			left.setVisibility(visible);
		}
		if (right.getVisibility() != visible){
			right.setVisibility(visible);
		}

		shrinkMenu();
	};

	private OnLongClickListener mOnLongClickListener = v -> {
		if (ViewUtils.isFastDoubleClick()) {
			return false;
		}

		showSignalDialog();

		return true;
	};

	/**
	 * 点击事件监听
	 */
	private OnClickListener mOnClickListener = v -> {
		if (ViewUtils.isFastDoubleClick()) {
			return;
		}

		int source = AppUtils.getCurrentSource();
		if (source >= 0 && source != 34){
			Log.d(TAG, "在非Android通道下, source->" + source);
			if (FloatWindowService.mDataHandler != null){
				FloatWindowService.mDataHandler.sendEmptyMessageDelayed(1, 500);
			}
		} else { // 收缩处理
			mHandler.removeCallbacks(shrinkRunnable);
			mHandler.postDelayed(shrinkRunnable, 5000);
		}

		// 关闭其他界面
		closeOtherUI();

		int id = v.getId();

		if (id != R.id.rl_btn_show_setting) {
			MyUtils.closeSettingActivity();
		}

		switch (id) {
			case R.id.rl_btn_arrow: {// 展开和收缩菜单
				changeMenuView();
				break;
			}
			case R.id.rl_btn_whiteboard: {// 批注显示
				showAnnotate();
				break;
			}
			case R.id.rl_btn_back: { // 返回
				AppUtils.keyEventBySystem(KeyEvent.KEYCODE_BACK);
				break;
			}
			case R.id.rl_btn_home: { // 回到主页
				AppUtils.keyEventBySystem(KeyEvent.KEYCODE_HOME);
				// 发送SOURCE广播
				AppUtils.noticeChangeSignal(getContext(), -1);
				break;
			}
			case R.id.rl_btn_app_switch: {// 多任务列表
				// 任务界面
				AppUtils.keyEventBySystem(KeyEvent.KEYCODE_APP_SWITCH);
				// 发送SOURCE广播
				AppUtils.noticeChangeSignal(getContext(), -1);
				break;
			}
			case R.id.rl_btn_show_setting: {// 显示更多设置界面
				if (AppUtils.isActivityRunning(getContext(),"com.android.systemui.recents.RecentsActivity")){
					// 任务界面
					AppUtils.keyEventBySystem(KeyEvent.KEYCODE_APP_SWITCH);

					mHandler.postDelayed(()->{
						showSetting();
					}, 500);
				} else {
					showSetting();
				}
				break;
			}
			case R.id.rl_btn_user_defined: {// 用户自定义
				showUserDefined();
				break;
			}
			case R.id.rl_btn_user_pc: {// 快捷PC
				changeSource(shortSource);
				break;
			}
			default: {
				break;
			}
		}
	};

	public static final int MSG_SHRINK_MENU_TOOLS = 1;

	/**
	 * UI异步处理
	 */
	public static final class UIHandler extends Handler {
		WeakReference<ControlMenuLayout> weakReference;

		public UIHandler(ControlMenuLayout activity) {
			super();
			this.weakReference = new WeakReference<>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			ControlMenuLayout menuLayout = weakReference.get();
			if (menuLayout == null){
				return;
			}

			switch (msg.what){
				case MSG_SHRINK_MENU_TOOLS:{ // 操作栏
//					menuLayout.handleShrink();
					break;
				}
				default:{
					break;
				}
			}
		}
	}

	/**
	 * 隐藏的动画
	 */
	private Animation.AnimationListener hideAnimationListener = new Animation.AnimationListener() {
		@Override
		public void onAnimationStart(Animation animation) {
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			menuLayout.setVisibility(View.GONE);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}
	};
}
