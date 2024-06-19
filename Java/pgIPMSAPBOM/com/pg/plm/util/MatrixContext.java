package com.pg.plm.util;

import java.net.ConnectException;

//import com.pg.plm.config.PLMIntegrationConfigurator;
//import com.pg.plm.config.PLMKeysConfigurator;
//import com.pg.plm.integration.IntegrationException;

import matrix.db.Context;
import matrix.util.MatrixException;

public class MatrixContext {

	
	public static Context getMatrixContext(String sUser, String sPass) {
		try {
			String server = ":bos";
//			String host = "//localhost:1199";
//			String host = "http://bdc-plm004.na.pg.com/enovia";
			String host = "http://bdlx4910:31004/enovia/";
//			String host = "http://bdlx4828.na.pg.com:8001/enovia/";
			
			
			Context _context = new Context(server, host);
			_context.setUser(sUser);
			_context.setPassword(sPass);
			
			_context.setVault("eService Production");
			_context.setProtocol("http:");
			_context.connect(true);
			return _context;
			
		} catch (MatrixException me	) {
			
			me.printStackTrace();
			return null;			
		}
	}
	
	public static Context getMatrixContext(String sUser, String sPass, String host) {
		try {
			String server = ":bos";
//			String host = "//localhost:1199";
//			String host = "http://bdc-plm004.na.pg.com/enovia";
//			String host = "http://bdlx4828.na.pg.com:8001/enovia/";
			
			
			Context _context = new Context(server, host);
			_context.setUser(sUser);
			_context.setPassword(sPass);
			
			_context.setVault("eService Production");
			_context.setProtocol("http:");
			_context.connect(true);
			return _context;
			
		} catch (MatrixException me	) {
			
			me.printStackTrace();
			return null;			
		}
	}
	
	/*public static Context getIntegrationMatrixContext() 
	throws IntegrationException{
		String sUser = PLMIntegrationConfigurator.getProperty("IntegrationUser");
		String sPass = PLMIntegrationConfigurator.getProperty("IntegrationUserPass");
		try {
			sPass=CryptoUtil.decryptString(sPass, PLMKeysConfigurator.getProperty("PLMKey"), "");
		} catch (Exception e) {
			throw new IntegrationException(e);
		}
		
		String server = ":bos";

//			String host = "//localhost:1199";
//			String host = "http://bdc-plm004.na.pg.com/enovia";
		String host = PLMIntegrationConfigurator.getProperty("Host");
System.out.println(host);
		try {
			Context cTmp = new Context(server, host);
			cTmp.setUser(sUser);
			cTmp.setPassword(sPass);
			
			cTmp.setVault("eService Production");
			cTmp.setProtocol("http:");
			cTmp.connect(true);
			return cTmp;
		} catch (MatrixException e) {
			e.printStackTrace();
			try {
				String backupHost = PLMIntegrationConfigurator.getProperty("BackupHost");

				Context cTmp = new Context(server, backupHost);
				cTmp.setUser(sUser);
				cTmp.setPassword(sPass);
				
				cTmp.setVault("eService Production");
				cTmp.setProtocol("http:");
				cTmp.connect(true);
				return cTmp;						
			} catch (MatrixException me) {
				throw new IntegrationException(me);
			}
	
		}


	}*/
}
