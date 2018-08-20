package com.cookandroid.mom.my;

import android.content.Context;
import android.content.DialogInterface;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cookandroid.mom.R;
import com.cookandroid.mom.util.Exercise;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by RJH on 2017-10-21.
 */

public class ExerciseActivity extends Exercise {
    private Context context = this;
    private GoogleApiClient mApiclient;
    private int btnCheck;

    private TextView timer, step;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise);
        final FloatingActionButton btnStart = (FloatingActionButton)findViewById(R.id.btnStart);
        final FloatingActionButton btnStop = (FloatingActionButton)findViewById(R.id.btnStop);


        timer = (TextView)findViewById(R.id.timer);
        step = (TextView)findViewById(R.id.step);
        super.timer = timer;
        super.step = step;

        btnCheck = 0;
        btnStart.setImageResource(R.drawable.ic_play_arrow_white_24dp);
        btnStop.setImageResource(R.drawable.ic_stop_white_24dp);
        btnStop.setVisibility(View.GONE);

        setmApiClient(mApiclient);
        init();//google 피트니스 api를 사용하기 위한 client 설정

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (btnCheck){
                    case 0:

                            AlertDialog.Builder alert_confirm = new AlertDialog.Builder(context);
                            alert_confirm.setMessage("운동을 시작하시겠습니까?").setCancelable(false).setPositiveButton("예",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            healthStart();
                                            btnStop.setVisibility(View.VISIBLE);
                                            btnStart.setImageResource(R.drawable.ic_pause_white_24dp);

                                            Toast.makeText(context, "운동을 시작합니다.", Toast.LENGTH_SHORT).show();
                                            btnCheck = 1;
                                        }

                                    }).setNegativeButton("아니오",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // 'No'
                                            dialog.cancel();
                                            return;
                                        }
                                    });
                            AlertDialog alert = alert_confirm.create();
                            alert.show();

                        break;
                    case 1:
                        //btnCheck=1 상태에서 눌렀을 때 일시정지 하면서    버튼 바뀜 btnCheck=1 -> 2
                        healthPause();
                        btnStart.setImageResource(R.drawable.ic_play_arrow_white_24dp);
                        btnCheck = 2;
                        //safety 모드를 일시정지합니다. Toast
                        Toast.makeText(context, "운동을 일시정지 합니다.", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        //btnCheck=2 상태에서 눌렀을 때 다시 시작하면서 버튼 바뀜 btnCheck=2 -> 1
                        healthStart();
                        btnStart.setImageResource(R.drawable.ic_pause_white_24dp);

                        //safety 모드를 다시시작합니다. Toast
                        Toast.makeText(context, "운동를 다시 시작합니다.", Toast.LENGTH_SHORT).show();
                        btnCheck = 1;
                        break;
                    default:
                        break;

                }

            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //safety 기능 멈춤 btnCheck =1or2 -> 0
                //safety 기능을 멈추겠냐고 물어본 후 종료.
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(context);
                alert_confirm.setMessage("운동를 종료하시겠습니까?").setCancelable(false).setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 'YES'
                                healthStop(btnCheck);
                                btnStop.setVisibility(View.GONE);
                                btnStart.setImageResource(R.drawable.ic_play_arrow_white_24dp);
                                btnCheck = 0;

                                Toast.makeText(getApplicationContext(), "운동를 종료합니다.", Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 'No'
                                return;
                            }
                        });
                AlertDialog alert = alert_confirm.create();
                alert.show();


            }
        });
    }//End onCreate() Method.

    @Override
    protected void onResume() {
        super.onResume();

    }
    protected void onPause() {
        super.onPause();

    }

}
