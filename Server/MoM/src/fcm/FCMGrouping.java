package fcm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;
import org.json.JSONArray;

public class FCMGrouping {
   final String requestMethod;
   final String fcmServerKey;   //*오류1 : "key="도 Authorization속성의 값이다.=>이것때문에 인증이 안됐었다...
   final String contentType;
   final String senderId;
   /* 오류2 : application\\json => application/json
    *AndroidToServer에선 Get방식이라서 Request의 ContentType이 application\\json(안되는 것)으로 해도 문제가 없었는데,
    *Post방식에선 원래 안먹혔던  코드를 되는지 안되는지 모르고 붙여넣어서 받는쪽 서버에서 content를 일반텍스트로 읽었나보다. 그래서 json형식으로 보낸 토큰을 못읽었다. 
    */
   //HTTP 요청 HEADER에 설정될 값들
   //fcmServerKey(firebase 콘솔) : https://console.firebase.google.com/project/messaging-2e41b/settings/cloudmessaging/android:com.google.firebase.quickstart.fcm
   
   JSONObject body = new JSONObject();
   //request 메세지 body에 실릴 JSON메세지객체.

   final String notification_key_name;
   final String notification_key;
   JSONArray registration_ids = null;
   //디바이스토큰들을 나타내는 String 배열
   //body에 실릴 데이터
   
   final String fcmGroupingUrl = "https://android.googleapis.com/gcm/notification";
   URL fcmserver = null;
   HttpURLConnection conn = null;
   //FCM 서버와 연결할 HTTP 연결객체
   OutputStream out=null;
   //요청 body를 전송할 스트림
   
   public FCMGrouping(String group_id, String group_token){
      requestMethod = FCMUtil.requestMethod;
      fcmServerKey = FCMUtil.fcmServerKey;   //*오류1 : "key="도 Authorization속성의 값이다.=>이것때문에 인증이 안됐었다...
      contentType = FCMUtil.contentType;
      senderId = FCMUtil.senderId;
      /*
       * create : group_id, null
       * add/remove : group_id, group_token
       * 
       * 생성 후 순서 => add/setRegId() -> create/add/remove
       * */
      notification_key_name = group_id;
      notification_key = group_token;
      //group id는 유일해야함.
      try {
         
         fcmserver = new URL(fcmGroupingUrl);
         conn = (HttpURLConnection)fcmserver.openConnection();
         //HTTP 연결객체 생성
         conn.setRequestMethod(requestMethod);
         conn.setRequestProperty("Authorization", fcmServerKey);
         conn.setRequestProperty("Content-Type", contentType);
         conn.setRequestProperty("project_id", senderId);
         conn.setDoOutput(true);//몰랐는데 OutputStream을 열려면, 이 설정을 해줘야함. 
         //HTTP 연결객체 HEADER 설정
         
      }catch(Exception e) {
         e.printStackTrace();
      }
   }
   
   public void addRegId(String token) { //단일 추가
      if(registration_ids==null) {
         registration_ids = new JSONArray();
      }
      registration_ids.put(token);
   }
   
   public void setRegIds(JSONArray regIds) { //다중 추가
      this.registration_ids = regIds; 
   }
   
   void bodySetting() {
      if(notification_key!=null) {body.put("notification_key", notification_key);} // add나 remove의 경우
      body.put("notification_key_name", notification_key_name);
      body.put("registration_ids", registration_ids);
      
   }
   
   
   private String send() {
      //생성된 그룹메소드요청을 FCM서버로 전송.
      bodySetting();
      try {
      out = conn.getOutputStream();
      //스트림을 열었을때부터 통신시작.(3 hand shaking)
      out.write(body.toString().getBytes());
      System.out.println(body.toString());
      //body 보내기
      out.close();
      //요청 끝.
      
      BufferedReader responseReader; // 응답메세지를 읽어들일 Reader객체

        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
           responseReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
           responseReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        //응답이 정상일때, 에러일때 각각 메세지 읽을 스트림, 에러메세지 읽을 스트림을 연결.
        
        StringBuilder responseMessage = new StringBuilder();
        String line;
        while ((line = responseReader.readLine()) != null) {
           responseMessage.append(line);

        }
        //응답메세지 읽어들임.
        //참고 :  https://firebase.google.com/docs/cloud-messaging/http-server-ref?hl=ko#interpret-downstream
        // 응답메세지~오류메세지까지
        
        return responseMessage.toString();
      
      }catch(IOException e) {
         e.printStackTrace();
         return "appserver error";
      }
      
   }
   
   public String create() {
      body.put("operation", "create");
      if(registration_ids!=null && registration_ids.length()!=0) {
         return send();
      }else {
         return "appserver error : tokens is empty";
      }
      
      //create가 성공할 경우 반환 메세지는 notification_key이며 추가작업을 하는 데에 사용한다.
      //참고: notification_key_name은 등록 토큰을 추가하거나 삭제하는 데 필요하지 않지만 포함할 경우 실수로 잘못된 notification_key를 사용하는 것을 방지할 수 있습니다.
   }
   
   public String add() {
      body.put("operation", "add");
      if(registration_ids!=null && registration_ids.length()!=0) {
         return send();
      }else {
         return "appserver error : tokens is empty";
      }
      //성공시 notification_key가 반환된다.
   }
   
   public String remove() {
      body.put("operation", "remove");
      if(registration_ids!=null && registration_ids.length()!=0) {
         return send();
      }else {
         return "appserver error : tokens is empty";
      }
   }
}