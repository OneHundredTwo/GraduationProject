<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ page import = "java.sql.*,org.json.*,database.MoMConnection, java.io.File"%>
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
   int boardNum = Integer.parseInt(request.getParameter("boardNum"));
   try{
      conn = MoMConnection.getConnection();
      String sql = "";
      
      sql = "Select boardImg from mountain.board where boardNum=? ";
      pstmt = conn.prepareStatement(sql);
      pstmt.setInt(1, boardNum);
      rs = pstmt.executeQuery();
      
      String fileName = "";
      while(rs.next()){
    	  fileName = rs.getString(1);
    	  System.out.println(fileName);
      }
      
      if(fileName != null){
    	  String filePath = MoMConnection.timelinepath+fileName;
    	  File file = new File(filePath);
    	  if(file.delete()){
    		  System.out.println("���� ����");
    		 
    	  }else{
    		  System.out.println("���� ����");
    	  }
      }
      
      sql = "Delete from mountain.good where boardNum=?";
      pstmt = conn.prepareStatement(sql);
      pstmt.setInt(1, boardNum);
      pstmt.execute();
      pstmt.close();
      
      sql = "Delete from mountain.reply where boardNum = ?";
      pstmt = conn.prepareStatement(sql);
      pstmt.setInt(1, boardNum);
      pstmt.execute();
      
      
      
      sql = "Delete from mountain.board where boardNum=? and writerID=?";
      pstmt = conn.prepareStatement(sql);
      pstmt.setInt(1, boardNum);
      pstmt.setString(2, request.getParameter("id"));
      pstmt.execute();
      pstmt.close();
      
      
      System.out.println("Ÿ�Ӷ����� ���� �Ǿ����ϴ�.");
      out.println("Ÿ�Ӷ����� ���� �Ǿ����ϴ�.");
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