package com.pg.enovia.mos.config;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.pg.enovia.mos.restapp.bean.Preferences;

import matrix.db.Context;
import matrix.db.Page;
import matrix.util.MatrixException;

public class MosConfig {

	private Preferences rootObject = null;

	/**
	 * @return
	 */
	public Preferences getRootObject() {
		return rootObject;
	}

	/**
	 * @param rootObject
	 */
	private void setRootObject(Preferences rootObject) {
		this.rootObject = rootObject;
	}

	public MosConfig parseColumnPreferences(Context paramContext, String paramString) throws JAXBException, MatrixException {
		Class<Preferences>[] cls=new Class[1];
		cls[0]=Preferences.class;
		Page localPage = new Page(paramString);
		localPage.open(paramContext);
		String str = localPage.getContents(paramContext);
		if (str.length() > 0) {
			JAXBContext jc = JAXBContext.newInstance(cls);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			InputStream elementsXML = org.apache.commons.io.IOUtils.toInputStream(str, StandardCharsets.UTF_8);
			Preferences rootObject1 = (Preferences) unmarshaller.unmarshal(elementsXML);
			setRootObject(rootObject1);
		}
		return this;
	}

}
