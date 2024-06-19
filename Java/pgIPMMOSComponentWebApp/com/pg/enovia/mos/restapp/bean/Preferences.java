package com.pg.enovia.mos.restapp.bean;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "preference"
})
@XmlRootElement(name = "Preferences", namespace = "")
public class Preferences {

    @XmlElement(name = "Preference", namespace = "")
    protected List<Preference> preference;
    
    
    /**
     * @return
     */
    public List<Preference> getPreference() {
    	 if(preference == null) {
    		 preference = new ArrayList<>();
         }
         return this.preference;
	}

	/**
	 * @param preference
	 */
	public void setPreference(List<Preference> preference) {
		this.preference = preference;
	}
}
