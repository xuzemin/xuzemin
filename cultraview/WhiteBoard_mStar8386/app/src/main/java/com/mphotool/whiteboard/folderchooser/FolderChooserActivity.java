package com.mphotool.whiteboard.folderchooser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mphotool.whiteboard.R;
import com.mphotool.whiteboard.activity.WhiteBoardApplication;
import com.mphotool.whiteboard.utils.BaseUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 文件和文件夹选择器
 */
public class FolderChooserActivity extends Activity {
    private static String TAG = "FolderChooserActivity";
    public static boolean isRunning = false;
    private static final int MSG_SET_DATA = 10;
    TextView savePath;
    RecyclerView recyclerView;
    LinearLayout loading_view;

    //是否为文件夹选择器。true文件夹，false文件
    private boolean isFolderChooser = false;
    private String mimeType = "*/*";
    private String mInitialPath = Environment.getExternalStorageDirectory().getAbsolutePath();

    private FolderChooserAdapter mAdapter;
    private List<FolderChooserInfo> mData;

    private File parentFolder;
    private List<FolderChooserInfo> parentContents;
    private boolean canGoUp = true;
    private ImageButton mBtnBack;

    private ExecutorService singleThreadExecutor;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case MSG_SET_DATA:
                    savePath.setText(parentFolder.getAbsolutePath());
                    mData.clear();
                    mData.addAll(getContentsArray());
                    mAdapter.notifyDataSetChanged();

