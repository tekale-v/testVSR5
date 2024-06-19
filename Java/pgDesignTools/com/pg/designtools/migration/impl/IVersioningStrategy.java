package com.pg.designtools.migration.impl;

import java.util.Map;

import com.dassault_systemes.enovia.versioning.util.ENOVersioningException;
import com.matrixone.apps.domain.DomainObject;

import matrix.db.Context;
import matrix.util.MatrixException;

public interface IVersioningStrategy {

	public Map execute(Context context,DomainObject doObject) throws MatrixException, ENOVersioningException;
}
