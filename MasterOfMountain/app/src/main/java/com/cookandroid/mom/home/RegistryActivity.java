package com.cookandroid.mom.home;

import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.regex.Pattern;

import com.cookandroid.mom.MainActivity;
import com.cookandroid.mom.util.Util;
import com.cookandroid.mom.R;
import com.dd.processbutton.iml.ActionProcessButton;

/**
 * Created by S401 on 2017-05-26.
 */


public class RegistryActivity extends AppCompatActivity {
    private EditText id, password, password2, address, email, phone, hint;
    private RadioGroup rg;
    private Button btnCancle;
    private Toolbar toolbar;
    private ActionProcessButton done;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_regi_activity);


        id = (EditText)findViewById(R.id.regId);
        password = (EditText)findViewById(R.id.regPassword);
        password2 = (EditText)findViewById(R.id.regPassword2);
        address = (EditText)findViewById(R.id.regAddress);
        email = (EditText)findViewById(R.id.regEmail);
        phone = (EditText)findViewById(R.id.regPhone);
        hint = (EditText)findViewById(R.id.regHint);
        rg = (RadioGroup)findViewById(R.id.rgSex);
        done = (ActionProcessButton)findViewById(R.id.btnDone);
        done.setMode(ActionProcessButton.Mode.ENDLESS);
        done.setProgress(0);
        btnCancle = (Button)findViewById(R.id.btnCancel);

        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                done.setProgress(50);
                final RadioButton sex = (RadioButton)findViewById(rg.getCheckedRadioButtonId());
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if(!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches())
                        {
                            done.setProgress(-1);
                            Toast.makeText(RegistryActivity.this,"이메일 형식이 아닙니다",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(!Pattern.matches("^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$", phone.getText().toString())) {
                            done.setProgress(-1);
                            Toast.makeText(RegistryActivity.this, "올바른 핸드폰 번호가 아닙니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try {
                            final String message = Util.user.registry(id.getText().toString(), password.getText().toString(),password2.getText().toString(),address.getText().toString(),email.getText().toString(),phone.getText().toString(),sex.getText().toString(),hint.getText().toString());
                            Toast.makeText(RegistryActivity.this, message, Toast.LENGTH_SHORT).show();
                            if(message.equals("가입 성공")){
                                done.setProgress(100);
                                finish();
                            }
                            return;
                        } catch (Exception e) {
                            done.setProgress(-1);
                        }
                    }
                }, 1000);


            }
        });

        toolbar = (Toolbar)findViewById(R.id.registry_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("회원가입");
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