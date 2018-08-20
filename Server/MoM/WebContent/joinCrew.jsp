<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ page import = "java.sql.*,org.json.*,database.MoMConnection,fcm.*"%>
<% request.setCharacterEncoding("UTF-8");
response.setCharacterEncoding("UTF-8"); %>
<%! 
   Connection conn = null;
	PreparedStatement pstmt = null;
	PreparedStatement pstmt1 = null;
	PreparedStatement pstmt2 = null;
	PreparedStatement pstmt3 = null;
	ResultSet rs = null;
%>


<% 
	System.out.println(request.getRequestURL());
	JSONArray jsonArr = new JSONArray();
	int planNum= Integer.parseInt(request.getParameter("planNum"));
	String grouptoken = null;
	int n=0;
	try{
		conn = MoMConnection.getConnection();
		String sql1 = "select prejoin, grouptoken from plan where planNum= ?";//인원 가져오기
		String sql2 = "INSERT INTO mountain.join VALUES(?,?)";//가입목록 데이터베이스에 등록
		pstmt1 = conn.prepareStatement(sql1);
		pstmt1.setInt(1, planNum);
		rs = pstmt1.executeQuery();
		if(rs.next()){
		String sql3 = "update mountain.plan set prejoin = ? where planNum = ?";//크루 인원 변경
		grouptoken = rs.getString(2);
		pstmt3 = conn.prepareStatement(sql3);
		pstmt3.setInt(1, rs.getInt(1)+1);
		pstmt3.setInt(2, planNum);
		pstmt3.executeUpdate();
		}
		pstmt2 = conn.prepareStatement(sql2);
		pstmt2.setInt(1, planNum);
		pstmt2.setString(2, request.getParameter("id"));
		pstmt2.executeUpdate();
		
		
		System.out.println("크루에 참여되었습니다.");
		out.println("크루에 참여되었습니다.");
		
		conn.close();
		
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
		System.out.println(message);
		
	}catch(SQLException e){
		System.out.println(e.toString());
	}finally{
		if(conn!=null)
			conn.close();
		if(rs!=null)
			rs.close();
		if(pstmt!=null)
			pstmt.close();
	}

%>
