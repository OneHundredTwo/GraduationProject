package com.cookandroid.mom.community.club;

/**
 * Created by wdj46 on 2017-08-08.
 */

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cookandroid.mom.R;
import com.cookandroid.mom.util.MountainManager;
import com.cookandroid.mom.util.User;
import com.cookandroid.mom.util.Util;
import com.dd.processbutton.iml.ActionProcessButton;
import com.google.firebase.iid.FirebaseInstanceId;

public class InsertCrewActivity extends ActionBarActivity {
    final int DIALOG_DATE = 1;
    final int DIALOG_TIME = 2;

    private Button btnData,btnTime, crewCancel;
    private ActionProcessButton crewOK;
    private String [][] club, mountain,courses;
    private TextView tvYear;
    private TextView tvMonth;
    private TextView tvDay;

    private TextView tvHour;
    private TextView tvMinute;

    private EditText crewcontent;

    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;

    private Calendar today;

    private MountainManager.Mountain mom;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community_insert_crew);
        try {
            club = Util.user.getClub(0, Util.user.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //스피너
        String[] clubName = club[0];
        String[] region = {"서울/경기", "강원도", "충청도", "전라도","경상도", "제주도"};
        String[] joinNum = {"3", "4", "5", "6", "7", "8", "9", "10"};

        Toolbar toolbar = (Toolbar) findViewById(R.id.crewregistry_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("크루 등록");

        btnData = (Button) findViewById(R.id.btnData);
        btnTime = (Button) findViewById(R.id.btnTime);
        crewOK =  (ActionProcessButton) findViewById(R.id.insertCrewOK);
        crewOK.setMode(ActionProcessButton.Mode.ENDLESS);
        crewCancel = (Button)findViewById(R.id.cancelCrew) ;

        tvYear = (TextView) findViewById(R.id.year);
        tvMonth = (TextView) findViewById(R.id.monthOfyear);
        tvDay = (TextView) findViewById(R.id.dayOfmonth);

        tvHour = (TextView)findViewById(R.id.hourOfday);
        tvMinute = (TextView)findViewById(R.id.minute);

        crewcontent = (EditText)findViewById(R.id.CrewContent);

        btnData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_DATE);
            }
        });
        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_TIME);
            }
        });
        crewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        today = Calendar.getInstance();

        final Spinner sp_ClubName = (Spinner)findViewById(R.id.clubID);
        final Spinner sp_Region = (Spinner)findViewById(R.id.region);
        final Spinner sp_mName = (Spinner)findViewById(R.id.mName);
        final Spinner sp_Course = (Spinner)findViewById(R.id.course);
        final Spinner sp_JoinNum = (Spinner)findViewById(R.id.joinNum);

        //ClubName에 대한 Spinner
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), R.layout.spinner_item, clubName);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);

        sp_ClubName.setAdapter(adapter);

        //Region에 대한 Spinner
        ArrayAdapter adapter1 = new ArrayAdapter(getApplicationContext(), R.layout.spinner_item, region);
        adapter1.setDropDownViewResource(R.layout.spinner_dropdown);

        sp_Region.setAdapter(adapter1);

        sp_Region.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, final int position, long l) {
                Log.e("지역:",""+sp_Region.getSelectedItemPosition());
                String area = null;
                switch (position){
                    case 0:
                        area = "경기도,서울특별시,인천광역시";
                        try {
                            mountain = Util.mtManager.getAreaMts(-1, area);
                            String[] mName = mountain[1];
                            ArrayAdapter adapter2 = new ArrayAdapter(getApplicationContext(), R.layout.spinner_item, mName);
                            adapter2.setDropDownViewResource(R.layout.spinner_dropdown);

                            sp_mName.setAdapter(adapter2);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 1:
                        try {
                            mountain = Util.mtManager.getAreaMts(-1, sp_Region.getSelectedItem().toString());
                            String[] mName = mountain[1];
                            ArrayAdapter adapter2 = new ArrayAdapter(getApplicationContext(), R.layout.spinner_item, mName);
                            adapter2.setDropDownViewResource(R.layout.spinner_dropdown);

                            sp_mName.setAdapter(adapter2);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        area = "충청북도,충청남도,대전광역시";
                        try {
                            mountain = Util.mtManager.getAreaMts(-1, area);
                            String[] mName = mountain[1];
                            ArrayAdapter adapter2 = new ArrayAdapter(getApplicationContext(), R.layout.spinner_item, mName);
                            adapter2.setDropDownViewResource(R.layout.spinner_dropdown);

                            sp_mName.setAdapter(adapter2);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 3:
                        area = "전라북도,전라남도,광주광역시";
                        try {
                            mountain = Util.mtManager.getAreaMts(-1, area);
                            String[] mName = mountain[1];
                            ArrayAdapter adapter2 = new ArrayAdapter(getApplicationContext(), R.layout.spinner_item, mName);
                            adapter2.setDropDownViewResource(R.layout.spinner_dropdown);

                            sp_mName.setAdapter(adapter2);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 4:
                        area = "경상북도,경상남도,부산광역시,울산광역시";
                        try {
                            mountain = Util.mtManager.getAreaMts(-1, area);
                            String[] mName = mountain[1];
                            ArrayAdapter adapter2 = new ArrayAdapter(getApplicationContext(), R.layout.spinner_item, mName);
                            adapter2.setDropDownViewResource(R.layout.spinner_dropdown);

                            sp_mName.setAdapter(adapter2);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 5:
                        try {
                            mountain = Util.mtManager.getAreaMts(-1, sp_Region.getSelectedItem().toString());
                            String[] mName = mountain[1];
                            ArrayAdapter adapter2 = new ArrayAdapter(getApplicationContext(), R.layout.spinner_item, mName);
                            adapter2.setDropDownViewResource(R.layout.spinner_dropdown);

                            sp_mName.setAdapter(adapter2);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //mName에 대한 Spinner

        sp_mName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                Log.e("산이름:",""+sp_mName.getSelectedItemPosition());
                mom = Util.mtManager.getMtDetail(sp_mName.getSelectedItem().toString());
                courses = Util.mtManager.getMtCourses(mom.mtCode);
                String[] course = courses[1];
                ArrayAdapter adapter3 = new ArrayAdapter(getApplicationContext(), R.layout.spinner_item, course);
                adapter3.setDropDownViewResource(R.layout.spinner_dropdown);
                sp_Course.setAdapter(adapter3);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        //Course에 대한 Spinner



        //JoinNum에 대한 Spinner
        ArrayAdapter adapter4 = new ArrayAdapter(getApplicationContext(), R.layout.spinner_item, joinNum);
        adapter4.setDropDownViewResource(R.layout.spinner_dropdown);

        sp_JoinNum.setAdapter(adapter4);

        crewOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crewOK.setProgress(50);
                if(tvYear ==null){
                    crewOK.setProgress(-1);
                    Toast.makeText(InsertCrewActivity.this, "날짜를 설정해주십시오.", Toast.LENGTH_SHORT).show();
                }else if(tvHour==null){
                    crewOK.setProgress(-1);
                    Toast.makeText(InsertCrewActivity.this, "시간을 설정해주십시오.", Toast.LENGTH_SHORT).show();
                }else {
                    User.Club club = Util.user.getClubDetail(sp_ClubName.getSelectedItem().toString());
                    String date = tvYear.getText().toString().trim() + "-" + tvMonth.getText().toString().trim() + "-" + tvDay.getText().toString().trim() + " " + tvHour.getText().toString().trim()
                            + ":" + tvMinute.getText().toString().trim();
                    final String message = Util.community.InsertCrew(Util.user.getId(), sp_JoinNum.getSelectedItem().toString(),
                            sp_mName.getSelectedItem().toString() , date, club.clubNum,crewcontent.getText().toString(), sp_Region.getSelectedItem().toString(),sp_Course.getSelectedItem().toString(),
                            FirebaseInstanceId.getInstance().getToken());
                    new Handler().postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Toast.makeText(InsertCrewActivity.this,message, Toast.LENGTH_SHORT).show();
                            if (message.equals("크루가 등록되었습니다.")) {
                                crewOK.setProgress(100);
                                finish();
                            }else{
                                crewOK.setProgress(-1);
                            }
                        }
                    }, 1000);
                }
            }
        });
    }//onCrete

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notice_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home: {
                //해당 버튼을 클릭시 액션 삽입
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    @TargetApi(Build.VERSION_CODES.N)
    protected Dialog onCreateDialog(int id){
        switch (id){
            case DIALOG_DATE:
                DatePickerDialog dpd = new DatePickerDialog(InsertCrewActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        mYear = year;
                        mMonth = month+1;
                        mDay = dayOfMonth;
                        updateDate();
                    }
                }, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
                return dpd;
            case DIALOG_TIME:
                TimePickerDialog tpd = new TimePickerDialog(InsertCrewActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mHour = hourOfDay;
                        mMinute = minute;
                        updateTime();
                    }
                }, 12, 00, false);
                return tpd;
        }
        return super.onCreateDialog(id);
    }

    private void updateDate(){
        tvYear.setText(" "+mYear);
        tvMonth.setText(" "+mMonth);
        tvDay.setText(" "+mDay);
    }
    private void updateTime(){
        tvHour.setText(" "+mHour);
        tvMinute.setText(" "+mMinute);
    }
}
