package pg.custom.AWLUtility;

import com.matrixone.apps.domain.util.PropertyUtil;

public class AWLUtilityConstants
{
  public static final String APPROVED = "Approved";
  
  public static final String VAULT_ESERVICEPRODUCTION = PropertyUtil.getSchemaProperty("vault_eServiceProduction");
  
  public static final String TYPE_PG_BRAND = PropertyUtil.getSchemaProperty("type_pgBrand");
  public static final String TYPE_PG_SUBBRAND = PropertyUtil.getSchemaProperty("type_pgSubBrand");
  public static final String TYPE_PG_PACKINGMATERIAL = PropertyUtil.getSchemaProperty("type_pgPackingMaterial");


  public static final String POLICY_PG_PICKLISTITEM = PropertyUtil.getSchemaProperty("policy_pgPicklistItem");

  public static final String STATE_PG_PICKLISTITEM_ACTIVE = PropertyUtil.getSchemaProperty("policy", POLICY_PG_PICKLISTITEM, "state_Active");
    
  
  public static final String RELATIONSHIP_PG_GCAS = PropertyUtil.getSchemaProperty("relationship_pgGCAS");
  public static final String RELATIONSHIP_PG_BRAND = PropertyUtil.getSchemaProperty("relationship_pgBrand");
  public static final String RELATIONSHIP_PG_SUBBRAND = PropertyUtil.getSchemaProperty("relationship_pgSubBrand");
  
  public static final String ATTRIBUTE_PG_AWLINSTRUCTIONS   = PropertyUtil.getSchemaProperty("attribute_pgAWLInstructions");
   

}
