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
	JSONArray schOfYear = new JSONArray(); //�������� ������ 1��ġ ��¥���� ������ �迭
	JSONArray daySchedule = new JSONArray(); //��û�� ��¥�� �������� ������ �迭
	int n=0;
	try{
		conn = MoMConnection.getConnection();
		
		String id = request.getParameter("id");
		Date date = new Date(Long.parseLong(request.getParameter("date")));
		/*
		1.�޷¿� ǥ���� �������� �ִ� ��¥�� ��ȸ�ϱ�.
		*/
		String sql = ""
				+ "select days, max(color) from "
				+ "( "
				+ "select date_format(mDate,'%Y%m%d') as days, max(importance) as color from schedule "
				+ "where userid = ? group by days "
				+ "Union "
				+ "select date_format(planDate,'%Y%m%d') as days, 6 as color from plan "
				+ "where planNum in (select planNum from mountain.join where id = ?) "
				+ "group by days "
				+ ") as date_color "
				+ "group by days "
				+ "order by days";;
		
		
		pstmt = conn.prepareStatement(sql);
		
		// (����)�׳� �����
		
		pstmt.setString(1, id);
		pstmt.setString(2, id);
		
		rs = pstmt.executeQuery();
		
		while(rs.next()){
			JSONObject daySch = new JSONObject();
			daySch.put("date",rs.getString(1));
			daySch.put("importance",rs.getInt(2));
			schOfYear.put(daySch);;
		}
		
		pstmt.close();
		rs.close();
		
		/*
		2.����Ʈ�� ����� ���õ� ��¥ ������ ��ȸ�ϱ�.
		*/
		
		//yyyyMMdd hhmm
		sql = ""
				+ "select * from "
				+ "( "
				+ "select date_format(a.mDate,'%Y-%m-%d %H:%i') as mtime, a.schContent, b.mName, a.importance, a.schId, c.category "
				+ "from mountain.schedule a, mountain.mountain b, mountain.area_category c "
				+ "where a.mNum = b.mNum and b.mArea = c.mArea and date_format(a.mDate,'%Y%m%d') like ? and a.userid = ? "
				+ "union "
				+ "select date_format(planDate,'%Y-%m-%d %H:%i') mtime, planContent, mtNm,  6 as importance, planNum, planregion "
				+ "from plan "
				+ "where planNum in (select planNum from mountain.join where id = ?) and date_format(planDate,'%Y%m%d') like ? "
				+ ") all_sch order by mtime asc";
		pstmt = conn.prepareStatement(sql);
		//�ش����� �������� �����
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
		
		responseJSON.put("schOfYear",schOfYear);
		responseJSON.put("daySchedule", daySchedule);
		
		
		out.println(responseJSON);
		
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
