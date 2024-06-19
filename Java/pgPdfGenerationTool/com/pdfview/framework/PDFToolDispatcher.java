/*
Java File Name: DataHandlerDispatcher
Clone From/Reference: NA
Purpose:  This File is used for to Find the Canonical Packet
*/

package com.pdfview.framework;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pdfview.canonicalPacket.CanonicalPacket;
import com.pdfview.exception.PDFToolBusyException;
import com.pdfview.exception.PDFToolCustomException;
import com.pdfview.exception.PDFToolWebServiceException;
import com.pdfview.registry.RegisteredItem;
import com.pdfview.util.CanonicalPacketUtility;
import com.pdfview.util.RegistryUtility;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.util.MatrixException;

public class PDFToolDispatcher {

	private String _sPacketName = DomainConstants.EMPTY_STRING;
	private String _sType = DomainConstants.EMPTY_STRING;
	private String _sName = DomainConstants.EMPTY_STRING;
	private String _sRevision = DomainConstants.EMPTY_STRING;
	private List<String> componentList = null;

	public PDFToolDispatcher(String sPacketName, String sDataTarget, String sType, String sName, String sRev) {

		_sPacketName = sPacketName;
		_sType = sType;
		_sRevision = sRev;
		_sName = sName;
	}

	/**
	 * @description: This method is used to find the Packet corresponding to type
	 * 
	 * @param Context
	 * @param         boolean
	 * @return String
	 * @throws Exception
	 */
	public String getDataHandler(Context ctxApplicationUser, boolean b)
			throws JAXBException, ClassNotFoundException, NoSuchMethodException, InstantiationException,
			IllegalAccessException, InvocationTargetException, SecurityException, IllegalArgumentException,
			NoSuchFieldException, MatrixException, SQLException, IOException, PDFToolWebServiceException,
			PDFToolBusyException, TransformerException, PDFToolCustomException {

		String sResults = DomainConstants.EMPTY_STRING;

		CanonicalPacketUtility cpUtil = new CanonicalPacketUtility();
		CanonicalPacket cp = cpUtil.getCanonicalPacket(ctxApplicationUser,_sPacketName);
		if (null == cp) {
			throw new PDFToolCustomException("Invalid Packet Name", "E016");
		}

		RegisteredItem ri = null;
		if (!cp.getCanonicalPacketName().equals(_sPacketName))
			ri = RegistryUtility.findEntry(ctxApplicationUser,_sPacketName);
		else {
			ri = RegistryUtility.findEntry(ctxApplicationUser,cpUtil.actualPacketName(cp, _sType));
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
		PDFToolConfigurator dh = null;
		dh = new PDFToolEnoviaConfigurator(ctxApplicationUser, bo, ri);
		dh.setRequestedComponents(componentList);
		sResults = dh.generateXML();
		return sResults;
	}
}
