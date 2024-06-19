/**
  JAVA class created for IRM 2018x.6
   Project Name: IRM(Sogeti)
   JAVA Name: ITextUtil
   Purpose: JAVA class created to implement the iText HTML to PDF Converter.
 **/
package com.pg.pdf.util;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.StringList;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.kernel.font.PdfFont;
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
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.eMatrixDateFormat;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.pdf.enumerations.PDFConstants;

import java.util.logging.Logger;

import com.pg.v3.custom.pgV3Constants;

public class ITextUtil extends PDFConstants {
	private static final Logger logger = Logger.getLogger(ITextUtil.class.getName());
	volatile String sPDFPAGEHEIGHT = DomainConstants.EMPTY_STRING;
	volatile String sPDFPAGEWIDTH = DomainConstants.EMPTY_STRING;
	volatile String sPDFRESOURCES = DomainConstants.EMPTY_STRING;
	volatile String sPDFLICENSE = DomainConstants.EMPTY_STRING;
	volatile String sPDFDATEFORMAT = DomainConstants.EMPTY_STRING;
	String sFONT = DomainConstants.EMPTY_STRING;
	StringBuilder sbFONTNAME3DS = null;

	String sPRODUCTDATATYPES = DomainConstants.EMPTY_STRING;
	String sNonStructured = DomainConstants.EMPTY_STRING;

