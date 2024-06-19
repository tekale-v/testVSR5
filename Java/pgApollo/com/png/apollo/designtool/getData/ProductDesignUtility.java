/*
 * Added by APOLLO Team
 * For CATIA Web Services
 */

package com.png.apollo.designtool.getData;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.png.apollo.pgApolloConstants;

import matrix.util.Pattern;
import matrix.util.StringList;

public class ProductDesignUtility 
{
	ProductDesignUtility() {
	}
	/**
	 * Method to fetch connected VPMReference objects of APP
	 * @param context
	 * @param sAPPObjectId
	 * @return MapList
	 */
	public static MapList getConnectedPhysicalProduct(matrix.db.Context context, String sAPPObjectId) throws FrameworkException 
	{
		MapList mlPhysicalProduct = new MapList();
		if(UIUtil.isNotNullAndNotEmpty(sAPPObjectId))
		{
			StringList busSelects = new StringList();
			busSelects.add(DomainConstants.SELECT_ID);
			busSelects.add(pgApolloConstants.SELECT_PHYSICAL_ID);
			busSelects.add(DomainConstants.SELECT_NAME);
			
			StringList relSelects = new StringList();
			relSelects.add(DomainRelationship.SELECT_ID);
			
			DomainObject domAPP = DomainObject.newInstance(context, sAPPObjectId);
			mlPhysicalProduct = domAPP.getRelatedObjects(context,//context
					pgApolloConstants.RELATIONSHIP_PART_SPECIFICATION,		// relationship pattern
					pgApolloConstants.TYPE_VPMREFERENCE,				// type pattern
					busSelects,					// object selects
					relSelects,					// relationship selects
					false,							// to direction
					true,							// from direction
					(short) 0,						// recursion level
					DomainConstants.EMPTY_STRING, // object where clause
					DomainConstants.EMPTY_STRING,// relationship where clause
					0);								// objects Limit
		}
		return mlPhysicalProduct;
	}
	
	/**
	 * Method to get expand view of Physical Product along with the 3DShape, Drawing and other child physical products
	 * @param context
	 * @param sObjectId - Object ID
	 * @return MapList
	 */
	public static MapList expandPhysicalProduct(matrix.db.Context context, String sObjectId) throws FrameworkException 
	{
		MapList mlPhysicalProduct = new MapList();
		if(UIUtil.isNotNullAndNotEmpty(sObjectId))
		{
			StringList busSelects = new StringList();
			busSelects.add(DomainConstants.SELECT_ID);
			busSelects.add(pgApolloConstants.SELECT_PHYSICAL_ID);
			busSelects.add(DomainConstants.SELECT_NAME);
			busSelects.add(pgApolloConstants.SELECT_HAS_READ_ACCESS);
			
			StringList relSelects = new StringList();
			relSelects.add(DomainRelationship.SELECT_ID);
			
			Pattern relPattern = new Pattern(pgApolloConstants.RELATIONSHIP_VPMINSTANCE);
			relPattern.addPattern(pgApolloConstants.RELATIONSHIP_VPMRepInstance);
			relPattern.addPattern(pgApolloConstants.RELATIONSHIP_PART_SPECIFICATION);
			
			Pattern typePattern = new Pattern(pgApolloConstants.TYPE_VPMREFERENCE);
			typePattern.addPattern(pgApolloConstants.TYPE_DRAWING);
			typePattern.addPattern(pgApolloConstants.TYPE_3DSHAPE);
			
			DomainObject domVPMRef = DomainObject.newInstance(context, sObjectId);
			mlPhysicalProduct = domVPMRef.getRelatedObjects(context,//context
					relPattern.getPattern(),		// relationship pattern
					typePattern.getPattern(),				// type pattern
					busSelects,					// object selects
					relSelects,					// relationship selects
					false,							// to direction
					true,							// from direction
					(short) 0,						// recursion level
					DomainConstants.EMPTY_STRING, // object where clause
					DomainConstants.EMPTY_STRING,// relationship where clause
					0);//object limit
		}
		return mlPhysicalProduct;
	}
}
