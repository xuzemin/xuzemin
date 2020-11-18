package com.ctv.settings.security.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctv.settings.R;
import com.ctv.settings.security.bean.PermissionBean;

import java.util.List;

public class PermissionsDetailListAadpter extends BaseAdapter {
	private PackageManager packageManager;

	private LayoutInflater inflater;

	private Context context;

	private List<PermissionBean> permissionBeans;

	private int selectedId = -1;

	public void setAppData(List<PermissionBean> packages) {
		this.permissionBeans = packages;
		notifyDataSetChanged();
	}

	public PermissionsDetailListAadpter(Context context, List<PermissionBean> packages) {
		this.context = context;
		this.permissionBeans = packages;
		inflater = LayoutInflater.from(context);
		packageManager = context.getPackageManager();
	}

	@Override
	public int getCount() {
		return permissionBeans == null ? 0 : permissionBeans.size();
	}

	@Override
	public Object getItem(int i) {
		return permissionBeans.get(i);
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

		PermissionBean bean = permissionBeans.get(i);
		String appName = bean.getName();
		if (convertView == null) {

			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_persission_detali,
					null);
			holder.tv = (TextView) convertView.findViewById(R.id.app_item_tv);
			holder.iv = (ImageView) convertView.findViewById(R.id.app_item_iv);
			Log.d("qkmin adapter","isChecked:"+bean.isChecked());
			holder.iv.setBackgroundResource(
					bean.isChecked()?R.mipmap.on:R.mipmap.off);
			holder.tv.setText(appName);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
			holder.tv.setText(appName);
			holder.iv.setBackgroundResource(
					bean.isChecked()?R.mipmap.on:R.mipmap.off);
		}
		return convertView;
	}

	class ViewHolder {
		ImageView iv;
		TextView tv;
	}
}
