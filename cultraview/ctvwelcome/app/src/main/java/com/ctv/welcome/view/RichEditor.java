
package com.ctv.welcome.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;

import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import androidx.core.view.ViewCompat;

import com.bumptech.glide.load.Key;
import com.ctv.welcome.util.LogUtils;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RichEditor extends WebView {
    private static final String CALLBACK_SCHEME = "re-callback://";

    private static final String SETUP_HTML = "file:///android_asset/editor.html";

    private static final String STATE_SCHEME = "re-state://";

    public boolean change;

    private boolean isReady;

    private String mContents;

    private OnDecorationStateListener mDecorationStateListener;

    private AfterInitialLoadListener mLoadListener;

    private OnTextChangeListener mTextChangeListener;

    private OnLocationListener onLocationListener;

    private int webviewContentWidth;

    float x1;

    float x2;

    float y1;

    float y2;

    public interface OnLocationListener {
        void onLocation(float f, float f2);
    }

    public interface OnTextChangeListener {
        void onTextChange(String str);
    }

    public interface AfterInitialLoadListener {
        void onAfterInitialLoad(boolean z);
    }

    protected class EditorWebViewClient extends WebViewClient {
        protected EditorWebViewClient() {
        }

        public void onPageFinished(WebView view, String url) {
            RichEditor.this.isReady = url.equalsIgnoreCase(RichEditor.SETUP_HTML);
            if (RichEditor.this.mLoadListener != null) {
                RichEditor.this.mLoadListener.onAfterInitialLoad(RichEditor.this.isReady);
            }
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            try {
                String decode = URLDecoder.decode(url, Key.STRING_CHARSET_NAME);
                if (TextUtils.indexOf(url, RichEditor.CALLBACK_SCHEME) == 0) {
                    RichEditor.this.callback(decode);
                    return true;
                } else if (TextUtils.indexOf(url, RichEditor.STATE_SCHEME) != 0) {
                    return super.shouldOverrideUrlLoading(view, url);
                } else {
                    RichEditor.this.stateCheck(decode);
                    return true;
                }
            } catch (UnsupportedEncodingException e) {
                return false;
            }
        }
    }

    class JavaScriptInterface {
        JavaScriptInterface() {
        }

        public void getContentWidth(String value) {
            if (value != null) {
                RichEditor.this.webviewContentWidth = Integer.parseInt(value);
                LogUtils.i("ContentWidth of webpage is: " + RichEditor.this.webviewContentWidth
                        + "px");
            }
        }
    }

    public interface OnDecorationStateListener {
        void onStateChangeListener(String str, List<Type> list);
    }

    public enum Type {
        BOLD, ITALIC, SUBSCRIPT, SUPERSCRIPT, STRIKETHROUGH, UNDERLINE, H1, H2, H3, H4, H5, H6, ORDEREDLIST, UNORDEREDLIST, JUSTIFYCENTER, JUSTIFYFULL, JUSTUFYLEFT, JUSTIFYRIGHT
    }

    public RichEditor(Context context) {
        this(context, null);
    }

    public RichEditor(Context context, AttributeSet attrs) {
        this(context, attrs, 16842885);
    }

    @SuppressLint({
        "SetJavaScriptEnabled"
    })
    public RichEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.isReady = false;
        this.change = false;
        this.webviewContentWidth = 0;
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        getSettings().setJavaScriptEnabled(true);
        setWebChromeClient(new WebChromeClient());
        setWebViewClient(createWebviewClient());
        loadUrl(SETUP_HTML);
        applyAttributes(context, attrs);
        setEditorEmpty();
    }

    public void setEditorEmpty() {
        exec("javascript:RE.clear();");
    }

    public void breakWord() {
        exec("javascript:RE.br();");
    }

    public int getEditorWidth() {
        return 0;
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case 0:
                this.x1 = event.getX();
                this.y1 = event.getY();
                if (this.onLocationListener != null) {
                    this.onLocationListener.onLocation(event.getRawX() - event.getX(),
                            event.getRawY() - event.getY());
                    break;
                }
                break;
            case 1:
                break;
            case 2:
                this.x2 = event.getX();
                this.y2 = event.getY();
                int offsetY = (int) (this.y2 - this.y1);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
                params.leftMargin = getLeft() + ((int) (this.x2 - this.x1));
                params.topMargin = getTop() + offsetY;
                setLayoutParams(params);
                break;
        }
        if (this.onLocationListener != null) {
            this.onLocationListener.onLocation(event.getRawX() - event.getX(), event.getRawY()
                    - event.getY());
        }
        return super.dispatchTouchEvent(event);
    }

    public void setOnLocationListener(OnLocationListener onLocationListener) {
        this.onLocationListener = onLocationListener;
    }

    public void setEditorEnable(boolean value) {
        exec("javascript:RE.editable('" + value + "');");
    }

    public void setEditorModify(String value) {
        exec("javascript:RE.write('" + value + "');");
    }

    public void setBorder(String value) {
        exec("javascript:RE.border('" + value + "');");
    }

    protected EditorWebViewClient createWebviewClient() {
        return new EditorWebViewClient();
    }

    public void setOnTextChangeListener(OnTextChangeListener listener) {
        this.mTextChangeListener = listener;
    }

    public void setOnDecorationChangeListener(OnDecorationStateListener listener) {
        this.mDecorationStateListener = listener;
    }

    public void setOnInitialLoadListener(AfterInitialLoadListener listener) {
        this.mLoadListener = listener;
    }

    private void callback(String text) {
        this.mContents = text.replaceFirst(CALLBACK_SCHEME, "");
        if (this.mTextChangeListener != null) {
            this.mTextChangeListener.onTextChange(this.mContents);
        }
    }

    private void stateCheck(String text) {
        String state = text.replaceFirst(STATE_SCHEME, "").toUpperCase(Locale.ENGLISH);
        List<Type> types = new ArrayList();
        for (Type type : Type.values()) {
            if (TextUtils.indexOf(state, type.name()) != -1) {
                types.add(type);
            }
        }
        if (this.mDecorationStateListener != null) {
            this.mDecorationStateListener.onStateChangeListener(state, types);
        }
    }

    private void applyAttributes(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, new int[] {
            16842927
        });
        switch (ta.getInt(0, -1)) {
            case 1:
                exec("javascript:RE.setTextAlign(\"center\")");
                break;
            case 3:
                exec("javascript:RE.setTextAlign(\"left\")");
                break;
            case 5:
                exec("javascript:RE.setTextAlign(\"right\")");
                break;
            case 16:
                exec("javascript:RE.setVerticalAlign(\"middle\")");
                break;
            case 17:
                exec("javascript:RE.setVerticalAlign(\"middle\")");
                exec("javascript:RE.setTextAlign(\"center\")");
                break;
            case 48:
                exec("javascript:RE.setVerticalAlign(\"top\")");
                break;
            case 80:
                exec("javascript:RE.setVerticalAlign(\"bottom\")");
                break;
        }
        ta.recycle();
    }

    public String getHtml() {
        return this.mContents;
    }

    public void setHtml(String contents) {
        if (contents == null) {
            contents = "";
        }
        try {
            exec("javascript:RE.setHtml('" + URLEncoder.encode(contents, Key.STRING_CHARSET_NAME)
                    + "');");
        } catch (UnsupportedEncodingException e) {
        }
        this.mContents = contents;
    }

    public void setText(String text) {
        exec("javascript:RE.setText('" + text + "');");
    }

    public void setFontName(String fontName) {
        exec("javascript:RE.setFontName('" + fontName + "');");
    }

    public void setBaseFontName(String fontName) {
        exec("javascript:RE.setBaseFontName('" + fontName + "');");
    }

    public void setBaseFontSize(String px) {
        LogUtils.d("RichEditor", "setBaseFontSize,px:" + px);
        exec("javascript:RE.setBaseFontSize('" + px + "');");
    }

    public void setEditorFontSize(double px) {
        exec("javascript:RE.setBaseFontSize('" + px + "px');");
    }

    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        exec("javascript:RE.setPadding('" + left + "px', '" + top + "px', '" + right + "px', '"
                + bottom + "px');");
    }

    public void setPaddingRelative(int start, int top, int end, int bottom) {
        setPadding(start, top, end, bottom);
    }

    public void setEditorBackgroundColor(int color) {
        setBackgroundColor(color);
    }

    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
    }

    public void setBackgroundResource(int resid) {
        Bitmap bitmap = Utils.decodeResource(getContext(), resid);
        String base64 = Utils.toBase64(bitmap);
        bitmap.recycle();
        exec("javascript:RE.setBackgroundImage('url(data:image/png;base64," + base64 + ")');");
    }

    public void setBackground(Drawable background) {
        Bitmap bitmap = Utils.toBitmap(background);
        String base64 = Utils.toBase64(bitmap);
        bitmap.recycle();
        exec("javascript:RE.setBackgroundImage('url(data:image/png;base64," + base64 + ")');");
    }

    public void setBackground(String url) {
        exec("javascript:RE.setBackgroundImage('url(" + url + ")');");
    }

    public void setEditorWidth(int px) {
        exec("javascript:RE.setWidth('" + px + "px');");
    }

    public void setEditorHeight(int px) {
        exec("javascript:RE.setHeight('" + px + "px');");
    }

    public void setPlaceholder(String placeholder) {
        exec("javascript:RE.setPlaceholder('" + placeholder + "');");
    }

    public void loadCSS(String cssFile) {
        exec("javascript:"
                + ("(function() {    var head  = document.getElementsByTagName(\"head\")[0];    var link  = document.createElement(\"link\");    link.rel  = \"stylesheet\";    link.type = \"text/css\";    link.href = \""
                        + cssFile
                        + "\";"
                        + "    link.media = \"all\";"
                        + "    head.appendChild(link);" + "}) ();") + "");
    }

    public void undo() {
        exec("javascript:RE.undo();");
    }

    public void redo() {
        exec("javascript:RE.redo();");
    }

    public void setBold() {
        exec("javascript:RE.setBold();");
    }

    public void setItalic() {
        exec("javascript:RE.setItalic();");
    }

    public void setSubscript() {
        exec("javascript:RE.setSubscript();");
    }

    public void setSuperscript() {
        exec("javascript:RE.setSuperscript();");
    }

    public void setStrikeThrough() {
        exec("javascript:RE.setStrikeThrough();");
    }

    public void setUnderline() {
        exec("javascript:RE.setUnderline();");
    }

    public void setEditorFontColor(int color) {
        exec("javascript:RE.setBaseTextColor('" + convertHexColorString(color) + "');");
    }

    public void setBaseTextColor(int color) {
        String hex = convertHexColorString(color);
        LogUtils.d("RichEditor", "setBaseTextColor,color:" + hex);
        exec("javascript:RE.setBaseTextColor('" + hex + "');");
    }

    public void setTextColor(int color) {
        exec("javascript:RE.prepareInsert();");
        exec("javascript:RE.setTextColor('" + convertHexColorString(color) + "');");
    }

    public void setTextBackgroundColor(int color) {
        exec("javascript:RE.prepareInsert();");
        exec("javascript:RE.setTextBackgroundColor('" + convertHexColorString(color) + "');");
    }

    public void setFontSize(int fontSize, Float px) {
        if (fontSize > 7) {
            fontSize = 7;
        }
        exec("javascript:RE.setFontSize('" + fontSize + "','" + px + "px');");
    }

    public void setFontSize2(int fontSize) {
        if (fontSize > 7 || fontSize < 1) {
            Log.e("RichEditor", "Font size should have a value between 1-7");
        }
        LogUtils.d("RichEditor", "setFontSize2,fontSize:" + fontSize);
        exec("javascript:RE.setFontSize('" + fontSize + "');");
    }

    public void setEditorFontSize(String fontSize) {
        exec("javascript:RE.setEditFontSize('" + fontSize + "px');");
    }

    public void removeFormat() {
        exec("javascript:RE.removeFormat();");
    }

    public void setHeading(int heading) {
        exec("javascript:RE.setHeading('" + heading + "');");
    }

    public void setIndent() {
        exec("javascript:RE.setIndent();");
    }

    public void setOutdent() {
        exec("javascript:RE.setOutdent();");
    }

    public void setAlignLeft() {
        exec("javascript:RE.setJustifyLeft();");
    }

    public void setAlignCenter() {
        exec("javascript:RE.setJustifyCenter();");
    }

    public void setAlignRight() {
        exec("javascript:RE.setJustifyRight();");
    }

    public void setBlockquote() {
        exec("javascript:RE.setBlockquote();");
    }

    public void setBullets() {
        exec("javascript:RE.setBullets();");
    }

    public void setNumbers() {
        exec("javascript:RE.setNumbers();");
    }

    public void enableObjectResizing(boolean enable) {
        exec("javascript:RE.enableObjectResizing('" + enable + "');");
    }

    public void insertImage(String url, String alt, boolean resizing) {
        exec("javascript:RE.prepareInsert();");
        exec("javascript:RE.insertImage('" + url + "', '" + alt + "','" + resizing + "');");
    }

    public void insertLink(String href, String title) {
        exec("javascript:RE.prepareInsert();");
        exec("javascript:RE.insertLink('" + href + "', '" + title + "');");
    }

    public void insertTodo() {
        exec("javascript:RE.prepareInsert();");
        exec("javascript:RE.setTodo('" + Utils.getCurrentTime() + "');");
    }

    public void focusEditor() {
        requestFocus();
        exec("javascript:RE.focus();");
    }

    public void clearFocusEditor() {
        exec("javascript:RE.blurFocus();");
    }

    private String convertHexColorString(int color) {
        return String.format("#%06X", new Object[] {
            Integer.valueOf(ViewCompat.MEASURED_SIZE_MASK & color)
        });
    }

    protected void exec(final String trigger) {
        if (this.isReady) {
            load(trigger);
        } else {
            postDelayed(new Runnable() {
                public void run() {
                    RichEditor.this.exec(trigger);
                }
            }, 100);
        }
    }

    private void load(String trigger) {
        if (VERSION.SDK_INT >= 19) {
            evaluateJavascript(trigger, null);
        } else {
            loadUrl(trigger);
        }
    }
}
