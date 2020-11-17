package com.ctv.annotation.utils;

import android.content.Context;
import android.util.Log;

import com.ctv.annotation.WhiteBoardApplication;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ToofifiLog {
    public static final String TAG = ToofifiLog.class.getSimpleName();
    private static final Boolean DEBUG = true ;

    public static final int LOG_LEVEL_TRACE = 1;
    public static final int LOG_LEVEL_DEBUG = 2;
    public static final int LOG_LEVEL_INFO = 3;
    public static final int LOG_LEVEL_WARN = 4;
    public static final int LOG_LEVEL_ERROR = 5;
    public static final int LOG_LEVEL_FATAL = 6;


    private static final int LOG_SWTICH_SIZE = 5 * 1024 * 1024;
    private static final int LOG_SWTICH_CHECK_COUNT = 512;
    private static final int LOG_DATA_INFO_OBJ_COUNT = 1024;

    private static final int LOG_BUFFER_HEAD = 56;
    private static final int LOG_BUFFER_BODY = (1024 - LOG_BUFFER_HEAD);
    private static final String LOG_FILE_NAME = "ToofifiLog";
    private static final String LOG_FILE_NAME_BACK = LOG_FILE_NAME + ".back";


    private String logFilePath = null;
    private String backLogFilePath = null;
    private File logFile = null;
    private FileWriter fileWriter = null;
    private BufferedWriter bufferedWriter = null;
    private FileOutputStream logFileOutputStream = null;
    private ObjectOutputStream logObjectOutputStream = null;

    private static BlockingQueue<LogDataInfo> log_info_queue = new ArrayBlockingQueue<>(LOG_DATA_INFO_OBJ_COUNT);

    private Context context;
    private int app_log_level = LOG_LEVEL_INFO;

    private WriteLogThread mWriteLogThread;
    private int tag_run_WriteLogThread = 1;

    private static SimpleDateFormat m_dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

    private static final String LOG_TRACE_LEVER = "[TRACE]";
    private static final String LOG_DEBUG_LEVER = "[DEBUG]";
    private static final String LOG_INFO_LEVER = "[INFO] ";
    private static final String LOG_WARN_LEVER = "[WARN] ";
    private static final String LOG_ERROR_LEVER = "[ERROR]";
    private static final String LOG_FATAL_LEVER = "[FATAL]";

    public static void t(String TAG, String log_content)
    {
        if(DEBUG)
            Log.v(TAG, log_content);
    }

    public static void d(String TAG, String log_content)
    {
        if(DEBUG)
            Log.d(TAG, log_content);
    }

    public static void i(String TAG, String log_content)
    {
        if(DEBUG)
            Log.i(TAG, log_content);
    }

    public static void w(String TAG, String log_content)
    {
        if(DEBUG)
            Log.w(TAG, log_content);
    }

    public static void e(String TAG, String log_content)
    {
        if(DEBUG)
            Log.e(TAG, log_content);
    }

    public static void f(String TAG, String log_content)
    {
        if(DEBUG)
            Log.v(TAG, log_content);
    }

    public static void TOOFIFI_WRITE_LOG(int log_level, String log_content)
    {
        int log_boby_len = 0;
        LogDataInfo logDataInfo = null;
        if (WhiteBoardApplication.getInstance().AppLogObj == null)
        {
            return;
        }

        if (log_level < LOG_LEVEL_TRACE || log_level > LOG_LEVEL_FATAL)
        {
            return;
        }

        logDataInfo = ObjectManager.allocateLogDataInfoObj();
        if (logDataInfo == null)
        {
            return;
        }

        logDataInfo.logContent.delete(0, logDataInfo.logContent.length());

        switch (log_level)
        {
            case LOG_LEVEL_TRACE:
                logDataInfo.logContent.append(LOG_TRACE_LEVER);
                break;
            case LOG_LEVEL_DEBUG:
                logDataInfo.logContent.append(LOG_DEBUG_LEVER);
                break;
            case LOG_LEVEL_INFO:
                logDataInfo.logContent.append(LOG_INFO_LEVER);
                break;
            case LOG_LEVEL_WARN:
                logDataInfo.logContent.append(LOG_WARN_LEVER);
                break;
            case LOG_LEVEL_ERROR:
                logDataInfo.logContent.append(LOG_ERROR_LEVER);
                break;
            case LOG_LEVEL_FATAL:
                logDataInfo.logContent.append(LOG_FATAL_LEVER);
                break;
            default:
                logDataInfo.logContent.append(LOG_INFO_LEVER);
                break;
        }

        logDataInfo.logContent.append(getCurrentTime());
        logDataInfo.logContent.append(": ");

        if (log_content.length() > LOG_BUFFER_BODY)
        {
            log_boby_len = LOG_BUFFER_BODY;
        }
        else
        {
            log_boby_len = log_content.length();
        }

        logDataInfo.logContent.append(log_content, 0, log_boby_len);
        logDataInfo.logContent.append("\n");

        logDataInfo.data_len = logDataInfo.logContent.length();

        try
        {
            log_info_queue.put(logDataInfo);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();

            ObjectManager.freeLogDataInfoQueueObj(logDataInfo);
        }

    }

    public class WriteLogThread extends Thread {
        public void run()
        {
            LogDataInfo logDataInfo = null;
            int maxDataCount = 256;
            BlockingQueue<LogDataInfo> temp_log_info_queue = new ArrayBlockingQueue<>(maxDataCount);
            int iLoop = 0;
            int get_log_count = 0;
            int temp_switch_check_count = 0;

            while (1 == tag_run_WriteLogThread)
            {
                try
                {
                    logDataInfo = log_info_queue.take();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                    logDataInfo = null;
                }

                if (logDataInfo != null)
                {
                    try
                    {
                        temp_log_info_queue.put(logDataInfo);
                        get_log_count = get_log_count + 1;
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }

                    if (log_info_queue.size() > 1 && get_log_count < maxDataCount)
                    {
                        continue;
                    }
                }

                for (iLoop = 0; iLoop < get_log_count; iLoop++)
                {
                    logDataInfo = temp_log_info_queue.poll();
                    if (logDataInfo != null)
                    {
                        if (bufferedWriter != null)
                        {
                            try
                            {
                                bufferedWriter.write(logDataInfo.logContent.toString(), 0, logDataInfo.data_len);
                                //logObjectOutputStream.writeObject(logDataInfo.logContent);
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        }

                        ObjectManager.freeLogDataInfoQueueObj(logDataInfo);

                        temp_switch_check_count++;
                    }
                }

                if (get_log_count > 0 && bufferedWriter != null)
                {
                    try
                    {
                        bufferedWriter.flush();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                get_log_count = 0;

                if (temp_switch_check_count >= LOG_SWTICH_CHECK_COUNT)
                {
                    if (logFile.length() >= LOG_SWTICH_SIZE)   // 5M
                    {
                        closeLogWriter();
                        renameFile();
                        initWirterBuffer();
                    }

                    temp_switch_check_count = 0;
                }
            }
        }
    }

    public ToofifiLog(Context context, int config_log_level)
    {
        this.context = context;

        if (config_log_level < LOG_LEVEL_TRACE || config_log_level > LOG_LEVEL_FATAL)
        {
            this.app_log_level = LOG_LEVEL_INFO;
        }
        else
        {
            this.app_log_level = config_log_level;
        }

        logFilePath = getLogFilePath() + "/" + LOG_FILE_NAME;
        backLogFilePath = getLogFilePath() + "/" + LOG_FILE_NAME_BACK;
        if (logFilePath != null)
        {
            logFile = createNewLogFile(logFilePath);
        }
        if (logFile != null)
        {
            initWirterBuffer();
        }
    }

    public void startThread()
    {
        if (logFile != null)
        {
            initWirterBuffer();
        }

        mWriteLogThread = new WriteLogThread();
        mWriteLogThread.start();

        Log.e(TAG, TAG + ", startThread ...");

    }

    public void stopThread()
    {
        tag_run_WriteLogThread = 0;
        mWriteLogThread.interrupt();

        closeLogWriter();

        Log.e(TAG, TAG + ", stopThread ...");
    }


    /**
     * 获取当前时间
     **/
    public static String getCurrentTime()
    {
        return m_dateFormat.format(System.currentTimeMillis());
    }

    /**
     * 获取日志文件的路径
     **/
    public String getLogFilePath()
    {
        File f = context.getFilesDir();
        if (f != null)
        {
            return f.getAbsolutePath();
        }
        return null;
    }

    /**
     * 创建一个空日志文件
     **/
    public File createNewLogFile(String path)
    {
        File file = new File(path);
        if (!file.exists())
        {
            try
            {
                file.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * 文件重命名
     **/
    public void renameFile()
    {

        File backfile = new File(backLogFilePath);
        if (backfile.exists())
        {
            backfile.delete();
        }

        logFile.renameTo(backfile);

        backfile = null;
        logFile = null;


        logFile = new File(logFilePath);
        try
        {
            logFile.createNewFile();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 初始化文件读写流
     **/
    public void initWirterBuffer()
    {
        try
        {
            if (logFile != null)
            {
                fileWriter = new FileWriter(logFile, true);
                bufferedWriter = new BufferedWriter(fileWriter);
                //logFileOutputStream = new FileOutputStream(logFile, true);
                //logObjectOutputStream = new ObjectOutputStream(logFileOutputStream);
                Log.e(TAG, "initWirterBuffer ....");
            }
            else
            {
                Log.e(TAG, "init writer buffer error, cause file is null.");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    public void closeLogWriter()
    {
        try
        {
            Log.e(TAG, "before closeIO bufferedWriter=" + bufferedWriter + "; fileWriter=" + fileWriter);
            if (bufferedWriter != null)
            {
                bufferedWriter.close();
                bufferedWriter = null;
            }

            if (fileWriter != null)
            {
                fileWriter.close();
                fileWriter = null;
            }
            Log.e(TAG, "after closeIO bufferedWriter=" + bufferedWriter + "; fileWriter=" + fileWriter);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    /**
     * 获取调用栈
     *
     * @param level 为调用getStackTrace()的层数级别，0表示当前层，每+1，则向上追溯1层级
     * @return level对应的类名和方法名
     */
    public static String whoCalledMe(int level)
    {
        StackTraceElement ste = new Throwable().getStackTrace()[level];
        // if (level < 2 || level > 3)return "";
        // 获得包名、类名和方法名
        String clazzName = ste.getClassName();
        String mothodName = ste.getMethodName();
        int index = clazzName.lastIndexOf(".");
        clazzName = clazzName.substring(index < 0 ? 0 : index + 1);
        String str = "[" + clazzName + ":" + mothodName + "]";
        return str;
    }

}
