package fcm;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.*;
public class FCMSendMessage {
	final private static String requestMethod = FCMUtil.requestMethod;
	final private static String fcmServerKey = FCMUtil.fcmServerKey;
	//*오류1 : "key="도 Authorization속성의 값이다.=>이것때문에 인증이 안됐었다...
	final private static String contentType = FCMUtil.contentType;
	/* 오류2 : application\\json => application/json
	 *AndroidToServer에선 Get방식이라서 Request의 ContentType이 application\\json(안되는 것)으로 해도 문제가 없었는데,
	 *Post방식에선 원래 안먹혔던  코드를 되는지 안되는지 모르고 붙여넣어서 받는쪽 서버에서 content를 일반텍스트로 읽었나보다. 그래서 json형식으로 보낸 토큰을 못읽었다. 
	 */
	//HTTP 요청 HEADER에 설정될 값들
	//fcmServerKey(firebase 콘솔) : https://console.firebase.google.com/project/messaging-2e41b/settings/cloudmessaging/android:com.google.firebase.quickstart.fcm
	
	private JSONObject body = new JSONObject();
	//request 메세지 body에 실릴 JSON메세지객체.

	private String to;
	private JSONObject data = null;
	private JSONObject notification = null;
	//body에 실릴 데이터
	
	final private static String fcmSendUrl = "https://fcm.googleapis.com/fcm/send";
	private URL fcmserver = null;
	private HttpURLConnection conn = null;
	//FCM 서버와 연결할 HTTP 연결객체
	OutputStream out=null;
	//요청 body를 전송할 스트림
	
