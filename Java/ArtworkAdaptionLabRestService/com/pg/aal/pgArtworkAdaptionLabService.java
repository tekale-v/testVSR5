package com.pg.aal;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import matrix.util.Pattern;

import org.apache.commons.io.FileUtils;
import com.dassault_systemes.platform.restServices.RestService;
import com.matrixone.apps.awl.util.ArtworkCheckInUtil;
import com.matrixone.apps.awl.util.BusinessUtil;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.i18nNow;
import com.pg.util.EncryptCrypto;
import com.pg.v3.custom.pgV3Constants;
//Modified By RTA Sogeti for Defect #31866 STARTS
import java.util.Base64;
//Modified By RTA Sogeti for Defect #31866 ENDS
import matrix.db.Context;
import matrix.util.StringList;

import com.matrixone.apps.awl.dao.ArtworkFile;
import com.matrixone.apps.awl.dao.POA;
import com.matrixone.apps.awl.enumeration.AWLFormat;
import com.matrixone.apps.awl.enumeration.AWLRel;
import com.matrixone.apps.awl.gs1assembly.GS1ResponseValidationUtil;
import com.matrixone.apps.awl.util.Triplet;
@javax.ws.rs.Path(value = "/artworkAdaptionLab")
public class pgArtworkAdaptionLabService extends RestService {
	private static final String ONE = "001";
	private static final String AUTHORIZED = "authorized";
	private static final String DOES_NOT_EXIST = "does not exist";
	private static final String NO_CONTEXT_USER = "No context user";
	private static final String INVALID_PASSWORD = "Invalid password";
	private static final String Decode_Error = "Decode Error";
	private static final String Invalid_Compression = "Invalid Compression";
	private static final String EMPTY_BYTE_STREAM = "Empty Byte Stream";
	private static final Logger logger = Logger.getLogger("com.pg.aal.pgArtworkAdaptionLabService");
	private static final int BUFFER_SIZE = 4096;
	private static final String SEPARATOR = java.io.File.separator;
	private static final String POA_DETAILS = "POADetails";
	private static final String FORMAT_PDF = "pdf";
	private static final String FORMAT_AI = "ai";
	private static final String FORMAT_XML = "xml";
	private static final String FORMAT_ZIP = ".zip";
	private static final String XSD_PATH = "/awl/xsd/SchemaGS1/gs1/ecom/ArtworkContentResponse.xsd";
	private static final String SUCCESS_MESSAGE = "Files are successfully checked into the POA";
	private static final String STATE_REVIEW = "Review";
	private static final String STATE_RELEASE = "Release";
	private static final String STATE_OBSOLETE = "Obsolete";
	private static final String GENERIC_CHECK_IN_ERROR_1 = "GLN value must be 13 characters";
	private static final String GENERIC_CHECK_IN_ERROR_2 = "locale code must be 5 characters";
	private static final String GENERIC_CHECK_IN_ERROR_3 = "Date should be YYYY-MM-DDThh:mm:ss format";
	private static final String GENERIC_CHECK_IN_ERROR_4 = "Missing Node/Attributes";
	private static final String RESPONSE_TWO_ZERO_ZERO = "200 OK";
	private static final String RESPONSE_TWO_ZERO_ONE = "201 OK";
	private static final String RESPONSE_FOUR_ZERO_ONE = "401 UNAUTHORIZED";
	private static final String RESPONSE_FOUR_ZERO_FOUR = "404 NOT FOUND";
	private static final String RESPONSE_FIVE_ZERO_ZERO = "500 INTERNAL SERVER ERROR";
	private static final String RESP_VAL="RESPONSEVALIDATED";
	private static final String FILE_ID="file.id";
	private static final String FILE_NAME="file.name";
	private static final String FROM="from";
	private static final String BRO="[";
	private static final String BRC = "]";
	private static final String ID=".id";
	private static final String REGX = "\\$";
	private static final String DOT=".";
	private static final String NON_MATCH_EXISTING_FILE = "Input Files are Not Matching Existing File Names";
	private static final String ARTWORK_NOT_ALLOWED_IN_REVIEW = "Artwork File $ is not allowed in '"+STATE_REVIEW+"' state of POA";
	private static final String STRING_RESOURCE_EMX_CPN = "emxCPN";
	private static final String STRING_RESOURCE_KEY_APP_USER = "emxCPN.AAL.applicationUser";
	private static final String STRING_RESOURCE_KEY_APP_PASS = "emxCPN.AAL.applicationUserPass";
	private static final String STRING_RESOURCE_KEY_SERVER_PATH = "emxCPN.ServerPath";
	private static final String STR_REJEX = "[\\[{\\{\\((\\))\\}}\\]]";
	private static final String STR_TRUE = "TRUE";
	private static final String STR_FALSE = "FALSE";
	
