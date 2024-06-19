/*
 * Added by (DSM-Sogeti) for 22x.02 Defect - 52113
 * 
 **/
package com.pg.dsm.ui.table.market_clearance;

import java.util.List;

public class MarketClearanceTable {
    List<MarketClearanceRow> marketClearanceRowList;

    public MarketClearanceTable(List<MarketClearanceRow> marketClearanceRowList) {
        this.marketClearanceRowList = marketClearanceRowList;
    }

    public List<MarketClearanceRow> getMarketClearanceRowList() {
        return marketClearanceRowList;
    }

    @Override
    public String toString() {
        return "MarketClearanceTable{" +
                "marketClearanceRowList=" + marketClearanceRowList +
                '}';
    }
}
