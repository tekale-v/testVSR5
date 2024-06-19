package com.pg.designtools.integrations.tops;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("bundle")
public class Bundle {
	
	@XStreamAlias("name")
	private String name;
	@XStreamAlias("EnoviaMap")
	private String enoviaMap;

	public Bundle() {
		super();
	}	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getEnoviaMap() {
		return enoviaMap;
	}
	public void setEnoviaMap(String enoviaMap) {
		this.enoviaMap = enoviaMap;
	}
	
}
