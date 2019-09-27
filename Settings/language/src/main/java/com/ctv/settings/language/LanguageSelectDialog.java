
package com.ctv.settings.language;

import android.app.ActivityManagerNative;
import android.app.Dialog;
import android.app.IActivityManager;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.ctv.settings.adapter.LanguageDailogAdapter;
import com.ctv.settings.utils.ReflectUtil;
import com.cultraview.tv.CtvTvManager;
import com.mstar.android.tv.TvFactoryManager;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumLanguage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class LanguageSelectDialog extends Dialog {

    private static final String TAG = "LanguageSelectDialog";

    private final Context ctvContext;

    private ListView language_imput_lv;

    private int mSelect;

    private String[] mCountries;

    private String mLocaleCountry;

    private LanguageDailogAdapter languageDailogAdapter;

    private final ArrayList<Locale> mCurrentLocales = new ArrayList<Locale>();

    public LanguageSelectDialog(Context ctvContext) {
        super(ctvContext);
        this.ctvContext = ctvContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.language_dialog);
        setWindowStyle();
        findViews();
    }

    private void setWindowStyle() {
        setCanceledOnTouchOutside(true);

        Window w = getWindow();
        Resources res = ctvContext.getResources();
        Drawable drab = res.getDrawable(R.drawable.transparency_bg);
        w.setBackgroundDrawable(drab);
        WindowManager.LayoutParams lp = w.getAttributes();
        final float scale = res.getDisplayMetrics().density;
        // In the mid-point to calculate the offset x and y
        lp.y = (int) (-36 * scale + 0.5f);
        lp.width = (int) (680 * scale + 0.5f);
        lp.height = (int) (408 * scale + 0.5f);
        // Range is from 1.0 for completely opaque to 0.0 for no dim.
        w.setDimAmount(0.0f);
        w.setAttributes(lp);
    }

    /**
     * init compontent.
     */
    private void findViews() {
        Configuration conf = ctvContext.getResources().getConfiguration();
        mLocaleCountry = conf.locale.getCountry();
        Log.d(TAG, "localeCountry, " + mLocaleCountry);
        language_imput_lv = (ListView) findViewById(R.id.language_imput_lv);
        getLanguageList();
        Log.d(TAG, "mSelect, " + mSelect);
        languageDailogAdapter = new LanguageDailogAdapter(ctvContext, mCountries, mSelect);
        language_imput_lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        language_imput_lv.setAdapter(languageDailogAdapter);
        language_imput_lv.setItemChecked(mSelect, true);
        language_imput_lv.setSelection(mSelect);
        language_imput_lv.setOnItemClickListener(mListViewListener);
    }

    OnItemClickListener mListViewListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            dismiss();

            if (position == mSelect) {
                return;
            }

            mSelect = position;
            languageDailogAdapter.setSelect(position);
            languageDailogAdapter.notifyDataSetChanged();
            Locale locale = mCurrentLocales.get(position);
            setPositionLanguage(locale);
            SystemProperties.set("sys.lanuage.mode", "1");
        }
    };

    /**
     * @author sh
     * @Description:
     */
    private void setPositionLanguage(Locale locale) {
        final String keyboadr = Settings.Secure.getString(ctvContext.getContentResolver(),
                Settings.Secure.DEFAULT_INPUT_METHOD);
        try {
            IActivityManager am = ActivityManagerNative.getDefault();
            Configuration config = am.getConfiguration();

            config.locale = locale;
            setLanguage(locale);
            ReflectUtil.setFieldNoException(Configuration.class, config, "userSetLocale", true);
//            config.userSetLocale = true;
            am.updateConfiguration(config);
            BackupManager.dataChanged("com.android.providers.settings");
        } catch (Exception e) {
            dismiss();
            e.printStackTrace();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Settings.Secure.putString(ctvContext.getContentResolver(),
                        Settings.Secure.DEFAULT_INPUT_METHOD, keyboadr);

            }
        }).start();
    }

    private void getLanguageList() {
        // TODO Auto-generated method stub
        Set<String> allLanguage = new HashSet<String>();
        allLanguage.addAll(TvFactoryManager.getCultraviewgetlanguagelist(ctvContext));
        getCountryName(allLanguage.toArray(new String[allLanguage.size()]));
    }

    private void getCountryName(String[] languages) {
        mCountries = new String[languages.length];
        String language = null;
        String country = null;
        for (int i = 0; i < languages.length; i++) {
            language = languages[i].substring(0, 2);
            country = languages[i].substring(3, 5);
            final Locale locale = new Locale(language, country);
            if (country.equals(mLocaleCountry)) {
                mSelect = i;
            }
            mCurrentLocales.add(locale);
            mCountries[i] = locale.getDisplayLanguage();
            if (language.equals("zh")) {
                if (country.equals("TW")) {
                    mCountries[i] = ctvContext.getResources().getStringArray(
                            R.array.language_names_zh)[1];
                } else if (country.equals("CN")) {
                    mCountries[i] = ctvContext.getResources().getStringArray(
                            R.array.language_names_zh)[0];
                } else {
                    // Default
                    mCountries[i] = ctvContext.getResources().getStringArray(
                            R.array.language_names_zh)[0];
                }
            } else if (language.equals("ar")) {
                mCountries[i] = ctvContext.getResources().getStringArray(R.array.language_names_ar)[0];
            } else if (language.equals("de")) {
                mCountries[i] = ctvContext.getResources().getStringArray(R.array.language_names_de)[0];
            } else if (language.equals("en")) {
                mCountries[i] = ctvContext.getResources().getStringArray(R.array.language_names_en)[0];
            } else if (language.equals("es")) {
                mCountries[i] = ctvContext.getResources().getStringArray(R.array.language_names_es)[0];
            } else if (language.equals("fa")) {
                mCountries[i] = ctvContext.getResources().getStringArray(R.array.language_names_fa)[0];
            } else if (language.equals("fr")) {
                mCountries[i] = ctvContext.getResources().getStringArray(R.array.language_names_fr)[0];
            } else if (language.equals("it")) {
                mCountries[i] = ctvContext.getResources().getStringArray(R.array.language_names_it)[0];
            } else if (language.equals("iw")) {
                mCountries[i] = ctvContext.getResources().getStringArray(R.array.language_names_iw)[0];
            } else if (language.equals("pt")) {
                mCountries[i] = ctvContext.getResources().getStringArray(R.array.language_names_pt)[0];
            } else if (language.equals("ru")) {
                mCountries[i] = ctvContext.getResources().getStringArray(R.array.language_names_ru)[0];
            } else if (language.equals("th")) {
                mCountries[i] = ctvContext.getResources().getStringArray(R.array.language_names_th)[0];
            } else if (language.equals("tr")) {
                mCountries[i] = ctvContext.getResources().getStringArray(R.array.language_names_tr)[0];
            }
        }
    }

    private void setLanguage(Locale locale) {
        int value = 0;
        String language = locale.getLanguage();
        if (language.equals("zh")) {
            value = EnumLanguage.E_CHINESE.ordinal();
        } else if (language.equals("en")) {
            value = EnumLanguage.E_ENGLISH.ordinal();
        } else if (language.equals("ja")) {
            value = EnumLanguage.E_JAPANESE.ordinal();
        } else if (language.equals("fi")) {
            value = EnumLanguage.E_FINNISH.ordinal();
        } else if (language.equals("da")) {
            value = EnumLanguage.E_DANISH.ordinal();
        } else if (language.equals("nb")) {
            value = EnumLanguage.E_NORWEGIAN.ordinal();
        } else if (language.equals("sv")) {
            value = EnumLanguage.E_SWEDISH.ordinal();
        } else if (language.equals("iw_IL")) {
            value = EnumLanguage.E_HEBREW.ordinal();
        } else {
            value = EnumLanguage.E_CHINESE.ordinal();
        }
        CtvTvManager ctvManager = CtvTvManager.getInstance();
        try {
            ctvManager.setLanguage(EnumLanguage.values()[value].ordinal());
        } catch (Exception e) {
        }
    }
}
