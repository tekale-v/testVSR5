package com.matrixone.migration;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.datamigration.mt.IDataMigrationProcessor;
import java.util.List;
import matrix.db.BusinessObject;
import matrix.db.BusinessObjectWithSelect;
import matrix.db.Context;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

public class pgBookmarkMaturityMigrationProcessor  implements IDataMigrationProcessor<BusinessObjectWithSelect> {
  private final XLogger _log = XLoggerFactory.getXLogger(getClass());
  
  String targetType;
  
  public pgBookmarkMaturityMigrationProcessor () {}
  
  public pgBookmarkMaturityMigrationProcessor (String paramString) {
    this.targetType = paramString;
  }
  
  public void process(Context paramContext, List<BusinessObjectWithSelect> paramList) throws Exception {
    this._log.debug("Processing start... at : {}", Long.valueOf(System.currentTimeMillis()));
    try {
      String strWorkSpacePolicy = PropertyUtil.getSchemaProperty(paramContext, "policy_Project");
	  String strWorkSpaceVaultPolicy = PropertyUtil.getSchemaProperty(paramContext, "policy_ProjectVault");
      String strActive = PropertyUtil.getSchemaProperty(paramContext, "policy", strWorkSpacePolicy, "state_Active");
      String strWorkspaceArchive = PropertyUtil.getSchemaProperty(paramContext, "policy", strWorkSpacePolicy, "state_Archive");
      String strArchive = PropertyUtil.getSchemaProperty(paramContext, "policy", strWorkSpaceVaultPolicy, "state_Archive");
      String strExist = PropertyUtil.getSchemaProperty(paramContext, "policy", strWorkSpaceVaultPolicy, "state_Exists");
      boolean bool = true;
      for (BusinessObjectWithSelect businessObjectWithSelect : paramList) {
        String strName = businessObjectWithSelect.getSelectData("name");
        String strRevision = businessObjectWithSelect.getSelectData("revision");
        String strCurrentState = businessObjectWithSelect.getSelectData("current");
        DomainObject domainObject = DomainObject.newInstance(paramContext, (BusinessObject)businessObjectWithSelect);
        domainObject.setPolicy(paramContext, strWorkSpacePolicy);
        bool = "".equals(strRevision) ? false : true;
        if (bool) {
        	if(strCurrentState.equals(strExist)) {
        		domainObject.setState(paramContext, strActive); 
        	}else if(strCurrentState.equals(strArchive)) {
        		domainObject.setState(paramContext, strWorkspaceArchive);
        	}
        } 
          
      } 
    } catch (Exception exception) {
      this._log.debug("\nERROR: An error occurred during BookmarkMaturityMigration for " + this.targetType + "\n");
      this._log.debug("\tNO data compromised\n");
      this._log.debug("\tThe process has been aborted. Please run the program again.\n");
      this._log.debug("STACK :" + exception.getLocalizedMessage());
    } 
  }
  
  public void init(Context paramContext) {
    this._log.debug("init");
  }
  
  public void close(Context paramContext) {
    this._log.debug("close");
  }
}