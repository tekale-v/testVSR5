package com.pg.designtools.integrations.tops;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("TopsData")
public class UnitLoadAnalysisRoot {

	@XStreamAlias("analysis0")
	private UnitLoadAnalysis analysis;
	
	public UnitLoadAnalysisRoot() {
		super();
	}
	
	public UnitLoadAnalysis getAnalysis() {
		return analysis;
	}
	public void setAnalysis(UnitLoadAnalysis info) {
		this.analysis = info;
	}
	
	public String toString() {
		return this.getClass().getSimpleName() + " [" + "]";
	}
}
