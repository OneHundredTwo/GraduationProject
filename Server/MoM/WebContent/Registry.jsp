<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import = "java.sql.*,database.MoMConnection"%>
<% request.setCharacterEncoding("UTF-8"); %>
<%! 
   Connection conn = null;
   PreparedStatement pstmt = null;
   ResultSet rs = null;
%>


<% 
	System.out.println(request.getRequestURL());
	int n=0;
	try{
		conn = MoMConnection.getConnection();
		String sql = "SELECT id FROM member WHERE id=?";
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, request.getParameter("id"));
		rs = pstmt.executeQuery();
		if(rs.next()){
			System.out.println("이미 중복된 아이디입니다.");
			out.println("이미 중복된 아이디입니다.");
			rs.close();
		}
		else if(!request.getParameter("password").equals(request.getParameter("password2"))){
			System.out.println("비밀번호가 다릅니다.");
			out.println("비밀번호가 다릅니다.");	
			rs.close();
		}else{
		pstmt.close();
		System.out.println("가입성공");
		out.println("가입 성공");
		sql = "INSERT INTO member VALUES(?,?,?,?,?,?,?,0)";
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, request.getParameter("id"));
		pstmt.setString(2, request.getParameter("password"));
		pstmt.setString(3, request.getParameter("address"));
		pstmt.setString(4, request.getParameter("sex"));
		pstmt.setString(5, request.getParameter("phone"));
		pstmt.setString(6, request.getParameter("email"));
		pstmt.setString(7, request.getParameter("hint"));
		pstmt.executeUpdate();
		}
		
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