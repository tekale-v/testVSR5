package com.pg.designtools.integrations.tops;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("CubeSpec")
public class UnitLoadAnalysisSpec {
	
	public UnitLoadAnalysisSpec() {
		super();
	}
	
	@XStreamAlias("strUser1")
	private String spsinfo;	
	
	@XStreamAlias("strUser3")
	private String documenttype;	
	
	public String getspsinfo() {
		return spsinfo;
	}
	public void setSPSinfo(String spsinfo) {
		this.spsinfo = spsinfo;
	}
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [" + spsinfo + "]";
	}

}