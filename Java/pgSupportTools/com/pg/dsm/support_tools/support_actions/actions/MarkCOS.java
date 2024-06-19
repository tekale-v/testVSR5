package com.pg.dsm.support_tools.support_actions.actions;

import java.util.Map;

import com.matrixone.apps.awl.util.Access;
import com.matrixone.apps.domain.util.MqlUtil;
import com.pg.dsm.support_tools.support_actions.util.SupportAction;
import com.pg.dsm.support_tools.support_actions.util.SupportType;
import com.pg.dsm.support_tools.support_actions.util.SupportUtil;


import matrix.db.Context;
import matrix.db.JPO;

public class MarkCOS extends SupportAction {

	Context _context;
	String _supportAction;
	Map<String, String> _infoMap;

	public MarkCOS(Context _context, String _supportAction, Map<String, String> _infoMap) {
		this._context = _context;
		this._supportAction = _supportAction;
		this._infoMap = _infoMap;
	}
	@Override
	public boolean checkState() throws Exception {
		return SupportUtil.isStateRelease((String)_infoMap.get(SELECT_CURRENT));
	}
	@Override
	public boolean hasAccess() throws Exception {
		return hasReadAccess();
	}
	public boolean hasReadAccess() throws Exception {
		return SupportUtil.hasAccess(_context, (String)_infoMap.get(SELECT_ID), ACCESS_READ, ACCESS_SHOW);
	}
	@Override
	public boolean isQualified() throws Exception {
		return isTypeQualified();
	}
	/**DSM 2018x.3 - On Demand Support Tools 
	 * @about - Helper method to call the marking COS program 
	 * @return String 
	 * @throws Exception
	 */
	@Override
	public String execute() throws Exception {
		String ret = UNSUPPORTED_ACTION;
		try {
			System.out.println("Inside Mark COS - execute:"+_supportAction);
			String args[] = { (String)_infoMap.get(SELECT_ID), (String)_infoMap.get(SELECT_TYPE) };
			JPO.invoke(_context, SupportType.MARK_COS.getJPO(), null, SupportType.MARK_COS.getMethod(), args, String.class);
			ret = SUPPORT_ACTION_SUCCESS;
		} catch(Exception e) {
			ret = SupportType.MARK_COS.getException();
			e.printStackTrace();
		}
		return ret;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(MARK_COS);
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
	 * @about - Helper method to check if given object is supported for mark COS 
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
	public void recordHistory() throws Exception {
		try {
			MqlUtil.mqlCommand(_context,"modify bus $1 add history $2 comment $3", _infoMap.get(SELECT_ID), "custom", SupportType.MARK_COS.getComment().concat(_infoMap.get(SUPPORT_CONTEXT_USER)));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
