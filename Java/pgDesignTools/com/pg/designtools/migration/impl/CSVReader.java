package com.pg.designtools.migration.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.StringUtil;
import com.pg.designtools.datamanagement.DataConstants;

import matrix.util.StringList;

public class CSVReader implements IInputFileReader{
	
	@Override
	public MapList readContents(String strInputFilePath) throws IOException {
		MapList mlObjectDetails=new MapList();
		File filePath = new File(strInputFilePath);
		int i=1;
		String strHeader="";
			
		if(filePath.exists()) {
			 //read the file 
			try(BufferedReader br = new BufferedReader(new FileReader(filePath))){
				  String strLineInfo;
				  StringList slObjectInfo;
				  Map mpObjectInfo;
				  
				  while ((strLineInfo = br.readLine()) != null) {
					  slObjectInfo=StringUtil.split(strLineInfo, DataConstants.SEPARATOR_COMMA);
					if(i==1) {
						strHeader=strLineInfo;
					}else {
						  mpObjectInfo=new HashMap();
						
						  mpObjectInfo.put(DataConstants.CONSTANT_HEADER,strHeader);
						  
						  if(slObjectInfo.size()==9) {
							  mpObjectInfo.put(DataConstants.CONSTANT_ECPART_TYPE_KEY, "");
							  mpObjectInfo.put(DataConstants.CONSTANT_ECPART_NAME_KEY, slObjectInfo.get(0));
							  mpObjectInfo.put(DataConstants.CONSTANT_ECPART_REVISION_KEY,slObjectInfo.get(1));
							  mpObjectInfo.put(DataConstants.CONSTANT_VPMREF_TYPE_KEY, slObjectInfo.get(2));
							  mpObjectInfo.put(DataConstants.CONSTANT_VPMREF_NAME_KEY, slObjectInfo.get(3));
							  mpObjectInfo.put(DataConstants.CONSTANT_VPMREF_REVISION_KEY, slObjectInfo.get(4));
							  mpObjectInfo.put(DataConstants.CONSTANT_OWNER_KEY, slObjectInfo.get(5));
						  }else  if(slObjectInfo.size()==10) {
							  mpObjectInfo.put(DataConstants.CONSTANT_ECPART_TYPE_KEY, slObjectInfo.get(0));
							  mpObjectInfo.put(DataConstants.CONSTANT_ECPART_NAME_KEY, slObjectInfo.get(1));
							  mpObjectInfo.put(DataConstants.CONSTANT_ECPART_REVISION_KEY,slObjectInfo.get(2));
							  mpObjectInfo.put(DataConstants.CONSTANT_VPMREF_TYPE_KEY, slObjectInfo.get(3));
							  mpObjectInfo.put(DataConstants.CONSTANT_VPMREF_NAME_KEY, slObjectInfo.get(4));
							  mpObjectInfo.put(DataConstants.CONSTANT_VPMREF_REVISION_KEY, slObjectInfo.get(5));
							  mpObjectInfo.put(DataConstants.CONSTANT_OWNER_KEY, slObjectInfo.get(6));
						  }
						  mpObjectInfo.put(DataConstants.CONSTANT_INPUT,generateInputString(mpObjectInfo));
						  mlObjectDetails.add(mpObjectInfo);
					  }
					  i++;
				  }
			}
		 }else {
			throw new IOException("The file "+strInputFilePath+" does not exist");
		 }
		return mlObjectDetails;
	}

	/**
	 * Method to generate the input string
	 * @param mpObjectInfo
	 * @return String
	 */
	private String generateInputString(Map mpObjectInfo) {
		
		StringBuilder sbInputString=new StringBuilder();
		sbInputString.append(mpObjectInfo.get(DataConstants.CONSTANT_ECPART_TYPE_KEY)).append(DataConstants.SEPARATOR_COMMA);
		sbInputString.append(mpObjectInfo.get(DataConstants.CONSTANT_ECPART_NAME_KEY)).append(DataConstants.SEPARATOR_COMMA);
		sbInputString.append(mpObjectInfo.get(DataConstants.CONSTANT_ECPART_REVISION_KEY)).append(DataConstants.SEPARATOR_COMMA);
		
		sbInputString.append(mpObjectInfo.get(DataConstants.CONSTANT_VPMREF_TYPE_KEY)).append(DataConstants.SEPARATOR_COMMA);
		sbInputString.append(mpObjectInfo.get(DataConstants.CONSTANT_VPMREF_NAME_KEY)).append(DataConstants.SEPARATOR_COMMA);
		sbInputString.append(mpObjectInfo.get(DataConstants.CONSTANT_VPMREF_REVISION_KEY)).append(DataConstants.SEPARATOR_COMMA);
		
		sbInputString.append(mpObjectInfo.get(DataConstants.CONSTANT_OWNER_KEY));
		
		return sbInputString.toString();
	}
}
