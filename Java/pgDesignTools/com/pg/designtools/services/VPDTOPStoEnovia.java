package com.pg.designtools.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import com.dassault_systemes.enovia.gls.common.model.PRSPContext;
import com.dassault_systemes.platform.restServices.RestService;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.vplmintegration.util.VPLMIntegTraceUtil;
import com.pg.designtools.datamanagement.DataConstants;
import com.pg.designtools.datamanagement.StackingPattern;
import com.pg.designtools.datamanagement.TransportUnitPart;
import com.pg.designtools.datamanagement.StackingPattern.StackingPatternDocument;
import com.pg.designtools.integrations.datahandlers.TOPSXStreamHandler;
import com.pg.designtools.integrations.exception.DesignToolsIntegrationException;
import com.pg.designtools.util.FileManagement.ObjectFiles;
import matrix.db.BusinessObject;

public class VPDTOPStoEnovia extends RestService {

	public VPDTOPStoEnovia() {
		/*
		 * Currently nothing to be done here
		 */
	}

	protected matrix.db.Context context;
	DataConstants.customTOPSExceptions errorInvalidSPSName = DataConstants.customTOPSExceptions.ERROR_400_INVALID_SPS_NAME;
	DataConstants.customTOPSExceptions errorInvalidFileFormat = DataConstants.customTOPSExceptions.ERROR_400_FILE_FORMAT_NOT_VALID;
	DataConstants.customTOPSExceptions errorScrFolderNotPresent = DataConstants.customTOPSExceptions.ERROR_400_SRCFOLDER_NOT_PRESENT;
	DataConstants.customTOPSExceptions errorSPSRevisionNotPresent = DataConstants.customTOPSExceptions.ERROR_400_NO_REVISION_INSIDE_NAME;
	DataConstants.customTOPSExceptions errorNoContent = DataConstants.customTOPSExceptions.ERROR_400_NO_CONTENT;

	@POST
	@Path("/checkinAnalysis")

	public Response checkinFromTOPS(@Context HttpServletRequest request, String strInputData) throws Exception {
		try {
			context = getAuthenticatedContext(request, false);
			PRSPContext.set(context);
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>> checkinFromTOPS: strInputData=" + strInputData);

			JSONObject localJsonObject = new JSONObject(strInputData);
			Charset charset = StandardCharsets.UTF_16;

			byte[] content=localJsonObject.getString("content").getBytes(charset);
			String filename=localJsonObject.getString("filename");
			
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>> checkinFromTOPS: content.length=" + content.length);
			//START DTCLD-745 ALM 56568 Added exception if the content is not present (0B file)
			if(content.length==0) {
				throw new DesignToolsIntegrationException(errorNoContent.getExceptionCode(),
						errorNoContent.getExceptionMessage()+filename);
			}
			//END DTCLD-745 ALM 56568
			checkin2int(localJsonObject.getString("name"), localJsonObject.getString("srcFolder"),filename, content,false);
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>>  checkinFromTOPS END");
			return Response.status(200).entity("Success").build();

		} catch (DesignToolsIntegrationException e1) {
			VPLMIntegTraceUtil.trace(context, ">>  checkinFromTOPS e1.getStrErrorMessage()" + e1.getStrErrorMessage());
			if (context.isTransactionActive()) {
				ContextUtil.abortTransaction(context);
			}
			return Response.status(e1.getnErrorCode()).entity(e1.getStrErrorMessage()).build();
			
		} catch (Exception e2) {
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>  checkinFromTOPS e2.getMessage()" + e2.getMessage());
			VPLMIntegTraceUtil.trace(context, e2.getMessage());
			if (context.isTransactionActive()) {
				ContextUtil.abortTransaction(context);
			}
			return Response.status(500).entity(e2.getMessage()).build();
		}
	}

