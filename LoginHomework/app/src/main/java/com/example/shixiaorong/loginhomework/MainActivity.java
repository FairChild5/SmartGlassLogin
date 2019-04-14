package com.example.shixiaorong.loginhomework;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

//MD5
class MD5Utils {
    //md5 加密算法
    public static String md5(String text) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("md5");
            // 数组 byte[] result -> digest.digest( );  文本 text.getBytes();
            byte[] result = digest.digest(text.getBytes());
            //创建StringBuilder对象 然后建议StringBuffer，安全性高
            //StringBuilder sb = new StringBuilder();
            StringBuffer sb = new StringBuffer();
            // result数组，digest.digest ( ); -> text.getBytes();
            // for 循环数组byte[] result;
            for (byte b : result){
                // 0xff 为16进制
                int number = b & 0xff;
                // number值 转换 字符串 Integer.toHexString( );
                String hex = Integer.toHexString(number);
                if (hex.length() == 1){
                    sb.append("0"+hex);
                }else {
                    sb.append(hex);
                }
            }
            //sb StringBuffer sb = new StringBuffer();对象实例化
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            //发送异常return空字符串
            return "";
        }
    }
}


public class MainActivity extends AppCompatActivity {

    //实现登录逻辑
    TextView tv_main_title;//标题
    TextView tv_back, tv_register, tv_find_psw;//返回键,显示的注册，找回密码
    Button btn_login;//登录按钮
    String userName, psw, spPsw;//获取的用户名，密码，加密密码
    EditText et_user_name, et_psw;//编辑框


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        EditText editPhone = (EditText) findViewById(R.id.EditPhone);
        Drawable drawablePhone = getResources().getDrawable(R.drawable.phone);
        drawablePhone.setBounds(0, 0, 60, 60);
        editPhone.setCompoundDrawables(drawablePhone, null, null, null);

        EditText editPassword = (EditText) findViewById(R.id.EditPassword);
        Drawable drawablePassword = getResources().getDrawable(R.drawable.password);
        drawablePassword.setBounds(0, 0, 60, 60);
        editPassword.setCompoundDrawables(drawablePassword, null, null, null);


        //初始化
        init();


    }


        //获取界面内的控件
        //获取界面控件
        private void init() {
        btn_login=findViewById(R.id.btn_login);
        et_user_name=findViewById(R.id.EditPhone);
        et_psw=findViewById(R.id.EditPassword);

        //向注册界面跳转(注册按钮)
        Button btn_register = (Button) findViewById(R.id.register_btn);
        btn_register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }

        });


        //登录按钮的点击事件
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开始登录，获取用户名和密码 getText().toString().trim();
                userName=et_user_name.getText().toString().trim();
                psw=et_psw.getText().toString().trim();
                //对当前用户输入的密码进行MD5加密再进行比对判断, MD5Utils.md5( ); psw 进行加密判断是否一致
                String md5Psw= MD5Utils.md5(psw);
                // md5Psw ; spPsw 为 根据从SharedPreferences中用户名读取密码
                // 定义方法 readPsw为了读取用户名，得到密码
                spPsw=readPsw(userName);
                // TextUtils.isEmpty
                if(TextUtils.isEmpty(userName)){
                    Toast.makeText(MainActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                    return;
                }else if(TextUtils.isEmpty(psw)){
                    Toast.makeText(MainActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                    // md5Psw.equals(); 判断，输入的密码加密后，是否与保存在SharedPreferences中一致
                }else if(md5Psw.equals(spPsw)){
                    //一致登录成功
                    Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    //保存登录状态，在界面保存登录的用户名 定义个方法 saveLoginStatus boolean 状态 , userName 用户名;
                    saveLoginStatus(true, userName);
                    //登录成功后关闭此页面进入主页
                    Intent data=new Intent();
                    //datad.putExtra( ); name , value ;
                    data.putExtra("isLogin",true);
                    //RESULT_OK为Activity系统常量，状态码为-1
                    // 表示此页面下的内容操作成功将data返回到上一页面，如果是用back返回过去的则不存在用setResult传递data值
                    setResult(RESULT_OK,data);
                    //销毁登录界面
                    MainActivity.this.finish();
                    //跳转到主界面，登录成功的状态传递到 MainActivity 中
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                    return;
                }else if((spPsw!=null&&!TextUtils.isEmpty(spPsw)&&!md5Psw.equals(spPsw))){
                    Toast.makeText(MainActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    Toast.makeText(MainActivity.this, "此用户不存在", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

        /**
         *从SharedPreferences中根据用户名读取密码
         */
        private String readPsw(String userName){
            //getSharedPreferences("loginInfo",MODE_PRIVATE);
            //"loginInfo",mode_private; MODE_PRIVATE表示可以继续写入
            SharedPreferences sp=getSharedPreferences("loginInfo", MODE_PRIVATE);
            //sp.getString() userName, "";
            return sp.getString(userName , "");
        }
        /**
         *保存登录状态和登录用户名到SharedPreferences中
         */
        private void saveLoginStatus(boolean status,String userName){
            //saveLoginStatus(true, userName);
            //loginInfo表示文件名  SharedPreferences sp=getSharedPreferences("loginInfo", MODE_PRIVATE);
            SharedPreferences sp=getSharedPreferences("loginInfo", MODE_PRIVATE);
            //获取编辑器
            SharedPreferences.Editor editor=sp.edit();
            //存入boolean类型的登录状态
            editor.putBoolean("isLogin", status);
            //存入登录状态时的用户名
            editor.putString("loginUserName", userName);
            //提交修改
            editor.commit();
        }
        /**
         * 注册成功的数据返回至此
         * @param requestCode 请求码
         * @param resultCode 结果码
         * @param data 数据
         */
        @Override
        //显示数据， onActivityResult
        //startActivityForResult(intent, 1); 从注册界面中获取数据
        //int requestCode , int resultCode , Intent data
        // LoginActivity -> startActivityForResult -> onActivityResult();
        protected void onActivityResult(int requestCode, int resultCode, Intent data)
        {
            //super.onActivityResult(requestCode, resultCode, data);
            super.onActivityResult(requestCode, resultCode, data);
            if(data!=null){
                //是获取注册界面回传过来的用户名
                // getExtra().getString("***");
                String userName=data.getStringExtra("userName");
                if(!TextUtils.isEmpty(userName)){
                    //设置用户名到 et_user_name 控件
                    et_user_name.setText(userName);
                    //et_user_name控件的setSelection()方法来设置光标位置
                    et_user_name.setSelection(userName.length());
                }
            }
        }
    }

