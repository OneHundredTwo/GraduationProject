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
	         System.out.println("���̵� ���ų� ��й�ȣ�� �ùٸ��� �ʽ��ϴ�");
	         responseJSON.put("status", false);
	         responseJSON.put("islogin", false);
	         responseJSON.put("message", "���̵� ���ų� ��й�ȣ�� �ùٸ��� �ʽ��ϴ�");
	      }else{
	    	pstmt.close();
	    	rs.close();
	    	
	    	// �ش� ���̵�� ���ε� �ٸ� ��ū�� �ִ��� �˻��ϰ�, �̹� �ڵ��α��ε� ��Ⱑ ������, �ش� �α����� ����.
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
	      	responseJSON.put("message","�̹� �α��ε� ��Ⱑ �ֽ��ϴ�. �����α����� �����ϰ� �α����Ͻðڽ��ϱ�?");
	  		
	      }
	      
	      out.println(responseJSON.toString());
	      System.out.println(responseJSON.toString());
	         
		
		
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
