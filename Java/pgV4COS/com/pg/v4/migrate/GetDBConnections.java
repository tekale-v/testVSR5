package com.pg.v4.migrate;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import matrix.db.Context;

import com.pg.ctrlm.custom.CtrlmJobContext;
//Added by 2015x.2 for incorporating Encryption_Decryption Framework - Starts
import com.pg.util.EncryptCrypto;
//Added by 2015x.2 for incorporating Encryption_Decryption Framework - Ends

public class GetDBConnections {

	public static Properties props = getProperties();

	public static Properties getProperties(){
		Properties props = new Properties();
		try{

			File file = new File("Constants.properties");
			System.out.println("Constants File found"+ file.exists());
			System.out.println("printing File Name: " + file.getName() + " size: "+ file.length());
			FileInputStream fis = new FileInputStream(file);
			props.load(fis);
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return props;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		getPLMContext();
	}

	public static Context getPLMContext()
	{
		Context context = null;		
		try {
			//Modified by DSM(Sogeti)-2018x.6 Dec CW for CTRLM Password Management Defect ID #45002 - Starts
			context = CtrlmJobContext.getCtrlmContext();
			java.util.logging.Logger.getLogger(GetDBConnections.class.getName()).log(java.util.logging.Level.INFO, "Connected context user : {0}", context.getUser());
			//Modified by DSM(Sogeti)-2018x.6 Dec CW for CTRLM Password Management Defect ID #45002 - Ends
		} catch (Exception e) {
			e.printStackTrace();
		}
		return context;
	}
	

	
	
	public static Statement getRMTConnection(){
		Connection conn =null;
		Statement stmt = null;
		String evUserName = props.getProperty("RMT_USER_NAME");
		String evPassword = props.getProperty("RMT_PASSWORD");
		String evURL = props.getProperty("RMT_URL");
		String evDriver = props.getProperty("DB_DRIVER");
		try {
			Class.forName(evDriver).newInstance();
			conn = DriverManager.getConnection(evURL, evUserName, evPassword);
			stmt = conn.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return stmt;
	}

}
