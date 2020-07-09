
package mstar.factorymenu.ui.utils;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemProperties;
import android.util.Log;

import com.cultraview.tv.CtvChannelManager;
import com.cultraview.tv.CtvCommonManager;
import com.cultraview.tv.CtvFactoryManager;
import com.cultraview.tv.CtvTvManager;
import com.cultraview.tv.common.exception.CtvCommonException;
import com.cultraview.tv.utils.CtvCommonUtils;

import mstar.tvsetting.factory.ui.designmenu.DesignMenuActivity;

public class MyBurningService extends Service {
    private static final String TAG = "MyBurningService";

    private boolean ret = true;

    public boolean mIsSignalLock = false;

    private Thread thread = null;

    private Thread checkSingalLock = null;

    private Thread flashLED = null;

    public boolean GorR = true;

    // public boolean is338 = false;

    public int[] LedGpio = {
            255, 255
    };

    public static boolean WATCHDOGON = true;

    public static boolean WATCHDOGOFF = false;

    public CtvFactoryManager mTvFactoryManager;

    public CtvCommonManager mTvCommonManager;

    public CtvChannelManager mTvChannelManager;

    public int testPatternIndex = 0;

    public int currentTvInputSource = 0;

    private String musicPath;

    private MediaPlayer mp3Player;

    public static final int FLAG_IS_PLAYING = 0x1;

    public static final int FLAG_NEED_PLAY = 0x2;

    public static final int FLAG_NEED_STOP = 0x4;

    public static final int MSG_SYNC_STATUS = 1;

    public static final int MSG_PLAY = 2;

    public static final int MSG_STOP = 3;

    public static int playFlag = 0;

    private SharedPreferences.Editor editor;

    public String[] testPatternType = {
            "White", "Red", "Green", "Blue", "Black"
    };

    private SharedPreferences preferences;

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see android.app.Service#onCreate()
     */
    @Override
    public void onCreate() {
        LogUtils.d("MyBurningService--->");
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.mstar.factory.BurningStop");
        registerReceiver(stopServiceReceiver, filter);
        startForeground(1, new Notification());
        mTvFactoryManager = CtvFactoryManager.getInstance();
        mTvCommonManager = CtvCommonManager.getInstance();
        mTvChannelManager = CtvChannelManager.getInstance();
        LedGpio = getLedGpio();
        autoBuring();
        flashLed();
        checkSignalLuck();
        mTvFactoryManager.setWatchDogMode(WATCHDOGOFF);

        // save start ----保存老化前的状态，以便退出时还原
        preferences = getSharedPreferences("saved_state", Activity.MODE_PRIVATE);
        editor = preferences.edit();
        if (CtvCommonManager.getInstance().getBlueScreenMode()) {
            editor.putBoolean("bluescreen_mode", CtvCommonManager.getInstance().getBlueScreenMode());
            try {
                editor.putString("atv_bluescreen_mode",
                        CtvTvManager.getInstance().getEnvironment("ATVBLUESCREENFLAG"));
            } catch (CtvCommonException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            editor.commit();
            // save end
        }

        try {
            CtvTvManager.getInstance().setEnvironment("ATVBLUESCREENFLAG", "0");
        } catch (CtvCommonException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        CtvCommonManager.getInstance().setBlueScreenMode(false);
        CtvFactoryManager.getInstance().setNoSignalAutoShutdown(false);
        Log.v(TAG, " service onCreate...............");
    }

    private int[] getLedGpio() {
        // TODO Auto-generated method stub
        int[] led = {
                255, 255
        };
        String Board = SystemProperties.get("ro.board.platform");
        if (Board.equals("messi")) {
            Log.i(TAG, "messi!");
            led[0] = 8;
            led[1] = 9;
            return led;
        } else if (Board.equals("monet")) {
            Log.i(TAG, "monet!");
            led[0] = 36;
            led[1] = 36;
            return led;
        } else if (Board.equals("mooney")) {
            Log.i(TAG, "mooney!");
            led[0] = 8;
            led[1] = 9;
            return led;
        } else if (Board.equals("macan")) {
            Log.i(TAG, "macan!");
            led[0] = 9;
            led[1] = 8;
            return led;
        } else if (Board.equals("mainz")) {
            if (getBoardName(this).contains("358")) {
                led[0] = 9;
                led[1] = 9;
            } else {
                led[0] = 8;
                led[1] = 8;
            }
            return led;
        } else {
            Log.i(TAG, "board error!");
            return led;
        }
    }

    private void autoBuring() {
        playFlag = 0;
        playMusic();
        handler.sendEmptyMessageDelayed(MSG_SYNC_STATUS, 1000);
        if (thread == null) {
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (ret) {
                        for (int i = 1; i <= testPatternType.length; i++) {
                            testPatternIndex = i;
                            if (mIsSignalLock == false && ret) {
                                mTvFactoryManager.setVideoTestPattern(testPatternIndex);
                                Log.d(TAG, "setVideoTestPattern "
                                        + testPatternType[testPatternIndex - 1]);
                            } else {
                                if (mTvFactoryManager.getVideoTestPattern() != CtvFactoryManager.SCREEN_MUTE_OFF) {
                                    Log.d(TAG,
                                            "getVideoTestPattern ="
                                                    + mTvFactoryManager.getVideoTestPattern());
                                    mTvFactoryManager
                                            .setVideoTestPattern(CtvFactoryManager.SCREEN_MUTE_OFF);
                                }
                            }
                            try {
                                Thread.sleep(600);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }

                    }
                    // stopMusic();
                    handler.sendEmptyMessageDelayed(MSG_STOP, 1);
                    handler.removeMessages(MSG_SYNC_STATUS);
                }
            });

        }
    }

