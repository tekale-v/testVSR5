/*
java Name : HtmlToExcel
Clone From/Reference :
Purpose : The main usage of this class is to bring the Rich Text Content in excel sheet.
Change History :
*/

package com.cg.richtext;
import java.io.IOException;
import java.io.InputStream;
//modified for 35760 by  RTA on 23-09-2020 -- START
import com.ibm.icu.text.Bidi;
//modified for 35760 by  RTA on 23-09-2020 -- END
import com.ibm.icu.text.BreakIterator;
import com.matrixone.apps.awl.util.BusinessUtil;
//modified for 37804 by RTA on 16-12-2020 -- START
import com.matrixone.apps.domain.util.FrameworkUtil;
//modified for 37804 by RTA on 16-12-2020 -- END
import com.matrixone.apps.domain.util.StringUtil;

import matrix.util.StringList;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.apache.poi.ss.usermodel.CellType;


public class HtmlToExcel {

    private static final int START_TAG = 0;
    private static final int END_TAG = 1;
    private static final String EMPTY_STRING = "";
    private static final String STR_S_SPACE = " ";
    private static Map<String,String> mXMLToXLSConfigMapping;
    private static final String UNDERSCORE = "XMLToXLS.UNDERSCORE_STRING";
    private static final String SLASH      = "XMLToXLS.S_SLASH_STRING";
    private static final String CLOSE_TAG  = "XMLToXLS.STR_TAG_CLOSE";
    private static final String OPEN_TAG   = "XMLToXLS.STR_TAG_OPEN";
    private static final String STR_RTL    = "XMLToXLS.MixedContent.Dir.STR_RTL";
    private static final String TEXT_NODE  = "XMLToXLS.STR_TEXT_NODE_NAME";
  //modified for 35760 by  RTA on 23-09-2020 -- START
    private static final String SPAN_LTR   = "XMLToXLS.MixedContent.Dir.STR_LTR";
  //modified for 35760 by  RTA on 23-09-2020 -- END
    //modified for 37804 by RTA on 16-12-2020 -- START
    private static StringList slTags       = new StringList(0);  
  	//modified for 37804 by RTA on 16-12-2020 -- END
  
    
    private static final Logger logger = Logger.getLogger(HtmlToExcel.class.getName());
    static
    {
	   readPropertyFile();
	}
    
    /*
	Method Purpose:     To read the properties file and store it in hashmap
	Exception details : Exception 
	*/
    private static void readPropertyFile() {
        Properties prop = new Properties();
        InputStream inputStream = null;
        mXMLToXLSConfigMapping = new HashMap<>();
        try{
        	inputStream = HtmlToExcel.class.getResourceAsStream("XMLToXLSConversionConstants.properties");
            prop.load(inputStream); 
            for (final Entry<Object, Object> entry : prop.entrySet()) {
            	mXMLToXLSConfigMapping.put((String) entry.getKey(), (String) entry.getValue());
            }
        }catch(IOException exIO) {
        	logger.log(Level.SEVERE, exIO.getMessage());
        }
        
        finally {
        	if(inputStream != null)
        	{
				try {
					inputStream.close();
				} catch (IOException e) {
					logger.log(Level.SEVERE, e.getMessage());
				}
        	}
        }
    }
    
	/*
	Method Purpose: 
	Parameter details  :cellValues should be List<RichTextDetails>,excelWorkBook should be XSSFWorkbook , 
						cellNo should be int and row should be Row.
	Exception details : No Exception
	*/
	public static Cell createCell(List<RichTextDetails> cellValues,
            XSSFWorkbook excelWorkBook, int cellNo,  Row row) {
        XSSFRichTextString cellValue = mergeTextDetails(cellValues, excelWorkBook);
        XSSFCellStyle cellStyle = excelWorkBook.createCellStyle();
        XSSFFont cellFont = excelWorkBook.createFont();
        Cell cell = row.createCell(cellNo);
        cell.setCellType(CellType.STRING);
        cellFont.setFontName(mXMLToXLSConfigMapping.get("XMLToXLS.Conversion.Workbook.Font"));
        cellStyle.setFont(cellFont);
        cell.setCellStyle(cellStyle);
        cell.setCellValue(cellValue);
        return cell;
       
    }
    
