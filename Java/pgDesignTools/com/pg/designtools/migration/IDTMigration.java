package com.pg.designtools.migration;

import java.io.IOException;
import java.util.Map;

import com.dassault_systemes.enovia.versioning.util.ENOVersioningException;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.MapList;

import matrix.db.Context;
import matrix.util.MatrixException;

public interface IDTMigration {

	public MapList init(Context context,String strInputFilePath) throws IOException, MatrixException;
	public Map process(Context context,DomainObject doObject,String strType) throws MatrixException,ENOVersioningException;
	public void updateObjects(Context context, String strNewVPMReferenceObjectId, String strNewECPartObjectId)  throws MatrixException, ENOVersioningException, Exception;
	public void setOutputPath(String strOutputFilePath,String strInputFilePath);
	public void getOutput() throws IOException;
	public void processObjects(Context context,Map mpObjectInfo,String strMode) throws Exception;
}
