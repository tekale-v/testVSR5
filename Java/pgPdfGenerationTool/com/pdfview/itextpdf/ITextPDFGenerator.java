package com.pdfview.itextpdf;

/**
   Project Name: DSM(Sogeti)
   JPO Name: ITextPDFGenerator
   Clone From/Reference:
   Purpose: Class created to implement the iText HTML to PDF Converter.
 **/
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
import com.matrixone.apps.cpn.util.BusinessUtil;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.eMatrixDateFormat;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pdfview.util.AppPathUtility;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.StringList;
public class ITextPDFGenerator {
	
	public static volatile String DATE_FORMAT = null;
	public static volatile String RESOURCES = null;
	public static volatile String LICENSE = null;
	public static volatile String PRODUCT_DATA_TYPES = null;
	public static volatile String NONSTRUCTURED_TYPES = null;
	public static volatile String PAGE_HEIGHT = null;
	public static volatile String PAGE_WIDTH = null;
	private static final String CONST_OBJECTID = "objectId";
	private static final String CONST_PAGE1_HEADER1_COL1 = "page1.header1.col1";
	private static final String CONST_PAGE1_HEADER1_CENTER = "page1.header1.center";
	private static final String CONST_PAGE1_HEADER2_CENTER = "page1.header2.center";
	private static final String CONST_PAGE1_HEADER3_COL1 = "page1.header3.col1";
	private static final String CONST_PAGE1_HEADER3_COL2 = "page1.header3.col2";
	private static final String CONST_PAGE1_HEADER3_COL3 = "page1.header3.col3";
	private static final String CONST_PAGE1_HEADER3_COL4 = "page1.header3.col4";
	private static final String CONST_PAGE1_HEADER4_COL4 = "page1.header4.col4";

	private static final String CONST_PAGE2_HEADER2_CENTER = "page2.header2.center";

	private static final String CONST_PAGE2_HEADER3_COL3 = "page2.header3.col3";
	private static final String CONST_PAGE2_HEADER3_COL4 = "page2.header3.col4";

	private static final String CONST_PG_COMPANY = "The Procter & Gamble Company - ";
	private static final String CONST_HAS_ATS = "Has ATS: ";
	private static final String CONST_NAME_COLON = "Name: ";
	private static final String CONST_REVISION_COLON = "Revision: ";
	
	public static String FONT = DomainConstants.EMPTY_STRING;
	StringBuilder fontName = null;
	
	// Constructor
	public ITextPDFGenerator(Context context, String args[]) throws Exception {
		DATE_FORMAT = EnoviaResourceBundle.getProperty(context, "eServiceSuiteCPN.defaultDateFormat");
		PRODUCT_DATA_TYPES = EnoviaResourceBundle.getProperty(context,
				"emxCPN.ProductDataTemplate.type_ProductDataIPMPDF.TypeInclusionList");
		NONSTRUCTURED_TYPES = EnoviaResourceBundle.getProperty(context,
				"emxCPN.ProductDataTemplate.type_ProductDataIPMPDF.NonStructuredTypeInclusionList");
		PAGE_HEIGHT = EnoviaResourceBundle.getProperty(context, "emxCPN.RenderPDF.iText.PAGEHEIGHT");
		PAGE_WIDTH = EnoviaResourceBundle.getProperty(context, "emxCPN.RenderPDF.iText.PAGEWIDTH");
		// Load License information
		RESOURCES = EnoviaResourceBundle.getProperty(context, "emxCPN.RenderPDF.iTextResource");
		LICENSE = EnoviaResourceBundle.getProperty(context, "emxCPN.RenderPDF.iTextLicenseKey");
		LicenseKey.loadLicenseFile(LICENSE);

		fontName = new StringBuilder(AppPathUtility.getConfigDirectory(context)).append(File.separator).append("fonts")
				.append(File.separator).append("3ds-Regular.ttf");
		
	}

