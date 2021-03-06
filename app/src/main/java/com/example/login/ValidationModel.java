package com.example.login;

import android.text.TextUtils;
import android.widget.EditText;

/**
 * Created by lenovo on 2015/12/20.
 */
public class ValidationModel {
    private EditText editText;
    private ValidationExecutor validationExecutor;

    public ValidationModel(EditText editText, ValidationExecutor validationExecutor) {
        this.editText = editText;
        this.validationExecutor = validationExecutor;
    }

    public EditText getEditText() {
        return editText;
    }

    public ValidationModel setEditText(EditText editText) {
        this.editText = editText;
        return this;
    }

    public ValidationExecutor getValidationExecutor() {
        return validationExecutor;
    }

    public ValidationModel setValidationExecutor(ValidationExecutor validationExecutor) {
        this.validationExecutor = validationExecutor;
        return this;
    }

    public boolean isTextEmpty() {
        if (editText==null|| TextUtils.isEmpty(editText.getText())) {
            return true;
        }
        return false;
    }

}