	/**
	 * @Desc Method used to load required static data for generate pdf
	 * @param context
	 * @param args    - passing working directory
	 * @return void
	 */
	// Constructor
	@SuppressWarnings("rawtypes")
	public ITextUtil(Context context, String[] args) throws Exception {
		//Can't be parameterized as the Map is holding both Sting and Maps
		Map argMap = (HashMap) JPO.unpackArgs(args);
		String workDir = (String) argMap.get(CONST_WORK_DIR);
		sPRODUCTDATATYPES = EnoviaResourceBundle.getProperty(context,
				"emxCPN.ProductDataTemplate.type_ProductDataIPMPDF.TypeInclusionList");
		sNonStructured = EnoviaResourceBundle.getProperty(context,
				"emxCPN.ProductDataTemplate.type_ProductDataIPMPDF.NonStructuredTypeInclusionList");
		sPDFDATEFORMAT = EnoviaResourceBundle.getProperty(context, "eServiceSuiteCPN.defaultDateFormat");
		sPDFPAGEHEIGHT = EnoviaResourceBundle.getProperty(context, "emxCPN.RenderPDF.iText.PAGEHEIGHT");
		sPDFPAGEWIDTH = EnoviaResourceBundle.getProperty(context, "emxCPN.RenderPDF.iText.PAGEWIDTH");
		sPDFRESOURCES = EnoviaResourceBundle.getProperty(context, "emxCPN.RenderPDF.iTextResource");
		sPDFLICENSE = EnoviaResourceBundle.getProperty(context, "emxCPN.RenderPDF.iTextLicenseKey");
		LicenseKey.loadLicenseFile(sPDFLICENSE);
		sbFONTNAME3DS = new StringBuilder(workDir).append(CONSTANT_3DS_FONT);
	}
	/**
	 * @Desc Method to create and stamp the PDF file.
	 * @Author Added by IRM(Sogeti)-2018x.6 to use iText for PDF Views
	 * @param Context
	 * @param args
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String createPdf(Context context, String[] args) throws Exception {
		Map<?, ?> argMap = (HashMap<?, ?>) JPO.unpackArgs(args);
		String objectId = (String) argMap.get(DomainConstants.SELECT_ID);
		String htmlCodeData = (String) argMap.get(CONSTANT_HTML_DATA);
		String pdfFileName = (String) argMap.get(CONSTANT_PDF_FILE_NAME);
		Map<String, String> pdfPayLoad = (Map<String, String>) argMap.get(CONSTANT_PDF_PAY_LOAD);
		return (createPdf(context, htmlCodeData, objectId, pdfFileName, pdfPayLoad));
	}
	/**
	 * @Desc Method to create and stamp the PDF file.
	 * @Author Added by IRM(Sogeti)-2018x.6 to use iText for PDF Views
	 * @param Context
	 * @param strHtmlSource    - HTML file
	 * @param strObjectId      - Object Id
	 * @param strDestFileName  - PDF file name
	 * @param mapPDFTicketData - object and pdf header/footer details
	 * @return String - PDF Path
	 * @throws IOException 
	 * @throws Exception
	 */
	public String createPdf(Context context, String htmlCodeData, String objectId, String sPDFFileName, Map<String, String> pdfPayLoad)
	  {
logger.info("Enter  ITextUtil -method createPdf");
String sPDFFilePath = DomainConstants.EMPTY_STRING;
PdfDocument pdfDocuments = null;
boolean isFooterExist = false;

try {
	// Creating Temp directories
	String sSessionId = context.getSession().getSessionId();
	if (sSessionId.indexOf(CONSTANT_COLON) != -1) {
		sSessionId = sSessionId.substring(0, sSessionId.indexOf(CONSTANT_COLON));
	}
	String sTimestamp = Long.toString(System.currentTimeMillis());
	String sPDFTempFolderName = new StringBuffer(sSessionId).append(sTimestamp).append(objectId).toString();
	String sPDFTempDir = System.getProperty(CONSTANT_JAVA_IO_TMP_DIR);
	sPDFFilePath = new StringBuffer(sPDFTempDir).append(java.io.File.separator).append(sPDFTempFolderName)
			.append(java.io.File.separator).append(sPDFFileName).toString();
	File fPdf = new File(sPDFFilePath);
	fPdf.getParentFile().mkdirs();

	// Define page size of PDF
	float pageHeight = Float.parseFloat(sPDFPAGEHEIGHT);
	float pageWidth = Float.parseFloat(sPDFPAGEWIDTH);

	Rectangle pageSize = new Rectangle(pageHeight * 72, pageWidth * 72);
	PageSize newPage = new PageSize(pageSize);

	// Create Document
	PdfDocument pdfDocument = getPDFDocument(sPDFFilePath);
	pdfDocument.setDefaultPageSize(newPage);

	// PDF conversion
	ConverterProperties convertProperties = new ConverterProperties();
	FontProvider fontProvider = new FontProvider();
	fontProvider.addStandardPdfFonts();
	fontProvider.addSystemFonts();
	fontProvider.addFont(sFONT);

	fontProvider.addDirectory(sPDFRESOURCES);
	convertProperties.setFontProvider(fontProvider);
	convertProperties.setBaseUri(sPDFRESOURCES);

	// Convert HTML to PDF.
	InputStream inputStream = new ByteArrayInputStream(htmlCodeData.getBytes(StandardCharsets.UTF_8));
	Document doc = HtmlConverter.convertToDocument(inputStream, pdfDocument, convertProperties);
	splitCharacters(doc,pdfDocument);
	
	// Append TS Files
	String sPDFTempPath;
	isFooterExist = true;
	stampHeaderAndFooter(sPDFFilePath, sPDFTempFolderName, sPDFTempDir, sPDFFileName, pdfPayLoad, true,
			isFooterExist);
	stampHeaderAndFooter(sPDFFilePath, sPDFTempFolderName, sPDFTempDir, sPDFFileName, pdfPayLoad,
			false, true);
	List<InputStream> inputPdfList = new ArrayList<>();
	inputPdfList.add(new FileInputStream(sPDFFilePath));
	sPDFTempPath = new StringBuffer(sPDFTempDir).append(java.io.File.separator).append(sPDFTempFolderName)
			.append(java.io.File.separator).append(CONSTANT_PDF_MERGE_FILE_NAME).toString();
	pdfDocuments = getPDFDocument(sPDFTempPath);
	
	Iterator<InputStream> pdfIterator = inputPdfList.iterator();
	
	PdfDocument pdfDoc = null;
	StringList slPageNumbers = new StringList();
	while (pdfIterator.hasNext()) {
		getpdfIterator(pdfIterator,pdfDoc,pdfDocuments,slPageNumbers);
		
	}
	Document document = new Document(pdfDocuments);
	closeDocument(document);
		// Deleting the temp file
	File file = new File(sPDFFilePath);
	Files.deleteIfExists(Paths.get(file.toString()));
		// Renaming the file
		File oldfile = new File(sPDFTempPath);
		file = new File(sPDFFilePath);
		if(oldfile.renameTo(file)){
			logger.info("  ITextUtil File Successfully renamed");
		}
	int iPages = 0;
	int pageNumberSize = slPageNumbers.size();
	for (int i = 1; i <= pageNumberSize; i++) {
		iPages = iPages + Integer.valueOf(slPageNumbers.get(i - 1));
		if (i != 1) {
			stampHeaderAndFooter(sPDFFilePath, sPDFTempFolderName, sPDFTempDir, sPDFFileName,
					pdfPayLoad, false, false);
		}
	}
} catch (Exception e) {
	logger.info("Exception in  ITextUtil -method createPdf"+e.getMessage());
}

logger.info("Exit  ITextUtil -method createPdf");
return sPDFFilePath;
}

