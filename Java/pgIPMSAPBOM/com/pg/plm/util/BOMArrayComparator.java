package com.pg.plm.util;

import java.util.Comparator;

import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.plm.sap.BomDataFeed;

public class BOMArrayComparator implements Comparator
{
	public int compare(Object obj1, Object obj2)
	{
		int result = 0;

		String[] str1 = (String[]) obj1;
		String[] str2 = (String[]) obj2;


		/* Sort on first element of each array (last name) */
		if ((result = str1[BomDataFeed.PHASE_NAME].compareTo(str2[BomDataFeed.PHASE_NAME])) == 0)
		{
			/* If same last name, sort on second element (first name) */
			Float in1 = null;
			if(UIUtil.isNotNullAndNotEmpty(str1[BomDataFeed.FIND_NUMBER]) && !(str1[BomDataFeed.FIND_NUMBER].contains("#DENIED!")))
			{
				 in1=Float.valueOf(str1[BomDataFeed.FIND_NUMBER]);
			}
			Float in2 = null;
			if(UIUtil.isNotNullAndNotEmpty(str2[BomDataFeed.FIND_NUMBER]) && !(str2[BomDataFeed.FIND_NUMBER].contains("#DENIED!")))
			{
				 in1=Float.valueOf(str2[BomDataFeed.FIND_NUMBER]);
			}
			if(null != in1 && null != in2)
				result = in1.compareTo(in2);
		}

		return result;
	}
}
