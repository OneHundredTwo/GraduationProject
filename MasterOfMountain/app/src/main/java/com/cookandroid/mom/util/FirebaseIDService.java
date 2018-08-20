package com.cookandroid.mom.util;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Shining on 2017-08-01.
 */

public class FirebaseIDService extends FirebaseInstanceIdService {
    private final String tag = "FirebaseIDService";
    /*
    *클라이언트가 Firebase ID서버에 등록되지 않은 상태에서 FirebaseInstanceId.getInstance().getToken()를 호출하면
    토큰이 없는 경우 토큰을 생성한 후 null을 반환하고
    토큰이 있는 경우 token값을 반환한다.

    *토큰이 생성될 경우, Service에 등록된 FirebaseInstanceIdService를 상속한 Service객체의 onTokenRefresh()를 호출한다.(이미 있는 토큰을 갖고오는 경우는 호출하지 않는다)
        -서버에서 클라이언트를 관리하고자 할 때, 토큰을 새로 등록하거나 갱신해야하는 경우 onTokenRefresh()를 override하여 앱서버와의 작업을 정의한다.

    *토큰삭제방법
        -메인스레드가 아닌 스레드에서의 FirebaseInstanceId.getInstance().deleteInstanceId() 호출
        -어플리케이션 삭제/재설치
        -사용자가 앱 데이터 제거(쿠키삭제 등)
    * */
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        /*
        보통 토큰이 새로 등록되었을때 앱서버에 해당클라이언트에 대한 토큰정보를 저장하는 동작을 호출하는 작동을 기술하는 메소드.
            -Service는 메인스레드와 독립적으로 작동하는 쓰레드이기때문에 따로 스레드를 생성하지 않고도 앱서버에 http요청을 보내는 코드를 작성할 수 있다.
        * */
        //AndroidToServer의 http작업을 메소드로 묶어서 doInBackground하고 Service에서 각각 사용할 수 있도록해야겠다.
        String uri = "FCMService/AddToken.jsp";
        Log.e(tag, "onTokenRefresh() start");
        Util.getAnt().httpGetToServer(uri,"token:"+FirebaseInstanceId.getInstance().getToken());
        Log.e(tag, "onTokenRefresh() end");
    }
}
