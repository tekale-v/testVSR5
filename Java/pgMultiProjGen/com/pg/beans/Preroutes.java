package com.pg.beans;
import java.io.Serializable;
import java.util.List;

public class Preroutes implements Serializable{

	List <Member> lMemberPOACreation;
	List <Member> lMemberFPCCodeCreation;
	List <Member> lMemberDesign;
	
	//Added by RTA Capgemini Offshore for 18x.6-OctCW Req 38995 Starts
	String strSearchPOARouteTemplateDisplay;
	String strSearchFPCRouteTemplateDisplay;
	String strSearchDesignRouteTemplateDisplay;
	//Added by RTA Capgemini Offshore for 18x.6-OctCW Req 38995 Ends
	
	public List<Member> getlMemberPOACreation() {
		return lMemberPOACreation;
	}
	public void setlMemberPOACreation(List<Member> lMemberPOACreation) {
		this.lMemberPOACreation = lMemberPOACreation;
	}
	public List<Member> getlMemberFPCCodeCreation() {
		return lMemberFPCCodeCreation;
	}
	public void setlMemberFPCCodeCreation(List<Member> lMemberFPCCodeCreation) {
		this.lMemberFPCCodeCreation = lMemberFPCCodeCreation;
	}
	public List<Member> getlMemberDesign() {
		return lMemberDesign;
	}
	public void setlMemberDesign(List<Member> lMemberDesign) {
		this.lMemberDesign = lMemberDesign;
	}
	//Added by RTA Capgemini Offshore for 18x.6-OctCW Req 38995 Starts
	public String getPOASearchRouteTemplateDisplay() {
		return strSearchPOARouteTemplateDisplay;
	}
	public void setPOASearchRouteTemplateDisplay(String SearchPOARouteTemplateDisplay) {
		this.strSearchPOARouteTemplateDisplay = SearchPOARouteTemplateDisplay;
	}
	public String getFPCSearchRouteTemplateDisplay() {
		return strSearchFPCRouteTemplateDisplay;
	}
	public void setFPCSearchRouteTemplateDisplay(String SearchFPCRouteTemplateDisplay) {
		this.strSearchFPCRouteTemplateDisplay = SearchFPCRouteTemplateDisplay;
	}
	public String getDesignSearchRouteTemplateDisplay() {
		return strSearchDesignRouteTemplateDisplay;
	}
	public void setDesignSearchRouteTemplateDisplay(String SearchDesignRouteTemplateDisplay) {
		this.strSearchDesignRouteTemplateDisplay = SearchDesignRouteTemplateDisplay;
	}
	//Added by RTA Capgemini Offshore for 18x.6-OctCW Req 38995 Ends
}
