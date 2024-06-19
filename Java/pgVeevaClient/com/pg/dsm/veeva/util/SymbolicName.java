/*
 **   SymbolicName.java
 **   Description - Introduced as part of Veeva integration.      
 **   Enum to capture symbolic name
 **
 */
package com.pg.dsm.veeva.util;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.PropertyUtil;

import matrix.db.Context;

public enum SymbolicName {
	ARTWORK("type_pgArtwork"),
	IPMSPECIFICATION("policy_IPMSpecification"),
	ESERVICEPRODUCTION("vault_eServiceProduction"),
	CHANGEORDER("type_ChangeOrder"),
	FASTTRACKCHANGE("policy_FasttrackChange");
	private final String _name;
	SymbolicName(String paramString) { this._name = paramString; }
	
	public String getType(Context context) throws FrameworkException { 
		return FrameworkUtil.getAliasForAdmin(context, DomainConstants.SELECT_TYPE, PropertyUtil.getSchemaProperty(context, this._name), true); 
	}
	
	public String getPolicy(Context context) throws FrameworkException { 
		return FrameworkUtil.getAliasForAdmin(context, DomainConstants.SELECT_POLICY, PropertyUtil.getSchemaProperty(context, this._name), true); 
	}
	
	public String getVault(Context context) throws FrameworkException { 
		return FrameworkUtil.getAliasForAdmin(context, DomainConstants.SELECT_VAULT, PropertyUtil.getSchemaProperty(context, this._name), false); 
	}
	
	public String toString() { return this._name; }
}