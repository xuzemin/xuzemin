
package com.ctv.welcome.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build.VERSION;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import com.ctv.welcome.R;
import com.bumptech.glide.Glide;
import com.ctv.welcome.activity.CustomActivity;
import com.ctv.welcome.activity.EditActivity;
import com.ctv.welcome.task.ThreadManager;
import com.ctv.welcome.util.DBUtil;
import com.ctv.welcome.util.FileUtil;
import com.ctv.welcome.util.LogUtils;
import com.ctv.welcome.vo.CustomContentData;
import com.ctv.welcome.vo.PicData;
import com.ctv.welcome.vo.PicDataDao;
import com.ctv.welcome.vo.PicDataDao.Properties;
import com.ctv.welcome.vo.RichEditorInfo;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.greenrobot.greendao.query.WhereCondition;

public class CustomContentAdapter extends Adapter<CustomContentAdapter.ViewHolder> {
    private static final String TAG = "CustomContentAdapter";

    final int NORMAL_ITEM = 0;

    private String SDBACKUPPATH = "/vip_backup_pic/";

    final int SPEICAL_ITEM = 1;

    private Activity activity;

    private TextView cancel;

    private Context context;

    private CustomAdapter customAdapter;

    public List<CustomContentData> datas;

    private String filePath;

    public ViewHolder holder;

    private RelativeLayout inflate;

    private boolean isAdd;

    boolean isDeleteVisible = false;

    private ArrayList<View> itemList = new ArrayList();

    private LayoutInflater layoutInflater;

    private RelativeLayout mContainer;

    private ImageView mDelete;

    public PopupWindow mPopupWindow;

    public View mView;

    private List<PicData> picDataList;

    private RelativeLayout prewview;

    private boolean showDelete = false;

    private class TouchLister implements OnTouchListener {
        private float downX;

        private float downY;

        private ImageView imageView;

        private float moveX;

        private float moveY;

        private int pos;

        TouchLister(int pos, ImageView imageView) {
            this.pos = pos;
            this.imageView = imageView;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case 0:
                    this.downX = motionEvent.getX();
                    this.downY = motionEvent.getY();
                    LogUtils.d(this.downX + "downX");
                    break;
                case 1:
                    float upX = motionEvent.getX();
                    float upY = motionEvent.getY();
                    LogUtils.d(upX + "upX");
                    LogUtils.d(Math.abs(this.moveX - this.downX) + "Math.abs(moveX - downX)");
                    LogUtils.d(Math.abs(this.downX - upX) + "(Math.abs(downX - upX)");
                    System.out.println(Math.abs(this.moveX - this.downX));
                    System.out.println(Math.abs(this.downX - upX));
                    String baseDir = FileUtil.getBaseStorageDir();
                    if (this.moveX - this.downX <= 0.0f
                            || Math.abs(this.moveX - this.downX) <= 200.0f
                            || Math.abs(upX - this.downX) <= 100.0f) {
                        if (this.moveX - this.downX >= 0.0f
                                || Math.abs(this.moveX - this.downX) <= 200.0f
                                || Math.abs(upX - this.downX) <= 100.0f) {
                            if ((Math.abs(this.moveY - this.downY) >= 50.0f && Math.abs(this.moveX
                                    - this.downX) >= 50.0f)
                                    || this.pos < 0) {
                                launchActivity();
                                break;
                            }
                            launchActivity();
                            break;
                        }
                        if (this.pos < CustomContentAdapter.this.datas.size() - 1 && this.pos >= 0) {
                            this.pos++;
                            Glide.with(CustomContentAdapter.this.context)
                                    .load(((CustomContentData) CustomContentAdapter.this.datas
                                            .get(this.pos)).getFilepath()).into(this.imageView);
                        }
                        if (this.pos == CustomContentAdapter.this.datas.size() - 1) {
                            Toast.makeText(CustomContentAdapter.this.context, R.string.the_end, Toast.LENGTH_SHORT)
                                    .show();
                        }
                        CustomContentAdapter.this.filePath = baseDir
                                + "/QRCode/"
                                + ((CustomContentData) CustomContentAdapter.this.datas
                                        .get(this.pos)).getFilename() + FileUtil.JPEG;
                        if (!CustomContentAdapter.this.isAdd) {
                            CustomContentAdapter.this.hideOrShowQrCode();
                            break;
                        }
                    } else if (!CustomContentAdapter.this.isAdd || this.pos != 1) {
                        if (CustomContentAdapter.this.datas.size() > 0 && this.pos >= 0) {
                            if (this.pos == 0) {
                                Glide.with(CustomContentAdapter.this.context)
                                        .load(((CustomContentData) CustomContentAdapter.this.datas
                                                .get(this.pos)).getFilepath()).into(this.imageView);
                            } else {
                                this.pos--;
                                Glide.with(CustomContentAdapter.this.context)
                                        .load(((CustomContentData) CustomContentAdapter.this.datas
                                                .get(this.pos)).getFilepath()).into(this.imageView);
                            }
                            CustomContentAdapter.this.filePath = baseDir
                                    + "/QRCode/"
                                    + ((CustomContentData) CustomContentAdapter.this.datas
                                            .get(this.pos)).getFilename() + FileUtil.JPEG;
                            if (!CustomContentAdapter.this.isAdd) {
                                CustomContentAdapter.this.hideOrShowQrCode();
                                break;
                            }
                        }
                    } else {
                        return false;
                    }
                    break;
                case 2:
                    this.moveX = motionEvent.getX();
                    this.moveY = motionEvent.getY();
                    LogUtils.d(this.moveX + "moveX");
                    break;
            }
            return true;
        }

