package com.cookandroid.mom.my;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cookandroid.mom.MainActivity;
import com.cookandroid.mom.R;
import com.cookandroid.mom.community.TimelineFragment1;
import com.cookandroid.mom.home.HomeFragment;
import com.cookandroid.mom.home.HomeFragment;
import com.cookandroid.mom.util.DataStringFormat;
import com.cookandroid.mom.util.EventDecorator;
import com.cookandroid.mom.util.User;
import com.cookandroid.mom.util.User.Schedule;
import com.cookandroid.mom.util.Util;
import com.cookandroid.mom.util.User.ScheduleList;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by Shining on 2017-09-02.
 */

public class MySchduleFragment3 extends Fragment {
    private String TAG = Util.tagHeader+"MyScheduleFramgent3";
    LayoutInflater popupinflater = null;

    MaterialCalendarView schCalendar = null;
    ListView schDate = null;
    TextView status = null;
    Button schInsert = null;

    Calendar date = null;

    ArrayList<ArrayList> schDays=null;
    ArrayList<Schedule> daySchs=null;

    ScheduleList schAdapter = null;

    User.OnSchChangeListener delete = new User.OnSchChangeListener(){
        public void onDelete(long s[]){
            Toast.makeText(getContext(), "삭제하였습니다.", Toast.LENGTH_SHORT).show();
            MyFragment parent = (MyFragment) getParentFragment();
            Calendar date = Calendar.getInstance();
            //등록한 스케쥴의 날짜
            date.setTimeInMillis(s[1]);
            //Fragment 갈아끼우기
            parent.setChildFragment(MySchduleFragment3.newInstance(date), getFragmentManager());

            Util.main.homeFragment.refresh();


        }
        public void onUpdate(Schedule sch){
            String schId = sch.getSchId();
            MySchduleFragment3.ScheduleDialogPopupListener popup =new MySchduleFragment3.ScheduleDialogPopupListener();
            popup.setSchedule(sch);
            popup.onClick(new View(getContext()));



            /*
            1. 클릭된 view에 적용된 data를 알아낸다. <= 이걸 어떻게 얻어오지;; <= View.setTag()이용함.
            2. schId를 호출한다
            3. 서버에선 schId로 조회하여 갱신하는 코드를짠다.
            DB쓴다
            스케쥴을 수정한다
            스케쥴을 수정한걸 서버에 보낸다
            4.프래그먼트 갱신

            * */
        }
    };




    public MySchduleFragment3(){
    }

    public static MySchduleFragment3 newInstance(){
        return new MySchduleFragment3();
    }
    public static MySchduleFragment3 newInstance(Calendar cal){
        MySchduleFragment3 newcal = new MySchduleFragment3();
        newcal.setToday(cal);
        return newcal;
    }

    public void setToday(Calendar cal){
        date=cal;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_3schedule, container, false);

        popupinflater = getActivity().getLayoutInflater();

        schCalendar = (MaterialCalendarView)view.findViewById(R.id.shcCalendar);
        status = (TextView)view.findViewById(R.id.txtSchStatus);
        schInsert = (Button)view.findViewById(R.id.schInsert);


        //calendar 기본셋팅
        //글씨크기 키우기
        schCalendar.setHeaderTextAppearance(R.style.TextAppearance_AppCompat_Large);
        schCalendar.setDateTextAppearance(R.style.TextAppearance_AppCompat_Medium);
        schCalendar.setWeekDayTextAppearance(R.style.TextAppearance_AppCompat_Medium);
        schDate = (ListView)view.findViewById(R.id.shcDate);


        //날짜 선택하기
        if(date == null){date = Calendar.getInstance();}
        schCalendar.setCurrentDate(date);
        schCalendar.setSelectedDate(CalendarDay.from(date));

        ArrayList<ArrayList> datas = Util.user.getCalendarInitData(date);
        schDays = datas.get(0);
        daySchs = datas.get(1);