    public void flashLed() {
        if (flashLED == null) {
            flashLED = new Thread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub

                    while (ret) {
                        setLedGpioStatus(GorR);
                        GorR = !GorR;
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                }
            });
        }
    }

    protected void setLedGpioStatus(boolean gorR2) {
        // TODO Auto-generated method stub
        if (LedGpio[0] == 255 || LedGpio[1] == 255)
            return;
        Log.i(TAG, "LedGpio[red/grn] : " + LedGpio[0] + "/" + LedGpio[1]);
        try {
            CtvTvManager.getInstance().setGpioDeviceStatus(LedGpio[0], gorR2);
            if (LedGpio[0] != LedGpio[1]) {
                CtvTvManager.getInstance().setGpioDeviceStatus(LedGpio[1], !gorR2);
            }
        } catch (CtvCommonException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void checkSignalLuck() {
        if (checkSingalLock == null) {
            checkSingalLock = new Thread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    while (ret) {
                        currentTvInputSource = mTvCommonManager.getCurrentTvInputSource();
                        mIsSignalLock = mTvCommonManager.isSignalStable(currentTvInputSource);
                        Log.d(TAG, "currentTvInputSource <" + currentTvInputSource
                                + "> mIsSignalLock =" + mIsSignalLock);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                }
            });
        }
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.w(TAG, "service onDestroy..................");
        stopForeground(true);
        Boolean blueScreenMode = preferences.getBoolean("bluescreen_mode", false);
        if (blueScreenMode) {
            CtvCommonManager.getInstance().setBlueScreenMode(true);
            try {
                CtvTvManager.getInstance().setEnvironment("ATVBLUESCREENFLAG",
                        preferences.getString("atv_bluescreen_mode", "0"));
            } catch (CtvCommonException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            editor.putBoolean("bluescreen_mode", false);
            editor.putString("atv_bluescreen_mode", "0");
            editor.commit();
        }

        mTvFactoryManager.setWatchDogMode(WATCHDOGON);
        if (thread != null && checkSingalLock != null && flashLED != null) {
            ret = false;
            thread.interrupt();
            thread = null;
            Log.w(TAG, "service onDestroy.............set screen mute off.....");
            mTvFactoryManager.setVideoTestPattern(CtvFactoryManager.SCREEN_MUTE_OFF);

            flashLED.interrupt();
            flashLED = null;
            setLedGpioStatus(true);
            try {
                CtvTvManager.getInstance().setEnvironment("factory_burningmode", "0");
            } catch (CtvCommonException e) {
                e.printStackTrace();
            }
            refreshSource();
            checkSingalLock.interrupt();
            checkSingalLock = null;
        }
        unregisterReceiver(stopServiceReceiver);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);
        if (thread != null && checkSingalLock != null && flashLED != null
                && !checkSingalLock.isAlive() && !thread.isAlive() && !flashLED.isAlive()) {
            thread.start();
            flashLED.start();
            checkSingalLock.start();

        }
    }

    private void refreshSource() {
        int curInputSource = mTvCommonManager.getCurrentTvInputSource();
        Log.d(TAG, "curInputSource is " + curInputSource);
        mTvCommonManager.setInputSource(curInputSource);
        Log.d(TAG, "setInputSource " + curInputSource);
        if (curInputSource == CtvCommonManager.INPUT_SOURCE_ATV) {
            int curChannelNumber = mTvChannelManager.getCurrentChannelNumber();
            if (curChannelNumber > 0xFF) {
                curChannelNumber = 0;
            }
            mTvChannelManager.selectProgram(curChannelNumber, CtvChannelManager.SERVICE_TYPE_ATV);
        } else if (curInputSource == CtvCommonManager.INPUT_SOURCE_DTV) {
            curInputSource = mTvCommonManager.getCurrentTvInputSource();
            int curChannelNumber = mTvChannelManager.getCurrentChannelNumber();
            if (curChannelNumber > 0xFF) {
                curChannelNumber = 0;
            }
            mTvChannelManager.selectProgram(curChannelNumber, CtvChannelManager.SERVICE_TYPE_DTV);
        }
    }

    private boolean playMusic() {
        playFlag &= ~FLAG_NEED_PLAY;
        Log.i(TAG, "start to check ctv music on usb..");
        musicPath = FindFileOnUSB("ctv_music.mp3");
        if ("" == musicPath) {
            Log.i(TAG, "not find music..");
            return false;
        }
        Log.i(TAG, " find the music in the dir: " + musicPath);

        try {
            if (mp3Player != null && mp3Player.isPlaying())
                mp3Player.stop();
            mp3Player = new MediaPlayer();
            mp3Player.setDataSource(musicPath);
            mp3Player.prepare();
            mp3Player.start();
            playFlag |= FLAG_IS_PLAYING;
            return true;
        } catch (IOException ex) {
            Log.d(TAG, "playMusic failed:", ex);
            // fall through
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "playMusic failed:", ex);
            // fall through
        } catch (SecurityException ex) {
            Log.d(TAG, "playMusic failed:", ex);
            // fall through
        }
        return false;
    }

    private void stopMusic() {
        Log.i(TAG, "stopmusic");
        playFlag = 0;
        if (mp3Player == null || !mp3Player.isPlaying())
            return;
        try {
            mp3Player.stop();
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "stopMusic failed:", ex);
            // fall through
        } catch (SecurityException ex) {
            Log.d(TAG, "stopMusic failed:", ex);
            // fall through
        }
    }

    private void syncPlayMusicStatus() {
        Log.i(TAG, "playFlag = " + playFlag + "mp3Player.isPlaying() = "
                + ((mp3Player == null) ? mp3Player : mp3Player.isPlaying()));
        if ((playFlag & FLAG_NEED_PLAY) > 0
                || (((playFlag & FLAG_IS_PLAYING)) > 0 && (mp3Player == null || !mp3Player
                        .isPlaying()))) {
            if (!playMusic()) {
                playFlag &= ~FLAG_IS_PLAYING;
            }
        } else if ((playFlag & FLAG_NEED_STOP) > 0) {
            stopMusic();
        }
    }

    public static String FindFileOnUSB(String filename) {
        // TODO Find File On USB function
        String filepath = "";
        File usbroot = new File("/mnt/usb/");
        File targetfile;
        if (usbroot != null && usbroot.exists()) {
            File[] usbitems = usbroot.listFiles();

            for (int sdx = 0; sdx < usbitems.length; sdx++) {
                if (usbitems[sdx].isDirectory()) {
                    targetfile = new File(usbitems[sdx].getPath() + "/" + filename);
                    if (targetfile != null && targetfile.exists()) {
                        filepath = usbitems[sdx].getPath() + "/" + filename;
                        break;
                    }
                }
            }
        }
        return filepath;
    }

    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SYNC_STATUS:
                    handler.removeMessages(MSG_SYNC_STATUS);
                    syncPlayMusicStatus();
                    handler.sendEmptyMessageDelayed(MSG_SYNC_STATUS, 1000);
                    break;
                case MSG_PLAY:
                    playMusic();
                    handler.removeMessages(MSG_SYNC_STATUS);
                    handler.sendEmptyMessageDelayed(MSG_SYNC_STATUS, 1000);
                    break;
                case MSG_STOP:
                    stopMusic();
                    break;
                default:
                    break;
            }
        }
    };

    private BroadcastReceiver stopServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopSelf();
        }
    };

    public String getBoardName(Context ctvContext) {
        String boardName = "CV358H_A";
        String arrays[] = CtvCommonUtils.getCultraviewProjectInfo(ctvContext,
                "tbl_SoftwareVersion", "MainVersion").split("-");
        if (arrays.length > 1) {
            boardName = arrays[1];
        }
        return boardName;
    }
}
