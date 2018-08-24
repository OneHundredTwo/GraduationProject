# MasterOfMountain
![MasterOfMountain intro](https://github.com/OneHundredTwo/GraduationProject/blob/master/forReadme/img/img_loading.png "MoM 인트로")
> 대한민국 100대 명산 데이터를 바탕으로 등산로 및 등산커뮤니티기능을 제공하는 안드로이드 네이티브 어플리케이션 입니다.


## [팀 소개 및 기획](https://github.com/OneHundredTwo/GraduationProject/blob/master/forReadme/기획및설계.pdf)

## 개발 환경
* **개발언어** : JAVA 9, XML, JSP
* **WAS** : Apache Tomcat 9
* **IDE** : Eclipse 4.6 Neon, Android Studio 2.3 
* **OS** : windows 10, Android 5.0, Android 6.0
* **DBMS** : Mysql 5.7, SQLite
* **Android Project 관리도구** : Gradle

## 개발 파트
### 안드로이드
* 스케쥴러
	* [Material-Calendar View](https://github.com/prolificinteractive/material-calendarview)를 활용한 스케쥴러 UI
	* Calendar View를 이용한 월별 주요일정 확인 및 날짜 터치시 날짜 스케쥴리스트 출력 및 관리
	* 일정 중요도 및 크루일정을 색깔로 나누고 EventDecorator를 상속하여 데코레이션 커스터마이징
	* 개인일정과 등록 및 가입한 크루일정을 한 스케쥴러에서 관리할 수 있도록 조인
![MoM 스케쥴러](https://github.com/OneHundredTwo/GraduationProject/blob/master/forReadme/img/and_schedular.png "mom 스케쥴러")
* SK Planet Weather API 를 이용한 산 날씨정보 출력
![날씨 API를 이용한 산날씨 출력](https://github.com/OneHundredTwo/GraduationProject/blob/master/forReadme/img/and_weather.png "날씨 API를 이용한 산날씨 출력")
* Utilities 개발
	* **DataStringFormat** : 전역에 흩어져있는 String Format을 DataStringFormat로 모아서 문자열포맷팅 관심사를 분리
	* **AndroidToServer** : 안드로이드에서 MoM서버로 HTTP요청을 보내고 JSON메세지를 받을때 네트워크 통신을 하기위한 목적으로 AsyncTask 객체를 생성하는 코드가 여러 액티비티 및 프래그먼트에 분산되어있는 것을 모아서 하나의 인터페이스로 분리해 코드를 줄이고, 유지보수성을 높임.
	![Android To Server](https://github.com/OneHundredTwo/GraduationProject/blob/master/forReadme/img/and_ant.png "Android To Server")
### 서버
*  FCM(Firebase Cloude Message) API를 이용한 Push 알림 메세지 
	* 회원가입, 로그인, 크루가입 등 서비스 전역에서 활용되는 알림서비스를 팀원들이 보다 쉽게 접근할 수 있도록 빌더 패턴을 이용해 FCM API 요청 객체를 생성할 수 있도록 함.(FCMSendMessage, FCMGrouping)
	![FCM Builder Pattern](https://github.com/OneHundredTwo/GraduationProject/blob/master/forReadme/img/server_fcm1.png "FCM Builder Pattern")
	* FCM Notification API의 Subscribe 기능을 이용해 크루 단체 알림메세지 push
```JAVA
FCMGrouping group = new FCMGrouping(request.getParameter("planNum"), grouptoken);
	group.addRegId(request.getParameter("token"));
	String res = group.add();
	JSONObject responseJSON = new JSONObject(res);
	String noti_key = responseJSON.getString("notification_key");
	System.out.println(noti_key);
	
	FCMSendMessage message = new FCMSendMessage.Builder()
				.setTo(noti_key)
				.setTitle("참여")
				.setBody(request.getParameter("id")+"님이 크루에 참여하셨습니다!")
				.build();
	message.send();
```
	![Notifiaction Service](https://github.com/OneHundredTwo/GraduationProject/blob/master/forReadme/img/server_fcm_notification.png "Notification Service")
* 로그인 및 자동로그인 기능
	* firebase.iid 패키지에서 제공하는 FirebaseInstanceIdService를 이용, 어플리케이션을 처음 킬 때 디바이스를 유일하게 구분하는 토큰을 발급받음
	* 서버 DB에 토큰을 저장하고, 비로그인시 로그인할 때 해당 토큰값을 넘겨주면 저장된 사용자를 인증하여 자동로그인
	* 토큰과 매핑된 아이디가 서버의 클라이언트로 유일하게 연결될 수 있도록하여, 중복로그인 방지