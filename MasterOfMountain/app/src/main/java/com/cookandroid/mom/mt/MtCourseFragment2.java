package com.cookandroid.mom.mt;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cookandroid.mom.R;
import com.cookandroid.mom.mt.map.CourseItemMapActivity;
import com.cookandroid.mom.util.MountainManager;
import com.cookandroid.mom.util.Util;

import java.util.ArrayList;

/**
 * Created by user on 2017-06-07.
 */

public class MtCourseFragment2 extends Fragment {
    private String[][] courseInfo;
    private String[] arrNum, arrCourseNum, arrLatitude, arrLongitude, arrEmdCd, arrSec_len, arrUp_min, arrDown_min, arrStart_z, arrEnd_z, arrCat_nam;


    public MtCourseFragment2(){
    }

    private String mNum;
    private MountainManager.Mountain mountain;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mNum = MtDetailInfoActivity.mNum;
        mountain = MtDetailInfoActivity.mountain;
        //Log.e("CourseFragment mNum", mNum);
        try {
            courseInfo = Util.mtManager.getMtCourses(mNum);
            arrNum = courseInfo[0];
            arrCourseNum = courseInfo[1];
            arrLatitude = courseInfo[2];
            arrLongitude = courseInfo[3];
            arrEmdCd = courseInfo[4];
            arrSec_len = courseInfo[5];
            arrUp_min = courseInfo[6];
            arrDown_min = courseInfo[7];
            arrStart_z = courseInfo[8];
            arrEnd_z = courseInfo[9];
            arrCat_nam = courseInfo[10];
            /*arrLength = new String[courseInfos.size()];
            arrNum = new String[courseInfos.size()];
            arrContent = new String[courseInfos.size()];
            //0. 코스정보 넣을 테이블 만들기 혹은 그냥 API 쓰든가
            //1. 코스정보를 API에서 받아서 해당 산에다 넣기.
            //2. 코스정보를 순번, 내용으로 넣기
            for(int i=0; i<courseInfos.size(); i++) {
                String courseinfo = courseInfos.get(i);
                arrLength[i] = courseinfo.substring(courseinfo.lastIndexOf("(")+1,courseinfo.lastIndexOf(")"));
                arrNum[i] = ""+(i+1);
                arrContent[i] = courseinfo.substring(courseinfo.indexOf(" "),courseinfo.lastIndexOf("("));
            }*/



        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.mt_detail_2course_frag, container, false);

        ListView listview = (ListView)view.findViewById(R.id.list_course);


        ArrayList<CData> alist = new ArrayList<CData>();


        DataAdapter adapter = new DataAdapter(getActivity(), alist);

        listview.setAdapter(adapter);
        try {
            for (int i = 0; i < arrNum.length; i++) {
                adapter.add(new CData(getActivity().getApplicationContext(), arrCourseNum[i],
                         arrSec_len[i]+"m", arrUp_min[i]+"분", arrDown_min[i]+"분"
                        , arrStart_z[i]+"m", arrEnd_z[i]+"m"
                        , arrCat_nam[i], R.drawable.a));
            }
        }catch(NullPointerException e){
            Log.d("NullPointCheck ",courseInfo==null?"courseInfo null":"courseInfo not null");
            Log.d("NullPointCheck ",arrNum==null?"arrLength null":"arrLength not null");

        }

        return view;
    }
    private class DataAdapter extends ArrayAdapter<CData> {
        // 레이아웃 XML을 읽어들이기 위한 객체
        private LayoutInflater mInflater;

        public DataAdapter(Context context, ArrayList<CData> object) {

            // 상위 클래스의 초기화 과정
            // context, 0, 자료구조
            super(context, 0, object);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            View view = null;
            final int i = position;
            // 현재 리스트의 하나의 항목에 보일 컨트롤 얻기

            if (v == null) {

                // XML 레이아웃을 직접 읽어서 리스트뷰에 넣음
                view = mInflater.inflate(R.layout.mt_list_course, null);
            } else {
                view = v;
            }

            // 자료를 받는다.
            final CData data = this.getItem(position);

            if (data != null) {
                // 화면 출력
                TextView num = (TextView) view.findViewById(R.id.courseNum);
                TextView sec_len = (TextView) view.findViewById(R.id.sec_len);
                TextView up_min = (TextView) view.findViewById(R.id.up_min);
                TextView down_min = (TextView) view.findViewById(R.id.down_min);
                /*TextView start_z = (TextView) view.findViewById(R.id.start_z);
                TextView end_z = (TextView) view.findViewById(R.id.end_z);*/
                TextView cat_nam = (TextView) view.findViewById(R.id.cat_nam);
                num.setText(data.getmNum());
                sec_len.setText(data.getmSec_len());
                up_min.setText(data.getmUp_min());
                down_min.setText(data.getmDown_min());
               /* start_z.setText(data.getmStart_z());
                end_z.setText(data.getmEnd_z());*/
                cat_nam.setText(data.getmCat_nam());
                num.setTextColor(Color.BLACK);
                sec_len.setTextColor(Color.BLACK);
                up_min.setTextColor(Color.BLACK);
                down_min.setTextColor(Color.BLACK);
                cat_nam.setTextColor(Color.BLACK);
                RelativeLayout btnCourseView = (RelativeLayout) view.findViewById(R.id.btnCourseView);
                btnCourseView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(getActivity(), "코스 보기 버튼 눌림", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), CourseItemMapActivity.class);
                        intent.putExtra("mLatitude", arrLatitude[i]);
                        intent.putExtra("mLongitude", arrLongitude[i]);
                        startActivity(intent);
                    }
                });

            }

            return view;

        }

    }

    // CData안에 받은 값을 직접 할당
    class CData {

        private String mNum, mSec_len, mUp_min, mDown_min, mStart_z, mEnd_z, mCat_nam;
        private int m_courseMap;

        public CData(Context context, String mNum, String mSec_len,
                     String mUp_min, String mDown_min, String mStart_z, String mEnd_z,String mCat_nam,int pic) {

            this.mNum = mNum;
            this.mSec_len = mSec_len;
            this.mUp_min = mUp_min;
            this.mDown_min = mDown_min;
            this.mStart_z = mStart_z;
            this.mEnd_z = mEnd_z;
            this.mCat_nam = mCat_nam;
            m_courseMap = pic;

        }

        public String getmNum() {
            return mNum;
        }

        public String getmSec_len() {
            return mSec_len;
        }

        public String getmUp_min() {
            return mUp_min;
        }

        public String getmDown_min() {
            return mDown_min;
        }

        public String getmStart_z() {
            return mStart_z;
        }

        public String getmEnd_z() {
            return mEnd_z;
        }

        public String getmCat_nam() {
            return mCat_nam;
        }

        public int getM_courseMap() {
            return m_courseMap;
        }
    }

    /*class jspfile extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String URL = "http://192.168.0.90:10000/MountainInformationList.jsp";

            DefaultHttpClient client = new DefaultHttpClient();

            String result = "";
            try {
                HttpPost post = new HttpPost(URL);


			*//* 데이터 보낸 뒤 서버에서 데이터를 받아오는 과정 *//*

                HttpResponse response = client.execute(post);

                BufferedReader bufreader = new BufferedReader(

                        new InputStreamReader(response.getEntity().getContent(),

                                "utf-8"));


                String line = null;


                while ((line = bufreader.readLine()) != null) {

                    result += line;

                }

            } catch (Exception e) {
                e.getStackTrace();
            }
            return result;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);

        }
    }*/
}
