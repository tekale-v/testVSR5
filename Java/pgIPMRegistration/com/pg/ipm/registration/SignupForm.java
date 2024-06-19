package com.pg.ipm.registration;

import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import java.util.Map;
import matrix.db.Context;
import matrix.db.Policy;

public class SignupForm
{
  public void newUserRegistration(Context paramContext, Map paramMap)
  {
    String strVault = PropertyUtil.getSchemaProperty("vault_eServiceProduction");
    String strType = PropertyUtil.getSchemaProperty("type_pgSignupForm");
    String strPolicy = PropertyUtil.getSchemaProperty("policy_pgSignupForm");
    String strpgTnumber = PropertyUtil.getSchemaProperty("attribute_pgTNumber");
    String strpgUserName = PropertyUtil.getSchemaProperty("attribute_pgUserName");
    try
    {
      ContextUtil.startTransaction(paramContext, true);
      String strUserName = (String)paramMap.get(strpgUserName);

      Policy localPolicy = new Policy(strPolicy);
      String strRevision = localPolicy.getFirstInSequence(paramContext);

      DomainObject localDomainObject = DomainObject.newInstance(paramContext);
      localDomainObject.createObject(paramContext, strType, strUserName, strRevision, strPolicy, strVault);
      localDomainObject.setAttributeValues(paramContext, paramMap);
      ContextUtil.commitTransaction(paramContext);
    }
    catch (Exception localException)
    {
      ContextUtil.abortTransaction(paramContext);
    }
  }
}