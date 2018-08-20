<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import = "java.sql.*, org.json.JSONObject,database.MoMConnection, fcm.FCMSendMessage"%>
<% request.setCharacterEncoding("UTF-8"); %>
<%! 
   Connection conn = null;
   PreparedStatement pstmt = null;
   ResultSet rs = null;
%>
<% 
System.out.println(request.getRequestURL());
   int n=0;
   JSONObject responseJSON = new JSONObject();
   try{
      conn = MoMConnection.getConnection();
      
      
      
      String id, password, token;
      boolean isAdmin;
      id = request.getParameter("id");
      token = request.getParameter("token");
      boolean islogin = Boolean.parseBoolean(request.getParameter("islogin"));
      
    	//토큰 있는지 체크, 없으면 등록 (개발당시의 편의를 위해 추가한 코드)
      String sql = "select tokenid from token where tokenid=?";
    	pstmt = conn.prepareStatement(sql);
    	pstmt.setString(1,request.getParameter("token"));
    	
    	System.out.println(token);
    	
    	rs = pstmt.executeQuery();
    	
    	if(!rs.next()){
    		pstmt.close();	
    		sql = "Insert into token(tokenid) values(?)";
    		pstmt = conn.prepareStatement(sql);
    		pstmt.setString(1,token);
    		pstmt.executeUpdate();
    		
    	}
    	pstmt.close();
    	rs.close();
    	//------------------------------------------------
    	
    	//만약 로그인된 상태에서 사용자가 로그인하겠다고 하는 경우 먼저 디바이스에 연결된 로그인을 해제함.
    	if(islogin){
    		sql = "Update mountain.token set id = null where tokenid in (select a.tokenid from (select tokenid from mountain.token where id = ?) as a)";
    		pstmt = conn.prepareStatement(sql);
    		pstmt.setString(1, id);
    		pstmt.executeUpdate();
    		
    		pstmt.close();
    	}
    	//-------------------------------------------------------------
    	
    	//사용자와 연결된 디바이스 정보 업데이트 
         sql = "Update token set id = ? where tokenid = ?";
         
         pstmt = conn.prepareStatement(sql);
         pstmt.setString(1, id);
         pstmt.setString(2, token);
         pstmt.executeUpdate();
         pstmt.close();
         //----------------------------------------------------------
         
         //로그인 메세지 보내기
         FCMSendMessage message = new FCMSendMessage.Builder()
        		 .setTo(token)
        		 .setTitle("환영합니다!")
        		 .setBody(request.getParameter("id")+"님 로그인하셨다!!!")
        		 .build();
         message.send();
         //---------------------------------------------------------
         
         //사용자 정보 보내기
         sql = "select id, isadmin from mountain.member where id=?";
         pstmt = conn.prepareStatement(sql);
         pstmt.setString(1,id);
         rs = pstmt.executeQuery();
         
         if(rs.next()){
        	 JSONObject user_info = new JSONObject();
        	 user_info.put("id", rs.getString("id"));
        	 user_info.put("isAdmin", rs.getBoolean("isadmin"));
        	 
             responseJSON.put("user_info",user_info);
         }
         
         
      
      responseJSON.put("status",1);
      
   }catch(SQLException e){
	   responseJSON.put("status",0);
     	e.printStackTrace();
}finally{
	out.println(responseJSON.toString());
	if(conn!=null)
		conn.close();
	if(rs!=null)
		rs.close();
	if(pstmt!=null)
		pstmt.close();
}
%>