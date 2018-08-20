package com.cookandroid.mom.my;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cookandroid.mom.R;
import com.cookandroid.mom.util.Util;
import com.dd.processbutton.iml.ActionProcessButton;

import java.util.regex.Pattern;

/**
 * Created by S401 on 2017-05-26.
 */


public class EditActivity extends AppCompatActivity {
    private EditText  password, password2, address, email, phone, hint;
    private TextView id, sex;
    private Button btnCancle;
    private Toolbar toolbar;
    private ActionProcessButton done;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_edituser_activity);

        Intent intent = getIntent();
        id = (TextView)findViewById(R.id.editId);
        password = (EditText)findViewById(R.id.editPassword);
        password2 = (EditText)findViewById(R.id.editPassword2);
        address = (EditText)findViewById(R.id.editAddress);
        email = (EditText)findViewById(R.id.editEmail);
        phone = (EditText)findViewById(R.id.editPhone);
        hint = (EditText)findViewById(R.id.editHint);
        sex = (TextView) findViewById(R.id.editSex);
        done = (ActionProcessButton)findViewById(R.id.btnDone);
        done.setMode(ActionProcessButton.Mode.ENDLESS);
        done.setProgress(0);
        btnCancle = (Button)findViewById(R.id.btnCancel);

        id.setText("ID : "+ intent.getStringExtra("id"));
        address.setText(intent.getStringExtra("address"));
        email.setText(intent.getStringExtra("email"));
        phone.setText(intent.getStringExtra("phone"));
        hint.setText(intent.getStringExtra("hint"));
        sex.setText("성별 : "+intent.getStringExtra("sex"));

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
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if(!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches())
                        {
                            done.setProgress(0);
                            Toast.makeText(EditActivity.this,"이메일 형식이 아닙니다",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(!Pattern.matches("^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$", phone.getText().toString())) {
                            done.setProgress(0);
                            Toast.makeText(EditActivity.this, "올바른 핸드폰 번호가 아닙니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                            final String message = Util.user.Edit(Util.user.getId(),password.getText().toString(),password2.getText().toString(),address.getText().toString(),email.getText().toString(),phone.getText().toString(),hint.getText().toString());
                            Toast.makeText(EditActivity.this, message, Toast.LENGTH_SHORT).show();
                            if(message.equals("회원정보가 수정되었습니다.")){
                                done.setProgress(100);
                                finish();
                            }else{
                                done.setProgress(0);
                            }
                            return;

                    }
                }, 1000);


            }
        });

        toolbar = (Toolbar)findViewById(R.id.edituser_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("회원정보수정");
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