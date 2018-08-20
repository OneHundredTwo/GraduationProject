package com.cookandroid.mom.community.club;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cookandroid.mom.R;
import com.cookandroid.mom.util.User;
import com.cookandroid.mom.util.Util;
import com.dd.processbutton.iml.ActionProcessButton;


/**
 * Created by wdj46 on 2017-08-10.
 */

public class ClubDetailActivity extends ActionBarActivity {
    final Context context = this;
    private ViewPager club_vp;
    private TextView tab_clubInfo, tab_clubTl, tab_clubHt;
    private View club_ll;
    private FrameLayout layout;
    private TextView clubname;
    private ImageView clubPic;
    private Button clubcancel;
    static String clubNm, summit;
    static User.Club club;
    private AlertDialog alertDialog;
    static final String userID = Util.user.getId();
    private ActionProcessButton clubsummit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community_clubdetail_activity);

        clubsummit = (ActionProcessButton) findViewById(R.id.clubsummit);
        clubsummit.setMode(ActionProcessButton.Mode.ENDLESS);
        clubcancel = (Button)findViewById(R.id.clubcancel);
        Toolbar toolbar = (Toolbar)findViewById(R.id.clubdetail_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("동호회 상세정보");

        Intent intent = getIntent();
        clubNm = intent.getStringExtra("clubNm");
        summit = intent.getStringExtra("summit");

        clubname = (TextView)findViewById(R.id.club_name);
        try {
            club = Util.user.getClubDetail(clubNm);
            clubname.setText(club.clubNm);
            clubname.setTextColor(Color.BLACK);
            clubPic = (ImageView)findViewById(R.id.clubPic);
            clubPic.setImageBitmap(Util.getIFS().execute(club.clubPhoto).get());
        } catch (Exception e) {
            e.printStackTrace();
        }
        layout = (FrameLayout)findViewById(R.id.clubdetailLayout);


        club_vp = (ViewPager)findViewById(R.id.club_vp);
        club_ll = findViewById(R.id.club_ll);


        tab_clubInfo = (TextView)findViewById(R.id.tab_clubInfo);
        tab_clubTl = (TextView)findViewById(R.id.tab_clubTimeline);

        club_vp.setAdapter(new pagerAdapter(getSupportFragmentManager()));
        club_vp.setCurrentItem(0);

        tab_clubInfo.setOnClickListener(movePageListener);
        tab_clubInfo.setTag(0);
        tab_clubTl.setOnClickListener(movePageListener);
        tab_clubTl.setTag(1);

        tab_clubInfo.setSelected(true);

        //탭 색깔 바꾸기
        club_vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int i =  0;
                while(i<2){
                    if(position==i)
                        club_ll.findViewWithTag(i).setSelected(true);
                    else
                        club_ll.findViewWithTag(i).setSelected(false);
                    i++;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if(summit.equals("관리")){
            if(club.clubID.equals(userID)){
                clubsummit.setText("해체");
            }else{
                clubsummit.setText("탈퇴");
            }
            clubsummit.setOnClickListener(secession);
        }else{
            clubsummit.setText("가입");
            clubsummit.setOnClickListener(registry);
        }

        clubcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.notice_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:{
                //해당 버튼을 클릭시 액션 삽입
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    View.OnClickListener registry = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.clubsummit:
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                    alertDialogBuilder.setTitle("가입");

                    alertDialogBuilder
                            .setMessage("이 동호회에 가입하시겠습니까?")
                            .setCancelable(false)
                            .setPositiveButton("가입",
                                    new DialogInterface.OnClickListener(){

                                        @Override
                                        public void onClick(final DialogInterface dialog, int which) {
                                            try {
                                                clubsummit.setProgress(50);
                                                final String message = Util.user.regClub(club.clubNum, userID);
                                                new Handler().postDelayed(new Runnable()
                                                {
                                                    @Override
                                                    public void run()
                                                    {
                                                        Toast.makeText(ClubDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                                                        if(message.equals("동호회에 가입되었습니다.")){
                                                            clubsummit.setProgress(100);
                                                            dialog.cancel();
                                                            Intent intent = new Intent(ClubDetailActivity.this, ClubRegistryActivity.class);
                                                            startActivity(intent);
                                                            dialog.dismiss();
                                                            finish();
                                                        }else{
                                                            clubsummit.setProgress(-1);
                                                        }
                                                    }
                                                }, 1000);

                                            } catch (Exception e) {
                                                clubsummit.setProgress(-1);
                                                e.printStackTrace();
                                            }
                                        }
                                    })
                            .setNegativeButton("취소", new DialogInterface.OnClickListener(){

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                    alertDialog = alertDialogBuilder.create();
                    if(alertDialog != null && alertDialog.isShowing() ){
                        alertDialog.dismiss();
                    }
                    alertDialog.show();
                    break;
            }

        }
    };
    View.OnClickListener secession = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.clubsummit:
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    if (club.clubID.equals(userID)) {

                        alertDialogBuilder.setTitle("해체");
                        Log.e("해체","해체하기");
                        alertDialogBuilder
                                .setMessage("이 동호회를 해체하시겠습니까? 동호회를 해체하면 가입되어있는 회원들 모두 탈퇴됩니다.")
                                .setCancelable(false)
                                .setPositiveButton("해체",
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(final DialogInterface dialog, int which) {
                                                try {
                                                    clubsummit.setProgress(50);
                                                    final String message = Util.user.dismissClub(club.clubNum, userID,club.clubPhoto);
                                                    new Handler().postDelayed(new Runnable()
                                                    {
                                                        @Override
                                                        public void run()
                                                        {
                                                            if (message.equals("동호회가 해체되었습니다.")) {
                                                                clubsummit.setProgress(100);
                                                                dialog.cancel();
                                                                Intent intent = new Intent(ClubDetailActivity.this, ClubManagementActivity.class);
                                                                startActivity(intent);
                                                                dialog.dismiss();
                                                                finish();
                                                            }else{
                                                                clubsummit.setProgress(-1);
                                                            }
                                                        }
                                                    }, 1000);
                                                    Toast.makeText(ClubDetailActivity.this, message, Toast.LENGTH_SHORT).show();

                                                } catch (Exception e) {
                                                    clubsummit.setProgress(-1);
                                                    e.printStackTrace();
                                                }
                                            }
                                        })
                                .setNegativeButton("취소", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                    }


                    else {

                        alertDialogBuilder.setTitle("탈퇴");
                        Log.e("탈퇴","탈퇴하기");
                        alertDialogBuilder
                                .setMessage("이 동호회에 탈퇴하시겠습니까?")
                                .setCancelable(false)
                                .setPositiveButton("탈퇴",
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(final DialogInterface dialog, int which) {
                                                try {
                                                    clubsummit.setProgress(50);
                                                    final String message = Util.user.secClub(club.clubNum, userID);
                                                    new Handler().postDelayed(new Runnable()
                                                    {
                                                        @Override
                                                        public void run()
                                                        {
                                                            Toast.makeText(ClubDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                                                            if (message.equals("동호회에 탈퇴되었습니다.")) {
                                                                clubsummit.setProgress(100);
                                                                dialog.cancel();
                                                                Intent intent = new Intent(ClubDetailActivity.this, ClubManagementActivity.class);
                                                                startActivity(intent);
                                                                dialog.dismiss();
                                                                finish();
                                                            }else{
                                                                clubsummit.setProgress(-1);
                                                            }
                                                        }
                                                    }, 1000);

                                                } catch (Exception e) {
                                                    clubsummit.setProgress(-1);
                                                    e.printStackTrace();
                                                }
                                            }
                                        })
                                .setNegativeButton("취소", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });


                    }
                    alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                    break;


            }
        }
    };

    //탭 색깔 바꾸기
    View.OnClickListener movePageListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int tag = (int) v.getTag();

            int i = 0;
            while(i<2){
                if(tag==i)
                    club_ll.findViewWithTag(i).setSelected(true);
                else
                    club_ll.findViewWithTag(i).setSelected(false);
                i++;
            }

            club_vp.setCurrentItem(tag);
        }
    };


    private class pagerAdapter extends FragmentStatePagerAdapter
    {
        public pagerAdapter(android.support.v4.app.FragmentManager fm)
        {
            super(fm);
        }
        @Override
        public Fragment getItem(int position)
        {
            switch(position)
            {
                case 0:
                    return new ClubInfoFragment();
                case 1:
                    return new ClubTimelineFragment();
                default:
                    return null;
            }
        }
        @Override
        public int getCount()
        {
            return 2;
        }
    }

}
