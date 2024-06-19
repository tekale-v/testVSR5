/*
* Java File Name: MarketBean.java
* Created By: PLM DSM-2018x.6 - Sogeti
* Clone From/Reference: NA
* Purpose:  This File contains market details bean.
* Change History: Added by DSM(Sogeti)-2018x.6 (req id: 8802) for Market Clearance.
*/
package com.pg.v4.util.marketclearance.bean;

import java.util.List;
import java.util.Map;
import com.matrixone.apps.domain.DomainConstants;

public class MarketBean {
   
    private String connectionId;
    
    private List<MarketRegistrationBean> marketRegistrationBeans;
    
    /**
     * This constructor updated private variable
     * @param objMap
     * @param marketRegistrationBeans
     */
    public MarketBean(Map<?, ?> objMap, List<MarketRegistrationBean> marketRegistrationBeans) {
        this.marketRegistrationBeans = marketRegistrationBeans;
        this.connectionId=(String)objMap.get(DomainConstants.SELECT_RELATIONSHIP_ID);
    }

   /**
     * This method return list of market registration's in bean format
     * @return
     */
    public List<MarketRegistrationBean> getMarketRegistrationBeans() {
		return marketRegistrationBeans;
	}

	/**
	 * This method update list of market registration's
	 * @param marketRegistrationBeans
	 */
	public void setMarketRegistrationBeans(List<MarketRegistrationBean> marketRegistrationBeans) {
		this.marketRegistrationBeans = marketRegistrationBeans;
	}

	/**
	 * This method retrieve market connection id
	 * @return
	 */
	public String getConnectionId() {
		return connectionId;
	}

	/**
	 * This method update market connection id
	 * @param connectionId 
	 */
	public void setConnectionId(String connectionId) {
		this.connectionId = connectionId;
	}
}
