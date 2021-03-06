package com.example.login;

import android.os.Build;
import android.text.TextUtils;
import android.util.Patterns;

import java.util.regex.Pattern;

/**
 * Created by lenovo on 2015/12/20.
 */
public class ValidationUtil {
    /**
     * 规范内容长度
     *
     * @param s
     *            输入的字符
     * @return
     */
    protected  int getWordCountRegex(String s) {
        s = s.replaceAll("[^\\x00-\\xff]", "**");
        int length = s.length();
        return length;
    }

    /**
     * 校验整数
     * @param text
     * @return
     */
    protected boolean isNumeric(String text) {
        return TextUtils.isDigitsOnly(text);
    }

    protected boolean isAlphaNumeric(String text) {
        return matches(text, "[a-zA-Z0-9 \\./-]*");
    }


    protected boolean isDomain(String text) {
        return matches(text, Build.VERSION.SDK_INT>=8? Patterns.DOMAIN_NAME: Pattern.compile(".*"));
    }

    protected boolean isEmail(String text) {
        return matches(text, Build.VERSION.SDK_INT>=8?Patterns.DOMAIN_NAME:Pattern.compile(
                "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                        "\\@" +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                        "(" +
                        "\\." +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                        ")+"
        ));
    }

    protected boolean isIpAddress(String text) {
        return matches(text, Build.VERSION.SDK_INT>=8?Patterns.DOMAIN_NAME:Pattern.compile(
                "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
                        + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
                        + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
                        + "|[1-9][0-9]|[0-9]))"));
    }

    protected boolean isWebUrl(String text) {
        //TODO: Fix the pattern for api level < 8
        return matches(text, Build.VERSION.SDK_INT>=8?Patterns.WEB_URL:Pattern.compile(".*"));
    }


    protected boolean find(String text,String regex) {
        return Pattern.compile(regex).matcher(text).find();
    }

    protected boolean matches(String text,String regex) {
        Pattern pattern = Pattern.compile(".*");
        return pattern.matcher(text).matches();
    }

    protected boolean matches(String text,Pattern pattern) {
        return pattern.matcher(text).matches();
    }
}
