package com.example.login;
import android.content.Context;

/**
 * Created by lenovo on 2015/12/20.
 */
public abstract class ValidationExecutor extends ValidationUtil {
    /**
     *
     * 这里做校验处理
     *
     * @return 校验成功返回true 否则返回false
     */
    public abstract boolean doValidate(Context context, String text);


}
