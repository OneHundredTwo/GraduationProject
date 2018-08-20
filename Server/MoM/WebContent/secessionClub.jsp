<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ page import = "java.sql.*,org.json.*,database.MoMConnection,fcm.*"%>
<% request.setCharacterEncoding("UTF-8");
response.setCharacterEncoding("UTF-8"); %>
<%! 
   Connection conn = null;
PreparedStatement pstmt = null;
PreparedStatement pstmt1 = null;
PreparedStatement pstmt2 = null;
PreparedStatement pstmt3 = null;
PreparedStatement pstmt4 = null;
ResultSet rs = null;
%>


<% 
	System.out.println(request.getRequestURL());
	JSONObject responseJSON = new JSONObject();
	JSONArray jsonArr = new JSONArray();
	int clubNum = Integer.parseInt(request.getParameter("clubNum"));
	int n=0;
	try{
		conn = MoMConnection.getConnection();
		String sql1 = "SET FOREIGN_KEY_CHECKS=0";
		
		pstmt = conn.prepareStatement(sql1);
		pstmt.execute();
		pstmt.close();
		
		String sql2 = "select clubjoin from club where clubNum= ?";
		String sql3 = "Delete from mountain.clubmem where clubNum = ? and clubmem = ?";
		pstmt = conn.prepareStatement(sql3);
		pstmt.setInt(1, clubNum);
		pstmt.setString(2, request.getParameter("id"));
		pstmt.execute();
		
		pstmt1 = conn.prepareStatement(sql2);
		pstmt1.setInt(1, clubNum);
		rs = pstmt1.executeQuery();
		
		if(rs.next()){
		String sql4 = "update mountain.club set clubjoin = ? where clubNum = ?";//동호회 인원 변경
		String sql5 = "Delete from mountain.plan where planID = ? and clubNum = ?";
		
		pstmt2 = conn.prepareStatement(sql4);
		pstmt3 = conn.prepareStatement(sql5);
		pstmt2.setInt(1, rs.getInt(1)-1);
		pstmt2.setInt(2, clubNum);
		pstmt2.executeUpdate();
		pstmt3.setString(1, request.getParameter("id"));
		pstmt3.setInt(2, clubNum);
		pstmt3.execute();
		}
		String sql6 = "SET FOREIGN_KEY_CHECKS=1";
		pstmt4 = conn.prepareStatement(sql6);
		pstmt4.execute();
		
		System.out.println("동호회에 탈퇴되었습니다.");
		out.println("동호회에 탈퇴되었습니다.");
		
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
