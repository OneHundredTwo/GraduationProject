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
		�̰��� �� jsp���� ����.
		*/
		/*
		"id:"+Util.user.userid, // ��ĥ�� Util.user.getId()�� ����
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
	       // �����⿬���ϰ� String���� �ٲܰ�.
	      pstmt.setString(1, dateFormat.format(startTime)); // �ȵ���̵� ���߿� starttime���� ������.
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
	      
	      //���̵� �������� ����ų�, �ƴϸ� �ȵ���̵忡�� ���� �����°ɷ� �ٲܱ�...
	      
	      
	      sql = "select health_id from health where userid=? order by starttime desc limit 0,1";
	      pstmt = conn.prepareStatement(sql);
	      
	      pstmt.setString(1,id);
	      
	      rs = pstmt.executeQuery();
	      String health_id = null;
	      if(rs.next()){
	    	  health_id = rs.getString(1);
	      }
	      
	      out.println(health_id);
	      
	      
	      System.out.println("����� ����Ǿ����ϴ�.");
	      
		
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