	private FCMSendMessage(){
		try {
			
			fcmserver = new URL(fcmSendUrl);
			conn = (HttpURLConnection)fcmserver.openConnection();
			//HTTP 연결객체 생성
			conn.setRequestMethod(requestMethod);
			conn.setRequestProperty("Authorization", fcmServerKey);
			conn.setRequestProperty("Content-Type", contentType);
			conn.setDoOutput(true);//몰랐는데 OutputStream을 열려면, 이 설정을 해줘야함. 
			//HTTP 연결객체 HEADER 설정
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setData(String key, String value) {
		
		//알림 json객체에 할당될 수 있는 key 종류(안드로이드 부분 확인) : https://firebase.google.com/docs/cloud-messaging/http-server-ref#--
		/*
		 * title, body, sound, icon, click_action, tag, color...(그외 로컬라이징 관련 속성)
		 * */
		if(data==null) {
			data = new JSONObject();
		}
		data.put(key,value);
	}
	//데이터 메세지를 보낼때 데이터를 세팅할 메소드
	//key,value를 받아서 속성을 하나씩 셋팅한다.
	private void setNotification(String key, String value) {
		if(notification == null) {
			notification = new JSONObject();
		}
		notification.put(key, value);
	}
	//알림 메세지를 보낼때 데이터를 세팅할 메소드
	//key,value를 받아서 속성을 하나씩 셋팅한다.
	
	/* 혹은 JSON객체를 매개변수로 받아서 할당*/
	private void setData(JSONObject data) {
		this.data = data;
	}
	private void setNotification(JSONObject notification) {
		this.notification = notification;
	}
	
	private void setConfig(String key, String value) {
		body.put(key, value);
	}
	private void setConfig(String key, JSONArray value) {
		body.put(key, value);
	}
	private void setConfig(String key, int value) {
		body.put(key, value);
	}
	private void setConfig(String key, boolean value) {
		body.put(key, value);
	}
	
	void messageSetting() {
		
		//수신자설정 값들이 있어야 보내기 가능.
		if(body.has("to")||body.has("registration_ids")||body.has("condition")) {
			if(data != null) {
				body.put("data", data);
			}//data가 추가되었으면 data를 나타내는 속성을 추가.
			if(notification != null) {
				body.put("notification", notification);
			}//notification가 추가되었으면 notification를 나타내는 속성을 추가.
		}
		
	}
	
	
	public void send() {
		//생성된 JSON메세지를 FCM서버로 전송하는 메소드.
		messageSetting();
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
        
        System.out.println(responseMessage);
		
		}catch(IOException e) {
			e.printStackTrace();
		}
		
	}
	
	static public class Builder{
		private final FCMSendMessage message;
		
		public Builder() {
			message = new FCMSendMessage();
		}
		
		//메세지 수신자 설정값
		public Builder setTo(String token_groupToken_topicReg) {
			//to를 단일 토큰 및 그룹키로 설정
			message.setConfig("to", token_groupToken_topicReg);
			return this;
		}
		
		public Builder setRegistrationIds(String[] tokens) {
			//그룹이나 주제를 제외한 다수의 토큰을 수신자목록 값 설정
			JSONArray multicast_receivers = new JSONArray(tokens);
			message.setConfig("registration_ids", multicast_receivers);
			return this;
		}
		
		public Builder setCondition(String topicExp) {
			//대상주제 설정식(ex:'topicA' || 'topicB') 값 설정
			message.setConfig("condition", topicExp);
			return this;
		}
		//메세지 내용 설정값:Notification 및 Data 설정
		//Notification
		public Builder setNotification(JSONObject notification) {
			message.setNotification(notification);
			return this;
		}
		public Builder setTitle(String notificationTitle) {
			message.setNotification("title", notificationTitle);
			return this;
		}
		public Builder setBody(String body) {
			message.setNotification("body", body);
			return this;
		}
		public Builder setIcon(String android_icon_res) {
			//알림의 작은아이콘
			//"/res/drawable/"
			message.setNotification("icon", "/res/drawable/"+android_icon_res);
			return this;
		}
		public Builder setSound(String android_sound_res) {
			//알림을 수신하면 재생할 알림음
			//"default"또는 앱에 번들로 포함된 사운드 리소스의 파일이름을 지원합니다.
			//사운드 파일은 "/res/raw/"에 있어야합니다.
			message.setNotification("sound", "/res/raw/"+android_sound_res);
			return this;
		}
		public Builder setTag(String notify_tag) {
			//알람 구분태그
			//1번 태그를 갖는 알람을 전송하고 또 한번 더 전송하면 첫번째로 갔던 알람을 나중에 간 알람이 대체한다.
			message.setNotification("tag", notify_tag);
			return this;
		}
		public Builder setColor(Color color) {
			
			return this;
		}
		public Builder setClick_action(String action) {
			//알림을 클릭했을때 실행할 액션을 설정합니다.
			//설정한 액션에 해당하는 인텐트필터가 설정된 액티비티를 실행합니다.
			message.setNotification("click_action", action);
			return this;
		}
		/*body_loc_key, body_loc_args,title_loc_key,title_loc_args => 알람내용 로컬라이징에 쓰이는 설정값들*/
		
		//Data
		public Builder setData(JSONObject data) {
			message.setData(data);
			return this;
		}
		public Builder setData(String key, String value) {
			message.setData(key, value);
			return this;
		}
		//기타설정
		public Builder setPriority(String priority) {
		//normal or high
			message.setConfig("priority", priority);
			return this;
		}
		public Builder setCollapse_key(String collapse_key) {
			message.setConfig("collapse_key", collapse_key);
			return this;
		}
		public Builder setTTL(int time_to_live) {
			//FCM에서 오프라인상태의 대상디바이스에게 전달되지 못한 메세지를 얼마나 보관할지설정.
			//min = 1초, default,max=  2419200(4주)
			message.setConfig("time_to_live", time_to_live);
			return this;
		}
		public Builder setRestricted_package_name(String package_name) {
			//등록토큰이 일치해야 메세지를 수신할 수 있는 애플리케이션의 패키지 이름을 지정합니다. :모르겠음.
			message.setConfig("restricted_package", package_name);
			return this;
		}
		public Builder setTestmode(boolean isTestmode) {
			//true -> 개발자가 실제로 메세지를 보내지 않고도 요청을 테스트할 수 있다.(onMessageRecieve 작동하지 않고)
			message.setConfig("dry_run", isTestmode);
			return this;
		}
		public FCMSendMessage build() {
			return message;
		}
	}
}
