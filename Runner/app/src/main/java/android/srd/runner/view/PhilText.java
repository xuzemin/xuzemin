package android.srd.runner.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import java.io.File;

/**
 * Created by Phil on 2017/9/5.
 */

@SuppressLint("AppCompatCustomView")
public class PhilText extends TextView{

    public PhilText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        String file = "fonts" + File.separator + "digital-7.ttf";
        AssetManager assets = context.getAssets();
        Typeface font = Typeface.createFromAsset(assets, file);

        setTypeface(font);
    }
}