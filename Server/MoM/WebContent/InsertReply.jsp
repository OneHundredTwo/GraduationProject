<%@page import="fcm.FCMSendMessage"%>
<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ page import = "java.sql.*,org.json.*,database.MoMConnection"%>
<% request.setCharacterEncoding("UTF-8");
response.setCharacterEncoding("UTF-8"); %>
<%! 
   Connection conn = null;
   PreparedStatement pstmt = null;
   PreparedStatement pstmt1 = null;
   ResultSet rs = null;
%>


<% 
   System.out.println(request.getRequestURL());
   JSONObject responseJSON = new JSONObject();
   JSONArray jsonArr = new JSONArray();
   String content = request.getParameter("content");
   if(content.length()>=20){
	   content = content.substring(0,20);
   }
   int n=0;
   try{
      conn = MoMConnection.getConnection();
      String sql = "insert into mountain.reply(boardNum,id,replyContent,replyDate) "
    		     + "values(?,?,?,date_format(now(),"+"'%Y%m%d%H%i%s'"+"))";
      String sql2 = "Select tokenid from token where id=?";
      pstmt = conn.prepareStatement(sql);
      pstmt1 = conn.prepareStatement(sql2);
      
      pstmt.setInt(1, Integer.parseInt(request.getParameter("boardNum")));
      pstmt.setString(2, request.getParameter("id"));
      pstmt.setString(3, request.getParameter("content"));
      
      pstmt1.setString(1, request.getParameter("writer"));
     
      pstmt.execute();
      
      rs = pstmt1.executeQuery();
      
      if(rs.next()){
    	  String sql1 = "UPDATE mountain.board SET board.reply = board.reply+1 WHERE boardNum = ?";
    	  pstmt = conn.prepareStatement(sql1);
    	  pstmt.setInt(1, Integer.parseInt(request.getParameter("boardNum")));
    	  pstmt.execute();
    	  FCMSendMessage message = new FCMSendMessage.Builder()
    			  .setTo(rs.getString(1))
    			  .setTitle("���")
    			  .setBody(request.getParameter("id")+"���� ����� ����Ͽ����ϴ�.."+content)
    			  .build();
    	  
    	  message.send();
      }
      pstmt1.close();
      
      System.out.println("����� ��ϵǾ����ϴ�.");
      out.println("����� ��ϵǾ����ϴ�.");
      
      /*
      �̰��� �� jsp���� ����.
      */
      
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