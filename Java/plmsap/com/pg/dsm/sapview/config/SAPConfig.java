package com.pg.dsm.sapview.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pg.ctrlm.custom.CtrlmJobContext;
import com.pg.dsm.sapview.beans.SAPProperties;
import com.pg.dsm.sapview.enumeration.SAPViewConstant;

import matrix.db.Context;
import matrix.util.StringList;

/**
 * @author DSM(Sogeti) - Added for 2018x.6 Dec CW SAP Requirement #40804,#40805.
 *
 */
public class SAPConfig {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	private Context loginContext;
	private boolean loaded;
	private SAPProperties properties;
	private StringList deliveredParts;

	public SAPConfig() {
		this.loaded = Boolean.FALSE;
	}

	/**
	 * @return the loginContext
	 */
	public Context getContext() {
		return loginContext;
	}

	/**
	 * @param loginContext the loginContext to set
	 */
	private void setLoginContext(Context loginContext) {
		this.loginContext = loginContext;
	}

	/**
	 * @return the loaded
	 */
	public boolean isLoaded() {
		return loaded;
	}

	/**
	 * @return the properties
	 */
	public SAPProperties getProperties() {
		return properties;
	}

	/**
	 * @return
	 */
	public SAPConfig config() {
		this.setLoginContext(this.connectContext());
		this.loadProperties();
		return this;
	}

	/**
	 * @return
	 */
	private Context connectContext() {
		Context context = null;
		try {
			context = CtrlmJobContext.getCtrlmContext();
			logger.log(Level.INFO, "Context connected sucessfully..!!");
		} catch (Exception e) {
			logger.log(Level.WARNING, null, e);
		}
		return context;
	}
	
	/**
	 * @return
	 * @throws IOException
	 */
	private void loadProperties() {
		try (InputStream inputStream = new FileInputStream(
				new File(SAPViewConstant.CTRLM_PROPERTIES_FILE.getValue()))) {
			Properties sapproperties = new Properties();
			sapproperties.load(inputStream);
			this.properties = new SAPProperties(sapproperties);
			this.loaded = this.properties.isPropertiesLoaded();
		} catch (IOException e) {
			logger.log(Level.WARNING, null, e);
		}
	}

	/**
	 * @return the deliveredParts
	 */
	public StringList getDeliveredParts() {
		if (this.deliveredParts == null)
			this.deliveredParts = new StringList();
		return deliveredParts;
	}

}
