package com.android.hy.hyplayer;

import android.app.Activity;
import android.os.Bundle;

import javax.crypto.Cipher;

import nz.co.iswe.android.airplay.AirPlayServer;

public class MainActivity extends Activity {
    private AirPlayServer airPlayServer;
    private Thread thread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String transformation = "RSA/None/OAEPWithSHA1AndMGF1Padding";
        try {
            Cipher rsaPkCS1OaepCipher = Cipher
                    .getInstance(transformation);
        } catch (Exception e) {
            e.printStackTrace();
        }

        airPlayServer = AirPlayServer.getIstance();

        airPlayServer.setRtspPort(5000);

        thread = new Thread(airPlayServer);
        thread.start();

        System.out.println("开启AirPlay");
    }
}
