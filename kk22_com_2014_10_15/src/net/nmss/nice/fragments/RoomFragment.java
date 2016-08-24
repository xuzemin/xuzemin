package net.nmss.nice.fragments;

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
import net.nmss.nice.adapters.RoomFragmentAdapter;
import net.nmss.nice.widget.RoundImageView;
import net.nmss.nice.bean.NiceUserInfo;
import net.nmss.nice.bean.RoomBean;
import net.nmss.nice.utils.AsyncHttpRequestUtil;
import net.nmss.nice.utils.ImageLoaderUtils;
import net.nmss.nice.utils.LogUtil;
import net.nmss.nice.utils.MD5Utils;
import net.nmss.nice.utils.NiceConstants;
import net.nmss.nice.utils.UrlHelper;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RoomFragment extends Fragment implements
		OnRefreshListener2<ListView> {
	private final static String LOG_TAG = "RoomFragment";
	private View mRootView;
	private Activity mContext;
	private RoomFragmentAdapter adapter;
	private PullToRefreshListView lv;
	private RequestHandle firstPageHandler;
	private RequestHandle nextPageHandler;
	private int currentPage = 1;
	private int lvFirstViewPosition;

	@Override
	public void onAttach(Activity activity) {
		// TODO 自动生成的方法存根
		this.mContext = activity;
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		mRootView = inflater.inflate(R.layout.room_fragment_layout, null);
		initView();
		return mRootView;
	}

	@Override
	public void onResume() {
		// TODO 自动生成的方法存根
		if (adapter == null || adapter.getCount() == 0)
			getFirstDynamic(false);
		super.onResume();
	}
	
	private void initView() {
		if(adapter == null) {
			adapter = new RoomFragmentAdapter(mContext);
		}
		lv = (PullToRefreshListView) mRootView.findViewById(R.id.lv_room);
		View view = View.inflate(mContext, R.layout.room_fragment_head_layout, null);
		RoundImageView headImg = (RoundImageView) view
				.findViewById(R.id.book_fragment_head_img);

		TextView userName = (TextView) view
				.findViewById(R.id.book_fragment_head_name);
		TextView userDeclaration = (TextView) view
				.findViewById(R.id.book_fragment_head_declaration);
		setDateView(headImg, userName, userDeclaration);
		lv.getRefreshableView().addHeaderView(view);
		lv.getRefreshableView().setAdapter(adapter);
		lv.getRefreshableView().setSelection(lvFirstViewPosition);
		lv.setMode(PullToRefreshBase.Mode.BOTH);
		lv.setOnRefreshListener(this);
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
		getFirstDynamic(true);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO 自动生成的方法存根
		LogUtil.i(LOG_TAG, "onPullUpToRefresh");
		lvFirstViewPosition = lv.getRefreshableView().getFirstVisiblePosition();
		if (nextPageHandler != null && !nextPageHandler.isFinished()) {
			Toast.makeText(mContext, "正在获取,请稍候...", Toast.LENGTH_SHORT).show();
			return;
		}
		if (firstPageHandler != null && !firstPageHandler.isFinished()) {
			firstPageHandler.cancel(true);
		}
		nextPageHandler = getDynamic(++currentPage, true, true);

	}

	private void getFirstDynamic(boolean isRefresh) {
		if (firstPageHandler != null && !firstPageHandler.isFinished()) {
			Toast.makeText(mContext, "正在获取,请稍候...", Toast.LENGTH_SHORT).show();
			return;
		}
		if (nextPageHandler != null && !nextPageHandler.isFinished()) {
			nextPageHandler.cancel(true);
		}
		currentPage = 1;
		firstPageHandler = getDynamic(1, isRefresh, false);
	}

	private RequestHandle getDynamic(int pageNum, final boolean isRefresh,
			final boolean isAppend) {
		NiceUserInfo info = NiceUserInfo.getInstance();
		RequestParams params = new RequestParams();
		params.put(NiceConstants.UID, info.getUId());
		params.put(NiceConstants.PAGE, String.valueOf(pageNum));
		params.put(NiceConstants.sign, MD5Utils.getSign());
		String url = UrlHelper.getAbsoluteUrl(UrlHelper.MOODLIST);
		LogUtil.i(LOG_TAG, params.toString());
		LogUtil.i(LOG_TAG, "url:" + url);
		return AsyncHttpRequestUtil.get(url, params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onStart() {
					}

					@Override
					public void onSuccess(int statusCode, String content) {
						LogUtil.i(LOG_TAG, "firstPageHandler -> onSuccess:"
								+ content);
						parseDynamic(content, isAppend);
					}

					@Override
					public void onFailure(int statusCode, Throwable error,
							String content) {
						LogUtil.i(LOG_TAG, "firstPageHandler -> onFailure:"
								+ content);
						Toast.makeText(mContext, "网络不给力...", Toast.LENGTH_SHORT)
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
	
	private void parseDynamic(String json, boolean isAppend){
		try {
			JSONObject jsonObject = new JSONObject(json);
			int rCode = jsonObject.getInt(NiceConstants.RECODE);
			if (rCode != 200000) {
				Toast.makeText(mContext,
						jsonObject.getString(NiceConstants.RECONTENT),
						Toast.LENGTH_SHORT).show();
				return;
			}

			if (rCode == 200000) {
				JSONArray jArray = jsonObject
						.getJSONArray(NiceConstants.RECONTENT);
				if (jArray.length() == 0) {
					return;
				}
				List<RoomBean> moods = parseDynamicList(jArray);
				if (moods == null || moods.isEmpty()) {
					return;
				}
				if (isAppend) {
					this.adapter.appendMood(moods);

				} else {
					this.adapter.updateMoods(moods);
					lv.getRefreshableView().setSelection(0);
				}
				lv.getRefreshableView().setSelection(lvFirstViewPosition);
			}

		} catch (JSONException e) {
			LogUtil.i(
					LOG_TAG,
					"parseMoodListResponseJson->JSONException:"
							+ e.getMessage());
		}
	}
	
	private List<RoomBean> parseDynamicList(JSONArray jArray)
			throws JSONException {
		List<RoomBean> moods = new ArrayList<RoomBean>();
		for (int i = 0; i < jArray.length(); i++) {
			JSONObject object = jArray.getJSONObject(i);
			LogUtil.i(LOG_TAG, object.toString());
			RoomBean bean = new RoomBean();
			bean.setUid(object.getString(NiceConstants.UID));
			bean.setMessageId(object.getString(NiceConstants.MESSAGE_ID));
			bean.setHeadPic(object.getString(NiceConstants.HEAD_PIC));
			bean.setNickname(object.getString(NiceConstants.NICKNAME));
			bean.setMessageTime(object.getString(NiceConstants.MESSAGE_TIME));
			bean.setMessageContent(object
					.getString(NiceConstants.MESSAGE_CONTENT));
			bean.setType(object.getString(NiceConstants.TYPE));
			bean.setDiggNum(Integer.parseInt(object
					.getString(NiceConstants.DIGG_NUM)));
			bean.setCommentNum(Integer.parseInt(object
					.getString(NiceConstants.COMMENT_NUM)));
			bean.setBigPic(object.getString(NiceConstants.BIG_PIC));
			bean.setSmallPic(object.getString(NiceConstants.SMALL_PIC));
			bean.setDigg(object.getInt(NiceConstants.DIGG_STATUS) == 1 ? true
					: false);
			moods.add(bean);
		}
		return moods;

	}
}
