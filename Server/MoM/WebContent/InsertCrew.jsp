<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ page import = "java.sql.*,org.json.*,database.MoMConnection,fcm.*"%>
<% request.setCharacterEncoding("UTF-8");
response.setCharacterEncoding("UTF-8"); %>
<%! 
   Connection conn = null;
	PreparedStatement pstmt = null;
	PreparedStatement pstmt2 = null;
	PreparedStatement pstmt3 = null;
	ResultSet rs = null;
%>


<% 
	System.out.println(request.getRequestURL());
	JSONArray jsonArr = new JSONArray();
	String planNum="";
	int n=0;
	try{
		conn = MoMConnection.getConnection();
		conn.setAutoCommit(false);
		String sql = "INSERT INTO plan(planID, prejoin,joinNum, mtNm, planDate, clubNum,planContent,planregion,coursenum)";
	      sql+="VALUES(?, ?, ?, ?,DATE_FORMAT(?,'%Y-%m-%d %H:%i'),?,?,?,?)";
	      pstmt = conn.prepareStatement(sql);
	      pstmt.setString(1, request.getParameter("ID"));
	      pstmt.setInt(2, 1);
	      pstmt.setInt(3, Integer.parseInt(request.getParameter("joinNum")));
	      pstmt.setString(4, request.getParameter("mNum"));
	      pstmt.setString(5, request.getParameter("date"));
	      pstmt.setInt(6,Integer.parseInt(request.getParameter("clubNum")));
	      pstmt.setString(7, request.getParameter("content"));
	      pstmt.setString(8, request.getParameter("region"));
	      pstmt.setInt(9, Integer.parseInt(request.getParameter("course")));
	      pstmt.executeUpdate();
	      conn.commit();
	      pstmt.close();
	      
	      String sql2 = "SELECT planNum FROM plan WHERE planID = ? AND prejoin=? AND joinNum=? AND mtNm=? AND "
	    		  +"planDate = DATE_FORMAT(?,'%Y-%m-%d %H:%i') AND clubNum=? AND planContent=? AND planregion = ? AND coursenum=?";
	      pstmt2 = conn.prepareStatement(sql2);
	      pstmt2.setString(1, request.getParameter("ID"));
	      pstmt2.setInt(2, 1);
	      pstmt2.setInt(3, Integer.parseInt(request.getParameter("joinNum")));
	      pstmt2.setString(4, request.getParameter("mNum"));
	      pstmt2.setString(5, request.getParameter("date"));
	      pstmt2.setInt(6,Integer.parseInt(request.getParameter("clubNum")));
	      pstmt2.setString(7, request.getParameter("content"));
	      pstmt2.setString(8, request.getParameter("region"));
	      pstmt2.setInt(9, Integer.parseInt(request.getParameter("course")));
	      rs = pstmt2.executeQuery();
	  
	      if(rs.next()){
	    	  planNum = String.valueOf(rs.getInt(1));
	    	  System.out.println(planNum);
	    	  String sql3 = "INSERT INTO mountain.join(planNum,id) VALUES(?,?)";
	    	  pstmt3 = conn.prepareStatement(sql3);
	    	  pstmt3.setInt(1, rs.getInt(1));
	    	  pstmt3.setString(2, request.getParameter("ID"));
	    	  pstmt3.executeUpdate();
	    	  
	      }
	      rs.close();
	      System.out.println("크루가 등록되었습니다.");
	      out.println("크루가 등록되었습니다.");
	      conn.commit();
	      
	      FCMGrouping group = new FCMGrouping(planNum, null);
	      group.addRegId(request.getParameter("token"));
	 	  String res = group.create();
	      JSONObject responseJSON = new JSONObject(res);
	      String noti_key = responseJSON.getString("notification_key");
	      System.out.println(noti_key);
	      
	     String sql4 = "update mountain.plan set grouptoken = ? where planNum = ?";
	     pstmt = conn.prepareStatement(sql4);
	     pstmt.setString(1, noti_key);
	     pstmt.setInt(2, Integer.parseInt(planNum));
	     pstmt.execute();
	     conn.commit();
	     
	     FCMSendMessage message = new FCMSendMessage.Builder()
	    		 				.setTo(noti_key)
	    		 				.setTitle("개설")
	    		 				.setBody("크루를 개설하셨습니다!")
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
