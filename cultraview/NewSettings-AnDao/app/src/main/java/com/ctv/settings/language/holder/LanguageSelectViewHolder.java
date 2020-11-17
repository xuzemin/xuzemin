package com.ctv.settings.language.holder;

import android.app.Activity;
import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.app.backup.BackupManager;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.internal.app.LocalePicker;
import com.ctv.settings.R;
import com.ctv.settings.base.IBaseViewHolder;
import com.ctv.settings.language.adapter.LanguageDailogAdapter;
import com.ctv.settings.utils.L;
import com.ctv.settings.utils.ReflectUtil;
import com.cultraview.tv.CtvTvManager;
import com.mstar.android.tv.TvFactoryManager;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumLanguage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * @Description: 语言选择
 * @Author: wanghang
 * @CreateDate: 2019/9/28 10:28
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019/9/28 10:28
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class LanguageSelectViewHolder implements IBaseViewHolder {
    private static final String TAG = "LanguageSelect";

    private Activity ctvContext;
    private Handler mHandler;

    private ListView language_imput_lv;

    private int mSelect;

    private String[] mCountries;

    private String mLocaleCountry;

    private LanguageDailogAdapter languageDailogAdapter;

    private final ArrayList<Locale> mCurrentLocales = new ArrayList<Locale>();

    private Locale mNewLocale;
    private static final int LANGUAGE_SET_DELAY_MS = 500;

    private final Handler mDelayHandler = new Handler();

    private final Runnable mSetLanguageRunnable = new Runnable() {
        @Override
        public void run() {
            LocalePicker.updateLocale(mNewLocale);
        }
    };


    public LanguageSelectViewHolder(Activity activity) {
        this.ctvContext = activity;

        initUI(ctvContext);
    }

    public LanguageSelectViewHolder(Activity activity, Handler handler) {
        this.ctvContext = activity;
        this.mHandler = handler;

        initUI(ctvContext);
    }

    /**
     * 初始化UI
     *
     * @param activity
     */
    @Override
    public void initUI(Activity activity) {
        initBackTopLayout();
        findViews();
    }

    /**
     * 初始化数据
     *
     * @param activity
     */
    @Override
    public void initData(Activity activity) {

    }

    /**
     * 初始化监听
     */
    @Override
    public void initListener() {

    }

    /**
     * 刷新指定view
     *
     * @param view
     */
    @Override
    public void refreshUI(View view) {

    }

    /**
     * 返回布局设置
     */
    private void initBackTopLayout() {
        // 返回按钮
        ImageView backBtn = (ImageView) ctvContext.findViewById(R.id.back_btn);
        backBtn.setOnClickListener((view) -> {
            ctvContext.finish();
        });

        // 设置title
        TextView backTitle = (TextView) ctvContext.findViewById(R.id.back_title);
        backTitle.setText(R.string.item_language);
    }

    /**
     * init compontent.
     */
    private void findViews() {
        Configuration conf = ctvContext.getResources().getConfiguration();
        mLocaleCountry = conf.locale.getCountry();
        Log.d(TAG, "localeCountry, " + mLocaleCountry);
        language_imput_lv = (ListView) ctvContext.findViewById(R.id.language_imput_lv);
        getLanguageList();
        Log.d(TAG, "mSelect, " + mSelect);
        languageDailogAdapter = new LanguageDailogAdapter(ctvContext, mCountries, mSelect);
        language_imput_lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        language_imput_lv.setAdapter(languageDailogAdapter);
        language_imput_lv.setItemChecked(mSelect, true);
        language_imput_lv.setSelection(mSelect);
        language_imput_lv.setOnItemClickListener(mListViewListener);
    }

    /**
     * 点击事件监听
     */
    AdapterView.OnItemClickListener mListViewListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if (position == mSelect) {
                return;
            }

            mSelect = position;
            languageDailogAdapter.setSelect(position);
            languageDailogAdapter.notifyDataSetChanged();
            Locale locale = mCurrentLocales.get(position);
            setPositionLanguage(locale);
            SystemProperties.set("sys.lanuage.mode", "1");

            ctvContext.finish(); // 界面已隐藏



//            if (position == mSelect) {
//                return;
//            }
//
//            mSelect = position;
//            languageDailogAdapter.setSelect(position);
//            languageDailogAdapter.notifyDataSetChanged();
//            mNewLocale = mCurrentLocales.get(position);
//
////            Locale locale = mCurrentLocales.get(position);
////            setPositionLanguage(locale);
////            SystemProperties.set("sys.lanuage.mode", "1");
//
////            mNewLocale = mLocaleInfoMap.get(radioPreference.getKey()).getLocale();
//            mDelayHandler.removeCallbacks(mSetLanguageRunnable);
//            mDelayHandler.postDelayed(mSetLanguageRunnable, LANGUAGE_SET_DELAY_MS);
//
//            ctvContext.finish(); // 界面已隐藏
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
            ctvContext.finish(); // 界面已隐藏
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

    /**
     * 原生方法获取语言
     */
    private void getCity() {

        Locale currentLocale = getCurrentLauguageUseResources();
        final boolean isInDeveloperMode = Settings.Global.getInt(ctvContext.getContentResolver(),
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0) != 0;
        final List<LocalePicker.LocaleInfo> localeInfoList =
                LocalePicker.getAllAssetLocales(ctvContext, isInDeveloperMode);
        mCountries = new String[3]; //3个语言

        for (int i = 0; i < localeInfoList.size(); i++) {

            if (TextUtils.isEmpty(localeInfoList.get(i).getLocale().getCountry())) {
                continue;
            }
            final String languageTag = localeInfoList.get(i).getLocale().toLanguageTag();
            L.d("qkmin----->2222" + localeInfoList.get(i).getLabel() + "label:" + localeInfoList.get(i).getLocale());

            if (languageTag.equals("zh-CN")) {
                mCountries[0] = localeInfoList.get(i).getLabel();
                mCurrentLocales.add(localeInfoList.get(i).getLocale());
                if (currentLocale.equals(localeInfoList.get(i).getLocale())) {
                    mSelect = 0;
                }
            } else if (languageTag.equals("zh-TW")) {
                mCountries[1] = localeInfoList.get(i).getLabel();
                mCurrentLocales.add(localeInfoList.get(i).getLocale());
                if (currentLocale.equals(localeInfoList.get(i).getLocale())) {
                    mSelect = 1;
                }
            } else if (languageTag.equals("en-US")) {
                mCountries[2] = localeInfoList.get(i).getLabel();
                mCurrentLocales.add(localeInfoList.get(i).getLocale());
                if (currentLocale.equals(localeInfoList.get(i).getLocale())) {
                    mSelect = 2;
                }
            }


        }

    }

    private static String getCurrentLauguage() {
        //获取系统当前使用的语言
        String mCurrentLanguage = Locale.getDefault().getLanguage();

        //设置成简体中文的时候，getLanguage()返回的是zh
        return mCurrentLanguage;
    }

    private Locale getCurrentLauguageUseResources() {
        /**
         * 获得当前系统语言
         */
        Locale locale = ctvContext.getResources().getConfiguration().locale;
        return locale;
    }

    /**
     * 读取数据库 获得语言列表
     */
    private void getLanguageList() {
        // TODO Auto-generated method stub
        Set<String> allLanguage = new HashSet<String>();
        allLanguage.addAll(TvFactoryManager.getCultraviewgetlanguagelist(ctvContext));
        getCountryName(allLanguage.toArray(new String[allLanguage.size()]));
    }

    /**
     * 获得语言名称
     * @param languages
     */
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
