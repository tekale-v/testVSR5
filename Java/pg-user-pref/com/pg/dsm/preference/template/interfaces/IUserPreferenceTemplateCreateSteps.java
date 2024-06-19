package com.pg.dsm.preference.template.interfaces;

import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;

public interface IUserPreferenceTemplateCreateSteps {
    String getCreatedID(String objectName, String type, String policy) throws Exception;

    void updateSecurityPreferences(DomainObject domainObject) throws FrameworkException;

    void updateChangeActionPreferences(DomainObject domainObject) throws FrameworkException;

    void updatePlantPreferences(DomainObject domainObject) throws Exception;

    void updateSharingMemberPreferences(DomainObject domainObject) throws FrameworkException;

    void updateTemplateSharingMemberPreferences(DomainObject domainObject) throws Exception;

    void updatePackagingPreferences(DomainObject domainObject) throws FrameworkException;

    void updateProductPreferences(DomainObject domainObject) throws FrameworkException;

    void updateRawMaterialPreferences(DomainObject domainObject) throws FrameworkException;

    void updateTechnicalSpecificationPreferences(DomainObject domainObject) throws FrameworkException;

    void updateExplorationPreferences(DomainObject domainObject) throws FrameworkException;
    
	//Added by DSM for 22x CW-06 for Requirement #50186,50187 - START
    void updateManufacturingEquivalent(DomainObject domainObject) throws FrameworkException;
    
    void updateSupplierEquivalent(DomainObject domainObject) throws FrameworkException;
	//Added by DSM for 22x CW-06 for Requirement #50186,50187 - END


}
