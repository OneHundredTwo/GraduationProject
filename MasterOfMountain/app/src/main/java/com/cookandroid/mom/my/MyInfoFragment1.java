package com.cookandroid.mom.my;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cookandroid.mom.MainActivity;
import com.cookandroid.mom.util.Util;
import com.cookandroid.mom.R;
import com.github.clans.fab.FloatingActionMenu;

/**
 * Created by RJH on 2017-06-15.
 */
public class MyInfoFragment1 extends Fragment {
    //private Button btnChangePwd;
    private TextView myID, mySex, myPhone, myEmail, myAddress;
    private String[] myInfo;

    private FloatingActionMenu menuRed;
    private com.github.clans.fab.FloatingActionButton fab1, fab2;
    private AlertDialog alertDialog;


    public MyInfoFragment1(){

    }
    public static MyInfoFragment1 newInstance(){
        return new MyInfoFragment1();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_1myinfo, container, false);
        myID = (TextView)view.findViewById(R.id.myinfoID);
        mySex = (TextView)view.findViewById(R.id.myGender);
        myPhone = (TextView)view.findViewById(R.id.myPhone);
        myEmail = (TextView)view.findViewById(R.id.myEmail);
        myAddress = (TextView)view.findViewById(R.id.myAddress);

        menuRed = (FloatingActionMenu) view.findViewById(R.id.menu_red);
////////////////////////////////////////////////////////////////////////////////////////////
        fab1 = (com.github.clans.fab.FloatingActionButton) view.findViewById(R.id.fab1);
        fab2 = (com.github.clans.fab.FloatingActionButton) view.findViewById(R.id.fab2);
        menuRed.setClosedOnTouchOutside(true);

        try {
            myInfo= Util.user.myInfo();
            myID.setText(myInfo[0]);
            myAddress.setText(myInfo[1]);
            mySex.setText(myInfo[2]);
            myPhone.setText(myInfo[3]);
            myEmail.setText(myInfo[4]);
            // 받아온 pRecvServerPage를 분석하는 부분

        } catch (Exception e) {
            e.getStackTrace();
        }

        fab1.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(Util.user.getId()==null){
                    Toast.makeText(getActivity(), "비회원은 접근할수 없습니다.", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(getActivity(), EditActivity.class);
                    intent.putExtra("id",myID.getText().toString());
                    intent.putExtra("address",myAddress.getText().toString());
                    intent.putExtra("sex",mySex.getText().toString());
                    intent.putExtra("phone",myPhone.getText().toString());
                    intent.putExtra("email",myEmail.getText().toString());
                    intent.putExtra("hint",myInfo[5]);

                    startActivity(intent);
                }
            }
        });

        fab2.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

                alertDialogBuilder.setTitle("탈퇴");

                alertDialogBuilder
                        .setMessage("계정을 탈퇴하시겠습니까? 계정을 탈퇴하게 되면 이전에 존재하던 게시글, 동호회, 크루가 모두 사라집니다.")
                        .setCancelable(false)
                        .setPositiveButton("예",
                                new DialogInterface.OnClickListener(){

                                    @Override
                                    public void onClick(final DialogInterface dialog, int which) {
                                        String message = Util.user.Session(Util.user.getId());

                                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                        if(message.equals("계정이 탈퇴되었습니다.")){
                                            //계정이 탈퇴되었으니 다시 홈화면 호출하기 위하여 MainActivity 인텐트 후 기존MainActivity finish
                                            Util.user.setId(null);
                                            Intent intent = new Intent(getActivity(), MainActivity.class);
                                            startActivity(intent);
                                            getActivity().finish();
                                            dialog.cancel();
                                        }


                                    }
                                })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener(){

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog = alertDialogBuilder.create();
                if(alertDialog != null && alertDialog.isShowing() ){
                    alertDialog.dismiss();
                }
                alertDialog.show();
            }
        });

        return view;
    }
}
