package com.android.lottery.lottery.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import com.android.lottery.lottery.R;

public class MyProgressDialog extends ProgressDialog{

	
	public MyProgressDialog(Context context) {
		super(context);
	}

	public MyProgressDialog(Context context, int theme) {
		super(context, theme);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.progressdialog_layout);
		this.setCancelable(true);
	}
	
}
