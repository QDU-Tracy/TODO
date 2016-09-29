package com.example.login;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Register_MainActivity extends AppCompatActivity {

    private EditText etid,etname;
    private Button btn_qu,btn_sure;

    SQLiteOpenHelper helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        //设置无标题窗口
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        //隐藏状态栏
//        //定义全屏参数
//        int flag= WindowManager.LayoutParams.FLAG_FULLSCREEN;
//        //获得当前窗体对象
//        Window window=Register_MainActivity.this.getWindow();
//        //设置当前窗体为全屏显示
//        window.setFlags(flag, flag);

        setContentView(R.layout.register_activity_main);
        helper=new SqlliteOpenHelper(this);
        helper.getWritableDatabase();
        etid=(EditText)findViewById(R.id.etid);
        etname=(EditText)findViewById(R.id.etname);
        btn_qu=(Button)findViewById(R.id.btn_qu);
        btn_sure=(Button)findViewById(R.id.btn_sure);
        btn_sure.setOnClickListener(new sureListener());
        btn_qu.setOnClickListener(new quListener());

    }

    class sureListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            try {
                //读取数据
                SQLiteDatabase sdb = helper.getWritableDatabase();
                String n1 = etid.getText().toString();
                String n2 = etname.getText().toString();
                //判断字符串是否为空
                if (n1.equals("") || n2.equals("")) {
                    Toast.makeText(getApplicationContext(), "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    ContentValues values = new ContentValues();
                    //添加数据
                    values.put("id", etid.getText().toString());
                    values.put("name", etname.getText().toString());
                    sdb.insert("student", null, values);
                    Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Register_MainActivity.this, MainActivity.class);
                    //携带数据
                    Bundle bundle = new Bundle();
                    bundle.putString("name", etname.getText().toString());
                    intent.putExtras(bundle);
                    //开始activity
                    startActivity(intent);
                }
            } catch (SQLiteException e) {
                Toast.makeText(getApplicationContext(), "注册失败", Toast.LENGTH_SHORT).show();
            }
        }
    }


        class quListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Intent intent=new Intent(Register_MainActivity.this,MainActivity.class);
            startActivity(intent);
        }

    }

}
