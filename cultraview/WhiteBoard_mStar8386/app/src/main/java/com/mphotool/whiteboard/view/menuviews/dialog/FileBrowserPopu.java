
package com.mphotool.whiteboard.view.menuviews.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mphotool.whiteboard.R;
import com.mphotool.whiteboard.folderchooser.FileUtils;
import com.mphotool.whiteboard.folderchooser.HFileGridAdapter;
import com.mphotool.whiteboard.folderchooser.HFileListAdapter;
import com.mphotool.whiteboard.utils.BaseUtils;
import com.mphotool.whiteboard.utils.Constants;
import com.mphotool.whiteboard.utils.OnTouchStateChangeListener;
import com.mphotool.whiteboard.view.menuviews.HFile;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FileBrowserPopu implements OnClickListener, OnItemClickListener {
    private static String TAG = "FileBrowserPopu";
    private OnChooseListenter chooseListenter;

    private String currentBasePath = Constants.WHITE_PATH;

    private List<FileUtils> fileList = new ArrayList<FileUtils>();
    private FileUtils fileParent;

    private String currentSelectPath;

    private SimpleDateFormat format = new SimpleDateFormat("y/MM/dd hh:mm");

    private boolean isGridLayout = true;

    private boolean isLocalStore = true;

    private boolean isSortByTime;

    private ImageView mBack;

    private TextView mCancel;

    private TextView mConfirm;

    private Context mContext;

    private TextView mCurrentTime;

    private HFileGridAdapter mFileGridAdapter;

    private HFileListAdapter mFileListAdapter;

    private RelativeLayout mFileListLayout;

    private TextView mFileName;

    private GridView mFilePathGrid;

    private ImageView mFilePathLayout;

    private ListView mFilePathList;

    private ImageView mFilePathSort;

    private RelativeLayout mFileSaveLayout;

    private TextView mFileType;

    private HFile.FileType[] mFileTypes;

    private ImageView mLocalStore;

    private TextView mTitle;

    private ImageView mUsbStore;

    private TextView mfilePath;

    private MyPopu myPopu;

    private int mType = 0;

    private int function = 0;

    private String fileName;
    private String OpenfileName = null;

    public interface OnChooseListenter {
        void onConfirm(String path,String name,int type);
    }

    public FileBrowserPopu(Context ctx, View view, int type,String name,int function) {
        this.myPopu = new MyPopu(ctx, -1, -1, R.layout.layout_file_browser);
        this.mContext = ctx;
        this.mType = type;
        this.fileName = name;
        this.function = function;
        this.myPopu.setAlpha(140);
        this.myPopu.showAtLocation(view, 16, 0, 0);

        initView();
    }

    private void initView() {
        this.mTitle = (TextView) this.myPopu.getView(R.id.title);
        this.mBack = (ImageView) this.myPopu.getView(R.id.back);
        this.mFilePathSort = (ImageView) this.myPopu.getView(R.id.file_path_sort);
        this.mFilePathLayout = (ImageView) this.myPopu.getView(R.id.file_path_layout);
        this.mFileListLayout = (RelativeLayout) this.myPopu.getView(R.id.file_list_layout);
        this.mLocalStore = (ImageView) this.myPopu.getView(R.id.local_store);
        this.mUsbStore = (ImageView) this.myPopu.getView(R.id.usb_store);
        this.mfilePath = (TextView) this.myPopu.getView(R.id.file_path);
        this.mFilePathGrid = (GridView) this.myPopu.getView(R.id.file_path_grid);
        this.mFilePathList = (ListView) this.myPopu.getView(R.id.file_path_list);
        this.mCurrentTime = (TextView) this.myPopu.getView(R.id.current_time);
        this.mCancel = (TextView) this.myPopu.getView(R.id.cancel);
        this.mConfirm = (TextView) this.myPopu.getView(R.id.confirm);
        this.mFileSaveLayout = (RelativeLayout) this.myPopu.getView(R.id.file_save_layout);
        this.mFileName = (TextView) this.myPopu.getView(R.id.file_name);
        this.mFileType = (TextView) this.myPopu.getView(R.id.file_type);
        this.mBack.setOnClickListener(this);
        this.mFilePathSort.setOnClickListener(this);
        this.mFilePathLayout.setOnClickListener(this);
        this.mLocalStore.setOnClickListener(this);
        this.mUsbStore.setOnClickListener(this);
        this.mCancel.setOnClickListener(this);
        this.mConfirm.setOnClickListener(this);
        this.mCancel.setOnTouchListener(OnTouchStateChangeListener.getInstance());
        this.mConfirm.setOnTouchListener(OnTouchStateChangeListener.getInstance());
        this.mCurrentTime.setText(this.format.format(new Date(System.currentTimeMillis())));
        if(function == 1) {
            mTitle.setText(R.string.insert_pic);
            mFileName.setFocusable(false);
        }else{
            mTitle.setText(R.string.save_file);
        }
        if(fileName != null)
            this.mFileName.setText(fileName);
        else
            this.mFileName.setText(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        initInternalPath();
        this.mFilePathGrid.setOnItemClickListener(this);
        this.mFilePathList.setOnItemClickListener(this);
    }

    private void initInternalPath() {
        File root;
        String path = Constants.WHITE_BOARD_PATH;
        if(this.mType == 0)
            path = Constants.WHITE_BOARD_PDF_PATH;
        else if(this.mType == 1)
            path = Constants.WHITE_BOARD_IMAGE_PATH;
        else if(this.mType == 2)
            path = Constants.WHITE_BOARD_MRC_PATH;
        root = new File(path);
        if (!root.exists()) {
            root.mkdir();
        }

        if (this.fileParent == null) {
            this.fileParent = new FileUtils(root);
        }
        new ScanTask().execute("init");
    }

    private void initExternalPath() {
        File usbroot = new File(Constants.PATH);
        if (!usbroot.exists()) {
            usbroot.mkdir();
        }
        if (this.fileParent == null) {
            this.fileParent = new FileUtils(usbroot);
        }
        new ScanTask().execute("init");

    }

    private List<FileUtils> filter(File[] files,int type) {
        List<FileUtils> currentFiles = new ArrayList();
        if (files != null) {
            for (File file : files) {
                FileUtils mFile = new FileUtils(file);
                if(file.isDirectory()){
//                    BaseUtils.dbg(TAG,"000 file. name=" + file.getPath() + " leng=" +file.getPath().length());

                    if(file.getPath().length() >= 4 && file.getPath().substring(0,4).equals("/mnt")){
                        if(file.getPath().length() >= 8 && file.getPath().substring(0,8).equals(Constants.USB_PATH)){
                            mFile.setFilePath(file.getPath());
                            mFile.setFileName(file.getName());
                            mFile.setFileType(4);
                            currentFiles.add(mFile);
                        }
                        if(file.getPath().length() >= 17 && file.getPath().substring(0,17).equals(Constants.SD_PATH)){
                            mFile.setFilePath(file.getPath());
                            mFile.setFileName(file.getName());
                            mFile.setFileType(4);
                            currentFiles.add(mFile);
                        }
                    }else {
                        mFile.setFilePath(file.getPath());
                        mFile.setFileName(file.getName());
                        mFile.setFileType(4);
                        currentFiles.add(mFile);
                    }
                }else{
                    int dot = file.getName().lastIndexOf(46);
                    if (!(dot == -1 || dot + 1 == file.getName().length() - 1)) {
                        String name = file.getName().substring(dot + 1, file.getName().length()).toLowerCase();
                        if(name.equalsIgnoreCase("pdf") && mType == 0){
                            mFile.setFilePath(file.getPath());
                            mFile.setFileName(file.getName());
                            mFile.setFileType(0);
                            currentFiles.add(mFile);
                        }else if(name.equalsIgnoreCase("mrc") && mType == 2){
                            mFile.setFilePath(file.getPath());
                            mFile.setFileName(file.getName());
                            mFile.setFileType(2);
                            currentFiles.add(mFile);
                        }else if((name.equalsIgnoreCase("jpg") ||
                                name.equalsIgnoreCase("png") ||
                                name.equalsIgnoreCase("bmp")) && mType == 1){
                            mFile.setFilePath(file.getPath());
                            mFile.setFileName(file.getName());
                            mFile.setFileType(1);
                            currentFiles.add(mFile);
                        }
                    }
                }
            }
        }
        return currentFiles;
    }

    public void setChooseListenter(OnChooseListenter chooseListenter) {
        this.chooseListenter = chooseListenter;
    }

    public String getNewFileName(){
        if(function == 1 && OpenfileName != null)
            return OpenfileName;
        if(mFileName == null)
            return null;

        return mFileName.getText().toString();
    }

    public String getNewFilePath()
    {
        return fileParent.getFilePath().toString();
    }
    public void dismiss() {
        this.myPopu.dismiss();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                if (this.fileParent != null
                        && !this.fileParent.getFilePath().equals(currentBasePath)
                        && this.fileParent.getParentFile() != null
                        && this.fileParent.getParentFile().listFiles() != null) {
                    new ScanTask().execute("child");
                }
                return;
            case R.id.cancel:
                dismiss();
                return;
            case R.id.confirm:
                if (this.chooseListenter != null) {
                    this.chooseListenter.onConfirm(currentSelectPath,getNewFileName(),mType);
                    return;
                }
                return;
            case R.id.file_path_layout:
                if (this.isGridLayout) {
                    this.mFilePathLayout.setImageResource(R.drawable.filepath_list);
                    this.mFileListLayout.setVisibility(View.VISIBLE);
                    this.mFilePathGrid.setVisibility(View.GONE);
                } else {
                    this.mFilePathLayout.setImageResource(R.drawable.filepath_thumbnail);
                    this.mFileListLayout.setVisibility(View.GONE);
                    this.mFilePathGrid.setVisibility(View.VISIBLE);
                }
                this.isGridLayout ^= true;
                if (this.isLocalStore) {
                    initInternalPath();
                    return;
                } else {
                    initExternalPath();
                    return;
                }
            case R.id.file_path_sort:
                if (this.isSortByTime) {
                    this.mFilePathSort.setImageResource(R.drawable.filepath_sort_normal);
                } else {
                    this.mFilePathSort.setImageResource(R.drawable.filepath_sort_selected);
                }
                this.isSortByTime ^= true;
                if (this.isGridLayout) {
                    this.mFileGridAdapter
                            .notifyDataSetChanged(fileList, this.isSortByTime);
                    return;
                } else {
                    this.mFileListAdapter
                            .notifyDataSetChanged(fileList, this.isSortByTime);
                    return;
                }
            case R.id.local_store:
                this.currentBasePath = Constants.WHITE_PATH;
                this.fileParent = null;
                initInternalPath();
                this.mLocalStore.setBackgroundResource(R.drawable.local_storage_selected);
                this.mUsbStore.setBackgroundResource(R.drawable.usb_normal);
                this.isLocalStore = true;
                return;
            case R.id.usb_store:
                this.currentBasePath = Constants.PATH;
                this.fileParent = null;
                initExternalPath();
                this.mLocalStore.setBackgroundResource(R.drawable.local_storage_normal);
                this.mUsbStore.setBackgroundResource(R.drawable.usb_selected);
                this.isLocalStore = false;
                return;
            default:
                return;
        }
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if(fileList == null && fileList.size() < 1)
            return;
        FileUtils file = fileList.get(position);
        if (file.getFileType() == 4) {
            new ScanTask().execute("init");
            if (fileList != null) {
                this.fileParent = file;
                String mTemp= fileParent.getFilePath();
                StringBuilder sb = new StringBuilder();
//                BaseUtils.dbg(TAG," file. mTemp=" + mTemp+ " leng=" +mTemp.length());
                if(!mTemp.substring(0,4).equals(Constants.PATH)) {
                    sb.append(mContext.getString(R.string.save_internal));
                    sb.append(mTemp.substring(19,mTemp.length()));
                }else {
                    if((mTemp.length() >= 8 && mTemp.substring(5,8).equals("usb")) || (mTemp.length() >= 17 && mTemp.substring(5,17).equals("other_sdcard"))) {
                        if ((mTemp.length() >= 8 && mTemp.substring(5, 8).equals("usb"))) {
                            sb.append(mContext.getString(R.string.save_external));
                            if(mTemp.length() >= 18){
                                sb.append(mTemp.substring(18, mTemp.length()));
                            }else{
                                sb.append(mTemp.substring(8, mTemp.length()));
                            }
                        }
                        if (mTemp.length() >= 17 && mTemp.substring(5, 17).equals("other_sdcard")) {
                            sb.append(mContext.getString(R.string.save_sd));
                            if(mTemp.length() >= 27) {
                                sb.append(mTemp.substring(27, mTemp.length()));
                            }else{
                                sb.append(mTemp.substring(17, mTemp.length()));
                            }
                        }
                    }else{
                        sb.append(Constants.PATH);
                    }
                }
                this.mfilePath.setText(sb.toString());
            }
        } else {
            for (int i = 0; i < this.fileList.size(); i++) {
                FileUtils hFile = fileList.get(i);
                if (i == position) {
                    hFile.isSelect = true;
                } else {
                    hFile.isSelect = false;
                }
            }
            String name = file.getFileName();
            if (!(TextUtils.isEmpty(name) || name.indexOf(".") == -1)) {
                this.mFileName.setText(name.substring(0, name.indexOf(".")));
                if(function == 1) {
                    OpenfileName = name;
                    int dot = file.getFileName().lastIndexOf(46);
                    if (!(dot == -1 || dot + 1 == file.getFileName().length() - 1)) {
                        String mtemp = file.getFileName().substring(dot + 1, file.getFileName().length()).toLowerCase();
                        mFileType.setText(mtemp);
                    }
                }
            }
        }
        if (this.isGridLayout) {
            this.mFileGridAdapter.notifyDataSetChanged(this.fileList);
        } else {
            this.mFileListAdapter.notifyDataSetChanged(this.fileList);
        }
    }

    private void setTextType(int type){
        if(type == 0)
            mFileType.setText("pdf");
        else if (type == 1)
            mFileType.setText("png");
        else if(type == 2)
            mFileType.setText("mrc");
    }

    private class ScanTask extends AsyncTask<String, Integer, String> {
        AlertDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new SpotsDialog(mContext, mContext.getResources().getString(R.string.loading));
            dialog.show();
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    dialog.dismiss();
                }
            });
        }

        @Override
        protected String doInBackground(String... params) {
            fileList.clear();
            if (params != null && params[0].equalsIgnoreCase("child")) {
                fileList = filter(fileParent.getParentFile().listFiles(), mType);
                fileParent = new FileUtils(fileParent.getParentFile());
                currentSelectPath = fileParent.getFilePath();
            }else {
                fileList = filter(fileParent.listFiles(), mType);
                currentSelectPath = fileParent.getFilePath();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            dialog.dismiss();
            setTextType(mType);
            StringBuilder sb = new StringBuilder();
            String mTemp= fileParent.getFilePath();
            if(!mTemp.substring(0,4).equals(Constants.PATH)) {
                sb.append(mContext.getString(R.string.save_internal));
                sb.append(mTemp.substring(19,mTemp.length()));
            }else {
                if((mTemp.length() >= 8 && mTemp.substring(5,8).equals("usb")) || (mTemp.length() >= 17 && mTemp.substring(5,17).equals("other_sdcard"))) {
                    if ((mTemp.length() >= 8 && mTemp.substring(5, 8).equals("usb"))) {
                        sb.append(mContext.getString(R.string.save_external));
                        if(mTemp.length() >= 18){
                            sb.append(mTemp.substring(18, mTemp.length()));
                        }else{
                            sb.append(mTemp.substring(8, mTemp.length()));
                        }
                    }
                    if (mTemp.length() >= 17 && mTemp.substring(5, 17).equals("other_sdcard")) {
                        sb.append(mContext.getString(R.string.save_sd));
                        if(mTemp.length() >= 27) {
                            sb.append(mTemp.substring(27, mTemp.length()));
                        }else{
                            sb.append(mTemp.substring(17, mTemp.length()));
                        }
                    }
                }else{
                    sb.append(Constants.PATH);
                }
            }
            mfilePath.setText(sb.toString());
            if (isGridLayout) {
                if(mFileGridAdapter != null) {
                    if(fileList != null) {
                        mFileGridAdapter.notifyDataSetChanged(fileList);
                    }
                }else{
                    mFileGridAdapter = new HFileGridAdapter(mContext, fileList);
                    mFilePathGrid.setAdapter(mFileGridAdapter);
                }
            } else {
                if(mFileListAdapter != null) {
                    if(fileList != null)
                        mFileListAdapter.notifyDataSetChanged(fileList);
                }else{
                    mFileListAdapter = new HFileListAdapter(mContext, fileList);
                    mFilePathList.setAdapter(mFileListAdapter);
                }
            }
        }
    }
}
