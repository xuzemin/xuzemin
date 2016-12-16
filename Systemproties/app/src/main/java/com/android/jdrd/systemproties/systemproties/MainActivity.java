package com.android.jdrd.systemproties.systemproties;

import android.os.SystemProperties;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) this.findViewById(R.id.text2);


    }
    public String getMacAddress()
    {
        String macAddress = SystemProperties.get("ubootenv.var.ethaddr");

        String[] strings = macAddress.toLowerCase().split(":");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < strings.length; i++)
        {
            stringBuilder.append(strings[i]);
        }
        //Log.i(TAG, "--- DVB Mac Address : " + stringBuilder.toString());
        return stringBuilder.toString();
    }
}
