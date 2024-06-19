/**
  JAVA class created for IRM 2018x.3
   Project Name: IRM(Sogeti)
   JAVA Name: ITextUtil
   Purpose: JAVA class created to implement the iText HTML to PDF Converter.
 **/
package com.pg.irm.pdf.util;

import java.io.ByteArrayInputStream;
import java.io.File;
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
import com.pg.v3.custom.pgV3Constants;
import com.pg.irm.pdf.PDFConstants;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class ITextUtil implements PDFConstants {

	public static volatile String PDF_PAGE_HEIGHT = null;
	public static volatile String PDF_PAGE_WIDTH = null;
	public static volatile String PDF_RESOURCES = null;
	public static volatile String PDF_LICENSE = null;
	public static volatile String PDF_DATE_FORMAT = null;
	public static String FONT = DomainConstants.EMPTY_STRING;
	public static StringBuilder FONT_NAME_3DS = null;

	public static volatile String PRODUCT_DATA_TYPES = null;
	public static volatile String NONSTRUCTURED_TYPES = null;

	/**
	 * @Desc Method used to load requrired static data for generate pdf
	 * @param context
	 * @param args    - passing working directory
	 * @return void
	 */
	// Constructor
	public ITextUtil(Context context, String[] args) throws Exception {
		Map argMap = (HashMap) JPO.unpackArgs(args);
		String workDir = (String) argMap.get(CONST_WORK_DIR);
		PRODUCT_DATA_TYPES = EnoviaResourceBundle.getProperty(context,
				"emxCPN.ProductDataTemplate.type_ProductDataIPMPDF.TypeInclusionList");
		NONSTRUCTURED_TYPES = EnoviaResourceBundle.getProperty(context,
				"emxCPN.ProductDataTemplate.type_ProductDataIPMPDF.NonStructuredTypeInclusionList");
		PDF_DATE_FORMAT = EnoviaResourceBundle.getProperty(context, "eServiceSuiteCPN.defaultDateFormat");
		PDF_PAGE_HEIGHT = EnoviaResourceBundle.getProperty(context, "emxCPN.RenderPDF.iText.PAGEHEIGHT");
		PDF_PAGE_WIDTH = EnoviaResourceBundle.getProperty(context, "emxCPN.RenderPDF.iText.PAGEWIDTH");
		PDF_RESOURCES = EnoviaResourceBundle.getProperty(context, "emxCPN.RenderPDF.iTextResource");

		PDF_LICENSE = EnoviaResourceBundle.getProperty(context, "emxCPN.RenderPDF.iTextLicenseKey");
		LicenseKey.loadLicenseFile(PDF_LICENSE);
		
		FONT_NAME_3DS = new StringBuilder(workDir).append(CONSTANT_3DS_FONT);
	}
	/**
	 * @Desc Method to create and stamp the PDF file.
	 * @Author Added by IRM(Sogeti)-2018x.3 to use iText for PDF Views
	 * @param Context
	 * @param args
	 * @throws Exception
	 */
	public String createPdf(Context context, String args[], boolean bln) throws Exception {
		Map argMap = (HashMap) JPO.unpackArgs(args);
		String objectId = (String) argMap.get(DomainConstants.SELECT_ID);
		String htmlCodeData = (String) argMap.get(CONSTANT_HTML_DATA);
		String pdfFileName = (String) argMap.get(CONSTANT_PDF_FILE_NAME);
		Map pdfPayLoad = (Map) argMap.get(CONSTANT_PDF_PAY_LOAD);
		return (createPdf(context, htmlCodeData, objectId, pdfFileName, pdfPayLoad));
	}
	/**
	 * @Desc Method to create and stamp the PDF file.
	 * @Author Added by IRM(Sogeti)-2018x.3 to use iText for PDF Views
	 * @param Context
	 * @param strHtmlSource    - HTML file
	 * @param strObjectId      - Object Id
	 * @param strDestFileName  - PDF file name
	 * @param mapPDFTicketData - object and pdf header/footer details
	 * @return String - PDF Path
	 * @throws Exception
	 */
	public String createPdf(Context context, String htmlCodeData, String objectId, String sPDFFileName, Map pdfPayLoad)
			throws Exception {

		String sPDFFilePath = null;
		PdfDocument pdfDocuments = null;
		boolean isFooterExist = false;

		try {
			// Creating Temp directories
			String sSessionId = context.getSession().getSessionId();
			if (sSessionId.indexOf(":") != -1) {
				sSessionId = sSessionId.substring(0, sSessionId.indexOf(":"));
			}
			String sTimestamp = Long.toString(System.currentTimeMillis());
			String sPDFTempFolderName = new StringBuffer(sSessionId).append(sTimestamp).append(objectId).toString();
			String sPDFTempDir = System.getProperty(CONSTANT_JAVA_IO_TMP_DIR);
			sPDFFilePath = new StringBuffer(sPDFTempDir).append(java.io.File.separator).append(sPDFTempFolderName)
					.append(java.io.File.separator).append(sPDFFileName).toString();

			File fPdf = new File(sPDFFilePath);
			fPdf.getParentFile().mkdirs();

			// Define page size of PDF
			float pageHeight = Float.parseFloat(PDF_PAGE_HEIGHT);
			float pageWidth = Float.parseFloat(PDF_PAGE_WIDTH);

			Rectangle pageSize = new Rectangle(pageHeight * 72, pageWidth * 72);
			PageSize newPage = new PageSize(pageSize);

			// Create Document
			PdfWriter pWriter = new PdfWriter(sPDFFilePath);
			PdfDocument pdfDocument = new PdfDocument(pWriter);
			int iTotalPages = pdfDocument.getNumberOfPages();
			pdfDocument.setDefaultPageSize(newPage);

			// PDF conversion
			ConverterProperties convertProperties = new ConverterProperties();
			FontProvider fontProvider = new FontProvider();
			fontProvider.addStandardPdfFonts();
			fontProvider.addSystemFonts();
			fontProvider.addFont(FONT);

			fontProvider.addDirectory(PDF_RESOURCES);
			convertProperties.setFontProvider(fontProvider);
			convertProperties.setBaseUri(PDF_RESOURCES);

			// Convert HTML to PDF.
			InputStream inputStream = new ByteArrayInputStream(htmlCodeData.getBytes(CONSTANT_UTF_8));
			//InputStream htmlFile = new FileInputStream(new File(htmlFilePath));
			Document doc = HtmlConverter.convertToDocument(inputStream, pdfDocument, convertProperties);

			try {
				doc.setSplitCharacters(new SplitChar());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				doc.close();
				pdfDocument.close();
			}
			// Append TS Files
			String sPDFTempPath = null;
			isFooterExist = true;

			stampHeaderAndFooter(context, sPDFFilePath, sPDFTempFolderName, sPDFTempDir, sPDFFileName, pdfPayLoad, true,
					isFooterExist, 1, iTotalPages);

			stampHeaderAndFooter(context, sPDFFilePath, sPDFTempFolderName, sPDFTempDir, sPDFFileName, pdfPayLoad,
					false, true, 1, iTotalPages);

			List<InputStream> inputPdfList = new ArrayList<InputStream>();
			inputPdfList.add(new FileInputStream(sPDFFilePath));
			sPDFTempPath = new StringBuffer(sPDFTempDir).append(java.io.File.separator).append(sPDFTempFolderName)
					.append(java.io.File.separator).append(CONSTANT_PDF_MERGE_FILE_NAME).toString();
			pdfDocuments = new PdfDocument(new PdfWriter(sPDFTempPath));
			Iterator<InputStream> pdfIterator = inputPdfList.iterator();
			int count = 0;
			InputStream pdf = null;
			PdfDocument pdfDoc = null;
			StringList slPageNumbers = new StringList();
			while (pdfIterator.hasNext()) {
				try {
					pdf = pdfIterator.next();
					pdfDoc = new PdfDocument(new PdfReader(pdf));
					pdfDoc.copyPagesTo(1, pdfDoc.getNumberOfPages(), pdfDocuments);
					count++;
					slPageNumbers.add(Integer.toString(pdfDoc.getNumberOfPages()));
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					pdfDoc.close();
					pdf.close();
				}
			}
			int iTotalPages_ = 0;
			Document document = new Document(pdfDocuments);
			try {
				iTotalPages_ = pdfDocuments.getNumberOfPages();
			} catch (Exception e) {

				e.printStackTrace();

			} finally {
				document.close();
			}
			// Deleting the temp file
			File file = new File(sPDFFilePath);
			if (file.delete()) {
				// Renaming the file
				File oldfile = new File(sPDFTempPath);
				file = new File(sPDFFilePath);
				oldfile.renameTo(file);
			}
			int iPages = 0;
			int temp = 0;
			int pageNumberSize = slPageNumbers.size();

			for (int i = 1; i <= pageNumberSize; i++) {
				iPages = iPages + Integer.valueOf(slPageNumbers.get(i - 1));
				if (i != 1) {
					stampHeaderAndFooter(context, sPDFFilePath, sPDFTempFolderName, sPDFTempDir, sPDFFileName,
							pdfPayLoad, false, false, temp + 1, iPages);
				}
				temp = iPages;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return sPDFFilePath;
	}

	/**
	 * @Desc Class to override the default split character behaviour. Break string
	 *       if string is more than 25character long
	 * @Author Added by IRM(Sogeti)-2018x.3 to use iText for PDF Views
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
				if (glyphPos >= (start + 25)) {
					isBreak = true;
				}
			}
			return super.isSplitCharacter(text, glyphPos) || isBreak;
		}
	}

	/**
	 * @Desc Method to stamp the PDF file.
	 * @Author Added by IRM(Sogeti)-2018x.3 to use iText for PDF Views
	 * @param Context
	 * @param strPDFpath         - PDF file path
	 * @param strTempFolderName  - Temporary Folder to hold stamp file
	 * @param strTempIODirectory - Directory Path
	 * @param strDestFileName    - PDF File Name
	 * @param mapPDFTicketData   - object and pdf header details
	 * @throws Exception
	 */
	public void stampHeaderAndFooter(Context context, String sPDFFilePath, String sPDFTempFolderName,
			String sPDFTempDir, String sPDFFileName, Map pdfPayLoad, boolean bStampHeader, boolean bStampFooter,
			int iStartPageNumber, int iEndPageNumber) throws Exception {

		String strDestPDFpath = new StringBuffer(sPDFTempDir).append(java.io.File.separator).append(sPDFTempFolderName)
				.append(java.io.File.separator).append(CONSTANT_TEMP_PREFIX + sPDFFileName).toString();

		PdfDocument pdfDocmt = new PdfDocument(new PdfReader(sPDFFilePath), new PdfWriter(strDestPDFpath));
		Document doc = new Document(pdfDocmt);
		try {

			int iTotalPages = pdfDocmt.getNumberOfPages();
			if (bStampFooter) {
				addFooter(context, doc, iTotalPages, pdfPayLoad, pdfDocmt);
			}
			if (bStampHeader) {
				addHeader(context, doc, iTotalPages, pdfPayLoad);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			doc.close();
		}
		// Deleting the temp file
		File file = new File(sPDFFilePath);
		if (file.delete()) {
			// Renaming the file
			File oldfile = new File(strDestPDFpath);
			file = new File(sPDFFilePath);
			oldfile.renameTo(file);
		}
	}

	/**
	 * @Desc Method to stamp Footer in the PDF file.
	 * @Author Added by IRM(Sogeti)-2018x.3 to use iText for PDF Views
	 * @param Context
	 * @param doc         - Document to be stamped
	 * @param iTotalPages - Total number of page in pdf file
	 * @param pdfPayLoad  - object and pdf header details
	 * @throws Exception
	 */
	public void addFooter(Context context, Document doc, int iTotalPages, Map pdfPayLoad, PdfDocument pdfDoc)
			throws Exception {

		doc.setFontSize(12f);

		String type = getValueFromMap(pdfPayLoad, DomainConstants.SELECT_TYPE);
		String state = getValueFromMap(pdfPayLoad, DomainConstants.SELECT_CURRENT);
		String revision = getValueFromMap(pdfPayLoad, DomainConstants.SELECT_REVISION);

		String sStatus = getValueFromMap(pdfPayLoad, pgV3Constants.SELECT_ATTRIBUTE_STATUS);
		String sViewType = getValueFromMap(pdfPayLoad, pgV3Constants.PDF_VIEW);
		String sEffectivityDate = getValueFromMap(pdfPayLoad, pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE);
		String sArchiveDate = getValueFromMap(pdfPayLoad, pgV3Constants.SELECT_ATTRIBUTE_PGARCHIVEDATE);

		String sClassification = getValueFromMap(pdfPayLoad, pgV3Constants.SELECT_ATTRIBUTE_PGIPCLASSIFICATION);
		String sSecurityStatus = String.format(Locale.ENGLISH, "%s", sClassification);

		sEffectivityDate = getFormattedDate(sEffectivityDate);
		sArchiveDate = getFormattedDate(sArchiveDate);

		String sFooterStamp = getFooterStamp(context, type, state, sStatus, sViewType, sEffectivityDate, sArchiveDate,
				revision, sSecurityStatus);

		PdfFont sFont3ds = PdfFontFactory.createFont(FONT_NAME_3DS.toString(), PdfEncodings.IDENTITY_H, true);

		String sFooter = EMPTY_STRING;
		Rectangle pageSize = null;
		float PageMargin = 0.0f;
		for (int i = 1; i <= iTotalPages; i++) {
			sFooter = new StringBuffer(sFooterStamp).append(CONSTANT_PAGE).append(String.valueOf(i))
					.append(CONSTANT_OF).append(String.valueOf(iTotalPages)).toString();
			pageSize = pdfDoc.getPage(i).getPageSize();
			PageMargin = (pageSize.getRight() + doc.getRightMargin() - (pageSize.getLeft() + doc.getLeftMargin()))
					/ 2;
			doc.showTextAligned(new Paragraph(sFooter).setFont(sFont3ds), PageMargin, (doc.getLeftMargin() - 10), i,
					TextAlignment.CENTER, VerticalAlignment.TOP, 0);
		}
	}

	/**
	 * @Desc Method to update Footer Stampings
	 * @Author Added by IRM(Sogeti)-2018x.3 to use iText for PDF Views
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
	public static String getFooterStamp(Context context, String type, String state, String sStatus, String sViewType,
			String sEffectivityDate, String sArchiveDate, String revision, String sSecurityStatus) throws Exception {

		StringBuffer footer = new StringBuffer();
		Date today = new Date();
		String date = new SimpleDateFormat(PDF_DATE_FORMAT).format(today);

		String sPLMViewStamp = EnoviaResourceBundle.getProperty(context, "emxCPN.PLMViewStamp.value");
		String sBusinessUseStamp = DomainConstants.EMPTY_STRING;

		if (pgV3Constants.RESTRICTED.equalsIgnoreCase(sSecurityStatus)
				|| pgV3Constants.INTERNAL_USE.equalsIgnoreCase(sSecurityStatus)) {
			sBusinessUseStamp = pgV3Constants.BUSINESS_USE_HYPHEN;
		}
		//Added for Defect 32707
		footer.append(sBusinessUseStamp).append(CONSTANT_BUSINESS_USE_CASE_VIEW).append(SYMBOL_HYPHEN)
		.append(CONST_PG_MARKINGS).append(SYMBOL_SPACE).append(state).append(SYMBOL_SPACE).append(CONST_PRINTED).append(SYMBOL_SPACE).append(date);
		return footer.toString();
	}

	/**
	 * @Desc Method to format date for PDF
	 * @param strDate - Date
	 * @return String - Formatted Date
	 */
	public static String getFormattedDate(String strDate) throws MatrixException {
		SimpleDateFormat formatter = null;
		Date tmpDate = null;
		String formatedDate = null;
		StringBuffer sbformatedDate = new StringBuffer();
		try {
			if (isValidateString(strDate) && (!strDate.contains(CONSTANT_DENIED))) {
				formatter = new SimpleDateFormat(eMatrixDateFormat.getEMatrixDateFormat(), Locale.US);
				tmpDate = formatter.parse(strDate);
				formatter = new SimpleDateFormat(PDF_DATE_FORMAT);
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
	 * @Desc Method used to validate invalid String
	 * @param strVerifyString
	 * @return boolean
	 */
	public static boolean isValidateString(String strVerifyString) {
		boolean isStringDesired = false;
		if (null != strVerifyString && !EMPTY_STRING.equals(strVerifyString)
				&& !CONSTANT_NULL.equalsIgnoreCase(strVerifyString)) {
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
	 * @Desc Method used to pad string to right
	 * @param s - String to be padded
	 * @param n - Pad length
	 * @return String - Padded String
	 */
	public static String padRight(String s, int n) {
		return String.format("%1$-" + n + "s", s);
	}

	/**
	 * @Desc Method used to pad string to left
	 * @param s - String to be padded
	 * @param n - Pad length
	 * @return String - Padded String
	 */
	public static String padLeft(String s, int n) {
		return String.format("%1$#" + n + "s", s);
	}

	/**
	 * @Desc Method used to get value based on key
	 * @param map
	 * @param key
	 * @return -value of the key
	 * @throws Exception
	 */
	public static String getValueFromMap(Map<String, String> map, String key) throws Exception {
		String val = (String) map.get(key);
		return val == null ? EMPTY_STRING : val.trim();
	}
	/**
	 * @Desc Method to stamp Header in the PDF file.
	 * @Author Added by IRM(Sogeti)-2018x.3 to use iText for PDF Views
	 * @param Context 
	 * @param doc - Document to be stamped
	 * @param iTotalPages - Total number of page in pdf file
	 * @param pdfPayLoad - object and pdf header details
	 * @throws Exception
	 */
	public void addHeader(Context context, Document doc, int iTotalPages, Map pdfPayLoad) throws Exception {

		Map<String, String> mpHeaderData = addHeaderStamp(context, doc, iTotalPages, pdfPayLoad);

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

		String strEffectivityDate = (String) getValueFromMap(mpHeaderData, CONST_PAGE2_HEADER3_COL4);

		doc.setFontSize(10f);
		PdfFont font3DS = PdfFontFactory.createFont(FONT_NAME_3DS.toString(), PdfEncodings.IDENTITY_H, true);

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
				doc.showTextAligned(new Paragraph("Page " + String.valueOf(i) + " of " + String.valueOf(iTotalPages))
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

				doc.showTextAligned(new Paragraph("Page " + String.valueOf(i) + " of " + String.valueOf(iTotalPages))
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
	}

	/**
	 * @Desc Method to update header stampings for PDF
	 * @Author Added by IRM (Sogeti)-2018x.3 to use iText for PDF Views
	 * @param Context
	 * @param doc              - Document to be stamped
	 * @param iTotalPages      - Total number of page in pdf file
	 * @param pdfPayLoad              - Header Stamp
	 * @throws Exception
	 */
	public Map addHeaderStamp(Context context, Document doc, int iTotalPages, Map pdfPayLoad) throws Exception {

		Map<String, String> hmResponse = new HashMap<String, String>();

		String name = getValueFromMap(pdfPayLoad, DomainConstants.SELECT_NAME);
		String originator = getValueFromMap(pdfPayLoad, DomainConstants.SELECT_ORIGINATOR);
		String revision = getValueFromMap(pdfPayLoad, DomainConstants.SELECT_REVISION);
		String current = getValueFromMap(pdfPayLoad, DomainConstants.SELECT_CURRENT);
		String type = getValueFromMap(pdfPayLoad, DomainConstants.SELECT_TYPE);
		String policy = getValueFromMap(pdfPayLoad, DomainConstants.SELECT_POLICY);
		String policyDisplayName = EnoviaResourceBundle.getStateI18NString(context, policy, current,
				context.getLocale().getLanguage());

		String sIPClassification = getValueFromMap(pdfPayLoad, pgV3Constants.SELECT_ATTRIBUTE_PGIPCLASSIFICATION);
		String sSecurityStatus = sIPClassification.format(Locale.ENGLISH, "%s", sIPClassification);

		String viewKind = (String) pdfPayLoad.get(CONSTANT_VIEW_KIND);

		System.out.println("CONSTANT_VIEW_KIND:" + CONSTANT_VIEW_KIND);

		String categoryRequestType = (String) pdfPayLoad.get(CONSTANT_TYPE_KIND);
		String requestCategory = (String) pdfPayLoad.get(CONSTANT_REQUEST_CATEGORY);
		categoryRequestType = categoryRequestType + SYMBOL_SPACE + SYMBOL_OPEN_BRACKET + requestCategory
				+ SYMBOL_CLOSE_BRACKET;

		if (pgV3Constants.RESTRICTED.equalsIgnoreCase(sSecurityStatus)) {
			sSecurityStatus = EnoviaResourceBundle.getRangeI18NString(context,
					pgV3Constants.ATTRIBUTE_PGIPCLASSIFICATION, sSecurityStatus, context.getSession().getLanguage());
		}

		sSecurityStatus = CONSTANT_RESTRICTED_VIEW;

		hmResponse.put(PDF_HEADER_CONST_COMPANY_AND_VIEW,
				CONSTANT_COMPANY_DISPLAY_NAME + SYMBOL_HYPHEN + getViewDisplayName(viewKind));
		hmResponse.put(PDF_HEADER_CONST_SECURITY_CLASSIFICATION, sSecurityStatus);
		hmResponse.put(PDF_HEADER_CONST_NAME_COLON, PDF_HEADER_NAME_COLON + name);
		hmResponse.put(PDF_HEADER_CONST_REVISION_COLON, PDF_HEADER_REVISION_COLON + revision);

		hmResponse.put(PDF_HEADER_CONST_STATE_COLON, PDF_HEADER_STATE_COLON + current);
		hmResponse.put(PDF_HEADER_CONST_HAS_ATS_COLON, PDF_HEADER_HAS_ATS_COLON + CONSTANT_NO);

		// display gps assessment category request
		hmResponse.put(PDF_HEADER_CONST_ATTRIBUTE_GPS_DISPLAY_NAME, categoryRequestType);

		return hmResponse;
	}

	/**
	 * @Desc Method to return display value based on string
	 * @Author Added by IRM (Sogeti)-2018x.3 to use iText for PDF Views
	 * @param view
	 * @return
	 * @throws Exception
	 */
	public static String getViewDisplayName(String view) throws Exception {
		switch (view) {
		case VIEW_ALL_INFO:
			return ALL_INFO_VIEW_DISPLAY_NAME;
		}
		return EMPTY_STRING;
	}

}
