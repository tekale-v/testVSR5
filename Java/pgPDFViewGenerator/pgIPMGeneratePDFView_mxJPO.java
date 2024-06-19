/**
  JPO created for DSM 2015x.5
   Project Name: DSM(Sogeti)
   JPO Name: pgIPMGeneratePDFView
   Clone From/Reference:
   Purpose: JPO created to implement the iText HTML to PDF Converter.
 **/
import java.io.ByteArrayInputStream;
import java.io.File;
//Modified by DSM(Sogeti) - 2015x.5 fix for PDF Views defects 16725 - Ends
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.font.PdfEncodings;
//Modified by DSM(Sogeti) - 2015x.5.1 fix for PDF Views defects 17543 - Ends
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import com.itextpdf.layout.splitting.DefaultSplitCharacters;
import com.itextpdf.licensekey.LicenseKey;
//Modified for 2018x State is showing backend name for FPP START
import com.matrixone.apps.cpn.util.BusinessUtil;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.eMatrixDateFormat;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
//Modified by DSM(Sogeti) - 2015x.5 fix for PDF Views defects 16725 - Starts
import matrix.util.StringList;


public class pgIPMGeneratePDFView_mxJPO {

	public static volatile String DATE_FORMAT = null;
	public static volatile String RESOURCES  = null;
	public static volatile String LICENSE  = null;
	public static volatile String PRODUCT_DATA_TYPES  = null;
	public static volatile String NONSTRUCTURED_TYPES  = null;
	public static volatile String PAGE_HEIGHT  = null;
	public static volatile String PAGE_WIDTH  = null;
	private static final String CONST_OBJECTID = "objectId";
	private static final String CONST_EFFECTIVE_DATE_WITHSPACE = " Effective Date ";
	private static final String CONST_PRINTED_WITHSPACE2 = " Printed  ";
	private static final String CONST_PRINTED_WITHSPACE = " Printed ";
	private static final String CONST_PAGE1_HEADER1_COL1 = "page1.header1.col1";
	private static final String CONST_PAGE1_HEADER1_CENTER = "page1.header1.center";
	private static final String CONST_PAGE1_HEADER2_CENTER = "page1.header2.center";
	private static final String CONST_PAGE1_HEADER3_COL1 = "page1.header3.col1";
	private static final String CONST_PAGE1_HEADER3_COL2 = "page1.header3.col2";
	private static final String CONST_PAGE1_HEADER3_COL3 = "page1.header3.col3";
	private static final String CONST_PAGE1_HEADER3_COL4 = "page1.header3.col4";
	private static final String CONST_PAGE1_HEADER4_COL4 = "page1.header4.col4";
		
	private static final String CONST_PAGE2_HEADER2_CENTER = "page2.header2.center";
	private static final String CONST_PAGE2_HEADER1_COL1 = "page2.header1.col1";
	private static final String CONST_PAGE2_HEADER3_COL1 = "page2.header3.col1";
	private static final String CONST_PAGE2_HEADER3_COL2 = "page2.header3.col2";
	private static final String CONST_PAGE2_HEADER3_COL3 = "page2.header3.col3";
	private static final String CONST_PAGE2_HEADER3_COL4 = "page2.header3.col4";
		
	private static final String CONST_PG_COMPANY = "The Procter & Gamble Company - ";
	private static final String CONST_HAS_ATS = "Has ATS: ";
	private static final String CONST_NAME_COLON = "Name: ";
	private static final String CONST_REVISION_COLON = "Revision: ";
	//Added by DSM-2022x.6 for PDF Views (Defect:57677) - Start
	private static final String CONST_TITLE_COLON = "Title: ";
	//Added by DSM-2022x.6 for PDF Views (Defect:57677) - End
	private static final String CONST_SAP_DESCRIPTION = "SAP Description: ";
	//Added by DSM(Sogeti)-2018x.5 for PDF Views Requirements 35306, 32960, 32964, 32965, 32966, 35338, 34926, 32866 and 32869 POA ATS Gendoc- Starts
	private static final String CONST_EXPIRATION_DATE_WITHSPACE = " Expiration Date ";
	private static final String CONST_COLON = ":";
	//Added by DSM(Sogeti)-2018x.5 for PDF Views Requirements 35306, 32960, 32964, 32965, 32966, 35338, 34926, 32866 and 32869 POA ATS Gendoc- Starts
	//Modified by DSM(Sogeti) - 2015x.5.1 fix for PDF Views defects 17543 - Starts
	public static String FONT = "";
	//public static PdfFont FONTDSREGULAR = null;
	StringBuilder fontName = null;
	//Modified by DSM(Sogeti) - 2015x.5.1 fix for PDF Views defects 17543 - Ends
	// Added  by DSM-2018x.3  for PDF Views (Defect id #30319) - Starts
	StringBuilder fontNameForSpecialCharacters = null;
	// Added  by DSM-2018x.3  for PDF Views (Defect id #30319) - Ends
	//22x Upgrade Modification Start
	public static final String CONSTANT_APP_CPN = "emxCPN";
	public static final String CONSTANT_SRV_PATH = "emxCPN.ServerPath";
	public static final String CONST_PDF_BASE_FOLDER = "pdfHtmlBase";
	public static final String CONSTANT_3DS_FONT = "3ds-Regular.ttf";
	public static final String CONSTANT_FREE_SANS = "FreeSans.ttf";
	public static final String CONSTANT_FOLDER_FONTS = "fonts";
	//22x Upgrade Modification End
	
	//Constructor
	public pgIPMGeneratePDFView_mxJPO (Context context, String args[]) throws Exception {  
		DATE_FORMAT = EnoviaResourceBundle.getProperty(context,"eServiceSuiteCPN.defaultDateFormat");
		PRODUCT_DATA_TYPES = EnoviaResourceBundle.getProperty(context,"emxCPN.ProductDataTemplate.type_ProductDataIPMPDF.TypeInclusionList");
		NONSTRUCTURED_TYPES = EnoviaResourceBundle.getProperty(context,"emxCPN.ProductDataTemplate.type_ProductDataIPMPDF.NonStructuredTypeInclusionList");
		//Load the PageSize Info
		PAGE_HEIGHT = EnoviaResourceBundle.getProperty(context,"emxCPN.RenderPDF.iText.PAGEHEIGHT");
		PAGE_WIDTH = EnoviaResourceBundle.getProperty(context,"emxCPN.RenderPDF.iText.PAGEWIDTH");
		//Load License information
		RESOURCES = EnoviaResourceBundle.getProperty(context,"emxCPN.RenderPDF.iTextResource");
		LICENSE = EnoviaResourceBundle.getProperty(context,"emxCPN.RenderPDF.iTextLicenseKey");
		LicenseKey.loadLicenseFile(LICENSE);
		
		
		//Modified by DSM(Sogeti) - 2015x.5.1 fix for PDF Views defects 17543 - Starts
		//22x Upgrade Modification Start
		fontName = new StringBuilder(EnoviaResourceBundle.getProperty(context, CONSTANT_APP_CPN, context.getLocale(),CONSTANT_SRV_PATH)).append(File.separator).append(CONST_PDF_BASE_FOLDER).append(File.separator).append(CONSTANT_FOLDER_FONTS).append(File.separator).append(CONSTANT_3DS_FONT);
		//22x Upgrade Modification End
		//Modified by DSM(Sogeti) - 2015x.5.1 fix for PDF Views defects 17543 - Ends
		// Added  by DSM-2018x.3  for PDF Views (Defect id #30319) - Starts
		//22x Upgrade Modification Start
		fontNameForSpecialCharacters = new StringBuilder(EnoviaResourceBundle.getProperty(context, CONSTANT_APP_CPN, context.getLocale(),CONSTANT_SRV_PATH)).append(File.separator).append(CONST_PDF_BASE_FOLDER).append(File.separator).append(CONSTANT_FOLDER_FONTS).append(File.separator).append(CONSTANT_FREE_SANS);
		//22x Upgrade Modification End
		// Added  by DSM-2018x.3  for PDF Views (Defect id #30319) - Ends
		
	}
	
	
	
	
	