	/**
	 * @param poaRequestData
	 * @return
	 * @throws Exception
	 */
	@POST
	@javax.ws.rs.Path("/postPOADetails")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response postPOADetails(POARequestData poaRequestData) throws Exception {
		logger.info("\n--- Inside postPOADetails() method --- starts");
		Context context = null;
		POAResponseData responseData = null;
		try {
			responseData = findPOA(poaRequestData, context, new String[] { poaRequestData.getPoaNumber() });
		} catch (Exception e) {
			logger.info("\n--- Got an exception in postPOADetails(). " + e);
		}
		logger.info("Inside postPOADetails() method --- ends");
		return Response.ok(responseData).build();
	}
	
	/**
	 * Get the POA and check in files 
	 * 
	 * @param poaRequestData
	 * @param context
	 * @param args
	 * @return
	 * @throws Exception
	 */
	private POAResponseData findPOA(POARequestData poaRequestData, Context context, String[] args) throws Exception {
		logger.info("\n--- inside findPOA method ---");
		String errorMsg = DomainConstants.EMPTY_STRING;
		POAResponseData response = null;
		File poaDetails = null;
		File zipFile = null;
		boolean isCheckinErrorThrown = false;
		boolean isContextPushed = false;
		ArrayList<String> inputFileNames = new ArrayList<String>();
		String isArtworkInvolved = STR_FALSE;
		String isGS1Involved = STR_FALSE;
		try {
			String poaName = args[0];
			logger.info("\n--- poaName : "+poaName+" ---");
			logger.info("\n--- Before adding to StringList ---");
			StringList poaSelects = new StringList(DomainConstants.SELECT_ID);
			logger.info("\n--- After adding to StringList ---");
			// get context
			context = new Context(DomainConstants.EMPTY_STRING);
			i18nNow i18nObject = new i18nNow();
			String strLocale = context.getLocale().toString();
			//logger.info("\n--- EncryptCrypto.decryptString(poaRequestData.getApplicationUserPass()) : "+EncryptCrypto.decryptString(poaRequestData.getApplicationUserPass()));
			String webServiceUser = i18nObject.GetString(STRING_RESOURCE_EMX_CPN, strLocale, STRING_RESOURCE_KEY_APP_USER);
			context.setUser(webServiceUser);
			logger.info("\n--- App User :"+webServiceUser+" ---");
			//context.setPassword(EncryptCrypto.decryptString(poaRequestData.getApplicationUserPass()));
			String webServiceUserPass = i18nObject.GetString(STRING_RESOURCE_EMX_CPN, strLocale, STRING_RESOURCE_KEY_APP_PASS);
			logger.info("\n--- App User Pass :"+webServiceUserPass+" ---");
			//webServiceUserPass = webServiceUserPass==null ? "" : webServiceUserPass.trim();
			if (BusinessUtil.isNotNullOrEmpty(webServiceUserPass)) {
				webServiceUserPass = EncryptCrypto.decryptString(webServiceUserPass);
			}
			context.setPassword(webServiceUserPass);
			context.connect();
			ContextUtil.pushContext(context);
			isContextPushed = true;
			logger.info("\n --- Context Pushed with Super ---");
			MapList poaMap = DomainObject.findObjects(context, 
					pgV3Constants.TYPE_POA, // POA Type
					poaName,               // POA Name
					ONE,                   // POA Revision 
			        DomainConstants.QUERY_WILDCARD, // Owner Pattern
			        pgV3Constants.VAULT_ESERVICEPRODUCTION, // VAULT
			        DomainConstants.EMPTY_STRING, // DomainConstant
			        false,              //Expand Type 
			        poaSelects);        // Id
			DomainObject poaObject = null;
			logger.info("\n--- poaMap : "+poaMap);
			if (BusinessUtil.isNotNullOrEmpty(poaMap)) {
				logger.info("\n--- POA MapList is not empty ---");
				Map infoMap = (Map) poaMap.get(0);
				String poaObjectId = (String) infoMap.get(poaSelects.get(0));
				poaObject = DomainObject.newInstance(context);
				poaObject.setId(poaObjectId);
				String poaState = poaObject.getInfo(context, DomainConstants.SELECT_CURRENT);
				logger.info("\n --- poaState "+poaState+" --- ");
				if(BusinessUtil.isNotNullOrEmpty(poaState)) {
					if(STATE_RELEASE.equalsIgnoreCase(poaState) || STATE_OBSOLETE.equalsIgnoreCase(poaState)) {
						logger.info("\n--- POA is in check in not possible state ---");
						errorMsg = "POA is not in a suitable state to check in files";
						response = new POAResponseData(RESPONSE_TWO_ZERO_ONE, errorMsg);
					} else {
						logger.info("\n--- POA is in check in possible state ---");
						String zipFileData = poaRequestData.getZipFileData();
						zipFileData = zipFileData.replaceAll(STR_REJEX, DomainConstants.EMPTY_STRING);
						String workSpacePath = context.createWorkspace();
						logger.info("\n--- workSpacePath "+workSpacePath+" ---");
						byte[] data = new byte[0];
						try {
							
							//Modified By RTA Sogeti for Defect #31866 STARTS
							data = Base64.getDecoder().decode(zipFileData);
						} catch (IllegalArgumentException e) {
							//Modified By RTA Sogeti for Defect #31866 ENDS
							throw new Exception(Decode_Error);
						}
						logger.info("\n--- BYTE LENGTH "+data.length+"---");
						Path destinationFile = Paths.get(workSpacePath, POA_DETAILS.concat(FORMAT_ZIP));
						Files.write(destinationFile, data);
						poaDetails = new File(workSpacePath.concat(SEPARATOR).concat(POA_DETAILS));
						zipFile = new File(workSpacePath.concat(SEPARATOR).concat(POA_DETAILS).concat(FORMAT_ZIP));
						if(!poaDetails.exists()) {
							poaDetails.mkdir();
						}
						//unzipFile(workSpacePath + SEPARATOR + POA_DETAILS + FORMAT_ZIP, poaDetails.getAbsolutePath());
						StringBuffer Buffer= new StringBuffer();
						Buffer.append(workSpacePath);
						Buffer.append(SEPARATOR);
						Buffer.append(POA_DETAILS);
						Buffer.append(FORMAT_ZIP);
						try {
							unzipFile(Buffer.toString(),poaDetails.getAbsolutePath());
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							throw new Exception(Invalid_Compression);
						}
						File[] directoryListing = poaDetails.listFiles();
						for (File child : directoryListing) {
							logger.info("\n--- File --> "+child.getName());
							logger.info("\n--- File --> "+child.getAbsolutePath());
						}
						logger.info("\n--- directory size "+directoryListing.length+" ---");
						if (directoryListing != null) {
							logger.info("\n--- converted zip contains files ---");
							POA poa = new POA(poaObjectId);
							ArtworkFile artworkfile = poa.getArtworkFile(context);
							String artworkFileId = artworkfile.getObjectId(context);
							String existingArtworkFileName = getExistingFileName(context, artworkfile, AWLFormat.ARTWORK.get(context));
							String existingResponseFileName = getExistingFileName(context, artworkfile, AWLFormat.GS1_RESPONSE.get(context));
							List<Triplet<AWLFormat, File, String>> tripletList = new ArrayList();
							AWLFormat formatGS1Response = AWLFormat.getFormatENUM(context, AWLFormat.GS1_RESPONSE.get(context), false);
							AWLFormat formatArtwork = AWLFormat.getFormatENUM(context, AWLFormat.ARTWORK.get(context), false);
							String fileFormat = null;
							String strServerPath = i18nObject.GetString(STRING_RESOURCE_EMX_CPN, strLocale, STRING_RESOURCE_KEY_SERVER_PATH);
							File xsdFile = new File(strServerPath + XSD_PATH);
							logger.info("\n--- xsdFile: "+strServerPath + XSD_PATH+" ---");
							String inputFileName = null;
							boolean fileNotMatchingException = false;
							boolean artworkFileInReviewState = false;
							String artworkFileInReview = null;
							HashMap<String, String> existingFileNames = new HashMap<String, String>();
							logger.info("\n--- preparing triplets for check in ---");
							
							//Modified By RTA Sogeti for Defect #33493 STARTS
							//FIRST ITERATION TO TAKE ONLY XML FILES AND PLACE IT FIRST IN THE TRIPLET
							for (File child : directoryListing) {
								inputFileName = child.getName();
								inputFileNames.add(inputFileName);
								fileFormat = getFileFormat(inputFileName);
								if(BusinessUtil.isNotNullOrEmpty(fileFormat)) {
									if(FORMAT_XML.equalsIgnoreCase(fileFormat)) {
										if(DomainConstants.EMPTY_STRING.equals(existingResponseFileName) || inputFileName.equals(existingResponseFileName)) {
											tripletList.add(new Triplet<AWLFormat, java.io.File, String>(context, formatGS1Response, child, AWLFormat.GS1_RESPONSE.get(context)));
											isGS1Involved = STR_TRUE;
										} else {
											//THERE IS ANOTHER ARTWORK CHECKED INTO POA WITH A DIFFERENT NAME
											//CANNOT CHECK IN ARTWORK WITH THIS NAME
											fileNotMatchingException = true;
											existingFileNames.put(AWLFormat.GS1_RESPONSE.get(context), existingResponseFileName);
											logger.info("\n\n&&&&&&&&&&&& GS1 NOT MATCHING &&&&&&&&&&&&&&");
										}
									}
								}
						    }
							
							//SECOND ITERATION TO TAKE ONLY PDF FILES AND PLACE IT SECOND IN THE TRIPLET IF XML ALSO PRESENT
							for (File child : directoryListing) {
								inputFileName = child.getName();
								//inputFileNames.add(inputFileName);
								fileFormat = getFileFormat(inputFileName);
								if(BusinessUtil.isNotNullOrEmpty(fileFormat)) {
									if(FORMAT_AI.equalsIgnoreCase(fileFormat) || FORMAT_PDF.equalsIgnoreCase(fileFormat)) {
										if(DomainConstants.EMPTY_STRING.equals(existingArtworkFileName) || inputFileName.equals(existingArtworkFileName)) {
											if(STATE_REVIEW.equalsIgnoreCase(poaState)) {
												artworkFileInReviewState = true;
												artworkFileInReview = inputFileName;
												break;
											}
											tripletList.add(new Triplet<AWLFormat, java.io.File, String>(context, formatArtwork, child, AWLFormat.ARTWORK.get(context)));
											isArtworkInvolved = STR_TRUE;
										} else {
											//THERE IS ANOTHER ARTWORK CHECKED INTO POA WITH A DIFFERENT NAME
											//CANNOT CHECK IN ARTWORK WITH THIS NAME
											fileNotMatchingException = true;
											existingFileNames.put(AWLFormat.ARTWORK.get(context), existingArtworkFileName);
											logger.info("\n\n&&&&&&&&&&&& ARTWORK NOT MATCHING &&&&&&&&&&&&&&");
										}
										
									} 
								}
						    }
							//Modified By RTA Sogeti for Defect #33493 ENDS
							
							
							if(fileNotMatchingException) {
								return new POAResponseData(RESPONSE_TWO_ZERO_ZERO, NON_MATCH_EXISTING_FILE, existingFileNames, inputFileNames);
							}
							if(artworkFileInReviewState) {
								return new POAResponseData(RESPONSE_TWO_ZERO_ZERO, ARTWORK_NOT_ALLOWED_IN_REVIEW.replaceAll(REGX,BRO.concat(artworkFileInReview).concat(BRC)));
							}
							try {
								logger.info("\n--- isArtworkInvolved: "+isArtworkInvolved+" ---");
								logger.info("\n--- isGS1Involved: "+isGS1Involved+" ---");
								logger.info("\n--- attempt check in ---");
								logger.info("\n--- tripletList : "+tripletList.size()+" ---");
								if(tripletList!=null && tripletList.size()>0) {
									logger.info("\n--- tripletList: "+tripletList+" ---");
									new ArtworkCheckInUtil().checkInFilesToPOA(context, poaObjectId, tripletList, xsdFile);
									normalizeArtworkContentResponse(context, new String[] {artworkFileId});
									transferOwnership(context, new String[] {artworkFileId, isArtworkInvolved, isGS1Involved, webServiceUser});
									logger.info("\n--- AFTER CHECKIN SET RESPONSE 200 OK ---");
									response = new POAResponseData(RESPONSE_TWO_ZERO_ZERO, SUCCESS_MESSAGE, inputFileNames);
									logger.info("\n--- completed check in ---");
								}
							} catch (Exception e) {
								e.printStackTrace();
								errorMsg = e.getMessage();
								logger.info("\n\n--- CHECK IN ERROR = "+errorMsg+" ---");
								logger.info("\n--- ERROR WHILE CHECK IN ---");
								isCheckinErrorThrown = true;
								if(errorMsg.contains(GENERIC_CHECK_IN_ERROR_1) || errorMsg.contains(GENERIC_CHECK_IN_ERROR_2) || errorMsg.contains(GENERIC_CHECK_IN_ERROR_3) || errorMsg.contains(GENERIC_CHECK_IN_ERROR_4)) {
									logger.info("\n\n%%%%%%%%%%%%% START NOTMALIZER FROM CATCH %%%%%%%%%%%%%%%%%%%");
									normalizeArtworkContentResponse(context, new String[] {artworkFileId});
									transferOwnership(context, new String[] {artworkFileId, isArtworkInvolved, isGS1Involved, webServiceUser});
									logger.info("\n\n%%%%%%%%%%%%% END NOTMALIZER FROM CATCH %%%%%%%%%%%%%%%%%%%");
								}
								throw e;
							}
						} 
					}
				}
			} else {
				logger.info("\n--- Empty POA MapList ---");
				errorMsg = "POA does not exist in the Enovia System";
				response = new POAResponseData(RESPONSE_TWO_ZERO_ONE, errorMsg);
				logger.info("\n--- SET RESPONSE 201 OK ---");
			}
		} catch(Exception e) {
			logger.info("\n--- OUTER CATCH EXECUTES ---");
			errorMsg = e.getMessage().trim();
			if (errorMsg.contains(NO_CONTEXT_USER) || errorMsg.contains(DOES_NOT_EXIST)) {
				e.printStackTrace();
				errorMsg = "User does not exist.";
				response = new POAResponseData(RESPONSE_FOUR_ZERO_FOUR, errorMsg);
				logger.info("\n--- SET RESPONSE 404 NOT FOUND ---");
				logger.info("\nerrorMsg:"+errorMsg);
			} else if (errorMsg.contains(AUTHORIZED) || errorMsg.contains(INVALID_PASSWORD)) {
				e.printStackTrace();
				errorMsg = "User not authorised.";
				response = new POAResponseData(RESPONSE_FOUR_ZERO_ONE, errorMsg);
				logger.info("\n--- SET RESPONSE 401 UNAUTHORIZED ---");
				logger.info("\nerrorMsg:"+errorMsg);
			} else if(isCheckinErrorThrown && (errorMsg.contains(GENERIC_CHECK_IN_ERROR_1) || errorMsg.contains(GENERIC_CHECK_IN_ERROR_2) || errorMsg.contains(GENERIC_CHECK_IN_ERROR_3) || errorMsg.contains(GENERIC_CHECK_IN_ERROR_4))) {
				logger.info("\nerrorMsg:"+errorMsg);
				response = new POAResponseData(RESPONSE_TWO_ZERO_ZERO, SUCCESS_MESSAGE, inputFileNames);
				logger.info("\n--- SET RESPONSE 200 OK "+SUCCESS_MESSAGE+"---");
			} else if(errorMsg.equals(Decode_Error)) {
				e.printStackTrace();
				errorMsg = "Error Occurred while Unzipping.";
				response = new POAResponseData(RESPONSE_FOUR_ZERO_FOUR, errorMsg);
				logger.info("\nerrorMsg:"+errorMsg);
			} else if(errorMsg.equals(Invalid_Compression)) {
				e.printStackTrace();
				errorMsg = "Error Occurred while Decoding.";
				response = new POAResponseData(RESPONSE_FOUR_ZERO_FOUR, errorMsg);
				logger.info("\nerrorMsg:"+errorMsg);
			} else {
				e.printStackTrace();
				errorMsg = "Enovia System Error, please contact Administrator.";
				response = new POAResponseData(RESPONSE_FIVE_ZERO_ZERO, errorMsg);
				logger.info("\n--- SET RESPONSE 500 INTERNAL SERVER ERROR ---");
				logger.info("\nerrorMsg:"+errorMsg);
			}
		} finally {
			logger.info("\n--- FINALLY EXECUTES ---");
			if(zipFile!=null) {
				poaDetails.delete();
			}
			if(poaDetails!=null) {
				FileUtils.deleteDirectory(poaDetails);
			}
			if(isContextPushed) {
				ContextUtil.popContext(context);
				logger.info("\n --- Context with Super Popped ---");
			}
			if(context!=null) {
				context.shutdown();
				logger.info("\n --- Context Shut Down ---");
			}	
		} 
		return response;
	}
	
