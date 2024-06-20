package com.pg.widgets.nexusPerformanceChars;

import java.io.File;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import com.dassault_systemes.platform.restServices.RestService;

/**
 * Services class for Performance Characteristics web services
 */
@Path("/perfcharsServices")
public class PGPerfCharsServices extends RestService {
	
	private static final Logger logger= LoggerFactory.getLogger(PGPerfCharsServices.class.getName()); 
	private static final String TYPE_APPLICATION_FORMAT = "application/json";
	private static final String EXCEPTION_MESSAGE  = "Exception in PGPerfCharsServices";
	
	/**This Method return eMatrix context object
	 * @param request
	 * @param isSCMandatory 
	 * @return
	 */
	private matrix.db.Context getContext(HttpServletRequest request, boolean isSCMandatory) {
		matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
		//To Clear Mql Error Notice if any
		context.clearClientTasks();
		return context;
	}
	
	/**
	 * Method to get the 'Performance Characteristics' object details
	 * @param request
	 * @param strJsonInput
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/fetchPerfCharsData")
	public Response fetchPerfCharsData(@javax.ws.rs.core.Context HttpServletRequest request, String strJsonInput) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			PGPerfCharsFetchData pgPerfCharsFetchDataObj = new PGPerfCharsFetchData();
			String strOutput = pgPerfCharsFetchDataObj.fetchPerfCharsData(context, strJsonInput);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception excep) {
			logger.error(EXCEPTION_MESSAGE, excep.getMessage());
			res = Response.status(Response.Status.BAD_REQUEST).entity(excep.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to get the 'Performance Characteristics' object details from ENOVIA for some external system/s
	 * 
	 * @param request
	 * @param strJsonInput
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/fetchPerfCharsDataFromEnovia")
	public Response fetchPerfCharsDataFromEnovia(@javax.ws.rs.core.Context HttpServletRequest request,
			@QueryParam("id") String strOjectId) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = false; //true; 
			matrix.db.Context context = getContext(request, isSCMandatory);
			PGPerfCharsFetchData pgPerfCharsFetchDataObj = new PGPerfCharsFetchData();
			Map<String, String> mpArgsMap = new HashMap<>();
			mpArgsMap.put("id",strOjectId);
			String strOutput = pgPerfCharsFetchDataObj.fetchPerfCharsDataFromEnovia(context, mpArgsMap);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception excep) {
			logger.error(EXCEPTION_MESSAGE, excep.getMessage());
			res = Response.status(Response.Status.BAD_REQUEST).entity(excep.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to create, edit or remove 'Performance Characteristics' objects
	 * @param request
	 * @param strJsonInput
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/createEditPerfChars")
	public Response createEditPerfChars(@javax.ws.rs.core.Context HttpServletRequest request, String strJsonInput) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			PGPerfCharsCreateEditUtil pgPerfCharsCreateEditUtilObj = new PGPerfCharsCreateEditUtil();
			String strOutput = pgPerfCharsCreateEditUtilObj.createUpdatePerfChars(context, strJsonInput);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception excep) {
			logger.error(EXCEPTION_MESSAGE, excep.getMessage());
			res = Response.status(Response.Status.BAD_REQUEST).entity(excep.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to import the PC objects from the excel file.
	 * @param request
	 * @param strJsonInput
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/importPerfChars")
	public Response importPerfCharsFromExcel(@javax.ws.rs.core.Context HttpServletRequest request, String strJsonInput) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			PGPerfCharsImportFromFileUtil pgPerfCharsImportFromFileUtilObj = new PGPerfCharsImportFromFileUtil();
			String strOutput = pgPerfCharsImportFromFileUtilObj.importPerfCharsFromExcel(context, strJsonInput);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception excep) {
			logger.error(EXCEPTION_MESSAGE, excep.getMessage());
			res = Response.status(Response.Status.BAD_REQUEST).entity(excep.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to get the data for 'Release Criteria' wizard
	 * 
	 * @param request
	 * @param strJsonInput
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/fetchStructuredReleaseCriteria")
	public Response fetchStructuredReleaseCriteria(@javax.ws.rs.core.Context HttpServletRequest request, String strJsonInput) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			PGPerfCharsReleaseCriteriaWizardUtil pgPerfCharsReleaseCriteriaWizardUtilObj = new PGPerfCharsReleaseCriteriaWizardUtil();
			String strOutput = pgPerfCharsReleaseCriteriaWizardUtilObj.fetchReleaseCriteriaWizardData(context, strJsonInput);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception excep) {
			logger.error(EXCEPTION_MESSAGE, excep.getMessage());
			res = Response.status(Response.Status.BAD_REQUEST).entity(excep.getMessage()).build();
		}
		return res;
	}
		
	/**
	 * This method is used to delete connected Performance Characteristics
	 * @param context
	 * @param mpRequestMap
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/removeSelPerfChars")
	public Response removeSelPerfChars(@javax.ws.rs.core.Context HttpServletRequest request, Map mpRequestMap) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			PGPerfCharsDeleteUtil pgPerfCharsDeleteUtil = new PGPerfCharsDeleteUtil();
			String strOutput = pgPerfCharsDeleteUtil.deletePCFromAPP(context, mpRequestMap);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception excep) {
			logger.error(EXCEPTION_MESSAGE, excep.getMessage());
			res = Response.status(Response.Status.BAD_REQUEST).entity(excep.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * This method is to Clone and Connect 'Performance Characteristic' objects from 'Product Data' objects
	 * @param context
	 * @param args : Program Args
	 * @return 'true' if process completes successfully
	 * @throws Exception
	 */
	@POST
	@Path("/copyPerfCharsFromProductData") 
	public Response copyPerfCharsFromProductData(@javax.ws.rs.core.Context HttpServletRequest request, Map mpRequestMap)
			throws Exception {
		
		Response res = null;
		PGPerfCharsCopyFromProductData pgPerfCharsCopyFromPD = new PGPerfCharsCopyFromProductData();
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			String strOutput = pgPerfCharsCopyFromPD.copyCharacteristicsFromProductData(context, mpRequestMap);
			
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception excep) {
			logger.error(EXCEPTION_MESSAGE, excep.getMessage());
			res = Response.status(Response.Status.BAD_REQUEST).entity(excep.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * This method is to Validate 'Performance Characteristic' objects connected to 'Product Data' objects
	 * @param context
	 * @param args : Program Args
	 * @return 'Pass' if data is valide 'Fail' if data is Invalid
	 * @throws Exception
	 */
	@POST
	@Path("/validatePerfChars") 
	public Response validatePerfChars(@javax.ws.rs.core.Context HttpServletRequest request, String strJsonInput)
			throws Exception {
		Response res = null;
		PGPerfCharsValidateData pgPerfCharsValidatePC = new PGPerfCharsValidateData();
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			String strOutput =pgPerfCharsValidatePC.validatePerformanceCharacteristicsData(context, strJsonInput);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception excep) {
			logger.error(EXCEPTION_MESSAGE, excep.getMessage());
			res = Response.status(Response.Status.BAD_REQUEST).entity(excep.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * This method is to Clone and Connect 'Performance Characteristic' objects from 'Product Data' objects
	 * @param context
	 * @param args : Program Args
	 * @return 'true' if process completes successfully
	 * @throws Exception
	 */
	@POST
	@Path("/initialAccessDataPerfChars") 
	public Response getCommandTableAccessDetails(@javax.ws.rs.core.Context HttpServletRequest request, String strJsonInput)
			throws Exception {
		
		Response res = null;
		PGPerfCharsAccessDetails pgPerfCharsAccessDetails = new PGPerfCharsAccessDetails();
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			String strOutput =pgPerfCharsAccessDetails.getAccessDetails(context, strJsonInput);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception excep) {
			System.out.println("--- getCommandTableAccessDetails excep---"+excep);
			logger.error(EXCEPTION_MESSAGE, excep.getMessage());
			res = Response.status(Response.Status.BAD_REQUEST).entity(excep.getMessage()).build();
		}
		return res;
	}
	@POST
	@Path("/validateCharacteristic")
	public Response validateCharacteristic(@javax.ws.rs.core.Context HttpServletRequest request,  String strJsonInput)
			throws Exception {
		Response res = null;
		PGPerfCharsValidateUtil pgPerfCharsValidateUtil = new PGPerfCharsValidateUtil();
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			String strOutput = pgPerfCharsValidateUtil.validateCharacteristics(context, strJsonInput);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception excep) {
			logger.error(EXCEPTION_MESSAGE, excep.getMessage());
			res = Response.status(Response.Status.BAD_REQUEST).entity(excep.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * This method is for 'Test Method Specifics Wizard' 
	 * @param context
	 * @param args : Program Args
	 * @return Initial fetch details for Test Method Specifics
	 * @throws Exception
	 */
	@POST
	@Path("/fetchNexusTMSpecificsPerfChars") 
	public Response fetchNexusTMSpecificsPerfChars(@javax.ws.rs.core.Context HttpServletRequest request, String strJsonInput)
			throws Exception {
		Response res = null;
		PGFetchNexusTMSpecificsPerfChars pgFetchNexusTMSpecificsPerfChars = new PGFetchNexusTMSpecificsPerfChars();
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			String strOutput =pgFetchNexusTMSpecificsPerfChars.fetchNexusTMSpecificsPerfChars(context, strJsonInput);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception excep) {
			logger.error(EXCEPTION_MESSAGE, excep.getMessage());
			res = Response.status(Response.Status.BAD_REQUEST).entity(excep.getMessage()).build();
		}
		return res;
	}
	/**
	 * Method to get If the Test Method is Nexus Or Not
	 * @param request
	 * @param strJsonInput
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/fetchNexusPCData")
	public Response fetchNexusPCData(@javax.ws.rs.core.Context HttpServletRequest request, String strJsonInput) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			PGPerfCharsUtil pgPerCharUtil = new PGPerfCharsUtil();
			String strOutput = pgPerCharUtil.fetchTesMethodDetails(context, strJsonInput);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception excep) {
			logger.error(EXCEPTION_MESSAGE, excep.getMessage());
			res = Response.status(Response.Status.BAD_REQUEST).entity(excep.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * This method is used to Export the Performace Characterstics Details into Excel
	 * 
	 * @param context
	 * @param mpRequestMap
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/exportPerCharsToExcel")
	@Produces("application/excel")
	public Response exportPerCharsToExcel(@javax.ws.rs.core.Context HttpServletRequest request, String strJsonInput)
			throws Exception {
		Response res = null;
		PGPerfCharsExportExcel pgPerfCharsExportExcel = new PGPerfCharsExportExcel();
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			java.util.HashMap strOutputMap = pgPerfCharsExportExcel.exportToExcel(context, strJsonInput);

			//converting to base64 format to fix the corrupted file issue on download
			byte[] fileContent = (byte[]) strOutputMap.get("bytes");
	        String strFileContent = Base64.getEncoder().encodeToString(fileContent);
			
			ResponseBuilder response = Response.ok(strFileContent);
			response.header("Content-Disposition",
					"attachment; filename=\"" + (String) strOutputMap.get("filename") + "\"");
			response.header("filename", (String) strOutputMap.get("filename"));
			return response.build();
		} catch (Exception excep) {
			logger.error(EXCEPTION_MESSAGE, excep.getMessage());
			res = Response.status(Response.Status.BAD_REQUEST).entity(excep.getMessage()).build();
		}
		return res;
	}
	/**
	 * Method to Clone 'Performance Characteristics' objects
	 * @param request
	 * @param strJsonInput
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/pgCloneNexusPerfChars")
	public Response pgCloneNexusPerfChars(@javax.ws.rs.core.Context HttpServletRequest request, String strJsonInput) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			PGPerfCharsCreateEditUtil pgPerfCharsCreateEditUtilObj = new PGPerfCharsCreateEditUtil();
			String strOutput = PGPerfCharClonePCData.createClonePerfChars(context, strJsonInput);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception excep) {
			logger.error(EXCEPTION_MESSAGE, excep.getMessage());
			res = Response.status(Response.Status.BAD_REQUEST).entity(excep.getMessage()).build();
		}
		return res;
	}
	
}