	// Main Method
	public int mxMain(Context context, String args[]) throws Exception {
		
		return 1;
	}

	/**
	 * @Desc Method to create and stamp the PDF file.
	 * @Author Added by DSM(Sogeti) to use iText for PDF Views
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
	 * @Author Added by DSM(Sogeti) to use iText for PDF Views
	 * @param Context
	 * @param strHtmlSource    - HTML file
	 * @param strObjectId      - Object Id
	 * @param strDestFileName  - PDF file name
	 * @param mapPDFTicketData - object and pdf header/footer details
	 * @return String - PDF Path
	 * @throws Exception
	 */
	public String createPdf(Context context, String strHtmlSource, String strObjectId, String strDestFileName,
			Map mapPDFTicketData) throws Exception {
		String strPDFpath = null;
		PdfDocument readers = null;
		boolean isFooterExist = false;
		try {
			// Creating Temp directories
			String strSessionId = context.getSession().getSessionId();
			if (strSessionId.indexOf(pgV3Constants.SYMBOL_COLON) != -1) {
				strSessionId = strSessionId.substring(0, strSessionId.indexOf(pgV3Constants.SYMBOL_COLON));
			}
			String strTimeStamp = Long.toString(System.currentTimeMillis());
			String strTempFolderName = new StringBuffer(strSessionId).append(strTimeStamp).append(strObjectId)
					.toString();
			String strTempIODirectory = System.getProperty("java.io.tmpdir");
			strPDFpath = new StringBuffer(strTempIODirectory).append(java.io.File.separator).append(strTempFolderName)
					.append(java.io.File.separator).append(strDestFileName).toString();
			File fPdf = new File(strPDFpath);
			fPdf.getParentFile().mkdirs();

			// Define page size of PDF
			float fheight = Float.parseFloat(PAGE_HEIGHT);
			float fwidth = Float.parseFloat(PAGE_WIDTH);
			Rectangle pagesize = new Rectangle(fheight * 72, fwidth * 72);
			PageSize newpage = new PageSize(pagesize);

			// Create Document
			PdfWriter pWriter = new PdfWriter(strPDFpath);
			PdfDocument pdfDocument = new PdfDocument(pWriter);
			int iTotalPages1 = pdfDocument.getNumberOfPages();
			pdfDocument.setDefaultPageSize(newpage);
			
			// PDF conversion
			ConverterProperties cp = new ConverterProperties();
			FontProvider fp = new FontProvider();
			fp.addStandardPdfFonts();
			fp.addSystemFonts();
			fp.addFont(FONT);
			
			fp.addDirectory(RESOURCES);
			cp.setFontProvider(fp);
			cp.setBaseUri(RESOURCES);
			// Convert HTML to PDF.
			InputStream inputStream = new ByteArrayInputStream(strHtmlSource.getBytes("UTF-8"));
			Document doc = HtmlConverter.convertToDocument(inputStream, pdfDocument, cp);
			try {
				// Setting the default split character
				doc.setSplitCharacters(new SplitChar());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				doc.close();
				pdfDocument.close();
			}
			String strPDFViewType = (String) mapPDFTicketData.get(pgV3Constants.PDF_VIEW);
			// Append TS Files
			String strPDFTemp = null;

			InputStream pdf = null;
			PdfDocument pdfDoc = null;
			String strTempDirectory = new StringBuffer(strTempIODirectory).append(java.io.File.separator)
					.append(strTempFolderName).toString();
			if (!pgV3Constants.PDFVIEW_CONSOLIDATEDPACKAGING.equalsIgnoreCase(strPDFViewType)
					&& !pgV3Constants.PDFVIEW_WAREHOUSE.equalsIgnoreCase(strPDFViewType)) {
				isFooterExist = true;
			}
			// Stamp header and footer for PDF
			stampHeaderAndFooter(context, strPDFpath, strTempFolderName, strTempIODirectory, strDestFileName,
					mapPDFTicketData, true, isFooterExist, 1, iTotalPages1);
			StringList slPageNum = new StringList();
			// Footer stamping will be done for XML generated pdf pages.
			stampHeaderAndFooter(context, strPDFpath, strTempFolderName, strTempIODirectory, strDestFileName,
					mapPDFTicketData, false, true, 1, iTotalPages1);
			Map Argsmap = new HashMap();
			Argsmap.put(CONST_OBJECTID, strObjectId);
			Argsmap.put("strPDFViewType", strPDFViewType);
			Argsmap.put("strTempDirectory", strTempDirectory);
			String[] strArgs = JPO.packArgs(Argsmap);
			int iTotalPages = 0;
			List<InputStream> inputPdfList = new ArrayList<InputStream>();
			inputPdfList.add(new FileInputStream(strPDFpath));
			strPDFTemp = new StringBuffer(strTempIODirectory).append(java.io.File.separator).append(strTempFolderName)
					.append(java.io.File.separator).append("Merge.pdf").toString();
			readers = new PdfDocument(new PdfWriter(strPDFTemp));
			Iterator<InputStream> pdfIterator = inputPdfList.iterator();
			int count = 0;
			// Create reader list for the input pdf files.
			while (pdfIterator.hasNext()) {
				try {
					pdf = pdfIterator.next();
					pdfDoc = new PdfDocument(new PdfReader(pdf));
					pdfDoc.copyPagesTo(1, pdfDoc.getNumberOfPages(), readers);
					count++;
					slPageNum.add(Integer.toString(pdfDoc.getNumberOfPages()));
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					pdfDoc.close();
					pdf.close();
				}

			}
			// Add Footer Stamp to the merged pagesize

			Document document = new Document(readers);
			try {
				iTotalPages = readers.getNumberOfPages();
			} catch (Exception e) {

				e.printStackTrace();

			} finally {

				document.close();
			}
			// Deleting the temp file
			File file = new File(strPDFpath);
			if (file.delete()) {
				// Renaming the file
				File oldfile = new File(strPDFTemp);
				file = new File(strPDFpath);
				oldfile.renameTo(file);
			}
			int iXMLPages = 0;
			int temp = 0;
			int ilPageNumsize=slPageNum.size();
			for (int i = 1; i <= ilPageNumsize; i++) {
				iXMLPages = iXMLPages + Integer.valueOf(slPageNum.get(i - 1));
				if (i != 1) {
					stampHeaderAndFooter(context, strPDFpath, strTempFolderName, strTempIODirectory, strDestFileName,
							mapPDFTicketData, false, false, temp + 1, iXMLPages);
				}
				temp = iXMLPages;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return strPDFpath;
	}

	/**
	 * @Desc Method to stamp the PDF file.
	 * @Author Added by DSM(Sogeti) to use iText for PDF Views
	 * @param Context
	 * @param strPDFpath         - PDF file path
	 * @param strTempFolderName  - Temporary Folder to hold stamp file
	 * @param strTempIODirectory - Directory Path
	 * @param strDestFileName    - PDF File Name
	 * @param mapPDFTicketData   - object and pdf header details
	 * @throws Exception
	 */
	public void stampHeaderAndFooter(Context context, String strPDFpath, String strTempFolderName,
			String strTempIODirectory, String strDestFileName, Map mapPDFTicketData, boolean bStampHeader,
			boolean bStampFooter, int iStartPageNumber, int iEndPageNumber) throws Exception {
		String strDestPDFpath = new StringBuffer(strTempIODirectory).append(java.io.File.separator)
				.append(strTempFolderName).append(java.io.File.separator).append("temp_" + strDestFileName).toString();
		PdfDocument pdfDocmt = new PdfDocument(new PdfReader(strPDFpath), new PdfWriter(strDestPDFpath));
		Document doc = new Document(pdfDocmt);

		try {

			int iTotalPages = pdfDocmt.getNumberOfPages();
			String strPDFViewType = (String) mapPDFTicketData.get(pgV3Constants.PDF_VIEW);
			// stamp the header and footer
			if (!pgV3Constants.PDFVIEW_GENDOC.equalsIgnoreCase(strPDFViewType) && bStampFooter) {
				addFooter(context, doc, iTotalPages, mapPDFTicketData, pdfDocmt);
			}
			if (bStampHeader) {
				addHeader(context, doc, iTotalPages, mapPDFTicketData);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			doc.close();
		}
		// Deleting the temp file
		File file = new File(strPDFpath);
		if (file.delete()) {
			// Renaming the file
			File oldfile = new File(strDestPDFpath);
			file = new File(strPDFpath);
			oldfile.renameTo(file);
		}
	}

	/**
	 * @Desc Method to stamp Footer in the PDF file.
	 * @Author Added by DSM(Sogeti) to use iText for PDF Views
	 * @param Context
	 * @param doc              - Document to be stamped
	 * @param iTotalPages      - Total number of page in pdf file
	 * @param mapPDFTicketData - object and pdf header details
	 * @throws Exception
	 */
	public void addFooter(Context context, Document doc, int iTotalPages, Map mapPDFTicketData, PdfDocument pdfDoc)
			throws Exception {
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

		String strFooterStamp = getFooterStampings(context, strObjectType, strObjectCurrentState, strObjectStatus,
				strPDFViewType, strEffectiveDate, strArchiveDate, strObjectRevision, strObjectSecurityStatus);
		PdfFont FONTDSREGULAR = PdfFontFactory.createFont(fontName.toString(), PdfEncodings.IDENTITY_H, true);
		for (int i = 1; i <= iTotalPages; i++) {
			String strFooter = new StringBuffer(strFooterStamp).append(" Page ").append(String.valueOf(i))
					.append(" of ").append(String.valueOf(iTotalPages)).toString();
			Rectangle pageSize = pdfDoc.getPage(i).getPageSize();
			float PageMargin = (pageSize.getRight() + doc.getRightMargin() - (pageSize.getLeft() + doc.getLeftMargin()))
					/ 2;
			doc.showTextAligned(new Paragraph(strFooter).setFont(FONTDSREGULAR), PageMargin, (doc.getLeftMargin() - 10),
					i, TextAlignment.CENTER, VerticalAlignment.TOP, 0);
		}
	}

	/**
	 * @Desc Method to update Footer Stampings
	 * @Author Added by DSM(Sogeti) to use iText for PDF Views
	 * @param Context
	 * @param strType                 - Object Type
	 * @param strObjectLifeCycleState - Object Current
	 * @param strObjectStatus         - Status attribute of object
	 * @param strPDFViewType          - PDF View
	 * @param strStampingDate         - Stamp Date
	 * @param strArchiveDate          - Archieve Date
	 * @param strObjectRevision       - Object Revision
	 * @param String                  - Footer Stamp
	 * @throws Exception
	 */
	public static String getFooterStampings(Context context, String strType, String strObjectLifeCycleState,
			String strObjectStatus, String strPDFViewType, String strStampingDate, String strArchiveDate,
			String strObjectRevision, String strObjectSecurityStatus) throws Exception {
		StringBuffer sbFooterBuffer = new StringBuffer();

		Date today = null;
		String sCurrentDate = null;
		today = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		sCurrentDate = dateFormat.format(today);
		
		String strPLMViewStamp = EnoviaResourceBundle.getProperty(context, "emxCPN.PLMViewStamp.value");
		String strBusinessUseStamp = DomainConstants.EMPTY_STRING;
		if (pgV3Constants.RESTRICTED.equalsIgnoreCase(strObjectSecurityStatus)
				|| pgV3Constants.INTERNAL_USE.equalsIgnoreCase(strObjectSecurityStatus)) {
			strBusinessUseStamp = pgV3Constants.BUSINESS_USE_HYPHEN;
		}
		sbFooterBuffer.append(strBusinessUseStamp).append(strPLMViewStamp).append(":").append(sCurrentDate);
		return sbFooterBuffer.toString();
	}

	/**
	 * @Desc Method to stamp Header in the PDF file.
	 * @Author Added by DSM(Sogeti) to use iText for PDF Views
	 * @param Context
	 * @param doc              - Document to be stamped
	 * @param iTotalPages      - Total number of page in pdf file
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
		String strAcronymAndPGSAPTypePage2 = (String) mpHeaderData.get(CONST_PAGE2_HEADER2_CENTER);
		String strEffectivityDate = (String) mpHeaderData.get(CONST_PAGE2_HEADER3_COL4);
		String strSAPDescriptionOrTitlePage2 = (String) mpHeaderData.get(CONST_PAGE2_HEADER3_COL3);

		strPGSecurityClassification = validateNullString(strPGSecurityClassification);
		strCompanyAndPDFView = validateNullString(strCompanyAndPDFView);
		strAcronymAndPGSAPTypePage1 = validateNullString(strAcronymAndPGSAPTypePage1);
		strName = validateNullString(strName);
		strRevision = validateNullString(strRevision);
		strCurrent = validateNullString(strCurrent);
		strIsATS = validateNullString(strIsATS);
		strHasATS = validateNullString(strHasATS);
		strSAPDescriptionOrTitle = validateNullString(strSAPDescriptionOrTitle);

		strAcronymAndPGSAPTypePage2 = validateNullString(strAcronymAndPGSAPTypePage2);
		strEffectivityDate = validateNullString(strEffectivityDate);
		strSAPDescriptionOrTitlePage2 = validateNullString(strSAPDescriptionOrTitlePage2);

		doc.setFontSize(10f);
		PdfFont FONTDSREGULAR = PdfFontFactory.createFont(fontName.toString(), PdfEncodings.IDENTITY_H, true);
		for (int i = 1; i <= iTotalPages; i++) {

			if (i == 1) {
				// Adding header for page 1
				doc.showTextAligned(new Paragraph(strPGSecurityClassification).setFont(FONTDSREGULAR), 35, 570, i,
						TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
				doc.showTextAligned(new Paragraph(strCompanyAndPDFView).setFont(FONTDSREGULAR), 410, 570, i,
						TextAlignment.CENTER, VerticalAlignment.BOTTOM, 0);
				doc.showTextAligned(new Paragraph("Page " + String.valueOf(i) + " of " + String.valueOf(iTotalPages))
						.setFont(FONTDSREGULAR), 790, 570, i, TextAlignment.RIGHT, VerticalAlignment.BOTTOM, 0);
				doc.setFontSize(8f);
				doc.setBold();
				doc.showTextAligned(new Paragraph(strAcronymAndPGSAPTypePage1).setFont(FONTDSREGULAR), 410, 557, i,
						TextAlignment.CENTER, VerticalAlignment.BOTTOM, 0);
				doc.deleteOwnProperty(8);
				doc.setFontSize(10f);
				doc.showTextAligned(new Paragraph(strName).setFont(FONTDSREGULAR), 35, 544, i, TextAlignment.LEFT,
						VerticalAlignment.BOTTOM, 0);
				doc.showTextAligned(new Paragraph(strRevision).setFont(FONTDSREGULAR), 195, 544, i,
						TextAlignment.JUSTIFIED, VerticalAlignment.BOTTOM, 0);
				doc.showTextAligned(new Paragraph(strCurrent).setFont(FONTDSREGULAR), 355, 544, i,
						TextAlignment.JUSTIFIED, VerticalAlignment.BOTTOM, 0);
				doc.showTextAligned(new Paragraph(strIsATS).setFont(FONTDSREGULAR), 575, 544, i,
						TextAlignment.JUSTIFIED, VerticalAlignment.BOTTOM, 0);
				doc.showTextAligned(new Paragraph(strSAPDescriptionOrTitle).setFont(FONTDSREGULAR), 35, 532, i,
						TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
				doc.showTextAligned(new Paragraph(strHasATS).setFont(FONTDSREGULAR), 575, 532, i,
						TextAlignment.JUSTIFIED, VerticalAlignment.BOTTOM, 0);
			} else {
				doc.showTextAligned(new Paragraph(strPGSecurityClassification).setFont(FONTDSREGULAR), 35, 570, i,
						TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
				doc.showTextAligned(new Paragraph(strCompanyAndPDFView).setFont(FONTDSREGULAR), 410, 570, i,
						TextAlignment.CENTER, VerticalAlignment.BOTTOM, 0);
				doc.showTextAligned(new Paragraph("Page " + String.valueOf(i) + " of " + String.valueOf(iTotalPages))
						.setFont(FONTDSREGULAR), 790, 570, i, TextAlignment.RIGHT, VerticalAlignment.BOTTOM, 0);
				doc.setFontSize(8f);
				doc.setBold();
				doc.showTextAligned(new Paragraph(strAcronymAndPGSAPTypePage2).setFont(FONTDSREGULAR), 410, 557, i,
						TextAlignment.CENTER, VerticalAlignment.BOTTOM, 0);
				doc.deleteOwnProperty(8);
				doc.setFontSize(10f);
				doc.showTextAligned(new Paragraph(strName).setFont(FONTDSREGULAR), 35, 544, i, TextAlignment.LEFT,
						VerticalAlignment.BOTTOM, 0);
				doc.showTextAligned(new Paragraph(strRevision).setFont(FONTDSREGULAR), 165, 544, i,
						TextAlignment.JUSTIFIED, VerticalAlignment.BOTTOM, 0);
				doc.showTextAligned(new Paragraph(strSAPDescriptionOrTitlePage2).setFont(FONTDSREGULAR), 245, 544, i,
						TextAlignment.JUSTIFIED, VerticalAlignment.BOTTOM, 0);
				doc.showTextAligned(new Paragraph(strEffectivityDate).setFont(FONTDSREGULAR), 790, 544, i,
						TextAlignment.RIGHT, VerticalAlignment.BOTTOM, 0);
			}

			doc.showTextAligned(new Paragraph(
					"_________________________________________________________________________________________________________________________________________")
							.setFont(FONTDSREGULAR),
					35, 520, i, TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
		}
	}

	/**
	 * @Desc Method to update header stampings for PDF
	 * @Author Added by DSM(Sogeti) to use iText for PDF Views
	 * @param Context
	 * @param doc              - Document to be stamped
	 * @param iTotalPages      - Total number of page in pdf file
	 * @param mapPDFTicketData - object and pdf header details
	 * @param Map              - Header Stamp
	 * @throws Exception
	 */
	public Map addHeaderStamp(Context context, Document doc, int iTotalPages, Map mapObjectHeaderData)
			throws Exception {
		boolean isTextRightUpdate = true;
		Map hmResponse = new HashMap();
		String strObjectName = (String) mapObjectHeaderData.get(DomainConstants.SELECT_NAME);
		String strObjectOriginator = (String) mapObjectHeaderData.get(DomainConstants.SELECT_ORIGINATOR);
		String strObjectRevision = (String) mapObjectHeaderData.get(DomainConstants.SELECT_REVISION);
		String strObjectCurrentState = (String) mapObjectHeaderData.get(DomainConstants.SELECT_CURRENT);
		String strObjectType = (String) mapObjectHeaderData.get(DomainConstants.SELECT_TYPE);
		String strOriginatingSource = (String) mapObjectHeaderData
				.get(pgV3Constants.SELECT_ATTRIBUTE_PGORIGINATINGSOURCE);
		String strObjectPolicy = (String) mapObjectHeaderData.get(DomainConstants.SELECT_POLICY);
		String strObjectCurrentDisplay = EnoviaResourceBundle.getStateI18NString(context, strObjectPolicy,
				strObjectCurrentState, context.getLocale().getLanguage());
		if (BusinessUtil.isNotNullOrEmpty(strObjectCurrentDisplay))
			strObjectCurrentState = strObjectCurrentDisplay;
		String pgIPClassification = (String) mapObjectHeaderData.get(pgV3Constants.SELECT_ATTRIBUTE_PGIPCLASSIFICATION);
		String strPDFViewType = (String) mapObjectHeaderData.get("View");
		String strObjectSecurityStatus = pgIPClassification.format(Locale.ENGLISH, "%s", pgIPClassification);
		String strHeader1 = (String) mapObjectHeaderData.get("header1");
		String strHeader2 = (String) mapObjectHeaderData.get("header2");
		String strHeader3 = (String) mapObjectHeaderData.get("header3");
		strHeader3 = validateNullString(strHeader3);
		
		String strProductDataHeaderViews = EnoviaResourceBundle.getProperty(context, "emxCPN.HeaderViews.ProductData");

		if (pgV3Constants.RESTRICTED.equalsIgnoreCase(strObjectSecurityStatus)) {
			strObjectSecurityStatus = EnoviaResourceBundle.getRangeI18NString(context,
					pgV3Constants.ATTRIBUTE_PGIPCLASSIFICATION, strObjectSecurityStatus,
					context.getSession().getLanguage());
		}
		String strArchiveDate = (String) mapObjectHeaderData.get(pgV3Constants.SELECT_ATTRIBUTE_PGARCHIVEDATE);
		if (validateString(strArchiveDate)) {
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
		// Obtain HASATS details
		String strHasATS = (String) JPO.invoke(context, "pgDSOCPNProductData", null, "displayhasATSHeaderAttribute",
				JPO.packArgs(hmRequestMap), String.class);
		// Obtain ISATS details
		String strIsATS = (String) JPO.invoke(context, "pgIPMProductData", null, "displayIsATSHeaderAttribute",
				JPO.packArgs(hmRequestMap), String.class);

		String strSAPTitle = (String) mapObjectHeaderData.get(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
		String strSAPType = (String) mapObjectHeaderData.get(pgV3Constants.SELECT_ATTRIBUTE_PGSAPTYPE);
		strSAPType = getSAPTypeForPDF(strPDFViewType, strSAPType, strObjectType, PRODUCT_DATA_TYPES,
				NONSTRUCTURED_TYPES, strOriginatingSource);
		String strProductDataObjectName = padRight(strObjectName, 12);
		String strProductDataObjectRevision = padRight(strObjectRevision, 5);
		strObjectName = padRight(strObjectName, 25);
		strObjectOriginator = padRight(strObjectOriginator, 25);
		strObjectRevision = padRight(strObjectRevision, 25);
		strObjectCurrentState = padRight(strObjectCurrentState, 34);
		if ("".equals(strEffectiveDate) || "".equals(strExpiryDate) || "".equals(strReleaseDate)) {
			strEffectiveDate = padRight(strEffectiveDate, 19);
			strExpiryDate = padRight(strExpiryDate, 19);
			strReleaseDate = padRight(strReleaseDate, 18) + ".";
		}

			hmResponse.put(CONST_PAGE1_HEADER1_CENTER, CONST_PG_COMPANY + strHeader1);
			hmResponse.put(CONST_PAGE1_HEADER1_COL1, strObjectSecurityStatus);
			hmResponse.put(CONST_PAGE1_HEADER3_COL1, CONST_NAME_COLON + strObjectName);
			hmResponse.put(CONST_PAGE1_HEADER3_COL2, CONST_REVISION_COLON + strObjectRevision);
			hmResponse.put(CONST_PAGE1_HEADER3_COL3, "State: " + strObjectCurrentState);
			hmResponse.put(CONST_PAGE1_HEADER4_COL4, CONST_HAS_ATS + strHasATS);
			String strHeader22 = strHeader2.substring(0, strHeader2.length() - 2);
			hmResponse.put(CONST_PAGE2_HEADER2_CENTER, strHeader22);
			hmResponse.put(CONST_PAGE2_HEADER3_COL4, "Effective Date: " + strEffectiveDate);
			hmResponse.put(CONST_PAGE1_HEADER2_CENTER, strHeader2 + " " + strSAPType + " " + strHeader3);
			
		return hmResponse;
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
			if (validateString(strDate) && (!strDate.contains("DENIED"))) {
				formatter = new SimpleDateFormat(eMatrixDateFormat.getEMatrixDateFormat(), Locale.US);
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
	 * @param strPDFViewType        - Represent which PDF View.
	 * @param strSAPType            - Actual SAP Type
	 * @param strObjectType         - Actual object Type
	 * @param strObjectType         - Structured type list
	 * @param strNonStructuredTypes - Non Structured type list
	 * @param strOriginatingSource  - Object originating source
	 * @return SAP Type to be used for PDF Views
	 * @throws Exception
	 */
	private static String getSAPTypeForPDF(String strPDFViewType, String strSAPType, String strObjectType,
			String strProductDataTypes, String strNonStructuredTypes, String strOriginatingSource) throws Exception {
		if (strProductDataTypes.contains(strObjectType) || (strNonStructuredTypes.contains(strObjectType)
				&& !(strObjectType.equalsIgnoreCase(pgV3Constants.TYPE_PGRAWMATERIAL)))) {
			if (pgV3Constants.PDFVIEW_ALLINFO.equalsIgnoreCase(strPDFViewType)
					|| pgV3Constants.PDFVIEW_GENDOC.equalsIgnoreCase(strPDFViewType)
					|| (pgV3Constants.PDFVIEW_CONSOLIDATEDPACKAGING.equalsIgnoreCase(strPDFViewType)
							&& pgV3Constants.TYPE_FINISHEDPRODUCTPART.equals(strObjectType))
					|| pgV3Constants.PQR_VIEW.equalsIgnoreCase(strPDFViewType)) {
				strSAPType = validateString(strSAPType) ? "(" + strSAPType + ")" : "(N/A)";
			} else if ((pgV3Constants.PDFVIEW_COMBINEDWITHMASTER.equalsIgnoreCase(strPDFViewType))
					&& (((pgV3Constants.TYPE_PGRAWMATERIAL.equals(strObjectType))
							&& strOriginatingSource.contains(pgV3Constants.CSS_SOURCE))
							|| pgV3Constants.TYPE_PGMASTERRAWMATERIAL.equals(strObjectType))) {
				strSAPType = DomainConstants.EMPTY_STRING;
			}
		} else {
			strSAPType = (pgV3Constants.PDFVIEW_ALLINFO.equalsIgnoreCase(strPDFViewType) && validateString(strSAPType))
					? "(" + strSAPType + ")"
					: DomainConstants.EMPTY_STRING;
		}
		return strSAPType;
	}

	/**
	 * @Desc Method used to validate invalid String
	 * @param strVerifyString
	 * @return boolean
	 */
	public static boolean validateString(String strVerifyString) {
		boolean isStringDesired = false;
		if (UIUtil.isNotNullAndNotEmpty(strVerifyString)) {
			isStringDesired = true;
		}
		return isStringDesired;
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
	 * @Desc Class to override the default split character behaviour. Break string
	 *       if string is more than 25character long
	 * @Author Added by DSM(Sogeti) to use iText for PDF Views
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

	public String removeLastChar(String str) {
		if (str != null && str.length() > 0 && str.charAt(str.length() - 2) == '-') {
			str = str.substring(0, str.length() - 3);
		}
		else if (str != null && str.length() > 0 && str.charAt(str.length() - 1) == '-') {
			str = str.substring(0, str.length() - 2);
		}
		else {
			return str;
		}
		return str;
	}
}
