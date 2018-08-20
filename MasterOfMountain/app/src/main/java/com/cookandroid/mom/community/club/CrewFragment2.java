package com.cookandroid.mom.community.club;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cookandroid.mom.R;
import com.cookandroid.mom.util.Util;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;

/**
 * 커뮤니티 탭 크루모집 버튼 프래그먼트
 * */
public class CrewFragment2 extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private FloatingActionMenu menuRed;
    private com.github.clans.fab.FloatingActionButton fab1, fab2, fab3;
    private String[][] crewInfo;
    private String[] planNum;
    private String[] crewID;
    private String[] prejoin;
    private String[] joinNum;
    private String[] mNm;
    private String[] planDate;
    private String[] clubNm;

    private ListView listview;
    private ArrayList<CData> alist;
    private DataAdapter adapter;
    private SwipeRefreshLayout crewRefresh;

    public CrewFragment2(){
    }
    public static com.cookandroid.mom.community.club.CrewFragment2 newInstance(){
        return new com.cookandroid.mom.community.club.CrewFragment2();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.community_2crew_frag, container, false);


        //새로고침 레이아웃 선언
        crewRefresh = (SwipeRefreshLayout) view.findViewById(R.id.crewRefresh);

        // 선언한 리스트뷰에 사용할 리스뷰연결
        listview = (ListView) view.findViewById(R.id.listView);

        // 객체를 생성합니다
        alist = new ArrayList<CData>();

        // 데이터를 받기위해 데이터어댑터 객체 선언
        adapter = new DataAdapter(getActivity(), alist);

        // 리스트뷰에 어댑터 연결
        listview.setAdapter(adapter);
        getData();

        crewRefresh.setOnRefreshListener(this);

        menuRed = (FloatingActionMenu) view.findViewById(R.id.menu_red);

        fab1 = (com.github.clans.fab.FloatingActionButton) view.findViewById(R.id.fab1);
        fab2 = (com.github.clans.fab.FloatingActionButton) view.findViewById(R.id.fab2);
        fab3 = (com.github.clans.fab.FloatingActionButton) view.findViewById(R.id.fab3);
        menuRed.setClosedOnTouchOutside(true);

        fab1.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(Util.user.getId()==null){
                    Toast.makeText(getActivity(), "비회원은 접근할수 없습니다.", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(getActivity(), InsertClubActivity.class);
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
                    Intent intent = new Intent(getActivity(), ClubRegistryActivity.class);
                    startActivityForResult(intent, 0);
                }
            }
        });
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Util.user.getId()==null){
                    Toast.makeText(getActivity(), "비회원은 접근할수 없습니다.", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(getActivity(), ClubManagementActivity.class);
                    startActivity(intent);
                }
            }
        });

        //현재 사용하지 않는 버튼이라 hide 시킴킴
        final android.support.design.widget.FloatingActionButton fab = (android.support.design.widget.FloatingActionButton)view.findViewById(R.id.btnFloating);
        fab.setBackgroundColor(Color.BLUE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Util.user.getId()==null){
                    Toast.makeText(getActivity(), "비회원은 접근할수 없습니다.", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(getActivity(),CrewManagementActivity.class);
                    startActivityForResult(intent, 0);
                }
            }
        });

        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == SCROLL_STATE_FLING || scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    fab.hide();
                }else{
                    fab.show();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });


        return view;
    }

    public void onRefresh(){
        crewRefresh.setRefreshing(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(CrewFragment2.this).attach(CrewFragment2.this).commit();
                crewRefresh.setRefreshing(false);

            }
        }, 1000);
    }

    private class DataAdapter extends ArrayAdapter<CData> {
        // 레이아웃 XML을 읽어들이기 위한 객체
        private LayoutInflater mInflater;

        public DataAdapter(Context context, ArrayList<CData> object) {

            // 상위 클래스의 초기화 과정
            // context, 0, 자료구조
            super(context, 0, object);
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            View view = null;

            // 현재 리스트의 하나의 항목에 보일 컨트롤 얻기

            if (v == null) {

                // XML 레이아웃을 직접 읽어서 리스트뷰에 넣음
                view = mInflater.inflate(R.layout.community_list_crew, null);
            } else {

                view = v;
            }

            // 자료를 받는다.
            final CData data = this.getItem(position);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //<여기다 상세정보데이터 얻어오는 메소드>
                    // ArrayAdapter객체(여기선 adapter)의 getItem메소드로 선택된 리스트아이템의 position을 얻어서 해당 아이템(여기선 제너릭으로 CData)형식으로 형변환하여 가져옴.

                    Intent intent = new Intent(getContext(), CrewDetailActivity.class);
                    intent.putExtra("planNum",data.getC_Num())
                            .putExtra("submit","참여");
                    startActivity(intent);

                }
            });

            if (data != null) {
                // 화면 출력
                TextView crewID = (TextView) view.findViewById(R.id.crewID);
                TextView crewjoin = (TextView) view.findViewById(R.id.crewjoin);
                TextView crewDate = (TextView) view.findViewById(R.id.crewdate);
                TextView clubNm = (TextView)view.findViewById(R.id.crewClub);
                TextView mName = (TextView)view.findViewById(R.id.crewMt);

                clubNm.setText(data.getC_clubNm());
                crewID.setText(data.getC_name());
                crewjoin.setText(data.getC_join());
                crewDate.setText(data.getC_planDate());
                mName.setText(data.getC_mt());

                crewID.setTextColor(Color.BLACK);
                crewjoin.setTextColor(Color.GRAY);
                crewDate.setTextColor(Color.BLACK);
                clubNm.setTextColor(Color.BLACK);
                mName.setTextColor(Color.BLACK);



            }
            return view;
        }
    }
    public void getData(){
        try {

        crewInfo = Util.user.getCrew(1, Util.user.getId());
        planNum = crewInfo[0];
        crewID = crewInfo[1];
        prejoin = crewInfo[2];
        joinNum = crewInfo[3];
        mNm = crewInfo[4];
        planDate = crewInfo[5];
        clubNm = crewInfo[6];
        for (int i = 0; i < planNum.length; i++) {
            adapter.add(new CData(getContext(), Integer.parseInt(planNum[i]), "크루등록자 : " + crewID[i], "참여인원 : "+prejoin[i]+"/"+joinNum[i],"산이름 :" +mNm[i],"크루 일정 : "+planDate[i],"동호회 이름 : "+clubNm[i]));
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // CData안에 받은 값을 직접 할당
    class CData {
        private int c_Num;
        private String c_name, c_join, c_mt, c_planDate,c_clubNm;

        public CData(Context context, int nm, String name,
                     String join, String mt, String date, String club) {

            this.c_Num = nm;
            this.c_name = name;
            this.c_join = join;
            this.c_mt = mt;
            this.c_planDate = date;
            this.c_clubNm = club;

        }

        public int getC_Num() {
            return c_Num;
        }

        public String getC_name() {
            return c_name;
        }

        public String getC_join() {
            return c_join;
        }

        public String getC_mt() {
            return c_mt;
        }

        public String getC_planDate() {
            return c_planDate;
        }

        public String getC_clubNm() {
            return c_clubNm;
        }



  /*      public int getM_pic() {
            return m_pic;
        }

        public int getM_imageView() {
            return m_imageView;
        }*/
    }
}