package com.d3.yiqi.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.XMPPException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.d3.yiqi.R;
import com.d3.yiqi.adapter.ChatAdapter;
import com.d3.yiqi.listener.ImgInterceptor;
import com.d3.yiqi.model.Expressions;
import com.d3.yiqi.model.OneFridenMessages;
import com.d3.yiqi.service.XmppApplication;
import com.d3.yiqi.service.XmppConnection;
import com.d3.yiqi.service.XmppService;

public class ChatActivity extends Activity implements OnClickListener{

	private ChatAdapter adapter;
	private String pFRIENDID;
	private EditText msgText;
	private TextView chat_name;
	private Button mBtnBack;
	private Button btsend;
	private ListView listview;
	private Chat newchat;
	// 我们需要知道，收到了新的消息和发出了新的消息
	UpMessageReceiver mUpMessageReceiver;
	OneFridenMessages friendMessageBean;

	private ViewPager viewPager;
	private ArrayList<GridView> grids;
	private int[] expressionImages;
	private String[] expressionImageNames;
	private int[] expressionImages1;
	private String[] expressionImageNames1;
	private int[] expressionImages2;
	private String[] expressionImageNames2;
	private ImageButton biaoqingBtn;
	private ImageButton biaoqingfocuseBtn;
	private LinearLayout page_select;
	private ImageView page0;
	private ImageView page1;
	private ImageView page2;
	private GridView gView1;
	private GridView gView2;
	private GridView gView3;

