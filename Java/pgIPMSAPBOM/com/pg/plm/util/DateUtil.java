package com.pg.plm.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.TimeZone;

public class DateUtil {

	 public static final String ORACLE_DATE_FORMAT = "d-MMM-yyyy";
	 public static final String MATRIX_DATE_LONG_FORMAT = "EEE MMM d, yyyy hh:mm:ss aa zzz";
	 public static final String GMT_DATE_FORMAT = "d MMM yyyy hh:mm:ss zzz";
	 public static final String MATRIX_DATE_SHORT_FORMAT = "MM/dd/yy";
	 public static final String CSS_DATE_FORMAT = "ddMMMyyyy";
	 public static final String SORTABLE_DATE_FORMAT = "yyyyMMdd";
	 public static final String CSS_DATE_TIME_FORMAT = "ddMMMyyyy HH:mm:ss '('z')'";
	 public static final String JAVA_DATE_LONG_FORMAT = "MM/dd/yyyy hh:mm aa, zzz";
	 public static final String PLM_DATE_FORMAT = "MM/dd/yyyy hh:mm:ss aa zzz";
	 public static final String PLM_DATE_FORMAT2 = "MM/dd/yyyy hh:mm:ss aa";
	
	public static DateUtil getDateUtilInstance() {
		return new DateUtil();
	}
	
	public static String convertPLMDateToSAP(String sDate ) {
        String shortdate = null;
        Date matrixdate;
        Date plmdate = null;
        if (sDate.equals("")) return "";
        try
        {
             SimpleDateFormat formatter = new SimpleDateFormat(PLM_DATE_FORMAT2);
             SimpleDateFormat newformatter = new SimpleDateFormat(SORTABLE_DATE_FORMAT);
             matrixdate = formatter.parse(sDate);

             shortdate = newformatter.format(matrixdate);
             Date dConvertedFormatted = newformatter.parse(shortdate);
             Calendar c= Calendar.getInstance();
             c.setTime(dConvertedFormatted);
//             c.setTimeZone(TimeZone.getTimeZone("GMT"));

//             c.add(Calendar.MINUTE, matrixdate.getTimezoneOffset());
             Date dTemp = c.getTime();

             String sTemp=newformatter.format(dTemp);
             
             shortdate =sTemp;
        }
      catch (Exception e)
        {
             System.out.println(e.toString());
             e.printStackTrace();
        }
        return(shortdate);
	}
	
	public String convertCssDate(String sDate) {
		String sReturn = "";
		try {
			String sMonths = "Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec";
			String[] saMonths = sMonths.split("|");
			
			Calendar c = Calendar.getInstance();
			
			//Mon May 2, 2011 12:00:00 AM EDT
			String sSubString = sDate;
			String sDay = sDate.substring(0, sDate.indexOf(" "));
			sSubString = sDate.substring(sDate.indexOf(" ")+1);
			
			String sMonth = sSubString.substring(0, sSubString.indexOf(" "));
			sSubString = sSubString.substring(sSubString.indexOf(" ")+1);
			
			String sDateVal = sSubString.substring(0, sSubString.indexOf(","));
			sSubString = sSubString.substring(sSubString.indexOf(" ")+1);
			
			String sYear = sSubString.substring(0, sSubString.indexOf(" "));
			sSubString = sSubString.substring(sSubString.indexOf(" ")+1);

			String sHour = sSubString.substring(0, sSubString.indexOf(":"));
			sSubString = sSubString.substring(sSubString.indexOf(":")+1);
			
			String sMinutes = sSubString.substring(0, sSubString.indexOf(":"));
			sSubString = sSubString.substring(sSubString.indexOf(":")+1);

			String sSeconds = sSubString.substring(0, sSubString.indexOf(" "));
			sSubString = sSubString.substring(sSubString.indexOf(" ")+1);

			String sAMPM = sSubString.substring(0, sSubString.indexOf(" "));
			sSubString = sSubString.substring(sSubString.indexOf(" "));
			
			String sTimeZone = sDate.substring(sDate.lastIndexOf(" ")+1);
		
		} catch (Exception e) {
			e.printStackTrace();
			sReturn = null;
		}
		return sReturn;
	}

    public static String getPLMDate(String strdate)
    {
         String shortdate = null;
         Date matrixdate;
         Date plmdate = null;
         if (strdate.equals("")) return "";
         try
         {
              SimpleDateFormat formatter = new SimpleDateFormat(MATRIX_DATE_LONG_FORMAT);
              SimpleDateFormat gmtformatter = new SimpleDateFormat(GMT_DATE_FORMAT);
              SimpleDateFormat newformatter = new SimpleDateFormat(PLM_DATE_FORMAT);
              matrixdate = formatter.parse(strdate);

              shortdate = newformatter.format(matrixdate);
              Date dConvertedFormatted = newformatter.parse(shortdate);
              Calendar c= Calendar.getInstance();
              c.setTime(dConvertedFormatted);
//              c.setTimeZone(TimeZone.getTimeZone("GMT"));

//              c.add(Calendar.MINUTE, matrixdate.getTimezoneOffset());
              Date dTemp = c.getTime();

              String sTemp=newformatter.format(dTemp);
              
              shortdate =sTemp.substring(0,sTemp.lastIndexOf(" ") );
         }
       catch (Exception e)
         {
              System.out.println(e.toString());
              e.printStackTrace();
         }
         return(shortdate);
    }
    
    public static String getCSSDate(String strdate)
    { 
    	
    	if (strdate.equals("")) return "";
    	
    	String cssDate = "";
    	SimpleDateFormat cssformatter = new SimpleDateFormat(CSS_DATE_FORMAT);
    	SimpleDateFormat formatter = new SimpleDateFormat(MATRIX_DATE_LONG_FORMAT);
    	Date matrixdate;
    	
    	try {
			matrixdate = cssformatter.parse(strdate);
			cssDate = formatter.format(matrixdate);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return getPLMDate(cssDate);
    }

}