	private PdfDocument getPDFDocument(String sPDFFilePath) throws FileNotFoundException {
		PdfWriter pWriter = new PdfWriter(sPDFFilePath);
		return (new PdfDocument(pWriter));
		
	}
	private void getpdfIterator(Iterator<InputStream> pdfIterator,  PdfDocument pdfDoc,
			PdfDocument pdfDocuments, StringList slPageNumbers) throws IOException {
		try(InputStream pdf =pdfIterator.next(); ) {
			 
			pdfDoc = new PdfDocument(new PdfReader(pdf));
			pdfDoc.copyPagesTo(1, pdfDoc.getNumberOfPages(), pdfDocuments);
			slPageNumbers.add(Integer.toString(pdfDoc.getNumberOfPages()));
		} catch (Exception e) {
			logger.info("Exception  ITextUtil -method getpdfIterator"+e.getMessage());
		} finally {
			pdfDoc.close();
			
			
		}
		
	}
	private void splitCharacters(Document doc, PdfDocument pdfDocument) {
		try {
			doc.setSplitCharacters(new SplitChar());
		} catch (Exception e) {
			logger.info("Exception  ITextUtil -method splitCharacters"+e.getMessage());
		} finally {
			doc.close();
			pdfDocument.close();
		}
		
	}
	private void closeDocument(Document document) {
		try {
			logger.info("closing DOcument  ITextUtil -method createPdf");
		} catch (Exception e) {

			logger.info("Exception closing DOcument  ITextUtil -method createPdf"+e.getMessage());

		} finally {
			document.close();
		}

		
	}

