package com.cookandroid.mom.home;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.content.Intent;

import com.cookandroid.mom.util.User;
import com.cookandroid.mom.util.Util;
import com.cookandroid.mom.R;
import com.dd.processbutton.iml.ActionProcessButton;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    private Button btnRegist;
    private EditText etId, etPassword;
    private Toolbar toolbar;

    User.LoginStatus ls;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_login_activity);


        btnRegist = (Button)findViewById(R.id.btnRegist);
        final ActionProcessButton btnLogin = (ActionProcessButton) findViewById(R.id.btnLogin);
        btnLogin.setMode(ActionProcessButton.Mode.ENDLESS);
        btnLogin.setProgress(0);
        etId = (EditText)findViewById(R.id.etid);
        etPassword = (EditText)findViewById(R.id.etPassword);
        Intent intent2 = new Intent(this.getIntent());
        if(intent2 != null) {
            etId.setText(intent2.getStringExtra("id"));
        }
        btnRegist.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(LoginActivity.this, RegistryActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if (!etId.getText().toString().trim().equals("")) {
                    if (!etPassword.getText().toString().trim().equals("")) {
                        try {
                            ls = Util.user.loginCheck(etId.getText().toString(), etPassword.getText().toString());
                            btnLogin.setProgress(50);
                            new Handler().postDelayed(new Runnable()
                            {
                                @Override
                                public void run() {
                                    if (ls.isStatus()) {
                                        //isStatus : id와 password가 맞는지 성공여부
                                        if (ls.islogin()) {
                                            //islogin : 이미 로그인된 기기가 있는지 여부
                                            View view = getLayoutInflater().inflate(R.layout.login_check_dia, null);
                                            new AlertDialog.Builder(LoginActivity.this)
                                                    .setTitle("로그인 확인")
                                                    .setView(view)
                                                    .setPositiveButton("로그인", new Dialog.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            LoginActivity.this.login();
                                                        }
                                                    })
                                                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            btnLogin.setProgress(0);
                                                        }
                                                    })
                                                    .show();
                                        } else {
                                            login();
                                        }
                                    } else {
                                        String message = ls.getMessage();
                                        btnLogin.setProgress(-1);
                                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                                        btnLogin.setProgress(0);
                                    }
                                }
                            },1000);



                            return;
                        } catch (Exception e) {
                            Log.e("login",e.toString());
                            e.printStackTrace();
                        }
                    }else{
                        btnLogin.setProgress(-1);
                        Toast.makeText(LoginActivity.this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                        btnLogin.setProgress(0);}
                }else{
                    btnLogin.setProgress(-1);
                    Toast.makeText(LoginActivity.this, "아이디를 입력해주세요", Toast.LENGTH_SHORT).show();
                    btnLogin.setProgress(0); }
            }
        });

        toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("로그인");
    }

    public void login(){
        // id와 password가 맞으면서, 로그인이 되어있지 않은경우 : 로그인
        try {
            String message = Util.user.login(etId.getText().toString(), ls.islogin());
            //로그인 시키고.
            //성공 메세지 호출후 인텐트.
            if (message.equals("로그인 성공")) {
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(LoginActivity.this, HomeFragment.class);
                LoginActivity.this.setResult(Activity.RESULT_OK, intent);
                finish();
            } else {
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // 메뉴를 연동하고
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notice_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case android.R.id.home:
            {
                // 해당 버튼을 눌렀을 때 적절한 액션을 넣는다.
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
