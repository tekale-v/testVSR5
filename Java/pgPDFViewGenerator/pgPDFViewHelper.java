import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.StringList;

public class pgPDFViewHelper {
	public static Object executeIntermediatorClassMethod(Context context, String methodName, String[] args)
			throws Exception {

		Object mlRelatedObjects = (Object) JPO.invoke(context, "pgPDFViewIntermediator", null, methodName, args,
				Object.class);

		return mlRelatedObjects;
	}

	public static Object executeMainClassMethod(Context context, String classname, String methodName, String[] args)
			throws Exception {

		Object mlRelatedObjects = (Object) JPO.invoke(context, classname, null, methodName, args, Object.class);

		return mlRelatedObjects;
	}
	public static Object executepgDSOCPNProductDataClassMethod(Context context, String strIfYesselectbatterytype, String strpgPicklistTypeName, String strpgPicklistAttributeName)
			throws Exception {
		HashMap hm=new HashMap();
		hm.put("strIfYesselectbatterytype", strIfYesselectbatterytype);
		hm.put("strpgPicklistTypeName", strpgPicklistTypeName);
		hm.put("strpgPicklistAttributeName", strpgPicklistAttributeName);
		String[] args =JPO.packArgs(hm);
		Object mlRelatedObjects =executeIntermediatorClassMethod(context, "getBatteryTypeFields", args);
		return mlRelatedObjects;
	}
	public static Object executepgDSOCommonUtilsMethod(Context context, String[] args,String paramName)
			throws Exception {
		
		Map paramMap = (Map) JPO.unpackArgs(args);
		paramMap.put("paramName", paramName);
		String[] args1 =JPO.packArgs(paramMap);
		Object mlRelatedObjects =executeIntermediatorClassMethod(context, "getParam", args1);

		return mlRelatedObjects;
	}
	public static StringList createSelects(String... selects)
	{
		StringList selectList = new StringList();
		for(String select : selects)
		{
			selectList.add(select);
		}
		return selectList;
	}
	
	/**
	 * @param strDescription
	 * @return
	 */
	public static String getNoteDescription(String strDescription) {
		strDescription =strDescription.replace("&gt;","#GREATER_THAN");
		strDescription =strDescription.replace("[<]","#LESS_THAN");
		strDescription =strDescription.replace("&#x3a;", ":");
		strDescription =strDescription.replace("&#x40;", "@");
		strDescription =strDescription.replace("&amp;", "&");
		strDescription =strDescription.replace("&#x3d;", "=");
		strDescription =strDescription.replace("&#x3b;", ";");
		strDescription =strDescription.replace("&#x28;", "(");
		strDescription =strDescription.replace("&#x29;", ")");
		strDescription =strDescription.replace("&#x2f;", "/");
		strDescription =strDescription.replace("&#x23;", "#");
		strDescription =strDescription.replace("&#x21;", "!");
		strDescription =strDescription.replace("&#x27;", "'");
		strDescription =strDescription.replace("&#x3f;", "?");
		strDescription =strDescription.replace("&#x5e;", "^");
		strDescription =strDescription.replace("&#x7b;", "{");
		strDescription =strDescription.replace("&#x7d;", "}");
		strDescription =strDescription.replace("&#x5b;", "[");
		strDescription =strDescription.replace("&#x5d;", "]");
		strDescription =strDescription.replace("&#x7c;", "|");
		strDescription =strDescription.replace("&#x7e;", "~");
		strDescription =strDescription.replace("&#x60;", "`");
		strDescription =strDescription.replace("&quot;", "\"");
		strDescription =strDescription.replace("&#xa;", "<BR/>");
		strDescription =StringEscapeUtils.escapeJava(strDescription);
		return strDescription;
	}
	
}
