package net.nmss.nice.activity;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;
import net.nmss.nice.activity.PhotoViewActivity;
import net.nmss.nice.R;
import net.nmss.nice.utils.ImageLoaderUtils;
import net.nmss.nice.utils.LogUtil;
import net.nmss.nice.utils.NiceConstants;
import android.os.Bundle;
import android.widget.ImageView.ScaleType;


public class PhotoViewActivity extends BaseActivity{

	private final static String LOG_TAG = "PhotoViewActivity";
	private PhotoView mImageView;
	private PhotoViewAttacher mAttacher;
	private String bigPicUrl;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zoom_photo_view_activity_layout);
		mImageView = (PhotoView) findViewById(R.id.iv_photo);
		mAttacher = new PhotoViewAttacher(mImageView);
		mAttacher.setZoomable(true);
		mAttacher.setScaleType(ScaleType.CENTER_INSIDE);
		bigPicUrl = (String)(getIntent().getExtras().getString(NiceConstants.BIG_PIC));
		LogUtil.i(LOG_TAG, "bigPicUrl:"+bigPicUrl);
		ImageLoaderUtils.getInstance().displayImageWithCache(bigPicUrl, mImageView);
	
	}
	
	@Override
	protected void onDestroy() {
		mAttacher.cleanup();
		super.onDestroy();
	}

}
