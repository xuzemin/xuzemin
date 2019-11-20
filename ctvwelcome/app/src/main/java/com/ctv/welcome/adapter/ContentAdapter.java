
package com.ctv.welcome.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.ctv.welcome.R;
import com.bumptech.glide.Glide;
import com.ctv.welcome.activity.CustomActivity;
import com.ctv.welcome.activity.EditActivity;
import com.ctv.welcome.activity.IndexActivity;
import com.ctv.welcome.constant.Config;
import com.ctv.welcome.task.ThreadManager;
import com.ctv.welcome.util.DBUtil;
import com.ctv.welcome.util.FileUtil;
import com.ctv.welcome.vo.CategoryContentData;
import com.ctv.welcome.vo.PicData;
import com.ctv.welcome.vo.PicDataDao;
import com.ctv.welcome.vo.PicDataDao.Properties;
import com.ctv.welcome.vo.RichEditorInfo;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.greenrobot.greendao.query.WhereCondition;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ViewHolder> {
    private static final String TAG = "ContentAdapter";

    private final int ADD = 2;

    private final int CUSTOM = 1;

    private final int NORMAL = 0;

    private TextView cancel;

    private List<CategoryContentData> dataList;

    public ImageView image;

    private String industryName;

    private final boolean isAddTheme;

    boolean isDeleteVisible = false;

    private LayoutInflater layoutInflater;

    private Context mContext;

    public PopupWindow mPopupWindow;

    private ViewGroup parent;

    public int position;

    class RefreshContent implements Runnable {
        private boolean isSuccess;

        public RefreshContent(boolean isSuccess) {
            this.isSuccess = isSuccess;
        }

        public void run() {
            if (this.isSuccess) {
                ContentAdapter.this.notifyDataSetChanged();
            } else {
                Toast.makeText(ContentAdapter.this.mContext, R.string.add_module_failed, Toast.LENGTH_SHORT).show();
            }
        }
    }

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
                    break;
                case 1:
                    float upX = motionEvent.getX();
                    if (this.moveX - this.downX > 0.0f
                            && Math.abs(this.moveX - this.downX) > 200.0f
                            && Math.abs(upX - this.downX) > 100.0f) {
                        if (ContentAdapter.this.dataList.size() > 0 && this.pos >= 0) {
                            if (this.pos != 0) {
                                this.pos--;
                                Glide.with(ContentAdapter.this.mContext)
                                        .load(((CategoryContentData) ContentAdapter.this.dataList
                                                .get(this.pos)).getIcon()).into(this.imageView);
                                break;
                            }
                            Glide.with(ContentAdapter.this.mContext)
                                    .load(((CategoryContentData) ContentAdapter.this.dataList
                                            .get(this.pos)).getIcon()).into(this.imageView);
                            break;
                        }
                    } else if (this.moveX - this.downX >= 0.0f
                            || Math.abs(this.moveX - this.downX) <= 200.0f
                            || Math.abs(upX - this.downX) <= 100.0f) {
                        if ((Math.abs(this.moveY - this.downY) >= 50.0f && Math.abs(this.moveX
                                - this.downX) >= 50.0f)
                                || this.pos < 0) {
                            lauchAcitivty();
                            break;
                        }
                        lauchAcitivty();
                        break;
                    } else {
                        if (this.pos < ContentAdapter.this.dataList.size() - 1 && this.pos >= 0) {
                            this.pos++;
                            Glide.with(ContentAdapter.this.mContext)
                                    .load(((CategoryContentData) ContentAdapter.this.dataList
                                            .get(this.pos)).getIcon()).into(this.imageView);
                        }
                        if (this.pos == ContentAdapter.this.dataList.size() - 1) {
                            Toast.makeText(ContentAdapter.this.mContext, R.string.the_end, Toast.LENGTH_SHORT)
                                    .show();
                            break;
                        }
                    }
                    break;
                case 2:
                    this.moveX = motionEvent.getX();
                    this.moveY = motionEvent.getY();
                    break;
            }
            return true;
        }

        private void lauchAcitivty() {
            if (ContentAdapter.this.dataList.size() >= 50) {
                Toast.makeText(ContentAdapter.this.mContext, R.string.custom_module_full_hint, Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            CategoryContentData data = (CategoryContentData) ContentAdapter.this.dataList
                    .get(this.pos);
            Intent intent = new Intent(ContentAdapter.this.mContext, EditActivity.class);
            Object icon = data.getImageRes();
            if (icon instanceof Integer) {
                intent.putExtra("IMAGE_PATH_ICON", ((Integer) icon).intValue());
            } else if (icon instanceof String) {
                intent.putExtra("IMAGE_PATH", (String)icon);
            }
            intent.putExtra("SHOW_QROCDE", true);
            if (!(TextUtils.isEmpty(data.getMainTitleHtml())
                    && TextUtils.isEmpty(data.getSubTitleHtml()) && TextUtils.isEmpty(data
                    .getSubTitle2Html()))) {
                ArrayList<RichEditorInfo> infoList = new ArrayList();
                if (!TextUtils.isEmpty(data.getMainTitleHtml())) {
                    infoList.add(new RichEditorInfo(data.getMainTitleX(), data.getMainTitleY(),
                            data.getMainTitleHtml()));
                }
                if (!TextUtils.isEmpty(data.getSubTitleHtml())) {
                    infoList.add(new RichEditorInfo(data.getSubTitleX(), data.getSubTitleY(), data
                            .getSubTitleHtml()));
                }
                if (!TextUtils.isEmpty(data.getSubTitle2Html())) {
                    infoList.add(new RichEditorInfo(data.getSubTitle2X(), data.getSubTitle2Y(),
                            data.getSubTitle2Html()));
                }
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("RICHEDITOR_LIST", infoList);
                intent.putExtras(bundle);
            }
            intent.putExtra("INDUSTRY_NAME", ContentAdapter.this.industryName);
            intent.putExtra("isAddTheme", ContentAdapter.this.isAddTheme);
            intent.putExtra("isAddModule", data.isAddModule());
            ContentAdapter.this.mContext.startActivity(intent);
            if (ContentAdapter.this.mPopupWindow.isShowing()) {
                ContentAdapter.this.mPopupWindow.dismiss();
            }
        }
    }

    public ContentAdapter(List<CategoryContentData> dataList, Context context, String industryName,
            boolean isAddTheme) {
        this.dataList = dataList;
        this.mContext = context;
        this.industryName = industryName;
        this.isAddTheme = isAddTheme;
    }

    public void setDeleteVisible(boolean visible) {
        this.isDeleteVisible = visible;
        notifyDataSetChanged();
    }

    public void addCategoryContent(String file_path, String industryName, boolean isAddTheme) {
        String copyPath;
        CategoryContentData data = new CategoryContentData();
        int startIndex = file_path.lastIndexOf("/");
        String name = file_path.substring(startIndex + 1, file_path.lastIndexOf("."));
        final String srcFileName = new File(file_path).getName();
        if (isAddTheme) {
            copyPath = Config.CUSTOM_CATEGORY_CONTENT_PATH + Config.CUSTOME + "/" + industryName
                    + "/" + srcFileName;
        } else {
            copyPath = Config.CUSTOM_CATEGORY_CONTENT_PATH + industryName + "/" + srcFileName;
        }
        data.setName(name);
        data.setText(name);
        data.setIcon(copyPath);
        data.setImageRes(copyPath);
        data.setAddModule(true);
        this.dataList.add(data);
        final boolean z = isAddTheme;
        final String str = file_path;
        final String str2 = industryName;
        ThreadManager.getThreadPoolProxy().excute(new Runnable() {
            public void run() {
                boolean isSuccess;
                if (z) {
                    isSuccess = FileUtil.copyPic(str, Config.CUSTOM_CATEGORY_CONTENT_PATH
                            + Config.CUSTOME + "/" + str2, srcFileName);
                } else {
                    isSuccess = FileUtil.copyPic(str, Config.CUSTOM_CATEGORY_CONTENT_PATH + str2,
                            srcFileName);
                }
                ((Activity) ContentAdapter.this.mContext).runOnUiThread(new RefreshContent(
                        isSuccess));
            }
        });
    }

    private int getFixPosition() {
        if (this.industryName.equals(this.mContext.getString(R.string.welcome_module_zh))) {
            return 15;
        }
        if (this.industryName.equals(this.mContext.getString(R.string.company_zh))) {
            return 6;
        }
        if (this.industryName.equals(this.mContext.getString(R.string.goverment_zh))
                || this.industryName.equals(this.mContext.getString(R.string.medical_zh))) {
            return 2;
        }
        if (this.industryName.equals(this.mContext.getString(R.string.education_zh))) {
            return 3;
        }
        if (this.industryName.equals(this.mContext.getString(R.string.advertising_zh))) {
            return 3;
        }
        if (this.industryName.equals(this.mContext.getString(R.string.traffic_zh))
                || this.industryName.equals(this.mContext.getString(R.string.car_zh))
                || this.industryName.equals(this.mContext.getString(R.string.manufacture_zh))
                || this.industryName.equals(this.mContext.getString(R.string.clothing_zh))
                || this.industryName.equals(this.mContext.getString(R.string.building_zh))
                || this.industryName.equals(this.mContext.getString(R.string.tobacco_zh))) {
            return 2;
        }
        return 0;
    }

    public int getItemViewType(int position) {
        if (position == this.dataList.size()) {
            return 2;
        }
        if (position < getFixPosition()) {
            return 0;
        }
        return 1;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        if (this.layoutInflater == null) {
            this.layoutInflater = LayoutInflater.from(parent.getContext());
        }
        View view = null;
        switch (viewType) {
            case 0:
            case 2:
                if (!IndexActivity.mIsWindowMode) {
                    view = this.layoutInflater.inflate(R.layout.adp_content_item, parent, false);
                    break;
                }
                view = this.layoutInflater.inflate(R.layout.adp_content_item_window, parent, false);
                break;
            case 1:
                if (!IndexActivity.mIsWindowMode) {
                    view = this.layoutInflater.inflate(R.layout.adp_content_custom_item, parent,
                            false);
                    break;
                }
                view = this.layoutInflater.inflate(R.layout.adp_content_custom_item_window, parent,
                        false);
                break;
        }
        return new ViewHolder(view);
    }

    public void onBindViewHolder(final ViewHolder holder, final int position) {
        int viewType = getItemViewType(position);
        Log.d(TAG, "onBindViewHolder,viewType:" + viewType);
        switch (viewType) {
            case 0:
            case 1:
                final CategoryContentData data = (CategoryContentData) this.dataList.get(position);
                ((TextView) holder.getView(R.id.adp_content_name_tv)).setText(data.getName());
                Object icon = data.getIcon();
                if (icon instanceof Integer) {
                    Glide.with(this.mContext).load(Integer.valueOf(((Integer) icon).intValue()))
                            .into((ImageView) holder.getView(R.id.adp_content_icon_iv));
                } else if (icon instanceof String) {
                    Glide.with(this.mContext).load(icon)
                            .into((ImageView) holder.getView(R.id.adp_content_icon_iv));
                }
                holder.itemView.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        ContentAdapter.this.showThemePreview(position, data.getIcon());
                    }
                });
                break;
            case 2:
                ((TextView) holder.getView(R.id.adp_content_name_tv)).setText(this.mContext
                        .getString(R.string.add_module));
                Glide.with(this.mContext).load(Integer.valueOf(R.drawable.add_category_module))
                        .into((ImageView) holder.getView(R.id.adp_content_icon_iv));
                holder.itemView.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        if (ContentAdapter.this.dataList.size() >= 50) {
                            Toast.makeText(ContentAdapter.this.mContext, R.string.module_full_hint,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            ContentAdapter.this.launchAddModuleActivity();
                        }
                    }
                });
                return;
        }
        switch (viewType) {
            case 1:
                holder.getView(R.id.adp_content_delete_iv).setOnClickListener(
                        new OnClickListener() {
                            public void onClick(View view) {
                                ContentAdapter.this.showDeleteDialog(holder, position,
                                        (ImageView) view);
                            }
                        });
                return;
            default:
                return;
        }
    }

    private void showDeleteDialog(ViewHolder holder, final int position, ImageView deleteView) {
        View view = LayoutInflater.from(this.mContext).inflate(R.layout.dialog_confim_delete_hint,
                null);
        final AlertDialog dialog = new Builder(this.mContext, R.style.myDialog).create();
        TextView txtCancel = (TextView) view.findViewById(R.id.txt_delete_cancel);
        ((TextView) view.findViewById(R.id.txt_delete_confirm))
                .setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        dialog.dismiss();
                        ContentAdapter.this.deleteItem(position);
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

    private void showDeletePopup(ViewHolder holder, final int position, ImageView delete) {
        if (this.isDeleteVisible) {
            delete.setVisibility(View.VISIBLE);
            holder.itemView.setOnClickListener(null);
            return;
        }
        delete.setVisibility(View.GONE);
        holder.itemView.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                ContentAdapter.this.showThemePreview(position,
                        ((CategoryContentData) ContentAdapter.this.dataList.get(position))
                                .getIcon());
            }
        });
    }

    private void showDeleteLongClick(ViewHolder holder, int position) {
        holder.itemView.setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View view) {
                ContentAdapter.this.isDeleteVisible = true;
                ContentAdapter.this.notifyDataSetChanged();
                if (ContentAdapter.this.cancel != null
                        && ContentAdapter.this.cancel.getVisibility() == View.GONE) {
                    ContentAdapter.this.cancel.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });
    }

    private void deleteItem(final int position) {
        if (this.dataList.size() > 0 && position < this.dataList.size()) {
            ThreadManager.getThreadPoolProxy().excute(new Runnable() {
                public void run() {
                    final CategoryContentData contentData = (CategoryContentData) ContentAdapter.this.dataList
                            .get(position);
                    String file = contentData.getName();
                    Log.d(ContentAdapter.TAG, "deleteItem file path:" + contentData.getIcon());
                    final List<PicData> picDataList = DBUtil.mDaoSession.getPicDataDao()
                            .queryBuilder()
                            .where(Properties.FileName.like(file), new WhereCondition[0]).list();
                    ((Activity) ContentAdapter.this.mContext).runOnUiThread(new Runnable() {
                        public void run() {
                            ContentAdapter.this.dataList.remove(position);
                            ContentAdapter.this.notifyDataSetChanged();
                            Object filePathobj = contentData.getIcon();
                            if (filePathobj instanceof String) {
                                new File((String)filePathobj).delete();
                            }
                            if (picDataList != null && picDataList.size() > 0) {
                                ThreadManager.getThreadPoolProxy().excute(new Runnable() {
                                    public void run() {
                                        PicDataDao daoSession = DBUtil.mDaoSession.getPicDataDao();
                                        String fileName = ((PicData) picDataList.get(0))
                                                .getFileName();
                                        String backUpFileName = ((PicData) picDataList.get(0))
                                                .getFilePath();
                                        if (!TextUtils.isEmpty(fileName)) {
                                            for (PicData data : picDataList) {
                                                daoSession.deleteByKey(data.getId());
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            });
        }
    }

    private void launchAddModuleActivity() {
        Intent intent = new Intent(this.mContext, CustomActivity.class);
        intent.putExtra("add_module", true);
        intent.putExtra("industry_name", this.industryName);
        intent.putExtra("isAddTheme", this.isAddTheme);
        ((IndexActivity) this.mContext).startActivityForResult(intent, 1);
    }

    private void showThemePreview(int position, Object icon) {
        View prewview = this.layoutInflater.inflate(R.layout.layout_preview_theme, null);
        this.image = (ImageView) prewview.findViewById(R.id.lt_preview_iv);
        if (icon instanceof Integer) {
            Glide.with(this.mContext).load(Integer.valueOf(((Integer) icon).intValue()))
                    .into(this.image);
        } else if (icon instanceof String) {
            Glide.with(this.mContext).load((String) icon).into(this.image);
        }
        this.mPopupWindow = new PopupWindow(prewview, FileUtil.dip2px(this.mContext, 480.0f), -2,
                true);
        this.mPopupWindow.setFocusable(true);
        this.mPopupWindow.setOutsideTouchable(true);
        this.mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        this.mPopupWindow.showAtLocation(this.parent, 17, 0, 0);
        prewview.setOnTouchListener(new TouchLister(position, this.image));
    }

    public int getItemCount() {
        return this.dataList.size() + 1;
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
}
