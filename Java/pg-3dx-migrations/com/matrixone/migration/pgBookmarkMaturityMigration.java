package com.matrixone.migration;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.datamigration.mt.IDataMigration;
import com.matrixone.datamigration.mt.IDataMigrationProcessor;
import com.matrixone.datamigration.mt.IteratorAdapterForBO;
import com.matrixone.datamigration.mt.MTParameters;

import java.util.Iterator;
import matrix.db.BusinessObjectWithSelect;
import matrix.db.Context;
import matrix.db.Query;
import matrix.db.QueryIterator;
import matrix.util.MatrixException;
import matrix.util.StringList;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

public class pgBookmarkMaturityMigration implements IDataMigration<BusinessObjectWithSelect> {
  private final XLogger _log = XLoggerFactory.getXLogger(getClass());
  
  private String whereClause;
  
  protected String targetType;
  
  public void init(Context paramContext, MTParameters paramMTParameters, String[] paramArrayOfString) throws Exception {
    this._log.debug("init");
    String strWorkVaultSpaceType = PropertyUtil.getSchemaProperty(paramContext, "type_ProjectVault");
    String strWorkVaultSpacePolicy = PropertyUtil.getSchemaProperty(paramContext, "policy_ProjectVault");
    this.targetType = strWorkVaultSpaceType;
    if (paramArrayOfString[0] != null)
      this.targetType = paramArrayOfString[0]; 
    this.whereClause = "policy == '" + strWorkVaultSpacePolicy + "'";
    this._log.debug("types to migrate: " + this.targetType + "\n");
    this._log.debug("where clause: " + this.whereClause);
  }
  
  public boolean supportMultiThreading() {
    return true;
  }
  
  public IDataMigrationProcessor<BusinessObjectWithSelect> createObjectProcessor() {
    return new pgBookmarkMaturityMigrationProcessor(this.targetType);
  }
  
  public Iterator<BusinessObjectWithSelect> createObjectIterator(Context paramContext) throws Exception {
    this._log.debug("createObjectIterator start at : {}", Long.valueOf(System.currentTimeMillis()));
    Query query = new Query();
    QueryIterator queryIterator = null;
    try {
      query.setVaultPattern("*");
      query.setBusinessObjectType(this.targetType);
      StringList stringList = new StringList(new String[] { "id", "name", "revision", "current" });
      query.setWhereExpression(this.whereClause);
      queryIterator = query.getIterator(paramContext, stringList, (short)1024);
      return (Iterator<BusinessObjectWithSelect>)new IteratorAdapterForBO(queryIterator);
    } catch (Exception exception) {
      this._log.debug("ERROR: ", exception.getMessage());
      throw new MatrixException(exception);
    } finally {
      if (null != query)
        query.close(paramContext); 
    } 
  }
  
  public void close(Context paramContext) {
    this._log.debug("close");
  }
}
