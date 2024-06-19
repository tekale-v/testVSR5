/*
 **   VeevaConfigXMLBuilder.java
 **   Description - Introduced as part of Veeva integration.      
 **   Bean to load Veeva Config xml to (JAXB) object bean for Veeva extraction.
 **
 */
package com.pg.dsm.veeva.config;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import com.pg.dsm.veeva.util.Utility;
import com.pg.dsm.veeva.vql.xml.binder.VeevaConfigXML;

import matrix.db.Context;


public class VeevaConfigXMLBuilder {
	boolean isLoaded;
	VeevaConfigXML config;
	
	/** 
	 * @about Constructor
	 * @param Builder - (builder class)
	 * @since DSM 2018x.3
	 */
	private VeevaConfigXMLBuilder(Builder builder) {
		this.config = builder.config;
		this.isLoaded = builder.isLoaded;
	}
	/** 
	 * @about Method to check loader bean
	 * @return DomainObject
	 * @since DSM 2018x.3
	 */
	public boolean isLoaded() {
		return isLoaded;
	}
	/** 
	 * @about Getter method - to config object xml bean
	 * @return VeevaConfigXML
	 * @since DSM 2018x.3
	 */
	public VeevaConfigXML getVeevaXMLConfig() {
		return config;
	}
	public static class Builder {
		
		private final Logger logger = Logger.getLogger(this.getClass().getName());
		
		VeevaConfigXML config;
		boolean isLoaded;
		Context context;
		
		/** 
		 * @about Constructor
		 * @param Context 
		 * @since DSM 2018x.3
		 */
		public Builder(Context context) {
			this.context = context;
		}
		/** 
		 * @about Builder method
		 * @return VeevaConfigXMLBuilder
		 * @since DSM 2018x.3
		 */
		public VeevaConfigXMLBuilder load() {
			try {
				config=(VeevaConfigXML)unmarshallXMLFile(new StringReader(Utility.readVeevaXMLPage(context)), VeevaConfigXML.class);
				this.isLoaded = true;
				logger.info("Veeva XML Page object loaded in bean");
			} 
			catch(Exception e) {
				this.isLoaded = false;
				logger.error("************FAILED >>> Unable to load Veeva XML config Page object to bean "+e);
			}
			return new VeevaConfigXMLBuilder(this);
		}
		/** 
		 * @about Method to marshal xml to bean
		 * @return Object
		 * @throws JAXBException
		 * @since DSM 2018x.3
		 */
		public Object unmarshallXMLFile(StringReader reader, Class<?> cls) throws JAXBException {
			JAXBContext jc = JAXBContext.newInstance(cls);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			Object object = unmarshaller.unmarshal(reader);
			return object;
		}
	}
	
}