                    loading_view.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        WhiteBoardApplication.addActivity(this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
        WindowManager.LayoutParams p = getWindow().getAttributes();  //获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.5);
        p.width = (int) (d.getWidth() * 0.5);
        p.alpha = 1.0f;      //设置本身透明度
        p.dimAmount = 0.1f;      //设置窗口外黑暗度
        getWindow().setAttributes(p);
        getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_file_chooser));

        setContentView(R.layout.activity_folder_chooser);

        LinearLayout root = (LinearLayout) findViewById(R.id.root_layout);
        root.setLayoutParams(new FrameLayout.LayoutParams((int) (d.getWidth() * 0.5), (int) (d.getHeight() * 0.5)));

        isFolderChooser = getIntent().getBooleanExtra("isFolderChooser", false);
        String mime_Type = getIntent().getStringExtra("mimeType");
        String file_path = getIntent().getStringExtra("file_path");
        mimeType = mime_Type == null ? mimeType : mime_Type;
        singleThreadExecutor = Executors.newSingleThreadExecutor();

        mInitialPath = file_path == null ? mInitialPath : file_path;
        parentFolder = new File(mInitialPath);
        initView();
        setData();
    }

    @Override protected void onResume()
    {
        super.onResume();
        isRunning = true;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }

    @Override protected void onPause()
    {

        super.onPause();
        isRunning = false;
    }

    @Override protected void onStop()
    {
        super.onStop();

    }

    @Override
    protected void onDestroy()
    {
        WhiteBoardApplication.removeActivity(this);
        super.onDestroy();
    }

    private void setData()
    {
        singleThreadExecutor.execute(new Runnable() {
            @Override
            public void run()
            {
                parentContents = isFolderChooser ? listFiles() : listFiles(mimeType);
                handler.sendEmptyMessage(MSG_SET_DATA);
            }
        });
    }

    private void initView()
    {
        findViewById();
        setRecyclerView();
        savePath.setText(parentFolder.getAbsolutePath());
    }


    private void setRecyclerView()
    {
        mData = new ArrayList<>();
        //设置布局管理器
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);

        recyclerView.addItemDecoration(new SimpleItemDecoration(this, getResources().getDimensionPixelSize(R.dimen.divider_height)));

        //设置适配器
        mAdapter = new FolderChooserAdapter(this, mData, new ItemClickCallback() {
            @Override
            public void onClick(View view, int position, FolderChooserInfo info)
            {
                onSelection(view, position, info);
            }
        });
        recyclerView.setAdapter(mAdapter);
    }

    private List<FolderChooserInfo> getContentsArray()
    {
        List<FolderChooserInfo> results = new ArrayList<>();
        if (parentContents == null)
        {
            if (canGoUp)
            {
                FolderChooserInfo info = new FolderChooserInfo();
                info.setName("...");
                info.setFile(null);
                info.setImage(R.drawable.floder_back);
                results.add(info);
            }
            return results;
        }
        if (canGoUp)
        {
            FolderChooserInfo info = new FolderChooserInfo();
            info.setName("...");
            info.setFile(null);
            info.setImage(R.drawable.floder_back);
            results.add(info);
        }
        results.addAll(parentContents);
        return results;
    }

    public void onSelection(View view, int position, FolderChooserInfo info)
    {
        BaseUtils.dbg(TAG,"canGoUp=" + (canGoUp?"true":"false") + " position=" + position);
        if (canGoUp && position == 0)
        {
            if (parentFolder.isFile())
            {
                parentFolder = parentFolder.getParentFile();
            }
            parentFolder = parentFolder.getParentFile();
            if (parentFolder.getAbsolutePath().equals("/storage/emulated"))
            {
                parentFolder = parentFolder.getParentFile();
            }
            canGoUp = parentFolder.getParent() != null;
        }
        else
        {
            parentFolder = info.getFile();
            canGoUp = true;
            if (parentFolder.getAbsolutePath().equals("/storage/emulated"))
            {
                parentFolder = new File("/mnt/usb");
            }
        }
        if (parentFolder.isFile())
        {
            BaseUtils.dbg(TAG,"0000");
            ChooserEnd();
        }
        else
        {
            BaseUtils.dbg(TAG,"1111");
            loading_view.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            setData();
        }
    }

    private List<FolderChooserInfo> listFiles()
    {
        File[] contents = parentFolder.listFiles();
        List<FolderChooserInfo> results = new ArrayList<>();
        if (contents != null)
        {
            for (File fi : contents)
            {
                if (fi.isDirectory())
                {
                    FolderChooserInfo info = new FolderChooserInfo();
                    info.setName(fi.getName());
                    info.setFile(fi);
                    info.setImage(fileType(fi));
                    results.add(info);
                }
            }
            Collections.sort(results, new FolderSorter());
            return results;
        }
        return null;
    }

    private List<FolderChooserInfo> listFiles(String mimeType)
    {
        File[] contents = parentFolder.listFiles();
        List<FolderChooserInfo> results = new ArrayList<>();
        if (contents != null)
        {
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            for (File fi : contents)
            {
                if (fi.isDirectory())
                {
                    FolderChooserInfo info = new FolderChooserInfo();
                    info.setName(fi.getName());
                    info.setFile(fi);
                    info.setImage(fileType(fi));
                    results.add(info);
                }
                else
                {
                    if (fileIsMimeType(fi, mimeType, mimeTypeMap))
                    {
                        FolderChooserInfo info = new FolderChooserInfo();
                        info.setName(fi.getName());
                        info.setFile(fi);
                        info.setImage(fileType(fi));
                        results.add(info);
                    }
                }
            }
            Collections.sort(results, new FileSorter());
            return results;
        }
        return null;
    }

    boolean fileIsMimeType(File file, String mimeType, MimeTypeMap mimeTypeMap)
    {
        if (mimeType == null || mimeType.equals("*/*"))
        {
            return true;
        }
        else
        {
            // get the file mime type
            String filename = file.toURI().toString();
            int dotPos = filename.lastIndexOf('.');
            if (dotPos == -1)
            {
                return false;
            }
            String fileExtension = filename.substring(dotPos + 1);

            /**增加对NCC文件的支持*/
            if (mimeType.contains("note") && fileExtension.contains("mrc"))
            {
                return true;
            }

            String fileType = mimeTypeMap.getMimeTypeFromExtension(fileExtension);
            if (fileType == null)
            {
                return false;
            }
            // check the 'type/subtype' pattern
            if (fileType.equals(mimeType))
            {
                return true;
            }
            // check the 'type/*' pattern
            int mimeTypeDelimiter = mimeType.lastIndexOf('/');
            if (mimeTypeDelimiter == -1)
            {
                return false;
            }
            String mimeTypeMainType = mimeType.substring(0, mimeTypeDelimiter);
            String mimeTypeSubtype = mimeType.substring(mimeTypeDelimiter + 1);
            if (!mimeTypeSubtype.equals("*"))
            {
                return false;
            }
            int fileTypeDelimiter = fileType.lastIndexOf('/');
            if (fileTypeDelimiter == -1)
            {
                return false;
            }
            String fileTypeMainType = fileType.substring(0, fileTypeDelimiter);
            if (fileTypeMainType.equals(mimeTypeMainType))
            {
                return true;
            }
        }
        return false;
    }

    private static class FileSorter implements Comparator<FolderChooserInfo> {
        @Override
        public int compare(FolderChooserInfo lhs, FolderChooserInfo rhs)
        {
            if (lhs.getFile().isDirectory() && !rhs.getFile().isDirectory())
            {
                return -1;
            }
            else if (!lhs.getFile().isDirectory() && rhs.getFile().isDirectory())
            {
                return 1;
            }
            else
            {
                return lhs.getName().compareTo(rhs.getName());
            }
        }
    }

    private static class FolderSorter implements Comparator<FolderChooserInfo> {
        @Override
        public int compare(FolderChooserInfo lhs, FolderChooserInfo rhs)
        {
            return lhs.getName().compareTo(rhs.getName());
        }
    }

    public interface ItemClickCallback {
        void onClick(View view, int position, FolderChooserInfo info);
    }

    private void findViewById()
    {
        mBtnBack = (ImageButton) findViewById(R.id.btn_back);
        savePath = (TextView) findViewById(R.id.save_path);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        loading_view = (LinearLayout) findViewById(R.id.loading_view);

        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v)
            {
//                BaseUtils.dbg(TAG,"canGoUp=" + (canGoUp?"true":"false"));
                if (canGoUp)
                {
                    if (parentFolder.isFile())
                    {
                        parentFolder = parentFolder.getParentFile();
                    }
                    parentFolder = parentFolder.getParentFile();
                    if (parentFolder.getAbsolutePath().equals("/storage/emulated"))
                    {
                        parentFolder = parentFolder.getParentFile();
                    }
                    canGoUp = parentFolder.getParent() != null;
                    if (!parentFolder.isFile())
                    {
                        loading_view.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        setData();
                    }
                }
                else
                {
                    finish();
                }
            }
        });
    }

    private void ChooserEnd()
    {
        File result = parentFolder;
        BaseUtils.dbg(TAG, "FolderChooserAdapter result= " + result.toString());
        Intent intent = new Intent();
        intent.putExtra("file_path", result);
        setResult(RESULT_OK, intent);
        finish();
    }

    private int fileType(File file)
    {
        int image = R.drawable.type_file;
        if (file.isDirectory())
        {
            image = R.drawable.type_folder;
        }
        else
        {
            try
            {
//            指定文件类型的图标
                String[] token = file.getName().split("\\.");
                String suffix = token[token.length - 1];
                if (suffix.equalsIgnoreCase("txt"))
                {
                    image = R.drawable.type_txt;
                }
                else if (suffix.equalsIgnoreCase("pdf"))
                {
                    image = R.drawable.type_pdf;
                }
                else if (suffix.equalsIgnoreCase("ncc"))
                {
                    image = R.drawable.type_note;
                }
                else if (suffix.equalsIgnoreCase("png") || suffix.equalsIgnoreCase("jpg") || suffix.equalsIgnoreCase("jpeg") || suffix.equalsIgnoreCase("gif"))
                {
                    image = R.drawable.type_image;
                }
                else if (suffix.equalsIgnoreCase("mp3"))
                {
                    image = R.drawable.type_mp3;
                }
                else if (suffix.equalsIgnoreCase("mp4") || suffix.equalsIgnoreCase("rmvb") || suffix.equalsIgnoreCase("avi"))
                {
                    image = R.drawable.type_video;
                }
                else if (suffix.equalsIgnoreCase("apk"))
                {
                    image = R.drawable.type_apk;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return image;
    }


    public class SimpleItemDecoration extends RecyclerView.ItemDecoration {

        private Drawable mDivider;     //分割线Drawable
        private int mDividerHeight;  //分割线高度

        /**
         * 使用line_divider中定义好的颜色
         *
         * @param context
         * @param dividerHeight 分割线高度
         */
        public SimpleItemDecoration(Context context, int dividerHeight)
        {
            mDivider = context.getResources().getDrawable(R.drawable.line_divider);
            mDividerHeight = dividerHeight;
        }

        //获取分割线尺寸
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
        {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(0, 0, 0, mDividerHeight);
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state)
        {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++)
            {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDividerHeight;
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }
}
