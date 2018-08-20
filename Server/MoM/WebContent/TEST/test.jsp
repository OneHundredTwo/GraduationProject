<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
    <%@ page import = "java.sql.*,org.json.*,database.MoMConnection, database.test, java.util.Calendar"%>
<% request.setCharacterEncoding("UTF-8");
response.setCharacterEncoding("UTF-8"); %>
<%! 
   Connection conn = null;
   PreparedStatement pstmt = null;
   ResultSet rs = null;
   
   Calendar textToCalendar(String txt){
       Calendar cal = Calendar.getInstance();
    

       int fields[] = {Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH};
		
       //split�޼ҵ�� ǥ�������� ����ǥ������ �Ű������� �ޱ⶧����, dot(.)�� ����ǥ���Ŀ��� '���'�� �ǹ��ϹǷ�, ����ǥ������ �ƴ� ���� '.'�� �������� �и��ϰ����Ѵٸ� 
       // '\\.'�� ����Ѵ�.
       String[] textbuf = txt.split("\\.");
       System.out.println(txt + " : " +textbuf.length);

       int i = 0;
       for(String token : textbuf){
           int token_int = Integer.parseInt(token);
           System.out.println(token_int);
           
           if(i == 1){ token_int -= 1;} //MONTH�� 0~11 : sibal...
           
           cal.set(fields[i++], token_int);
       }

       return cal;
   }
%>


<% 
	System.out.println(request.getRequestURL());
	JSONObject responseJSON = new JSONObject();
	JSONArray jsonArr = new JSONArray();
	int n=0;
	try{
		conn = MoMConnection.getConnection();
		String sql = "Update mountain.mountain set emdCd=? where mName = ?";
		pstmt = conn.prepareStatement(sql);
	
		/* String[] sannames = {"�񽽻�","�Ȱ���","���ϻ�","�źһ�","������","ȭ�ǻ�(�ߺ�)","��ɻ�","��ǻ�","õ����","�����","�빮��","���ǻ�","����(��õ)","������","��Ÿ��","���ǻ�","��ȭ��","���»�","������","��ϻ�","�����ջ�","����","������","������","��ǻ�","���׻�","����(����)","����","�����","�����","���ۻ�","��ȭ��","����","�ҹ��","������","�Ӹ���","��������","õ�»�","�ݼ���","���ǻ�","������","�����","��л�","������(������)","ĥ����","�����","��ǻ�","�����","����","��Ȼ�","�����","���̻�","�����","�����","����(����)","�߿���","��õ��","�����","��ϻ�","õ����","�η���","����","��߻�","Ȳ���","�����","û����","���κ�","���","�ֿջ�","������","��ȭ��","�ݻ�","���л�","����","õ����","�̸���","������(�뿵)","Ȳ�Ż�","������(�ڿ��)","�ҿ��","�¹��","����","�����","�����","�ݿ���","���ѻ�","���ǻ�","������","ġ�ǻ�","�Ⱥ���","������(������)","�ȿ���","����","Ȳ�ǻ�","���߻�","������","������","ȭ�ջ�","Ȳ����","�Ѷ��"};
		String[] emdcds = {"27710340","47720330","28710340","31710390","41820350","41820350","41360340","41820345","41360262","41820310","41830400","41630320","41650380","42230350","42170124","42210112","42750250","42810330","42830310","42800320","42770250","42780256","42110380","42110380","42110350","42230360","42770259","42720390","42760360","42760320","42720350","42790310","47280253","47210250","47250420","47250420","43740350","43740390","43150330","43150350","43800360","44710390","44230380","44810360","44790320","45790320","45210450","45730330","45800360","45740250","45180119","45720250","45720400","46830250","46230310","46710380","45770350","46150250","45770370","46800253","46820310","47130120","47280253","47280350","47280250","47920370","47940250","47820350","47750250","47113330","48820380","48840320","48125102","48270350","48330340","48220250","48220370","48890450","41150102","41650340","47920390","44250330","45790420","46710330","47850253","11305104","41290107","47820350","42130310","42720380","45730320","46770440","46910360","47150380","47840330","48860360","26410103","48740250","48870360","50110134"};
		
		for(int i=0; i<sannames.length; i++){
			pstmt.setString(1, emdcds[i]);
			pstmt.setString(2, sannames[i]);
			pstmt.executeUpdate();
			
		} */
		
		Calendar cal = Calendar.getInstance();
		System.out.println(" : "+cal.get(Calendar.YEAR)+"."+cal.get(Calendar.MONTH)+"."+cal.get(Calendar.DAY_OF_MONTH));
		
		String a = "2017.05.07";
		
		cal = textToCalendar(a);
		
		System.out.println(a+" : "+cal.get(Calendar.YEAR)+"."+cal.get(Calendar.MONTH)+"."+cal.get(Calendar.DAY_OF_MONTH));
		
		
		System.out.println(MoMConnection.path);
		System.out.println(test.path);
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