	/**
	 * @param context
	 * @param args
	 * @throws Exception
	 */
	private static void normalizeArtworkContentResponse(Context context, String[] args) throws Exception {
		if(args!=null && args.length>0) {
			try {
				logger.info("\n--------- INSIDE NORMALIZER START -----------");
				logger.info("args[0] = "+args[0]);
				ArtworkFile artworkFile = new ArtworkFile(args[0]);
				logger.info("artworkFile = "+artworkFile.getObjectId());
				MapList format1 = artworkFile.getFileVersionInfoByFileFormat(context, AWLFormat.GS1_RESPONSE.get(context));
				MapList format2 = artworkFile.getFileVersionInfoByFileFormat(context, AWLFormat.ARTWORK.get(context));
				if(BusinessUtil.isNotNullOrEmpty(format1) && BusinessUtil.isNotNullOrEmpty(format2)) {
					logger.info("arg1 = "+format1);
					logger.info("arg2 = "+format2);
					context.setCustomData(RESP_VAL, "true");
					logger.info("setCustomData SET TO TRUE");
					Map fileformat1 = (Map)format1.get(0);
					Map fileformat2 = (Map)format2.get(0);
					DomainObject artworkType = DomainObject.newInstance(context, DomainConstants.EMPTY_STRING+fileformat2.get(FILE_ID));
					DomainObject gs1ResponseType = DomainObject.newInstance(context, DomainConstants.EMPTY_STRING+fileformat1.get(FILE_ID));
					boolean setArtworkResponse = false;
					StringBuffer strBuf = new StringBuffer();
					strBuf.append(FROM);
					strBuf.append(BRO);
					strBuf.append(AWLRel.ARTWORK_RESPONSE.get(context));
					strBuf.append(BRC);
					strBuf.append(ID);
					StringList objectSelects = new StringList(strBuf.toString());
					logger.info("objectSelects = "+objectSelects);
					Map infoMap = artworkType.getInfo(context, objectSelects);
					logger.info("infoMap = "+infoMap);
					if(infoMap!=null && infoMap.size()>0) {
						String connectionId = (String)infoMap.get(objectSelects.get(0));
						if(BusinessUtil.isNotNullOrEmpty(connectionId)) {
							DomainRelationship.setToObject(context, connectionId, gs1ResponseType);
							setArtworkResponse = true;
						}
					}
					logger.info("setArtworkResponse = "+setArtworkResponse);
					if(!setArtworkResponse) {
						artworkFile.linkArtworkResponse(context,
								(GS1ResponseValidationUtil) null,
								(String) fileformat1.get(FILE_ID),
								(String) fileformat2.get(FILE_ID));
					}
				}
				logger.info("\n--------- INSIDE NORMALIZER END -----------");
			} catch (Exception exp) {
				logger.info("\n\n$$$$$$$ ERROR IN NORMALIZER $$$$$$ - "+exp.getMessage());
				exp.printStackTrace();
				throw exp;
			}
		}
	}
	
