package com.pg.dsm.preference.template.irm;

import java.util.Map;

import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;

import matrix.util.MatrixException;

public interface IIRMTemplateCreateSteps {
    DomainObject createTemplate(Map attributeMap) throws FrameworkException;
    boolean addInterface(DomainObject domainObject) throws MatrixException;
    boolean updateAttributes(DomainObject domainObject, Map attributeMap) throws FrameworkException;
    boolean applyShareTemplateWith(DomainObject domainObject, String shareTemplateWithValue) throws Exception;
}
