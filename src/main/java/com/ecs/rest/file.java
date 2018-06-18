package com.ecs.rest;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.sql.Timestamp;

 
public class file {
    //static final String USER_NAME = "Asmita";
    //static final String PASSWORD = "little";
    //e.g. Assuming your network folder is: \my.myserver.netsharedpublicphotos
    //static final String NETWORK_FOLDER = "smb://DESKTOP-ASMITA/webapps/";
 
    public static void main(String USER_NAME,String PASSWORD,String srcLOC,String destLOC,String fileName) throws IOException ,FileNotFoundException{
    	//String fileLoc = "C:/apache-tomee-plume-7.0.3/webapps/ECS.war";
		//String fileName = "ECS.war";
		InputStream in = new FileInputStream(new File(srcLOC));
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println(timestamp);
		//new file().copyFiles(in, fileName);
		
    
 
   //public boolean copyFiles(InputStream fileContent, String fileName) {
        boolean successful = false;
         try{
                String user = USER_NAME + ":" + PASSWORD;
                System.out.println("User: " + user);
 
                NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(user);
                String path = destLOC + fileName;
                System.out.println("Path: " +path);
 
                SmbFile sFile = new SmbFile(path, auth);
                SmbFileOutputStream sfos = new SmbFileOutputStream(sFile);
                int read=0;
                byte[] b=new byte[4096];
                while((read = in.read(b)) != -1) 
                	sfos.write(b,0,read);
                
                sfos.close();
                successful = true;
                System.out.println("Successful");
                Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());
                System.out.println(timestamp1);
         }
         catch (Exception e) {
                successful = false;
                e.printStackTrace();
            }
        
    }
}