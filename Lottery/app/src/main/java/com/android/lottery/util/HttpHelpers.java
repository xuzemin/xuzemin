package com.android.lottery.util;

import org.json.JSONException;
import org.json.JSONObject;

//import net.nmss.nice.activity.DynamicCommentActivity;
//import net.nmss.nice.activity.LossWeightKnowledgeDetailsActivity;
//import net.nmss.nice.activity.PartnersDynamicCommentActivity;
//import net.nmss.nice.activity.WayActivity;
import android.os.Handler;
import android.os.Message;

import com.android.lottery.bean.NiceUserInfo;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

public class HttpHelpers {

	private final static String LOG_TAG = "HttpHelpers";

	/**
	 * 赞一个 type 赞的类型 5为心情 文章（减肥知识）type为1 享瘦为7
	 * 
	 * @param megId
	 * @param position
	 * @param handler
	 */
	public static void makeDigg(String megId, final int position, int type,
			final Handler handler) {
		RequestParams params = new RequestParams();
		NiceUserInfo info = NiceUserInfo.getInstance();
		params.put(NiceConstants.UID, info.getUId());
		params.put(NiceConstants.TYPE, String.valueOf(type));
		params.put("item_id", megId);
		params.put(NiceConstants.sign, MD5Utils.getSign());
		String url = UrlHelper.getAbsoluteUrl(UrlHelper.DIGG);
		LogUtil.i(LOG_TAG, "params:" + params);
		LogUtil.i(LOG_TAG, "Url:" + url);
		AsyncHttpRequestUtil.get(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onStart() {
				super.onStart();
				LogUtil.i(LOG_TAG, "makeDiggForEnjoy onStart");
			}

			@Override
			public void onSuccess(int statusCode, String content) {
				LogUtil.i(LOG_TAG, "makeDiggForParatnaersDynamic onSuccess:"
						+ content);
				Message msg = new Message();
				msg.what = NiceConstants.ON_SUCCESS;
				msg.arg1 = position;
				msg.arg2 = 1111;
				msg.obj = content;
				handler.sendMessage(msg);
			}

			@Override
			public void onFailure(int statusCode, Throwable error,
					String content) {
				LogUtil.i(LOG_TAG, "makeDiggForParatnaersDynamic onFailure:"
						+ content + "->" + error.getMessage());
				handler.sendEmptyMessage(NiceConstants.ON_FAILURE);
			}
		});
	}

	/**
	 * 发送评论
	 * 
	 * @param msgId
	 * @param content
	 * @param handler
	 */
	public static void sendDynamicComment(final String msgId,
			final String content, int type, final Handler handler) {
		RequestParams params = new RequestParams();
		params.put(NiceConstants.UID, NiceUserInfo.getInstance().getUId());
		params.put(NiceConstants.ITEM_ID, msgId);
		params.put(NiceConstants.CONTENT, content);
		params.put(NiceConstants.TYPE, String.valueOf(type));
		params.put(NiceConstants.sign, MD5Utils.getSign());
		String url = UrlHelper.getAbsoluteUrl(UrlHelper.ADD_COMMENT);
		LogUtil.i(LOG_TAG, "params:" + params);
		LogUtil.i(LOG_TAG, "url:" + url);
		AsyncHttpRequestUtil.get(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onStart() {
				LogUtil.i(LOG_TAG, "sendDynamicComment->onStart()");
				super.onStart();
			}

			@Override
			public void onSuccess(int statusCode, String content) {
				LogUtil.i(LOG_TAG, "sendDynamicComment->onSuccess():" + content);
				Message msg = new Message();
				msg.what = 1;
				msg.obj = content;
				handler.sendMessage(msg);
			}

			@Override
			public void onFailure(int statusCode, Throwable error,
					String content) {
				Message msg = new Message();
				msg.what = NiceConstants.ON_FAILURE;
				handler.sendMessage(msg);
				LogUtil.i(LOG_TAG,
						"sendDynamicComment->onFailure():" + error.getMessage());
			}
		});
	}

	public static void getDynamicCommentContentList(String moodId,
			final Handler handler) {
		RequestParams params = new RequestParams();
		params.put(NiceConstants.UID, NiceUserInfo.getInstance().getUId());
		params.put(NiceConstants.MOOD_ID, moodId);
		params.put(NiceConstants.sign, MD5Utils.getSign());
		String url = UrlHelper.getAbsoluteUrl(UrlHelper.GET_MOOD_DETAIL);
		LogUtil.i(LOG_TAG, "params:" + params);
		LogUtil.i(LOG_TAG, "url:" + url);
		AsyncHttpRequestUtil.get(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onStart() {
				LogUtil.i(LOG_TAG, "getDynamicCommentContent->onStart");
			}

			@Override
			public void onSuccess(int statusCode, String content) {
				LogUtil.i(LOG_TAG, "getDynamicCommentContent->onSuccess:"
						+ content);
				Message msg = new Message();
				msg.what = 5;
				msg.obj = content;
				handler.sendMessage(msg);
			}

			@Override
			public void onFailure(int statusCode, Throwable error,
					String content) {
				LogUtil.i(LOG_TAG, "getDynamicCommentContent->onFailure:"
						+ error.getMessage());
				handler.sendEmptyMessage(NiceConstants.ON_FAILURE);
			}

		});
	}

	public static RequestHandle getLossWeightKnowledge(final int limit,
			final Handler handler, final boolean isRefresh) {
		RequestParams params = new RequestParams();
		params.put(NiceConstants.sign, MD5Utils.getSign());
		params.put(NiceConstants.LIMIT, String.valueOf(limit));
		params.put(NiceConstants.UID, NiceUserInfo.getInstance().getUId());
		String url = UrlHelper
				.getAbsoluteUrl(UrlHelper.GET_LOSS_WEIGHT_KNOWLEDGE);
		LogUtil.i(LOG_TAG, "url:" + url);
		LogUtil.e(LOG_TAG, "params:" + params);
		return AsyncHttpRequestUtil.get(url, params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onStart() {
						LogUtil.i(LOG_TAG, "getLossWeightKnowledge->onStart");
						super.onStart();
					}

					@Override
					public void onSuccess(int statusCode, String content) {
						LogUtil.i(LOG_TAG, "getLossWeightKnowledge->onSuccess:"
								+ content);
						Message msg = new Message();
						msg.what = (limit == 1 ? 2 : 3);
						msg.obj = content;
						handler.sendMessage(msg);
					}

					@Override
					public void onFailure(int statusCode, Throwable error,
							String content) {
						LogUtil.i(LOG_TAG, "getLossWeightKnowledge->onFailure:"
								+ error.getMessage());
						// handler.sendEmptyMessage(LossWeightKnowledgeFragment.ON_FAILURE);
					}

					@Override
					public void onFinish() {

						super.onFinish();
						LogUtil.i(LOG_TAG, "getLossWeightKnowledge->onFinish");
						if (isRefresh)
							return;
						// handler.sendEmptyMessage(LossWeightKnowledgeFragment.CLOSE_REFRESH);
					}
				});
	}

	public static RequestHandle getLossWeightKnowledgeDetails(String newid,
			final Handler handler) {
		RequestParams params = new RequestParams();
		params.put(NiceConstants.UID, NiceUserInfo.getInstance().getUId());
		params.put("news_id", newid);
		params.put(NiceConstants.sign, MD5Utils.getSign());
		String url = UrlHelper
				.getAbsoluteUrl(UrlHelper.GET_LOSS_WEIGHT_KNOWLEDGE_DETAILS);
		LogUtil.i(LOG_TAG, "params:" + params);
		LogUtil.i(LOG_TAG, "Url:" + url);
		return AsyncHttpRequestUtil.get(url, params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onStart() {
						LogUtil.i(LOG_TAG,
								"getLossWeightKnowledgeDetails onStart");
						super.onStart();
					}

					@Override
					public void onSuccess(int statusCode, String content) {
						LogUtil.i(LOG_TAG,
								"getLossWeightKnowledgeDetails onSuccess:"
										+ content);
						Message msg = new Message();
						msg.what = NiceConstants.ON_SUCCESS;
						msg.obj = content;
						handler.sendMessage(msg);
					}

					@Override
					public void onFailure(int statusCode, Throwable error,
							String content) {
						handler.sendEmptyMessage(NiceConstants.ON_FAILURE);
						LogUtil.i(LOG_TAG,
								"getLossWeightKnowledgeDetails onFailure:"
										+ content);
					}

					@Override
					public void onFinish() {
						LogUtil.i(LOG_TAG,
								"getLossWeightKnowledgeDetails onFinish:");
						super.onFinish();
					}
				});
	}

	public static void updateUserInfo() {
		NiceUserInfo userInfo = NiceUserInfo.getInstance();
		RequestParams params = new RequestParams();
		params.put(NiceConstants.UID, userInfo.getUId());
		params.put(NiceConstants.sign, MD5Utils.getSign());
		params.put(NiceConstants.NICKNAME, userInfo.getName());

		String declarationStr = (userInfo.getDeclaration() == null || userInfo
				.getDeclaration().equals("null")) ? null : userInfo
				.getDeclaration();
		params.put("declaration", declarationStr);
		String sex = userInfo.getGender();
		if ("男".equals(sex)) {
			sex = "0";
		} else if ("女".equals(sex)) {
			sex = "1";
		}
		params.put(NiceConstants.gender, sex);
		params.put("birthday",
				userInfo.getBirthday() == null ? null : userInfo.getBirthday());
		params.put("height",
				userInfo.getHeight() == null ? null : userInfo.getHeight());
		params.put("weight",
				userInfo.getWeight() == null ? null : userInfo.getWeight());
		params.put("address",
				userInfo.getArea() == null ? null : userInfo.getArea());
		params.put("target_weight", userInfo.getTargetWeight() == null ? null
				: userInfo.getTargetWeight());
		params.put("end_time",userInfo.getTargetTime() == null ? null :
				userInfo.getTargetTime());
		String url = UrlHelper.getAbsoluteUrl(UrlHelper.SUBMITUSERINFO);
		LogUtil.i(LOG_TAG, "updateUserInfo->params:" + params);
		LogUtil.i(LOG_TAG, "updateUserInfo->url:" + url);
		AsyncHttpRequestUtil.get(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onStart() {
				super.onStart();
			}

			@Override
			public void onSuccess(int statusCode, String content) {
				LogUtil.i(LOG_TAG, "onSuccess->content:" + content);
				
			}

			@Override
			public void onFailure(int statusCode, Throwable error,
					String content) {
					LogUtil.i(LOG_TAG, "onFailure->content:" + content);
			}

			@Override
			public void onFinish() {

				super.onFinish();
			}
		});
	}

	public static void updataUserName(final Handler handler){
		RequestParams params = new RequestParams();
		params.put(NiceConstants.UID, NiceUserInfo.getInstance().getUId());
		params.put(NiceConstants.NICKNAME, NiceUserInfo.getInstance().getName());
		String url = UrlHelper.getAbsoluteUrl(UrlHelper.SUBMITUSERINFO);
		LogUtil.i(LOG_TAG, "updataUserName->params:" + params);
		LogUtil.i(LOG_TAG, "updataUserName->url:" + url);
		AsyncHttpRequestUtil.get(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onStart() {
				super.onStart();
			}

			@Override
			public void onSuccess(int statusCode, String content) {
				LogUtil.i(LOG_TAG, "onSuccess->content:" + content);
				JSONObject obj;
				try {
					obj = new JSONObject(content);
					int r_e = obj.getInt("r_e");
					if(r_e == 400002){
						Message msg = new Message();
						msg.what = 1;
						msg.obj = obj.getString("r_c");
						handler.sendMessage(msg);
					}
					if(r_e == 0){
						Message msg = new Message();
						msg.what = 2;
						msg.obj = content;
						handler.sendMessage(msg);
					}
				} catch (JSONException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int statusCode, Throwable error,
					String content) {
					LogUtil.i(LOG_TAG, "onFailure->content:" + content);
			}

			@Override
			public void onFinish() {

				super.onFinish();
			}
		});
	}
	
	/**
	 * 注册成功自动发送一条消息
	 */
	public static void sendFirstMessage() {
		String sendText = "新人报到!今天是我加入YES瘦的第一天,希望能够快速融入到小伙伴们当中。";

		RequestParams params = new RequestParams();
		params.put(NiceConstants.sign, MD5Utils.getSign());
		params.put("time", String.valueOf(System.currentTimeMillis()));
		params.put(NiceConstants.UID, NiceUserInfo.getInstance().getUId());
		params.put("message_content", sendText);
		String url = UrlHelper.getAbsoluteUrl(UrlHelper.UPLOADPHOTO);
		LogUtil.i(LOG_TAG, "params:" + params);
		LogUtil.i(LOG_TAG, "url:" + url);

		AsyncHttpRequestUtil.get(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onStart() {
				super.onStart();
			}

			@Override
			public void onSuccess(int statusCode, String content) {
				LogUtil.i(LOG_TAG, "content:" + content);
			}

			@Override
			public void onFailure(int statusCode, Throwable error,
					String content) {

			}

		});
	}

	/**
	 * 判断是否有动态
	 */

	public static void parse(final Handler handler) {
		RequestParams params = new RequestParams();
		params.put(NiceConstants.UID, NiceUserInfo.getInstance().getUId());
		String url = UrlHelper.getAbsoluteUrl("newsApp/test");
		LogUtil.i(LOG_TAG, url);
		AsyncHttpRequestUtil.get(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onStart() {
				// TODO 自动生成的方法存根
				super.onStart();
				LogUtil.i(LOG_TAG, "onStart");
			}

			@Override
			public void onSuccess(int statusCode, String content) {
				// TODO 自动生成的方法存根
				LogUtil.i(LOG_TAG, "onSuccess" + content);
				try {
					JSONObject obj = new JSONObject(content);
					int r_e = obj.getInt("r_e");
					if (r_e == 1) {
						LogUtil.e("有料到", obj.getString(NiceConstants.RECONTENT));
						Message msg = new Message();
						msg.what = 1;
						handler.sendMessage(msg);
					}
				} catch (JSONException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}

			}

			@Override
			public void onFailure(int statusCode, Throwable error,
					String content) {
				// TODO 自动生成的方法存根
				LogUtil.i(LOG_TAG, "onFailure" + content);
			}
		});
	}

	public static void parseRelation(String uid, String follow_id,
			final Handler handler) {
		RequestParams params = new RequestParams();
		params.put(NiceConstants.UID, uid);
		params.put("follow_id", follow_id);
		String url = UrlHelper.getAbsoluteUrl(UrlHelper.ISRELATION);
		LogUtil.i("是否关注", params.toString());
		LogUtil.i("是否关注", "url:" + url);
		AsyncHttpRequestUtil.get(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				LogUtil.i("parseRelation", "onStart");
				super.onStart();
			}

			@Override
			public void onSuccess(int statusCode, String content) {
				// TODO Auto-generated method stub
				LogUtil.i("parseRelation", "onSuccess content:" + content);
				int relation = 0;
				try {
					JSONObject obj = new JSONObject(content);
					int r_e = obj.getInt("r_e");
					JSONObject r_c = obj.getJSONObject("r_c");
					if (r_e == 200000) {
						LogUtil.i("parseRelation", "已关注");
					} else {
						LogUtil.i("parseRelation", "未关注");
					}
					relation = r_c.getInt("is_relation");
				} catch (JSONException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
				Message msg = new Message();
				msg.what = relation;
				handler.sendMessage(msg);
			}

			@Override
			public void onFailure(int statusCode, Throwable error,
					String content) {
				LogUtil.i("parseRelation", "onFailure content:" + content);
			}
		});
	}

	public static void doAttention(String follow_id, final int add_del_friend,
			final Handler handler) {
		RequestParams params = new RequestParams();
		params.put(NiceConstants.UID, NiceUserInfo.getInstance().getUId());
		params.put("friend_id", follow_id);
		params.put("add_del_friend", String.valueOf(add_del_friend));
		String url = UrlHelper.getAbsoluteUrl(UrlHelper.ADDFRIEND);
		LogUtil.i("添加朋友", params.toString());
		LogUtil.i("添加朋友", "url:" + url);
		AsyncHttpRequestUtil.get(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onStart() {
				LogUtil.i("添加朋友", "doAttention -> onStart:");
				super.onStart();
			}

			@Override
			public void onSuccess(int statusCode, String content) {
				LogUtil.i("添加朋友", "doAttention -> onSuccess:" + content);
				Message msg = new Message();
				msg.what = 3;
				msg.obj = content;
				handler.sendMessage(msg);
			}

			@Override
			public void onFailure(int statusCode, Throwable error,
					String content) {
				LogUtil.i("添加朋友", "doAttention -> onFailure:" + content);
			}

			@Override
			public void onFinish() {
				LogUtil.i("添加朋友", "doAttention -> onFinish:");
				super.onFinish();
			}
		});
	}

	public static void parseUserInfo() {
		RequestParams params = new RequestParams();
		params.put(NiceConstants.UID, NiceUserInfo.getInstance().getUId());
		AsyncHttpRequestUtil.get(UrlHelper.getAbsoluteUrl(UrlHelper.USERINFO),
				params, new AsyncHttpResponseHandler() {
					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						LogUtil.i("parseUserInfo", "onStart");
						super.onStart();
					}

					@Override
					public void onSuccess(int statusCode, String content) {
						// TODO Auto-generated method stub
						LogUtil.i("parseUserInfo", "onSuccess content:"
								+ content);
						parseUserInfoJson(content);
					}

					@Override
					public void onFailure(int statusCode, Throwable error,
							String content) {
						LogUtil.i("parseUserInfo", "onFailure content:"
								+ content);
					}
				});
	}

	private static void parseUserInfoJson(String json) {
		try {
			JSONObject obj = new JSONObject(json);
			String r_e = obj.getString("r_e");
			if ("200000".equals(r_e)) {
				JSONObject r_c = obj.getJSONObject("r_c");
				NiceUserInfo info = NiceUserInfo.getInstance();
				info.setBirthday(r_c.getString("birthday"));
				info.setCoin(r_c.getString("coin"));
				info.setDeclaration(r_c.getString("declaration"));
				info.setFansNum(r_c.getString("fans_num"));
				info.setGender(r_c.getString("gender"));
				info.setHead_pic(r_c.getString("head_pic"));
				info.setHeight(r_c.getString("height"));
				info.setIntegral(r_c.getString("integral"));
				info.setMemlevel(r_c.getString("memlevel"));
				info.setMoodNum(r_c.getString("mood_num"));
				info.setName(r_c.getString("nickname"));
				info.setRealtionNum(r_c.getString("realtion_num"));
				info.setTaskNum(r_c.getInt("task_num"));
				info.setWeight(r_c.getString("weight"));
				info.setTelephone(r_c.getString("telephone"));
				LogUtil.i("parseUserInfo", "记录用户信息成功");
			}
		} catch (JSONException e) {
			// TODO: handle exception
		}
	}

	public static void newUpdateInfo(final Handler handler) {
		NiceUserInfo userInfo = NiceUserInfo.getInstance();
		RequestParams params = new RequestParams();
		params.put(NiceConstants.UID, userInfo.getUId());
		params.put(NiceConstants.sign, MD5Utils.getSign());
		params.put(NiceConstants.NICKNAME, userInfo.getName());
		String sex = userInfo.getGender();
		if ("男".equals(sex)) {
			sex = "0";
		} else if ("女".equals(sex)) {
			sex = "1";
		}
		params.put(NiceConstants.gender, sex);
		params.put("birthday",
				userInfo.getBirthday() == null ? null : userInfo.getBirthday());
		params.put("height",
				userInfo.getHeight() == null ? null : userInfo.getHeight());
		params.put("weight",
				userInfo.getWeight() == null ? null : userInfo.getWeight());
		params.put("pwd", userInfo.getPwd());
		String url = UrlHelper.getAbsoluteUrl(UrlHelper.REGISTERUSERINFO);
		LogUtil.i(LOG_TAG, "newUpdateInfo->params:" + params);
		LogUtil.i(LOG_TAG, "newUpdateInfo->url:" + url);
		AsyncHttpRequestUtil.get(
				UrlHelper.getAbsoluteUrl(UrlHelper.REGISTERUSERINFO), params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onStart() {
						super.onStart();
					}

					@Override
					public void onSuccess(int statusCode, String content) {
						LogUtil.i(LOG_TAG, "newUpdateInfo->content:" + content);
						Message msg = new Message();
						msg.what = 1;
						msg.obj = content;
						handler.sendMessage(msg);
						LogUtil.e("", "");
					}

					@Override
					public void onFailure(int statusCode, Throwable error,
							String content) {

					}

					@Override
					public void onFinish() {

						super.onFinish();
					}
				});
	}

	public static void getHeadPic(String type, final Handler handler){
		RequestParams params = new RequestParams();
		params.put(NiceConstants.UID, NiceUserInfo.getInstance().getUId());
		params.put(NiceConstants.TYPE, type);
		String url = UrlHelper.getAbsoluteUrl(UrlHelper.MOODHEADER);
		LogUtil.i(LOG_TAG, params.toString());
		LogUtil.i(LOG_TAG, "url:" + url);
		AsyncHttpRequestUtil.get(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onStart() {
				LogUtil.i(LOG_TAG, "getHeaderImg -> onStart:");
			}

			@Override
			public void onSuccess(int statusCode, String content) {
				LogUtil.i(LOG_TAG, "getHeaderImg -> onSuccess:" + content);
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(content);
					if (jsonObject.getInt(NiceConstants.RECODE) == 200000) {
						JSONObject r_c = jsonObject
								.getJSONObject(NiceConstants.RECONTENT);
						NiceConstants.resolution_480_800 = r_c.getString("800x480");
						NiceConstants.resolution_540_960 = r_c.getString("960X540");
						NiceConstants.resolution_768_1024 = r_c.getString("1024x768");
						NiceConstants.resolution_800_1280 = r_c.getString("1280x800");
						NiceConstants.resolution_1080_1800 = r_c.getString("1800X1080");
						NiceConstants.resolution_1080_1920 = r_c.getString("1920X1080");
						Message msg = new Message();
						msg.what = 4;
						handler.sendMessage(msg);
					}else{
						Message msg = new Message();
						msg.what = 5;
						msg.obj = content;
						handler.sendMessage(msg);
					}
					
				} catch (JSONException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int statusCode, Throwable error,
					String content) {
				LogUtil.i(LOG_TAG, "getHeaderImg -> onFailure:" + content);
				Message msg = new Message();
				msg.what = 5;
				msg.obj = content;
				handler.sendMessage(msg);
			}

			@Override
			public void onFinish() {
				super.onFinish();
			}

		});
	}
	
}
