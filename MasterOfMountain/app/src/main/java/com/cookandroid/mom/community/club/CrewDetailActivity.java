package com.cookandroid.mom.community.club;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cookandroid.mom.R;
import com.cookandroid.mom.util.User;
import com.cookandroid.mom.util.Util;
import com.dd.processbutton.iml.ActionProcessButton;
import com.google.firebase.iid.FirebaseInstanceId;


/**
 * Created by wdj46 on 2017-08-10.
 */

public class CrewDetailActivity extends ActionBarActivity {
    final Context context = this;
    private TextView crewdetail_club, crewdetail_region, crewdetail_mt, crewdetail_course, crewdetail_ID;
    private TextView crewdetail_content, crewdetail_join, crewdetail_date;
    private Button  crewcancel;
    private int planNum;
    private String submit;
    static User.Crew crew;
    private AlertDialog alertDialog;
    static final String userID = Util.user.getId();
    private ActionProcessButton crewsubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community_crewdetail_activity);

        crewsubmit = (ActionProcessButton)findViewById(R.id.crewsubmit);
        crewsubmit.setMode(ActionProcessButton.Mode.ENDLESS);
        crewcancel = (Button)findViewById(R.id.crewcancel);
        Toolbar toolbar = (Toolbar)findViewById(R.id.crewdetail_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("크루 상세정보");

        Intent intent = getIntent();
        planNum = intent.getIntExtra("planNum",-1);
        Log.e("planNum",""+planNum);
        submit = intent.getStringExtra("submit");

        crewdetail_club = (TextView)findViewById(R.id.crewdetail_club);
        crewdetail_region = (TextView)findViewById(R.id.crewdetail_region);
        crewdetail_mt = (TextView)findViewById(R.id.crewdetail_mt);
        crewdetail_course = (TextView)findViewById(R.id.crewdetail_course);
        crewdetail_content = (TextView)findViewById(R.id.crewdetail_Content);
        crewdetail_join = (TextView)findViewById(R.id.crewdetail_join);
        crewdetail_date = (TextView)findViewById(R.id.crewdetail_date);
        crewdetail_ID = (TextView)findViewById(R.id.crewdetail_ID);
        try {
            crew = Util.user.getCrewDetail(planNum);
            crewdetail_club.setText("동호회 : "+crew.clubNum);
            crewdetail_region.setText("지역 : "+crew.planregion);
            crewdetail_mt.setText("산 : "+crew.mtNm);
            crewdetail_course.setText("코스 : "+crew.coursenum+"코스");
            crewdetail_content.setText(crew.plancontent);
            crewdetail_join.setText("인원 : "+crew.prejoin+"/"+crew.joinNum+"명");
            crewdetail_date.setText("일정 : "+crew.planDate);
            crewdetail_ID.setText("크루 관리자 : "+crew.planID);

            crewdetail_club.setTextColor(Color.BLACK);
            crewdetail_region.setTextColor(Color.BLACK);
            crewdetail_mt.setTextColor(Color.BLACK);
            crewdetail_course.setTextColor(Color.BLACK);
            crewdetail_content.setTextColor(Color.BLACK);
            crewdetail_join.setTextColor(Color.BLACK);
            crewdetail_date.setTextColor(Color.BLACK);
            crewdetail_ID.setTextColor(Color.BLACK);
        } catch (Exception e) {
            e.printStackTrace();
        }



        if(submit.equals("관리")){
            if(crew.planID.equals(userID)){
                crewsubmit.setText("해체");
            }else{
                crewsubmit.setText("탈퇴");
            }
            crewsubmit.setOnClickListener(secession);
        }else{
            crewsubmit.setText("참여");
            crewsubmit.setOnClickListener(join);
        }
        crewcancel.setOnClickListener(secession);
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

    View.OnClickListener join = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.crewsubmit:
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                    alertDialogBuilder.setTitle("참여");

                    alertDialogBuilder
                            .setMessage("이 크루에 참여하시겠습니까?")
                            .setCancelable(false)
                            .setPositiveButton("참여",
                                    new DialogInterface.OnClickListener(){

                                        @Override
                                        public void onClick(final DialogInterface dialog, int which) {
                                            try {
                                                crewsubmit.setProgress(50);
                                                final String message = Util.user.joinCrew(crew.planNum,userID, FirebaseInstanceId.getInstance().getToken());
                                                new Handler().postDelayed(new Runnable()
                                                {
                                                    @Override
                                                    public void run()
                                                    {
                                                        Toast.makeText(CrewDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                                                        if(message.equals("크루에 참여되었습니다.")){
                                                            crewsubmit.setProgress(100);
                                                            dialog.cancel();
                                                            finish();
                                                        }else{
                                                            crewsubmit.setProgress(-1);
                                                        }
                                                    }
                                                }, 1000);

                                            } catch (Exception e) {
                                                crewsubmit.setProgress(-1);
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
                case R.id.crewcancel:
                    finish();
                    break;
            }

        }
    };
    View.OnClickListener secession = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.crewsubmit:
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    if (crew.planID.equals(userID)) {

                        alertDialogBuilder.setTitle("해체");
                        Log.e("해체","해체하기");
                        alertDialogBuilder
                                .setMessage("이 크루를 해체하시겠습니까? 동호회를 해체하면 가입되어있는 회원들 모두 탈퇴됩니다.")
                                .setCancelable(false)
                                .setPositiveButton("해체",
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(final DialogInterface dialog, int which) {
                                                try {
                                                    crewsubmit.setProgress(50);
                                                    final String message = Util.user.dismissCrew(crew.planNum,userID);
                                                    new Handler().postDelayed(new Runnable()
                                                    {
                                                        @Override
                                                        public void run()
                                                        {
                                                            Toast.makeText(CrewDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                                                            if (message.equals("크루가 해체되었습니다.")) {
                                                                crewsubmit.setProgress(100);
                                                                dialog.cancel();
                                                                Intent intent = new Intent(CrewDetailActivity.this, CrewManagementActivity.class);
                                                                startActivity(intent);
                                                                dialog.dismiss();
                                                                finish();
                                                            }else{
                                                                crewsubmit.setProgress(-1);
                                                            }
                                                        }
                                                    }, 1000);
                                                } catch (Exception e) {
                                                    crewsubmit.setProgress(-1);
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
                                .setMessage("이 크루에 탈퇴하시겠습니까?")
                                .setCancelable(false)
                                .setPositiveButton("탈퇴",
                                        new DialogInterface.OnClickListener() {

                                            public void onClick(final DialogInterface dialog, int which) {
                                                try {
                                                    crewsubmit.setProgress(50);
                                                    final String message = Util.user.secCrew(crew.planNum,userID,FirebaseInstanceId.getInstance().getToken());
                                                    new Handler().postDelayed(new Runnable()
                                                    {
                                                        @Override
                                                        public void run()
                                                        {
                                                            Toast.makeText(CrewDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                                                            if (message.equals("크루에 탈퇴되었습니다.")) {
                                                                crewsubmit.setProgress(100);
                                                                dialog.cancel();
                                                                Intent intent = new Intent(CrewDetailActivity.this, CrewManagementActivity.class);
                                                                startActivity(intent);
                                                                dialog.dismiss();
                                                                finish();
                                                            }else{
                                                                crewsubmit.setProgress(-1);
                                                            }
                                                        }
                                                    }, 1000);
                                                } catch (Exception e) {
                                                    crewsubmit.setProgress(-1);
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
                case R.id.crewcancel:
                    finish();
                    break;



            }
        }
    };


}
