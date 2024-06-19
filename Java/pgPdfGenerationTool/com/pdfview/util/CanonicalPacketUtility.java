/*
Java File Name: CanonicalPacketUtility
Clone From/Reference: NA
Purpose: This file is used to load CanonicalPacket
*/

package com.pdfview.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pdfview.canonicalPacket.ActualPacket;
import com.pdfview.canonicalPacket.CanonicalPacket;
import com.pdfview.canonicalPacket.CanonicalPackets;
import com.pdfview.canonicalPacket.ObjectTypes;
import com.pdfview.exception.PDFToolCustomException;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.MQLCommand;
import matrix.util.MatrixException;

public class CanonicalPacketUtility {

	/**
	 * @description: Method to load Canonical file
	 * 
	 * @return CanonicalPackets
	 * @throws PDFToolCustomException
	 */
	public CanonicalPackets loadCanonicalFile(Context context) throws PDFToolCustomException {
		CanonicalPackets canonicalPackets = null;
		String canonicalFilePath = AppPathUtility.getConfigDirectory(context) + "/ConfigurationFiles/CanonicalPackets.xml";
		try {
			Class classObj = ReflectionUtility.getClass("com.pdfview.canonicalPacket.CanonicalPackets");
			canonicalPackets = (CanonicalPackets) XMLUtility.unmarshallXMLFile(canonicalFilePath, classObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return canonicalPackets;
	}

	/**
	 * @description: Method to get Canonical Packet
	 * 
	 * @param packetName
	 * @return CanonicalPacket
	 * @throws PDFToolCustomException
	 */
	public CanonicalPacket getCanonicalPacket(Context context,String packetName) throws PDFToolCustomException {
		CanonicalPacket canonicalPacket = null;
		CanonicalPackets canonicalPackets = loadCanonicalFile(context);
		if (null != canonicalPackets && DHUtility.isValidText(packetName)) {
			for (CanonicalPacket packet : canonicalPackets.getCanonicalPacket()) {
				if (packetName.equalsIgnoreCase(packet.getCanonicalPacketName())) {
					canonicalPacket = packet;
					return packet;
				} else {
					List<ActualPacket> actualPackets = packet.getActualPackets().getActualPacket();
					for (ActualPacket ap : actualPackets) {
						if (ap.getActualPacketName().equals(packetName))
							return packet;
					}
				}
			}
		}
		return canonicalPacket;
	}

	/**
	 * @description: Method to get Actual Packet
	 * 
	 * @param List
	 * @return List
	 */
	public static List<ActualPacket> getActualPacketList(CanonicalPacket canonicalPacket) {
		List<ActualPacket> actualPacketNameList = null;
		if (null != canonicalPacket) {
			actualPacketNameList = canonicalPacket.getActualPackets().getActualPacket();
		}
		return actualPacketNameList;

	}

	/**
	 * @description: Method to get Actual Packet
	 * 
	 * @param actualPacketList
	 * @param actualPacketName
	 * @return ActualPacket
	 */
	public static ActualPacket getActualPacket(List<ActualPacket> actualPacketList, String actualPacketName) {
		ActualPacket actualPacket = null;
		if (DHUtility.validateList(actualPacketList) && DHUtility.isValidText(actualPacketName)) {
			for (ActualPacket packet : actualPacketList) {
				if (packet.getActualPacketName().equalsIgnoreCase(actualPacketName)) {
					actualPacket = packet;
					break;
				}
			}
		}
		return actualPacket;
	}

	/**
	 * @description: Method to check is type exists
	 * 
	 * @param packet
	 * @param actualType
	 * @return boolean
	 */
	public boolean isTypeExist(ActualPacket packet, String actualType) {
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
	 * @description: Method to get actual packet name
	 * @param cp
	 * @param actualType
	 * @return String
	 * @throws PDFToolCustomException
	 */
	public String actualPacketName(CanonicalPacket cp, String actualType) throws PDFToolCustomException {

		List<ActualPacket> actualPacketList = cp.getActualPackets().getActualPacket();
		String actualPacketName = DomainConstants.EMPTY_STRING;
		if (null != actualPacketList) {
			for (ActualPacket packet : actualPacketList) {
				if (isTypeExist(packet, actualType)) {
					actualPacketName = packet.getActualPacketName();
					break;
				}
			}
		}
		return actualPacketName;
	}

	/**
	 * @description: Method to get business object
	 * @param context
	 * @param pType
	 * @param pName
	 * @param pRev
	 * @param pVault
	 * @return BusinessObject
	 * @throws PDFToolCustomException
	 * @throws MatrixException
	 */
	public BusinessObject getBusinessObject(Context context, String pType, String pName, String pRev, String pVault)
			throws MatrixException, PDFToolCustomException {

		boolean isInvalidType = false;
		BusinessObject bo = null;
		List<String> typeLst = new ArrayList<String>();
		typeLst.add(pType);
		String objectId = getObject(context, typeLst, pName, pRev);
		if (UIUtil.isNotNullAndNotEmpty(objectId)) {

			bo = new BusinessObject(objectId);
			bo.open(context);
		} else {
			isInvalidType = true;
		}

		if (isInvalidType) {

			throw new PDFToolCustomException(
					"No data found for type:" + pType + " ,name:" + pName + " ,revision:" + pRev, "E021");
		}

		return bo;
	}

	/**
	 * @description: Method to get object
	 * @param context
	 * @param typeNameLst
	 * @param pName
	 * @param pRev
	 * @return String
	 * @throws PDFToolCustomException
	 * @throws MatrixException
	 */
	public String getObject(Context context, List<String> typeNameLst, String pName, String pRev)
			throws MatrixException, PDFToolCustomException {

		String objectId = DomainConstants.EMPTY_STRING;
		String query = createTempQuery(typeNameLst, pName, pRev);

		MQLCommand myCmd = new MQLCommand();
		myCmd.open(context);
		myCmd.executeCommand(context, query);

		String[] listOfRecords = myCmd.getResult().trim().split(pgV3Constants.SYMBOL_NEXT_LINE);

		int size = listOfRecords.length;

		if (size > 1) {
			listOfRecords = prioritizeResults(listOfRecords, pRev);
			if (listOfRecords == null) {
				throw new PDFToolCustomException(
						"Caught Exception in CanonicalPacketUtility.getObject(): null list returned prioritizing results!",
						"E701");
			}
			size = listOfRecords.length;
		}

		if (size == 1) {
			String row = listOfRecords[0].trim();

			StringTokenizer tnr = new StringTokenizer(row, pgV3Constants.SYMBOL_PIPE);

			List<String> list = new ArrayList<>();
			while (tnr.hasMoreTokens()) {
				list.add(tnr.nextToken());
			}
			if (list.size() > 1) {
				if (list.size() == 5) {
					objectId = list.get(3);
				} else if (list.size() == 6) {
					String sLastState = list.get(3);
					String sLastId = list.get(4);
					if (sLastState.equals(pgV3Constants.STATE_RELEASE)) {
						objectId = sLastId;
					}
				} else if (list.size() == 8) {
					String sLastState = list.get(3);
					String sLastId = list.get(4);
					String sSecondToLastState = list.get(5);
					String sSecondToLastId = list.get(6);

					if (sLastState.equals(pgV3Constants.STATE_RELEASE)) {
						objectId = sLastId;
					} else if (sSecondToLastState.equals(pgV3Constants.STATE_RELEASE)) {
						objectId = sSecondToLastId;
					}
				}
			}
		} else if (size > 1) {
			StringBuffer sbResults = new StringBuffer();
			int ilistOfRecordslength=listOfRecords.length;
			for (int x = 0; x < ilistOfRecordslength; x++) {
				String sRow = listOfRecords[x];
				if (sRow == null)
					sRow = DomainConstants.EMPTY_STRING;
				sbResults.append("[" + sRow + "] ");
			}
			throw new PDFToolCustomException(
					"More than one row for Query:  (" + query + ") - Results:  " + sbResults.toString(), "E007");
		}

		return objectId;
	}

	/**
	 * @description: Method to create temp query
	 * @param typeList
	 * @param pName
	 * @param pRev
	 * @return String
	 */
	private String createTempQuery(List<String> typeList, String pName, String pRev) {

		String query = "temp query bus '";

		int size = typeList.size();

		boolean bSkipOrigSourceCheck = false;
		String sEnoviaType = DomainConstants.EMPTY_STRING;
		for (int i = 0; i < size; i++) {
			sEnoviaType = typeList.get(i).trim();
			if (sEnoviaType.equals("ECO") || sEnoviaType.equals("Change Request") || sEnoviaType.equals("Change Order")
					|| sEnoviaType.equals("Change Action") || sEnoviaType.equals("Product Line")
					|| sEnoviaType.equals("Product Configuration") || sEnoviaType.equals("CPG Product")
					|| sEnoviaType.equals("Logical Feature") || sEnoviaType.equals("Configuration Feature")
					|| sEnoviaType.equals("Configuration Option") || sEnoviaType.equals("POA")
					|| sEnoviaType.equals("Substance") || sEnoviaType.equals("Internal Material")
					|| sEnoviaType.equals("Qualification") || sEnoviaType.equals("pgPPMConstituent")) {
				bSkipOrigSourceCheck = true;
			}
			query = query + sEnoviaType;
			if (i < (size - 1))
				query = query + pgV3Constants.SYMBOL_COMMA;
		}

		String postTag = DomainConstants.EMPTY_STRING;

		if (DHUtility.isEmptyText(pRev)) {

			if (bSkipOrigSourceCheck) {
				postTag = "' '" + pName + "' * !expand ";
				postTag += "select last.current last.id last.previous.current last.previous.id policy dump |;";
			} else {
				postTag = "' '" + pName
						+ "' * !expand where \"attribute[pgOriginatingSource]=='DSO' || attribute[pgOriginatingSource]~='CSS*' \" ";
				postTag += "select last.current last.id last.previous.current last.previous.id attribute[pgOriginatingSource] dump |;";
			}

		} else {

			if (bSkipOrigSourceCheck) {
				postTag = "' '" + pName + "' '" + pRev + "' !expand ";
				postTag += "select id policy dump |;";
			} else {
				postTag = "' '" + pName + "' '" + pRev
						+ "' !expand where \"attribute[pgOriginatingSource]=='DSO' || attribute[pgOriginatingSource]~='CSS*' \" ";
				postTag += "select id attribute[pgOriginatingSource] dump |;";
			}
		}

		query = query + postTag;

		return query;
	}

	/**
	 * @description: Method to prioritize results
	 * @param listOfRecords
	 * @param pRev
	 * @return String[]
	 * @throws PDFToolCustomException
	 */
	private String[] prioritizeResults(String[] listOfRecords, String pRev) throws PDFToolCustomException {

		String[] stringArrReturn = null;

		try {

			List<String> fixedList = Arrays.asList(listOfRecords);
			List<String> slOriginalList = new ArrayList<String>(fixedList);

			boolean bHasDSORecords = false;

			if (DHUtility.isEmptyText(pRev)) {

				List<String> slOnlyOneRev = new ArrayList<String>();
				List<String> slOnlyMultipleRev = new ArrayList<String>();
				List<String> slCombinedList = new ArrayList<String>();

				Vector<String> vDuplicateCheck = new Vector<String>();

				for (Iterator<String> iterator = slOriginalList.iterator(); iterator.hasNext();) {

					String row = iterator.next();

					StringTokenizer tnr = new StringTokenizer(row, pgV3Constants.SYMBOL_PIPE);
					if (tnr.countTokens() == 6) {

						if (row.indexOf(pgV3Constants.STATE_RELEASE) > 0) {
							if (row.trim().endsWith(pgV3Constants.DSM_ORIGIN))
								bHasDSORecords = true;
							slOnlyOneRev.add(row);
						}
					} else if (tnr.countTokens() == 8) {

						if (row.indexOf(pgV3Constants.STATE_RELEASE) > 0) {
							if (row.trim().endsWith(pgV3Constants.DSM_ORIGIN))
								bHasDSORecords = true;

							String sRowMinusRev = DomainConstants.EMPTY_STRING;
							int iRevCounter = 0;
							while (tnr.hasMoreTokens()) {
								iRevCounter++;
								String sToken = tnr.nextToken().trim();
								if (iRevCounter == 3 || iRevCounter == 8) { // remove rev and originating source from
																			// key for duplicate check

								} else {

									sRowMinusRev += sToken + pgV3Constants.SYMBOL_PIPE;
								}
							}

							if (!vDuplicateCheck.contains(sRowMinusRev)) {
								vDuplicateCheck.add(sRowMinusRev);
								slOnlyMultipleRev.add(row);
							}
						}
					}
				}

				if (bHasDSORecords) {

					for (Iterator<String> iterator = slOnlyOneRev.iterator(); iterator.hasNext();) {
						String row = iterator.next();
						if (!row.trim().endsWith(pgV3Constants.DSM_ORIGIN)) {
							iterator.remove();
						}
					}
					for (Iterator<String> iterator = slOnlyMultipleRev.iterator(); iterator.hasNext();) {
						String row = iterator.next();
						if (!row.trim().endsWith(pgV3Constants.DSM_ORIGIN)) {
							iterator.remove();
						}
					}
				}

				if (slOnlyOneRev.size() > 0) {
					for (Iterator<String> iterator = slOnlyOneRev.iterator(); iterator.hasNext();) {
						String row = iterator.next();
						slCombinedList.add(row);
					}
				}

				if (slOnlyMultipleRev.size() > 0) {
					for (Iterator<String> iterator = slOnlyMultipleRev.iterator(); iterator.hasNext();) {
						String row = iterator.next();
						slCombinedList.add(row);
					}
				}

				stringArrReturn = slCombinedList.toArray(new String[] {});

			} else {

				for (Iterator<String> iterator = slOriginalList.iterator(); iterator.hasNext();) {
					String row = iterator.next();
					if (row.trim().endsWith(pgV3Constants.DSM_ORIGIN)) {
						bHasDSORecords = true;
						break;
					}
				}

				if (bHasDSORecords) {

					for (Iterator<String> iterator = slOriginalList.iterator(); iterator.hasNext();) {
						String row = iterator.next();
						if (!row.trim().endsWith(pgV3Constants.DSM_ORIGIN)) {
							iterator.remove();
						}
					}
				}

				stringArrReturn = slOriginalList.toArray(new String[] {});

			}

		} catch (Exception e) {

			throw new PDFToolCustomException(
					"Caught Exception in CanonicalPacketUtility.prioritizeResults():  " + e.toString(), "E700");
		}

		return stringArrReturn;

	}

}
