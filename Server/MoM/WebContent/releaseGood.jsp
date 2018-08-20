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
   int n=0;
   try{
      conn = MoMConnection.getConnection();
      String sql = "Delete from mountain.good where boardNum = ? and goodID = ?";
      String sql2 = "Select tokenid from token where id=?";
      pstmt = conn.prepareStatement(sql);
      pstmt1 = conn.prepareStatement(sql2);
      
      pstmt.setInt(1, Integer.parseInt(request.getParameter("boardNum")));
      pstmt.setString(2, request.getParameter("id"));
      
      pstmt1.setString(1, request.getParameter("boardID"));
      
      rs= pstmt1.executeQuery();
      pstmt.execute();
      pstmt.close();
      if(rs.next()){
    	  String sql1 = "UPDATE mountain.board SET board.good = board.good-1 WHERE boardNum = ?";
    	  pstmt = conn.prepareStatement(sql1);
    	  pstmt.setInt(1, Integer.parseInt(request.getParameter("boardNum")));
    	  pstmt.execute();
      }
      pstmt1.close();
      
      System.out.println("게시물 좋아요를 해제합니다.");
      out.println("게시물 좋아요를 해제합니다.");
      
      /*
      이곳에 각 jsp내용 구현.
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