package com.pg.designtools.integrations.tops;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("range")
public class Range {
	@XStreamAlias("range1")
	private String range1;
	@XStreamAlias("range2")
	private String range2;
	
	public Range() {
		super();
	}
	public Range(String range1, String range2) {
		super();
		this.range1 = range1;
		this.range2 = range2;
	}
	public String getRange1() {
		return range1;
	}
	public void setRange1(String range1) {
		this.range1 = range1;
	}
	public String getRange2() {
		return range2;
	}
	public void setRange2(String range2) {
		this.range2 = range2;
	}
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [" + range1 + ", " + range2 + "]";
	}
}
