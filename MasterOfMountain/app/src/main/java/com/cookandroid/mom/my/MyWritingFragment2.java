package com.cookandroid.mom.my;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cookandroid.mom.util.Util;
import com.cookandroid.mom.R;

import java.util.ArrayList;

/**
 * Created by RJH on 2017-06-15.
 */
public class MyWritingFragment2 extends Fragment {

    private String[][] brdInfo;
    private String[] brdID;
    private String[] brdContent;
    private String[] brdDate;

    public MyWritingFragment2(){
    }

    public static MyWritingFragment2 newInstance(){
        return new MyWritingFragment2();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_2mywriting, container, false);

        try {
            brdInfo = Util.user.getmyBoard();
            brdID = brdInfo[0];
            brdContent = brdInfo[1];
            brdDate = brdInfo[2];

            // 받아온 pRecvServerPage를 분석하는 부분
        } catch (Exception e) {
            e.getStackTrace();
        }

        // 선언한 리스트뷰에 사용할 리스뷰연결
        ListView listview = (ListView) view.findViewById(R.id.listView_MyWriting);

        // 객체를 생성합니다
        ArrayList<CData> alist = new ArrayList<CData>();

        // 데이터를 받기위해 데이터어댑터 객체 선언
        DataAdapter adapter = new DataAdapter(getActivity(), alist);

        // 리스트뷰에 어댑터 연결
        listview.setAdapter(adapter);


        for(int i=0; i<brdDate.length; i++) {
            adapter.add(new CData(getActivity().getApplicationContext(), brdID[i], brdDate[i], brdContent[i]));
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
                view = mInflater.inflate(R.layout.my_list_mywriting, null);
            } else {

                view = v;
            }

            // 자료를 받는다.
            final CData data = this.getItem(position);

            if (data != null) {
                // 화면 출력
                TextView name = (TextView) view.findViewById(R.id.name);
                TextView time = (TextView) view.findViewById(R.id.time);
                TextView content = (TextView) view.findViewById(R.id.content);
                name.setText(data.getM_name());
                time.setText(data.getM_time());
                content.setText(data.getM_content());
                name.setTextColor(Color.BLACK);
                time.setTextColor(Color.GRAY);
                content.setTextColor(Color.BLACK);

                ImageView pic = (ImageView) view.findViewById(R.id.pic);
                ImageView imageView = (ImageView) view.findViewById(R.id.imageView);

            } else {

                TextView notContent = (TextView) view.findViewById(R.id.notContent);
                notContent.setTextColor(Color.BLACK);
            }

            return view;

        }

    }

    // CData안에 받은 값을 직접 할당
    class CData {

        private String m_name, m_time, m_content;
       // private int m_pic, m_imageView;

        public CData(Context context, String name, String time,
                     String content/*, int pic, int imageView*/) {

            m_name = name;
            m_time = time;
            m_content = content;
        /*    m_pic = pic;
            m_imageView = imageView;*/

        }

        public String getM_name() {
            return m_name;
        }

        public String getM_time() {
            return m_time;
        }

        public String getM_content() { return m_content; }

/*        public int getM_pic() {
            return m_pic;
        }

        public int getM_imageView() {
            return m_imageView;
        }*/
    }
}
