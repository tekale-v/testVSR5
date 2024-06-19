/*
Java File Name: DataHandlerEnoviaConfigurator
Clone From/Reference: NA
Purpose:  This File is used to Configure Enovia Settings from Input & to Output files
*/

package com.pdfview.framework;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pdfview.combinedcomponent.definition.Component;
import com.pdfview.combinedcomponent.definition.Elements;
import com.pdfview.combinedelement.definition.DetailedElement;
import com.pdfview.constant.PDFConstant;
import com.pdfview.exception.PDFToolCustomException;
import com.pdfview.exception.PDFToolFieldIsListException;
import com.pdfview.exception.PDFToolWebServiceException;
import com.pdfview.helper.EnoviaHelper;
import com.pdfview.helper.StringHelper;
import com.pdfview.registry.RegisteredItem;
import com.pdfview.util.AppPathUtility;
import com.pdfview.util.DHUtility;
import com.pdfview.util.PDFToolConfigUtility;
import com.pdfview.util.ReflectionUtility;
import com.pdfview.util.XMLUtility;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.BusinessObject;
import matrix.db.BusinessObjectWithSelect;
import matrix.db.Context;
import matrix.db.ExpansionIterator;
import matrix.db.RelationshipWithSelect;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class PDFToolEnoviaConfigurator implements PDFToolConfigurator {


	private String _startTag = DomainConstants.EMPTY_STRING;
	private Object _configuration = null;
	public HashMap<String, Object> _elements = new HashMap<String, Object>();
	public HashMap<String, Object> _components = new HashMap<String, Object>();
	private String _oid = DomainConstants.EMPTY_STRING;
	private Context _context = null;
	private BusinessObject _bo;
	private List<String> _lRelationshipsExpandedOver = new ArrayList<String>();
	private List<String> _lRecursionDetection = new ArrayList<String>();
	private List<String> _alRequestedComponents = null;
	private List<String> _lComponentSelected = new ArrayList<String>();

	/**
	 * Gets the value of the _alRequestedComponents property.
	 * 
	 * @return List
	 */
	public List<String> getAlRequestedComponents() {
		return _alRequestedComponents;
	}

	/**
	 * Sets the value of the _alRequestedComponents property.
	 * 
	 * @param value List
	 */
	public void setRequestedComponents(List<String> alRequestedComponents) {
		this._alRequestedComponents = alRequestedComponents;
	}

	/**
	 * @description: The jaxb class defines what the output xml will look likee
	 * 
	 * @param context
	 * @param bo
	 * @param ri
	 * @return DataHandlerEnoviaConfigurator Object
	 * @throws IOException
	 * @throws MatrixException
	 * @throws JAXBException
	 * @throws PDFToolCustomException
	 * @throws PDFToolWebServiceException
	 */
	public PDFToolEnoviaConfigurator(Context context, BusinessObject bo, RegisteredItem ri) throws IOException,
			MatrixException, JAXBException, PDFToolWebServiceException, PDFToolCustomException {

		String sVault = (String) _components.get("enoviaVault");
		if (UIUtil.isNullOrEmpty(sVault))
			sVault = "eService Production";

		_context = context;
		_bo = bo;
		bo.open(_context);
		_startTag = StringUtils.substringAfterLast(ri.getJaxbCLass(), pgV3Constants.SYMBOL_DOT);

		_components = PDFToolConfigUtility.getComponentHashMap(AppPathUtility.getConfigDirectory(context),
				ri.getComponentConfigurationFile());

		_elements = PDFToolConfigUtility.getDetailedElementHashMap(AppPathUtility.getConfigDirectory(context),
				(String) _components.get("elementConfigurationFiles"));

		
		String oid = bo.getObjectId();
		_oid = oid;
		bo.close(_context);

	}

	/**
	 * @description: Method to generated xml content form processed object
	 * 
	 * @return String
	* @throws ClassNotFoundException
	* @throws NoSuchMethodException
	* @throws SecurityException
	* @throws InstantiationException
	* @throws IllegalAccessException
	* @throws IllegalArgumentException
	* @throws InvocationTargetException
	* @throws JAXBException
	* @throws NoSuchFieldException
	* @throws MatrixException
	* @throws TransformerException
	* @throws PDFToolCustomException
	 */
	public String generateXML() throws ClassNotFoundException, NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			JAXBException, NoSuchFieldException, MatrixException, TransformerException, PDFToolCustomException {
		String results = DomainConstants.EMPTY_STRING;

		TreeSet<String> alSelectables = new TreeSet<String>();

		//checkAccessExpression((Component) _components.get(_startTag));

		loadEnoviaComponentExtractDetails((Component) _components.get(_startTag), alSelectables);

		BusinessObject bo = null;

		if (_bo != null)
			bo = _bo;
		else
			bo = new BusinessObject(_oid);
		bo.open(_context);


		StringList slSelectables = StringHelper.convertArrayListToStringList(alSelectables);
		BusinessObjectWithSelect bows = bo.select(_context, slSelectables);
		Component firstTagComponent = (Component) _components.get(_startTag);
		Elements elements = firstTagComponent.getElements();
		List<String> elementWithoutSelectedNames = elements.getElement();
		
		List<String> elementNames = new ArrayList<String>();
		String selectTableList[] = PDFConstant.selectedTableNames.split(pgV3Constants.SYMBOL_COMMA);
		String sTableName = DomainConstants.EMPTY_STRING;
		int iselectTableListlength=selectTableList.length;
		for (int i = 0; i < iselectTableListlength; i++) {
			sTableName = PDFConstant.BASE_TAG + selectTableList[i];
			if (elementWithoutSelectedNames.contains(sTableName))
				elementNames.add(sTableName);
		}
		
		String sCommonTagNames[] = PDFConstant.sCommonTagNames.split(pgV3Constants.SYMBOL_COMMA);
		int iCommonTagNameslength=sCommonTagNames.length;
		for (int i = 0; i < iCommonTagNameslength; i++) {
			sTableName =  PDFConstant.BASE_TAG + sCommonTagNames[i];
			elementNames.add(sTableName);
		}
		elements.setElement(elementNames);

		_configuration = loadEnoviaComponentStructure(firstTagComponent, bows, null, null, null);

		results = XMLUtility.generateXMLString(_configuration);
		results = XMLUtility.prettyFormat(results);
		return results;
	}

	/**
	 * @description: Method to check Access Expression on context user and input object
	 * 
	 * @return String 
	 */
	public String checkTableAccessExpression(Component detailedComponent) {
		Elements elements = detailedComponent.getElements();
		List<String> elementNamesList = elements.getElement();
		int eleSize = elementNamesList.size();
		StringBuilder sTableNamesSB = new StringBuilder();
		String elementName =DomainConstants.EMPTY_STRING;
		String strAccessExpression= DomainConstants.EMPTY_STRING;
		String expression = DomainConstants.EMPTY_STRING;
		boolean isAccess =false;
		DetailedElement deTemp = null;
		for (int i = 0; i < eleSize - 1; i++) {
			elementName = elementNamesList.get(i);
			deTemp = (DetailedElement) _elements.get(elementName);
			strAccessExpression=deTemp.getAccessExpression();
			if (UIUtil.isNotNullAndNotEmpty(strAccessExpression)) {
				expression = deTemp.getAccessExpression();
				isAccess = EnoviaHelper.hasExpressionFilterAccess(_context, _bo, expression);
				if (isAccess) {
					elementName = elementName.replace(PDFConstant.BASE_TAG, DomainConstants.EMPTY_STRING);
					sTableNamesSB.append(elementName + pgV3Constants.SYMBOL_COMMA);
				}
			} else {
				elementName = elementName.replace(PDFConstant.BASE_TAG, DomainConstants.EMPTY_STRING);
				sTableNamesSB.append(elementName + pgV3Constants.SYMBOL_COMMA);
			}
		}
		return sTableNamesSB.toString();
	}
	/**
	 * @description: Method to get user allowed table list 
	 * 
	 * @return String 
	 */
	public String getTableNamesData() {
		return checkTableAccessExpression((Component) _components.get(_startTag));
	}

	/**
	 * @description: Method to load extracted data details
	 * 
	 * @param detailedComponent
	 * @param alSelectables
	 * @return void
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws MatrixException
	 */
	private void loadEnoviaComponentExtractDetails(Component detailedComponent, TreeSet alSelectables)
			throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException,
			InvocationTargetException, SecurityException, NoSuchFieldException, MatrixException {

		if (_lComponentSelected.contains(detailedComponent.getName())) {

		} else {
			_lComponentSelected.add(detailedComponent.getName());
			Elements elements = detailedComponent.getElements();
			List<String> elementNamesList = elements.getElement();
			String strEnoviaMultiValuedSelectable=detailedComponent.getEnoviaMultiValuedSelectable();
			int isaSelectableslength=0;
			if (UIUtil.isNotNullAndNotEmpty(strEnoviaMultiValuedSelectable)) {
				String[] saSelectables = StringUtils.split(detailedComponent.getEnoviaMultiValuedSelectable(), pgV3Constants.SYMBOL_PIPE);
				isaSelectableslength=saSelectables.length;
				for (int i = 0; i < isaSelectableslength; i++) {
					alSelectables.add(saSelectables[i]);
				}

			}
			String strEnoviaSelectable=DomainConstants.EMPTY_STRING;
			for (String elementName : elementNamesList) {
				
				DetailedElement deTemp = (DetailedElement) _elements.get(elementName);
				strEnoviaSelectable=deTemp.getEnoviaSelectable();					
				if (UIUtil.isNotNullAndNotEmpty(strEnoviaSelectable)) {
					String[] saSelectables = StringUtils.split(deTemp.getEnoviaSelectable(), pgV3Constants.SYMBOL_PIPE);

					if (_alRequestedComponents == null) {
						isaSelectableslength=saSelectables.length;
						for (int i = 0; i < isaSelectableslength; i++) {
							alSelectables.add(saSelectables[i]);
						}
					}
					else {
						if (_alRequestedComponents.contains(detailedComponent.getName())) {
							isaSelectableslength=saSelectables.length;
							for (int i = 0; i < isaSelectableslength; i++) {
								alSelectables.add(saSelectables[i]);
							}
						}
					}
				}

				if (deTemp.getSubcomponent() != null && !deTemp.getSubcomponent().equals("")) {
					String sTemp = deTemp.getSubcomponent();
					Component subcomponentDetails = (Component) _components.get(sTemp);
					if (subcomponentDetails.getEnoviaTargetObjectSelectable() != null
							&& subcomponentDetails.getEnoviaTargetObjectSelectable().equals("")) {
						loadEnoviaComponentExtractDetails(subcomponentDetails, alSelectables);
					} else {
						if (_alRequestedComponents == null)
							alSelectables.add(subcomponentDetails.getEnoviaTargetObjectSelectable());
						else {
							if (_alRequestedComponents.contains(detailedComponent.getName()))
								alSelectables.add(subcomponentDetails.getEnoviaTargetObjectSelectable());
						}

					}

				}

			}
		}

	}

	/**
	 * @description: Method will recursively loop over components and their
	 *               subcomponents -defined in a DetailedComponent configuration
	 *               file and populate the jaxb -classes defined by that
	 *               DetailedComponent
	 * 
	 * @param detailedComponent
	 * @param bows
	 * @param parentComponent
	 * @param parentObject
	 * @param currentExpandLevel
	 * @return Object
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws PDFToolFieldIsListException
	 * @throws MatrixException
	 * @throws PDFToolCustomException
	 */

	private Object loadEnoviaComponentStructure(Component detailedComponent, BusinessObjectWithSelect bows,
			Component parentComponent, Object parentObject, String currentExpandLevel) throws ClassNotFoundException,
			NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException,
			SecurityException, NoSuchFieldException, MatrixException, PDFToolCustomException {

		Object results = null;

		String sExpandRel = detailedComponent.getEnoviaExpandRelationship();
		String sMultiValuedSelect = detailedComponent.getEnoviaMultiValuedSelectable();
		String sHelperClass = detailedComponent.getEnoviaHelperClass();

		boolean bIncludeComponent = true;
		if (this._alRequestedComponents != null && !_alRequestedComponents.contains(detailedComponent.getName()))
			bIncludeComponent = false;

		StopWatch sp = new StopWatch();
		sp.start();

		if (UIUtil.isNotNullAndNotEmpty(sHelperClass)) {

			results = ReflectionUtility.executeEnoviaHelperClass(_context, _oid, sHelperClass);

		} else {
			if (UIUtil.isNotNullAndNotEmpty(sExpandRel)) {
				if (bIncludeComponent) {

					loadExpandData(detailedComponent, bows, parentObject, sExpandRel);
				}
			} else if (UIUtil.isNotNullAndNotEmpty(sMultiValuedSelect)) {
				results = loadEnoviaMultiValuedComponent(detailedComponent, bows, parentObject);
			} else {
				results = loadEnoviaSingleValuedComponent(detailedComponent, bows, parentObject, currentExpandLevel);

			}
		}

		return results;

	}

	/**
	 * @description: Method will load the data to expanded
	 * 
	 * @param detailedComponent
	 * @param bows
	 * @param parentComponent
	 * @param parentObject
	 * @param sExpandRel
	 * @return void
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws MatrixException
	 * @throws PDFToolCustomException
	 */
	private void loadExpandData(Component detailedComponent, BusinessObjectWithSelect bows, Object parentObject,
			String sExpandRel) throws MatrixException, ClassNotFoundException, NoSuchMethodException,
			InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchFieldException,
			SecurityException, PDFToolCustomException {
		Context contextTemp = _context.getFrameContext("ExpandContext_");
		contextTemp.start(true);

		TreeSet<String> alSelectables = new TreeSet<String>();

		this._lComponentSelected = new ArrayList<String>();
		loadEnoviaComponentExtractDetails(detailedComponent, alSelectables);

		StringList slSelectables = StringHelper.convertArrayListToStringList(alSelectables);

		String varName = detailedComponent.getJaxbclass();
		varName = varName.substring(varName.lastIndexOf(pgV3Constants.SYMBOL_DOT) + 1, varName.length());

		varName = StringUtils.uncapitalize(varName);
		Field fList = null;

		fList = ReflectionUtility.getListField(parentObject.getClass(), varName);
		if (fList == null) {
			varName = StringUtils.substringAfterLast(detailedComponent.getName(), pgV3Constants.SYMBOL_UNDERSCORE);
			fList = ReflectionUtility.getListField(parentObject.getClass(), varName);
		}

		List multiValuedList = ReflectionUtility.getListInstance(parentObject, varName);

		String componentTypes = detailedComponent.getEnoviaExpandType();

		bows.open(contextTemp);
		boolean bExpandFrom = false;
		boolean bExpandTo = false;
		if (detailedComponent.getEnoviaExpandDirection().equals("from")) {
			bExpandFrom = true;
			bExpandTo = false;
		} else if (detailedComponent.getEnoviaExpandDirection().equals("both")) {
			bExpandFrom = true;
			bExpandTo = true;
		} else {
			bExpandFrom = false;
			bExpandTo = true;
		}

		String sLevel = detailedComponent.getEnoviaExpandRecurseLevel();
		Short shLevel = Short.valueOf(sLevel);
		if (shLevel > 5)
			shLevel = 5;
		if (shLevel < 1)
			shLevel = 1;

		String sRelWhere = DomainConstants.EMPTY_STRING;
		String sParentType = bows.getTypeName();

		if (sExpandRel.indexOf("EBOM") != -1 && sParentType.equals("Finished Product Part")) {
			sRelWhere = "to.type != from.type";
		}

		ExpansionIterator expItr = bows.getExpansionIterator(contextTemp, sExpandRel, componentTypes, slSelectables,
				slSelectables, bExpandTo, bExpandFrom, shLevel, DomainConstants.EMPTY_STRING, sRelWhere, (short) 0, true, false, (short) 0);

		while (expItr.hasNext()) {
			RelationshipWithSelect rel = expItr.next();

			if (_lRelationshipsExpandedOver.contains(rel.getName())) {
				_lRecursionDetection.add(rel.getName());
			} else {
				_lRelationshipsExpandedOver.add(rel.getName());
				BusinessObjectWithSelect bowsTarget = rel.getTarget();
				BusinessObject boSource = rel.getFrom();
				BusinessObjectWithSelect bowsSource = boSource.select(contextTemp, slSelectables);
				bowsTarget.open(contextTemp);

				Object subObject = getSingleExpandRowObject(detailedComponent, parentObject, rel, bowsTarget,
						bowsSource);

				if (multiValuedList != null) {
					if (ReflectionUtility.containsNonNullItems(subObject))
						multiValuedList.add(subObject);
				} else {
					ReflectionUtility.setVariableOnObject(parentObject, varName, subObject);
				}
			}
			_lRelationshipsExpandedOver.add(rel.getName());

		}
		expItr.close();
		contextTemp.commit();
	}

	/**
	 * @description: Method called from loadExpandData
	 * 
	 * @param detailedComponent
	 * @param parentObject
	 * @param rel
	 * @param bowsTarget
	 * @param bowsSource
	 * @return Object
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws InstantiationException
	 * @throws NoSuchFieldException
	 * @throws PDFToolCustomException
	 * @throws MatrixException
	 * 
	 */

	private Object getSingleExpandRowObject(Component detailedComponent, Object parentObject,
			RelationshipWithSelect rel, BusinessObjectWithSelect bowsTarget, BusinessObjectWithSelect bowsSource)
			throws IllegalAccessException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException,
			InstantiationException, NoSuchFieldException, PDFToolCustomException, MatrixException {
		Object subObject = null;
		if (detailedComponent.getJaxbclass().contains("$"))
			subObject = ReflectionUtility.getObjectInstance(parentObject, detailedComponent.getName());
		else
			subObject = ReflectionUtility.getObjectInstance(detailedComponent.getJaxbclass());

		Elements elements = detailedComponent.getElements();
		List<String> listElementNames = elements.getElement();
		int isaLevelslength=0;
		for (String sElementName : listElementNames) {
			DetailedElement deTemp = (DetailedElement) _elements.get(sElementName);

			String[] saLevels = StringUtils.split(deTemp.getEnoviaExpandLevel(), pgV3Constants.SYMBOL_PIPE);
			String sTargetLevel = "" + rel.getLevel();

			boolean bIncludeThisComponent = false;
			if (saLevels != null) {
				isaLevelslength=saLevels.length;
				for (int i = 0; i < isaLevelslength; i++) {
					if (saLevels[i].equals(sTargetLevel)) {
						bIncludeThisComponent = true;
					}
				}
			}

			if (!bIncludeThisComponent && (saLevels != null && saLevels.length > 0))
				continue;

			if (deTemp.getEnoviaSelectableTarget().equals("parent")) {

				String[] saSelectables = StringUtils.split(deTemp.getEnoviaSelectable(), pgV3Constants.SYMBOL_PIPE);
				int isaSelectableslength=saSelectables.length;
				StringList slValue = null;
				for (int i = 0; i < isaSelectableslength; i++) {
					slValue = bowsSource.getSelectDataList(saSelectables[i]);
					if (slValue != null && !slValue.equals(""))
						break;
				}

				StringBuffer sbTemp = new StringBuffer();
				if (slValue != null) {
					Iterator itrValue = slValue.iterator();
					while (itrValue.hasNext()) {
						if (sbTemp.length() > 0)
							sbTemp.append(" | ");

						sbTemp.append(itrValue.next());

					}
				}

				if (!deTemp.getEnoviaHelperMethod().equals("")) {

					ArrayList<String> alHelperMethodArray = new ArrayList();
					String[] saHelperSelectables = StringUtils.split(deTemp.getEnoviaSelectable(), pgV3Constants.SYMBOL_PIPE);
					int isaHelperSelectableslength=saHelperSelectables.length;;
					for (int i = 0; i < isaHelperSelectableslength; i++) {
						StringList slHelperValue = bowsSource.getSelectDataList(saHelperSelectables[i]);
						StringBuffer sbHelperTemp = new StringBuffer();
						if (slHelperValue != null) {
							Iterator itrValue = slHelperValue.iterator();
							while (itrValue.hasNext()) {
								if (sbHelperTemp.length() > 0)
									sbHelperTemp.append(" | ");
								sbHelperTemp.append(itrValue.next());
							}
						}
						alHelperMethodArray.add(sbHelperTemp.toString());
					}
					String value = callHelperMethod(deTemp.getEnoviaHelperMethod(), alHelperMethodArray);
					sbTemp = new StringBuffer(value);
				}
				Object setValue = DHUtility.setDateFormate(deTemp.getElementIsADate(), sbTemp.toString());
				ReflectionUtility.setVariableOnObject(subObject, deTemp.getJaxbElementName(), setValue);
			} else if (deTemp.getEnoviaSelectableTarget().equals("relationship")) {
				setDataFromRelationship(rel, subObject, deTemp);
			} else if (deTemp.getEnoviaSelectableTarget().equals("child")) {

				String[] saSelectables = StringUtils.split(deTemp.getEnoviaSelectable(), pgV3Constants.SYMBOL_PIPE);

				StringList slValue = null;
				StringList slValueToStore = null;
				boolean bValueSet = false;
				int isaSelectableslength=saSelectables.length;
				String sTemp = DomainConstants.EMPTY_STRING;
				for (int i = 0; i < isaSelectableslength; i++) {
					bowsSource.open(_context);
					slValue = bowsTarget.getSelectDataList(saSelectables[i]);
					if (slValue != null && slValue.size() > 0) {
						Iterator itrTemp = slValue.iterator();
						while (itrTemp.hasNext()) {
							sTemp = (String) itrTemp.next();
							if (UIUtil.isNotNullAndNotEmpty(sTemp)) {
								slValueToStore = slValue;
								bValueSet = true;
								break;
							}
						}
						if (bValueSet)
							break;
					}
				}

				StringBuffer sbTemp = new StringBuffer();
				if (slValueToStore != null) {
					Iterator itrValue = slValueToStore.iterator();
					while (itrValue.hasNext()) {
						if (sbTemp.length() > 0)
							sbTemp.append(" | ");

						sbTemp.append(itrValue.next());

					}
				}
				if (!deTemp.getEnoviaHelperMethod().equals("")) {
					ArrayList<String> alHelperMethodArray = new ArrayList();
					String[] saHelperSelectables = StringUtils.split(deTemp.getEnoviaSelectable(), pgV3Constants.SYMBOL_PIPE);
					int isaHelperSelectableslength=saHelperSelectables.length;
					StringList slHelperValue =null;
					for (int i = 0; i < isaHelperSelectableslength; i++) {
						slHelperValue = bowsTarget.getSelectDataList(saHelperSelectables[i]);

						StringBuffer sbHelperTemp = new StringBuffer();
						if (slHelperValue != null) {
							Iterator itrValue = slHelperValue.iterator();
							while (itrValue.hasNext()) {
								if (sbHelperTemp.length() > 0)
									sbHelperTemp.append(" | ");

								sbHelperTemp.append(itrValue.next());

							}
						}

						alHelperMethodArray.add(sbHelperTemp.toString());
					}

					String value = callHelperMethod(deTemp.getEnoviaHelperMethod(), alHelperMethodArray);
					sbTemp = new StringBuffer(value);
				}
				Object setValue = DHUtility.setDateFormate(deTemp.getElementIsADate(), sbTemp.toString());
				ReflectionUtility.setVariableOnObject(subObject, deTemp.getJaxbElementName(), setValue);
			}

			String sTemp = deTemp.getSubcomponent();
			if (UIUtil.isNotNullAndNotEmpty(sTemp)) {
				Component subcomponentDetails = (Component) _components.get(sTemp);

				Object expandSubObject = null;
				if (detailedComponent.getJaxbclass().contains("$"))
					expandSubObject = ReflectionUtility.getObjectInstance(parentObject, detailedComponent.getName());
				else
					expandSubObject = ReflectionUtility.getObjectInstance(subcomponentDetails.getJaxbclass());

				if (!subcomponentDetails.getEnoviaExpandRelationship().equals("")) {
					expandSubObject = loadEnoviaComponentStructure(subcomponentDetails, bowsTarget, detailedComponent,
							subObject, sTargetLevel);
				} else {
					expandSubObject = getSingleExpandRowObject(subcomponentDetails, parentObject, rel, bowsTarget,
							bowsSource);
				}

				if (ReflectionUtility.containsNonNullItems(expandSubObject))
					ReflectionUtility.setVariableOnObject(subObject, deTemp.getJaxbElementName(), expandSubObject);
			}
		}
		return subObject;
	}

	/**
	 * @description: Method to set relationship Data
	 * 
	 * @param rel
	 * @param subObject
	 * @param deTemp
	 * @return void
	 * @throws NoSuchMethodException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchFieldException
	 * @throws PDFToolCustomException
	 * @throws SecurityException
	 * @throws PDFToolFieldIsListException
	 */

	private void setDataFromRelationship(RelationshipWithSelect rel, Object subObject, DetailedElement deTemp)
			throws NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchFieldException, SecurityException, PDFToolCustomException {

		if (!deTemp.getEnoviaSelectable().equals("")) {

			if (!deTemp.getEnoviaHelperMethod().equals("")) {
				StringList slHelperValue =null;
				ArrayList<String> alHelperMethodArray = new ArrayList();
				String[] saHelperSelectables = StringUtils.split(deTemp.getEnoviaSelectable(), pgV3Constants.SYMBOL_PIPE);
				int saHelperSelectablesSize=saHelperSelectables.length;
				for (int i = 0; i < saHelperSelectablesSize; i++) {
					slHelperValue = rel.getSelectDataList(saHelperSelectables[i]);

					StringBuffer sbHelperTemp = new StringBuffer();
					if (slHelperValue != null) {
						Iterator itrValue = slHelperValue.iterator();
						while (itrValue.hasNext()) {
							if (sbHelperTemp.length() > 0)
								sbHelperTemp.append(" | ");

							sbHelperTemp.append(itrValue.next());

						}
					}
					alHelperMethodArray.add(sbHelperTemp.toString());
				}

				String value = callHelperMethod(deTemp.getEnoviaHelperMethod(), alHelperMethodArray);

				Object setValue = DHUtility.setDateFormate(deTemp.getElementIsADate(), value);
				ReflectionUtility.setVariableOnObject(subObject, deTemp.getJaxbElementName(), setValue);
			} else {

				String[] saSelectables = StringUtils.split(deTemp.getEnoviaSelectable(), pgV3Constants.SYMBOL_PIPE);

				StringList slValue = null;
				boolean bValueSet = false;
				StringList slValueToStore = new StringList();
				String sTemp = DomainConstants.EMPTY_STRING; 
				int saSelectablesSize=saSelectables.length;
				for (int i = 0; i < saSelectablesSize; i++) {

					slValue = rel.getSelectDataList(saSelectables[i]);

					if (slValue != null && slValue.size() > 0) {
						Iterator itrTemp = slValue.iterator();
						while (itrTemp.hasNext()) {
							sTemp = (String) itrTemp.next();
							if (UIUtil.isNotNullAndNotEmpty(sTemp)) {
								slValueToStore = slValue;
								bValueSet = true;
								break;
							}
						}
						if (bValueSet)
							break;
					}
				}

				StringBuffer sbTemp = new StringBuffer();
				if (slValueToStore != null) {
					Iterator itrValue = slValueToStore.iterator();
					while (itrValue.hasNext()) {
						if (sbTemp.length() > 0)
							sbTemp.append(" | ");

						sbTemp.append(itrValue.next());

					}
				}
				Object setValue = DHUtility.setDateFormate(deTemp.getElementIsADate(), sbTemp.toString());
				ReflectionUtility.setVariableOnObject(subObject, deTemp.getJaxbElementName(), setValue);
			}

		} else {
			if (!deTemp.getEnoviaHelperMethod().equals("")) {

				ArrayList<String> alHelperMethodArray = new ArrayList();
				String[] saHelperSelectables = StringUtils.split(deTemp.getEnoviaSelectable(), pgV3Constants.SYMBOL_PIPE);
				StringList slHelperValue = null;
				StringBuffer sbHelperTemp = null;
				int isaHelperSelectableslength=saHelperSelectables.length;
				for (int i = 0; i < isaHelperSelectableslength; i++) {
					slHelperValue = rel.getSelectDataList(saHelperSelectables[i]);

					sbHelperTemp = new StringBuffer();
					if (slHelperValue != null) {
						Iterator itrValue = slHelperValue.iterator();
						while (itrValue.hasNext()) {
							if (sbHelperTemp.length() > 0)
								sbHelperTemp.append(" | ");

							sbHelperTemp.append(itrValue.next());

						}
					}

					alHelperMethodArray.add(sbHelperTemp.toString());

				}

				String value = callHelperMethod(deTemp.getEnoviaHelperMethod(), alHelperMethodArray);
				Object setValue = DHUtility.setDateFormate(deTemp.getElementIsADate(), value);
				ReflectionUtility.setVariableOnObject(subObject, deTemp.getJaxbElementName(), setValue);
			} else
				ReflectionUtility.setVariableOnObject(subObject, deTemp.getJaxbElementName(), null);
		}

	}

	/**
	 * @description: Method to set relationship Data
	 * 
	 * @param detailedComponent
	 * @param bows
	 * @param parentObject
	 * @param currentExpandLevel
	 * @return Object
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchFieldException
	 * @throws PDFToolFieldIsListException
	 * @throws MatrixException
	 * @throws SecurityException
	 * @throws PDFToolCustomException
	 */

	private Object loadEnoviaSingleValuedComponent(Component detailedComponent, BusinessObjectWithSelect bows,
			Object parentObject, String currentExpandLevel) throws ClassNotFoundException, NoSuchMethodException,
			InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchFieldException,
			SecurityException, MatrixException, PDFToolCustomException {
		Object results;
		if (detailedComponent.getJaxbclass().contains("$"))
			results = ReflectionUtility.getObjectInstance(parentObject, detailedComponent.getName());
		else
			results = ReflectionUtility.getObjectInstance(detailedComponent.getJaxbclass());

		Elements elements = detailedComponent.getElements();
		List<String> elementNamesList = elements.getElement();

		boolean bIncludeComponent = true;
		if (this._alRequestedComponents != null && !_alRequestedComponents.contains(detailedComponent.getName()))
			bIncludeComponent = false;
		
		for (String elementName : elementNamesList) {

			DetailedElement deTemp = (DetailedElement) _elements.get(elementName);
			Object subObject = null;
			if ((deTemp.getSubcomponent() != null) && (!deTemp.getSubcomponent().equals(""))) {
				 String sTemp = deTemp.getSubcomponent();
				Component subcomponentDetails = (Component) _components.get(sTemp);
				if (UIUtil.isNullOrEmpty(subcomponentDetails.getEnoviaTargetObjectSelectable()))
					subObject = loadEnoviaComponentStructure(subcomponentDetails, bows, detailedComponent, results,
							currentExpandLevel);

			}

			if (subObject == null) {
				String strExpandLevel=deTemp.getEnoviaExpandLevel();
				if (UIUtil.isNotNullAndNotEmpty(strExpandLevel)) {
					boolean bFoundMatch = false;
					String[] saLevels = strExpandLevel.split(pgV3Constants.SYMBOL_PIPE);
					int isaLevelslength=saLevels.length;
					for (int i = 0; i < isaLevelslength; i++) {
						if (saLevels[i].equals(currentExpandLevel)) {
							bFoundMatch = true;
							break;
						}
					}
					if (!bFoundMatch)
						bIncludeComponent = false;
				}
				String strEnoviaSelectable=deTemp.getEnoviaSelectable();
				if (UIUtil.isNotNullAndNotEmpty(strEnoviaSelectable) && bIncludeComponent) {
					String[] saSelectables = StringUtils.split(deTemp.getEnoviaSelectable(), pgV3Constants.SYMBOL_PIPE);
					Object value = null;
					int isaSelectableslength=saSelectables.length;
					for (int i = 0; i < isaSelectableslength; i++) {
						StringList slValue = bows.getSelectDataList(saSelectables[i]);

						StringBuffer sbTemp = new StringBuffer();
						if (slValue != null) {
							Iterator itrValue = slValue.iterator();
							while (itrValue.hasNext()) {
								if (sbTemp.length() > 0)
									sbTemp.append(" | ");

								sbTemp.append(itrValue.next());

							}
						}
						value = sbTemp.toString();

						if (value != null && !value.equals(""))
							break;
					}

					if (!deTemp.getEnoviaHelperMethod().equals("")) {

						ArrayList<String> alHelperMethodArray = new ArrayList();
						String[] saHelperSelectables = StringUtils.split(deTemp.getEnoviaSelectable(),pgV3Constants.SYMBOL_PIPE);
						int isaHelperSelectableslength=saHelperSelectables.length;
						StringList slHelperValue =null;
						for (int i = 0; i < isaHelperSelectableslength; i++) {
							slHelperValue = bows.getSelectDataList(saHelperSelectables[i]);

							StringBuffer sbHelperTemp = new StringBuffer();
							if (slHelperValue != null) {
								Iterator itrValue = slHelperValue.iterator();
								while (itrValue.hasNext()) {
									if (sbHelperTemp.length() > 0)
										sbHelperTemp.append(" | ");

									sbHelperTemp.append(itrValue.next());

								}
							}

							alHelperMethodArray.add(sbHelperTemp.toString());

						}

						value = callHelperMethod(deTemp.getEnoviaHelperMethod(), alHelperMethodArray);
					}
					Object setValue = DHUtility.setDateFormate(deTemp.getElementIsADate(), value);
					ReflectionUtility.setVariableOnObject(results, deTemp.getJaxbElementName(), setValue);
				} else {
					String[] saSelectables = StringUtils.split(deTemp.getEnoviaSelectable(), pgV3Constants.SYMBOL_PIPE);

					Object value = null;
					int isaSelectableslength=saSelectables.length;
					StringList slValue = null;
					for (int i = 0; i < isaSelectableslength; i++) {
						slValue = bows.getSelectDataList(saSelectables[i]);

						StringBuffer sbTemp = new StringBuffer();
						if (slValue != null) {
							Iterator itrValue = slValue.iterator();
							while (itrValue.hasNext()) {
								if (sbTemp.length() > 0)
									sbTemp.append(" | ");

								sbTemp.append(itrValue.next());

							}
						}

						value = sbTemp.toString();
						if (value != null && !value.equals(""))
							break;
					}

					if (!deTemp.getEnoviaHelperMethod().equals("")) {

						ArrayList<String> alHelperMethodArray = new ArrayList();
						String[] saHelperSelectables = StringUtils.split(deTemp.getEnoviaSelectable(), pgV3Constants.SYMBOL_PIPE);
						int isaHelperSelectableslength=saHelperSelectables.length;
						StringList slHelperValue =null;
						for (int i = 0; i < isaHelperSelectableslength; i++) {
							slHelperValue = bows.getSelectDataList(saHelperSelectables[i]);

							StringBuffer sbHelperTemp = new StringBuffer();
							if (slHelperValue != null) {
								Iterator itrValue = slHelperValue.iterator();
								while (itrValue.hasNext()) {
									if (sbHelperTemp.length() > 0)
										sbHelperTemp.append(" | ");

									sbHelperTemp.append(itrValue.next());

								}
							}

							alHelperMethodArray.add(sbHelperTemp.toString());

						}

						String newvalue = callHelperMethod(deTemp.getEnoviaHelperMethod(), alHelperMethodArray);
						ReflectionUtility.setVariableOnObject(results, deTemp.getJaxbElementName(), newvalue);
					}

				}
			} else if (bIncludeComponent) {

				if (ReflectionUtility.containsNonNullItems(subObject)) {
					ReflectionUtility.setVariableOnObject(results, deTemp.getJaxbElementName(), subObject);
				}
			}
		}
		return results;
	}

	/**
	 * @description: Method to call helper method
	 * 
	 * @param helperMethodName
	 * @param values
	 * @return String
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */

	private String callHelperMethod(String helperMethodName, ArrayList<String> values) throws ClassNotFoundException,
			NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		Object results = null;
		Object helperObject = ReflectionUtility.getObjectInstance("com.pdfview.helper.EnoviaHelper");
		Class[] params = { Context.class, String.class, ArrayList.class };
		Object[] paramValues = { _context, _oid, values };

		Method method = helperObject.getClass().getDeclaredMethod(helperMethodName, params);

		results = method.invoke(helperObject, paramValues);
		return results.toString();
	}

	/**
	 * @description: Method to load Multi value component
	 * 
	 * @param detailedComponent
	 * @param bows
	 * @param parentObject
	 * @return Object
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchFieldException
	 * @throws PDFToolFieldIsListException
	 * @throws MatrixException
	 * @throws SecurityException
	 */

	private Object loadEnoviaMultiValuedComponent(Component detailedComponent, BusinessObjectWithSelect bows,
			Object parentObject)
			throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchFieldException, SecurityException, MatrixException {
		Object results;
		if (detailedComponent.getJaxbclass().contains("$")) {
			results = ReflectionUtility.getObjectInstance(parentObject, detailedComponent.getName());
		} else
			results = ReflectionUtility.getObjectInstance(detailedComponent.getJaxbclass());

		Elements elements = detailedComponent.getElements();
		List<String> elementNamesList = elements.getElement();
		int isaSelectableslength=0;
		for (String elementName : elementNamesList) {
			DetailedElement deTemp = (DetailedElement) _elements.get(elementName);

			if (!detailedComponent.getEnoviaMultiValuedSelectable().equals("")) {

				StringList slResults = null;
				String[] saSelectables = StringUtils.split(detailedComponent.getEnoviaMultiValuedSelectable(), pgV3Constants.SYMBOL_PIPE);
				isaSelectableslength=saSelectables.length;
				for (int i = 0; i < isaSelectableslength; i++) {
					slResults = bows.getSelectDataList(saSelectables[i]);
					if (slResults != null && !slResults.equals(""))
						break;
				}

				Field fList = ReflectionUtility.getListField(results.getClass(), deTemp.getJaxbElementName());
				List multiValuedList = ReflectionUtility.getListInstance(results,
						StringUtils.capitalize(deTemp.getJaxbElementName()));
				if(slResults!=null && !slResults.isEmpty()) {
					Iterator itrResults = slResults.iterator();
					while (itrResults.hasNext()) {
						String sResult = (String) itrResults.next();
						if (ReflectionUtility.containsNonNullItems(sResult))
							multiValuedList.add(sResult);
					}	
				}
			}
		}
		return results;
	}
}
