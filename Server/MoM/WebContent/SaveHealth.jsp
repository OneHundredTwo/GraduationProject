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
		String sql = "Insert into mountain.health"
				+"(hDate,starttime,endtime,step,calories,distance,userid,exer_time,picpath,location,health_id)"
				+" values(?,?,?,?,?,?,?,?,?,?,(concat(?,now(3)+0))) ";
		pstmt = conn.prepareStatement(sql);
		
		/*
		이곳에 각 jsp내용 구현.
		*/
		/*
		"id:"+Util.user.userid, // 합칠때 Util.user.getId()로 수정
        "exer_time:"+result.getEx_time(),
        "startTime:"+result.getEx_start(),
        "endTime:"+result.getEx_end(),
        "step:"+result.getStep(),
        "calories:"+result.getCalories(),
        "distance:"+result.getDistance(),
        "location:"+result.getEx_location(),
        "picpath:"+result.getEx_pic()
		*/
		
		Date startTime = new Date(Long.parseLong(request.getParameter("startTime")));
		  Date endTime =  new Date(Long.parseLong(request.getParameter("endTime")));
		  
		  String id = request.getParameter("id");
		  
		  String exer_time = request.getParameter("exer_time");
		  String picpath = request.getParameter("picpath");
		  String location = request.getParameter("location");
		  
		  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	       // 충전기연결하고 String으로 바꿀것.
	      pstmt.setString(1, dateFormat.format(startTime)); // 안드로이드 나중에 starttime으로 넣을것.
	      pstmt.setString(2, dateFormat.format(startTime));
	      pstmt.setString(3, dateFormat.format(endTime));
	      pstmt.setInt(4, Integer.parseInt(request.getParameter("step")));
	      pstmt.setInt(5, Integer.parseInt(request.getParameter("calories")));
	      pstmt.setInt(6, Integer.parseInt(request.getParameter("distance")));
	      pstmt.setString(7, id);
	      pstmt.setString(8,exer_time);
	      pstmt.setString(9,picpath);
	      pstmt.setString(10,location);
	      pstmt.setString(11,id);
	      
	      
	      pstmt.executeUpdate(); 
	      pstmt.close();
	      
	      //아이디를 서버에서 만들거나, 아니면 안드로이드에서 만들어서 보내는걸로 바꿀까...
	      
	      
	      sql = "select health_id from health where userid=? order by starttime desc limit 0,1";
	      pstmt = conn.prepareStatement(sql);
	      
	      pstmt.setString(1,id);
	      
	      rs = pstmt.executeQuery();
	      String health_id = null;
	      if(rs.next()){
	    	  health_id = rs.getString(1);
	      }
	      
	      out.println(health_id);
	      
	      
	      System.out.println("운동량이 저장되었습니다.");
	      
		
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

