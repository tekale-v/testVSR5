/*
 **   RePublish.java
 **   Description - Introduced as part of 2018x.5 On Demand Support Tools.  
 **   Helper class to Republish data to downstreams for Specific Subscriber
 **
 */
package com.pg.dsm.support_tools.support_actions.actions;

import java.util.Map;

import matrix.db.Context;
import matrix.db.JPO;

import com.matrixone.apps.domain.util.MqlUtil;
import com.pg.dsm.support_tools.support_actions.util.SupportAction;
import com.pg.dsm.support_tools.support_actions.util.SupportType;
import com.pg.dsm.support_tools.support_actions.util.SupportUtil;

public class RePublish extends SupportAction {
	Context _context;
	String _supportAction;
	Map<String, String> _infoMap;

	public RePublish(Context _context, String _supportAction, Map<String, String> _infoMap) {
		this._context = _context;
		this._supportAction = _supportAction;
		this._infoMap = _infoMap;
	}
	@Override
	public boolean hasAccess() throws Exception {
		return hasReadAccess();
	}
	public boolean hasReadAccess() throws Exception {
		return SupportUtil.hasAccess(_context, _infoMap.get(SELECT_ID), ACCESS_READ, ACCESS_SHOW);
	}

	@Override
	public boolean checkState() throws Exception {
		return true;
	}

	@Override
	public boolean isQualified() throws Exception {
		return true;
	}
	
	/**DSM 2018x.5 - On Demand Support Tools 
	 * @about - Helper method to republish data to specific Subscriber 
	 * @return String
	 * @throws Exception
	 */
	@Override
	public String execute() throws Exception {
		String strReturn = UNSUPPORTED_ACTION;
		try {
		    String[] args = JPO.packArgs(_infoMap);			
		    Map<?, ?> mReturnMap = JPO.invoke(_context, SupportType.REPUBLISH.getJPO(), null, SupportType.REPUBLISH.getMethod(), args, Map.class);			
	
			if(mReturnMap.size()>0){
				strReturn = (String)mReturnMap.get(RESULT);
			}else{				
				strReturn = SupportType.REPUBLISH.getException();
			}
		} catch(Exception e) {
			strReturn = SupportType.REPUBLISH.getException();
			e.printStackTrace();
		}
		return strReturn;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(REPUBLISH);
		builder.append(SYMB_SQUARE_BRACKET_OPEN);
		builder.append(_context);
		builder.append(SYMBOL_COMMA);
		builder.append(_supportAction);
		builder.append(SYMBOL_COMMA);
		builder.append(_infoMap);
		builder.append(SYMB_SQUARE_BRACKET_CLOSE);
		return builder.toString();
	}
	
	/**DSM 2018x.5 - On Demand Support Tools 
	 * @about - Helper method to record the history when the flagging is performed.  
	 * @return void 
	 * @throws Exception
	 */
	@Override
	public void recordHistory() throws Exception {
		try {
			MqlUtil.mqlCommand(_context,"modify bus $1 add history $2 comment $3", _infoMap.get(SELECT_ID), PARAM_CUSTOM, SupportType.REPUBLISH.getComment().concat(_infoMap.get(SUPPORT_CONTEXT_USER))); 
		} catch(Exception e) {
			e.printStackTrace();
		} 
	}
	

}

