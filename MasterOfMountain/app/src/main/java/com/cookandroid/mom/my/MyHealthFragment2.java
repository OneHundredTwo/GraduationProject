package com.cookandroid.mom.my;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cookandroid.mom.R;
import com.cookandroid.mom.home.search.MtSearchActivity;
import com.cookandroid.mom.util.Util;
import com.cookandroid.mom.util.User.ExerciseItem;
import com.github.clans.fab.FloatingActionMenu;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Shining on 2017-10-19.
 */

public class MyHealthFragment2 extends Fragment {

    ListView health_list = null;

    TextView startTime,endTime;
    Calendar start,end;

    ImageButton startBtn, endBtn;
    Button search;

    TextView info;

    HealthList list = null;
    SwipeRefreshLayout refresh;

    private FloatingActionMenu menuRed;
    private com.github.clans.fab.FloatingActionButton fab1, fab2;

    //검색한경우의 스크롤락
    boolean searching = false;
    boolean lockListView = false;
    int rownum = 0;

    SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");

    class HealthList extends ArrayAdapter<ExerciseItem> {


        public HealthList(Context context, ArrayList<ExerciseItem> list) {
            super(context, 0, list);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = null;
            if (convertView != null) {
                view = convertView;
            } else {
                view = getActivity().getLayoutInflater().inflate(R.layout.my_list_exercise_item, null);
            }
            final ExerciseItem data = this.getItem(position);
            view.setTag(data);

            Bitmap image = null;

            //prefix : exList_it_
            ImageView img = (ImageView)view.findViewById(R.id.exList_it_img);
            TextView day = (TextView)view.findViewById(R.id.exList_it_day);
            TextView time = (TextView)view.findViewById(R.id.exList_it_time);
            TextView location = (TextView)view.findViewById(R.id.exList_it_location);

            img.setImageDrawable(getResources().getDrawable(R.drawable.mom_notify_icon));
            day.setText(data.getEx_day());
            time.setText(data.getTime());
            location.setText(data.getEx_location());
            /*try {
                image = Util.getIFS().execute(data.getEx_pic()).get();
                img.setImageBitmap(image);
            }catch(Exception e){
                e.printStackTrace();
            }*/

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final ExerciseItem data = (ExerciseItem)view.getTag();

                    AlertDialog.Builder schDialog = new AlertDialog.Builder(getActivity());

                    //운동결과화면을 다이얼로그로 만들고, 그걸 여기서 재사용하자.
                    View dialog_view = getActivity().getLayoutInflater().inflate(R.layout.health_popup_rersult, null);
                    View v = dialog_view;

                    TextView day, mtName, course, steps, distance, calories, extime;

                    day = (TextView)v.findViewById(R.id.exResult_day);
                    mtName = (TextView)v.findViewById(R.id.exResult_mtName);
                    steps = (TextView)v.findViewById(R.id.exResult_steps);
                    distance = (TextView)v.findViewById(R.id.exResult_distance);
                    calories = (TextView)v.findViewById(R.id.exResult_calories);
                    extime = (TextView)v.findViewById(R.id.exResult_extime);

                    day.setText(data.getEx_day());
                    mtName.setText(data.getEx_location());
                    steps.setText(data.getStep()+"steps");
                    distance.setText(data.getDistance()+"m");
                    calories.setText(data.getCalories()+"cal");
                    extime.setText(data.getEx_time());



                    schDialog
                            //.setIcon()
                            .setView(dialog_view)
                            .setPositiveButton("확인", new AlertDialog.OnClickListener(){
                                public void onClick(DialogInterface dialog, int which){


                                }
                            })
                            .setNegativeButton("삭제", new AlertDialog.OnClickListener(){
                                public void onClick(DialogInterface dialog, int which){
                                    Log.e("dfsdf", "onClick list item view : "+data.getEx_Id() );
                                    Util.user.deleteExData(data);
                                    list.remove(data);
                                }
                            })
                            .show();
                }
            });

            return view;
        }

        public void add(ArrayList<ExerciseItem> items){
            for(ExerciseItem item : items){
                this.add(item);
            }
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("ddd", "onResume: fragment onresume");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View health_list_fragment  = inflater.inflate(R.layout.my_list_exercise, container, false);
        View v = health_list_fragment;
        rownum = 0;

        //리스트뷰의 아이템을 관리할 어댑터 생성
        list = new HealthList(getActivity().getApplicationContext(), new ArrayList<ExerciseItem>());


        menuRed = (FloatingActionMenu) v.findViewById(R.id.menu_red);

        fab1 = (com.github.clans.fab.FloatingActionButton) v.findViewById(R.id.fab1);
        fab2 = (com.github.clans.fab.FloatingActionButton) v.findViewById(R.id.fab2);
        menuRed.setClosedOnTouchOutside(true);

        // 구성 View들 초기화
        health_list = (ListView)v.findViewById(R.id.exList_list);
        refresh = (SwipeRefreshLayout)v.findViewById(R.id.exList_refresh);

        startTime = (TextView)v.findViewById(R.id.exList_stTime);
        startBtn = (ImageButton)v.findViewById(R.id.exList_stBtn);

        endTime  = (TextView)v.findViewById(R.id.exList_endTime);
        endBtn = (ImageButton)v.findViewById(R.id.exList_endBtn);

        search = (Button)v.findViewById(R.id.exList_search);

        info = (TextView)v.findViewById(R.id.exList_info);

        end = Calendar.getInstance();
        start = Calendar.getInstance();
        start.add(Calendar.DAY_OF_YEAR,-7);

        startTime.setTag(start);
        endTime.setTag(end);

        startTime.setText(format.format(start.getTime()));
        endTime.setText(format.format(end.getTime()));

        startBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                showDatePickerDialog(
                        getActivity(),
                        startTime,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                /* calendar dialog에서 확인버튼을 눌렀을때 발생하는 이벤트. */
                                // 달력에서 선택한 년,월,일이 각각 year, monthOfYear(0부터시작하는 값), dayOfMonth
                                setCalendar(startTime,year,monthOfYear,dayOfMonth);
                            }
                        }
                );
            }
        });
        endBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                showDatePickerDialog(
                        getActivity(),
                        endTime,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                /* calendar dialog에서 확인버튼을 눌렀을때 발생하는 이벤트. */
                                // 달력에서 선택한 년,월,일이 각각 year, monthOfYear(0부터시작하는 값), dayOfMonth
                                setCalendar(endTime,year,monthOfYear,dayOfMonth);
                            }
                        }
                );
            }
        });

        search.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                setSearchingList();
                rownum = 0;
            }
        });
        fab1.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(Util.user.getId()==null){
                    Toast.makeText(getActivity(), "비회원은 접근할수 없습니다.", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(getActivity(), ExerciseActivity.class);
                    startActivity(intent);
                }
            }
        });

        fab2.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(Util.user.getId()==null){
                    Toast.makeText(getActivity(), "비회원은 접근할수 없습니다.", Toast.LENGTH_SHORT).show();
                }else {
                        Intent intent = new Intent(getActivity(), MtSearchActivity.class);
                        intent.putExtra("km", 3);
                        intent.putExtra("zoom", 5);
                        startActivity(intent);

                }
            }
        });

        /*
        리스트뷰 스크롤 이벤트 할당
        * */
        health_list.setAdapter(list);

        health_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(!searching) {
                    ListAdapter adapter = health_list.getAdapter();

                    rownum = (adapter.isEmpty()) ? 0 : adapter.getCount();
                    //스크롤이 맨 아래일경우
                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lockListView) {
                        try {
                            addItems();
                        } catch (Exception e) {
                            e.getStackTrace();
                        }
                    }
                }

            }
            @Override
            //스크롤 시
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                lockListView = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);



            }
        });

        //refresh listner 할당

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            public void onRefresh(){
                refresh.setRefreshing(true);
                if(!searching) {
                    rownum = 0;
                    list.clear();
                    try {
                        addItems();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                refresh.setRefreshing(false);
            }
        });

        // 리스트 아이템 초기화(일주일치 최근 운동기록 로드)
        info.setVisibility(View.INVISIBLE);
        //초기 모드 : 모두보기
        addItems();


        return health_list_fragment;

    }
    // 셋팅용 메소드
    void setCalendar(TextView txt, int year, int month, int day_of_month){
        Calendar cal = (Calendar)txt.getTag();

        int fields[] = {Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH};
        int values[] = {year, month, day_of_month};

        for(int i=0; i<fields.length; i++){
            cal.set(fields[i], values[i]);
        }

        txt.setText(format.format(cal.getTime()));

        checkOverAndSet();

    }
    void checkOverAndSet(){
        int over = end.get(Calendar.DAY_OF_YEAR) - start.get(Calendar.DAY_OF_YEAR);
        if(over < 0 ){
            start.add(Calendar.DAY_OF_YEAR,7);
            end.setTime(start.getTime());
            start.add(Calendar.DAY_OF_YEAR,-7);

            endTime.setText(format.format(end.getTime()));
        }
    }
    public void showDatePickerDialog(Context context, TextView txt,
                                     DatePickerDialog.OnDateSetListener callback){
        Calendar cal = (Calendar)txt.getTag();
        int year=cal.get(Calendar.YEAR);
        int month=cal.get(Calendar.MONTH);
        int day=cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog=new DatePickerDialog(
                context,0,callback,year, month, day
        );
        dialog.show();
    }
    public void setSearchingList(){
        //모두보기/검색 => 검색 : 이경우에 이전에 검색했던 리스트아이템들은 다 날려버리기.
        list.clear();

        //시간 인터벌을 넘겨주면, 시간순으로 내림차순 정렬된 ExerciseItem ArrayList 객체를 반환.
        ArrayList<ExerciseItem> items = Util.user.getExerciseItems(start.getTimeInMillis(), end.getTimeInMillis());
        list.add(items);
        health_list.setSelection(0);

        searching = true;

        //검색해온 아이템이 없으면 없다는 표시.
        if(items.size() == 0){
            info.setVisibility(View.VISIBLE);
        }else{
            info.setVisibility(View.INVISIBLE);
        }
    }

    public void addItems(){
        ArrayList<ExerciseItem> items = Util.user.getExerciseItems(rownum);

        //검색 => 모두보기 : 기존내용 삭제
        //모두보기 => 모두보기 : 기존내용에 덧붙이기
        if(searching == true){
            list.clear();
            searching = false;
        }

        list.add(items);
        rownum += 10;

        lockListView = false;

        if(list.getCount() == 0){
            info.setVisibility(View.VISIBLE);
        }else{
            info.setVisibility(View.INVISIBLE);
        }

    }

}


