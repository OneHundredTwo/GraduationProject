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
ResultSet rs = null;
ResultSet rs1 = null;

%>


<% 
	System.out.println(request.getRequestURL());
	JSONObject responseJSON = new JSONObject();
	JSONArray jsonArr = new JSONArray();
	int clubNum= Integer.parseInt(request.getParameter("clubNum"));
	int n=0;
	try{
		conn = MoMConnection.getConnection();
		String sql = "SELECT clubmem FROM clubmem WHERE clubNum=? and clubmem=?";
		pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, clubNum);
		pstmt.setString(2, request.getParameter("id"));
		rs = pstmt.executeQuery();
		if(rs.next()){
			//동호회 가입 유효성 검사
			System.out.println("이미 동호회에 가입되어있습니다.");
			out.println("이미 동호회에 가입되어있습니다.");
			pstmt.close();
			rs.close();
		}else{
		
		String sql1 = "select clubjoin from club where clubNum= ?";//인원 가져오기
		String sql2 = "INSERT INTO clubmem VALUES(?,?)";//가입목록 데이터베이스에 등록
		pstmt1 = conn.prepareStatement(sql1);
		pstmt1.setInt(1, clubNum);
		rs1 = pstmt1.executeQuery();
		if(rs1.next()){
		String sql3 = "update mountain.club set clubjoin = ? where clubNum = ?";//동호회 인원 변경
		pstmt3 = conn.prepareStatement(sql3);
		pstmt3.setInt(1, rs1.getInt(1)+1);
		pstmt3.setInt(2, clubNum);
		pstmt3.executeUpdate();
		}
		pstmt2 = conn.prepareStatement(sql2);
		pstmt2.setInt(1, clubNum);
		pstmt2.setString(2, request.getParameter("id"));
		pstmt2.executeUpdate();
		
		
		System.out.println("동호회에 가입되었습니다.");
		out.println("동호회에 가입되었습니다.");
		}
		conn.close();
		
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