        //날짜를 터치했을때 발생하는 이벤트 설정
        //날짜정보(CalendarDay),
        schCalendar.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                ArrayList<Schedule> schs = Util.user.getDaySchedule(date.getDate().getTime());
                if(schs.isEmpty()){
                    status.setVisibility(View.VISIBLE);
                }else{
                    status.setVisibility(View.INVISIBLE);
                }
                schDate.setAdapter(new ScheduleList(getContext(),schs,delete));
            }
        });

        //스케쥴이 있는날 캘린더에 표시하기
        //중요도 범주에 해당하는 색깔의 데코레이터를 해당날짜 리스트를 넘겨줘서 각각 적용시킨다.
        schCalendar.addDecorator(new EventDecorator(User.IMPORTANCE_COLOR_LOW, (List)schDays.get(User.IMPORTANCE_LOW))); //중요도 1,2인 날짜들
        schCalendar.addDecorator(new EventDecorator(User.IMPORTANCE_COLOR_MIDDLE, (List)schDays.get(User.IMPORTANCE_MIDDLE))); //중요도 3인 날짜들
        schCalendar.addDecorator(new EventDecorator(User.IMPORTANCE_COLOR_HIGH, (List)schDays.get(User.IMPORTANCE_HIGH))); //중요도 4인 날짜들
        schCalendar.addDecorator(new EventDecorator(User.IMPORTANCE_COLOR_CREW,(List)schDays.get(User.IMPORTANCE_CREW))); // 크루 날짜.

        /*
        오늘 스케쥴리스트 출력
        * */
        schAdapter = new ScheduleList(getContext(),daySchs,delete);

        schDate.setAdapter(schAdapter);
        //오늘스케쥴이없으면 스케쥴 없다고 알리는 텍스트뷰 활성화
        if(daySchs.isEmpty()){
            status.setVisibility(View.VISIBLE);
        }else{
            status.setVisibility(View.INVISIBLE);
        }

        /*
        스케쥴 입력 버튼 이벤트 설정
         */

        schInsert.setOnClickListener(new ScheduleDialogPopupListener());

        return view;
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        schCalendar.setSelectedDate(CalendarDay.from(date));
        Log.e(TAG, "onResume: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(TAG, "onDestroyView: ");
    }

    class ScheduleDialogPopupListener implements View.OnClickListener {

        NumberPicker npYear, npMonth, npDay, npTime, npMinute;
        Spinner spArea, spMts;
        RatingBar rtLevel;LayerDrawable stars;
        ImageButton btnCal, btnClock;
        EditText editSchMemo;
        String[][] mountain;

        //string
        String btn_ok="등록";
        String btn_cancle="취소";
        String alert = "스케쥴을 등록하였습니다";

        //일정등록/수정 dialog를 재활용하기 위해서 각 상황에 맞게 수정해야만 하는 값들 및, 기본값 할당.
        //setScheduleData()로 재할당할 수 있고, onClick시 할당된 값들에 따라서 설정한 값이 셋팅된 다이얼로그 출력.

        float ratingValue = 1f; // RatingBar 설정값.
        Calendar cal = schCalendar.getSelectedDate().getCalendar(); // 기본값 : 일정등록 프래그먼트의 CalendarView
        Schedule sch = null;

        SimpleDateFormat format = Util.user.schFormat;

        void setSchedule(Schedule sch){
            this.sch = sch;
        }

        void setScheduleData(){
            try {
                ratingValue = (float)sch.getImportance();
                cal.setTime(format.parse(sch.getTime()));
                btn_ok = "완료";
                alert = "스케쥴을 수정하였습니다";
            }catch(Exception e){
                e.printStackTrace();
            }
        }


    public void onClick(View view) {
        View popupview = popupinflater.inflate(R.layout.my_popup_schedule_insert, null);

        //일정등록/수정 dialog의 구성 View 초기화 및 리스너 할당

        int situdation = Util.INSERT;

        npYear = (NumberPicker) popupview.findViewById(R.id.npYear);
        npMonth = (NumberPicker) popupview.findViewById(R.id.npMonth);
        npDay = (NumberPicker) popupview.findViewById(R.id.npDay);

        npTime = (NumberPicker) popupview.findViewById(R.id.npTime);
        npMinute = (NumberPicker) popupview.findViewById(R.id.npMinute);

        spArea = (Spinner) popupview.findViewById(R.id.spArea);
        spMts = (Spinner) popupview.findViewById(R.id.spMts);

        btnCal = (ImageButton) popupview.findViewById(R.id.btnCal);
        btnClock = (ImageButton) popupview.findViewById(R.id.btnClock);

        rtLevel = (RatingBar)popupview.findViewById(R.id.rtLevel);

        editSchMemo = (EditText) popupview.findViewById(R.id.editSchMemo);

        //스케쥴 데이터가 있는경우(수정버튼을 눌렀을경우, ScheduleDialogPopupListener를 생성하고 setSchdule을 생성함.)
        if(sch!=null){
            situdation = Util.UPDATE;
            setScheduleData();
        }else{
            sch = new Schedule();
            cal = schCalendar.getSelectedDate().getCalendar();
            Calendar now = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY));
            cal.set(Calendar.MINUTE, now.get(Calendar.MINUTE));

        }

        initYearMonthDay();
        initTime();
        initAreaMts();

        btnCal.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                                                    /*Calendar dialog*/

                showDatePickerDialog(
                        getContext(),
                        CalendarDay.from(npYear.getValue(), npMonth.getValue()-1, npDay.getValue()),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                                                      /* calendar dialog에서 확인버튼을 눌렀을때 발생하는 이벤트. */
                                // 달력에서 선택한 년,월,일이 각각 year, monthOfYear(0부터시작하는 값), dayOfMonth
                                npYear.setValue(year);
                                npMonth.setValue(monthOfYear+1);
                                setDay(dayOfMonth);
                            }
                        });
            }
        });

        btnClock.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                                                        /*Clock dialog*/
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, npTime.getValue());
                cal.set(Calendar.MINUTE, npMinute.getValue());
                showTimePickerDialog(getContext(),
                        cal,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hour, int minute) {
                                npTime.setValue(hour);
                                npMinute.setValue(minute);
                            }
                        });
            }
        });

        rtLevel.setRating(ratingValue); // setRatingValue()

        //별 색깔 초기화
        //아마도 View의 색깔, 모양을 담당하는 클래스인듯함.
        stars = (LayerDrawable) rtLevel.getProgressDrawable();
        //애니메이션색깔(없게함)
        stars.getDrawable(0).setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_ATOP);
        //별 back 색깔(PorterDuff 정체 모름)
        stars.getDrawable(1).setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_ATOP);
        //front 색깔 : 초기색깔
        //front 색깔
        setStarColor((int)rtLevel.getRating());


        rtLevel.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                //front 색깔
                setStarColor((int)rating);
            }
        });

        editSchMemo.setText(sch!=null?sch.getContent():"");


        final int s = situdation;
        AlertDialog.Builder schDialog = new AlertDialog.Builder(getContext());

        //dialog띄우기.
       AlertDialog dialog =  schDialog
                .setView(popupview)
                .setPositiveButton(btn_ok, new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        //등록일때 schId 필요없음
                        //수정일때 schId 필요함.

                        Calendar schDate = Calendar.getInstance();
                        schDate.set(npYear.getValue(), npMonth.getValue()-1, npDay.getValue(), npTime.getValue(), npMinute.getValue());
                        Log.e(TAG, "onClick: "+DataStringFormat.y_mon_d_h_m.format(schDate.getTime()) );



                        String time  = format.format(schDate.getTime());
                        Log.e(TAG, "onClick: "+time );
                        int importance = (int)rtLevel.getRating();
                        String content  = editSchMemo.getText().toString();
                        String mtName = (String)spMts.getSelectedItem();

                        /*
                        Importance
                        Time : String : yyyy-MM-dd hh:mm
                        mName
                        Content
                        ---------등록이든 수정이든 다 있는거 ------------
                        AreaCat
                        schId
                        ------- 수정할때만 있는거 ----------
                        * */
                        sch.setTime(time);
                        sch.setContent(content);
                        sch.setMname(mtName);
                        sch.setImportance(importance);

                        long[] s = Util.user.updateSch(sch);

                        if((s != null)&&s[0]==1){
                            Toast.makeText(getContext(), alert, Toast.LENGTH_SHORT).show();
                            MyFragment parent = (MyFragment)getParentFragment();
                            Calendar date = Calendar.getInstance();
                            //등록한 스케쥴의 날짜
                            date.setTimeInMillis(s[1]);
                            //Fragment 갈아끼우기
                            parent.setChildFragment(MySchduleFragment3.newInstance(date), getFragmentManager());

                            sch = null;

                        }else{
                            Toast.makeText(getContext(), "스케쥴등록에 실패하였습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(s){
                            case Util.INSERT:{
                                sch = null;
                                break;
                            }
                            case Util.UPDATE:{
                                break;
                            }
                        }
                    }
                })
                .show();

        setButtonSize(dialog);
    }

    public void initYearMonthDay() {
        npYear.setMinValue(1970);
        npYear.setMaxValue(2100);
        npYear.setValue(cal.get(Calendar.YEAR));

        npMonth.setMinValue(1);
        npMonth.setMaxValue(12);
        npMonth.setValue(cal.get(Calendar.MONTH) + 1);

        setDay(cal.get(Calendar.DATE));

        NumberPicker.OnValueChangeListener listener = new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                setDay(npDay.getValue());
            }
        };
        npYear.setOnValueChangedListener(listener);
        npMonth.setOnValueChangedListener(listener);
    }

    public void setDay(int date) {
        Calendar selectCal = Calendar.getInstance();
        //현재 선택된 년/월 1일의 Calendar생성
        selectCal.set(npYear.getValue(), npMonth.getValue() - 1, 1);
        int maxDate = selectCal.getActualMaximum(Calendar.DAY_OF_MONTH);

        npDay.setMinValue(1);
        npDay.setMaxValue(maxDate);
        if(date>maxDate){date=maxDate;}
        npDay.setValue(date);
    }

    public void initTime() {
        npTime.setMinValue(0);
        npTime.setMaxValue(23);

        npMinute.setMinValue(0);
        npMinute.setMaxValue(59);

        npTime.setValue(cal.get(Calendar.HOUR_OF_DAY));
        Log.e(TAG, "initTime: "+DataStringFormat.y_mon_d_h_m.format(cal.getTime()));
        Log.e(TAG, "initTime: "+npTime.getValue() );
        npMinute.setValue(cal.get(Calendar.MINUTE));
        Log.e(TAG, "initTime: "+npMinute.getValue() );
    }

    //산,지역 Spinner 초기화
    public void initAreaMts() {
        //나중엔 산목록 전부 받아온상태에서 지역으로 필터링하는 방식으로 바꾸는게 좋을듯.

        spArea.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.spinner_item, Util.mtManager.category));
        if(sch == null) {
            setSpinMts(Util.mtManager.cat_areasStr.get(Util.mtManager.category[0]));
        }else{
            String areacat = sch.getAreaCat();
            for(int i=0; i<Util.mtManager.category.length; i++){
                if(Util.mtManager.category[i].equals(areacat)){
                    spArea.setSelection(i);
                    break;
                }
            }
            setSpinMts(Util.mtManager.cat_areasStr.get(areacat));
        }


        spArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                String area = (String)spArea.getSelectedItem();
                Log.e(TAG,"지역:"+"" + spArea.getSelectedItemPosition()+" : "+area);

                area = Util.mtManager.cat_areasStr.get(Util.mtManager.category[position]);

                setSpinMts(area);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
    }

    public void setSpinMts(String area){
        try {
            mountain = Util.mtManager.getAreaMts(-1, area);
            String[] mName = mountain[1];
            ArrayAdapter adapter2 = new ArrayAdapter(getContext(), R.layout.spinner_item, mName);
            //adapter2.setDropDownViewResource(R.layout.spin_dropdown);

            spMts.setAdapter(adapter2);
            if(sch!=null){
                Log.e(TAG,"setSpinMts"+" enter the setMts");
                String mtname = sch.getMname();
                for(int i = 0 ; i<mName.length; i++){
                    if(mName[i].equals(mtname)){
                        spMts.setSelection(i);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showDatePickerDialog(Context context, CalendarDay day,
                                     DatePickerDialog.OnDateSetListener callback) {
        if (day == null) {
            day = CalendarDay.today();
        }
        DatePickerDialog dialog = new DatePickerDialog(
                context, 0, callback, day.getYear(), day.getMonth(), day.getDay()
        );
        dialog.show();
    }

    public void showTimePickerDialog(Context context, Calendar day,
                                     TimePickerDialog.OnTimeSetListener callback) {
        if (day == null) {
            day = Calendar.getInstance();
        }
        TimePickerDialog dialog = new TimePickerDialog(
                context, callback, day.get(Calendar.HOUR_OF_DAY), day.get(Calendar.MINUTE), true
        );
        dialog.show();
    }

        void setStarColor(int number) {
            switch (number) {
                case 1:
                case 2:
                    stars.getDrawable(2).setColorFilter(User.IMPORTANCE_COLOR_LOW, PorterDuff.Mode.SRC_ATOP);
                    break;
                case 3:
                    //Orange
                    stars.getDrawable(2).setColorFilter(User.IMPORTANCE_COLOR_MIDDLE, PorterDuff.Mode.SRC_ATOP);
                    break;
                case 4:
                    stars.getDrawable(2).setColorFilter(User.IMPORTANCE_COLOR_HIGH, PorterDuff.Mode.SRC_ATOP);
                    break;

            }
        }
    }

    private void setButtonSize(AlertDialog dialog) {
        Button ok = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button cancle = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        int height = getContext().getResources().getDimensionPixelSize(R.dimen.alertdialog_button_height);
        int width  = getContext().getResources().getDimensionPixelSize(R.dimen.alertdialog_button_width);

        ok.setHeight(height);
        ok.setWidth(width);
        ok.setTextSize(TypedValue.COMPLEX_UNIT_DIP,35f);
        ok.setTextColor(Color.rgb(89, 244, 66));

        cancle.setHeight(height);
        cancle.setWidth(width);
        cancle.setTextSize(TypedValue.COMPLEX_UNIT_DIP,35f);
        cancle.setTextColor(Color.rgb(69, 220, 66));
    }



}
