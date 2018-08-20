<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ page import = "java.sql.*,org.json.*,database.MoMConnection, java.text.SimpleDateFormat"%>
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
		String schId = request.getParameter("schId");
		String sql ="";
		if(schId == null){
		 sql="Insert into mountain.schedule(mDate,mNum,schContent,importance,schId,userid) "
		 +"values(?,(select mNum from mountain.mountain where mName = ?),?,?,concat(?,now(3)+0),?)";
		}else{
			/*
			수정되는 속성 
			날짜,산코드,메모,중요도
			*/
			sql = "Update mountain.schedule set mDate=?, mNum=(select mNum from mountain.mountain where mName = ?), schContent=?, importance=? "
					+"where schId = ?";
		}
		pstmt = conn.prepareStatement(sql);
		
		String id = request.getParameter("id");
		String time = request.getParameter("time");
		int importance = Integer.parseInt(request.getParameter("importance"));
		String mtName = request.getParameter("mtName");
		String content = request.getParameter("content");	
		
		pstmt.setString(1,time);
		pstmt.setString(2,mtName);
		pstmt.setString(3,content);
		pstmt.setInt(4,importance);
		
		if(schId==null){
			pstmt.setString(5,id);
			pstmt.setString(6,id);
		}else{
			pstmt.setString(5,schId);
		}
		
		pstmt.executeUpdate();
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		
		
		
		
		out.println("{'status':1,'date':"+format.parse(time).getTime()+"}");
		System.out.println("{'status':1}");
		
		/*
		이곳에 각 jsp내용 구현.
		*/
		
	}catch(SQLException e){
		System.out.println(e.toString());
		out.println("{'status':0}");
		System.out.println("{'status':0}");
	}finally{
		if(conn!=null)
			conn.close();
		if(rs!=null)
			rs.close();
		if(pstmt!=null)
			pstmt.close();
	}

%>
