package com.pg.pdf.views;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import matrix.db.Context;
import matrix.util.MatrixException;
import org.apache.log4j.PropertyConfigurator;
import com.matrixone.jdom.Document;
import com.pg.pdf.bean.input.xml.XML;
import com.pg.pdf.bean.output.xml.OutPutXML;
import com.pg.pdf.enumerations.PDFConstants;
import com.pg.pdf.enumerations.UIConstant;
import com.pg.pdf.util.PDFUtil;
public class PDFApp extends PDFConstants{
	private  final Logger logger = Logger.getLogger(PDFApp.class.getName());
	Context context;
	XML xml;
	PDFUtil pdfUtil;
	OutPutXML outPutXMLBean;
	boolean isValidView;
	public PDFApp(Context context) throws JAXBException, MatrixException {
		this.context = context;
		pdfUtil = new PDFUtil();
		this.xml = pdfUtil.getXMLPage(context);
	}
	public void init(Properties properties) {
		PropertyConfigurator.configure(new File(properties.getProperty(UIConstant.PROPERTY_FILE_LOGGER.getValue())).getAbsolutePath());
		logger.info("log4j loaded");
	}
	public String generatePDF(Context context, String[] args) throws MatrixException{
		logger.info("Enter  PDFApp -method generatePDF");
		String objectId = args[0];
		String viewName = args[1];
		String pdfFile = EMPTY_STRING;
		try {
			Map<String, String>  requestMap = new HashMap<> ();
			requestMap.put(CONSTANT_OBJECT_ID, objectId);
			requestMap.put(CONST_VIEW, viewName);
			requestMap.put(CONSTANT_LANGUAGE, context.getLocale().getLanguage());
			pdfUtil.setObjDetailsInBean(context, xml, requestMap);
			Document xmlDoc = pdfUtil.createXML();
			String  workDir = pdfUtil.createWorkdir(context);
			String htmlCodeData = pdfUtil.generateHTML(context,xmlDoc);
			pdfFile =   pdfUtil.buildPayLoadCalliText(context,objectId,viewName,htmlCodeData,workDir);
		} catch (Exception e) {
			logger.info("Exception occurred in PDFApp -method generatePDF: " + e.getMessage());
			
		}finally {
			xml.getSections().clear();
		}
		logger.info("Exit  PDFApp -method generatePDF");
		return pdfFile;   
	}
	

}
