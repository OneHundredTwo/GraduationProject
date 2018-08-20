<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ page import = "java.sql.*,org.json.*,database.MoMConnection, java.text.SimpleDateFormat, java.util.Calendar"%>
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
		String sql = "select * from health where userid = ? and hDate between ? and ? order by starttime desc";
		pstmt = conn.prepareStatement(sql);
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sformat = new SimpleDateFormat("yyyy�� MM�� dd��");
		SimpleDateFormat lformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		
		String id = request.getParameter("id");
		
		String stDt = request.getParameter("stTime");
		String enDt = request.getParameter("endTime");
		
		Calendar cSt = Calendar.getInstance();
		Calendar cEnd = Calendar.getInstance();
		
		cSt.setTimeInMillis(Long.parseLong(stDt));
		cEnd.setTimeInMillis(Long.parseLong(enDt));
		
		
		stDt = format.format(cSt.getTime());
		enDt = format.format(cEnd.getTime());
		
		
		pstmt.setString(1,id);
		pstmt.setString(2,stDt);
		pstmt.setString(3,enDt);
		
		rs = pstmt.executeQuery();
		
		
		while(rs.next()){
			JSONObject item = new JSONObject();
			
			Long start = lformat.parse(rs.getString("starttime")).getTime();
			Long end = lformat.parse(rs.getString("endtime")).getTime();
			
			item.put("ex_id", rs.getString("health_id"));
			
			item.put("ex_pic", rs.getString("picpath"));
			item.put("ex_day",sformat.format(format.parse(rs.getString("hDate"))));
			item.put("ex_start",start);
			item.put("ex_end",end);
			item.put("ex_location",rs.getString("location"));
			item.put("step", rs.getInt("step"));
			item.put("calories",rs.getInt("calories"));
			item.put("distance", rs.getInt("distance"));
			item.put("ex_time", rs.getString("exer_time"));
			jsonArr.put(item);
		}
		
		responseJSON.put("items",jsonArr);
		
		out.println(responseJSON.toString());
		
		
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
