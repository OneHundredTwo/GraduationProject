<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
    <%@ page import = "java.text.SimpleDateFormat,java.sql.*,org.json.*,database.MoMConnection"%>
<% request.setCharacterEncoding("UTF-8");
response.setCharacterEncoding("UTF-8");%>
<%! 
   Connection conn = null;
   PreparedStatement pstmt = null;
   ResultSet rs = null;
%>


<% 
	System.out.println(request.getRequestURL());
	JSONObject responseJSON = new JSONObject();
	JSONArray daySchedule = new JSONArray();
	int n=0;
	try{
		String id = request.getParameter("id");
		Date date = new Date(Long.parseLong(request.getParameter("date")));
		
		conn = MoMConnection.getConnection();
		String sql = ""
				+ "select * from "
				+ "( "
				+ "select date_format(a.mDate,'%Y-%m-%d %H:%i') as mtime, a.schContent, b.mName, a.importance, a.schId, c.category "
				+ "from mountain.schedule a, mountain.mountain b, mountain.area_category c "
				+ "where a.mNum = b.mNum and b.mArea = c.mArea and date_format(a.mDate,'%Y%m%d') like ? and a.userid = ? "
				+ "union "
				+ "select date_format(planDate,'%Y-%m-%d %H:%i') mtime, planContent, mtNm,  6 as importance, planNum, planregion "
				+ "from plan "
				+ "where planNum in (select planNum from mountain.join where id = ?) and date_format(planDate,'%Y%m%d') like ?  "
				+ ") all_sch order by mtime asc";
		pstmt = conn.prepareStatement(sql);
		
		//해당일의 스케쥴을 갖고옴
		SimpleDateFormat year_month_day = new SimpleDateFormat("yyyyMMdd");
		
		pstmt.setString(1, year_month_day.format(date));
		pstmt.setString(2, id);
		pstmt.setString(3, id);
		pstmt.setString(4, year_month_day.format(date));
		
		
		rs = pstmt.executeQuery();
		
		while(rs.next()){
			JSONObject sch = new JSONObject();
			sch.put("time", rs.getString(1));
			sch.put("content", rs.getString(2));
			sch.put("mname", rs.getString(3));
			sch.put("importance", rs.getInt(4));
			sch.put("schId", rs.getString(5));
			sch.put("areaCat", rs.getString(6));
			daySchedule.put(sch);
		}
		
		responseJSON.put("daySchedule", daySchedule);
		
		out.println(responseJSON);
		
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
