package com.ctv.settings.helper;

import android.content.Context;

import com.ctv.settings.language.R;

import java.util.Locale;

/**
 * @Description: 作用描述
 * @Author: wanghang
 * @CreateDate: 2019/9/23 11:10
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019/9/23 11:10
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class LangInputHelper {
    /**
     * 获得国家名称
     *
     * @return
     */
    public static String getCountryName(Context ctvContext){
        Locale locale = ctvContext.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        String country= ctvContext.getResources().getConfiguration().locale.getCountry();
        String name = locale.getDisplayLanguage();
        if (language.equals("zh")) {
            if (country.equals("TW")) {
                name = ctvContext.getResources().getStringArray(
                        R.array.language_names_zh)[1];
            } else if (country.equals("CN")) {
                name = ctvContext.getResources().getStringArray(
                        R.array.language_names_zh)[0];
            } else {
                // Default
                name = ctvContext.getResources().getStringArray(
                        R.array.language_names_zh)[0];
            }
        } else if (language.equals("ar")) {
            name = ctvContext.getResources().getStringArray(R.array.language_names_ar)[0];
        } else if (language.equals("de")) {
            name = ctvContext.getResources().getStringArray(R.array.language_names_de)[0];
        } else if (language.equals("en")) {
            name = ctvContext.getResources().getStringArray(R.array.language_names_en)[0];
        } else if (language.equals("es")) {
            name = ctvContext.getResources().getStringArray(R.array.language_names_es)[0];
        } else if (language.equals("fa")) {
            name = ctvContext.getResources().getStringArray(R.array.language_names_fa)[0];
        } else if (language.equals("fr")) {
            name = ctvContext.getResources().getStringArray(R.array.language_names_fr)[0];
        } else if (language.equals("it")) {
            name = ctvContext.getResources().getStringArray(R.array.language_names_it)[0];
        } else if (language.equals("iw")) {
            name = ctvContext.getResources().getStringArray(R.array.language_names_iw)[0];
        } else if (language.equals("pt")) {
            name = ctvContext.getResources().getStringArray(R.array.language_names_pt)[0];
        } else if (language.equals("ru")) {
            name = ctvContext.getResources().getStringArray(R.array.language_names_ru)[0];
        } else if (language.equals("th")) {
            name = ctvContext.getResources().getStringArray(R.array.language_names_th)[0];
        } else if (language.equals("tr")) {
            name = ctvContext.getResources().getStringArray(R.array.language_names_tr)[0];
        }
        return name;
    }
}
