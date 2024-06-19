package com.pg.designtools.integrations.tops;

import java.io.IOException;

import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.designtools.datamanagement.CommonProductData;
import com.pg.designtools.datamanagement.DataConstants;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import tops.Dims;
import matrix.db.Attribute;
import matrix.db.AttributeList;
import matrix.db.AttributeType;

@XStreamAlias("priPack")
public class OutputPriPack implements IMappableComponent {	
	@XStreamAlias("dims")
	private Dims dims;
	@XStreamAlias("info")
	private OutputInfo info;
	@XStreamAlias("type")
	private String type;
		
	public OutputPriPack() {
		super();
		
	}
	public Dims getDims() {
		return dims;
	}
	public void setDims(Dims dims) {
		this.dims = dims;
	}
	public OutputInfo getInfo() {
		return info;
	}
	public void setInfo(OutputInfo info) {
		this.info = info;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [" +  dims + ", " + info + ", " + type +"]";
	}
	
	//Declarations
	//Mapping for MCOP 
	private AttributeList mappingAttributes;
	String sLenUnits;
	String sGeometryBodyInterfaceName;
	
	/**
	 * Create a list of attribute values from xml tag logic
	 * @throws Exception 
	 * 
	 */
	public void processMappingAttributes() throws FrameworkException, IOException{
		
		determineLenUnitOfMeasure();
		determineGeometryBodyInterfaceName();
		processGeometryBodyInterfaces();
		
	}
	
	private void determineLenUnitOfMeasure() {
		sLenUnits = getDims().getLenUnits();
		if(null != sLenUnits && !"".equalsIgnoreCase(sLenUnits) && sLenUnits.equalsIgnoreCase(DataConstants.UOM_MM)){
			sLenUnits=DataConstants.UOM_MILLIMETER;
		}
	}
	
	private void determineGeometryBodyInterfaceName() {
		
		String sGeometryShape = type;
		String sBodyShape = dims.getBodyShape();
		
		if("NA".equalsIgnoreCase(sBodyShape))
			sBodyShape = "";
		
		if(UIUtil.isNotNullAndNotEmpty(sGeometryShape))
			sGeometryBodyInterfaceName = "pgTOPS"+sGeometryShape+sBodyShape;
	}
		
	/**
	 * @return an AttributeList of mapping attributes defined 
	 * in processMappingAttributes and called by mapAttributes(node)
	 */
	public AttributeList getMappingAttributes() {	
		return mappingAttributes;	
	}
	
	private void processGeometryBodyInterfaces() {
		mappingAttributes = new AttributeList();
		CommonProductData objectCPD = new CommonProductData();
		mappingAttributes.addElement(new Attribute(new AttributeType(DataConstants.ATTRIBUTE_PG_DIMENSTION_UOM),DataConstants.UOM_MILLIMETER));
		if(UIUtil.isNotNullAndNotEmpty(sGeometryBodyInterfaceName)) {
			switch (sGeometryBodyInterfaceName) {
			
			 case "pgTOPSCan" :	
				 mappingAttributes.addElement(new Attribute(new AttributeType(DataConstants.ATTRIBUTE_PG_DIAMETER), objectCPD.convertDimensionToMM(dims.getDiameter(),sLenUnits)));
				 break;
			 case "pgTOPSTubRound" :	
				 mappingAttributes.addElement(new Attribute(new AttributeType(DataConstants.ATTRIBUTE_PG_TOPDIAMETER), objectCPD.convertDimensionToMM(dims.getTopDiameter(),sLenUnits)));
				 mappingAttributes.addElement(new Attribute(new AttributeType(DataConstants.ATTRIBUTE_PG_BOTTOMDIAMETER), objectCPD.convertDimensionToMM(dims.getBottomDiameter(),sLenUnits)));
				 mappingAttributes.addElement(new Attribute(new AttributeType(DataConstants.ATTRIBUTE_PG_PITCH), objectCPD.convertDimensionToMM(dims.getPitch(),sLenUnits)));
				 break;
			 case "pgTOPSTubRectangular" :	
				 mappingAttributes.addElement(new Attribute(new AttributeType(DataConstants.ATTRIBUTE_PG_TOPDEPTH), objectCPD.convertDimensionToMM(dims.getTopLength(),sLenUnits)));
				 mappingAttributes.addElement(new Attribute(new AttributeType(DataConstants.ATTRIBUTE_PG_TOPWIDTH), objectCPD.convertDimensionToMM(dims.getTopWidth(),sLenUnits)));
				 mappingAttributes.addElement(new Attribute(new AttributeType(DataConstants.ATTRIBUTE_PG_BOTTOMWIDTH), objectCPD.convertDimensionToMM(dims.getBotWidth(),sLenUnits)));
				 mappingAttributes.addElement(new Attribute(new AttributeType(DataConstants.ATTRIBUTE_PG_PITCH), objectCPD.convertDimensionToMM(dims.getPitch(),sLenUnits)));
				 break;
			 case "pgTOPSBottleRound" :	
				 mappingAttributes.addElement(new Attribute(new AttributeType(DataConstants.ATTRIBUTE_PG_BODYDIAMETER), objectCPD.convertDimensionToMM(dims.getBodyDiameter(),sLenUnits)));
				 mappingAttributes.addElement(new Attribute(new AttributeType(DataConstants.ATTRIBUTE_PG_NECKDIAMETER), objectCPD.convertDimensionToMM(dims.getNeckDiameter(),sLenUnits)));
				 mappingAttributes.addElement(new Attribute(new AttributeType(DataConstants.ATTRIBUTE_PG_NECKHEIGHT), objectCPD.convertDimensionToMM(dims.getNeckHgt(),sLenUnits)));
				 mappingAttributes.addElement(new Attribute(new AttributeType(DataConstants.ATTRIBUTE_PG_SHOULDERHEIGHT), objectCPD.convertDimensionToMM(dims.getShHgt(),sLenUnits)));
				 break;
			 case "pgTOPSBottleRectangular":
			 case "pgTOPSBottleOval" :
				 mappingAttributes.addElement(new Attribute(new AttributeType(DataConstants.ATTRIBUTE_PG_BODYDEPTH), objectCPD.convertDimensionToMM(dims.getBodyLength(),sLenUnits)));
				 mappingAttributes.addElement(new Attribute(new AttributeType(DataConstants.ATTRIBUTE_PG_BODYWIDTH), objectCPD.convertDimensionToMM(dims.getBodyWidth(),sLenUnits)));
				 mappingAttributes.addElement(new Attribute(new AttributeType(DataConstants.ATTRIBUTE_PG_NECKDIAMETER), objectCPD.convertDimensionToMM(dims.getNeckDiameter(),sLenUnits)));
				 mappingAttributes.addElement(new Attribute(new AttributeType(DataConstants.ATTRIBUTE_PG_SHOULDERHEIGHT), objectCPD.convertDimensionToMM(dims.getShHgt(),sLenUnits)));
				 break;
			 case "pgTOPSFRADimensions" :	
				 break;
				 
			 default:
			}
		}		
	}	
}