	public static String img_path;
	private static final int CHOOSE_PICTURE = 0;
	private static final int TAKE_PICTURE = 1;
	private static final int MODIFY_FINISH =  2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.chat_client);
		this.pFRIENDID = getIntent().getStringExtra("FRIENDID");
		//获得聊天记录
		if(XmppApplication.AllFriendsMessageMapData.get(pFRIENDID)==null)
			XmppApplication.dbHelper.getChatMsg(pFRIENDID);
		//会话内容改变，接受广播
		mUpMessageReceiver = new UpMessageReceiver();
		IntentFilter messageIntentFilter = new IntentFilter(XmppApplication.XMPP_UP_MESSAGE_ACTION);
		messageIntentFilter.addDataScheme("xmpp");
		messageIntentFilter.addDataAuthority(XmppService.getUsername(pFRIENDID), null);
		registerReceiver(mUpMessageReceiver, messageIntentFilter);

		//创建回话
		ChatManager cm = XmppConnection.getConnection().getChatManager();
		//发送消息给pc服务器的好友（获取自己的服务器，和好友）
		newchat = cm.createChat(pFRIENDID, null);
		//消息监听
		friendMessageBean = XmppApplication.AllFriendsMessageMapData.get(pFRIENDID);
		if (null == friendMessageBean) {
			friendMessageBean = new OneFridenMessages();
			XmppApplication.AllFriendsMessageMapData.put(pFRIENDID,friendMessageBean);
		}

		initView();
		chat_name.setText(XmppService.getUsername(pFRIENDID));           //会话的名称，好友名字
		initViewPager();
		//新消息设为0
		XmppApplication.AllFriendsMessageMapData.get(pFRIENDID).NewMessageCount = 0;
		XmppApplication.sharedPreferences.edit().putInt(pFRIENDID+XmppApplication.user,0).commit();


		//上拉刷新，待完成
		listview.setOnScrollListener(new OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
					// 当不滚动时
					case OnScrollListener.SCROLL_STATE_IDLE:
						if (listview.getFirstVisiblePosition() == 0) {
							System.out.println("up");
							XmppApplication.dbHelper.getChatMsgByFriendMore(listview.getCount(), pFRIENDID);
							adapter.notifyDataSetChanged();
						}
						break;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
								 int visibleItemCount, int totalItemCount) {
			}
		});

	}

	private void initView(){
		listview = (ListView) findViewById(R.id.formclient_listview);
		listview.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		chat_name = (TextView) findViewById(R.id.chat_name);
		// 获取文本信息
		msgText = (EditText) findViewById(R.id.formclient_text);
		msgText.setOnClickListener(this);
		// 返回按钮
		mBtnBack = (Button) findViewById(R.id.chat_back);
		mBtnBack.setOnClickListener(this);
		// 发送消息
		btsend = (Button) findViewById(R.id.formclient_btsend);
		btsend.setOnClickListener(this);
		this.adapter = new ChatAdapter(this,friendMessageBean);
		listview.setAdapter(adapter);
		//表情导航
		page_select = (LinearLayout) findViewById(R.id.page_select);
		page0 = (ImageView) findViewById(R.id.page0_select);
		page1 = (ImageView) findViewById(R.id.page1_select);
		page2 = (ImageView) findViewById(R.id.page2_select);
		// 引入表情
		expressionImages = Expressions.expressionImgs;
		expressionImageNames = Expressions.expressionImgNames;
		expressionImages1 = Expressions.expressionImgs1;
		expressionImageNames1 = Expressions.expressionImgNames1;
		expressionImages2 = Expressions.expressionImgs2;
		expressionImageNames2 = Expressions.expressionImgNames2;
		// 创建ViewPager
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		// 表情
		biaoqingBtn = (ImageButton) findViewById(R.id.chatting_biaoqing_btn);
		biaoqingBtn.setOnClickListener(this);
		biaoqingfocuseBtn = (ImageButton) findViewById(R.id.chatting_biaoqing_focuse_btn);
		biaoqingfocuseBtn.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.formclient_text:
				removeExpression();
				break;
			case R.id.chat_back:
				showPicturePicker(ChatActivity.this);
				break;
			case R.id.formclient_btsend:
				sendMsg();
				break;
			case R.id.chatting_biaoqing_btn:
				showExpression(v);
				break;
			case R.id.chatting_biaoqing_focuse_btn:
				removeExpression();
				break;
			default:
				break;
		}
	}


	public void showPicturePicker(Context context){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("图片来源");
		builder.setNegativeButton("取消", null);
		builder.setItems(new String[]{"相册","拍照"}, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				img_path = XmppConnection.SAVE_PATH + "/tmp_pic_"+ SystemClock.currentThreadTimeMillis()+".jpg";
				switch (which) {
					case CHOOSE_PICTURE:
						Intent intent = new Intent(Intent.ACTION_PICK);
						intent.setType("image/*");
						startActivityForResult(intent,CHOOSE_PICTURE);
						break;

					case TAKE_PICTURE:
						String status = Environment.getExternalStorageState();
						if (status.equals(Environment.MEDIA_MOUNTED)) {
							try {
								File filePath = new File(XmppConnection.SAVE_PATH);
								if (!filePath.exists()) {
									filePath.mkdirs();
								}
								Intent picIntent = new Intent(
										android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
								File f = new File(img_path);
								// localTempImgDir和localTempImageFileName是自己定义的名字
								Uri u = Uri.fromFile(f);
								picIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
								picIntent.putExtra(MediaStore.EXTRA_OUTPUT, u);
								startActivityForResult(picIntent, TAKE_PICTURE);
							} catch (ActivityNotFoundException e) {
								//
							}
						}
						break;

					default:
						break;
				}
			}
		});
		builder.create().show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case CHOOSE_PICTURE:
					if (data != null) {
						Uri uri = data.getData();
						if (!TextUtils.isEmpty(uri.getAuthority())) {
							Cursor cursor = getContentResolver().query(uri,
									new String[] { MediaStore.Images.Media.DATA },
									null, null, null);
							if (null == cursor) {
								Toast.makeText(getApplicationContext(), "图片没找到", Toast.LENGTH_SHORT).show();
								return;
							}
							cursor.moveToFirst();
							String path = cursor.getString(cursor
									.getColumnIndex(MediaStore.Images.Media.DATA));
							cursor.close();
							Intent intent = new Intent(this, CropImageActivity.class);
							intent.putExtra("path", path);
							startActivityForResult(intent, MODIFY_FINISH);
						} else {
							Intent intent = new Intent(this, CropImageActivity.class);
							intent.putExtra("path", uri.getPath());
							startActivityForResult(intent, MODIFY_FINISH);
						}
					}
					break;
				case TAKE_PICTURE:
					File f = new File(img_path);
					Intent intent = new Intent(this, CropImageActivity.class);
					intent.putExtra("path", f.getAbsolutePath());
					startActivityForResult(intent, MODIFY_FINISH);
					break;
				case MODIFY_FINISH:
					if (data != null) {
						final String path = data.getStringExtra("path");
						try {
							ImgInterceptor.sendFile(pFRIENDID+"/Smack",new File(path));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					break;

				default:
					break;
			}
		}
	}


	private void sendMsg(){
		String msg = msgText.getText().toString();	//获取text文本

		if(!XmppConnection.getConnection().isConnected())
		{
			Toast.makeText(ChatActivity.this, "当前网络不可用，请检查你的网络设置", Toast.LENGTH_SHORT).show();
		}
		else if(msg.length() > 0){
			try {
				newchat.sendMessage(msg);
			}catch (XMPPException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			Toast.makeText(ChatActivity.this, "发送信息不能为空", Toast.LENGTH_SHORT).show();
		}
		//清空text
		msgText.setText("");
	}

	private void showExpression(View view){
		InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
		biaoqingBtn.setVisibility(biaoqingBtn.GONE);
		biaoqingfocuseBtn.setVisibility(biaoqingfocuseBtn.VISIBLE);
		viewPager.setVisibility(viewPager.VISIBLE);
		page_select.setVisibility(page_select.VISIBLE);
	}

	private void removeExpression(){
		biaoqingBtn.setVisibility(biaoqingBtn.VISIBLE);
		biaoqingfocuseBtn.setVisibility(biaoqingfocuseBtn.GONE);
		viewPager.setVisibility(viewPager.GONE);
		page_select.setVisibility(page_select.GONE);
	}


	private void initViewPager(){
		LayoutInflater inflater = LayoutInflater.from(this);
		grids = new ArrayList<GridView>();
		gView1 = (GridView) inflater.inflate(R.layout.grid1, null);

		setPage(page0, gView1, expressionImages, expressionImageNames);
		grids.add(gView1);

		gView2 = (GridView) inflater.inflate(R.layout.grid2, null);
		grids.add(gView2);

		gView3 = (GridView) inflater.inflate(R.layout.grid3, null);
		grids.add(gView3);

		// 填充ViewPager的数据适配器
		PagerAdapter mPagerAdapter = new PagerAdapter() {
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return grids.size();
			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager) container).removeView(grids.get(position));
			}

			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager) container).addView(grids.get(position));
				return grids.get(position);
			}

			@Override
			public void finishUpdate(View arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void restoreState(Parcelable arg0, ClassLoader arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public Parcelable saveState() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void startUpdate(View arg0) {
				// TODO Auto-generated method stub

			}

		};
		viewPager.setAdapter(mPagerAdapter);
		viewPager.setOnPageChangeListener(new GuidePageChangeListener());
	}

	@Override
	protected void onResume() {
		adapter.notifyDataSetChanged();
		super.onResume();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		//新消息设为0
		XmppApplication.AllFriendsMessageMapData.get(pFRIENDID).NewMessageCount = 0;
		XmppApplication.sharedPreferences.edit().putInt(pFRIENDID+XmppApplication.user,0).commit();
		unregisterReceiver(mUpMessageReceiver);
		finish();
	}

	private class UpMessageReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 收到廣播更新我们的界面
			adapter.notifyDataSetChanged();
		}
	}

	// ** 指引页面改监听器 */
	class GuidePageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int arg0) {
			switch (arg0) {
				case 0:
					page0.setImageDrawable(getResources().getDrawable(
							R.mipmap.page_focused));
					page1.setImageDrawable(getResources().getDrawable(
							R.mipmap.page_unfocused));
					break;
				case 1:
					setPage(page1, gView2, expressionImages1, expressionImageNames1);
					break;
				case 2:
					setPage(page2, gView3, expressionImages2, expressionImageNames2);
					break;

			}
		}
	}


	public void setPage(ImageView pageFocused,GridView gridView,final int[] expressionImages,final String[] expressionImageNames){
		page0.setImageDrawable(getResources().getDrawable(
				R.mipmap.page_unfocused));
		page1.setImageDrawable(getResources().getDrawable(
				R.mipmap.page_unfocused));
		page2.setImageDrawable(getResources().getDrawable(
				R.mipmap.page_unfocused));
		pageFocused.setImageDrawable(getResources().getDrawable(
				R.mipmap.page_focused));
		List<Map<String, Object>> listItems1 = new ArrayList<Map<String, Object>>();
		// 生成24个表情
		for (int i = 0; i < 24; i++) {
			Map<String, Object> listItem = new HashMap<String, Object>();
			listItem.put("image", expressionImages[i]);
			listItems1.add(listItem);
		}

		SimpleAdapter simpleAdapter1 = new SimpleAdapter(getApplicationContext(),
				listItems1, R.layout.singleexpression,
				new String[] { "image" }, new int[] { R.id.image });
		gridView.setAdapter(simpleAdapter1);
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
									int arg2, long arg3) {
				Bitmap bitmap = null;
				bitmap = BitmapFactory.decodeResource(getResources(),
						expressionImages[arg2
								% expressionImages.length]);
				ImageSpan imageSpan = new ImageSpan(getApplicationContext(), bitmap);
				SpannableString spannableString = new SpannableString(
						expressionImageNames[arg2]
								.substring(1,
										expressionImageNames[arg2]
												.length() - 1));
				spannableString.setSpan(imageSpan, 0,
						expressionImageNames[arg2].length() - 2,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				// 编辑框设置数据
				msgText.append(spannableString);
			}
		});
	}
}