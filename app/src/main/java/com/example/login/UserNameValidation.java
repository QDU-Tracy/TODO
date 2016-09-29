package com.example.login;

import android.content.Context;
import android.widget.Toast;

import java.util.regex.Pattern;

/**
 * Created by lenovo on 2015/12/20.
 */
public class UserNameValidation extends ValidationExecutor {
    public boolean doValidate(Context context, String text) {

//        String regex = "^[a-zA-Z](?=.*?[a-zA-Z])(?=.*?[0-9])[a-zA-Z0-9_]{7,11}$";
        String regex="[0-9]+@+[a-z]+.+[a-z]";
        boolean result = Pattern.compile(regex).matcher(text).find();
        if (!result) {
            Toast.makeText(context, context.getString(R.string.e_username_hint1), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
//private boolean isEmailValid(String email) {
//    //TODO: Replace this with your own logic
//    return email.contains("@");
//}
//
//    private boolean isPasswordValid(String password) {
//        //TODO: Replace this with your own logic
//        return password.length() > 4;
//    }
}
