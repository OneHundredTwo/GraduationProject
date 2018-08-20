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
      String sql = "SELECT boardNum,ifnull(id,'알수 없음') as id,replyContent, replyDate FROM mountain.reply WHERE boardNum=? ORDER BY replyDate DESC";
      
      pstmt = conn.prepareStatement(sql);
     
      pstmt.setInt(1, Integer.parseInt(request.getParameter("boardNum")));
      
      rs = pstmt.executeQuery();
      
      while(rs.next()){	
			JSONObject jsonobj = new JSONObject();
			jsonobj.put("id",rs.getString(2));
			jsonobj.put("content",rs.getString(3));
			jsonobj.put("date",rs.getString(4));
			
			jsonArr.put(n,jsonobj);
			n++;
			}
      
        responseJSON.put("reply",jsonArr);
		System.out.println(responseJSON.toString());
		out.println(responseJSON.toString());
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