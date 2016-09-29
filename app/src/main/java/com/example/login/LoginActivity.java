package com.example.login;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText et_id, et_name;
    private Button btn_test, btn_local;
    //1,SQLite的声明
    SQLiteOpenHelper helper;
    private String _id;
    private String _name;
    private EditTextValidator editTextValidator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        //设置无标题窗口
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        //隐藏状态栏
//        //定义全屏参数
//        int flag= WindowManager.LayoutParams.FLAG_FULLSCREEN;
//        //获得当前窗体对象
//        Window window=LoginActivity.this.getWindow();
//        //设置当前窗体为全屏显示
//        window.setFlags(flag, flag);

        setContentView(R.layout.activity_login);
        // 2，数据库的创建，及调用
        helper = new SqlliteOpenHelper(this);
        helper.getWritableDatabase();

        et_id = (EditText) findViewById(R.id.editText1);
        et_name = (EditText) findViewById(R.id.editText2);
        btn_test = (Button) findViewById(R.id.button1);
        btn_local = (Button) findViewById(R.id.button2);

        btn_test.setOnClickListener(new testListener());
        editTextValidator = new EditTextValidator(this)
                .setButton(btn_local)
                .add(new ValidationModel(et_id,new UserNameValidation()))
                .add(new ValidationModel(et_name,new PasswordValidation()))
                .execute();
        btn_local.setOnClickListener(new localListener());

    }

    class testListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Intent intent = new Intent(LoginActivity.this, Register_MainActivity.class);
            startActivity(intent);
        }

    }
    //登陆按钮

    class localListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            _id = et_id.getText().toString();
            _name = et_name.getText().toString();
            if (_name.equals("") || _id.equals("")) {         Toast.makeText(getApplicationContext(), "请输入账号或密码！",Toast.LENGTH_SHORT).show();
            } else {
                sureuser(_id, _name);
            }

        }


    }

    private void sureuser(String userid, String username) {
        //3,数据库的操作，查询
        SQLiteDatabase sdb = helper.getReadableDatabase();
        try {
            String sql = "select * from student where id=? and name=?";
            // 实现遍历id和name
            Cursor cursor = sdb.rawQuery(sql, new String[] { _id, _name });
            if (cursor.getCount() > 0) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("name", _name);
                intent.putExtras(bundle);
                startActivity(intent);
                this.finish();
            } else {
                Toast.makeText(getApplicationContext(), "登录失败",
                        Toast.LENGTH_SHORT).show();
            }
            cursor.close();
            sdb.close();
        } catch (SQLiteException e) {
            Toast.makeText(getApplicationContext(), "亲，请注册！",
                    Toast.LENGTH_SHORT).show();
        }
    }

}
