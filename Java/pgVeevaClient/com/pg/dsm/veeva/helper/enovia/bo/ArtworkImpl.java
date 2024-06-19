/*
 **   ArtworkImpl.java
 **   Description - Introduced as part of Veeva integration.      
 **   Artwork implementation utility.
 **
 */
package com.pg.dsm.veeva.helper.enovia.bo;

import java.io.File;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.eMatrixDateFormat;
import com.matrixone.apps.domain.util.i18nNow;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.veeva.util.Veeva;
import com.pg.dsm.veeva.util.SymbolicName;
import com.pg.dsm.veeva.vql.json.binder.document_property.DocumentProperty;
import com.pg.dsm.veeva.vql.xml.binder.bo.Select;

import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.util.StringList;

public class ArtworkImpl implements Veeva {
	private final Logger logger = Logger.getLogger(this.getClass().getName());
	public ArtworkImpl() {
	}
	/**
	 * @param context: matrix context
	 * @param properties: holds veeva properties
	 * @param documentProperty: holds document properties
	 * @param selects: holds veeva document selectables mapping
	 * @return String artwork id
	 * @throws Exception
	 * Creates artwork and updates artwork attributes
	 * DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
	 */
	public String createArtwork(Context context, Properties properties, DocumentProperty documentProperty,
			List<Select> selects) throws Exception {
		logger.info("Entered method createArtwork:");
		String sArtworkId = EMPTY_STRING;

		String strGCAS = documentProperty.getPmp();

		if (UIUtil.isNotNullAndNotEmpty(strGCAS)) {	
				sArtworkId = FrameworkUtil.autoName(context, SymbolicName.ARTWORK.getType(context), EMPTY_STRING,
						SymbolicName.IPMSPECIFICATION.getPolicy(context), SymbolicName.ESERVICEPRODUCTION.getVault(context),
						EMPTY_STRING, true);

				DomainObject domArt = DomainObject.newInstance(context, sArtworkId);
				domArt.setOwner(context, Veeva.USER_CORPORATE);
				domArt.setPrimaryOwnership(context, Veeva.PROJECT, Veeva.ORGANIZATION);
			    updateArtworkAttributes(context, properties, documentProperty, selects, sArtworkId);
		}
		logger.info("Exit method createArtwork:");
		return sArtworkId;
	}


	/**
	 * @param context: Matrix Context
	 * @param properties: Veeva Properties
	 * @param documentProperty: Veeva Document Properties
	 * @param selects: Veeva Documents Selects
	 * @param artworkId: Artwork Object Id
	 * @throws Exception
	 * This method artwork object attributes
	 */
	public void updateArtworkAttributes(Context context, Properties properties, DocumentProperty documentProperty,
			List<Select> selects, String artworkId) throws Exception {
		logger.info("Entered method updateArtworkAttributes:");
		DomainObject domArt = DomainObject.newInstance(context, artworkId);
		Map<String, String> attribMap = new HashMap<String, String>();
		Map<String, Method> methodMap = new HashMap<String, Method>();
		Method[] declaredMethods = DocumentProperty.class.getDeclaredMethods();
		for (Method method : declaredMethods) {
			if (method.isAnnotationPresent(JsonGetter.class)) {
				String annotationValue = method.getAnnotation(JsonGetter.class).value();
				methodMap.put(annotationValue, method);
			}
		}
		// do the attribute mapping from xml
		Iterator<?> itr = selects.iterator();
		while (itr.hasNext()) {
			Select select = (Select) itr.next();
			String mappingType = select.getType();
			String mappingKey = select.getKey();
			String mappingName = select.getMappingName();
			Method method = (Method) methodMap.get(mappingKey);
			String result = (String) method.invoke(documentProperty);

			if (UIUtil.isNotNullAndNotEmpty(result)) {
				if (STR_BASIC.equals(mappingType)) {
					Method busMethod = domArt.getClass().getMethod(select.getMethod(), Context.class, String.class);
					busMethod.invoke(domArt, context, result);
				}
				if (STR_ATTRIBUTE.equals(mappingType)) {
					attribMap.put(mappingName, result);
				}
			}
		}
		attribMap.put(ATTRIBUTE_ORIGINATOR, context.getUser());
		attribMap.put(ATTRIBUTE_PGSAPTYPE, properties.getProperty("veeva.artwork.attribute.pgsaptype.default"));
		attribMap.put(ATTRIBUTE_PGCSSTYPE, properties.getProperty("veeva.artwork.attribure.pgcsstype.default"));
		attribMap.put(ATTRIBUTE_PGORIGINATINGSOURCE,
				properties.getProperty("veeva.artwork.attribute.pgorginatedsource.default"));
		attribMap.put(ATTRIBUTE_RELEASE_PHASE, Veeva.ATTRIBUTE_STAGE_PRODUCTION_VALUE);
		String sCurrentDate = getCurrentDate(context);
		attribMap.put(Veeva.ATTRIBUTE_RELEASE_DATE, sCurrentDate);
		attribMap.put(Veeva.ATTRIBUTE_EFFECTIVITYDATE, sCurrentDate);

		domArt.setAttributeValues(context, attribMap);
		logger.info("Artwork attribute Map---" + attribMap);
		logger.info("Exit method updateArtworkAttributes:");

	}

