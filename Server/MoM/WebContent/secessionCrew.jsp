<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ page import = "java.sql.*,org.json.*,database.MoMConnection,fcm.*"%>
<% request.setCharacterEncoding("UTF-8");
response.setCharacterEncoding("UTF-8"); %>
<%! 
	Connection conn = null;	
	PreparedStatement pstmt = null;
	PreparedStatement pstmt1 = null;
	ResultSet rs = null;
%>


<% 
	String grouptoken = null;
	int planNum = Integer.parseInt(request.getParameter("planNum"));
	System.out.println(request.getRequestURL());
	JSONArray jsonArr = new JSONArray();
	int n=0;
	try{
		conn = MoMConnection.getConnection();
		String sql1 = "select prejoin, grouptoken from mountain.plan where planNum= ?";
		String sql = "Delete from mountain.join where planNum = ? and id = ?";
		pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, planNum);
		pstmt.setString(2, request.getParameter("id"));
		pstmt.execute();
		pstmt.close();
		
		pstmt = conn.prepareStatement(sql1);
		pstmt.setInt(1, planNum);
		rs = pstmt.executeQuery();
		
		if(rs.next()){
		String sql3 = "update mountain.plan set prejoin = ? where planNum = ?";//Å©·ç Âü¿© ÀÎ¿ø º¯°æ
		grouptoken = rs.getString(2);
		pstmt1 = conn.prepareStatement(sql3);
		pstmt1.setInt(1, rs.getInt(1)-1);
		pstmt1.setInt(2, planNum);
		pstmt1.executeUpdate();
		pstmt1.close();
		}
		
		System.out.println("Å©·ç¿¡ Å»ÅðµÇ¾ú½À´Ï´Ù.");
		out.println("Å©·ç¿¡ Å»ÅðµÇ¾ú½À´Ï´Ù.");
		
		FCMGrouping group = new FCMGrouping(request.getParameter("planNum"),grouptoken);
		group.addRegId(request.getParameter("token"));
		String res = group.remove();
		JSONObject responseJSON = new JSONObject(res);
		String noti_key = responseJSON.getString("notification_key");
		System.out.println(noti_key);
		
		FCMSendMessage message = new FCMSendMessage.Builder()
 				.setTo(noti_key)
 				.setTitle("Å»Åð")
 				.setBody(request.getParameter("id")+"´ÔÀÌ Å©·ç¿¡ Å»ÅðÇÏ¼Ì½À´Ï´Ù")
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
