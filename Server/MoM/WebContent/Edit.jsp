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
		if(!request.getParameter("password").equals(request.getParameter("password2"))){
			System.out.println("��й�ȣ�� �ٸ��ϴ�.");
			out.println("��й�ȣ�� �ٸ��ϴ�.");	
		}else{
		conn = MoMConnection.getConnection();
		String sql = "update mountain.member set member.password=?,address=?,phone=?,email=?,hint=? where id=?";
	      pstmt = conn.prepareStatement(sql);
	      pstmt.setString(1, request.getParameter("password"));
	      pstmt.setString(2, request.getParameter("address"));
	      pstmt.setString(3, request.getParameter("phone"));
	      pstmt.setString(4, request.getParameter("email"));
	      pstmt.setString(5, request.getParameter("hint"));
	      pstmt.setString(6, request.getParameter("id"));
	      
	      pstmt.execute();
	      
	      System.out.println("ȸ�������� �����Ǿ����ϴ�.");
	      out.println("ȸ�������� �����Ǿ����ϴ�.");
	      
		}
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