	/*
	Method Purpose: 
	Parameter details  :cellValues should be List<RichTextDetails>,excelWorkBook should be XSSFWorkbook , 
						cellNo should be int ,row should be Row and strFontType should be String.
	Exception details : No Exception
	*/
	public static Cell createCell(String cellValue,
            XSSFWorkbook excelWorkBook, int cellNo, XSSFRow row, String strFontType) {
    	XSSFCell cell = row.createCell(cellNo);
        XSSFFont cellFont = excelWorkBook.createFont();
		XSSFCellStyle cellStyle  = excelWorkBook.createCellStyle();
		cellFont.setFontName(mXMLToXLSConfigMapping.get("XMLToXLS.Conversion.Workbook.Font"));
		if("HyperLink".equalsIgnoreCase(strFontType)){
			cellFont.setColor(HSSFColor.HSSFColorPredefined.BLUE.getIndex());
			cellFont.setUnderline(Font.U_SINGLE);
        	cellStyle.setFont(cellFont);
        	cellStyle.setAlignment(HorizontalAlignment.LEFT);
        	CreationHelper createHelper = excelWorkBook.getCreationHelper();
            XSSFHyperlink link = (XSSFHyperlink)createHelper.createHyperlink(HyperlinkType.URL);
            link.setAddress(cellValue);
        	cell.setHyperlink(link);
        	cell.setCellStyle(cellStyle);
        }else if("BOLD".equalsIgnoreCase(strFontType)){
        	cellFont.setBold(true);
        }
		cellStyle.setFont(cellFont);
		cell.setCellStyle(cellStyle);
        cell.setCellType(CellType.STRING);
        cell.setCellValue(cellValue);
        cell.setAsActiveCell();
        
        return cell;
    }
    
	/*
	Method Purpose: 
	Parameter details  :cellValues should be List<RichTextDetails> and excelWorkBook should be XSSFWorkbook.
	Exception details : No Exception
	*/
    private static XSSFRichTextString mergeTextDetails(
            List<RichTextDetails> cellValues, XSSFWorkbook excelWorkBook) {
    	StringBuilder textBuffer = new StringBuilder(EMPTY_STRING);
        Map<Integer, XSSFFont> mergedMap = new LinkedHashMap<>();
        //Added by RTA Sogeti offshore 2018x.5 def-27538 - Starts
		// Added by RTA Capgemini Offshore for 2018x.6-SeptCW Req 37893  - starts
        String strFont = mXMLToXLSConfigMapping.get("XMLToXLS.STR_CONTENT_FONT");
     // Added by RTA Capgemini Offshore for 2018x.6-SeptCW Req 37893  - ends
	Map<Integer, String> tagMapStorage = new LinkedHashMap<>();
	//Added by RTA Sogeti offshore 2018x.5 def-27538 - Ends
        int currentIndex = 0;
        XSSFRichTextString richText;
        XSSFFont currentFont;
        //modified for 35760 by  Sogeti on 23-09-2020 -- START
        String strSpanLTR = mXMLToXLSConfigMapping.get("XMLToXLS.MixedContent.code.Map.SPAN");
        boolean bMixedRTL = false;
        //modified for 35760 by  Sogeti on 23-09-2020 -- End
        for(RichTextDetails richTextDetail : cellValues){
            currentIndex = textBuffer.length();
	    //Modified & Added by RTA Sogeti offshore 2018x.5 def-27538 - Starts
            for (Entry<String, XSSFFont> entry : richTextDetail.getFontMap().entrySet()) {
            	String strKey = entry.getKey();
            	String str1 = strKey.substring(0, strKey.lastIndexOf(mXMLToXLSConfigMapping.get(UNDERSCORE)) + 1);
            	int i = Integer.parseInt(strKey.substring(strKey.lastIndexOf(mXMLToXLSConfigMapping.get(UNDERSCORE))+1, strKey.length()));
            	mergedMap.put(i + currentIndex, entry.getValue());
            	tagMapStorage.put(i + currentIndex, str1);
            	//modified for 35760 by  RTA on 23-09-2020 -- START
            	//check if tag contains span, in case of span it is mixed content for direction.
            	if(str1.contains(strSpanLTR)) {
            		bMixedRTL = true;
            	}
            	//modified for 35760 by RTA on 23-09-2020 -- END
            }
	    //Modified & Added by RTA Sogeti offshore 2018x.5 def-27538 - Ends
            textBuffer.append(richTextDetail.getRichText());
            
            
        }
        //Commented by RTA Sogeti offshore 2018x.5 def-27538 - Starts
        //Commented by RTA Sogeti offshore 2018x.5 def-27538 - Ends
        //Added & Modified by RTA Sogeti offshore 2018x.5 def-27538/PRB0059132:Def-26020 - Start
    	String strInitial = tagMapStorage.get(0);
    	if(strInitial != null && strInitial.toUpperCase().contains(mXMLToXLSConfigMapping.get("XMLToXLS.MixedContent.code.Map.STR_DIV_RTL"))){
    		richText = new XSSFRichTextString(mXMLToXLSConfigMapping.get(STR_RTL));//"\u200F" for dir RTL
    		//modified for 35760 by  Sogeti on 23-09-2020 -- START
    		if(!bMixedRTL)
            {
            	textBuffer =  updateRTLUnicodeInString(textBuffer);
            }
    		//modified for 35760 by  Sogeti on 23-09-2020 -- END
    	}else{
    		richText = new XSSFRichTextString(EMPTY_STRING);
    	}    
    	// Added by RTA Capgemini Offshore for 2018x.6-JulyCW Req 37994  - starts
        BreakIterator breakIterator = BreakIterator.getCharacterInstance();
        // Added by RTA Capgemini Offshore for 2018x.6-JulyCW Req 37994  - end
        breakIterator.setText(textBuffer.toString());
        int start = breakIterator.first();
		//modified for 35760 by  RTA on 23-09-2020 -- START
        String sbTempWord = null;
        String strTempTag = null;
        for (int end = breakIterator.next(); end != BreakIterator.DONE;start = end,end = breakIterator.next()) {
        	sbTempWord = (textBuffer.substring(start, end));
            currentFont = mergedMap.get(start);
	    	strTempTag = tagMapStorage.get(start);
	    	
	    	if(bMixedRTL && strTempTag != null && strTempTag.toUpperCase().contains(mXMLToXLSConfigMapping.get("XMLToXLS.MixedContent.code.Map.STR_SPAN_LTR"))){
	    		sbTempWord= new StringBuilder( mXMLToXLSConfigMapping.get(SPAN_LTR)).append( sbTempWord).append(mXMLToXLSConfigMapping.get(SPAN_LTR)).toString();
	    	}else if(bMixedRTL && strInitial != null  &&  strInitial.toUpperCase().contains(mXMLToXLSConfigMapping.get("XMLToXLS.MixedContent.code.Map.STR_DIV_RTL"))){
	    		sbTempWord=new StringBuilder( mXMLToXLSConfigMapping.get(STR_RTL)).append(sbTempWord).append(mXMLToXLSConfigMapping.get(STR_RTL)).toString();
	    	}
	    	//modified for 35760 by  RTA on 23-09-2020 -- END
	    	if (currentFont != null) {
	    		// Added by RTA Capgemini Offshore for 2018x.6-SeptCW Req 37893  - starts
	    		currentFont.setFontName(strFont);
	    		// Added by RTA Capgemini Offshore for 2018x.6-SeptCW Req 37893  - ends
	        	richText.append(sbTempWord, currentFont);
	        }else{
	        	currentFont = excelWorkBook.createFont();
	        	// Added by RTA Capgemini Offshore for 2018x.6-SeptCW Req 37893  - starts
	        	currentFont.setFontName(strFont);
	        	// Added by RTA Capgemini Offshore for 2018x.6-SeptCW Req 37893  - ends
	        	richText.append(sbTempWord, currentFont);
	        }
	        
	}
	//Added & Modified by RTA Sogeti offshore 2018x.5 def-27538/PRB0059132:Def-26020 - Ends
        return richText;
    }
    
