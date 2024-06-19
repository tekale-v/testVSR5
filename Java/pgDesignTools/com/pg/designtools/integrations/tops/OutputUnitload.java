package com.pg.designtools.integrations.tops;

import java.io.IOException;

import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.designtools.datamanagement.CommonProductData;
import com.pg.designtools.datamanagement.DataConstants;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import matrix.db.Attribute;
import matrix.db.AttributeList;
import matrix.db.AttributeType;

@XStreamAlias("unitload")
public class OutputUnitload implements IMappableComponent{
	@XStreamAlias("name")
	private String name;
	@XStreamAlias("dims")
	private Dims dims;
	@XStreamAlias("info")
	private OutputUnitloadInfo info;
	@XStreamAlias("palType")
	private String palType;
	
	public OutputUnitload() {
		super();
	}
	public OutputUnitload( String name) {
		super();
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public Dims getDims() {
		return dims;
	}
	public void setDims(Dims dims) {
		this.dims = dims;
	}
	public OutputUnitloadInfo getInfo() {
		return info;
	}
	public void setInfo(OutputUnitloadInfo info) {
		this.info = info;
	}
	public String getPalType() {
		return palType;
	}
	public void setPalType(String palType) {
		this.palType = palType;
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [" + name + ", " + dims + ", " + info +"]";
	}
	
	//data processing for attributes to be mapped to 3DX
	private AttributeList mappingAttributes;
	String sLenUnits;
	
	
	private void determineLenUnitOfMeasure() {
		sLenUnits = getDims().getLenUnits();
		
		if(null != sLenUnits && !"".equalsIgnoreCase(sLenUnits) && sLenUnits.equalsIgnoreCase(DataConstants.UOM_MM)){
			sLenUnits=DataConstants.UOM_MILLIMETER;
		}
	}
	
	/**
	 * Create a list of attribute values from xml tag logic
	 * @throws IOException 
	 * @throws FrameworkException 
	 * @throws Exception 
	 * 
	 */
	public void processMappingAttributes() throws FrameworkException, IOException{
		determineLenUnitOfMeasure();
		CommonProductData objectCPD = new CommonProductData();
		mappingAttributes = new AttributeList();
		if(UIUtil.isNotNullAndNotEmpty(palType))
		{
			mappingAttributes.addElement(new Attribute(new AttributeType(DataConstants.ATTR_PG_PALLETTYPE),palType));
		}
		
		mappingAttributes.addElement(new Attribute(new AttributeType(DataConstants.ATTRIBUTE_PG_DIMENSTION_UOM),sLenUnits));
		
		if(info!=null)
		{
			mappingAttributes.addElement(new Attribute(new AttributeType(DataConstants.ATTR_PG_STACKING_PATTERN_TYPE),info.getPattern()));	
			mappingAttributes.addElement(new Attribute(new AttributeType(DataConstants.ATTR_PG_CUBE_EFFECIENCY),info.getCubicEff().substring(0,info.getCubicEff().length() -1)));
			mappingAttributes.addElement(new Attribute(new AttributeType(DataConstants.ATTR_AREA_EFFICIENCY),info.getAreaEff().substring(0,info.getAreaEff().length() -1)));
			mappingAttributes.addElement(new Attribute(new AttributeType(DataConstants.ATTRIBUTE_LAYERS_PERTRANSPORTUNIT),info.getLayersPerLoad()));
			mappingAttributes.addElement(new Attribute(new AttributeType(DataConstants.ATTRIBUTE_CUSTOMER_UNITS_PRELAYER),info.getShippersPerlayer()));
			
			mappingAttributes.addElement(new Attribute(new AttributeType(DataConstants.ATTR_OVER_HANG_ACTUAL_LENGTH),objectCPD.convertDimensionToMM(info.getMaxOHLen(),DataConstants.UOM_INCHES)));
			mappingAttributes.addElement(new Attribute(new AttributeType(DataConstants.ATTR_OVER_HANG_ACTUAL_WIDTH),objectCPD.convertDimensionToMM(info.getMaxOHWid(),DataConstants.UOM_INCHES)));	
			mappingAttributes.addElement(new Attribute(new AttributeType(DataConstants.ATTR_UNDER_HANG_ACTUAL_LENGTH),objectCPD.convertDimensionToMM(info.getMaxUHLen(),DataConstants.UOM_INCHES)));
			mappingAttributes.addElement(new Attribute(new AttributeType(DataConstants.ATTR_UNDER_HANG_ACTUAL_WIDTH),objectCPD.convertDimensionToMM(info.getMaxUHWid(),DataConstants.UOM_INCHES)));
		}
	}
	
	/**
	 * @return an AttributeList of mapping attributes defined 
	 * in processMappingAttributes and called by mapAttributes(node)
	 */
	public AttributeList getMappingAttributes() {
		
		return this.mappingAttributes;
		
	}
	
	public String getShipperq()
	{
		if(info!=null)
			return info.getShippers();
		else
			return "1";	
	}

}
