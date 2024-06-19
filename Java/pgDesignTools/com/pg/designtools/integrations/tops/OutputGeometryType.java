package com.pg.designtools.integrations.tops;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("GeometryType")
public class OutputGeometryType {
	@XStreamAlias("type")
	private String type;
	
	public OutputGeometryType() {
		super();
	}
	public OutputGeometryType(String type) {
		super();
		this.type = type;
	}
	
	public String gettype() {
		return type;
	}
	public void setPattern(String type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [" + type + "]";			
	}
}
