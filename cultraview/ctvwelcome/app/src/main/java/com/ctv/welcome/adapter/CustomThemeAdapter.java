
package com.ctv.welcome.adapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ctv.welcome.R;
import com.ctv.welcome.constant.Config;
import com.ctv.welcome.dialog.AddThemeDialog;
import com.ctv.welcome.dialog.AddThemeDialog.DialogCallBackListener;
import com.ctv.welcome.task.ThreadManager;
import com.ctv.welcome.util.DBUtil;
import com.ctv.welcome.util.FileUtil;
import com.ctv.welcome.util.FileUtils;
import com.ctv.welcome.vo.PicData;
import com.ctv.welcome.vo.PicDataDao;
import com.ctv.welcome.vo.PicDataDao.Properties;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.greenrobot.greendao.query.WhereCondition;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
public class CustomThemeAdapter extends Adapter<CustomThemeAdapter.ViewHolder> {
    private static final String TAG = "CustomThemeAdapter";

    final int NORMAL_ITEM = 0;

    final int SPEICAL_ITEM = 1;

    private Activity activity;

    private Context context;

    public List<String> datas;

    private String filePath;

    private RelativeLayout inflate;

    private ArrayList<View> itemList = new ArrayList();

    private LayoutInflater layoutInflater;

    private onItemClickListener listener;

    private ImageView mDelete;

    private List<PicData> picDataList;

    private RelativeLayout prewview;

    private boolean showDelete = false;

    public interface onItemClickListener {
        void onItemClick(View view, int i);
    }

    public CustomThemeAdapter(Activity activity, List<String> datas) {
        this.datas = datas;
        this.activity = activity;
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        this.listener = listener;
    }

    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        }
        return 1;
    }

    public void setDatas(List<String> dataList) {
        this.datas = dataList;
        notifyDataSetChanged();
    }

    public List<String> getDatas() {
        return this.datas;
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
                view = this.layoutInflater.inflate(R.layout.adp_custom_theme_item, parent, false);
                break;
        }
        return new ViewHolder(view);
    }

    public void onBindViewHolder(ViewHolder holder, final int position) {
        String data = (String) this.datas.get(position);
        ((TextView) holder.getView(R.id.adp_category_name_tv)).setText(data);
        holder.itemView.setTag(data);
        switch (getItemViewType(position)) {
            case 0:
                holder.itemView.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        CustomThemeAdapter.this.showInputThemeDialog();
                    }
                });
                return;
            case 1:
                holder.itemView.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        CustomThemeAdapter.this.listener.onItemClick(view, position);
                    }
                });
                holder.getView(R.id.adp_category_delete_iv).setOnClickListener(
                        new OnClickListener() {
                            public void onClick(View view) {
                                CustomThemeAdapter.this.showDeleteDialog(position);
                            }
                        });
                return;
            default:
                return;
        }
    }

    private void showInputThemeDialog() {
        new AddThemeDialog(this.activity, R.style.myDialog, new DialogCallBackListener() {
            public void refreshParentActivityUI(final String themeName) {
                ThreadManager.getThreadPoolProxy().excute(new Runnable() {
                    public void run() {
                        File file = new File(Config.CUSTOM_THEME_MODULE_PATH + "/" + themeName);
                        if (!file.exists()) {
                            file.mkdirs();
                        }
                    }
                });
                CustomThemeAdapter.this.datas.add(themeName);
                CustomThemeAdapter.this.notifyItemInserted(CustomThemeAdapter.this.datas.size() - 1);
            }
        }).show();
    }

    private void showDeleteDialog(final int position) {
        View view = LayoutInflater.from(this.activity).inflate(R.layout.dialog_confim_delete_hint,
                null);
        final AlertDialog dialog = new Builder(this.activity, R.style.myDialog).create();
        TextView txtCancel = (TextView) view.findViewById(R.id.txt_delete_cancel);
        ((TextView) view.findViewById(R.id.txt_delete_confirm))
                .setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        dialog.dismiss();
                        CustomThemeAdapter.this.deleteItem(position);
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

    private void deleteItem(final int position) {
        if (this.datas.size() > 0 && position < this.datas.size()) {
            ThreadManager.getThreadPoolProxy().excute(new Runnable() {
                public void run() {
                    String categoryName = (String) CustomThemeAdapter.this.datas.get(position);
                    FileUtils.deleteDir(Config.CUSTOM_THEME_MODULE_PATH + "/" + categoryName);
                    String path = FileUtil.getBaseStorageDir() + "/" + Config.THEME_PICTURE + "/"
                            + Config.CUSTOME + "/" + categoryName;
                    FileUtils.deleteDir(path);
                    List<String> themePicList = FileUtils.getDirFiles(path);
                    for (int i = 0; i < themePicList.size(); i++) {
                        String picName = (String) themePicList.get(i);
                        Log.d(CustomThemeAdapter.TAG, "find theme custom pic name:" + picName);
                        CustomThemeAdapter.this.picDataList.addAll(DBUtil.mDaoSession
                                .getPicDataDao().queryBuilder()
                                .where(Properties.FileName.like(picName), new WhereCondition[0])
                                .list());
                    }
                    ((Activity) CustomThemeAdapter.this.context).runOnUiThread(new Runnable() {
                        public void run() {
                            CustomThemeAdapter.this.datas.remove(position);
                            CustomThemeAdapter.this.notifyDataSetChanged();
                            if (CustomThemeAdapter.this.picDataList != null
                                    && CustomThemeAdapter.this.picDataList.size() > 0) {
                                ThreadManager.getThreadPoolProxy().excute(new Runnable() {
                                    public void run() {
                                        String baseDir = FileUtil.getBaseStorageDir();
                                        PicDataDao daoSession = DBUtil.mDaoSession.getPicDataDao();
                                        String fileName = ((PicData) CustomThemeAdapter.this.picDataList
                                                .get(0)).getFileName();
                                        String backUpFileName = ((PicData) CustomThemeAdapter.this.picDataList
                                                .get(0)).getFilePath();
                                        if (!TextUtils.isEmpty(backUpFileName)) {
                                            new File(baseDir + "/" + Config.SDBACKUPPATH + "/"
                                                    + backUpFileName).delete();
                                        }
                                        if (!TextUtils.isEmpty(fileName)) {
                                            for (PicData data : CustomThemeAdapter.this.picDataList) {
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

    public int getItemCount() {
        return this.datas.size();
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