    /**
	 * @Desc This method will getInformation of tags in given HTML String
	 * @param Element element
	 * @return void
   */
  //Added by RTA Sogeti offshore 2018x.5 def-27538 Starts
    private static void getInfo(Element element, Map<String, HtmlToExcel.TagInfo> tagMap) {
    	String strAtt = null;
    	String strDirAtt = mXMLToXLSConfigMapping.get("XMLToXLS.MixedContent.tag.attribute.dir");
    	StringBuilder sbStartTag = new StringBuilder(mXMLToXLSConfigMapping.get(OPEN_TAG));
    	if(element.hasAttr(strDirAtt)){
    		strAtt = element.attr(strDirAtt);
    		sbStartTag.append(element.nodeName()).append(STR_S_SPACE).append(strDirAtt).append(mXMLToXLSConfigMapping.get("XMLToXLS.STR_EQUAL_TO"))
        			.append(mXMLToXLSConfigMapping.get("XMLToXLS.STR_D_QUOTE")).append(strAtt).append(mXMLToXLSConfigMapping.get("XMLToXLS.STR_D_QUOTE")).append(mXMLToXLSConfigMapping.get(CLOSE_TAG));
    	} else {
    		sbStartTag.append(element.nodeName()).append(mXMLToXLSConfigMapping.get(CLOSE_TAG));
    	}
    	//modified for 35760 by  RTA on 23-09-2020 -- START
    	if(slTags.contains(element.nodeName()))
    	{
        tagMap.put(sbStartTag.toString(), new TagInfo(element.nodeName(), strAtt, START_TAG));
    	}
    	//modified for 35760 by  RTA on 23-09-2020 -- END
        if (!element.childNodes().isEmpty()) {
            List<Node> children = element.childNodes();
            for (Node child : children){
            	if((child.nodeName()).equals(mXMLToXLSConfigMapping.get(TEXT_NODE)))
            		continue;
                getInfo((Element)child, tagMap);
            }
        }
        //modified for 35760 by  RTA on 23-09-2020 -- START
        if(slTags.contains(element.nodeName()))
    	{
        tagMap.put(new StringBuilder(mXMLToXLSConfigMapping.get(OPEN_TAG))
        							.append(mXMLToXLSConfigMapping.get(SLASH))
        							.append( element.nodeName())
        							.append( mXMLToXLSConfigMapping.get(CLOSE_TAG)).toString()
        			, new TagInfo(element.nodeName(), END_TAG));
    	}
    	//modified for 35760 by  RTA on 23-09-2020 -- END
    }
    //Added by RTA Sogeti offshore 2018x.5 def-27538 Ends
    
