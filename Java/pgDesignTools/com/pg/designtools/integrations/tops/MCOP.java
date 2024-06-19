package com.pg.designtools.integrations.tops;

import com.pg.designtools.datamanagement.DataConstants;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("MCOP")
public class MCOP {
	@XStreamAlias("depth")
	private String depth;
	@XStreamAlias("width")
	private String width;
	@XStreamAlias("height")
	private String height;
	@XStreamAlias("qty")
	private String qty;
	
	public MCOP() {
		super();
	}
	
	public String getSchemaName() {
		return DataConstants.SCHEMA_TYPE_PG_MASTERCONSUMERUNIT;
	}
	
	public MCOP(String depth, String width, String height, String qty) {
		super();
		this.depth = depth;
		this.width = width;
		this.height = height;
		this.qty = qty;
	}
	public String getdepth() {
		return depth;
	}
	public void setdepth(String depth) {
		this.depth = depth;
	}
	public String getwidth() {
		return width;
	}
	public void setwidth(String width) {
		this.width = width;
	}
	public String getheight() {
		return height;
	}
	public void setheight(String height) {
		this.height = height;
	}
	public String getqty() {
		return qty;
	}
	public void setqty(String qty) {
		this.qty = qty;
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [" + depth + ", " + width + ", " + height + ", " + qty + "]";
	}
}
