package com.cookandroid.mom.home;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cookandroid.mom.MainActivity;
import com.cookandroid.mom.R;
import com.cookandroid.mom.community.TimelineFragment1;
import com.cookandroid.mom.home.search.MtSearchActivity;
import com.cookandroid.mom.mt.MtDetailInfoActivity;
import com.cookandroid.mom.util.MountainManager;
import com.cookandroid.mom.util.User;
import com.cookandroid.mom.util.Util;
import com.cookandroid.mom.util.User.Schedule;
import com.cookandroid.mom.util.User.ScheduleList;
import com.cookandroid.mom.util.Community.Notice;
import com.cookandroid.mom.util.Community.NoticeAdapter;
import com.cookandroid.mom.util.Community.NoticeClickListener;
import com.github.clans.fab.FloatingActionMenu;
import android.support.design.widget.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class HomeFragment extends Fragment {
    private String TAG = Util.tagHeader+"HomeFragment";

    private ArrayList<MountainManager.TodayMountain> todayMts;
    ArrayList<View> toMtViews;

    private ViewPager todayMtPager ;
    private LinearLayout dots;

    private ListView listNotice, listMySchedule;
    private ArrayList<Notice> noticeData;
    private ArrayList<Schedule> scheduleData;
    private NoticeAdapter noticeAdapter;
    private ScheduleList scheduleAdapter;

    private ImageView todayMtPic;
    private TextView todayMtName;
    private Button mtSearch;

    TextView statusNotice, statusSchedule;

    FloatingActionButton btnManageHome;

    private final int MY_PERMISSION_LOCATION = 1;
    private String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};



    public HomeFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*
        MainActivity -> HomeFragment가 보여줘야할 항목
        1.산 찾기 버튼
        2.오늘의 산
        3.공지사항 리스트
        4.오늘스케쥴
        * */
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        //1.산찾기 버튼

        mtSearch = (Button)view.findViewById(R.id.sanSearch);
        mtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Util.user.getId() == null) {
                    Toast.makeText(getContext(), "접근이 불가능합니다. 로그인을 해주세요.", Toast.LENGTH_SHORT).show();
                }else {
                    final String[] arrDistance = new String[]{"3km", "5km", "10km", "15km"};

                    AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
                    dlg.setTitle("원하는 범위를 선택하세요.");
                    //dlg.setIcon(R.drawable.ic_stop_white_24dp);
                    dlg.setItems(arrDistance, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            switch (i) {
                                case 0:
                                    distanceChoice(3, 5);
                                    break;
                                case 1:
                                    distanceChoice(5, 5);
                                    break;
                                case 2:
                                    distanceChoice(10, 6);
                                    break;
                                case 3:
                                    distanceChoice(15, 7);
                                    break;
                                default:
                                    break;
                            }
                        }

                        private void distanceChoice(int km, int zoom) {
                            Intent intent = new Intent(getActivity(), MtSearchActivity.class);
                            intent.putExtra("km", km);
                            intent.putExtra("zoom", zoom);
                            startActivity(intent);
                        }
                    });
                    dlg.setNegativeButton("닫기", null);
                    dlg.show();
                }
            }
        });

        //로그인 버튼에 로그인상황에따라 텍스트와 클릭이벤트를 할당하는 코드
        // ** 프래그먼트는 다음에 탭이 눌릴 것이라 가정하는 상황에서 로드되므로(ex:2번째 프래그먼트에서 오른쪽슬라이드로 넘길때 0번째 프래그먼트가 다음에 보여질 것이라 예상하여 프래그먼트를 새로 생성하여 로드)
        // 로그인정보를 확인하여 할당한다.

        //2.오늘의 산을 할당하는 코드(현재는 고정, 나중에 운영자 관리페이지 구현시 상세구현)
        todayMtPager = (ViewPager)view.findViewById(R.id.vpTodayMts);
        dots = (LinearLayout)view.findViewById(R.id.dots);
        Drawable drawable = getResources().getDrawable(R.drawable.dot);

        RelativeLayout.LayoutParams dotParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        final int width = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        final int height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        dotParams.width = width;
        dotParams.height = height;
        dotParams.setMargins(0,0,width,0);



        try{
            todayMts = Util.mtManager.getTodayMt();
            toMtViews = new ArrayList();

            int i=0;
            //isshow 갯수만큼 반복.
            for(MountainManager.TodayMountain mt : todayMts){
                //view 생성
                View mtView = inflater.inflate(R.layout.home_todaymt_view, null);
                ImageView todayPic = (ImageView)mtView.findViewById(R.id.todayMtimage);
                TextView todayTitle = (TextView)mtView.findViewById(R.id.todayMtName);

                todayPic.setImageBitmap(Util.getIFS().execute(mt.getPicture()).get());
                todayTitle.setText(mt.getTitle());

                View dotview = (View)inflater.inflate(R.layout.home_todaymt_dot, null);
                //ImageView dot = (ImageView)dotview.findViewById(R.id.dot);
                Drawable d = getResources().getDrawable(R.drawable.dot);
                ImageView dot = new ImageView(getContext());
                dot.setImageDrawable(d);
                dot.setLayoutParams(dotParams);

                dot.setTag(i++);
                dots.addView(dot);

                mtView.setTag(mt);
                toMtViews.add(mtView);
            }

            todayMtPager.setAdapter(new pagerAdapter(getContext()));

            todayMtPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    int i =  0;
                    while(i<todayMts.size()){
                        if(position==i)
                            dots.findViewWithTag(i).setSelected(true);
                        else
                            dots.findViewWithTag(i).setSelected(false);
                        i++;
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            dots.findViewWithTag(0).setSelected(true);
            todayMtPager.setCurrentItem(0);
        }catch (Exception e){
            e.printStackTrace();
        }


        /* 3. 리스트뷰에 공지사항, 오늘의 스케쥴 데이터를 할당하는 코드 */
        /*
        1.레이아웃에 할당될 데이터를 표현할 Data클래스를 정의하고 생성한다.(여기선 CData)
        2.각 리스트의 항목이 되는 레이아웃을 inflate하여 데이터를 할당시키는 ArrayAdapter클래스를 구현한다.
        3.Data객체를 생성하고 서버에서 얻어온 데이터를 할당하여 ArrayAdapter에 추가한다.
        * */

        statusSchedule = (TextView)view.findViewById(R.id.statusSchedule);
        statusNotice = (TextView)view.findViewById(R.id.statusNotice);

        listNotice = (ListView) view.findViewById(R.id.listNotice);
        listMySchedule = (ListView) view.findViewById(R.id.listMySchedule);

        // 리스트에 추가할 항목들을 ArrayList객체로 생성.
        noticeData = new ArrayList<Notice>();
        scheduleData = new ArrayList<Schedule>();

        //공지사항 데이터 받아오기.
        noticeData = Util.community.getNotices();
        //스케쥴 데이터 받아오기.
        scheduleData = Util.user.getDaySchedule(Calendar.getInstance().getTimeInMillis());


        //어댑터 할당.
        noticeAdapter = new NoticeAdapter(getActivity(), noticeData, new NoticeClickListener() {
            @Override
            public void onClick(View view) {
                Notice notice = (Notice)view.getTag();
                Intent intent = new Intent(getActivity(),NoticeActivity.class);
                intent.putExtra("data", notice);
                startActivity(intent);
            }
        });
        scheduleAdapter = new ScheduleList(getActivity(), scheduleData,new User.OnSchChangeListener(){
            @Override
            public void onDelete(long[] s) {
                if(scheduleAdapter.isEmpty()){
                    statusSchedule.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onUpdate(Schedule sch) {

            }
        });

        // 리스트뷰에 어댑터 연결
        listNotice.setAdapter(noticeAdapter);
        listMySchedule.setAdapter(scheduleAdapter);


        //공지사항 상태에 따라서 메세지 출력.
        if (noticeAdapter.isEmpty()) {
            statusNotice.setText("등록된 공지사항이 없습니다.");
        } else {
            listNotice.setVisibility(View.VISIBLE);
            statusNotice.setVisibility(View.INVISIBLE);
        }

        //로그인 상태에 따라서 메세지 출력.
        if(Util.user.getId() != null) {
            if (scheduleAdapter.isEmpty()) {
                statusSchedule.setVisibility(View.VISIBLE);
            } else {
                statusSchedule.setVisibility(View.INVISIBLE);
            }
        }else{
            statusSchedule.setText("로그인 해주세요");
            statusSchedule.setVisibility(View.VISIBLE);
        }

        // 관리자권한 있는 유저의 관리자 관리페이지 연결 버튼 활성화
        btnManageHome = (FloatingActionButton) view.findViewById(R.id.btnManageHome);

        btnManageHome.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent(getActivity(), ManageActivity.class);
                startActivity(intent);
            }
        });


        if(Util.user.isAdmin()){
            btnManageHome.setVisibility(View.VISIBLE);
        }else{
            btnManageHome.setVisibility(View.GONE);
        }

        return view;
    }

    private class pagerAdapter extends PagerAdapter{

        private LayoutInflater mInflater;

        public pagerAdapter(Context c){
            super();
            mInflater = LayoutInflater.from(c);
        }
        @Override
        public int getCount() {
            return todayMts.size();
        }

        @Override
        public Object instantiateItem(View pager, int position) {
            View v = toMtViews.get(position);

            ((ViewPager)pager).addView(v, 0);

            return v;
        }

        @Override
        public void destroyItem(View pager, int position, Object view) {
            ((ViewPager)pager).removeView((View)view);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

    }

    public void refresh(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(HomeFragment.this).attach(HomeFragment.this).commit();
            }
        }, 1000);
    }

    public void checkPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            int result;
            List<String> permissionList = new ArrayList<>();
            for(String pm : permissions){
                result = ContextCompat.checkSelfPermission(getActivity(), pm);
                if(result != PackageManager.PERMISSION_GRANTED){
                    permissionList.add(pm);
                }
            }
            if(!permissionList.isEmpty()){
                ActivityCompat.requestPermissions(getActivity(), permissionList.toArray(new String[permissionList.size()]), MY_PERMISSION_LOCATION);
            }
        } else {
            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if ((ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)) || (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION))) {
                    Log.e("2", "");
                    new AlertDialog.Builder(getActivity())
                            .setTitle("알림")
                            .setMessage("위치 권한이 거부 되어있습니다. 사용을 원하시면 설정에서 해당 권한을 허용해 주세요.")
                            .setNeutralButton("설정", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                                    startActivity(intent);
                                }
                            })
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    getActivity().finish();
                                }
                            })
                            .setCancelable(false)
                            .create()
                            .show();
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_LOCATION);
                }
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            switch (requestCode) {
                case MY_PERMISSION_LOCATION: {
                    if (grantResults.length > 0) {
                        for (int i = 0; i < permissions.length; i++) {
                            if (permissions[i].equals(this.permissions[0])) {
                                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                    showNoPermissionToastAndFinish();
                                }
                            }else if(permissions[i].equals(this.permissions[1])){
                                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                    showNoPermissionToastAndFinish();
                                }
                            }
                        }
                    } else {
                        showNoPermissionToastAndFinish();
                    }
                    return;
                }
            }
        }else {
            switch (requestCode) {
                case MY_PERMISSION_LOCATION: {
                    for (int i = 0; i < grantResults.length; i++) {
                        //grantResults[] : 허용된 권한은 0, 거부한 권한은 -1
                        if (grantResults[i] < 0) {
                            Toast.makeText(getActivity(), "해당 권한을 활성화 하셔야 합니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    //허용했다면 이부분에서
                    break;
                }
            }
        }
    }

    private void showNoPermissionToastAndFinish(){
        Toast.makeText(getActivity(), "권한 요청에 동의 해주셔야 이용 가능합니다. 설정에서 권한 허용 하시기 바랍니다", Toast.LENGTH_SHORT).show();
        new AlertDialog.Builder(getActivity())
                .setTitle("알림")
                .setMessage("위치 권한이 거부되었습니다. 사용을 원하신다면 설정에서 해당 권한을 직접 허용하셔야합니다.")
                .setNeutralButton("설정", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i){
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:"+getActivity().getPackageName()));
                        startActivity(intent);
                    }
                })
                .setPositiveButton("확인", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i){
                        getActivity().finish();
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }
}



