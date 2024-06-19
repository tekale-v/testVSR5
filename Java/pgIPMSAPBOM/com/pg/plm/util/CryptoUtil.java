/******************************************************************************
###############################################################################
#
#   Copyright (c) 2001 Procter & Gamble, Inc.  All Rights Reserved.
#   This program contains proprietary and trade secret information of
#   Procter & Gamble, Inc.  Copyright notice is precautionary only and does not
#   evidence any actual or intended publication of such program.
#
###############################################################################
******************************************************************************/
/******************************************************************************
Program:	    CSSCryptoUtil.java
Description: 	Utility to generate a key, encript a string, or decrypt a string
Assumptions:	1) none
Modified:		9/30/02 Jeremy Kirst
******************************************************************************/

package com.pg.plm.util;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.SecureRandom;
import java.security.Security;
//import sun.misc.BASE64Decoder;
import java.util.Base64;
import java.util.Base64.Decoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtil {

	private final static String ENCRYPT_ALG = "DESede";
	private final static int KEY_SIZE = 112;

    /**
     *  Generates a key to be used for encryption/decryption and outputs to 
	 *  the console and a file if a path and a file name are given.
	 *
     *  @param String sFile : Path and file name for output file.
     */	
	public static String makeSecretKey(String sFile) throws Exception {

		try {
			// use default SUN provider
			Security.addProvider(java.security.Security.getProvider("SunJCE"));
		
			KeyGenerator generator = KeyGenerator.getInstance(ENCRYPT_ALG, "SunJCE");
			generator.init(KEY_SIZE, new SecureRandom());
			SecretKey secretkey = generator.generateKey(); 

			byte[] encodedKey = secretkey.getEncoded();

			// encode bytes into a base 64 string for easier storage
			String b64encodedKey = Base64.getEncoder().encodeToString(encodedKey);
	
			if (!sFile.equals("")) {
				// save key to a file
				FileOutputStream out = new FileOutputStream(sFile); 
				out.write(b64encodedKey.getBytes());
				out.flush(); 
			}

			return b64encodedKey;

		} catch (Exception e) {
			return e.toString();
		}
	}

    /**
     *  Encrypts a string based on key from parameter or file.
	 *
     *  @param String sClearText : String to be encrypted.
     *  @param String sKey : Key for encryption.
     *  @param String sFile : Path and file name for key input file (overides sKey).
     */	
	public static String encryptString(String sClearText, String sKey, String sFile) throws Exception {
		
		try {

			// use default SUN provider
			Security.addProvider(java.security.Security.getProvider("SunJCE"));

			//BASE64Decoder keydecoder = new BASE64Decoder();
			Decoder keydecoder = Base64.getDecoder(); //added for 22x upgrade
			byte[] decodedKeyC = null;

			if (sFile.equals("")) {
				// use sKey string for key
				//code changed for 22x upgrade - Starts
				decodedKeyC = keydecoder.decode(sKey);
				//code changed for 22x upgrade - Ends
			} else {
				// read in key from file
				FileInputStream in = new FileInputStream(sFile);
				decodedKeyC = new byte[ in.available() ];
				in.read(decodedKeyC);
				in.close();

				// convert from byte[] to String
				String b64decodedKeyC = new String(decodedKeyC,"UTF8");

				// decode base 64 String into byte[]
				//code changed for 22x upgrade - Starts
				decodedKeyC = keydecoder.decode(b64decodedKeyC);
				//code changed for 22x upgrade - Ends
			}

			SecretKeySpec desKeySpec = new SecretKeySpec(decodedKeyC, ENCRYPT_ALG); 
			SecretKey key = (javax.crypto.SecretKey) desKeySpec;

			// Set up cipher for encrypting
	        Cipher ecipher;
            ecipher = Cipher.getInstance(ENCRYPT_ALG, "SunJCE");
            ecipher.init(Cipher.ENCRYPT_MODE, key);

            // Encode the string into bytes using utf-8
            byte[] utf8 = sClearText.getBytes("UTF8");

            // Encrypt
            byte[] enc = ecipher.doFinal(utf8);

            // Encode bytes to base64 to get a string
            String sEncrypted = Base64.getEncoder().encodeToString(enc);
			sEncrypted = sEncrypted.trim();

			return sEncrypted;

		} catch (Exception e) {
			return e.toString();
		}
	}

    /**
     *  Decrypts a string based on key from parameter or file.
	 *
     *  @param String sEncrypted : Encrypted string to be decrypted.
     *  @param String sKey : Key for decryption.
     *  @param String sFile : Path and file name for key input file (overides sKey).
     */	
	public static String decryptString(String sEncrypted, String sKey, String sFile) throws Exception {

		try {

			// use default SUN provider
			Security.addProvider(java.security.Security.getProvider("SunJCE"));

			//BASE64Decoder keydecoder = new BASE64Decoder();
			Decoder keydecoder = Base64.getDecoder(); //added for 22x upgrade
			byte[] decodedKeyC = null;

			if (sFile.equals("")) {
				// use sKey string for key
				//code changed for 22x upgrade - Starts
				decodedKeyC = keydecoder.decode(sKey);
				//code changed for 22x upgrade - Ends
			} else {
				// read in key from file
				FileInputStream in = new FileInputStream(sFile);
				decodedKeyC = new byte[ in.available() ];
				in.read(decodedKeyC);
				in.close();

				// convert from byte[] to String
				String b64decodedKeyC = new String(decodedKeyC,"UTF8");

				// decode base 64 String into byte[]
				//code changed for 22x upgrade - Starts
				decodedKeyC = keydecoder.decode(b64decodedKeyC);
				//code changed for 22x upgrade - Ends
			}

			SecretKeySpec desKeySpec = new SecretKeySpec(decodedKeyC, ENCRYPT_ALG); 
			SecretKey key = (javax.crypto.SecretKey) desKeySpec;

	        Cipher dcipher;
            dcipher = Cipher.getInstance(ENCRYPT_ALG, "SunJCE");
            dcipher.init(Cipher.DECRYPT_MODE, key);
			
			//code changed for 22x upgrade - Starts
			Decoder decoder = Base64.getDecoder(); 
			byte[] decode_input_bytes = decoder.decode(sEncrypted);
			//code changed for 22x upgrade - Ends
			byte[] decode_output_bytes = dcipher.doFinal( decode_input_bytes );
			String decoded_string = new String( decode_output_bytes, "UTF8" );
			String sDecrypted = decoded_string.trim();

			return sDecrypted;

		} catch  (Exception e) {
			return e.toString();
		}
	}

    /**
     *  Main allows methods to be called from console
	 *
     *  @param String[] args : 	arg0="Encrypt" | "Decrypt" | "MakeKey"
	 *							arg1=<encrypted string> 
	 *							arg2=<key> 
	 * 							arg3=<output file>
     */	
	public static void main(String[] args) throws Exception {

		try {

			String sReturn = "";

			if (args[0].equals("MakeKey")) {
				System.out.println("Making Key...");
				sReturn = makeSecretKey(args[3]);
				System.out.println("Key=" + sReturn);
			} else if (args[0].equals("Encrypt")) {
				System.out.println("Encrypting String...");
				sReturn = encryptString(args[1],args[2],args[3]);
				System.out.println("Encrypted String=" + sReturn);
			} else if (args[0].equals("Decrypt")) {
				System.out.println("Decrypting String...");
				sReturn = decryptString(args[1],args[2],args[3]);
				System.out.println("Decrypted String=" + sReturn);
			} else {
				System.out.println("Usage:  arg0=[Encrypt]|[Decrypt]|[MakeKey] arg1=<encrypted string> arg2=<key> arg3=<output file> ");
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
