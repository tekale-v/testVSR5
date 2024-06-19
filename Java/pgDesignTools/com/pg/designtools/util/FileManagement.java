package com.pg.designtools.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.fileupload.FileItem;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.client.fcs.FcsClient;
import com.matrixone.client.fcs.InputStreamSource_2;
import com.matrixone.fcs.mcs.CheckinEnd;
import com.matrixone.fcs.mcs.CheckinStart;
import com.pg.designtools.datamanagement.DataConstants;
import matrix.db.BusinessObject;
import matrix.db.BusinessObjectProxy;
import matrix.db.Context;
import matrix.db.TicketWrapper;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class FileManagement implements Serializable {
	/**
	 * Checkin file without streaming : the file is located in the server
	 * 
	 * @param context
	 * @param bo
	 * @param format
	 * @param srcFolder
	 * @return true if checkin goes well
	 * @throws MatrixException
	 */
	// GQS - sourced from pgVPDTOPStoEnovia_mxJPO
	public void directCheckin(Context context, BusinessObject bo, String format, String srcFilename, String srcFolder)
			throws MatrixException {

		bo.checkinFile(context, true, true, null, format, srcFilename, srcFolder);
	}
	
	public FileManagement() {
		super();
	}
	
	/**
	 * Method to read contents of given file for Sim Docs
	 * @param strInputFilePath
	 * @return Map having name_Rev list and objectId list
	 * @throws IOException
	 */
	public Map<String,StringList> readInputFileForSimDocs(String strInputFilePath) throws IOException {
		Map<String,StringList> mpSimDocInfo=new HashMap<>();
		java.io.File inputFile = new java.io.File(strInputFilePath);
		 if(inputFile.exists()) {
			 //read the file 
			try(BufferedReader br = new BufferedReader(new FileReader(inputFile))){
				  String strLine;
				  StringList slInfo;
				  StringList slNameRevList=new StringList();
				  StringList slObjectIdList=new StringList();
				  StringBuilder sbLineInfo=new StringBuilder();
				  String strName;
				  
				  while ((strLine = br.readLine()) != null) {
					  	sbLineInfo=new StringBuilder();
					  	slInfo=StringUtil.split(strLine, "\t");
					  	if(slInfo.size()==3) {
					  		strName=slInfo.get(0);
				  			strName=strName.replace(DataConstants.FORWARD_SLASH, "");
				  			strName=strName.replace(DataConstants.BACKWARD_SLASH, "");
				  			strName=strName.replace(DataConstants.SEPARATOR_STAR, "");
				  			strName=strName.replace(DataConstants.SEPARATOR_PIPE, "");
				  			strName=strName.replace(DataConstants.SEPARATOR_DOLLAR, "");
					  		sbLineInfo.append(strName).append(DataConstants.SEPARATOR_UNDERSCORE).append(slInfo.get(1));
						  	slNameRevList.add(sbLineInfo.toString());
						  	slObjectIdList.add(slInfo.get(2));
					  	}else {
					  		throw new IOException(DataConstants.SIMDOC_GENERATE_MODE_WRONG_INPUT);
					  	}
				  }
				  mpSimDocInfo.put("NameRevList", slNameRevList);
				  mpSimDocInfo.put("ObjectIdList", slObjectIdList);
			}
		 }else {
			 throw new IOException("The file "+strInputFilePath+" does not exist");
		 }
		 return mpSimDocInfo;
	}
	
	/**
	 * Method to read contents of given file for Incorrect Maturity State
	 * @param strInputFilePath
	 * @return Map having Type_Name_Rev list 
	 * @throws IOException
	 */
	public Map<String,StringList> readInputFileForTNR(String strInputFilePath) throws IOException {
		Map<String,StringList> mpObjectInfo=new HashMap<>();
		java.io.File inputFile = new java.io.File(strInputFilePath);
		 if(inputFile.exists()) {
			 //read the file 
			try(BufferedReader br = new BufferedReader(new FileReader(inputFile))){
				  String strLine;
				  StringList slInfo;
				  StringList slTypeNameRevList=new StringList();
				  StringBuilder sbLineInfo=new StringBuilder();
				  
				  while ((strLine = br.readLine()) != null) {
					  	sbLineInfo=new StringBuilder();
					  	slInfo=StringUtil.split(strLine, "\t");
					  	if(slInfo.size()==3) {
					  		sbLineInfo.append(slInfo.get(0)).append(DataConstants.SEPARATOR_HASH);
					  		sbLineInfo.append(slInfo.get(1)).append(DataConstants.SEPARATOR_HASH).append(slInfo.get(2));
					  		slTypeNameRevList.add(sbLineInfo.toString());
					  	}else {
					  		throw new IOException(DataConstants.INCORRECT_MATURITY_STATE_GENERATE_MODE_WRONG_INPUT);
					  	}
				  }
				  mpObjectInfo.put("TypeNameRevList", slTypeNameRevList);
			}
		 }else {
			 throw new IOException("The file "+strInputFilePath+" does not exist");
		 }
		 return mpObjectInfo;
	}
	
	/**
	 * Checkin file with streaming : the file is streamed in a byte array from the
	 * client
	 *
	 * Perform MCS compliant checkin
	 *
	 * @param context
	 * @param bo
	 * @param format
	 * @param stream
	 * @return true if checkin goes well
	 */
	// GQS - sourced from pgVPDTOPStoEnovia_mxJPO
	public void streamCheckin(Context context, BusinessObject bo, String format, String store, String srcFilename,
			InputStreamSource_2 stream) throws Exception {

		String cookieStr = null;
		ArrayList<BusinessObjectProxy> list = new ArrayList<>();
		BusinessObjectProxy bproxy = new BusinessObjectProxy(bo.getObjectId(context), format, srcFilename, true, false);
		list.add(bproxy);
		TicketWrapper ticket = CheckinStart.doIt(context, context.getSession().getConnectString(), store, list);
		cookieStr = context.getSession().getCookieString();

		String receipt = FcsClient.checkin(ticket.getExportString(), ticket.getActionURL(), cookieStr, null, stream,
				context.getSession().getConnectString());
		CheckinEnd.doIt(context, store, receipt);

	}

	/**
	 * Intern class used by streaming checkin
	 *
	 * @author mathiot_s
	 */
	public static class FCSInputStreamSource implements InputStreamSource_2 {
		/**
		 * Absolute filename
		 */
		private String strFilename = null;
		private String strFormat = null;
		private Integer nSize = null;
		private InputStream inputStream = null;
		private boolean done = false;

		public FCSInputStreamSource(InputStream is, String filename, String format, Integer size) {
			inputStream = is;
			strFilename = filename;
			strFormat = format;
			nSize = size;
		}

		/**
		 * Get the next item to be processed
		 */
		public InputStream getInputStream() throws IOException {
			return inputStream;
		}

		/**
		 * Get the next filename to be processed
		 */
		public String getFileName() {
			return strFilename;
		}

		@Override
		public boolean hasNext() {
			return !done;
		}

		@Override
		public String getFormat() {
			return strFormat;
		}

		@Override
		public long getFileSize() {
			return nSize;
		}

		@Override
		public void next() {
			done = true;
		}

		@Override
		public void reset() {
			done = false;
		}
	}

	public ObjectFiles getObjectFilesFromSource(List<FileItem> fileSource, String workspacePath, String objectId)
			throws Exception {

		ObjectFiles fileObjs = new ObjectFiles();
		Iterator<FileItem> iFileSrc = fileSource.iterator();

		FileItem fileItem;
		String fileName = "";
		int nIndex;
		File fileToBeUploaded;
		byte[] content = null;
		
		while (iFileSrc.hasNext()) {
			fileItem = iFileSrc.next();

			if (!fileItem.isFormField()) {
				fileName = fileItem.getName();

				if (fileName.contains(DataConstants.FORWARD_SLASH)) {
					nIndex = fileName.lastIndexOf(DataConstants.FORWARD_SLASH);
					fileName = fileName.substring(nIndex + 1);
				}

				if (fileName.toLowerCase().endsWith("." + DataConstants.FILE_FORMAT_XML)
						|| fileName.toLowerCase().endsWith("." + DataConstants.FILE_FORMAT_PDF)) {
					fileToBeUploaded = new File(workspacePath + DataConstants.FORWARD_SLASH + fileName);
					fileItem.write(fileToBeUploaded);

					fileObjs.addObjectFile(objectId, DataConstants.STORE_PLMX, DomainConstants.FORMAT_GENERIC,
							workspacePath, fileName, content, false);
				}

			}
		}
		return fileObjs;
	}

	@SuppressWarnings("rawtypes")
	public static class ObjectFiles implements Iterator<Object> {

		Context context = null;
		int count = 0;

		HashMap<String, ObjectFile> hmObjectFiles = null;
		Set<String> keySet;
		Iterator<String> keyIterator;

		public ObjectFiles() {

			if (hmObjectFiles == null) {
				hmObjectFiles = new HashMap<>();
			}
		}

		public ObjectFiles(Context context) {
			this.context = context;
		}

		public void addObjectFile(String uniquekey, String store, String format, String srcFolder, String filename,
				byte[] content, boolean streaming) {
			String key;
			count ++;
			if(UIUtil.isNullOrEmpty(uniquekey))
			{
				key = String.valueOf(count).concat(format).concat(filename);
			}else
			{
				key = uniquekey.concat(format).concat(filename);
			}
		
			ObjectFile objFile;

			if (hmObjectFiles.containsKey(key)) {
				objFile = hmObjectFiles.get(key);
				objFile.setFileStore(store);
				objFile.setFileFormat(format);
				objFile.setFileDir(srcFolder);
				objFile.setFileName(filename);
				objFile.setFileContent(content);
				objFile.setFileStreaming(streaming);
			} else {
				objFile = new ObjectFile();
				objFile.setFileStore(store);
				objFile.setFileFormat(format);
				objFile.setFileDir(srcFolder);
				objFile.setFileName(filename);
				objFile.setFileContent(content);
				objFile.setFileStreaming(streaming);

				hmObjectFiles.put(key, objFile);
			}
		}

		public int size() {
			return hmObjectFiles.size();
		}

		@Override
		public boolean hasNext() {
			if (keySet == null) {
				keySet = hmObjectFiles.keySet();
			}
			if (keyIterator == null) {
				keyIterator = keySet.iterator();
			}
			return keyIterator.hasNext();
		}

		@Override
		public ObjectFile next() {

			return hmObjectFiles.get(keyIterator.next());
		}

		public void reset() {
			if (keyIterator != null) {
				keyIterator = keySet.iterator();
			}
		}

		@Override
		public void remove() {
			/**/
		}

	}

	public static class ObjectFile {

		public static final String SEPARATOR = java.io.File.separator;

		private String objectId;
		private String fileFormat;
		private String fileName;
		private String fileDir;
		private String fileStore;
		private byte[] fileContent;
		private boolean fileStreaming;

		// rename file extension to lowercase
		public boolean rename() {
			boolean bRename = false;
			String strFilePath = fileDir.concat(SEPARATOR).concat(fileName);

			String base = getBaseName(fileName);
			String ext = getExtension(fileName);
			if (base != null && ext != null) {
				ext = ext.toLowerCase();
				fileName = base.concat(EXTENSION_SEPARATOR).concat(ext);
				String strNewFilePath = fileDir.concat(SEPARATOR).concat(fileName);

				java.io.File newFile = new java.io.File(strNewFilePath);
				java.io.File oldFileName = new java.io.File(strFilePath);
				bRename = oldFileName.renameTo(newFile);
			}
			return bRename;
		}

		// taken from org.apache.commons.io.fileutils
		public static final String EXTENSION_SEPARATOR = ".";

		public static String getBaseName(String filename) {
			return removeExtension(getName(filename));
		}

		public static String getName(String filename) {
			if (filename == null) {
				return null;
			} else {
				int index = indexOfLastSeparator(filename);
				return filename.substring(index + 1);
			}
		}

		public static String getExtension(String filename) {
			if (filename == null) {
				return null;
			} else {
				int index = indexOfExtension(filename);
				return index == -1 ? "" : filename.substring(index + 1);
			}
		}

		public static String removeExtension(String filename) {
			if (filename == null) {
				return null;
			} else {
				int index = indexOfExtension(filename);
				return index == -1 ? filename : filename.substring(0, index);
			}
		}

		public static int indexOfExtension(String filename) {
			if (filename == null) {
				return -1;
			} else {
				int extensionPos = filename.lastIndexOf(46);
				int lastSeparator = indexOfLastSeparator(filename);
				return lastSeparator > extensionPos ? -1 : extensionPos;
			}
		}

		public static int indexOfLastSeparator(String filename) {
			if (filename == null) {
				return -1;
			} else {
				int lastUnixPos = filename.lastIndexOf(47);
				int lastWindowsPos = filename.lastIndexOf(92);
				return Math.max(lastUnixPos, lastWindowsPos);
			}
		}

		public String getFileFormat() {
			return fileFormat;
		}

		public void setFileFormat(String fileFormat) {
			this.fileFormat = fileFormat;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public String getFileDir() {
			return fileDir;
		}

		public void setFileDir(String fileDir) {
			this.fileDir = fileDir;
		}

		public String getFileStore() {
			return fileStore;
		}

		public void setFileStore(String fileStore) {
			this.fileStore = fileStore;
		}

		public byte[] getFileContent() {
			return fileContent;
		}

		public void setFileContent(byte[] fileContent) {
			this.fileContent = fileContent;
		}

		public boolean isFileStreaming() {
			return fileStreaming;
		}

		public void setFileStreaming(boolean fileStreaming) {
			this.fileStreaming = fileStreaming;
		}

		public String getObjectId() {
			return objectId;
		}

		public void setObjectId(String objectId) {
			this.objectId = objectId;
		}

	}
	
	/**
	 * Method to read contents of given file
	 * @param strInputFilePath
	 * @return StringList of the content
	 * @throws IOException
	 */
	public StringList readInputFile(String strInputFilePath) throws IOException {
		StringList slNameList=new StringList();
		 java.io.File inputFile = new java.io.File(strInputFilePath);
		 if(inputFile.exists()) {
			 //read the file 
			try(BufferedReader br = new BufferedReader(new FileReader(inputFile))){
				  String strName;
				  while ((strName = br.readLine()) != null) {
					  slNameList.addElement(strName);
				  }
			}
		 }else {
			 throw new IOException("The file "+strInputFilePath+" does not exist");
		 }
		 return slNameList;
	}
	
		/**
	 * Method to read contents of given file 
	 * @param strInputFilePath
	 * @return Map having name_Rev list 
	 * @throws IOException
	 */
	public Map<String,StringList> readInputFileForNameRev(String strInputFilePath) throws IOException {
		Map<String,StringList> mpVPMReferenceInfo=new HashMap<>();
		java.io.File inputFile = new java.io.File(strInputFilePath);
		 if(inputFile.exists()) {
			 //read the file 
			try(BufferedReader br = new BufferedReader(new FileReader(inputFile))){
				  String strLine;
				  StringList slInfo;
				  StringList slNameRevList=new StringList();
				  StringBuilder sbLineInfo=new StringBuilder();
				  
				  while ((strLine = br.readLine()) != null) {
					  	sbLineInfo=new StringBuilder();
					  	slInfo=StringUtil.split(strLine, "\t");
					  	if(slInfo.size()==2) {
					  		sbLineInfo.append(slInfo.get(0)).append(DataConstants.SEPARATOR_HASH).append(slInfo.get(1));
						  	slNameRevList.add(sbLineInfo.toString());
					  	}else {
					  		throw new IOException(DataConstants.DESIGNDOMAIN_GENERATE_MODE_WRONG_INPUT);
					  	}
				  }
				  mpVPMReferenceInfo.put("NameRevList", slNameRevList);
			}
		 }else {
			 throw new IOException("The file "+strInputFilePath+" does not exist");
		 }
		 return mpVPMReferenceInfo;
	}
}
