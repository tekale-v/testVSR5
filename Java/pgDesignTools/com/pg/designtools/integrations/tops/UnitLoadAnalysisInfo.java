package com.pg.designtools.integrations.tops;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("infomation")
public class UnitLoadAnalysisInfo {
	
	public UnitLoadAnalysisInfo() {
		super();
	}
	
	@XStreamAlias("title")
	private String title;
	
	@XStreamAlias("EnoviaValidation")
	private String flagValidation;
	
}