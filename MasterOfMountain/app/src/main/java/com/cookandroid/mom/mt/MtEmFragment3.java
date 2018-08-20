package com.cookandroid.mom.mt;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cookandroid.mom.util.Util;
import com.cookandroid.mom.R;
import com.cookandroid.mom.util.MountainManager;

import java.util.ArrayList;

/**
 * Created by user on 2017-06-14.
 */

public class MtEmFragment3 extends Fragment {
    private ArrayList<MountainManager.Facility> facilInfos;
    private String[] arrFacilNm;
    private String[] arrFacilContent;
    private String[] mtName;
    private MountainManager.Mountain mountain;

    public MtEmFragment3(){    }
    public MtEmFragment3 newInstance() {
        return new MtEmFragment3();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mountain = MtDetailInfoActivity.mountain;



        try {
            facilInfos = Util.mtManager.getFacilitys(mountain);
            arrFacilNm =  new String[facilInfos.size()];
            arrFacilContent = new String[facilInfos.size()];
            mtName = new String[facilInfos.size()];

            for(int i=0; i<facilInfos.size(); i++) {
                MountainManager.Facility facilinfo = facilInfos.get(i);
                mtName[i] = facilinfo.mtName;
                arrFacilNm[i] = facilinfo.facilName;
                arrFacilContent[i] = facilinfo.content;
            }


        }catch(Exception e){
            e.getStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mt_detail_3em_frag, container, false);

        // 선언한 리스트뷰에 사용할 리스뷰연결
        ListView listview = (ListView) view.findViewById(R.id.list_em);

        // 객체를 생성합니다
        ArrayList<CData> alist = new ArrayList<CData>();

        // 데이터를 받기위해 데이터어댑터 객체 선언
        DataAdapter adapter = new DataAdapter(getActivity(), alist);



        // 리스트뷰에 어댑터 연결
        listview.setAdapter(adapter);


        for(int i=0; i<arrFacilNm.length; i++) {
            adapter.add(new CData(getActivity().getApplicationContext(), arrFacilNm[i],
                    "["+mtName[i]+"]", arrFacilContent[i], R.drawable.a));
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

            // 현재 리스트의 하나의 항목에 보일 컨트롤 얻기

            if (v == null) {

                // XML 레이아웃을 직접 읽어서 리스트뷰에 넣음
                view = mInflater.inflate(R.layout.mt_list_em, null);
            } else {

                view = v;
            }

            // 자료를 받는다.
            final CData data = this.getItem(position);

            if (data != null) {
                // 화면 출력
                TextView name = (TextView) view.findViewById(R.id.emName);
                TextView add = (TextView) view.findViewById(R.id.emMtNm);
                TextView content = (TextView) view.findViewById(R.id.emContent);
                name.setText(data.getM_emName());
                add.setText(data.getM_emAdd());
                content.setText(data.getM_emContent());

                name.setTextColor(Color.BLACK);
                add.setTextColor(Color.GRAY);
                content.setTextColor(Color.BLACK);

            }

            return view;

        }
    }

    // CData안에 받은 값을 직접 할당
    class CData {

        private String m_emName, m_emAdd, m_emContent;

        public CData(Context context, String name, String add,
                     String content, int pic) {

            m_emName = name;
            m_emAdd = add;
            m_emContent = content;

        }

        public String getM_emName() {
            return m_emName;
        }

        public String getM_emAdd() {
            return m_emAdd;
        }

        public String getM_emContent() {
            return m_emContent;
        }

    }

}
