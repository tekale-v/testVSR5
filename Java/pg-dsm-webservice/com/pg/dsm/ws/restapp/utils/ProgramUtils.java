package com.pg.dsm.ws.restapp.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import matrix.db.Context;

/**
 * @author DSM(Sogeti)
 *
 */
public class ProgramUtils {
	private Context ctx;
	private String objId;
	private String[] args;
	
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * @param context
	 * @param objectId
	 * @param pdfView
	 */
	public ProgramUtils(Context context, String objectId) {
		this.ctx = context;
		this.objId = objectId;
	}

	/**
	 * @param argsdata
	 */
	public ProgramUtils addMethodParameters(String[] argsdata) {
		this.args = argsdata;
		return this;
	}

	/**
	 * @param sProgramName @param sMethodName @throws
	 */
	public Object execute(String sProgramName) {
		Object returnObj = null;
		try {
			String[] prog=sProgramName.split(":");
			if(prog.length==2) {
				Object object = Class.forName(prog[0]).getConstructor(Context.class, String.class)
						.newInstance(this.ctx, this.objId);
				returnObj = object.getClass().getMethod(prog[1], String[].class).invoke(object,
						new Object[] { this.args });	
			}else {
				logger.log(Level.WARNING, null, "Error in input program:function name...!!");
			}
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			logger.log(Level.WARNING, null, e);
		} 
		return returnObj;
	}
}
