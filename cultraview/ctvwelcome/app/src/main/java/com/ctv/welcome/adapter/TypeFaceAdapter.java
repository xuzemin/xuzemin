
package com.ctv.welcome.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ctv.welcome.R;
import com.ctv.welcome.activity.EditActivity;
import com.ctv.welcome.util.LogUtils;
import com.ctv.welcome.view.RichEditor;
import com.ctv.welcome.vo.RichEditorInfo;
import java.util.List;

public class TypeFaceAdapter extends RecyclerView.Adapter<TypeFaceAdapter.ViewHolder> {
    private Context context;

    private final List<String> datas;

    public View itemView;

    private Editor mEditor;

    private SharedPreferences mPref;

    private RecyclerView mRecyclerView;

    private RichEditor mRichEditor;

    private RichEditorInfo mRichEditorInfo;

    private RichEditorInfo mTextRichEditorInfo;

    private ImageView mTextSize;

    private ImageView mTypeFace;

    private int savePosistion;

    boolean scorll = false;

    public int selectItem = -1;

    int selectedPosition = -1;

    String[] strs = new String[] {
            "arial", "time_new_roman", "georgia", "song_ti", "hei_ti", "fang_song", "kai_ti"
    };

    private RichEditor textRichEditor;

    String[] textSizeStr = new String[] {
            "40", "60", "80", "100", "120", "140", "160", "180"
    };

    private boolean type;

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView typeFace;

        ViewHolder(View v) {
            super(v);
            this.typeFace = (TextView) v.findViewById(R.id.typeface_iv);
        }
    }

    public TypeFaceAdapter(List<String> datas, Context context, boolean type,
            RecyclerView mRecyclerView) {
        this.datas = datas;
        this.context = context;
        this.mPref = context.getSharedPreferences("position", 0);
        this.type = type;
        this.mRecyclerView = mRecyclerView;
        this.mEditor = this.mPref.edit();
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(this.context).inflate(R.layout.item_typeface,
                parent, false));
    }

    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.typeFace.setText((CharSequence) this.datas.get(position));
        if (this.type) {
            this.savePosistion = this.mPref.getInt("typeFace_position", -1);
        } else {
            this.savePosistion = this.mPref.getInt("typeSize_position", -1);
        }
        if (!this.scorll) {
            this.mRecyclerView.scrollToPosition(this.savePosistion);
            this.scorll = true;
        }
        if (this.savePosistion != -1) {
            setSelectItem(this.savePosistion);
        }
        if (position == this.selectItem) {
            holder.typeFace.setTextColor(this.context.getResources().getColor(
                    R.color.font_select_blue));
        } else {
            holder.typeFace.setTextColor(-1);
        }
        holder.itemView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                TypeFaceAdapter.this.setSelectItem(position);
                TypeFaceAdapter.this.notifyDataSetChanged();
                EditActivity context;
                if (TypeFaceAdapter.this.type) {
                    context = (EditActivity) TypeFaceAdapter.this.context;
                    TypeFaceAdapter.this.mRichEditor
                            .setFontName(TypeFaceAdapter.this.strs[position]);
                    TypeFaceAdapter.this.mTypeFace.setImageResource(R.drawable.typeface_normal);
                    context.isCheckedTP = false;
                    TypeFaceAdapter.this.mRichEditorInfo
                            .setCurFontType(TypeFaceAdapter.this.strs[position]);
                    context.isFontTypeChange = true;
                    LogUtils.i("字体样式 click,typeFace:" + TypeFaceAdapter.this.strs[position]);
                    TypeFaceAdapter.this.mEditor.putInt("typeFace_position", position).commit();
                } else {
                    context = (EditActivity) TypeFaceAdapter.this.context;
                    context.fontsize = position + 1;
                    context.px = TypeFaceAdapter.this.textSizeStr[position];
                    LogUtils.i("字体大小 click,fontSize:" + context.fontsize + ",px:" + context.px);
                    TypeFaceAdapter.this.textRichEditor
                            .setBaseFontSize(TypeFaceAdapter.this.textSizeStr[position] + "px");
                    TypeFaceAdapter.this.mTextRichEditorInfo
                            .setCurFontSize(TypeFaceAdapter.this.textSizeStr[position] + "px");
                    context.isFontSizeChange = true;
                    context.isCheckedTS = false;
                    TypeFaceAdapter.this.mTextSize.setImageResource(R.drawable.textsize_normal);
                    TypeFaceAdapter.this.mEditor.putInt("typeSize_position", position).commit();
                }
                TypeFaceAdapter.this.mRecyclerView.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void clearSharedPreferencesPosition() {
        this.mEditor.clear();
    }

    public void handleTypeFaceClick(RichEditor mRichEditor, ImageView mTypeFace,
            RichEditorInfo mRichEditorInfo) {
        this.mRichEditor = mRichEditor;
        this.mTypeFace = mTypeFace;
        this.mRichEditorInfo = mRichEditorInfo;
    }

    public void handleTextSizeClick(RichEditor textRichEditor, ImageView mTextSize,
            RichEditorInfo mTextRichEditorInfo) {
        this.textRichEditor = textRichEditor;
        this.mTextSize = mTextSize;
        this.mTextRichEditorInfo = mTextRichEditorInfo;
    }

    private void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    public int getItemCount() {
        return this.datas.size();
    }
}
