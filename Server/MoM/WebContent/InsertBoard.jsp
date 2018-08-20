<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import = "java.sql.*,database.MoMConnection"%>
<% request.setCharacterEncoding("UTF-8"); %>
<%! 
   Connection conn = null;
   PreparedStatement pstmt = null;
   ResultSet rs = null;
%>


<% 
	System.out.println(request.getRequestURL());
	int n=0;
	String clubNum=null;
	try{
	
		conn = MoMConnection.getConnection();
		
	
		System.out.println(request.getQueryString());
		//디비에 이름 같은 다른 사진이 저장될 때 중복 되지 않도록 저장하기 위함
	      String photo = request.getParameter("photo");
		if(!photo.equals("null")){
	      String photoOK = photo.substring(0,photo.lastIndexOf("."));//파일 이름
	       String extension = photo.substring(photo.lastIndexOf("."));//확장자
	       //System.out.println("extension:"+extension);
	       //System.out.println("photoOK:"+photoOK);
	       String select = "Select boardImg from mountain.board";
	      pstmt = conn.prepareStatement(select);
	      rs = pstmt.executeQuery();
	      int i=1;
	      
	      while(rs.next()){
	         //System.out.println(photo +"," + rs.getString(1));
	         if(photo.equals(rs.getString(1))){
	            String rsltPhoto= "";
	            while(true){
	               rsltPhoto = photoOK+i; 
	               //System.out.println("rsltPhoto확인:"+rsltPhoto);
	               if(!(rsltPhoto+extension).equals(rs.getString(1))){
	                  photo = rsltPhoto+extension;
	                  break;
	               }
	               i++;
	            }
	         }
	      }
	      System.out.println("변경후photo확인:"+photo);
	      
	      pstmt.close();
	      rs.close();
	      
		}
	      
      //페이스북 타임라인 형식으로 나타내기위해 dateformat을 일렬로 나열하여 계산하기위해 형식을 바꿔 집어넣는다
		   //페이스북 타임라인 형식으로 나타내기위해 dateformat을 일렬로 나열하여 계산하기위해 형식을 바꿔 집어넣는다
	      String sql = "INSERT INTO mountain.board(writerID,boardContent,good,boardrange,reply,clubNum,boardImg,boardDate,Latitude,Hardness,mName)";
	      sql+=" VALUES(?,?,0,?,0,?,?,date_format(now(),"+"'%Y%m%d%H%i%s'"+"),?,?,?)";
	      pstmt = conn.prepareStatement(sql);
	      pstmt.setString(1, request.getParameter("id"));
	      pstmt.setString(2, request.getParameter("content"));
	      pstmt.setString(3, request.getParameter("range"));
	      if(Integer.parseInt(request.getParameter("clubNum"))==0){
	    	  pstmt.setInt(4, 0);//동호회 번호가 0번이 없기때문에 조회를 가능하지 못하게 하기위하여 0으로 지정
	      }else{
	    	  pstmt.setInt(4, Integer.parseInt(request.getParameter("clubNum")));
	      }
	     		
	      pstmt.setString(5, photo.equals("null")?null:photo);
	      
	      String currentLatitude = request.getParameter("currentLatitude");
	      pstmt.setString(6, currentLatitude.equals("null")?null:currentLatitude);
	      
	      String currentLongitude = request.getParameter("currentLongitude");
	      pstmt.setString(7, currentLongitude.equals("null")?null:currentLongitude);
	      
	      String nearMt = request.getParameter("nearMt");
	      pstmt.setString(8, nearMt.equals("null")?null:nearMt);
	      pstmt.executeUpdate();
	      System.out.println("글이 등록되었습니다.");
	      out.println("글이 등록되었습니다.");
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