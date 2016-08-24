package net.nmss.nice.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import net.nmss.nice.R;
import net.nmss.nice.bean.NiceUserInfo;
import net.nmss.nice.bean.RoomBean;
import net.nmss.nice.utils.ImageLoaderUtils;
import net.nmss.nice.widget.RoundImageView;
import net.nmss.nice.utils.LogUtil;
import net.nmss.nice.utils.AsyncHttpRequestUtil;
import net.nmss.nice.utils.MD5Utils;
import net.nmss.nice.utils.NiceConstants;
import net.nmss.nice.utils.UrlHelper;
import net.nmss.nice.adapters.MyHomePageAdapter;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MyHomePageActivity extends BaseActivity implements
		OnRefreshListener2<ListView> {
	private final static String LOG_TAG = "MyHomePageActivity";
	private PullToRefreshListView lv;
	private List<RoomBean> bean;
	private RoomBean room;
	private MyHomePageAdapter adapter;
	private RequestHandle firstPageHandler;
	private RequestHandle nextPageHandler;
	private int currentPage = 1;
	private int lvFirstViewPosition;
	private TextView tvTitle;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homepage_layout);
		initView();
	}

	private void initView() {
		lv = (PullToRefreshListView) this.findViewById(R.id.lv_homepage);
		tvTitle = (TextView) this.findViewById(R.id.title_center_content);
		tvTitle.setText("我的主页");
		View view = View.inflate(this, R.layout.myhomepage_head_layout, null);
		RoundImageView headImg = (RoundImageView) view
				.findViewById(R.id.myhomepage_head_img);

		TextView userName = (TextView) view
				.findViewById(R.id.myhomepage_head_name);
		TextView userDeclaration = (TextView) view
				.findViewById(R.id.myhomepage_head_declaration);
		setDateView(headImg, userName, userDeclaration);
		lv.getRefreshableView().addHeaderView(view);
		lv.getRefreshableView().setAdapter(adapter);
		lv.getRefreshableView().setSelection(lvFirstViewPosition);
		lv.setMode(PullToRefreshBase.Mode.BOTH);
		lv.setOnRefreshListener(this);
	}
	
	public void doClick(View v){
		switch (v.getId()) {
		case R.id.myhomepage_friends_lt:
			
			break;
			
		case R.id.myhomepage_fans_lt:
			break;

		case R.id.title_left_img:
			finish();
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onResume() {
		// TODO 自动生成的方法存根
		getFirstPage(false);
		super.onResume();
	}
	
	private void setDateView(RoundImageView img, TextView name, TextView delt) {
		NiceUserInfo info = NiceUserInfo.getInstance();
		ImageLoaderUtils.getInstance().displayImageWithCache(
				info.getHead_pic(), img);
		name.setText(info.getName());
		delt.setText(info.getDeclaration());
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO 自动生成的方法存根
		getFirstPage(true);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO 自动生成的方法存根
		LogUtil.i(LOG_TAG, "onPullUpToRefresh");
		if (nextPageHandler != null && !nextPageHandler.isFinished()) {
			Toast.makeText(this, "正在获取,请稍候...", Toast.LENGTH_SHORT).show();
			return;
		}
		if (firstPageHandler != null && !firstPageHandler.isFinished()) {
			firstPageHandler.cancel(true);
		}
		nextPageHandler = getHomeAgeList(++currentPage, true, true);
	}
	
	private void getFirstPage(boolean isRefresh) {
		// TODO 自动生成的方法存根
		if (firstPageHandler != null && !firstPageHandler.isFinished()) {
			Toast.makeText(this, "正在获取,请稍候...", Toast.LENGTH_SHORT).show();
			return;
		}
		if (nextPageHandler != null && !nextPageHandler.isFinished()) {
			nextPageHandler.cancel(true);
		}
		currentPage = 1;
		firstPageHandler = getHomeAgeList(1, isRefresh, false);
	}
	
	private RequestHandle getHomeAgeList(int pageNum, final boolean isRefresh,
			final boolean isAppend) {
		RequestParams params = new RequestParams();
		params.put(NiceConstants.UID, NiceUserInfo.getInstance().getUId());
		params.put(NiceConstants.PAGE, String.valueOf(pageNum));
		params.put(NiceConstants.sign, MD5Utils.getSign());
		String url = UrlHelper.getAbsoluteUrl(UrlHelper.USERMESSAGE);
		LogUtil.i(LOG_TAG, params.toString());
		LogUtil.i(LOG_TAG, "url:" + url);
		return AsyncHttpRequestUtil.get(url, params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onStart() {
						showProgress();
						super.onStart();
					}

					@Override
					public void onSuccess(int statusCode, String content) {
						LogUtil.i(LOG_TAG, "firstPageHandler -> onSuccess:"
								+ content);
						hideProgress();
						parseHomeAgeListResponseJson(content, isAppend);
					}

					@Override
					public void onFailure(int statusCode, Throwable error,
							String content) {
						hideProgress();
						LogUtil.i(LOG_TAG, "firstPageHandler -> onFailure:"
								+ content);
						Toast.makeText(MyHomePageActivity.this, "网络不给力...", Toast.LENGTH_SHORT)
								.show();
					}

					@Override
					public void onFinish() {
						if (isRefresh)
							lv.onRefreshComplete();
						super.onFinish();
					}
				});
	}

	private void parseHomeAgeListResponseJson(String jsonStr, boolean isAppend) {
		try {
			JSONObject object = new JSONObject(jsonStr);
			int r_e = object.getInt(NiceConstants.RECODE);
			if (r_e != 0) {
				Toast.makeText(MyHomePageActivity.this,
						object.getString(NiceConstants.RECONTENT),
						Toast.LENGTH_SHORT).show();
				return;
			}

			if (r_e == 0) {
				JSONArray js = object.getJSONArray(NiceConstants.RECONTENT);
				if (js.length() == 0) {
					return;
				}
				parseJSList(js);
			}
		} catch (JSONException e) {
			// TODO: handle exception
			LogUtil.i(
					LOG_TAG,
					"parseHomeAgeListResponseJson->JSONException:"
							+ e.getMessage());
		}
	}
	
	private void parseJSList(JSONArray js) {
		try {
			bean = new ArrayList<RoomBean>();
			for (int i = 0; i < js.length(); i++) {
				JSONObject object = js.getJSONObject(i);
				room = new RoomBean();
				room.setUid(object.getString("uid"));
				room.setNickname(object.getString("nickname"));
				room.setMessageId(object.getString("message_id"));
				room.setMessageTime(object.getString("message_time"));
				room.setMessageContent(object.getString("message_content"));
				room.setBigPic(object.getString("big_pic"));
				room.setSmallPic(object.getString("small_pic"));
				room.setHeadPic(object.getString("head_pic"));
				room.setDiggNum(object.getInt("digg_num"));
				room.setCommentNum(object.getInt("comment_num"));
				room.setDigg(object.getInt("digg_status") == 1 ? true : false);
				bean.add(room);
			}
			handler.sendEmptyMessage(0);
		} catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO 自动生成的方法存根
			switch (msg.what) {
			case 0:
				adapter = new MyHomePageAdapter(MyHomePageActivity.this, bean);
				lv.setAdapter(adapter);

				break;
			default:
				break;
			}
		}
	};
}
