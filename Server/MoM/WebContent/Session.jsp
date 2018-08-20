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
		String sql = "";
		sql = "UPDATE club LEFT OUTER JOIN clubmem ON club.clubNum=clubmem.clubNum  SET clubjoin=clubjoin-1 WHERE clubmem.clubmem = ?";
		pstmt = conn.prepareStatement(sql);
		
		pstmt.setString(1, request.getParameter("id"));
		pstmt.execute();
		
		pstmt.close();
		
		sql = "UPDATE plan a LEFT OUTER JOIN mountain.join b ON a.planNum = b.planNum SET prejoin=prejoin-1 WHERE b.id=?";
		pstmt = conn.prepareStatement(sql);
		
		pstmt.setString(1, request.getParameter("id"));
		pstmt.execute();
		
		pstmt.close();
		
		sql = "DELETE FROM mountain.member where id=?";
		pstmt = conn.prepareStatement(sql);
		
		pstmt.setString(1, request.getParameter("id"));
		pstmt.execute();
		
		System.out.println("°èÁ¤ÀÌ Å»ÅðµÇ¾ú½À´Ï´Ù.");
		out.println("°èÁ¤ÀÌ Å»ÅðµÇ¾ú½À´Ï´Ù.");
		
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
