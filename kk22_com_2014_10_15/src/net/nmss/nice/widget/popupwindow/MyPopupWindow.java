package net.nmss.nice.widget.popupwindow;

import java.io.File;

import net.nmss.nice.R;
import net.nmss.nice.activity.SecondRegisterActivity;
import net.nmss.nice.utils.ImageTools;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;

public class MyPopupWindow extends PopupWindow implements OnClickListener {
	public static final int TAKE_PICTURE = 0;
	public static final int CHOOSE_PICTURE = 1;
	public static final int CROP = 2;
	public static final int CROP_PICTURE = 3;
	public int REQUEST_CODE;
	
	private Button get_phone_pai;
	private Button get_photo_phone;
	private Button get_photo_exit;
	private Intent intent;
	
	private File file;
	private String fileName = null;
	private Uri imageUri;
	private boolean crop;
	private Context context;
	private SecondRegisterActivity activity;
	private SecondRegisterActivity userProfile;


	public MyPopupWindow(Context context,SecondRegisterActivity secondRegisterActivity,SecondRegisterActivity secondRegisterActivity2,boolean isCrop) {
		super(context);
		this.context = context;
		this.crop = isCrop;
		this.activity = secondRegisterActivity;
		this.userProfile = secondRegisterActivity2;
		
//		View view = View.inflate(context, R.layout.popup_winddow, null);
		View view = LayoutInflater.from(context).inflate(R.layout.popup_winddow, null);
		get_phone_pai = (Button) view.findViewById(R.id.get_phone_pai);
		get_photo_phone = (Button) view.findViewById(R.id.get_photo_phone);
		get_photo_exit = (Button) view.findViewById(R.id.get_photo_exit);

		this.setContentView(view);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.MATCH_PARENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);

		get_phone_pai.setOnClickListener(this);
		get_photo_phone.setOnClickListener(this);
		get_photo_exit.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.get_phone_pai:
			try {
				// 拍照我们用Action为MediaStore.ACTION_IMAGE_CAPTURE，
				// 有些人使用其他的Action但我发现在有些机子中会出问题，所以优先选择这个
				intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				if (crop) {
					REQUEST_CODE = CROP;
				} else {
					REQUEST_CODE = TAKE_PICTURE;
				}
				// 保存本次截图临时文件名字
				fileName = String.valueOf(System.currentTimeMillis())
						+ "_tmp.jpg";

				imageUri = Uri.fromFile(new File(ImageTools.getPicCachePath(),
						fileName));
				// 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
				intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				startActivityForResult(intent, REQUEST_CODE);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case R.id.get_photo_phone:
			intent = new Intent(Intent.ACTION_PICK);
			if (crop) {
				REQUEST_CODE = CROP;
			} else {
				REQUEST_CODE = CHOOSE_PICTURE;
			}
			intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					"image/*");
			startActivityForResult(intent, REQUEST_CODE);
			break;

		default:
			dismiss();
			break;
		}
	}
	
	private void startActivityForResult(Intent intent, int request_code) {
		 if (activity != null) {
			 activity.startActivityForResult(intent, request_code);

		}
	}

	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case TAKE_PICTURE:
				file = new File(imageUri.getPath());
				userProfile.getPhoto(true, file);
				break;

			case CHOOSE_PICTURE:
				// 照片的原始资源地址
				Uri originalUri = data.getData();
				if (originalUri == null) {
					return;
				}
				String path = null;
				if (originalUri.getScheme().equals("content")) {
					String[] pojo = { MediaStore.Images.Media.DATA };
					// Cursor cursor =activity.managedQuery(originalUri, pojo,
					// null, null, null);
					ContentResolver cr = context.getContentResolver();
					Cursor cursor = cr.query(originalUri, pojo, null, null,
							null);
					if (cursor != null) {
						int colunm_index = cursor
								.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
						cursor.moveToFirst();
						path = cursor.getString(colunm_index);
						cursor.close();
					} else {
						return;
					}
				} else {
					path = originalUri.getPath();
				}
				file = new File(path);
				userProfile.getPhoto(true, file);
				break;

			case CROP:
				Intent intent = null;
				if (data != null) {
					Uri srcUri = data.getData();
					// 保存本次截图临时文件名字
					String fileName = String
							.valueOf(System.currentTimeMillis()) + "_tmp.jpg";
					imageUri = Uri.fromFile(new File(ImageTools
							.getPicCachePath(), fileName));
					intent = getCropImageIntent(srcUri, imageUri);
				} else {
					intent = getCropImageIntent(imageUri);
				}
				startActivityForResult(intent, CROP_PICTURE);
				break;

			case CROP_PICTURE:
				if (data == null) {
					return;
				}
				// Uri photoUri = data.getData();
				if (imageUri != null) {
					userProfile.getPhoto(true, new File(imageUri.getPath()));
				}
				break;
			default:
				break;
			}
		}
	}

	private Intent getCropImageIntent(Uri data) {
		return getCropImageIntent(data, data);
	}

	private Intent getCropImageIntent(Uri data, Uri outUri) {
		Intent intent = new Intent();
		if (data == null) {
			return intent;
		}
		intent.setAction("com.android.camera.action.CROP");
		intent.setDataAndType(data, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1); // 设置按长和宽的比例裁剪
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 250); // 设置输出的大小
		intent.putExtra("outputY", 250);
		intent.putExtra("scale", true); // 设置是否允许拉伸
		// 如果要在给定的uri中获取图片，则必须设置为false，如果设置为true，那么便不会在给定的uri中获取到裁剪的图片
		intent.putExtra("return-data", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());// 设置输出格式
		intent.putExtra("noFaceDetection", true); // 无需人脸识别 默认不需要设置
		return intent;
	}

	
}
