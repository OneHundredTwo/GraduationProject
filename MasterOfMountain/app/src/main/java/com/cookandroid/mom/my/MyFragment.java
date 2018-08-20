package com.cookandroid.mom.my;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cookandroid.mom.R;


public class MyFragment extends Fragment implements View.OnClickListener{

    private Fragment fg, now_selected;
    private Button btnMyInfo, btnMyWriting, btnMySch;
    FragmentManager fm;
    MyInfoFragment1 myInfo = MyInfoFragment1.newInstance();
    MyHealthFragment2 myHealth = new MyHealthFragment2();
    MySchduleFragment3 mySch;
    public MyFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.my_fragment, container, false);

        //onCreateView에서 현재날짜로 설정하는 코드가 있음에도, 적용되지 않아서, Calendar프래그먼트는 계속 새로 생성함.
         mySch = MySchduleFragment3.newInstance();


        fg = myInfo;
        fm = getChildFragmentManager();
        //getFragmentManager()로 불러오는 매니저객체는 새로운 객체이며,
        FragmentTransaction transaction = fm.beginTransaction();
        if(fm.getFragments()!=null) {
            for (Fragment f : fm.getFragments()) {
                transaction.remove(f);
            }
        }
        /** * R.id.container(activity_main.xml)에 띄우겠다.
         * * 첫번째로 보여지는 Fragment는 firstFragment로 설정한다. */
        transaction.replace(R.id.child_fourthfragment_container, fg);
        /** * Fragment의 변경사항을 반영시킨다. */
        transaction.commit();

        btnMyInfo = (Button)view.findViewById(R.id.btnMyInfo);
        btnMyWriting = (Button)view.findViewById(R.id.btnMyHealth);
        btnMySch = (Button)view.findViewById(R.id.btnMySch);
        btnMyInfo.setOnClickListener(this);
        btnMyWriting.setOnClickListener(this);
        btnMySch.setOnClickListener(this);

        return view;
    }

    //내정보, 내가 쓴 글 버튼 눌렀을 시
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnMyInfo:
                Log.e("button", "btnMyInfo");
                fg = myInfo;
                break;
            case R.id.btnMyHealth:
                Log.e("button", "btnMyWriting");
                fg = myHealth;
                break;
            case R.id.btnMySch:
                Log.e("button", "btnMySch");
                fg = mySch;
                break;
        }
        setChildFragment(fg);

        Log.e("SetFragment", fg.toString()+":"+fg.getId());


        for(Fragment f : fm.getFragments()){
            Log.e("FragmentCnt",""+(f!=null?f.toString()+":"+f.getId():"null"));
        }
        Log.e("Total ",fm.getFragments().size()+"");
        /*
        현재방식으론 Fragment가 계속해서 쌓인다. 기존에 로드되어있던 Fragment는 Fragment에서 삭제하거나,
        있는 Fragment를 돌려쓰면서 관리해야한다.
        ==> FragmentManager에서 관리할 Fragment를 생성하는 것은 onCreateView에서 한번만 수행하도록 변경.
        ==> Fragment가 화면에 보여질때는 onCreateView가 항상 수행되는듯하므로, 눌렀을때 변경되는사항은 반영됨.

        * */
    }

    protected void setChildFragment(Fragment child) {
        FragmentTransaction childFt = fm.beginTransaction();

        if (!child.isAdded()) { //프래그먼트 존재하는 지 확인
            childFt.replace(R.id.child_fourthfragment_container, child); //컨테이너 재사용
            childFt.addToBackStack(null); //프래그먼트 스택 (back 버튼 누를 때 순차적으로 back)
            childFt.commit();
        }
    }
    protected void setChildFragment(Fragment child, FragmentManager fm) {
        FragmentTransaction childFt = fm.beginTransaction();

        /*if(getActivity().getFragmentManager() == this.fm){

        }*/
        if(this.fm==fm){
            Log.e("Checkfms","Parents child fragment manager fm == child get fragment manager");
        }

        if (!child.isAdded()) { //프래그먼트 존재하는 지 확인
            childFt.replace(R.id.child_fourthfragment_container, child); //컨테이너 재사용
            childFt.addToBackStack(null); //프래그먼트 스택 (back 버튼 누를 때 순차적으로 back)
            childFt.commit();
        }
    }
}
