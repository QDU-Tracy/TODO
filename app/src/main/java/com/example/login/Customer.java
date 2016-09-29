package com.example.login;
import java.io.Serializable;

/**
 * Created by lenovo on 2015/12/29.
 */
public class Customer implements Serializable {

    private String usernameEditText;
    private String passwordEditText;


    public Customer(String  usernameEditText) {
        super();

        this.usernameEditText=usernameEditText;


    }

    public Customer(String usernameEditText, String passwordEditText
                   ) {
        super();

        this.usernameEditText=usernameEditText;
        this.passwordEditText=passwordEditText;

    }

    public String getUsernameEditText() {
        return usernameEditText;
    }

    public void setUsernameEditText(String usernameEditText) {
        this.usernameEditText = usernameEditText;
    }

    public String getPasswordEditText() {
        return passwordEditText;
    }

    public void setPasswordEditText(String passwordEditText) {
        this.passwordEditText = passwordEditText;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "usernameEditText='" + usernameEditText + '\'' +
                ", passwordEditText='" + passwordEditText + '\'' +
                '}';
    }
}
