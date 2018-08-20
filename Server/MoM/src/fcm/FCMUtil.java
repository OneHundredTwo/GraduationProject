package fcm;

public class FCMUtil {
   final protected static String requestMethod = "POST";
   final protected static String fcmServerKey = "key=AAAAP1XEhW0:APA91bEbxe82l2MJTrrf4yDy5ULeNAjG2sGEuixICGAPwsQ9I4rRjDsmaqtmidO7cjufi-iTeHFjhpf3kgmlUqyaqs2RU2snXHhoALuMEo69ycAQIyithg7B7KIUyvN7NE_nC9l48Pou";
   //*오류1 : "key="도 Authorization속성의 값이다.=>이것때문에 인증이 안됐었다...
   final protected static String contentType = "application/json";
   final protected static String senderId = "272021882221";
   }