        private void launchActivity() {
            final Intent intent = new Intent(CustomContentAdapter.this.context, EditActivity.class);
            SharedPreferences sp = CustomContentAdapter.this.context.getSharedPreferences(
                    "image_data", 0);
            ThreadManager.getThreadPoolProxy().excute(new Runnable() {
                public void run() {
                    final String baseDir = FileUtil.getBaseStorageDir();
                    CustomContentAdapter.this.picDataList = DBUtil.mDaoSession
                            .getPicDataDao()
                            .queryBuilder()
                            .where(Properties.FileName
                                    .like(((CustomContentData) CustomContentAdapter.this.datas
                                            .get(TouchLister.this.pos)).getFilename()),
                                    new WhereCondition[0]).list();
                    ((Activity) CustomContentAdapter.this.context).runOnUiThread(new Runnable() {
                        public void run() {
                            if (!CustomContentAdapter.this.isAdd) {
                                return;
                            }
                            if (CustomContentAdapter.this.picDataList == null
                                    || CustomContentAdapter.this.picDataList.size() <= 0) {
                                Toast.makeText(CustomContentAdapter.this.context, "原文件不存在", Toast.LENGTH_SHORT)
                                        .show();
                                return;
                            }
                            String filePath = ((PicData) CustomContentAdapter.this.picDataList
                                    .get(0)).getFilePath();
                            String fileName = ((PicData) CustomContentAdapter.this.picDataList
                                    .get(0)).getFileName();
                            ArrayList<RichEditorInfo> infoList = new ArrayList();
                            for (PicData picData : CustomContentAdapter.this.picDataList) {
                                infoList.add(new RichEditorInfo(picData.getX(), picData.getY(),
                                        picData.getHtmlText()));
                            }
                            Bundle bundle = new Bundle();
                            bundle.putParcelableArrayList("RICHEDITOR_LIST", infoList);
                            intent.putExtras(bundle);
                            if (!TextUtils.isEmpty(filePath)) {
                                intent.putExtra("IMAGE_PATH", baseDir
                                        + CustomContentAdapter.this.SDBACKUPPATH + filePath);
                                intent.putExtra("SHOW_QROCDE", true);
                                intent.putExtra("CUSTOM", true);
                                intent.putExtra("CUSTOM_NAME", fileName);
                                CustomContentAdapter.this.context.startActivity(intent);
                                if (CustomContentAdapter.this.mPopupWindow.isShowing()) {
                                    CustomContentAdapter.this.mPopupWindow.dismiss();
                                }
                            }
                        }
                    });
                }
            });
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private Map<Integer, View> mCacheView = new HashMap();

        ViewHolder(View itemView) {
            super(itemView);
        }

        View getView(int resId) {
            if (this.mCacheView.containsKey(Integer.valueOf(resId))) {
                return (View) this.mCacheView.get(Integer.valueOf(resId));
            }
            View view = this.itemView.findViewById(resId);
            this.mCacheView.put(Integer.valueOf(resId), view);
            return view;
        }
    }