	public static void transferOwnership(Context context, String[] args) throws Exception {
		if(args!=null && args.length>3) {
			boolean isArtworkInvlolved = false;
			try {
				logger.info("\n--- Transfer Ownership Start ---");
				ArtworkFile artworkFile = new ArtworkFile(args[0]);
				if(BusinessUtil.isNotNullOrEmpty(args[1]) && "TRUE".equals(args[1])) {
					MapList format2 = artworkFile.getFileVersionInfoByFileFormat(context, AWLFormat.ARTWORK.get(context));
					Map fileformat2 = (Map)format2.get(0);
					DomainObject artworkType = DomainObject.newInstance(context, DomainConstants.EMPTY_STRING+fileformat2.get(FILE_ID));
					if(BusinessUtil.isNotNullOrEmpty(args[3])) {
						artworkType.setOwner(context, args[3]);
					}
					isArtworkInvlolved = true;
				}
				if(BusinessUtil.isNotNullOrEmpty(args[2]) && "TRUE".equals(args[2])) {
					MapList format1 = artworkFile.getFileVersionInfoByFileFormat(context, AWLFormat.GS1_RESPONSE.get(context));
					Map fileformat1 = (Map)format1.get(0);
					DomainObject gs1ResponseType = DomainObject.newInstance(context, DomainConstants.EMPTY_STRING+fileformat1.get(FILE_ID));
					if(BusinessUtil.isNotNullOrEmpty(args[3])) {
						gs1ResponseType.setOwner(context, args[3]);
					}
				}
				if(isArtworkInvlolved) {
					String PERSON_USER_AGENT = PropertyUtil.getSchemaProperty(context,"person_UserAgent");
					logger.info("\n--- PERSON_USER_AGENT: "+PERSON_USER_AGENT+" ---");
					String  RELATIONSHIP_IMAGE_HOLDER = PropertyUtil.getSchemaProperty(context,"relationship_ImageHolder");
					String  RELATIONSHIP_XMP_DOC = PropertyUtil.getSchemaProperty(context,"relationship_pgXMPDocument");
					String  TYPE_IMAGE_HOLDER = PropertyUtil.getSchemaProperty(context,"type_ImageHolder");
					String  TYPE_XMP_DOC = PropertyUtil.getSchemaProperty(context,"type_pgXMPDocument");
					Pattern relPattern = new Pattern(RELATIONSHIP_IMAGE_HOLDER);
					relPattern.addPattern(RELATIONSHIP_XMP_DOC);
					Pattern typePattern = new Pattern(TYPE_IMAGE_HOLDER);
					typePattern.addPattern(TYPE_XMP_DOC);
					String strSelects = DomainConstants.SELECT_ID+"|"+DomainConstants.SELECT_NAME+"|"+DomainConstants.SELECT_OWNER;
					StringList slObjectSelects = FrameworkUtil.split(strSelects, "|");
					MapList mlObjectList = artworkFile.getRelatedObjects(context,
								relPattern.getPattern(), //relationship Pattern
								typePattern.getPattern(), //type Pattern
								slObjectSelects, //objectSelects
								null, //relSelects
								true, //getTo
								true, //getFrom
								(short) 1, //recurse
								null, //objectWhere
								null, //relWhere
								0);
					logger.info("\n--- mlObjectList: "+mlObjectList+" ---");
					if(BusinessUtil.isNotNullOrEmpty(mlObjectList)) {
						Map tempMap = null;
						String owner = null;
						DomainObject connectedObj = null;
						for(int i = 0; i<mlObjectList.size(); i++) {
							tempMap = (Map)mlObjectList.get(i);
							owner = (String)tempMap.get(DomainConstants.SELECT_OWNER);
							if(owner.equalsIgnoreCase(PERSON_USER_AGENT)) {
								connectedObj = new DomainObject((String)tempMap.get(DomainConstants.SELECT_ID));
								connectedObj.setOwner(context, args[3]);
							}
						}
					}
				}
				logger.info("\n--- Transfer Ownership End ---");
			} catch (Exception exp) {
				logger.info("\n--- Exception in Transfer Ownership ---");
				exp.printStackTrace();
			}
		}
	}
	
