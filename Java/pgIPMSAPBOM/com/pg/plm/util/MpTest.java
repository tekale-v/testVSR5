package com.pg.plm.util;

import java.io.File;
import java.util.Calendar;

public class MpTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
			File f = new File("c:/css-Plm test/CSS-CRP-CR0832772.1267475555831.1267542627101.xml");
			System.out.println(f.lastModified());
			
			int year = Integer.parseInt("2009");
			int month = Integer.parseInt("03");
			int date = Integer.parseInt("08");
			int hourOfDay = Integer.parseInt("12");
			int minute = Integer.parseInt("00");
			
			Calendar c = Calendar.getInstance();
			System.out.println(c.getTimeInMillis());
			c.set(year, month, date, hourOfDay, minute);
			System.out.println(c.getTimeInMillis());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
