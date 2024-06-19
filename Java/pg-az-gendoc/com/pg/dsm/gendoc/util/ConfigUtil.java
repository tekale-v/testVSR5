package com.pg.dsm.gendoc.util;

import com.matrixone.apps.cpn.util.BusinessUtil;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.pg.dsm.gendoc.enumeration.CloudConstants;
import com.pg.v3.custom.pgV3Constants;
import matrix.db.Context;
import matrix.util.StringList;
import org.apache.commons.lang3.StringUtils;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;

import java.util.Map;

public class ConfigUtil {

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public static String getIRMSupportedFileExtensions(Context context) throws FrameworkException {
        return getSupportedExtensionForAzureAndIText(context, getIRMAzureConfigObjectID(context));
    }

    /**
     * @param supportedFileExtensions
     * @param fileExtension
     * @return
     */
    public static boolean isSupportedExtension(String supportedFileExtensions, String fileExtension) {
        return StringUtils.containsIgnoreCase(supportedFileExtensions, fileExtension);
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public static String getIRMAzureConfigObjectID(Context context) throws FrameworkException {
		
		String var2 = CloudConstants.Basic.INNOVATION_RECORD_CLOUD_GEN_DOC_CONFIG_OBJECT.getValue();
		String var1 = pgV3Constants.TYPE_PGCONFIGURATIONADMIN;
		StringList var3 = StringList.create(new String[]{"id", "name"});
		MapList var4 = DomainObject.findObjects(context, var1, var2.trim(), "*", "*",
				PropertyUtil.getSchemaProperty("vault_eServiceProduction"), "", true, var3);
		if (var4.size() > 1) {
			throw new FrameworkException("More than one '".concat(var1).concat("' exists with name ").concat(var2));
		} else {
			return (String) ((Map) var4.get(0)).get("id");
		}
		
        //return BusinessUtil.getObjectID(context, pgV3Constants.TYPE_PGCONFIGURATIONADMIN, CloudConstants.Basic.INNOVATION_RECORD_CLOUD_GEN_DOC_CONFIG_OBJECT.getValue());
    }

    /**
     * @param context
     * @param configOid
     * @return
     * @throws FrameworkException
     */
    public static String getSupportedExtensionForAzureAndIText(Context context, String configOid) throws FrameworkException {
        String azureExtensionSelect = CloudConstants.Attribute.GEN_DOC_FILE_EXTENSIONS_FOR_CLOUD.getSelect(context);
        String iTextExtensionSelect = CloudConstants.Attribute.GEN_DOC_FILE_EXTENSIONS_FOR_ITEXT.getSelect(context);
        DomainObject configObject = DomainObject.newInstance(context, configOid);
        Map<String, String> objInfo = configObject.getInfo(context, StringList.create(azureExtensionSelect, iTextExtensionSelect));
        StringBuilder extensionBuilder = new StringBuilder(CloudConstants.Basic.FILE_EXTENSION_PDF.getValue());
        extensionBuilder.append(CloudConstants.Basic.SYMBOL_COMMA.getValue());
        extensionBuilder.append(objInfo.get(azureExtensionSelect));
        extensionBuilder.append(CloudConstants.Basic.SYMBOL_COMMA.getValue());
        extensionBuilder.append(objInfo.get(iTextExtensionSelect));
        return extensionBuilder.toString();
    }

}
