package com.cookandroid.mom;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;

import com.cookandroid.mom.util.Util;
import com.cookandroid.mom.R;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

/**
 * Created by RJH on 2017-05-31.
 */
public class LoadingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        startLoading();
    }

    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Util.init();
                String autoID = null;
                Log.e("token",FirebaseInstanceId.getInstance().getToken()!=null?"null":"token");


                //앱 설치시 토큰 등록
                try {
                    String message = Util.user.autoLogin(FirebaseInstanceId.getInstance().getToken());
                    JSONObject response = new JSONObject(message);
                    int status = response.getInt("status");

                    if(status==1) {
                        JSONObject user_info = response.getJSONObject("user_info");
                        autoID = user_info.getString("id");
                        Util.user.setId(autoID);
                        Util.user.setAdmin(user_info.getBoolean("isAdmin"));
                        Log.e("id :", "" + Util.user.getId());
                    }else{

                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);


                //앱 설치시 토큰 등록
                finish();
            }
        }, 2000);
    }

}
