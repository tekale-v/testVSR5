package com.pg.designtools.integrations.tops;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("values")
public class Values {
	@XStreamAlias("value1")
	private String value1;
	@XStreamAlias("value2")
	private String value2;
	@XStreamAlias("value3")
	private String value3;
	@XStreamAlias("value4")
	private String value4;
	@XStreamAlias("value5")
	private String value5;
	
	public Values() {
		super();
	}
	public Values(String value1, String value2, String value3, String value4, String value5) {
		super();
		this.value1 = value1;
		this.value2 = value2;
		this.value3 = value3;
		this.value4 = value4;
		this.value5 = value5;
	}
	public String getValue1() {
		return value1;
	}
	public void setValue1(String value1) {
		this.value1 = value1;
	}
	public String getValue2() {
		return value2;
	}
	public void setValue2(String value2) {
		this.value2 = value2;
	}
	public String getValue3() {
		return value3;
	}
	public void setValue3(String value3) {
		this.value3 = value3;
	}
	public String getValue4() {
		return value4;
	}
	public void setValue4(String value4) {
		this.value4 = value4;
	}
	public String getValue5() {
		return value5;
	}
	public void setValue5(String value5) {
		this.value5 = value5;
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [" + value1 + ", " + value2 + ", " + value3 + ", " + value4 + ", " + value5 + "]";
	}
}
