<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ page import = "java.sql.*,org.json.*,database.MoMConnection, java.io.*"%>
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
	JSONObject jsonMain = new JSONObject();
	JSONArray jsonarr = new JSONArray();
	
	try{
		conn = MoMConnection.getConnection();
		String sql = "Delete from mountain.clubmem where clubNum = ?"; // 동호회 회원들 삭제
		String sql2 = "Delete from mountain.club where clubID = ? and clubNum = ?";//동호회 삭제
		String sql3 = "Delete from mountain.plan where clubNum=?";
		String sql1 = "SET FOREIGN_KEY_CHECKS=0";
		
		pstmt = conn.prepareStatement(sql1);
		pstmt.execute();
		pstmt.close();
		
		pstmt1 = conn.prepareStatement(sql3);
		pstmt1.setInt(1, Integer.parseInt(request.getParameter("clubNum")));
		pstmt1.execute();
		
		
		pstmt2 = conn.prepareStatement(sql2);
		pstmt2.setString(1, request.getParameter("id"));
		pstmt2.setInt(2, Integer.parseInt(request.getParameter("clubNum")));
		pstmt2.execute();
		pstmt2.close();
	
		pstmt3 = conn.prepareStatement(sql);
		pstmt3.setInt(1, Integer.parseInt(request.getParameter("clubNum")));
		pstmt3.execute();
		
		sql2 = "SET FOREIGN_KEY_CHECKS=1";
		pstmt4 = conn.prepareStatement(sql2);
		pstmt4.execute();
		
		String realpath = "C:/Users/wdj46/Desktop/graduation/20170808/Server/MoM/WebContent/img/";
		String filename = request.getParameter("Icon");
		
		File f = new File(realpath+filename);
		System.out.println(realpath+filename);
		if(f.exists()){
			f.delete();
			System.out.println("파일이 삭제되었습니다.");
		}else{
			System.out.println("파일이 없습니다.");
		}
		
		System.out.println("동호회가 해체되었습니다.");
		out.println("동호회가 해체되었습니다.");
		
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
