package com.pg.widgets.helloworldtrusted;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import com.matrixone.apps.domain.util.PersonUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import matrix.db.Context;

public class PGHelloWorldUtil {
	
	private static final String JSON_OUTPUT_KEY_ERROR = "error";
	private static final String JSON_OUTPUT_KEY_DATA = "data";

	private static final Logger logger = Logger.getLogger(PGHelloWorldUtil.class.getName());
		

	/**
	 * The method returns the full name of the user
	 * 
	 * @param context
	 *            the Enovia Context object
	 * @return The full name of the logged in user
	 */
	public static Object getPersonFullName(Context context) {
		JsonObjectBuilder output = Json.createObjectBuilder();
		try {
			JsonArrayBuilder outArr = Json.createArrayBuilder();
			String name = PersonUtil.getFullName(context);
			if(UIUtil.isNotNullAndNotEmpty(name)) {
				outArr.add(name);
			}
			output.add(JSON_OUTPUT_KEY_DATA, outArr);
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Exception in PGHelloWorldUtil : getPersonFullName ::" + ex.getMessage(), ex);
			output.add(JSON_OUTPUT_KEY_ERROR, ex.getMessage());
		}
		return output;
	}	
}
