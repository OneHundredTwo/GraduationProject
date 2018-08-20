<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ page import = "java.sql.*,org.json.*,database.MoMConnection, fcm.*"%>
<% request.setCharacterEncoding("UTF-8");
response.setCharacterEncoding("UTF-8"); %>
<%! 
   Connection conn = null;
   PreparedStatement pstmt = null;
   ResultSet rs = null;
	ResultSet rs1 = null;
%>


<% 
	System.out.println(request.getRequestURL());
	int planNum = Integer.parseInt(request.getParameter("planNum"));
	JSONObject responseJSON = new JSONObject();
	JSONArray jarr = new JSONArray();
	int n=0;
	try{
		conn = MoMConnection.getConnection();
		String sql = "Delete from mountain.join where planNum = ?"; // 크루에 가입된 회원 삭제
		String sql1 = "select grouptoken from plan where planNum = ?";
		String sql2 = "Delete from mountain.plan where planID = ? and planNum = ?";//크루 삭제
		String sql3 = "SELECT tokenid from mountain.join,token where join.id = token.id and join.planNum = ?";
	
		pstmt = conn.prepareStatement(sql3);
		pstmt.setInt(1, planNum);
		rs = pstmt.executeQuery();
		while(rs.next()){
			jarr.put(rs.getString(1));
		}
		rs.close();
		pstmt.close();
		
		pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, planNum);
		pstmt.execute();
		pstmt.close();
		
		pstmt = conn.prepareStatement(sql1);
		pstmt.setInt(1, planNum);
		rs1 = pstmt.executeQuery();
		if(rs1.next()){
			FCMSendMessage message = new FCMSendMessage.Builder()
									.setTo(rs1.getString(1))
									.setTitle("해체")
									.setBody("크루가 해체되었습니다.")
									.build();
							message.send();
			FCMGrouping group = new FCMGrouping(request.getParameter("planNum"), rs1.getString(1));
			group.setRegIds(jarr);
			group.remove();
		}
		
		pstmt = conn.prepareStatement(sql2);
		pstmt.setString(1, request.getParameter("id"));
		pstmt.setInt(2, Integer.parseInt(request.getParameter("planNum")));
		pstmt.execute();
		pstmt.close();
		
		System.out.println("크루가 해체되었습니다.");
		out.println("크루가 해체되었습니다.");
		
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
