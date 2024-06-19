package com.pg.dsm.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class UIProgramUtil {
	
	Context context;
	public UIProgramUtil(Context context) {
		this.context = context;
	}

	/**
	 * Added by DSM (Sogeti) for 22x.1 - Defect 51450
     * (Finished Product Part) is connected with Supporting Doc (IRM Documents) with relationship (pgWnDExcpSupportDoc)
     *
     * @param objectOid
     * @return
     */
    public String getFinishedProductRelatedSupportingDoc(String objectOid) throws MatrixException {
        Map<Object, Object> requestMap = new HashMap<>();
        requestMap.put("objectId", objectOid);
        Map<Object, Object> paramMap = new HashMap<>();
        paramMap.put("objectId", objectOid);
        Map<Object, Object> programMap = new HashMap<>();
        programMap.put("paramMap", paramMap);
        programMap.put("requestMap", requestMap);

        String resultText = JPO.invoke(
                context,
                "pgDSMPDTUtil",
                null,
                "getpgWnDExcpSupportDocuments",
                JPO.packArgs(programMap),
                String.class);
        return extractDataBetweenAnchorTag(resultText);
    }

    /**
     * Added by DSM (Sogeti) for 22x.1 - Defect 51450
     * Method to extract data which is between anchor tags.
     * @param inputText
     * @return
     */
    private String extractDataBetweenAnchorTag(String inputText) {
        StringBuilder stringBuilder = new StringBuilder();
        if(UIUtil.isNotNullAndNotEmpty(inputText)) {
            StringList dataList = StringUtil.split(inputText, "|");
            for (String data : dataList) {
                Pattern pattern = Pattern.compile(PdfConstants.Basic.EXTRACT_ANCHOR_TAG_DATA.get());
                Matcher matcher = pattern.matcher(data);
                while (matcher.find()) {
                    stringBuilder.append(matcher.group(1));
                    stringBuilder.append(" ");
                    stringBuilder.append("|");
                }
            }
            if (stringBuilder.length() > 0) {
                stringBuilder.setLength(stringBuilder.length() - 1);
            }
        }
        return stringBuilder.toString();
    }


}
