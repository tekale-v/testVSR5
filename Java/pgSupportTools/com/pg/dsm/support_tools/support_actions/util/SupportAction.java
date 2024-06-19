package com.pg.dsm.support_tools.support_actions.util;
/**DSM 2018x.3 - On Demand Support Tools 
 * @about - Abstract class which calls the other abstract methods.
 * @return String 
 * @throws Exception
 */
public abstract class SupportAction implements SupportConstants {
	public abstract boolean hasAccess() throws Exception;
	public abstract boolean checkState() throws Exception;
	public abstract boolean isQualified() throws Exception;
	public abstract String execute() throws Exception;
	public abstract void recordHistory() throws Exception;
}
