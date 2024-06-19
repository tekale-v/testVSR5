package com.pg.designtools.util;

import com.dassault_systemes.smaslm.matrix.common.AccessUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import matrix.db.Context;

public class AccessManagement extends AccessUtil {

	public AccessManagement() {
		super();
	}

	/**
	 * This method would be invoked to add access of the parent object on the target
	 * object args[0] is the source object id--whose access is to be added args[1]
	 * is the target object id, on which the access is to be added args[2] is the
	 * comma separated access list like read,show
	 * 
	 * @param context
	 * @param args
	 * @throws FrameworkException
	 */
	public void addAccess(Context context, String[] args) throws FrameworkException {
		inheritAccess(context, args[0], args[1], args[2]);
	}

	/**
	 * This method would add read,show access of the parent object on the target
	 * object args[0] is the source object id--whose access is to be added args[1]
	 * is the target object id, on which the access is to be added
	 * 
	 * @param context
	 * @param args
	 * @throws FrameworkException
	 */
	public void addIPAccess(Context context, String[] args) throws FrameworkException {
		AccessUtil.inheritAccess(context, args[0], args[1]);
	}

	/**
	 * This method would add read,show,checkout access of the parent object on the
	 * target object args[0] is the source object id--whose access is to be added
	 * args[1] is the target object id, on which the access is to be added
	 * 
	 * @param context
	 * @param args
	 * @throws FrameworkException
	 */
	public void addEnhancedIPAccess(Context context, String[] args) throws FrameworkException {
		String strAccess = "read,show,checkout";
		inheritAccess(context, args[0], args[1], strAccess);
	}

	/**
	 * This method would fetch the access on the object
	 * 
	 * @param context
	 * @param String
	 *            objectId
	 * @return String of objectIds added as access
	 * @throws FrameworkException
	 */
	public String getAccess(Context context, String strObjectId) throws FrameworkException {
		String strCommand = "print bus $1 select access.businessobject dump";
		return MqlUtil.mqlCommand(context, strCommand, strObjectId);
	}

	/**
	 * This method would invoke revoke of access of the parent object from the
	 * target object args[0] is the source object id--whose access needs to be
	 * revoked args[1] is the target object id,on which the access is to be revoked
	 * 
	 * @param context
	 * @param args
	 * @throws FrameworkException
	 */
	public void removeAccess(Context context, String[] args) throws FrameworkException {
		String strAccess = getAccess(context, args[1]);

		if (UIUtil.isNotNullAndNotEmpty(strAccess) && strAccess.contains(args[0])) {
			revokeAccess(context, args[0], args[1]);
		}
	}

	/**
	 * This method would add given access of the parent object on the target object
	 * 
	 * @param context
	 * @param strSourceObjId
	 * @param strTargetObjId
	 * @param strAccessList
	 * @throws FrameworkException
	 */
	private void inheritAccess(Context context, String strSourceObjId, String strTargetObjId, String strAccessList)
			throws FrameworkException {
		String strCommand = "mod bus $1 add access bus $2 as $3";
		MqlUtil.mqlCommand(context, strCommand, strTargetObjId, strSourceObjId, strAccessList);
	}

	/**
	 * This method would remove access of the parent object from the target object
	 * 
	 * @param context
	 * @param strSourceObjId
	 * @param strTargetObjId
	 * @throws FrameworkException
	 */
	private void revokeAccess(Context context, String strSourceObjId, String strTargetObjId) throws FrameworkException {
		String strCommand = "mod bus $1 remove access bus $2";
		MqlUtil.mqlCommand(context, strCommand, strTargetObjId, strSourceObjId);
	}

}
