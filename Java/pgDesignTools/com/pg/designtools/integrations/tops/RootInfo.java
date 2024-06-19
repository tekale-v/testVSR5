package com.pg.designtools.integrations.tops;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("info")
public class RootInfo {
	@XStreamAlias("action")
	private String action;
	@XStreamAlias("name")
	private String name;
	@XStreamAlias("user")
	private String user;
	@XStreamAlias("extType")
	private String extType;
	@XStreamAlias("extName")
	private String extName;
	@XStreamAlias("extRevision")
	private String extRevision;
	@XStreamAlias("extAddlInfo")
	private String extAddlInfo;
	@XStreamAlias("extDesignDesc")
	private String extDesignDesc;
	@XStreamAlias("extSecPackConfig")
	private String extSecPackConfig;
	
	public RootInfo() {
		super();
	}
	public RootInfo(String name, String user, String action, String extType,
			String extName, String extRevision, String extAddlInfo,String extDesignDesc, String extSecPackConfig) {
		super();
		this.action = action;
		this.name = name;
		this.user = user;
		this.extType = extType;
		this.extName = extName;
		this.extRevision = extRevision;
		this.extAddlInfo = extAddlInfo;
		this.extDesignDesc = extDesignDesc;
		this.extSecPackConfig = extSecPackConfig;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getExtType() {
		return extType;
	}
	public void setExtType(String extType) {
		this.extType = extType;
	}
	public String getExtName() {
		return extName;
	}
	public void setExtName(String extName) {
		this.extName = extName;
	}
	public String getExtRevision() {
		return extRevision;
	}
	public void setExtRevision(String extRevision) {
		this.extRevision = extRevision;
	}
	public String getExtAddlInfo() {
		return extAddlInfo;
	}
	public void setExtAddlInfo(String extAddlInfo) {
		this.extAddlInfo = extAddlInfo;
	}
	public String getExtDesignDesc() {
		return extDesignDesc;
	}
	public void setExtDesignDesc(String extDesignDesc) {
		this.extDesignDesc = extDesignDesc;
	}
	public String getExtSecPackConfig() {
		return extSecPackConfig;
	}
	public void setExtSecPackConfig(String extSecPackConfig) {
		this.extSecPackConfig = extSecPackConfig;
	}
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [" +  name + ", " + user + ", " + action + ", " + extType + ", " + extName + ", " + extRevision + ", " + extAddlInfo + "]";
	}
}