    /**
	 * @Desc This method will replace all provided pattern from given StringBuilder
	 * @param StringBuilder sbHTMLString
	 * @param Pattern pattern
	 * @param String strReplacement
	 * @return void
   */
    public static void replaceAll(StringBuilder sbHTMLString, Pattern pattern, String strReplacement) {
        Matcher matcher = pattern.matcher(sbHTMLString);
        int start = 0;
        while (matcher.find(start)) {
            sbHTMLString.replace(matcher.start(), matcher.end(), strReplacement);
            start = matcher.start() + strReplacement.length();
        }
    }
    
    /**
	 * @Desc This method will insert unicode character in provided StringBuilder
	 * @param StringBuilder sbHTML
	 * @param String strTarget
	 * @param String strUnicode
	 * @return String
   */
    public static String insertUnicodeforDir(StringBuilder sbHTML, String strTarget, String strUnicode)
    {
    	StringBuilder sbReplacement = new StringBuilder(0);
    	StringList slDirTags = StringUtil.split(strTarget, ",");
		String strReplacer = "";
		for(int i=0; i<slDirTags.size(); i++) 
		{
			sbReplacement.delete(0, sbReplacement.length());
			strReplacer = slDirTags.get(i);
			if(sbHTML.indexOf(strReplacer) != -1 )
			{
				sbReplacement.append(strReplacer).append(strUnicode);
				replaceAll(sbHTML, Pattern.compile(strReplacer), sbReplacement.toString());
			}
		}
		return sbHTML.toString();
    }
    
