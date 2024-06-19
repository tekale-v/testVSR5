package com.pg.designtools.integrations.datahandlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

import com.dassault_systemes.enovia.gls.common.model.PRSPContext;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.vplmintegration.util.VPLMIntegTraceUtil;
import com.pg.designtools.datamanagement.CommonUtility;
import com.pg.designtools.datamanagement.DataConstants;
import com.pg.designtools.integrations.exception.DesignToolsIntegrationException;
import com.pg.designtools.integrations.tops.PriPack;
import com.pg.designtools.integrations.tops.Shipper;
import com.pg.designtools.integrations.tops.UnitLoadAnalysis;
import com.pg.designtools.integrations.tops.UnitLoadAnalysisRoot;
import com.pg.designtools.integrations.tops.Unitload;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.BooleanConverter;
import matrix.util.MatrixException;

public class AnalysisXML {

	private XStream xstream;

	private File analysisXMLFile;
	private UnitLoadAnalysis unitLoadAnalysis;
	DomainObject domSPSObj;
	DomainObject domExistingTUPObj;
	DataConstants.customTOPSExceptions errorAnalysis0TagNotPresent = DataConstants.customTOPSExceptions.ERROR_400_ANALYSIS0_TAG_NOT_PRESENT;
	DataConstants.customTOPSExceptions errorInvalidSPSName = DataConstants.customTOPSExceptions.ERROR_400_INVALID_SPS_NAME;
	DataConstants.customTOPSExceptions errorInvalidSPSOrigination = DataConstants.customTOPSExceptions.ERROR_400_INVALID_SPS_ORIGINATION;

	public AnalysisXML(File file) {

		analysisXMLFile = file;
		xstream = new XStream();
		xstream.registerConverter(new BooleanConverter("1", "0", false));
		xstream.ignoreUnknownElements();

	}

	public boolean isFormat() throws DocumentException, SAXException {

		boolean retVal = false;
		SAXReader saxReader = new SAXReader();
		saxReader.setFeature(EnoviaResourceBundle.getProperty(PRSPContext.get(), "emxFrameworkStringResource",
				PRSPContext.get().getLocale(), "emxFramework.saxReader.Feature"), true); // Compliant
		Document document = saxReader.read(analysisXMLFile);

		Element rootElement = document.getRootElement();
		if (rootElement.hasContent() && DataConstants.ANALYSIS_XML_ROOT.equalsIgnoreCase(rootElement.getName())) {
			retVal = true;
		}

		return retVal;
	}

	void getRoot() throws FileNotFoundException {
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>>>>> getRoot Analysis XML START");
		xstream.processAnnotations(UnitLoadAnalysisRoot.class);
		Class<?>[] classes = new Class[] { UnitLoadAnalysisRoot.class, UnitLoadAnalysis.class, PriPack.class,
				Shipper.class, Unitload.class };
		XStream.setupDefaultSecurity(xstream);
		xstream.allowTypes(classes);

		UnitLoadAnalysisRoot root = (UnitLoadAnalysisRoot) xstream.fromXML(new FileReader(analysisXMLFile));
		unitLoadAnalysis = root.getAnalysis();
		if (unitLoadAnalysis == null) {
			throw new DesignToolsIntegrationException(errorAnalysis0TagNotPresent.getExceptionCode(),
					errorAnalysis0TagNotPresent.getExceptionMessage());
		}
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>>>>>>>>>> getRoot Analysis XML END");
	}

	boolean validateXML(DomainObject domSPSObj) throws MatrixException, FileNotFoundException {
		getRoot();
		String strSPSName = unitLoadAnalysis.getanalysisSpec().getspsinfo();
		boolean readyToUpload = true;
		if (domSPSObj != null
				&& !(domSPSObj.getInfo(PRSPContext.get(), DomainConstants.SELECT_NAME).equals(strSPSName))) {
			readyToUpload = false;
		}
		if (!isSPSObjectValid(strSPSName)) {
			readyToUpload = false;
		}
		return readyToUpload;
	}

	boolean isSPSObjectValid(String strSPSName) throws MatrixException {
		boolean isSPSObjectvalid = false;

		if (UIUtil.isNotNullAndNotEmpty(strSPSName)) {
			domSPSObj = CommonUtility.getSPSFromTNR(DataConstants.TYPE_PG_STACKINGPATTERN, strSPSName,
					DataConstants.CONSTANT_FIRST_REVISION);
			VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>> isSPSObjectValid Analysis XML domSPSObj " + domSPSObj);
			if (domSPSObj != null) {
				isSPSObjectvalid = true;
				String strSPSOrigination = domSPSObj.getAttributeValue(PRSPContext.get(),
						DataConstants.ATTR_PG_SPS_ORIGINATION);

				if (DataConstants.RANGE_VALUE_MANUAL.equals(strSPSOrigination)) {
					throw new DesignToolsIntegrationException(errorInvalidSPSOrigination.getExceptionCode(),
							errorInvalidSPSOrigination.getExceptionMessage());
				}
			}
		}
		return isSPSObjectvalid;
	}

	void process() {
		/* We will no more process Analysis XML */
	}

	public String toString() {
		return this.getClass().getSimpleName();
	}

	public static Map<Object, Object> getExtAddlInfo() {
		return new HashMap<>();
	}
}
