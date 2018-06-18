package com.ecs.rest;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.RandomStringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import com.ibatis.common.jdbc.ScriptRunner;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Path("/services")
public class services {

	@GET
	@Path("/login")
	public Response login(@QueryParam("user") String user,@QueryParam("pass") String pass) throws ClassNotFoundException, SQLException, InterruptedException, URISyntaxException {
		// Load the JDBC driver
		Class.forName("com.mysql.jdbc.Driver");
		// Connect to a database
		Connection con= DriverManager.getConnection("jdbc:mysql://localhost/ecs","root","");
		Statement stmt=con.createStatement();
		String query = "SELECT * FROM user_profiles WHERE User_Nam='"+user+"' AND Secret_Key='"+pass+"';";
		ResultSet rs = stmt.executeQuery(query) ;
		String s="";
		if(rs.next()){
			s="Login successful";
			try {
		        java.net.URI location = new java.net.URI("../adminHome.html");
		        throw new WebApplicationException(Response.temporaryRedirect(location).build());
		    } catch (URISyntaxException e) {
		        e.printStackTrace();
		    }
		}
		else
			s="Login Failed";
		return Response.status(200).entity(s).build();
	}
	@GET
	@Path("/custLogin")
	public Response custLogin(@QueryParam("user") String user,@QueryParam("pass") String pass) throws ClassNotFoundException, SQLException, InterruptedException, URISyntaxException {
		// Load the JDBC driver
		Class.forName("com.mysql.jdbc.Driver");
		// Connect to a database
		Connection con= DriverManager.getConnection("jdbc:mysql://localhost/ecs","root","");
		Statement stmt=con.createStatement();
		String query = "SELECT * FROM cust_master WHERE cust_username ='"+user+"' AND cust_pwd='"+pass+"';";
		ResultSet rs = stmt.executeQuery(query) ;
		String s="";
		if(rs.next()){
			s="Login successful";
			try {
		        java.net.URI location = new java.net.URI("../custHome.jsp?custId="+rs.getInt("cust_id"));
		        throw new WebApplicationException(Response.temporaryRedirect(location).build());
		    } catch (URISyntaxException e) {
		        e.printStackTrace();
		    }
		}
		else
			s="Login Failed";
		return Response.status(200).entity(s).build();
	}
	@GET
	@Path("/customer")
	public Response cust_onboard(@QueryParam("custName") String custName,@QueryParam("userName") String userName,@QueryParam("email") String email,@QueryParam("appId") String appId,@QueryParam("appName") String appName,@QueryParam("app_server") String appServer,@QueryParam("app_server_list") String appServerList,@QueryParam("db_server") String dbServer,@QueryParam("db_server_list") String dbServerList) 
					throws ClassNotFoundException, SQLException, InterruptedException {

		Connection conn = null,conn1 = null,conn2= null,conn3 = null,conn12 = null;
		Statement stmt = null,stmt1 = null,stmt2 = null, stmt3 = null,stmt12 = null;
		
		String s;
		int custId;
		//String sql_loc = "C:\\apache-tomee-plume-7.0.3\\sql_file\\"+appId+"_"+appName+".sql";
		//String war_loc = "C:\\apache-tomee-plume-7.0.3\\webapps\\"+appId+"_"+appName+".war";
		
		//String sql_location = "C:\\apache-tomee-plume-7.0.3\\sql_file\\"+id+"_"+appName+".sql";
		String sql_loc = "C:\\Program Files\\Apache Software Foundation\\Tomcat 8.0\\sql_file\\"+appId+"_"+appName+".sql";
		//String war_location = "C:\\apache-tomee-plume-7.0.3\\webapps\\"+id+"_"+appName+".war";
		String war_loc = "C:\\Program Files\\Apache Software Foundation\\Tomcat 8.0\\war_file\\"+appId+"_"+appName+".war";
				
		File sql_file = new File(sql_loc);
		File war_file = new File(war_loc);
		if((war_file.exists() && !war_file.isDirectory()) && (sql_file.exists() && !sql_file.isDirectory())) {
			try{
				//STEP 2: Register JDBC driver
				Class.forName("com.mysql.jdbc.Driver");
				//STEP 3: Open a connection
				System.out.println("Connecting to a selected database...");
				conn = DriverManager.getConnection("jdbc:mysql://localhost/ecs","root","");
				System.out.println("Connected database successfully...");
				//STEP 4: Execute a query
				System.out.println("Inserting records into the table...");
				
				conn3 = DriverManager.getConnection("jdbc:mysql://localhost/ecs","root","");
				stmt3 = conn3.createStatement();
				String query = "SELECT * FROM cust_master WHERE cust_username = '"+userName+"'";
				ResultSet rset = stmt3.executeQuery(query);
				
				System.out.println("Query executed");
				
				if(rset.next())
				{
					System.out.println("Inside If");
					//rset.next();
					custId = rset.getInt("cust_id");
					System.out.println("Customer exist");
				}
				else {
					System.out.println("Inside Else");
					stmt = conn.createStatement();
					String pwd = randomGenerator();
					String sql = "INSERT INTO cust_master(cust_name,cust_username,cust_email,cust_pwd) VALUES ('"+custName+"','"+userName+"','"+email+"','"+pwd+"')";
					int j = stmt.executeUpdate(sql);
					
					if(j>0)
						System.out.println("Customer created");
					else
						System.out.println("Customer creation failed");
					
					sql = "SELECT * FROM cust_master WHERE cust_username = '"+userName+"' ORDER BY cust_id DESC";
					ResultSet rs = stmt.executeQuery(sql) ;
					rs.next();
					custId = rs.getInt("cust_id");
				}
				String appip="";
				String dbip = "";
				stmt = conn.createStatement();
				String query6 = "SELECT * from resources WHERE id = "+appServerList;
				ResultSet rs1 = stmt.executeQuery(query6);
				if(rs1.next()){
					appip = rs1.getString("ip");
				}
				
				String query7 = "SELECT * from resources WHERE id = "+dbServerList;
				ResultSet rs2 = stmt.executeQuery(query7);
				if(rs2.next()){
					dbip = rs2.getString("ip");
				}	
				
				String db = custId+"_"+appId;
				stmt = conn.createStatement();
				String sql3 = "INSERT INTO cust_app(cust_id,app_id,appServer_type,dbServer_type,appServer_id,dbServer_id,app_url,db_url) VALUES ('"+custId+"','"+appId+"','"+appServer+"','"+dbServer+"','"+appServerList+"','"+dbServerList+"','"+appip+"','"+dbip+"')";
				int k = stmt.executeUpdate(sql3);				
				if(k>0)
					System.out.println("Customer mapped");
				else
					System.out.println("Customer mapping failed");
				
				
				
				
				//MOVE WAR
				Connection con= DriverManager.getConnection("jdbc:mysql://localhost/ecs","root","");
				Statement stmt4=con.createStatement();
				String query4 = "SELECT * from resources WHERE id = "+appServerList;
				ResultSet rs = stmt.executeQuery(query4);
				
				JSONParser parser = new JSONParser();
				
				if(rs.next())
				{
					Object obj = parser.parse((String)rs.getString("servers"));
					JSONObject jsonObject = (JSONObject) obj;
					JSONObject json = (JSONObject) jsonObject.get("network");
					System.out.println("UserName : "+(String)json.get("username"));
					System.out.println("Password : " +(String)json.get("password"));
					file.main((String)json.get("username"), (String)json.get("password"), war_loc, "smb://"+json.get("path")+"/webapps/",appId+"_"+appName+".war" );		
					
					
				}	
				//Create Instance
				Connection con1= DriverManager.getConnection("jdbc:mysql://localhost/ecs","root","");
				Statement stmt5=con1.createStatement();
				String query5 = "SELECT * from resources WHERE id = "+dbServerList;
				ResultSet rs3 = stmt.executeQuery(query5);
				
				if(rs3.next()){
					String ip = rs3.getString("ip");
					System.out.println(ip);
					conn12 = DriverManager.getConnection("jdbc:mysql://"+ip,"root","");
					stmt12 = conn12.createStatement();
					String sql12 = "CREATE DATABASE " + db;
					int l = stmt12.executeUpdate(sql12);
					if(l>0)
						System.out.println("Database created");
					else
						System.out.println("Database creation failed.");
					
					
					conn1 = DriverManager.getConnection("jdbc:mysql://"+ip+"/"+db,"root","");
				ScriptRunner runner = new ScriptRunner(conn1, false, false);
				runner.runScript(new BufferedReader(new FileReader(sql_loc)));
				conn1.close();
				System.out.println("Instance created...");
				s = "SUCCESSFUL";
				
				conn2 = DriverManager.getConnection("jdbc:mysql://localhost/ecs","root","");
				
				stmt2 = conn2.createStatement();				
				int x = stmt2.executeUpdate("UPDATE cust_master SET cust_status = 'ACTIVE' WHERE cust_id = "+custId);
				
				if(x>0)
					System.out.println("Status updated");
				else
					System.out.println("Status updation failed.");
				conn2.close(); 		
				}
					
			}catch(SQLException se){
				//Handle errors for JDBC
				se.printStackTrace();
			}catch(Exception e){
				//Handle errors for Class.forName
				e.printStackTrace();
			}finally{
				//finally block used to close resources
				try{
					if(stmt!=null)
						conn.close();
				}catch(SQLException se){
				}// do nothing
				try{
					if(conn!=null)
						conn.close();
				}catch(SQLException se){
					se.printStackTrace();
				}//end finally try
			}//end try
	
	
			return Response.status(200).entity("SUCCESS").build();
		}
		else {
			System.out.println("File doesnot exists!!");
			return Response.status(200).entity("FAILED").build();
		}
	}

