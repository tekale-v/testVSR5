package com.pg.designtools.integrations.tops;

import java.io.IOException;

import com.matrixone.apps.domain.util.FrameworkException;

import matrix.db.AttributeList;

public interface IMappableComponent {
	
	/**
	 * Create a list of attribute values from xml tag logic
	 * @throws Exception 
	 * 
	 */
	public void processMappingAttributes() throws FrameworkException, IOException;
	
	/**
	 * @return an AttributeList of mapping attributes defined 
	 * in processMappingAttributes and called by mapAttributes(node)
	 */
	public AttributeList getMappingAttributes();

}