    public CustomContentAdapter(List<CustomContentData> datas, RelativeLayout mContainer,
            TextView cancel, Activity activity, boolean isAdd) {
        this.datas = datas;
        this.activity = activity;
        this.isAdd = isAdd;
        this.mContainer = mContainer;
        this.cancel = cancel;
        this.customAdapter = this.customAdapter;
        notifyDataSetChanged();
    }

    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        }
        return 1;
    }

    public void setDatas(List<CustomContentData> dataList) {
        this.datas = dataList;
        notifyDataSetChanged();
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        if (this.layoutInflater == null) {
            this.layoutInflater = LayoutInflater.from(this.context);
        }
        View view = null;
        switch (viewType) {
            case 0:
                view = this.layoutInflater.inflate(R.layout.adp_custom_item, parent, false);
                break;
            case 1:
                view = this.layoutInflater.inflate(R.layout.adp_custom_content_item, parent, false);
                break;
        }
        this.mView = this.layoutInflater.inflate(R.layout.adp_custom_content_item, parent, false);
        if (this.isAdd) {
            return new ViewHolder(view);
        }
        return new ViewHolder(this.layoutInflater.inflate(R.layout.adp_custom_content_item, parent,
                false));
    }

    public void onBindViewHolder(ViewHolder holder, final int position) {
        this.holder = holder;
        CustomContentData customContentData = (CustomContentData) this.datas.get(position);
        if (this.isAdd) {
            switch (getItemViewType(position)) {
                case 0:
                    ImageView image = (ImageView) holder.getView(R.id.adp_category_icon_iv);
                    ((TextView) holder.getView(R.id.adp_category_name_tv))
                            .setText(customContentData.getFilename());
                    Object filepath = customContentData.getFilepath();
                    if (filepath != null) {
                        Glide.with(this.context).load(filepath).into(image);
                    }
                    enterLocalFileActivity(holder, position);
                    return;
                case 1:
                    ImageView imageSpecial = (ImageView) holder
                            .getView(R.id.adp_custom_content_icon_iv);
                    TextView filename = (TextView) holder.getView(R.id.adp_custom_content_name_tv);
                    this.mDelete = (ImageView) holder.getView(R.id.adp_custom_content_delete_iv);
                    String path = (String)customContentData.getFilepath();
                    if (path != null && (path instanceof String)) {
                        Glide.with(this.context).load(path).into(imageSpecial);
                    }
                    filename.setText(customContentData.getFilename());
                    showSignaturePopup(holder, position, this.mDelete);
                    showDeleteLongClick(holder, position);
                    deleteItem(position);
                    return;
                default:
                    return;
            }
        }
        String mFilename = customContentData.getFilename();
        ImageView imageSpeical = (ImageView) holder.getView(R.id.adp_custom_content_icon_iv);
        TextView filename = (TextView) holder.getView(R.id.adp_custom_content_name_tv);
        this.mDelete = (ImageView) holder.getView(R.id.adp_custom_content_delete_iv);
        Object path2 = customContentData.getFilepath();
        if (path2 != null) {
            Glide.with(this.context).load(path2).into(imageSpeical);
        }
        filename.setText(mFilename);
        showSignaturePopup(holder, position, this.mDelete);
        this.mDelete.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                CustomContentAdapter.this.showDeleteSignatureDialog(position);
            }
        });
    }

    private void showDeleteSignatureDialog(final int position) {
        View view = LayoutInflater.from(this.activity).inflate(R.layout.dialog_confim_delete_hint,
                null);
        final AlertDialog dialog = new Builder(this.activity, R.style.myDialog).create();
        TextView txtCancel = (TextView) view.findViewById(R.id.txt_delete_cancel);
        ((TextView) view.findViewById(R.id.txt_delete_confirm))
                .setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        dialog.dismiss();
                        CustomContentAdapter.this.deleteItem(position);
                    }
                });
        txtCancel.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.setContentView(view);
        dialog.getWindow().clearFlags(131072);
    }

    private void enterLocalFileActivity(ViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (!CustomContentAdapter.this.isAdd || position != 0) {
                    return;
                }
                if (VERSION.SDK_INT < 23) {
                    CustomContentAdapter.this.activity.startActivity(new Intent(
                            CustomContentAdapter.this.activity, CustomActivity.class));
                } else if (ContextCompat.checkSelfPermission(CustomContentAdapter.this.activity,
                        "android.permission.READ_EXTERNAL_STORAGE") != 0) {
                    ActivityCompat.requestPermissions(CustomContentAdapter.this.activity,
                            new String[] {
                                "android.permission.READ_EXTERNAL_STORAGE"
                            }, 1);
                } else {
                    CustomContentAdapter.this.activity.startActivity(new Intent(
                            CustomContentAdapter.this.activity, CustomActivity.class));
                }
            }
        });
    }

    private void deleteItem(final int position) {
        if (this.datas.size() > 0 && position < this.datas.size()) {
            ThreadManager.getThreadPoolProxy().excute(new Runnable() {
                public void run() {
                    final CustomContentData customContentData = (CustomContentData) CustomContentAdapter.this.datas
                            .get(position);
                    CustomContentAdapter.this.picDataList = DBUtil.mDaoSession
                            .getPicDataDao()
                            .queryBuilder()
                            .where(Properties.FileName.like(customContentData.getFilename()),
                                    new WhereCondition[0]).list();
                    ((Activity) CustomContentAdapter.this.context).runOnUiThread(new Runnable() {
                        public void run() {
                            CustomContentAdapter.this.datas.remove(position);
                            CustomContentAdapter.this.notifyDataSetChanged();
                            String filePathobj = (String)customContentData.getFilepath();
                            if (filePathobj instanceof String) {
                                new File(filePathobj).delete();
                            }
                            if (CustomContentAdapter.this.isAdd) {
                                if (CustomContentAdapter.this.picDataList != null
                                        && CustomContentAdapter.this.picDataList.size() > 0) {
                                    ThreadManager.getThreadPoolProxy().excute(new Runnable() {
                                        public void run() {
                                            String baseDir = FileUtil.getBaseStorageDir();
                                            PicDataDao daoSession = DBUtil.mDaoSession
                                                    .getPicDataDao();
                                            String fileName = ((PicData) CustomContentAdapter.this.picDataList
                                                    .get(0)).getFileName();
                                            String backUpFileName = ((PicData) CustomContentAdapter.this.picDataList
                                                    .get(0)).getFilePath();
                                            if (!TextUtils.isEmpty(backUpFileName)) {
                                                new File(baseDir
                                                        + CustomContentAdapter.this.SDBACKUPPATH
                                                        + backUpFileName).delete();
                                            }
                                            if (!TextUtils.isEmpty(fileName)) {
                                                for (PicData data : CustomContentAdapter.this.picDataList) {
                                                    daoSession.deleteByKey(data.getId());
                                                }
                                            }
                                        }
                                    });
                                }
                                if (CustomContentAdapter.this.datas.size() == 1
                                        && CustomContentAdapter.this.cancel.getVisibility() == View.VISIBLE) {
                                    CustomContentAdapter.this.cancel.setVisibility(View.GONE);
                                }
                            } else if (CustomContentAdapter.this.datas.size() == 0
                                    && CustomContentAdapter.this.mContainer != null) {
                                CustomContentAdapter.this.mContainer.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
            });
        }
    }

    private void showDeleteLongClick(ViewHolder holder, int position) {
        holder.itemView.setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View view) {
                CustomContentAdapter.this.isDeleteVisible = true;
                CustomContentAdapter.this.notifyDataSetChanged();
                if (CustomContentAdapter.this.cancel != null
                        && CustomContentAdapter.this.cancel.getVisibility() == View.GONE) {
                    CustomContentAdapter.this.cancel.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });
    }

    private void showSignaturePopup(ViewHolder holder, final int position, ImageView delete) {
        holder.itemView.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (CustomContentAdapter.this.isAdd) {
                    CustomContentAdapter.this.prewview = (RelativeLayout) LayoutInflater.from(
                            CustomContentAdapter.this.context).inflate(
                            R.layout.layout_preview_theme, null);
                } else {
                    CustomContentAdapter.this.prewview = (RelativeLayout) LayoutInflater.from(
                            CustomContentAdapter.this.context).inflate(
                            R.layout.layout_preview_signature, null);
                }
                ImageView image = (ImageView) CustomContentAdapter.this.prewview
                        .findViewById(R.id.lt_preview_iv);
                TextView text = (TextView) CustomContentAdapter.this.prewview
                        .findViewById(R.id.lt_preview_hint_tv);
                if (!CustomContentAdapter.this.isAdd) {
                    text.setVisibility(View.INVISIBLE);
                }
                text.setText(CustomContentAdapter.this.context.getString(R.string.preview_hint));
                Object filepath = ((CustomContentData) CustomContentAdapter.this.datas
                        .get(position)).getFilepath();
                if (filepath instanceof String) {
                    Glide.with(CustomContentAdapter.this.context).load(filepath).into(image);
                } else if (filepath instanceof Integer) {
                    Picasso.with(CustomContentAdapter.this.context)
                            .load(((Integer) filepath).intValue()).fit().into(image);
                }
                CustomContentAdapter.this.filePath = FileUtil.getBaseStorageDir()
                        + "/QRCode/"
                        + ((CustomContentData) CustomContentAdapter.this.datas.get(position))
                                .getFilename() + FileUtil.JPEG;
                LogUtils.i(CustomContentAdapter.TAG, "filepath"
                        + CustomContentAdapter.this.filePath);
                if (!(CustomContentAdapter.this.isAdd || TextUtils
                        .isEmpty(CustomContentAdapter.this.filePath))) {
                    if (new File(CustomContentAdapter.this.filePath).exists()) {
                        CustomContentAdapter.this.createQrcodeUI();
                    } else if (CustomContentAdapter.this.inflate != null) {
                        CustomContentAdapter.this.prewview
                                .removeView(CustomContentAdapter.this.inflate);
                        CustomContentAdapter.this.inflate = null;
                    }
                }
                CustomContentAdapter.this.mPopupWindow = new PopupWindow(
                        CustomContentAdapter.this.prewview, -2, -2, true);
                CustomContentAdapter.this.mPopupWindow.setFocusable(true);
                CustomContentAdapter.this.mPopupWindow.setOutsideTouchable(true);
                CustomContentAdapter.this.mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
                CustomContentAdapter.this.mPopupWindow.showAtLocation(view, 17, 0, 0);
                CustomContentAdapter.this.prewview.setOnTouchListener(new TouchLister(position,
                        image));
            }
        });
    }

    private void hideOrShowQrCode() {
        if (!TextUtils.isEmpty(this.filePath)) {
            if (new File(this.filePath).exists()) {
                if (this.inflate == null) {
                    createQrcodeUI();
                    return;
                }
                Glide.with(this.context).load(this.filePath)
                        .into((ImageView) this.inflate.findViewById(R.id.act_code_pic_iv));
            } else if (this.inflate != null) {
                this.prewview.removeView(this.inflate);
                this.inflate = null;
            }
        }
    }

    private void createQrcodeUI() {
        this.inflate = (RelativeLayout) LayoutInflater.from(this.context).inflate(
                R.layout.layout_qrcode, null);
        ((TextView) this.inflate.findViewById(R.id.act_code_pic_tv)).setText(R.string.code_text);
        Glide.with(this.context).load(this.filePath)
                .into((ImageView) this.inflate.findViewById(R.id.act_code_pic_iv));
        LayoutParams lp = new LayoutParams(-2, -2);
        lp.addRule(8, R.id.lt_preview_iv);
        lp.addRule(7, R.id.lt_preview_iv);
        if (this.prewview != null) {
            this.prewview.addView(this.inflate, lp);
        }
    }

    public void setDeleteVisible(boolean visible) {
        this.isDeleteVisible = visible;
        notifyDataSetChanged();
    }

    public int getItemCount() {
        return this.datas.size();
    }
}
