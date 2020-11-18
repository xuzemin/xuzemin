package com.ctv.settings.security.adapter;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ctv.settings.R;
import com.ctv.settings.security.bean.ApkInfo;

import java.util.List;

public class PermissionsListAadpter extends BaseQuickAdapter<ApkInfo, BaseViewHolder> {
	public PermissionsListAadpter(int layoutResId, List<ApkInfo> packages) {
		super(layoutResId, packages);
	}

	/**
	 * Implement this method and use the helper to adapt the view to the given item.
	 *
	 * @param helper A fully initialized helper.
	 * @param item   The item that needs to be displayed.
	 */
	@Override
	protected void convert(@NonNull BaseViewHolder helper, ApkInfo item) {
		if (item == null){
			return;
		}
		String appName = item.getLabel();
		Drawable icon = item.getApk_icon();
		helper.setText(R.id.app_item_tv, appName);
		Glide.with(mContext).load(icon).into((ImageView)helper.getView(R.id.app_item_iv));
	}
}
