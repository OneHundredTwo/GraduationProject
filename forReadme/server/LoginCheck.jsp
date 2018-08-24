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
		pstmt = conn.prepareStatement(sql);
		
		String id = request.getParameter("id");
		String password = request.getParameter("password");
		
		
		sql = "SELECT id FROM member WHERE id=? and password=?";
	      pstmt = conn.prepareStatement(sql);
	      System.out.println(id + " : "+password);
	      pstmt.setString(1, id);
	      pstmt.setString(2, password);
	      rs = pstmt.executeQuery();
	      if(!rs.next()){
	         System.out.println("아이디가 없거나 비밀번호가 올바르지 않습니다");
	         responseJSON.put("status", false);
	         responseJSON.put("islogin", false);
	         responseJSON.put("message", "아이디가 없거나 비밀번호가 올바르지 않습니다");
	      }else{
	    	pstmt.close();
	    	rs.close();
	    	
	    	// 해당 아이디와 매핑된 다른 토큰이 있는지 검사하고, 이미 자동로그인된 기기가 있으면, 해당 로그인을 해제.
	      	sql = "select tokenid from mountain.token where id=?";
	      	pstmt = conn.prepareStatement(sql);
	      	pstmt.setString(1,id);
	      	
	    	responseJSON.put("status", true);
	      	rs = pstmt.executeQuery();
	      	if(rs.next()){
	      		responseJSON.put("islogin",true);
	      	}else{
	      		responseJSON.put("islogin",false);
	      	}
	      	responseJSON.put("message","이미 로그인된 기기가 있습니다. 기존로그인을 해제하고 로그인하시겠습니까?");
	  		
	      }
	      
	      out.println(responseJSON.toString());
	      System.out.println(responseJSON.toString());
	         
		
		
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
