package com.cookandroid.mom.util;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class AndroidToServer extends AsyncTask<String, Void, String> {
    final private String tag = "AndroidToServer";

    //AsyncTask<Param Type, Processing Type, Result Type>
    //AsyncTask는 AJAX와 같은 성질의 비동기식 연결방식으로 소량의 데이터만 요청하는 비교적 짧은 연결을 할때 쓰레드를 생성하여 연결하는 클래스 입니다.
    //무조건 상속하여 doInBackground를 재정의해서 사용해야합니다. (얘가 쓰레드가 run()될때 실행되는 내용을 담은 메소드)
    //세가지 제너릭은 doInBackground의 매개변수, 프로세싱, 반환형 의 데이터타입을 정의합니다.
    //그래서 해당작업을 수행하려면, AsyncTask를 상속받은 클래스로 객체를 생성하고, 해당객체의 excute(Params)메소드에 doInBackground메소드에 넘길 파라미터를 지정하여 실행합니다.

    //*doInBackground의 매개변수는 무조건 variable(가변인자) 해야합니다. => (String... params) 형태

    private String SERVER_IP = Util.AppServerIP; // 서버환경에 따라서 수정
    //공인아이피로 외부접속이면 공인아이피로 설정, 후에 공유기에서 DHCP고정아이피로 지정, 포트포워딩작업.)
    //사설아이피로 내부접속이면 해당 디바이스의 사설ip로 지정(192.168.25.xxx)
    private String PORT = Util.AppServerPORT;
    //SK브로드밴드 공유기(인지 통신사인지 모르겠으나)사용시 포트포워딩 8080으로 안됨.
    //포트포워딩 할때 해당 포트번호로, tomcat->conf/server.xml에서 Connector 태그의 port속성을 해당번호로 수정.

    void setIP(String ip){
        SERVER_IP = ip;
    }
    String getIP(){
        return SERVER_IP;
    }

    void setPORT(String port){
        PORT = port;
    }
    String getPORT(){
        return PORT;
    }

    String getIPnPort(){return SERVER_IP+":"+PORT;};

    public AndroidToServer(){
        super();
    }

    AndroidToServer(String ip, String port){
        super();
        setIP(ip);
        setPORT(port);
    }

    protected String httpPostToServer(String ... params){
        StringBuilder sb = new StringBuilder(); //응답메세지를 저장할 객체
        try {
            //형식은 공공데이터 api요청 형식을 그대로 빌림.
            StringBuilder urlBuilder = new StringBuilder("http://" + SERVER_IP + PORT + "/" + params[0]);

            /*
            excute로 넘어오는 매개변수 형식
            (PageName, Key:Value, Key:Value...)
            PageName : 해당 UI에서 필요한 데이터베이스 작업을 수행할 jsp페이지 경로
            Key:Value : 쿼리에 조회할 데이터로 필요한 Parameter를 전달할때 "Key:Value"형식으로 전달.
            * */

            JSONObject requestData = new JSONObject();

            //get / post 방식으로 나누려면 : parameter생성/ paramter전송방식 / request content-type 속성 설정 => 이렇게 다르다.
            for(int i=2; i<params.length; i++){
                String[] key_value = params[i].split(":",2);
                String key = key_value[0];
                String value = key_value[1];

                requestData.put(key, value);

            }
            Log.e(tag,urlBuilder.toString());


            URL url = new URL(urlBuilder.toString()); //생성된 URL형식 문자열로 URL객체 생성.
            HttpURLConnection conn = (HttpURLConnection) url.openConnection(); //Request,Response가 오고갈 Connection객체를 url객체의 openConnection()를 이용해 생성함.
            conn.setRequestMethod("POST"); //Request 요청방식
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type","application/json");
             /* 여기까지 Request 요청준비 */

             //requestData 송신
            OutputStream out = conn.getOutputStream();
            out.write(requestData.toString().getBytes());


            BufferedReader rd; // 응답메세지를 읽어들일 Reader객체

            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            //응답이 정상일때, 에러일때 각각 메세지 읽을 스트림, 에러메세지 읽을 스트림을 연결.


            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);

            }
            // 메세지 읽어들임.

            rd.close();
            conn.disconnect();
            //메세지를 다 읽어들이면 연결 해제
            Log.e(tag,sb.toString());

            return sb.toString(); // 응답메세지 반환. <json형식의 응답메세지를 파싱해서 객체로 반환하는 메소드를 만들것인지, 각 액티비티마다 알아서 처리할 것인지?>
        }catch(Exception e){
            e.printStackTrace();
            return "AndroidToServer Connection part : "+e.toString();

        }

    }

    protected String httpGetToServer(String ... params){
        StringBuilder sb = new StringBuilder(); //응답메세지를 저장할 객체
        try {
            //형식은 공공데이터 api요청 형식을 그대로 빌림.
            StringBuilder urlBuilder = new StringBuilder("http://" + SERVER_IP + PORT + "/" + params[0]);

            /*
                //후에 쿼리로 요청데이터를 넘겨야 할 경우.
                urlBuilder.append("?" + URLEncoder.encode("[키1]","UTF-8") + "=[값1]");
                urlBuilder.append("&" + URLEncoder.encode("[키2]","UTF-8") + "=[값2]");
            */

            /*
            excute로 넘어오는 매개변수 형식
            (PageName, Key:Value, Key:Value...)
            PageName : 해당 UI에서 필요한 데이터베이스 작업을 수행할 jsp페이지 경로
            Key:Value : 쿼리에 조회할 데이터로 필요한 Parameter를 전달할때 "Key:Value"형식으로 전달.
            * */

            String connectChar ="?";

            //get / post 방식으로 나누려면 : parameter생성/ paramter전송방식 / request content-type 속성 설정 => 이렇게 다르다.
            for(int i=1; i<params.length; i++){
                String[] key_value = params[i].split(":",2);
                String key = key_value[0];
                String value = key_value[1];

                urlBuilder.append(connectChar + URLEncoder.encode(key,"UTF-8") +"="+(key.equals("ServiceKey")?value:URLEncoder.encode(value,"UTF-8")));
                // URL에는 literal space를 넣을 수 없다 => URL인코딩으로 바꿔라(URL인코딩에서 공백 : %)

                connectChar = "&";
            }
            Log.e(tag,urlBuilder.toString());


            URL url = new URL(urlBuilder.toString()); //생성된 URL형식 문자열로 URL객체 생성.
            HttpURLConnection conn = (HttpURLConnection) url.openConnection(); //Request,Response가 오고갈 Connection객체를 url객체의 openConnection()를 이용해 생성함.
            conn.setRequestMethod("GET"); //Request 요청방식 => POST로 바꾸기
             /* 여기까지 Request 요청준비 */

            BufferedReader rd; // 응답메세지를 읽어들일 Reader객체

            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            //응답이 정상일때, 에러일때 각각 메세지 읽을 스트림, 에러메세지 읽을 스트림을 연결.


            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);

            }
            // 메세지 읽어들임.

            rd.close();
            conn.disconnect();
            //메세지를 다 읽어들이면 연결 해제
            Log.e(tag,"response -"+sb.toString());

            return sb.toString(); // 응답메세지 반환. <json형식의 응답메세지를 파싱해서 객체로 반환하는 메소드를 만들것인지, 각 액티비티마다 알아서 처리할 것인지?>
        }catch(Exception e){
            e.printStackTrace();
            return "AndroidToServer Connection part : "+e.toString();

        }
    }

    protected String doInBackground(String... params){
        //가변인자 쓰는 방법은, 배열로 넘어오는 매개변수와 동일하게 사용(첫번째 인덱스부터 접근함.)
        if(params.length >= 2 && (params[1].equals("POST") || params[1].equals("post"))){
            return httpPostToServer(params);
        }else {
            return httpGetToServer(params);
        }
    }


}