/*Created by DSM for Requirement 47389 2022x.04 Dec CW 2023 */
package com.pg.dsm.gendocondemand.config;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;
import com.pg.ctrlm.custom.CtrlmJobContext;
import com.pg.dsm.gendocondemand.beans.GenDocProperties;
import com.pg.dsm.gendocondemand.enumeration.GenDocConstant;

import matrix.db.Context;

public class GenDocOnDemandConfig {

	
	private PrintWriter out;
	private Context loginContext;
	private boolean loaded;
	private GenDocProperties properties;

	public GenDocOnDemandConfig() {
		this.loaded = Boolean.FALSE;
	}

	public Context getContext() {
		return loginContext;
	}

	private void setLoginContext(Context loginContext) {
		this.loginContext = loginContext;
	}
	public boolean isLoaded() {
		return loaded;
	}
	public GenDocProperties getProperties() {
		return properties;
	}
	public GenDocOnDemandConfig config(PrintWriter out) {
		this.setLoginContext(this.connectContext(out));
		this.loadProperties(out);
		this.out =out;
	    return this;
	}
	private Context connectContext(PrintWriter out) {
		Context context = null;
		try {
			context = CtrlmJobContext.getCtrlmContext();
			out.append("Context connected sucessfully..!! \n");
			} catch (Exception e) {
			out.append("Error while connecting context "+e.getMessage()+"\n");
		}
		return context;
	}
	private void loadProperties(PrintWriter out) {
			try (InputStream inputStream = new FileInputStream(
				new File(GenDocConstant.CTRLM_PROPERTIES_FILE.getValue()))) {
			Properties gendocproperties = new Properties();
			gendocproperties.load(inputStream);
			this.properties = new GenDocProperties(gendocproperties);
			this.loaded = this.properties.isPropertiesLoaded();
		} catch (IOException e) {
			out.append("Error while load properties "+e.getMessage()+"\n");
		}
	}
}
