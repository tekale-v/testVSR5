package com.pdfview.dataExtract;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;

import com.matrixone.apps.awl.util.AWLPropertyUtil;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pdfview.canonicalPacket.ActualPacket;
import com.pdfview.canonicalPacket.CanonicalPacket;
import com.pdfview.canonicalPacket.CanonicalPackets;
import com.pdfview.canonicalPacket.ObjectTypes;
import com.pdfview.constant.PDFConstant;
import com.pdfview.exception.PDFToolCustomException;
import com.pdfview.framework.PDFToolConfigurator;
import com.pdfview.framework.PDFToolDispatcher;
import com.pdfview.framework.PDFToolEnoviaConfigurator;
import com.pdfview.helper.EnoviaHelper;
import com.pdfview.itextpdf.ITextPDFGenerator;
import com.pdfview.registry.RegisteredItem;
import com.pdfview.util.AppPathUtility;
import com.pdfview.util.CanonicalPacketUtility;
import com.pdfview.util.RegistryUtility;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.SelectList;
import matrix.util.StringList;

public class PDFViewController {

	/**
	 * @description: This is the main PDF Tool method which performs: - Metadata XML
	 *               extraction - various PDF Views extraction - various Reference
	 *               Documents extraction - File Details XML extraction
	 * 
	 * @param context
	 * @param String[]
	 * @return void
	 * @throws Exception
	 */
	public String performExtraction(Context context, String[] sArgs) throws Exception {
		String strFileInfo = DomainConstants.EMPTY_STRING;
		try {
			setServerPath(context);
			String sId = DomainConstants.EMPTY_STRING;
			String sType = DomainConstants.EMPTY_STRING;
			String sName = DomainConstants.EMPTY_STRING;
			String sRevision = DomainConstants.EMPTY_STRING;
			String sPacketName = DomainConstants.EMPTY_STRING;
			String sMetaDataOutput = DomainConstants.EMPTY_STRING;
			String sTableNames = DomainConstants.EMPTY_STRING;
			sType = sArgs[0];
			sName = sArgs[1];
			sRevision = sArgs[2];
			sTableNames = sArgs[3];
			String xslFileName = sType.replaceAll(pgV3Constants.SYMBOL_SPACE, DomainConstants.EMPTY_STRING)
					.concat(".xsl");

			PDFConstant.selectedTableNames = sTableNames;
			MapList doclist = getObjectId(context, sType, sName, sRevision);
			Map mapAdlibTicketData = new HashMap();
			if (doclist != null && !doclist.isEmpty()) {
				mapAdlibTicketData = (Map) doclist.get(0);
			}

			// get packet name based on type
			sPacketName = getPacketNameForType(context, sType);
			if (UIUtil.isNullOrEmpty(sPacketName)) {
				System.out.println("Packet Name is Empty.........................!!!");
			}
			System.out.println("sPacketName -->  " + sPacketName);
			
			sMetaDataOutput = extractMetaData(context, sPacketName, sType, sName, sRevision);
			boolean isXMLDataIsEmpty = false;
			StringList sList = FrameworkUtil.split(sTableNames, pgV3Constants.SYMBOL_COMMA);
			int size = sList.size();
			String elementName =DomainConstants.EMPTY_STRING;
			for (int i = 0; i < size; i++) {
				elementName = sList.get(i);
				if (sMetaDataOutput.contains(elementName)) {
					isXMLDataIsEmpty = true;
					break;
				}
			}
			if (!isXMLDataIsEmpty) {
				System.out.println("XML Data Is Empty......!!!");
				return pgV3Constants.FALSE;
			}
			System.out.println("XML data:" + sMetaDataOutput);
			InputStream is = new ByteArrayInputStream(sMetaDataOutput.getBytes());
			String htlmStringData = getHTMLCode(context, is, xslFileName);
			sId = (String) mapAdlibTicketData.get(DomainConstants.SELECT_ID);
			System.out.println(" XSL template --> " + xslFileName);
			String shortName = EnoviaHelper.loadShortType(context, sType);
			sType = EnoviaHelper.getTypeName(context, sType);
			mapAdlibTicketData.put("header2", sType + " (" + shortName + ") -");
			mapAdlibTicketData.put("header1", PDFConstant.ALL_INFORMATION_VIEW);
			mapAdlibTicketData.put("View", "allinfo");
			Map Argsmap = new HashMap();
			
			String strPDFfileName = sType + "-" + sName + "-Rev" + sRevision + ".pdf";
			
			Argsmap.put("strHTMLWithData", htlmStringData);
			Argsmap.put("objectId", sId);
			Argsmap.put("strPDFfileName", strPDFfileName);
			Argsmap.put("mapPDFTicketData", mapAdlibTicketData);
			String[] strArgs = JPO.packArgs(Argsmap);
			if (UIUtil.isNotNullAndNotEmpty(htlmStringData)) {
				ITextPDFGenerator pig;
				try {
					pig = new ITextPDFGenerator(context, strArgs);
					strFileInfo = (String) pig.createPdf(context, strArgs);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				strFileInfo = pgV3Constants.FALSE;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (context.isConnected())
				context.shutdown();
		}
		return strFileInfo;
	}

	public String getTableNames(Context ctxApplicationUser, String[] sArgs) {
		String tableNames = DomainConstants.EMPTY_STRING;
		try {

			String _sType = DomainConstants.EMPTY_STRING;
			String _sName = DomainConstants.EMPTY_STRING;
			String _sRevision = DomainConstants.EMPTY_STRING;
			String _sPacketName = DomainConstants.EMPTY_STRING;
			_sType = sArgs[0];
			_sName = sArgs[1];
			_sRevision = sArgs[2];
			_sPacketName = getPacketNameForType(ctxApplicationUser, _sType);
			if (UIUtil.isNullOrEmpty(_sPacketName)) {
				System.out.println("Packet Name is Empty.........................!!!");
			}
			CanonicalPacketUtility cpUtil = new CanonicalPacketUtility();
			CanonicalPacket cp = cpUtil.getCanonicalPacket(ctxApplicationUser, _sPacketName);
			if (null == cp) {
				throw new PDFToolCustomException("Invalid Packet Name", "E016");
			}

			RegisteredItem ri = null;
			if (!cp.getCanonicalPacketName().equals(_sPacketName))
				ri = RegistryUtility.findEntry(ctxApplicationUser, _sPacketName);
			else {
				ri = RegistryUtility.findEntry(ctxApplicationUser, cpUtil.actualPacketName(cp, _sType));
			}

			if (null == ri) {
				throw new PDFToolCustomException("Invalid Packet Name", "E016", cp, 4);
			}

			String sTopLevelClass = ri.getJaxbCLass();
			if (null == sTopLevelClass) {
				throw new PDFToolCustomException("Invalid JAXB class for Packet", "E020", cp, 4);
			}
			String sVault = DomainConstants.EMPTY_STRING;
			if (UIUtil.isNullOrEmpty(sVault))
				sVault = pgV3Constants.VAULT_ESERVICEPRODUCTION;
			BusinessObject bo = new BusinessObject(_sType, _sName, _sRevision, sVault);
			PDFToolConfigurator dh = new PDFToolEnoviaConfigurator(ctxApplicationUser, bo, ri);
			tableNames = dh.getTableNamesData();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return tableNames;
	}

	public static String getHTMLCode(Context context, InputStream inputXMLinputStreamData, String xslFileName) {
		StringBuffer output = new StringBuffer();
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

			File xsl = new File(AppPathUtility.getConfigDirectory(context) + "/xsltemplate/" + xslFileName);

			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(inputXMLinputStreamData);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			StreamSource style = new StreamSource(xsl);
			Transformer transformer = transformerFactory.newTransformer(style);

			DOMSource source = new DOMSource(document);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			transformer.transform(source, result);
			output = writer.getBuffer();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output.toString();

	}

	/**
	 * @description: This method is used to get the Id of object against its TNR
	 * @param context sType : Object type sName : Object name sRevision : Object
	 *                revision
	 * @return String
	 */
	private MapList getObjectId(Context context, String sType, String sName, String sRevision) {
		MapList doclist = null;
		try {
			SelectList selects = new SelectList(12);
			selects.add(DomainConstants.SELECT_ID);

			selects.add(pgV3Constants.SELECT_ATTRIBUTE_PGIPCLASSIFICATION);
			selects.add(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
			selects.add("from["+pgV3Constants.RELATIONSHIP_PGMASTER+"]");
			selects.add("to["+pgV3Constants.RELATIONSHIP_AUTHORIZEDTEMPORARYSPECIFICATION+"]");
			selects.add(pgV3Constants.SELECT_ATTRIBUTE_PGSAPTYPE);
			selects.add(DomainConstants.SELECT_REVISION);
			selects.add(DomainConstants.SELECT_NAME);
			selects.add(DomainConstants.SELECT_POLICY);
			selects.add(DomainConstants.SELECT_TYPE);
			selects.add(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE);
			selects.add(pgV3Constants.SELECT_CURRENT);
			doclist = DomainObject.findObjects(context, sType, sName, sRevision, pgV3Constants.SYMBOL_STAR,
					pgV3Constants.VAULT_ESERVICEPRODUCTION, null, false, selects);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return doclist;
	}

	/**
	 * @description: This method is used to get the Type specific Packet Name
	 * @param sType - Type of the object
	 * @return String
	 */
	private static String getPacketNameForType(Context context, String sType) {
		String strPacketName = DomainConstants.EMPTY_STRING;
		try {
			PDFViewController ctlr = new PDFViewController();
			CanonicalPacket cp = new CanonicalPacket();
			cp = ctlr.getCanonicalPacket(context, sType);

			if (null != cp.getCanonicalPacketName()) {
				strPacketName = cp.getCanonicalPacketName();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return strPacketName;
	}

	/**
	 * @description: This is a sub method to get the Type specific Packet Name
	 * @param sActualType - Type of the object
	 * @return CanonicalPacket
	 * @throws PDFToolCustomException
	 */
	public CanonicalPacket getCanonicalPacket(Context context, String sActualType) throws PDFToolCustomException {
		CanonicalPacket canonicalPacket = null;
		CanonicalPacketUtility cpUtil = new CanonicalPacketUtility();
		CanonicalPackets canonicalPackets = cpUtil.loadCanonicalFile(context);

		if (null != canonicalPackets && UIUtil.isNotNullAndNotEmpty(sActualType)) {
			for (CanonicalPacket packet : canonicalPackets.getCanonicalPacket()) {
				List<ActualPacket> actualPackets = packet.getActualPackets().getActualPacket();
				for (ActualPacket ap : actualPackets) {
					if (doesTypeExist(ap, sActualType))
						return packet;
				}
			}
		}
		return canonicalPacket;
	}

	/**
	 * @description: This is a sub method to get the Type specific Packet Name - It
	 *               checks if the object Type is handled by our Packets
	 * @param sActualType - Type of the object
	 * @return boolean
	 */
	public boolean doesTypeExist(ActualPacket packet, String actualType) {
		boolean result = false;
		if (null != packet) {
			ObjectTypes packetTypes = packet.getObjectTypes();
			for (String type : packetTypes.getObjectType()) {
				if (actualType.equalsIgnoreCase(type)) {
					result = true;
					break;
				}
			}
		}
		return result;
	}

	/**
	 * @description: This method is used to extract the Metadata for each input
	 *               entry
	 * 
	 * @param context sPacketName : Type specific Packet Name sType : Object type
	 *                sName : Object name sRevision : Object revision
	 * @return String
	 * @throws Exception
	 */
	private String extractMetaData(Context context, String sPacketName, String Type, String sName, String sRev)
			throws Exception {
		String sMetaDataOutput = DomainConstants.EMPTY_STRING;
		try {
			String sTarget = pgV3Constants.KEY_TARGET;
			PDFToolDispatcher dhp = new PDFToolDispatcher(sPacketName, sTarget, Type, sName, sRev);
			sMetaDataOutput = dhp.getDataHandler(context, false);

			if (UIUtil.isNotNullAndNotEmpty(sMetaDataOutput)) {
				System.out.println(pgV3Constants.MSG_SUCCESSFULL_EXTRACTION_METADATA);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return sMetaDataOutput;
	}

	public static String getPDFViewTableValuesForListbox(Context context, String[] args) throws Exception {
		StringBuilder sbuffer = new StringBuilder();

		try {
			setServerPath(context);
			HashMap programMap = (HashMap) JPO.unpackArgs(args);
			HashMap requestMap = (HashMap) programMap.get("requestMap");

			String strProductType = (String) requestMap.get(DomainConstants.SELECT_TYPE);
			String sName = (String) requestMap.get(DomainConstants.SELECT_NAME);
			String sRevision = (String) requestMap.get(DomainConstants.SELECT_REVISION);

			String[] arguments = new String[3];
			AWLPropertyUtil.getI18NString(context, "emxCommonButton.Cancel");
			arguments[0] = strProductType;
			arguments[1] = sName;
			arguments[2] = sRevision;
			PDFViewController cont = new PDFViewController();
			String strPDFViewKeys = cont.getTableNames(context, arguments);
			String sToolNamesKey = "emxCPN.PDFGenerationTool.ViewKeys";
			String sToolValueKey = "emxCPN.PDFGenerationTool.DisplayViewValue";
			
			String strPDFViewTableKeys = EnoviaResourceBundle.getProperty(context, "emxCPNStringResource",
					context.getLocale(), sToolNamesKey);
			String strPDFViewTableDisplayvalue = EnoviaResourceBundle.getProperty(context, "emxCPNStringResource",
					context.getLocale(), sToolValueKey);
			StringList slPDFViewKeys = FrameworkUtil.split(strPDFViewKeys, pgV3Constants.SYMBOL_COMMA);

			StringList slPDFViewPropertiesKeys = FrameworkUtil.split(strPDFViewTableKeys, pgV3Constants.SYMBOL_COMMA);
			StringList slPDFViewTableDisplayvalue = FrameworkUtil.split(strPDFViewTableDisplayvalue,
					pgV3Constants.SYMBOL_COMMA);
			HashMap pairMap = new HashMap();
			String optionKey = null;
			String optionValue = null;
			Iterator itr1 = slPDFViewPropertiesKeys.iterator();
			Iterator itr3 = slPDFViewTableDisplayvalue.iterator();
			while (itr1.hasNext()) {
				optionKey = (String) itr1.next();
				optionValue = (String) itr3.next();
				optionValue = optionValue.replaceAll(pgV3Constants.SYMBOL_UNDERSCORE, pgV3Constants.SYMBOL_COMMA);
				pairMap.put(optionKey, optionValue);
			}
			String optionID = null;
			sbuffer.append("<select id=\"");
			sbuffer.append("PDFViewTable");
			sbuffer.append("\" name=\"PDFViewTable\" onchange=\"isAllDataSelect()\" size=\"7\" multiple=\"multiple\">");
			Iterator itr2 = slPDFViewKeys.iterator();

			while (itr2.hasNext()) {
				optionID = (String) itr2.next();
				optionValue = pairMap.containsKey(optionID) ? (String) pairMap.get(optionID)
						: DomainConstants.EMPTY_STRING;
				if (!UIUtil.isNullOrEmpty(optionID) && !UIUtil.isNullOrEmpty(optionValue)) {
					sbuffer.append("<option value=\"" + optionID + "\">" + optionValue + "</option>");
				}
			}
			sbuffer.append("</select>");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return sbuffer.toString();
	}

	/**
	 * 
	 * @param context
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public String deleteTempFolder(Context context, String args[]) throws Exception {
		String strPathToDel = args[0];
		boolean bDel = deleteDir(new File(strPathToDel));
		if (bDel) {
			return pgV3Constants.CONST_TRUE;
		} else {
			return pgV3Constants.FALSE;
		}
	}

	/**
	 * 
	 * @param dir
	 * @return
	 */
	public static boolean deleteDir(File dir) {
		boolean success = false;
		if (dir.isDirectory()) {
			String[] children = dir.list();
			int ichildrenlength=children.length;
			for (int i = 0; i < ichildrenlength; i++) {
				success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}

	public static void setServerPath(Context context) {
		String strServerPath = EnoviaResourceBundle.getProperty(context, "emxCPN", context.getLocale(),"emxCPN.ServerPath");
		String strPDFGenerationToolBasePath = strServerPath + java.io.File.separator + PDFConstant.SERVER_PATH;
	}
}
