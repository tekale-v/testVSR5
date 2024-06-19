package com.pg.dsm.support_tools.support_actions.actions;

import java.util.Map;

import com.matrixone.apps.domain.util.MqlUtil;
import com.pg.dsm.support_tools.support_actions.util.SupportAction;
import com.pg.dsm.support_tools.support_actions.util.SupportType;
import com.pg.dsm.support_tools.support_actions.util.SupportUtil;

import matrix.db.Context;
import matrix.db.JPO;

public class MarkEBP extends SupportAction {

	Context _context;
	String _supportAction;
	Map<String, String> _infoMap;

	public MarkEBP(Context _context, String _supportAction, Map<String, String> _infoMap) {
		this._context = _context;
		this._supportAction = _supportAction;
		this._infoMap = _infoMap;
	}
	@Override
	public boolean checkState() throws Exception {
		return SupportUtil.isStateRelease(_infoMap.get(SELECT_CURRENT));
	}
	@Override
	public boolean hasAccess() throws Exception {
		return hasReadAccess();
	}
	public boolean hasReadAccess() throws Exception {
		return SupportUtil.hasAccess(_context, _infoMap.get(SELECT_ID), ACCESS_READ, ACCESS_SHOW);
	
	}
	
	
	@Override
	public boolean isQualified() throws Exception {
		return isTypeQualified();
	}
	/**DSM 2018x.3 - On Demand Support Tools 
	 * @about - Helper method to execute the respective supported action invoking existing JPO.
	 * @return String - containing program/method
	 * @throws Exception
	 */
	@Override
	public String execute() throws Exception {
		String ret = UNSUPPORTED_ACTION;
		try {
			System.out.println("Inside Mark EBP - execute:"+_supportAction);
			String args[] = { (String)_infoMap.get(SELECT_ID), EVENT_DEMOTE, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING };
			JPO.invoke(_context, SupportType.MARK_EBP.getJPO(), null, SupportType.MARK_EBP.getMethod(), args, String.class);
			ret = SUPPORT_ACTION_SUCCESS;

		} catch(Exception e) {
			ret = SupportType.MARK_EBP.getException();
			e.printStackTrace();
		}
		return ret;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(MARK_EBP);
		builder.append(SYMB_SQUARE_BRACKET_OPEN);
		builder.append(_context);
		builder.append(SYMBOL_COMMA);
		builder.append(_supportAction);
		builder.append(SYMBOL_COMMA);
		builder.append(_infoMap);
		builder.append(SYMB_SQUARE_BRACKET_CLOSE);
		return builder.toString();
	}
	/**DSM 2018x.3 - On Demand Support Tools 
	 * @about - Helper method to check if given object is supported for mark EBP Security 
	 * @return boolean - true/false
	 * @throws Exception
	 */
	public boolean isTypeQualified() throws Exception {
		return SupportUtil.getTypes(SupportUtil.getValue(_infoMap, SUPPORT_ACTION_TYPE)).contains(SupportUtil.getTypeSymbolicName(_context, SupportUtil.getValue(_infoMap, SELECT_TYPE)));
	}
	/**DSM 2018x.3 - On Demand Support Tools 
	 * @about - Helper method to record the history when the flagging is performed.  
	 * @return void 
	 * @throws Exception
	 */
	@Override
	public void recordHistory() throws Exception {
		try {
			MqlUtil.mqlCommand(_context,"modify bus $1 add history $2 comment $3", _infoMap.get(SELECT_ID), PARAM_CUSTOM, SupportType.MARK_EBP.getComment().concat(_infoMap.get(SUPPORT_CONTEXT_USER))); 
		} catch(Exception e) {
			e.printStackTrace();
		} 
	}

}
