package com.yinghe.whiteboardlib.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestReg {
    public static void main(String[] args){
        String content = "可以是任何字符串www.baidu.com你是www.google.com";
        List<String> urLs = parseURLs(content);

        if (!urLs.isEmpty()){
            for (String s:urLs){
                System.out.println(s);
            }
        }
    }

    /**
     * 提取字符串中的URL
     *
     * @param content
     * @return
     */
    public static List<String> parseURLs(String content){
        List<String> result = new ArrayList<>();

        //提取url
        String regex = "^(?:https?://)?[\\w]{1,}(?:\\.?[\\w]{1,})+[\\w-_/?&=#%:]*$";
        regex = "(?<!\\d)(?:(?:[\\w[.-://]]*\\.[com|cn|net|tv|gov|org|biz|cc|uk|jp|edu]+[^\\s|^\\u4e00-\\u9fa5]*))";
        Pattern pb = Pattern.compile(regex);
        Matcher mb = pb.matcher(content);
        while (mb.find()) {
            result.add(mb.group());
        }

        return result;
    }
}
