<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ page import = "java.sql.*,org.json.*,database.MoMConnection, fcm.*"%>
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
	JSONObject responseJSON = new JSONObject();
	JSONArray jarr = new JSONArray();
	int n=0;
	try{
		conn = MoMConnection.getConnection();
		String sql = "INSERT INTO mountain.club(clubName,clubID, clubContent,clubDate,clubjoin, clubIcon)";
	      sql+="VALUES(?,?,?,now(),?,?)";
	      pstmt = conn.prepareStatement(sql);
	      pstmt.setString(1, request.getParameter("name"));
	      pstmt.setString(2, request.getParameter("clubID"));
	      pstmt.setString(3, request.getParameter("content"));
	      pstmt.setInt(4, 1);
	      pstmt.setString(5,request.getParameter("Icon"));
	      pstmt.executeUpdate();
	     
	      String sql2 = "SELECT clubNum FROM mountain.club WHERE clubName = ? and clubID = ? and clubContent =? ";
	      pstmt2 = conn.prepareStatement(sql2);
	      pstmt2.setString(1, request.getParameter("name"));
	      pstmt2.setString(2,request.getParameter("clubID"));
	      pstmt2.setString(3,request.getParameter("content"));
	      rs = pstmt2.executeQuery();
	      if(rs.next()){
	    	  String sql3 = "INSERT INTO mountain.clubmem(clubNum,clubmem)VALUES(?,?)";
	    	  pstmt3 = conn.prepareStatement(sql3);
	    	  pstmt3.setInt(1, rs.getInt(1));
	    	  pstmt3.setString(2, request.getParameter("clubID"));
	    	  pstmt3.executeUpdate();
	      }
	      pstmt.close();
	      pstmt2.close();
	      pstmt3.close();
	      rs.close();
	      conn.close();
	      System.out.println("동호회가 등록되었습니다.");
	      out.println("동호회가 등록되었습니다.");
		
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