	public void checkin2int(String name, String srcFolder, String filename, byte[] content, boolean streaming)
			throws Exception {
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>>>>>>>>>>  checkin2int START");

		String strType = DataConstants.TYPE_PG_STACKINGPATTERN;
		String strSPSName = DomainConstants.EMPTY_STRING;
		String strSPSRevision = DomainConstants.EMPTY_STRING;
		String store = DataConstants.STORE_STORE;
		String format = DataConstants.FORMAT_GENERIC;
		DomainObject domSPS = null;

		validateInputParameters(name, filename, srcFolder);

		if (name.contains(".") || name.contains("_")) {
			strSPSName = name.substring(0, name.length() - 4);
			strSPSName = strSPSName.replaceAll("\\s", "");

			strSPSRevision = name.substring(name.length() - 3);
			strSPSRevision = strSPSRevision.replaceAll("\\s", "");
			strSPSRevision = strSPSRevision.trim();

			BusinessObject busSPS = new BusinessObject(strType, strSPSName, strSPSRevision,
					DataConstants.VAULT_ESERVICE_PRODUCTION);
			try {
			domSPS = DomainObject.newInstance(context, busSPS);
			}
			catch(Exception e) {
				throw new DesignToolsIntegrationException(errorInvalidSPSName.getExceptionCode(),
						errorInvalidSPSName.getExceptionMessage());
			}

			BusinessObject busLastRevision = domSPS.getLastRevision(context);
			strSPSRevision = busLastRevision.getRevision();

			BusinessObject busLatestRevSPS = new BusinessObject(strType, strSPSName, strSPSRevision,
					DataConstants.VAULT_ESERVICE_PRODUCTION);
			domSPS = DomainObject.newInstance(context, busLatestRevSPS);

		} else {
			throw new DesignToolsIntegrationException(errorSPSRevisionNotPresent.getExceptionCode(),
					errorSPSRevisionNotPresent.getExceptionMessage());
		}

		try {
			String wsPath = context.createWorkspace();
			srcFolder = wsPath;
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>>>>>>>  checkin2int wsPath " + wsPath);
			File fileToBeUploaded = new File(wsPath + File.separator + filename);
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>>>>>>>  checkin2int fileToBeUploaded " + fileToBeUploaded);
			Base64 codec = new org.apache.commons.codec.binary.Base64();
			byte[] decoded = codec.decode(content);		
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>>>>>>>>>>>>>  checkin2int After decode ");
		
			try (FileOutputStream fos = new FileOutputStream(fileToBeUploaded)) {
				fos.write(decoded);
				VPLMIntegTraceUtil.trace(context, ">>>>>  checkin2int After fos.write");
			} catch (IOException e) {
				VPLMIntegTraceUtil.trace(context, e.getMessage());
			}
			
			ContextUtil.startTransaction(context, true);
			
			StackingPattern objSPS = new StackingPattern(context, strSPSName, strSPSRevision, filename, domSPS);

			ObjectFiles objectFiles = new ObjectFiles();
 
			StringBuilder uniquekey = new StringBuilder();
			uniquekey.append(strType).append(DataConstants.SEPARATOR_COLON).append(name)
					.append(DataConstants.SEPARATOR_COLON).append(strSPSRevision);

			objectFiles.addObjectFile(uniquekey.toString(), store, format, srcFolder, filename, content, streaming);

			StackingPatternDocument objSPD = objSPS.new StackingPatternDocument();
			objSPD.processFile(objectFiles);
			
			if (filename.startsWith(DataConstants.SIMPLE_XML_PREFIX)) {
				TOPSXStreamHandler.parse(context, domSPS, fileToBeUploaded);
			}
			
			//START: DTCLD-777: connect SPS to TUP CO/CA
			TransportUnitPart tup = new TransportUnitPart();
			DomainObject doTUP=objSPS.getRelatedTUP(domSPS);
			String strTUPChangeActionID = tup.processChangeManagement(context,doTUP.getInfo(context, DomainConstants.SELECT_ID));
			VPLMIntegTraceUtil.trace(context, ">>>>>  checkin2int strTUPChangeActionID::"+strTUPChangeActionID);
			objSPS.processChangeManagement(context,domSPS.getInfo(context, DomainConstants.SELECT_ID),strTUPChangeActionID);
			//END: DTCLD-777: connect SPS to TUP CO/CA
			
			if (context.isTransactionActive()) {
				ContextUtil.commitTransaction(context);
			}
		} catch (Exception ex) {
			VPLMIntegTraceUtil.trace(context, ">>>> Inside catch of checkin2int::"+ex.getMessage());
		} finally {
			// Delete all the files from the workspace
			VPLMIntegTraceUtil.trace(context, ">>>> checkin2int, filename to be deleted from workspace " + filename);
			context.deleteWorkspaceFile(filename);
			VPLMIntegTraceUtil.trace(context, ">>>> checkin2int, workspace file deleted ");
		}

	}

	public void validateInputParameters(String name, String filename, String srcFolder) {
		if (UIUtil.isNullOrEmpty(name)) {

			throw new DesignToolsIntegrationException(errorInvalidSPSName.getExceptionCode(),
					errorInvalidSPSName.getExceptionMessage());

		}
		if (!filename.endsWith(DataConstants.FILE_FORMAT_XML) && !filename.endsWith(DataConstants.FILE_FORMAT_PDF)) {
			throw new DesignToolsIntegrationException(errorInvalidFileFormat.getExceptionCode(),
					errorInvalidFileFormat.getExceptionMessage());
		}
		if (UIUtil.isNullOrEmpty(srcFolder)) {
			throw new DesignToolsIntegrationException(errorScrFolderNotPresent.getExceptionCode(),
					errorScrFolderNotPresent.getExceptionMessage());
		}

	}

}