	/**
	 * @Desc Class to override the default split character behaviour. Break string
	 *       if string is more than 25character long
	 * @Author Added by IRM(Sogeti)-2018x.6 to use iText for PDF Views
	 **/
	class SplitChar extends DefaultSplitCharacters {
		@Override
		public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
			boolean isBreak = false;
			String strText = text.toString();
			Pattern pLongChar = Pattern.compile("\\b\\w{25,256}\\b");
			Matcher matcher = pLongChar.matcher(strText);
			int start = 0;
			while (matcher.find()) {
				start = matcher.start();
				if (glyphPos >= (start + 25)) {
					isBreak = true;
				}
			}
			return super.isSplitCharacter(text, glyphPos) || isBreak;
		}
	}

	/**
	 * @Desc Method to stamp the PDF file.
	 * @Author Added by IRM(Sogeti)-2018x.6 to use iText for PDF Views
	 * @param Context
	 * @param strPDFpath         - PDF file path
	 * @param strTempFolderName  - Temporary Folder to hold stamp file
	 * @param strTempIODirectory - Directory Path
	 * @param strDestFileName    - PDF File Name
	 * @param mapPDFTicketData   - object and pdf header details
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws Exception
	 */
	public void stampHeaderAndFooter(String sPDFFilePath, String sPDFTempFolderName,
			String sPDFTempDir, String sPDFFileName, Map<String, String> pdfPayLoad, boolean bStampHeader, boolean bStampFooter) throws IOException {
		logger.info("Enter  ITextUtil -method stampHeaderAndFooter");
		String strDestPDFpath = new StringBuffer(sPDFTempDir).append(java.io.File.separator).append(sPDFTempFolderName)
				.append(java.io.File.separator).append(CONSTANT_TEMP_PREFIX + sPDFFileName).toString();
		try(PdfDocument pdfDocmt = new PdfDocument(new PdfReader(sPDFFilePath), new PdfWriter(strDestPDFpath))){
			Document doc = new Document(pdfDocmt);
			getHeaderFooter(doc,pdfDocmt,pdfPayLoad,bStampHeader,bStampFooter);
				}catch(Exception e){
			logger.info("Exception occurred in  ITextUtil -method stampHeaderAndFooter PdfReader"+ e.getMessage());
		}
		// Deleting the temp file
		
		File file = new File(sPDFFilePath);
		Files.deleteIfExists(Paths.get(file.toString()));
			File oldfile = new File(strDestPDFpath);
			file = new File(sPDFFilePath);
			if(oldfile.renameTo(file)){
				logger.info("ITextUtil File Successfully renamed after delete");
			}

		logger.info("Exit  ITextUtil -method stampHeaderAndFooter");
	}

	private void getHeaderFooter(Document doc, PdfDocument pdfDocmt,
			Map<String, String> pdfPayLoad, boolean bStampHeader, boolean bStampFooter) {
		try {

			int iTotalPages = pdfDocmt.getNumberOfPages();
			if (bStampFooter) {
				addFooter(doc, iTotalPages, pdfPayLoad, pdfDocmt);
			}
			if (bStampHeader) {
				addHeader(doc, iTotalPages, pdfPayLoad);
			}

		} catch (Exception e) {
			logger.info("Exception occurred in  ITextUtil -method stampHeaderAndFooter"+ e.getMessage());
			
		} finally {
			
			doc.close();
		}

		
	}
	/**
	 * @Desc Method to stamp Footer in the PDF file.
	 * @Author Added by IRM(Sogeti)-2018x.6 to use iText for PDF Views
	 * @param Context
	 * @param doc         - Document to be stamped
	 * @param iTotalPages - Total number of page in pdf file
	 * @param pdfPayLoad  - object and pdf header details
	 * @throws MatrixException 
	 * @throws IOException 
	 * @throws Exception
	 */
	public void addFooter(Document doc, int iTotalPages, Map<String, String> pdfPayLoad, PdfDocument pdfDoc) throws IOException {
		logger.info("Enter  ITextUtil -method addFooter");
		doc.setFontSize(12f);
		String state = getValueFromMap(pdfPayLoad, DomainConstants.SELECT_CURRENT);
		String sClassification = getValueFromMap(pdfPayLoad, pgV3Constants.SELECT_ATTRIBUTE_PGIPCLASSIFICATION);
		String sSecurityStatus = String.format(Locale.ENGLISH, "%s", sClassification);
		String sFooterStamp = getFooterStamp(state, sSecurityStatus);
		PdfFont sFont3ds = PdfFontFactory.createFont(sbFONTNAME3DS.toString(), PdfEncodings.IDENTITY_H, true);
		String sFooter;
		Rectangle pageSize = null;
		float fPageMargin;
		for (int i = 1; i <= iTotalPages; i++) {
			sFooter = new StringBuffer(sFooterStamp).append(CONSTANT_PAGE).append(String.valueOf(i))
					.append(CONSTANT_OF).append(String.valueOf(iTotalPages)).toString();
			pageSize = pdfDoc.getPage(i).getPageSize();
			fPageMargin = (pageSize.getRight() + doc.getRightMargin() - (pageSize.getLeft() + doc.getLeftMargin()))
					/ 2;
			doc.showTextAligned(new Paragraph(sFooter).setFont(sFont3ds), fPageMargin, (doc.getLeftMargin() - 10), i,
					TextAlignment.CENTER, VerticalAlignment.TOP, 0);
		}
		logger.info("Exit  ITextUtil -method addFooter");
	}

	/**
	 * @Desc Method to update Footer Stampings
	 * @Author Added by IRM(Sogeti)-2018x.6 to use iText for PDF Views
	 * @param Context
	 * @param type             - Object Type
	 * @param state            - Object Current
	 * @param sStatus          - Status attribute of object
	 * @param sViewType        - PDF View
	 * @param sEffectivityDate - Stamp Date
	 * @param sArchiveDate     - Archieve Date
	 * @param sSecurityStatus  - Object Revision
	 * @return String - Footer Stamp
	 * @throws Exception
	 */
	public  String getFooterStamp(String state, String sSecurityStatus) {
		logger.info("Enter  ITextUtil -method getFooterStamp");
		StringBuilder footer = new StringBuilder();
		Date today = new Date();
		String date = new SimpleDateFormat(sPDFDATEFORMAT).format(today);
		String sBusinessUseStamp = DomainConstants.EMPTY_STRING;
		if (pgV3Constants.RESTRICTED.equalsIgnoreCase(sSecurityStatus)
				|| pgV3Constants.INTERNAL_USE.equalsIgnoreCase(sSecurityStatus)) {
			sBusinessUseStamp = pgV3Constants.BUSINESS_USE_HYPHEN;
		}
		footer.append(sBusinessUseStamp).append(CONSTANT_BUSINESS_USE_CASE_VIEW).append(SYMBOL_HYPHEN)
		.append(CONST_PG_MARKINGS).append(SYMBOL_SPACE).append(state).append(SYMBOL_SPACE).append(CONST_PRINTED).append(SYMBOL_SPACE).append(date);
		logger.info("Exit  ITextUtil -method getFooterStamp");
		return footer.toString();
	}

	/**
	 * @Desc Method to format date for PDF
	 * @param strDate - Date
	 * @return String - Formatted Date
	 */
	public  String getFormattedDate(String strDate){
		logger.info("Enter  ITextUtil -method getFormattedDate");
		SimpleDateFormat formatter = null;
		Date tmpDate = null;
		String formatedDate = null;
		StringBuilder sbformatedDate = new StringBuilder();
		try {
			if (isValidateString(strDate) && (!strDate.contains(CONSTANT_DENIED))) {
				formatter = new SimpleDateFormat(eMatrixDateFormat.getEMatrixDateFormat(), Locale.US);
				tmpDate = formatter.parse(strDate);
				formatter = new SimpleDateFormat(sPDFDATEFORMAT);
				formatedDate = formatter.format(tmpDate);
				sbformatedDate.append(formatedDate);
			}
		} catch (ParseException ex) {
			logger.info("Exception occurred in ITextUtil -method getFormattedDate"+ ex.getMessage());
		}
		logger.info("Exit  ITextUtil -method getFormattedDate");
		return sbformatedDate.toString();
	}

	/**
	 * @Desc Method used to validate invalid String
	 * @param strVerifyString
	 * @return boolean
	 */
	public static boolean isValidateString(String strVerifyString) {
		boolean isStringDesired = false;
		if (UIUtil.isNotNullAndNotEmpty(strVerifyString)) {
			isStringDesired = true;
		}
		return isStringDesired;
	}

	/**
	 * @Desc Method used to validate null string
	 * @param strValid - String to be validated
	 * @return String - Validated string
	 */
	public static String validateNullString(String strValid) {
		if (null == strValid)
			strValid = DomainConstants.EMPTY_STRING;
		return strValid;
	}


	/**
	 * @Desc Method used to get value based on key
	 * @param map
	 * @param key
	 * @return -value of the key
	 * @throws Exception
	 */
	public static String getValueFromMap(Map<String, String> map, String key) {
		String val =  map.get(key);
		return val == null ? EMPTY_STRING : val.trim();
	}
	/**
	 * @Desc Method to stamp Header in the PDF file.
	 * @Author Added by IRM(Sogeti)-2018x.6 to use iText for PDF Views
	 * @param Context 
	 * @param doc - Document to be stamped
	 * @param iTotalPages - Total number of page in pdf file
	 * @param pdfPayLoad - object and pdf header details
	 * @throws IOException 
	 * @throws Exception
	 */
	public void addHeader(Document doc, int iTotalPages, Map<String, String> pdfPayLoad) throws IOException{
		logger.info("Enter  ITextUtil -method addHeader");
		Map<String, String> mpHeaderData = addHeaderStamp(doc, iTotalPages, pdfPayLoad);
		String securityClassification = getValueFromMap(mpHeaderData, PDF_HEADER_CONST_SECURITY_CLASSIFICATION);
		String companyAndViewType = getValueFromMap(mpHeaderData, PDF_HEADER_CONST_COMPANY_AND_VIEW);
		String strAcronymAndPGSAPTypePage1 = getValueFromMap(mpHeaderData, PDF_HEADER_CONST_ATTRIBUTE_GPS_DISPLAY_NAME);
		String busName = getValueFromMap(mpHeaderData, PDF_HEADER_CONST_NAME_COLON);
		String busRevision = getValueFromMap(mpHeaderData, PDF_HEADER_CONST_REVISION_COLON);
		String busCurrent = getValueFromMap(mpHeaderData, PDF_HEADER_CONST_STATE_COLON);
		String sATS = getValueFromMap(mpHeaderData, CONST_PAGE1_HEADER3_COL4);
		String sHasATS = getValueFromMap(mpHeaderData, CONST_PAGE1_HEADER4_COL4);
		String strSAPDescriptionOrTitle = getValueFromMap(mpHeaderData, "page1.header4.col1");
		String strAcronymAndPGSAPTypePage2 = getValueFromMap(mpHeaderData, PDF_HEADER_CONST_ATTRIBUTE_GPS_DISPLAY_NAME);
		String strEffectivityDate =getValueFromMap(mpHeaderData, CONST_PAGE2_HEADER3_COL4);
		doc.setFontSize(10f);
		PdfFont font3DS = PdfFontFactory.createFont(sbFONTNAME3DS.toString(), PdfEncodings.IDENTITY_H, true);
		for (int i = 1; i <= iTotalPages; i++) {
			if (i == 1) {
				// Adding header for page 1
				// security classification header.
				doc.showTextAligned(new Paragraph(securityClassification).setFont(font3DS), 35, 570, i,
						TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
				// set company + view header
				doc.showTextAligned(new Paragraph(companyAndViewType).setFont(font3DS), 410, 570, i,
						TextAlignment.CENTER, VerticalAlignment.BOTTOM, 0);
				// set page number
				doc.showTextAligned(new Paragraph("Page " + i + " of " + iTotalPages)
				.setFont(font3DS), 790, 570, i, TextAlignment.RIGHT, VerticalAlignment.BOTTOM, 0);
				doc.setFontSize(8f);
				doc.setBold();
				// set type (if sap)
				doc.showTextAligned(new Paragraph(strAcronymAndPGSAPTypePage1).setFont(font3DS), 410, 557, i,
						TextAlignment.CENTER, VerticalAlignment.BOTTOM, 0);
				doc.deleteOwnProperty(8);
				doc.setFontSize(10f);
				// set type, name, revision
				doc.showTextAligned(new Paragraph(busName).setFont(font3DS), 35, 544, i, TextAlignment.LEFT,
						VerticalAlignment.BOTTOM, 0);
				doc.showTextAligned(new Paragraph(busRevision).setFont(font3DS), 195, 544, i, TextAlignment.JUSTIFIED,
						VerticalAlignment.BOTTOM, 0);
				doc.showTextAligned(new Paragraph(busCurrent).setFont(font3DS), 355, 544, i, TextAlignment.JUSTIFIED,
						VerticalAlignment.BOTTOM, 0);
				// set ATS
				doc.showTextAligned(new Paragraph(sATS).setFont(font3DS), 575, 544, i, TextAlignment.JUSTIFIED,
						VerticalAlignment.BOTTOM, 0);
				// set description
				doc.showTextAligned(new Paragraph(strSAPDescriptionOrTitle).setFont(font3DS), 35, 532, i,
						TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
				doc.showTextAligned(new Paragraph(sHasATS).setFont(font3DS), 575, 532, i, TextAlignment.JUSTIFIED,
						VerticalAlignment.BOTTOM, 0);
			} else {
				doc.showTextAligned(new Paragraph(securityClassification).setFont(font3DS), 35, 570, i,
						TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
				doc.showTextAligned(new Paragraph(companyAndViewType).setFont(font3DS), 410, 570, i,
						TextAlignment.CENTER, VerticalAlignment.BOTTOM, 0);
				doc.showTextAligned(new Paragraph("Page " + i + " of " + iTotalPages)
				.setFont(font3DS), 790, 570, i, TextAlignment.RIGHT, VerticalAlignment.BOTTOM, 0);
				doc.setFontSize(8f);
				doc.setBold();
				doc.showTextAligned(new Paragraph(strAcronymAndPGSAPTypePage2).setFont(font3DS), 410, 557, i,
						TextAlignment.CENTER, VerticalAlignment.BOTTOM, 0);
				doc.deleteOwnProperty(8);
				doc.setFontSize(10f);
				// set type, name, revision
				doc.showTextAligned(new Paragraph(busName).setFont(font3DS), 35, 544, i, TextAlignment.LEFT,
						VerticalAlignment.BOTTOM, 0);
				doc.showTextAligned(new Paragraph(busRevision).setFont(font3DS), 195, 544, i, TextAlignment.JUSTIFIED,
						VerticalAlignment.BOTTOM, 0);
				doc.showTextAligned(new Paragraph(busCurrent).setFont(font3DS), 355, 544, i, TextAlignment.JUSTIFIED,
						VerticalAlignment.BOTTOM, 0);
				doc.showTextAligned(new Paragraph(strEffectivityDate).setFont(font3DS), 790, 544, i,
						TextAlignment.RIGHT, VerticalAlignment.BOTTOM, 0);
			}
			doc.showTextAligned(new Paragraph(
					"_________________________________________________________________________________________________________________________________________")
			.setFont(font3DS),
			35, 520, i, TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
		}
		logger.info("Exit  ITextUtil -method addHeader");
	}

	/**
	 * @Desc Method to update header stampings for PDF
	 * @Author Added by IRM (Sogeti)-2018x.6 to use iText for PDF Views
	 * @param Context
	 * @param doc              - Document to be stamped
	 * @param iTotalPages      - Total number of page in pdf file
	 * @param pdfPayLoad              - Header Stamp
	 * @throws Exception
	 */

	public Map<String, String> addHeaderStamp( Document doc, int iTotalPages, Map<String, String> pdfPayLoad) {
		logger.info("Enter  ITextUtil -method addHeaderStamp");
		Map<String, String> hmResponse = new HashMap<>();
		String name = getValueFromMap(pdfPayLoad, DomainConstants.SELECT_NAME);
		String revision = getValueFromMap(pdfPayLoad, DomainConstants.SELECT_REVISION);
		String current = getValueFromMap(pdfPayLoad, DomainConstants.SELECT_CURRENT);
		String sIPClassification = getValueFromMap(pdfPayLoad,CONSTANT_CLASSIFICATION);
		String viewKind =  pdfPayLoad.get(CONSTANT_VIEW_KIND);
		String categoryRequestType = pdfPayLoad.get(CONSTANT_TYPE_KIND);
		if (UIUtil.isNotNullAndNotEmpty(sIPClassification)
				&& sIPClassification.equalsIgnoreCase(pgV3Constants.RESTRICTED)) {
			sIPClassification = pgV3Constants.BUSINESS_USE;
		}
		hmResponse.put(PDF_HEADER_CONST_COMPANY_AND_VIEW,
				CONSTANT_COMPANY_DISPLAY_NAME + SYMBOL_HYPHEN + getViewDisplayName(viewKind));
		hmResponse.put(PDF_HEADER_CONST_SECURITY_CLASSIFICATION, sIPClassification);
		hmResponse.put(PDF_HEADER_CONST_NAME_COLON, PDF_HEADER_NAME_COLON + name);
		hmResponse.put(PDF_HEADER_CONST_REVISION_COLON, PDF_HEADER_REVISION_COLON + revision);
		hmResponse.put(PDF_HEADER_CONST_STATE_COLON, PDF_HEADER_STATE_COLON + current);
		hmResponse.put(PDF_HEADER_CONST_HAS_ATS_COLON, PDF_HEADER_HAS_ATS_COLON + CONSTANT_NO);
		// display gps assessment category request
		hmResponse.put(PDF_HEADER_CONST_ATTRIBUTE_GPS_DISPLAY_NAME, categoryRequestType);
		logger.info("Exit  ITextUtil -method addHeaderStamp");
		return hmResponse;
	}

	/**
	 * @Desc Method to return display value based on string
	 * @Author Added by IRM (Sogeti)-2018x.6 to use iText for PDF Views
	 * @param view
	 * @return
	 * @throws Exception
	 */
	public static String getViewDisplayName(String view) {
		switch (view) {
		case CONSTANT_ALL_INFO_VIEW:
			return ALL_INFO_VIEW_DISPLAY_NAME;
		case CONSTANT_PLACEMENTAGENCY_VIEW:
			return PLACEMENT_AGENCY_VIEW_DISPLAY_NAME;
		default:
			return EMPTY_STRING;
		}
	}

}
