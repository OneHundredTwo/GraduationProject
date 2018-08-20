<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ page import = "java.sql.*,org.json.*,database.MoMConnection"%>
<% request.setCharacterEncoding("UTF-8");
response.setCharacterEncoding("UTF-8"); %>
<%! 
   Connection conn = null;
   PreparedStatement pstmt = null;
   ResultSet rs = null;
%>


<% 
   System.out.println(request.getRequestURL());
   JSONObject responseJSON = new JSONObject();
   JSONArray jsonArr = new JSONArray();
   int n=0;
   try{
      conn = MoMConnection.getConnection();
      String sql = "update mountain.reply set replyContent=? where boardNum=? and replyDate=?";
      pstmt = conn.prepareStatement(sql);
      pstmt.setString(1, request.getParameter("content"));
      pstmt.setInt(2, Integer.parseInt(request.getParameter("boardNum")));
      pstmt.setString(3, request.getParameter("time"));
      
      pstmt.execute();
      
      pstmt.close();
      
      if(pstmt.isClosed()){{
    	  String sql1 = "update mountain.reply set replyDate = date_format(now(),"+"'%Y%m%d%H%i%s'"+") where boardNum = ? and replyDate=?";
    	  pstmt = conn.prepareStatement(sql1);
    	  pstmt.setInt(1, Integer.parseInt(request.getParameter("boardNum")));
    	  pstmt.setString(2, request.getParameter("time"));
    	  
    	  pstmt.execute();
    	  
      }
    	  System.out.println("글이 수정되었습니다.");
    	  out.println("글이 수정되었습니다.");
      }
      
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