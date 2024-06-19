package com.pg.dsm.ui.table.helper;

import java.util.Map;

import com.matrixone.apps.domain.util.MapList;

public class EnoviaTableHelper {
	
	private EnoviaTableHelper() {}
	
	/**
	 * @param relBusObjList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static MapList getFilterTableObjList(MapList relBusObjList) {
		MapList retBusObjList = new MapList();
		if(relBusObjList != null && !relBusObjList.isEmpty()){
			int iSize = relBusObjList.size();
			Map<?,?> mMap ;
			for (int i = 0; i < iSize; i++) {
				mMap = (Map<?,?>) relBusObjList.get(i);
				if(mMap.containsKey("id")) {
					retBusObjList.add(mMap);
				} 				
			}
		}
		return retBusObjList;		
	}
}
