package com.pg.util;


/******************************************************************************
Program:        EncryptCrypto.java
Description:    Utility to encrypt a string and decrypt a string
Assumptions:    None
Added:       10/24/2016 Samrita Kulkarni
 ******************************************************************************/

import java.io.FileOutputStream;
import java.util.Properties;
import com.pg.plm.util.CryptoUtil;

public class EncryptCrypto {
	
	static int nLevel;
	
	/**
	 *  Generates a Secret key to be used for encryption/decryption and outputs to 
	 *  the property file
	 *  
	 *  @param : none
	 * 	@throws  Exception	- if operation fail 
	 */
	
	public static void generateKey() throws Exception 
	{
		
		try{
			
			Properties props = new Properties();
			props.load(EncryptCrypto.class.getClassLoader().getResourceAsStream("crypto.properties"));
			String strInitialKey=props.getProperty("key");
			nLevel=Integer.valueOf(props.getProperty("Level"));
			props.clear();
						
			for(int keyCount=0; keyCount<nLevel; keyCount++)
			{
				props.setProperty("SecretKey"+keyCount,CryptoUtil.makeSecretKey(strInitialKey) );	
			}
			
			 FileOutputStream out = new FileOutputStream("crypto.properties", true);
		      props.store(out, null);
		      out.flush();
		      out.close();
		}
		catch (Exception e) {
			System.out.println("exception "+e.toString());
			throw new Exception(e);
		}
			
	}
	
	/**
     *  Encrypts a string upto N level using Secret keys from property file.
	 *
     *  @param String Pass : String to be encrypted.
     *  @throws  Exception	- if operation fail
     */

	public static String encryptString(String strPassword) throws Exception {
			
		try{
		
			Properties props = new Properties();
			props.load(EncryptCrypto.class.getClassLoader().getResourceAsStream("crypto.properties"));
			
			nLevel=Integer.valueOf(props.getProperty("Level"));
			String strEncryptedPassword=strPassword;
		
			for (int keyCount=0; keyCount<nLevel; keyCount++)
			{
				strEncryptedPassword= CryptoUtil.encryptString(strEncryptedPassword,props.getProperty("SecretKey"+keyCount),"");
			}
			props.clear();
		
			return strEncryptedPassword;	
		} 
		catch (Exception e) {
			System.out.println("exception "+e.toString());
			throw new Exception(e);
		}
			
	}
		
		
	/**
	 *  Decrypts N level encrypted string  using Secret keys from property file.
	 *
	 *  @param String Pass : Encrypted string to be decrypted.
	 *  @throws  Exception	- if operation fail
	 */
	public static String decryptString(String encryptedPassword) throws Exception{
			
		try{

			Properties props = new Properties();
			props.load(EncryptCrypto.class.getClassLoader().getResourceAsStream("crypto.properties"));
						
			nLevel = Integer.valueOf(props.getProperty("Level"));
			String strDecryptedPassword = encryptedPassword;
			for (int keyCount=nLevel-1; keyCount>=0; keyCount--)
			{
				strDecryptedPassword = CryptoUtil.decryptString(strDecryptedPassword,props.getProperty("SecretKey"+keyCount),"");				
			}
			
			return strDecryptedPassword;	
		}
		catch (Exception e) {
			System.out.println("exception "+e.toString());
			throw new Exception(e);
		}
			
	}

	/**
	 *  Main method allows methods to be called from console
	 *
	 *  @param String[] args :      arg0="GenerateKey" | "Encrypt" | "Decrypt" 
	 *                              arg1=<original password>|<encrypted string>
	 *  @throws  Exception	- if operation fail
	 */ 
	
	public static void main(String[] args) throws Exception {

		try {
			
			String strReturn = null;
			
			if (args[0].equalsIgnoreCase("generateKey")) {
				generateKey();
							
			} else if (args[0].equalsIgnoreCase("Encrypt")) {
				
				strReturn = encryptString(args[1]);
								
			} else if (args[0].equalsIgnoreCase("Decrypt")) {
				
				strReturn = decryptString(args[1]);
				
			} else {
				System.out.println("Invalid Arguments \n Usage:  arg0=[GenerateKey]|[Encrypt]|[Decrypt] arg1=<Password>|<encrypted string>");
			}
			
			System.out.println(strReturn);
			
			} catch (Exception e) {
				System.out.println("exception "+e.toString());
				throw new Exception(e);
		}
	}
	
}