	public String randomGenerator(){
		//String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$&";
		//String pwd = RandomStringUtils.random( 8, characters ); //Lang JAR
		String pwd = "123";
		return pwd;
	}
	
	@POST
	@Path("/appform")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response appformOnboard(@FormDataParam("file_war") InputStream uploadedInputStream,@FormDataParam("file_war") FormDataContentDisposition fileDetail,@FormDataParam("file_schema") InputStream uploadedInputStreamsql,@FormDataParam("file_schema") FormDataContentDisposition fileDetailsql,@FormDataParam("app_name") String appName) throws ClassNotFoundException, SQLException, IOException{
	
		// Load the JDBC driver
		Class.forName("com.mysql.jdbc.Driver");
		// Connect to a database
		Connection con= DriverManager.getConnection("jdbc:mysql://localhost/ecs","root","");
		Statement stmt=con.createStatement();

		String query = "INSERT INTO app_master (app_name) VALUES ('"+appName+"');";
		stmt.executeUpdate(query);

		ResultSet rs = stmt.executeQuery("SELECT app_id FROM app_master WHERE app_name='"+appName+"';") ;
		rs.next();
		int id = rs.getInt("app_id");
		query = "UPDATE app_master SET app_url = 'http://localhost:8080/"+id+"_"+appName+"' WHERE app_id = "+id;
		stmt.executeUpdate(query);
		//String sql_location = "C:\\apache-tomee-plume-7.0.3\\sql_file\\"+id+"_"+appName+".sql";
		String sql_location = "C:\\Program Files\\Apache Software Foundation\\Tomcat 8.0\\sql_file\\"+id+"_"+appName+".sql";
		//String war_location = "C:\\apache-tomee-plume-7.0.3\\webapps\\"+id+"_"+appName+".war";
		String war_location = "C:\\Program Files\\Apache Software Foundation\\Tomcat 8.0\\war_file\\"+id+"_"+appName+".war";
		
		//saving WAR file  
		try {  
			FileOutputStream out_war = new FileOutputStream(new File(war_location));  
			int read = 0;   
			while ((read = uploadedInputStream.read())!=-1) {  
				out_war.write(read);  
			}  
			out_war.flush();  
			out_war.close();  
		} catch (IOException e) {e.printStackTrace();}

		
		//saving SQL file  
		try {  
			FileOutputStream out_sql = new FileOutputStream(new File(sql_location));  
			int read = 0;   
			while ((read = uploadedInputStreamsql.read())!=-1) {  
				out_sql.write(read);  
			}  
			out_sql.flush();  
			out_sql.close();  
		} catch (IOException e) {e.printStackTrace();}


		stmt.executeUpdate("UPDATE app_master SET app_status='ACTIVE' WHERE app_id='"+id+"';");

		String s="Application onboarded";
		return Response.status(200).entity(s).build();

	}
	