	/*
	Method Purpose: 
	Parameter details  :htmlString should be String,excelWorkBook should be XSSFWorkbook.
	Exception details : No Exception
	*/
    public static RichTextDetails createCellValue(String htmlString, XSSFWorkbook excelWorkBook)  {
    	
    	StringBuilder sbHTML = new StringBuilder(htmlString);
    	if (BusinessUtil.isNotNullOrEmpty(htmlString)  )
    	{
    		insertUnicodeforDir(sbHTML, mXMLToXLSConfigMapping.get("XMLToXLS.MixedContent.dir.RTL"), mXMLToXLSConfigMapping.get(STR_RTL) );
    		insertUnicodeforDir(sbHTML, mXMLToXLSConfigMapping.get("XMLToXLS.MixedContent.dir.LTR"), mXMLToXLSConfigMapping.get(SPAN_LTR) );
    		
    	}
    	
    	htmlString = sbHTML.toString();
    	//Modified by RTA Sogeti offshore 2018x.5 def-27538 Starts
    	if(htmlString.contains(mXMLToXLSConfigMapping.get("XMLToXLS.STR_TAG_BR_PARTIAL"))){
    		htmlString = htmlString.replaceAll(mXMLToXLSConfigMapping.get("XMLToXLS.Conversion.RTL.brTag.Replacer"), mXMLToXLSConfigMapping.get("XMLToXLS.NEW_LINE"));
    	}
		
        Map<String, TagInfo> tagMap = new LinkedHashMap<>();
		String htmlTags= mXMLToXLSConfigMapping.get("XMLToXLS.Conversion.Html.Tags");
		String[] htmlTagArr = htmlTags.split(mXMLToXLSConfigMapping.get("XMLToXLS.COMMA_STRING"));
		//modified for 35760 by  RTA on 23-09-2020 -- START
		String strBlackListTags= mXMLToXLSConfigMapping.get("XMLToXLS.Conversion.Html.excludeTags");
		slTags = FrameworkUtil.split(strBlackListTags, mXMLToXLSConfigMapping.get("XMLToXLS.COMMA_STRING"));
		//modified for 35760 by  RTA on 23-09-2020 -- END
		//Modified by RTA Sogeti offshore 2018x.5 def-27538 Ends
		StringBuilder sbStartTagKey;
		StringBuilder sbEndTagKey;
		//Added by RTA Sogeti Offshore for 18x.2 DEC CW  Def.29905/code Review starts
		//modified for 35760 by  RTA on 23-09-2020 -- START
		String strOpenTagReplacer;
		String strCloseTagReplacer; 
		//modified for 35760 by  RTA on 23-09-2020 -- END
		//Added by RTA Sogeti Offshore for 18x.2 DEC CW  Def.29905/Code Review Ends
		for (String strTag : htmlTagArr) {	
			//Added by RTA Sogeti Offshore for 18x.2 DEC CW  Def.29905 starts
			//modified for 35760 by  RTA on 23-09-2020 -- START
			strOpenTagReplacer = mXMLToXLSConfigMapping.get(("XMLToXLS.Conversion.Html.Tags.replacer.open.").concat(strTag.toLowerCase()));
			strCloseTagReplacer =mXMLToXLSConfigMapping.get(("XMLToXLS.Conversion.Html.Tags.replacer.close.").concat(strTag.toLowerCase()));
            htmlString = htmlString.replaceAll(strOpenTagReplacer, mXMLToXLSConfigMapping.get(OPEN_TAG).concat(strTag).concat(mXMLToXLSConfigMapping.get(CLOSE_TAG))); 
            htmlString = htmlString.replaceAll(strCloseTagReplacer, mXMLToXLSConfigMapping.get(OPEN_TAG).concat(mXMLToXLSConfigMapping.get(SLASH)).concat(strTag).concat(mXMLToXLSConfigMapping.get(CLOSE_TAG)));
          //modified for 35760 by  RTA on 23-09-2020 -- END
            //Added by RTA Sogeti Offshore for 18x.2 DEC CW  Def.29905 ends
			sbStartTagKey = new StringBuilder(mXMLToXLSConfigMapping.get(OPEN_TAG));
			sbStartTagKey.append(strTag.trim());
			sbStartTagKey.append(mXMLToXLSConfigMapping.get(CLOSE_TAG));
			tagMap.put(sbStartTagKey.toString(), new TagInfo(strTag.trim(), null, START_TAG));
			sbEndTagKey = new StringBuilder(mXMLToXLSConfigMapping.get(OPEN_TAG));
			sbEndTagKey.append(mXMLToXLSConfigMapping.get(SLASH));
			sbEndTagKey.append(strTag.trim());
			sbEndTagKey.append(mXMLToXLSConfigMapping.get(CLOSE_TAG));
			//modified for 35760 by  RTA on 23-09-2020 -- START
			if(!slTags.contains(strTag))
				slTags.addElement(strTag);
			//modified for 35760 by  RTA on 23-09-2020 -- END
			tagMap.put(sbEndTagKey.toString(), new TagInfo(strTag.trim(), null, END_TAG));
		}
		//Added by RTA Sogeti offshore 2018x.5 def-27538 Starts
		htmlString = mXMLToXLSConfigMapping.get("XMLToXLS.STR_TAG_SPECIAL_START") + htmlString + mXMLToXLSConfigMapping.get("XMLToXLS.STR_TAG_SPECIAL_END");
		Document doc = Jsoup.parse(htmlString);
		Element mainElement = doc.getElementsByTag(mXMLToXLSConfigMapping.get("XMLToXLS.STR_TAG_SPECIAL_NAME")).get(0);//XMLToXLSConstants.STR_TAG_SPECIAL_NAME
		getInfo(mainElement, tagMap);
		//Added by RTA Sogeti offshore 2018x.5 def-27538 Ends
		Pattern pattern;
		Matcher matcher;
		Pattern patternHTML;
		Matcher matcherHTML;
		
        StringBuilder sbPatternString;
        String patternString = EMPTY_STRING;
        StringBuffer textBuffer = new StringBuffer();
        StringBuffer textBufferHTML = new StringBuffer();
        List<RichTextInfo> textInfos = new ArrayList<>();
        Deque<RichTextInfo> richTextBuffer = new ArrayDeque<>();
        TagInfo currentTag;
        RichTextInfo info;
        if(!tagMap.keySet().isEmpty()){
        	sbPatternString = new StringBuilder(mXMLToXLSConfigMapping.get("XMLToXLS.STR_OPEN_BRACKET_STRING"));
        	sbPatternString.append(StringUtils.join(tagMap.keySet(), mXMLToXLSConfigMapping.get("XMLToXLS.STR_PIPE"))); 
        	sbPatternString.append(mXMLToXLSConfigMapping.get("XMLToXLS.STR_CLOSE_BRACKET_STRING"));
        	patternString = sbPatternString.toString();
        }
        patternHTML = Pattern.compile(mXMLToXLSConfigMapping.get("XMLToXLS.Conversion.AllEmptyTag.Replacer"));
        matcherHTML = patternHTML.matcher(htmlString);
		while (matcherHTML.find()) {
			matcherHTML.appendReplacement(textBufferHTML, EMPTY_STRING);
		}
		matcherHTML.appendTail(textBufferHTML);
		htmlString=textBufferHTML.toString();
		pattern = Pattern.compile(patternString);
        matcher = pattern.matcher(htmlString);

        while (matcher.find()) {
            matcher.appendReplacement(textBuffer, EMPTY_STRING);
            currentTag = tagMap.get(matcher.group(1));
            if (START_TAG == currentTag.getTagType()) {
                richTextBuffer.push(getRichTextInfo(currentTag, textBuffer.length()));
            } else if (!richTextBuffer.isEmpty()) {
                info = richTextBuffer.pop();
                if (info != null) {
                    info.setEndIndex(textBuffer.length());
                    textInfos.add(info);
                }
            }
        }
        //Added by RTA Sogeti offshore 2018x.5 def-27538 Starts
        if (!richTextBuffer.isEmpty()) {
            info = richTextBuffer.pop();
            if (info != null) {
                info.setEndIndex(textBuffer.length());
                textInfos.add(info);
            }
        }
        //Added by RTA Sogeti offshore 2018x.5 def-27538 Ends
        
        matcher.appendTail(textBuffer);
        //Modified by RTA Sogeti offshore 2018x.5 def-27538 Starts
       	Map<String, XSSFFont> fontMap = buildFontMap(textInfos, excelWorkBook);
		//Modified by RTA Sogeti offshore 2018x.5 def-27538 Ends
       	return new RichTextDetails(textBuffer.toString(), fontMap);
    }