	/**
	 * @param context: matrix context
	 * @return String current date
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public String getCurrentDate(Context context) throws Exception {
		String strRelease_And_Effectivity_Date = EMPTY_STRING;
		try {
			Calendar c = Calendar.getInstance();
			String strTZ = i18nNow.getI18nString("emxCPN.ePADEx.America", "emxCPNStringResource",
					context.getSession().getLanguage());
			TimeZone tz = TimeZone.getTimeZone(strTZ);
			SimpleDateFormat formatter = new SimpleDateFormat(eMatrixDateFormat.getEMatrixDateFormat());
			formatter.setTimeZone(tz);
			strRelease_And_Effectivity_Date = formatter.format(c.getTime());
		} catch (Exception e) {
			logger.error("Exception in ArtworkImpl getCurrentDate method "+e);
			e.printStackTrace();
		}
		return strRelease_And_Effectivity_Date;
	}
	/**
	 * @param context:           matrix context
	 * @param artworkObjId:      holds artwork object id
	 * @param veevaProperties:   holds veeva properties
	 * @param renditionFiles:    holds the redition files of veeva document
	 * @param renditionFilePath: holds the rendition file path
	 * @param documentID:        holds the veeva document number
	 * @throws Exception Creates the IPMDocuments with checkedin rendition file and
	 *                   connects artwork object
	 */
	public void createAndConnectIPMDoc(Context context, String artworkObjId, Properties veevaProperties,
			File[] renditionFiles, String renditionFilePath, String documentID) throws Exception {
		logger.info("Entered createAndConnectIPMDoc Method");
		try {
			DomainObject domArtwork = DomainObject.newInstance(context, artworkObjId);
			String artName = domArtwork.getInfo(context, DomainConstants.SELECT_NAME);
			String strRenditionName = new StringBuffer().append(Veeva.RENDITION_PREFIX).append(artName)
					.append(Veeva.SYMBOL_DOT).append(Veeva.RENDITION_SUFIX).toString();
			DomainObject docObj = DomainObject.newInstance(context, Veeva.TYPE_PGIPMDOCUMENT);
			docObj.createObject(context, Veeva.TYPE_PGIPMDOCUMENT, strRenditionName, Veeva.RENDITION_REV,
					Veeva.POLICY_PGIPMDOCUMENT, Veeva.VAULT_ESERVICEPRODUCTION);
			docObj.setPrimaryOwnership(context, Veeva.PROJECT, Veeva.ORGANIZATION);
			DomainObject dobject = DomainObject.newInstance(context, new BusinessObject(Veeva.TYPE_PGIPMDOCUMENT,
					strRenditionName, Veeva.RENDITION_REV, Veeva.VAULT_ESERVICEPRODUCTION));
			if(dobject.exists(context)) {
				String strDocId = dobject.getInfo(context, DomainConstants.SELECT_ID);
				logger.info("IPMDoc created document Id " + strDocId);				
				DomainRelationship.connect(context, artworkObjId, Veeva.RELATIONSHIP_REFERENCEDOCUMENT, strDocId, true);
				logger.info("IPMDoc connected to artwork");
				checkinRenditions(context, renditionFiles, renditionFilePath, docObj, documentID);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception in ArtworkImpl createAndConnectIPMDoc method "+e);
			throw e;
		}
		logger.info("Exit createAndConnectIPMDoc Method");
	}

	/** 
	 * @about method to perform checkin
	 * @param  Context - object
	 * @param  File[] - files
	 * @param String - path
	 * @param DomainObject - object
	 * @param String - doc number
	 * @throws Exception
	 * @return void
	 * @since DSM 2018x.3
	 */
	public void checkinRenditions(Context context, File[] renditionFiles, String renditionFilePath, DomainObject domIPMDoc, String sDocNumber) throws Exception {
		logger.info("Entered checkinRenditions Method");
		try {
			int count = 0;
			File file;
			File actualFile;
			StringBuffer sbReplacePrefix = new StringBuffer();
			sbReplacePrefix.append(sDocNumber).append(STR_RENDITION_PREFIX);
			while (count < renditionFiles.length) {
				file = new File(renditionFiles[count].getPath());
				String fileName = file.getName().replaceAll(sbReplacePrefix.toString(), Veeva.EMPTY_STRING);
				actualFile = new File(
						file.getCanonicalPath().replaceAll(sbReplacePrefix.toString(), Veeva.EMPTY_STRING));
				if (file.renameTo(actualFile)) {
					logger.info("Rendition name successfully modified to "
							+ file.getCanonicalPath().replaceAll(sbReplacePrefix.toString(), Veeva.EMPTY_STRING));

				}
				count++;
				domIPMDoc.checkinFile(context, false, false, DomainConstants.EMPTY_STRING, FORMAT_GENERIC, fileName,
						renditionFilePath);
				logger.info("Checkin in successfull for " + fileName);
			}
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception in rendition checkin" + e);
			throw e;
		}
		logger.info("Exit checkinRenditions Method");
	}
}