	@GET
	@Path("/cust")
	@Produces({MediaType.TEXT_HTML})
	public String viewHome(@QueryParam("user") String user,@QueryParam("pass") String pass) throws ClassNotFoundException, SQLException
	{
		Class.forName("com.mysql.jdbc.Driver");
		// Connect to a database
		Connection con= DriverManager.getConnection("jdbc:mysql://localhost/ecs","root","");
		Statement stmt=con.createStatement();
		String sql = "SELECT * FROM cust_master WHERE cust_username = '"+user+"' AND cust_pwd = '"+pass+"'";
		ResultSet rset = stmt.executeQuery(sql);
		if(rset.next())
		{
			int custId = rset.getInt("cust_id");
			String query = "SELECT * FROM cust_app WHERE cust_id='"+custId+"';";
			ResultSet rs = stmt.executeQuery(query) ;
			Connection conn2 = DriverManager.getConnection("jdbc:mysql://localhost/ecs","root","");
			Statement stmt1=conn2.createStatement();
			String app_query = "SELECT * FROM app_master WHERE app_id='";
			String result="";
			int app;
			ResultSet rs1;
			while(rs.next())
			{
				app=rs.getInt("app_id");
				rs1 = stmt1.executeQuery(app_query+app+"';") ;
				rs1.next();
				result+="<li><a href=http://"+rs.getString("app_url")+":8080/"+app+"_"+rs1.getString("app_name")+" target='_blank' >"+rs1.getString("app_name")+"</a></li>";
				//result+="<li><a href="+rs1.getString("app_url")+" target='_blank' >"+rs1.getString("app_name")+"</a></li>";
			}
			String body="<h2>Customer Id: "+custId+"</h2>";
			String s="<html><body>"+body+"<ul>"+result+"</ul></body></html>";
		   return s;
		}
		else {
			String s = "Login Failed";
			return s;
		}
	}
	@SuppressWarnings("unchecked")
	@GET
	@Path("/server")
	@Produces("application/json")
	public String getAppMachines(@QueryParam("val") String mType) throws ClassNotFoundException, SQLException, IOException, ParseException{
		Class.forName("com.mysql.jdbc.Driver");
		Connection con= DriverManager.getConnection("jdbc:mysql://localhost/ecs","root","");
		Statement stmt=con.createStatement();
		String query = "SELECT * from resources WHERE app_server=1;";
		ResultSet rs = stmt.executeQuery(query);
		JSONArray machines = new JSONArray();
		JSONParser parser = new JSONParser();
		String s="";
		while(rs.next())
		{
			Object obj = parser.parse((String)rs.getString("servers"));
			JSONObject jsonObject = (JSONObject) obj;
			JSONObject json = (JSONObject) jsonObject.get("application");
			JSONObject json2 = (JSONObject) json.get("servers");
			if(mType.equals((String)json.get("type"))){
			machines.add(jsonObject);
			if(!(mType.equals("dedicated") &&  ((String)json2.get("status")).equals("busy")))
				s+="<option value='"+rs.getString("id")+"'>"+rs.getString("name")+"</option>";
			}
			
		}
		
		return s;
	}
	