	/*
	Method Purpose: 
	Parameter details  :textInfos should be List<RichTextInfo> and excelWorkBook should be XSSFWorkbook.
	Exception details : No Exception
	*/
    //Modified by RTA Sogeti offshore 2018x.5 def-27538 Starts
    private static Map<String, XSSFFont> buildFontMap(
            List<RichTextInfo> textInfos, XSSFWorkbook excelWorkBook) {
    //Modified by RTA Sogeti offshore 2018x.5 def-27538 Ends
    	//Added & Modified by RTA Sogeti offshore 2018x.5 def-27538 Starts
        Map<String, XSSFFont> fontMap = new LinkedHashMap<>();
        String strKey;
        String strKeyDefault;
        String strKeyDup;
        for (RichTextInfo richTextInfo : textInfos) {
            if (richTextInfo.isValid()) {
            	strKey = null;
                for (int i = richTextInfo.getStartIndex(); i < richTextInfo.getEndIndex(); i++) {
                	strKey = getExistingKeys(fontMap, i);
                	strKeyDup = strKey;
                	//modified for 35760 by  RTA on 23-09-2020 -- START
                	strKeyDefault = richTextInfo.getFontStyle()+ mXMLToXLSConfigMapping.get(UNDERSCORE) + richTextInfo.getFontValue() + mXMLToXLSConfigMapping.get(UNDERSCORE);
                	//modified for 35760 by  RTA on 23-09-2020 -- END
                	if(strKey != null) {
                		strKey =  strKeyDefault + strKey;
                		fontMap.put(strKey, mergeFont(fontMap.get(strKeyDup), richTextInfo.getFontStyle(), richTextInfo.getFontValue(), excelWorkBook));
                		fontMap.remove(strKeyDup);
                	} else{
                		fontMap.put(strKeyDefault + i, mergeFont(fontMap.get(strKeyDefault + i), richTextInfo.getFontStyle(), richTextInfo.getFontValue(), excelWorkBook));
                	}
                }
            }
	    //Added & Modified by RTA Sogeti offshore 2018x.5 def-27538 Ends
        }

        return fontMap;
    }
	//Method Header to be added
	//Added by RTA Sogeti offshore 2018x.5 def-27538 Starts
    
    /*
	Method Purpose: 
	Parameter details  :Map should be String, XSSFont , 
						int i.
	Return : String					
	Exception details : No Exception
	*/
	private static String getExistingKeys(Map<String, XSSFFont> fontMap, int i) {
		for(String key: fontMap.keySet()){
			if(key.endsWith(mXMLToXLSConfigMapping.get(UNDERSCORE)+i)){
				return key;
			}
		}
		return null;
	}
	//Added by RTA Sogeti offshore 2018x.5 def-27538 Ends

