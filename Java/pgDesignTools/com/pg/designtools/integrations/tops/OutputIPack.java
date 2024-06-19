package com.pg.designtools.integrations.tops;

import java.io.IOException;

import com.matrixone.apps.domain.util.FrameworkException;
import com.pg.designtools.datamanagement.DataConstants;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import matrix.db.Attribute;
import matrix.db.AttributeList;
import matrix.db.AttributeType;
import tops.Dims;
import tops.OutputInfo;

@XStreamAlias("priPack")
public class OutputIPack implements IMappableComponent{	
	@XStreamAlias("dims")
	private Dims dims;
	@XStreamAlias("info")
	private OutputInfo info;
	
	public OutputIPack() {
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
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [" +  dims + ", " + info + "]";
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
	 * @throws Exception 
	 * 
	 */
	public void processMappingAttributes() throws FrameworkException, IOException{
		determineLenUnitOfMeasure();
		mappingAttributes = new AttributeList();		
		mappingAttributes.addElement(new Attribute(new AttributeType(DataConstants.ATTRIBUTE_PG_DIMENSTION_UOM),DataConstants.UOM_MILLIMETER));
	}
	
	/**
	 * @return an AttributeList of mapping attributes defined 
	 * in processMappingAttributes and called by mapAttributes(node)
	 */
	public AttributeList getMappingAttributes() {
		
		return this.mappingAttributes;
		
	}
	
	public String getPriPackq()
	{
		if(info!=null)
			return info.getPriPacks();
		else
			return "1";	
	}

}
