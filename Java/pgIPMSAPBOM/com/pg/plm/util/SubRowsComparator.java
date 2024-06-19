package com.pg.plm.util;

import java.util.Comparator;

public class SubRowsComparator implements Comparator
{
	public int compare(Object obj1, Object obj2)
	{
		int result = 0;

		String[] str1 = (String[]) obj1;
		String[] str2 = (String[]) obj2;

		/* Sort on Substitute Combination Number */
		Float in1 = Float.valueOf(str1[5]);
		Float in2 = Float.valueOf(str2[5]);
		result = in1.compareTo(in2);

		return result;
	}
}