	/*
	Method Purpose: 
	Parameter details  :xssfFont should be XSSFFont, fontStyle should be STYLES, 
						fontValue should be String, excelWorkBook should be XSSFWorkbook.
	Exception details : No Exception
	*/
    private static XSSFFont mergeFont(XSSFFont xssfFont, STYLES fontStyle,
            String fontValue, XSSFWorkbook excelWorkBook) {
        if (xssfFont == null) {
            xssfFont = excelWorkBook.createFont();
            
            xssfFont.setTypeOffset((short)0);
        }
        
        
        switch (fontStyle) {
            case BOLD:
            case STRONG:
                xssfFont.setBold(true);
                
                break;
            case UNDERLINE:
                xssfFont.setUnderline(Font.U_SINGLE);
                break;
            case EM:
            case ITALLICS:
                xssfFont.setItalic(true);
                break;
            case STRIKE:
            	xssfFont.setStrikeout(true);
            	break;
            case SUB :
            	xssfFont.setTypeOffset(Font.SS_SUB);
            	break;
            case SUP :
            	xssfFont.setTypeOffset(Font.SS_SUPER);
            	break;
            	//Added by RTA Sogeti Offshore for 18x.2 DEC CW  Def.29905 starts
            case SPAN :
            	xssfFont.setTypeOffset(Font.SS_NONE);
            	break;
            	//Added by RTA Sogeti Offshore for 18x.2 DEC CW  Def.29905 ends
            case COLOR:
                if (!StringUtils.isEmpty(fontValue)) {

                    xssfFont.setColor(IndexedColors.RED.getIndex());
                }
                break;
            default:
                break;
        }

        return xssfFont;
    }
	/*
	Method Purpose: 
	Parameter details  :xssfFont should be XSSFFont, fontStyle should be STYLES, 
						fontValue should be String, excelWorkBook should be XSSFWorkbook.
	Exception details : No Exception
	*/
    private static RichTextInfo getRichTextInfo(TagInfo currentTag,
            int startIndex) {
        RichTextInfo info = null;
        
        //Modified by RTA Sogeti offshore 2018x.5 def-27538 Starts
        info = new RichTextInfo(startIndex, -1, STYLES.fromValue(currentTag.getTagName()), currentTag.getStyle());
        //Modified by RTA Sogeti offshore 2018x.5 def-27538 Ends
             
        return info;
    }

	/*
	Project Name : XML to Excel Conversion
	Java Name : HtmlToExcel
	Clone From/Reference :
	Purpose : 
	Change History :
	*/

    public static class RichTextInfo {
        private int startIndex;
        private int endIndex;
        private STYLES fontStyle;
        private String fontValue;

        public RichTextInfo(int startIndex, int endIndex, STYLES fontStyle) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.fontStyle = fontStyle;
        }

        public RichTextInfo(int startIndex, int endIndex, STYLES fontStyle,
                String fontValue) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.fontStyle = fontStyle;
            this.fontValue = fontValue;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public void setStartIndex(int startIndex) {
            this.startIndex = startIndex;
        }

        public int getEndIndex() {
            return endIndex;
        }

        public void setEndIndex(int endIndex) {
            this.endIndex = endIndex;
        }

        public STYLES getFontStyle() {
            return fontStyle;
        }

        public void setFontStyle(STYLES fontStyle) {
            this.fontStyle = fontStyle;
        }

        public String getFontValue() {
            return fontValue;
        }

        public void setFontValue(String fontValue) {
            this.fontValue = fontValue;
        }

        public boolean isValid() {
            return (startIndex != -1 && endIndex != -1 && endIndex >= startIndex);
        }

