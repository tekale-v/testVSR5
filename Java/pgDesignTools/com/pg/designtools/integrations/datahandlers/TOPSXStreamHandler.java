package com.pg.designtools.integrations.datahandlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import org.apache.commons.fileupload.FileItem;
import com.dassault_systemes.enovia.gls.common.model.PRSPContext;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.vplmintegration.util.VPLMIntegTraceUtil;
import com.pg.designtools.datamanagement.DataConstants;
import com.pg.designtools.datamanagement.StackingPattern;
import com.pg.designtools.datamanagement.StackingPattern.StackingPatternDocument;
import com.pg.designtools.util.FileManagement;
import com.pg.designtools.util.FileManagement.ObjectFile;
import com.pg.designtools.util.FileManagement.ObjectFiles;
import matrix.db.Context;
import matrix.util.MatrixException;

public class TOPSXStreamHandler {

	static SimpleXML simpleParser;

	FileManagement fileProcessor = new FileManagement();
	static DataConstants.customTOPSExceptions errorInvalidXMLFormat = DataConstants.customTOPSExceptions.ERROR_400_INVALID_XML_FORMAT;
	DataConstants.customTOPSExceptions errorInvalidStructureType = DataConstants.customTOPSExceptions.ERROR_400_INVALID_STRUCTURE_TYPE;

	public static boolean parse(Context context, DomainObject domSPS, File fileToBeUploaded) throws Exception {
		PRSPContext.set(context);
		boolean retVal = false;
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> parse START ");
		simpleParser = new SimpleXML(fileToBeUploaded);
		simpleParser.validateXML(domSPS);
		simpleParser.process();
		return retVal;
	}

	public String getObjectFilesFromSource(Context context, List<FileItem> fileSource, String workspacePath,
			String objectId) throws Exception {
		String strCheckedInFileNames = "";
		ObjectFiles inputfiles = fileProcessor.getObjectFilesFromSource(fileSource, workspacePath, objectId);
		DomainObject domSPSObj = null;
		PRSPContext.set(context);
		if (UIUtil.isNotNullAndNotEmpty(objectId)) {
			domSPSObj = DomainObject.newInstance(context, objectId);
		} else {
			domSPSObj = getSPSObjectFromXMLFiles(inputfiles);
		}
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>getObjectFilesFromSource  domSPSObj " + domSPSObj);
		if (domSPSObj != null) {
			strCheckedInFileNames = processInputFiles(context, domSPSObj, inputfiles);
		}
		return strCheckedInFileNames;
	}

	public String processInputFiles(Context context, DomainObject domSPSObj, ObjectFiles inputfiles) throws Exception {
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>processInputFiles  start ");
		ObjectFile objFile = null;
		File fileToBeUploaded;
		StringBuilder sbcheckedInFilesNames = new StringBuilder();
		while (inputfiles.hasNext()) {
			objFile = inputfiles.next();
			VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>processInputFiles  objFile " + objFile);
			fileToBeUploaded = new File(objFile.getFileDir() + DataConstants.FORWARD_SLASH + objFile.getFileName());
			if (objFile.getFileName().endsWith(DataConstants.FILE_FORMAT_XML)
					&& objFile.getFileName().startsWith(DataConstants.SIMPLE_XML_PREFIX)) {
				parse(context, domSPSObj, fileToBeUploaded);

			}

			StackingPattern objSPS = new StackingPattern(PRSPContext.get(),
					domSPSObj.getInfo(context, DomainConstants.SELECT_NAME),
					domSPSObj.getInfo(context, DomainConstants.SELECT_REVISION), objFile.getFileName(), domSPSObj);

			StackingPatternDocument objSPD = objSPS.new StackingPatternDocument();
			VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>processInputFiles  before checkin ");
			objSPD.checkinFile(objFile.getFileStore(), objFile.getFileFormat(), objFile.getFileDir(),
					objFile.getFileContent(), false);
			VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>processInputFiles  after checkin ");
			sbcheckedInFilesNames.append(objFile.getFileName());
			sbcheckedInFilesNames.append(DataConstants.SEPARATOR_COMMA);
		}
		return sbcheckedInFilesNames.toString().replaceAll(",$", "");
	}

	public static DomainObject getSPSObjectFromXMLFiles(ObjectFiles inputObjectfiles)
			throws FileNotFoundException, MatrixException {
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>getSPSObjectFromXMLFiles  Start ");
		DomainObject domSPSObjFromXML = null;
		ObjectFile objectFile = null;
		File sFile;
		while (inputObjectfiles.hasNext()) {
			objectFile = inputObjectfiles.next();
			VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>getSPSObjectFromXMLFiles  objectFile " + objectFile);
			if (objectFile.getFileName().endsWith(DataConstants.FILE_FORMAT_XML)
					&& objectFile.getFileName().startsWith(DataConstants.SIMPLE_XML_PREFIX)) {
				sFile = new File(objectFile.getFileDir() + DataConstants.FORWARD_SLASH + objectFile.getFileName());
				simpleParser = new SimpleXML(sFile);
				domSPSObjFromXML = simpleParser.getSPSObjectFromXML();

			}
		}
		inputObjectfiles.reset();
		return domSPSObjFromXML;
	}

}