	/**
	 * @param context
	 * @param artwork
	 * @param format
	 * @return
	 * @throws Exception
	 */
	private static String getExistingFileName(Context context, ArtworkFile artwork, String format) throws Exception {
		String fileName = DomainConstants.EMPTY_STRING;
		MapList versionInfo = artwork.getFileVersionInfoByFileFormat(context, format);
		if(versionInfo!=null && versionInfo.size()>0) {
			Map tempMap = (Map)versionInfo.get(0);
			fileName = (String)tempMap.get(FILE_NAME);
		}
		fileName = fileName==null ? DomainConstants.EMPTY_STRING : fileName.trim();
		return fileName;
	}
	/**
	 * @param zipFilePath
	 * @param destDirectory
	 * @throws IOException
	 */
	private static void unzipFile(String zipFilePath, String destDirectory) throws IOException {
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        String filePath = null;
        StringBuffer buffer;
        BufferedOutputStream bos = null;
        byte[] bytesIn;
        int read = 0;
        while (entry != null) {
            //filePath = destDirectory + File.separator + entry.getName();
        	buffer = new StringBuffer();
        	buffer.append(destDirectory);
        	buffer.append(File.separator);
        	buffer.append(entry.getName());
        	filePath = buffer.toString();  
            if (!entry.isDirectory()) {
                // if the entry is a file, extracts it
            	bos = new BufferedOutputStream(new FileOutputStream(filePath));
            	bytesIn = new byte[BUFFER_SIZE];
     	        read = 0;
     	        while ((read = zipIn.read(bytesIn)) != -1) {
     	            bos.write(bytesIn, 0, read);
     	        }
     	        bos.close();
            } else {
                // if the entry is a directory, make the directory
                File dir = new File(filePath);
                dir.mkdir();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
    }
	
	/**
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	private static String getFileFormat(String fileName) throws Exception {
		return fileName.substring(fileName.lastIndexOf(DOT)+1, fileName.length());
	}

}
