package com.pg.designtools.datamanagement;

import com.matrixone.apps.domain.util.FrameworkException;

import matrix.db.Context;
import matrix.util.MatrixException;

public interface ITransientJob {

	public abstract String init(Context context,String strJobEvent,String strObjType,String strObjName,String strObjRev)throws Exception;
	public abstract boolean isValidEvent(String strJobEvent) throws FrameworkException;
	public abstract String doEvent(Context context,String strJobEvent,String strObjectId) throws Exception;
	public abstract int addInterface(Context context,String strObjectId) throws MatrixException;
	public abstract int removeInterface(Context context,String strObjectId) throws MatrixException;
	public abstract void addEvent();
	public abstract String getObject(Context context,String strObjType,String strObjName,String strObjRev)throws FrameworkException;

}