	//Main Method
	public int mxMain(Context context, String args[]) throws Exception {
		return 1;
	}
	/**
	 * @Desc Method to create and stamp the PDF file.
	 * @Author Added by DSM(Sogeti)-2015x.5 to use iText for PDF Views
	 * @param Context
	 * @param args
	 * @throws Exception
	 */
	public String createPdf(Context context, String args[]) throws Exception {
		HashMap argumemtMap = (HashMap) JPO.unpackArgs(args);
		String strHtmlSource = (String) argumemtMap.get("strHTMLWithData");
		String strObjectId = (String) argumemtMap.get(CONST_OBJECTID);
		String strDestFileName = (String) argumemtMap.get("strPDFfileName");
		Map mapPDFTicketData = (Map) argumemtMap.get("mapPDFTicketData");
		return (createPdf(context, strHtmlSource, strObjectId, strDestFileName, mapPDFTicketData));
	}
	/**
	 * @Desc Method to create and stamp the PDF file.
	 * @Author Added by DSM(Sogeti)-2015x.5 to use iText for PDF Views
	 * @param Context 
	 * @param strHtmlSource - HTML file
	 * @param strObjectId - Object Id
	 * @param strDestFileName - PDF file name
	 * @param mapPDFTicketData - object and pdf header/footer details
	 * @return String - PDF Path
	 * @throws Exception
	 */
	public String createPdf(Context context, String strHtmlSource, String strObjectId, String strDestFileName, Map mapPDFTicketData) throws Exception {
		String strPDFpath = null;
		String strPDFpathGendoc = null;
		Map BOMPack = new HashMap();
		String	RefDocID = null;
		String[] args = null;
		//Modified by DSM(Sogeti) - 2015x.5 fix for PDF Views defects 16725 - Starts
		PdfDocument readers = null;
		//Modified by DSM(Sogeti) - 2015x.5 fix for PDF Views defects 16725 - Ends
		//Modified by DSM(Sogeti) - 2015x.5 fix for PDF Views defects 15255 - Starts
		boolean isFooterExist = false;
		//Modified by DSM(Sogeti) - 2015x.5 fix for PDF Views defects 15255 - Ends
		try {
			//Creating Temp directories
			String strSessionId = context.getSession().getSessionId();
			if (strSessionId.indexOf(":") != -1) {
					strSessionId = strSessionId.substring(0, strSessionId.indexOf(":"));
			}
			String strTimeStamp = Long.toString(System.currentTimeMillis());
			String strTempFolderName = new StringBuffer(strSessionId).append(strTimeStamp).append(strObjectId).toString();
			String strTempIODirectory = System.getProperty("java.io.tmpdir");
			strPDFpath = new StringBuffer(strTempIODirectory).append(java.io.File.separator).append(strTempFolderName).append(java.io.File.separator).append(strDestFileName).toString();
			File fPdf = new File(strPDFpath);
			fPdf.getParentFile().mkdirs();
			
			//Define page size of PDF
			float fheight = Float.parseFloat(PAGE_HEIGHT);
			float fwidth = Float.parseFloat(PAGE_WIDTH);
			Rectangle pagesize = new Rectangle(fheight*72,fwidth*72);
			PageSize newpage = new PageSize(pagesize);
			
			//Create Document
			PdfWriter pWriter= new PdfWriter(strPDFpath);
			PdfDocument pdfDocument = new PdfDocument(pWriter);
			//Modified by DSM(Sogeti) - 2015x.5 fix for PDF Views defects 16725 - Starts
			int iTotalPages1 = pdfDocument.getNumberOfPages();
			//Modified by DSM(Sogeti) - 2015x.5 fix for PDF Views defects 16725 - Ends
			pdfDocument.setDefaultPageSize(newpage);
			
			//PDF conversion
			ConverterProperties cp = new ConverterProperties();
			FontProvider fp = new FontProvider();
			fp.addStandardPdfFonts();
			//Added by DSM(Sogeti)-2015x.5 for PDF Views (Defect ID-16606) - Starts
			fp.addSystemFonts();
			//Added by DSM(Sogeti)-2015x.5 for PDF Views (Defect ID-16606) - Ends
			
			//Modified by DSM(Sogeti) - 2015x.5.1 fix for PDF Views defects 17543 - Starts
			fp.addFont(FONT);
			//Modified by DSM(Sogeti) - 2015x.5.1 fix for PDF Views defects 17543 - End
			// Added  by DSM-2018x.3  for PDF Views (Defect id #30319) - Starts
			fp.addFont(fontNameForSpecialCharacters.toString());
			// Added  by DSM-2018x.3  for PDF Views (Defect id #30319) - Ends
			
			fp.addDirectory(RESOURCES);
			cp.setFontProvider(fp);
			cp.setBaseUri(RESOURCES);
			//Convert HTML to PDF.
			InputStream inputStream = new ByteArrayInputStream(strHtmlSource.getBytes("UTF-8"));
			Document doc = HtmlConverter.convertToDocument(inputStream, pdfDocument, cp);
			try {
			//Setting the default split character
			doc.setSplitCharacters(new SplitChar());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				doc.close();
			    pdfDocument.close();
			}
			String strPDFViewType = (String) mapPDFTicketData.get(pgV3Constants.PDF_VIEW);
			//Append TS Files
			MapList genDocList = new MapList();
			String strObjectType = (String) mapPDFTicketData.get(DomainConstants.SELECT_TYPE);
			String strPDFTemp = null;
			StringBuffer sbFileName = null;
			StringBuffer sbDocList = null;
			String strfileName = null;
			Map genDocMap = new HashMap();
			InputStream pdf = null;
			PdfDocument pdfDoc = null;
			String strTempDirectory = new StringBuffer(strTempIODirectory).append(java.io.File.separator).append(strTempFolderName).toString();
			//Modified by DSM(Sogeti) - 2015x.5 fix for PDF Views defects 15255,15686 - Starts
			if(!pgV3Constants.PDFVIEW_CONSOLIDATEDPACKAGING.equalsIgnoreCase(strPDFViewType) && !pgV3Constants.PDFVIEW_WAREHOUSE.equalsIgnoreCase(strPDFViewType)){
			isFooterExist = true;
			}
			//Stamp header and footer for PDF
			//Modified by DSM(Sogeti) - 2015x.5 fix for PDF Views defects 16725 - Starts
			stampHeaderAndFooter(context,strPDFpath,strTempFolderName,strTempIODirectory,strDestFileName,mapPDFTicketData,true,isFooterExist,1,iTotalPages1);
			//Modified by DSM(Sogeti) - 2015x.5 fix for PDF Views defects 16725 - Ends
			if(pgV3Constants.PDFVIEW_GENDOC.equalsIgnoreCase(strPDFViewType)){
				//Get Connected Rendition Object
				BOMPack.put(CONST_OBJECTID, strObjectId);
				args = JPO.packArgs(BOMPack); 
				pgIPMPDFViewUtil_mxJPO RefDoc =new pgIPMPDFViewUtil_mxJPO(context,args);
				RefDocID = RefDoc.connectFPPToRenditionobj(context,strObjectId);				
				//Check-in GenDoc File to Rendition object
				if(null != RefDocID && !"".equals(RefDocID)) {
					strPDFpathGendoc = new StringBuffer(strTempIODirectory).append(java.io.File.separator).append(strTempFolderName).toString();
					//Modified by DSM(Sogeti) - 2022x.6 fix for PDF Views Requirement 47656  - Starts
					String strObjType = (String) mapPDFTicketData.get(DomainConstants.SELECT_TYPE);
					if(pgV3Constants.TYPE_STRUCTURED_ATS.equals(strObjType)) {
						strPDFpathGendoc = new StringBuffer(strTempIODirectory).append(java.io.File.separator).append(strTempFolderName).toString();
						Map<String, String> ioParams1 = new HashMap<>();
						ioParams1.put("PDF_PATH",strPDFpath );
						ioParams1.put("TEMP_FOLDER_NAME", strTempFolderName);
						ioParams1.put("TEMP_IO_DIRECTORY", strTempIODirectory);
						ioParams1.put("DEST_FILE_NAME", strDestFileName);
						stampHeaderAndFooterGendocPDFView(ioParams1,mapPDFTicketData,1,iTotalPages1);
					}
					//Modified by DSM(Sogeti) - 2022x.6 fix for PDF Views Requirement 47656  - Ends
					DomainObject dobject = DomainObject.newInstance(context, RefDocID);
					//Commented by DSM for PDF views 2018x.3 Defect - 24078 : Starts
					/*String strProject = EnoviaResourceBundle.getProperty(context,"emxCPN.RenderPDF.projectName");
					String strOrganization = EnoviaResourceBundle.getProperty(context,"emxCPN.RenderPDF.organizationName");
					dobject.setPrimaryOwnership(context, strProject, strOrganization);*/
					//Commented by DSM for PDF views 2018x.3 Defect - 24078 : Ends
					dobject.checkinFile(context, false, false, "",  pgV3Constants.FILE_FORMAT_GENERIC, strDestFileName, strPDFpathGendoc);
				}
			}
			//Modified by DSM(Sogeti) - 2015x.5 fix for PDF Views defects 15255,15686 - Ends
			if(pgV3Constants.PDFVIEW_CONSOLIDATEDPACKAGING.equalsIgnoreCase(strPDFViewType)|| pgV3Constants.PDFVIEW_WAREHOUSE.equalsIgnoreCase(strPDFViewType) || (pgV3Constants.PDFVIEW_CONTRACTPACKAGING.equalsIgnoreCase(strPDFViewType) && pgV3Constants.TYPE_STRUCTURED_ATS.equals(strObjectType))) { //Modified by DSM(Sogeti) - 2022x.6 fix for PDF Views Req 47667  - Starts/Ends
				//Modified by DSM(Sogeti) - 2015x.5 fix for PDF Views defects 16725 - Starts
				StringList slPageNum = new StringList();
				//Footer stamping will be done for XML generated pdf pages.
				stampHeaderAndFooter(context,strPDFpath,strTempFolderName,strTempIODirectory,strDestFileName,mapPDFTicketData,false,true,1,iTotalPages1);
				//Modified by DSM(Sogeti) - 2015x.5 fix for PDF Views defects 16725 - Ends
				Map Argsmap = new HashMap();
				Argsmap.put(CONST_OBJECTID, strObjectId);
				Argsmap.put("strPDFViewType", strPDFViewType);
				Argsmap.put("strTempDirectory", strTempDirectory);
				String[] strArgs= JPO.packArgs(Argsmap);
				pgIPMPDFViewUtil_mxJPO pgIPMPDF=new pgIPMPDFViewUtil_mxJPO(context, strArgs);
				if(pgV3Constants.TYPE_PGFINISHEDPRODUCT.equals(strObjectType)||pgV3Constants.TYPE_PGMASTERFINISHEDPRODUCT.equals(strObjectType) || (pgV3Constants.PDFVIEW_CONSOLIDATEDPACKAGING.equalsIgnoreCase(strPDFViewType)&& pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY.equals(strObjectType))){
//					genDocList = (MapList) JPO.invoke(context, "pgIPMPDFViewUtil", null, "appendedGenDocList", strArgs, MapList.class);
					
					genDocList=(MapList)pgIPMPDF.appendedGenDocList(context, strArgs);
				} else if(pgV3Constants.TYPE_FINISHEDPRODUCTPART.equals(strObjectType)){
//					genDocList = (MapList) JPO.invoke(context, "pgIPMPDFViewUtil", null, "appendedGenDocListForDSOType", strArgs, MapList.class);
					genDocList=(MapList)pgIPMPDF.appendedGenDocListForDSOType(context, strArgs);
				//Modified by DSM(Sogeti) - 2022x.6 fix for PDF Views Req 47667  - Starts
				} else if(pgV3Constants.TYPE_STRUCTURED_ATS.equals(strObjectType)){
					genDocList=(MapList)pgIPMPDF.appendedGenDocListForATSType(context, strArgs);
				}
				//Modified by DSM(Sogeti) - 2022x.6 fix for PDF Views Req 47667  - Ends

				//Modified by DSM(Sogeti) - 2015x.5 fix for PDF Views defects 16725 - Starts
				int iTotalPages =0;
				//Modified by DSM(Sogeti) - 2015x.5 fix for PDF Views defects 16725 - Starts
				if( null != genDocList && genDocList.size()>0)
				{
					List<InputStream> inputPdfList = new ArrayList<InputStream>();					
					inputPdfList.add(new FileInputStream(strPDFpath));
					strPDFTemp = new StringBuffer(strTempIODirectory).append(java.io.File.separator).append(strTempFolderName).append(java.io.File.separator).append("Merge.pdf").toString();
					//Modified by DSM(Sogeti) - 2015x.5 fix for PDF Views defects 16725 - Starts
					readers = new PdfDocument(new PdfWriter(strPDFTemp));
					//Modified by DSM(Sogeti) - 2015x.5 fix for PDF Views defects 16725 - Ends
					sbFileName =  new StringBuffer();
					for (Iterator iterator = genDocList.iterator(); iterator.hasNext();) 
					{
						genDocMap = (Map) iterator.next();
						strfileName = (String)genDocMap.get("fileName");
						sbDocList = new StringBuffer(strTempDirectory).append(java.io.File.separator).append(strfileName);
						inputPdfList.add(new FileInputStream(sbDocList.toString()));
					}
					Iterator<InputStream> pdfIterator = inputPdfList.iterator();
					//Modified by DSM(Sogeti) - 2015x.5 fix for PDF Views defects 16725 - Starts
					int count = 0;
					//Modified by DSM(Sogeti) - 2015x.5 fix for PDF Views defects 16725 - Ends
					// Create reader list for the input pdf files.
					while (pdfIterator.hasNext()) {
							try {
							pdf = pdfIterator.next();
							pdfDoc = new PdfDocument(new PdfReader(pdf));
							pdfDoc.copyPagesTo(1, pdfDoc.getNumberOfPages(), readers);
							//Modified by DSM(Sogeti) - 2015x.5 fix for PDF Views defects 16725 - Starts
							count ++;
							//Modified for 2018x Upgrade STARTS
							//slPageNum.add(pdfDoc.getNumberOfPages());
							slPageNum.add(Integer.toString(pdfDoc.getNumberOfPages()));
							//Modified for 2018x Upgrade ENDS
							//Modified by DSM(Sogeti) - 2015x.5 fix for PDF Views defects 16725 - Ends	
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								pdfDoc.close();
								pdf.close();
							}
							
					}
					//Add Footer Stamp to the merged pagesize
						
					Document document = new Document(readers);
					try {
					//Modified by DSM(Sogeti) - 2015x.5 fix for PDF Views defects 16725 - Starts
					iTotalPages = readers.getNumberOfPages();
					//Modified by DSM(Sogeti) - 2015x.5 fix for PDF Views defects 16725 - Ends
					//Commented by DSM(Sogeti) - 2015x.5 fix for PDF Views defects 15255 - Starts
					//addFooter(context, document, iTotalPages, mapPDFTicketData ,readers);
					

					//isFooterExist = true;
          //Commented by DSM(Sogeti) - 2015x.5 fix for PDF Views defects 15255 - Ends
										
					} catch (Exception e) {
						
						e.printStackTrace();
						
					} finally {
					
						document.close();
						//Modified by DSM(Sogeti) - 2015x.5 fix for PDF Views defects 16725 - Starts
					    //readers.close();
						//Modified by DSM(Sogeti) - 2015x.5 fix for PDF Views defects 16725 - Ends
					}
					 //Deleting the temp file
					File file = new File(strPDFpath);
					if(file.delete()) {
						//Renaming the file
						File oldfile = new File(strPDFTemp);
						file = new File(strPDFpath);
						oldfile.renameTo(file);
					}
				}
				//Modified by DSM(Sogeti) - 2015x.5 fix for PDF Views defects 15255 - Starts
				//Modified by DSM(Sogeti) - 2015x.5 fix for PDF Views defects 16725 - Starts
				//Modified by DSM(Sogeti) - 2022x.6 fix for PDF Views defects 57530  - Starts
				int iXMLPages = 0;
				int temp =0;
				Map mpGendoc;
				String strfooter="";
				//Modified by DSM(Sogeti) - 2022x.6 fix for PDF Views defects 57530  - Ends

				for(int i=1;i<=slPageNum.size();i++){
					//Modified for 2018x Upgrade STARTS
					iXMLPages = iXMLPages + Integer.valueOf(slPageNum.get(i-1)); 
					//iXMLPages = iXMLPages + (Int) slPageNum.get(i-1); 					
					//Modified for 2018x Upgrade ENDS
					//Footer stamping will be skipped for XML generated pdf pages and will be stamped for appended gendocs.
					if(i != 1)
					{
						//Modified by DSM(Sogeti) - 2022x.6 fix for PDF Views defects 57530  - Starts
						if(pgV3Constants.PDFVIEW_CONSOLIDATEDPACKAGING.equalsIgnoreCase(strPDFViewType) && pgV3Constants.TYPE_FINISHEDPRODUCTPART.equals(strObjectType)) {
							if(genDocList.size()>=i-2) {
								mpGendoc = (Map) genDocList.get(i-2);
								strfooter = (String)mpGendoc.get("Footer");
							}
							Map<String, String> ioParams = new HashMap<>();
							ioParams.put("PDF_PATH",strPDFpath );
							ioParams.put("TEMP_FOLDER_NAME", strTempFolderName);
							ioParams.put("TEMP_IO_DIRECTORY", strTempIODirectory);
							ioParams.put("DEST_FILE_NAME", strDestFileName);
							ioParams.put("FOOTER", strfooter);
							stampHeaderAndFooterCMPDFView(ioParams,mapPDFTicketData,temp+1,iXMLPages);
							//Modified by DSM(Sogeti) - 2022x.6 fix for PDF Views defects 57530  - Ends

							
						} else {
						stampHeaderAndFooter(context,strPDFpath,strTempFolderName,strTempIODirectory,strDestFileName,mapPDFTicketData,false,false,temp+1,iXMLPages);
						}
						
					}
					temp = iXMLPages;
				}
				//Modified by DSM(Sogeti) - 2015x.5 fix for PDF Views defects 16725 - Ends
				//Modified by DSM(Sogeti) - 2015x.5 fix for PDF Views defects 15255 - Ends
				}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return strPDFpath;
	}
	
	//Modified by DSM(Sogeti) - 2022x.6 fix for PDF Views Requirement 47656  - Starts
			/**
			 * @Desc Method to stamp the PDF file.
			 * @Author Added by DSM(Sogeti)-2022x.6 to use iText for PDF Views
			 * @param Context 
			 * @param ioparam - contains file related data
			 * @param mapPDFTicketData - object and pdf header details
			 * @throws Exception
			 */
			public void stampHeaderAndFooterGendocPDFView(Map<String,String> ioParams ,Map mapPDFTicketData,int iStartPageNumber, int iEndPageNumber) throws Exception {
				String strTempIODirectory =DomainConstants.EMPTY_STRING;
				String strTempFolderName = DomainConstants.EMPTY_STRING;
				String strDestFileName = DomainConstants.EMPTY_STRING;
				String strPDFpath = DomainConstants.EMPTY_STRING;
		        String strfooter = DomainConstants.EMPTY_STRING;
				final String CONST_PGAUTH ="P&G AUTHORIZED ";
				final String CONST_EFF = " Eff ";
		        final String CONST_EXP = " Exp ";
		        final String CONST_NA = "N/A";
		        final String CONST_PRINT = "(Printed ";
		        final String CONST_VALID = " valid for 24hrs) ";
		        StringBuffer sbFooterBuffer = new StringBuffer();
				String strEffectiveDate = (String) mapPDFTicketData.get(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE);
	            String strExpirationDate = (String) mapPDFTicketData.get(pgV3Constants.SELECT_ATTRIBUTE_EXPIRATION_DATE);
	            String strRestrictedStamp = pgV3Constants.HIGHLY_RESTRICTED_HYPHEN;
	            String strName = (String) mapPDFTicketData.get(DomainConstants.SELECT_NAME);
	            String strRev = (String) mapPDFTicketData.get(DomainConstants.SELECT_REVISION);
	            Date today = null;
	    		String sCurrentDate = null;
	    		today = new Date();
	    		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
	    		sCurrentDate = dateFormat.format(today);
				if(null != ioParams && ioParams.size() > 0) {
					strTempIODirectory =ioParams.get("TEMP_IO_DIRECTORY");
					strTempFolderName = ioParams.get("TEMP_FOLDER_NAME");
					strDestFileName = ioParams.get("DEST_FILE_NAME");
					strPDFpath = ioParams.get("PDF_PATH");
				}
				strEffectiveDate = getFormattedDate(strEffectiveDate);
				strExpirationDate = getFormattedDate(strExpirationDate);
				sbFooterBuffer.append(strRestrictedStamp).append(CONST_PGAUTH).append(strName).append(pgV3Constants.SYMBOL_DOT).append(strRev).append(CONST_EFF);
				if(UIUtil.isNotNullAndNotEmpty(strEffectiveDate)) {
					sbFooterBuffer.append(strEffectiveDate);
				} else {
					sbFooterBuffer.append(CONST_NA);
				}
				sbFooterBuffer.append(CONST_EXP);
				if(UIUtil.isNotNullAndNotEmpty(strExpirationDate)) {
					sbFooterBuffer.append(strExpirationDate);
				} else {
					sbFooterBuffer.append(CONST_NA);
				}
			    sbFooterBuffer.append(CONST_PRINT).append(sCurrentDate).append(CONST_VALID);
			    strfooter= sbFooterBuffer.toString();
				String strDestPDFpath = new StringBuffer(strTempIODirectory).append(java.io.File.separator).append(strTempFolderName).append(java.io.File.separator).append("temp_"+strDestFileName).toString();
				PdfDocument pdfDocmt = new PdfDocument(new PdfReader(strPDFpath), new PdfWriter(strDestPDFpath));
				Document doc = new Document(pdfDocmt);
				try {
				addFooterforGendocPDFView(doc,pdfDocmt.getNumberOfPages() , mapPDFTicketData,pdfDocmt,iStartPageNumber,strfooter);
				} catch (Exception e) {
				} finally {
					doc.close();
				}
				File file = new File(strPDFpath);
				if(file.delete()){
					File oldfile = new File(strDestPDFpath);
					file = new File(strPDFpath);
					oldfile.renameTo(file);
				}
			}
			
			public void addFooterforGendocPDFView(Document doc, int iTotalPages, Map mapPDFTicketData,PdfDocument pdfDoc,int iStartPageNumber,String strFooter) throws Exception {
				doc.setFontSize(8f);
				doc.setBold();
		        int iPageNumber=1;
		        float PageMargin = 0;
		        PdfFont FONTDSREGULAR = PdfFontFactory.createFont(FontConstants.TIMES_BOLDITALIC);
				for (int i = iStartPageNumber; i <= pdfDoc.getNumberOfPages(); i++) {
					String sFooter = new StringBuffer(strFooter).append( " Page ").append(String.valueOf(iPageNumber)).append(" of ").append(String.valueOf(iTotalPages-iStartPageNumber+1)).toString();
					Rectangle pageSize = pdfDoc.getPage(i).getPageSize();
					PageMargin = (pageSize.getRight() + doc.getRightMargin() - (pageSize.getLeft() + doc.getLeftMargin())) / 2;
					doc.showTextAligned(new Paragraph(sFooter).setFont(FONTDSREGULAR),PageMargin, (doc.getLeftMargin()-10), i, TextAlignment.CENTER, VerticalAlignment.TOP, 0);  
					iPageNumber+=1;
				}
				
			}
			//Modified by DSM(Sogeti) - 2022x.6 fix for PDF Views Requirement 47656  - Ends
	//Modified by DSM(Sogeti) - 2022x.6 fix for PDF Views defects 57530  - Starts
	/**
	 * @Desc Method to stamp the PDF file.
	 * @Author Added by DSM(Sogeti)-2022x.6 to use iText for PDF Views
	 * @param Context 
	 * @param ioparam - contains file related data
	 * @param mapPDFTicketData - object and pdf header details
	 * @throws Exception
	 */
	public void stampHeaderAndFooterCMPDFView(Map<String,String> ioParams ,Map mapPDFTicketData,int iStartPageNumber, int iEndPageNumber) throws Exception {
		String strTempIODirectory ="";
		String strTempFolderName = "";
		String strDestFileName = "";
		String strPDFpath = "";
        String strfooter = "";
		if(null != ioParams && ioParams.size() > 0) {
			strTempIODirectory =ioParams.get("TEMP_IO_DIRECTORY");
			strTempFolderName = ioParams.get("TEMP_FOLDER_NAME");
			strDestFileName = ioParams.get("DEST_FILE_NAME");
			strPDFpath = ioParams.get("PDF_PATH");
            strfooter = ioParams.get("FOOTER");
		}
		String strDestPDFpath = new StringBuffer(strTempIODirectory).append(java.io.File.separator).append(strTempFolderName).append(java.io.File.separator).append("temp_"+strDestFileName).toString();
		PdfDocument pdfDocmt = new PdfDocument(new PdfReader(strPDFpath), new PdfWriter(strDestPDFpath));
		Document doc = new Document(pdfDocmt);
		try {
		String strPDFViewType  = (String) mapPDFTicketData.get(pgV3Constants.PDF_VIEW);
		if((pgV3Constants.PDFVIEW_CONSOLIDATEDPACKAGING.equalsIgnoreCase(strPDFViewType) || pgV3Constants.PDFVIEW_WAREHOUSE.equalsIgnoreCase(strPDFViewType)) ) {
			addFooterforCMWarehousePDFView(doc, iEndPageNumber, mapPDFTicketData,pdfDocmt,iStartPageNumber,strfooter);
		}
		} catch (Exception e) {
		} finally {
			doc.close();
		}
		File file = new File(strPDFpath);
		if(file.delete()){
			File oldfile = new File(strDestPDFpath);
			file = new File(strPDFpath);
			oldfile.renameTo(file);
		}
	}
	
	public void addFooterforCMWarehousePDFView(Document doc, int iTotalPages, Map mapPDFTicketData,PdfDocument pdfDoc,int iStartPageNumber,String strFooter) throws Exception {
		doc.setFontSize(8f);
        int iPageNumber=1;
        doc.setBold();
        doc.setItalic();
        float PageMargin = 0;
		PdfFont FONTDSREGULAR = PdfFontFactory.createFont(FontConstants.TIMES_BOLDITALIC);
		for (int i = iStartPageNumber; i <= iTotalPages; i++) {
			String sFooter = new StringBuffer(strFooter).append( " Page ").append(String.valueOf(iPageNumber)).append(" of ").append(String.valueOf(iTotalPages-iStartPageNumber+1)).toString();
			Rectangle pageSize = pdfDoc.getPage(i).getPageSize();
			PageMargin = (pageSize.getRight() + doc.getRightMargin() - (pageSize.getLeft() + doc.getLeftMargin())) / 2;
			doc.showTextAligned(new Paragraph(sFooter).setFont(FONTDSREGULAR),PageMargin, (doc.getLeftMargin()-10), i, TextAlignment.CENTER, VerticalAlignment.TOP, 0);  
			iPageNumber+=1;
		}
		
	}
	//Modified by DSM(Sogeti) - 2022x.6 fix for PDF Views defects 57530  - Ends

	/**
	 * @Desc Method to stamp the PDF file.
	 * @Author Added by DSM(Sogeti)-2015x.5 to use iText for PDF Views
	 * @param Context 
	 * @param strPDFpath - PDF file path
	 * @param strTempFolderName - Temporary Folder to hold stamp file
	 * @param strTempIODirectory - Directory Path
	 * @param strDestFileName - PDF File Name
	 * @param mapPDFTicketData - object and pdf header details
	 * @throws Exception
	 */
	//Modified by DSM(Sogeti) - 2015x.5 fix for PDF Views defects 15255 - Starts
	//Modified by DSM(Sogeti) - 2015x.5 fix for PDF Views defects 16725 - Starts
	public void stampHeaderAndFooter(Context context, String strPDFpath, String strTempFolderName, String strTempIODirectory, String strDestFileName, Map mapPDFTicketData, boolean bStampHeader, boolean bStampFooter,int iStartPageNumber, int iEndPageNumber) throws Exception {
	//Modified by DSM(Sogeti) - 2015x.5 fix for PDF Views defects 16725 - Ends
	//Modified by DSM(Sogeti) - 2015x.5 fix for PDF Views defects 15255 - Ends
		String strDestPDFpath = new StringBuffer(strTempIODirectory).append(java.io.File.separator).append(strTempFolderName).append(java.io.File.separator).append("temp_"+strDestFileName).toString();
		PdfDocument pdfDocmt = new PdfDocument(new PdfReader(strPDFpath), new PdfWriter(strDestPDFpath));
		Document doc = new Document(pdfDocmt);
		
		try {
		
		int iTotalPages = pdfDocmt.getNumberOfPages();
		String strPDFViewType  = (String) mapPDFTicketData.get(pgV3Constants.PDF_VIEW);
		//stamp the header and footer
		//Modified by DSM(Sogeti) - 2015x.5 fix for PDF Views defects 15255 - Starts
		if(!pgV3Constants.PDFVIEW_GENDOC.equalsIgnoreCase(strPDFViewType) && bStampFooter) {
			addFooter(context, doc, iTotalPages, mapPDFTicketData,pdfDocmt);
		}
		//Modified by DSM(Sogeti) - 2015x.5 fix for PDF Views defects 16725 - Starts
		if((pgV3Constants.PDFVIEW_CONSOLIDATEDPACKAGING.equalsIgnoreCase(strPDFViewType) || pgV3Constants.PDFVIEW_WAREHOUSE.equalsIgnoreCase(strPDFViewType)  || pgV3Constants.PDFVIEW_CONTRACTPACKAGING.equalsIgnoreCase(strPDFViewType))  && !bStampFooter) { //Modified by DSM(Sogeti) - 2022x.6 fix for PDF Views Req 47667  - Starts/Ends
			addFooterforCMWarehouseView(context, doc, iEndPageNumber, mapPDFTicketData,pdfDocmt,iStartPageNumber);
		}
		//Modified by DSM(Sogeti) - 2015x.5 fix for PDF Views defects 16725 - Ends
		if(bStampHeader)
		{
			addHeader(context, doc, iTotalPages, mapPDFTicketData);
		}
		//Modified by DSM(Sogeti) - 2015x.5 fix for PDF Views defects 15255 - Ends
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			doc.close();
		}
		//Deleting the temp file
		File file = new File(strPDFpath);
		if(file.delete()){
			//Renaming the file
			File oldfile = new File(strDestPDFpath);
			file = new File(strPDFpath);
			oldfile.renameTo(file);
		}
	}
	
	//Modified by DSM(Sogeti) - 2015x.5 fix for PDF Views defects 16725 - Starts
	public void addFooterforCMWarehouseView(Context context, Document doc, int iTotalPages, Map mapPDFTicketData,PdfDocument pdfDoc,int iStartPageNumber) throws Exception {
		doc.setFontSize(12f);
		String strObjectType = (String) mapPDFTicketData.get(DomainConstants.SELECT_TYPE);
		String strObjectCurrentState = (String) mapPDFTicketData.get(DomainConstants.SELECT_CURRENT);
		String strObjectStatus = (String) mapPDFTicketData.get(pgV3Constants.SELECT_ATTRIBUTE_STATUS);
		String strPDFViewType = (String) mapPDFTicketData.get(pgV3Constants.PDF_VIEW);
		String strEffectiveDate = (String) mapPDFTicketData.get(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE);
		String strArchiveDate = (String) mapPDFTicketData.get(pgV3Constants.SELECT_ATTRIBUTE_PGARCHIVEDATE);
		String strObjectRevision = (String) mapPDFTicketData.get(DomainConstants.SELECT_REVISION);
		
		String pgIPClassification = (String) mapPDFTicketData.get(pgV3Constants.SELECT_ATTRIBUTE_PGIPCLASSIFICATION);
		String strObjectSecurityStatus = pgIPClassification.format(Locale.ENGLISH, "%s", pgIPClassification);
		
		strEffectiveDate = getFormattedDate(strEffectiveDate);
		strArchiveDate = getFormattedDate(strArchiveDate);
		
		String strFooterStamp = getFooterStampings(context,strObjectType,strObjectCurrentState,strObjectStatus,strPDFViewType,strEffectiveDate,strArchiveDate,strObjectRevision,strObjectSecurityStatus);
		int iPageNumber=1;
		//Modified by DSM(Sogeti) - 2015x.5 PDF Views for requirement 18529,18527 - Start
		PdfFont FONTDSREGULAR = PdfFontFactory.createFont(fontName.toString(), PdfEncodings.IDENTITY_H, true);
		//Modified by DSM(Sogeti) - 2015x.5 PDF Views for requirement 18529,18527 - End
		for (int i = iStartPageNumber; i <= iTotalPages; i++) {
			String strFooter = new StringBuffer(strFooterStamp).append( " Page ").append(String.valueOf(iPageNumber)).append(" of ").append(String.valueOf(iTotalPages-iStartPageNumber+1)).toString();
			Rectangle pageSize = pdfDoc.getPage(i).getPageSize();
			float PageMargin = (pageSize.getRight() + doc.getRightMargin() - (pageSize.getLeft() + doc.getLeftMargin())) / 2;
			doc.showTextAligned(new Paragraph(strFooter).setFont(FONTDSREGULAR),PageMargin, (doc.getLeftMargin()-10), i, TextAlignment.CENTER, VerticalAlignment.TOP, 0);  
			iPageNumber+=1;
		}
	}
	//Modified by DSM(Sogeti) - 2015x.5 fix for PDF Views defects 16725 - Ends
	/**
	 * @Desc Method to stamp Footer in the PDF file.
	 * @Author Added by DSM(Sogeti)-2015x.5 to use iText for PDF Views
	 * @param Context 
	 * @param doc - Document to be stamped
	 * @param iTotalPages - Total number of page in pdf file
	 * @param mapPDFTicketData - object and pdf header details
	 * @throws Exception
	 */
	public void addFooter(Context context, Document doc, int iTotalPages, Map mapPDFTicketData,PdfDocument pdfDoc) throws Exception {
		doc.setFontSize(12f);
		String strObjectType = (String) mapPDFTicketData.get(DomainConstants.SELECT_TYPE);
		String strObjectCurrentState = (String) mapPDFTicketData.get(DomainConstants.SELECT_CURRENT);
		String strObjectStatus = (String) mapPDFTicketData.get(pgV3Constants.SELECT_ATTRIBUTE_STATUS);
		String strPDFViewType = (String) mapPDFTicketData.get(pgV3Constants.PDF_VIEW);
		String strEffectiveDate = (String) mapPDFTicketData.get(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE);
		String strArchiveDate = (String) mapPDFTicketData.get(pgV3Constants.SELECT_ATTRIBUTE_PGARCHIVEDATE);
		String strObjectRevision = (String) mapPDFTicketData.get(DomainConstants.SELECT_REVISION);
		
		//Modified by DSM(Sogeti) - 2015x.5 PDF Views for requirement 18846 - Starts
		String pgIPClassification = (String) mapPDFTicketData.get(pgV3Constants.SELECT_ATTRIBUTE_PGIPCLASSIFICATION);
		String strObjectSecurityStatus = pgIPClassification.format(Locale.ENGLISH, "%s", pgIPClassification);
		
		strEffectiveDate = getFormattedDate(strEffectiveDate);
		strArchiveDate = getFormattedDate(strArchiveDate);
		
		String strFooterStamp = getFooterStampings(context,strObjectType,strObjectCurrentState,strObjectStatus,strPDFViewType,strEffectiveDate,strArchiveDate,strObjectRevision,strObjectSecurityStatus);
		//Modified by DSM(Sogeti) - 2015x.5 PDF Views for requirement 18846 - Ends
		//Modified by DSM(Sogeti) - 2015x.5 PDF Views for requirement 18529,18527 - Start
		PdfFont FONTDSREGULAR = PdfFontFactory.createFont(fontName.toString(), PdfEncodings.IDENTITY_H, true);
		//Modified by DSM(Sogeti) - 2015x.5 PDF Views for requirement 18529,18527 - End
		for (int i = 1; i <= iTotalPages; i++) {
			String strFooter = new StringBuffer(strFooterStamp).append( " Page ").append(String.valueOf(i)).append(" of ").append(String.valueOf(iTotalPages)).toString();
			Rectangle pageSize = pdfDoc.getPage(i).getPageSize();
			float PageMargin = (pageSize.getRight() + doc.getRightMargin() - (pageSize.getLeft() + doc.getLeftMargin())) / 2;
			doc.showTextAligned(new Paragraph(strFooter).setFont(FONTDSREGULAR),PageMargin, (doc.getLeftMargin()-10), i, TextAlignment.CENTER, VerticalAlignment.TOP, 0);  
		}
	}
	
	/**
	 * @Desc Method to update Footer Stampings
	 * @Author Added by DSM(Sogeti)-2015x.5 to use iText for PDF Views
	 * @param Context 
	 * @param strType - Object Type
	 * @param strObjectLifeCycleState - Object Current
	 * @param strObjectStatus - Status attribute of object
	 * @param strPDFViewType - PDF View
	 * @param strStampingDate - Stamp Date
	 * @param strArchiveDate - Archieve Date
	 * @param strObjectRevision - Object Revision
	 * @param String - Footer Stamp
	 * @throws Exception
	 */
	public static String getFooterStampings(Context context,String strType,String strObjectLifeCycleState,String strObjectStatus,String strPDFViewType,String strStampingDate,String strArchiveDate,String strObjectRevision,String strObjectSecurityStatus) throws Exception {
		StringBuffer sbFooterBuffer = new StringBuffer();

		Date today = null;
		String sCurrentDate = null;
		today = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		sCurrentDate = dateFormat.format(today);
		String strContractSupplier = EnoviaResourceBundle.getProperty(context,"emxCPN.ContractSupplier.value");
		String strPlanningMessage = EnoviaResourceBundle.getProperty(context,"emxCPN.PlanningMessage.value");
		String strPreliminary = EnoviaResourceBundle.getProperty(context,"emxCPN.Preliminary.value");
		String sObsolete = EnoviaResourceBundle.getProperty(context,"emxCPN.PDFViewObsolete.value");
		String strObsolate=sObsolete+" "+strArchiveDate;
		String strPnGAuthorize = EnoviaResourceBundle.getProperty(context,"emxCPN.PnGAuthorize.value");
		String strPnGAuthorizePlann = EnoviaResourceBundle.getProperty(context,"emxCPN.PnGAuthorizePlann.value");
		String strPrereleaseState = EnoviaResourceBundle.getProperty(context,"emxCPN.ProductDataTemplate.type_ProductDataIPMPDF.PRERELEASED_STATE");
		String strPreReleaseStamp = EnoviaResourceBundle.getProperty(context,"emxCPN.PreReleaseStamp.value");
		String strPLMViewStamp = EnoviaResourceBundle.getProperty(context,"emxCPN.PLMViewStamp.value");
		String strStampContractSupplier = EnoviaResourceBundle.getProperty(context,"emxCPN.StampContractSupplier.value");
		String strProductDataFooterViews = EnoviaResourceBundle.getProperty(context,"emxCPN.FooterViews.ProductData");
		String strPnGAuthorizeUpdate = EnoviaResourceBundle.getProperty(context,"emxCPN.PnGAuthorizeUpdate.value");
		
		//Modified by DSM(Sogeti) - 2015x.5 PDF Views for requirement 18846 - Starts
		String strBusinessUseStamp = DomainConstants.EMPTY_STRING;
		if(pgV3Constants.RESTRICTED.equalsIgnoreCase(strObjectSecurityStatus) || pgV3Constants.INTERNAL_USE.equalsIgnoreCase(strObjectSecurityStatus)) {
			strBusinessUseStamp = pgV3Constants.BUSINESS_USE_HYPHEN;
		}
		if (PRODUCT_DATA_TYPES.contains(strType) && strProductDataFooterViews.contains(strPDFViewType)) {
			/* Modified by DSM (Req id #47506) - Start */
			if(pgV3Constants.TYPE_STRUCTURED_ATS.equals(strType) && pgV3Constants.PDFVIEW_ALLINFO.equalsIgnoreCase(strPDFViewType)){
				if(UIUtil.isNullOrEmpty(strBusinessUseStamp)){
				strBusinessUseStamp = pgV3Constants.BUSINESS_USE_HYPHEN;
				}
				sbFooterBuffer.append(strBusinessUseStamp).append(strPLMViewStamp).append(":").append(sCurrentDate);
			}
			else{
			/* Modified by DSM (Req id #47506) - Ends */	
			if(pgV3Constants.STATE_RELEASE.equalsIgnoreCase(strObjectLifeCycleState)){
				if(pgV3Constants.PDFVIEW_ALLINFO.equalsIgnoreCase(strPDFViewType)){
					sbFooterBuffer.append(strBusinessUseStamp).append(strPLMViewStamp).append(":").append(sCurrentDate);
				} else{
					sbFooterBuffer.append(strBusinessUseStamp).append(strPnGAuthorizePlann).append(" ").append(strObjectRevision).append(CONST_EFFECTIVE_DATE_WITHSPACE).append(strStampingDate).append(CONST_PRINTED_WITHSPACE2).append(sCurrentDate);
				}
			} else if (strPrereleaseState.contains(strObjectLifeCycleState)) {
				sbFooterBuffer.append(strBusinessUseStamp).append(strPreReleaseStamp).append(" ").append(strObjectRevision).append(CONST_EFFECTIVE_DATE_WITHSPACE).append(strStampingDate).append(CONST_PRINTED_WITHSPACE).append(sCurrentDate);
			} else if (pgV3Constants.STATE_OBSOLETE.equalsIgnoreCase(strObjectLifeCycleState)) {
				sbFooterBuffer.append(strBusinessUseStamp).append(strObsolate).append(" PRINTED ").append(sCurrentDate);
			} else {
				sbFooterBuffer.append(strBusinessUseStamp).append(strContractSupplier).append(sCurrentDate);
			}
		/* Modified by DSM (Req id #47506) - Start */
		}
		/* Modified by DSM (Req id #47506) - End */
		} else if(pgV3Constants.PDFVIEW_SUPPLIER.equalsIgnoreCase(strPDFViewType) || pgV3Constants.PDFVIEW_CONTRACTPACKAGING.equalsIgnoreCase(strPDFViewType)) {
			if(pgV3Constants.STATE_RELEASE.equalsIgnoreCase(strObjectLifeCycleState)){
				//Modified by DSM(Sogeti) - 2015x.5 PDF Views for Defect Id 17215 - Starts
				//if(UIUtil.isNotNullAndNotEmpty(strBusinessUseStamp))
				//Modified by DSM(Sogeti) - 2015x.5 PDF Views for Defect Id 17215 - Ends
					sbFooterBuffer.append(strBusinessUseStamp).append(strPnGAuthorizePlann).append(" ").append(strObjectRevision).append(CONST_EFFECTIVE_DATE_WITHSPACE).append(strStampingDate).append(CONST_PRINTED_WITHSPACE).append(sCurrentDate);
			} else if (pgV3Constants.STATE_OBSOLETE.equalsIgnoreCase(strObjectLifeCycleState)) {	
				sbFooterBuffer.append(strBusinessUseStamp).append(strObsolate).append(" PRINTED ").append(sCurrentDate);
			} else {
				sbFooterBuffer.append(strBusinessUseStamp).append(strStampContractSupplier).append(" ").append(sCurrentDate);
			}
		} else {
			//Added BY IRM Team in 2018x.5 for Defect 33007 Starts
			if(pgV3Constants.TYPE_PGPKGSTUDYPROTOCOL.equalsIgnoreCase(strType)){
				if (pgV3Constants.STATE_PRELIMINARY.equalsIgnoreCase(strObjectLifeCycleState)) {
					sbFooterBuffer.append(strBusinessUseStamp).append(strPreliminary).append(pgV3Constants.SYMBOL_SPACE).append(sCurrentDate);
				} else if (pgPDFViewConstants.STATE_REVIEW.equalsIgnoreCase(strObjectLifeCycleState)) {
					sbFooterBuffer.append(strBusinessUseStamp).append(pgPDFViewConstants.STR_PG_REVIEW).append(pgV3Constants.SYMBOL_SPACE).append(sCurrentDate);
				} else if (pgV3Constants.STATE_RELEASE.equalsIgnoreCase(strObjectLifeCycleState)) {
					sbFooterBuffer.append(strBusinessUseStamp).append(pgPDFViewConstants.STR_PG_RELEASE).append(pgV3Constants.SYMBOL_SPACE).append(sCurrentDate);
				}else if (pgV3Constants.STATE_OBSOLETE.equalsIgnoreCase(strObjectLifeCycleState)) {
					sbFooterBuffer.append(strBusinessUseStamp).append(pgPDFViewConstants.STR_PG_OBSOLETE).append(pgV3Constants.SYMBOL_SPACE).append(sCurrentDate);
				}
			} else {
			//Added BY IRM Team in 2018x.5 for Defect 33007 Ends
			if(pgV3Constants.STATE_PRELIMINARY.equalsIgnoreCase(strObjectLifeCycleState)) {
				sbFooterBuffer.append(strBusinessUseStamp).append(strPreliminary).append(" ").append(sCurrentDate);
			} else if(pgV3Constants.STATE_RELEASED.equalsIgnoreCase(strObjectLifeCycleState)) {
				sbFooterBuffer.append(strBusinessUseStamp).append(strPnGAuthorize).append(" ").append(strStampingDate).append(CONST_PRINTED_WITHSPACE2).append(sCurrentDate);
			} else if(pgV3Constants.STATE_OBSOLETE.equalsIgnoreCase(strObjectLifeCycleState)) {
				sbFooterBuffer.append(strBusinessUseStamp).append(strObsolate).append(CONST_PRINTED_WITHSPACE).append(sCurrentDate);
			} else if(pgV3Constants.RANGE_ATTRIBUTE_STATUS_PLANNING.equalsIgnoreCase(strObjectStatus)) {
				sbFooterBuffer.append(strBusinessUseStamp).append(strPlanningMessage).append(pgV3Constants.SYMBOL_NEXT_LINE).append(strPnGAuthorizeUpdate).append(" ").append(strStampingDate).append(CONST_PRINTED_WITHSPACE).append(sCurrentDate);
			} else if(pgV3Constants.PDFVIEW_WAREHOUSE.equalsIgnoreCase(strPDFViewType)
					|| pgV3Constants.PDFVIEW_CONSOLIDATEDPACKAGING.equalsIgnoreCase(strPDFViewType)
					) {
				//Modified by DSM(Sogeti) - 2015x.5 PDF Views for requirement 16850 16854 16855 16856 - Starts
				if(pgV3Constants.TYPE_PGFINISHEDPRODUCT.equalsIgnoreCase(strType) ||  
						pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY.equalsIgnoreCase(strType) || 
						pgV3Constants.TYPE_PGMASTERFINISHEDPRODUCT.equalsIgnoreCase(strType))
						{
						sbFooterBuffer.append(strBusinessUseStamp).append(strPnGAuthorizePlann).append(" ").append(strObjectRevision).append(CONST_EFFECTIVE_DATE_WITHSPACE).append(strStampingDate).append(CONST_PRINTED_WITHSPACE2).append(sCurrentDate);
						}
					else		
						{
						sbFooterBuffer.append(strBusinessUseStamp).append(strContractSupplier).append(sCurrentDate);
						}
				//Modified by DSM(Sogeti) - 2015x.5 PDF Views for requirement 16850 16854 16855 16856 - Ends
			} else {
				sbFooterBuffer.append(strBusinessUseStamp).append(strPLMViewStamp).append(":").append(sCurrentDate);
			}
		  }
		}
		//Modified by DSM(Sogeti) - 2015x.5 PDF Views for requirement 18846 - Ends
		return sbFooterBuffer.toString();
	}
	/**
	 * @Desc Method to stamp Header in the PDF file.
	 * @Author Added by DSM(Sogeti)-2015x.5 to use iText for PDF Views
	 * @param Context 
	 * @param doc - Document to be stamped
	 * @param iTotalPages - Total number of page in pdf file
	 * @param mapPDFTicketData - object and pdf header details
	 * @throws Exception
	 */
	public void addHeader(Context context, Document doc, int iTotalPages, Map mapPDFTicketData) throws Exception {
		Map mpHeaderData = addHeaderStamp(context, doc, iTotalPages, mapPDFTicketData);
		
		String strPGSecurityClassification = (String) mpHeaderData.get(CONST_PAGE1_HEADER1_COL1);
		String strCompanyAndPDFView = (String) mpHeaderData.get(CONST_PAGE1_HEADER1_CENTER);
		String strAcronymAndPGSAPTypePage1 = (String) mpHeaderData.get(CONST_PAGE1_HEADER2_CENTER);
		String strName = (String) mpHeaderData.get(CONST_PAGE1_HEADER3_COL1);
		String strRevision = (String) mpHeaderData.get(CONST_PAGE1_HEADER3_COL2);
		String strCurrent = (String) mpHeaderData.get(CONST_PAGE1_HEADER3_COL3);
		String strIsATS = (String) mpHeaderData.get(CONST_PAGE1_HEADER3_COL4);
		String strHasATS = (String) mpHeaderData.get(CONST_PAGE1_HEADER4_COL4);
		String strSAPDescriptionOrTitle = (String) mpHeaderData.get("page1.header4.col1");
		String strMaster = (String) mpHeaderData.get("page1.header4.col5");
		String strAcronymAndPGSAPTypePage2 = (String) mpHeaderData.get(CONST_PAGE2_HEADER2_CENTER);
		String strEffectivityDate = (String) mpHeaderData.get(CONST_PAGE2_HEADER3_COL4);
		String strSAPDescriptionOrTitlePage2 = (String) mpHeaderData.get(CONST_PAGE2_HEADER3_COL3);
		
		strPGSecurityClassification=validateNullString(strPGSecurityClassification);
		strCompanyAndPDFView=validateNullString(strCompanyAndPDFView);
		strAcronymAndPGSAPTypePage1=validateNullString(strAcronymAndPGSAPTypePage1);
		strName=validateNullString(strName);
		strRevision=validateNullString(strRevision);
		strCurrent=validateNullString(strCurrent);
		strIsATS=validateNullString(strIsATS);
		strHasATS=validateNullString(strHasATS);
		strSAPDescriptionOrTitle=validateNullString(strSAPDescriptionOrTitle);
		strMaster=validateNullString(strMaster);
		strAcronymAndPGSAPTypePage2=validateNullString(strAcronymAndPGSAPTypePage2);
		strEffectivityDate=validateNullString(strEffectivityDate);
		strSAPDescriptionOrTitlePage2=validateNullString(strSAPDescriptionOrTitlePage2);
		
		doc.setFontSize(10f);
		//Modified by DSM(Sogeti) - 2015x.5 PDF Views for requirement 18529,18527 - Start
		PdfFont FONTDSREGULAR = PdfFontFactory.createFont(fontName.toString(), PdfEncodings.IDENTITY_H, true);
		//Modified by DSM(Sogeti) - 2015x.5 PDF Views for requirement 18529,18527 - End
		for (int i = 1; i <= iTotalPages; i++) {
			
			if(i==1){
					//Adding header for page 1
					doc.showTextAligned(new Paragraph(strPGSecurityClassification).setFont(FONTDSREGULAR),
							35, 570, i, TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
					doc.showTextAligned(new Paragraph(strCompanyAndPDFView).setFont(FONTDSREGULAR),
							410, 570, i, TextAlignment.CENTER, VerticalAlignment.BOTTOM, 0);
					doc.showTextAligned(new Paragraph("Page "+String.valueOf(i) + " of " + String.valueOf(iTotalPages)).setFont(FONTDSREGULAR),
							790, 570, i, TextAlignment.RIGHT, VerticalAlignment.BOTTOM, 0);
					doc.setFontSize(8f);
					doc.setBold();
					doc.showTextAligned(new Paragraph(strAcronymAndPGSAPTypePage1).setFont(FONTDSREGULAR),
							410, 557, i, TextAlignment.CENTER, VerticalAlignment.BOTTOM, 0);
					doc.deleteOwnProperty(8);
					doc.setFontSize(10f);
					doc.showTextAligned(new Paragraph(strName).setFont(FONTDSREGULAR),
							35, 544, i, TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
					doc.showTextAligned(new Paragraph(strRevision).setFont(FONTDSREGULAR),
							195, 544, i, TextAlignment.JUSTIFIED, VerticalAlignment.BOTTOM, 0);
					//Added/Modified by DSM(Sogeti)-2018x.6 for PDF Views Requirement #40493 - Starts
					if(pgV3Constants.TYPE_PGAUTHORIZEDTEMPORARYSTANDARD.equals(mapPDFTicketData.get(DomainConstants.SELECT_TYPE))) {
						doc.showTextAligned(new Paragraph(strCurrent).setFont(FONTDSREGULAR),
								410, 544, i, TextAlignment.CENTER, VerticalAlignment.BOTTOM, 0);
						doc.showTextAligned(new Paragraph(strIsATS).setFont(FONTDSREGULAR),
								790, 544, i, TextAlignment.RIGHT, VerticalAlignment.BOTTOM, 0);
					//Added by DSM (Sogeti) for 22x.04 (Dec CW 2023) Defect 55289 - Start
					}else if(pgV3Constants.TYPE_STRUCTURED_ATS.equals(mapPDFTicketData.get(DomainConstants.SELECT_TYPE))) {
						doc.showTextAligned(new Paragraph(strCurrent).setFont(FONTDSREGULAR),
								410, 544, i, TextAlignment.CENTER, VerticalAlignment.BOTTOM, 0);
						doc.showTextAligned(new Paragraph(strIsATS).setFont(FONTDSREGULAR),
								790, 544, i, TextAlignment.RIGHT, VerticalAlignment.BOTTOM, 0);
					}
					//Added by DSM (Sogeti) for 22x.04 (Dec CW 2023) Defect 55289 - Ends
					else {
					doc.showTextAligned(new Paragraph(strCurrent).setFont(FONTDSREGULAR),
							355, 544, i, TextAlignment.JUSTIFIED, VerticalAlignment.BOTTOM, 0);
					doc.showTextAligned(new Paragraph(strIsATS).setFont(FONTDSREGULAR),
							575, 544, i, TextAlignment.JUSTIFIED, VerticalAlignment.BOTTOM, 0);
					}
					//Added by DSM(Sogeti)-2018x.6 for PDF Views Requirement #40493 - Ends
					doc.showTextAligned(new Paragraph(strSAPDescriptionOrTitle).setFont(FONTDSREGULAR),
							35, 532, i, TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
					doc.showTextAligned(new Paragraph(strHasATS).setFont(FONTDSREGULAR),
							575, 532, i, TextAlignment.JUSTIFIED, VerticalAlignment.BOTTOM, 0);
					doc.showTextAligned(new Paragraph(strMaster).setFont(FONTDSREGULAR),
							790, 532, i, TextAlignment.RIGHT, VerticalAlignment.BOTTOM, 0);
			} else {
					doc.showTextAligned(new Paragraph(strPGSecurityClassification).setFont(FONTDSREGULAR),
							35, 570, i, TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
					doc.showTextAligned(new Paragraph(strCompanyAndPDFView).setFont(FONTDSREGULAR),
							410, 570, i, TextAlignment.CENTER, VerticalAlignment.BOTTOM, 0);
					doc.showTextAligned(new Paragraph("Page "+String.valueOf(i) + " of " + String.valueOf(iTotalPages)).setFont(FONTDSREGULAR),
							790, 570, i, TextAlignment.RIGHT, VerticalAlignment.BOTTOM, 0);
					doc.setFontSize(8f);
					doc.setBold();
					doc.showTextAligned(new Paragraph(strAcronymAndPGSAPTypePage2).setFont(FONTDSREGULAR),
							410, 557, i, TextAlignment.CENTER, VerticalAlignment.BOTTOM, 0);
					doc.deleteOwnProperty(8);
					doc.setFontSize(10f);
					doc.showTextAligned(new Paragraph(strName).setFont(FONTDSREGULAR),
							35, 544, i, TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
					doc.showTextAligned(new Paragraph(strRevision).setFont(FONTDSREGULAR),
							165, 544, i, TextAlignment.JUSTIFIED, VerticalAlignment.BOTTOM, 0);
					doc.showTextAligned(new Paragraph(strSAPDescriptionOrTitlePage2).setFont(FONTDSREGULAR),
							245, 544, i, TextAlignment.JUSTIFIED, VerticalAlignment.BOTTOM, 0);
					doc.showTextAligned(new Paragraph(strEffectivityDate).setFont(FONTDSREGULAR),
							790, 544, i, TextAlignment.RIGHT, VerticalAlignment.BOTTOM, 0);
			}
			
			doc.showTextAligned(new Paragraph("_________________________________________________________________________________________________________________________________________").setFont(FONTDSREGULAR),
					35, 520, i, TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
		}
	}

	/**
	 * @Desc Method to update header stampings for PDF
	 * @Author Added by DSM(Sogeti)-2015x.5 to use iText for PDF Views
	 * @param Context 
	 * @param doc - Document to be stamped
	 * @param iTotalPages - Total number of page in pdf file
	 * @param mapPDFTicketData - object and pdf header details
	 * @param Map - Header Stamp
	 * @throws Exception
	 */
	public Map addHeaderStamp(Context context, Document doc, int iTotalPages, Map mapObjectHeaderData) throws Exception {
		System.out.println("addHeaderStamp method START===>");
        boolean isTextRightUpdate = true;
		Map hmResponse = new HashMap();
		String strObjectName = (String) mapObjectHeaderData.get(DomainConstants.SELECT_NAME);
		String strObjectOriginator = (String) mapObjectHeaderData.get(DomainConstants.SELECT_ORIGINATOR);
		String strObjectRevision = (String) mapObjectHeaderData.get(DomainConstants.SELECT_REVISION);
		String strObjectCurrentState = (String) mapObjectHeaderData.get(DomainConstants.SELECT_CURRENT);
		String strObjectType = (String) mapObjectHeaderData.get(DomainConstants.SELECT_TYPE);
		String strOriginatingSource = (String) mapObjectHeaderData.get(pgV3Constants.SELECT_ATTRIBUTE_PGORIGINATINGSOURCE);
		String strObjectPolicy = (String) mapObjectHeaderData.get(DomainConstants.SELECT_POLICY);
		//Modified for 2018x State is showing backend name for FPP START
		System.out.println("strObjectCurrentState======="+strObjectCurrentState);
		//Added for Upgrade 2018.0 start
		 String strObjectCurrentDisplay=EnoviaResourceBundle.getStateI18NString(context, strObjectPolicy, strObjectCurrentState,context.getLocale().getLanguage());
		System.out.println("strObjectCurrentDisplay=====1=="+strObjectCurrentDisplay);
		 System.out.println("strObjectPolicy=====1=="+strObjectPolicy);
		 if(BusinessUtil.isNotNullOrEmpty(strObjectCurrentDisplay))
		     strObjectCurrentState = strObjectCurrentDisplay;
		 //Added for Upgrade 2018.0 End
		//Modified for 2018x State is showing backend name for FPP END
		System.out.println("strObjectCurrentDisplay====2==="+strObjectCurrentDisplay);
		String pgIPClassification = (String) mapObjectHeaderData.get(pgV3Constants.SELECT_ATTRIBUTE_PGIPCLASSIFICATION);
		String strPDFViewType = (String) mapObjectHeaderData.get("View");
		String strObjectSecurityStatus = pgIPClassification.format(Locale.ENGLISH, "%s", pgIPClassification);
		String strHeader1 = (String) mapObjectHeaderData.get("header1");
		String strHeader2 = (String) mapObjectHeaderData.get("header2");
		String strHeader3 = (String) mapObjectHeaderData.get("header3");
		strHeader3 = validateNullString(strHeader3);
		String strHavingMaster=(String)mapObjectHeaderData.get("from["+pgV3Constants.RELATIONSHIP_PGMASTER+"]");
		String strMaster=(String)mapObjectHeaderData.get("from["+pgV3Constants.RELATIONSHIP_PGMASTER+"].to.name");
		String strProductDataHeaderViews = EnoviaResourceBundle.getProperty(context,"emxCPN.HeaderViews.ProductData");
		
		//Modified by DSM(Sogeti) - 2015x.5 fix for defect 15117 - Starts
		if(pgV3Constants.RESTRICTED.equalsIgnoreCase(strObjectSecurityStatus)) {
			strObjectSecurityStatus = EnoviaResourceBundle.getRangeI18NString(context, pgV3Constants.ATTRIBUTE_PGIPCLASSIFICATION, strObjectSecurityStatus, context.getSession().getLanguage());
		}
		//Modified by DSM(Sogeti) - 2015x.5 fix for defect 15117 - Ends
		
		String strArchiveDate = (String) mapObjectHeaderData.get(pgV3Constants.SELECT_ATTRIBUTE_PGARCHIVEDATE);
		if(validateString(strArchiveDate)){
			strArchiveDate = getFormattedDate(strArchiveDate);
		}
		String strEffectiveDate = (String) mapObjectHeaderData.get(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE);
		strEffectiveDate = getFormattedDate(strEffectiveDate);
		String strExpiryDate = (String) mapObjectHeaderData.get(pgV3Constants.SELECT_ATTRIBUTE_EXPIRATION_DATE);
		strExpiryDate = getFormattedDate(strExpiryDate);
		String strReleaseDate = (String) mapObjectHeaderData.get(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_DATE);
		strReleaseDate = getFormattedDate(strReleaseDate);

		String strObjectID = (String) mapObjectHeaderData.get(DomainConstants.SELECT_ID);
		Map hmRequestMap = new HashMap();
		Map hmArgMap = new HashMap();
		hmArgMap.put(CONST_OBJECTID, strObjectID);
		hmRequestMap.put("requestMap", hmArgMap);
		//Obtain HASATS details
		String strHasATS = (String) JPO.invoke(context, "pgDSOCPNProductData", null, "displayhasATSHeaderAttribute", JPO.packArgs(hmRequestMap),String.class);
		//Obtain ISATS details
		String strIsATS = (String) JPO.invoke(context, "pgIPMProductData", null, "displayIsATSHeaderAttribute", JPO.packArgs(hmRequestMap),String.class);

		String strSAPTitle = (String) mapObjectHeaderData.get(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
		String strSAPType = (String) mapObjectHeaderData.get(pgV3Constants.SELECT_ATTRIBUTE_PGSAPTYPE);
		strSAPType = getSAPTypeForPDF(strPDFViewType,strSAPType,strObjectType,PRODUCT_DATA_TYPES,NONSTRUCTURED_TYPES, strOriginatingSource);
		String strProductDataObjectName = padRight(strObjectName, 12);
		String strProductDataObjectRevision = padRight(strObjectRevision, 5);
		strObjectName = padRight(strObjectName, 25);
		strObjectOriginator = padRight(strObjectOriginator, 25);
		strObjectRevision = padRight(strObjectRevision, 25);
		strObjectCurrentState = padRight(strObjectCurrentState, 34);
		if("".equals(strEffectiveDate) || "".equals(strExpiryDate) || "".equals(strReleaseDate))
		{
			strEffectiveDate = padRight(strEffectiveDate, 19);
			strExpiryDate = padRight(strExpiryDate, 19) ;
			strReleaseDate = padRight(strReleaseDate, 18) + ".";
		}

		if (!(pgV3Constants.PDFVIEW_GENDOC.equalsIgnoreCase(strPDFViewType) && (NONSTRUCTURED_TYPES.contains(strObjectType) && !(strObjectType.equalsIgnoreCase(pgV3Constants.TYPE_PGRAWMATERIAL))))){
			if(!(pgV3Constants.PDFVIEW_GENDOC.equalsIgnoreCase(strPDFViewType))){
				hmResponse.put(CONST_PAGE1_HEADER1_CENTER,CONST_PG_COMPANY + strHeader1);
				if ((Boolean.parseBoolean(strHavingMaster)) && ((pgV3Constants.TYPE_PGFINISHEDPRODUCT.equals(strObjectType)) || (pgV3Constants.TYPE_PGPACKINGMATERIAL.equals(strObjectType)) || (pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY.equals(strObjectType)) || ((pgV3Constants.TYPE_PGRAWMATERIAL.equals(strObjectType)) && strOriginatingSource.contains(pgV3Constants.CSS_SOURCE)))){
					hmResponse.put(CONST_PAGE1_HEADER3_COL4,"Is ATS: "+strIsATS);
					hmResponse.put(CONST_PAGE1_HEADER4_COL4,CONST_HAS_ATS+strHasATS);
					hmResponse.put("page1.header4.col5","Master: "+strMaster);
					isTextRightUpdate = false;
				}
				if((strProductDataHeaderViews.contains(strPDFViewType)) && (PRODUCT_DATA_TYPES.contains(strObjectType)) && !(pgV3Constants.TYPE_PGMASTERRAWMATERIAL.equals(strObjectType)) && !(pgV3Constants.TYPE_PGRAWMATERIAL.equals(strObjectType)&& strOriginatingSource.contains(pgV3Constants.CSS_SOURCE))){
					hmResponse.put(CONST_PAGE1_HEADER1_COL1,strObjectSecurityStatus);
					hmResponse.put(CONST_PAGE1_HEADER3_COL1,CONST_NAME_COLON+strObjectName);
					hmResponse.put(CONST_PAGE1_HEADER3_COL2,CONST_REVISION_COLON+strObjectRevision);
					hmResponse.put(CONST_PAGE1_HEADER3_COL3,"State: "+strObjectCurrentState);
	      //Modified by DSM for Requirement 47656,47667 2022x.04 Dec CW 2023 - Start			
					if (!((pgV3Constants.POLICY_MANUFACTUREREQUIVALENT
							.equalsIgnoreCase(strObjectPolicy)) || (pgV3Constants.POLICY_SUPPLIEREQUIVALENT
							.equalsIgnoreCase(strObjectPolicy)) || (pgV3Constants.PQR_VIEW.equals(strPDFViewType))) && !strObjectType.equals(pgV3Constants.TYPE_STRUCTURED_ATS) || ("supplier".equals(strPDFViewType))) {
		  //Modified DSM for Requirement 47656,47667 2022x.04 Dec CW 2023 - End
						hmResponse.put(CONST_PAGE1_HEADER3_COL4,CONST_HAS_ATS+strHasATS);
					}
		   //Added DSM for Requirement 47656,47667 2022x.04 Dec CW 2023 - Start			
					else if(strObjectType.equals(pgV3Constants.TYPE_STRUCTURED_ATS)) {
						hmResponse.put(CONST_PAGE1_HEADER3_COL4,CONST_EXPIRATION_DATE_WITHSPACE+CONST_COLON+strExpiryDate);
					}
		     //Added DSM for Requirement 47656,47667 2022x.04 Dec CW 2023 - End	
					} else {
					hmResponse.put(CONST_PAGE1_HEADER1_COL1,strObjectSecurityStatus);
					hmResponse.put(CONST_PAGE1_HEADER3_COL1,CONST_NAME_COLON+strObjectName);
					hmResponse.put(CONST_PAGE1_HEADER3_COL2,CONST_REVISION_COLON+strObjectRevision);
					hmResponse.put(CONST_PAGE1_HEADER3_COL3,"State: "+strObjectCurrentState);
					//Added for defect 28457 - starts
					//if(!(pgV3Constants.POLICY_MANUFACTUREREQUIVALENT.equalsIgnoreCase(strObjectPolicy)) && !(pgV3Constants.POLICY_SUPPLIEREQUIVALENT.equalsIgnoreCase(strObjectPolicy)))
					//Modified by IRM-2018x.3 for PDF Views (Req Id #32753) : Starts -->
					//Modified by DSM for Requirement 47656,47667 2022x.04 Dec CW 2023 - Start
					if(!(pgV3Constants.PQR_VIEW.equals(strPDFViewType))&& !strObjectType.equals(pgV3Constants.TYPE_PGPKGSTUDYPROTOCOL)|| !strObjectType.equals(pgV3Constants.TYPE_STRUCTURED_ATS))
					//Modified by DSM for Requirement 47656,47667 2022x.04 Dec CW 2023 - End
						//Modified by IRM-2018x.3 for PDF Views (Req Id #32753) : ends -->
					{
					//Modified by DSM-2022x.6 for PDF Views (Defect:57677) - Start	
					hmResponse.put("page1.header4.col1",CONST_TITLE_COLON+strSAPTitle);
					//Modified by DSM-2022x.6 for PDF Views (Defect:57677) - End	
					if(isTextRightUpdate){
						hmResponse.put(CONST_PAGE1_HEADER3_COL4,"Is ATS: "+strIsATS);
						hmResponse.put(CONST_PAGE1_HEADER4_COL4,CONST_HAS_ATS+strHasATS);
					}
					//Added for defect 28457 - Ends
					}
				}
				//Modified If condition by DSM(Sogeti) - 2015x.5 PDF Views for requirement 16926
				if(((pgV3Constants.PDFVIEW_SUPPLIER.equalsIgnoreCase(strPDFViewType)) && (PRODUCT_DATA_TYPES.contains(strObjectType))) || (pgV3Constants.PQR_VIEW.equals(strPDFViewType) && (pgV3Constants.POLICY_MANUFACTUREREQUIVALENT.equalsIgnoreCase(strObjectPolicy) ||pgV3Constants.POLICY_SUPPLIEREQUIVALENT.equalsIgnoreCase(strObjectPolicy))) || ((pgV3Constants.PDFVIEW_CONTRACTPACKAGING.equalsIgnoreCase(strPDFViewType)) && !(pgV3Constants.TYPE_INTERMEDIATE_PRODUCT_PART.equals(strObjectType) || pgV3Constants.TYPE_PGFORMULATEDPRODUCT.equals(strObjectType)))) {
					String strHeader22 = strHeader2.substring(0,strHeader2.length()-2);
					hmResponse.put(CONST_PAGE1_HEADER2_CENTER,strHeader22);
				} else {
					//Modified by DSM(Sogeti) - 2015x.5 PDF Views for requirement 16850 16854 16855 16856 - Starts
					if(strSAPType.isEmpty() && strHeader3.isEmpty()) {
						strHeader2 = removeLastChar(strHeader2);
						hmResponse.put(CONST_PAGE1_HEADER2_CENTER,strHeader2 + " " + strSAPType + " " + strHeader3);
					} else {
						hmResponse.put(CONST_PAGE1_HEADER2_CENTER,strHeader2 + " " + strSAPType + " " + strHeader3);
					}
					//Modified by DSM(Sogeti) - 2015x.5 PDF Views for requirement 16850 16854 16855 16856 - Ends	
				}
			}
			if((pgV3Constants.PDFVIEW_GENDOC.equalsIgnoreCase(strPDFViewType)) && (PRODUCT_DATA_TYPES.contains(strObjectType)) && !(pgV3Constants.POLICY_MANUFACTUREREQUIVALENT.equalsIgnoreCase(strObjectPolicy)|| pgV3Constants.POLICY_SUPPLIEREQUIVALENT.equalsIgnoreCase(strObjectPolicy))){
				hmResponse.put(CONST_PAGE1_HEADER1_CENTER,CONST_PG_COMPANY + strHeader1);
				hmResponse.put(CONST_PAGE1_HEADER1_COL1,strObjectSecurityStatus);
				hmResponse.put(CONST_PAGE1_HEADER3_COL1,CONST_NAME_COLON+strObjectName);
				hmResponse.put(CONST_PAGE1_HEADER3_COL2,CONST_REVISION_COLON+strObjectRevision);
				hmResponse.put(CONST_PAGE1_HEADER2_CENTER,strHeader2 + " " + strSAPType + " " + strHeader3 );
				hmResponse.put("page2.header1.center",CONST_PG_COMPANY + strHeader1);
				//Added DSM for Requirement 47656,47667 2022x.04 Dec CW 2023 - Start
				hmResponse.put(CONST_PAGE1_HEADER3_COL3,pgPDFViewConstants.CONST_IS_ATS_SPECIFIC_TO_IMPACTED_PART_OR_SPEC_REV+CONST_COLON+validateNullString((String)mapObjectHeaderData.get(pgPDFViewConstants.SELECT_ATTRIBUTE_PGISATSSPECIFICTOIMPACTEDPARTORSPECREV)));
				hmResponse.put(CONST_PAGE1_HEADER3_COL4,"Expiration Date: "+strExpiryDate);
				//Added DSM for Requirement 47656,47667 2022x.04 Dec CW 2023 - End    							
				if(strObjectType.equals(pgV3Constants.TYPE_PGPROMOTIONALITEMPART))
				{
					hmResponse.put(CONST_PAGE2_HEADER1_COL1,strObjectSecurityStatus);
					hmResponse.put(CONST_PAGE2_HEADER3_COL1,CONST_NAME_COLON+strProductDataObjectName);
					hmResponse.put(CONST_PAGE2_HEADER3_COL2,CONST_REVISION_COLON+strProductDataObjectRevision);
					hmResponse.put(CONST_PAGE2_HEADER3_COL3,CONST_SAP_DESCRIPTION+strSAPTitle);
					hmResponse.put(CONST_PAGE2_HEADER3_COL4,"Effective Date: "+strEffectiveDate);
				}
				  //Added DSM for Requirement 47656,47667 2022x.04 Dec CW 2023 - Start
				else if(strObjectType.equals(pgV3Constants.TYPE_STRUCTURED_ATS)) {
				
					
					hmResponse.put(CONST_PAGE2_HEADER1_COL1,strObjectSecurityStatus);
					hmResponse.put(CONST_PAGE2_HEADER3_COL1,CONST_NAME_COLON+strObjectName);
					hmResponse.put(CONST_PAGE2_HEADER3_COL2,CONST_REVISION_COLON+strObjectRevision);
					hmResponse.put(CONST_PAGE2_HEADER3_COL3,"Title: "+strSAPTitle);
					hmResponse.put(CONST_PAGE2_HEADER3_COL4,"Effective Date: "+strEffectiveDate);									
				}
				  //Added DSM for Requirement 47656,47667 2022x.04 Dec CW 2023 - End
				else
					{
					hmResponse.put(CONST_PAGE2_HEADER1_COL1,strObjectSecurityStatus);
					hmResponse.put(CONST_PAGE2_HEADER3_COL1,CONST_NAME_COLON+strObjectName);
					hmResponse.put(CONST_PAGE2_HEADER3_COL2,CONST_REVISION_COLON+strObjectRevision);
					hmResponse.put(CONST_PAGE2_HEADER3_COL3,"Title: "+strSAPTitle);
				}

				String strHeader22 = strHeader2.substring(0,strHeader2.length()-2);
				hmResponse.put(CONST_PAGE2_HEADER2_CENTER,strHeader22);
			} else if(!(pgV3Constants.PDFVIEW_GENDOC.equalsIgnoreCase(strPDFViewType) && (pgV3Constants.POLICY_MANUFACTUREREQUIVALENT.equalsIgnoreCase(strObjectPolicy)|| pgV3Constants.POLICY_SUPPLIEREQUIVALENT.equalsIgnoreCase(strObjectPolicy)))){
					hmResponse.put("page2.header1.center",CONST_PG_COMPANY + strHeader1);
					hmResponse.put(CONST_PAGE2_HEADER1_COL1,strObjectSecurityStatus);
					hmResponse.put(CONST_PAGE2_HEADER3_COL1,CONST_NAME_COLON+strProductDataObjectName);
					hmResponse.put(CONST_PAGE2_HEADER3_COL2,CONST_REVISION_COLON+strProductDataObjectRevision);
					hmResponse.put(CONST_PAGE2_HEADER3_COL4,"Effective Date: "+strEffectiveDate);
				if(PRODUCT_DATA_TYPES.contains(strObjectType) && !(pgV3Constants.TYPE_PGMASTERRAWMATERIAL.equals(strObjectType))&& !(pgV3Constants.TYPE_PGRAWMATERIAL.equals(strObjectType) && strOriginatingSource.contains(pgV3Constants.CSS_SOURCE))) {
					hmResponse.put(CONST_PAGE2_HEADER3_COL3,"Title: "+strSAPTitle);
				}else
				{
					hmResponse.put(CONST_PAGE2_HEADER3_COL3,CONST_SAP_DESCRIPTION+strSAPTitle);
				}
				//Modified by DSM(Sogeti) - 2015x.5 fix for defect 15390 - Starts
				if(PRODUCT_DATA_TYPES.contains(strObjectType)){
					//Modified If condition by DSM(Sogeti) - 2015x.5 PDF Views for requirement 16926
					if(((strHeader2.contains("MRMS") || strHeader2.contains("MATL")) && !(pgV3Constants.PDFVIEW_SUPPLIER.equalsIgnoreCase(strPDFViewType))) || ((pgV3Constants.PDFVIEW_WAREHOUSE.equalsIgnoreCase(strPDFViewType) && (pgV3Constants.TYPE_FINISHEDPRODUCTPART.equals(strObjectType))))){
						hmResponse.put(CONST_PAGE2_HEADER2_CENTER,strHeader2 + " " + strSAPType);
					} else {
						String strHeader22 = strHeader2.substring(0,strHeader2.length()-2);
						hmResponse.put(CONST_PAGE2_HEADER2_CENTER,strHeader22);
					}
				//Modified by DSM(Sogeti) - 2015x.5 fix for defect 15390 - Ends
				} else {
					if(strSAPType.isEmpty() && strHeader3.isEmpty()) {
						//Modified by DSM(Sogeti) - 2015x.5 PDF Views for requirement 16850 16854 16855 16856 - Starts
						strHeader2 = removeLastChar(strHeader2);
						hmResponse.put(CONST_PAGE2_HEADER2_CENTER,strHeader2 + " " + strSAPType + " " + strHeader3 );
					} else {
						hmResponse.put(CONST_PAGE2_HEADER2_CENTER,strHeader2 + " " + strSAPType + " " + strHeader3 );
					}
				     //Modified by DSM(Sogeti) - 2015x.5 PDF Views for requirement 16850 16854 16855 16856 - Ends
				}
			} 
		}
		//Added by DSM(Sogeti)-2018x.5 for PDF Views Requirements 35306, 32960, 32964, 32965, 32966, 35338, 34926, 32866 and 32869 POA ATS Gendoc- Starts
		else if (pgV3Constants.PDFVIEW_GENDOC.equalsIgnoreCase(strPDFViewType) && (pgV3Constants.TYPE_PGARTWORK.equals(strObjectType) ||  pgV3Constants.TYPE_PGAUTHORIZEDTEMPORARYSTANDARD.equals(strObjectType))) {
			hmResponse.put(CONST_PAGE1_HEADER1_CENTER,CONST_PG_COMPANY + strHeader1);
			hmResponse.put(CONST_PAGE1_HEADER1_COL1,strObjectSecurityStatus);
			hmResponse.put(CONST_PAGE1_HEADER3_COL1,CONST_NAME_COLON+strObjectName);
			hmResponse.put(CONST_PAGE1_HEADER3_COL2,CONST_REVISION_COLON+strObjectRevision);
			//Added by DSM(Sogeti)-2018x.6 for PDF Views Requirement #40493 - Starts
			if(pgV3Constants.TYPE_PGAUTHORIZEDTEMPORARYSTANDARD.equals(strObjectType)) {
				hmResponse.put(CONST_PAGE1_HEADER3_COL3,pgPDFViewConstants.CONST_IS_ATS_SPECIFIC_TO_IMPACTED_PART_OR_SPEC_REV+CONST_COLON+validateNullString((String)mapObjectHeaderData.get(pgPDFViewConstants.SELECT_ATTRIBUTE_PGISATSSPECIFICTOIMPACTEDPARTORSPECREV)));
			}else {
			hmResponse.put(CONST_PAGE1_HEADER3_COL3,CONST_EFFECTIVE_DATE_WITHSPACE+CONST_COLON+strEffectiveDate);
			}
			//Added by DSM(Sogeti)-2018x.6 for PDF Views Requirement #40493 - Ends
			hmResponse.put(CONST_PAGE1_HEADER3_COL4,CONST_EXPIRATION_DATE_WITHSPACE+CONST_COLON+strExpiryDate);
			hmResponse.put(CONST_PAGE1_HEADER2_CENTER,strHeader2 + " " + strSAPType + " " + strHeader3 );
			hmResponse.put("page2.header1.center",CONST_PG_COMPANY + strHeader1);
		}
		//Added by DSM(Sogeti)-2018x.5 for PDF Views Requirements 35306, 32960, 32964, 32965, 32966, 35338, 34926, 32866 and 32869 POA ATS Gendoc- Ends
		return hmResponse;
	}
	
	/**
	 * @Desc Method to format date for PDF
	 * @param strDate - Date
	 * @return String - Formatted Date
	 */
	public static String getFormattedDate(String strDate) throws MatrixException
	{
		SimpleDateFormat formatter = null;
		Date tmpDate = null;
		String formatedDate = null;
		StringBuffer sbformatedDate = new StringBuffer();
		try {
			if (validateString(strDate) && (!strDate.contains("DENIED")))
			{
				formatter = new SimpleDateFormat(eMatrixDateFormat.getEMatrixDateFormat(),Locale.US);
				tmpDate = formatter.parse(strDate);
				formatter = new SimpleDateFormat(DATE_FORMAT);
				formatedDate = formatter.format(tmpDate);
				sbformatedDate.append(formatedDate);
			}
		} catch (ParseException ex) {
			ex.printStackTrace();
			throw new MatrixException(ex);
		}
		return sbformatedDate.toString();
	}
	
	
	/**
	 * @Desc This method returns valid SAP type for PDF Views
	 * @param strPDFViewType - Represent which PDF View.
	 * @param strSAPType - Actual SAP Type
	 * @param strObjectType - Actual object Type
	 * @param strObjectType - Structured type list
	 * @param strNonStructuredTypes - Non Structured type list
	 * @param strOriginatingSource - Object originating source
	 * @return SAP Type to be used for PDF Views
	 * @throws Exception
	 */
	private static String getSAPTypeForPDF (String strPDFViewType, String strSAPType, String strObjectType, String strProductDataTypes, String strNonStructuredTypes, String strOriginatingSource) throws Exception{
		if(strProductDataTypes.contains(strObjectType) || (strNonStructuredTypes.contains(strObjectType) &&  !(strObjectType.equalsIgnoreCase(pgV3Constants.TYPE_PGRAWMATERIAL)))){
			if(pgV3Constants.PDFVIEW_ALLINFO.equalsIgnoreCase(strPDFViewType) || pgV3Constants.PDFVIEW_GENDOC.equalsIgnoreCase(strPDFViewType) || (pgV3Constants.PDFVIEW_CONSOLIDATEDPACKAGING.equalsIgnoreCase(strPDFViewType) && pgV3Constants.TYPE_FINISHEDPRODUCTPART.equals(strObjectType)) || pgV3Constants.PQR_VIEW.equalsIgnoreCase(strPDFViewType)){
				strSAPType = validateString(strSAPType) ? "("+strSAPType+")" : "(N/A)";
			//Modified else If condition by DSM(Sogeti) - 2015x.5 PDF Views for requirement 16926
			} else if((pgV3Constants.PDFVIEW_COMBINEDWITHMASTER.equalsIgnoreCase(strPDFViewType)) && (((pgV3Constants.TYPE_PGRAWMATERIAL.equals(strObjectType)) && strOriginatingSource.contains(pgV3Constants.CSS_SOURCE))|| pgV3Constants.TYPE_PGMASTERRAWMATERIAL.equals(strObjectType))){
				strSAPType = DomainConstants.EMPTY_STRING;
			}
		}
		else {
			strSAPType = (pgV3Constants.PDFVIEW_ALLINFO.equalsIgnoreCase(strPDFViewType) && validateString(strSAPType)) ? "("+strSAPType+")" : DomainConstants.EMPTY_STRING;
		}
		return strSAPType;
	}
	
	/**
	 * @Desc Method used to validate invalid String
	 * @param strVerifyString
	 * @return boolean
	 */
	public static boolean validateString(String strVerifyString)
	{
		boolean isStringDesired=false;
		if(null != strVerifyString && !"".equals(strVerifyString) && !"null".equalsIgnoreCase(strVerifyString))
		{
			isStringDesired=true;
		}
		return isStringDesired;
	}
	
	/**
	 * @Desc Method used to pad string to right
	 * @param s - String to be padded
	 * @param n - Pad length
	 * @return String - Padded String
	 */
	public static String padRight(String s, int n)
	{
		return String.format("%1$-" + n + "s", s);
	}
		
	/**
	 * @Desc Method used to pad string to left
	 * @param s - String to be padded
	 * @param n - Pad length
	 * @return String - Padded String
	 */
	public static String padLeft(String s, int n)
	{
		return String.format("%1$#" + n + "s", s);
	}
	
	/**
	 * @Desc Method used to validate null string
	 * @param strValid - String to be validated
	 * @return String - Validated string
	 */
	public static String validateNullString(String strValid) {
		if (null==strValid)
			strValid = DomainConstants.EMPTY_STRING;
		return strValid;
	}
	
	/**
	 * @Desc Class to override the default split character behaviour.
	 * Break string if string is more than 25character long
	 * @Author Added by DSM(Sogeti)-2015x.5 to use iText for PDF Views
	**/
	class SplitChar extends DefaultSplitCharacters {
	    @Override
	    public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
	        boolean isBreak = false;
			String strText = text.toString();
			Pattern pLongChar = Pattern.compile("\\b\\w{25,256}\\b");
			Matcher matcher = pLongChar.matcher(strText);
			int start = 0;
			int end = 0;
			while (matcher.find()) {
				start = matcher.start();
				end = matcher.end();
				if(glyphPos>=(start+25)) {
					isBreak = true;
				}
			}
		return super.isSplitCharacter(text, glyphPos) || isBreak;
	    }
	}
	//Added by DSM(Sogeti) - 2015x.5 PDF Views for requirement 16850 16854 16855 16856 - Starts
	public String removeLastChar(String str) {
	//Modified by DSM(Sogeti) - 2018x.1 PDF Views for Defect #23438 - Starts
    if (str != null && str.length() > 0 && str.charAt(str.length() - 2) == '-') {		
        str = str.substring(0, str.length() - 3);
	//Modified by DSM(Sogeti) - 2018x.1 PDF Views for Defect #23438 - Ends		
    } 
    //Added for the Defect: 29135 - Starts
    else if (str != null && str.length() > 0 && str.charAt(str.length() - 1) == '-') {		
        str = str.substring(0, str.length() - 2);
    }
  //Added for the Defect: 29135 - Ends
    else {
		 return str;
	}
    return str;
	}
	//Added by DSM(Sogeti) - 2015x.5 PDF Views for requirement 16850 16854 16855 16856 - Ends
}
