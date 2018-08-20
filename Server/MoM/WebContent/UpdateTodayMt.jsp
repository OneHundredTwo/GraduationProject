<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ page import = "java.sql.*,org.json.*,database.MoMConnection, java.io.File, java.text.SimpleDateFormat"%>
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
		
		int todayNum = Integer.parseInt(request.getParameter("todayNum"));
		boolean isChgPic = Boolean.getBoolean(request.getParameter("isChgPic"));
		
		
		if(isChgPic){
			sql = "select picture from mountain.todaymt where todaymt_num = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1,todayNum);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()){
				String img_root = "C:/GraduationProject/Server/MoM/WebContent/img";
				String del_picture = img_root + rs.getString(1);
				
				 File f = new File(del_picture);

			    if (f.delete()) {
			      System.out.println("파일 또는 디렉토리를 성공적으로 지웠습니다: " + del_picture);
			    } else {
			      System.err.println("파일 또는 디렉토리 지우기 실패: " + del_picture);
			    }
			}
			
			rs.close();
			pstmt.close();
			
			/* 기존의 파일 삭제하는 코드. */ 
		}
		
		String mtNum = request.getParameter("mtNum");
		String event = request.getParameter("event");
		String content = request.getParameter("content");
		String picture = request.getParameter("picture");
		String title = request.getParameter("title");
		long date = Long.parseLong(request.getParameter("date"));
		String date_s = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(date);
		boolean isshow = Boolean.parseBoolean(request.getParameter("isshow"));
		
		sql = ""
				+ "UPDATE `mountain`.`todaymt` "
				+ "SET "
				+ "`mtNum` = ?, "
				+ "`event` = ?, "
				+ "`content` = ?, "
				+ "`title` = ?, "
				+ "`date` = ?, "
				+ "`isshow` = ? ";
		
		if(isChgPic){ sql += "`picture` = ?, ";}
		
		sql += "WHERE `todaymt_num` = ?;";
		
		pstmt = conn.prepareStatement(sql);
		
		int i=1;
		
		pstmt.setString(i++, mtNum);
		pstmt.setString(i++, event);
		pstmt.setString(i++,content);
		pstmt.setString(i++, title);
		pstmt.setString(i++,date_s);
		pstmt.setBoolean(i++, isshow);
		if(isChgPic){ pstmt.setString(i++, picture);}
		
		pstmt.setInt(i,todayNum);
		
		pstmt.executeUpdate();
		
		
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