		@Override
		public String toString() {
			return new StringBuilder("RichTextInfo [startIndex=").append(startIndex).append(", endIndex=")
					.append(endIndex).append(", fontStyle=").append(fontStyle).append(", fontValue=").append(fontValue)
					.append("]").toString();
		}
	}

	/*
	Project Name : XML to Excel Conversion
	Java Name : HtmlToExcel
	Clone From/Reference :
	Purpose : 
	Change History :
	*/
    public static class RichTextDetails {
        private String richText;
	//Modified by RTA Sogeti offshore 2018x.5 def-27538 Starts
        private Map<String, XSSFFont> fontMap;
	//Modified by RTA Sogeti offshore 2018x.5 def-27538 Ends

	//Modified by RTA Sogeti offshore 2018x.5 def-27538 Starts
        public RichTextDetails(String richText,
                Map<String, XSSFFont> fontMap) {
	//Modified by RTA Sogeti offshore 2018x.5 def-27538 Ends
            this.richText = richText;
            this.fontMap = fontMap;
        }

        public String getRichText() {
            return richText;
        }
        public void setRichText(String richText) {
            this.richText = richText;
        }
	//Modified by RTA Sogeti offshore 2018x.5 def-27538 Starts
        public Map<String, XSSFFont> getFontMap() {
    	//Modified by RTA Sogeti offshore 2018x.5 def-27538 Ends
            return fontMap;
        }
	//Modified by RTA Sogeti offshore 2018x.5 def-27538 Starts
        public void setFontMap(Map<String, XSSFFont> fontMap) {
	//Modified by RTA Sogeti offshore 2018x.5 def-27538 Ends
            this.fontMap = fontMap;
        }
    }

	/*
	Project Name : XML to Excel Conversion
	Java Name : HtmlToExcel
	Clone From/Reference :
	Purpose : 
	Change History :
	*/
    static class TagInfo {
        private String tagName;
        private String style;
        private int tagType;

        public TagInfo(String tagName, String style, int tagType) {
            this.tagName = tagName;
            this.style = style;
            this.tagType = tagType;
        }

        public TagInfo(String tagName, int tagType) {
            this.tagName = tagName;
            this.tagType = tagType;
        }

        public String getTagName() {
            return tagName;
        }

        public void setTagName(String tagName) {
            this.tagName = tagName;
        }

        public int getTagType() {
            return tagType;
        }

        public void setTagType(int tagType) {
            this.tagType = tagType;
        }

        public String getStyle() {
            return style;
        }

        public void setStyle(String style) {
            this.style = style;
        }

		@Override
		public String toString() {
			return new StringBuilder("TagInfo [tagName=").append(tagName).append(", style=").append(style)
					.append(", tagType=").append(tagType).append("]").toString();
		}
	}

    enum STYLES {
        BOLD("b"), 
        EM("em"), 
        STRONG("strong"), 
        COLOR("color"), 
        UNDERLINE("u"), 
        SPAN("span"), 
        ITALLICS("i"),
        DIV("div"),
        STRIKE("strike"),
        SUB("sub"),
        SUP("sup"),
        UNKNOWN("unknown");
        

        private String type;

        private STYLES(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public static STYLES fromValue(String type) {
            for (STYLES style : values()) {
                if (style.type.equalsIgnoreCase(type)) {
                    return style;
                }
            }
            return UNKNOWN;
        }
    }
    
  //modified for 35760 by RTA on 23-09-2020 -- START
    /**
	 * @Desc This method will return stringBuilder with direction unicode assigned as per BiDi API
	 * @param String strText
	 * @return String
	*/
    public static StringBuilder updateRTLUnicodeInString(StringBuilder sbText) 
    {
    	StringBuilder sbReturn = new StringBuilder();
    	
    	if(BusinessUtil.isNotNullOrEmpty(sbText.toString())) 
    	{
    		Bidi bidi = new Bidi(sbText.toString(), Bidi.DIRECTION_RIGHT_TO_LEFT);
    		int iTextLength = bidi.getLength();
    		int iRunStart = 0;
        	int iRunLevel = 0;
        	int iRunEnd   = 0;
        	int iRunCount =bidi.getRunCount();
        	
        	//iterate through all runs and apply direction unicode as per level.
        	//run means string part which has direction change.
        	//run level indicates direction. even means LTR and odd means RTL
    		for(int i=0;i<iRunCount;i++) 
    		{
            	 iRunStart = bidi.getRunStart(i);
            	 iRunLevel = bidi.getRunLevel(i);
            	 
            	 if(iRunCount>i+1) 
            	 {
            		 iRunEnd   = bidi.getRunStart(i+1);
            	 } 
            	 else 
            	 {
            		 iRunEnd   = iTextLength;
            	 }
            	 sbReturn.append(updateUnicode(sbText.substring(iRunStart, iRunEnd), iRunLevel)); 			
            }
            
    	}
    	
    	return sbReturn;
    	
    }
    
    /**
	 * @Desc This method will return string with direction unicode assigned as per BiDi API
	 * @param String strText
	 * @param int iRunLevel
	 * @return String
	*/
    public static String updateUnicode(String strText, int iRunLevel) 
    {
        //get unicode for LTR	
    	String strUnicode = mXMLToXLSConfigMapping.get(SPAN_LTR);
    	//check if RunLevel is odd, if odd get Unicode for LTR
    	if(iRunLevel != 0 && (iRunLevel%2) != 0)
    	{
    		strUnicode = mXMLToXLSConfigMapping.get(STR_RTL);
    	}
    	//append unicode to start and end of the run.
    	strText = new StringBuilder(strUnicode).append(strText).append(strUnicode).toString();
        return strText;
    }
  //modified for 35760 by  RTA on 23-09-2020 -- END
}
