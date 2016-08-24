package net.nmss.nice.share;

//import net.slgb.nice.bean.LossWeightKnowledgeBean;
//import net.slgb.nice.bean.MoodMsgBean;
//import net.slgb.nice.bean.MyHomePageBean;
//import net.slgb.nice.bean.QuestionDetailBean;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.baidu.frontia.Frontia;
import com.baidu.frontia.api.FrontiaSocialShare;
import com.baidu.frontia.api.FrontiaSocialShareContent;
import com.baidu.frontia.api.FrontiaSocialShareListener;
import com.baidu.frontia.api.FrontiaAuthorization.MediaType;
import com.baidu.frontia.api.FrontiaSocialShare.FrontiaTheme;

public class ShareHelper {

	private final static String LOG_TAG = "ShareUtils";
	private FrontiaSocialShare mSocialShare;
	private FrontiaSocialShareContent mImageContent = new FrontiaSocialShareContent();
	private Activity mContext;

	public ShareHelper(Activity context) {
		this.mContext = context;
		init(context);
	}

	private void init(Context context) {
		mSocialShare = Frontia.getSocialShare();
		mSocialShare.setContext(context);
		mSocialShare.setClientId(MediaType.SINAWEIBO.toString(),
				Conf.SINA_APP_KEY);
		mSocialShare.setClientId(MediaType.QZONE.toString(), "1102001493");
		mSocialShare.setClientId(MediaType.QQFRIEND.toString(), "1102001493");
		mSocialShare.setClientName(MediaType.QQFRIEND.toString(), "调理网");
		mSocialShare.setClientId(MediaType.WEIXIN.toString(),
				"wx17fd97457e2e77a6");
	}

//	public void share(MoodMsgBean bean) {
//		mImageContent.setTitle("#来自一起瘦吧android客户端#");
//		mImageContent.setContent(bean.getMessageContent());
//		mImageContent.setLinkUrl("http://www.kk22.com/" + bean.getMessageId());
//		mImageContent.setImageUri(Uri.parse(bean.getBigPic()));
//		mSocialShare.show(mContext.getWindow().getDecorView(), mImageContent,
//				FrontiaTheme.LIGHT, new ShareListener());
//	}
//
//	public void share(LossWeightKnowledgeBean bean) {
//		mImageContent.setTitle("#来自一起瘦吧android客户端#");
//		mImageContent.setContent(bean.getSummary());
//		mImageContent.setLinkUrl("http://news.kk22.com/" + bean.getNewsId());
//		// mImageContent.setImageUri(Uri.parse(bean.getBigPic()));
//		mSocialShare.show(mContext.getWindow().getDecorView(), mImageContent,
//				FrontiaTheme.LIGHT, new ShareListener());
//	}
//
//	public void share(QuestionDetailBean bean) {
//		mImageContent.setTitle("#来自一起瘦吧android客户端#");
//		mImageContent.setContent(bean.getContent());
//		mImageContent.setLinkUrl("http://news.kk22.com/");
//		mSocialShare.show(mContext.getWindow().getDecorView(), mImageContent,
//				FrontiaTheme.LIGHT, new ShareListener());
//	}
//	
//
//	public void share(MyHomePageBean myHomePageBean) {
//		// TODO 自动生成的方法存根
//		mImageContent.setTitle("#来自一起瘦吧android客户端#");
//		mImageContent.setContent(myHomePageBean.getMessageContent());
//		mImageContent.setLinkUrl("http://www.kk22.com/" + myHomePageBean.getMessage_id());
//		mImageContent.setImageUri(Uri.parse(myHomePageBean.getBigPic()));
//		mSocialShare.show(mContext.getWindow().getDecorView(), mImageContent,
//				FrontiaTheme.LIGHT, new ShareListener());
//	}
	
	public void share(String content) {
		// TODO 自动生成的方法存根
		mImageContent.setTitle("#来自一起瘦吧android客户端#");
		mImageContent.setContent(content);
		mImageContent.setLinkUrl("http://news.kk22.com/question");
		mSocialShare.show(mContext.getWindow().getDecorView(), mImageContent,
				FrontiaTheme.LIGHT, new ShareListener());
	}

	private class ShareListener implements FrontiaSocialShareListener {

		@Override
		public void onSuccess() {
			Log.i(LOG_TAG, "share success");
			Toast.makeText(mContext, "谢谢你的分享", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onFailure(int errCode, String errMsg) {
			Log.i(LOG_TAG, "share errCode " + errCode);
			Toast.makeText(mContext, "分享失败", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onCancel() {
			Log.i(LOG_TAG, "cancel ");
		}

	}
	
}
