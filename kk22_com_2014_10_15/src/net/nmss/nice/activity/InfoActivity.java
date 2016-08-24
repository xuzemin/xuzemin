package net.nmss.nice.activity;

import net.nmss.nice.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class InfoActivity extends Activity{
	private TextView textView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Intent intent = this.getIntent();  
		Bundle bundle = intent.getExtras();       
		String str = bundle.getString("something");
		setContentView(R.layout.activity_info);
		textView = (TextView) findViewById(R.id.text);
		textView.setText(str);
	}
}
