<%@page import="java.sql.*"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.sql.Statement"%>
<%@page import="java.sql.DriverManager"%>
<%@page import="java.net.ConnectException"%>

<%! Statement stmt=null,stmt1=null; Connection con = null; ResultSet rs=null,rs1=null; %> 
<!DOCTYPE html>
<html lang="en">
<%
try{
Class.forName("com.mysql.jdbc.Driver");
// Connect to a database
con= DriverManager.getConnection("jdbc:mysql://localhost/ecs","root","");
stmt=con.createStatement();
stmt1 =con.createStatement();
}catch(Exception e){out.println(e);}
%>

<head>
	<title>ECS</title>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<!-- Animate.css -->
	<link rel="stylesheet" href="css1/animate.css">
	<!-- Icomoon Icon Fonts-->
	<link rel="stylesheet" href="css1/icomoon.css">
	<!-- Bootstrap  -->
	<link rel="stylesheet" href="css1/bootstrap.css">
	<!-- Flexslider  -->
	<link rel="stylesheet" href="css1/flexslider.css">
	<!-- Theme style  -->
	<link rel="stylesheet" href="css1/style.css">


</head>
<body>
		<div id="fh5co-pricing-section"  style="background-image: url('images/slide_1.jpg');">
		<div class="container">
					<center><h1>CUSTOMER DASHBOARD</h1></center>
					<%
					try{
					rs = stmt.executeQuery("SELECT * FROM cust_app where cust_id = '"+ request.getParameter("custId")+"'");
					//out.println("CUST ID "+request.getParameter("custId"));
					while(rs.next())
					{
						try{
							//out.println("\nJUST AFTER TRY");
							//out.println("APP ID " + Integer.parseInt(rs.getString("app_id")));
							String res = "SELECT * FROM app_master where app_id = "+ Integer.parseInt(rs.getString("app_id"));
							//out.println("\nQUERY : "+res);
						try{
							rs1 = stmt1.executeQuery("SELECT * FROM app_master where app_id = "+ Integer.parseInt(rs.getString("app_id")));
						}catch(Exception e) {out.println("\nAFTER RS1 "+ e); }
							//out.println("\nAFTER RS1");
						while(rs1.next())
						{
							String result ="http://"+rs.getString("app_url")+":8080/"+rs1.getInt("app_id")+"_"+rs1.getString("app_name");
							//out.println("\nRESULT "+result);
					%>
					<div class="col-md-4 col-xs-8 push-xs-2 animate-box fadeInUp animated">
						<div class="price-box">
						
							<div class="price"><div style="height:55px"></div><h3><%=rs1.getString("app_name") %></h3></div>
							<a href=<%=result %> target="_blank" class="btn btn-select-plan btn-sm">GO TO APPLICATION</a>
							
						</div>
					</div>
					<% } 
						}catch(Exception e) {out.println("erfdvcbnmjh :  "+e); }
						}
					}catch(Exception e) {out.println(e);}%>
					
              			
			</div>
	</div>


	
<!--===============================================================================================-->
	<script src="vendor/jquery/jquery-3.2.1.min.js"></script>
<!--===============================================================================================-->
	<script src="vendor/animsition/js/animsition.min.js"></script>
<!--===============================================================================================-->
	<script src="vendor/bootstrap/js/popper.js"></script>
	<script src="vendor/bootstrap/js/bootstrap.min.js"></script>
<!--===============================================================================================-->
	<script src="vendor/select2/select2.min.js"></script>
<!--===============================================================================================-->
	<script src="vendor/daterangepicker/moment.min.js"></script>
	<script src="vendor/daterangepicker/daterangepicker.js"></script>
<!--===============================================================================================-->
	<script src="vendor/countdowntime/countdowntime.js"></script>
<!--===============================================================================================-->
	<script src="js/main.js"></script>

</body>
</html>