	@SuppressWarnings("unchecked")
	@GET
	@Path("/db")
	@Produces("application/json")
	public String getDBMachines(@QueryParam("val") String mType) throws ClassNotFoundException, SQLException, IOException, ParseException{
		Class.forName("com.mysql.jdbc.Driver");
		Connection con= DriverManager.getConnection("jdbc:mysql://localhost/ecs","root","");
		Statement stmt=con.createStatement();
		String query = "SELECT * from resources WHERE db_server=1;";
		ResultSet rs = stmt.executeQuery(query);
		
		JSONParser parser = new JSONParser();
		String s="";
		while(rs.next())
		{
			Object obj = parser.parse((String)rs.getString("servers"));
			JSONObject jsonObject = (JSONObject) obj;
			JSONObject json = (JSONObject) jsonObject.get("database");
			JSONObject json2 = (JSONObject) json.get("servers");
			if(mType.equals((String)json.get("type"))){
				if(!(mType.equals("dedicated") &&  ((String)json2.get("status")).equals("busy")))
					s+="<option value='"+rs.getString("id")+"'>"+rs.getString("name")+"</option>";
			}
		}
		
		return s;
	}
	
	@GET
	@Path("/machine")
	@Produces({MediaType.TEXT_HTML})
	public String getMachineDetails(@QueryParam("val") String mId) throws ClassNotFoundException, SQLException, ParseException{
		Class.forName("com.mysql.jdbc.Driver");
		Connection con= DriverManager.getConnection("jdbc:mysql://localhost/ecs","root","");
		Statement stmt=con.createStatement();
		String query = "SELECT * from resources WHERE id="+mId+";";
		ResultSet rs = stmt.executeQuery(query);
		if(rs.next()){
			JSONParser parser = new JSONParser();
			Object obj = parser.parse((String)rs.getString("machine_details"));
			JSONObject jsonObject = (JSONObject) obj;
			return jsonObject.toString();
		}
			return "";
	}
	
}