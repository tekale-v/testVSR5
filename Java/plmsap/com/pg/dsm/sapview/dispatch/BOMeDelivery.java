package com.pg.dsm.sapview.dispatch;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.StringList;

/**
 * @author DSM(Sogeti) - Added for 2018x.6 Dec CW SAP Requirement #40804,#40805.
 */
public class BOMeDelivery {
	private Context ctx;
	private String objId;

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * @param context
	 * @param oId
	 */
	public BOMeDelivery(Context context, String oId) {
		this.ctx = context;
		this.objId = oId;
	}

	/**
	 * @return
	 */
	public boolean deliverToSAP() {
		String[] strArgs = new String[6];
		strArgs[0] = this.objId;
		boolean isPartDeliver = Boolean.TRUE;
		DomainObject doTS;
		try {
			doTS = DomainObject.newInstance(this.ctx, this.objId);

			Map infoMap = doTS.getInfo(this.ctx, this.getSelecTables());
			String strCurrent = (String) infoMap.get(DomainConstants.SELECT_CURRENT);
			strArgs[1] = (String) infoMap.get(DomainConstants.SELECT_TYPE);
			strArgs[2] = (String) infoMap.get(DomainConstants.SELECT_NAME);
			strArgs[3] = (String) infoMap.get(DomainConstants.SELECT_REVISION);
			strArgs[4] = pgV3Constants.DUMMY_ARG;// need to add plant ids
			strArgs[5] = pgV3Constants.DUMMY_ARG;

			if (strCurrent.equalsIgnoreCase(pgV3Constants.STATE_RELEASE)) {

				int iReturn = JPO.invoke(this.ctx, "pgIPMUtil_Deferred", null, "doBOMeDelivery", strArgs);

				if (iReturn == 0) {
					isPartDeliver = Boolean.TRUE;
				} else {
					isPartDeliver = Boolean.FALSE;
				}

			}
		} catch (MatrixException e) {
			logger.log(Level.WARNING, null, e);
		}
		return isPartDeliver;
	}

	/**
	 * @return
	 */
	private StringList getSelecTables() {
		StringList list = new StringList();
		list.add(DomainConstants.SELECT_TYPE);
		list.add(DomainConstants.SELECT_NAME);
		list.add(DomainConstants.SELECT_REVISION);
		list.add(DomainConstants.SELECT_CURRENT);
		return list;
	}
}
