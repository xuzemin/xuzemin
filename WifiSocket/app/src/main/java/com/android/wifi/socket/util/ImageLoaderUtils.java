package com.android.wifi.socket.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.widget.ImageView;

import com.android.wifi.socket.wifisocket.R;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class ImageLoaderUtils extends ImageLoader {

	private DisplayImageOptions optionsWithCache;
	private DisplayImageOptions optionsWithOutCache;
	private DisplayImageOptions optionsWithLoadHeadPic;
	private static ImageLoaderUtils instance;

	private ImageLoaderUtils() {

	}

	public static ImageLoaderUtils getInstance() {
		if (instance == null) {
			instance = new ImageLoaderUtils();
		}
		return instance;
	}

	/**
	 * @param uri
	 * @param imageView
	 *            加载图片，并带有缓存功能
	 */
	public void displayImageWithCache(String uri, ImageView imageView) {
		if (instance == null)
			instance = getInstance();
		try {
			instance.displayImage(uri, imageView, getOptionsWithCache());
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * @param uri
	 * @param imageView
	 *            加载用户头像，
	 */
	public void displayUserHeadPic(String uri, ImageView imageView) {
		if (instance == null)
			instance = getInstance();
		try {
			instance.displayImage(uri, imageView, getOptionsWithLoadHeadPic());
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * @param uri
	 * @param imageView
	 *            加载用户头像，
	 */

	public void displayImageWithOutCache(String uri, ImageView imageView) {
		if (instance == null)
			instance = getInstance();
		try {
			instance.displayImage(uri, imageView, getOptionsWithOutCache());
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	public DisplayImageOptions getOptionsWithOutCache() {
		if (optionsWithOutCache == null) {
			optionsWithOutCache = new DisplayImageOptions.Builder()
					.showImageOnLoading(R.mipmap.load_failure)
					.showImageForEmptyUri(R.mipmap.load_failure)
					.showImageOnFail(R.mipmap.load_failure)
					.cacheInMemory(false).cacheOnDisc(false)
					.considerExifParams(true)
					.imageScaleType(ImageScaleType.EXACTLY)
					.bitmapConfig(Bitmap.Config.RGB_565).build();
		}
		return optionsWithOutCache;
	}

	private DisplayImageOptions getOptionsWithCache() {
		if (optionsWithCache == null) {
			optionsWithCache = new DisplayImageOptions.Builder()
					.showImageOnLoading(R.mipmap.load_failure)
					.showImageForEmptyUri(R.mipmap.load_failure)
					.showImageOnFail(R.mipmap.load_failure)
					.cacheInMemory(false).cacheOnDisc(true)
					.considerExifParams(true)
					.imageScaleType(ImageScaleType.EXACTLY)
					.bitmapConfig(Bitmap.Config.RGB_565).build();
		}
		return optionsWithCache;
	}

	private DisplayImageOptions getOptionsWithLoadHeadPic() {
		if (optionsWithLoadHeadPic == null) {
			optionsWithLoadHeadPic = new DisplayImageOptions.Builder()
					.showImageOnLoading(R.mipmap.default_head_pic)
					.showImageForEmptyUri(R.mipmap.default_head_pic)
					.showImageOnFail(R.mipmap.default_head_pic)
					.cacheInMemory(false).cacheOnDisc(true)
					.considerExifParams(true)
					.bitmapConfig(Bitmap.Config.RGB_565).build();
		}
		return optionsWithLoadHeadPic;
	}

	/**
	 * 异步图片加载ImageLoader的初始化操作，在Application中调用此方法
	 * 
	 * @param context
	 *            上下文对象
	 */
	public void initImageLoader(Context context) {
		// 配置ImageLoader
		// 获取本地缓存的目录，该目录在SDCard的根目录下
		// File cacheDir = StorageUtils.getOwnCacheDirectory(context,
		// cacheDisc);
		// 实例化Builder
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).memoryCacheExtraOptions(480, 800) // default = device
															// screen dimensions

				.discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75, null)
				// .threadPoolSize(3) // default
				// .threadPriority(Thread.NORM_PRIORITY - 1) // default
				// .tasksProcessingOrder(QueueProcessingType.FIFO) // default
				.denyCacheImageMultipleSizesInMemory()
				// .memoryCacheSizePercentage(13) // default
				// .discCache(new UnlimitedDiscCache(cacheDir)) // default
				// .imageDownloader(new BaseImageDownloader(this)) // default
				// .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				// // default
				// .writeDebugLogs()
				.memoryCache(new WeakMemoryCache()).build();
		instance.init(config);
	}
}
