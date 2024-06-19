package com.pg.beans;

import java.io.Serializable;
import java.util.List;


public class Project implements Serializable{

	private String Type;
	private String Name;
	private String ProjectType;
	private String Description;
	private String ProjectDate;
	private String ScheduleBasedOn;
	private String BrandDisplay;
	
	private List<String> RegionDisplay;
	private String region;
	
	private String SeachProjectDisplay;
	
	private String artworkType;
	private List<String> ArtworkTypeDisplay;
	
	private String Category;
	private String Sector;
	private String SubSector;
	private String Scale;
	private String ArtworkReadiness;
	private String ArtworkReadinessComment;
	
	private List<String> RTABusinessProjectTypeDisplay ;
	private String rtabusinessProjectType;
	
	private String MDODrivenProject;
	private List<Member> lMembers;
	private Preroutes preRouteMembers;
	

	public Preroutes getlPreRouteMembers() {
		return preRouteMembers;
	}
	public void setlPreRouteMember(Preroutes preRouteMembers) {
		this.preRouteMembers = preRouteMembers;
	}
	public List<Member> getlMembers() {
		return lMembers;
	}
	public void setlMembers(List<Member> lMembers) {
		this.lMembers = lMembers;
	}
	public String getsMDODrivenPrj() {
		return MDODrivenProject;
	}
	public void setsMDODrivenPrj(String sMDODrivenPrj) {
		this.MDODrivenProject = sMDODrivenPrj;
	}
	public String getsPrjType() {
		return ProjectType;
	}
	public void setsPrjType(String sPrjType) {
		this.ProjectType = sPrjType;
	}
	
	public String getType() {
		return Type;
	}
	public void setType(String type) {
		this.Type = type;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		this.Name = name;
	}
	public String getProjectType() {
		return ProjectType;
	}
	public void setProjectType(String projectType) {
		this.ProjectType = projectType;
	}
	public String getDescription() {
		return Description;
	}
	public void setDescription(String description) {
		this.Description = description;
		
	}
	public String getProjectDate() {
		return ProjectDate;
	}
	public void setProjectDate(String projectDate) {
		this.ProjectDate = projectDate;
	}
	public String getScheduleBasedOn() {
		return ScheduleBasedOn;
	}
	public void setScheduleBasedOn(String scheduleBasedOn) {
		this.ScheduleBasedOn = scheduleBasedOn;
	}
	public String getBrandDisplay() {
		return BrandDisplay;
	}
	public void setBrandDisplay(String brandDisplay) {
		this.BrandDisplay = brandDisplay;
	}
	public List<String> getRegionDisplay() {
		return RegionDisplay;
	}
	public void setRegionDisplay(List<String> regionDisplay) {
		this.RegionDisplay = regionDisplay;
	}
	public String getSeachProjectDisplay() {
		return SeachProjectDisplay;
	}
	public void setSeachProjectDisplay(String seachProjectDisplay) {
		this.SeachProjectDisplay = seachProjectDisplay;
	}
	public List<String> getArtworkTypeDisplay() {
		return ArtworkTypeDisplay;
	}
	public void setArtworkTypeDisplay(List<String> artworkTypeDisplay) {
		this.ArtworkTypeDisplay = artworkTypeDisplay;
	}
	public String getCategory() {
		return Category;
	}
	public void setCategory(String category) {
		this.Category = category;
	}
	public String getSector() {
		return Sector;
	}
	public void setSector(String sector) {
		this.Sector = sector;
	}
	public String getSubSector() {
		return SubSector;
	}
	public void setSubSector(String subSector) {
		this.SubSector = subSector;
	}
	public String getScale() {
		return Scale;
	}
	public void setScale(String scale) {
		this.Scale = scale;
	}
	public String getArtworkReadiness() {
		return ArtworkReadiness;
	}
	public void setArtworkReadiness(String artworkReadiness) {
		this.ArtworkReadiness = artworkReadiness;
	}
	public String getArtworkReadinessComment() {
		return ArtworkReadinessComment;
	}
	public void setArtworkReadinessComment(String artworkReadinessComment) {
		this.ArtworkReadinessComment = artworkReadinessComment;
	}
	public List<String> getRTABusinessProjectTypeDisplay() {
		return RTABusinessProjectTypeDisplay;
	}
	public void setRTABusinessProjectTypeDisplay(List<String> rTABusinessProjectTypeDisplay) {
		this.RTABusinessProjectTypeDisplay = rTABusinessProjectTypeDisplay;
	}
	public String getMDODrivenProject() {
		return MDODrivenProject;
	}
	public void setMDODrivenProject(String mDODrivenProject) {
		this.MDODrivenProject = mDODrivenProject;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getArtworkType() {
		return artworkType;
	}
	public void setArtworkType(String artworkType) {
		this.artworkType = artworkType;
	}
	public String getRtabusinessProjectType() {
		return rtabusinessProjectType;
	}
	public void setRtabusinessProjectType(String rtabusinessProjectType) {
		this.rtabusinessProjectType = rtabusinessProjectType;
	}
	
}
