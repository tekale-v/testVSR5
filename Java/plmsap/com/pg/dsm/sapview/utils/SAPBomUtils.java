package com.pg.dsm.sapview.utils;

import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.sapview.beans.bo.SAPPartBean;
import com.pg.dsm.sapview.config.SAPConfig;
import com.pg.dsm.sapview.dispatch.BOMeDelivery;

import matrix.util.StringList;

/**
 * @author DSM(Sogeti) - Added for 2018x.6 Dec CW SAP Requirement #40804,#40805.
 */
public class SAPBomUtils {

	private SAPConfig config;

	private final Logger logger = Logger.getLogger(this.getClass().getName());
	private StringList deliveredParts;
	private StringList failureParts;

	/**
	 * @param conf
	 */
	public SAPBomUtils(SAPConfig conf) {
		this.config = conf;
	}

	/**
	 * @return the deliveredParts
	 */
	public StringList getDeliveredParts() {
		return deliveredParts;
	}

	/**
	 * @return the failureParts
	 */
	public StringList getFailureParts() {
		return failureParts;
	}

	/**
	 * @param productPart
	 */
	public void sendValidPartToBomDelivery(SAPPartBean productPart) {
		this.deliveredParts = new StringList();
		this.failureParts = new StringList();

		if (productPart.isBOMeDeliveryToSAP())
			this.sendPartToSAP(productPart.getId());

		if (productPart.isBOMeDeliveryParentToSAP()) {
			StringList parentParts = new PartExpansionUtil(this.config, productPart).execute().getValidParts();
			if (parentParts != null && !parentParts.isEmpty()) {
				for (Iterator iterator = parentParts.iterator(); iterator.hasNext();) {
					this.sendPartToSAP((String) iterator.next());
				}
			}
		}
	}

	/**
	 * @param partId
	 */
	private void sendPartToSAP(String partId) {
		if (UIUtil.isNotNullAndNotEmpty(partId) && !this.config.getDeliveredParts().contains(partId)) {
			try {
				DomainObject domObj = DomainObject.newInstance(this.config.getContext(), partId);
				Map objMap = domObj.getInfo(this.config.getContext(), this.getBusSelecTables());
				String type = (String) objMap.get(DomainConstants.SELECT_TYPE);
				String name = (String) objMap.get(DomainConstants.SELECT_NAME);
				String revision = (String) objMap.get(DomainConstants.SELECT_REVISION);
				if (this.config.getProperties().getSapParentTypes().contains(type)) {

					boolean isDelivered = new BOMeDelivery(this.config.getContext(), partId).deliverToSAP();
					if (isDelivered) {
						this.config.getDeliveredParts().add(partId);
						this.deliveredParts.add(type + "|" + name + "|" + revision);
					} else {
						this.failureParts.add(type + "|" + name + "|" + revision);
					}
				}
			} catch (FrameworkException e) {
				logger.log(Level.WARNING, null, e);
			}
		}
	}

	private StringList busSelecTable;

	/**
	 * @return
	 */
	private StringList getBusSelecTables() {
		if (this.busSelecTable == null || this.busSelecTable.isEmpty()) {
			this.busSelecTable = new StringList();
			this.busSelecTable.add(DomainConstants.SELECT_TYPE);
			this.busSelecTable.add(DomainConstants.SELECT_NAME);
			this.busSelecTable.add(DomainConstants.SELECT_REVISION);
		}
		return this.busSelecTable;
	}
}
