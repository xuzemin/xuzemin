package com.ctv.settings.security.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctv.settings.security.bean.ApkInfo;
import com.ctv.settings.security.R;

import java.util.List;

public class PermissionsListAadpter extends BaseAdapter {
	private PackageManager packageManager;

	private LayoutInflater inflater;

	private Context context;

	private List<ApkInfo> packages;

	private int selectedId = -1;

	public void setAppData(List<ApkInfo> packages) {
		this.packages = packages;
		notifyDataSetChanged();
	}

	public PermissionsListAadpter(Context context, List<ApkInfo> packages) {
		this.context = context;
		this.packages = packages;
		inflater = LayoutInflater.from(context);
		packageManager = context.getPackageManager();
	}

	@Override
	public int getCount() {
		return packages == null ? 0 : packages.size();
	}

	@Override
	public Object getItem(int i) {
		return packages.get(i);
	}

	@Override
	public long getItemId(int i) {
		return 0;
	}

	public void setSelectedId(int selectedId) {
		this.selectedId = selectedId;
		notifyDataSetChanged();
	}

	@Override
	public View getView(int i, View convertView, ViewGroup parent) {
		ViewHolder holder = null;

		ApkInfo appInfo = packages.get(i);
		String appName = appInfo.getLabel();
		Drawable icon = appInfo.getApk_icon();
		if (convertView == null) {

			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.permission_item_adapter,
					null);
			// convertView.setAlpha(0.5f);
			holder.tv = (TextView) convertView.findViewById(R.id.app_item_tv);
			holder.iv = (ImageView) convertView.findViewById(R.id.app_item_iv);
			holder.iv.setImageDrawable(icon);
			holder.tv.setText(appName);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
			holder.iv.setImageDrawable(icon);
			holder.tv.setText(appName);
		}
		return convertView;
	}

	class ViewHolder {
		ImageView iv;
		TextView tv;
	}
}
