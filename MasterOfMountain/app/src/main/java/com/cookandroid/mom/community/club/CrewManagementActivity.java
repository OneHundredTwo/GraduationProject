package com.cookandroid.mom.community.club;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.cookandroid.mom.R;
import com.cookandroid.mom.util.Util;

import java.util.ArrayList;


/**
 * Created by wdj46 on 2017-09-06.
 */

public class CrewManagementActivity extends ActionBarActivity{

    final Context context = this;
    private ListView listview;
    private ArrayList<CData> alist;
    private DataAdapter adapter;
    private String[][] crewInfo;
    private String[] planNum;
    private String[] crewID;
    private String[] planDate;
    private String[] prejoin;
    private String[] mNm;
    private String[] joinNum;
    private String[] clubNm;
    private Button Insertcrew;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community_crew_management);
        Toolbar toolbar = (Toolbar)findViewById(R.id.crewmanagement_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("크루 관리");
        Insertcrew = (Button)findViewById(R.id.insertCrew);
        listview = (ListView)findViewById(R.id.listView);
        alist = new ArrayList<CData>();
        adapter = new DataAdapter(this, alist);
        listview.setAdapter(adapter);

        try {
            getData();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Insertcrew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CrewManagementActivity.this, InsertCrewActivity.class);
        startActivity(intent);
    }
});
    }
    @Override
    public void onResume(){
        super.onResume();
        adapter.clear();
        alist = new ArrayList<>();
        adapter = new DataAdapter(this, alist);
        listview.setAdapter(adapter);
        try {
            getData();
        } catch (Exception e) {
            e.printStackTrace();
        }

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
                            .putExtra("submit","관리");
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

            crewInfo = Util.user.getCrew(0, Util.user.getId());
            planNum = crewInfo[0];
            crewID = crewInfo[1];
            prejoin = crewInfo[2];
            joinNum = crewInfo[3];
            mNm = crewInfo[4];
            planDate = crewInfo[5];
            clubNm = crewInfo[6];
            for (int i = 0; i < planNum.length; i++) {
                adapter.add(new CData(this, Integer.parseInt(planNum[i]), "크루등록자 : " + crewID[i], "참여인원 : "+prejoin[i]+"/"+joinNum[i],"산이름 :" +mNm[i],"크루 일정 : "+planDate[i],"동호회 이름 : "+clubNm[i]));
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