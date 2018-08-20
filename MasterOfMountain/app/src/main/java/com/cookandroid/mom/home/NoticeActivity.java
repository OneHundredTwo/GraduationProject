package com.cookandroid.mom.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.cookandroid.mom.R;
import com.cookandroid.mom.util.Community;
import com.cookandroid.mom.util.Community.Notice;
import com.cookandroid.mom.util.DataStringFormat;

/**
 * Created by RJH on 2017-06-18.
 */
public class NoticeActivity extends ActionBarActivity {

    private Toolbar toolbar;
    private TextView notiId, notiContent, notiDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_notice_activity);

        notiId = (TextView)findViewById(R.id.noticeID);
        notiContent = (TextView)findViewById(R.id.noticeContent);
        notiDate = (TextView)findViewById(R.id.noticeDate);
        Intent intent=new Intent(this.getIntent());
        Notice notice = (Notice)intent.getSerializableExtra("data");

        notiId.setText(notice.getAdmin_id());
        notiContent.setText(notice.getContent());
        notiDate.setText(DataStringFormat.y_mon_d_h_m.format(notice.getDate()));

        toolbar = (Toolbar)findViewById(R.id.notice_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("공지사항");
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
